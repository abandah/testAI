package com.ubitc.popuppush.providers.realtimedatabase

import android.os.Handler
import android.os.Looper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.ubitc.popuppush.App
import com.ubitc.popuppush.BuildConfig
import com.ubitc.popuppush.MConstants
import com.ubitc.popuppush.models.Action
import com.ubitc.popuppush.service.apis.ApiService
import com.ubitc.popuppush.service.db.DbService
import com.ubitc.popuppush.service.remote_control.RemoteController
import com.ubitc.popuppush.ui.login.Device
import com.ubitc.popuppush.views.satellite.ServiceEntity


class RealTimeDBProvider(serial: String, val duration: Long = 10000L) {
    private var database: FirebaseDatabase? = null
    private var deviceRef: DatabaseReference? = null
    private var settingsRef: DatabaseReference? = null
    private var countriesRef: DatabaseReference? = null
    private var actionsRef: DatabaseReference? = null
    private var auth: FirebaseAuth? = null


    companion object {
        private var inv = BuildConfig.FIREBASE_INV
        private val deviceRoot = "$inv/Devices"
        private const val lastLogin = "lastLogin"
        private var INSTANCE: RealTimeDBProvider? = null
        fun getInstance(
            serial: String = MConstants.serialNumberSrt.serialName!!,
            duration: Long = 10000L
        ): RealTimeDBProvider {
            if (INSTANCE == null) {
                INSTANCE = RealTimeDBProvider(serial, duration)
            }
            return INSTANCE!!
        }
    }

    init {
        database = Firebase.database
        settingsRef = database!!.getReference("$inv/AppSettings")
        countriesRef = database!!.getReference("$inv/Countries")
        deviceRef = database!!.getReference("$deviceRoot/$serial")
        actionsRef = deviceRef!!.child("actions")
        auth = Firebase.auth
        auth!!.signInAnonymously().addOnCompleteListener {
            if (it.isSuccessful) {
                //val user = auth!!.currentUser
                initDeviceListener()
            }
        }


    }

    private val deviceValueEventListener =
        object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                val action = dataSnapshot.getValue(Action::class.java)
                markDeviceActionAsRead(action)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }


        }


    fun isPlayingState(playing: Boolean) {
        deviceRef?.child("DeviceState")?.child("isPlaying")?.setValue(playing)
    }

    fun pushScreenAngle(angle: Int) {
        DbService.getInstance().saveValue("screenAngle", angle.toString())
        deviceRef?.child("DeviceState")?.child("screenAngle")?.setValue(angle)
    }

    fun getScreenAngle(onSuccess: (Int) -> Unit) {
        DbService.getInstance().getValue("screenAngle")?.let {
            onSuccess(it.toIntOrNull() ?: 0)
            return
        }
        deviceRef?.child("DeviceState")?.child("screenAngle")
            ?.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    onSuccess(snapshot.value.toString().toIntOrNull() ?: 0)
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    fun pushCampaignAssigned(current_campaign: String, current_campaign_id: String) {
        deviceRef?.child("DeviceState")?.child("current_campaign")?.setValue(current_campaign)
        deviceRef?.child("DeviceState")?.child("current_campaign_id")?.setValue(current_campaign_id)
    }

    private fun markDeviceActionAsRead(value: Action?) {
        val query = actionsRef?.orderByChild("id")?.equalTo(value?.id)?.limitToFirst(1)
        query?.addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.iterator().forEach {
                    it.ref.removeValue()
                }
                RemoteController.getInstance().handleNotification(value)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

    }

    fun removeDevice() {
        deviceRef?.removeValue()
    }


    private fun initDeviceListener() {
        removeDeviceListener()
        actionsRef?.addChildEventListener(deviceValueEventListener)
    }

    private fun removeDeviceListener() {
        actionsRef?.removeEventListener(deviceValueEventListener)
    }

    private val iAmAliveHandler: Handler = Handler(Looper.getMainLooper())
    private val iAmAliveRunnable = object : Runnable {
        override fun run() {
            deviceRef?.child(lastLogin)?.setValue(ServerValue.TIMESTAMP)
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        //Log.d("RealTimeDBProvider", "iAmAliveHandler: success")
                    } else {
                        //Log.d("RealTimeDBProvider", "iAmAliveHandler: failed")
                    }
                }
            if (Device.Token != null) {
                ApiService.getInstance().sendDeviceLastLogin()

            }
            iAmAliveHandler.postDelayed(this, duration)
        }

    }

    fun startUpdatingIAmAlive() {

        iAmAliveHandler.removeCallbacks(iAmAliveRunnable)
        iAmAliveHandler.post(iAmAliveRunnable)
    }

    fun sendChannel(allTvList: ArrayList<ServiceEntity?>?) {
        deviceRef?.child("DeviceSource")?.child("Channels")?.setValue(allTvList)
    }

    fun sendUsbFiles(files: List<Pair<String, String>>) {
        deviceRef?.child("DeviceSource")?.child("UsbFiles")?.setValue(files)

    }

    fun doAppNeedsClearData(function: (Boolean, String) -> Unit) {
        val versionCode = BuildConfig.VERSION_CODE.toString()
        val oldVersionName = DbService.getInstance().getValue("versionCode")
        if (oldVersionName == versionCode) {
            function(false, versionCode)
            return
        }
        settingsRef?.child("NeedClearDataAfterUpdate")?.child(versionCode)
            ?.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    function(snapshot.value.toString().toBoolean(), versionCode)
                }

                override fun onCancelled(error: DatabaseError) {
                    function(false, versionCode)
                }
            })

    }


    var prayerRespCounter = 1
    private val childEventListener = object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            val actionFromFirebaseKey = snapshot.key
            if (actionFromFirebaseKey.isNullOrEmpty() || actionFromFirebaseKey != "pray") {
                return
            }
            if(prayerRespCounter == 0){
                prayerRespCounter++
                return
            }
            val value = snapshot.value.toString().toBoolean()
            App.currentActivity?.prayerStart(value ?: false)
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
//
//            val actionFromFirebaseKey = snapshot.key
//            if (actionFromFirebaseKey.isNullOrEmpty() || actionFromFirebaseKey != "pray") {
//                return
//            }
//            if(prayerRespCounter == 0){
//                prayerRespCounter++
//                return
//            }
//            val value = snapshot.value.toString().toBoolean()
//            App.currentActivity?.prayerStart(value ?: false)
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
        }

        override fun onCancelled(error: DatabaseError) {
        }
    }


    fun startPrayingTimeChecker() {
        val country = Device.country_name
        if (country.isNullOrEmpty()) {
            return
        }

        countriesRef?.child(country)?.removeEventListener(childEventListener)
        if (Device.is_prayer_enable == false) {
            return
        }
        countriesRef?.child(country)?.addChildEventListener(childEventListener)


    }

    fun resetCounter() {
        prayerRespCounter = 1
    }

//    private fun stopService(){
//        iAmAliveHandler.removeCallbacks(iAmAliveRunnable)
//        removeDeviceListener()
//    }


}
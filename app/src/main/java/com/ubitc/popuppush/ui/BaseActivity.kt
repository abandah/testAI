package com.ubitc.popuppush.ui

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.*
import android.view.KeyEvent
import androidx.fragment.app.FragmentActivity
import com.ubitc.popuppush.App
import com.ubitc.popuppush.models.Action
import com.ubitc.popuppush.models.Campaign
import com.ubitc.popuppush.models.MAIN
import com.ubitc.popuppush.models.MediaModel
import com.ubitc.popuppush.providers.downloadx.DownloaderRecyclerView
import com.ubitc.popuppush.providers.realtimedatabase.RealTimeDBProvider
import com.ubitc.popuppush.receiver.NetworkChangeReceiver
import com.ubitc.popuppush.service.db.DbService
import com.ubitc.popuppush.service.remote_control.RemoteMainFunction
import com.ubitc.popuppush.ui.login.Device
import com.ubitc.popuppush.ui.login.LoginScreen
import com.ubitc.popuppush.views.satellite.SatelliteView
import com.ubitc.popuppush.views.satellite.ServiceEntity
import com.ubitc.popuppush.views.tools.MyView
import com.ubitc.popuppush.views.tools.ViewListener
import kotlin.system.exitProcess


open class BaseActivity : FragmentActivity(), RemoteMainFunction,
    NetworkChangeReceiver.ConnectionListener {

    var logDialogIsShowing: Boolean = false
    var scheduleCampaignDialog: BaseDialog? = null
    var infoDialog: BaseDialog? = null
    var prayDialog: BaseDialog? = null

    private var audioManager :AudioManager? = null
    private var maxVolume :Int =  0
    private var currentVolume :Int = 0
    private var volumeStep :Int = 1

    private val volumeSourceType = AudioManager.STREAM_MUSIC
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.currentActivity = this
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        audioManager?.let {
            maxVolume = it.getStreamMaxVolume(volumeSourceType)
            currentVolume = it.getStreamVolume(volumeSourceType)
            volumeStep= maxVolume*10/100


        }
        //   InternetReceivers.getInstance()
    }
    private var code:String = ""
    private val clickHandler = Handler(Looper.getMainLooper())
    val r = Runnable {
        code = ""

    }
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {


        when (keyCode) {
            KeyEvent.KEYCODE_BACK -> {
                finish()
                return true
            }
            KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> {
                playPause(null)
                return true
            }
            KeyEvent.KEYCODE_MEDIA_NEXT -> {
                nextMedia()
                return true
            }
            KeyEvent.KEYCODE_MEDIA_PREVIOUS -> {
                previousMedia()
                return true
            }
            KeyEvent.KEYCODE_MEDIA_STOP -> {
                stopMedia()
                return true
            }
            KeyEvent.KEYCODE_DPAD_CENTER -> {
                showInfo()
                return true
            }
            KeyEvent.KEYCODE_PROG_GREEN -> {
                onClickGreenButton()

                return true
            }
            KeyEvent.KEYCODE_PROG_BLUE -> {
                onClickBlueButton()

                return true
            }
            KeyEvent.KEYCODE_1 -> {

                clickHandler.removeCallbacks(r)
                clickHandler.postDelayed(r, 500)
                code += "1"
                handleNumberCode(code)
                return true

            }
            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                rotateScreen(+90)
                return true
            }

            KeyEvent.KEYCODE_DPAD_LEFT -> {
                rotateScreen(-90)
                return true
            }
//            KeyEvent.KEYCODE_PROG_RED -> {
//                onClickRedButton()
//
//                return true
//            }

//            KeyEvent.KEYCODE_DPAD_UP -> {
//                return true
//            }
//            KeyEvent.KEYCODE_PROG_BLUE -> {
//                onClickBlueButton()
//               // sendChannel()
//                return true
//            }
//            KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> {
//                playPause(null)
//                return true
//            }
//
//            KeyEvent.KEYCODE_NUMPAD_ENTER -> {
//                playPause(null)
//                return true
//            }
//            KeyEvent.KEYCODE_F2 -> {
//                prayerStart()
//                return true
//            }
//            KeyEvent.KEYCODE_0 -> {
//                prayerStart()
//                return true
//            }


//            KeyEvent.KEYCODE_NUMPAD_DOT -> {
//                nextMedia()
//                return true
//            }
//            KeyEvent.KEYCODE_NUMPAD_0 -> {
//                previousMedia()
//                return true
//            }
//
//            KeyEvent.KEYCODE_NUMPAD_5 -> {
//                stopMedia()
//                return true
//            }
//            KeyEvent.KEYCODE_1 ->{
//                val r = Runnable {
//                    code = ""
//
//                }
//                clickHandler.removeCallbacks(r)
//                clickHandler.postDelayed(r, 1000)
//                code += "1"
//              handleNumberCode(code)
//                return true
//
//            }
//            KeyEvent.KEYCODE_NUMPAD_SUBTRACT ->{
//                val desireVolume = currentVolume - volumeStep
//                if(desireVolume > 1){
//                    audioManager?.setStreamVolume(volumeSourceType, desireVolume, 0)
//                }else{
//                    audioManager?.setStreamVolume(volumeSourceType, 0, 0)
//                }
//                audioManager?.let {
//                    currentVolume = it.getStreamVolume(volumeSourceType)
//                }
//           }
//            KeyEvent.KEYCODE_NUMPAD_ADD ->{
//
//                val desireVolume = currentVolume + volumeStep
//                if(desireVolume > maxVolume){
//                    audioManager?.setStreamVolume(volumeSourceType, maxVolume, 0)
//                }else{
//                    audioManager?.setStreamVolume(volumeSourceType, desireVolume, 0)
//                }
//                audioManager?.let {
//                    currentVolume = it.getStreamVolume(volumeSourceType)
//                }
//            }
        }


        return false
    }

    open fun showInfo() {

    }

    private fun handleNumberCode(code: String) {
        if(code.length == 4){
            if(code == "1111"){
                deviceRestart()
                this.code = ""
            }
        }
    }

    open fun stopMedia() {

    }

    open fun previousMedia() {
    }

    open fun nextMedia() {

    }

    open fun rotateScreen(action: Int) {
    }


    open fun onClickGreenButton() {
    }

    open fun onClickBlueButton() {

    }


    override fun onKeyLongPress(keyCode: Int, event: KeyEvent?): Boolean {
        return super.onKeyLongPress(keyCode, event)
    }

    override fun onKeyMultiple(keyCode: Int, repeatCount: Int, event: KeyEvent?): Boolean {
        return super.onKeyMultiple(keyCode, repeatCount, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        return super.onKeyUp(keyCode, event)
    }

    override fun onKeyShortcut(keyCode: Int, event: KeyEvent?): Boolean {
        return super.onKeyShortcut(keyCode, event)
    }


    override fun onConnected() {
    }

    override fun onNotConnected() {
    }

    override fun campaignAssigned() {


    }

    open fun runScheduleCampaign(campaign: Campaign) {


    }

    open fun changeTheCampaign(){

    }

    override fun updateMedia() {
    }

    override fun deviceDetached() {
        cacheDir.deleteRecursively()
        deviceRestart()
    }

    override fun deviceFlushed() {

        cacheDir.deleteRecursively()
        deviceRestart()
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    override fun deviceRestart() {
//        val launchIntent = packageManager.getLaunchIntentForPackage(
//            applicationContext.packageName
//        )
//        Thread.sleep(1000)
//
//        //System.exit(10)
//        startActivity(launchIntent)
//        finish()
//        Process.killProcess(Process.myPid())
//        exitProcess(10)

        val mStartActivity = Intent(this, LoginScreen::class.java)
        val mPendingIntentId = 123456
        val mPendingIntent: PendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(
                this,
                mPendingIntentId,
                mStartActivity,
                PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
            )
        } else {
            PendingIntent.getActivity(
                this,
                mPendingIntentId,
                mStartActivity,
                PendingIntent.FLAG_CANCEL_CURRENT
            )
        }
        val mgr: AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        mgr.setExact(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent)
        finish()
        Process.killProcess(Process.myPid())
        exitProcess(10)
    }

    override fun statusUpdate() {


    }

    override fun deviceDeleted() {
        Device.deviceDeleted {
            cacheDir.deleteRecursively()
            DbService.getInstance().clearDb()
            RealTimeDBProvider.getInstance().removeDevice()
            deviceRestart()
        }

    }

   override fun playPause(b: Boolean?) {
    }

    override fun prayerStart(toBoolean: Boolean) {

    }

    open fun getCurrentMain(): MAIN? {
        return null

    }

    open fun heapInfo(heapInfo: String) {

    }

    open fun ramInfo(ramInfo: String) {

    }

    open fun campaignHasEnded(campaign: Campaign?) {

    }

    open fun getRootView(): DownloaderRecyclerView? {
        return null
    }

    open fun rotateScreen(action: Action) {

    }

    open fun sendChannel() {

            var sat: SatelliteView? = SatelliteView(this)
            val media = MediaModel()
            media.path = "100"
            sat?.prepareView(media, object : ViewListener {
                override fun finished(view: MyView?) {
                }

                override fun viewIsReady(view: MyView?) {
//                    view!!.playView(view.media)
//                    if ((view as SatelliteView).isConnected()) {
                        val allTvList: ArrayList<ServiceEntity?>? = (view as SatelliteView).allTvList
                        RealTimeDBProvider.getInstance().sendChannel(allTvList)

                        sat?.forceStop()
                        sat = null
//                    }
                }

                override fun showLayer1(media: MediaModel?) {
                }

                override fun showLayer2(media: MediaModel?) {
                }

                override fun showLayer3(media: MediaModel?) {
                }

                override fun viewNotSupported(view: MyView?) {

                }
            })
        }


}
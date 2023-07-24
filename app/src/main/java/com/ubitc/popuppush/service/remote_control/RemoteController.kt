package com.ubitc.popuppush.service.remote_control

import com.ubitc.popuppush.App
import com.ubitc.popuppush.AppMainFeatureConstants
import com.ubitc.popuppush.asynctasks.DeviceInfoAsyncTask
import com.ubitc.popuppush.models.Action
import com.ubitc.popuppush.models.NotificationActions
import com.ubitc.popuppush.service.campaignman.CampaignManagerService
import com.ubitc.popuppush.service.db.DbService
import com.ubitc.popuppush.ui.login.Device

class RemoteController : RemoteMainFunction {

    companion object {

        @Volatile
        private var instance: RemoteController? = null
        fun getInstance(): RemoteController {
            if (instance != null) return instance!!
            return synchronized(this) {
                val checkAgain = instance
                if (checkAgain != null) checkAgain
                else {
                    instance = RemoteController()
                    return instance!!
                }
            }
        }
    }

    fun handleNotification(action: Action?) {
        when (NotificationActions.getAction(action?.action!!)) {
            NotificationActions.AddDevice -> statusUpdate()
            NotificationActions.Assigned -> campaignAssigned()
            NotificationActions.UpdateOrder -> updateMedia()
            NotificationActions.Detach -> deviceDetached()
            NotificationActions.Flush -> deviceFlushed()
            NotificationActions.Restart -> deviceRestart()
            NotificationActions.StatusUpdated -> statusUpdate()
            NotificationActions.DeletedDevice -> deviceDeleted()
            NotificationActions.Play -> playPause(true)
            NotificationActions.Pause -> playPause(false)
            NotificationActions.Rotate -> rotateScreen(action)
            NotificationActions.GetChannels -> sendChannel()
            NotificationActions.START_SCHEDULE -> startScheduled(action)
            else -> return
        }


    }

    private fun startScheduled(action: Action) {
        val campaignId = action.data_id
        if(campaignId.isNullOrEmpty()) return
        val c = CampaignManagerService.getInstance().getCampaignById(campaignId)
        if (c == null) {
            return
        }
        App.currentActivity?.runScheduleCampaign(c)
    }



    private fun sendChannel() {
        App.currentActivity?.sendChannel()
    }

    private fun rotateScreen(action: Action) {
        App.currentActivity?.rotateScreen(action)
    }


    override fun campaignAssigned() {
        Device.campaignAssigned {
            if (AppMainFeatureConstants.enableDelayBeforeNotification)
                Thread.sleep(2000)
            App.currentActivity?.changeTheCampaign()
        }

    }

    override fun updateMedia() {
        if (AppMainFeatureConstants.enableDelayBeforeNotification)
            Thread.sleep(2000)
        App.currentActivity?.updateMedia()
    }

    override fun deviceDetached() {
        Device.deviceDetached {
            if (AppMainFeatureConstants.enableDelayBeforeNotification)
                Thread.sleep(2000)
            App.currentActivity?.deviceDetached()
        }

    }

    override fun deviceFlushed() {
        Device.deviceFlushed {
            if (AppMainFeatureConstants.enableDelayBeforeNotification)
                Thread.sleep(2000)
            App.currentActivity?.deviceFlushed()
        }

    }

    override fun deviceRestart() {
        App.currentActivity?.deviceRestart()
    }

    override fun statusUpdate() {
        Device.needToGetNewInfo = true
        DbService.getInstance().getCampaign()
        DeviceInfoAsyncTask().executeAsync("") {
            App.currentActivity?.statusUpdate()
        }

    }

    override fun deviceDeleted() {
        Device.deviceDeleted {
            if (AppMainFeatureConstants.enableDelayBeforeNotification)
                Thread.sleep(2000)
            App.currentActivity?.deviceDeleted()
        }
    }

    override fun playPause(b: Boolean?) {
        App.currentActivity?.playPause(b)
    }

    override fun prayerStart(toBoolean: Boolean) {
       // App.currentActivity?.prayerStart()
    }
}
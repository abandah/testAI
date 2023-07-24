package com.ubitc.popuppush.asynctasks

import com.android.volley.VolleyError
import com.ubitc.popuppush.service.apis.ApiService
import com.ubitc.popuppush.taskrunner.TaskRunner
import com.ubitc.popuppush.ui.login.Device


class DeviceInfoAsyncTask : TaskRunner<String, Int, String>() {

    override fun doInBackground(params: String): String {
       getDeviceInfoAsync()
        return "Done"
    }

    override fun onProgressUpdate(progress: Int) {
    }

    override fun onPreExecute() {
    }


    private fun getDeviceInfoAsync() {
        if(Device.deviceId == null) {
            return
        }
        ApiService.getInstance()
            .getDeviceInfo(Device.deviceId!!, onSuccess = { it2 ->
                val res = it2.data
                Device.deviceInfo = res
                Device.Token = it2.data.token
                Device.isActive = it2.data.isActive()
                Device.isAddedToCompany = it2.data.isAddedToCompany()
                Device.isCampaignAdded = it2.data.isCampaignAdded()
                Device.deviceId = it2.data.id
                Device.needToGetNewInfo = false
                Device.country_name = it2.data.country_name
                Device.is_prayer_enable = it2.data.is_prayer_enable

                onPostExecute("Done")


            }, onError2 = { _: Int, _: String?, _: VolleyError ->

            })
    }

}
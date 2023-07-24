package com.ubitc.popuppush.asynctasks


import com.android.volley.VolleyError
import com.ubitc.popuppush.App
import com.ubitc.popuppush.models.CampaignResponse
import com.ubitc.popuppush.service.apis.ApiService
import com.ubitc.popuppush.service.campaignman.CampaignManagerService
import com.ubitc.popuppush.service.db.DbService
import com.ubitc.popuppush.taskrunner.TaskRunner
import com.ubitc.popuppush.ui.login.Device

class ChangeCampingTask : TaskRunner<String, Int, String>() {


    override fun doInBackground(params: String): String {
        getChannelMedia()

        return "Done"
    }

    override fun onProgressUpdate(progress: Int) {


    }

    override fun onPreExecute() {

    }


    fun onSuccess(campaignId: String?) {
        onPostExecute(campaignId)

    }


    private fun getChannelMedia() {

        ApiService.getInstance().getMedia(onSuccess = {
            Thread {
                handleResponse(it)
            }.start()


        }, onError2 = { errorCode: Int, _: String?, _: VolleyError ->
            val res = DbService.getInstance().getCampaign()
            if (res != null && res.MAIN?.isEmpty() == false) {
                return@getMedia
            }
            if (errorCode == 0 && (res == null || res.MAIN?.isEmpty() == true)) {
                return@getMedia
            }

        })
    }

    private fun handleResponse(campaignResponse: CampaignResponse) {
        if (campaignResponse.isEmpty()) {
            Device.isCampaignAdded = false
            App.currentActivity?.deviceFlushed()
            return
        }

        val main = campaignResponse.getCampaignFromResponse()

        CampaignManagerService.getInstance().saveCampaign(main!!) { campaign ->
            Device.isCampaignAdded = true
            campaignResponse.setCampaignFromResponse(campaign)
            startSaveScheduledCampaigns(campaignResponse)
        }

    }

    private fun startSaveScheduledCampaigns(campaignResponse: CampaignResponse) {
        val scheduled = campaignResponse.getScheduleCampaigns()
        if (!scheduled.isNullOrEmpty()) {
            CampaignManagerService.getInstance().saveCampaignList(scheduled) { campaignList ->
                campaignResponse.scheduled_campaigns = campaignList
                CampaignManagerService.getInstance().saveCampaignIndex(campaignResponse)
                onSuccess(campaignResponse.getCampaignFromResponse()?.campaign_id)
            }
        } else {
            campaignResponse.scheduled_campaigns = null
            CampaignManagerService.getInstance().saveCampaignIndex(campaignResponse)
            onSuccess(campaignResponse.getCampaignFromResponse()?.campaign_id)

        }
    }


}
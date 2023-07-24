package com.ubitc.popuppush.service.campaignman

import com.ubitc.popuppush.models.CampaignResponse
import com.ubitc.popuppush.models.CampaignSimple

class CampaignIndex(response: CampaignResponse) {
    var main : CampaignSimple? = null
    private val scheduledCampaigns : ArrayList<CampaignSimple> = ArrayList()

    init {
        val tempMain = CampaignSimple()
        tempMain.id = response.getCampaignFromResponse()?.campaign_id
        tempMain.name = response.getCampaignFromResponse()?.campaign_name
        tempMain.is_repeated = response.getCampaignFromResponse()?.is_repeated
        tempMain.start_time = response.getCampaignFromResponse()?.start_time
        tempMain.end_time = response.getCampaignFromResponse()?.end_time
        tempMain.type = response.getCampaignFromResponse()?.type
        tempMain.date = response.getCampaignFromResponse()?.date
        tempMain.days = response.getCampaignFromResponse()?.days
        tempMain.scheduled_id = response.getCampaignFromResponse()?.scheduled_id
        main = tempMain
        response.getScheduleCampaigns()?.let {

            it.forEach { scheduledCampaign ->
                val tempScheduledCampaigns = CampaignSimple()
                tempScheduledCampaigns.id = scheduledCampaign.campaign_id
                tempScheduledCampaigns.name = scheduledCampaign.campaign_name
                tempScheduledCampaigns.is_repeated = scheduledCampaign.is_repeated
                tempScheduledCampaigns.start_time = scheduledCampaign.start_time
                tempScheduledCampaigns.end_time = scheduledCampaign.end_time
                tempScheduledCampaigns.type = scheduledCampaign.type
                tempScheduledCampaigns.date = scheduledCampaign.date
                tempScheduledCampaigns.days = scheduledCampaign.days
                tempScheduledCampaigns.scheduled_id = scheduledCampaign.scheduled_id
                scheduledCampaigns.add(tempScheduledCampaigns)
            }

        }
    }

    override fun toString(): String {
        return "CampaignIndex(main=$main, scheduledCampaigns=$scheduledCampaigns)"
    }

}
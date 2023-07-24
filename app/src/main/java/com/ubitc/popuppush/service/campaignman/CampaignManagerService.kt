package com.ubitc.popuppush.service.campaignman

import com.google.gson.Gson
import com.ubitc.popuppush.App
import com.ubitc.popuppush.models.Campaign
import com.ubitc.popuppush.models.CampaignResponse
import com.ubitc.popuppush.providers.downloader.DownloadProvider
import com.ubitc.popuppush.providers.realtimedatabase.RealTimeDBProvider
import com.ubitc.popuppush.service.db.DbService
import com.ubitc.popuppush.service.updator.LocalMediaService

class CampaignManagerService : CampaignManager {

    var campaignID: String?
        set(value) {
            DbService.getInstance().saveValue("PlayingcampaignID", value!!)
        }
        get() {
            return DbService.getInstance().getValue("PlayingcampaignID")
        }

    companion object {
        @Volatile
        var providerInstance: CampaignManagerService? = null
        fun getInstance(): CampaignManagerService {
            if (providerInstance == null) {
                providerInstance = CampaignManagerService()
            }
            return providerInstance!!
        }
    }

    override fun saveCampaign(campaign: Campaign, onSuccess: (Campaign) -> Unit) {
        DownloadProvider.getInstance()
            .gatherMediaAndDownloadThemAndDoNotChangeCampaign(campaign, onSuccess = {
                campaign.MAIN = it?.MAIN
                DbService.getInstance().saveValue(campaign.campaign_id!!, Gson().toJson(campaign))
                if(!it?.scheduled_id.isNullOrEmpty()){
                    DbService.getInstance().saveValue(campaign.scheduled_id!!, Gson().toJson(campaign))
                }
                onSuccess(campaign)
            })
    }

    override fun saveCampaignList(
        campaignList: ArrayList<Campaign>,
        onSuccess: (ArrayList<Campaign>) -> Unit
    ) {

        downloadCampaignList(campaignList, campaignList.size - 1) {
            onSuccess.invoke(it)
        }

    }

    private fun downloadCampaignList(
        campaignList: java.util.ArrayList<Campaign>,
        steps: Int,
        onSuccess: (java.util.ArrayList<Campaign>) -> Unit
    ) {
        if (steps < 0) {
            onSuccess.invoke(campaignList)
            return
        }
        val campaign = campaignList[steps]
        saveCampaign(campaign) {
            campaignList[steps] = it
            val nextStep = steps - 1
            downloadCampaignList(campaignList, nextStep, onSuccess)
        }
    }


    override fun getCampaignById(id: String): Campaign? {
        DbService.getInstance().getValue(id)?.let {

            return Gson().fromJson(it, Campaign::class.java)
           // return campaign
        }
        return null
    }


    override fun changeToCampaign(campaignID: String?, onSuccess: () -> Unit) {
        this.campaignID = campaignID

        getCampaignById(campaignID!!).let {
            if (it != null) {
                RealTimeDBProvider.getInstance().pushCampaignAssigned(
                    current_campaign = it.campaign_name!!,
                    current_campaign_id = it.campaign_id!!
                )
                DbService.getInstance().saveMediaToDB(it) {
                    LocalMediaService.getInstance().refreshAllMediasFromDB {
                        onSuccess()
                        App.currentActivity?.campaignAssigned()

                    }
                }
            }
        }

    }

    override fun changeToCampaignSilently(campaignID: String?, onSuccess: () -> Unit) {
        this.campaignID = campaignID

        getCampaignById(campaignID!!).let {
            if (it != null) {
                RealTimeDBProvider.getInstance().pushCampaignAssigned(
                    current_campaign = it.campaign_name!!,
                    current_campaign_id = it.campaign_id!!
                )
                DbService.getInstance().saveMediaToDB(it) {
                    LocalMediaService.getInstance().refreshAllMediasFromDB {
                        onSuccess()
                       // App.currentActivity?.campaignAssigned()

                    }
                }
            }
        }
    }

    override fun saveCampaignIndex(response: CampaignResponse) {
        val campaignIndex = CampaignIndex(response)
        DbService.getInstance().saveValue("campaignIndex", Gson().toJson(campaignIndex))

    }

    override fun getCampaignIndex(): CampaignIndex? {
        DbService.getInstance().getValue("campaignIndex")?.let {
            return Gson().fromJson(it, CampaignIndex::class.java)
        }
        return null
    }
}
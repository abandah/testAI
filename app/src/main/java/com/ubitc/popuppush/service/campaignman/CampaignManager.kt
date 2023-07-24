package com.ubitc.popuppush.service.campaignman

import com.ubitc.popuppush.models.Campaign
import com.ubitc.popuppush.models.CampaignResponse

interface CampaignManager {
    fun saveCampaign(campaign: Campaign, onSuccess: (Campaign) -> Unit)
    fun saveCampaignList(campaignList: ArrayList<Campaign>, onSuccess: (ArrayList<Campaign>) -> Unit)
    fun getCampaignById(id: String): Campaign?
    fun changeToCampaign(campaignID: String?, onSuccess: () -> Unit)
    fun changeToCampaignSilently(campaignID: String?, onSuccess: () -> Unit)
    fun saveCampaignIndex(response: CampaignResponse)
    fun getCampaignIndex(): CampaignIndex?




}
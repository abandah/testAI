@file:Suppress("PropertyName")

package com.ubitc.popuppush.models

import com.google.gson.annotations.SerializedName

class AssignCampaignReqModel  ( id: String, campaign_ids: String){
    @SerializedName("device_id")
    var id: String? = null

    @SerializedName("campaign_ids")
    var campaign_ids: ArrayList<String>? = null
    init {
        this.id = id
        this.campaign_ids= arrayListOf(campaign_ids)
    }
}
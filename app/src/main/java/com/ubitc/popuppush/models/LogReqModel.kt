package com.ubitc.popuppush.models

import com.google.gson.annotations.SerializedName

class LogReqModel {

    @SerializedName("campaign_id")
    var campaignId: String? = null


    @SerializedName("media_id")
    var mediaId: String? = null


    @SerializedName("layer")
    var layer: String? = null


    @SerializedName("duration")
    var duration: String? = null


    @SerializedName("available_ram")
    var availableRam: String? = null


}
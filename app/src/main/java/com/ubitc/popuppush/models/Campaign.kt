@file:Suppress("PropertyName")

package com.ubitc.popuppush.models



class Campaign {
    var campaign_id: String? = null
    var campaign_name: String? = null
    var is_repeated: Int? = null
    var start_time: String? = null
    var end_time: String? = null
    var type: String? = null
    var date: String? = null
    var days: String? = null
    var MAIN: ArrayList<MAIN>? = null
    var PLAYGROUND: ArrayList<PLAYGROUND>? = null
    var CORNER: ArrayList<CORNER>? = null
    var scheduled_id :String? = null
}

class MAIN : MediaModel() {
    var MAIN: ArrayList<MAIN>? = null
    var PLAYGROUND: ArrayList<PLAYGROUND>? = null
    var CORNER: ArrayList<CORNER>? = null
    var show_layer_one :Boolean= false
    var show_layer_two  :Boolean= false
    var show_layer_three  :Boolean= false

}

class PLAYGROUND : MediaModel()
class CORNER : MediaModel()
class CampaignResponse {

   private var data: ArrayList<Campaign>? = null
    var scheduled_campaigns: ArrayList<Campaign>? = null
    fun getCampaignFromResponse(): Campaign? {
        return data?.get(0)
    }

    fun setCampaignFromResponse(campaign: Campaign) {
        data?.clear()
        data?.add(campaign)
    }

    fun getScheduleCampaigns(): ArrayList<Campaign>? {
        return scheduled_campaigns
    }
    fun isEmpty(): Boolean {
        return data.isNullOrEmpty() || getCampaignFromResponse()?.MAIN.isNullOrEmpty()
    }
}


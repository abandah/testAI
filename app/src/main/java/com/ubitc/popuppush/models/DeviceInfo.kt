@file:Suppress("PropertyName")

package com.ubitc.popuppush.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class DeviceInfo : Serializable {


    @SerializedName("id")
    var id: String? = null

    @SerializedName("company_id")
    var companyId: String? = null

    @SerializedName("user_id")
    var userId: String? = null

    @SerializedName("status")
    var status: String? = null

    @SerializedName("code")
    var code: String? = null

    @SerializedName("token")
    var token: String? = null

    @SerializedName("name")
    var name: String? = null
        get() {
            if (field.isNullOrEmpty()) {
                return null
            }
            return field
        }


    @SerializedName("company_name")
    var company_name: String? = null

    @SerializedName("is_prayer_enable")
    var is_prayer_enable: Boolean? = null

    @SerializedName("country_name")
    var country_name: String? = null


    @SerializedName("user_name")
    var user_name: String? = null


    @SerializedName("campaigns")
    var campaigns: ArrayList<Campaigns> = arrayListOf()

    fun isActive(): Boolean {
        return status.equals("ACTIVE", ignoreCase = true)
    }

    fun isAddedToCompany(): Boolean {
        return !companyId.isNullOrEmpty() || !userId.isNullOrEmpty()
    }

    fun isCampaignAdded(): Boolean {
        return campaigns.isNotEmpty()
    }

    data class Campaigns(

        @SerializedName("id") var id: String? = null,
        @SerializedName("user_id") var userId: String? = null,
        @SerializedName("company_id") var companyId: String? = null,
        @SerializedName("name") var name: String? = null,
        @SerializedName("status") var status: String? = null,
        @SerializedName("created_at") var createdAt: String? = null,
        @SerializedName("updated_at") var updatedAt: String? = null,
        @SerializedName("deleted_at") var deletedAt: String? = null,
        @SerializedName("pivot") var pivot: Pivot? = Pivot()

    )

    data class Pivot(

        @SerializedName("device_id") var deviceId: String? = null,
        @SerializedName("campaign_id") var campaignId: String? = null,
        @SerializedName("created_at") var createdAt: String? = null,
        @SerializedName("updated_at") var updatedAt: String? = null

    )
}

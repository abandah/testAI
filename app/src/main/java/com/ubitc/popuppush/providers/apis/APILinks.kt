package com.ubitc.popuppush.providers.apis

import com.ubitc.popuppush.BuildConfig
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone


class APILinks {
    companion object {

        var BaseURL = BuildConfig.SERVER_URL

        //  const val BaseURL = "popuppush.livaat.com"
        //  const val BaseURL = "backend.popuppush.com"
        private val ApiUrl = "https://$BaseURL"
        private val DeviceURL = "$ApiUrl/device"
        private val CompanyURL = "$ApiUrl/api"
        private val CampaignURL = "$ApiUrl/api"


        //device URLs

        val addDevice = "$DeviceURL/create"
        val getMedia = "$DeviceURL/campaign/media"
        val updateInfo = "$DeviceURL/update"
        val updateUri = "$DeviceURL/media/local/uri"
        val updateFCM = "$DeviceURL/update/fcm"
        fun getCampaignById(id: String) = "$DeviceURL/campaign/device/content?campaign_id=$id"

        fun notificationDone(id: String) = "$DeviceURL/notification/$id/done"
        fun getDeviceInfo(deviceId: String) = "$DeviceURL/info/$deviceId"
        fun updateLocation(deviceId: String) = "$DeviceURL/location/$deviceId}"
        val sendChannels = "$DeviceURL/channel"
        val sendDeviceLog = "$DeviceURL/logging/create"
        val sendRAMLog = "$DeviceURL/logging/ram"
        val sendLastLogin = "$DeviceURL/update/last/login"
        val getCompanyCampaigns = "$CompanyURL/company/campaigns?per_page=50"
        val assignCampaign = "$CampaignURL/assign"
        fun getPrayersTimes(): String {
            val tz: TimeZone = TimeZone.getDefault()
            Calendar.getInstance().get(Calendar.MONTH)
            Calendar.getInstance().get(Calendar.YEAR)
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val currentDate = sdf.format(Date())
            return "https://api.aladhan.com/v1/timingsByCity?date=$currentDate&method=2&timezonestring=${tz.id}&adjustment=0&school=0&city=amman&country=jordan"
        }


    }
}
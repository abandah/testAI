package com.ubitc.popuppush.service.apis

import com.android.volley.VolleyError
import com.ubitc.popuppush.api_send_model.SerialNumber
import com.ubitc.popuppush.models.Campaign
import com.ubitc.popuppush.models.CampaignResponse
import com.ubitc.popuppush.models.CampaignSimple
import com.ubitc.popuppush.models.DeviceDetailsRequestModel
import com.ubitc.popuppush.models.DeviceInfo
import com.ubitc.popuppush.models.FCMReqModel
import com.ubitc.popuppush.models.LocationReqModel
import com.ubitc.popuppush.models.MediaModel
import com.ubitc.popuppush.models.UriReqModel
import com.ubitc.popuppush.providers.apis.ModelResp
import com.ubitc.popuppush.views.satellite.ServiceEntity

abstract class ApiMainFunction {
    abstract fun addDevice(
        serialNumberSrt: SerialNumber,
        onSuccess: ((ModelResp<DeviceInfo>) -> Unit)? = null,
        onError2: ((errorCode: Int, body: String?, error: VolleyError) -> Unit)? = null
    )

    abstract fun getMedia(
        onSuccess: ((CampaignResponse) -> Unit)? = null,
        onError2: ((errorCode: Int, body: String?, error: VolleyError) -> Unit)? = null
    )

    abstract fun updateInfo(
        deviceInfo: DeviceDetailsRequestModel,
        onSuccess: ((ModelResp<DeviceInfo>) -> Unit)? = null,
        onError2: ((errorCode: Int, body: String?, error: VolleyError) -> Unit)? = null
    )

    abstract fun updateUri(
        uriReqModel: UriReqModel,
        onSuccess: ((String) -> Unit)?,
        onError2: ((errorCode: Int, body: String?, error: VolleyError) -> Unit)?
    )

    abstract fun updateFCM(
        fcmReqModel: FCMReqModel? = null,
        onSuccess: ((String) -> Unit)? = null,
        onError2: ((errorCode: Int, body: String?, error: VolleyError) -> Unit)? = null
    )

    abstract fun notificationDone(
        id: String?,
        onSuccess: ((String) -> Unit)? = null,
        onError2: ((errorCode: Int, body: String?, error: VolleyError) -> Unit)? = null
    )

    abstract fun getDeviceInfo(
        deviceId: String,
        onSuccess: ((ModelResp<DeviceInfo>) -> Unit)? = null,
        onError2: ((errorCode: Int, body: String?, error: VolleyError) -> Unit)? = null
    )


    abstract fun updateLocation(
        location: LocationReqModel,
        onSuccess: ((String) -> Unit)? = null,
        onError2: ((errorCode: Int, body: String?, error: VolleyError) -> Unit)? = null
    )

    abstract fun sendDeviceSupportHDMI(
        onSuccess: ((String) -> Unit)? = null,
        onError2: ((errorCode: Int, body: String?, error: VolleyError) -> Unit)? = null,
        isSupport: Boolean
    )

    abstract fun sendDeviceSupportSatellite(
        onSuccess: ((String) -> Unit)? = null,
        onError2: ((errorCode: Int, body: String?, error: VolleyError) -> Unit)? = null,
        isSupport: Boolean
    )

    abstract fun sendChannels(
        allTvList: ArrayList<ServiceEntity?>?,
        onSuccess: ((String) -> Unit)? = null,
        onError2: ((errorCode: Int, body: String?, error: VolleyError) -> Unit)? = null
    )


    abstract fun senDeviceHeap(
        heap: Long,
        onSuccess: ((String) -> Unit)? = null,
        onError2: ((errorCode: Int, body: String?, error: VolleyError) -> Unit)? = null
    )

    abstract fun sendDeviceLog(
        media: MediaModel?,
        onSuccess: ((String) -> Unit)? = null,
        onError2: ((errorCode: Int, body: String?, error: VolleyError) -> Unit)? = null
    )

    abstract fun sendRamLog(
        onSuccess: ((String) -> Unit)? = null,
        onError2: ((errorCode: Int, body: String?, error: VolleyError) -> Unit)? = null
    )


    abstract fun sendDeviceLastLogin(
        onSuccess: ((String) -> Unit)? = null,
        onError2: ((errorCode: Int, body: String?, error: VolleyError) -> Unit)? = null
    )


    abstract fun getAllCampaigns(
        onSuccess: ((ModelResp<ArrayList<CampaignSimple>>) -> Unit)? = null,
        onError2: ((errorCode: Int, body: String?, error: VolleyError) -> Unit)? = null
    )

    abstract fun assignCampaign(
        id: String,
        onSuccess: (() -> Unit)? = null,
        onError2: ((errorCode: Int, body: String?, error: VolleyError) -> Unit)? = null
    )

    abstract fun getCampaignById(
        id: String,
        onSuccess: ((Campaign) -> Unit)? = null,
        onError2: ((errorCode: Int, body: String?, error: VolleyError) -> Unit)? = null
    )



}
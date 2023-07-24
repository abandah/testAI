package com.ubitc.popuppush.service.apis

import com.android.volley.VolleyError
import com.ubitc.popuppush.App
import com.ubitc.popuppush.MConstants
import com.ubitc.popuppush.R
import com.ubitc.popuppush.api_send_model.SerialNumber
import com.ubitc.popuppush.models.Campaign
import com.ubitc.popuppush.models.CampaignResponse
import com.ubitc.popuppush.models.CampaignSimple
import com.ubitc.popuppush.models.DeviceDetails
import com.ubitc.popuppush.models.DeviceDetailsRequestModel
import com.ubitc.popuppush.models.DeviceInfo
import com.ubitc.popuppush.models.FCMReqModel
import com.ubitc.popuppush.models.LocationReqModel
import com.ubitc.popuppush.models.MediaModel
import com.ubitc.popuppush.models.UriReqModel
import com.ubitc.popuppush.providers.apis.ApiServerProvider
import com.ubitc.popuppush.providers.apis.ModelResp
import com.ubitc.popuppush.views.satellite.ServiceEntity

class ApiService(private val apiProvider: ApiMainFunction) : ApiMainFunction() {


    companion object {
        @Volatile
        var providerInstance: ApiService? = null
        fun getInstance(): ApiService {
            if (providerInstance == null) {
                providerInstance = ApiService(ApiServerProvider())
            }
            return providerInstance!!
        }
    }

    override fun addDevice(
        serialNumberSrt: SerialNumber,
        onSuccess: ((ModelResp<DeviceInfo>) -> Unit)?,
        onError2: ((errorCode: Int, body: String?, error: VolleyError) -> Unit)?
    ) {
        if (MConstants.isInternetAvailable) {
            apiProvider.addDevice(serialNumberSrt, onSuccess, onError2)
        } else {
            onError2?.invoke(0, App.getString(R.string.no_internet), VolleyError(App.getString(R.string.no_internet)))
        }
    }

    override fun getMedia(
        onSuccess: ((CampaignResponse) -> Unit)?,
        onError2: ((errorCode: Int, body: String?, error: VolleyError) -> Unit)?
    ) {
        if (MConstants.isInternetAvailable) {
            apiProvider.getMedia(onSuccess, onError2)
        } else {
            onError2?.invoke(0, App.getString(R.string.no_internet), VolleyError(App.getString(R.string.no_internet)))
        }
    }

    override fun updateInfo(
        deviceInfo: DeviceDetailsRequestModel,
        onSuccess: ((ModelResp<DeviceInfo>) -> Unit)?,
        onError2: ((errorCode: Int, body: String?, error: VolleyError) -> Unit)?
    ) {
        if (MConstants.isInternetAvailable) {
            apiProvider.updateInfo(deviceInfo, onSuccess, onError2)
        } else {
            onError2?.invoke(0, App.getString(R.string.no_internet), VolleyError(App.getString(R.string.no_internet)))
        }
    }

    override fun updateUri(
        uriReqModel: UriReqModel,
        onSuccess: ((String) -> Unit)?,
        onError2: ((errorCode: Int, body: String?, error: VolleyError) -> Unit)?
    ) {

        if (MConstants.isInternetAvailable) {
            apiProvider.updateUri(uriReqModel, onSuccess, onError2)
        } else {
            onError2?.invoke(0, App.getString(R.string.no_internet), VolleyError(App.getString(R.string.no_internet)))
        }
    }

    override fun updateFCM(
        fcmReqModel: FCMReqModel?,
        onSuccess: ((String) -> Unit)?,
        onError2: ((errorCode: Int, body: String?, error: VolleyError) -> Unit)?
    ) {
       onSuccess?.invoke( App.getString(R.string.success))


    }

    override fun notificationDone(
        id: String?,
        onSuccess: ((String) -> Unit)?,
        onError2: ((errorCode: Int, body: String?, error: VolleyError) -> Unit)?
    ) {
        if (MConstants.isInternetAvailable) {
            apiProvider.notificationDone(id, onSuccess, onError2)
        } else {
            onError2?.invoke(0, App.getString(R.string.no_internet), VolleyError(App.getString(R.string.no_internet)))
        }
    }

    override fun getDeviceInfo(
        deviceId: String,
        onSuccess: ((ModelResp<DeviceInfo>) -> Unit)?,
        onError2: ((errorCode: Int, body: String?, error: VolleyError) -> Unit)?
    ) {
        if (MConstants.isInternetAvailable) {
            apiProvider.getDeviceInfo(deviceId, onSuccess, onError2)
        } else {
            onError2?.invoke(0, App.getString(R.string.no_internet), VolleyError(App.getString(R.string.no_internet)))
        }

    }



    override fun updateLocation(
        location: LocationReqModel,
        onSuccess: ((String) -> Unit)?,
        onError2: ((errorCode: Int, body: String?, error: VolleyError) -> Unit)?
    ) {
        if (MConstants.isInternetAvailable) {
            apiProvider.updateLocation(location, onSuccess, onError2)
        } else {
            onError2?.invoke(0, App.getString(R.string.no_internet), VolleyError(App.getString(R.string.no_internet)))
        }
    }

    override fun sendDeviceSupportHDMI(
        onSuccess: ((String) -> Unit)?,
        onError2: ((errorCode: Int, body: String?, error: VolleyError) -> Unit)?,
        isSupport: Boolean
    ) {
        val details = DeviceDetails.getHDMIDetails(isSupport)
        if (MConstants.isInternetAvailable) {
            apiProvider.updateInfo(details, null, onError2)
        } else {
            onError2?.invoke(0, App.getString(R.string.no_internet), VolleyError(App.getString(R.string.no_internet)))
        }
    }

    override fun sendDeviceSupportSatellite(
        onSuccess: ((String) -> Unit)?,
        onError2: ((errorCode: Int, body: String?, error: VolleyError) -> Unit)?,
        isSupport: Boolean
    ) {
        val details = DeviceDetails.getSatelliteDetails(isSupport)
        if (MConstants.isInternetAvailable) {
            apiProvider.updateInfo(details, null, onError2)
        } else {
            onError2?.invoke(0, App.getString(R.string.no_internet), VolleyError(App.getString(R.string.no_internet)))
        }
    }

    override fun sendChannels(
        allTvList: ArrayList<ServiceEntity?>?,
        onSuccess: ((String) -> Unit)?,
        onError2: ((errorCode: Int, body: String?, error: VolleyError) -> Unit)?
    ) {
        apiProvider.sendChannels(allTvList, onSuccess, onError2)
    }


    override fun senDeviceHeap(
        heap: Long,
        onSuccess: ((String) -> Unit)?,
        onError2: ((errorCode: Int, body: String?, error: VolleyError) -> Unit)?
    ) {
        val details = DeviceDetails.getHeapDetails()
        if (MConstants.isInternetAvailable) {
            apiProvider.updateInfo(details, null, onError2)
        } else {
            onError2?.invoke(0, App.getString(R.string.no_internet), VolleyError(App.getString(R.string.no_internet)))
        }
    }

    override fun sendDeviceLog(
        media: MediaModel?,
        onSuccess: ((String) -> Unit)?,
        onError2: ((errorCode: Int, body: String?, error: VolleyError) -> Unit)?
    ) {
        if (MConstants.isInternetAvailable) {
            apiProvider.sendDeviceLog(media, onSuccess, onError2)
        } else {
            onError2?.invoke(0, App.getString(R.string.no_internet), VolleyError(App.getString(R.string.no_internet)))
        }
    }

    override fun sendRamLog(
        onSuccess: ((String) -> Unit)?,
        onError2: ((errorCode: Int, body: String?, error: VolleyError) -> Unit)?
    ) {
        if (MConstants.isInternetAvailable) {
            apiProvider.sendRamLog(onSuccess, onError2)
        } else {
            onError2?.invoke(0, App.getString(R.string.no_internet), VolleyError(App.getString(R.string.no_internet)))
        }
    }

    override fun sendDeviceLastLogin(
        onSuccess: ((String) -> Unit)?,
        onError2: ((errorCode: Int, body: String?, error: VolleyError) -> Unit)?
    ) {
        if (MConstants.isInternetAvailable) {
            apiProvider.sendDeviceLastLogin(onSuccess, onError2)
        } else {
            onError2?.invoke(0, App.getString(R.string.no_internet), VolleyError(App.getString(R.string.no_internet)))
        }
    }

    override fun getAllCampaigns(
        onSuccess: ((ModelResp<ArrayList<CampaignSimple>>) -> Unit)?,
        onError2: ((errorCode: Int, body: String?, error: VolleyError) -> Unit)?
    ) {
        if (MConstants.isInternetAvailable) {
            apiProvider.getAllCampaigns(onSuccess, onError2)
        } else {
            onError2?.invoke(0, App.getString(R.string.no_internet), VolleyError(App.getString(R.string.no_internet)))
        }
    }

    override fun assignCampaign(
        id:String,
        onSuccess: (() -> Unit)?,
        onError2: ((errorCode: Int, body: String?, error: VolleyError) -> Unit)?
    ) {
        if (MConstants.isInternetAvailable) {
            apiProvider.assignCampaign(id,onSuccess, onError2)
        } else {
            onError2?.invoke(0, App.getString(R.string.no_internet), VolleyError(App.getString(R.string.no_internet)))
        }
    }

    override fun getCampaignById(
        id: String,
        onSuccess: ((Campaign) -> Unit)?,
        onError2: ((errorCode: Int, body: String?, error: VolleyError) -> Unit)?
    ) {
        if (MConstants.isInternetAvailable) {
            apiProvider.getCampaignById(id,onSuccess, onError2)
        } else {
            onError2?.invoke(0, App.getString(R.string.no_internet), VolleyError(App.getString(R.string.no_internet)))
        }
    }

}
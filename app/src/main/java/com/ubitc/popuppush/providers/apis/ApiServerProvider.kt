package com.ubitc.popuppush.providers.apis

import com.android.volley.Request
import com.android.volley.VolleyError
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ubitc.popuppush.MConstants
import com.ubitc.popuppush.api_send_model.SerialNumber
import com.ubitc.popuppush.models.AssignCampaignReqModel
import com.ubitc.popuppush.models.Campaign
import com.ubitc.popuppush.models.CampaignResponse
import com.ubitc.popuppush.models.CampaignSimple
import com.ubitc.popuppush.models.ChannelReqModel
import com.ubitc.popuppush.models.DeviceDetailsRequestModel
import com.ubitc.popuppush.models.DeviceInfo
import com.ubitc.popuppush.models.FCMReqModel
import com.ubitc.popuppush.models.LastLoginLogReqModel
import com.ubitc.popuppush.models.LocationReqModel
import com.ubitc.popuppush.models.LogReqModel
import com.ubitc.popuppush.models.MediaModel
import com.ubitc.popuppush.models.RamLogReqModel
import com.ubitc.popuppush.models.UriReqModel
import com.ubitc.popuppush.providers.BackgroundProcess
import com.ubitc.popuppush.service.apis.ApiMainFunction
import com.ubitc.popuppush.ui.login.Device
import com.ubitc.popuppush.views.satellite.ServiceEntity

class ApiServerProvider : ApiMainFunction() {

    override fun addDevice(
        serialNumberSrt: SerialNumber,
        onSuccess: ((ModelResp<DeviceInfo>) -> Unit)?,
        onError2: ((errorCode: Int, body: String?, error: VolleyError) -> Unit)?
    ) {
        APICaller.Builder(Request.Method.POST)
            .needToken(false)
            .subUrl(APILinks.addDevice)
            .body(MConstants.serialNumberSrt)
            .build(object : ApisResponse {
                override fun onSuccess(response: String) {
                    val type = object : TypeToken<ModelResp<DeviceInfo?>?>() {}.type
                    val resp = Gson().fromJson<ModelResp<DeviceInfo>>(response, type)
                    if (onSuccess != null)
                        onSuccess(resp)
                }

                override fun onError(errorCode: Int, body: String?, error: VolleyError) {
                    if (onError2 != null)
                        onError2(errorCode, body, error)
                }

            })
    }

    override fun getMedia(
        onSuccess: ((CampaignResponse) -> Unit)?,
        onError2: ((errorCode: Int, body: String?, error: VolleyError) -> Unit)?
    ) {
        APICaller.Builder(Request.Method.GET)
            .subUrl(APILinks.getMedia)
            //   .body(MConstants.serialNumberSrt)
            .build(object : ApisResponse {
                override fun onSuccess(response: String) {

                    val type = object : TypeToken<CampaignResponse>() {}.type
                    var resp = Gson().fromJson<CampaignResponse>(response, type)
                    resp = handleAndCheckLocalUrl(resp)

                    if (onSuccess != null)
                        onSuccess(resp)

                }

                override fun onError(errorCode: Int, body: String?, error: VolleyError) {
                    if (onError2 != null)
                        onError2(errorCode, body, error)
                }

            })
    }

    private fun handleAndCheckLocalUrl(resp: CampaignResponse?): CampaignResponse {
        if (resp!!.isEmpty()) {
            return resp
        }
        resp.getCampaignFromResponse()?.MAIN?.iterator()?.forEach()
        { it2 ->
            if (it2.MAIN.isNullOrEmpty()) {
                it2.checkFileExist()
            } else {
                it2.MAIN?.iterator()?.forEach {
                    it.checkFileExist()
                }

            }
            it2.PLAYGROUND?.iterator()?.forEach()
            {
                it.checkFileExist()
            }
            it2.CORNER?.iterator()?.forEach()
            {
                it.checkFileExist()
            }

        }
        resp.getCampaignFromResponse()?.PLAYGROUND?.iterator()?.forEach()
        {
            it.checkFileExist()
        }
        resp.getCampaignFromResponse()?.CORNER?.iterator()?.forEach()
        {
            it.checkFileExist()
        }

        resp.getScheduleCampaigns()?.iterator()?.forEach()
        {
            sc ->
            sc.MAIN?.iterator()?.forEach()
            { it2 ->
                if (it2.MAIN.isNullOrEmpty()) {
                    it2.checkFileExist()
                } else {
                    it2.MAIN?.iterator()?.forEach {
                        it.checkFileExist()
                    }

                }
                it2.PLAYGROUND?.iterator()?.forEach()
                {
                    it.checkFileExist()
                }
                it2.CORNER?.iterator()?.forEach()
                {
                    it.checkFileExist()
                }

            }
            sc.PLAYGROUND?.iterator()?.forEach()
            {
                it.checkFileExist()
            }
            sc.CORNER?.iterator()?.forEach()
            {
                it.checkFileExist()
            }
        }
        return resp
    }

    override fun updateInfo(
        deviceInfo: DeviceDetailsRequestModel,
        onSuccess: ((ModelResp<DeviceInfo>) -> Unit)?,
        onError2: ((errorCode: Int, body: String?, error: VolleyError) -> Unit)?
    ) {
        APICaller.Builder(Request.Method.POST)
            .subUrl(APILinks.updateInfo)
            .body(deviceInfo)
            .build(object : ApisResponse {
                override fun onSuccess(response: String) {
                    val type = object : TypeToken<ModelResp<DeviceInfo?>?>() {}.type
                    val resp = Gson().fromJson<ModelResp<DeviceInfo>>(response, type)
                    if (onSuccess != null)
                        onSuccess(resp)
                }

                override fun onError(errorCode: Int, body: String?, error: VolleyError) {
                    if (onError2 != null)
                        onError2(errorCode, body, error)
                }

            })
    }

    override fun updateUri(
        uriReqModel: UriReqModel,
        onSuccess: ((String) -> Unit)?,
        onError2: ((errorCode: Int, body: String?, error: VolleyError) -> Unit)?
    ) {
        APICaller.Builder(Request.Method.PUT)
            .subUrl(APILinks.updateUri)
            .restartOn401(false)
            .body(uriReqModel)
            .build(object : ApisResponse {
                override fun onSuccess(response: String) {
                    if (onSuccess != null)
                        onSuccess(response)

                }

                override fun onError(errorCode: Int, body: String?, error: VolleyError) {
                    if (onError2 != null)
                        onError2(errorCode, body, error)
                }

            })
    }

    override fun updateFCM(
        fcmReqModel: FCMReqModel?,
        onSuccess: ((String) -> Unit)?,
        onError2: ((errorCode: Int, body: String?, error: VolleyError) -> Unit)?
    ) {
        APICaller.Builder(Request.Method.PUT)
            .subUrl(APILinks.updateFCM)
            .body(fcmReqModel)
            .build(object : ApisResponse {
                override fun onSuccess(response: String) {
                    if (onSuccess != null)
                        onSuccess(response)

                }

                override fun onError(errorCode: Int, body: String?, error: VolleyError) {
                    if (onError2 != null)
                        onError2(errorCode, body, error)
                }

            })
    }


    override fun notificationDone(
        id: String?,
        onSuccess: ((String) -> Unit)?,
        onError2: ((errorCode: Int, body: String?, error: VolleyError) -> Unit)?
    ) {

        APICaller.Builder(Request.Method.PUT)
            .subUrl(APILinks.notificationDone(id!!))
            .build(object : ApisResponse {
                override fun onSuccess(response: String) {
                    if (onSuccess != null)
                        onSuccess(response)

                }

                override fun onError(errorCode: Int, body: String?, error: VolleyError) {
                    if (onError2 != null)
                        onError2(errorCode, body, error)
                }

            })
    }

    override fun getDeviceInfo(
        deviceId: String,
        onSuccess: ((ModelResp<DeviceInfo>) -> Unit)?,
        onError2: ((errorCode: Int, body: String?, error: VolleyError) -> Unit)?
    ) {
        APICaller.Builder(Request.Method.GET)
            .subUrl(APILinks.getDeviceInfo(deviceId))
            .build(object : ApisResponse {
                override fun onSuccess(response: String) {
                    val type = object : TypeToken<ModelResp<DeviceInfo?>?>() {}.type
                    val resp = Gson().fromJson<ModelResp<DeviceInfo>>(response, type)
                    if (onSuccess != null)
                        onSuccess(resp)

                }

                override fun onError(errorCode: Int, body: String?, error: VolleyError) {
                    if (onError2 != null)
                        onError2(errorCode, body, error)
                }

            })
    }


    override fun updateLocation(
        location: LocationReqModel,
        onSuccess: ((String) -> Unit)?,
        onError2: ((errorCode: Int, body: String?, error: VolleyError) -> Unit)?
    ) {
        APICaller.Builder(Request.Method.POST)
            .subUrl(APILinks.updateLocation(Device.deviceId!!))
            .body(location)
            .build(object : ApisResponse {
                override fun onSuccess(response: String) {
                    if (onSuccess != null)
                        onSuccess(response)

                }

                override fun onError(errorCode: Int, body: String?, error: VolleyError) {
                    if (onError2 != null)
                        onError2(errorCode, body, error)
                }

            })
    }

    override fun sendDeviceSupportHDMI(
        onSuccess: ((String) -> Unit)?,
        onError2: ((errorCode: Int, body: String?, error: VolleyError) -> Unit)?,
        isSupport: Boolean
    ) {
//        APICaller.Builder(Request.Method.POST)
//            .SubUrl("/location/${MConstants.getDeviceInfo()!!.id}")
//            .build(object : ApisResponse {
//                override fun onSuccess(response: String) {
//                    if (onSuccess != null)
//                        onSuccess(response)
//
//                }
//
//                override fun onError(errorCode: Int, body: String?, error: VolleyError) {
//                    if (onError2 != null)
//                        onError2(errorCode, body, error)
//                }
//
//            })
    }

    override fun sendDeviceSupportSatellite(
        onSuccess: ((String) -> Unit)?,
        onError2: ((errorCode: Int, body: String?, error: VolleyError) -> Unit)?,
        isSupport: Boolean
    ) {

    }

    override fun sendChannels(
        allTvList: ArrayList<ServiceEntity?>?,
        onSuccess: ((String) -> Unit)?,
        onError2: ((errorCode: Int, body: String?, error: VolleyError) -> Unit)?
    ) {
        APICaller.Builder(Request.Method.POST)
            .body(ChannelReqModel(allTvList))
            .subUrl(APILinks.sendChannels)
            .build(object : ApisResponse {
                override fun onSuccess(response: String) {
                    if (onSuccess != null)
                        onSuccess(response)

                }

                override fun onError(errorCode: Int, body: String?, error: VolleyError) {
                    if (onError2 != null)
                        onError2(errorCode, body, error)
                }

            })
    }


    override fun senDeviceHeap(
        heap: Long,
        onSuccess: ((String) -> Unit)?,
        onError2: ((errorCode: Int, body: String?, error: VolleyError) -> Unit)?
    ) {
       // TODO("Not yet implemented")
    }

    override fun sendDeviceLog(
        media: MediaModel?,
        onSuccess: ((String) -> Unit)?,
        onError2: ((errorCode: Int, body: String?, error: VolleyError) -> Unit)?
    ) {
        val req = LogReqModel()
        req.campaignId =  "" //TODO()
        req.mediaId = media?.mediaId
        req.layer = media?.layer
        req.duration = media?.customDuration.toString()
        req.availableRam = BackgroundProcess.getInstance().ram.toString()

        APICaller.Builder(Request.Method.POST)
            .body(req)
            .subUrl(APILinks.sendDeviceLog)
            .build(object : ApisResponse {
                override fun onSuccess(response: String) {
                    if (onSuccess != null)
                        onSuccess(response)

                }

                override fun onError(errorCode: Int, body: String?, error: VolleyError) {
                    if (onError2 != null)
                        onError2(errorCode, body, error)
                }

            })
    }

    override fun sendRamLog(
        onSuccess: ((String) -> Unit)?,
        onError2: ((errorCode: Int, body: String?, error: VolleyError) -> Unit)?
    ) {
        val req = RamLogReqModel(BackgroundProcess.getInstance().ram.toString())
        APICaller.Builder(Request.Method.POST)
            .body(req)
            .restartOn401(false)
            .subUrl(APILinks.sendRAMLog)
            .build(object : ApisResponse {
                override fun onSuccess(response: String) {
                    if (onSuccess != null)
                        onSuccess(response)

                }

                override fun onError(errorCode: Int, body: String?, error: VolleyError) {
                    if (onError2 != null)
                        onError2(errorCode, body, error)
                }

            })
    }

    override fun sendDeviceLastLogin(
        onSuccess: ((String) -> Unit)?,
        onError2: ((errorCode: Int, body: String?, error: VolleyError) -> Unit)?
    ) {
        val req = LastLoginLogReqModel(System.currentTimeMillis())
        APICaller.Builder(Request.Method.PUT)
            .body(req)
            .restartOn401(false)
            .subUrl(APILinks.sendLastLogin)
            .build(object : ApisResponse {
                override fun onSuccess(response: String) {
                    if (onSuccess != null)
                        onSuccess(response)

                }

                override fun onError(errorCode: Int, body: String?, error: VolleyError) {
                    if (onError2 != null)
                        onError2(errorCode, body, error)
                }

            })
    }


    override fun assignCampaign(
        id: String,
        onSuccess: (() -> Unit)?,
        onError2: ((errorCode: Int, body: String?, error: VolleyError) -> Unit)?
    ) {
        APICaller.Builder(Request.Method.PUT)
            .restartOn401(false)
            .body(AssignCampaignReqModel(id,Device.deviceId!!))
            .subUrl(APILinks.assignCampaign)
            .build(object : ApisResponse {
                override fun onSuccess(response: String) {
                    onSuccess?.invoke()

                }

                override fun onError(errorCode: Int, body: String?, error: VolleyError) {
                    if (onError2 != null)
                        onError2(errorCode, body, error)
                }

            })
    }

    override fun getCampaignById(
        id: String,
        onSuccess: ((Campaign) -> Unit)?,
        onError2: ((errorCode: Int, body: String?, error: VolleyError) -> Unit)?
    ) {
        APICaller.Builder(Request.Method.GET)
            .restartOn401(false)
            .subUrl(APILinks.getCampaignById(id))
            .build(object : ApisResponse {
                override fun onSuccess(response: String) {
                    val type = object : TypeToken<ModelResp<Campaign>>() {}.type
                    val resp = Gson().fromJson<ModelResp<Campaign>>(response, type)
                    if (onSuccess != null)
                        onSuccess(resp.data)

                }

                override fun onError(errorCode: Int, body: String?, error: VolleyError) {
                    if (onError2 != null)
                        onError2(errorCode, body, error)
                }

            })
    }



    override fun getAllCampaigns(
        onSuccess: ((ModelResp<ArrayList<CampaignSimple>>) -> Unit)?,
        onError2: ((errorCode: Int, body: String?, error: VolleyError) -> Unit)?
    ) {

        APICaller.Builder(Request.Method.GET)
            .restartOn401(false)
            .subUrl(APILinks.getCompanyCampaigns)
            .build(object : ApisResponse {
                override fun onSuccess(response: String) {
                    val type = object : TypeToken<ModelResp<ArrayList<CampaignSimple>>>() {}.type
                    val resp = Gson().fromJson<ModelResp<ArrayList<CampaignSimple>>>(response, type)
                    if (onSuccess != null)
                        onSuccess(resp)

                }

                override fun onError(errorCode: Int, body: String?, error: VolleyError) {
                    if (onError2 != null)
                        onError2(errorCode, body, error)
                }

            })
    }


}
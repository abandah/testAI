package com.ubitc.popuppush.ui.login

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.StrictMode
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.android.volley.VolleyError
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.security.ProviderInstaller
import com.ubitc.popuppush.App
import com.ubitc.popuppush.MConstants
import com.ubitc.popuppush.R
import com.ubitc.popuppush.models.CampaignResponse
import com.ubitc.popuppush.models.DeviceDetails
import com.ubitc.popuppush.models.DeviceInfo
import com.ubitc.popuppush.providers.BackgroundProcess
import com.ubitc.popuppush.service.apis.ApiService
import com.ubitc.popuppush.service.campaignman.CampaignManagerService
import com.ubitc.popuppush.service.db.DbService
import com.ubitc.popuppush.ui.BaseViewModel
import com.ubitc.popuppush.ui.main_view.MainActivity
import javax.net.ssl.SSLContext


class LoginScreenViewModel : BaseViewModel() {
    private var code: String? = null
    var visibilityOfProgress: MutableLiveData<Int> = MutableLiveData(View.GONE)
    var visibilityOfQrCode: MutableLiveData<Int> = MutableLiveData(View.VISIBLE)
    var gifVideoIntroDone: MutableLiveData<Boolean> = MutableLiveData(false)
    var codeValue: MutableLiveData<String> = MutableLiveData("")
    var message: MutableLiveData<String> = MutableLiveData("")
    var description: MutableLiveData<String> = MutableLiveData("")

    var deviceNameValue: MutableLiveData<String> = MutableLiveData("")
    var deviceSerialValue: MutableLiveData<String> = MutableLiveData("")
    var visibilityOfDeviceName = MutableLiveData(View.GONE)

    var userNameValue: MutableLiveData<String> = MutableLiveData("")
    var visibilityOfUserName = MutableLiveData(View.GONE)

    var companyNameValue: MutableLiveData<String> = MutableLiveData("")
    var visibilityOfCompanyName = MutableLiveData(View.GONE)

//    var campaignNameValue: MutableLiveData<String> = MutableLiveData("")
//    var visibilityOfCampaignName = MutableLiveData(View.GONE)

    var splashNav: LoginNav? = null


    fun start() {
        Device.permissionStepDone = true
        installServiceProviderIfNeeded(App.activity?.get())


    }

    private fun installServiceProviderIfNeeded(context: Context?) {
        Thread {
            if (Device.installServiceProviderIfNeededDone == true) {
                Handler(Looper.getMainLooper()).post {
                    getCodeAndTokenFirstTime()
                }

                return@Thread
            }
            if (!MConstants.isInternetAvailable) {
                Handler(Looper.getMainLooper()).post {
                    getCodeAndTokenFirstTime()
                }

                return@Thread
            }
            try {
                ProviderInstaller.installIfNeeded(context!!)
            } catch (e: GooglePlayServicesRepairableException) {
                e.printStackTrace()
                //   GooglePlayServicesUtil.showErrorNotification(e.connectionStatusCode, context!!)
            } catch (e: GooglePlayServicesNotAvailableException) {
                e.printStackTrace()
            } finally {
                val sslContext: SSLContext = SSLContext.getInstance("TLSv1.2")
                sslContext.init(null, null, null)
                sslContext.createSSLEngine()
                val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
                StrictMode.setThreadPolicy(policy)
                Handler(Looper.getMainLooper()).post {
                    getCodeAndTokenFirstTime()
                }
            }
        }.start()
    }

    fun getString(id: Int): String {
        return App.activity?.get()!!.getString(id)
    }

    private fun getCodeAndTokenFirstTime() {

        Device.installServiceProviderIfNeededDone = true
        if (Device.Token != null && Device.isAddedToCompany == true) {
            updateDeviceDetails()
            return

        }
        showMessage(middleMessage = getString(R.string.checking_Device), showProgress = true)
        ApiService.getInstance().addDevice(MConstants.serialNumberSrt, onSuccess = { it2 ->
            val res = it2.data
            this.code = res.code
            Device.Token = it2.data.token
            Device.isActive = it2.data.isActive()
            Device.isAddedToCompany = it2.data.isAddedToCompany()
            Device.isCampaignAdded = it2.data.isCampaignAdded()
            Device.deviceId = it2.data.id

            updateDeviceDetails()


        }, onError2 = { errorCode: Int, _: String?, _: VolleyError ->
            if (errorCode == 0 && Device.Token != null) {
                updateDeviceDetails()
                return@addDevice
            } else if (errorCode == 0 && Device.Token == null) {
                showNoInternetDialog()
                return@addDevice
            }
            start()
        })
    }


    private fun updateDeviceDetails() {
        if (Device.deviceDetailsUpdated == true) {
            gettingLocation()
            return
        }
        showMessage(
            middleMessage = getString(R.string.updating_device_details),
            showProgress = true
        )
        val req = DeviceDetails.getFullDeviceDetails(App.currentActivity!!)

        ApiService.getInstance().updateInfo(req, onSuccess = {

            gettingLocation()
        }) { errorCode: Int, _: String?, _: VolleyError ->

            if (errorCode == 0) {
                if (Device.Token.isNullOrEmpty()) {
                    showNoInternetDialog()
                } else
                    gettingLocation()
                return@updateInfo
            }
            start()
        }
    }


    private fun gettingLocation() {
        Device.deviceDetailsUpdated = true
        if (Device.locationDone == true) {
            checkHDMI()
            return
        }
        showMessage(middleMessage = getString(R.string.checking_Location), showProgress = true)
        checkHDMI()
    }

    private fun checkHDMI() {
        //  checkChannels()

        Device.locationDone = true
        if (Device.checkHDMIDone == true) {
            checkChannels()
            return
        }
        showMessage(middleMessage = getString(R.string.checking_HDMI), showProgress = true)
        BackgroundProcess.getInstance().startCheckHDMI(App.currentActivity!!) {
            checkChannels()
        }


    }

    private fun checkChannels() {
//        checkYoutube()
//        if (true)
//            return
        Device.checkHDMIDone = true
        if (Device.checkChannelsDone == true) {
            checkYoutube()
            return
        }
        showMessage(middleMessage = getString(R.string.check_channels), showProgress = true)
        BackgroundProcess.getInstance().startCheckSatellite(App.currentActivity!!) {
            checkYoutube()

        }


    }

    private fun checkYoutube() {
//        getDeviceInfoFun()
//        if (true)
//            return
        Device.checkChannelsDone = true
        if (Device.checkYoutubeDone == true) {
            getDeviceInfoFun()
            return
        }
        showMessage(middleMessage = getString(R.string.checking_youtube), showProgress = true)
        BackgroundProcess.getInstance().startCheckYoutube {
            getDeviceInfoFun()
        }


    }

    private fun getDeviceInfoFun() {
        Device.checkYoutubeDone = true
        if (Device.needToGetNewInfo == false) {
            handelDeviceInfo(Device.deviceInfo)
            return
        }
        showMessage(middleMessage = getString(R.string.gettingDeviceInfo), showProgress = true)
        ApiService.getInstance()
            .getDeviceInfo(Device.deviceId!!, onSuccess = { it2 ->
                val res = it2.data
                Device.deviceInfo = res
                Device.Token = it2.data.token
                Device.isActive = it2.data.isActive()
                Device.isAddedToCompany = it2.data.isAddedToCompany()
                Device.isCampaignAdded = it2.data.isCampaignAdded()
                Device.deviceId = it2.data.id
                Device.country_name = it2.data.country_name
                Device.is_prayer_enable = it2.data.is_prayer_enable

                handelDeviceInfo(res)


            }, onError2 = { errorCode: Int, _: String?, _: VolleyError ->
                if (errorCode == 0) {
                    if (Device.Token.isNullOrEmpty()) {
                        showNoInternetDialog()
                    } else {
                        handelDeviceInfo(Device.deviceInfo)
                    }
                    return@getDeviceInfo
                }
                start()
            })
    }

    private fun showNoInternetDialog() {
        splashNav?.showNoInternetDialog()
    }


    fun handelDeviceInfo(res: DeviceInfo?) {
        res?.name?.let {
            deviceNameValue.value = it
            visibilityOfDeviceName.value = View.VISIBLE
            deviceSerialValue.value = MConstants.serialNumberSrt.serialName
        } ?: run { visibilityOfDeviceName.value = View.GONE }


        res?.company_name?.let {
            companyNameValue.value = it
            visibilityOfCompanyName.value = View.VISIBLE
        } ?: run { visibilityOfCompanyName.value = View.GONE }

        res?.user_name?.let {
            userNameValue.value = it
            visibilityOfUserName.value = View.VISIBLE
        } ?: run { visibilityOfUserName.value = View.GONE }


        if (Device.deviceInfo == null) {
            Device.needToGetNewInfo = true
            getDeviceInfoFun()
            return
        }

        Device.needToGetNewInfo = false

        if (res!!.isAddedToCompany()) {
            if (res.isActive()) {
                if (res.isCampaignAdded()) {
                    getChannelMedia()
                } else {
                    showMessage(
                        middleMessage = "No campaign assigned",
                        topMessage = "Device Status",
                        des = "Please assign a campaign to this device"
                    )
                }
            } else {
                showMessage(
                    middleMessage = "Device is inactive",
                    topMessage = "Device Status",
                    des = "Please activate your device from the admin panel"
                )
            }
        } else {
            if (res.code.isNullOrBlank()) {
                Device.reset {
                    start()
                }
                return

            }
            showMessage(
                middleMessage = res.code,
                topMessage = "Device Code",
                des = "Please enter the code in the admin panel to activate the device"
            )
        }
    }


    private fun getChannelMedia() {
        val campaignFromDB = DbService.getInstance().getCampaign()
        if (Device.isCampaignAdded == true && campaignFromDB != null && campaignFromDB.MAIN?.isEmpty() == false) {
            splashNav?.openActivity(MainActivity::class.java)
            return
        }

        showMessage(middleMessage = getString(R.string.get_Media_fromServer), showProgress = true)
        ApiService.getInstance().getMedia(onSuccess = { campaignResponse ->
            handleResponse(campaignResponse)
        }, onError2 = { errorCode: Int, _: String?, _: VolleyError ->
            val res = DbService.getInstance().getCampaign()
            if (res != null && res.MAIN?.isEmpty() == false) {
                splashNav?.openActivity(MainActivity::class.java)
                return@getMedia
            }
            if (errorCode == 0 && (res == null || res.MAIN?.isEmpty() == true)) {
                showNoInternetDialog()
                return@getMedia
            } else {
                showMessage(
                    middleMessage = "The campaign is empty",
                    topMessage = "Device Status",
                    des = "Please add content to the campaign"
                )
            }

        })
    }
    private fun handleResponse(campaignResponse: CampaignResponse) {
        if (campaignResponse.isEmpty()) {
            Device.isCampaignAdded = false
            showMessage(
                middleMessage = "The campaign is empty",
                topMessage = "Device Status",
                des = "Please add content to the campaign"
            )
            return
        }

        showMessage(
            middleMessage = getString(R.string.saveMediaToDisk),
            showProgress = true
        )

        val main = campaignResponse.getCampaignFromResponse()

        CampaignManagerService.getInstance().saveCampaign(main!!) { campaign ->
            Device.isCampaignAdded = true
            campaignResponse.setCampaignFromResponse(campaign)
            startSaveScheduledCampaigns(campaignResponse)
        }

    }

    private fun startSaveScheduledCampaigns(campaignResponse: CampaignResponse) {
        val scheduled = campaignResponse.getScheduleCampaigns()
        if (!scheduled.isNullOrEmpty()) {
            CampaignManagerService.getInstance().saveCampaignList(scheduled) { campaignList ->
                campaignResponse.scheduled_campaigns = campaignList
                CampaignManagerService.getInstance().saveCampaignIndex(campaignResponse)
                CampaignManagerService.getInstance().changeToCampaign(campaignResponse.getCampaignFromResponse()?.campaign_id){
                  //  splashNav?.openActivity(MainActivity::class.java)
                }
            }
        }else{
            campaignResponse.scheduled_campaigns = null
            CampaignManagerService.getInstance().saveCampaignIndex(campaignResponse)
            CampaignManagerService.getInstance().changeToCampaign(campaignResponse.getCampaignFromResponse()?.campaign_id){
              //  splashNav?.openActivity(MainActivity::class.java)
            }
        }
    }

    fun showMessage(
        topMessage: String? = null,
        middleMessage: String? = null,
        des: String? = null,
        showProgress: Boolean = false
    ) {

        Thread {
            val topMessage2: String = if (topMessage.isNullOrEmpty()) "" else topMessage

            var middleMessage2: String = if (middleMessage.isNullOrEmpty()) "" else middleMessage
            val code = middleMessage2.replace(" ", "").toIntOrNull() ?: 0

            if (code > 0) {
                splashNav?.showQrImage(code)
                visibilityOfQrCode.postValue(View.VISIBLE)
            } else {
                visibilityOfQrCode.postValue(View.GONE)
            }

            middleMessage2 = if (code > 0) {
                middleMessage2.replace("", "  ")
                    .trim { it <= ' ' }

            } else {
                middleMessage2
            }

            val des2: String = if (des.isNullOrEmpty()) "" else des
            val showProgress2: Boolean =
                showProgress || (middleMessage.isNullOrEmpty() && topMessage.isNullOrEmpty() && des.isNullOrEmpty())


            this.message.postValue(topMessage2)
            this.description.postValue(des2)
            this.codeValue.postValue(middleMessage2)
            this.visibilityOfProgress.postValue(if (showProgress2) View.VISIBLE else View.GONE)

        }.start()

    }


}

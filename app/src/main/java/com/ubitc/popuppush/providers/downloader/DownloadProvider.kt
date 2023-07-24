package com.ubitc.popuppush.providers.downloader

import android.os.Handler
import android.os.Looper
import com.android.volley.VolleyError
import com.ubitc.popuppush.App
import com.ubitc.popuppush.AppMainFeatureConstants
import com.ubitc.popuppush.MConstants
import com.ubitc.popuppush.models.Campaign
import com.ubitc.popuppush.models.MediaModel
import com.ubitc.popuppush.service.apis.ApiService
import com.ubitc.popuppush.service.downloader.DownloadMainFunction
import java.util.LinkedList

class DownloadProvider : DownloadMainFunction {


    // var context: Context? = null
    companion object {
        @Volatile
        var providerInstance: DownloadProvider? = null
        fun getInstance(): DownloadProvider {
            // if (providerInstance == null) {
            providerInstance = DownloadProvider()
            //   }
            return providerInstance!!
        }
    }

    init {
        App.currentActivity?.getRootView()?.addDownloadListener(this)

    }

    private var onSuccess: ((Campaign?) -> Unit?)? = null

    private var cashedItems = kotlin.collections.ArrayList<MediaModel>()
    private fun addLinkToQueue(mediaModel: MediaModel): Boolean {
        val availableToDownload: Boolean =
            mediaModel.localUri.isNullOrEmpty() && mediaModel.downloadable!!
        if (availableToDownload) {
            val index = cashedItems.indexOf(mediaModel)
            if (index == -1) {
                cashedItems.add(mediaModel)
            }

            return true
        }
        return false
    }

    private fun callOnSuccess() {
        ApiService.getInstance().getCampaignById(downloadingCampaign?.campaign_id!!, onSuccess = {
            val res = it
            downloadingCampaign?.MAIN = res.MAIN
            onSuccess?.invoke(downloadingCampaign)

        }, onError2 = { _: Int, _: String?, _: VolleyError ->

        })


    }

    private fun getMediasFromMains(campaign: Campaign): LinkedList<MediaModel> {
        val mainMediaList: LinkedList<MediaModel> = LinkedList()
        campaign.MAIN?.let {
            for (main in it) {
                main.MAIN?.let { it2 ->
                    for (main2 in it2) {
                        mainMediaList.add(main2)
                    }
                } ?: mainMediaList.add(main)
                main.PLAYGROUND?.let { playlistPlayGround ->
                    for (playground in playlistPlayGround) {
                        mainMediaList.add(playground)
                    }
                }
                main.CORNER?.let { playListCorner ->
                    for (corner in playListCorner) {
                        mainMediaList.add(corner)
                    }
                }
            }

        }
        campaign.PLAYGROUND?.let {
            for (playground in it) {
                mainMediaList.add(playground)
            }
        }
        campaign.CORNER?.let {
            for (corner in it) {
                mainMediaList.add(corner)
            }
        }
        return mainMediaList
    }


    private fun startDownload() {

        if (cashedItems.isEmpty()) {
            callOnSuccess()
            return
        }
        Thread {
            val array = cashedItems.take(AppMainFeatureConstants.numberOfDownloadItemInRecyclerView)
            cashedItems.removeAll(array.toSet())
            App.currentActivity?.getRootView()?.addItems(array)
        }.start()
    }

    override fun downloadFinished(mediaModel: MediaModel) {
        Handler(Looper.getMainLooper()).post {
            if (cashedItems.isEmpty()) {
                callOnSuccess()
                return@post
            }
            Thread {
                val array =
                    cashedItems.take(AppMainFeatureConstants.numberOfDownloadItemInRecyclerView)
                cashedItems.removeAll(array.toSet())
                App.currentActivity?.getRootView()?.addItems(array)
            }.start()
        }
    }

    // var waitingList =  ArrayList<Campaign>()
    //  var downloadingCampaign :Campaign ? = null
    private var downloadingCampaign: Campaign? = null
    fun gatherMediaAndDownloadThemAndDoNotChangeCampaign(
        campaign: Campaign,
        onSuccess: ((Campaign?) -> Unit)? = null
    ) {
        if (downloadingCampaign != null) {
            return
        }
//        downloadingCampaign = campaign
//        if(waitingList.indexOf(campaign) == -1){
//            waitingList.remove(campaign)
//        }
        downloadingCampaign = campaign

        if (onSuccess != null) {
            this.onSuccess = onSuccess
        }
        if (!AppMainFeatureConstants.downloadMediaToDeviceEnabled) {
            callOnSuccess()
            return
        }
        Thread {
            if (MConstants.isInternetAvailable) {
                var countOfThisCampaign = 0
                campaign.let {
                    val items = getMediasFromMains(it)
                    if (items.size > 0) {
                        for (item in items) {
                            if (addLinkToQueue(item)) {
                                countOfThisCampaign++
                            }
                        }
                    }
                }
                if (cashedItems.isEmpty()) {
                    callOnSuccess()
                } else {
                    if (cashedItems.size <= countOfThisCampaign) {
                        Handler(Looper.getMainLooper()).post {
                            startDownload()
                        }
                    }

                }
            } else {
                callOnSuccess()
            }
        }.start()


    }


}
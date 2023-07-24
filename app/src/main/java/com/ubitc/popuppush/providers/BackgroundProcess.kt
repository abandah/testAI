package com.ubitc.popuppush.providers


import android.os.Handler
import android.os.Looper
import com.ubitc.popuppush.App
import com.ubitc.popuppush.R
import com.ubitc.popuppush.asynctasks.RamTask
import com.ubitc.popuppush.models.MediaModel
import com.ubitc.popuppush.providers.info.api.RAMInfo
import com.ubitc.popuppush.providers.info.impl.androidsdk.pages.ram.DeviceRAMInfo
import com.ubitc.popuppush.providers.info.impl.androidsdk.pages.ram.recycler.TotalRAMInfoItem
import com.ubitc.popuppush.providers.realtimedatabase.RealTimeDBProvider
import com.ubitc.popuppush.service.apis.ApiService
import com.ubitc.popuppush.ui.BaseActivity
import com.ubitc.popuppush.views.hdmi.HDMIView
import com.ubitc.popuppush.views.satellite.SatelliteView
import com.ubitc.popuppush.views.tools.MyView
import com.ubitc.popuppush.views.tools.ViewListener

class BackgroundProcess {
    private var ramInfo: RAMInfo? = null
    val ramSendDuration = 10000L
    private val lastLoginSendDuration = 10000L

    companion object {
        private var INSTANCE: BackgroundProcess? = null
        fun getInstance(): BackgroundProcess {
            if (INSTANCE == null) {
                INSTANCE = BackgroundProcess()
            }
            return INSTANCE!!
        }
    }


    fun startUpdatingIAmAlive() {
        RealTimeDBProvider.getInstance(duration = lastLoginSendDuration).startUpdatingIAmAlive()
    }

    var ram = 0L
    var heap = 0L
    fun startUpdatingLastAvailableRam() {
        Thread {
            updatingLastAvailableRam()
        }.start()

    }

    private fun updatingLastAvailableRam() {
        ramInfo = DeviceRAMInfo(App.activity?.get()!!)
        ramInfo?.loadState()
        val totalRam = TotalRAMInfoItem(App.activity?.get()!!, ramInfo!!).body()
        val totalHeap = Runtime.getRuntime().maxMemory() / 1024 / 1024
        val handler = Handler(Looper.getMainLooper())
        handler.post(object : Runnable {
            override fun run() {
                RamTask(totalRam.toLong(), totalHeap).executeAsync("") {
                    ram = it!![0]
                    heap = it[1]
                    if (App.currentActivity!!.logDialogIsShowing) {
                        val mb = App.getString(R.string.MB)
                        val ramInfo = "$ram $mb / $totalRam $mb"
                        val heapInfo = "$heap $mb / $totalHeap $mb"

                        Handler(Looper.getMainLooper()).post {
                            App.currentActivity?.ramInfo(ramInfo)
                            App.currentActivity?.heapInfo(heapInfo)
                        }
                    }
                    ApiService.getInstance().sendRamLog()
                    ApiService.getInstance().senDeviceHeap(heap)
                }
                // val availableRam = Runtime.getRuntime().freeMemory() / 1024 / 1024

                handler.postDelayed(this, ramSendDuration)
            }
        })
    }

    fun startCheckHDMI(mainActivity: BaseActivity, onSuccess: () -> Unit) {
        // Thread() {
        checkHDMI(mainActivity, onSuccess)
        //    }.start()
    }


    private fun checkHDMI(mainActivity: BaseActivity, onSuccess: () -> Unit) {
        Thread.sleep(2000)
        var hdmi: HDMIView? = HDMIView(mainActivity)


        hdmi?.prepareView(null, object : ViewListener {
            override fun finished(view: MyView?) {
            }

            override fun viewIsReady(view: MyView?) {
                ApiService.getInstance().sendDeviceSupportHDMI(isSupport = true)
                hdmi?.forceStop()
                hdmi = null
                Handler(Looper.getMainLooper()).post {
                    onSuccess.invoke()
                }

            }

            override fun showLayer1(media: MediaModel?) {
            }

            override fun showLayer2(media: MediaModel?) {
            }

            override fun showLayer3(media: MediaModel?) {
            }

            override fun viewNotSupported(view: MyView?) {
                ApiService.getInstance().sendDeviceSupportHDMI(isSupport = false)
                hdmi?.forceStop()
                hdmi = null
                Handler(Looper.getMainLooper()).post {
                    onSuccess.invoke()
                }
            }
        })
    }

    fun startCheckSatellite(mainActivity: BaseActivity, onSuccess: () -> Unit) {
        //  Thread() {
        checkSatellite(mainActivity, onSuccess)
        //   }.start()
    }

    private fun checkSatellite(mainActivity: BaseActivity, onSuccess: () -> Unit) {
        Thread.sleep(2000)
        var sat: SatelliteView? = SatelliteView(mainActivity)
        val media = MediaModel()
        media.path = "100"
        sat?.prepareView(media, object : ViewListener {
            override fun finished(view: MyView?) {
            }

            override fun viewIsReady(view: MyView?) {
//                view!!.playView(view.media)
//                if ((view as SatelliteView).isConnected()) {
                    ApiService.getInstance().sendDeviceSupportSatellite(isSupport = true)
                    ApiService.getInstance().sendChannels((view as SatelliteView).allTvList)
                    sat?.forceStop()
                    sat = null
                    Handler(Looper.getMainLooper()).post {
                        onSuccess.invoke()
                    }
//                } else {
//                    Handler(Looper.getMainLooper()).post {
//                        onSuccess.invoke()
//                    }
//                    // TODO()
//
//                }
            }

            override fun showLayer1(media: MediaModel?) {
            }

            override fun showLayer2(media: MediaModel?) {
            }

            override fun showLayer3(media: MediaModel?) {
            }

            override fun viewNotSupported(view: MyView?) {
                ApiService.getInstance().sendDeviceSupportSatellite(isSupport = false)
                sat?.forceStop()
                sat = null
                Handler(Looper.getMainLooper()).post {
                    onSuccess.invoke()
                }
            }
        })
    }

    fun startCheckYoutube(onSuccess: () -> Unit) {
        Thread {
            checkYoutube(onSuccess)
        }.start()

    }

    private fun checkYoutube(onSuccess: () -> Unit) {
        Thread.sleep(2000)
        Handler(Looper.getMainLooper()).post {
            onSuccess.invoke()
        }

    }

    fun startPrayingTimeChecker() {
        RealTimeDBProvider.getInstance().startPrayingTimeChecker()
    }
}


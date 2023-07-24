@file:Suppress("PropertyName")
package com.ubitc.popuppush.models

import android.content.Context
import android.os.Environment
import android.os.StatFs
import com.google.gson.annotations.SerializedName
import com.ubitc.popuppush.BuildConfig
import com.ubitc.popuppush.providers.BackgroundProcess
import com.ubitc.popuppush.providers.info.api.CPUInfo
import com.ubitc.popuppush.providers.info.api.RAMInfo
import com.ubitc.popuppush.providers.info.api.SystemInfo
import com.ubitc.popuppush.providers.info.impl.androidsdk.pages.cpu.DeviceCPUInfo
import com.ubitc.popuppush.providers.info.impl.androidsdk.pages.cpu.recycler.CPUCoresInfoItem
import com.ubitc.popuppush.providers.info.impl.androidsdk.pages.cpu.recycler.CPUFreqInfoItem
import com.ubitc.popuppush.providers.info.impl.androidsdk.pages.ram.DeviceRAMInfo
import com.ubitc.popuppush.providers.info.impl.androidsdk.pages.ram.recycler.TotalRAMInfoItem
import com.ubitc.popuppush.providers.info.impl.androidsdk.pages.system.DeviceSystemInfo
import com.ubitc.popuppush.providers.info.impl.androidsdk.pages.system.recycler.ApiLevelInfoItem
import com.ubitc.popuppush.providers.info.impl.androidsdk.pages.system.recycler.ReleaseVersionInfoItem

class DeviceDetails {

    @SerializedName("api_level")
    var apiLevel: String? = null

    @SerializedName("android_release_version")
    var androidReleaseVersion: String? = null

    @SerializedName("cpu_core")
    var cpuCore: String? = null

    @SerializedName("cpu_freq")
    var cpuFreq: String? = null

    @SerializedName("ram_available")
    var ramAvailable: String? = null

    @SerializedName("ram_total")
    var ramTotal: String? = null

  @SerializedName("heap_total")
    var heap_total: String? = null


  @SerializedName("heap_available")
    var heap_available: String? = null


  @SerializedName("HDMI_is_available")
    var HDMI: Int? = null

  @SerializedName("satellite_is_available")
    var satellite: Int? = null

    @SerializedName("storage_available")
    var storageAvailable: String? = null

    @SerializedName("storage_total")
    var storageTotal: String? = null

    @SerializedName("version_code")
    var versionCode: String? = null

    @SerializedName("version_name")
    var versionName: String? = null



    companion object {
        fun getFullDeviceDetails(context: Context): DeviceDetailsRequestModel {
            val deviceDetails = DeviceDetails()

            val systemInfo: SystemInfo = DeviceSystemInfo()
            val cpuInfo: CPUInfo = DeviceCPUInfo()
            val ramInfo: RAMInfo = DeviceRAMInfo(context)
            ramInfo.loadState()

            deviceDetails.apiLevel = ApiLevelInfoItem(context, systemInfo).body()
            deviceDetails.androidReleaseVersion =
                ReleaseVersionInfoItem(context, systemInfo).body()
            deviceDetails.cpuCore = CPUCoresInfoItem(context, cpuInfo).body()
            deviceDetails.cpuFreq = CPUFreqInfoItem(context, cpuInfo).body()
            deviceDetails.ramTotal = TotalRAMInfoItem(context, ramInfo).body()
            deviceDetails.storageAvailable = getAvailableInternalMemorySize()
            deviceDetails.storageTotal = getTotalInternalMemorySize()
            deviceDetails.versionCode = BuildConfig.VERSION_CODE.toString()
            deviceDetails.versionName = BuildConfig.VERSION_NAME
            deviceDetails.heap_total = getTotalHeapSize()
            deviceDetails.HDMI = 0
            deviceDetails.satellite = 0
            deviceDetails.ramAvailable = 0.toString()
            deviceDetails.heap_available = 0.toString()

            return DeviceDetailsRequestModel( deviceDetails)
        }


        fun getHeapDetails(): DeviceDetailsRequestModel {
            val deviceDetails = DeviceDetails()
            deviceDetails.heap_available = BackgroundProcess.getInstance().heap.toString()
            return DeviceDetailsRequestModel( deviceDetails)
        }


        fun getHDMIDetails(isEnabled:Boolean): DeviceDetailsRequestModel {
            val deviceDetails = DeviceDetails()
            deviceDetails.HDMI =if(isEnabled) 1 else 0
            return DeviceDetailsRequestModel( deviceDetails)
        }

        fun getSatelliteDetails(isEnabled:Boolean): DeviceDetailsRequestModel {
            val deviceDetails = DeviceDetails()
            deviceDetails.satellite =if(isEnabled) 1 else 0
            return DeviceDetailsRequestModel( deviceDetails)
        }



        private fun getTotalHeapSize(): String {
            val maxMemory =Runtime.getRuntime().maxMemory()/1024/1024
            return maxMemory.toString()
        }

        private fun getAvailableInternalMemorySize(): String {
            val path = Environment.getDataDirectory()
            val stat = StatFs(path.path)
            val blockSize = stat.blockSizeLong
            val availableBlocks = stat.availableBlocksLong
            return formatSize(availableBlocks * blockSize)
        }

        private fun getTotalInternalMemorySize(): String {
            val path = Environment.getDataDirectory()
            val stat = StatFs(path.path)
            val blockSize = stat.blockSizeLong
            val totalBlocks = stat.blockCountLong
            return formatSize(totalBlocks * blockSize)
        }


        private fun formatSize(size: Long): String {
            var size1 = size
            //var suffix: String? = null
            if (size1 >= 1024) {
              //  suffix = "KB"
                size1 /= 1024
                if (size1 >= 1024) {
                 //   suffix = "MB"
                    size1 /= 1024
                }
            }
            val resultBuffer = StringBuilder(size1.toString())
            var commaOffset = resultBuffer.length - 3
            while (commaOffset > 0) {
                resultBuffer.insert(commaOffset, ',')
                commaOffset -= 3
            }
            return resultBuffer.toString()
        }

    }
}

class DeviceDetailsRequestModel(device_info: DeviceDetails){
    @SerializedName("device_info")
    var device_info: DeviceDetails? = null

    init {
        this.device_info = device_info
    }
}
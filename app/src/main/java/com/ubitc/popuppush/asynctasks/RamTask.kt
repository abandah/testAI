package com.ubitc.popuppush.asynctasks

import com.ubitc.popuppush.App
import com.ubitc.popuppush.providers.info.api.RAMInfo
import com.ubitc.popuppush.providers.info.impl.androidsdk.pages.ram.DeviceRAMInfo
import com.ubitc.popuppush.providers.info.impl.androidsdk.pages.ram.recycler.AvailableRAMInfoItem
import com.ubitc.popuppush.taskrunner.TaskRunner

class RamTask (private var totalRam :Long, private var totalHeap:Long) : TaskRunner<String, Int, Array<Long>>() {



    override fun doInBackground(params: String): Array<Long> {
        val ramInfo: RAMInfo = DeviceRAMInfo(App.currentActivity!!)
        ramInfo.loadState()
        val availableRam = AvailableRAMInfoItem(App.currentActivity!!, ramInfo).body()
        val usedRam = totalRam -availableRam.toLong()

        val value =
            totalHeap - (Runtime.getRuntime().totalMemory() / 1024/1024)

        // Debug.getNativeHeapAllocatedSize() android.os.Build.VERSION.SDK_INT >= 11

        val heap = totalHeap-value
        val  arrayResults= arrayOf(usedRam,heap)
        onPostExecute(arrayResults)
        return arrayResults
    }

    override fun onProgressUpdate(progress: Int) {

    }

    override fun onPreExecute() {

    }

    fun onSuccess() {
        onPostExecute(null)

    }


}
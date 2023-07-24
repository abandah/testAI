package com.ubitc.popuppush.providers.info.impl.androidsdk.pages.cpu.recycler

import android.content.Context
import com.ubitc.popuppush.R
import com.ubitc.popuppush.providers.info.api.CPUInfo
import com.ubitc.popuppush.providers.info.impl.androidsdk.pages.cpu.Ghz
import com.ubitc.popuppush.providers.info.impl.androidsdk.pages.recycler.InfoItem

class CPUFreqInfoItem(
    private val context: Context,
    private val cpuInfo: CPUInfo
) : InfoItem {

    override fun title() = context.getString(R.string.item_info_title_cpu_freq)

    override fun body(): String {
        val minimumFreq = Ghz(
            cpuInfo.minimumFreq()
        ).value()
        val maximumFreq = Ghz(
            cpuInfo.maximumFreq()
        ).value()
        return "%.1f - %.1f".format(minimumFreq, maximumFreq)
    }

}

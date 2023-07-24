package com.ubitc.popuppush.providers.info.impl.androidsdk.pages.ram.recycler

import android.content.Context
import com.ubitc.popuppush.R
import com.ubitc.popuppush.providers.info.api.RAMInfo
import com.ubitc.popuppush.providers.info.impl.androidsdk.pages.recycler.InfoItem

class TotalRAMInfoItem(
    private val context: Context,
    private val ramInfo: RAMInfo
) : InfoItem {

    override fun title() = context.getString(R.string.item_info_title_ram_total)

    override fun body() = "${ramInfo.totalRAM()}"

}

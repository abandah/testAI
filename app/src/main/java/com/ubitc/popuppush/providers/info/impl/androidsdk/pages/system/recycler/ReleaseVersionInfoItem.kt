package com.ubitc.popuppush.providers.info.impl.androidsdk.pages.system.recycler

import android.content.Context
import com.ubitc.popuppush.R
import com.ubitc.popuppush.providers.info.api.SystemInfo
import com.ubitc.popuppush.providers.info.impl.androidsdk.pages.recycler.InfoItem

class ReleaseVersionInfoItem(
    private val context: Context,
    private val systemInfo: SystemInfo
) : InfoItem {

    override fun title() = context.getString(R.string.item_info_title_system_release_version)

    override fun body() = systemInfo.releaseVersion()

}

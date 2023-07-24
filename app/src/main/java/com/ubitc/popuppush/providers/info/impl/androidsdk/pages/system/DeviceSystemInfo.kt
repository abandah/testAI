package com.ubitc.popuppush.providers.info.impl.androidsdk.pages.system


import android.os.Build
import com.ubitc.popuppush.providers.info.api.SystemInfo
import java.util.*

class DeviceSystemInfo : SystemInfo {

    override fun releaseVersion(): String = Build.VERSION.RELEASE

    @Suppress("DEPRECATION")
    override fun apiLevel(): String = Build.VERSION.SDK



//    private fun errorResult() = context.getString(R.string.common_empty_result)

}

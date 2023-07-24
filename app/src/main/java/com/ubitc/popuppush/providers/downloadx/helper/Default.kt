package com.ubitc.popuppush.providers.downloadx.helper

import com.ubitc.popuppush.App
import com.ubitc.popuppush.AppMainFeatureConstants

object Default {

    val DEFAULT_SAVE_PATH = App.activity?.get()!!.cacheDir.toString() + "/popuppush"

    const val DEFAULT_RANGE_SIZE = 5L * 1024 * 1024


    const val DEFAULT_RANGE_CURRENCY = AppMainFeatureConstants.numberOfDownloadItemInRecyclerView


    const val MAX_TASK_NUMBER = AppMainFeatureConstants.downloadTaskMaxNumber


    val RANGE_CHECK_HEADER = mapOf("Range" to "bytes=0-")
}
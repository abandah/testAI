package com.ubitc.popuppush.providers.info.api

interface CPUInfo {

    fun cores(): Int
    fun minimumFreq(): Int
    fun maximumFreq(): Int

}

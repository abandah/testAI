package com.ubitc.popuppush.providers.info.api

@SuppressWarnings("TooManyFunctions")
interface SystemInfo {

    fun releaseVersion(): String
    fun apiLevel(): String

}

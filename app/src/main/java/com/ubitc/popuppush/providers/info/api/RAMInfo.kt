package com.ubitc.popuppush.providers.info.api

interface RAMInfo {

    fun loadState()
    fun totalRAM(): Long
    fun availableRAM(): Long

}

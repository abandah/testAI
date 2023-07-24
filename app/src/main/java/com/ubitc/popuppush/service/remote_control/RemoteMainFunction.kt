package com.ubitc.popuppush.service.remote_control

interface RemoteMainFunction {
    fun campaignAssigned()
    fun updateMedia()
    fun deviceDetached()
    fun deviceFlushed()
    fun deviceRestart()
    fun statusUpdate()
    fun deviceDeleted()
    fun playPause(b: Boolean? = null)
    fun prayerStart(toBoolean: Boolean)

}
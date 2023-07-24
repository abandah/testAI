package com.ubitc.popuppush.api_send_model

import com.google.gson.annotations.SerializedName

class SerialNumber(serial: String) {
    @SerializedName("serial_name")
    var serialName: String? = null
    init {
        this.serialName = serial
    }
}


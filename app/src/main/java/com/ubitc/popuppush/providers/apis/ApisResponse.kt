package com.ubitc.popuppush.providers.apis
import com.android.volley.VolleyError

interface ApisResponse {
    fun onSuccess(response:String)
    fun onError(errorCode: Int, body: String?, error: VolleyError)
}
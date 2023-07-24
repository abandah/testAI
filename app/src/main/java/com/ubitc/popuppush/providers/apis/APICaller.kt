package com.ubitc.popuppush.providers.apis


import android.util.Log
import com.android.volley.DefaultRetryPolicy
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ubitc.popuppush.App
import com.ubitc.popuppush.R
import com.ubitc.popuppush.ui.login.Device
import org.json.JSONObject
import java.nio.charset.Charset
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.util.TimeZone
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import kotlin.math.abs


class APICaller(
    private val method: Int,
    private val subUrl: String,
    private val body: Any?,
    private val needToken: Boolean,
    private val restartOn401: Boolean
) {
    companion object {
        var stopQueue = false
        var queue: RequestQueue? = null
    }

    fun run(mainResponse: ApisResponse?) {
        if(stopQueue) {
            return
        }
        if (needToken && Device.Token == null) {
            stopQueue = true
            //Device.deviceDeleted()
            queue?.cancelAll { true }
            stopQueue = true
            App.currentActivity?.deviceDeleted()
            return
        }
        val requestBody = convertObjToUrlParam(body)

        val url = subUrl
        val stringReq: StringRequest = object : StringRequest(method, url, Response.Listener {
            mainResponse?.onSuccess(it)
        }, Response.ErrorListener { error ->


            val  methodStr = "Method : ${when (method) {
                0 -> "GET"
                1 -> "POST"
                2 -> "PUT"
                else -> "Unknown"
            }} \n"
            val urlStr = "URL : $url \n"
            val bodyStr = "Body : ${when (method) {
                0 -> requestBody
                else -> JSONObject(Gson().toJson(this@APICaller.body)).toString()
            }} \n"

            val errorStr = "Error : ${error.message} \n"
            log(methodStr + urlStr + bodyStr + errorStr)



            val body = if(error?.networkResponse?.data != null) {
                String(error.networkResponse.data, Charsets.UTF_8)
            } else {
                null
            }

            val statusCode = error?.networkResponse?.statusCode ?: -1


            if (statusCode == 401 && restartOn401){
                stopQueue = true
                queue?.cancelAll { true }

                App.currentActivity?.deviceDeleted()
                return@ErrorListener
            }

            mainResponse?.onError(errorCode = statusCode, body, error)

        }) {
            override fun getBody(): ByteArray {
                return when (method) {
                    Method.GET -> {
                        return requestBody.toByteArray(Charset.defaultCharset())
                    }
                    else -> {

                        JSONObject(Gson().toJson(this@APICaller.body)).toString()
                            .toByteArray(Charset.defaultCharset())
                    }
                }


            }

            override fun getHeaders(): MutableMap<String, String> {
                var headers: MutableMap<String, String> = super.getHeaders()
                if (headers.isEmpty()) {
                    headers = HashMap()
                }
                //     headers["Content-Type"] = "application/x-www-form-urlencoded"
                headers[App.getString(R.string.content_key)] = App.getString(R.string.content_value)
                headers[App.getString(R.string.accept_key)] = App.getString(R.string.accept_value)
                val timeZone = TimeZone.getDefault()
                val offsetInMillis = timeZone.rawOffset
                val offsetHours = offsetInMillis / (1000 * 60 * 60)
                val offsetMinutes = abs(offsetInMillis / (1000 * 60) % 60)

                val sign = if (offsetInMillis >= 0) "+" else "-"

                val timeZoneString = "GMT$sign${padZero(offsetHours)}:${padZero(offsetMinutes)}"
                headers["Timezone"] = timeZoneString
                Device.Token?.let { token ->
                    headers[App.getString(R.string.authorization_key)] =
                        "${App.getString(R.string.authorization_bearer)} $token"
                }

                return headers
            }

            override fun getBodyContentType(): String {
                return App.getString(R.string.accept_value)
            }

        }

        stringReq.retryPolicy =
            DefaultRetryPolicy(10 * 1000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        if (queue == null) {
            val trustManager = provideX509TrustManager()
            queue = Volley.newRequestQueue(
                App.activity?.get()!!, HurlStack(null, ClientSSLSocketFactory(trustManager!!))
            )
            queue!!.add(stringReq)
        } else {
            queue!!.add(stringReq)
        }

    }
    fun padZero(number: Int): String {
        return String.format("%02d", number)
    }
    private fun log(url: String) {
        Log.e("APIS", url)
    }

    private fun convertObjToUrlParam(string: Any?): String {
        if (string == null) return ""
        val gson = Gson()
        val json = gson.toJson(string)
        val mapType = object : TypeToken<Map<String, Any>>() {}.type
        val map: Map<String, Any> = gson.fromJson(json, mapType)
        val sb = StringBuilder()
        for ((key, value) in map) {
            if (sb.isNotEmpty()) {
                sb.append("&")
            }
            sb.append(key).append("=").append(value)
        }
        return sb.toString()
    }

    data class Builder(private var method: Int) {
        private var subUrl: String = ""
        private var body: Any? = null
        private var needToken = true
        private var restartOn401 = true

        fun subUrl(SubUrl: String) = apply { this.subUrl = SubUrl }
        fun body(body: Any?) = apply { this.body = body }

        fun needToken(needToken: Boolean) = apply { this.needToken = needToken }
        fun build(mainResponse: ApisResponse?) {
            val apiCaller = APICaller(method, subUrl, body, needToken,restartOn401)
            apiCaller.run(mainResponse)
        }

        fun restartOn401(b: Boolean) = apply { this.restartOn401 = b }


    }

    private fun provideX509TrustManager(): TrustManager? {
        try {
            val factory: TrustManagerFactory =
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            factory.init(null as KeyStore?)
            val trustManagers: Array<TrustManager> = factory.trustManagers
            return trustManagers[0]
        } catch (exception: NoSuchAlgorithmException) {
            Log.e(javaClass.simpleName, "not trust manager available", exception)
        } catch (exception: KeyStoreException) {
            Log.e(javaClass.simpleName, "not trust manager available", exception)
        }
        return null
    }
}
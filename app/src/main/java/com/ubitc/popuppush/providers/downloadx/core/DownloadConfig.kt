package com.ubitc.popuppush.providers.downloadx.core

import com.ubitc.popuppush.providers.downloadx.helper.Default.DEFAULT_RANGE_CURRENCY
import com.ubitc.popuppush.providers.downloadx.helper.Default.DEFAULT_RANGE_SIZE
import com.ubitc.popuppush.providers.downloadx.helper.apiCreator
import okhttp3.ResponseBody
import retrofit2.Response

class DownloadConfig(

    val disableRangeDownload: Boolean = false,

    val taskManager: TaskManager = DefaultTaskManager,

    val queue: DownloadQueue = DefaultDownloadQueue.get(),


    private val customHeader: Map<String, String> = emptyMap(),


    val rangeSize: Long = DEFAULT_RANGE_SIZE,

    val rangeCurrency: Int = DEFAULT_RANGE_CURRENCY,


    val dispatcher: DownloadDispatcher = DefaultDownloadDispatcher,


    val validator: FileValidator = DefaultFileValidator,


    httpClientFactory: HttpClientFactory = DefaultHttpClientFactory
) {
    private val api = apiCreator(httpClientFactory.create())

    suspend fun request(url: String, header: Map<String, String>): Response<ResponseBody> {
        val tempHeader = mutableMapOf<String, String>().also {
            it.putAll(customHeader)
            it.putAll(header)
        }
        return api.get(url, tempHeader)
    }
}
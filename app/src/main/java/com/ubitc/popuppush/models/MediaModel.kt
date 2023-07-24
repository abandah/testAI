@file:Suppress("PropertyName")

package com.ubitc.popuppush.models

import com.google.gson.annotations.SerializedName
import com.ubitc.popuppush.MConstants
import com.ubitc.popuppush.providers.downloadx.helper.Default
import com.ubitc.popuppush.service.apis.ApiService
import java.io.File

open class MediaModel {
    @SerializedName("local_uri")
    var localUri: String? = null
    var layer: String? = null

    @SerializedName("default_duration")
    var default_duration: Int? = null

    @SerializedName("custom_duration")
    var customDuration: Int? = null
        get() {
            when (MediaTypes.getIntValue(mediaType!!)) {
                1, 2 ->{
                    if (field == null || field == 0 )
                        field = 0
                }
                3 ,4,5,6,7-> {
                    if (field == null|| field == 0 )
                        field = default_duration
                }
            }

            return field!! * 1000
        }

    //  @SerializedName("default_duration")
    // var defaultDuration: Int? = null // TODO: 2020-05-27  delete this
    @SerializedName("is_muted")
    var isMuted: Boolean = false
    var downloadable: Boolean? = null
        get() {
            if (field == true && !fileDownloaded()) {
                return true
            }
            return false
        }

    private fun fileDownloaded(): Boolean {
        if (localUri.isNullOrEmpty()) {
            return false
        }
        val f = localUri?.let { File(it) }
        if (!f!!.exists()) {
            return false
        }
        return true
    }

    @SerializedName("media_id")
    var mediaId: String? = null

    @SerializedName("file_name")
    var fileName: String? = null
    var path: String? = null
        get() {
            return if (!localUri.isNullOrEmpty())
                "$localUri"
            // "file:///$local_uri"
            else
                if (MConstants.isInternetAvailable) field else null
        }

    @SerializedName("media_type")
    var mediaType: String? = null

    @SerializedName("thumb")
    var thumb: String? = null

    @SerializedName("media_name")
    var mediaName: String? = null
    var size = 0

    //    @SerializedName("playlist_layer")
//    var playlistLayer: String? = null
    @SerializedName("playlist_id")
    var playlistId: String? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MediaModel) return false

        if (mediaId != other.mediaId) return false
        if (path != other.path) return false

        return true
    }

    override fun hashCode(): Int {
        var result = mediaId?.hashCode() ?: 0
        result = 31 * result + (path?.hashCode() ?: 0)
        return result
    }

    fun checkFileExist() {
        var isURIEmpty = false
        if (localUri == null || localUri!!.isEmpty()) {
            isURIEmpty = true
        }
        val fileUri = isFileExistOnDevice(fileName!!)
        var isFileExist = true
        if (fileUri.isNullOrEmpty()) {
            isFileExist = false
        }

        if (isURIEmpty && isFileExist) {
            localUri = fileUri
            ApiService.getInstance().updateUri(UriReqModel(localUri!!, mediaId!!), null, null)
        } else if (!isURIEmpty && !isFileExist) {
            localUri = null
            ApiService.getInstance().updateUri(UriReqModel("", mediaId!!), null, null)
        }


    }

    private fun isFileExistOnDevice(file_name: String): String? {
        val downloadsPath = Default.DEFAULT_SAVE_PATH
        val outputFile = File(downloadsPath, file_name)
        if (!outputFile.exists() || !outputFile.isFile) {

            localUri = null
            return null
        }
        return "$downloadsPath/$file_name"
    }

    fun isCashed(): String {
        return if (localUri.isNullOrEmpty()) "" else "Cashed"
    }

}
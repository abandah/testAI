package com.ubitc.popuppush.service.downloader

import com.ubitc.popuppush.models.MediaModel

interface  DownloadMainFunction {
     fun downloadFinished(mediaModel: MediaModel)
//    abstract fun downloadMedia(mediaModel: MediaModel)
//    abstract fun onProgressUpdate(progress :Int)
//    abstract fun onFailed(cashItem: MediaModel)
//    abstract fun onPreExecute()
//    abstract fun onDownloadComplete(cashItem: MediaModel)
//    abstract fun deleteAllMedia(function: () -> Unit)
}
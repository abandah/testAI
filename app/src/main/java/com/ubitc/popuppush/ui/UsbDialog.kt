package com.ubitc.popuppush.ui
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import com.ubitc.popuppush.App
import com.ubitc.popuppush.providers.realtimedatabase.RealTimeDBProvider

class UsbDialog : BaseViewModel() {
    @SuppressLint("StaticFieldLeak")
    private val context = App.currentActivity!!
    private var usbConnected: Boolean = false
    //private val usbFilesLiveData = MutableLiveData<List<Pair<String, String>>>()

    private val usbStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                if (it.action == Intent.ACTION_MEDIA_MOUNTED) {
                    val usbPath = it.data?.path
                    if (usbPath != null) {
                        usbConnected = true
                        start()
                    }
                } else if (it.action == Intent.ACTION_MEDIA_REMOVED ||
                    it.action == Intent.ACTION_MEDIA_UNMOUNTED ||
                    it.action == Intent.ACTION_MEDIA_BAD_REMOVAL
                ) {
                    usbConnected = false
                    // Prompt the user to insert the USB device
                    // You can display a dialog or show a Toast message here
                }
            }
        }
    }

    init {
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED)
        filter.addAction(Intent.ACTION_MEDIA_REMOVED)
        filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED)
        filter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL)
        filter.addDataScheme("file")
        context.registerReceiver(usbStateReceiver, filter)
    }

    override fun onCleared() {
        super.onCleared()
        context.unregisterReceiver(usbStateReceiver)
    }

    fun start() {
        if (usbConnected) {
            val usbFiles = getUsbMediaFiles()
            finishedGatherFiles(usbFiles)
        } else {

            val filter = IntentFilter()
            filter.addAction(Intent.ACTION_MEDIA_MOUNTED)
            filter.addAction(Intent.ACTION_MEDIA_REMOVED)
            filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED)
            filter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL)
            filter.addDataScheme("file")
            context.registerReceiver(usbStateReceiver, filter)
            // Prompt the user to insert the USB device
            // You can display a dialog or show a Toast message here
        }
    }

    @SuppressLint("Range")
    private fun getUsbMediaFiles(): List<Pair<String, String>> {
        val fileExtensions = listOf("mp4", "mp3", "png", "gif")
        val contentResolver: ContentResolver = context.contentResolver
        val mimeTypeMap: MimeTypeMap = MimeTypeMap.getSingleton()
        val mediaFiles = mutableListOf<Pair<String, String>>()

        for (extension in fileExtensions) {
            val mimeType = mimeTypeMap.getMimeTypeFromExtension(extension)
            if (mimeType != null) {
                val selection = MediaStore.Files.FileColumns.MIME_TYPE + "=?"
                val selectionArgs = arrayOf(mimeType)
                val projection = arrayOf(
                    MediaStore.Files.FileColumns.DISPLAY_NAME,
                    MediaStore.Files.FileColumns.DATA
                )
                val cursor = contentResolver.query(
                    MediaStore.Files.getContentUri("external"),
                    projection,
                    selection,
                    selectionArgs,
                    null
                )

                cursor?.use {
                    while (cursor.moveToNext()) {
                        val fileName =
                            cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME))
                        val filePath =
                            cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA))
                        mediaFiles.add(fileName to filePath)
                    }
                }
            }
        }

        return mediaFiles
    }

//    private fun MimeTypeMap.getMimeTypeFromExtension(extension: String): String? {
//        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase())
//    }

    private fun finishedGatherFiles(files: List<Pair<String, String>>) {
        RealTimeDBProvider.getInstance().sendUsbFiles(files)
    }
}
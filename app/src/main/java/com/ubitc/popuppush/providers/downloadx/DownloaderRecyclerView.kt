package com.ubitc.popuppush.providers.downloadx

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.ubitc.popuppush.models.MediaModel
import com.ubitc.popuppush.service.downloader.DownloadMainFunction

class DownloaderRecyclerView : RecyclerView, DownloadMainFunction {

    private var downloadMainFunction: DownloadMainFunction? = null

    constructor(context: Context?) : super(context!!) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!, attrs, defStyleAttr
    ) {
        init()
    }

    fun init() {
        val linearLayoutManager = FlexboxLayoutManager(context).apply {
            justifyContent = JustifyContent.CENTER
            alignItems = AlignItems.CENTER
            flexDirection = FlexDirection.ROW
            flexWrap = FlexWrap.WRAP
        }
        layoutManager = linearLayoutManager

     //   layoutAnimation = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_right_to_left)
     //   scheduleLayoutAnimation()
        adapter = DownloaderAdapter()
        adapter?.let {
            (it as DownloaderAdapter).addDownloadListener(this)
        }


    }


    fun addItems(array: List<MediaModel>) {
        Handler(Looper.getMainLooper()).postDelayed({
            adapter?.let {
                (it as DownloaderAdapter).addItems(array)
            }
        }, 500)


    }


    fun addDownloadListener(downloadMainFunction: DownloadMainFunction) {
        this.downloadMainFunction = downloadMainFunction
    }

    override fun downloadFinished(mediaModel: MediaModel) {
            downloadMainFunction?.downloadFinished(mediaModel)

    }


}
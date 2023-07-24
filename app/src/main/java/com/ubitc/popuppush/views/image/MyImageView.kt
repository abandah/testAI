package com.ubitc.popuppush.views.image

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.ubitc.popuppush.App
import com.ubitc.popuppush.MConstants
import com.ubitc.popuppush.models.MediaModel
import com.ubitc.popuppush.views.tools.MyView
import com.ubitc.popuppush.views.tools.ViewListener

class MyImageView : AppCompatImageView, MyView {
    override var media: MediaModel?=null
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

    override fun init() {
        this.scaleType = ScaleType.FIT_XY
        setBackgroundResource(android.R.color.transparent)
       // alpha = 0.0f
      //  setBackgroundResource(R.color.background)
    }

    override fun prepareView(media: MediaModel?, viewListener: ViewListener?) {
        super.prepareView(media, viewListener)
        val requestListener: RequestListener<Drawable?> = object : RequestListener<Drawable?> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any,
                target: Target<Drawable?>,
                isFirstResource: Boolean
            ): Boolean {
                viewListener?.finished(this@MyImageView)
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any,
                target: Target<Drawable?>,
                dataSource: DataSource,
                isFirstResource: Boolean
            ): Boolean {
                viewListener?.viewIsReady(this@MyImageView)
                return false
            }
        }
        Glide.with(App.activity?.get()!!).load(media!!.path).dontAnimate()
            .override(
                MConstants.dimension!!.widthPixels,
                MConstants.dimension!!.heightPixels
            )
            .listener(requestListener)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(this@MyImageView)


    }


    override fun isVisible(): Boolean {
        return this.visibility == VISIBLE
    }


    override fun forceStop() {

        Handler(Looper.getMainLooper()).post {
            Glide.with(App.activity?.get()!!).clear(this)
         //   setBackgroundResource(android.R.color.transparent)
        }
//        Glide.with(App.activity?.get()!!).clear(this)
//        setBackgroundResource(android.R.color.transparent)
        super.forceStop()

    }

    override fun pauseView() {
    }

    override fun resumeView() {
    }

    override fun playView(media: MediaModel?) {

    }
    override fun mute() {

    }

}
package com.ubitc.popuppush.views.corner

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.ubitc.popuppush.R
import com.ubitc.popuppush.models.MediaModel
import com.ubitc.popuppush.views.tools.MyView
import com.ubitc.popuppush.views.tools.ViewListener
import pl.droidsonroids.gif.GifDrawable
import java.io.FileNotFoundException


open class GifImageView : AppCompatImageView, MyView {
    private var mContext: Context? = null

    constructor(context: Context) : super(context) {
        mContext = context
    }

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int = 0) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        mContext = context
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.GifImageView)

        val src = typedArray.getResourceId(R.styleable.GifImageView_gif, 0)
        setGifImageResource(src)
        typedArray.recycle()
        init()
    }

    override var media: MediaModel? = null

    final override fun init() {
        setLayerType(LAYER_TYPE_HARDWARE, null)
        isFocusable = true
        requestLayout()
    }

    fun prepareView(media: String?, viewListener: ViewListener?) {
        val mediaModel = MediaModel()
        mediaModel.path = media
        prepareView(mediaModel, viewListener)
    }

    override fun prepareView(media: MediaModel?, viewListener: ViewListener?) {
        super.prepareView(media, viewListener)

        val filePath = media!!.path
        try {
            if (filePath!!.startsWith("http")) {

                if (filePath.endsWith(".gif")) {

                    GIFDownloader().executeAsync(filePath) {
                        val d = GifDrawable(it!!)
                        d.also { gif ->
                            if (filePath.endsWith(".gif")) {
                                gif.start()
                            }
                            setImageDrawable(gif)
                            viewListener?.viewIsReady(this@GifImageView)
                        }
                    }
                } else {
                    GIFDownloader().executeAsync(filePath) {
                        val image: Drawable = BitmapDrawable(
                            resources, BitmapFactory.decodeByteArray(it, 0, it!!.size)
                        )
                        setImageDrawable(image)


                    }
                }

            } else if (filePath.endsWith(".gif")) {
                try {
                    val d = GifDrawable(filePath)
                    d.also {
                        it.start()
                        setImageDrawable(it)
                        viewListener?.viewIsReady(this@GifImageView)
                    }
                } catch (e: Exception) {
                    androidx.appcompat.graphics.drawable.AnimatedStateListDrawableCompat.createFromPath(
                        filePath
                    ).also {
                        setImageDrawable(it)
                        viewListener?.viewIsReady(this@GifImageView)
                    }
                }

            } else if (filePath.endsWith(".webp")) {

                Glide.with(this)
                    .load(filePath)
                    .into(this)
            } else {
                androidx.appcompat.graphics.drawable.AnimatedStateListDrawableCompat.createFromPath(
                    filePath
                ).also {
                    setImageDrawable(it)
                    viewListener?.viewIsReady(this@GifImageView)
                }

            }
        } catch (e: FileNotFoundException) {
            viewListener?.finished(this)
        }

    }


    override fun forceStop() {

        setLayerType(LAYER_TYPE_NONE, null)
        if (drawable is GifDrawable) {
            (drawable as GifDrawable).stop()
            (drawable as GifDrawable).recycle()
        }
        if (drawable is BitmapDrawable && (drawable as BitmapDrawable).bitmap != null) {
            (drawable as BitmapDrawable).bitmap.recycle()
        }
        if (drawable is Drawable) {
            (drawable as Drawable).callback = null
        }
        setImageDrawable(null)
        clearAnimation()
        invalidate()
        requestLayout()
        setImageBitmap(null)
        super.forceStop()
    }

    override fun isVisible(): Boolean {
        return visibility == VISIBLE
    }

    override fun pauseView() {

        if (drawable is GifDrawable) {
            (drawable as GifDrawable).pause() // Pause the GIF animation
        }
        if (drawable is BitmapDrawable && (drawable as BitmapDrawable).bitmap != null) {
        }
        if (drawable is Drawable) {
        }
    }

    override fun resumeView() {
        if (drawable is GifDrawable) {
            (drawable as GifDrawable).start() // Resume the GIF animation
        }
    }

    override fun playView(media: MediaModel?) {
        invalidate()
        requestLayout()
    }

    override fun mute() {


    }

    private fun setGifImageResource(id: Int) {
        if (id == 0)
            return
        requestLayout()
    }


}
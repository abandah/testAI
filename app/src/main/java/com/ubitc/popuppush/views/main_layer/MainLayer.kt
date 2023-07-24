package com.ubitc.popuppush.views.main_layer

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.util.Xml
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import com.ubitc.popuppush.App
import com.ubitc.popuppush.R
import com.ubitc.popuppush.models.MAIN
import com.ubitc.popuppush.models.MediaModel
import com.ubitc.popuppush.models.MediaTypes
import com.ubitc.popuppush.service.updator.LocalMediaService
import com.ubitc.popuppush.ui.main_view.MainActivity
import com.ubitc.popuppush.views.corner.GifImageView
import com.ubitc.popuppush.views.hdmi.HDMIView
import com.ubitc.popuppush.views.image.MyImageView
import com.ubitc.popuppush.views.local_player.MP4Player
import com.ubitc.popuppush.views.player.PlayerView
import com.ubitc.popuppush.views.satellite.SatelliteView
import com.ubitc.popuppush.views.satellite.ServiceEntity
import com.ubitc.popuppush.views.tools.CountDownTimer
import com.ubitc.popuppush.views.tools.MyView
import com.ubitc.popuppush.views.tools.ViewListener
import com.ubitc.popuppush.views.web_view.MyWebView

class MainLayer : FrameLayout, MyView {
    internal var isPaused: Boolean = false

    var viewListener: ViewListener? = null

    //  private var media: MediaModel? = null;
    private var attrs: AttributeSet? = null

    private var timer2: CountDownTimer? = null
    var view: MyView? = null
    private var oldView: View? = null

    private val enableTranslation = false

    constructor(context: Context?) : super(context!!) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
        this.attrs = attrs
        init()
    }

    override var media: MediaModel? = null


    override fun init() {
        view?.init()
    }

    override fun prepareView(media: MediaModel?, viewListener: ViewListener?) {
        if(isPaused)
            isPaused = false
        //  view = MP4Player(context, attrs)
        // super.prepareView(media, viewListener)
        if (media?.path.isNullOrBlank()) {

            viewListener?.finished(this)
            return
        }
        var tempView = this.view
        oldView = tempView as View?
//        oldView?.let {
//            this.oldView = MyImageView(context, attrs)
//        }

        this.view = null
        this.viewListener = viewListener
        val xmlAttributeSet = context.resources.getXml(R.xml.view_player).let {
            try {
                it.next()
                it.nextTag()
            } catch (e: Exception) {
                null
            }
            Xml.asAttributeSet(it)
        }
        val mp4xml = context.resources.getXml(R.xml.original_player).let {
            try {
                it.next()
                it.nextTag()
            } catch (e: Exception) {
                null
            }
            Xml.asAttributeSet(it)
        }
        when (MediaTypes.getIntValue(media!!.mediaType!!)) {
            1, 2 -> tempView = if (media.localUri != null && media.localUri!!.endsWith(".mp4")) {
                MP4Player(context, mp4xml)
            } else {
                PlayerView(context, xmlAttributeSet)

            }
            //    1, 2 -> tempView = PlayerView(context, xmlAttributeSet)
            3 -> tempView = GifImageView(context, attrs)
            4 -> tempView = MyImageView(context, attrs)
            5 -> tempView = HDMIView(context, attrs)
            6 -> tempView = SatelliteView(context, attrs)
            7 -> tempView = MyWebView(context, attrs)
        }

        // this@MainLayer.media = media
        val enable = if (oldView == null) false else enableTranslation
        if (enable)
            (tempView as View).alpha = 0.0f


        tempView?.prepareView(media, object : ViewListener {

            override fun finished(view: MyView?) {
                view?.pauseView()
                if (view == this@MainLayer.view) {
                    stopTimer()
                    viewListener?.finished(this@MainLayer)
                }
            }

            override fun viewIsReady(view: MyView?) {
//                if(oldView == null){
//                    oldView = view as View
//                }

                //  var newBitMap = loadBitmapFromView(view as View)
                if (!isPaused) {
                    //lastShowLayer1value = null
                //    lastShowLayer2value = null

                    if (view == tempView) {
//                        if (oldView != null && (oldView as MyView).media is MAIN) {
//
//                            val oldMedia: MAIN = (oldView as MyView).media as MAIN
//                            val newMedia: MAIN = view.media as MAIN
//                            if (oldMedia.show_layer_one != newMedia.show_layer_one) {
//                                lastShowLayer1value = null
//                            }
//                            if (oldMedia.show_layer_two != newMedia.show_layer_two) {
//                                lastShowLayer2value = null
//                            }
//
//                            if (oldMedia.playlistId != newMedia.playlistId) {
//                                lastShowLayer1value = null
//                                lastShowLayer2value = null
//                            }
//                        }


                        this@MainLayer.view = view
                        addView(view as View)
                        playView(getCurrentMedia())
                        viewListener?.viewIsReady(this@MainLayer)
                    }
                }

            }

        })
    }

    private fun getCurrentView(): MyView? {
        return view
    }

    fun getCurrentMedia(): MediaModel? {
        return view?.media
    }

    fun getCurrentMainMedia(): MAIN? {
        val media = view?.media
        if (media is MAIN)
            return (view?.media) as MAIN?
        return null
    }


    private fun startTranslation() {
        val enable = if (oldView == null) false else enableTranslation

        if (enable) {
            oldView?.apply {
                alpha = 1.0f
                ViewCompat.animate(this)
                    .alpha(0.0f)
                    .setDuration(2000)
                    .withEndAction {
                        (this as MyView).forceStop()
                        // removeView(this)
                        oldView = null

                    }
            }

            (view as View).apply {
                alpha = 0.0f
                ViewCompat.animate(this)
                    .alpha(1.0f)
                    .setDuration(2000)
                    .withEndAction {
                        setTimer()
                    }
            }

        } else {
            oldView?.let {
                (it as MyView).forceStop()
                //   removeView(it as View)
            }

            setTimer()
        }

    }

    override fun forceStop() {
        if (isPaused) {
            isPaused = false
        }

        // removeAllViews()
        Handler(Looper.getMainLooper()).post(Runnable {
            view?.forceStop()
            oldView?.let {
                (it as MyView).forceStop()
            }
            stopTimer()
        })

    }

    private fun stopTimer() {
        timer2?.cancel()
        timer2 = null

    }


    override fun isVisible(): Boolean {
        return view?.isVisible() == true
    }

    override fun pauseView() {
//        if (isPaused) {
//            return
//        }
        isPaused = true
        view?.pauseView()
        timer2?.pause()
    }

    override fun resumeView() {
        if (!isPaused) {
            return
        }
        isPaused = false
        Handler(Looper.getMainLooper()).post(Runnable {
            getCurrentMedia()?.customDuration?.let {
                if (it > 0) {
                    timer2?.resume(it.toLong())
                }
            }
            view?.resumeView()
        })


    }

    override fun playView(media: MediaModel?) {

        checkOnShowingLayerStatus(if (media is MAIN) media else null)

        (view as View).visibility = View.VISIBLE
        view?.playView()
        startTranslation()
        //removeView(oldView as View)
        //ApiService.getInstance().sendDeviceLog(media)


    }

    private fun setTimer() {

        stopTimer()
        if (getCurrentMedia()?.customDuration == 0) {
            return
        }

        val duration: Long = getCurrentMedia()?.customDuration?.toLong()!!
        timer2 = object : CountDownTimer(duration) {
            override fun onTick(millisUntilFinished: Long) {
                if (getCurrentMedia() is MAIN) {
                    (App.currentActivity as MainActivity).onTickMAIN(millisUntilFinished / 1000)
                }
            }

            override fun onFinish() {
                viewListener?.finished(this@MainLayer)
            }
        }

        //  (timer2 as CountDownTimer).prepare()
        (timer2 as CountDownTimer).start()

    }

    fun getNewMedia(onSuccess: () -> Unit) {
        lastShowLayer1value = null
        lastShowLayer2value = null

        getCurrentMainMedia()?.let {
            LocalMediaService.getInstance().getNewMedia { newMain ->

                if (newMain != null) {
                    if (newMain == getCurrentView()?.media) {
                        onSuccess()
                    }else{
                        Log.e("TAGTAG","case 1")
                        resetTheMedia()
                    }


                } else {
                    Log.e("TAGTAG","case 2")
                    resetTheMedia()

                }
            }
        } ?: run {
            Log.e("TAGTAG","case 3")
            resetTheMedia()
        }

    }

    var lastShowLayer1value: Boolean? = false
    var lastShowLayer2value: Boolean? = false
    private var lastShowLayer3value = false

    private fun checkOnShowingLayerStatus(main: MAIN? = null) {

        val media = main ?: getCurrentMainMedia()
        media?.let {
            //reflect on screen
            //   ((App.currentActivity) as MainActivity).updateListOnScreen()

            if (media.show_layer_one != lastShowLayer1value) {
                this.lastShowLayer1value = media.show_layer_one
                viewListener?.showLayer1(media as MAIN?)
            }
            if (media.show_layer_two != lastShowLayer2value) {
                this.lastShowLayer2value = media.show_layer_two
                viewListener?.showLayer2(media as MAIN?)
            }
            if (media.show_layer_three != lastShowLayer3value) {
                this.lastShowLayer3value = media.show_layer_three
                viewListener?.showLayer3(media as MAIN?)
            }


        }


    }


    override fun addView(child: View?) {
        try {
            if (childCount > 1) {
                removeViewAt(0)
            }

            super.addView(child)
            requestLayout()
        } catch (_: Exception) {
        }

    }

    override fun removeView(view: View?) {
        super.removeView(view)

        if (childCount > 1) {
            App.currentActivity?.let {
                if (it is MainActivity) {
                    it.pugDetected("Views count $childCount")
                }
            }
            if (childCount > 2) {
                removeAllViews()
            }
            try {
                removeViewAt(childCount - 1)
            } catch (_: Exception) {
            }
        }

    }

    override fun mute() {
        view?.mute()

    }

    fun nextMedia() {
        resumeView()
        view?.pauseView()
        stopTimer()
        viewListener?.finished(this@MainLayer)

    }

    fun previousMedia() {
        resumeView()

        LocalMediaService.getInstance().previousMedia()
        view?.pauseView()
        stopTimer()
        viewListener?.finished(this@MainLayer)


    }

    fun restartMedia() {
        resumeView()

        LocalMediaService.getInstance().resetCurrentMedia()
        view?.pauseView()
        stopTimer()
        viewListener?.finished(this@MainLayer)

    }

    fun resetTheMedia(){
        resumeView()
        LocalMediaService.getInstance().resetCurrentMedia()
        stopTimer()
        viewListener?.finished(this@MainLayer)
    }


    fun getChannel(): ArrayList<ServiceEntity?>? {
        if (this@MainLayer.view is SatelliteView) {
            return (this@MainLayer.view as SatelliteView).allTvList
        }
        return null

    }


}



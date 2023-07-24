package com.ubitc.popuppush.views.tools

import android.view.View
import android.widget.FrameLayout
import com.ubitc.popuppush.models.MediaModel

interface MyView{
    var media: MediaModel?

    fun init()

    fun prepareView(media: MediaModel?, viewListener: ViewListener?){
        (this as View).tag = media?.mediaName
        this.media = media
    }
    fun forceStop(){
        this.media = null
        val thisView = this as View?
        val parent = thisView?.parent

        parent?.let {
            if (parent is FrameLayout) {
                parent.removeView(thisView)
                parent.invalidate()
            }
        }
    }
    fun isVisible(): Boolean
    fun pauseView()
    fun resumeView()
    fun playView(media: MediaModel?= null)
    fun mute()


}
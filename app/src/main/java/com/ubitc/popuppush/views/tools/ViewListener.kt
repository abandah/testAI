package com.ubitc.popuppush.views.tools

import com.ubitc.popuppush.models.MediaModel

interface ViewListener {
    fun finished(view: MyView?){}
    fun viewIsReady(view: MyView?){}
    fun showLayer1(media: MediaModel?){}
    fun showLayer2(media: MediaModel?){}
    fun showLayer3(media: MediaModel?){}
    fun viewNotSupported(view: MyView?){}
}
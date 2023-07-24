package com.ubitc.popuppush.api_send_model

import com.ubitc.popuppush.service.db.DbService
import com.ubitc.popuppush.service.updator.LocalMediaService

class CurrentState(duration: Int = 0) {
    var mainIndex  : Int? = null
    private var nextMainIndex : Int? = null

    private var cornerIndex : Int? = null
    private var nextCornerIndex : Int? = null

    private var playgroundIndex : Int? = null
    private var nextPlaygroundIndex : Int? = null

    var currentDuration : Int? = null
    init {
        currentDuration = duration
        mainIndex = LocalMediaService.LocalMediaProvider.getInstance().mainIndex
        nextMainIndex = LocalMediaService.LocalMediaProvider.getInstance().nextMainIndex
        cornerIndex = LocalMediaService.LocalMediaProvider.getInstance().cornerIndex
        nextCornerIndex = LocalMediaService.LocalMediaProvider.getInstance().nextCornerIndex
        playgroundIndex = LocalMediaService.LocalMediaProvider.getInstance().playgroundIndex
        nextPlaygroundIndex = LocalMediaService.LocalMediaProvider.getInstance().nextPlaygroundIndex

        DbService.getInstance().saveCurrentState(this)

    }




}
package com.ubitc.popuppush.models

class ChannelReqModel(allTvList:ArrayList<*>?) {
    private var channels :ArrayList<*>? = null
    init {
        this.channels = allTvList
    }
}
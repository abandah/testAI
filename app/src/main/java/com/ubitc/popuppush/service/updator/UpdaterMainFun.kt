package com.ubitc.popuppush.service.updator

import com.ubitc.popuppush.models.MAIN
import com.ubitc.popuppush.models.MediaModel

interface UpdaterMainFun {
    fun getMediaToPlayFromMainLayerREAD(onSuccess:(main : MAIN?, index :Int) ->Unit)
    fun getMediaToPlayFromPLAYGROUNDREAD(mediaModel: MediaModel, onSuccess:(main : MediaModel?, index :Int) ->Unit)
    fun getMediaToPlayFromCORNERREAD(mediaModel: MediaModel, onSuccess:(main : MediaModel?, index :Int) ->Unit)
    fun getMediaToPlayFromThirdLayer(onSuccess:(main : MediaModel?) ->Unit)
    fun syncMedia(mediaModel: MAIN,onSuccess:(main : MAIN?) ->Unit)
    fun syncMedia(onSuccess:(main : MAIN?) ->Unit)
    fun getNewMedia(onSuccess:(main : MAIN?) ->Unit)
    fun updateMain(onSuccess: (List<MAIN>?, index :Int) -> Unit)
    fun updateCornerList(mediaModel: MediaModel, onSuccess: (List<MediaModel>?) -> Unit)
    fun updatePLAYGROUND(mediaModel: MediaModel, onSuccess: (List<MediaModel>?) -> Unit)
    fun getMainMediaList(onSuccess: (List<MAIN>? , index :Int) -> Unit)
    fun getL1MediaList(onSuccess: (List<MediaModel>? , index :Int) -> Unit)
    fun getL2MediaList(onSuccess: (List<MediaModel>? , index :Int) -> Unit)
    fun getL3MediaList(onSuccess: (List<MediaModel>? , index :Int) -> Unit)
    fun resetCornerIndex()
    fun resetMainIndex()
    fun resetPlaygroundIndex()
    fun resetAllIndex()
    fun previousMedia()
    fun refreshAllMediasFromDB(onSuccess: (main: MAIN?) -> Unit)
    fun resetCurrentMedia()

    fun clearLocalLists()


}
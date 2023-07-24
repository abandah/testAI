package com.ubitc.popuppush.service.updator


import android.util.Log
import com.ubitc.popuppush.App
import com.ubitc.popuppush.AppMainFeatureConstants
import com.ubitc.popuppush.api_send_model.CurrentState
import com.ubitc.popuppush.models.MAIN
import com.ubitc.popuppush.models.MediaModel
import com.ubitc.popuppush.service.db.DbService

class LocalMediaService(private val dbProvider: UpdaterMainFun) : UpdaterMainFun {

    companion object {
        @Volatile
        var providerInstance: LocalMediaService? = null
        fun getInstance(): LocalMediaService {
            if (providerInstance == null) {
                providerInstance = LocalMediaService(LocalMediaProvider.getInstance())
            }
            return providerInstance!!
        }
    }


    override fun getMediaToPlayFromMainLayerREAD(onSuccess: (main: MAIN?, index: Int) -> Unit) {
        dbProvider.getMediaToPlayFromMainLayerREAD(onSuccess)
    }

    override fun getMediaToPlayFromPLAYGROUNDREAD(
        mediaModel: MediaModel,
        onSuccess: (main: MediaModel?, index: Int) -> Unit
    ) {
        return dbProvider.getMediaToPlayFromPLAYGROUNDREAD(mediaModel, onSuccess)
    }

    override fun getMediaToPlayFromCORNERREAD(
        mediaModel: MediaModel,
        onSuccess: (main: MediaModel?, index: Int) -> Unit
    ) {
        return dbProvider.getMediaToPlayFromCORNERREAD(mediaModel, onSuccess)
    }

    override fun getMediaToPlayFromThirdLayer(onSuccess: (main: MediaModel?) -> Unit) {
        return dbProvider.getMediaToPlayFromThirdLayer(onSuccess)
    }

    override fun syncMedia(mediaModel: MAIN, onSuccess: (main: MAIN?) -> Unit) {
        return dbProvider.syncMedia(mediaModel, onSuccess)
    }

    override fun syncMedia(onSuccess: (main: MAIN?) -> Unit) {
        dbProvider.syncMedia(onSuccess)
    }

    override fun getNewMedia(onSuccess: (main: MAIN?) -> Unit) {
        dbProvider.getNewMedia(onSuccess)
    }

    override fun updateMain(onSuccess: (List<MAIN>?, index: Int) -> Unit) {
        dbProvider.updateMain(onSuccess)
    }

    override fun updateCornerList(mediaModel: MediaModel, onSuccess: (List<MediaModel>?) -> Unit) {
        dbProvider.updateCornerList(mediaModel, onSuccess)
    }

    override fun updatePLAYGROUND(mediaModel: MediaModel, onSuccess: (List<MediaModel>?) -> Unit) {
        dbProvider.updatePLAYGROUND(mediaModel, onSuccess)
    }

    override fun getMainMediaList(onSuccess: (List<MAIN>?, index: Int) -> Unit) {
        dbProvider.getMainMediaList(onSuccess)
    }

    override fun getL1MediaList(onSuccess: (List<MediaModel>?, index: Int) -> Unit) {
        if (App.currentActivity!!.logDialogIsShowing)
            dbProvider.getL1MediaList(onSuccess)
    }

    override fun getL2MediaList(onSuccess: (List<MediaModel>?, index: Int) -> Unit) {
        if (App.currentActivity!!.logDialogIsShowing)
            dbProvider.getL2MediaList(onSuccess)

    }

    override fun getL3MediaList(onSuccess: (List<MediaModel>?, index: Int) -> Unit) {
        if (App.currentActivity!!.logDialogIsShowing)
            dbProvider.getL3MediaList(onSuccess)
    }

    override fun resetCornerIndex() {
        dbProvider.resetCornerIndex()
    }

    override fun resetMainIndex() {
        dbProvider.resetMainIndex()
    }

    override fun resetPlaygroundIndex() {
        dbProvider.resetPlaygroundIndex()
    }

    override fun resetAllIndex() {
        dbProvider.resetAllIndex()
    }
    override fun resetCurrentMedia(){
     dbProvider.resetCurrentMedia()
    }

    override fun clearLocalLists() {
        dbProvider.clearLocalLists()
    }

    override fun previousMedia() {
        dbProvider.previousMedia()
    }

    override fun refreshAllMediasFromDB(onSuccess: (main: MAIN?) -> Unit) {
        dbProvider.refreshAllMediasFromDB(onSuccess)
    }

    class LocalMediaProvider : UpdaterMainFun {

        // private var mainIndex = 0
        var mainIndex = 0
        var nextMainIndex = 0

        private var mainList: List<MAIN>? = null

        private var parentMAIN: MediaModel? = null

        var cornerIndex = 0
        var nextCornerIndex = 0
        private var cornerList: List<MediaModel>? = null

        var playgroundIndex = 0
        private var playgroundList: List<MediaModel>? = null
        var nextPlaygroundIndex = 0


        companion object {
            private var INSTANCE: LocalMediaProvider? = null
            fun getInstance(): LocalMediaProvider {
                if (INSTANCE == null) {
                    INSTANCE = LocalMediaProvider()
                }
                return INSTANCE!!
            }
        }

        private fun updateMAINCurrentIndexAndNext() {
            if (mainList != null && nextMainIndex >= mainList!!.size) {
                App.currentActivity?.campaignHasEnded(DbService.getInstance().getCampaign())
                nextMainIndex = 0
                mainIndex = 0
            }
            mainIndex = nextMainIndex
            nextMainIndex++
        }

        private fun updatePLAYGROUNDCurrentIndexAndNext() {
            if (playgroundList != null && nextPlaygroundIndex >= playgroundList!!.size) {
                nextPlaygroundIndex = 0
                playgroundIndex = 0
            }
            playgroundIndex = nextPlaygroundIndex
            nextPlaygroundIndex++
        }

        private fun updateCORNERCurrentIndexAndNext() {
            if (AppMainFeatureConstants.repeatGIFsEnabled && cornerList != null && nextCornerIndex >= cornerList!!.size) {
                nextCornerIndex = 0
                cornerIndex = 0
            }
            cornerIndex = nextCornerIndex
            nextCornerIndex++
        }


        override fun getMediaToPlayFromMainLayerREAD(onSuccess: (main: MAIN?, index: Int) -> Unit) {
            updateMAINCurrentIndexAndNext()
            if (mainList.isNullOrEmpty()) {
                updateMain { _, _ ->
                    if (!mainList.isNullOrEmpty()) {
                        if (mainIndex >= mainList!!.size) {
                            mainIndex = 0

                        }

                        onSuccess(getMainMediaFromCurrentState(), mainIndex)
                    }

                }

            } else {
                if (mainIndex >= mainList!!.size) {
                    mainIndex = 0
                }
                val main = mainList!![mainIndex]
                onSuccess(main, mainIndex)
            }


        }

        private fun getMainMediaFromCurrentState(): MAIN {
            val currentState: CurrentState =
                DbService.getInstance().getCurrentState() ?: return mainList!![mainIndex]
            mainIndex = currentState.mainIndex!!
            updateMAINCurrentIndexAndNext()
            val main = mainList!![mainIndex]
            main.customDuration = currentState.currentDuration
            return main
        }


        override fun getMediaToPlayFromPLAYGROUNDREAD(
            mediaModel: MediaModel,
            onSuccess: (main: MediaModel?, index: Int) -> Unit
        ) {

            updatePLAYGROUNDCurrentIndexAndNext()
            if (playgroundList.isNullOrEmpty() || (mediaModel as MAIN).PLAYGROUND.isNullOrEmpty() || mediaModel.PLAYGROUND?.equals(
                    playgroundList
                ) == false
            ) {
                updatePLAYGROUND(mediaModel) {
                    if (!playgroundList.isNullOrEmpty()) {
                        if (playgroundIndex >= playgroundList!!.size) {
                            playgroundIndex = 0
                        }
                        //    App.Companion.currentActivity!!.printLog(" playground INDEX $playgroundIndex")
                        onSuccess(playgroundList!![playgroundIndex], playgroundIndex)
                    } else {
                        onSuccess(null, 0)
                    }
                }
            } else {
                if (!playgroundList.isNullOrEmpty()) {
                    if (playgroundIndex >= playgroundList!!.size) {
                        playgroundIndex = 0
                    }
                    //   App.Companion.currentActivity!!.printLog(" playground INDEX $playgroundIndex")
                    onSuccess(playgroundList!![playgroundIndex], playgroundIndex)
                }
            }

        }


        override fun getMediaToPlayFromCORNERREAD(
            mediaModel: MediaModel,
            onSuccess: (main: MediaModel?, index: Int) -> Unit
        ) {
            if (mediaModel != parentMAIN) {
                parentMAIN = mediaModel
                cornerIndex = 0
                nextCornerIndex = 0
            }
            updateCORNERCurrentIndexAndNext()
            if (cornerList != null) {
                if (cornerList!!.isEmpty()) {
                    onSuccess(null, 0)
                    return
                }
                if (cornerIndex >= cornerList!!.size) {
                    if (!AppMainFeatureConstants.repeatGIFsEnabled && mediaModel == parentMAIN) {
                        onSuccess(null, 0)
                        return
                    } else {
                        cornerIndex = 0
                        nextCornerIndex = 0
                        updateCORNERCurrentIndexAndNext()
                    }
                }

                (mediaModel as MAIN).CORNER?.let { innerCorner ->
                    if ((innerCorner == cornerList).not()) {
                        updateCornerList(mediaModel) { newCorner ->
                            cornerIndex = 0
                            nextCornerIndex = 0
                            if (newCorner.isNullOrEmpty()) {
                                onSuccess(null, 0)
                                return@updateCornerList
                            }
                            updateCORNERCurrentIndexAndNext()

                        }
                    }
                }


            } else {
                updateCornerList(mediaModel) {
                    cornerIndex = 0
                    nextCornerIndex = 0
                    if (it.isNullOrEmpty()) {
                        onSuccess(null, 0)
                        return@updateCornerList
                    }
                    updateCORNERCurrentIndexAndNext()
                }
            }

            onSuccess(
                if (cornerList.isNullOrEmpty()) null else cornerList!![cornerIndex],
                cornerIndex
            )

        }

        override fun updateCornerList(
            mediaModel: MediaModel,
            onSuccess: (List<MediaModel>?) -> Unit
        ) {
            DbService.getInstance().getMediasToPlayFromCORNER(mediaModel) {
                cornerList = it
                onSuccess(it)
            }

        }

        override fun updatePLAYGROUND(
            mediaModel: MediaModel,
            onSuccess: (List<MediaModel>?) -> Unit
        ) {
            DbService.getInstance().getMediasToPlayFromPLAYGROUND(mediaModel) {
                if (it != playgroundList) {
                    playgroundIndex = 0
                    nextPlaygroundIndex = 0
                }
                playgroundList = it
                if (playgroundList != null && playgroundIndex > playgroundList!!.size - 1) {
                    playgroundIndex = 0
                    nextPlaygroundIndex = 0
                    //        App.Companion.currentActivity!!.printLog("in Updating  Playground INDEX $playgroundIndex")
                }
                onSuccess(it)
            }

        }

        override fun getMainMediaList(onSuccess: (List<MAIN>?, index: Int) -> Unit) {
            onSuccess(mainList, mainIndex)
        }

        override fun getL1MediaList(onSuccess: (List<MediaModel>?, index: Int) -> Unit) {
            onSuccess(playgroundList, playgroundIndex)
        }

        override fun getL2MediaList(onSuccess: (List<MediaModel>?, index: Int) -> Unit) {
            onSuccess(cornerList, cornerIndex)
        }

        override fun getL3MediaList(onSuccess: (List<MediaModel>?, index: Int) -> Unit) {
            TODO("Not yet implemented")
        }

        override fun resetCornerIndex() {
            cornerIndex = 0
            nextCornerIndex = 0
        }

        override fun resetMainIndex() {
            mainIndex = 0
            nextMainIndex = 0
        }

        override fun resetPlaygroundIndex() {
            playgroundIndex = 0
            nextPlaygroundIndex = 0
        }

        override fun resetAllIndex() {
            resetCornerIndex()
            resetMainIndex()
            resetPlaygroundIndex()
        }

        override fun previousMedia() {
            Log.e("previousMedia", mainIndex.toString())
            nextMainIndex = mainIndex-1
            if(nextMainIndex < 0)
                nextMainIndex = mainList!!.size-1

        }
        override fun resetCurrentMedia(){
           nextMainIndex = mainIndex
        }

        override fun clearLocalLists() {
            TODO("Not yet implemented")
        }

        override fun updateMain(onSuccess: (List<MAIN>?, index: Int) -> Unit) {
            DbService.getInstance().getMediasToPlayFromMainLayer {
                mainList = it
                if (mainList != null && mainIndex > mainList!!.size - 1) {
                    mainIndex = 0
                }
                onSuccess(it, mainIndex)
            }

        }

        override fun getMediaToPlayFromThirdLayer(onSuccess: (main: MediaModel?) -> Unit) {
        }


        override fun syncMedia(mediaModel: MAIN, onSuccess: (main: MAIN?) -> Unit) {

            if (mainList == null || mainList?.size == 0) {
                //TODO() No Campaigns
                onSuccess(null)
                return
            }
            var currentIndex = if (mainIndex == 0) 0 else mainIndex - 1

            if (currentIndex > mainList?.size?.minus(1)!!) {
                currentIndex = 0
            }

            val currentMedia: MAIN? = mainList?.singleOrNull { it == mediaModel }

            if (currentMedia == null) {
                if (currentIndex > mainList?.size?.minus(1)!!) {
                    mainIndex = 0
                    nextMainIndex = 0 + 1
                }
                onSuccess(mainList!![mainIndex])
                return
            } else {
                currentIndex = mainList!!.indexOf(currentMedia)
                mainIndex = currentIndex
                nextMainIndex = mainIndex + 1
                onSuccess(currentMedia)
                return
            }

        }


        override fun getNewMedia(onSuccess: (main: MAIN?) -> Unit) {
            val index: Int = mainIndex
            val media = mainList!![index]
            onSuccess(media)

        }

        override fun syncMedia(onSuccess: (main: MAIN?) -> Unit) {

            if (mainList == null || mainList?.size == 0) {
                //TODO(no campaign)
                onSuccess(null)
                return
            }
            var currentIndex = if (mainIndex == 0) 0 else mainIndex - 1

            if (currentIndex > mainList?.size?.minus(1)!!) {
                currentIndex = 0
            }
            val defMedia = mainList!![currentIndex]

            val currentMedia: MAIN? = mainList?.singleOrNull { it == defMedia }

            if (currentMedia == null) {
                if (currentIndex > mainList?.size?.minus(1)!!) {
                    mainIndex = 0
                }
                onSuccess(defMedia)
                return
            } else {
                currentIndex = mainList!!.indexOf(currentMedia)
                mainIndex = currentIndex
                onSuccess(currentMedia)
                return
            }

        }

        override fun refreshAllMediasFromDB(onSuccess: (main: MAIN?) -> Unit) {
                LocalMediaService.getInstance().updateMain { mains, _ ->
                    mains?.get(0)?.let {
                        LocalMediaService.getInstance().updateCornerList(mains[0]) {
                            LocalMediaService.getInstance().updatePLAYGROUND(mains[0]) {
//                                mainIndex = 0
//                                nextMainIndex = 0
//                                cornerIndex = 0
//                                nextCornerIndex = 0
//                                playgroundIndex = 0
//                                nextPlaygroundIndex = 0
                                onSuccess(mains[0])

                            }
                        }
                    }?:run{
                      // App.currentActivity?.deviceFlushed()
                    }


                }
            }



    }
}
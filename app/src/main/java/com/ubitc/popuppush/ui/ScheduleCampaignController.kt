package com.ubitc.popuppush.ui

import com.ubitc.popuppush.AppMainFeatureConstants
import com.ubitc.popuppush.databinding.DialogScheduleCampaignBinding
import com.ubitc.popuppush.models.Campaign
import com.ubitc.popuppush.models.MAIN
import com.ubitc.popuppush.models.MediaModel
import com.ubitc.popuppush.views.tools.MyView
import com.ubitc.popuppush.views.tools.ViewListener

class ScheduleCampaignController(
    val binding: DialogScheduleCampaignBinding,
    val campaign: Campaign
) {
    private var mainIndex = 0
    private var nextMainIndex = 0

    private var mainList: List<MAIN>? = null

    private var parentMAIN: MediaModel? = null

    private var cornerIndex = 0
    private var nextCornerIndex = 0
    private var cornerList: List<MediaModel>? = null

    private var playgroundIndex = 0
    private var playgroundList: List<MediaModel>? = null
    private var nextPlaygroundIndex = 0


    private lateinit var campaignEnded: () -> Unit

    fun init() {
        mainIndex = 0
        nextMainIndex = 0
        cornerIndex = 0
        nextCornerIndex = 0
        playgroundIndex = 0
        nextPlaygroundIndex = 0

        mainList = campaign.MAIN
        cornerList = campaign.CORNER
        playgroundList = campaign.PLAYGROUND


    }

    private fun playMain(media: MediaModel?) {
        if (media == null) {
            campaignEnded()
            return
        }
        parentMAIN = media
        val view = binding.mainLayer
        view.prepareView(media, object : ViewListener {
            override fun finished(view: MyView?) {
                getMediaToPlayFromMainLayerREAD { it, _ ->
                    playMain(it)
                }
            }

            override fun showLayer1(media: MediaModel?) {
                if (!(media as MAIN).show_layer_one) {
                    binding.playgroundLayer.forceStop()
                    binding.mainLayer.lastShowLayer1value = false
                } else {
                    getMediaToPlayFromPLAYGROUNDREAD(media) { it, index ->
                        if (it == null && index == 0) {
                            binding.mainLayer.lastShowLayer1value = false
                            binding.playgroundLayer.forceStop()
                        } else {
                            playLayerPlayGround(it, media)
                        }
                    }
                }
            }

            override fun showLayer2(media: MediaModel?) {
                if (!(media as MAIN).show_layer_two) {
                    resetCornerIndex()
                    binding.cornerLayer.forceStop()
                    binding.mainLayer.lastShowLayer2value = false
                } else {
                    getMediaToPlayFromCORNERREAD(media) { it, index ->
                        if (it == null && index == 0) {
                            binding.mainLayer.lastShowLayer2value = false
                            binding.cornerLayer.forceStop()
                        } else {
                            playLayerCorner(it, media)
                        }
                    }
                }
            }

        })

    }

    fun resetCornerIndex() {
        cornerIndex = 0
        nextCornerIndex = 0
    }

    private fun playLayerPlayGround(currentImage: MediaModel?, parent: MediaModel?) {

        val view = binding.playgroundLayer

        if (currentImage == null) {
            view.forceStop()
            return
        }

        view.prepareView(currentImage, object : ViewListener {
            override fun finished(view: MyView?) {
                getMediaToPlayFromPLAYGROUNDREAD(parent!!) { it, index ->
                    if (it == null && index == 0) {
                        binding.mainLayer.lastShowLayer1value = false
                        binding.playgroundLayer.forceStop()
                    } else {
                        playLayerPlayGround(it, parent)
                    }
                }
            }

        })

    }

    private fun playLayerCorner(currentImage: MediaModel?, parent: MediaModel?) {

        val view = binding.cornerLayer
        if (currentImage == null) {
            view.forceStop()
            return
        }

        view.prepareView(currentImage, object : ViewListener {
            override fun finished(view: MyView?) {
                getMediaToPlayFromCORNERREAD(parent!!) { it, index ->
                    if (it == null && index == 0) {
                        binding.mainLayer.lastShowLayer2value = false
                        binding.cornerLayer.forceStop()
                    } else {
                        playLayerCorner(it, parent)
                    }
                }
            }

        })

    }

    fun onDestroy() {
        binding.mainLayer.forceStop()
        binding.playgroundLayer.forceStop()
        binding.cornerLayer.forceStop()
        binding.frameLayout.removeAllViews()
    }


    fun getMediaToPlayFromMainLayerREAD(onSuccess: (main: MAIN?, index: Int) -> Unit) {
        mainIndex = nextMainIndex
        nextMainIndex++
        if (mainList.isNullOrEmpty()) {

            campaignEnded()
            return
        }

        if (mainIndex >= mainList!!.size) {

            campaignEnded()
            return
        }
        if (mainList.isNullOrEmpty()) {

            campaignEnded()
            return
        }

        val main = mainList!![mainIndex].MAIN?.get(0)
        if (main?.path.isNullOrEmpty()) {
            campaignEnded()
            return
        }
        onSuccess(main, mainIndex)


    }

    fun getMediaToPlayFromPLAYGROUNDREAD(
        mediaModel: MediaModel,
        onSuccess: (main: MediaModel?, index: Int) -> Unit
    ) {
        if (!(mediaModel as MAIN).PLAYGROUND.isNullOrEmpty() && mediaModel.PLAYGROUND?.equals(
                playgroundList
            ) != true
        ) {
            playgroundList = mediaModel.PLAYGROUND
            playgroundIndex = 0
            nextPlaygroundIndex = 0
        }
        if (playgroundList.isNullOrEmpty()) {
            playgroundList = campaign.PLAYGROUND
            playgroundIndex = 0
            nextPlaygroundIndex = 0
        }
        playgroundList?.let {

            if (playgroundIndex > playgroundList!!.size-1) {
                playgroundIndex = 0
                onSuccess(null, 0)
                return
            }
            if (nextPlaygroundIndex >= playgroundList!!.size) {
                nextPlaygroundIndex = 0
                playgroundIndex = 0
            }
            playgroundIndex = nextPlaygroundIndex
            nextPlaygroundIndex++
            //    App.Companion.currentActivity!!.printLog(" playground INDEX $playgroundIndex")
            if (playgroundIndex > playgroundList!!.size-1) {
                playgroundIndex = 0
                onSuccess(null, 0)
                return
            }
            onSuccess(playgroundList!![playgroundIndex], playgroundIndex)
        }

    }


    fun getMediaToPlayFromCORNERREAD(
        mediaModel: MediaModel,
        onSuccess: (main: MediaModel?, index: Int) -> Unit
    ) {

        if (!(mediaModel as MAIN).CORNER.isNullOrEmpty() && mediaModel.CORNER?.equals(
                cornerList
            ) != true
        ) {
            cornerList = mediaModel.CORNER
            cornerIndex = 0
            nextCornerIndex = 0
        }
        if (cornerList.isNullOrEmpty()) {
            cornerList = campaign.CORNER
            cornerIndex = 0
            nextCornerIndex = 0
        }
        cornerList?.let {

            if (cornerIndex > it.size-1) {
                cornerIndex = 0
                onSuccess(null, 0)
                return
            }
            if (AppMainFeatureConstants.repeatGIFsEnabled && nextCornerIndex >= cornerList!!.size) {
                nextCornerIndex = 0
                cornerIndex = 0
            }
            cornerIndex = nextCornerIndex
            nextCornerIndex++
            //    App.Companion.currentActivity!!.printLog(" playground INDEX $playgroundIndex")
            if (cornerIndex > it.size-1) {
                cornerIndex = 0
                onSuccess(null, 0)
                return
            }
            onSuccess(it[cornerIndex], cornerIndex)
        }

    }

//    private fun updateMAINCurrentIndexAndNext() {
//        if (mainList != null && nextMainIndex >= mainList!!.size) {
//            this.campaignEnded()
//            return
//        }
//
//    }

//
//    private fun updatePLAYGROUNDCurrentIndexAndNext() {
//
//    }



    fun start(campaignEnded: () -> Unit) {
        this.campaignEnded = campaignEnded
        init()
        getMediaToPlayFromMainLayerREAD { it, _ ->
            if (it == null) {
                campaignEnded()
                return@getMediaToPlayFromMainLayerREAD
            }
            playMain(it)
        }
    }

    private fun campaignEnded() {
        onDestroy()
        this.campaignEnded.invoke()
    }

    fun playPause(b: Boolean) {
        if(!b) {
            binding.mainLayer.pauseView()
            binding.playgroundLayer.pauseView()
            binding.cornerLayer.pauseView()
        }else{
            binding.mainLayer.resumeView()
            binding.playgroundLayer.resumeView()
            binding.cornerLayer.resumeView()
        }
    }

}
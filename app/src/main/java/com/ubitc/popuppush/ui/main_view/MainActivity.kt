package com.ubitc.popuppush.ui.main_view

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.ubitc.popuppush.AppMainFeatureConstants
import com.ubitc.popuppush.MConstants
import com.ubitc.popuppush.R
import com.ubitc.popuppush.api_send_model.CurrentState
import com.ubitc.popuppush.asynctasks.ChangeCampingTask
import com.ubitc.popuppush.databinding.ActivityMainoneviewBinding
import com.ubitc.popuppush.databinding.DialogLogBinding
import com.ubitc.popuppush.databinding.DialogPlayerBinding
import com.ubitc.popuppush.databinding.DialogScheduleCampaignBinding
import com.ubitc.popuppush.models.Action
import com.ubitc.popuppush.models.Campaign
import com.ubitc.popuppush.models.MAIN
import com.ubitc.popuppush.models.MediaModel
import com.ubitc.popuppush.providers.BackgroundProcess
import com.ubitc.popuppush.providers.downloadx.DownloaderRecyclerView
import com.ubitc.popuppush.providers.realtimedatabase.RealTimeDBProvider
import com.ubitc.popuppush.service.campaignman.CampaignManagerService
import com.ubitc.popuppush.service.updator.LocalMediaService
import com.ubitc.popuppush.ui.BaseActivity
import com.ubitc.popuppush.ui.BaseDialog
import com.ubitc.popuppush.ui.ScheduleCampaignController
import com.ubitc.popuppush.ui.dialog.InternetDialog
import com.ubitc.popuppush.views.tools.MyView
import com.ubitc.popuppush.views.tools.ViewListener


class MainActivity : BaseActivity() {
    var binding: ActivityMainoneviewBinding? = null
    var viewModel: MainScreenViewModel? = null
    private var campaignChanged: Boolean = false


    private val requestInterNetDialogLauncherMainMedia =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { _ ->
            LocalMediaService.getInstance().getMediaToPlayFromMainLayerREAD { it, _ ->
                playMain(it)
            }

        }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainoneviewBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[MainScreenViewModel::class.java]
        binding!!.viewModel = viewModel
        binding!!.lifecycleOwner = this
        setContentView(binding!!.root)
        LocalMediaService.getInstance().refreshAllMediasFromDB {
            LocalMediaService.getInstance().resetAllIndex()
            LocalMediaService.getInstance().getMediaToPlayFromMainLayerREAD { mediaMain, _ ->
                playMain(mediaMain)
            }
        }

        //  campaignAssigned()
        RealTimeDBProvider.getInstance().resetCounter()

        BackgroundProcess.getInstance().startUpdatingLastAvailableRam()
        BackgroundProcess.getInstance().startPrayingTimeChecker()
    }

    override fun onStart() {
        super.onStart()
        RealTimeDBProvider.getInstance().getScreenAngle {
            rotateScreen(Action().apply {
                var newAngle = binding?.rotateLayout?.angle!! + it
                if (newAngle >= 360) {
                    newAngle -= 360
                }
                if (newAngle <= -360) {
                    newAngle += 360
                }
                binding?.rotateLayout?.angle = newAngle
            })
        }
    }

    override fun heapInfo(heapInfo: String) {
        viewModel!!.heapInfo.value = (heapInfo)
    }

    override fun ramInfo(ramInfo: String) {
        viewModel!!.availableRam.value = (ramInfo)
    }

    private fun playMain(media: MediaModel?) {

        LocalMediaService.getInstance().getMainMediaList { mains, index ->
            viewModel?.printList(mains, index)

        }
        val view = binding!!.mainLayer
        view.prepareView(media, object : ViewListener {
            override fun finished(view: MyView?) {
                LocalMediaService.getInstance().getMediaToPlayFromMainLayerREAD { it, _ ->
                    if (media == it && it?.path?.endsWith(".m3u8") == true && !MConstants.isInternetAvailable) {
                        requestInterNetDialogLauncherMainMedia.launch(InternetDialog.showDialog(this@MainActivity))
                        return@getMediaToPlayFromMainLayerREAD
                    }
                    playMain(it)
                }
            }

            override fun viewIsReady(view: MyView?) {

            }

            override fun showLayer1(media: MediaModel?) {
                if (!(media as MAIN).show_layer_one) {
                    //set color for playground as black
                    binding!!.playgroundLayer.setBackgroundColor(
                        ContextCompat.getColor(
                            this@MainActivity,
                            android.R.color.transparent
                        )
                    )
                    // binding!!.playgroundLayer.removeAllViews()
                    binding!!.playgroundLayer.forceStop()
                    binding!!.mainLayer.lastShowLayer1value = false
                } else {
                    binding!!.playgroundLayer.setBackgroundColor(
                        ContextCompat.getColor(
                            this@MainActivity,
                            R.color.black
                        )
                    )
                    LocalMediaService.getInstance()
                        .getMediaToPlayFromPLAYGROUNDREAD(media) { it, index ->
                            if (it == null && index == 0) {
                                binding!!.mainLayer.lastShowLayer1value = false
                                binding!!.playgroundLayer.forceStop()
                            } else {
                                playLayerPlayGround(it, media)
                            }
                        }
                }
            }

            override fun showLayer2(media: MediaModel?) {
                if (!(media as MAIN).show_layer_two) {
                    LocalMediaService.getInstance().resetCornerIndex()
                    //   binding!!.cornerLayer.removeAllViews()
                    binding!!.cornerLayer.forceStop()
                    binding!!.mainLayer.lastShowLayer2value = false
                } else {
                    LocalMediaService.getInstance()
                        .getMediaToPlayFromCORNERREAD(media) { it, index ->
                            if (it == null && index == 0) {
                                binding!!.mainLayer.lastShowLayer2value = false
                                binding!!.cornerLayer.forceStop()
                            } else {
                                playLayerCorner(it, media)
                            }
                        }
                }
            }

        })

    }


    private fun playLayerPlayGround(currentImage: MediaModel?, parent: MediaModel?) {
        LocalMediaService.getInstance().getL1MediaList { l1, index ->
            viewModel?.printL1List(l1, index)
        }
        val view = binding!!.playgroundLayer

        if (currentImage == null) {
            view.forceStop()
            return
        }

        view.prepareView(currentImage, object : ViewListener {
            override fun finished(view: MyView?) {
                LocalMediaService.getInstance()
                    .getMediaToPlayFromPLAYGROUNDREAD(parent!!) { it, index ->
                        if (it == null && index == 0) {
                            binding!!.mainLayer.lastShowLayer1value = false
                            binding!!.playgroundLayer.forceStop()
                        } else {
                            playLayerPlayGround(it, parent)
                        }
                    }
            }

        })

    }

    private fun playLayerCorner(currentImage: MediaModel?, parent: MediaModel?) {
        LocalMediaService.getInstance().getL2MediaList { l2, index ->
            viewModel?.printL2List(l2, index)
        }
        val view = binding!!.cornerLayer
        if (currentImage == null) {
            view.forceStop()
            return
        }

        view.prepareView(currentImage, object : ViewListener {
            override fun finished(view: MyView?) {
                LocalMediaService.getInstance()
                    .getMediaToPlayFromCORNERREAD(parent!!) { it, index ->
                        if (it == null && index == 0) {
                            binding!!.mainLayer.lastShowLayer2value = false
                            binding!!.cornerLayer.forceStop()
                        } else {
                            playLayerCorner(it, parent)
                        }
                    }
            }

        })

    }

    override fun onDestroy() {

        binding?.mainLayer!!.forceStop()
        binding?.playgroundLayer!!.forceStop()
        binding?.cornerLayer!!.forceStop()
        super.onDestroy()
    }

    override fun playPause(b: Boolean?) {
        if (b == null) {
            if (binding?.mainLayer?.isPaused != true) {
                binding?.mainLayer?.pauseView()
                binding?.playgroundLayer?.pauseView()
                binding?.cornerLayer?.pauseView()
            } else {
                binding?.mainLayer?.resumeView()
                binding?.playgroundLayer?.resumeView()
                binding?.cornerLayer?.resumeView()
            }
            return
        }


        if (binding?.mainLayer?.isPaused == true && b == true) {
            if (campaignChanged) {
                binding?.mainLayer?.getNewMedia() {

                    binding?.mainLayer?.resumeView()
                    binding?.playgroundLayer?.resumeView()
                    binding?.cornerLayer?.resumeView()

                    campaignChanged = false
                    CampaignManagerService.getInstance().campaignID?.let {
                        val campaign = CampaignManagerService.getInstance().getCampaignById(it)
                        RealTimeDBProvider.getInstance().pushCampaignAssigned(
                            current_campaign = campaign?.campaign_name!!,
                            current_campaign_id = campaign.campaign_id!!
                        )
                    }

                    RealTimeDBProvider.getInstance().isPlayingState(true)
                }

            } else {
                binding?.mainLayer?.resumeView()
                binding?.playgroundLayer?.resumeView()
                binding?.cornerLayer?.resumeView()
                CampaignManagerService.getInstance().campaignID?.let {
                    val campaign = CampaignManagerService.getInstance().getCampaignById(it)
                    RealTimeDBProvider.getInstance().pushCampaignAssigned(
                        current_campaign = campaign?.campaign_name!!,
                        current_campaign_id = campaign.campaign_id!!
                    )
                }

                RealTimeDBProvider.getInstance().isPlayingState(true)
            }


        } else if (binding?.mainLayer?.isPaused == false && b == false) {
            binding?.mainLayer?.pauseView()
            binding?.playgroundLayer?.pauseView()
            binding?.cornerLayer?.pauseView()
            RealTimeDBProvider.getInstance().isPlayingState(false)

        }
    }

    override fun onResume() {
        super.onResume()
        if (prayDialog == null) {
            binding?.mainLayer!!.resumeView()
            binding?.playgroundLayer!!.resumeView()
            binding?.cornerLayer!!.resumeView()
        }
        RealTimeDBProvider.getInstance().isPlayingState(true)
    }

    override fun onPause() {
        super.onPause()
        binding?.mainLayer!!.pauseView()
        binding?.playgroundLayer!!.pauseView()
        binding?.cornerLayer!!.pauseView()
        RealTimeDBProvider.getInstance().isPlayingState(false)
    }

    override fun campaignAssigned() {
        binding?.mainLayer!!.forceStop()
        binding?.playgroundLayer!!.forceStop()
        binding?.cornerLayer!!.forceStop()
        RealTimeDBProvider.getInstance().isPlayingState(false)
        deviceRestart()
    }

    override fun changeTheCampaign() {
        playPause(false)
        campaignChanged = true
        val currentCampaignId = CampaignManagerService.getInstance().campaignID
        ChangeCampingTask().executeAsync("dsf") {
            if (currentCampaignId == it) {
                CampaignManagerService.getInstance().getCampaignById(it!!).let { campById ->
                    if (campById != null)
                        RealTimeDBProvider.getInstance().pushCampaignAssigned(
                            current_campaign = campById.campaign_name!!,
                            current_campaign_id = campById.campaign_id!!
                        )
                    CampaignManagerService.getInstance().changeToCampaignSilently(it) {
                        if (prayDialog != null || scheduleCampaignDialog != null) {

                        } else {

                            binding?.mainLayer?.getNewMedia() {
                                playPause(true)
                            }
                            campaignChanged = false

                        }

                    }

                }
                return@executeAsync
            } else {

                playPause(false)
                CampaignManagerService.getInstance().changeToCampaign(it) {}
            }

        }
    }

    override fun deviceFlushed() {
        binding?.mainLayer!!.forceStop()
        binding?.playgroundLayer!!.forceStop()
        binding?.cornerLayer!!.forceStop()
        super.deviceFlushed()
    }


    override fun onClickGreenButton() {
        // binding!!.mainLayer.mute()
//        val s = "9979bc7f-9899-4d61-927f-45735049fb4b"
//
//        CampaignManagerService.getInstance().getCampaignById(s)?.let {
//            runScheduleCampaign(it)
//        }
    }

    override fun statusUpdate() {
        super.statusUpdate()
        deviceRestart()
    }

    override fun updateMedia() {
        changeTheCampaign()
    }

    override fun getCurrentMain(): MAIN? {
        return binding?.mainLayer?.getCurrentMainMedia()
    }

    fun onTickMAIN(l: Long) {
        viewModel?.mainTicks(l)
    }

    override fun campaignHasEnded(campaign: Campaign?) {
        LocalMediaService.getInstance().resetAllIndex()
        CampaignManagerService.getInstance().getCampaignIndex()?.let {
            if (it.main == null) {
                return
            }
            if (it.main!!.id == campaign?.campaign_id) {
                return
            }
            CampaignManagerService.getInstance().changeToCampaign(it.main!!.id) {
            }
        }

    }

    fun pugDetected(s: String) {
        viewModel?.bugDetected(s)
    }

    override fun deviceRestart() {
        val duration = viewModel?.getDenaturation()
        CurrentState(duration ?: 0)
        binding?.mainLayer!!.forceStop()
        binding?.playgroundLayer!!.forceStop()
        binding?.cornerLayer!!.forceStop()
        super.deviceRestart()
    }


    override fun getRootView(): DownloaderRecyclerView? {
        return binding?.downloadRecyclerView
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun showInfo() {

        if (AppMainFeatureConstants.logEnabled) {
            val binding = DialogLogBinding.inflate(layoutInflater)
            binding.viewModel = viewModel
            binding.lifecycleOwner = this

            infoDialog = BaseDialog.Builder().withTheme(
                dim = false,
                backGroundColor = R.color.trans_background,
                gravity = Gravity.END or Gravity.TOP
            ).content(binding.root).whenShowDo {
                logDialogIsShowing = true
                updateListOnScreen()

            }.whenFinishDo {
                logDialogIsShowing = false
                viewModel?.campaignsAdapters?.value?.notifyDataSetChanged()
                it.dismiss()
                infoDialog = null
            }.isFullscreen(isFull = true).build()
            infoDialog?.show()
        }
    }

    override fun onClickBlueButton() {
        //show red dialog with progress indise


//        if (logDialogIsShowing) {
        //    changeTheCampaign()
//        super.onClickBlueButton()
//        ApiService.getInstance().getPrayingTimes(onSuccess = {
//            val d = it
//            MyAlarmReceiver.scheduledThePrays(d)
//        })
    }

//    private fun showCampaignDialog() {
//        val binding = DialogCampaignsBinding.inflate(layoutInflater)
//        binding.viewModel = viewModel
//        binding.lifecycleOwner = this
//
//        dialog = BaseDialog.Builder().withTheme(
//            dim = false,
//            backGroundColor = R.color.trans_background,
//            gravity = Gravity.CENTER
//        ).content(binding.root).whenShowDo {
//            logDialogIsShowing = true
//
//        }.whenFinishDo {
//            logDialogIsShowing = false
//        }.isFullscreen(isFull = false)
//            .build()
//        dialog?.show()
//    }


    private fun updateListOnScreen() {
        LocalMediaService.getInstance().getMainMediaList { mains, index ->
            viewModel?.printList(mains, index)

        }
        LocalMediaService.getInstance().getL1MediaList { l1, index ->
            viewModel?.printL1List(l1, index)
        }
        LocalMediaService.getInstance().getL2MediaList { l2, index ->
            viewModel?.printL2List(l2, index)
        }
    }

    override fun nextMedia() {
        binding?.mainLayer?.nextMedia()
    }

    override fun previousMedia() {
        binding?.mainLayer?.previousMedia()
    }

    override fun stopMedia() {
        binding?.mainLayer?.restartMedia()
    }

    // private var dialogPlayer: BaseDialog? = null

    override fun prayerStart(toBoolean: Boolean) {
        if (!toBoolean) {
            return
        }
        if (scheduleCampaignDialog != null && sc != null) {
            sc?.playPause(false)
        }

        if (prayDialog != null) {
            prayDialog?.dismiss()
            prayDialog = null
            playPause(true)
            return
        }
        val dialogBinding = DialogPlayerBinding.inflate(layoutInflater)
        dialogBinding.viewModel = viewModel
        dialogBinding.lifecycleOwner = this
        prayDialog?.dismiss()
        prayDialog = null
        if (binding?.mainLayer?.isPaused == false) {
            playPause(false)
        }
        prayDialog = BaseDialog.Builder().withTheme(
            dim = false,
            backGroundColor = R.color.trans_background,
            gravity = Gravity.END or Gravity.TOP
        ).content(dialogBinding.root).whenShowDo {
            RealTimeDBProvider.getInstance().pushCampaignAssigned(
                current_campaign = "Pray",
                current_campaign_id = "00000000"
            )
            //   val videoResId = resources.getIdentifier("adan5", "raw", packageName)
            //     val ur = Uri.parse("android.resource://$packageName/$videoResId")
            val videoPath = "android.resource://" + packageName + "/" + R.raw.adan


            val videoUri = Uri.parse(videoPath)
            dialogBinding.videoView.setVideoURI(videoUri)
            dialogBinding.videoView.start()
            dialogBinding.videoView.setOnErrorListener { _, _, _ ->
                prayDialog?.dismiss()
                true
            }
            dialogBinding.videoView.setOnCompletionListener {
                prayDialog?.finishedDialog()
            }


        }.whenFinishDo {
            it.dismiss()
            prayDialog = null
            if (scheduleCampaignDialog != null && sc != null) {
                if (scheduleCampaignDialog?.isShowing != true) {
                    scheduleCampaignDialog?.show()
                } else {
                    sc?.playPause(true)
                }
            } else {
                if (binding?.mainLayer?.isPaused == true) {
                    playPause(true)
                }
            }

        }.isFullscreen(isFull = true).build()
        prayDialog?.show()


    }

    private var sc: ScheduleCampaignController? = null
    override fun runScheduleCampaign(campaign: Campaign) {
        if (scheduleCampaignDialog != null) {
            scheduleCampaignDialog?.dismiss()
            scheduleCampaignDialog = null
            return
        }
        val dialogBinding = DialogScheduleCampaignBinding.inflate(layoutInflater)
        dialogBinding.lifecycleOwner = this
        sc = ScheduleCampaignController(dialogBinding, campaign)

        if (binding?.mainLayer?.isPaused == false) {
            playPause(false)
        }
        scheduleCampaignDialog = BaseDialog.Builder().withTheme(
            dim = false,
            backGroundColor = R.color.trans_background,
            gravity = Gravity.CENTER,
        ).content(dialogBinding.root).whenShowDo {
            RealTimeDBProvider.getInstance().pushCampaignAssigned(
                current_campaign = "${campaign.campaign_name!!} (Scheduled)",
                current_campaign_id = campaign.campaign_id!!
            )
            Handler(Looper.getMainLooper()).post {
                sc?.start {
                    scheduleCampaignDialog?.finishedDialog()
                }
            }
        }.whenFinishDo {
            sc?.onDestroy()

            it.dismiss()
            scheduleCampaignDialog = null
//            dialog = null
            Handler(Looper.getMainLooper()).post {
                if (binding?.mainLayer?.isPaused == true) {
                    playPause(true)
                }
            }

        }.isFullscreen(isFull = true).build()
        if (prayDialog == null) {
            scheduleCampaignDialog?.show()
        } else {

        }


    }


//    fun openUSBDialog() {
//        val dialogBinding = DialogUsbBinding.inflate(layoutInflater)
//        val viewModel = ViewModelProvider(this)[UsbDialog::class.java]
//        dialogBinding.viewModel = viewModel
//        dialogBinding.lifecycleOwner = this
//
//        dialog?.dismiss()
//        dialog = null
//
//        dialog = BaseDialog.Builder().withTheme(
//            dim = false,
//            backGroundColor = R.color.trans_background,
//            gravity = Gravity.CENTER
//        ).content(dialogBinding.root).whenShowDo {
//            viewModel.start()
//
//
//        }.whenFinishDo {
//
//        }.isFullscreen(isFull = false).build()
//        dialog?.show()
//
//
//    }

    override fun rotateScreen(action: Action) {
        action.id?.let {
            var newAngle = binding?.rotateLayout?.angle!! + it.toInt()
            if (newAngle >= 360) {
                newAngle -= 360
            }
            if (newAngle <= -360) {
                newAngle += 360
            }
            binding?.rotateLayout?.angle = newAngle
            RealTimeDBProvider.getInstance().pushScreenAngle(newAngle)
        }
    }

    override fun rotateScreen(action: Int) {
        super.rotateScreen(action)
        var newAngle = binding?.rotateLayout?.angle!! + action
        if (newAngle >= 360) {
            newAngle -= 360
        }
        if (newAngle <= -360) {
            newAngle += 360
        }
        binding?.rotateLayout?.angle = newAngle
        RealTimeDBProvider.getInstance().pushScreenAngle(newAngle)

    }

    override fun sendChannel() {
        val l = binding!!.mainLayer.getChannel()
        if (l == null) {
            super.sendChannel()
        } else {
            RealTimeDBProvider.getInstance().sendChannel(l)
        }
    }
}
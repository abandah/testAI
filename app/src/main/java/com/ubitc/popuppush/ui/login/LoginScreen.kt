package com.ubitc.popuppush.ui.login

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.ubitc.popuppush.MConstants
import com.ubitc.popuppush.R
import com.ubitc.popuppush.databinding.ActivityLoginBinding
import com.ubitc.popuppush.models.MAIN
import com.ubitc.popuppush.models.MediaModel
import com.ubitc.popuppush.providers.BackgroundProcess
import com.ubitc.popuppush.providers.downloadx.DownloaderRecyclerView
import com.ubitc.popuppush.providers.realtimedatabase.RealTimeDBProvider
import com.ubitc.popuppush.service.db.DbService
import com.ubitc.popuppush.ui.BaseActivity
import com.ubitc.popuppush.ui.dialog.InternetDialog
import com.ubitc.popuppush.ui.main_view.MainActivity
import com.ubitc.popuppush.views.tools.MyView
import com.ubitc.popuppush.views.tools.ViewListener


/*
 * Main Activity class that loads {@link MainFragment}.
 */
class LoginScreen : BaseActivity(), LoginNav {
    var viewModel: LoginScreenViewModel? = null
    private var binding: ActivityLoginBinding? = null
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            reflectToUiAndGoToNextPermission()

        }
    private val requestInterNetDialogLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            viewModel?.start()

        }


    public override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        RealTimeDBProvider.getInstance().doAppNeedsClearData(){ needRestart , versionCode ->
            if(needRestart){
                cacheDir.deleteRecursively()
                DbService.getInstance().clearDb()
                DbService.getInstance().saveValue("versionCode",versionCode)
                deviceRestart()
            }else{
                proceedOnCreate()
                DbService.getInstance().saveValue("versionCode",versionCode)

            }
        }


    }

    private fun proceedOnCreate() {
        BackgroundProcess.getInstance().startUpdatingIAmAlive()
        Device.deviceInfo?.let {
            if (Device.isNeedAnyUpdate()) {
                if (Device.isActive == true) {
                    if (Device.isCampaignAdded == true) {
                        openActivity(MainActivity::class.java)
                        return
                    }
                }
            }
        }

        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[LoginScreenViewModel::class.java]
        binding!!.viewModel = viewModel
        binding!!.lifecycleOwner = this
        setContentView(binding!!.root)
        viewModel!!.splashNav = this
        decideHowToDealWithPermission()
        playVideoIntro()
    }


    private fun playVideoIntro() {
        binding!!.mainLayerPlayer.visibility = View.VISIBLE
        binding!!.mainLayerPlayer.playSound(R.raw.intro2, object : ViewListener {
            override fun finished(view: MyView?) {
                viewModel?.gifVideoIntroDone?.value = true
            }

            override fun viewIsReady(view: MyView?) {
                view?.playView(view.media)

            }

            override fun showLayer1(media: MediaModel?) {
            }

            override fun showLayer2(media: MediaModel?) {
            }

            override fun showLayer3(media: MediaModel?) {
            }
        })

    }


    override fun openActivity(classname: Class<*>?) {
//        binding!!.mainLayerPlayer.forceStop()
//        binding!!.mainBrowseFragment.removeAllViews()
        BackgroundProcess.getInstance().startUpdatingIAmAlive()
        val i = Intent(this, classname)
        startActivity(i)
        this.finish()

    }

    override fun showNoInternetDialog() {
        requestInterNetDialogLauncher.launch(InternetDialog.showDialog(this))
    }

    override fun showQrImage(code: Int) {
        Handler(Looper.getMainLooper()).post {
            Glide.with(this)
                .load("https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=$code")
                .into(binding!!.imageViewQr)

        }
    }

    override fun getCurrentMain(): MAIN? {
        return null
    }

    override fun campaignAssigned() {
        super.campaignAssigned()
        viewModel?.start()

    }

    override fun changeTheCampaign() {
        super.changeTheCampaign()
        viewModel?.start()
    }

    override fun statusUpdate() {
        super.statusUpdate()
        viewModel?.handelDeviceInfo(Device.deviceInfo!!)
        //viewModel?.checkDeviceActivity()
    }

    override fun deviceDeleted() {
        super.deviceDeleted()
        viewModel?.start()
    }

    override fun updateMedia() {
        super.updateMedia()
        viewModel?.start()
    }

    private var permissionIndex = 0
    private fun decideHowToDealWithPermission() {
        if (Device.permissionStepDone == true) {
            viewModel?.start()
            return
        }

        viewModel?.showMessage(
            middleMessage = getString(R.string.check_permissions),
            showProgress = true
        )
        val permission = MConstants.permissionsAll[permissionIndex]
        when (permission.permissionType) {
            0 -> {
                requestPermissionLauncher.launch(permission.permissionManifest)
            }
        }

    }

    private fun reflectToUiAndGoToNextPermission() {
        Thread {
            if (permissionIndex >= MConstants.permissionsAll.size) return@Thread
            permissionIndex++
            if (permissionIndex < MConstants.permissionsAll.size) {
                decideHowToDealWithPermission()
            } else {
                Handler(Looper.getMainLooper()).post {
                    viewModel?.start()
                }
            }
        }.start()


    }

    override fun getRootView(): DownloaderRecyclerView? {
        return binding?.downloadRecyclerView
    }

}
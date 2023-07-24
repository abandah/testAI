package com.ubitc.popuppush

import android.app.Application
import android.content.Context
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.multidex.MultiDex
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.security.ProviderInstaller
import com.ubitc.popuppush.ui.BaseActivity
import java.lang.ref.WeakReference
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import javax.net.ssl.SSLContext


class App : Application() {

    //./gradlew assembleRelease appDistributionUploadAppDistributionStagingDebug
    override fun onCreate() {
        super.onCreate()
        activity = WeakReference(this)
      //      StrictMode.enableDefaults();
//        UCEHandler.Builder(this)
//           // .setTrackActivitiesEnabled(true)
//            .build()
        try {
            // Google Play will install latest OpenSSL
            ProviderInstaller.installIfNeeded(applicationContext)
            val sslContext: SSLContext = SSLContext.getInstance("TLSv1.2")
            sslContext.init(null, null, null)
            sslContext.createSSLEngine()
        } catch (e: GooglePlayServicesRepairableException) {
            e.printStackTrace()
        } catch (e: GooglePlayServicesNotAvailableException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: KeyManagementException) {
            e.printStackTrace()
        }
    }

    override fun attachBaseContext(base: Context) {
        MultiDex.install(base)
        super.attachBaseContext(base)

    }


    companion object {
        fun getString(@StringRes contentKey: Int): String {
            return activity?.get()!!.getString(contentKey)

        }

        fun getColor(@ColorRes backGroundColor: Int): Int {
            return ContextCompat.getColor(currentActivity!!, backGroundColor)
        }

        var activity: WeakReference<Context>? = null
        var currentActivity: BaseActivity? = null

    }


}
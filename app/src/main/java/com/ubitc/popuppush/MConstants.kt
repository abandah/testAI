package com.ubitc.popuppush

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.os.StrictMode
import android.provider.Settings
import android.util.DisplayMetrics
import com.ubitc.popuppush.api_send_model.SerialNumber
import com.ubitc.popuppush.models.PermissionObj
import com.ubitc.popuppush.providers.apis.APILinks.Companion.BaseURL
import java.net.InetAddress


class MConstants {

    companion object {

        var dimension: DisplayMetrics? = null
            get() {
                if (field == null) {
                    field = App.currentActivity?.resources?.displayMetrics
                    return field
                }
                return field
            }

        val serialNumberSrt: SerialNumber
            @SuppressLint("HardwareIds")
            get() {
                @Suppress("DEPRECATION") val serial = Build.SERIAL

                return if (serial.isEmpty() || serial.equals("unknown", ignoreCase = true)) {
                    val androidId = Settings.Secure.getString(
                         App.activity?.get()?.contentResolver,
                        Settings.Secure.ANDROID_ID
                    )
                    SerialNumber(androidId + "")
                } else {
                    SerialNumber(serial)
                }
            }

        val permissionsAll: ArrayList<PermissionObj> = arrayListOf(
            PermissionObj
                .Builder(Manifest.permission.READ_EXTERNAL_STORAGE)
                .setTitle("read")
                .setMessage("READ_EXTERNAL_STORAGE")
                .setExplanationMessage("")
                .setImageResourceId(R.drawable.icon_vector)
                .setRequired(true)
                .setPermissionType(0)
                .build(),
            PermissionObj
                .Builder(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .setTitle("write")
                .setMessage("WRITE_EXTERNAL_STORAGE")
                .setExplanationMessage("")
                .setRequired(true)
                .setPermissionType(0)
                .setImageResourceId(R.drawable.icon_vector)
                .build(),
            PermissionObj.Builder(Manifest.permission.ACCESS_COARSE_LOCATION)
                .setTitle("location")
                .setMessage("ACCESS_COARSE_LOCATION")
                .setExplanationMessage("")
                .setRequired(true)
                .setPermissionType(0)
                .setImageResourceId(R.drawable.icon_vector)
                .build(),
            PermissionObj.Builder(Manifest.permission.ACCESS_FINE_LOCATION)
                .setTitle("location")
                .setMessage("ACCESS_FINE_LOCATION")
                .setExplanationMessage("")
                .setRequired(true)
                .setPermissionType(0)
                .setImageResourceId(R.drawable.icon_vector)
                .build(),
        )

        @Suppress("BooleanMethodIsAlwaysInverted")
        val isInternetAvailable: Boolean
            get() = try {
                val policy = StrictMode.ThreadPolicy.Builder()
                    .permitAll().build()
                StrictMode.setThreadPolicy(policy)
                val ipAdder = InetAddress.getByName(BaseURL)
                ipAdder.toString().isNotBlank()
            } catch (e: Exception) {
                false
            }
    }

}
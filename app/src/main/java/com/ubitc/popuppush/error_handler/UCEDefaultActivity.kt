package com.ubitc.popuppush.error_handler


import android.os.Bundle

import com.ubitc.popuppush.R
import com.ubitc.popuppush.service.apis.ApiService
import com.ubitc.popuppush.ui.BaseActivity


class UCEDefaultActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_default_error)
        ApiService.getInstance()
        deviceRestart()

    }

//    fun getApplicationName(context: Context): String {
//        val applicationInfo = context.applicationInfo
//        val stringId = applicationInfo.labelRes
//        return if (stringId == 0) applicationInfo.nonLocalizedLabel.toString() else context.getString(
//            stringId
//        )
//    }

//    private fun getVersionName(context: Context): String {
//        return try {
//            context.packageManager.getPackageInfo(context.packageName, 0).versionName
//        } catch (e: Exception) {
//            getString(R.string.Unknown)
//        }
//    }
//
//    private fun getActivityLogFromIntent(intent: Intent): String? {
//        return intent.getStringExtra(UCEHandler.eEXTRA_ACTIVITY_LOG)
//    }
//
//    private fun getError_Page(intent: Intent): String? {
//        return intent.getStringExtra(UCEHandler.eXTRA_Error_Page)
//    }
//
//    private fun getErrorMessage(intent: Intent): String? {
//        return intent.getStringExtra(UCEHandler.eXTRA_ErrorMessage)
//    }
//
//    private fun getStackTrace(intent: Intent): String? {
//        return intent.getStringExtra(UCEHandler.eXTRA_StackTrace)
//    }
//
//    private fun getStackTraceFromIntent(intent: Intent): String? {
//        return intent.getStringExtra(UCEHandler.eEXTRA_STACK_TRACE)
//    }
//
//    private fun getAllErrorDetailsFromIntent(context: Context, intent: Intent): String? {
//        return if (TextUtils.isEmpty(strCurrentErrorLog)) {
//            val LINE_SEPARATOR = "\n"
//            val errorReport = StringBuilder()
//            errorReport.append("\n***** DEVICE INFO \n")
//            errorReport.append("Brand: ")
//            errorReport.append(Build.BRAND)
//            errorReport.append(LINE_SEPARATOR)
//            errorReport.append("Device: ")
//            errorReport.append(Build.DEVICE)
//            errorReport.append(LINE_SEPARATOR)
//            errorReport.append("Model: ")
//            errorReport.append(Build.MODEL)
//            errorReport.append(LINE_SEPARATOR)
//            errorReport.append("Manufacturer: ")
//            errorReport.append(Build.MANUFACTURER)
//            errorReport.append(LINE_SEPARATOR)
//            errorReport.append("Product: ")
//            errorReport.append(Build.PRODUCT)
//            errorReport.append(LINE_SEPARATOR)
//            errorReport.append("SDK: ")
//            errorReport.append(Build.VERSION.SDK_INT)
//            errorReport.append(LINE_SEPARATOR)
//            errorReport.append("Release: ")
//            errorReport.append(Build.VERSION.RELEASE)
//            errorReport.append(LINE_SEPARATOR)
//            errorReport.append("\n***** APP INFO \n")
//            val versionName = getVersionName(context)
//            errorReport.append("Version: ")
//            errorReport.append(versionName)
//            errorReport.append(LINE_SEPARATOR)
//            val currentDate = Date()
//            val dateFormat: DateFormat =
//                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
//            val firstInstallTime = getFirstInstallTimeAsString(context, dateFormat)
//            if (!TextUtils.isEmpty(firstInstallTime)) {
//                errorReport.append("Installed On: ")
//                errorReport.append(firstInstallTime)
//                errorReport.append(LINE_SEPARATOR)
//            }
//            val lastUpdateTime = getLastUpdateTimeAsString(context, dateFormat)
//            if (!TextUtils.isEmpty(lastUpdateTime)) {
//                errorReport.append("Updated On: ")
//                errorReport.append(lastUpdateTime)
//                errorReport.append(LINE_SEPARATOR)
//            }
//            errorReport.append("Current Date: ")
//            errorReport.append(dateFormat.format(currentDate))
//            errorReport.append(LINE_SEPARATOR)
//            errorReport.append("\n***** ERROR LOG \n")
//            errorReport.append(getStackTraceFromIntent(intent))
//            errorReport.append(LINE_SEPARATOR)
//            val activityLog = getActivityLogFromIntent(intent)
//            errorReport.append(LINE_SEPARATOR)
//            if (activityLog != null) {
//                errorReport.append("\n***** USER ACTIVITIES \n")
//                errorReport.append("User Activities: ")
//                errorReport.append(activityLog)
//                errorReport.append(LINE_SEPARATOR)
//            }
//            errorReport.append("\n***** END OF LOG *****\n")
//            strCurrentErrorLog = errorReport.toString()
//            strCurrentErrorLog
//        } else {
//            strCurrentErrorLog
//        }
//    }
//
//    private fun getFirstInstallTimeAsString(context: Context, dateFormat: DateFormat): String {
//        val firstInstallTime: Long
//        return try {
//            firstInstallTime = context
//                .packageManager
//                .getPackageInfo(context.packageName, 0).firstInstallTime
//            dateFormat.format(Date(firstInstallTime))
//        } catch (e: PackageManager.NameNotFoundException) {
//            ""
//        }
//    }
//
//    private fun getLastUpdateTimeAsString(context: Context, dateFormat: DateFormat): String {
//        val lastUpdateTime: Long
//        return try {
//            lastUpdateTime = context
//                .packageManager
//                .getPackageInfo(context.packageName, 0).lastUpdateTime
//            dateFormat.format(Date(lastUpdateTime))
//        } catch (e: PackageManager.NameNotFoundException) {
//            ""
//        }
//    }
//
//    private fun SendError() {
//        val Error_Product = "OfferSwiper"
//        val Error_Customer = "Android"
//        val Error_Page = getError_Page(intent)
//        val Error_Message = getErrorMessage(intent)
//        val Error_Details = getStackTrace(intent)
//        val Error_note = getAllErrorDetailsFromIntent(this@UCEDefaultActivity, intent)
//    }
}
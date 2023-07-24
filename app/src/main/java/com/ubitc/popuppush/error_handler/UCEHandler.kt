package com.ubitc.popuppush.error_handler

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Process
import android.text.TextUtils
import android.util.Log
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import java.io.PrintWriter
import java.io.StringWriter
import java.lang.ref.WeakReference
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.exitProcess

class UCEHandler internal constructor(builder: Builder) {
    init {
        isUCEHEnabled = builder.isUCEHEnabled
        isTrackActivitiesEnabled = builder.isTrackActivitiesEnabled
        isBackgroundMode = builder.isBackgroundModeEnabled
        commaSeparatedEmailAddresses = builder.commaSeparatedEmailAddresses
        uceHandlerPackageName = getApplicationName(builder.context)
        setUCEHandler(builder.context)
    }

    private fun getApplicationName(context: Context): String {
        return context.applicationContext.packageName
    }

    class Builder(val context: Context) {
        var isUCEHEnabled = true
        var commaSeparatedEmailAddresses: String? = null
        var isTrackActivitiesEnabled = false
        var isBackgroundModeEnabled = true
//        fun setUCEHEnabled(isUCEHEnabled: Boolean) = apply { this.isUCEHEnabled = isUCEHEnabled }
//
//        fun setTrackActivitiesEnabled(isTrackActivitiesEnabled: Boolean)= apply {
//            this.isTrackActivitiesEnabled = isTrackActivitiesEnabled
//        }
//
//        fun setBackgroundModeEnabled(isBackgroundModeEnabled: Boolean)= apply {
//            this.isBackgroundModeEnabled = isBackgroundModeEnabled
//        }
//
//        fun addCommaSeparatedEmailAddresses(commaSeparatedEmailAddresses: String?)= apply {
//            this.commaSeparatedEmailAddresses = commaSeparatedEmailAddresses ?: ""
//        }

        fun build(): UCEHandler {
            return UCEHandler(this)
        }
    }

    companion object {
        private const val eXTRA_ErrorMessage = "ErrorMessage"
        private const val eXTRA_StackTrace = "StackTrace"
        private const val extraStackTrace = "eEXTRA_STACK_TRACE"
        private const val eXTRA_Error_Page = "Error_Page"
        private const val eEXTRA_ACTIVITY_LOG = "eEXTRA_ACTIVITY_LOG"
       // private const val TAG = "UCEHandler"
       // private const val DEFAULT_HANDLER_PACKAGE_NAME = "com.android.internal.os"
        private val DEFAULT_ANDROID_PACKAGE_NAME = getDefaultAndroidPackageName()
        private const val MAX_STACK_TRACE_SIZE = 131071 //128 KB - 1
        private const val MAX_ACTIVITIES_IN_LOG = 50
        private const val SHARED_PREFERENCES_FILE = "uceh_preferences"
        private const val SHARED_PREFERENCES_FIELD_TIMESTAMP = "last_crash_timestamp"
        private val activityLog: Deque<String> = ArrayDeque(MAX_ACTIVITIES_IN_LOG)
        var commaSeparatedEmailAddresses: String? = null
        private var uceHandlerPackageName = ""
       // var Link = ""
        private const val prefix = "\n\tat "

        @SuppressLint("StaticFieldLeak")
        private var application: Application? = null
        private var isInBackground = true
        private var isBackgroundMode: Boolean = false
        private var isUCEHEnabled: Boolean  = false
        private var isTrackActivitiesEnabled: Boolean = false
        private var lastActivityCreated = WeakReference<Activity?>(null)
        private fun setUCEHandler(context: Context?) {
            try {
                if (context != null) {
                    val oldHandler = Thread.getDefaultUncaughtExceptionHandler()
                    if (oldHandler == null || !oldHandler.javaClass.name.startsWith(uceHandlerPackageName)) {
//                        if (oldHandler == null || oldHandler.javaClass.name.startsWith(DEFAULT_HANDLER_PACKAGE_NAME)) {
//                        }
                        application = context.applicationContext as Application
                        //Setup UCE Handler.
                        Thread.setDefaultUncaughtExceptionHandler(Thread.UncaughtExceptionHandler { thread, throwable ->
                            Firebase.crashlytics.recordException(throwable)
                            if (isUCEHEnabled) {
                                if (hasCrashedInTheLastSeconds(application)) {
                                    if (oldHandler != null) {
                                        oldHandler.uncaughtException(thread, throwable)
                                        return@UncaughtExceptionHandler
                                    }
                                } else {
                                    setLastCrashTimestamp(application, Date().time)
                                    if (!isInBackground || isBackgroundMode) {
                                        val intent =
                                            Intent(application, UCEDefaultActivity::class.java)
                                        val sw = StringWriter()
                                        val pw = PrintWriter(sw)
                                        throwable.printStackTrace(pw)
                                        var stackTraceString = sw.toString()
                                        val mainStackTraceString = stackTraceString
                                        val strings2 = listOf(
                                            *stackTraceString.split(
                                                prefix
                                            ).toTypedArray()
                                        )
                                        val strings = ArrayList<String?>()
                                        val error = ArrayList<String>()
                                        // ArrayList<String> strings = new ArrayList<>();
                                        for (i in strings2.indices) {
                                            if (!strings2[i].contains(DEFAULT_ANDROID_PACKAGE_NAME)) {
                                                strings.add(strings2[i])
                                            }
                                            if (strings2[i].startsWith(uceHandlerPackageName)) {
                                                error.add(strings2[i])
                                            }
                                        }
                                        stackTraceString = TextUtils.join(prefix, strings)
                                        if (stackTraceString.length > MAX_STACK_TRACE_SIZE) {
                                            val disclaimer = " [stack trace too large]"
                                            stackTraceString = stackTraceString.substring(
                                                0,
                                                MAX_STACK_TRACE_SIZE - disclaimer.length
                                            ) + disclaimer
                                            Log.e(
                                                "UCEHandler",
                                                stackTraceString
                                            )
                                        }
                                        intent.putExtra(extraStackTrace, mainStackTraceString)
                                        if (isTrackActivitiesEnabled) {
                                            val activityLogStringBuilder = StringBuilder()
                                            while (!activityLog.isEmpty()) {
                                                activityLogStringBuilder.append(activityLog.poll())
                                            }
                                            intent.putExtra(
                                                eEXTRA_ACTIVITY_LOG,
                                                activityLogStringBuilder.toString()
                                            )
                                        }
                                        intent.putExtra(
                                            eXTRA_Error_Page,
                                            error[0]
                                        )
                                        intent.putExtra(eXTRA_ErrorMessage, strings2[0])
                                        intent.putExtra(eXTRA_StackTrace, mainStackTraceString)
                                        intent.flags =
                                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        application!!.startActivity(intent)
                                    } else {
                                        if (oldHandler != null) {
                                            oldHandler.uncaughtException(thread, throwable)
                                            return@UncaughtExceptionHandler
                                        }
                                        //If it is null (should not be), we let it continue and kill the process or it will be stuck
                                    }
                                }
                                val lastActivity = lastActivityCreated.get()
                                if (lastActivity != null) {
                                    lastActivity.finish()
                                    lastActivityCreated.clear()
                                }
                                killCurrentProcess()
                            } else oldHandler?.uncaughtException(thread, throwable)
                        })
                        application!!.registerActivityLifecycleCallbacks(object :
                            ActivityLifecycleCallbacks {
                            val dateFormat: DateFormat =
                                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
                            var currentlyStartedActivities = 0
                            override fun onActivityCreated(
                                activity: Activity,
                                savedInstanceState: Bundle?
                            ) {
                                if (activity.javaClass != UCEDefaultActivity::class.java) {
                                    lastActivityCreated = WeakReference(activity)
                                }
                                if (isTrackActivitiesEnabled) {
                                    activityLog.add(
                                        """${dateFormat.format(Date())}: ${activity.javaClass.simpleName} created
                """
                                    )
                                }
                            }

                            override fun onActivityStarted(activity: Activity) {
                                currentlyStartedActivities++
                                isInBackground = currentlyStartedActivities == 0
                            }

                            override fun onActivityResumed(activity: Activity) {
                                if (isTrackActivitiesEnabled) {
                                    activityLog.add(
                                        """${dateFormat.format(Date())}: ${activity.javaClass.simpleName} resumed
                """
                                    )
                                }
                            }

                            override fun onActivityPaused(activity: Activity) {
                                if (isTrackActivitiesEnabled) {
                                    activityLog.add(
                                        """${dateFormat.format(Date())}: ${activity.javaClass.simpleName} paused
                """
                                    )
                                }
                            }

                            override fun onActivityStopped(activity: Activity) {
                                currentlyStartedActivities--
                                isInBackground = currentlyStartedActivities == 0
                            }

                            override fun onActivitySaveInstanceState(
                                activity: Activity,
                                outState: Bundle
                            ) {
                            }

                            override fun onActivityDestroyed(activity: Activity) {
                                if (isTrackActivitiesEnabled) {
                                    activityLog.add(
                                        """${dateFormat.format(Date())}: ${activity.javaClass.simpleName} destroyed
                """
                                    )
                                }
                            }
                        })
                    }
                }

            } catch (_: Throwable) {
            }
        }

//        private fun getErrorPage(throwable: ArrayList<String?>): String {
//            return TextUtils.join("|", throwable)
//        }

        /**
         * INTERNAL method that tells if the app has crashed in the last seconds.
         * This is used to avoid restart loops.
         *
         * @return true if the app has crashed in the last seconds, false otherwise.
         */
        private fun hasCrashedInTheLastSeconds(context: Context?): Boolean {
            val lastTimestamp = getLastCrashTimestamp(context)
            val currentTimestamp = Date().time
            return lastTimestamp <= currentTimestamp && currentTimestamp - lastTimestamp < 3000
        }

        @SuppressLint("ApplySharedPref")
        private fun setLastCrashTimestamp(context: Context?, timestamp: Long) {
            context!!.getSharedPreferences(SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE).edit()
                .putLong(
                    SHARED_PREFERENCES_FIELD_TIMESTAMP, timestamp
                ).commit()
        }

        private fun killCurrentProcess() {
            Process.killProcess(Process.myPid())
            exitProcess(10)
        }

        private fun getLastCrashTimestamp(context: Context?): Long {
            return context!!.getSharedPreferences(SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE)
                .getLong(
                    SHARED_PREFERENCES_FIELD_TIMESTAMP, -1
                )
        }

//        fun closeApplication(activity: Activity) {
//            activity.finish()
//            killCurrentProcess()
//        }

//        fun restartApplication(activity: Activity) {
//            val launchIntent = activity.packageManager.getLaunchIntentForPackage(
//                uceHandlerPackageName
//            )
//            activity.startActivity(launchIntent)
//            activity.finish()
//            killCurrentProcess()
//        }

        private fun getDefaultAndroidPackageName(): String {
            val strings = ArrayList<String?>()
            strings.add("com.android.")
            strings.add("android.")
            strings.add("java.")
            var s = TextUtils.join("|", strings)
            s = "($s).*"
            return s
        }
    }
}
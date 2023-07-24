package com.ubitc.popuppush.receiver

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.IntentFilter

class InternetReceivers(val activity: Activity) {


    private var mNetworkReceiver: BroadcastReceiver? = null


    fun restartReceivers() {
        disconnectReceivers()
        initReceivers()
        registerReceivers()


    }

    private fun registerReceivers() {
        val filter = IntentFilter()
       // filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE" )

        activity.registerReceiver(
            mNetworkReceiver,
            filter
        )

    }

    fun disconnectReceivers() {
        try {
            activity.unregisterReceiver(mNetworkReceiver)

        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
        mNetworkReceiver = null

    }

    private fun initReceivers() {
        mNetworkReceiver = NetworkChangeReceiver(activity as NetworkChangeReceiver.ConnectionListener)

    }
}
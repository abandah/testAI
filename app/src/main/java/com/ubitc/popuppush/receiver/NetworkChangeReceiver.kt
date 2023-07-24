package com.ubitc.popuppush.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ubitc.popuppush.MConstants


class NetworkChangeReceiver(private var mainFragment: ConnectionListener?) : BroadcastReceiver() {


    override fun onReceive(context: Context, intent: Intent) {

        if (MConstants.isInternetAvailable) {

            mainFragment?.onConnected()

        }
    }


    interface ConnectionListener {
        fun onConnected()
        fun onNotConnected()
    }


}
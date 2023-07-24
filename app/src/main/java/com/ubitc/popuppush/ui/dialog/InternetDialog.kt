package com.ubitc.popuppush.ui.dialog

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ubitc.popuppush.R
import com.ubitc.popuppush.receiver.InternetReceivers
import com.ubitc.popuppush.receiver.NetworkChangeReceiver

class InternetDialog : AppCompatActivity() , NetworkChangeReceiver.ConnectionListener{

    companion object {
        fun showDialog(context: Context):Intent{
            return Intent(context,InternetDialog::class.java)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_internet_dialog)
        setFinishOnTouchOutside(false)
    }

    override fun onStart() {
        super.onStart()
        InternetReceivers(this).restartReceivers()
    }

    override fun onDestroy() {
        super.onDestroy()
        InternetReceivers(this).disconnectReceivers()
    }

    override fun onConnected() {
        finish()
        InternetReceivers(this).disconnectReceivers()
    }

    override fun onNotConnected() {
    }
}
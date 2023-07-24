package com.ubitc.popuppush.ui.login

interface LoginNav {
    fun openActivity(classname: Class<*>?)
    fun showNoInternetDialog()
    fun showQrImage(code: Int)
}
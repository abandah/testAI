package com.ubitc.popuppush.views.web_view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.webkit.WebSettings
import android.webkit.WebView
import com.ubitc.popuppush.models.MediaModel
import com.ubitc.popuppush.views.tools.MyView
import com.ubitc.popuppush.views.tools.ViewListener

class MyWebView : WebView, MyView {
    override var media: MediaModel?=null
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun init() {
        settings.javaScriptEnabled = true
        settings.loadWithOverviewMode = true
        settings.useWideViewPort = true
        settings.builtInZoomControls = true
        settings.javaScriptCanOpenWindowsAutomatically = true
        settings.loadWithOverviewMode = true
        //settings.allowFileAccessFromFileURLs = true
       // settings.allowUniversalAccessFromFileURLs = true
        settings.useWideViewPort = true
        settings.domStorageEnabled = true
        settings.setSupportMultipleWindows(true)
        settings.cacheMode = WebSettings.LOAD_NO_CACHE
       // settings.pluginState = WebSettings.PluginState.ON
        settings.builtInZoomControls = true
        scrollBarStyle = SCROLLBARS_OUTSIDE_OVERLAY
        isScrollbarFadingEnabled = false

    }

     override fun prepareView(media: MediaModel?, viewListener: ViewListener?) {super.prepareView(media, viewListener)
//        val webViewClient = object : WebViewClient() {
//            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap) {
//                super.onPageStarted(view, url, favicon)
//            }
//
//            override fun onPageFinished(view: WebView, url: String) {
//                super.onPageFinished(view, url)
//
//            }
//
//            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
//                return false
//            }
//        }
     //   setWebViewClient(webViewClient)
        //  webViewClient = chromeWebClient
        val url : String= media?.path!!
        loadUrl(url)

        viewListener?.viewIsReady(this@MyWebView)

     //   viewListener?.viewIsReady(this)

    }






    override fun isVisible(): Boolean {
        return this.visibility == VISIBLE
    }
    override fun pauseView() {

    }

    override fun resumeView() {

    }

    override fun playView(media: MediaModel?) {

    }

    override fun mute() {

    }

}
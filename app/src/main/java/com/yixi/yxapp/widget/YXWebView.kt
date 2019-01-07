package com.yixi.yxapp.widget

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import com.yixi.yxapp.BuildConfig
import com.yixi.yxapp.MainActivity

class YXWebView : WebView{
    internal var mContext: Context

    constructor(context: Context) : super(context) {
        this.mContext = context
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        this.mContext = context

    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        this.mContext = context
    }

    fun initialze() {
        isVerticalScrollBarEnabled = false
        isHorizontalScrollBarEnabled = false
        isVerticalFadingEdgeEnabled = false
        isHorizontalFadingEdgeEnabled = false
        webChromeClient = WebChromeClient()
        setOnLongClickListener { true }
        val settings = settings
        settings.setSupportZoom(true)
        settings.textZoom = 100
        if (BuildConfig.DEBUG && Build.VERSION.SDK_INT > 18) {
            WebView.setWebContentsDebuggingEnabled(true)
        }
        settings.builtInZoomControls = true
        settings.displayZoomControls = false
        settings.cacheMode = WebSettings.LOAD_DEFAULT
        if((context as MainActivity).isTableDevice()){
            settings.userAgentString = settings.userAgentString + " Android"
        }else{
            settings.userAgentString = settings.userAgentString + " Android"
        }
        if(Build.VERSION.SDK_INT >= 21){

            settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }



        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.setAppCachePath(mContext.applicationContext.cacheDir.absolutePath)
        settings.allowFileAccess = true
        settings.setAppCacheEnabled(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            settings.mediaPlaybackRequiresUserGesture = false
        }
    }
}
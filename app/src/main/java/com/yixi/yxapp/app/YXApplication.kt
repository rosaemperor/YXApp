package com.yixi.yxapp.app

import android.app.Activity
import android.app.Application
import android.os.Build
import android.os.Bundle
import com.qubuxing.qbx.utils.SharePrefenceHelper
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.vhall.classsdk.VHClass
import com.yixi.yxapp.commons.config

class YXApplication : Application(){
    var appCount = 0
    companion object {
        lateinit var  instance : YXApplication
        var buildCode = "20181015"
        lateinit var api : IWXAPI
        var device = Build.BOARD + Build.DEVICE + Build.SERIAL
    }

    val tag : String = "Application"
    /**
     * 如果需要分包，请将方法置于 super.onCreate() 之前
     */
    override fun onCreate() {
        super.onCreate()
        instance = this
        initUtils()
        initSdk()

    }

    private fun initSdk() {
        VHClass.getInstance().init(this@YXApplication, config.VIDEO_APPKey, config.VIDEO_SecretKey)
        registJPush()
        registToWX()
    }

    private fun registJPush() {
//        JPushInterface.setDebugMode(true)
//        JPushInterface.init(this)
    }

    private fun registToWX() {
        api = WXAPIFactory.createWXAPI(this,config.WXAPP_ID,true)
        api.registerApp(config.WXAPP_ID)
    }

    private fun initUtils() {
        SharePrefenceHelper.initSharePreference(applicationContext)
        registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityPaused(activity: Activity?) {

            }

            override fun onActivityResumed(activity: Activity?) {
            }

            override fun onActivityStarted(activity: Activity?) {
                appCount++
            }

            override fun onActivityDestroyed(activity: Activity?) {
            }

            override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
            }

            override fun onActivityStopped(activity: Activity?) {
                appCount--
            }

            override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
            }
        })

    }
    fun getWXAPI(): IWXAPI {
        return api
    }
    fun  isForeground() : Boolean{
        return appCount > 0
    }
}
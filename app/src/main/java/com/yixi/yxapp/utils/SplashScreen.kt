package com.qubuxing.qbx.utils

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.constraint.ConstraintLayout
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.*
import com.yixi.yxapp.MainActivity
import com.yixi.yxapp.R


import java.lang.ref.WeakReference

/**
 * SplashScreen
 * 启动屏
 * from：http://www.devio.org
 * Author:CrazyCodeBoy
 * GitHub:https://github.com/crazycodeboy
 * Email:crazycodeboy@gmail.com
 */
object SplashScreen {
    private var mSplashDialog: Dialog? = null
    private var mActivity: WeakReference<Activity>? = null
    var b = false
    lateinit var view: ConstraintLayout
    lateinit var containLayout: FrameLayout

    lateinit var fullImageView : ImageView
    var cameraList = ArrayList<String>()
    var adShowed = false
    /**
     * 打开启动屏
     */

    @JvmOverloads
    fun show(activity: Activity?, fullScreen: Boolean = true) {
        b = true
        if (activity == null) return
        mActivity = WeakReference(activity)
        activity.runOnUiThread {
            if (!activity.isFinishing) {
                mSplashDialog = Dialog(activity, if (fullScreen) R.style.SplashScreen_Fullscreen else R.style.SplashScreen_SplashTheme)
                view = LayoutInflater.from(activity).inflate(R.layout.dialog_splash_layout, null) as ConstraintLayout
                //                    params.setMargins(0,ScreenUtils.getStatusHeight(activity),0, 0);
                containLayout = view.findViewById<FrameLayout>(R.id.splash_container)
                //                    view.setLayoutParams(params);
                mSplashDialog!!.setContentView(view)
                var splashLayout = view.findViewById<ImageView>(R.id.splash_layout)
                if ((activity as MainActivity).isTableDevice()){
                    splashLayout.background = activity.resources.getDrawable(R.mipmap.spalsh_background_teacher_land,null)
                }
                mSplashDialog!!.setCancelable(false)
                fullImageView = view.findViewById(R.id.fullscreen_img)
                mSplashDialog!!.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
                mSplashDialog!!.window!!.setDimAmount(0f)
                if (mSplashDialog != null && !mSplashDialog!!.isShowing) {
                    mSplashDialog!!.show()
                }
            }
        }
    }

    /**
     * 关闭启动屏
     */
    fun hide(activity: Activity?) {
        var activity = activity
        if (activity == null) activity = mActivity!!.get()
        if (activity == null) return

        activity.runOnUiThread {
            if (mSplashDialog != null && mSplashDialog!!.isShowing ) {
                try {
                    mSplashDialog!!.dismiss()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
    }
}
/**
 * 打开启动屏
 */
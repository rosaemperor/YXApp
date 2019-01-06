package com.yixi.yxapp.parts

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.media.Image
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.telephony.TelephonyManager
import android.util.Base64
import android.util.Log
import android.view.View
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.widget.Toast
import com.bumptech.glide.Glide


import com.google.gson.Gson
import com.google.gson.JsonElement
import com.qubuxing.qbx.utils.*
import com.qubuxing.qbx.utils.SharePrefenceHelper.Companion.mContext
import com.tencent.mm.opensdk.modelmsg.*
import com.vhall.base.IVHPlayer
import com.vhall.base.IVHWatchCallBack
import com.vhall.classsdk.ClassInfo
import com.vhall.classsdk.Constant
import com.vhall.classsdk.VHClass
import com.vhall.classsdk.WatchLive
import com.vhall.jni.VhallLiveObs
import com.vhall.jni.VideoInfo
import com.vhall.watchlive.play.IVideoPlayer
import com.yixi.yxapp.FunctionActivity
import com.yixi.yxapp.MainActivity
import com.yixi.yxapp.R
import com.yixi.yxapp.app.YXApplication
import com.yixi.yxapp.databinding.ActivityMainBinding
import com.yixi.yxapp.http.HttpService
import com.yixi.yxapp.http.RetrofitUtil
import com.yixi.yxapp.http.entities.LiveRoomEntity
import com.yixi.yxapp.http.entities.MyWebbinar
import com.yixi.yxapp.http.entities.WXSeneEntity
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class WVWebViewClient constructor(webView: WebView,messageHandler: WVJBHandler? = null) : WVJBWebViewClient(webView,messageHandler) {
    var thread : Thread ?= null
    var gson:Gson = Gson()
    var imageGetMobile :String=""
    var imageType = ""
    var firstClickTime: Long = 0L
    var codeCallback: WVJBResponseCallback? = null
    var CAMERA_REQUEST_CODE=1110
    var READ_PHONE=10086
    var pageGetFinished = false
    var UUIDCallback : WVJBResponseCallback? = null
    var cameraList = ArrayList<String>()
    var ACTIVITYFOROMCLIENT = 10010
    var binding : ActivityMainBinding
     var stepCallback: WVJBResponseCallback? = null
    var httpHelper : HttpService = RetrofitUtil.instance.help
    var haveStepToday : Int = 0
    constructor(webView: WebView) : this(webView ,object :WVJBHandler{
        override fun request(data: Any?, callback: WVJBResponseCallback?) {
            callback!!.callback("Response for message from ObjC!")
        }
    })
    init {
        registerHandler("backPress", object : WVJBHandler {
            override fun request(data: Any?, callback: WVJBResponseCallback?) {
                val s = ""
                val jsonObject = data as org.json.JSONObject
                try {
                    val b = jsonObject.getInt("isRootPage")
                    when(b){
                        0->{

                        }
                        -1->{
                            if (System.currentTimeMillis() - firstClickTime < 2000) {
                                (webView.context as Activity).finish()
                            } else {
                                firstClickTime = System.currentTimeMillis()
                                Toast.makeText(webView.context, R.string.double_click_to_quit, Toast.LENGTH_SHORT).show()
                            }
                        }
                        else ->{
                            webView.goBackOrForward(-b)
                        }
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }
        })

//微信登录调起
        registerHandler("WXSceneSessionClick",object : WVJBHandler{
            override fun request(data: Any?, callback: WVJBResponseCallback?) {
                var entity = gson.fromJson<WXSeneEntity>(data.toString(),WXSeneEntity::class.java)
                var webpage = WXWebpageObject()
                webpage.webpageUrl = entity.webpageUrl
                var msg = WXMediaMessage()
                msg.mediaObject = webpage
                msg.title = entity.title
                msg.description =entity.information
                var req = SendMessageToWX.Req()
                var bitmap: Bitmap? = null
                thread = Thread(Runnable{
                    //                    bitmap = Glide.with(webView.context).asBitmap().load(entity.imageurl).into(500,500).get()
                    bitmap = Glide.with(webView.context).load(entity.imageurl).asBitmap().into(200,200).get()
                    bitmap = BitmapUtils.drawableBitmapOnWhiteBg(webView.context,bitmap!!)
                    msg.setThumbImage(bitmap)
                    req.message = msg
                    req.scene = SendMessageToWX.Req.WXSceneSession
                    req.transaction = entity.webpageUrl
                    var api = YXApplication.api
                    api.sendReq(req)
                })
                thread!!.start()


            }
        })
        registerHandler("WXSceneTimelineClick",object : WVJBHandler{
            override fun request(data: Any?, callback: WVJBResponseCallback?) {
                var entity = gson.fromJson<WXSeneEntity>(data.toString(),WXSeneEntity::class.java)
                var webpage = WXWebpageObject()
                webpage.webpageUrl = entity.webpageUrl

                var msg = WXMediaMessage()
                msg.mediaObject = webpage
                msg.title = entity.title
                msg.description =entity.information
                var req = SendMessageToWX.Req()
                var bitmap: Bitmap? = null
                thread = Thread(Runnable{
                    //                    bitmap = Glide.with(webView.context).asBitmap().load(entity.imageurl).into(500,500).get()

                    bitmap = Glide.with(webView.context).load(entity.imageurl).asBitmap().into(200,200).get()
                    bitmap = BitmapUtils.drawableBitmapOnWhiteBg(webView.context,bitmap!!)
                    msg.setThumbImage(bitmap)
                    req.message = msg
                    req.scene = SendMessageToWX.Req.WXSceneTimeline
                    req.transaction = entity.webpageUrl
                    var api = YXApplication.api
                    api.sendReq(req)
                })
                thread!!.start()
            }
        })
        binding = DataBindingUtil.findBinding<ActivityMainBinding>(webView)!!

        registerHandler("enterClassRoom", object : WVJBHandler{
            override fun request(data: Any?, callback: WVJBResponseCallback?) {
//                var entityVS = LiveRoomEntity()
//            entityVS.roomId = "edu_75ff4d18"
//            entityVS.nickName ="Arthur"
//            entityVS.keyValue = "862327"
//            var gson = Gson()
//            var dataStrig = gson.toJson(entityVS)
                var entity = gson.fromJson<LiveRoomEntity>(data.toString(),LiveRoomEntity::class.java)
                VHClass.getInstance().getClassInfo(entity.roomId,YXApplication.device, object : VHClass.ClassInfoCallback{
                    override fun onSuccess(p0: ClassInfo.Webinar?) {
                        var myWebbinar = p0  as MyWebbinar
//                    Log.d("TAG",myWebbinar.)
                        var requestCalback = object  : VHClass.RequestCallback{
                            override fun onSuccess() {
//                                currentFunc = VHClass.getInstance().classStatus
//                                Log.d("TAG",""+VHClass.getInstance().classStatus)
//                                if (currentFunc == MainActivity.FUC_LIVE && VHClass.getInstance().classStatus != Constant.CLASS_STATUS_LIVE) {
//                                    Toast.makeText(webView.context, "当前不是直播状态", Toast.LENGTH_SHORT).show()
//                                    return
//                                } else if (currentFunc == MainActivity.FUC_PLAYBACK && VHClass.getInstance().classStatus != Constant.CLASS_STATUS_BACK) {
//                                    Toast.makeText(webView.context, "当前不是回放状态", Toast.LENGTH_SHORT).show()
//                                    return
//                                }
                                val intent = Intent(webView.context, FunctionActivity::class.java)
                                intent.putExtra("fuc", VHClass.getInstance().classStatus)
                                webView.context.startActivity(intent)
//
//
                            }

                            override fun onError(p0: Int, p1: String?) {
                            }
                        }
                        VHClass.getInstance().joinClass("${entity.roomId}",YXApplication.device ,"${entity.nickName}","${entity.keyValue}", requestCalback)
                    }

                    override fun onError(p0: Int, p1: String?) {
                    }
                })
            }
        })

        registerHandler("enterLiveRoom",object : WVJBHandler{
            override fun request(data: Any?, callback: WVJBResponseCallback?) {
                var entity = gson.fromJson<LiveRoomEntity>(data.toString(),LiveRoomEntity::class.java)
                VHClass.getInstance().getClassInfo(entity.roomId,YXApplication.device, object : VHClass.ClassInfoCallback{
                    override fun onSuccess(p0: ClassInfo.Webinar?) {
                        var myWebbinar = p0  as MyWebbinar
//                    Log.d("TAG",myWebbinar.)
                        var requestCalback = object  : VHClass.RequestCallback{
                            override fun onSuccess() {
//                                currentFunc = VHClass.getInstance().classStatus
//                                Log.d("TAG",""+VHClass.getInstance().classStatus)
//                                if (currentFunc == MainActivity.FUC_LIVE && VHClass.getInstance().classStatus != Constant.CLASS_STATUS_LIVE) {
//                                    Toast.makeText(webView.context, "当前不是直播状态", Toast.LENGTH_SHORT).show()
//                                    return
//                                } else if (currentFunc == MainActivity.FUC_PLAYBACK && VHClass.getInstance().classStatus != Constant.CLASS_STATUS_BACK) {
//                                    Toast.makeText(webView.context, "当前不是回放状态", Toast.LENGTH_SHORT).show()
//                                    return
//                                }
                                val intent = Intent(webView.context, FunctionActivity::class.java)
                                intent.putExtra("fuc", VHClass.getInstance().classStatus)
                                webView.context.startActivity(intent)
//
//
                            }

                            override fun onError(p0: Int, p1: String?) {
                            }
                        }
                        VHClass.getInstance().joinClass("${entity.roomId}",YXApplication.device ,"${entity.nickName}","${entity.keyValue}", requestCalback)
                    }

                    override fun onError(p0: Int, p1: String?) {
                    }
                })
            }
        })

    }

    override fun onPageFinished(view: WebView, url: String) {
        super.onPageFinished(view, url)
        pageGetFinished = true
        var  binding = DataBindingUtil.findBinding<ActivityMainBinding>(view)
        if(binding!!.swipeLayout.isRefreshing) binding.swipeLayout.isRefreshing = false
//        showSplashAd()
        SplashScreen.hide(view.context as Activity?)
    }


    fun onActivityResult(intent : Intent){


    }






    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (grantResults.isEmpty()) return
        when(requestCode){
            CAMERA_REQUEST_CODE ->{
                if (grantResults[0]==0 && grantResults[1] ==0 ){
                    takePhoto()
                }
            }
        }

    }

    private fun takePhoto() {
        val intent = Intent("android.media.action.IMAGE_CAPTURE")
        (webView.context as Activity).startActivityForResult(intent, ACTIVITYFOROMCLIENT)
    }

    /**
     * 回传微信code给前端H5
     * @param code
     */
//    fun resopnseCode(code: String) {
//        if (null != codeCallback) {
//            var wxCode = WXCode()
//            wxCode.code=code
//            codeCallback!!.callback(gson.toJson(wxCode))
//        }
//    }


    override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
        networkError(webView)
    }

    override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (request.url.toString() == webView.url)
                networkError(view)
        } else {
            networkError(view)
        }
    }

    private fun networkError(view: WebView) {
        view.visibility = View.INVISIBLE
        pageGetFinished = true
                var binding = DataBindingUtil.findBinding<ActivityMainBinding>(view)
//        binding!!.webView.visibility = View.GONE
        binding!!.llNetworkError.visibility = View.VISIBLE
        if (SplashScreen.b) {
            SplashScreen.b = false
        }

    }




}
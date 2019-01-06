package com.yixi.yxapp

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.os.SystemClock
import android.support.annotation.RequiresApi
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.webkit.WebChromeClient
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.qubuxing.qbx.utils.SharePrefenceHelper
import com.qubuxing.qbx.utils.SplashScreen
import com.vhall.base.IVHPlayer
import com.vhall.base.IVHWatchCallBack
import com.vhall.classsdk.*
import com.vhall.classsdk.service.ChatServer
import com.vhall.classsdk.service.MessageServer
import com.vhall.watchlive.play.IVideoPlayer
import com.yixi.yxapp.app.YXApplication
import com.yixi.yxapp.base.BaseActivity
import com.yixi.yxapp.commons.config
import com.yixi.yxapp.databinding.ActivityMainBinding
import com.yixi.yxapp.http.entities.LiveRoomEntity
import com.yixi.yxapp.http.entities.MyWebbinar
import com.yixi.yxapp.parts.WVWebViewClient
import com.yixi.yxapp.viewModels.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

class MainActivity : BaseActivity() {
    lateinit var binding : ActivityMainBinding
    lateinit var client: WVWebViewClient

    lateinit var viewModel : MainViewModel
     var currentFunc = FUC_LIVE
    var mStepSum = 0
    lateinit var vhClassLive : WatchLive
    var cameraList = ArrayList<String>()

    var dontGrantedPermissions: MutableList<String> = ArrayList()
    override fun initBinding() {
        binding = DataBindingUtil.setContentView(this@MainActivity,R.layout.activity_main)
        SplashScreen.show(this@MainActivity)
    }
    companion object {
         val PAGE_ROOM = 0
         val PAGE_LOGIN = 1
         val PAGE_FUC = 2

        var FUC_LIVE  : Int = 1
        val FUC_INTERACTIVE = 6
        val FUC_PLAYBACK = 3
        val FUC_CHAT = 4
        val FUC_DOC = 5

        val CLASS_STATUS_START = 1 // 上课
        val CLASS_STATUS_PREPARE = 2 // 预告
        val CLASS_STATUS_BACK = 3 // 回放
        val CLASS_STATUS_CONVERT = 4 // 转播
        val CLASS_STATUS_STOP = 5 // 下课

         val REQUEST_PERMISSIONS = 1
    }

    @SuppressLint("ResourceAsColor")
    override fun initViewModel() {
//        Glide.setup(GlideBuilder(this@MainActivity).setDecodeFormat(DecodeFormat.ALWAYS_ARGB_8888))
        viewModel = MainViewModel()
        binding.swipeLayout.isEnabled = false
        binding.swipeLayout.setColorSchemeColors(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
            android.R.color.holo_orange_light, android.R.color.holo_red_light)
        binding.viewModel = viewModel
        viewModel.setLifecycle(lifecycle)

        binding.swipeLayout.setOnRefreshListener { binding.webView.reload() }
        binding.webView.initialze()
        binding.webView.webChromeClient = WebChromeClient()
        client = WVWebViewClient(binding.webView)
        binding.webView.webViewClient = client
        var link : Uri = Uri.parse(config.BASE_SERVER_WEBUI_URL)
        binding.webView.loadUrl(link.toString())
        binding.btnReload.setOnClickListener{
            binding.llNetworkError.visibility = View.GONE
            binding.webView.loadUrl(config.BASE_SERVER_WEBUI_URL)
        }
//        binding.splashLayout.setOnClickListener {
//            Toast.makeText(this@MainActivity,"wo sjdfajsdklf",Toast.LENGTH_LONG).show()
//        }

    }











    //按键处理
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//       var entityVS = LiveRoomEntity()
//            entityVS.roomId = "edu_75ff4d18"
//            entityVS.nickName ="Arthur"
//            entityVS.keyValue = "204420"
//            var gson = Gson()
//            var dataStrig = gson.toJson(entityVS)
//                var entity = gson.fromJson<LiveRoomEntity>(dataStrig.toString(),LiveRoomEntity::class.java)
//                VHClass.getInstance().getClassInfo(entity.roomId,YXApplication.device, object : VHClass.ClassInfoCallback{
//                    override fun onSuccess(p0: ClassInfo.Webinar?) {
//                        var myWebbinar = p0  as MyWebbinar
////                    Log.d("TAG",myWebbinar.)
//                        var requestCalback = object  : VHClass.RequestCallback{
//                            override fun onSuccess() {
////                                currentFunc = VHClass.getInstance().classStatus
////                                Log.d("TAG",""+VHClass.getInstance().classStatus)
////                                if (currentFunc == MainActivity.FUC_LIVE && VHClass.getInstance().classStatus != Constant.CLASS_STATUS_LIVE) {
////                                    Toast.makeText(webView.context, "当前不是直播状态", Toast.LENGTH_SHORT).show()
////                                    return
////                                } else if (currentFunc == MainActivity.FUC_PLAYBACK && VHClass.getInstance().classStatus != Constant.CLASS_STATUS_BACK) {
////                                    Toast.makeText(webView.context, "当前不是回放状态", Toast.LENGTH_SHORT).show()
////                                    return
////                                }
//                                val intent = Intent(webView.context, FunctionActivity::class.java)
//                                intent.putExtra("fuc", VHClass.getInstance().classStatus)
//                                webView.context.startActivity(intent)
////
////
//                            }
//
//                            override fun onError(p0: Int, p1: String?) {
//                            }
//                        }
//                        VHClass.getInstance().joinClass("${entity.roomId}",YXApplication.device ,"${entity.nickName}","${entity.keyValue}", requestCalback)
//                    }
//
//                    override fun onError(p0: Int, p1: String?) {
//                    }
//                })
//
////
////            var entityVS = LiveRoomEntity()
////            entityVS.roomId = "edu_75ff4d18"
////            entityVS.nickName ="Arthur"
////            entityVS.keyValue = "862327"
////            var gson = Gson()
////            var data = gson.toJson(entityVS)
////            var entity = gson.fromJson<LiveRoomEntity>(data.toString(),LiveRoomEntity::class.java)
////            VHClass.getInstance().getClassInfo(entity.roomId,YXApplication.device, object : VHClass.ClassInfoCallback{
////                override fun onSuccess(p0: ClassInfo.Webinar?) {
////                    var myWebbinar = p0  as MyWebbinar
//////                    Log.d("TAG",myWebbinar.)
////                    var requestCalback = object  : VHClass.RequestCallback{
////                        override fun onSuccess() {
//////                                currentFunc = VHClass.getInstance().classStatus
//////                                Log.d("TAG",""+VHClass.getInstance().classStatus)
//////                                if (currentFunc == MainActivity.FUC_LIVE && VHClass.getInstance().classStatus != Constant.CLASS_STATUS_LIVE) {
//////                                    Toast.makeText(webView.context, "当前不是直播状态", Toast.LENGTH_SHORT).show()
//////                                    return
//////                                } else if (currentFunc == MainActivity.FUC_PLAYBACK && VHClass.getInstance().classStatus != Constant.CLASS_STATUS_BACK) {
//////                                    Toast.makeText(webView.context, "当前不是回放状态", Toast.LENGTH_SHORT).show()
//////                                    return
//////                                }
////                            val intent = Intent(webView.context, FunctionActivity::class.java)
////                            intent.putExtra("fuc", VHClass.getInstance().classStatus)
////                            webView.context.startActivity(intent)
//////
//////
////                        }
////
////                        override fun onError(p0: Int, p1: String?) {
////                        }
////                    }
////                    VHClass.getInstance().joinClass("${entity.roomId}",YXApplication.device ,"${entity.nickName}","${entity.keyValue}", requestCalback)
////                }
////
////                override fun onError(p0: Int, p1: String?) {
////                }
////            })
//////            binding.player.visibility = View.VISIBLE
//////            vhClassLive.start()
//            return true
//        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onBackPressed() {
        client.callHandler("checkIsRootPage")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data ==null || resultCode != RESULT_OK) return
        if (requestCode == client.ACTIVITYFOROMCLIENT){
            client.onActivityResult(data)
        }
    }

    /**
     * 谁请求的谁处理
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when(requestCode){
            client.CAMERA_REQUEST_CODE -> client.onRequestPermissionsResult(requestCode,permissions,grantResults)
            client.READ_PHONE -> client.onRequestPermissionsResult(requestCode,permissions,grantResults)
        }



    }



//    fun setTagAndAlias(){
//        JPushInterface.setAlias(this@MainActivity,10000,"qubuxing")
//    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (null != intent && intent.hasExtra("code")) {
            when (intent.action) {
//                "qwerty" -> client.resopnseCode(intent.extras!!.get("code")!!.toString() + "")
//                "payResult" -> WVWebViewClient.responsePayResult(intent.extras!!.get("payResult")!!.toString() + "")
            }
        }
    }

    override fun onPause() {
        super.onPause()
    }
    override fun onDestroy() {
        super.onDestroy()
    }



}

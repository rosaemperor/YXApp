package com.yixi.yxapp

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import com.vhall.classsdk.ClassCallback
import com.vhall.classsdk.Constant
import com.vhall.classsdk.VHClass
import com.vhall.classsdk.WatchRTC
import com.yixi.yxapp.MainActivity
import com.vhall.classsdk.service.ChatServer
import com.vhall.classsdk.service.IConnectService
import com.vhall.classsdk.service.MessageServer
import com.yixi.yxapp.fragments.*
import com.yixi.yxapp.widget.CheckInterDialog

class FunctionActivity : AppCompatActivity() {
    internal var function: Int = 0
    private var mFragmanager: FragmentManager? = null
    private var mLiveFrag: WatchLiveFragment? = null
    private var mBackFrag: WatchPlayBackFragment? = null
    private var mRTCFrag: WatchRtcFragment? = null
    private var chatFragment: ChatFragment? = null
    private var documentFragment: DocumentFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN or WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE or WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        //如果需要打包平板设备放开
        if (!isTableDevice()){
            requestedOrientation= ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        function = intent.getIntExtra("fuc", MainActivity.FUC_LIVE)
        mFragmanager = supportFragmentManager
        setContentView(R.layout.activity_function)
        showFunction(function)

        VHClass.getInstance().setClassCallback(MyClassCallback())
    }

    //根据功能展示对应Fragmeng
    fun showFunction(function: Int) {
        val classStatus = -1
        when (function) {
            MainActivity.FUC_LIVE -> {
                mLiveFrag = WatchLiveFragment.newInstance()
                mFragmanager!!.beginTransaction().replace(R.id.container, mLiveFrag).commit()
            }
            MainActivity.FUC_PLAYBACK -> {
                mBackFrag = WatchPlayBackFragment.newInstance()
                mFragmanager!!.beginTransaction().replace(R.id.container, mBackFrag).commit()
            }
            MainActivity.FUC_INTERACTIVE -> {
                mRTCFrag = WatchRtcFragment.newInstance()
                mFragmanager!!.beginTransaction().replace(R.id.container, mRTCFrag).commit()
            }
            MainActivity.FUC_CHAT -> {
                chatFragment = ChatFragment.newInstance()
                mFragmanager!!.beginTransaction().replace(R.id.container, chatFragment).commit()
            }
            MainActivity.FUC_DOC -> {
                documentFragment = DocumentFragment.newInstance()
                mFragmanager!!.beginTransaction().replace(R.id.container, documentFragment).commit()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        VHClass.getInstance().leaveClass()
    }

    private inner class MyClassCallback : ClassCallback {

        override fun onEvent(eventCode: Int, msg: String) {

        }

        override fun onMessageReceived(msgInfo: MessageServer.MsgInfo) {
            Log.e(TAG, "msgInfo.event = " + msgInfo.event)
            when (msgInfo.event) {
                IConnectService.EVENT_CLASS_PREPARE_MICS//预上麦
                -> if (msgInfo.classStatus == WatchRTC.VHCLASS_RTC_MIC_UP) {//
                    showMicDialog()
                } else {
                    if (mRTCFrag != null)
                        mFragmanager!!.beginTransaction().remove(mRTCFrag).commit()
                }
                IConnectService.EVENT_KICKOUT -> {
                    Toast.makeText(this@FunctionActivity, "您已被提出", Toast.LENGTH_SHORT).show()
                    VHClass.getInstance().leaveClass()
                    finish()
                    val intent = Intent(this@FunctionActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
                IConnectService.EVENT_OVER -> {
                    Toast.makeText(this@FunctionActivity, "已下课", Toast.LENGTH_SHORT).show()
                    if (VHClass.getInstance().classStatus == Constant.CLASS_STATUS_STOP) {
                        if (mLiveFrag != null) {
                            mFragmanager!!.beginTransaction().remove(mLiveFrag).commit()
                        }
                        if (mRTCFrag != null)
                            mFragmanager!!.beginTransaction().remove(mRTCFrag).commit()
                    }
                }
                IConnectService.EVENT_CLASS_SWITCH_MIC -> if (msgInfo.classStatus == WatchRTC.VHCLASS_RTC_MIC_UP) {//
                } else { // 下麦
                    mLiveFrag = WatchLiveFragment.newInstance()
                    mFragmanager!!.beginTransaction().replace(R.id.container, mLiveFrag).commit()
                }
                MessageServer.EVENT_CLEARBOARD, MessageServer.EVENT_DELETEBOARD, MessageServer.EVENT_INITBOARD, MessageServer.EVENT_PAINTBOARD -> if (documentFragment != null)
                    documentFragment!!.drawDocument(msgInfo)
                // PPT 消息
                MessageServer.EVENT_CHANGEDOC//PPT翻页消息
                    , MessageServer.EVENT_CLEARDOC, MessageServer.EVENT_PAINTDOC, MessageServer.EVENT_DELETEDOC -> if (documentFragment != null)
                    documentFragment!!.drawDocument(msgInfo)
                MessageServer.EVENT_CLASS_DOC_SWITCH // 文档切换   0显示文档 1显示白板
                -> if (documentFragment != null)
                    documentFragment!!.switchDocumentMode(msgInfo.classStatus)
                MessageServer.EVENT_CLASS_OPEN_HAND//公开课专属 , 是否举手
                -> if (mLiveFrag != null)
                    mLiveFrag!!.switchHand(msgInfo.classStatus)
                MessageServer.EVENT_CLASS_OPEN_SCREENSHARE -> if (VHClass.getInstance().classStatus == 1) {
                    if (mLiveFrag != null) {
                        mLiveFrag!!.openShareScreen()
                    }
                }
                MessageServer.CLASS_EVENT_START // 上课
                -> Toast.makeText(this@FunctionActivity, "上课了", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onChatReceived(chatInfo: ChatServer.ChatInfo) {
            when (chatInfo.event) {
                IConnectService.eventMsgKey -> if (chatFragment != null)
                    chatFragment!!.updateData(chatInfo)
            }
        }

        override fun onError(eventCode: Int, msg: String) {
            Toast.makeText(this@FunctionActivity, "" + msg, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 互动显示邀请上麦弹窗
     */
    fun showMicDialog() {
        val dialog = CheckInterDialog(this)
        dialog.setClickCheckListener(object : CheckInterDialog.ClickCheckListener {
            override fun onAllow() {
                mRTCFrag = WatchRtcFragment.newInstance()
                mFragmanager!!.beginTransaction().replace(R.id.container, mRTCFrag).commit()
                dialog.dismiss()
            }

            override fun onRefuse() {
                //TODO 需要发送CMD消息通知PC端
                if (mLiveFrag != null) {
                    mLiveFrag!!.sendRefuseCmd()
                }
                dialog.dismiss()
            }
        })
        dialog.show()
    }

    companion object {
        private val TAG = "FunctionActivity"
    }
    fun isTableDevice(): Boolean{
        return (resources.configuration.screenLayout and  Configuration.SCREENLAYOUT_SIZE_MASK) >=
                Configuration.SCREENLAYOUT_SIZE_LARGE
    }

}

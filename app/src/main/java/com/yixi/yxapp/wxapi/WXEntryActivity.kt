package com.yixi.yxapp.wxapi

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log

import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.yixi.yxapp.MainActivity
import com.yixi.yxapp.app.YXApplication

class WXEntryActivity : Activity(),IWXAPIEventHandler{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        YXApplication.instance.getWXAPI().handleIntent(intent,this)
    }

    override fun onPointerCaptureChanged(hasCapture: Boolean) {

    }

    override fun onReq(baseReq: BaseReq) {

    }

    override fun onResp(baseResp: BaseResp) {
//        val resp = baseResp as SendAuth.Resp
        if (baseResp.errCode == 0 ){
            val intent = Intent("qwerty")
            intent.setClass(this@WXEntryActivity, MainActivity::class.java!!)
            val bundle = Bundle()
            if(baseResp.type ==1){
                bundle.putString("code", "" + (baseResp as SendAuth.Resp).code)
            }
            Log.d("TAGSS",""+baseResp.type)
            intent.putExtras(bundle)
            startActivity(intent)
            finish()
        }else{
            finish()
        }



    }

    override fun onPause() {
        super.onPause()
    }
}
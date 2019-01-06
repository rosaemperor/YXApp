package com.yixi.yxapp.commons

import com.yixi.yxapp.BuildConfig

object config {

    private var HOST_ADDRESS_PROD :String = "https://m.qubuxing.com/api/"
    private var HOST_ADDRESS_DEV : String ="https://qubuxing.lanlingdai.net/api/"


    private var WEB_UI_URL_PROD : String = "https://m.qubuxing.com"
    private var WEB_UI_URL_DEV : String = "https://yixi.zht87.com/?identity=10"


    val WXAPP_ID ="wx2d2573d29537aede"
    val VIDEO_APPKey = "3ed51bd50236e77df04faabdaf514f3f"
    val VIDEO_SecretKey ="589acb614416232a2f68e189e853a88a"


    //H5前端页面地址
    var BASE_SERVER_WEBUI_URL :String = if (BuildConfig.DEBUG) WEB_UI_URL_DEV else WEB_UI_URL_PROD
    //后台接口地址
    var HOST_ADDRESS :String = if (BuildConfig.DEBUG) HOST_ADDRESS_DEV else HOST_ADDRESS_PROD
}
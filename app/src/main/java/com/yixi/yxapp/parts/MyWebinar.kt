package com.yixi.yxapp.parts

import com.vhall.classsdk.ClassInfo

class MyWebinar : ClassInfo.Webinar(){


    fun getdispatch() : String{
        return dispatch
    }
    fun getpublish_url() : String{
        return publish_url
    }
    fun getreport_url() : String{
        return report_url
    }
}
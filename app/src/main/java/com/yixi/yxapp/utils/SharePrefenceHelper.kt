package com.qubuxing.qbx.utils

import android.content.Context
import android.content.SharedPreferences
import com.yixi.yxapp.app.YXApplication


class SharePrefenceHelper {
    companion object {
        var mContext : Context ?= null
        var preferences :SharedPreferences ?=null
        fun initSharePreference(context: Context){
            mContext = context
        }
        fun save (key : String , value : String){
            if (mContext == null) mContext=YXApplication.instance.applicationContext
            if (preferences == null)preferences= mContext!!.getSharedPreferences(mContext!!.packageName, Context.MODE_PRIVATE)

            var editor :SharedPreferences.Editor  = preferences!!.edit()
            editor.putString(key,value)
            editor.commit()
        }

        fun get(key : String) : String{
            if (mContext == null) mContext=YXApplication.instance.applicationContext
            if (preferences == null) mContext!!.getSharedPreferences(mContext!!.packageName, Context.MODE_PRIVATE)
            try {
                return preferences!!.getString(key,"")
            }catch (kot: KotlinNullPointerException){
                return ""
            }
        }
    }


}
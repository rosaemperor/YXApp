package com.yixi.yxapp.viewModels

import android.content.Context
import android.content.pm.PackageManager
import android.databinding.ObservableField
import android.view.View
import com.qubuxing.qbx.utils.DialogUtils
import com.yixi.yxapp.BaseViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel : BaseViewModel(){
    var webViewVisiable : ObservableField<Int> = ObservableField()
    var errorViewVisiable : ObservableField<Int> = ObservableField()
    override fun initViewModel() {
        webViewVisiable.set(View.VISIBLE)
        errorViewVisiable.set(View.GONE)
    }

    override fun initData() {
    }


}
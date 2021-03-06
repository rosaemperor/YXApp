package com.qubuxing.qbx.utils

import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.SyncStateContract.Helpers.update
import android.support.constraint.ConstraintLayout
import android.support.v4.content.FileProvider
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


class DialogUtils {

    companion object {
        lateinit var mContext : Context
        var file : File? =null
        var m_appNameStr = "temp_qubuxing.apk"
//        fun showUpdateDia( context: Context , updateResult : UpdateResultEntity) {
//            mContext = context
//            var rate : Long = 0
//            val updateDialog = Dialog(context,R.style.update_dialog)
//            val layout = LayoutInflater.from(context).inflate(R.layout.dia_update_layout, null) as ConstraintLayout
//            var description = layout.findViewById<TextView>(R.id.description)
//            var updateView = layout.findViewById<TextView>(R.id.update_view)
//            var progressBar = layout.findViewById<ProgressBar>(R.id.progress_bar)
//            var downLayout = layout.findViewById<LinearLayout>(R.id.down_layout)
//            var pers = layout.findViewById<TextView>(R.id.pers)
//            var startNow = layout.findViewById<TextView>(R.id.start)
//            description.text = updateResult.description
//            updateView.setOnClickListener {
//                updateView.visibility = View.GONE
//                downLayout.visibility = View.VISIBLE
//                var thread = Thread(Runnable {
//                    var downHelper = DownHelper()
//                    downHelper.helpDown(updateResult.releasePackageUrl, mContext as Activity , object : DownCallback{
//                        override fun onProgress(currentLength: String?) {
//                            progressBar.progress = currentLength!!.toInt()
//                                    pers.text = "$currentLength%"
//       }
//
//                        override fun onFinish(file: File?) {
//                            startNow.visibility = View.VISIBLE
//                            startNow.setOnClickListener {
//                                junmUpdate(file!!)
//                            }
//                            junmUpdate(file!!)
//                        }
//
//                        override fun onFailure() {
//
//                        }
//                    })
//                })
//                thread.start()
////                Toast.makeText(updateView.context,"更新更新",Toast.LENGTH_LONG).show()
//
//
//
//            }
////
//            updateDialog.setContentView(layout)
//            updateDialog.setCancelable(false)
//            if ( !updateDialog.isShowing) {
//                updateDialog.show()
//            }
//
//
//        }
        private fun junmUpdate() {
            val intent = Intent(Intent.ACTION_VIEW)
            var fileUri : Uri
            if (Build.VERSION.SDK_INT >= 24) {
                fileUri = FileProvider.getUriForFile(mContext, "com.qubuxing.qbx.fileprovider", file!!)
            } else {
                fileUri = Uri.fromFile(file)
            }
            intent.setDataAndType(fileUri,
                    "application/vnd.android.package-archive")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            (mContext as Activity).startActivity(intent)
        }
        private fun junmUpdate(downFile : File) {
            val intent = Intent(Intent.ACTION_VIEW)
            var fileUri : Uri
            if (Build.VERSION.SDK_INT >= 24) {
                fileUri = FileProvider.getUriForFile(mContext, "com.qubuxing.qbx.fileprovider", downFile!!)
                intent.data = fileUri
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION;
            } else {
                fileUri = Uri.fromFile(downFile)
                intent.setDataAndType(fileUri,
                        "application/vnd.android.package-archive")
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            Log.d("Uri","${fileUri}")

            (mContext as Activity).startActivity(intent)
        }

    }


}
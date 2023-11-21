package com.jhteck.icebox.face.model

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewModelScope
import com.hele.mrd.app.lib.base.BaseViewModel
import com.hele.mrd.app.lib.base.livedata.SingleLiveEvent
import com.jhteck.cameratest.FileUtils
import com.jhteck.icebox.apiserver.ILoginApiService
import com.jhteck.icebox.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService

/**
 *@Description:(登录 模块viewmodel)
 *@author wade
 *@date 2023/6/28 17:27
 */
class CameraViewModel(application: android.app.Application) :
    BaseViewModel<ILoginApiService>(application) {
    private val TAG = "CameraViewModel"
    private val userDao = DbUtil.getDb().accountDao();

    fun takePicture(context: Context, imageCamera: ImageCapture?) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                takePhoto(context,imageCamera)
            } catch (e: Exception) {
                toast(e.message)
            } finally {

            }
        }
    }
    //拍照
    private fun takePhoto(context: Context, imageCamera: ImageCapture?) {
        val imageCapture = imageCamera ?: return
        val mDateFormat = SimpleDateFormat("yyyyMMddHHmmss", Locale.US)
        val file =
            File(FileUtils.getImageFileName(), mDateFormat.format(Date()).toString() + ".png")

        val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()

        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(file)
                    val msg = "Photo capture succeeded: $savedUri"
//                  Log.d(TAG, msg)
                }
            })
    }

    val networkStatus by lazy {
        SingleLiveEvent<Boolean>()
    }

}
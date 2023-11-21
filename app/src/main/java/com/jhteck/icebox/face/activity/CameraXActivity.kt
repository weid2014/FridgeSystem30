package com.jhteck.icebox.face.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.jhteck.icebox.databinding.AppActivityCameraBinding
import com.jhteck.icebox.face.model.CameraViewModel
import com.hele.mrd.app.lib.base.BaseActivity
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraXActivity : BaseActivity<CameraViewModel, AppActivityCameraBinding>() {

    private val TAG = "CameraXActivity"
    private var imageCamera: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA//当前相机
    var preview: Preview? = null//预览对象
    var cameraProvider: ProcessCameraProvider? = null//相机信息
    var camera: Camera? = null//相机对象

    override fun createViewBinding(): AppActivityCameraBinding {
        return AppActivityCameraBinding.inflate(layoutInflater)
    }

    override fun createViewModel(): CameraViewModel {
        return viewModels<CameraViewModel>().value
    }

    override fun initView() {
        initPermission()
        binding.btnTakePicture.setOnClickListener {
            viewModel.takePicture(this@CameraXActivity,imageCamera)
        }
    }

    override fun tryLoadData() {

    }

    private fun initPermission() {
        if (allPermissionsGranted()) {
            // ImageCapture
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
    }

    /**
     * 开始拍照
     */
    @SuppressLint("RestrictedApi")
    private fun startCamera() {
        cameraExecutor = Executors.newSingleThreadExecutor()
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {
            cameraProvider = cameraProviderFuture.get()//获取相机信息

            //预览配置
            preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder?.surfaceProvider)
                }

            imageCamera = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .setFlashMode(ImageCapture.FLASH_MODE_AUTO)
                .build()
// previewView is a PreviewView instance
            try {
                cameraProvider?.unbindAll()//先解绑所有用例
                camera = cameraProvider?.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCamera
                )//绑定用例
                Log.e(TAG, "绑定用例")
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(
                    this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val TAG = "CameraXBasic"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
        )
    }
}
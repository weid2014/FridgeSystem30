package com.jhteck.icebox.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.hele.mrd.app.lib.base.BaseActivity
import com.jhteck.cameratest.FileUtils
import com.jhteck.icebox.adapter.LoginPageShowItemAdapter
import com.jhteck.icebox.api.*
import com.jhteck.icebox.bean.InventoryDao
import com.jhteck.icebox.databinding.AppActivityLoginOldBinding
import com.jhteck.icebox.face.activity.ActivationActivity
import com.jhteck.icebox.face.activity.RegisterAndRecognizeActivity
import com.jhteck.icebox.service.MyService
import com.jhteck.icebox.utils.SharedPreferencesUtils
import com.jhteck.icebox.utils.ToastUtils
import com.jhteck.icebox.viewmodel.LoginViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.stream.Collectors


/**
 *@Description:(最初的登录界面，只有常规区和暂存区)
 *@author wade
 *@date 2023/6/28 17:21
 */
class LoginOldActivity : BaseActivity<LoginViewModel, AppActivityLoginOldBinding>() {

    private var service: MyService? = null
    private var isBind = false

    private val TAG = "LoginOldActivity"
    private var imageCamera: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA//当前相机
    var preview: Preview? = null//预览对象
    var cameraProvider: ProcessCameraProvider? = null//相机信息
    var camera: Camera? = null//相机对象
    private var conn = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            isBind = true
            val myBinder = p1 as MyService.MyBinder
            service = myBinder.service
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            isBind = false
        }
    }

    override fun createViewBinding(): AppActivityLoginOldBinding {
        //隐藏底部的虚拟键并全屏显示
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_FULLSCREEN
        window.decorView.setOnSystemUiVisibilityChangeListener(
            View.OnSystemUiVisibilityChangeListener() {
                fun onSystemUiVisibilityChange(visibility: Int) {
                    window.decorView.systemUiVisibility =
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_FULLSCREEN
                }
            })
        return AppActivityLoginOldBinding.inflate(layoutInflater)
    }

    override fun createViewModel(): LoginViewModel {
        return viewModels<LoginViewModel>().value
    }


    override fun initView() {
        //开启service，初始化TCP服务
//        CrashReport.testJavaCrash();
        startService()
        //删除30天前的相片
//        FileUtils.deleteImageByDate(-30)
        binding.btnLogin.setOnClickListener {
            //登录按键点击事件
            takePhoto()
            viewModel.login(binding.edUserName.text.toString(), binding.edPassword.text.toString())
        }
        viewModel.initHFCradList()


        //点击切换到账号登录
        binding.llLoginByAccount.setOnClickListener {
            changLoginType(false)
        }
        binding.llLoginByCard.setOnClickListener {
            changLoginType(true)
        }
        changLoginType(true)


        initRecyclerView()

        if (DEBUG) {
            viewModel.mockDataToLocal()
        }
        viewModel.loadRfidsFromLocal();
        //本地离线数据
        viewModel.loadOfflineRfidsFromLocal();
        //测试入口

        binding.imTestLogin.setOnClickListener {
            if (DEBUG) {
//                takePhoto()
//                viewModel.login("admin", "Jinghe233")
//                service?.sendRfid()
                startActivity(Intent(this, ActivationActivity::class.java))
            }
        }
        initPermission()
//        doRegisterReceiver();
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

    fun initRecyclerView() {
        //区分暂存区域展示
        clearList()
        binding.rvNormalContent?.layoutManager =
            GridLayoutManager(this, 7)
        binding.rvNormalContent?.adapter = LoginPageShowItemAdapter(tempList)

        binding.rvPauseContent1?.layoutManager =
            GridLayoutManager(this, 7)
        binding.rvPauseContent1?.adapter = LoginPageShowItemAdapter(tempListPause)
    }

    override fun initObservables() {
        super.initObservables()
        viewModel.loginUserInfo.observe(this) {
            SharedPreferencesUtils.setPrefInt(this, ROLE_ID, it.role_id.toInt())
            toMainPage()
        }
        viewModel.loginStatus.observe(this) {
            //如果账户验证成功，跳转到主界面
            if (it)
                toMainPage()
        }
        loadRridsData()
    }

    var tempListPause = mutableListOf<InventoryDao>()//未编辑位置
    var tempList = mutableListOf<InventoryDao>()
    var tempListNormal = mutableListOf<AvailRfid>()
    private fun clearList() {
        tempList.clear()
        tempListNormal.clear()
        tempListPause.clear()
    }

    private fun loadRridsData() {
        viewModel.rfidDatas.observe(this) {
            clearList()
            for (i in it.avail_rfids) {
                if (i.remain == 100) {
                    tempListNormal.add(i)
                }
            }
            var map = it.avail_rfids.stream()
                .collect(Collectors.groupingBy { t -> t.material.eas_material_name + t.material.eas_unit_id + t.remain })//根据批号分组
            for (i in map.values) {
                val inventoryDao = InventoryDao(
                    "${i[0].material_batch.eas_lot}",
                    "${i[0].material?.eas_material_name}",
                    i.size,
                    i[0].cell_number
                )
                if (i[0].remain == 100) {
                    tempList.add(inventoryDao)
                } else {
                    tempListPause.add(inventoryDao)
                }
            }

            binding.rvNormalContent?.adapter?.notifyDataSetChanged()
            binding.tvNormalNum.text = "共${tempListNormal.size}个"

            binding.rvPauseContent1?.adapter?.notifyDataSetChanged()
            binding.tvPauseNum1?.text = "共${tempListPause.size}个"
        }
        viewModel.rfidOfflineDatas.observe(this) {
            binding.tvOfflineNum.text = "离线存储:共${it?.size}个"
        }
    }

    //拍照
    private fun takePhoto() {

        val imageCapture = imageCamera ?: return
        val mDateFormat = SimpleDateFormat("yyyyMMddHHmmss", Locale.US)
        val file =
            File(FileUtils.getImageFileName(), mDateFormat.format(Date()).toString() + ".png")

        val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()

        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                    ToastUtils.shortToast(" 拍照失败 ${exc.message}")
//                    Toasty.info(this@LoginActivity, " 拍照失败 ${exc.message}", Toast.LENGTH_SHORT, true).show()
//                    toMainPage()
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(file)
                    val msg = "Photo capture succeeded: $savedUri"
                    ToastUtils.shortToast(" 拍照成功 $savedUri")
//                    Toasty.info(this@LoginActivity, " 拍照成功 $savedUri", Toast.LENGTH_SHORT, true).show()
                    Log.d(TAG, msg)
//                    toMainPage()
                }
            })
    }

    private fun toMainPage() {
        //跳转到主页面
        binding.edPassword.setText("")
        val intent = Intent(this@LoginOldActivity, MainActivity::class.java)
        intent.putExtra("loginUserInfo", Gson().toJson(viewModel.loginUserInfo.value))
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent)
        finish()
    }

    override fun onStart() {
        super.onStart()
        doRegisterReceiver()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService()
//        cameraExecutor.shutdown()
    }

    override fun onRestart() {
        super.onRestart()
        viewModel.loadRfidsFromLocal();
        viewModel.loadOfflineRfidsFromLocal();
        //从主页面返回
    }

    private fun startService() {
        val intent = Intent(this, MyService::class.java)
        intent.putExtra("from", "LoginActivity")
        bindService(intent, conn, Context.BIND_AUTO_CREATE)
    }

    private fun stopService() {
        unbindService(conn);
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }

    private fun changLoginType(isLoginByCard: Boolean) {
        if (isLoginByCard) {
            binding.llHFCard.visibility = View.VISIBLE
            binding.llAccount?.visibility = View.GONE
        } else {
            binding.llHFCard.visibility = View.GONE
            binding.llAccount?.visibility = View.VISIBLE
        }
    }

    /**
     * 注册广播接收者
     */
    private var mReceiver: ContentReceiver? = null
    private fun doRegisterReceiver() {
        mReceiver = ContentReceiver()
        val filter = IntentFilter(
            BROADCAST_INTENT_FILTER
        )
        registerReceiver(mReceiver, filter)
    }

    inner class ContentReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val key = intent.getStringExtra(TCP_MSG_KEY)
            val value = intent.getStringExtra(TCP_MSG_VALUE)
            Log.d("BroadcastReceiver", "Login  key--->${key},value--${value}")

            when (key) {
                EXIT_APP_MSG -> {
                    finish()
                }
                HFCard -> {
                    takePhoto()
                    value?.let { viewModel.loginByCark(it) }
                }
            }
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
//                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
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
package com.jhteck.icebox.activity

import android.content.*
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.hele.mrd.app.lib.base.BaseActivity
import com.hele.mrd.app.lib.common.ext.toast
import com.jhteck.icebox.Lockmodel.LockManage
import com.jhteck.icebox.R
import com.jhteck.icebox.adapter.AntListAdapterSpash
import com.jhteck.icebox.adapter.IAntPowerCallback
import com.jhteck.icebox.api.*
import com.jhteck.icebox.databinding.AppActivitySpashBinding
import com.jhteck.icebox.myinterface.MyCallback
import com.jhteck.icebox.rfidmodel.RfidManage
import com.jhteck.icebox.service.MyService
import com.jhteck.icebox.utils.SharedPreferencesUtils
import com.jhteck.icebox.viewmodel.SpashViewModel
import com.naz.serial.port.SerialPortFinder
import org.json.JSONArray
import org.json.JSONObject


/**
 *@Description:(启动页面)
 *@author wade
 *@date 2023/7/3 16:25
 */
class SpashActivity : BaseActivity<SpashViewModel, AppActivitySpashBinding>() {
    private var isLink = false
    private var isLinkLock = false
    private var service: MyService? = null
    private var isBind = false
    private var tempList = mutableListOf<AntPowerDao>()
    private var mDevicesPath: Array<String>? = null
    private var isGetOldInfo: Boolean = false
    private var isSyncOtherSystem: Boolean = false
    private var  oldSncoede:String?=null

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

    override fun createViewBinding(): AppActivitySpashBinding {
        return AppActivitySpashBinding.inflate(layoutInflater)
    }

    override fun initView() {
        binding.llFridgesOperate.visibility = View.GONE
        startService()
        initSpinnerView()
        // 检查是否是第一次运行应用程序
        var isFirstRun =
            SharedPreferencesUtils.getPrefBoolean(this@SpashActivity, IS_FIRST_RUN, true)
        if (DEBUG) {
//            viewModel.registAdmin("Jinghe233")
            isFirstRun = true
        }
        if (isFirstRun) {
            binding.llFridgesOperate.visibility = View.VISIBLE
            binding.rlSpash.visibility = View.GONE
            var steps = mutableListOf<String>()
            steps.add("Step 1")
            steps.add("Step 2")
            binding.stepView.setSteps(steps)
            binding.stepView.setOnStepClickListener {
                binding.stepView.go(it, true)
                changeUI(it)
            }
            binding.btnStep1Next.setOnClickListener {
                oldSncoede = binding.edOldSncode.text.toString().trim()
                val sncode = binding.edSncode.text.toString().trim()
                if (!chechSnCode(oldSncoede!!)||!chechSnCode(sncode)) {
                    toast("请输入正确的SN码")
                    return@setOnClickListener
                }
                SharedPreferencesUtils.setPrefString(this@SpashActivity, SNCODE, sncode)
                changeUI(1)
                binding.stepView.go(1, true)
            }
            binding.btnStep2Back.setOnClickListener {
                binding.stepView.go(0, true)
                changeUI(0)
            }

            binding.btnStep2Finish.setOnClickListener {
                activyFinish()
            }

            binding.btnOpenLock.setOnClickListener {
                viewModel.openLock()
            }
            binding.btnCloseLock.setOnClickListener {
                viewModel.closeLock()
            }
            binding.btnLinkLock.setOnClickListener {
                val devicePath = mDevicesPath!![binding.spSerialNumberLock.selectedItemPosition]
                isLinkLock = !isLinkLock
                if (isLinkLock) {
                    LockManage.getInstance().initSerialByPort(devicePath)
                    SharedPreferencesUtils.setPrefString(this, SERIAL_PORT_LOCK, devicePath)
                    binding.btnLinkLock.text = "关闭"
                } else {
                    binding.btnLinkLock.text = "连接"
                    LockManage.getInstance().close()
                }
            }
            binding.btnLink.setOnClickListener {
                val devicePath = mDevicesPath!![binding.spSerialNumber.getSelectedItemPosition()]
                isLink = !isLink
                if (isLink) {
                    RfidManage.getInstance().linkDevice(isLink, devicePath)
                    SharedPreferencesUtils.setPrefString(this, SERIAL_PORT_RFID, devicePath)
                    binding.btnLink.text = "关闭"
                } else {
                    binding.btnLink.text = "连接"
                    RfidManage.getInstance().linkDevice(isLink, devicePath)
                }
            }
            binding.btnGetAntPower.setOnClickListener {
                if (DEBUG) {
                    initTempList()
                } else {
                    viewModel.getAntPower()
                }
            }

            binding.btnGetVersionLock.setOnClickListener {
                LockManage.getInstance().getVersion()
            }
            LockManage.getInstance().setVersionCallback(object : MyCallback<String> {
                override fun callback(result: String) {
                    runOnUiThread {
                        toast("版本号:${result}")
                    }
                }
            })
            binding.btnGetVersion.setOnClickListener {
                RfidManage.getInstance().getVersion()
            }

            RfidManage.getInstance().setVersionCallback(object : MyCallback<String> {
                override fun callback(result: String) {
                    runOnUiThread {
                        toast(result)
                    }
                }
            })
            changeUI(0)
        } else {
            // 不是第一次运行应用程序的操作
            binding.rlSpash.visibility = View.VISIBLE
            /*Glide.with(this)
                .load("file:///android_asset/start.gif")
                .into(binding.ivGif)*/
            viewModel.spash()
        }
        doRegisterReceiver()
    }

    private fun activyFinish() {
        viewModel.setAntPower(tempList)
    }

    fun chechSnCode(sncodeStr:String): Boolean {
        return sncodeStr.isNotEmpty()&&sncodeStr.length==16&&sncodeStr.startsWith("FEDCBA")
    }


    private fun initSpinnerView() {
        SharedPreferencesUtils.setPrefString(this@SpashActivity, URL_REQUEST, URL_TEST)
        val selectUrlList = listOf(
            URL_TEST, URL_KM
        )
        binding.spSelectUrl.adapter =
            ArrayAdapter(
                this,
                R.layout.app_item_text,
                R.id.tv_content,
                selectUrlList
            )
        binding.spSelectUrl.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    SharedPreferencesUtils.setPrefString(
                        this@SpashActivity,
                        URL_REQUEST,
                        selectUrlList[position]
                    )
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }

        val selectList = listOf(
            "不拉取", "拉取"
        )
        binding.spGetOldInfo.adapter =
            ArrayAdapter(
                this,
                R.layout.app_item_text,
                R.id.tv_content,
                selectList
            )
//        binding.spGetOldInfo.setSelection(item.power.toInt())
        binding.spGetOldInfo.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    isGetOldInfo = when (position) {
                        0 -> false
                        1 -> true
                        else -> false
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }

        val selectListSync = listOf(
            "不同步", "同步"
        )
        binding.spSyncOtherSystem.adapter =
            ArrayAdapter(
                this,
                R.layout.app_item_text,
                R.id.tv_content,
                selectListSync
            )
//        binding.spGetOldInfo.setSelection(item.power.toInt())
        binding.spSyncOtherSystem.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (position != 0) {
                        binding.edOldSncode.visibility = View.VISIBLE
                    } else {
                        binding.edOldSncode.visibility = View.GONE
                    }
                    isSyncOtherSystem = when (position) {
                        0 -> false
                        1 -> true
                        else -> false
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
    }

    private fun registIceBox() {
        val sncode = binding.edSncode.text.toString().trim()
        val deviceAlias = binding.edDeviceAlias.text.toString().trim()
        val adminName = "admin"
        val adminPassword = binding.edAdminPassword.text.toString().trim()
        val location = binding.edLocation.text.toString().trim()
        val style = 0//类型（ 0 - 普通冰箱； 1 - 常温冰箱），默认为 0，不传则不更新
        val cells = 17
        val fridgesActiveBo = FridgesActiveBo(
            adminName,
            adminPassword,
            cells,
            deviceAlias,
            location,
            sncode,
            style,
            1000
        )
//        viewModel.activeFridges(fridgesActiveBo)
        if (isGetOldInfo) {
            //同步其他冰箱信息，操作记录，库存记录等
            viewModel.syncOtherSystem(sncode)
        }
        if (isSyncOtherSystem) {
            //同步旧账号信息
            oldSncoede?.let { viewModel.synchronizedAccount(it) }
        }
        viewModel.activeFridges(fridgesActiveBo)
    }

    override fun tryLoadData() {

    }

    private fun changeUI(index: Int) {
        when (index) {
            0 -> {
                binding.llStep1.visibility = View.VISIBLE
                binding.llStep2.visibility = View.GONE
            }
            1 -> {
                binding.llStep1.visibility = View.GONE
                binding.llStep2.visibility = View.VISIBLE
                initTempList()
                if (DEBUG) {
                    initTempList()
                } else {
                    initSerialPort()
                    viewModel.getAntPower()
                }
            }
        }
    }

    private fun initTempList() {
        tempList.clear()
        for (i in 0 until 8) {
            tempList.add(AntPowerDao("0" + (i + 1), "30"))
        }
        initAntRecycleView(tempList)
    }

    override fun createViewModel(): SpashViewModel {
        return viewModels<SpashViewModel>().value
    }


    override fun initObservables() {
        super.initObservables()
        viewModel.loginStatus.observe(this) {
            startActivity(Intent(this, LoginOldActivity::class.java))
            finish()
            // 将标志位设置为 false，表示应用程序已经被安装过
            SharedPreferencesUtils.setPrefBoolean(this@SpashActivity, IS_FIRST_RUN, false)
        }
        viewModel.setAntPowerStatus.observe(this) {
            registIceBox()
        }
        viewModel.activeIceBoxStatus.observe(this) {
            val password = binding.edAdminPassword.text.toString()
            if (password.isNotEmpty()) {
                viewModel.registAdmin(password)
            } else {
                toast("密码不能为空!")
            }
        }

    }

    private fun startService() {
        //测试模式就不启动服务了
        if (!DEBUG) {
            val intent = Intent(this, MyService::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.putExtra("from", "LoginActivity")
            bindService(intent, conn, Context.BIND_AUTO_CREATE)
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
            Log.d("BroadcastReceiver", "accountfrag  key--->${key},value--${value}")
            if (key.equals(REPORT_ANT_POWER)) {
                tempList.clear()
                val jsonObject = JSONObject(value)
                //map获取list数据的object类
                val o: Any = jsonObject.get("power")
                //object转String 再转list
                val jsonArray = JSONArray(o.toString())
                for (i in 0 until jsonArray.length()) {
                    val `object` = jsonArray.getJSONObject(i)
                    val antid = `object`["antid"].toString()
                    val po = `object`["power"].toString()
                    tempList.add(AntPowerDao("$antid", po))
                }
                initAntRecycleView(tempList)
            } else if (key.equals(HFCard)) {
                runOnUiThread {
                    binding.tvNfcId.text = value
                    toast("测试卡号为：${value}")
                }
            } else if (key.equals(REPORT_ANT_POWER_30)) {
                tempList.clear()
                val jsonArray = JSONArray(value.toString())
                for (i in 0 until jsonArray.length()) {
                    val antid = i.toString()
                    val po = jsonArray[i].toString()
                    tempList.add(AntPowerDao("$antid", po))
                }
                initAntRecycleView(tempList)
            }
        }
    }

    private fun initAntRecycleView(antList: List<AntPowerDao>) {
        binding.llStep2.visibility = View.VISIBLE
        val layoutManager = GridLayoutManager(this, 2)
        binding.rvAnt.layoutManager = layoutManager
        binding.rvAnt.adapter =
            AntListAdapterSpash(
                this@SpashActivity,
                antList,
                object : IAntPowerCallback<AntPowerDao> {
                    override fun antPowerCallback(powerDao: AntPowerDao, position: Int) {
                        tempList[position].power = powerDao.power
                    }
                })
        /*binding.btnCancleAnt.setOnClickListener {
            binding.llAntPower.visibility = View.GONE
        }
        binding.btnSaveAnt.setOnClickListener {
            viewModel.setAntPower(tempList)
        }*/
    }

    private fun initSerialPort() {
        val portFinder = SerialPortFinder()
        val devices: Array<String> = portFinder.allDevices
        mDevicesPath = portFinder.allDevicesPath
        binding.spSerialNumber.adapter =
            ArrayAdapter(this, R.layout.app_item_text, R.id.tv_content, devices)
        binding.spSerialNumberLock.adapter =
            ArrayAdapter(this, R.layout.app_item_text, R.id.tv_content, devices)
        val normalDevice = "ttyS2"
        val normalDeviceLock = "ttyS8"
        for (i in devices.indices) {
            if (devices[i].startsWith(normalDevice)) {
                binding.spSerialNumber.setSelection(i)
                break
            }
        }
        for (i in devices.indices) {
            if (devices[i].startsWith(normalDeviceLock)) {
                binding.spSerialNumberLock.setSelection(i)
                break
            }
        }
    }

    private fun stopService() {
//        unbindService(conn);
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }

    override fun onDestroy() {
        stopService()
        super.onDestroy()
    }
}
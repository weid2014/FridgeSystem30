package com.jhteck.icebox.activity

import android.content.*
import android.graphics.Color
import android.os.IBinder
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.hele.mrd.app.lib.base.BaseActivity
import com.hele.mrd.app.lib.common.ext.toast
import com.jhteck.icebox.Lockmodel.LockManage
import com.jhteck.icebox.R
import com.jhteck.icebox.adapter.AntListAdapterSpash
import com.jhteck.icebox.adapter.IAntPowerCallback
import com.jhteck.icebox.api.*
import com.jhteck.icebox.databinding.AppActivitySpashBinding
import com.jhteck.icebox.myinterface.MyCallback
import com.jhteck.icebox.repository.entity.FridgesInfoEntity
import com.jhteck.icebox.rfidmodel.RfidManage
import com.jhteck.icebox.service.MyService
import com.jhteck.icebox.utils.MD5Util
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
        startService()
        binding.llFridgesOperate.visibility = View.GONE
        initIncludeTitle()
        initSpinnerView()
        // 检查是否是第一次运行应用程序
        var isFirstRun =
            SharedPreferencesUtils.getPrefBoolean(this@SpashActivity, IS_FIRST_RUN, true)
        /*if (DEBUG) {
            viewModel.registAdmin("Jinghe233")
            isFirstRun = true
        }*/
        if (isFirstRun) {
            binding.llFridgesOperate.visibility = View.VISIBLE
            binding.rlSpash.visibility = View.GONE
            var steps = mutableListOf<String>()
            steps.add("Step 1")
            steps.add("Step 2")
            steps.add("Step 3")
            binding.stepView.setSteps(steps)
            binding.stepView.setOnStepClickListener {
                binding.stepView.go(it, true)
                changeUI(it)
            }
            binding.edToken.addTextChangedListener(object :TextWatcher{
                override fun beforeTextChanged(charSequence: CharSequence, start: Int, count: Int, after: Int) {

                }

                override fun onTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {
                    val inputLength=charSequence.length
                    if(inputLength!=24){
                        binding.includeTitleToken.tvTitle.text="激活令牌(当前长度${inputLength})"
                        binding.includeTitleToken.tvTitle.setTextColor(Color.parseColor("#FF3030"))
                    }else{
                        binding.includeTitleToken.tvTitle.text="激活令牌"
                        binding.includeTitleToken.tvTitle.setTextColor(Color.parseColor("#000000"))
                    }
                }

                override fun afterTextChanged(p0: Editable?) {

                }

            })
            binding.btnStep1Next.setOnClickListener {
                val sncode = binding.edSncode.text.toString().trim()
                val token = binding.edToken.text.toString().trim()
                if (!checkSnCode(sncode)) {
                    toast("请输入正确的SN码")
                    return@setOnClickListener
                }
                viewModel.getFridgeInfo(token, sncode)

            }
            binding.llStep3.btnStep2Back.setOnClickListener {
                binding.stepView.go(0, true)
                changeUI(0)
            }
            binding.btnStep2Next.setOnClickListener {

                registIceBox()
            }

            binding.llStep3.btnFinish.setOnClickListener {
                activyFinish()
            }

            binding.llStep3.btnOpenLock.setOnClickListener {
                viewModel.openLock()
            }
            binding.llStep3.btnCloseLock.setOnClickListener {
                viewModel.closeLock()
            }
            binding.llStep3.btnLinkLock.setOnClickListener {
                val devicePath =
                    mDevicesPath!![binding.llStep3.spSerialNumberLock.selectedItemPosition]
                isLinkLock = !isLinkLock
                if (isLinkLock) {
                    LockManage.getInstance().initSerialByPort(devicePath)
                    SharedPreferencesUtils.setPrefString(this, SERIAL_PORT_LOCK, devicePath)
                    binding.llStep3.btnLinkLock.text = "关闭"
                } else {
                    binding.llStep3.btnLinkLock.text = "连接"
                    LockManage.getInstance().close()
                }
            }
            binding.llStep3.btnLink.setOnClickListener {
                val devicePath =
                    mDevicesPath!![binding.llStep3.spSerialNumber.getSelectedItemPosition()]
                isLink = !isLink
                if (isLink) {
                    RfidManage.getInstance().linkDevice(isLink, devicePath)
                    SharedPreferencesUtils.setPrefString(this, SERIAL_PORT_RFID, devicePath)
                    binding.llStep3.btnLink.text = "关闭"
                } else {
                    binding.llStep3.btnLink.text = "连接"
                    RfidManage.getInstance().linkDevice(isLink, devicePath)
                }
            }
            binding.llStep3.btnGetAntPower.setOnClickListener {
                if (DEBUG) {
                    initTempList()
                } else {
                    viewModel.getAntPower()
                }
            }

            binding.llStep3.btnGetVersionLock.setOnClickListener {
                LockManage.getInstance().getVersion()
            }
            LockManage.getInstance().setVersionCallback(object : MyCallback<String> {
                override fun callback(result: String) {
                    runOnUiThread {
                        toast("版本号:${result}")
                    }
                }
            })
            binding.llStep3.btnGetVersion.setOnClickListener {
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
            binding.llFridgesOperate.visibility = View.GONE
            /*Glide.with(this)
                .load("file:///android_asset/start.gif")
                .into(binding.ivGif)*/
            viewModel.spash()
        }
        doRegisterReceiver()
    }

    private fun initIncludeTitle() {
        binding.includeTitleSncode.tvTitle.text = "产品序列号"
        binding.includeTitleSncodeStep2.tvTitle.text = "产品序列号"
        binding.includeTitleToken.tvTitle.text = "激活令牌"
        binding.includeTitleTokenStep2.tvTitle.text = "激活令牌"
        binding.includeTitleHttp.tvTitle.text = "Http设置"
        binding.includeTitleHttpStep2.tvTitle.text = "Http设置"
        binding.includeTitleAliasStep2.tvTitle.text = "设备别名"
        binding.includeTitleLocationStep2.tvTitle.text = "设备库位"
        binding.includeTitlePasswordStep2.tvTitle.text = "管理员密码"
        binding.includeTitleFridgeType.tvTitle.text = "冰箱类型设置"
    }

    private fun activyFinish() {
        viewModel.setAntPower(tempList)
    }

    fun checkSnCode(sncodeStr: String): Boolean {
        return sncodeStr.isNotEmpty() && sncodeStr.length == 16 && sncodeStr.startsWith("FEDCBA")
    }


    private fun initSpinnerView() {
//        SharedPreferencesUtils.setPrefString(this@SpashActivity, URL_REQUEST, URL_TEST)
        val selectUrlList = listOf(
            URL_TEST, URL_KM, URL_KM1, URL_KM2
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

        SharedPreferencesUtils.setPrefInt(this@SpashActivity, DOOR_TYPE, DOOR_TYPE_SELECT)
        val selectFridgeType = listOf(
            DOOR_TYPE_TWO, DOOR_TYPE_ONE
        )
        binding.spSelectFridgeType.adapter =
            ArrayAdapter(
                this,
                R.layout.app_item_text,
                R.id.tv_content,
                selectFridgeType
            )
        binding.spSelectFridgeType.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    SharedPreferencesUtils.setPrefInt(
                        this@SpashActivity,
                        DOOR_TYPE,
                        position
                    )
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }

    }

    private var fridgesInfoEntity: FridgesInfoEntity? = null
    private fun registIceBox() {
        val adminPassword = binding.edAdminPassword.text.toString().trim()
        val fridgesActiveBo = FridgesActiveBo(
            fridgesInfoEntity?.admin_name,
            MD5Util.encrypt(adminPassword),
            fridgesInfoEntity?.cells?.toInt(),
            fridgesInfoEntity?.device_alias,
            fridgesInfoEntity?.location,
            fridgesInfoEntity?.sncode,
            fridgesInfoEntity?.style?.toInt(),
            fridgesInfoEntity?.temperature?.toInt()
        )
        viewModel.updateFridges(fridgesActiveBo)
    }

    override fun tryLoadData() {

    }

    private fun changeUI(index: Int) {
        when (index) {
            0 -> {
                binding.llStep1.visibility = View.VISIBLE
                binding.llStep2.visibility = View.GONE
                binding.llStep3.step3Layout.visibility = View.GONE
            }
            1 -> {
                binding.llStep1.visibility = View.GONE
                binding.llStep2.visibility = View.VISIBLE
                binding.llStep3.step3Layout.visibility = View.GONE
            }
            2 -> {
                binding.llStep1.visibility = View.GONE
                binding.llStep2.visibility = View.GONE
                binding.llStep3.step3Layout.visibility = View.VISIBLE
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
            if (it) {
                startActivity(Intent(this, LoginOldActivity::class.java))
                finish()
            }
        }

        viewModel.activeIceBoxStatus.observe(this) {
            binding.stepView.go(2, true)
            changeUI(2)
            viewModel.registAdmin(binding.edAdminPassword.text.toString())
        }
        viewModel.setAdminStatus.observe(this) {
            /* if (isSyncOtherSystem) {
                 //同步旧账号信息
                 oldSncoede?.let { viewModel.synchronizedAccount(it) }
             }*/
        }

        viewModel.fridgesActiveResultBo.observe(this) {
            binding.tvToken.text = SharedPreferencesUtils.getPrefString(this, TOKEN, TOKEN_DEFAULT)
            binding.tvLocation.text = it.location
            binding.tvDeviceAlias.text = it.device_alias
            binding.tvSncode.text = it.sncode
            binding.tvBaseUrl.text =
                SharedPreferencesUtils.getPrefString(this, URL_REQUEST, URL_TEST)
            changeUI(1)
            fridgesInfoEntity = it
            binding.stepView.go(1, true)
            viewModel.synchronizedAccountV1()
        }

    }

    private fun startService() {
        //测试模式就不启动服务了
        if (!DEBUG) {
            val intent = Intent(this, MyService::class.java)
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
                    binding.llStep3.tvNfcId.text = value
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
        binding.llStep3.step3Layout.visibility = View.VISIBLE
        val layoutManager = GridLayoutManager(this, 2)
        binding.llStep3.rvAnt.layoutManager = layoutManager
        binding.llStep3.rvAnt.adapter =
            AntListAdapterSpash(
                this@SpashActivity,
                antList,
                object : IAntPowerCallback<AntPowerDao> {
                    override fun antPowerCallback(powerDao: AntPowerDao, position: Int) {
                        tempList[position].power = powerDao.power
                    }
                })
    }

    private fun initSerialPort() {
        val portFinder = SerialPortFinder()
        val devices: Array<String> = portFinder.allDevices
        mDevicesPath = portFinder.allDevicesPath
        binding.llStep3.spSerialNumber.adapter =
            ArrayAdapter(this, R.layout.app_item_text, R.id.tv_content, devices)
        binding.llStep3.spSerialNumberLock.adapter =
            ArrayAdapter(this, R.layout.app_item_text, R.id.tv_content, devices)
        val normalDevice = "ttyS2"
        val normalDeviceLock = "ttyS8"
        for (i in devices.indices) {
            if (devices[i].startsWith(normalDevice)) {
                binding.llStep3.spSerialNumber.setSelection(i)
                break
            }
        }
        for (i in devices.indices) {
            if (devices[i].startsWith(normalDeviceLock)) {
                binding.llStep3.spSerialNumberLock.setSelection(i)
                break
            }
        }
    }

    private fun stopService() {
        if (!DEBUG) {
            unbindService(conn);
        }
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }

    override fun onDestroy() {

        super.onDestroy()
        stopService()
    }
}
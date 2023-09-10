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
import com.jhteck.icebox.R
import com.jhteck.icebox.adapter.AntListAdapterSpash
import com.jhteck.icebox.adapter.IAntPowerCallback
import com.jhteck.icebox.api.*
import com.jhteck.icebox.databinding.AppActivitySpashBinding
import com.jhteck.icebox.service.MyService
import com.jhteck.icebox.utils.SharedPreferencesUtils
import com.jhteck.icebox.viewmodel.SpashViewModel
import org.json.JSONArray
import org.json.JSONObject


/**
 *@Description:(启动页面)
 *@author wade
 *@date 2023/7/3 16:25
 */
class SpashActivity : BaseActivity<SpashViewModel, AppActivitySpashBinding>() {

    private var service: MyService? = null
    private var isBind = false
    private var tempList = mutableListOf<AntPowerDao>()
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
        binding.llFridgesOperate.visibility=View.GONE
        startService()
        // 检查是否是第一次运行应用程序
        var isFirstRun =
            SharedPreferencesUtils.getPrefBoolean(this@SpashActivity, IS_FIRST_RUN, true)
//        var isFirstRun = true
        if (isFirstRun) {
            binding.llFridgesOperate.visibility=View.VISIBLE
            binding.rlSpash.visibility=View.GONE
            var steps = mutableListOf<String>()
            steps.add("Step 1")
            steps.add("Step 2")
            steps.add("Step 3")
            binding.stepView.setSteps(steps)
            binding.stepView.setOnStepClickListener {
                binding.stepView.go(it, true)
                changeUI(it)
            }
            binding.btnStep1Next.setOnClickListener {
                changeUI(1)
                binding.stepView.go(1, true)
            }
            binding.btnStep2Back.setOnClickListener {
                binding.stepView.go(0, true)
                changeUI(0)
            }
            binding.btnStep2Next.setOnClickListener {
                binding.stepView.go(2, true)
                changeUI(2)
            }

            binding.btnStep3Back.setOnClickListener {
                binding.stepView.go(1, true)
                changeUI(1)
            }
            binding.btnStep3Finish.setOnClickListener {
                activyFinish()
            }

            binding.btnOpenLock.setOnClickListener {
                viewModel.openLock()
            }
            binding.btnCloseLock.setOnClickListener {
                viewModel.closeLock()
            }
            binding.btnCloseLamb.setOnClickListener {
                viewModel.closeLamp()
            }
            binding.btnOpenLamb.setOnClickListener {
                viewModel.openLamp()
            }

            changeUI(0)
            // 第一次运行应用程序的操作
            // TODO: 在这里执行你的操作，例如显示欢迎页面或引导用户完成设置等


        } else {
            // 不是第一次运行应用程序的操作
            binding.rlSpash.visibility=View.VISIBLE
            Glide.with(this)
                .load("file:///android_asset/start.gif")
                .into(binding.ivGif)
            viewModel.spash()
        }
        doRegisterReceiver()
    }

    private fun activyFinish() {
        val url = binding.edHttpUrl.toString()
        if (url.isNotEmpty()) {
            SharedPreferencesUtils.setPrefString(this, URL_REQUEST, url)
            viewModel.setAntPower(tempList)
        } else {
            toast("url不能为空")
        }
    }

    private var isGetOldInfo: Boolean = false
    private var isSyncOtherSystem: Boolean = false
    private fun initSpinnerView() {
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
        val style = 1
        val cells = 1
        val fridgesActiveBo = FridgesActiveBo(
            adminName,
            adminPassword,
            cells.toInt(),
            deviceAlias,
            location,
            sncode,
            style.toInt(),
            1000
        )
//        viewModel.activeFridges(fridgesActiveBo)
        if (isGetOldInfo) {
            //同步旧账号信息
            val url = binding.edHttpUrl.text.toString()
            if (url.isNotEmpty()) {
                Log.d("RetrofitClient", "正在同步冰箱账号信息==${url}")
                SharedPreferencesUtils.setPrefString(this, URL_REQUEST, url)
                viewModel.getOldInfo(url)
            } else {
                toast("url不能为空")
            }
        }
        if (isSyncOtherSystem) {
            //wait wait wait
            //同步其他冰箱信息，操作记录，库存记录等
            val oldSncoede = binding.edOldSncode.text.toString().trim()
            if (oldSncoede.isNotEmpty()) {
                viewModel.syncOtherSystem(oldSncoede)

            } else {
                toast("旧序列号不能为空")
            }
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
                binding.llStep3.visibility = View.GONE
            }
            1 -> {
                initSpinnerView()
                binding.llStep1.visibility = View.GONE
                binding.llStep2.visibility = View.VISIBLE
                binding.llStep3.visibility = View.GONE
            }
            2 -> {
                binding.llStep1.visibility = View.GONE
                binding.llStep2.visibility = View.GONE
                binding.llStep3.visibility = View.VISIBLE

                if (DEBUG) {
                    tempList.clear()
                    for (i in 0 until 8) {
                        tempList.add(AntPowerDao("0" + (i + 1), "30"))
                    }
                    initAntRecycleView(tempList)
                } else {
                    viewModel.getAntPower()
                }
            }
        }
    }

    override fun createViewModel(): SpashViewModel {
        return viewModels<SpashViewModel>().value
    }

    override fun initObservables() {
        super.initObservables()
        viewModel.loginStatus.observe(this) {
            startActivity(Intent(this, LoginActivity::class.java))
//            finish()
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
        val intent = Intent(this, MyService::class.java)
        intent.putExtra("from", "LoginActivity")
        bindService(intent, conn, Context.BIND_AUTO_CREATE)
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
                binding.tvNfcId.text = value
            }else if(key.equals(REPORT_ANT_POWER_30)){
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
        binding.llStep3.visibility = View.VISIBLE
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

    private fun stopService() {
        unbindService(conn);
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }

    override fun onDestroy() {
        stopService()
        super.onDestroy()
    }
}
package com.jhteck.icebox.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.hele.mrd.app.lib.base.BaseActivity
import com.hele.mrd.app.lib.base.BaseApp
import com.jhteck.icebox.R
import com.jhteck.icebox.adapter.AppTabPagerAdapter
import com.jhteck.icebox.adapter.ISettlement
import com.jhteck.icebox.adapter.InventoryListAdapter
import com.jhteck.icebox.adapter.InventoryListAdapterScener
import com.jhteck.icebox.api.*
import com.jhteck.icebox.bean.AccountOperationEnum
import com.jhteck.icebox.databinding.AppActivityMainBinding
import com.jhteck.icebox.fragment.InventoryListFrag
import com.jhteck.icebox.fragment.OperationLogFrag
import com.jhteck.icebox.fragment.SettingFrag
import com.jhteck.icebox.repository.entity.AccountEntity
import com.jhteck.icebox.repository.entity.OfflineRfidEntity
import com.jhteck.icebox.utils.BroadcastUtil
import com.jhteck.icebox.utils.DensityUtil
import com.jhteck.icebox.utils.SharedPreferencesUtils
import com.jhteck.icebox.view.SingletonPopupWindow
import com.jhteck.icebox.viewmodel.MainViewModel
import java.util.*
import java.util.stream.Collectors


/**
 *@Description:(主界面)
 *@author wade
 *@date 2023/6/28 20:17
 */
class MainActivity : BaseActivity<MainViewModel, AppActivityMainBinding>() {

    private val TAG = "MainActivity"
    private val tabs = arrayListOf<FrameLayout>()
    private var currentSelectItem = 0
    private val inventoryListFrag = InventoryListFrag.newInstance()
    private val operationLogFrag = OperationLogFrag.newInstance()
    private val settingFrag = SettingFrag.newInstance()

    private var retryScanNum = 0
    private var popupWindow: PopupWindow? = null

    private var localData: RfidDao? = null


    override fun createViewBinding(): AppActivityMainBinding {
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_FULLSCREEN
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        window.decorView.setOnSystemUiVisibilityChangeListener(
            View.OnSystemUiVisibilityChangeListener() {
                fun onSystemUiVisibilityChange(visibility: Int) {
                    window.decorView.systemUiVisibility =
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_FULLSCREEN
                }
            })
        return AppActivityMainBinding.inflate(layoutInflater)
    }

    override fun createViewModel(): MainViewModel {
        return viewModels<MainViewModel>().value
    }

    var loginUserInfo = AccountEntity();
    override fun initView() {
        try {
            val loginUserStr = intent.getStringExtra("loginUserInfo");
            if (loginUserStr != null) {
                loginUserInfo = Gson().fromJson(loginUserStr, AccountEntity::class.java);
                binding.tvUserName.text = loginUserInfo.nick_name;
                binding.tvUserID.text = "User ID:${loginUserInfo.km_user_id}"
            }
        } catch (e: Exception) {

        }
        if (DEBUG) {
            binding.tvUserName.setOnClickListener {
                BroadcastUtil.sendMyBroadcast(this@MainActivity, LOCKED_SUCCESS, "lock")
            }
        }

        val roleID = SharedPreferencesUtils.getPrefInt(this@MainActivity, ROLE_ID, 10)
        //viewpager初始化
        binding.vpContentMain.isUserInputEnabled = false
        binding.tvInventory.isSelected = true
        binding.vpContentMain.offscreenPageLimit = 3

        //现场人员没有设置选项
        if (roleID == 30) {
            tabs.add(binding.llTab1)
            tabs.add(binding.llTab2)
            binding.llTab3.visibility = View.GONE
            binding.vpContentMain.adapter =
                AppTabPagerAdapter(
                    arrayListOf(inventoryListFrag, operationLogFrag),
                    supportFragmentManager,
                    lifecycle
                )

        } else {
            tabs.add(binding.llTab1)
            tabs.add(binding.llTab2)
            tabs.add(binding.llTab3)
            binding.vpContentMain.adapter =
                AppTabPagerAdapter(
                    arrayListOf(inventoryListFrag, operationLogFrag, settingFrag),
                    supportFragmentManager,
                    lifecycle
                )
        }

        binding.llTab1.setOnClickListener {
            setupSelectItem(0)
        }
        binding.llTab2.setOnClickListener {
            setupSelectItem(1)

        }
        binding.llTab3.setOnClickListener {
            setupSelectItem(2)
        }

        binding.imLogo.setOnClickListener {
            //点击10次可以退出app
            exitAfterMany()
        }

        binding.fullscreenView.setOnClickListener {
            Log.d("MainActivity", "fullscreenView")
            viewModel.tryOpenLock()
        }

        viewModel.loadDataLocal()

    }

    var time: Long = 0 //上次点击时间
    var count = 1 //当前点击次数

    fun exitAfterMany() { //在点击事件里调用即可
        var timeNew = Date().time
        if ((timeNew - time) < 1000) { //连续点击间隔
            count += 1
        } else {
            count = 1
        }
        time = timeNew
        if (count >= 10) {  //点击次数
            finish()
            finishAffinity()
            System.exit(0)
//            service?.sendExitMsg()
        }
    }

    override fun tryLoadData() {

    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        viewModel.tryOpenLock()
        return super.dispatchTouchEvent(ev)
    }

    override fun initObservables() {
        super.initObservables()

        viewModel.deleteRfidData.observe(this) {
            //领出
            viewModel.deleteByRfid(it)
            showPopWindow(it, mutableListOf())
        }
        viewModel.addRfidData.observe(this) {
            //存入
            viewModel.addByRfid(it)
            showPopWindow(mutableListOf(), it)
        }
        viewModel.outAndInRfidData.observe(this) {
            viewModel.deleteByRfid(it[0])
            viewModel.addByRfid(it[1])
            showPopWindow(it[0], it[1])
        }
        viewModel.deleteOffRfidData.observe(this) {
            //领出
            showOffPopWindow(it, mutableListOf())
        }
        viewModel.addOffRfidData.observe(this) {
            //存入
            showOffPopWindow(mutableListOf(), it)
        }
        viewModel.outAndInRfidOffData.observe(this) {
            showOffPopWindow(it[0], it[1])
        }
        viewModel.noData.observe(this) {
            showPopWindow(mutableListOf(), mutableListOf())
        }

        viewModel.rfidData.observe(this) { it1 ->
            localData = it1
            inventoryListFrag?.let {
                inventoryListFrag.updateUIbyData(it1)
            }
            //网络请求获取到信息后保存到本地
            viewModel.saveToDb(it1)
        }

        viewModel.inventoryData.observe(this) {
            //网络请求。跟平台获取rfid对应信息
//            viewModel.rfidsSync(it)
        }

        viewModel.rfidsSync.observe(this) {
            viewModel.getRfidsInfo(it)
        }

        viewModel.scanStatus.observe(this) {
            if (it && retryScanNum < 3) {
                retryScanNum++
                viewModel.startFCLInventory30()//重新扫描
            }
        }

        viewModel.closeStatus.observe(this) {
            backLoginPage()
        }

        viewModel.remainTime.observe(this) {
            if (it > 0 && btnCountDownTime != null) {
                btnCountDownTime!!.isEnabled = false
                btnCountDownTime!!.text = "${it}秒后将自动关闭"
            } else {
                backLoginPage()
            }
        }
    }

    private fun setupSelectItem(position: Int) {
        if (position == currentSelectItem) {
            return
        }
        tabs[currentSelectItem].isSelected = false
        tabs[position].isSelected = true
        for (tab in tabs) {
            tab.background = getDrawable(R.drawable.radius_border_tab_gray)
        }
        tabs[position].background = getDrawable(R.drawable.radius_border_tab_select)
        currentSelectItem = position
        binding.vpContentMain.setCurrentItem(position, false)
        binding.tvInventory.setTextColor(getColor(R.color.app_black))
        binding.tvOperation.setTextColor(getColor(R.color.app_black))
        when (position) {
            0 -> {
                binding.tvInventory.setTextColor(getColor(R.color.app_white))
                binding.imInventory.setImageResource(R.mipmap.ic_inventory_select)
                binding.imOperation.setImageResource(R.mipmap.ic_operater)
                binding.imSetting.setImageResource(R.mipmap.ic_setting)
            }
            1 -> {
                binding.tvOperation.setTextColor(getColor(R.color.app_white))
                binding.imInventory.setImageResource(R.mipmap.ic_inventory)
                binding.imOperation.setImageResource(R.mipmap.ic_operater_select)
                binding.imSetting.setImageResource(R.mipmap.ic_setting)
            }
            2 -> {
                binding.imInventory.setImageResource(R.mipmap.ic_inventory)
                binding.imOperation.setImageResource(R.mipmap.ic_operater)
                binding.imSetting.setImageResource(R.mipmap.ic_setting_select)
            }
        }

    }

    override fun onStart() {
        super.onStart()
        viewModel.tryOpenLock()
        doRegisterReceiver();
    }

    override fun onResume() {
        super.onResume()
        if (mReceiver == null)
            doRegisterReceiver()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
        backLoginPage()
    }

    private fun backLoginPage() {
        if (popupWindow?.isShowing == true) {
            DensityUtil.backgroundAlpha(this@MainActivity, 1f)
            binding.cover.visibility = View.GONE
            popupWindow!!.dismiss()
            popupWindow = null
        }
        var intent = Intent(this, LoginOldActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent)
        finish()
    }


    /**
     * 管理员和仓库管理员  显示存入
     * 现场人员存入  显示暂存
     */
    private var showTitle = "存入列表"
    private var operatortype = 0;
    private var btnCountDownTime: Button? = null
    private fun showPopWindow(outList: List<AvailRfid>, inList: List<AvailRfid>) {
        //获取用户角色ID
        val roleID = SharedPreferencesUtils.getPrefInt(this, ROLE_ID, 10)
        var operationEntity = AccountOperationEnum.NO_OPERATION;
        when (roleID) {
            10, 20 -> {
                showTitle = "存入列表"
                if (outList.size > 0 && inList.size > 0) {
                    operatortype = 3
                    operationEntity =
                        (AccountOperationEnum.STORE_CONSUME)//存入取出
                } else if (outList.size > 0) {
                    operatortype = 2
                    operationEntity = (AccountOperationEnum.CONSUME)//取出
                } else if (inList.size > 0) {
                    operatortype = 1
                    operationEntity =
                        (AccountOperationEnum.STORE)//存入
                } else {
                    operationEntity =
                        (AccountOperationEnum.NO_OPERATION)//无操作
                }
            }
            else -> {
                showTitle = "暂存列表"
                if (outList.size > 0 && inList.size > 0) {
                    operatortype = 3
                    operationEntity =
                        (AccountOperationEnum.DESPOSIT_CONSUME)//存入取出
                } else if (outList.size > 0) {
                    operatortype = 2
                    operationEntity = (AccountOperationEnum.CONSUME)//取出
                } else if (inList.size > 0) {
                    operatortype = 1
                    operationEntity =
                        (AccountOperationEnum.DEPOSIT)//存入
                } else {
                    operationEntity =
                        (AccountOperationEnum.NO_OPERATION)//无操作
                }
            }
        }
        viewModel.accountOperationLog(operationEntity, loginUserInfo)//操作日志
        viewModel.rfidOperationLog(loginUserInfo, inList, outList)//操作日志

        //弹出结算界面
        popupWindow = SingletonPopupWindow.getInstance(this@MainActivity)
        val layoutInflater = LayoutInflater.from(this@MainActivity)
        popupWindow?.contentView =
            layoutInflater.inflate(R.layout.pup_out_list, null)

        binding.cover.visibility = View.VISIBLE
        DensityUtil.backgroundAlpha(this@MainActivity, 0.5f)
        //设置按钮
        val btnClosePop = popupWindow?.contentView?.findViewById<ImageButton>(R.id.btnClose)
        val tvCountIn = popupWindow?.contentView?.findViewById<TextView>(R.id.tvCountIn)
        val tvCountOut = popupWindow?.contentView?.findViewById<TextView>(R.id.tvCountOut)
        val tvRemainTitle = popupWindow?.contentView?.findViewById<TextView>(R.id.tvRemainTitle)
        val tvNoOperation =
            popupWindow?.contentView?.findViewById<TextView>(R.id.tv_no_operation)
        tvCountOut?.text = "领出列表(x${outList.size})"
        tvCountIn?.text = "${showTitle}(x${inList.size})"
        btnCountDownTime = popupWindow?.contentView?.findViewById<Button>(R.id.btnCountDownTime)
        when (roleID) {
            10, 20 -> {
                tvCountIn?.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    getDrawable(R.mipmap.ic_put_in),
                    null,
                    null,
                    null
                )
//                        tvCountIn.setBackgroundResource(R.mipmap.ic_put_in_pause)
            }
            else -> {
                tvCountIn?.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    getDrawable(R.mipmap.ic_put_in_pause),
                    null,
                    null,
                    null
                )
            }
        }

        btnClosePop?.setOnClickListener {
            viewModel.stopCountDownTime()
        }
        val rvInventoryResultIN =
            popupWindow?.contentView?.findViewById<RecyclerView>(R.id.rvInventoryResultIN)
        val layoutManagerResultIn = LinearLayoutManager(BaseApp.app)
        rvInventoryResultIN?.layoutManager = layoutManagerResultIn
        //存入列表适配器
        when (roleID) {
            10, 20 -> {
                tvRemainTitle?.visibility = View.INVISIBLE
                var mapinList = inList.stream()
                    .collect(Collectors.groupingBy { t -> t.material.eas_unit_name + t.material.eas_material_id })//根据批号分组
                val tempInList = mutableListOf<List<AvailRfid>>()
                for (key in mapinList.keys) {
                    mapinList.get(key)?.let { tempInList.add(it) }
                }

                rvInventoryResultIN?.adapter = InventoryListAdapter(tempInList)
            }
            else -> {
                if (inList.isNotEmpty()) {
                    tvRemainTitle?.visibility = View.VISIBLE
                }
                rvInventoryResultIN?.adapter =
                    InventoryListAdapterScener(inList, object : ISettlement {
                        override fun settlement(availRfid: AvailRfid) {
                            viewModel.update(availRfid)
                        }
                    })
            }
        }

        //领出列表适配器
        var mapOutList = outList.stream()
            .collect(Collectors.groupingBy { t -> t.material.eas_unit_name + t.material.eas_material_id })//根据批号分组

        val tempOutList = mutableListOf<List<AvailRfid>>()
        for (key in mapOutList.keys) {
            mapOutList.get(key)?.let { tempOutList.add(it) }
        }
        val rvInventoryResultOUT =
            popupWindow?.contentView?.findViewById<RecyclerView>(R.id.rvInventoryResultOUT)
        val layoutManagerResult = LinearLayoutManager(BaseApp.app)
        rvInventoryResultOUT?.layoutManager = layoutManagerResult
        rvInventoryResultOUT?.adapter = InventoryListAdapter(tempOutList)

        //判断存入，领出，控制显隐
        when (operatortype) {
            3 -> {
            }//存入取出
            2 -> {
                tvCountIn?.visibility = View.GONE
                rvInventoryResultIN?.visibility = View.GONE
            }//取出
            1 -> {
                tvCountOut?.visibility = View.GONE
                rvInventoryResultOUT?.visibility = View.GONE
            }//存入
            0 -> {
                tvNoOperation?.visibility = View.VISIBLE
                tvCountIn?.visibility = View.GONE
                rvInventoryResultIN?.visibility = View.GONE
                tvCountOut?.visibility = View.GONE
                rvInventoryResultOUT?.visibility = View.GONE
            }//无操作
        }
        popupWindow?.showAtLocation(binding.root, Gravity.CENTER, 0, 0);
        viewModel.startCountDownTime()
    }

    private fun showOffPopWindow(
        outOffList: List<OfflineRfidEntity>,
        inOffList: List<OfflineRfidEntity>
    ) {
        //获取用户角色ID
        val roleID = SharedPreferencesUtils.getPrefInt(this, ROLE_ID, 10)
        var operationEntity = AccountOperationEnum.NO_OPERATION;
        when (roleID) {
            10, 20 -> {
                showTitle = "存入列表"
                if (outOffList.size > 0 && inOffList.size > 0) {
                    operatortype = 3
                    operationEntity =
                        (AccountOperationEnum.STORE_CONSUME)//存入取出
                } else if (outOffList.size > 0) {
                    operatortype = 2
                    operationEntity = (AccountOperationEnum.CONSUME)//取出
                } else if (inOffList.size > 0) {
                    operatortype = 1
                    operationEntity =
                        (AccountOperationEnum.STORE)//存入
                } else {
                    operationEntity =
                        (AccountOperationEnum.NO_OPERATION)//无操作
                }
            }
            else -> {
                showTitle = "暂存列表"
                if (outOffList.size > 0 && inOffList.size > 0) {
                    operatortype = 3
                    operationEntity =
                        (AccountOperationEnum.DESPOSIT_CONSUME)//存入取出
                } else if (outOffList.size > 0) {
                    operatortype = 2
                    operationEntity = (AccountOperationEnum.CONSUME)//取出
                } else if (inOffList.size > 0) {
                    operatortype = 1
                    operationEntity =
                        (AccountOperationEnum.DEPOSIT)//存入
                } else {
                    operationEntity =
                        (AccountOperationEnum.NO_OPERATION)//无操作
                }
            }
        }
        viewModel.accountOperationLog(operationEntity, loginUserInfo)//操作日志

        viewModel.rfidOffLineOperationLog(loginUserInfo, inOffList, outOffList);//操作日志
//        viewModel.rfidOperationLog(loginUserInfo, inList, outList)//操作日志

        popupWindow = SingletonPopupWindow.getInstance(this@MainActivity)
        //入口参数配置
        val layoutInflater = LayoutInflater.from(this@MainActivity)
        popupWindow?.contentView =
            layoutInflater.inflate(R.layout.pup_out_off_list, null)
        binding.cover.visibility = View.VISIBLE
        DensityUtil.backgroundAlpha(this@MainActivity, 0.5f)
        //设置按钮
        val btnClosePop = popupWindow?.contentView?.findViewById<ImageButton>(R.id.btnClose)
        val tvCountIn = popupWindow?.contentView?.findViewById<TextView>(R.id.tvCountIn)
        val tvCountOut = popupWindow?.contentView?.findViewById<TextView>(R.id.tvCountOut)
        val tvNoOperation =
            popupWindow?.contentView?.findViewById<TextView>(R.id.tv_no_operation)
        tvCountOut?.text = "领出列表(x${outOffList.size})"
        tvCountIn?.text = "${showTitle}(x${inOffList.size})"
        btnCountDownTime = popupWindow?.contentView?.findViewById<Button>(R.id.btnCountDownTime)
        when (roleID) {
            10, 20 -> {
                tvCountIn?.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    getDrawable(R.mipmap.ic_put_in),
                    null,
                    null,
                    null
                )
//                        tvCountIn.setBackgroundResource(R.mipmap.ic_put_in_pause)
            }
            else -> {
                tvCountIn?.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    getDrawable(R.mipmap.ic_put_in_pause),
                    null,
                    null,
                    null
                )
            }
        }

        btnClosePop?.setOnClickListener {
            viewModel.stopCountDownTime()
        }

        //判断存入，领出，控制显隐
        when (operatortype) {
            3 -> {
            }//存入取出
            2 -> {
                tvCountIn?.visibility = View.GONE
            }//取出
            1 -> {
                tvCountOut?.visibility = View.GONE
            }//存入
            0 -> {
                tvNoOperation?.visibility = View.VISIBLE
                tvCountIn?.visibility = View.INVISIBLE
                tvCountOut?.visibility = View.INVISIBLE
            }//无操作
        }
        popupWindow?.showAtLocation(binding.root, Gravity.CENTER, 0, 0);
        viewModel.startCountDownTime()

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
            Log.d("BroadcastReceiver", "Main ---${key}value===${value}")

            when (key) {
                LOCKED_SUCCESS -> {
                    if (DEBUG) {
                        showPopWindow(
                            localData!!.results.avail_rfids,
                            localData!!.results.avail_rfids,
                        )
                        /* showPopWindow(
                             mutableListOf(),
                             mutableListOf(),
                         )*/
                    } else {
                        try {
                            retryScanNum = 0
                            viewModel.startFCLInventory30()
//                            viewModel.startFCLInventory()
                        } catch (e: Exception) {
                        }
                    }
                }
                HFCard -> {

                }
                UPDATE_ACCOUNT_MSG -> {
                    loginUserInfo = Gson().fromJson(value, AccountEntity::class.java);
                    binding.tvUserName.text = loginUserInfo.nick_name;
                    binding.tvUserID.text = "${loginUserInfo.km_user_id}"
                }
            }
        }
    }

}
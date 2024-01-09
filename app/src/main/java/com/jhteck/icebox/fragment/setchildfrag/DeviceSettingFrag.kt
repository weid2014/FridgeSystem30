package com.jhteck.icebox.fragment.setchildfrag

import android.R
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.hele.mrd.app.lib.base.BaseApp
import com.hele.mrd.app.lib.base.BaseFragment
import com.jhteck.icebox.Lockmodel.LockManage
import com.jhteck.icebox.adapter.AntListAdapter
import com.jhteck.icebox.adapter.IAntPowerCallback
import com.jhteck.icebox.api.*
import com.jhteck.icebox.databinding.AppFragmentSettingDeviceBinding
import com.jhteck.icebox.fragment.SettingFrag
import com.jhteck.icebox.myinterface.MyCallback
import com.jhteck.icebox.rfidmodel.RfidManage
import com.jhteck.icebox.utils.BroadcastUtil
import com.jhteck.icebox.utils.CustomDialog
import com.jhteck.icebox.utils.SharedPreferencesUtils
import com.jhteck.icebox.utils.ToastUtils
import com.jhteck.icebox.viewmodel.SettingViewModel
import com.naz.serial.port.SerialPortFinder
import org.json.JSONArray
import org.json.JSONObject


/**
 *@Description:(设备和网络设置页面Fragment)
 *@author wade
 *@date 2023/6/28 21:59
 */
class DeviceSettingFrag : BaseFragment<SettingViewModel, AppFragmentSettingDeviceBinding>() {
    private var TAG = "DeviceSettingFrag"
    private var isLink = true
    private var isLinkLock = true

    companion object {
        fun newInstance(): DeviceSettingFrag {
            return DeviceSettingFrag()
        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): AppFragmentSettingDeviceBinding {
        return AppFragmentSettingDeviceBinding.inflate(layoutInflater)
    }

    override fun createViewModel(): SettingViewModel {
        return viewModels<SettingViewModel>().value
    }

    private val frag by lazy {
        parentFragment as SettingFrag
    }

    private fun initIncludeTitle() {
        binding.fridgeInfo.includeTitleSncodeStep2.tvTitle.text = "产品序列号"
        binding.fridgeInfo.includeTitleTokenStep2.tvTitle.text = "激活令牌"
        binding.fridgeInfo.includeTitleHttpStep2.tvTitle.text = "Http设置"
        binding.fridgeInfo.includeTitleAliasStep2.tvTitle.text = "设备别名"
        binding.fridgeInfo.includeTitleLocationStep2.tvTitle.text = "设备库位"
        binding.fridgeInfo.includeTitlePasswordStep2.tvTitle.text = "管理员密码"
        binding.fridgeInfo.includeTitleFridgeType.tvTitle.text = "冰箱类型设置"
    }

    override fun initView() {
        initIncludeTitle()
        binding.btnFridgesOperate.setOnClickListener {
            viewModel.getFridgesInfo()
//            showFridgesOperateLayout()
        }



        /*binding.fridgeInfo.btnEditSave.setOnClickListener {
            val sncode = binding.fridgeInfo.edSncode.text.toString().trim()
            val deviceAlias = binding.fridgeInfo.edDeviceAlias.text.toString().trim()
            val adminName = binding.fridgeInfo.edAdminName.text.toString().trim()
            val adminPassword = binding.fridgeInfo.edAdminPassword.text.toString().trim()
            val location = binding.fridgeInfo.edLocation.text.toString().trim()
            val temperature = binding.fridgeInfo.edTemperature.text.toString().trim()
            val style = binding.fridgeInfo.edStyle.text.toString().trim()
            val cells = binding.fridgeInfo.edCells.text.toString().trim()
            val fridgesActiveBo = FridgesActiveBo(
                adminName,
                adminPassword,
                cells.toInt(),
                deviceAlias,
                location,
                sncode,
                style.toInt(),
                temperature.toInt()
            )
            if (binding.fridgeInfo.edSncode.text.length == 16) {
                viewModel.updateFridgesInfo(fridgesActiveBo)
                showFridgesOperateLayout()
            }
        }*/
        binding.fridgeInfo.btnCancle.setOnClickListener {
            binding.fridgeInfo.llContent.visibility = View.GONE
        }

        binding.btnSerialSetting.setOnClickListener {
            hideAllLayout()
            binding.includeSerialport.llContent.visibility = View.VISIBLE
            initSerialPort()
        }
        binding.includeSerialport.btnGetAntPower.setOnClickListener {
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
        tempList.clear()
        for (i in 0 until 8) {
            tempList.add(AntPowerDao("0" + (i + 1), "30"))
        }
        initAntRecycleView(tempList)
        if (isLink) {
            binding.includeSerialport.btnLink.text = "关闭"
        } else {
            binding.includeSerialport.btnLink.text = "连接"
        }
        binding.includeSerialport.btnLink.setOnClickListener {
            val devicePath = mDevicesPath!![binding.includeSerialport.spSerialNumber.getSelectedItemPosition()]
            isLink = !isLink
            if (isLink) {
                RfidManage.getInstance().linkDevice(isLink, devicePath)
                SharedPreferencesUtils.setPrefString(requireContext(), SERIAL_PORT_RFID, devicePath)
                binding.includeSerialport.btnLink.text = "关闭"
            } else {
                binding.includeSerialport.btnLink.text = "连接"
                RfidManage.getInstance().linkDevice(isLink, devicePath)
            }
        }

        binding.includeSerialport.btnGetVersion.setOnClickListener {
            RfidManage.getInstance().getVersion()
        }

        RfidManage.getInstance().setVersionCallback(object : MyCallback<String> {
            override fun callback(result: String) {
                requireActivity().runOnUiThread {
//                    binding.includeSerialport.tvVersion.text = result
                    ToastUtils.longToast(requireContext(),"扫描仪的版本号为:$result")
                }
            }
        })

        binding.btnUrlSelect.setOnClickListener {
            hideAllLayout()
            binding.llUrlSelect.visibility = View.VISIBLE

        }
        val baseUrl = SharedPreferencesUtils.getPrefString(requireContext(), URL_REQUEST, URL_TEST)
        if (baseUrl.equals(URL_TEST)) {
            binding.rbUrl1.isChecked = true
        } else if (baseUrl.equals(URL_KM)) {
            binding.rbUrl2.isChecked = true
        } else if (baseUrl.equals(URL_KM1)) {
            binding.rbUrl3.isChecked = true
        } else if (baseUrl.equals(URL_KM2)) {
            binding.rbUrl4.isChecked = true
        }

        binding.btnSaveUrl.setOnClickListener {

            val customDialog = CustomDialog(requireActivity())
            customDialog.setsTitle("温馨提示").setsMessage("是否修改网络连接地址并重启App？")
                .setsCancel("取消", View.OnClickListener {
                    customDialog.dismiss()
                }).setsConfirm("确定", View.OnClickListener {
                    //保存url
                    var urlText = URL_KM2
                    if (binding.rbUrl1.isChecked) {
                        urlText = URL_TEST
                    } else if (binding.rbUrl2.isChecked) {
                        urlText = URL_KM
                    } else if (binding.rbUrl3.isChecked) {
                        urlText = URL_KM1
                    } else if (binding.rbUrl4.isChecked) {
                        urlText = URL_KM2
                    }
                    SharedPreferencesUtils.setPrefString(requireContext(), URL_REQUEST, urlText)
                    binding.llUrlSelect.visibility = View.GONE

                    restartApp()
                    customDialog.dismiss()
                }).show()

        }
        binding.btnCancleUrl.setOnClickListener {
            binding.llUrlSelect.visibility = View.GONE
        }


        mArrayAdapter = ArrayAdapter<String>(requireContext(), R.layout.simple_list_item_1)
        binding.includeSerialport.lvInventory.setAdapter(mArrayAdapter)

        binding.includeSerialport.btnInventory.setOnClickListener {
            mArrayAdapter!!.clear()
            val inventoryTime = binding.includeSerialport.edInventoryTime.getText().toString().toInt() * 1000L
            SharedPreferencesUtils.setSettingLong(
                BaseApp.app, INVENTORY_TIME,
                inventoryTime
            )
            binding.includeSerialport.tvListSize.text = "盘点中..."
            viewModel.startFCLInventory30()
        }

        if (isLinkLock) {
            binding.includeSerialport.btnLinkLock.text = "关闭"
        } else {
            binding.includeSerialport.btnLinkLock.text = "连接"
        }
        binding.includeSerialport.btnLinkLock.setOnClickListener {
            val devicePath = mDevicesPath!![binding.includeSerialport.spSerialNumberLock.selectedItemPosition]
            isLinkLock = !isLinkLock
            if (isLinkLock) {
                LockManage.getInstance().initSerialByPort(devicePath)
                SharedPreferencesUtils.setPrefString(requireContext(), SERIAL_PORT_LOCK, devicePath)
                binding.includeSerialport.btnLinkLock.text = "关闭"
            } else {
                binding.includeSerialport.btnLinkLock.text = "连接"
                LockManage.getInstance().close()
            }
        }

        binding.includeSerialport.btnGetVersionLock.setOnClickListener {
            LockManage.getInstance().getVersion()
        }
        LockManage.getInstance().setVersionCallback(object : MyCallback<String> {
            override fun callback(result: String) {
                requireActivity().runOnUiThread {
                    ToastUtils.longToast(requireContext(),"电子锁的版本号为:${result}")
                }
            }
        })
        binding.includeSerialport.btnOpenLock.setOnClickListener {
            viewModel.openLock()
        }
        binding.includeSerialport.btnCloseLock.setOnClickListener {
            viewModel.closeLock()
        }

        //账号同步相关按键事件
        binding.btnAccountSync.setOnClickListener {
            showSyncAccountLayout()
        }
        binding.btnSyncAccountOk.setOnClickListener {
            //账号拉取
            val oldSncoede = binding.edOldSncode.text.toString().trim()
            if (!chechSnCode(oldSncoede)) {
                ToastUtils.longToast(requireContext(), "请输入正确的SN码")
                return@setOnClickListener
            }
            oldSncoede?.let { viewModel.synchronizedAccount(it) }
        }
        binding.btnSyncAccountCancle.setOnClickListener {
            hideAllLayout()
        }
        doRegisterReceiver()
    }

    private var mArrayAdapter: ArrayAdapter<String>? = null
    private var mDevicesPath: Array<String>? = null
    private fun initSerialPort() {
        val portFinder = SerialPortFinder()
        val devices: Array<String> = portFinder.allDevices
        mDevicesPath = portFinder.allDevicesPath
//        binding.spSerialNumber.adapter=ArrayAdapter(requireContext(), R.layout.simple_list_item_1, devices)
        binding.includeSerialport.spSerialNumber.adapter = ArrayAdapter(
            requireContext(),
            com.jhteck.icebox.R.layout.app_item_text,
            com.jhteck.icebox.R.id.tv_content,
            devices
        )
        binding.includeSerialport.spSerialNumberLock.adapter = ArrayAdapter(
            requireContext(),
            com.jhteck.icebox.R.layout.app_item_text,
            com.jhteck.icebox.R.id.tv_content,
            devices
        )
        val normalDevice = "ttyS2"
        val normalDeviceLock = "ttyS8"
        for (i in devices.indices) {
            if (devices[i].startsWith(normalDevice)) {
                binding.includeSerialport.spSerialNumber.setSelection(i)
                break
            }
        }
        for (i in devices.indices) {
            if (devices[i].startsWith(normalDeviceLock)) {
                binding.includeSerialport.spSerialNumberLock.setSelection(i)
                break
            }
        }
    }

    var tempList = mutableListOf<AntPowerDao>()

    private fun initAntRecycleView(antList: List<AntPowerDao>) {
        binding.includeSerialport.rvAnt.visibility = View.VISIBLE
        val layoutManager = GridLayoutManager(requireContext(), 2)
        binding.includeSerialport.rvAnt.layoutManager = layoutManager
        binding.includeSerialport.rvAnt.adapter =
            AntListAdapter(frag, antList, object : IAntPowerCallback<AntPowerDao> {
                override fun antPowerCallback(powerDao: AntPowerDao, position: Int) {
                    tempList[position].power = powerDao.power
                }
            })
        binding.includeSerialport.rvAnt?.adapter?.notifyDataSetChanged()
        binding.includeSerialport.btnCancleAnt.setOnClickListener {
            binding.includeSerialport.rvAnt.visibility = View.GONE
        }
        binding.includeSerialport.btnSaveAnt.setOnClickListener {
            viewModel.setAntPower(tempList)
        }
    }

    private fun showFridgesOperateLayout() {
        hideAllLayout()
        binding.fridgeInfo.llContent.visibility = View.VISIBLE
    }

    private fun showSyncAccountLayout() {
        hideAllLayout()
        binding.llAccountSync.visibility = View.VISIBLE
    }

    private fun hideAllLayout() {
        binding.fridgeInfo.llContent.visibility = View.GONE
        binding.includeSerialport.llContent.visibility = View.GONE
        binding.llUrlSelect.visibility = View.GONE
        binding.llAccountSync.visibility = View.GONE
    }

    override fun initObservables() {
        super.initObservables()
        viewModel.fridgesActiveResultBo.observe(this) {
            if (it != null) {
                showFridgesOperateLayout()
                binding.fridgeInfo.tvLocation.text = it.location
                binding.fridgeInfo.tvDeviceAlias.setText(it.device_alias)
//                binding.fridgeInfo.edCells.setText(it.cells.toString())
                binding.fridgeInfo.tvSncode.setText(it.sncode)
//                binding.fridgeInfo.edStyle.setText(it.style.toString())
//                binding.fridgeInfo.edTemperature.setText("${(it.temperature)}")
            } else {
                showFridgesOperateLayout()
                binding.fridgeInfo.tvLocation.setText("ceshi")
                binding.fridgeInfo.tvDeviceAlias.setText("ceshi")
//                binding.fridgeInfo.edCells.setText("ceshi")
                binding.fridgeInfo.tvSncode.setText("ceshi")
//                binding.fridgeInfo.edStyle.setText("ceshi")
//                binding.fridgeInfo.edTemperature.setText("ceshi")
                /*val customDialog = CustomDialog(requireContext())
                val sncode = SharedPreferencesUtils.getPrefString(
                    requireContext(), SNCODE,
                    SNCODE_TEST
                )
                customDialog.setsTitle("温馨提示").setsMessage("${sncode}状态异常，请重新激活")
                    .setsCancel("取消", View.OnClickListener {
                        customDialog.dismiss()
                    }).setsConfirm("确定", View.OnClickListener {
                        customDialog.dismiss()
                    }).show()*/
            }
        }

        viewModel.syncAccountSuccess.observe(this) {
            BroadcastUtil.sendMyBroadcast(
                this.requireContext(),
                SYNC_ACCOUNT_MSG,
                ""
            )
        }

        viewModel.rfidsList.observe(this) {
            for (i in it.indices) {
                mArrayAdapter!!.add(it[i])
            }
            mArrayAdapter!!.notifyDataSetChanged()
            binding.includeSerialport.tvListSize.text = "共${it.size}个"
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
        activity?.let {
            it.registerReceiver(mReceiver, filter)
        }
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
            } else if (key.equals(REPORT_ANT_POWER_30)) {
                stringToArray(value.toString())
            }
        }
    }

    private fun stringToArray(string: String) {
        tempList.clear()
        val jsonArray = JSONArray(string)
        for (i in 0 until jsonArray.length()) {
            val antid = i.toString()
            val po = jsonArray[i].toString()
            tempList.add(AntPowerDao("$antid", po))
        }
        initAntRecycleView(tempList)
    }

    //重启app
    private fun restartApp() {
        val intent =
            BaseApp.app.packageManager.getLaunchIntentForPackage(BaseApp.app.packageName)
        intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        requireActivity().finish()
    }

    fun chechSnCode(sncodeStr: String): Boolean {
        return sncodeStr.isNotEmpty() && sncodeStr.length == 16 && sncodeStr.startsWith("FEDCBA")
    }
}
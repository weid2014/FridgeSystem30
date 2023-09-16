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
import com.jhteck.icebox.utils.SharedPreferencesUtils
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
    private var isLink = false
    private var isLinkLock = false

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

    override fun initView() {
        binding.btnFridgesOperate.setOnClickListener {
            viewModel.getFridgesInfo()
//            showFridgesOperateLayout()
        }
        binding.btnActive.setOnClickListener {
            val sncode = binding.edSncode.text.toString().trim()
            val deviceAlias = binding.edDeviceAlias.text.toString().trim()
            val adminName = binding.edAdminName.text.toString().trim()
            val adminPassword = binding.edAdminPassword.text.toString().trim()
            val location = binding.edLocation.text.toString().trim()
            val temperature = binding.edTemperature.text.toString().trim()
            val style = binding.edStyle.text.toString().trim()
            val cells = binding.edCells.text.toString().trim()

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
            if (binding.edSncode.text.length == 16) {

                viewModel.activeFridges(fridgesActiveBo)
                showFridgesOperateLayout()
            }
        }

        binding.btnEditSave.setOnClickListener {
            val sncode = binding.edSncode.text.toString().trim()
            val deviceAlias = binding.edDeviceAlias.text.toString().trim()
            val adminName = binding.edAdminName.text.toString().trim()
            val adminPassword = binding.edAdminPassword.text.toString().trim()
            val location = binding.edLocation.text.toString().trim()
            val temperature = binding.edTemperature.text.toString().trim()
            val style = binding.edStyle.text.toString().trim()
            val cells = binding.edCells.text.toString().trim()
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
            if (binding.edSncode.text.length == 16) {
                viewModel.updateFridgesInfo(fridgesActiveBo)
                showFridgesOperateLayout()
            }
        }
        binding.btnCancle.setOnClickListener {
            binding.llFridgesOperate.visibility = View.GONE
        }

        binding.btnSerialSetting.setOnClickListener {
            hideAllLayout()
            binding.llSerialSetting.visibility = View.VISIBLE
            initSerialPort()
        }
        binding.btnGetAntPower.setOnClickListener {
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

        binding.btnLink.setOnClickListener {
            val devicePath = mDevicesPath!![binding.spSerialNumber.getSelectedItemPosition()]
            isLink = !isLink
            if (isLink) {
                RfidManage.getInstance().linkDevice(isLink, devicePath)
                SharedPreferencesUtils.setPrefString(requireContext(), SERIAL_PORT_RFID, devicePath)
                binding.btnLink.text = "关闭"
            } else {
                binding.btnLink.text = "连接"
                RfidManage.getInstance().linkDevice(isLink, devicePath)
            }
        }

        binding.btnGetVersion.setOnClickListener {
            RfidManage.getInstance().getVersion()
        }

        RfidManage.getInstance().setVersionCallback(object : MyCallback<String> {
            override fun callback(result: String) {
                requireActivity().runOnUiThread {
                    binding.tvVersion.text = result
                }
            }
        })

        binding.btnUrlSelect.setOnClickListener {
            hideAllLayout()
            binding.llUrlSelect.visibility = View.VISIBLE

        }
        if (SharedPreferencesUtils.getPrefString(requireContext(), URL_REQUEST, URL_TEST).equals(
                URL_TEST
            )
        ) {
            binding.rbUrl1.isChecked = true
        } else {
            binding.rbUrl2.isChecked = true
        }

        binding.btnSaveUrl.setOnClickListener {
            //保存url
            var urlText = URL_TEST
            if (binding.rbUrl1.isChecked) {
                urlText = URL_TEST
            } else if (binding.rbUrl2.isChecked) {
                urlText = URL_KM
            }
            SharedPreferencesUtils.setPrefString(requireContext(), URL_REQUEST, urlText)
            binding.llUrlSelect.visibility = View.GONE
        }
        binding.btnCancleUrl.setOnClickListener {
            binding.llUrlSelect.visibility = View.GONE
        }

        binding.btnSyncAccount.setOnClickListener {
            viewModel.syncAccount()
        }

        mArrayAdapter = ArrayAdapter<String>(requireContext(), R.layout.simple_list_item_1)
        binding.lvInventory.setAdapter(mArrayAdapter)

        binding.btnInventory.setOnClickListener {
            mArrayAdapter!!.clear()
            val inventoryTime = binding.edInventoryTime.getText().toString().toInt() * 1000L
            SharedPreferencesUtils.setSettingLong(
                BaseApp.app, INVENTORY_TIME,
                inventoryTime
            )
            binding.tvListSize.text = "盘点中..."
            viewModel.startFCLInventory30()
        }

        binding.btnLinkLock.setOnClickListener {
            val devicePath = mDevicesPath!![binding.spSerialNumberLock.selectedItemPosition]
            isLinkLock = !isLinkLock
            if (isLinkLock) {
                LockManage.getInstance().initSerialByPort(devicePath)
                SharedPreferencesUtils.setPrefString(requireContext(), SERIAL_PORT_LOCK, devicePath)
                binding.btnLinkLock.text = "关闭"
            } else {
                binding.btnLinkLock.text = "连接"
                LockManage.getInstance().close()
            }
        }

        binding.btnGetVersionLock.setOnClickListener {
            LockManage.getInstance().getVersion()
        }
        LockManage.getInstance().setVersionCallback(object : MyCallback<String> {
            override fun callback(result: String) {
                requireActivity().runOnUiThread {
                    binding.tvVersionLock.text = "版本号:${result}"
                }
            }
        })
        binding.btnOpenLock.setOnClickListener {
            viewModel.openLock()
        }
        binding.btnCloseLock.setOnClickListener {
            viewModel.closeLock()
        }
        binding.btnLockStatus.setOnClickListener {
            viewModel.getLockStatus()
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
        binding.spSerialNumber.adapter = ArrayAdapter(
            requireContext(),
            com.jhteck.icebox.R.layout.app_item_text,
            com.jhteck.icebox.R.id.tv_content,
            devices
        )
        binding.spSerialNumberLock.adapter = ArrayAdapter(
            requireContext(),
            com.jhteck.icebox.R.layout.app_item_text,
            com.jhteck.icebox.R.id.tv_content,
            devices
        )
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

    var tempList = mutableListOf<AntPowerDao>()

    private fun initAntRecycleView(antList: List<AntPowerDao>) {
        binding.llAntPower.visibility = View.VISIBLE
        val layoutManager = GridLayoutManager(requireContext(), 4)
        binding.rvAnt.layoutManager = layoutManager
        binding.rvAnt.adapter =
            AntListAdapter(frag, antList, object : IAntPowerCallback<AntPowerDao> {
                override fun antPowerCallback(powerDao: AntPowerDao, position: Int) {
                    tempList[position].power = powerDao.power
                }
            })
        binding.btnCancleAnt.setOnClickListener {
            binding.llAntPower.visibility = View.GONE
        }
        binding.btnSaveAnt.setOnClickListener {
            viewModel.setAntPower(tempList)
        }
    }

    private fun showFridgesOperateLayout() {
        hideAllLayout()
        binding.llFridgesOperate.visibility = View.VISIBLE
    }

    private fun hideAllLayout() {
        binding.llFridgesOperate.visibility = View.GONE
        binding.llSerialSetting.visibility = View.GONE
        binding.llUrlSelect.visibility = View.GONE
    }

    override fun initObservables() {
        super.initObservables()
        viewModel.fridgesActiveResultBo.observe(this) {
            showFridgesOperateLayout()
            binding.edLocation.setText(it.location)
            binding.edDeviceAlias.setText(it.device_alias)
            binding.edCells.setText(it.cells.toString())
            binding.edSncode.setText(it.sncode)
            binding.edStyle.setText(it.style.toString())
            binding.edTemperature.setText("${(it.temperature)}")
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
            binding.tvListSize.text = "共${it.size}个"
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

}
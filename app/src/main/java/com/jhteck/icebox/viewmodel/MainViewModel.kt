package com.jhteck.icebox.viewmodel

import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.hele.mrd.app.lib.base.BaseApp
import com.hele.mrd.app.lib.base.BaseViewModel
import com.hele.mrd.app.lib.base.livedata.SingleLiveEvent
import com.jhteck.icebox.Lockmodel.LockManage
import com.jhteck.icebox.api.*
import com.jhteck.icebox.api.request.RequestRfidsDao
import com.jhteck.icebox.api.request.RfidSync
import com.jhteck.icebox.api.request.requestSync
import com.jhteck.icebox.apiserver.IMainApiService
import com.jhteck.icebox.apiserver.LocalService
import com.jhteck.icebox.apiserver.RetrofitClient
import com.jhteck.icebox.bean.AccountOperationEnum
import com.jhteck.icebox.bean.OperationErrorEnum
import com.jhteck.icebox.repository.entity.AccountEntity
import com.jhteck.icebox.repository.entity.AccountOperationEntity
import com.jhteck.icebox.repository.entity.OperationErrorLogEntity
import com.jhteck.icebox.repository.entity.RfidOperationEntity
import com.jhteck.icebox.rfidmodel.RfidManage
import com.jhteck.icebox.utils.DateUtils
import com.jhteck.icebox.utils.DbUtil
import com.jhteck.icebox.utils.SharedPreferencesUtils
import com.jhteck.icebox.utils.SnowFlake
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeoutException

/**
 *@Description:(主界面viewModel )
 *@author wade
 *@date 2023/6/28 17:27
 */
class MainViewModel(application: android.app.Application) :
    BaseViewModel<IMainApiService>(application) {
    private val TAG = "MainViewModel"
    private var localRfidData: RfidDao? = null

    override fun createApiServiceType(): Class<IMainApiService> {
        return IMainApiService::class.java
    }

    fun loadDataLocal() {
        viewModelScope.launch(Dispatchers.Default) {//使用Room操作本地数据库 (Dispatchers.Default) 不能省略
            try {
                showLoading("加载数据，请稍等...")
                val gson = Gson()
//                LocalService.mockDataToLocal(gson);
                var result = LocalService.loadRfidsFromLocal(gson)
                delay(1000)
                loadDataLocalStatus.postValue(true)
                localRfidData = RfidDao(200, "12313", result)
                rfidData.postValue(localRfidData)
            } catch (e: Exception) {
                toast(e.message)
                loadDataLocalStatus.postValue(false)
                LocalService.cleanTable()
                toast("加载数据异常")
            } finally {
                hideLoading()
            }
        }
    }

    private var lastonclickTime = 0L;//全局变量

    /**
     * RFID扫描事件
     * 假如异常，重试3次
     */
    fun startFCLInventory() {
        //防止响应过于频繁
        var time = SystemClock.uptimeMillis();//局部变量
        if (time - lastonclickTime <= 8000) {

        } else {
            lastonclickTime = time
            viewModelScope.launch {
                try {
                    showLoading("正在结算中，请稍等...")

//                    MyTcpServerListener.getInstance().getFCLInventory()
//                    delay(5000)
////                    scanStatus.postValue(true)
//                    MyTcpServerListener.getInstance().setOnInventoryResult {
//                        //扫描结束后获取到rfid集合
////                        inventoryData.postValue(it.toList())
//                        Log.d(TAG, "弹出pop")
//                        rfidsSync(it.toList())
//                    }
                } catch (e: Exception) {
//                    scanStatus.postValue(false)
                    toast("结算异常")
                } finally {
                    hideLoading()
                }
            }
        }
    }

    fun startFCLInventory30() {
        //防止响应过于频繁
        var time = SystemClock.uptimeMillis();//局部变量
        if (time - lastonclickTime <= 18000) {

        } else {
            lastonclickTime = time
            viewModelScope.launch {
                try {
                    RfidManage.getInstance().startStop(true, false)
                    showLoading("正在结算中，请稍等...")
                    RfidManage.getInstance().setRfidArraysRendEndCallback {
                        Log.d(TAG, "正在结算中，请稍等...${it.toString()}")
                        rfidsSync(it.toList())
                    }
                    delay(
                        SharedPreferencesUtils.getPrefLong(
                            BaseApp.app, INVENTORY_TIME,
                            INVENTORY_TIME_DEFAULT
                        )
                    )
                    RfidManage.getInstance().startStop(false, false)
                } catch (e: Exception) {
//                    scanStatus.postValue(false)
                    toast("结算异常")
                } finally {
                    RfidManage.getInstance().setRfidArraysRendEndCallback();//清空掉
                    hideLoading()
                }
            }
        }
    }


    fun rfidsSync(rfids: List<String>) {
        Log.d(TAG, "rfidsSync")
        viewModelScope.launch {
            try {
                showLoading("全量上报，请稍等...")
                //全量上报
                val rfidList = mutableListOf<RfidSync>()
                for (rfid in rfids) {
                    rfidList.add(RfidSync(1, 100, rfid))
                }
                val bodySync = genBody(requestSync(rfidList))
//                apiService.syncRfids()
                val repSync = RetrofitClient.getService().syncRfids(bodySync)
                if (repSync.isSuccessful) {
                    Log.d(TAG, "全量上报成功")
                }
            } catch (e: Exception) {
                Log.e(TAG, "全量上报异常$e")
            } finally {
                rfidsSync.postValue(rfids)
                hideLoading()
            }
        }
    }

    fun getRfidsInfo(rfids: List<String>) {
        Log.d(TAG, "正在查询RFID===--->${rfids.toString()}")
        //测试用
        /*  var rfids = mutableListOf<String>()
           rfids.add("E1234567000000000000030B")
   //        testRfidList.add("E1234567000000000000004C")
           rfids.add("E1234567000000000000030A")
           rfids.add("E12345670000000000000309")*/

        viewModelScope.launch {
            try {
                showLoading("正在查询RFID，请稍等...")
                val body = genBody(RequestRfidsDao(rfids))
                val rep = RetrofitClient.getService().getRfids(body)
                val data = rep.body()
                //获取到之后跟本地数据对比(增加或减少了什么)
                val tempList = mutableListOf<String>()
                val outList = mutableListOf<AvailRfid>()//领出列表
                val inList = mutableListOf<AvailRfid>()//存入列表
                val localAvailRfid = localRfidData!!.results.avail_rfids
                val getAvailRfid = rep.body()!!.results.avail_rfids

                val aAvailRfid = mutableListOf<String>()
                for (availrfis in getAvailRfid) {
                    aAvailRfid.add(availrfis.rfid)
                }
                for (localRfid in localAvailRfid) {
                    tempList.add(localRfid.rfid)
                }
                for (rfid in tempList) {
                    if (!aAvailRfid.contains(rfid)) {
                        for (localRfid in localAvailRfid) {
                            if (localRfid.rfid == rfid) {
                                outList.add(localRfid)
                            }
                        }
                    }
                }
                for (rfid in rfids) {
                    if (!tempList.contains(rfid)) {
                        for (getRfid in getAvailRfid) {
                            if (getRfid.rfid == rfid) {
                                inList.add(getRfid)
                            }
                        }
                    }
                }
                delay(500)
                if (outList.size > 0 && inList.size > 0) {//存取都有
                    val tempList = mutableListOf<List<AvailRfid>>()
                    tempList.add(outList)
                    tempList.add(inList)
                    outAndInRfidData.postValue(tempList)
                } else if (outList.size > 0) {//领出
                    deleteRfidData.postValue(outList)
                } else if (inList.size > 0) {//领入
                    addRfidData.postValue(inList)
                } else {
                    noData.postValue(true)
                    toast("没有存取")
//                    closeStatus.postValue(true)
                }
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
                toast("查询异常$e")
            } finally {
                hideLoading()
            }
        }
    }

    fun deleteByRfid(list: List<AvailRfid>) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                showLoading("结果正在保存，请稍等...")
                LocalService.deleteDataToLocal(list);
                Log.d(TAG, "保存到本地成功")
            } catch (e: Exception) {
                toast("保存失败${e.message}")
                Log.d(TAG, "保存失败3${e.message}")
            } finally {
                hideLoading()
            }
        }
    }

    fun addByRfid(list: List<AvailRfid>) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                showLoading("结果正在保存，请稍等...")
                LocalService.addDataToLocal(list);
//                LocalService.realDataToLocal(list);
                Log.d(TAG, "保存到本地成功")

            } catch (e: Exception) {
                Log.d(TAG, "保存失败1${e.message}")
                toast("保存失败${e.message}")
            } finally {
                hideLoading()
            }
        }
    }

    //把结果保存到本地
    fun saveToDb(rfidDao: RfidDao) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                showLoading("结果正在保存，请稍等...")
                LocalService.realDataToLocal(rfidDao);
                Log.d(TAG, "保存到本地成功")

            } catch (e: Exception) {
                toast("保存失败${e.message}")
                Log.d(TAG, "保存失败2${e.message}")
            } finally {
                hideLoading()
            }
        }
    }

    /**
     * 单独更新AvailRfid
     */
    fun update(availRfid: AvailRfid) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                LocalService.updateAvailRfidOnly(availRfid)
            } catch (e: Exception) {

                toast("更新失败${e.message}")
                Log.d(TAG, "保存失败2${e.message}")
            }
        }
    }

    /**
     * 操作记录
     */
    fun accountOperationLog(
        accountOperationEnum: AccountOperationEnum,
        accountEntity: AccountEntity
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                var accountOperationEntity =
                    createAccountOperationEntity(accountOperationEnum, accountEntity.user_id)
                when (accountOperationEnum) {
                    AccountOperationEnum.DEPOSIT -> accountEntity.deposit_count += 1;
                    AccountOperationEnum.STORE -> accountEntity.store_count += 1;
                    AccountOperationEnum.CONSUME -> accountEntity.consume_count += 1;
                    AccountOperationEnum.STORE_CONSUME -> {
                        accountEntity.store_count += 1;
                        accountEntity.consume_count += 1;
                    }
                    AccountOperationEnum.DESPOSIT_CONSUME -> {
                        accountEntity.deposit_count += 1;
                        accountEntity.consume_count += 1;
                    }
                }
                DbUtil.getDb().accountDao().update(accountEntity)//更新用户操作

                DbUtil.getDb().accountOperationDao().insert(accountOperationEntity);//增加操作日志

                var logs = mutableListOf<AccountOperationEntity>()
                logs.add(accountOperationEntity)

                var accountOperationBO = AccountOperationBO(logs)

                var toJson = Gson().toJson(accountOperationBO)
                println(toJson)

                var result = RetrofitClient.getService().addAccountLogs(accountOperationBO)//发送到平台
                if (result.code() == 200) {
                    accountOperationEntity.hasUploaded = true
                    DbUtil.getDb().accountOperationDao().update(accountOperationEntity);//操作记录状态更新
                }

                // RFID
            } catch (e: Exception) {

                toast("更新失败${e.message}")
                Log.d(TAG, "保存失败2${e.message}")
            }
        }
    }

    /**
     * RFID操作
     */
    fun rfidOperationLog(
        accountEntity: AccountEntity,
        inList: List<AvailRfid>,
        outList: List<AvailRfid>
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                var updaloads = mutableListOf<RfidOperationEntity>();
                for (item in inList) {
                    var rfidOperationEntity = RfidOperationEntity()
                    rfidOperationEntity.user_id = accountEntity.user_id;
                    rfidOperationEntity.user_log_id = SnowFlake.getInstance().nextIdStr();
                    rfidOperationEntity.nick_name = accountEntity.nick_name;
                    if (accountEntity.role_id.toInt() == 30) {
                        rfidOperationEntity.operation = 2
                    } else {
                        rfidOperationEntity.operation = 0
                    }
                    rfidOperationEntity.log_at = "${
                        DateUtils.formatDateToString(
                            Date(),
                            DateUtils.format_yyyyMMddhhmmssfff
                        )
                    }+08:00".replace(" ", "T")
                    rfidOperationEntity.eas_material_number = item.material.eas_material_number;
                    rfidOperationEntity.eas_material_name = item.material.eas_material_name;
                    rfidOperationEntity.eas_unit_number = item.material.eas_unit_number;
                    rfidOperationEntity.eas_supplier_name = item.eas_supplier_name;
                    rfidOperationEntity.eas_unit_name = item.material.eas_unit_name;
                    rfidOperationEntity.number = "1";
                    rfidOperationEntity.eas_lot = item.material_batch.eas_lot;
                    rfidOperationEntity.rfid = item.rfid;
                    rfidOperationEntity.cell_number = item.cell_number
                    rfidOperationEntity.eas_specs = item.material_batch.eas_specs
                    rfidOperationEntity.eas_manufacturer = item.material.eas_manufacturer
                    rfidOperationEntity.role_id = accountEntity.role_id.toInt()
                    var id = DbUtil.getDb().rfidOperationDao().insert(rfidOperationEntity);
                    rfidOperationEntity.id = id
                    updaloads.add(rfidOperationEntity);
                }
                for (item in outList) {
                    var rfidOperationEntity = RfidOperationEntity()
                    rfidOperationEntity.user_id = accountEntity.user_id;
                    rfidOperationEntity.user_log_id = SnowFlake.getInstance().nextIdStr();
                    rfidOperationEntity.nick_name = accountEntity.nick_name;
                    rfidOperationEntity.operation = 1
                    rfidOperationEntity.log_at = "${
                        DateUtils.formatDateToString(
                            Date(),
                            DateUtils.format_yyyyMMddhhmmssfff
                        )
                    }+08:00".replace(" ", "T")
                    rfidOperationEntity.eas_material_number = item.material.eas_material_number;
                    rfidOperationEntity.eas_material_name = item.material.eas_material_name;
                    rfidOperationEntity.eas_unit_number = item.material.eas_unit_number;
                    rfidOperationEntity.eas_supplier_name = item.eas_supplier_name;
                    rfidOperationEntity.eas_unit_name = item.material.eas_unit_name;
                    rfidOperationEntity.number = "1";
                    rfidOperationEntity.eas_lot = item.material_batch.eas_lot;
                    rfidOperationEntity.rfid = item.rfid;
                    rfidOperationEntity.cell_number = item.cell_number
                    rfidOperationEntity.eas_specs = item.material_batch.eas_specs
                    rfidOperationEntity.eas_manufacturer = item.material.eas_manufacturer
                    rfidOperationEntity.role_id = accountEntity.role_id.toInt()
                    var id = DbUtil.getDb().rfidOperationDao().insert(rfidOperationEntity);
                    rfidOperationEntity.id = id
                    updaloads.add(rfidOperationEntity);
                }
                var rfidOperationBO = RfidOperationBO(updaloads);

                if (rfidOperationBO.logs.size > 0) {

                    var toJson = Gson().toJson(rfidOperationBO)

                    Log.d(TAG, "${toJson}")
                    try {
                        var res = RetrofitClient.getService().syncRfidsLogs(rfidOperationBO)
                        if (res.code() == 200) {
                            for (data in rfidOperationBO.logs) {
                                data.isHasUpload = true;
                                DbUtil.getDb().rfidOperationDao().update(data);
                            }
                        }
                    } catch (te: TimeoutException) {
                        Log.e(TAG, te.message.toString())
                        var operationErrorLogDao = DbUtil.getDb().operationErrorLogDao()
                        for (rfidOperationEntity in updaloads) {
                            var operationErrorLogEntity = OperationErrorLogEntity(
                                rfidOperationEntity.id,
                                rfidOperationEntity.user_id,
                                rfidOperationEntity.role_id.toString(),
                                100,
                                rfidOperationEntity.rfid,
                                OperationErrorEnum.TIME_OUT.v,
                                rfidOperationEntity.log_at,
                                false
                            )
                            operationErrorLogDao.insert(operationErrorLogEntity)//保存异常操作
                        }
                    }
                }

            } catch (e: Exception) {

                toast("更新失败${e.message}")
                Log.d(TAG, "保存失败2${e.message}")
            }
        }
    }

    fun tryOpenLock() {
       LockManage.getInstance().tryOpenLock();
    }

    private fun createAccountOperationEntity(
        operation: AccountOperationEnum,
        userId: String
    ): AccountOperationEntity {
        var accountOperationEntity = AccountOperationEntity()
        accountOperationEntity.operation = operation.v.toByte()
        accountOperationEntity.user_id = userId
        accountOperationEntity.user_log_id = SnowFlake.getInstance().nextIdStr()
        accountOperationEntity.log_at = Date().toString()
        accountOperationEntity.hasUploaded = false
        return accountOperationEntity
    }

    val scanStatus by lazy {
        SingleLiveEvent<Boolean>()
    }

    //本地rfid数据
    val rfidData by lazy {
        SingleLiveEvent<RfidDao>()
    }

    //需要删除的rfid数据
    val deleteRfidData by lazy {
        SingleLiveEvent<List<AvailRfid>>()
    }

    //需要增加的rfid数据
    val addRfidData by lazy {
        SingleLiveEvent<List<AvailRfid>>()
    }

    val outAndInRfidData by lazy {
        SingleLiveEvent<List<List<AvailRfid>>>()
    }

    val noData by lazy {
        SingleLiveEvent<Boolean>()
    }


    val loadDataLocalStatus by lazy {
        SingleLiveEvent<Boolean>()
    }

    val closeStatus by lazy {
        SingleLiveEvent<Boolean>()
    }
    val rfidsSync by lazy {
        SingleLiveEvent<List<String>>()
    }

    val inventoryData by lazy {
        SingleLiveEvent<List<String>>()
    }

}
package com.jhteck.icebox.viewmodel

import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.hele.mrd.app.lib.base.BaseViewModel
import com.hele.mrd.app.lib.base.livedata.SingleLiveEvent
import com.jhteck.icebox.Lockmodel.LockManage
import com.jhteck.icebox.api.AntPowerDao
import com.jhteck.icebox.api.FridgesActiveBo
import com.jhteck.icebox.api.SNCODE
import com.jhteck.icebox.apiserver.ILoginApiService
import com.jhteck.icebox.apiserver.RetrofitClient
import com.jhteck.icebox.repository.entity.AccountEntity
import com.jhteck.icebox.repository.entity.OperationErrorLogEntity
import com.jhteck.icebox.repository.entity.SysOperationErrorEntity
import com.jhteck.icebox.rfidmodel.RfidManage
import com.jhteck.icebox.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

/**
 *@Description:(登录 模块viewmodel)
 *@author wade
 *@date 2023/6/28 17:27
 */
class SpashViewModel(application: android.app.Application) :
    BaseViewModel<ILoginApiService>(application) {

    fun spash() {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                delay(3000)
                loginStatus.postValue(true)
            } catch (e: Exception) {

                toast(e.message)
            } finally {
                hideLoading()
            }
        }
    }

    private var lastonclickTime = 0L;//全局变量
    fun activeFridges(fridgesActiveBo: FridgesActiveBo) {
        var time = SystemClock.uptimeMillis();//局部变量
        if (time - lastonclickTime <= 3000) {
        } else {
            lastonclickTime = time
            viewModelScope.launch(Dispatchers.Default) {
                try {
                    showLoading("正在激活冰箱，请稍等...")
                    val response = RetrofitClient.getService().fridgesActive(fridgesActiveBo);
                    if (response.code() == 200) {
                        toast("激活冰箱成功")
                        var res = response.body()?.results;
                        if (res != null) {
                            var id = res.id;
                            if (id != null) {
                                var fridgesInfo = DbUtil.getDb().fridgesInfoDao().getById(id)
                                if (fridgesInfo != null) {
                                    res.f_id = fridgesInfo.f_id;
                                    DbUtil.getDb().fridgesInfoDao().update(res)
                                } else {
                                    DbUtil.getDb().fridgesInfoDao().insert(res)
                                }
                            }
                        }
                        SharedPreferencesUtils.setPrefString(
                            getApplication(),
                            SNCODE,
                            fridgesActiveBo.sncode
                        )
                        activeIceBoxStatus.postValue(true)
                        //wait wait wait
//                        synchronizedAccount()//同步账户
                    } else {
                        toast("激活冰箱异常${response.message()}")
                    }
                } catch (e: Exception) {
                    toast("激活冰箱异常${e.message}")
                } finally {
                    hideLoading()
                }
            }
        }
    }

    fun registAdmin(passWord: String) {
        object : Thread() {
            override fun run() {
                try {

                    var all = DbUtil.getDb().fridgesInfoDao().getAll()
                    if (all == null || all.size == 0) {
                        println("未激活")
                    } else {
                        SharedPreferencesUtils.setPrefString(
                            ContextUtils.getApplicationContext(),
                            SNCODE,
                            all.first().sncode
                        )//设备已激活
                    }
                    var accountDao = DbUtil.getDb().accountDao()
                    val adminUser = accountDao.findByRoleId("10")

                    if (adminUser == null) {
                        var user = AccountEntity();
                        user.id = 1;
                        user.user_id = SnowFlake.getInstance().nextId().toString();
                        user.km_user_id = "30812321";
                        user.real_name = "管理员";
                        user.nick_name = "admin";
                        user.role_id = "10";
                        user.password_digest = MD5Util.encrypt(passWord);
//                        user.nfc_id = "1698A803CB9C2B04010100048804F1FEF453";
                        user.nfc_id = "1698A858A115F60401010004880432E54BD9";
//                        user.nfc_id = "1698A803CB9C2B04010100048804C0E4718C"
                        user.status = 0;
                        user.created_time = "${
                            DateUtils.formatDateToString(
                                Date(),
                                DateUtils.format_yyyyMMddhhmmssfff
                            )
                        }+08:00".replace(" ", "T")
                        accountDao.insertAll(user)
                        var users = accountDao.getAll()
                        for (u in users) {
                            println(u)
                        }
                        loginStatus.postValue(true)
                        setAdminStatus.postValue(true)
                    } else {
//                        DbUtil.getDb().userDao().delete(adminUser)
                    }
                    //wait wait wait
//                    synchronizedAccount()//同步账户


//                    if (users.count() == 1) {
                    /*var keeper = AccountEntity();
                    keeper.id = 2;
                    keeper.user_id = SnowFlake.getInstance().nextId().toString();
                    keeper.km_user_id = SnowFlake.getInstance().nextId().toString();
                    keeper.real_name = "仓库管理员";
                    keeper.nick_name = "keeper";
                    keeper.role_id = "20";
                    keeper.password_digest = MD5Util.encrypt("Jinghe233");
                    keeper.status = 0;
                    keeper.created_time =
                        DateUtils.formatDateToString(Date(), DateUtils.format_yyyyMMddhhmmssfff)
                    accountDao.insertAll(keeper)

                    var scener = AccountEntity();
                   scener.id = 3;
                   scener.user_id = SnowFlake.getInstance().nextId().toString();
                   scener.km_user_id = SnowFlake.getInstance().nextId().toString();
                   scener.real_name = "现场人员";
                   scener.nick_name = "scener";
                   scener.role_id = "30";
                   scener.password_digest = MD5Util.encrypt("Jinghe233");
                   scener.status = 0;
                   scener.created_time =
                        DateUtils.formatDateToString(Date(), DateUtils.format_yyyyMMddhhmmssfff)
                    accountDao.insertAll(scener)*/
//                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }.start()
    }

    fun getAntPower() {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                showLoading("正在获取天线功率，请稍等...")
//                MyTcpServerListener.getInstance().getAntPower()
                RfidManage.getInstance().getOutputPower()
            } catch (e: Exception) {
                toast("获取天线功率异常${e.message}")
            } finally {
                hideLoading()
            }
        }
    }

    fun setAntPower(antPowerDaoList: List<AntPowerDao>) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                showLoading("正在设置天线功率，请稍等...")
//                MyTcpServerListener.getInstance().setAntPower(antPowerDaoList)
                RfidManage.getInstance().setOutputPower(antPowerDaoList)
                delay(1000)
            } catch (e: Exception) {
                toast("设置天线功率异常${e.message}")
            } finally {
                hideLoading()
                setAntPowerStatus.postValue(true)
            }
        }
    }

    fun openLock() {
//        MyTcpServerListener.getInstance().openLock()
        LockManage.getInstance().openLock()
    }

    fun closeLock() {
        LockManage.getInstance().closeLock()
    }

    fun openLamp() {
        //开灯
//        MyTcpServerListener.getInstance().sendOpenLamp()
    }

    fun closeLamp() {
        //关灯
//        MyTcpServerListener.getInstance().sendCloseLamp()
    }

    fun getOldInfo(requestUrl: String) {
        //
        Log.d("RetrofitClient", "requestUrl==${requestUrl}")
        synchronizedAccount(requestUrl)
        /*viewModelScope.launch(Dispatchers.Default) {
            try {
                showLoading("正在同步冰箱账号信息，请稍等...")
                synchronizedAccount(requestUrl)
                delay(2000)
            } catch (e: Exception) {
                toast("同步冰箱账号信息异常${e.message}")
            } finally {
                hideLoading()
            }
        }*/
    }

    val Target = "SettingViewModel"

    /**
     * 同步账户
     */
    private fun synchronizedAccount(requestUrl: String) {
        val accountDao = DbUtil.getDb().accountDao()
//        if (isNetAvailable()) {
        val users = accountDao.getAll();
        val uploadUsers = users.filter { user -> user.hasUpload == false }
        GlobalScope.launch(context = Dispatchers.IO) {
            var accountService = RetrofitClient.getService(baseUrl = requestUrl);
            //同步远端到本地账户
            for (user in uploadUsers) {
                if (user.status != 0) {
                    var response =
                        accountService.deleteAccount(user.user_id.toString())
                    if (response.code() == 200) {
                        Log.i(Target, user.nick_name + "---delete")
                        accountDao.delete(user)
                    }
                } else {
                    var response = accountService.addAccount(user)
                    if (response.code() == 200) {
                        //                                        Log.i("Application",user.nick_name+"---delete")
                        user.hasUpload = true
                        accountDao.update(user)
                    }
                }
            }
            var localUsers = accountDao.getAll();

            try {
                var accountResponse = accountService.getAccounts()
                Log.d("synchronizedAccount", "accountResponse=${accountResponse.body().toString()}")
                if (accountResponse.code() == 200) {
                    var remoteAccounts = accountResponse.body()?.results
                    if (remoteAccounts?.accounts != null) {
                        for (u in localUsers) {
                            if ("10".equals(u.role_id)) continue//系统管理员不做任何修改
                            var userLists =
                                remoteAccounts.accounts.filter { obj -> obj.user_id == u.user_id }
                            if (userLists == null && u.hasUpload) {
                                Log.i(Target, u.nick_name + "---delete")
                                accountDao.delete(u)//用户在远端被删除
                            } else {
                                for (ru in userLists) {
                                    ru.id = u.id;
                                    ru.status = u.status;
                                    ru.hasUpload = u.hasUpload;
                                }
                            }
                        }
                        //更新本地账户
                        localUsers = accountDao.getAll();
                        for (u in remoteAccounts.accounts) {
                            if (u.role_id == "10") continue;
                            val users = localUsers.filter { user -> user.user_id == u.user_id }
                            if (users != null && users.isNotEmpty()) continue;//存在的账户已经更新过
                            u.status = 0
                            u.hasUpload = true
                            accountDao.insertAll(u)
                            Log.i(Target, u.nick_name + "---sync")
                        }
//                        syncAccountSuccess.postValue(true)
                    }

                }
            } catch (e: Exception) {
                Log.e("synchronizedAccount", "${e}")
            }
        }

//        }
    }

    fun syncOtherSystem(sncode: String) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                showLoading("正在同步冰箱账号信息，请稍等...")
                Log.d("RetrofitClient", "正在同步冰箱账号信息==${sncode}")
                var service = RetrofitClient.getService(sncode = sncode)
                synchronousSystemOperatorErrors(service)
                synchronousOperatorErrors(service)

            } catch (e: Exception) {

            } finally {
                hideLoading()
            }
        }
        //
    }

    /**
     * 同步系统错误日志
     */
    private suspend fun synchronousSystemOperatorErrors(service: ILoginApiService) {
        try {
            val response = service.getSystemErrorLogs()
            if (response.code() == 200) {
                toast(response.body()?.results.toString())
                Log.i(Target, " 系统错误日志：${response.body()?.results.toString()}")
                //系统异常信息
                var sysOperationErrorDao = DbUtil.getDb().sysOperationErrorDao()
                var results = response.body()?.results;
                if (results?.logs != null) {
                    for (item in results.logs) {
                        var itemInfo = SysOperationErrorEntity(
                            item.id,
                            item.user.user_id,
                            item.user.nick_name,
                            item.user.role_id,
                            item.user.km_user_id,
                            item.user.real_name,
                            item.error_code,
                            item.network,
                            item.system_info,
                            item.app_version,
                            item.port_storage,
                            item.rf_storage,
                            item.log_at,
                            true
                        );
                        sysOperationErrorDao.insert(itemInfo)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(Target, e.message.toString());
//            toast("同步系统异常信息发生错误${e.message}")
        }
    }

    /**
     * 同步操作错误日志
     */
    private suspend fun synchronousOperatorErrors(service: ILoginApiService) {
        try {
            val response = service.getOperationErrorLogs()
            if (response.code() == 200) {
                toast(response.body()?.results.toString())
                Log.i(Target, " 操作错误日志：${response.body()?.results.toString()}")
                //操作异常信息
                var operationErrorDao = DbUtil.getDb().operationErrorLogDao()
                var results = response.body()?.results;
                if (results?.logs != null) {
                    for (item in results.logs) {
                        var itemInfo = OperationErrorLogEntity(
                            item.id.toLong(),
                            item.user.user_id,
                            item.user.role_id,
                            item.capture_item.remain,
                            item.capture_item.rfid,
                            item.error_code,
                            item.log_at,
                            true
                        );
                        operationErrorDao.insert(itemInfo)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(Target, e.message.toString());
//            toast("同步操作信息异常${e.message}")
        }
    }

    val loginStatus by lazy {
        SingleLiveEvent<Boolean>()
    }

    val setAntPowerStatus by lazy {
        SingleLiveEvent<Boolean>()
    }

    val setAdminStatus by lazy {
        SingleLiveEvent<Boolean>()
    }

    val activeIceBoxStatus by lazy {
        SingleLiveEvent<Boolean>()
    }
}
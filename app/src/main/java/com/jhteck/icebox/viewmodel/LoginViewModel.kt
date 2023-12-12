package com.jhteck.icebox.viewmodel

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import android.view.View
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.hele.mrd.app.lib.base.BaseApp
import com.hele.mrd.app.lib.base.BaseViewModel
import com.hele.mrd.app.lib.base.livedata.SingleLiveEvent
import com.jhteck.icebox.Lockmodel.LockManage
import com.jhteck.icebox.R
import com.jhteck.icebox.api.*
import com.jhteck.icebox.api.request.RequestRfidsDao
import com.jhteck.icebox.api.request.RfidSync
import com.jhteck.icebox.api.request.requestSync
import com.jhteck.icebox.apiserver.ILoginApiService
import com.jhteck.icebox.apiserver.LocalService
import com.jhteck.icebox.apiserver.RetrofitClient
import com.jhteck.icebox.bean.SystemOperationErrorEnum
import com.jhteck.icebox.broacast.AlarmReceiver
import com.jhteck.icebox.repository.entity.AccountEntity
import com.jhteck.icebox.repository.entity.OfflineRfidEntity
import com.jhteck.icebox.repository.entity.SysOperationErrorEntity
import com.jhteck.icebox.rfidmodel.RfidManage
import com.jhteck.icebox.service.CommandService
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
class LoginViewModel(application: android.app.Application) :
    BaseViewModel<ILoginApiService>(application) {
    private val TAG = "LoginViewModel"
    private val userDao = DbUtil.getDb().accountDao();
    fun login(username: String?, password: String?) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                showLoading(BaseApp.app.getString(R.string.login_tip))
                var userInfo = userDao.findByKmUserId(username)
                if (userInfo == null || userInfo.status != 0) {
                    toast("用户ID不存在")
                } else if (userInfo != null && !userInfo.password_digest.equals(
                        MD5Util.encrypt(
                            password
                        )
                    )
                ) {
                    toast("请检查密码")
                } else {
                    toast("登录成功")

                    //wait wait wait
                    if (!DEBUG) {
//                        MyTcpServerListener.getInstance().openLock()
                        LockManage.getInstance().preOpenLock();
                        LockManage.getInstance().openLock()
                    }
//                    delay(1000)
                    loginUserInfo.postValue(userInfo)
                    SharedPreferencesUtils.setPrefString(
                        ContextUtils.getApplicationContext(),
                        "loginUserInfo",
                        Gson().toJson(userInfo)
                    )
//                    loginOperator(userInfo)//记录登录信息
                }
            } catch (e: Exception) {
                toast(e.message)
//                toast(BaseApp.app.getString(R.string.login_tip_fail))
            } finally {
                hideLoading()
            }
        }
    }

    fun login_auto(username: String?, password: String?) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                delay(30 * 1000)
                if (SharedPreferencesUtils.getPrefBoolean(
                        BaseApp.app,
                        AUTO_LOGIN_STR,
                        AUTO_LOGIN
                    )
                ) {
                    login(username, password)
                }
            } catch (e: Exception) {
                toast(e.message)
            }
        }
    }

    private suspend fun loginOperator(accountEntity: AccountEntity) {
        try {
            var entity = SysOperationErrorEntity(
                SnowFlake.getInstance().nextId().toInt(),
                accountEntity.user_id,
                accountEntity.nick_name,
                accountEntity.role_id,
                accountEntity.km_user_id,
                accountEntity.real_name,
                SystemOperationErrorEnum.REBOOT.v,
                "网络状态",//todo 网络状态
                "系统信息",//todo
                "App版本",//todo
                "串口信息",//
                "冰箱信息",//
                DateUtils.currentStringFormatTime(),
                false
            );
            DbUtil.getDb().sysOperationErrorDao().insert(entity);//保存

            var logs = mutableListOf<SysOperationErrorEntity>()
            logs.add(entity);
            var rfidOperationBO = SysOperationErrorLogsBo(logs);

            var toJson = Gson().toJson(rfidOperationBO)
            Log.d(TAG, "${toJson}")
            var res = RetrofitClient.getService().addSystemErrorLogs(rfidOperationBO)
            if (res.code() == 200) {
                Log.d(TAG, "addSystemErrorLogs")
                for (data in rfidOperationBO.logs) {
                    data.hasUpload = true;
                    DbUtil.getDb().sysOperationErrorDao().update(data);
                }
            }
        } catch (e: Exception) {
            Log.i(TAG, "${e.message}")
        }

    }

    private var lastonclickTime = 0L;//全局变量
    fun loginByCark(myTcpMsg: String) {
        //防止刷卡响应过于频繁
        var time = SystemClock.uptimeMillis();//局部变量
        if (time - lastonclickTime <= 3000) {

        } else {
            lastonclickTime = time
            viewModelScope.launch(Dispatchers.Default) {
                try {
                    showLoading(BaseApp.app.getString(R.string.login_tip))
                    var userInfo = userDao.findByNfcId(myTcpMsg);
                    Log.d(TAG, userInfo.nfc_id)
                    if (userInfo == null) {
                        toast("${myTcpMsg} 未注册")
                        return@launch;
                    }

                    //wait wait wait
                    if (!DEBUG) {
//                        MyTcpServerListener.getInstance().openLock()
                        LockManage.getInstance().preOpenLock();
                        LockManage.getInstance().openLock()
                    }
//                    delay(1000)
                    loginUserInfo.postValue(userInfo)
                    SharedPreferencesUtils.setPrefString(
                        ContextUtils.getApplicationContext(),
                        "loginUserInfo",
                        Gson().toJson(userInfo)
                    )
//                    loginOperator(userInfo)//记录登录信息
//                    cardStatus.postValue(true)
                } catch (e: Exception) {
                    Log.e(TAG, e.toString())
                    toast(BaseApp.app.getString(R.string.login_tip_hfc_fail))
                } finally {
                    hideLoading()
                }
            }
        }
    }

    /**
     * 人脸登录接口
     */
    fun loginByFace(faceUrl: String) {
        //防止刷卡响应过于频繁
        var time = SystemClock.uptimeMillis();//局部变量
        if (time - lastonclickTime <= 8000) {

        } else {
            lastonclickTime = time
            viewModelScope.launch(Dispatchers.Default) {
                try {
                    showLoading(BaseApp.app.getString(R.string.login_tip))
                    var faceAccountEntity = DbUtil.getDb().faceAccountDao().getByFaceUrl(faceUrl)
                    if (faceAccountEntity == null) {
                        toast("人脸未注册")
                        return@launch
                    }
                    var userInfo = userDao.findById(faceAccountEntity.user_id);
                    Log.d(TAG, userInfo.nfc_id)
                    if (userInfo == null) {
                        toast("用户未注册")
                        return@launch;
                    }
                    //wait wait wait
                    if (!DEBUG) {
//                        MyTcpServerListener.getInstance().openLock()
                        LockManage.getInstance().preOpenLock();
                        LockManage.getInstance().openLock()
                    }
//                    delay(1000)
                    toast("人脸登录成功")
                    loginUserInfo.postValue(userInfo)
                    SharedPreferencesUtils.setPrefString(
                        ContextUtils.getApplicationContext(),
                        "loginUserInfo",
                        Gson().toJson(userInfo)
                    )
//                    loginOperator(userInfo)//记录登录信息
//                    cardStatus.postValue(true)
                } catch (e: Exception) {
                    Log.e(TAG, e.toString())
                    toast(BaseApp.app.getString(R.string.login_tip_hfc_fail))
                } finally {
                    hideLoading()
                }
            }
        }
    }

    private var testHFCardList = mutableListOf<String>()
    fun initHFCradList() {
        testHFCardList.add("1698A858A115F60401010004880432E54BD9")
        testHFCardList.add("1698A858A115F604010100048804C0E42E5B")
        testHFCardList.add("1698A858A115F6040101000488043254FC23")
        testHFCardList.add("1698A858A115F60401010004880432CDEEB3")
    }


    override fun createApiServiceType(): Class<ILoginApiService> {
        return ILoginApiService::class.java
    }

    fun mockDataToLocal() {
        viewModelScope.launch(Dispatchers.Default) {
            var gson = Gson();
            LocalService.mockDataToLocal(gson);
        }
    }

    fun loadRfidsFromLocal() {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                var gson = Gson();
                var datas = LocalService.loadRfidsFromLocal(gson);
                rfidDatas.postValue(datas);
            } catch (e: Exception) {
            }
        }
    }

    //离线数据
    fun loadOfflineRfidsFromLocal() {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                var offlineDate = DbUtil.getDb().offlineRfidDao().getAll()
                rfidOfflineDatas.postValue(offlineDate);
            } catch (e: Exception) {
            }
        }
    }

    //新冰箱第一次激活，把同步下来的账号更新到平台上
    fun synchronizedAccountToServer() {
        val accountDao = DbUtil.getDb().accountDao()
//        if (isNetAvailable()) {
        val users = accountDao.getAll();
        val sncode = SharedPreferencesUtils.getPrefString(
            BaseApp.app,
            SNCODE, SNCODE_TEST
        )
        Log.d(TAG, "sncode：${sncode}")
        GlobalScope.launch(context = Dispatchers.IO) {
            val accountService = RetrofitClient.getService(sncode = sncode);
            //同步远端到本地账户
            for (user in users) {
                if (user.status != 0) {
                    var response =
                        accountService.deleteAccount(user.user_id.toString())
                    if (response.code() == 200) {
                        Log.i(TAG, user.nick_name + "---delete")
                        accountDao.delete(user)
                    }
                } else {

                    var response = accountService.addAccount(user)

                    Log.i(TAG, user.nick_name + "---user=${user.toString()}")
                    Log.i(TAG, user.nick_name + "---add response=${response.code()}")
                    if (response.code() == 200) {
                        user.hasUpload = true
                        accountDao.update(user)
                    }
                    delay(100)
                }
            }
            var localUsers = accountDao.getAll();

            try {
                var accountResponse = accountService.getAccounts()
                Log.d(TAG, "accountResponse=${accountResponse.body()?.results}")
                if (accountResponse.code() == 200) {
                    var remoteAccounts = accountResponse.body()?.results
                    if (remoteAccounts?.accounts != null) {
                        for (u in localUsers) {
                            if ("10".equals(u.role_id)) continue//系统管理员不做任何修改
                            var userLists =
                                remoteAccounts.accounts.filter { obj -> obj.user_id == u.user_id }
                            if (userLists == null && u.hasUpload) {
                                Log.i(TAG, u.nick_name + "---delete")
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
                            Log.i(TAG, u.nick_name + "---sync")
                        }
                    }

                }
            } catch (e: Exception) {
                Log.e(TAG, "${e}")
            }
        }

    }

    fun sendCommAt4Clock(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager?
        val intent = Intent(context, CommandService::class.java)
        val pendingIntent = PendingIntent.getService(context, 0, intent, 0)

        // 设置闹钟在凌晨4点触发

        // 设置闹钟在凌晨4点触发
        val calendar: Calendar = Calendar.getInstance()
        calendar.setTimeInMillis(System.currentTimeMillis())
        calendar.set(Calendar.HOUR_OF_DAY, 20)
        calendar.set(Calendar.MINUTE, 25)

        // 如果当前时间已经过了今天的凌晨4点，那么设置的闹钟将在明天的凌晨4点触发
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1)
        }
        alarmManager!!.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent)

    }

    fun restartAppAt6Am(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager?
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)

        // 设置闹钟在每周6的六点触发

        // 设置闹钟在每周6的六点触发
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar[Calendar.DAY_OF_WEEK] = Calendar.SATURDAY
        calendar[Calendar.HOUR_OF_DAY] = 6
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0

        // 如果当前时间已经过了本周的星期六的六点，那么设置的闹钟将在下周的星期六的六点触发
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.WEEK_OF_YEAR, 1)
        }
        // 设置闹钟，使用 setRepeating 方法每周重复
        alarmManager!!.setRepeating(
            AlarmManager.RTC_WAKEUP, calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY * 7, pendingIntent
        )

    }

    fun startAutoTest(context: Context) {
        val isAutoLogin = SharedPreferencesUtils.getPrefBoolean(
            context,
            AUTO_LOGIN_STR,
            AUTO_LOGIN
        )
        val showMessageText = if (isAutoLogin) {
            "是否关闭老化测试"
        } else {
            "是否开启老化测试"
        }
        val customDialog = CustomDialog(context)
        customDialog.setsTitle("温馨提示").setsMessage(showMessageText)
            .setsCancel("取消", View.OnClickListener {
                customDialog.dismiss()
            }).setsConfirm("确定", View.OnClickListener {
                SharedPreferencesUtils.setPrefBoolean(
                    context,
                    AUTO_LOGIN_STR,
                    !isAutoLogin
                )
                customDialog.dismiss()
                startAutoTestStatus.postValue(true)
            }).show()
    }

    //在后台无人操作的时候30分钟扫描一次
    private var countMinutes: Int = 0
    private var scanThread: Thread? = null
    fun ListeningOperation() {
        countMinutes = 30
        if (scanThread == null) {
            synchronized(this) {
                if (scanThread == null) {
                    scanThread = Thread {
                        while (countMinutes > 0) {
                            try {
                                countMinutes--
                                Log.e(TAG, "无操作开始扫描: " + countMinutes)
                                Thread.sleep(60 * 1000)
                            } catch (e: java.lang.Exception) {
                                Log.e(TAG, "无操作开始扫描: " + e.message)
                            }
                        }
                        scanThread = null
                        Log.i(TAG, "无操作开始扫描1")
                        scanBehind()
                    }
                    scanThread!!.start()
                }
            }
        }
    }

    private fun scanBehind() {
        viewModelScope.launch {
            try {
                Log.d(TAG, "开始扫描}")
                RfidManage.getInstance().startStop(true, false)
                RfidManage.getInstance().setRfidArraysRendEndCallback {
                    Log.d(TAG, "扫描结果...${it.toString()}")
                    getRfidsInfo(it.toList())
                }
                delay(60 * 1000)
                RfidManage.getInstance().startStop(false, false)
            } catch (e: Exception) {
                Log.e(TAG, "扫描异常: " + e.message)
            } finally {
                RfidManage.getInstance().setRfidArraysRendEndCallback();//清空掉
            }
        }
    }

    private fun getRfidsInfo(rfids: List<String>) {
        Log.d(TAG, "正在查询RFID===--->${rfids.toString()}")
        viewModelScope.launch {
            try {
//                showLoading("正在查询RFID，请稍等...")
                //获取到之后跟本地数据对比(增加或减少了什么)
                val tempList = mutableListOf<String>()
                val outList = mutableListOf<AvailRfid>()//领出列表
                val inList = mutableListOf<AvailRfid>()//存入列表

                val body = genBody(RequestRfidsDao(rfids))
                val rep = RetrofitClient.getService().getRfids(body)
                if (rep.code() == 200) {
                    val localAvailRfid = LocalService.loadRfidsFromLocal(Gson()).avail_rfids
                    val getAvailRfid = rep.body()!!.results.avail_rfids

                    if (getAvailRfid != null && getAvailRfid.isNotEmpty()) {
                        GlobalScope.launch {
                            try {
                                var localDatas = DbUtil.getDb().availRfidDao().getAll()
                                val sncode = SharedPreferencesUtils.getPrefString(
                                    BaseApp.app, SNCODE,
                                    SNCODE_TEST
                                )
                                for (rfid in getAvailRfid) {
                                    if (rfid.cell_number > 1) {
                                        if (rfid.fridge_id != null && rfid.fridge_id != sncode) {
                                            continue// 跳过
                                        }
                                        var res =
                                            localDatas.stream()
                                                .filter { obj -> obj.rfid == rfid.rfid }
                                                .findFirst().orElse(null);
                                        if (res != null && res.cell_number != rfid.cell_number) {
                                            res.cell_number = rfid.cell_number
                                            DbUtil.getDb().availRfidDao().update(res)//更新宫格
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e(TAG, e.toString())
                            } finally {

                            }
                        }
                    }
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
                    if (outList.size > 0 && inList.size > 0) {//存取都有
                        deleteByRfid(outList)
                        addByRfid(inList)
                    } else if (outList.size > 0) {//领出
                        deleteByRfid(outList)
                    } else if (inList.size > 0) {//领入
                        addByRfid(inList)
                    }
                    rfidsSync(getAvailRfid)
                }
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }
    }

    private fun deleteByRfid(list: List<AvailRfid>) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                LocalService.deleteDataToLocal(list);
                Log.d(TAG, "保存到本地成功")
            } catch (e: Exception) {
                Log.e(TAG, "保存失败3${e.message}")
            }
        }
    }

    private fun addByRfid(list: List<AvailRfid>) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                LocalService.addDataToLocal(list);
                Log.d(TAG, "保存到本地成功")
            } catch (e: Exception) {
                Log.e(TAG, "保存失败1${e.message}")
            }
        }
    }

    private fun rfidsSync(rfids: List<AvailRfid>) {
        viewModelScope.launch {
            try {
                //全量上报
                val rfidList = mutableListOf<RfidSync>()
                for (rfid in rfids) {
                    rfidList.add(RfidSync(rfid.cell_number, rfid.remain, rfid.rfid))
                }
                val bodySync = genBody(requestSync(rfidList))
                Log.d(TAG, "全量上报成功 rfidList ${rfidList}")
                val repSync = RetrofitClient.getService().syncRfids(bodySync)
                if (repSync.code() == 200) {
                    Log.d(TAG, "全量上报成功")
                }
            } catch (e: Exception) {
                Log.e(TAG, "全量上报异常$e")
            } finally {
                DbUtil.getDb().offlineRfidDao().clean()
                loadRfidsFromLocal()
            }
        }
    }


    val loginUserInfo by lazy {
        SingleLiveEvent<AccountEntity>()
    }
    val rfidDatas by lazy {
        SingleLiveEvent<RfidResults>()
    }
    val rfidOfflineDatas by lazy {
        SingleLiveEvent<List<OfflineRfidEntity>>()
    }
    val loginStatus by lazy {
        SingleLiveEvent<Boolean>()
    }

    val startAutoTestStatus by lazy {
        SingleLiveEvent<Boolean>()
    }

}
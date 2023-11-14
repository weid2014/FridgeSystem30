package com.jhteck.icebox.viewmodel

import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.hele.mrd.app.lib.base.BaseApp
import com.hele.mrd.app.lib.base.BaseViewModel
import com.hele.mrd.app.lib.base.livedata.SingleLiveEvent
import com.jhteck.icebox.Lockmodel.LockManage
import com.jhteck.icebox.R
import com.jhteck.icebox.api.*
import com.jhteck.icebox.api.request.RequestRfidsDao
import com.jhteck.icebox.apiserver.ILoginApiService
import com.jhteck.icebox.apiserver.LocalService
import com.jhteck.icebox.apiserver.RetrofitClient
import com.jhteck.icebox.bean.SystemOperationErrorEnum
import com.jhteck.icebox.repository.entity.AccountEntity
import com.jhteck.icebox.repository.entity.OfflineRfidEntity
import com.jhteck.icebox.repository.entity.SysOperationErrorEntity
import com.jhteck.icebox.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
                var userInfo = userDao.findByName(username)
                if (userInfo == null || userInfo.status != 0) {
                    toast("用户不存在")
                } else if (userInfo != null && !userInfo.password_digest.equals(
                        MD5Util.encrypt(
                            password
                        )
                    )
                ) {
                    toast("请检查密码")
                } else {
                    toast("登录成功")

//                    delay(300)
//                    loginStatus.postValue(true)
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
                    loginOperator(userInfo)//记录登录信息
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
                delay(30*1000)
                if (SharedPreferencesUtils.getPrefBoolean(BaseApp.app, AUTO_LOGIN_STR, AUTO_LOGIN)){
                login(username, password)}
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
                    loginOperator(userInfo)//记录登录信息
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
                    loginOperator(userInfo)//记录登录信息
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

    fun loginTest() {
        //退出
        viewModelScope.launch {
            try {
                showLoading(BaseApp.app.getString(R.string.login_tip))
                val testFrid = "1698A858A115F60401010004880432E54BD9"
                val userInfo = userDao.findByNfcId(testFrid);
                if (userInfo == null) {
                    toast("${testFrid} 未注册")
                    return@launch;
                }

                delay(1000)
                loginStatus.postValue(true)
                cardStatus.postValue(true)

            } catch (e: Exception) {
                toast(BaseApp.app.getString(R.string.login_tip_hfc_fail))
            } finally {
                hideLoading()
            }
        }
    }

    fun getdataTest() {
        //测试接口
        var rfids = mutableListOf<String>()
        rfids.add("E1234567000000000000030B")
        //        testRfidList.add("E1234567000000000000004C")
        rfids.add("E1234567000000000000030A")
        rfids.add("E12345670000000000000309")
        viewModelScope.launch {
            try {
                val body = genBody(RequestRfidsDao(rfids))
                val rep = apiService.getRfids(body)
                toast(rep.body()?.results.toString())
            } catch (e: Exception) {
                toast(BaseApp.app.getString(R.string.login_tip_hfc_fail))
            } finally {
                hideLoading()
            }
        }
    }

    fun getUserListTest() {
        //测试接口
        var rfids = mutableListOf<String>()
        rfids.add("E1234567000000000000030B")
        //        testRfidList.add("E1234567000000000000004C")
        rfids.add("E1234567000000000000030A")
        rfids.add("E12345670000000000000309")
        viewModelScope.launch {
            try {
                val rep = apiService.getAccounts()
                toast(rep.body()?.results.toString())
            } catch (e: Exception) {
                toast(BaseApp.app.getString(R.string.login_tip_hfc_fail))
            } finally {
                hideLoading()
            }
        }
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
    val cardStatus by lazy {
        SingleLiveEvent<Boolean>()
    }

    val networkStatus by lazy {
        SingleLiveEvent<Boolean>()
    }

}
package com.jhteck.icebox.viewmodel

import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.arcsoft.face.ActiveFileInfo
import com.arcsoft.face.ErrorInfo
import com.arcsoft.face.FaceEngine
import com.google.gson.Gson
import com.hele.mrd.app.lib.base.BaseApp
import com.hele.mrd.app.lib.base.BaseViewModel
import com.hele.mrd.app.lib.base.livedata.SingleLiveEvent
import com.jhteck.icebox.Lockmodel.LockManage
import com.jhteck.icebox.R
import com.jhteck.icebox.api.APP_ID
import com.jhteck.icebox.api.DEBUG
import com.jhteck.icebox.api.RfidResults
import com.jhteck.icebox.api.SDK_KEY
import com.jhteck.icebox.api.request.RequestRfidsDao
import com.jhteck.icebox.apiserver.ILoginApiService
import com.jhteck.icebox.apiserver.LocalService
import com.jhteck.icebox.repository.entity.AccountEntity
import com.jhteck.icebox.repository.entity.FaceAccountEntity
import com.jhteck.icebox.utils.*
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
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
                delay(200)
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

                    delay(300)
//                    loginStatus.postValue(true)
                    //wait wait wait
                    if (!DEBUG) {
//                        MyTcpServerListener.getInstance().openLock()
                        LockManage.getInstance().preOpenLock();
                        LockManage.getInstance().openLock()
                    }
                    loginUserInfo.postValue(userInfo)
                    SharedPreferencesUtils.setPrefString(
                        ContextUtils.getApplicationContext(),
                        "loginUserInfo",
                        Gson().toJson(userInfo)
                    )
                }
            } catch (e: Exception) {
                toast(e.message)
//                toast(BaseApp.app.getString(R.string.login_tip_fail))
            } finally {
                hideLoading()
            }
        }
    }

    private var lastonclickTime = 0L;//全局变量
    fun loginByCark(myTcpMsg: String) {
        //防止刷卡响应过于频繁
        var time = SystemClock.uptimeMillis();//局部变量
        if (time - lastonclickTime <= 8000) {

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

    /**
     * 激活引擎
     *
     * @param view
     */
    fun activeEngine() {

        Observable.create<Int> { emitter ->
            val runtimeABI = FaceEngine.getRuntimeABI()

            val start = System.currentTimeMillis()
            val activeCode = FaceEngine.activeOnline(
                BaseApp.app,
                APP_ID,
                SDK_KEY
            )
            emitter.onNext(activeCode)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Int> {
                override fun onSubscribe(d: Disposable) {}
                override fun onNext(activeCode: Int) {
                    if (activeCode == ErrorInfo.MOK) {
//                            showToast(getString(R.string.active_success))
                        Log.d("activeEngine", "active success")
                    } else if (activeCode == ErrorInfo.MERR_ASF_ALREADY_ACTIVATED) {
//                            showToast(getString(R.string.already_activated))
                        Log.d("activeEngine", "already activated")
                    } else {
//                            showToast(getString(R.string.active_failed, activeCode))
                        Log.d("activeEngine", "active failed")
                    }
                    val activeFileInfo = ActiveFileInfo()
                    val res = FaceEngine.getActiveFileInfo(
                        BaseApp.app,
                        activeFileInfo
                    )
                    if (res == ErrorInfo.MOK) {
                        /*Log.i(
                            com.arcsoft.arcfacedemo.activity.ChooseFunctionActivity.TAG,
                            activeFileInfo.toString()
                        )*/
                    }
                }

                override fun onError(e: Throwable) {
                    Log.d("activeEngine", e.message.toString())

                }

                override fun onComplete() {}
            })
    }


    val loginUserInfo by lazy {
        SingleLiveEvent<AccountEntity>()
    }
    val rfidDatas by lazy {
        SingleLiveEvent<RfidResults>()
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
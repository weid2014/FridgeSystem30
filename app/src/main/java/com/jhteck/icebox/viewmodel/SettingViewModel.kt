package com.jhteck.icebox.viewmodel

import android.app.Application
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
import com.jhteck.icebox.repository.entity.FridgesInfoEntity
import com.jhteck.icebox.rfidmodel.RfidManage
import com.jhteck.icebox.tcpServer.MyTcpServerListener
import com.jhteck.icebox.utils.ContextUtils
import com.jhteck.icebox.utils.DbUtil
import com.jhteck.icebox.utils.NetworkUtil
import com.jhteck.icebox.utils.SharedPreferencesUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 *@Description:(操作记录ViewModel)
 *@author wade
 *@date 2023/6/28 22:04
 */
class SettingViewModel(application: Application) : BaseViewModel<ILoginApiService>(application) {

    val Target = "SettingViewModel"
    override fun createApiServiceType(): Class<ILoginApiService> {
        return ILoginApiService::class.java
    }

    fun openLock() {
//        MyTcpServerListener.getInstance().openLock()
        LockManage.getInstance().openLock()
    }

    fun closeLock(){
        LockManage.getInstance().closeLock()
    }

    fun openLamp() {
        //开灯
//        LightHelp().Test()
//        LightHelp.testStatic()
//        MyTcpServerListener.getInstance().sendOpenLamp()
    }

    fun closeLamp() {
        //关灯
//        MyTcpServerListener.getInstance().sendCloseLamp()
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

    fun updateFridgesInfo(fridgesActiveBo: FridgesActiveBo) {
        var time = SystemClock.uptimeMillis();//局部变量
        if (time - lastonclickTime <= 3000) {
        } else {
            lastonclickTime = time
            viewModelScope.launch(Dispatchers.Default) {
                try {
                    showLoading("正在保存冰箱信息，请稍等...")
                    val response = RetrofitClient.getService().updatefridgesInfo(fridgesActiveBo);
                    if (response.code() == 200) {
                        toast("编辑冰箱成功")
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

                    } else {
                        toast("编辑冰箱异常${response.message()}")
                    }
                } catch (e: Exception) {
                    toast("编辑冰箱异常${e.message}")
                } finally {
                    hideLoading()
                }
            }
        }
    }

    fun getFridgesInfo() {
        var time = SystemClock.uptimeMillis();//局部变量
        if (time - lastonclickTime <= 3000) {
        } else {
            lastonclickTime = time
            viewModelScope.launch(Dispatchers.Default) {
                try {
                    showLoading("正在获取冰箱信息，请稍等...")
                    val response = RetrofitClient.getService().fridgesInfo()
                    if (response.code() == 200) {
                        toast("获取冰箱信息成功")
                        fridgesActiveResultBo.postValue(response.body()?.results)
                    } else {
                        toast("获取冰箱信息异常${response.message()}")
                    }
                } catch (e: Exception) {
                    toast("获取冰箱信息异常${e.message}")
                } finally {
                    hideLoading()
                }
            }
        }
    }
    //同步账号
    fun syncAccount() {
        var time = SystemClock.uptimeMillis();//局部变量
        if (time - lastonclickTime <= 3000) {
        } else {
            lastonclickTime = time
            viewModelScope.launch(Dispatchers.Default) {
                try {
                    showLoading("正在同步冰箱账号信息，请稍等...")
                    synchronizedAccount()
                    delay(2000)
                } catch (e: Exception) {
                    toast("同步冰箱账号信息异常${e.message}")
                } finally {
                    hideLoading()
                }
            }
        }
    }

    fun getAntPower() {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                showLoading("正在获取天线功率，请稍等...")
                RfidManage.getInstance().getOutputPower()
//                MyTcpServerListener.getInstance().getAntPower()
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
                RfidManage.getInstance().setOutputPower(antPowerDaoList)
//                MyTcpServerListener.getInstance().setAntPower(antPowerDaoList)
            } catch (e: Exception) {
                toast("设置天线功率异常${e.message}")
            } finally {
                hideLoading()
            }
        }
    }

    /**
     * 同步账户
     */
    private fun synchronizedAccount() {
        val accountDao = DbUtil.getDb().accountDao()
//        if (isNetAvailable()) {
            val users = accountDao.getAll();
            val uploadUsers = users.filter { user -> user.hasUpload == false }
            GlobalScope.launch(context = Dispatchers.IO) {
                var accountService = RetrofitClient.getService();
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
                   Log.d("synchronizedAccount","accountResponse=${accountResponse.body().toString()}")
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
                               if (u.role_id=="10")continue;
                               val users = localUsers.filter { user -> user.user_id == u.user_id }
                               if (users != null && users.isNotEmpty()) continue;//存在的账户已经更新过
                               u.status = 0
                               u.hasUpload = true
                               accountDao.insertAll(u)
                               Log.i(Target, u.nick_name + "---sync")
                           }
                           syncAccountSuccess.postValue(true)
                       }

                   }
               }catch (e:Exception){
                   Log.e("synchronizedAccount","${e}")
               }
            }

//        }
    }

    private fun isNetAvailable(): Boolean {
        return !NetworkUtil.isNetSystemUsable(ContextUtils.getApplicationContext()) || !NetworkUtil.isNetOnline()
    }

    val fridgesActiveResultBo by lazy {
        SingleLiveEvent<FridgesInfoEntity>()
    }

    val syncAccountSuccess by lazy {
        SingleLiveEvent<Boolean>()
    }

}
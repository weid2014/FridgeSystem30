package com.jhteck.icebox.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.hele.mrd.app.lib.base.BaseViewModel
import com.hele.mrd.app.lib.base.livedata.SingleLiveEvent
import com.jhteck.icebox.api.UserResults
import com.jhteck.icebox.api.requestUser
import com.jhteck.icebox.apiserver.IAccountService
import com.jhteck.icebox.apiserver.ILoginApiService
import com.jhteck.icebox.repository.entity.AccountEntity

import com.jhteck.icebox.utils.DbUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UpgradesViewModel(application: android.app.Application) :
    BaseViewModel<ILoginApiService>(application) {

    val userDao = DbUtil.getDb().accountDao();

    /**
     * 新增用户
     */
    fun add(user: AccountEntity) {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                showLoading("正在新增账户，请稍等...")
                //更新用户到平台
//                val response = apiService.addAccount(body);
                val response=apiService.getAccounts()

                toast("$response")
            } catch (e: Exception) {
                toast("$e")
                Log.e("api",e.toString())
            } finally {
                hideLoading()
            }
        }
    }

    /**
     * 更新用户
     */
    fun update(user: AccountEntity) {
        viewModelScope.launch(Dispatchers.Default) {
            userDao.update(user)
//            apiService.updateAccount(user);
            getAllUsers();
        }
    }

    /**
     * 删除用户
     */
    fun delete(user: AccountEntity) {
        viewModelScope.launch(Dispatchers.Default) {
            userDao.delete(user)
            getAllUsers();
//            apiService.deleteAccount(user.userId);//上传 ->需要考虑上传失败的
        }
    }

    /**
     * 获取所有用户
     */
    fun getAllUsers() {
        viewModelScope.launch(Dispatchers.Default) {
            var userResults = userDao.getAll();

            onUsersLoaded.postValue(userResults)
        }
    }

    /**
     * 用户数据
     */
    val onUsersLoaded by lazy {
        SingleLiveEvent<List<AccountEntity>>()
    }
    val onFailed by lazy {
        SingleLiveEvent<String>()
    }
}
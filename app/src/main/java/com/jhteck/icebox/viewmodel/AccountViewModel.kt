package com.jhteck.icebox.viewmodel

import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.hele.mrd.app.lib.base.BaseApp
import com.hele.mrd.app.lib.base.BaseViewModel
import com.hele.mrd.app.lib.base.livedata.SingleLiveEvent
import com.jhteck.icebox.api.ROLE_ID
import com.jhteck.icebox.apiserver.IAccountService
import com.jhteck.icebox.apiserver.RetrofitClient
import com.jhteck.icebox.bean.ExceptionRecordBean
import com.jhteck.icebox.bean.OperationErrorEnum
import com.jhteck.icebox.repository.entity.AccountEntity
import com.jhteck.icebox.repository.entity.FaceAccountEntity
import com.jhteck.icebox.repository.entity.RfidOperationEntity
import com.jhteck.icebox.repository.entity.SysOperationErrorEntity
import com.jhteck.icebox.utils.ContextUtils
import com.jhteck.icebox.utils.DbUtil
import com.jhteck.icebox.utils.SharedPreferencesUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.stream.Collectors

class AccountViewModel(application: android.app.Application) :
    BaseViewModel<IAccountService>(application) {

    val userDao = DbUtil.getDb().accountDao();

    /**
     * 新增用户
     */
    /*fun add(user: AccountEntity, faceUrl: String? = null) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                showLoading("正在新增账户，请稍等...")

                var userEntity = userDao.findByKmUserId(user.km_user_id)
                if (userEntity != null) {
                    onFailed.postValue("UserId ${user.km_user_id} 已经存在")
                    return@launch
                }

                userEntity = userDao.findByName(user.nick_name)
                if (userEntity != null) {
                    onFailed.postValue("${user.nick_name} 用户名已经存在")
                    return@launch
                }
                user.status = 0
                userDao.insertAll(user)
                userEntity = userDao.findByName(user.nick_name)
                //人脸信息
                if (faceUrl != null) {
                    var faceAccountEntity = FaceAccountEntity(null, userEntity.user_id, faceUrl)
                    DbUtil.getDb().faceAccountDao().insert(faceAccountEntity)
                }
                getAllUsers();
                //更新用户到平台
                val response = RetrofitClient.getService().addAccount(userEntity);
                if (response.code() == 200) {
                    toast("平台成功新增账户")
                    user.hasUpload = true
                    userDao.update(user)
                    addStatus.postValue(true)
                } else {
                    toast("平台新增账户异常${response.message()}")
                    addStatus.postValue(false)
                }
            } catch (e: Exception) {
                toast("新增账户异常${e.message}")
                addStatus.postValue(false)
            } finally {
                hideLoading()
            }
        }
    }*/

    /**
     * 更新用户
     */
    fun update(user: AccountEntity, faceUrl: String? = null) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                showLoading("正在更新账户，请稍等...")
                user.hasUpload = false
                userDao.update(user)
                if (faceUrl != null) {
                    if (user.faceAccount == null || user.faceAccount.size == 0) {
                        var faceAccountEntity = FaceAccountEntity(null, user.user_id, faceUrl)
                        DbUtil.getDb().faceAccountDao().insert(faceAccountEntity)
                    } else {

                        var faceAcount = user.faceAccount.get(0)
                        faceAcount.faceUrl = faceUrl
                        faceAcount.createTime = System.currentTimeMillis()
                        DbUtil.getDb().faceAccountDao().update(faceAcount)
                    }
                }
                getAllUsers();
                val response = RetrofitClient.getService().updateAccount(user.id,user);
                if (response.code() == 200) {
                    toast("平台成功更新账户")
                    user.hasUpload = true
                    userDao.update(user)
                    addStatus.postValue(true)
                    updateUserInfo.postValue(user)
                } else {
                    toast("更新账户异常${response.message()}")
                    addStatus.postValue(false)
                }
            } catch (e: Exception) {
                toast("更新账户异常${e.message}")
                addStatus.postValue(false)
            } finally {
                hideLoading()
            }
        }
    }

    /**
     * 删除用户
     */
    fun delete(user: AccountEntity) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                showLoading("正在删除账户，请稍等...")
                user.hasUpload = false
                user.status = -1//删除状态
                userDao.update(user)
                if (user.faceAccount != null && user.faceAccount.size > 0) {
                    for (faceAccount in user.faceAccount) {
                        faceAccount.status = -1;
                        DbUtil.getDb().faceAccountDao().update(faceAccount)
                    }
                }

                getAllUsers();
                val response =
                    RetrofitClient.getService().deleteAccount(user.user_id);//上传 ->需要考虑上传失败的
                if (response.code() == 200) {
                    toast("删除账户成功")
                    userDao.delete(user)
                } else {
                    toast("删除账户异常${response.message()}")
                }
            } catch (e: Exception) {
                toast("删除账户异常${e.message}")
            } finally {
                hideLoading()
            }
        }
    }

    /**
     * 获取所有用户
     */
    fun getAllUsers() {
        viewModelScope.launch(Dispatchers.Default) {
            var userResults = userDao.getAll()
//            var userResults = userDao.getAll().filter { usr -> usr.status == 0 };
            for (user in userResults) {
                var faceAcountEntites =
                    DbUtil.getDb().faceAccountDao().getByFaceByUserId(user.user_number)
                if (faceAcountEntites != null && faceAcountEntites.size > 1) {
                    faceAcountEntites = faceAcountEntites.stream()
//                        .sorted(Comparator.comparing(FaceAccountEntity::createTime).reversed())
                        .collect(Collectors.toList());
                }
                user.faceAccount = faceAcountEntites;
            }
            onUsersLoaded.postValue(userResults)
        }
    }

    /**
     * 操作日志
     */
    fun getAllOperationLogs() {
        viewModelScope.launch(Dispatchers.Default) {
            var results = DbUtil.getDb().rfidOperationDao().getAll();
            val userInfoString = SharedPreferencesUtils.getPrefString(
                ContextUtils.getApplicationContext(),
                "loginUserInfo",
                null
            )

            var accountEntity = Gson().fromJson(userInfoString, AccountEntity::class.java)
            if (accountEntity != null && accountEntity.role != 10) {
                results =
                    results.filter { t -> t.role_id > accountEntity.role || t.user_id == accountEntity.user_id }
                        .map { t -> t }
            }
            onOperationsLoaded.postValue(results)
        }
    }

    fun getAllOperationErrorLogs() {
        viewModelScope.launch(Dispatchers.Default) {


            val errors = DbUtil.getDb().operationErrorLogDao().getAll()
            if (errors != null && errors.isNotEmpty()) {
                var ids =
                    errors.stream().map { i -> i.id }.collect(Collectors.toList()).toLongArray()

                var results = DbUtil.getDb().rfidOperationDao().getByIds(ids);
                if (results != null && results.isNotEmpty()) {

                    val userInfoString = SharedPreferencesUtils.getPrefString(
                        ContextUtils.getApplicationContext(),
                        "loginUserInfo",
                        null
                    )

                    var accountEntity = Gson().fromJson(userInfoString, AccountEntity::class.java)
                    if (accountEntity != null && accountEntity.role != 10) {
                        results =
                            results.filter { t -> t.role_id > accountEntity.role || t.user_id == accountEntity.user_id }
                                .map { t -> t }
                    }

                    var errorsResults = mutableListOf<ExceptionRecordBean>();
                    val gson = Gson();
                    for (r in results) {
                        var exceptionRecordBean = gson.fromJson<ExceptionRecordBean>(
                            gson.toJson(r),
                            ExceptionRecordBean::class.java
                        )

                        var error = errors.filter { e -> e.id == r.id }.first()
                        exceptionRecordBean.error_code = error.error_code
                        exceptionRecordBean.remain = error.remain
                        exceptionRecordBean.error_code_desc =
                            OperationErrorEnum.getEnumByV(error.error_code).desc
                        errorsResults.add(exceptionRecordBean)
                    }
                    onExceptionRecordLoad.postValue(errorsResults)
                }

            }


        }
    }

    /**
     * 用户数据
     */
    val onUsersLoaded by lazy {
        SingleLiveEvent<List<AccountEntity>>()
    }

    val onOperationsLoaded by lazy {
        SingleLiveEvent<List<RfidOperationEntity>>()
    }

    /**
     * 操作异常记录
     */
    val onExceptionRecordLoad by lazy {
        SingleLiveEvent<List<ExceptionRecordBean>>()
    }

    /**
     * 操作异常记录
     */
    val onSystemExceptionRecordLoad by lazy {
        SingleLiveEvent<List<SysOperationErrorEntity>>()
    }

    val onFailed by lazy {
        SingleLiveEvent<String>()
    }

    val addStatus by lazy {
        SingleLiveEvent<Boolean>()
    }

    val updateUserInfo by lazy {
        SingleLiveEvent<AccountEntity>()
    }
}
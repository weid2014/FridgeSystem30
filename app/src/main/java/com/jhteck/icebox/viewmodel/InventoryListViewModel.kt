package com.jhteck.icebox.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.hele.mrd.app.lib.base.BaseViewModel
import com.hele.mrd.app.lib.base.livedata.SingleLiveEvent
import com.jhteck.icebox.api.AvailRfid
import com.jhteck.icebox.apiserver.ILoginApiService
import com.jhteck.icebox.apiserver.LocalService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 *@Description:(库存ViewModel)
 *@author wade
 *@date 2023/6/28 22:04
 */
class InventoryListViewModel(application: Application) :
    BaseViewModel<ILoginApiService>(application) {
    private val TAG = "InventoryListViewModel"

    override fun createApiServiceType(): Class<ILoginApiService> {
        return ILoginApiService::class.java
    }

    fun editCellNumber(availRfid: AvailRfid, position: Int) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                showLoading("正在保存位置，请稍等...")
                LocalService.updateAvailRfidOnly(availRfid)
                toast("保存位置成功")
                onAvailRfidLoaded.postValue(position)
                /*val response =
                    RetrofitClient.getService().deleteAccount(user.user_id);//上传 ->需要考虑上传失败的
                if (response.code() == 200) {
                    toast("删除账户成功")
                } else {
                    toast("删除账户异常${response.message()}")
                }*/
            } catch (e: Exception) {
                toast("保存位置异常${e.message}")
            } finally {
                hideLoading()
            }
        }

    }

    val onAvailRfidLoaded by lazy {
        SingleLiveEvent<Int>()
    }

}
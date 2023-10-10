package com.jhteck.icebox.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.hele.mrd.app.lib.base.BaseViewModel
import com.hele.mrd.app.lib.base.livedata.SingleLiveEvent
import com.jhteck.icebox.api.AvailRfid
import com.jhteck.icebox.api.request.RfidSync
import com.jhteck.icebox.api.request.requestSync
import com.jhteck.icebox.apiserver.ILoginApiService
import com.jhteck.icebox.apiserver.LocalService
import com.jhteck.icebox.apiserver.RetrofitClient
import com.jhteck.icebox.utils.DbUtil
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

                val rfidList = mutableListOf<RfidSync>()

                rfidList.add(RfidSync(position, availRfid.remain, availRfid.rfid))

                val bodySync = genBody(requestSync(rfidList))
//                apiService.syncRfids()
                val repSync = RetrofitClient.getService().syncRfids(bodySync)
                if (repSync.isSuccessful) {
                    Log.d(TAG, "全量上报成功")
                }else{
                    Log.d(TAG, "全量上报失敗")
                }

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
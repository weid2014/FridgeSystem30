package com.jhteck.icebox.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import android.view.View
import androidx.lifecycle.viewModelScope
import com.hele.mrd.app.lib.base.BaseViewModel
import com.hele.mrd.app.lib.base.livedata.SingleLiveEvent
import com.jhteck.icebox.api.AUTO_LOGIN_STR
import com.jhteck.icebox.api.AvailRfid
import com.jhteck.icebox.api.request.RfidSync
import com.jhteck.icebox.api.request.requestSync
import com.jhteck.icebox.apiserver.ILoginApiService
import com.jhteck.icebox.apiserver.LocalService
import com.jhteck.icebox.apiserver.RetrofitClient
import com.jhteck.icebox.utils.CustomDialog
import com.jhteck.icebox.utils.DateUtils
import com.jhteck.icebox.utils.DbUtil
import com.jhteck.icebox.utils.SharedPreferencesUtils
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

    //弹出过期提示
    fun showOutTimeTip(context: Context, availRfidsList: List<AvailRfid>) {
        var expiredRfidList= mutableListOf<AvailRfid>()
        var expiredRfidNameList= mutableListOf<String>()
        for (rfidItem in availRfidsList){
            val showDate = rfidItem.material_batch?.expired_at.substring(0, 10)
            val remainDay= DateUtils.getDaysBetween(showDate, DateUtils.format_yyyyMMdd)
            if(remainDay<7){
                expiredRfidList.add(rfidItem)
                expiredRfidNameList.add(rfidItem.material.eas_material_name)
            }
        }
        if(expiredRfidList.size>0){
            val customDialog = CustomDialog(context)
            customDialog.setsTitle("温馨提示").setsMessage("以下物料即将过期(或已过期)，请及时处理：\n ${expiredRfidNameList.joinToString(separator = "\n")}")
                .setsCancel("取消", View.OnClickListener {
                    customDialog.dismiss()
                }).setsConfirm("知道了", View.OnClickListener {
                    customDialog.dismiss()
                }).show()
        }
    }

    val onAvailRfidLoaded by lazy {
        SingleLiveEvent<Int>()
    }

}
package com.jhteck.icebox.viewmodel

import android.app.Application
import android.content.Context
import android.os.CountDownTimer
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hele.mrd.app.lib.base.BaseViewModel
import com.hele.mrd.app.lib.base.livedata.SingleLiveEvent
import com.jhteck.icebox.R
import com.jhteck.icebox.adapter.InventoryListAdapter
import com.jhteck.icebox.api.AvailRfid
import com.jhteck.icebox.api.LAST_PROMPT_DATE
import com.jhteck.icebox.api.request.RfidSync
import com.jhteck.icebox.api.request.requestSync
import com.jhteck.icebox.apiserver.ILoginApiService
import com.jhteck.icebox.apiserver.LocalService
import com.jhteck.icebox.apiserver.RetrofitClient
import com.jhteck.icebox.databinding.AppFragmentInventoryBinding
import com.jhteck.icebox.utils.CustomDialog
import com.jhteck.icebox.utils.DateUtils
import com.jhteck.icebox.utils.DensityUtil
import com.jhteck.icebox.utils.SharedPreferencesUtils
import com.jhteck.icebox.view.SingletonPopupWindow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import java.util.stream.Collectors


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

                rfidList.add(
                    RfidSync(
                        position,
                        availRfid.remain,
                        availRfid.rfid,
                        DateUtils.currentStringFormatTime()
                    )
                )

                val bodySync = genBody(requestSync(rfidList))
//                apiService.syncRfids()
                val repSync = RetrofitClient.getService().syncRfids(bodySync)
                if (repSync.isSuccessful) {
                    Log.d(TAG, "全量上报成功")
                } else {
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
        var expiredRfidList = mutableListOf<AvailRfid>()
        var expiredRfidNameList = mutableListOf<String>()
        for (rfidItem in availRfidsList) {
            val showDate = rfidItem.material_batch?.expired_at.substring(0, 10)
            val remainDay = DateUtils.getDaysBetween(showDate, DateUtils.format_yyyyMMdd)
            if (remainDay < 7) {
                expiredRfidList.add(rfidItem)
                expiredRfidNameList.add(rfidItem.material.eas_material_name)
            }
        }
        if (expiredRfidList.size > 0) {
            val customDialog = CustomDialog(context)
            customDialog.setsTitle("温馨提示")
                .setsMessage("以下物料即将过期(或已过期)，请及时处理：\n ${expiredRfidNameList.joinToString(separator = "\n")}")
                .setsCancel("取消", View.OnClickListener {
                    customDialog.dismiss()
                }).setsConfirm("知道了", View.OnClickListener {
                    customDialog.dismiss()
                }).show()
        }
    }

    /**
     *
     * 过期，临期物料提示
     */
    private var btnCountDownTime: Button? = null
    private var popupWindow: PopupWindow? = null
    fun showOutTimePopWindow(
        fragment: Fragment,
        availRfidsList: List<AvailRfid>,
        binding: AppFragmentInventoryBinding
    ) {

        val expiredRfidList0 = mutableListOf<AvailRfid>()//已经过期
        val expiredRfidList7 = mutableListOf<AvailRfid>()//临期物料
        val expiredRfidList30 = mutableListOf<AvailRfid>()//呆滞物料
        for (rfidItem in availRfidsList) {
            val showDate = rfidItem.material_batch?.expired_at.substring(0, 10)

            val remainDay = DateUtils.getDaysBetween(showDate, DateUtils.format_yyyyMMdd)
            if (remainDay < 0) {
                expiredRfidList0.add(rfidItem)
            } else if (remainDay < 7) {
                expiredRfidList7.add(rfidItem)
            }

            //先取最后一次同步的时间，如果没有取创建的时间
            if (rfidItem.last_fridge_first_sync_at != null && !rfidItem.last_fridge_first_sync_at.equals(
                    "null"
                )
            ) {
                val showDate1 = rfidItem.last_fridge_first_sync_at?.substring(0, 10)
                if (DateUtils.getDaysBetween(showDate1, DateUtils.format_yyyyMMdd) < -30) {
                    expiredRfidList30.add(rfidItem)
                }
            } else if (rfidItem.created_at != "null") {
                val showDate1 = rfidItem.created_at?.substring(0, 10)
                if (DateUtils.getDaysBetween(showDate1, DateUtils.format_yyyyMMdd) < -30) {
                    expiredRfidList30.add(rfidItem)
                }
            }
        }
        if (expiredRfidList0.size > 0 || expiredRfidList7.size > 0 || expiredRfidList30.size > 0) {
            //每天只提示一次
            val currentDate = DateUtils.formatDateToString(Date(), DateUtils.format_yyyyMMdd)
            val lastPromptDate =
                SharedPreferencesUtils.getPrefString(
                    fragment.requireContext(),
                    LAST_PROMPT_DATE,
                    "1"
                )

            // 如果当天还没有提示过消息，则显示消息
            if (currentDate != lastPromptDate) {
                //弹出结算界面
                popupWindow = SingletonPopupWindow.getInstance(fragment.requireContext())
                val layoutInflater = LayoutInflater.from(fragment.requireContext())
                popupWindow?.contentView =
                    layoutInflater.inflate(R.layout.pup_out_time_list, null)


                //设置按钮
                val btnClosePop = popupWindow?.contentView?.findViewById<ImageButton>(R.id.btnClose)
                val btnOk = popupWindow?.contentView?.findViewById<Button>(R.id.btnOK)

                btnCountDownTime =
                    popupWindow?.contentView?.findViewById<Button>(R.id.btnCountDownTime)


                btnClosePop?.setOnClickListener {
                    popupWindow?.dismiss()
                    DensityUtil.backgroundAlpha(fragment.requireActivity(), 1.0f)
                }
                btnOk?.setOnClickListener {
                    popupWindow?.dismiss()
                    DensityUtil.backgroundAlpha(fragment.requireActivity(), 1.0f)
                }

                if (expiredRfidList0.size > 0) {
                    val mapExpiredRfidList0 = expiredRfidList0.stream()
                        .collect(Collectors.groupingBy { t -> t.rfid })//根据批号分组
                    val tempExpiredRfidList0 = mutableListOf<List<AvailRfid>>()
                    for (key in mapExpiredRfidList0.keys) {
                        mapExpiredRfidList0.get(key)?.let { tempExpiredRfidList0.add(it) }
                    }

                    val rvExpired0 =
                        popupWindow?.contentView?.findViewById<RecyclerView>(R.id.rvExpired0)
                    rvExpired0?.layoutManager = LinearLayoutManager(fragment.requireContext())
                    rvExpired0?.adapter = InventoryListAdapter(tempExpiredRfidList0)
                } else {
                    popupWindow?.contentView?.findViewById<LinearLayout>(R.id.ll_Expired0)?.visibility =
                        View.GONE
                }

                if (expiredRfidList7.size > 0) {
                    val mapExpiredRfidList7 = expiredRfidList7.stream()
                        .collect(Collectors.groupingBy { t -> t.rfid })//根据批号分组
                    val tempExpiredRfidList7 = mutableListOf<List<AvailRfid>>()
                    for (key in mapExpiredRfidList7.keys) {
                        mapExpiredRfidList7.get(key)?.let { tempExpiredRfidList7.add(it) }
                    }

                    val rvExpired7 =
                        popupWindow?.contentView?.findViewById<RecyclerView>(R.id.rvExpired7)
                    rvExpired7?.layoutManager = LinearLayoutManager(fragment.requireContext())
                    rvExpired7?.adapter = InventoryListAdapter(tempExpiredRfidList7)
                } else {
                    popupWindow?.contentView?.findViewById<LinearLayout>(R.id.ll_Expired7)?.visibility =
                        View.GONE
                }

                if (expiredRfidList30.size > 0) {
                    val mapExpiredRfidList30 = expiredRfidList30.stream()
                        .collect(Collectors.groupingBy { t -> t.rfid })//根据批号分组
                    val tempExpiredRfidList30 = mutableListOf<List<AvailRfid>>()
                    for (key in mapExpiredRfidList30.keys) {
                        mapExpiredRfidList30.get(key)?.let { tempExpiredRfidList30.add(it) }
                    }

                    val rvExpired30 =
                        popupWindow?.contentView?.findViewById<RecyclerView>(R.id.rvExpired30)
                    rvExpired30?.layoutManager = LinearLayoutManager(fragment.requireContext())
                    rvExpired30?.adapter = InventoryListAdapter(tempExpiredRfidList30)
                } else {
                    popupWindow?.contentView?.findViewById<LinearLayout>(R.id.ll_Expired30)?.visibility =
                        View.GONE
                }
                DensityUtil.backgroundAlpha(fragment.requireActivity(), 0.5f)
                popupWindow?.showAtLocation(binding.root, Gravity.CENTER, 0, 0);
                startCountDownTime()
                // 保存当前日期，以便下次检查
                SharedPreferencesUtils.setPrefString(
                    fragment.requireContext(),
                    LAST_PROMPT_DATE,
                    currentDate
                )
            }
        }

    }

    private var timer: CountDownTimer? = null
    private fun startCountDownTime() {
        timer = object : CountDownTimer(25 * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                println("seconds remaining: " + millisUntilFinished / 1000)
                var remainTimeInt: Int = (millisUntilFinished / 1000).toInt()
                btnCountDownTime?.text = "${remainTimeInt}秒后将自动关闭并确认接收以上信息"
            }

            override fun onFinish() {
                popupWindow?.dismiss()
            }
        }
        timer?.start()
    }

    val onAvailRfidLoaded by lazy {
        SingleLiveEvent<Int>()
    }

}
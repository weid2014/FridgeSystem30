package com.jhteck.icebox.fragment.operatechildfrag

import android.app.DatePickerDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.hele.mrd.app.lib.base.BaseApp
import com.hele.mrd.app.lib.base.BaseFragment
import com.hele.mrd.app.lib.base.ext.format
import com.jhteck.icebox.R
import com.jhteck.icebox.adapter.AppTabPagerAdapter
import com.jhteck.icebox.adapter.OperaRecordListAdapter
import com.jhteck.icebox.adapter.SystemOperationRecordListAdapter
import com.jhteck.icebox.api.ROLE_ID
import com.jhteck.icebox.bean.ExceptionRecordBean
import com.jhteck.icebox.bean.OperationErrorEnum
import com.jhteck.icebox.bean.SystemOperationErrorEnum
import com.jhteck.icebox.databinding.*
import com.jhteck.icebox.repository.entity.SysOperationErrorEntity
import com.jhteck.icebox.utils.DateUtils
import com.jhteck.icebox.utils.SharedPreferencesUtils
import com.jhteck.icebox.viewmodel.AccountViewModel
import com.jhteck.icebox.viewmodel.InventoryListViewModel
import com.jhteck.icebox.viewmodel.OperationLogViewModel
import com.jhteck.icebox.viewmodel.SettingViewModel
import java.util.*

/**
 *@Description:(异常记录页面Fragment)
 *@author wade
 *@date 2023/6/28 21:59
 */
class ErrorRecordFrag : BaseFragment<AccountViewModel, AppFragmentOperaErrorBinding>() {
    companion object {
        fun newInstance(): ErrorRecordFrag {
            return ErrorRecordFrag()
        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): AppFragmentOperaErrorBinding {
        return AppFragmentOperaErrorBinding.inflate(layoutInflater)
    }

    override fun createViewModel(): AccountViewModel {
        return viewModels<AccountViewModel>().value
    }

    private val frag by lazy {
        parentFragment as ErrorRecordFrag
    }
    private val beginTime = Calendar.getInstance()
    private val endTime = Calendar.getInstance()
    private var lastSelectPosition = -1

    private var rfidOperationList = mutableListOf<SysOperationErrorEntity>()

    override fun initView() {

        val layoutManagerOperaRecord = LinearLayoutManager(requireContext())
        binding.rvDrugContent.layoutManager = layoutManagerOperaRecord
        binding.btnBeginTime.setOnClickListener {
            val dialog = DatePickerDialog(requireContext())
            dialog.setOnDateSetListener { _, year, month, dayOfMonth ->
                beginTime[Calendar.YEAR] = year
                beginTime[Calendar.MONTH] = month
                beginTime[Calendar.DAY_OF_MONTH] = dayOfMonth
                binding.btnBeginTime.text = beginTime.timeInMillis.format("yyyy-MM-dd")
                if (endTime.after(beginTime) || endTime.equals(beginTime)) {
                    dateBtnStatus(btnTodaySelected = false, btnYesterdaySelected = false)
                    updateUIbyRfidOperationList(filterByDate(beginTime, endTime))
                } else {
                    Toast.makeText(requireContext(), "开始时间小于结束时间，请重新选择", Toast.LENGTH_SHORT).show()
                }
            }
            dialog.show()
        }


        binding.btnEndTime.setOnClickListener {
            val dialog = DatePickerDialog(requireContext())
            dialog.setOnDateSetListener { _, year, month, dayOfMonth ->
                endTime[Calendar.YEAR] = year
                endTime[Calendar.MONTH] = month
                endTime[Calendar.DAY_OF_MONTH] = dayOfMonth
                binding.btnEndTime.text = endTime.timeInMillis.format("yyyy-MM-dd")
                if (endTime.after(beginTime) || endTime.equals(beginTime)) {
                    Toast.makeText(requireContext(), "$beginTime", Toast.LENGTH_SHORT).show()
                    dateBtnStatus(btnTodaySelected = false, btnYesterdaySelected = false)
                    updateUIbyRfidOperationList(filterByDate(beginTime, endTime))
                } else {
                    Toast.makeText(requireContext(), "开始时间小于结束时间，请重新选择", Toast.LENGTH_SHORT).show()
                }

            }
            dialog.show()
        }

        val selectList = listOf(
            "全部",
            SystemOperationErrorEnum.REBOOT.desc,
            SystemOperationErrorEnum.TIME_OUT.desc,
            SystemOperationErrorEnum.NOT_MARK.desc
        )
        binding.spRefer.adapter =
            ArrayAdapter(requireContext(), R.layout.app_item_text, R.id.tv_content, selectList)

        binding.spRefer.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (position == 0)
                        view?.findViewById<ImageView>(R.id.im_down)?.visibility = View.VISIBLE
                    if (position != lastSelectPosition) {
                        view?.findViewById<ImageView>(R.id.im_down)?.visibility = View.VISIBLE
                        lastSelectPosition = position
                        when (position) {
                            0 -> updateUIbyRfidOperationList(filter(""))
                            1 -> updateUIbyRfidOperationList(
                                filterByOperation(
                                    SystemOperationErrorEnum.REBOOT.v
                                )
                            )
                            2 -> updateUIbyRfidOperationList(
                                filterByOperation(
                                    SystemOperationErrorEnum.TIME_OUT.v
                                )
                            )
                            3 -> updateUIbyRfidOperationList(
                                filterByOperation(
                                    SystemOperationErrorEnum.NOT_MARK.v
                                )
                            )
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
        lastSelectPosition = 0

        binding.btnToday.setOnClickListener {
            dateBtnStatus(btnTodaySelected = true, btnYesterdaySelected = false)
            selectDate(0)
            updateUIbyRfidOperationList(filterByDate(beginTime, endTime))
        }
        binding.btnYesterday.setOnClickListener {
            dateBtnStatus(btnTodaySelected = false, btnYesterdaySelected = true)
            selectDate(-1)
            updateUIbyRfidOperationList(filterByDate(beginTime, endTime))

        }
        dateBtnStatus(btnTodaySelected = false, btnYesterdaySelected = false)

        //监听搜索栏里面的变化
        binding.svInventoryList.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                //当提交了输入时
                return false
            }

            override fun onQueryTextChange(keyword: String?): Boolean {
                // 当修改了输入时的操作，根据关键字过滤列表，让Adapter填入新列表
                // 如果只是更新部分数据，推荐使用notifyItemRangeChanged()或者notifyItemChanged()
                val filterList = keyword?.let { filter(it) }
                updateUIbyRfidOperationList(filterList!!)
                return false
            }
        })

        //排序

    }

    override fun initObservables() {
        super.initObservables()
        if (bindingInitialzed()) {
            viewModel.onSystemExceptionRecordLoad.observe(this) {
                rfidOperationList.clear()
                for (user in it) {
                    rfidOperationList.add(user)
                }

                updateUIbyRfidOperationList(rfidOperationList)
            }
            viewModel.getAllOperationErrorLogs()
        }
    }

    private fun selectDate(day: Int) {
        beginTime[Calendar.HOUR_OF_DAY] = 0
        beginTime[Calendar.MINUTE] = 0
        beginTime[Calendar.SECOND] = 0
        endTime.timeInMillis = System.currentTimeMillis()
        beginTime[Calendar.DAY_OF_YEAR] = endTime[Calendar.DAY_OF_YEAR] + day
        endTime[Calendar.DAY_OF_YEAR] = beginTime[Calendar.DAY_OF_YEAR]
        binding.btnBeginTime.text = beginTime.timeInMillis.format("yyyy-MM-dd")
        binding.btnEndTime.text = endTime.timeInMillis.format("yyyy-MM-dd")
    }

    private fun filter(keyWord: String): List<SysOperationErrorEntity> {
        // 过滤原本的列表，返回一个新的列表
        if (keyWord == "") {
            return rfidOperationList;
        }
        val filterList = mutableListOf<SysOperationErrorEntity>()

        for (l in rfidOperationList!!) {
            if (l.system_info.contains(keyWord) || l.system_info.contains(keyWord))
                filterList.add(l)
        }
        return filterList
    }

    private fun filterByOperation(keyWord: Int): List<SysOperationErrorEntity> {
        // 过滤原本的列表，返回一个新的列表
        val filterList = mutableListOf<SysOperationErrorEntity>()
        for (l in rfidOperationList!!) {
            if (l.error_code.toInt() == keyWord)
                filterList.add(l)
        }
        return filterList
    }

    private fun filterByDate(
        beginTimeCalendar: Calendar,
        endTimeCalendar: Calendar,
    ): List<SysOperationErrorEntity> {
        // 过滤原本的列表，返回一个新的列表
        val filterList = mutableListOf<SysOperationErrorEntity>()
        for (l in rfidOperationList!!) {
            val tempTimeArray = l.log_at.replace("T", " ").replace("+08:00", "")
            if (tempTimeArray.isNotEmpty()) {
                val operaTime =
                    DateUtils.formatStringToDate(tempTimeArray, DateUtils.format_yyyyMMddhhmmssfff)
                if ((beginTimeCalendar.time.before(operaTime) || beginTimeCalendar.time.equals(
                        operaTime
                    )) && operaTime.before(endTimeCalendar.time)
                )
                    filterList.add(l)
            }
        }
        return filterList
    }

    private fun updateUIbyRfidOperationList(list: List<SysOperationErrorEntity>) {
        val roleID = SharedPreferencesUtils.getPrefInt(BaseApp.app, ROLE_ID, 10)
        binding.rvDrugContent.adapter = SystemOperationRecordListAdapter(frag, list)
    }

    private fun dateBtnStatus(btnTodaySelected: Boolean, btnYesterdaySelected: Boolean) {
        binding.btnToday.isSelected = btnTodaySelected
        binding.btnYesterday.isSelected = btnYesterdaySelected
    }

}
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
import com.jhteck.icebox.adapter.OperaRecordListAdapter
import com.jhteck.icebox.api.ROLE_ID
import com.jhteck.icebox.databinding.AppFragmentOperaRecordBinding
import com.jhteck.icebox.fragment.OperationLogFrag
import com.jhteck.icebox.repository.entity.RfidOperationEntity
import com.jhteck.icebox.utils.DateUtils
import com.jhteck.icebox.utils.SharedPreferencesUtils
import com.jhteck.icebox.viewmodel.AccountViewModel
import java.util.*

/**
 *@Description:(操作记录页面Fragment)
 *@author wade
 *@date 2023/6/28 21:59
 */
class OperaRecordFrag : BaseFragment<AccountViewModel, AppFragmentOperaRecordBinding>() {
    private var currentIndex0 = 0
    private var currentIndex2 = 2
    private var currentIndex4 = 4
    private var currentIndex6 = 6
    private var currentIndex8 = 8
    private var currentIndex10 = 10
    private var currentIndex12 = 12
    private var currentIndex14 = 14
    private var currentIndex16 = 16
    private var currentIndex18 = 18
    private var clickTvNumber = false;

    companion object {
        fun newInstance(): OperaRecordFrag {
            return OperaRecordFrag()
        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): AppFragmentOperaRecordBinding {
        return AppFragmentOperaRecordBinding.inflate(layoutInflater)
    }

    override fun createViewModel(): AccountViewModel {
        return viewModels<AccountViewModel>().value
    }

    private val frag by lazy {
        parentFragment as OperationLogFrag
    }
    private val beginTime = Calendar.getInstance()
    private val endTime = Calendar.getInstance()
    private var lastSelectPosition = -1

    override fun initView() {
        val layoutManagerOperaRecord = LinearLayoutManager(requireContext())
        binding.rvOperaContent.layoutManager = layoutManagerOperaRecord

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

        val selectList = listOf("全部", "存入", "取出", "暂存")
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
                            1 -> updateUIbyRfidOperationList(filterByOperation(0))
                            2 -> updateUIbyRfidOperationList(filterByOperation(1))
                            3 -> updateUIbyRfidOperationList(filterByOperation(2))
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
        binding.svOperationRecord.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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

        binding.tvDrugNumber.setOnClickListener {
            listSortBy(currentIndex0, binding.tvDrugNumber)
        }
        binding.tvDrugName.setOnClickListener {
            listSortBy(currentIndex2, binding.tvDrugName)
        }
        binding.tvDrugSpec.setOnClickListener {
            listSortBy(currentIndex4, binding.tvDrugSpec)
        }
        binding.tvDrugManufacturers.setOnClickListener {
            listSortBy(currentIndex6, binding.tvDrugManufacturers)
        }
        binding.tvUnit.setOnClickListener {
            listSortBy(currentIndex8, binding.tvUnit)
        }
        binding.tvNumber.setOnClickListener {
            clickTvNumber = true
            listSortBy(currentIndex10, binding.tvNumber)
        }
        binding.tvBatchNumber.setOnClickListener {
            listSortBy(currentIndex12, binding.tvBatchNumber)
        }
        binding.tvOperationType.setOnClickListener {
            listSortBy(currentIndex14, binding.tvOperationType)
        }
        binding.tvOperator.setOnClickListener {
            listSortBy(currentIndex16, binding.tvOperator)
        }
        binding.tvOperatorTime.setOnClickListener {
            listSortBy(currentIndex18, binding.tvOperatorTime)
        }
    }

    private fun filter(keyWord: String): List<RfidOperationEntity> {
        // 过滤原本的列表，返回一个新的列表
        val filterList = mutableListOf<RfidOperationEntity>()

        for (l in rfidOperationList!!) {
            if (l.eas_material_name.contains(keyWord) || l.eas_material_number.contains(keyWord))
                filterList.add(l)
        }
        return filterList
    }

    private fun filterByOperation(keyWord: Int): List<RfidOperationEntity> {
        // 过滤原本的列表，返回一个新的列表
        val filterList = mutableListOf<RfidOperationEntity>()
        for (l in rfidOperationList!!) {
            if (l.operation.toInt() == keyWord)
                filterList.add(l)
        }
        return filterList
    }

    private fun filterByDate(
        beginTimeCalendar: Calendar,
        endTimeCalendar: Calendar,
    ): List<RfidOperationEntity> {
        // 过滤原本的列表，返回一个新的列表
        val filterList = mutableListOf<RfidOperationEntity>()
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

    private fun updateUIbyRfidOperationList(list: List<RfidOperationEntity>) {
        binding.rvOperaContent.adapter = OperaRecordListAdapter(frag, list)
    }

    private var rfidOperationList = mutableListOf<RfidOperationEntity>()
    override fun initObservables() {
        super.initObservables()
        if (bindingInitialzed()) {
            viewModel.onOperationsLoaded.observe(this) {
                rfidOperationList.clear()
                for (user in it) {
                    rfidOperationList.add(user)
                }

                updateUIbyRfidOperationList(rfidOperationList)
            }
            viewModel.getAllOperationLogs()
        }
    }

    private fun dateBtnStatus(btnTodaySelected: Boolean, btnYesterdaySelected: Boolean) {
        binding.btnToday.isSelected = btnTodaySelected
        binding.btnYesterday.isSelected = btnYesterdaySelected
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

    private fun listSortBy(index: Int, tv: TextView) {
        var sortByList: List<RfidOperationEntity>? = null

        when (index) {
            // (0 1) 根据eas_material_number 升降序
            0 -> {
                changeTextViewIcon(tv, true)
                currentIndex0 = 1
                sortByList = rfidOperationList!!.sortedBy { it.eas_material_number }
            }
            1 -> {
                changeTextViewIcon(tv, false)
                currentIndex0 = 0
                sortByList = rfidOperationList!!.sortedByDescending { it.eas_material_number }
            }
            // (2 3) eas_material_name 升降序 之后一次类推
            2 -> {
                changeTextViewIcon(tv, true)
                currentIndex2 = 3
                sortByList = rfidOperationList!!.sortedBy { it.eas_material_name }
            }
            3 -> {
                changeTextViewIcon(tv, false)
                currentIndex2 = 2
                sortByList = rfidOperationList!!.sortedByDescending { it.eas_material_name }
            }

            // (4 5) eas_material_desc 升降序 之后一次类推
            4 -> {
                changeTextViewIcon(tv, true)
                currentIndex4 = 5
                sortByList = rfidOperationList!!.sortedBy { it.eas_specs }
            }
            5 -> {
                changeTextViewIcon(tv, false)
                currentIndex4 = 4
                sortByList = rfidOperationList!!.sortedByDescending { it.eas_specs }
            }
            // (6 7)生产厂家 eas_supplier_name 升降序 之后一次类推
            6 -> {
                changeTextViewIcon(tv, true)
                currentIndex6 = 7
                sortByList = rfidOperationList!!.sortedBy { it.eas_supplier_name }
            }
            7 -> {
                changeTextViewIcon(tv, false)
                currentIndex6 = 6
                sortByList = rfidOperationList!!.sortedByDescending { it.eas_supplier_name }
            }
            // (8 9) 单位 eas_unit_name 升降序 之后一次类推
            8 -> {
                changeTextViewIcon(tv, true)
                currentIndex8 = 9
                sortByList = rfidOperationList!!.sortedBy { it.eas_unit_name }
            }
            9 -> {
                changeTextViewIcon(tv, false)
                currentIndex8 = 9
                sortByList = rfidOperationList!!.sortedByDescending { it.eas_unit_name }
            }
            // (10 11) remain 升降序 之后一次类推
            10 -> {
                changeTextViewIcon(tv, true)
                currentIndex10 = 11
                sortByList = rfidOperationList!!.sortedBy { it.eas_material_name }
            }
            11 -> {
                changeTextViewIcon(tv, false)
                currentIndex10 = 10
                sortByList = rfidOperationList!!.sortedByDescending { it.eas_material_name }
            }
            // (12 13) remain 升降序 之后一次类推
            12 -> {
                changeTextViewIcon(tv, true)
                currentIndex12 = 13
                sortByList = rfidOperationList!!.sortedBy { it.eas_lot }
            }
            13 -> {
                changeTextViewIcon(tv, false)
                currentIndex12 = 12
                sortByList = rfidOperationList!!.sortedByDescending { it.eas_lot }
            }
            // (14 15) eas_unit_number 升降序 之后一次类推
            14 -> {
                changeTextViewIcon(tv, true)
                currentIndex14 = 15
                sortByList = rfidOperationList!!.sortedBy { it.operation }
            }
            15 -> {
                changeTextViewIcon(tv, false)
                currentIndex14 = 14
                sortByList = rfidOperationList!!.sortedByDescending { it.operation }
            }
            // (16 17)有效期 is_out_eas 升降序 之后一次类推
            16 -> {
                changeTextViewIcon(tv, true)
                currentIndex16 = 17
                sortByList = rfidOperationList!!.sortedBy { it.nick_name }
            }
            17 -> {
                changeTextViewIcon(tv, false)
                currentIndex16 = 16
                sortByList = rfidOperationList!!.sortedByDescending { it.nick_name }
            }
            // (2 3) is_out_eas 升降序 之后一次类推
            18 -> {
                changeTextViewIcon(tv, true)
                currentIndex18 = 19
                sortByList = rfidOperationList!!.sortedBy { it.log_at }
            }
            19 -> {
                changeTextViewIcon(tv, false)
                currentIndex18 = 18
                sortByList = rfidOperationList!!.sortedByDescending { it.log_at }
            }

            else -> sortByList = rfidOperationList!!
        }
        updateUIbyRfidOperationList(sortByList!!)
    }

    private fun changeTextViewIcon(tv: TextView, up: Boolean) {
        if (up) {
            tv.setCompoundDrawablesRelativeWithIntrinsicBounds(
                null,
                null,
                resources.getDrawable(R.mipmap.ic_triangle_up),
                null
            )
        } else {
            tv.setCompoundDrawablesRelativeWithIntrinsicBounds(
                null,
                null,
                resources.getDrawable(R.mipmap.ic_triangle_down),
                null
            )
        }
    }

}
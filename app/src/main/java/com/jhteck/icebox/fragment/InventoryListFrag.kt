package com.jhteck.icebox.fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.hele.mrd.app.lib.base.BaseApp
import com.hele.mrd.app.lib.base.BaseFragment
import com.jhteck.icebox.R
import com.jhteck.icebox.adapter.InventoryListItemAdapter
import com.jhteck.icebox.adapter.InventoryListItemAdapterScener
import com.jhteck.icebox.adapter.ItemEditCellNumberAdapter
import com.jhteck.icebox.api.AvailRfid
import com.jhteck.icebox.api.ROLE_ID
import com.jhteck.icebox.api.RfidDao
import com.jhteck.icebox.databinding.AppFragmentInventoryBinding
import com.jhteck.icebox.utils.SharedPreferencesUtils
import com.jhteck.icebox.viewmodel.InventoryListViewModel
import java.util.stream.Collectors

/**
 *@Description:(库存列表Fragment)
 *@author wade
 *@date 2023/6/28 21:59
 */
class InventoryListFrag : BaseFragment<InventoryListViewModel, AppFragmentInventoryBinding>() {

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
    private var currentIndex20 = 20
    private var clickTvNumber = false;

    companion object {
        fun newInstance(): InventoryListFrag {
            return InventoryListFrag()
        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): AppFragmentInventoryBinding {
        return AppFragmentInventoryBinding.inflate(layoutInflater)
    }

    override fun createViewModel(): InventoryListViewModel {
        return viewModels<InventoryListViewModel>().value
    }

    private val frag by lazy {
        parentFragment as InventoryListFrag
    }



    override fun initView() {
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
                updateUIbyAvailList(filterList!!)
                return false
            }
        })
        binding.tvDrugNumber.setOnClickListener {
            AvailRfidsListSortBy(currentIndex0, binding.tvDrugNumber)
        }
        binding.tvDrugName.setOnClickListener {
            AvailRfidsListSortBy(currentIndex2, binding.tvDrugName)
        }
        binding.tvDrugSpec.setOnClickListener {
            AvailRfidsListSortBy(currentIndex4, binding.tvDrugSpec)
        }
        binding.tvDrugManufacturers.setOnClickListener {
            AvailRfidsListSortBy(currentIndex6, binding.tvDrugManufacturers)
        }
        binding.tvUnit.setOnClickListener {
            AvailRfidsListSortBy(currentIndex8, binding.tvUnit)
        }
        binding.tvNumber.setOnClickListener {
            clickTvNumber = true
            AvailRfidsListSortBy(currentIndex10, binding.tvNumber)
        }
        binding.tvRemain.setOnClickListener {
            AvailRfidsListSortBy(currentIndex12, binding.tvRemain)
        }
        binding.tvBatchNumber.setOnClickListener {
            AvailRfidsListSortBy(currentIndex14, binding.tvBatchNumber)
        }
        binding.tvValidityDate.setOnClickListener {
            AvailRfidsListSortBy(currentIndex16, binding.tvValidityDate)
        }
        binding.tvValidityStatus.setOnClickListener {
            AvailRfidsListSortBy(currentIndex18, binding.tvValidityStatus)
        }
        binding.tvCellNumber.setOnClickListener {
            AvailRfidsListSortBy(currentIndex20, binding.tvCellNumber)
        }
    }

    private fun filter(keyWord: String): List<AvailRfid> {
        // 过滤原本的列表，返回一个新的列表
        val filterList = mutableListOf<AvailRfid>()

        for (l in availRfidsList!!) {
            if (l.material.eas_material_name.contains(keyWord)
                || l.material.eas_material_number.contains(keyWord)
            )
                filterList.add(l)
        }
        return filterList
    }




    private fun AvailRfidsListSortBy(index: Int, tv: TextView) {
        var sortByList: List<AvailRfid>? = null

        when (index) {
            // (0 1) 根据eas_material_number 升降序
            0 -> {
                changeTextViewIcon(tv, true)
                currentIndex0 = 1
                sortByList = availRfidsList!!.sortedBy { it.material.eas_material_number }
            }
            1 -> {
                changeTextViewIcon(tv, false)
                currentIndex0 = 0
                sortByList = availRfidsList!!.sortedByDescending { it.material.eas_material_number }
            }
            // (2 3) eas_material_name 升降序 之后一次类推
            2 -> {
                changeTextViewIcon(tv, true)
                currentIndex2 = 3
                sortByList = availRfidsList!!.sortedBy { it.material.eas_material_name }
            }
            3 -> {
                changeTextViewIcon(tv, false)
                currentIndex2 = 2
                sortByList = availRfidsList!!.sortedByDescending { it.material.eas_material_name }
            }

            // (4 5) eas_material_desc 升降序 之后一次类推
            4 -> {
                changeTextViewIcon(tv, true)
                currentIndex4 = 5
                sortByList = availRfidsList!!.sortedBy { it.remain }
            }
            5 -> {
                changeTextViewIcon(tv, false)
                currentIndex4 = 4
                sortByList = availRfidsList!!.sortedByDescending { it.remain }
            }
            // (6 7)生产厂家 eas_supplier_name 升降序 之后一次类推
            6 -> {
                changeTextViewIcon(tv, true)
                currentIndex6 = 7
                sortByList = availRfidsList!!.sortedBy { it.eas_supplier_name }
            }
            7 -> {
                changeTextViewIcon(tv, false)
                currentIndex6 = 6
                sortByList = availRfidsList!!.sortedByDescending { it.eas_supplier_name }
            }
            // (8 9) 单位 eas_unit_name 升降序 之后一次类推
            8 -> {
                changeTextViewIcon(tv, true)
                currentIndex8 = 9
                sortByList = availRfidsList!!.sortedBy { it.material.eas_unit_name }
            }
            9 -> {
                changeTextViewIcon(tv, false)
                currentIndex8 = 9
                sortByList = availRfidsList!!.sortedByDescending { it.material.eas_unit_name }
            }
            // (10 11) remain 升降序 之后一次类推
            10 -> {
                changeTextViewIcon(tv, true)
                currentIndex10 = 11
                sortByList = availRfidsList!!.sortedBy { it.material.eas_material_name }
            }
            11 -> {
                changeTextViewIcon(tv, false)
                currentIndex10 = 10
                sortByList = availRfidsList!!.sortedByDescending { it.material.eas_material_name }
            }
            // (12 13) remain 升降序 之后一次类推
            12 -> {
                changeTextViewIcon(tv, true)
                currentIndex12 = 13
                sortByList = availRfidsList!!.sortedBy { it.remain }
            }
            13 -> {
                changeTextViewIcon(tv, false)
                currentIndex12 = 12
                sortByList = availRfidsList!!.sortedByDescending { it.remain }
            }
            // (14 15) eas_unit_number 升降序 之后一次类推
            14 -> {
                changeTextViewIcon(tv, true)
                currentIndex14 = 15
                sortByList = availRfidsList!!.sortedBy { it.material_batch.eas_lot }
            }
            15 -> {
                changeTextViewIcon(tv, false)
                currentIndex14 = 14
                sortByList = availRfidsList!!.sortedByDescending { it.material_batch.eas_lot }
            }
            // (16 17)有效期 is_out_eas 升降序 之后一次类推
            16 -> {
                changeTextViewIcon(tv, true)
                currentIndex16 = 17
                sortByList = availRfidsList!!.sortedBy { it.material_batch.expired_at }
            }
            17 -> {
                changeTextViewIcon(tv, false)
                currentIndex16 = 16
                sortByList = availRfidsList!!.sortedByDescending { it.material_batch.expired_at }
            }
            // (2 3) is_out_eas 升降序 之后一次类推
            18 -> {
                changeTextViewIcon(tv, true)
                currentIndex18 = 19
                sortByList = availRfidsList!!.sortedBy { it.is_out_eas }
            }
            19 -> {
                changeTextViewIcon(tv, false)
                currentIndex18 = 18
                sortByList = availRfidsList!!.sortedByDescending { it.is_out_eas }
            }
            20 -> {
                changeTextViewIcon(tv, true)
                currentIndex20 = 21
                sortByList = availRfidsList!!.sortedBy { it.cell_number }
            }
            21 -> {
                changeTextViewIcon(tv, false)
                currentIndex20 = 20
                sortByList = availRfidsList!!.sortedByDescending { it.cell_number }
            }

            else -> sortByList = availRfidsList!!
        }
        updateUIbyAvailList(sortByList!!)
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

    var availRfidsList: List<AvailRfid>? = null
    fun updateUIbyData(rfidDao: RfidDao) {
        if (bindingInitialzed()) {
            //获取用户角色ID
            availRfidsList = rfidDao.results.avail_rfids
            updateUIbyAvailList(availRfidsList!!)
        }
    }

    private fun updateUIbyAvailList(list: List<AvailRfid>) {
        val roleID = SharedPreferencesUtils.getPrefInt(BaseApp.app, ROLE_ID, 10)
        binding.tvCountNumber.text = "共${list!!.size}个"
        if (list!!.isNotEmpty()) {
            val layoutManager = LinearLayoutManager(BaseApp.app)
            layoutManager.orientation = LinearLayoutManager.VERTICAL
            binding.rvDrugContent.layoutManager = layoutManager

            var mapOutList = list!!.stream()
                .collect(Collectors.groupingBy { t -> t.material.eas_material_name + t.material.eas_unit_number })//根据批号分组

            var tempOutList = mutableListOf<List<AvailRfid>>()
            for (key in mapOutList.keys) {
                mapOutList.get(key)?.let { tempOutList.add(it) }
            }
            if (clickTvNumber && currentIndex10 == 10) {
                tempOutList = tempOutList.sortedBy { it.size }.toMutableList()
            } else {
                tempOutList = tempOutList.sortedByDescending { it.size }.toMutableList()
            }
            clickTvNumber = false
            when (roleID) {
                10, 20 -> {
                    binding.rvDrugContent.adapter = InventoryListItemAdapter(this, tempOutList,
                        object : ItemEditCellNumberAdapter<AvailRfid> {
                            override fun onEdit(t: AvailRfid, position: Int) {
                                viewModel.editCellNumber(t, position)
                            }
                        })
                }
                else -> {
                    binding.rvDrugContent.adapter = InventoryListItemAdapterScener(this, list!!,
                        object : ItemEditCellNumberAdapter<AvailRfid> {
                            override fun onEdit(t: AvailRfid, position: Int) {
                                viewModel.editCellNumber(t, position)
                            }
                        })
                }
            }

            showEmptyImage(false)
        } else {
            showEmptyImage(true)
        }
    }

    private fun showEmptyImage(isEmpty: Boolean) {
        if (isEmpty) {
            binding.imEmpty.visibility = View.VISIBLE
            binding.rvDrugContent.visibility = View.GONE
        } else {
            binding.imEmpty.visibility = View.GONE
            binding.rvDrugContent.visibility = View.VISIBLE
        }
    }

    override fun initObservables() {
        super.initObservables()
        viewModel.onAvailRfidLoaded.observe(this) {
            binding.rvDrugContent.adapter?.notifyItemChanged(it)
        }
    }
}
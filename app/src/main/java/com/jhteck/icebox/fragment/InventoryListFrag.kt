package com.jhteck.icebox.fragment

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.hele.mrd.app.lib.base.BaseApp
import com.hele.mrd.app.lib.base.BaseFragment
import com.jhteck.icebox.Lockmodel.LockManage
import com.jhteck.icebox.R
import com.jhteck.icebox.adapter.InventoryListItemAdapter
import com.jhteck.icebox.adapter.ItemEditCellNumberAdapter
import com.jhteck.icebox.api.AvailRfid
import com.jhteck.icebox.api.RfidDao
import com.jhteck.icebox.databinding.AppFragmentInventoryBinding
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
                var mapOutList = filterList!!.stream()
                    .collect(Collectors.groupingBy { t -> t.rfid })//根据批号分组

                var tempOutList = mutableListOf<List<AvailRfid>>()
                for (key in mapOutList.keys) {
                    mapOutList.get(key)?.let { tempOutList.add(it) }
                }
                updateUIbyAvailList(tempOutList)
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

        binding.fullscreenView.setOnClickListener {
            Log.d("InventoryListFrag", "fullscreenView")
            LockManage.getInstance().tryOpenLock();
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
        var mapOutList = availRfidsList!!.stream()
            .collect(Collectors.groupingBy { t -> t.rfid })//根据批号分组

        var tempOutList = mutableListOf<List<AvailRfid>>()
        for (key in mapOutList.keys) {
            mapOutList.get(key)?.let { tempOutList.add(it) }
        }

        var tempsortList: List<List<AvailRfid>>? = null
        when (index) {
            // (0 1) 根据eas_material_number 升降序
            0 -> {
                changeTextViewIcon(tv, true)
                currentIndex0 = 1
                tempsortList = tempOutList.sortedBy { it[0].material.eas_material_number }
            }
            1 -> {
                changeTextViewIcon(tv, false)
                currentIndex0 = 0
                tempsortList = tempOutList.sortedByDescending { it[0].material.eas_material_number }
            }
            // (2 3) eas_material_name 升降序 之后一次类推
            2 -> {
                changeTextViewIcon(tv, true)
                currentIndex2 = 3
                tempsortList = tempOutList.sortedBy { it[0].material.eas_material_name }
            }
            3 -> {
                changeTextViewIcon(tv, false)
                currentIndex2 = 2
                tempsortList = tempOutList.sortedByDescending { it[0].material.eas_material_name }
            }

            // (4 5) eas_material_desc 升降序 之后一次类推
            4 -> {
                changeTextViewIcon(tv, true)
                currentIndex4 = 5
                tempsortList = tempOutList.sortedBy { it[0].material_batch.eas_specs }
            }
            5 -> {
                changeTextViewIcon(tv, false)
                currentIndex4 = 4
                tempsortList = tempOutList.sortedByDescending { it[0].material_batch.eas_specs }
            }
            // (6 7)生产厂家 eas_supplier_name 升降序 之后一次类推
            6 -> {
                changeTextViewIcon(tv, true)
                currentIndex6 = 7
                tempsortList = tempOutList.sortedBy { it[0].material.eas_manufacturer }
            }
            7 -> {
                changeTextViewIcon(tv, false)
                currentIndex6 = 6
                tempsortList = tempOutList.sortedByDescending { it[0].material.eas_manufacturer }
            }
            // (8 9) 单位 eas_unit_name 升降序 之后一次类推
            8 -> {
                changeTextViewIcon(tv, true)
                currentIndex8 = 9
                tempsortList = tempOutList.sortedBy { it[0].material_package.unit_name }
            }
            9 -> {
                changeTextViewIcon(tv, false)
                currentIndex8 = 9
                tempsortList = tempOutList.sortedByDescending { it[0].material_package.unit_name }
            }
            // (10 11) remain 升降序 之后一次类推
            10 -> {
                changeTextViewIcon(tv, true)
                currentIndex10 = 11
                tempsortList = tempOutList.sortedBy { it[0].material.eas_material_name }
            }
            11 -> {
                changeTextViewIcon(tv, false)
                currentIndex10 = 10
                tempsortList = tempOutList.sortedByDescending { it[0].material.eas_material_name }
            }
            // (12 13) remain 升降序 之后一次类推
            12 -> {
                changeTextViewIcon(tv, true)
                currentIndex12 = 13
                tempsortList = tempOutList.sortedBy { it[0].remain }
            }
            13 -> {
                changeTextViewIcon(tv, false)
                currentIndex12 = 12
                tempsortList = tempOutList.sortedByDescending { it[0].remain }
            }
            // (14 15) eas_unit_number 升降序 之后一次类推
            14 -> {
                changeTextViewIcon(tv, true)
                currentIndex14 = 15
                tempsortList = tempOutList.sortedBy { it[0].material_batch.eas_lot }
            }
            15 -> {
                changeTextViewIcon(tv, false)
                currentIndex14 = 14
                tempsortList = tempOutList.sortedByDescending { it[0].material_batch.eas_lot }
            }
            // (16 17)有效期 is_out_eas 升降序 之后一次类推
            16 -> {
                changeTextViewIcon(tv, true)
                currentIndex16 = 17
                tempsortList = tempOutList.sortedBy { it[0].material_batch.expired_at }
            }
            17 -> {
                changeTextViewIcon(tv, false)
                currentIndex16 = 16
                tempsortList = tempOutList.sortedByDescending { it[0].material_batch.expired_at }
            }
            // (2 3) is_out_eas 升降序 之后一次类推
            18 -> {
                changeTextViewIcon(tv, true)
                currentIndex18 = 19
                tempsortList = tempOutList.sortedBy { it[0].is_out_eas }
            }
            19 -> {
                changeTextViewIcon(tv, false)
                currentIndex18 = 18
                tempsortList = tempOutList.sortedByDescending { it[0].is_out_eas }
            }
            20 -> {
                changeTextViewIcon(tv, true)
                currentIndex20 = 21
                tempsortList = tempOutList.sortedBy { it[0].cell_number }
            }
            21 -> {
                changeTextViewIcon(tv, false)
                currentIndex20 = 20
                tempsortList = tempOutList.sortedByDescending { it[0].cell_number }
            }

            else -> tempsortList = tempOutList.sortedBy { it[0].material.eas_manufacturer }
        }

//        val temp = tempOutList.sortedBy { it[0].material_batch.expired_at }
        updateUIbyAvailList(tempsortList)
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

            /*var mapOutList = availRfidsList!!.stream()
                .collect(Collectors.groupingBy { t -> t.material.eas_material_name + t.material.eas_unit_number + t.remain })//根据批号分组

            var tempOutList = mutableListOf<List<AvailRfid>>()
            for (key in mapOutList.keys) {
                mapOutList.get(key)?.let { tempOutList.add(it) }
            }*/
            var tempOutList = mutableListOf<List<AvailRfid>>()
            for (item in availRfidsList!!) {
                val temp = mutableListOf<AvailRfid>()
                temp.add(item)
                tempOutList.add(temp)
            }
            updateUIbyAvailList(tempOutList)
            //进入页面初始化后按过期日期排序
            AvailRfidsListSortBy(currentIndex16, binding.tvValidityDate)
            viewModel.showOutTimePopWindow(this@InventoryListFrag, availRfidsList!!, binding)
        }
    }


    private fun updateUIbyAvailList(list: List<List<AvailRfid>>) {
        binding.tvCountNumber.text = "共${availRfidsList!!.size}个"
        if (list!!.isNotEmpty()) {
            val layoutManager = LinearLayoutManager(BaseApp.app)
            layoutManager.orientation = LinearLayoutManager.VERTICAL
            binding.rvDrugContent.layoutManager = layoutManager

            if (clickTvNumber && currentIndex10 == 10) {
                list.sortedBy { it.size }.toMutableList()
            } else {
                list.sortedByDescending { it.size }.toMutableList()
            }
            clickTvNumber = false
            binding.rvDrugContent.adapter = InventoryListItemAdapter(this, list,
                object : ItemEditCellNumberAdapter<AvailRfid> {
                    override fun onEdit(t: AvailRfid, position: Int) {
                        viewModel.editCellNumber(t, position)
                    }
                })
            binding.rvDrugContent.adapter?.notifyDataSetChanged()

            showEmptyImage(false)
        } else {
            showEmptyImage(true)
        }
    }

    private fun showEmptyImage(isEmpty: Boolean) {
        if (isEmpty) {
            binding.llEmpty.visibility = View.VISIBLE
            binding.rvDrugContent.visibility = View.GONE
        } else {
            binding.llEmpty.visibility = View.GONE
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
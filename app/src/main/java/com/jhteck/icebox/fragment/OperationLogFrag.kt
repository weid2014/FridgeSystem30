package com.jhteck.icebox.fragment

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import com.hele.mrd.app.lib.base.BaseFragment
import com.jhteck.icebox.R
import com.jhteck.icebox.adapter.AppTabPagerAdapter
import com.jhteck.icebox.databinding.AppFragmentOperationBinding
import com.jhteck.icebox.fragment.operatechildfrag.ErrorRecordFrag
import com.jhteck.icebox.fragment.operatechildfrag.ExceptionRecordFrag
import com.jhteck.icebox.fragment.operatechildfrag.OperaRecordFrag
import com.jhteck.icebox.viewmodel.OperationLogViewModel

/**
 *@Description:(操作记录Fragment)
 *@author wade
 *@date 2023/6/28 21:59
 */
class OperationLogFrag : BaseFragment<OperationLogViewModel, AppFragmentOperationBinding>() {

    private val tabs = arrayListOf<TextView>()
    private var currentSelectItem = 0

    companion object {
        fun newInstance(): OperationLogFrag {
            return OperationLogFrag()
        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): AppFragmentOperationBinding {
        return AppFragmentOperationBinding.inflate(layoutInflater)
    }

    override fun createViewModel(): OperationLogViewModel {
        return viewModels<OperationLogViewModel>().value
    }

    private val frag by lazy {
        parentFragment as OperationLogFrag
    }

    override fun initView() {
        initTab()
    }

    fun initTab() {
        val operaRecordFrag = OperaRecordFrag.newInstance()
        val exceptionRecordFrag = ExceptionRecordFrag.newInstance()
        val errorRecordFrag = ErrorRecordFrag.newInstance()
        tabs.add(binding.tvOperationRecord)
        tabs.add(binding.tvExceptionRecord)
        tabs.add(binding.tvErrorRecord)
        binding.vpContentOperation.adapter =
            AppTabPagerAdapter(
                arrayListOf(operaRecordFrag, exceptionRecordFrag, errorRecordFrag),
                childFragmentManager,
                lifecycle
            )
        binding.vpContentOperation.isSaveEnabled = false
        binding.vpContentOperation.isUserInputEnabled = false
        binding.tvOperationRecord.setOnClickListener {
            setupSelectItem(0)
        }
        binding.tvExceptionRecord.setOnClickListener {
            setupSelectItem(1)

        }
        binding.tvErrorRecord.setOnClickListener {
            setupSelectItem(2)
        }
        setupSelectItem(0)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setupSelectItem(position: Int) {
        if (position == currentSelectItem) {
            return
        }
        tabs[currentSelectItem].isSelected = false
        tabs[position].isSelected = true
        for (tab in tabs) {
            tab.background = resources.getDrawable(R.drawable.radius_border_tab_unselect)
        }
        tabs[position].background = resources.getDrawable(R.drawable.radius_border_tab)
        currentSelectItem = position
        binding.vpContentOperation.setCurrentItem(position, false)

    }
}
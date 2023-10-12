package com.jhteck.icebox.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jhteck.icebox.bean.OperationErrorEnum
import com.jhteck.icebox.databinding.AppFragmentOperaErrorBinding
import com.jhteck.icebox.fragment.OperationLogFrag
import com.jhteck.icebox.repository.entity.SysOperationErrorEntity

class SystemOperationRecordListAdapter(
    private val frag: OperationLogFrag,
    private val data: List<SysOperationErrorEntity>
) :
    RecyclerView.Adapter<SystemOperationRecordListAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            frag,
            AppFragmentOperaErrorBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(data[position])
    }

    class ViewHolder(
        private val frag: OperationLogFrag,
        private val binding: AppFragmentOperaErrorBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(item: SysOperationErrorEntity) {
            binding.tvOperator.text = item.log_at//操作時間
            binding.tvDrugName.text = item.system_info//操作時間
            binding.tvDrugSpec.text = item.app_version//操作時間
            binding.tvOperationType.text = OperationErrorEnum.getDesc(item.error_code)//操作時間
            binding.tvUnit.text = item.nick_name
        }

    }

    override fun getItemCount(): Int {
        return data.size
    }


}
package com.jhteck.icebox.adapter

import android.graphics.drawable.BitmapDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hele.mrd.app.lib.base.BaseApp
import com.jhteck.icebox.R
import com.jhteck.icebox.bean.OperationErrorEnum
import com.jhteck.icebox.databinding.AppFragmentOperaErrorBinding
import com.jhteck.icebox.databinding.RvOperaRecordItemBinding
import com.jhteck.icebox.databinding.RvRfidItemBinding
import com.jhteck.icebox.fragment.OperationLogFrag
import com.jhteck.icebox.fragment.operatechildfrag.ErrorRecordFrag
import com.jhteck.icebox.repository.entity.RfidOperationEntity
import com.jhteck.icebox.repository.entity.SysOperationErrorEntity
import com.jhteck.icebox.utils.DensityUtil
import com.jhteck.icebox.utils.PatternUtil

class SystemOperationRecordListAdapter(
    private val frag: ErrorRecordFrag,
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
        private val frag: ErrorRecordFrag,
        private val binding: AppFragmentOperaErrorBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(item: SysOperationErrorEntity) {
            binding.tvOperator.text = item.log_at//操作時間
            binding.tvDrugName.text = item.system_info//操作時間
            binding.tvDrugSpec.text = item.app_version//操作時間
            binding.tvOperationType.text = OperationErrorEnum.getDesc(item.error_code)//操作時間
            binding.tvUnit.text=item.nick_name
        }

    }

    override fun getItemCount(): Int {
        return data.size
    }


}
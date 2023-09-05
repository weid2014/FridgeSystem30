package com.jhteck.icebox.adapter

/**
 *@Description:(操作日志---单品码列表适配器)
 *@author wade
 *@date 2023/7/1 23:19
 */
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jhteck.icebox.databinding.RvRfidItemBinding
import com.jhteck.icebox.repository.entity.RfidOperationEntity
import com.jhteck.icebox.utils.PatternUtil

class OperationRecordItemAdapter(private val data: List<RfidOperationEntity>) :
    RecyclerView.Adapter<OperationRecordItemAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            RvRfidItemBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(data[position])
    }

    class ViewHolder(private val binding: RvRfidItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(item: RfidOperationEntity) {
            binding.tvRfid.text = item.rfid
            binding.tvDrugName.text =
                PatternUtil.removeDigitalAndLetter(item.eas_material_name)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }


}
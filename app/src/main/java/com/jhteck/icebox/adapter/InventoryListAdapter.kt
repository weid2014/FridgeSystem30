package com.jhteck.icebox.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jhteck.icebox.api.AvailRfid
import com.jhteck.icebox.databinding.PupItemDrugBinding
import com.jhteck.icebox.utils.PatternUtil

/**
 * 结算列表
 */
class InventoryListAdapter(private val data: List<List<AvailRfid>>) :
    RecyclerView.Adapter<InventoryListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            PupItemDrugBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ViewHolder(
        private val binding: PupItemDrugBinding,
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(item: List<AvailRfid>) {
            val tempName = PatternUtil.removeDigitalAndLetter(item[0].material?.eas_material_name)
            binding.tvDrugName.text = tempName
            binding.tvDrugNo.text = "x${item.size}"
        }
    }
}



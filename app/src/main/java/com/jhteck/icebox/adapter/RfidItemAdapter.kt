package com.jhteck.icebox.adapter

/**
 *@Description:(单品码列表适配器)
 *@author wade
 *@date 2023/7/1 23:19
 */
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jhteck.icebox.api.AvailRfid
import com.jhteck.icebox.databinding.RvRfidItemBinding
import com.jhteck.icebox.utils.PatternUtil

class RfidItemAdapter(private val data: List<AvailRfid>) :
    RecyclerView.Adapter<RfidItemAdapter.ViewHolder>() {


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
        fun bindData(item: AvailRfid) {
            binding.tvRfid.text = item.rfid
            binding.tvDrugName.text =
                PatternUtil.removeDigitalAndLetter(item.material.eas_material_name)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }


}
package com.jhteck.icebox.adapter

/**
 *@Description:(用一句话描述)
 *@author wade
 *@date 2023/7/1 23:19
 */
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jhteck.icebox.bean.InventoryDao
import com.jhteck.icebox.databinding.RvNomalContentItemBinding
import com.jhteck.icebox.utils.PatternUtil

class LoginPageShowItemAdapter(private val data: List<InventoryDao>) :
    RecyclerView.Adapter<LoginPageShowItemAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            RvNomalContentItemBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(data[position])
    }

    class ViewHolder(private val binding: RvNomalContentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(item: InventoryDao) {
            val tempName=PatternUtil.removeDigitalAndLetter(item.drugName)
            binding.tvDrugName.text = tempName
            binding.tvDrugName.isSelected = true;
            binding.tvDrugNo.text=item.drugNO
            binding.tvDrugNumber.text = "${item.drugNumber}盒"
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }


}
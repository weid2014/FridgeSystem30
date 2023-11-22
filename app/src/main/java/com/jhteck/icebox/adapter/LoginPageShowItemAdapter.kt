package com.jhteck.icebox.adapter

/**
 *@Description:(用一句话描述)
 *@author wade
 *@date 2023/7/1 23:19
 */
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hele.mrd.app.lib.base.BaseApp
import com.jhteck.icebox.R
import com.jhteck.icebox.bean.InventoryDao
import com.jhteck.icebox.databinding.RvNomalContentItemBinding
import com.jhteck.icebox.utils.DateUtils
import com.jhteck.icebox.utils.PatternUtil
import java.util.*

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
            val tempName = PatternUtil.removeDigitalAndLetter(item.drugName)
            binding.tvDrugName.text = tempName

            binding.tvDrugName.isSelected = true;
            binding.tvDrugNo.text = item.drugNO
            binding.tvDrugNumber.text = "${item.drugNumber}盒"

            val showDate = item.expired_at.substring(0, 10)
            val calendar = Calendar.getInstance()
            val toDay = DateUtils.formatDateToString(
                calendar.time,
                DateUtils.format_yyyyMMdd
            )
            val remainDay = DateUtils.getDaysBetween(toDay, showDate, DateUtils.format_yyyyMMdd)
            if (remainDay < 0) {
                binding.tvDrugName.setTextColor(BaseApp.app.getColor(R.color.app_color_ff3030))
                binding.tvDrugNumber.setTextColor(BaseApp.app.getColor(R.color.app_color_ff3030))
                binding.llDrugBg.setBackgroundResource(R.drawable.app_shape_red_radius_10)
                binding.vLine.setBackgroundResource(R.color.app_color_ff3030)
            } else if (remainDay < 7) {
                binding.tvDrugName.setTextColor(BaseApp.app.getColor(R.color.app_color_fbaf5d))
                binding.tvDrugNumber.setTextColor(BaseApp.app.getColor(R.color.app_color_fbaf5d))
                binding.llDrugBg.setBackgroundResource(R.drawable.app_shape_yellow_radius_10)
                binding.vLine.setBackgroundResource(R.color.app_color_fbaf5d)
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }


}
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
import com.jhteck.icebox.api.AvailRfid
import com.jhteck.icebox.databinding.RvNomalContentItemBinding
import com.jhteck.icebox.utils.DateUtils
import com.jhteck.icebox.utils.PatternUtil

class LoginPageShowItemAdapter(private val data: List<AvailRfid>) :
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

    override fun getItemViewType(position: Int): Int {
        return position
    }

    class ViewHolder(private val binding: RvNomalContentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(item: AvailRfid) {
            val tempName = PatternUtil.removeDigitalAndLetter(item.material.eas_material_name)
            binding.tvDrugName.text = tempName

            binding.tvDrugNumber.text =
                "${item.qty?.toFloat()!! / item.material_package.unit_qty}(${item.material_package.unit_name})"

            val showDate = item.material_batch.expired_at.substring(0, 10)
            val remainDay = DateUtils.getDaysBetween(showDate, DateUtils.format_yyyyMMdd)
            if (remainDay < 0) {
                binding.tvDrugName.setTextColor(BaseApp.app.getColor(R.color.app_white))
                binding.tvDrugNumber.setTextColor(BaseApp.app.getColor(R.color.app_white))
                binding.llDrugBg.setBackgroundResource(R.drawable.app_shape_red_radius_10)
                binding.vLine.setBackgroundResource(R.color.app_white)
            } else if (remainDay < 7) {
                binding.tvDrugName.setTextColor(BaseApp.app.getColor(R.color.app_white))
                binding.tvDrugNumber.setTextColor(BaseApp.app.getColor(R.color.app_white))
                binding.llDrugBg.setBackgroundResource(R.drawable.app_shape_yellow_radius_10)
                binding.vLine.setBackgroundResource(R.color.app_white)
            }
            //先取最后一次同步的时间，如果没有取创建的时间
            if (item.last_fridge_first_sync_at != null && !item.last_fridge_first_sync_at.equals(
                    "null"
                )
            ) {
                val showDate1 = item.last_fridge_first_sync_at?.substring(0, 10)
                if (DateUtils.getDaysBetween(showDate1, DateUtils.format_yyyyMMdd) < -30) {
                    binding.tvDrugName.setTextColor(BaseApp.app.getColor(R.color.app_white))
                    binding.tvDrugNumber.setTextColor(BaseApp.app.getColor(R.color.app_white))
                    binding.llDrugBg.setBackgroundResource(R.drawable.app_shape_blue_radius_5)
                    binding.vLine.setBackgroundResource(R.color.app_white)
                }
            } else if (item.created_at != "null") {
                val showDate1 = item.created_at?.substring(0, 10)
                if (DateUtils.getDaysBetween(showDate1, DateUtils.format_yyyyMMdd) < -30) {
                    binding.tvDrugName.setTextColor(BaseApp.app.getColor(R.color.app_white))
                    binding.tvDrugNumber.setTextColor(BaseApp.app.getColor(R.color.app_white))
                    binding.llDrugBg.setBackgroundResource(R.drawable.app_shape_blue_radius_5)
                    binding.vLine.setBackgroundResource(R.color.app_white)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }


}
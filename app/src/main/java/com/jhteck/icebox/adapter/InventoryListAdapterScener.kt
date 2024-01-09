package com.jhteck.icebox.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jhteck.icebox.R
import com.jhteck.icebox.api.AvailRfid
import com.jhteck.icebox.databinding.PupItemDrugScenerBinding
import com.jhteck.icebox.utils.PatternUtil

/**
 * 现场人员结算列表 ，可以选百分比存储
 */
class InventoryListAdapterScener(
    private val data: List<AvailRfid>,
    private val settlement: ISettlement
) :
    RecyclerView.Adapter<InventoryListAdapterScener.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            PupItemDrugScenerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bindData(data[position], settlement)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ViewHolder(
        private val binding: PupItemDrugScenerBinding,
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(item: AvailRfid, settlement: ISettlement) {
            val tempName = PatternUtil.removeDigitalAndLetter(item.material.eas_material_name)
            binding.tvDrugName.text = tempName


            var remainNumber = 100
            binding.tvRemain20.setOnClickListener {
                binding.tvRemain40.setBackgroundResource(R.color.app_white)
                binding.tvRemain60.setBackgroundResource(R.color.app_white)
                binding.tvRemain80.setBackgroundResource(R.color.app_white)
                binding.tvRemain100.setBackgroundResource(R.color.app_white)
                remainNumber = 20
                item.remain = 20
                settlement.settlement(item)
            }
            binding.tvRemain40.setOnClickListener {
                binding.tvRemain40.setBackgroundResource(R.color.app_blue_40)
                binding.tvRemain60.setBackgroundResource(R.color.app_white)
                binding.tvRemain80.setBackgroundResource(R.color.app_white)
                binding.tvRemain100.setBackgroundResource(R.color.app_white)
                remainNumber = 40
                item.remain = 40
                settlement.settlement(item)
            }
            binding.tvRemain60.setOnClickListener {
                binding.tvRemain40.setBackgroundResource(R.color.app_blue_40)
                binding.tvRemain60.setBackgroundResource(R.color.app_blue_60)
                binding.tvRemain80.setBackgroundResource(R.color.app_white)
                binding.tvRemain100.setBackgroundResource(R.color.app_white)
                remainNumber = 60
                item.remain = 60
                settlement.settlement(item)
            }
            binding.tvRemain80.setOnClickListener {
                binding.tvRemain40.setBackgroundResource(R.color.app_blue_40)
                binding.tvRemain60.setBackgroundResource(R.color.app_blue_60)
                binding.tvRemain80.setBackgroundResource(R.color.app_blue_80)
                binding.tvRemain100.setBackgroundResource(R.color.app_white)
                remainNumber = 80
                item.remain = 80
                settlement.settlement(item)
            }
            binding.tvRemain100.setOnClickListener {
                binding.tvRemain40.setBackgroundResource(R.color.app_blue_40)
                binding.tvRemain60.setBackgroundResource(R.color.app_blue_60)
                binding.tvRemain80.setBackgroundResource(R.color.app_blue_80)
                binding.tvRemain100.setBackgroundResource(R.drawable.app_shape_blue_radius_100)
                remainNumber = 100
                item.remain = 100
                settlement.settlement(item)
            }
            when (item.remain) {
                20 -> {
                    binding.tvRemain40.setBackgroundResource(R.color.app_white)
                    binding.tvRemain60.setBackgroundResource(R.color.app_white)
                    binding.tvRemain80.setBackgroundResource(R.color.app_white)
                    binding.tvRemain100.setBackgroundResource(R.color.app_white)
                    binding.tvRemain40.isClickable=false
                    binding.tvRemain60.isClickable=false
                    binding.tvRemain80.isClickable=false
                    binding.tvRemain100.isClickable=false
                }
                40 -> {
                    binding.tvRemain40.setBackgroundResource(R.color.app_blue_40)
                    binding.tvRemain60.setBackgroundResource(R.color.app_white)
                    binding.tvRemain80.setBackgroundResource(R.color.app_white)
                    binding.tvRemain100.setBackgroundResource(R.color.app_white)
                    binding.tvRemain60.isClickable=false
                    binding.tvRemain80.isClickable=false
                    binding.tvRemain100.isClickable=false
                }
                60 -> {
                    binding.tvRemain40.setBackgroundResource(R.color.app_blue_40)
                    binding.tvRemain60.setBackgroundResource(R.color.app_blue_60)
                    binding.tvRemain80.setBackgroundResource(R.color.app_white)
                    binding.tvRemain100.setBackgroundResource(R.color.app_white)
                    binding.tvRemain80.isClickable=false
                    binding.tvRemain100.isClickable=false
                }
                80 -> {
                    binding.tvRemain40.setBackgroundResource(R.color.app_blue_40)
                    binding.tvRemain60.setBackgroundResource(R.color.app_blue_60)
                    binding.tvRemain80.setBackgroundResource(R.color.app_blue_80)
                    binding.tvRemain100.setBackgroundResource(R.color.app_white)
                    binding.tvRemain100.isClickable=false
                }
            }
        }
    }
}



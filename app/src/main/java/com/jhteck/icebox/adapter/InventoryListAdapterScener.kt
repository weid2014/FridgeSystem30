package com.jhteck.icebox.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
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
            Log.d("TAG", "item.remain: $item.remain")
            when (item.remain) {
                20 -> {
                    binding.tvRemain40.visibility = View.INVISIBLE
                    binding.tvRemain60.visibility = View.INVISIBLE
                    binding.tvRemain80.visibility = View.INVISIBLE
                    binding.tvRemain100.visibility = View.INVISIBLE
                }
                40 -> {
                    binding.tvRemain40.visibility = View.VISIBLE
                    binding.tvRemain60.visibility = View.INVISIBLE
                    binding.tvRemain80.visibility = View.INVISIBLE
                    binding.tvRemain100.visibility = View.INVISIBLE
                }
                60 -> {
                    binding.tvRemain40.visibility = View.VISIBLE
                    binding.tvRemain60.visibility = View.VISIBLE
                    binding.tvRemain80.visibility = View.INVISIBLE
                    binding.tvRemain100.visibility = View.INVISIBLE
                }
                80 -> {
                    binding.tvRemain40.visibility = View.VISIBLE
                    binding.tvRemain60.visibility = View.VISIBLE
                    binding.tvRemain80.visibility = View.VISIBLE
                    binding.tvRemain100.visibility = View.INVISIBLE
                }
            }
            var remainNumber = 100
            binding.tvRemain20.setOnClickListener {
                binding.tvRemain40.visibility = View.INVISIBLE
                binding.tvRemain60.visibility = View.INVISIBLE
                binding.tvRemain80.visibility = View.INVISIBLE
                binding.tvRemain100.visibility = View.INVISIBLE
                remainNumber = 20
                item.remain = 20;
                settlement.settlement(item)
            }
            binding.tvRemain40.setOnClickListener {
                binding.tvRemain60.visibility = View.INVISIBLE
                binding.tvRemain80.visibility = View.INVISIBLE
                binding.tvRemain100.visibility = View.INVISIBLE
                remainNumber = 40
                item.remain = 40;
                settlement.settlement(item)
            }
            binding.tvRemain60.setOnClickListener {
                binding.tvRemain80.visibility = View.INVISIBLE
                binding.tvRemain100.visibility = View.INVISIBLE
                remainNumber = 60
                item.remain = 60;
                settlement.settlement(item)
            }
            binding.tvRemain80.setOnClickListener {
                binding.tvRemain100.visibility = View.INVISIBLE
                remainNumber = 80
                item.remain = 80;
                settlement.settlement(item)
            }

        }
    }
}



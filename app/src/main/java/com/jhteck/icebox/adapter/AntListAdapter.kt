package com.jhteck.icebox.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jhteck.icebox.R
import com.jhteck.icebox.api.AntPowerDao
import com.jhteck.icebox.api.AvailRfid
import com.jhteck.icebox.databinding.PupItemAntPowerBinding
import com.jhteck.icebox.fragment.SettingFrag

/**
 * 结算列表
 */
class AntListAdapter(private val frag: SettingFrag,
                     private var data: List<AntPowerDao>,
                     private val antPowerCallBack: IAntPowerCallback<AntPowerDao>
                     ) :
    RecyclerView.Adapter<AntListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            frag,
            PupItemAntPowerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(data[position],antPowerCallBack)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ViewHolder(
        private val frag: SettingFrag,
        private val binding: PupItemAntPowerBinding,
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(item: AntPowerDao, antPowerCallBack: IAntPowerCallback<AntPowerDao>) {
            binding.tvAntId.text = "ANT${item.antid}"
            val selectList = listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17,
                18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33
            )
            binding.spPowerRefer.adapter =
                ArrayAdapter(
                    frag.requireContext(),
                    R.layout.app_item_text,
                    R.id.tv_content,
                    selectList
                )
            binding.spPowerRefer.setSelection(item.power.toInt())
            binding.spPowerRefer.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        item.power=position.toString()
                        Log.d("lalala","item==$item")
                        antPowerCallBack.antPowerCallback(item,getPosition())
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }
                }
        }
    }
}



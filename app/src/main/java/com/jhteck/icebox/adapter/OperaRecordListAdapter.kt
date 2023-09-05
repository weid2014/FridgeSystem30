package com.jhteck.icebox.adapter

/**
 *@Description:(操作记录列表适配器)
 *@author wade
 *@date 2023/7/1 23:19
 */
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
import com.jhteck.icebox.databinding.RvOperaRecordItemBinding
import com.jhteck.icebox.fragment.OperationLogFrag
import com.jhteck.icebox.fragment.operatechildfrag.OperaRecordFrag
import com.jhteck.icebox.repository.entity.RfidOperationEntity
import com.jhteck.icebox.utils.DensityUtil
import com.jhteck.icebox.utils.PatternUtil

class OperaRecordListAdapter(
    private val frag: OperationLogFrag,
    private val data: List<RfidOperationEntity>
) :
    RecyclerView.Adapter<OperaRecordListAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            frag,
            RvOperaRecordItemBinding.inflate(
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
        private val frag: OperationLogFrag,
        private val binding: RvOperaRecordItemBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(item: RfidOperationEntity) {
            binding.tvDrugNumber.text = item.eas_material_number.toString()
            binding.tvDrugName.text =
                PatternUtil.removeDigitalAndLetter(item.eas_material_name.toString())
            binding.tvDrugSpec.text = item.eas_specs.toString()
            if(item.eas_manufacturer!=null) {
                binding.tvDrugManufacturers.text = item.eas_manufacturer.toString()
            }
            binding.tvUnit.text = item.eas_unit_name.toString()
            binding.tvNumber.text = item.number.toString()
            binding.tvBatchNumber.text = item.eas_lot.toString()
            when (item.operation.toInt()) {
                0 -> binding.tvOperationType.text = "存入"
                1 -> binding.tvOperationType.text = "取出"
                2 -> binding.tvOperationType.text = "暂存"
            }

            binding.tvOperator.text = item.nick_name.toString()
            binding.tvOperatorTime.text = item.log_at.substring(0,10)
            binding.btnItemCode.setOnClickListener {
                PopupWindow().apply {
                    //入口参数配置
                    val layoutInflater = LayoutInflater.from(BaseApp.app)
                    contentView =
                        layoutInflater.inflate(R.layout.pup_rfid, null)
                    width = ViewGroup.LayoutParams.WRAP_CONTENT
                    height = ViewGroup.LayoutParams.WRAP_CONTENT
                    isTouchable = true
                    isFocusable = true
                    isOutsideTouchable = false
                    setBackgroundDrawable(BitmapDrawable())
                    DensityUtil.backgroundAlpha(frag.activity, 0.5f)
                    //设置按钮
                    val btnClosePop = contentView.findViewById<ImageView>(R.id.btnCloseRfidPop)

                    //单品码和rfid对应列表
                    val rvRfidInfo = contentView.findViewById<RecyclerView>(R.id.rv_RfidInfo)
                    val layoutManager = LinearLayoutManager(BaseApp.app)
                    layoutManager.orientation = LinearLayoutManager.VERTICAL
                    rvRfidInfo.layoutManager = layoutManager
                    val tempList = mutableListOf<RfidOperationEntity>()
                    tempList.add(item)
                    rvRfidInfo.adapter = OperationRecordItemAdapter(tempList)

                    btnClosePop.setOnClickListener {
                        DensityUtil.backgroundAlpha(frag.activity, 1f)
                        dismiss()
                    }
                    showAtLocation(binding.root, Gravity.CENTER, 0, 0);
                }
            }

        }

    }

    override fun getItemCount(): Int {
        return data.size
    }


}
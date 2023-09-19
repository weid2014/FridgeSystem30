package com.jhteck.icebox.adapter

/**
 *@Description:(现场人员库存列表适配器)
 *@author wade
 *@date 2023/7/1 23:19
 */
import android.graphics.drawable.BitmapDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hele.mrd.app.lib.base.BaseApp
import com.jhteck.icebox.R
import com.jhteck.icebox.api.AvailRfid
import com.jhteck.icebox.databinding.RvDrugContentItemBinding
import com.jhteck.icebox.fragment.InventoryListFrag
import com.jhteck.icebox.utils.CellNumberUtil
import com.jhteck.icebox.utils.DensityUtil
import com.jhteck.icebox.utils.PatternUtil

class InventoryListItemAdapterScener(
    private val frag: InventoryListFrag,
    private val data: List<AvailRfid>,
    private val operator: ItemEditCellNumberAdapter<AvailRfid>
) :
    RecyclerView.Adapter<InventoryListItemAdapterScener.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            frag,
            RvDrugContentItemBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(data[position], operator)
    }

    class ViewHolder(
        private val frag: InventoryListFrag,
        private val binding: RvDrugContentItemBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(item: AvailRfid, operator: ItemEditCellNumberAdapter<AvailRfid>) {
            //商品编号
            val tempName = PatternUtil.removeDigitalAndLetter(item.material?.eas_material_name)
            binding.tvDrugName.text = tempName
            //商品名称
            binding.tvDrugNumber.text = item.material?.eas_material_number
            //商品规格
            binding.tvDrugSpec.text = "${item.remain}试剂/盒"
            //生产厂家
            binding.tvDrugFactory.text = item.eas_supplier_name
            //单位
            binding.tvDrugUnit.text = item.material?.eas_unit_name
            //数量
            binding.tvNumber.text = "1"
            //试剂余量
            binding.tvDrugMargin.text = "${item.remain}%"
            //批号
            binding.tvBatchNumber.text = item.material?.eas_unit_number
            //有效期
            val showDate = item.material_batch?.expired_at.substring(0, 10)
            binding.tvValidityDate.text = showDate
            //效期状态
            var validityStatus = "未过期"
            if (item.is_out_eas) {
                binding.tvValidityStatus.text = "过期"
                binding.tvValidityStatus.setTextColor(BaseApp.app.getColor(R.color.app_color_ff3030))
            } else {
                binding.tvValidityStatus.text = "未过期"
                binding.tvValidityStatus.setTextColor(BaseApp.app.getColor(R.color.app_color_00d88e))
            }
            //位置
            binding.tvCellNumber.text = CellNumberUtil.getCellNumberText(item.cell_number)

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
                    val tempList = mutableListOf<AvailRfid>()
                    tempList.add(item)
                    rvRfidInfo.adapter = RfidItemAdapter(tempList)

                    btnClosePop.setOnClickListener {
                        DensityUtil.backgroundAlpha(frag.activity, 1f)
                        dismiss()
                    }
                    showAtLocation(binding.root, Gravity.CENTER, 0, 0);
                }
            }

            binding.btnEditCellNumber.setOnClickListener {
                PopupWindow().apply {
                    //入口参数配置
                    val layoutInflater = LayoutInflater.from(BaseApp.app)
                    contentView =
                        layoutInflater.inflate(R.layout.pup_select_cell_number, null)
                    width = ViewGroup.LayoutParams.WRAP_CONTENT
                    height = ViewGroup.LayoutParams.WRAP_CONTENT
                    isTouchable = true
                    isFocusable = true
                    isOutsideTouchable = false
                    setBackgroundDrawable(BitmapDrawable())
                    DensityUtil.backgroundAlpha(frag.activity, 0.5f)
                    //设置按钮
                    val btnClosePop = contentView.findViewById<Button>(R.id.btn_cancle)
                    val btnSave = contentView.findViewById<Button>(R.id.btn_save)

                    val rgLayer =
                        contentView.findViewById<com.jhteck.icebox.view.CustomNestRadioGroup>(R.id.rg_layer)
                    val rgLeftRight =
                        contentView.findViewById<com.jhteck.icebox.view.CustomNestRadioGroup>(R.id.rg_left_right)

                    val tvSelectLeft = contentView.findViewById<TextView>(R.id.tv_select_left)
                    val tvSelectMiddle = contentView.findViewById<TextView>(R.id.tv_select_middle)
                    val tvSelectRight = contentView.findViewById<TextView>(R.id.tv_select_right)
                    val tvSelectLayer = contentView.findViewById<TextView>(R.id.tv_select_layer)
                    tvSelectLeft.setBackgroundResource(R.drawable.app_shape_green_radius_9)
                    rgLeftRight.setOnCheckedChangeListener { group, checkedId ->
                        tvSelectLeft.setBackgroundResource(R.drawable.app_shape_white_radius_9)
                        tvSelectMiddle.setBackgroundResource(R.drawable.app_shape_white_radius_9)
                        tvSelectRight.setBackgroundResource(R.drawable.app_shape_white_radius_9)
                        tvSelectLeft.setTextColor(BaseApp.app.getColor(R.color.app_black))
                        tvSelectMiddle.setTextColor(BaseApp.app.getColor(R.color.app_black))
                        tvSelectRight.setTextColor(BaseApp.app.getColor(R.color.app_black))
                        val checkedButton: RadioButton = contentView.findViewById(checkedId)
                        when (checkedButton.text) {
                            "左" -> {
                                tvSelectLeft.setBackgroundResource(R.drawable.app_shape_green_radius_9)
                                tvSelectLeft.setTextColor(BaseApp.app.getColor(R.color.app_white))
                            }
                            "中" -> {
                                tvSelectMiddle.setBackgroundResource(R.drawable.app_shape_green_radius_9)
                                tvSelectMiddle.setTextColor(BaseApp.app.getColor(R.color.app_white))
                            }
                            "右" -> {
                                tvSelectRight.setBackgroundResource(R.drawable.app_shape_green_radius_9)
                                tvSelectRight.setTextColor(BaseApp.app.getColor(R.color.app_white))
                            }
                        }
                    }
                    rgLayer.setOnCheckedChangeListener { group, checkedId ->
                        val checkedButton: RadioButton = contentView.findViewById(checkedId)
                        tvSelectLayer.text = checkedButton.text
                    }

                    when (item.cell_number) {
                        2 -> {
                            contentView.findViewById<RadioButton>(R.id.rb_layer1).isChecked =
                                true
                            contentView.findViewById<RadioButton>(R.id.rb_left).isChecked = true
                        }
                        3 -> {
                            contentView.findViewById<RadioButton>(R.id.rb_layer1).isChecked =
                                true
                            contentView.findViewById<RadioButton>(R.id.rb_middle).isChecked =
                                true
                        }
                        4 -> {
                            contentView.findViewById<RadioButton>(R.id.rb_layer1).isChecked =
                                true
                            contentView.findViewById<RadioButton>(R.id.rb_right).isChecked =
                                true
                        }

                        5 -> {
                            contentView.findViewById<RadioButton>(R.id.rb_layer2).isChecked =
                                true
                            contentView.findViewById<RadioButton>(R.id.rb_left).isChecked = true
                        }
                        6 -> {
                            contentView.findViewById<RadioButton>(R.id.rb_layer2).isChecked =
                                true
                            contentView.findViewById<RadioButton>(R.id.rb_middle).isChecked =
                                true
                        }
                        7 -> {
                            contentView.findViewById<RadioButton>(R.id.rb_layer2).isChecked =
                                true
                            contentView.findViewById<RadioButton>(R.id.rb_right).isChecked =
                                true
                        }

                        8 -> {
                            contentView.findViewById<RadioButton>(R.id.rb_layer3).isChecked =
                                true
                            contentView.findViewById<RadioButton>(R.id.rb_left).isChecked = true
                        }
                        9 -> {
                            contentView.findViewById<RadioButton>(R.id.rb_layer3).isChecked =
                                true
                            contentView.findViewById<RadioButton>(R.id.rb_middle).isChecked =
                                true
                        }
                        10 -> {
                            contentView.findViewById<RadioButton>(R.id.rb_layer3).isChecked =
                                true
                            contentView.findViewById<RadioButton>(R.id.rb_right).isChecked =
                                true
                        }

                        11 -> {
                            contentView.findViewById<RadioButton>(R.id.rb_layer4).isChecked =
                                true
                            contentView.findViewById<RadioButton>(R.id.rb_left).isChecked = true
                        }
                        12 -> {
                            contentView.findViewById<RadioButton>(R.id.rb_layer4).isChecked =
                                true
                            contentView.findViewById<RadioButton>(R.id.rb_middle).isChecked =
                                true
                        }
                        13 -> {
                            contentView.findViewById<RadioButton>(R.id.rb_layer4).isChecked =
                                true
                            contentView.findViewById<RadioButton>(R.id.rb_right).isChecked =
                                true
                        }

                        14 -> {
                            contentView.findViewById<RadioButton>(R.id.rb_layer5).isChecked =
                                true
                            contentView.findViewById<RadioButton>(R.id.rb_left).isChecked = true
                        }
                        15 -> {
                            contentView.findViewById<RadioButton>(R.id.rb_layer5).isChecked =
                                true
                            contentView.findViewById<RadioButton>(R.id.rb_middle).isChecked =
                                true
                        }
                        16 -> {
                            contentView.findViewById<RadioButton>(R.id.rb_layer5).isChecked =
                                true
                            contentView.findViewById<RadioButton>(R.id.rb_right).isChecked =
                                true
                        }
                        else -> {
                            contentView.findViewById<RadioButton>(R.id.rb_layer1).isChecked =
                                true
                            contentView.findViewById<RadioButton>(R.id.rb_left).isChecked = true
                        }

                    }
                    btnClosePop.setOnClickListener {
                        DensityUtil.backgroundAlpha(frag.activity, 1f)
                        dismiss()
                    }
                    btnSave.setOnClickListener {
                        val checkRadioButtonLayer =
                            contentView.findViewById<RadioButton>(rgLayer.checkedRadioButtonId)
                        val layerText = checkRadioButtonLayer.text.toString()

                        val checkRadioButtonLeftRight =
                            contentView.findViewById<RadioButton>(rgLeftRight.checkedRadioButtonId)
                        val leftRightText = checkRadioButtonLeftRight.text.toString()
                        item.cell_number =
                            CellNumberUtil.getCellNumberTextByStr(layerText, leftRightText)
                        operator?.onEdit(item, position)
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
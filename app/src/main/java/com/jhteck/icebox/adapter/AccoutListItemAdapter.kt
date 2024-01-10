package com.jhteck.icebox.adapter

/**
 *@Description:(账号列表适配器)
 *@author wade
 *@date 2023/7/1 23:19
 */
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hele.mrd.app.lib.base.BaseApp
import com.jhteck.icebox.api.ROLE_ID
import com.jhteck.icebox.databinding.RvAccoutContentItemBinding
import com.jhteck.icebox.repository.entity.AccountEntity
import com.jhteck.icebox.utils.SharedPreferencesUtils

class AccoutListItemAdapter(
    private val data: List<AccountEntity>,
    private val operator: ItemOperatorAdapter<AccountEntity>
) :
    RecyclerView.Adapter<AccoutListItemAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            RvAccoutContentItemBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(data[position], operator)
    }

    class ViewHolder(private val binding: RvAccoutContentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(item: AccountEntity, operator: ItemOperatorAdapter<AccountEntity>) {
            //姓名
            binding.tvNickName.text = item.name
            //用户名
            binding.tvUserName.text = item.user_name
            binding.tvUserID.text = item.user_number

            val roleID = SharedPreferencesUtils.getPrefInt(BaseApp.app, ROLE_ID, 10)

            var roleValue = "系统管理员";
            when (item.role) {
                10 -> {
                    roleValue = "系统管理员"
                    if (roleID == 10) {
                        //如果是系统管理员登录，可以编辑系统管理员的账号，
                        binding.btnEdit.visibility = View.VISIBLE
                        binding.btnDelete.visibility = View.INVISIBLE
                    } else {
                        binding.btnEdit.visibility = View.INVISIBLE
                        binding.btnDelete.visibility = View.INVISIBLE
                    }
                }
                2 -> {
                    roleValue = "冰箱管理员"
                    if (roleID == 10 || roleID == 2) {
                        //如果是系统管理员或者仓库管理员登录，可以编辑仓库管理员的账号，
                        binding.btnEdit.visibility = View.VISIBLE
                        binding.btnDelete.visibility = View.VISIBLE
                    } else {
                        binding.btnEdit.visibility = View.INVISIBLE
                        binding.btnDelete.visibility = View.INVISIBLE
                    }
                }
                3 -> roleValue = "冰箱普通用户";
            }
            binding.btnEdit.setOnClickListener {
                operator?.onEdit(item)
            }
            binding.btnDelete.visibility=View.GONE
            binding.btnDelete.setOnClickListener {
                operator?.onDelete(item)
            }

            //生产厂家
            binding.tvAccountType.text = roleValue
            //单位
            var nfcId="未录入"
            if (item.fridge_nfc_1.isNotEmpty()){
                nfcId=item.fridge_nfc_1
            }else if (item.fridge_nfc_2.isNotEmpty()){
                nfcId=item.fridge_nfc_2
            }else if (item.fridge_nfc_3.isNotEmpty()){
                nfcId=item.fridge_nfc_3
            }else if (item.fridge_nfc_4.isNotEmpty()){
                nfcId=item.fridge_nfc_4
            }else if (item.fridge_nfc_5.isNotEmpty()){
                nfcId=item.fridge_nfc_5
            }
            binding.tvNFC.text = nfcId
            //数量
            binding.tvInNumber.text = item.store_count.toString()
            //试剂余量
            binding.tvOutNumber.text = item.consume_count.toString()
            //暂存
            binding.tvPauseNumber.text = item.deposit_count.toString()

        }

    }

    override fun getItemCount(): Int {
        return data.size
    }


}
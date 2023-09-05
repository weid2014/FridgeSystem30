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
            //商品编号
            binding.tvNickName.text = item.nick_name
            //商品名称
            binding.tvUserName.text = item.real_name
            //商品规格
            binding.tvUserID.text = item.km_user_id

            val roleID = SharedPreferencesUtils.getPrefInt(BaseApp.app, ROLE_ID, 10)

            var roleValue = "系统管理员";
            when (item.role_id) {
                "10" -> {
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
                "20" -> {
                    roleValue = "仓库管理员";
                    if (roleID == 10 || roleID == 20) {
                        //如果是系统管理员或者仓库管理员登录，可以编辑仓库管理员的账号，
                        binding.btnEdit.visibility = View.VISIBLE
                        binding.btnDelete.visibility = View.VISIBLE
                    } else {
                        binding.btnEdit.visibility = View.INVISIBLE
                        binding.btnDelete.visibility = View.INVISIBLE
                    }
                }
                "30" -> roleValue = "现场人员";
            }
            binding.btnEdit.setOnClickListener {
                operator?.onEdit(item)
            }
            binding.btnDelete.setOnClickListener {
                operator?.onDelete(item)
            }

            //生产厂家
            binding.tvAccountType.text = roleValue
            //单位
            binding.tvNFC.text = item.nfc_id
            //数量
            binding.tvInNumber.text =item.store_count.toString()
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
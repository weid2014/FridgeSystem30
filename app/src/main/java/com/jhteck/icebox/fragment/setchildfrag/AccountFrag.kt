package com.jhteck.icebox.fragment.setchildfrag

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.hele.mrd.app.lib.base.BaseApp
import com.hele.mrd.app.lib.base.BaseFragment
import com.jhteck.icebox.Lockmodel.LockManage
import com.jhteck.icebox.R
import com.jhteck.icebox.adapter.AccoutListItemAdapter
import com.jhteck.icebox.adapter.ItemOperatorAdapter
import com.jhteck.icebox.adapter.OperaRecordListAdapter
import com.jhteck.icebox.api.*
import com.jhteck.icebox.bean.MyTcpMsg
import com.jhteck.icebox.databinding.AppFragmentSettingAccoutBinding
import com.jhteck.icebox.fragment.SettingFrag
import com.jhteck.icebox.repository.entity.AccountEntity
import com.jhteck.icebox.repository.entity.RfidOperationEntity
import com.jhteck.icebox.utils.*
import com.jhteck.icebox.utils.BroadcastUtil.sendMyBroadcast
import com.jhteck.icebox.viewmodel.AccountViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


/**
 *@Description:(账号和权限页面Fragment)
 *@author wade
 *@date 2023/6/28 21:59
 */
class AccountFrag : BaseFragment<AccountViewModel, AppFragmentSettingAccoutBinding>() {

    private var popupWindowEdit: PopupWindow? = null
    private var inAccountPage: Boolean = false

    companion object {
        fun newInstance(): AccountFrag {
            return AccountFrag()
        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): AppFragmentSettingAccoutBinding {
        return AppFragmentSettingAccoutBinding.inflate(layoutInflater)
    }

    override fun createViewModel(): AccountViewModel {
        return viewModels<AccountViewModel>().value
    }

    private val frag by lazy {
        parentFragment as SettingFrag
    }

    override fun tryLoadData() {
        super.tryLoadData()
        viewModel.getAllUsers()
    }

    override fun initView() {
        binding.btnNewAccount.visibility=View.GONE
        binding.fullscreenView.setOnClickListener {
            LockManage.getInstance().tryOpenLock();
        }
        initRecyclerView()
        //监听搜索栏里面的变化
        binding.svSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                //当提交了输入时
                return false
            }

            override fun onQueryTextChange(keyword: String?): Boolean {
                // 当修改了输入时的操作，根据关键字过滤列表，让Adapter填入新列表
                // 如果只是更新部分数据，推荐使用notifyItemRangeChanged()或者notifyItemChanged()
                val filterList = keyword?.let { filter(it) }
                filterList?.let { updateList(it) }
//                testList= filterList as MutableList<AccountEntity>
//                updateList(testList)
                inventoryListItemAdapter?.notifyDataSetChanged()
                binding.rvContentSettingAccount.adapter?.notifyDataSetChanged()
                return false
            }
        })
        doRegisterReceiver()
    }

    private fun filter(keyWord: String): List<AccountEntity> {
        // 过滤原本的列表，返回一个新的列表
        val filterList = mutableListOf<AccountEntity>()

        for (l in testList!!) {
            if (l.nick_name.contains(keyWord) || l.user_id.contains(keyWord))
                filterList.add(l)
        }
        return filterList
    }

    var testList: MutableList<AccountEntity> = mutableListOf()
    var inventoryListItemAdapter:AccoutListItemAdapter?=null
    //初始化用户列表
    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(BaseApp.app)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.rvContentSettingAccount.layoutManager = layoutManager
        viewModel.onFailed.observe(this) {
            Log.d("onFailed", it.toString())
        }
        viewModel.onUsersLoaded.observe(this) {
            testList = mutableListOf<AccountEntity>()
            when(SharedPreferencesUtils.getPrefInt(BaseApp.app, ROLE_ID, 10)){
                10->{
                    for (user in it) {
                        testList.add(user)
                    }
                }
                else->{
                    for (user in it) {
                        if(user.role.toInt()!=10) {
                            testList.add(user)
                        }
                    }
                }
            }
            updateList(testList)

        }
        viewModel.getAllUsers()
    }

    fun updateList(list: List<AccountEntity>) {
        inventoryListItemAdapter = AccoutListItemAdapter(list,
            object : ItemOperatorAdapter<AccountEntity> {
                override fun onDelete(t: AccountEntity) {
                    val customDialog = CustomDialog(requireActivity())
                    customDialog.setsTitle("温馨提示").setsMessage("是否删除（${t.nick_name})账号？")
                        .setsCancel("取消", View.OnClickListener {
                            customDialog.dismiss()
                        }).setsConfirm("确定", View.OnClickListener {
                            viewModel.delete(t)//删除用户
                            customDialog.dismiss()
                        }).show()

                }

                override fun onEdit(t: AccountEntity) {
                    showEditAccountPopWindow(t)
                }
            })
        binding.rvContentSettingAccount.adapter = inventoryListItemAdapter
        inventoryListItemAdapter?.notifyDataSetChanged()
        binding.rvContentSettingAccount.adapter?.notifyDataSetChanged()
    }

    private var tv_nfcId: TextView? = null
    private var ll_nfc_id: LinearLayout? = null
    fun showEditAccountPopWindow(item: AccountEntity) {
        //弹出结算界面
        popupWindowEdit = PopupWindow().apply {
            inAccountPage = true
            //入口参数配置
            val layoutInflater = LayoutInflater.from(BaseApp.app)
            contentView =
                layoutInflater.inflate(R.layout.pup_new_account, null)
            width = ViewGroup.LayoutParams.WRAP_CONTENT
            height = ViewGroup.LayoutParams.WRAP_CONTENT

            isTouchable = true
            isFocusable = true
            isOutsideTouchable = false
            setBackgroundDrawable(BitmapDrawable())
//            setTouchInterceptor(OnTouchListener { v, event -> true })
            DbUtil.setPopupWindowTouchModal(this, false)
            DensityUtil.backgroundAlpha(requireActivity(), 0.5f)
            update()
            //设置按钮
            val btnClosePop = contentView.findViewById<Button>(R.id.btn_back)

            btnClosePop.setOnClickListener {
                hidePopupWindow()
            }

            val btnSavePop = contentView.findViewById<Button>(R.id.btn_save)
            val edUsername = contentView.findViewById<EditText>(R.id.edUserName)
            edUsername.setText(item.user_name)
            tv_nfcId = contentView.findViewById<TextView>(R.id.tv_nfcId)
            ll_nfc_id = contentView.findViewById<LinearLayout>(R.id.ll_nfc_id)
            val tvTips = contentView.findViewById<TextView>(R.id.tv_tips)
            tv_nfcId?.setText(item.nfc_id)
            changeNfcBackground(true)
            val rgNfcCard = contentView.findViewById<RadioGroup>(R.id.rg_nfc_card)
            val rb_card1 = contentView.findViewById<RadioButton>(R.id.rb_card1)
            rb_card1.isChecked=true
            val rb_card2 = contentView.findViewById<RadioButton>(R.id.rb_card2)
            val rb_card3 = contentView.findViewById<RadioButton>(R.id.rb_card3)
            val rb_card4 = contentView.findViewById<RadioButton>(R.id.rb_card4)
            val rb_card5 = contentView.findViewById<RadioButton>(R.id.rb_card5)
            rb_card1.setOnClickListener {
                tv_nfcId?.setText(item.fridge_nfc_1)
            }
            rb_card2.setOnClickListener {
                tv_nfcId?.setText(item.fridge_nfc_2)
            }
            rb_card3.setOnClickListener {
                tv_nfcId?.setText(item.fridge_nfc_3)
            }
            rb_card4.setOnClickListener {
                tv_nfcId?.setText(item.fridge_nfc_4)
            }
            rb_card5.setOnClickListener {
                tv_nfcId?.setText(item.fridge_nfc_5)
            }
            val fullscreenView = contentView.findViewById<View>(R.id.fullscreen_view)
            fullscreenView.setOnClickListener {
                LockManage.getInstance().tryOpenLock();
            }
            fullscreenView.setOnTouchListener { view, motionEvent ->
                LockManage.getInstance().tryOpenLock();
                false
            }
            btnSavePop.setOnClickListener {

                if ( tv_nfcId?.text.toString().length == 0
                ) {
                    tvTips.setText("请输入必填项，请检查！")
                } else {
//                //获取账户信息
                    item.user_name = edUsername.text.trim().toString()
                    var checkRadioButton =
                        contentView.findViewById<RadioButton>(rgNfcCard.checkedRadioButtonId)
                    var roleText = checkRadioButton.text
                    when (roleText) {
                        "NFC_1" -> item.fridge_nfc_1 = tv_nfcId?.text.toString()
                        "NFC_2" -> item.fridge_nfc_2 = tv_nfcId?.text.toString()
                        "NFC_3" -> item.fridge_nfc_3 = tv_nfcId?.text.toString()
                        "NFC_4" -> item.fridge_nfc_4 = tv_nfcId?.text.toString()
                        "NFC_5" -> item.fridge_nfc_5 = tv_nfcId?.text.toString()
                    }
                    item.nfc_id = tv_nfcId?.text.toString()

                    //todo 删除旧的人脸数据，更新最新的人脸数据

                    viewModel.update(item);//更新
                }
            }
            showAtLocation(binding.root, Gravity.CENTER, width / 2, 0);
            DbUtil.setPopupWindowTouchModal(this, false)
        }

    }



    fun hidePopupWindow() {
        if (popupWindowEdit != null) {
            popupWindowEdit?.dismiss()
        }
        popupWindowEdit = null
        inAccountPage = false
        DensityUtil.backgroundAlpha(requireActivity(), 1f)
    }

    override fun initObservables() {
        super.initObservables()
        viewModel.addStatus.observe(this) {
            hidePopupWindow()
        }
        viewModel.updateUserInfo.observe(this) {
            sendMyBroadcast(this.requireContext(), UPDATE_ACCOUNT_MSG, Gson().toJson(it))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mReceiver != null) {
            activity?.let {
                it.unregisterReceiver(mReceiver);
            }
        }
    }


    /**
     * 注册广播接收者
     */
    private var mReceiver: ContentReceiver? = null
    private fun doRegisterReceiver() {
        mReceiver = ContentReceiver()
        val filter = IntentFilter(
            BROADCAST_INTENT_FILTER
        )
        activity?.let {
            it.registerReceiver(mReceiver, filter)
        }
    }

    inner class ContentReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val key = intent.getStringExtra(TCP_MSG_KEY)
            val value = intent.getStringExtra(TCP_MSG_VALUE)
            if (key == SYNC_ACCOUNT_MSG) {
                viewModel.getAllUsers()
            } else if(key== HFCard){
                if (inAccountPage) {
                    compareRfid(MyTcpMsg(key, value))
                }
            }
        }
    }

    fun compareRfid(myTcpMsg: MyTcpMsg) {
        CoroutineScope(Dispatchers.Main).launch {
            if (myTcpMsg.type.equals(HFCard)) {
                val accountDao = DbUtil.getDb().accountDao().findByNfcId(myTcpMsg.content)
                val userInfo = DbUtil.getDb().userDao().findByNfcId(myTcpMsg.content)
                if (accountDao != null || userInfo != null) {
                    tv_nfcId?.text = ""
                    changeNfcBackground(false)
                    Toast.makeText(activity, "${myTcpMsg.content} 已经注册", Toast.LENGTH_SHORT).show()
                } else {
                    tv_nfcId?.text = myTcpMsg.content.toString()
                    changeNfcBackground(true)
                }
            } else if (myTcpMsg.type.equals(LOCKED_SUCCESS)) {
                hidePopupWindow()
            }
        }
    }

    private fun changeNfcBackground(isChange: Boolean) {
        if (isChange) {
            tv_nfcId?.setTextColor(requireContext().getColor(R.color.app_white))
            ll_nfc_id?.setBackgroundResource(R.drawable.app_shape_green_radius_9)
        } else {
            tv_nfcId?.setTextColor(requireContext().getColor(R.color.app_black))
            ll_nfc_id?.setBackgroundResource(R.drawable.app_shape_white_radius_9)
        }
    }
}
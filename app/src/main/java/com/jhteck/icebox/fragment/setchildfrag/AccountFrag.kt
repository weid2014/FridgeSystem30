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
import com.jhteck.icebox.api.*
import com.jhteck.icebox.bean.MyTcpMsg
import com.jhteck.icebox.databinding.AppFragmentSettingAccoutBinding
import com.jhteck.icebox.repository.entity.AccountEntity
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

    private var popupWindowAdd: PopupWindow? = null
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
        parentFragment as AccountFrag
    }

    override fun initView() {
        binding.btnNewAccount.setOnClickListener {
            showNewAccountPopWindow()
        }
        binding.fullscreenView.setOnClickListener {
            Log.d("AccountFrag", "fullscreenView")
            LockManage.getInstance().tryOpenLock();
        }
        initRecyclerView()
        doRegisterReceiver()
    }


    //初始化用户列表
    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(BaseApp.app)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.rvContentSettingAccount.layoutManager = layoutManager
        viewModel.onFailed.observe(this) {
            Log.d("onFailed", it.toString())
        }
        viewModel.onUsersLoaded.observe(this) {
            val testList = mutableListOf<AccountEntity>()
            for (user in it) {
                testList.add(user)
            }
            val inventoryListItemAdapter = AccoutListItemAdapter(testList,
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
        }
        viewModel.getAllUsers()
    }

    private var tv_nfcId: TextView? = null
    private var ll_nfc_id: LinearLayout? = null
    fun showEditAccountPopWindow(item: AccountEntity) {
        val roleID = SharedPreferencesUtils.getPrefInt(BaseApp.app, ROLE_ID, 10)
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
            val edUsername = contentView.findViewById<EditText>(R.id.ed_UserName)
            edUsername.setText(item.nick_name)
            val edPassWord = contentView.findViewById<EditText>(R.id.ed_PassWord)
            val edUserId = contentView.findViewById<EditText>(R.id.ed_UserId)
            edUserId.setText(item.km_user_id)
            val edCommitPassWord = contentView.findViewById<EditText>(R.id.ed_CommitPassword)
            tv_nfcId = contentView.findViewById<TextView>(R.id.tv_nfcId)
            ll_nfc_id = contentView.findViewById<LinearLayout>(R.id.ll_nfc_id)
            val tvTips = contentView.findViewById<TextView>(R.id.tv_tips)
            tv_nfcId?.setText(item.nfc_id)
            changeNfcBackground(true)
            val tv_Title = contentView.findViewById<TextView>(R.id.tv_Title)
            val roleId = contentView.findViewById<RadioGroup>(R.id.rg_role)
            val rbRole0 = contentView.findViewById<RadioButton>(R.id.rb_role0)
            val rbRole1 = contentView.findViewById<RadioButton>(R.id.rb_role1)
            val rbRole2 = contentView.findViewById<RadioButton>(R.id.rb_role2)
            val fullscreenView = contentView.findViewById<View>(R.id.fullscreen_view)
            fullscreenView.setOnClickListener {
                LockManage.getInstance().tryOpenLock();
            }
            fullscreenView.setOnTouchListener { view, motionEvent ->
                LockManage.getInstance().tryOpenLock();
                false
            }
            tv_Title.text = "编辑账号"
            when (roleID) {
                10 -> {
                    if (item.role_id.toInt() == 10) {
                        rbRole0.visibility = View.VISIBLE
                        rbRole1.visibility = View.GONE
                        rbRole2.visibility = View.GONE
                        rbRole0.isChecked = true
                    } else if (item.role_id.toInt() == 20) {
                        rbRole0.visibility = View.GONE
                        rbRole1.visibility = View.VISIBLE
                        rbRole2.visibility = View.VISIBLE
                        rbRole1.isChecked = true
                    } else {
                        rbRole0.visibility = View.GONE
                        rbRole1.visibility = View.VISIBLE
                        rbRole2.visibility = View.VISIBLE
                        rbRole2.isChecked = true
                    }
                }
                20 -> {
                    if (item.role_id.toInt() == 20) {
                        rbRole0.visibility = View.GONE
                        rbRole1.visibility = View.VISIBLE
                        rbRole2.visibility = View.VISIBLE
                        rbRole1.isChecked = true
                    } else {
                        rbRole0.visibility = View.GONE
                        rbRole1.visibility = View.VISIBLE
                        rbRole2.visibility = View.VISIBLE
                        rbRole2.isChecked = true
                    }
                }
            }
            //admin可以新建仓库管理员和现场人员，仓库管理员可以新建现场人员账号

            btnSavePop.setOnClickListener {

                if (edPassWord.text.toString() == "" || edCommitPassWord.text.toString()
                    == "" || edUsername.text.toString() == ""
                    || edUserId.text.toString()
                    == "" || tv_nfcId?.text.toString().length == 0
                ) {
                    tvTips.setText("请输入必填项，请检查！")
                } else if (edPassWord.text.toString() != edCommitPassWord.text.toString()) {
                    tvTips.setText("密码不一致，请检查！")
                } else {
//                //获取账户信息
//                var user = AccountEntity();
                    item.nick_name = edUsername.text.toString();
                    item.password_digest = MD5Util.encrypt(edPassWord.text.toString());
                    item.real_name = edUsername.text.toString();
                    item.nfc_id = tv_nfcId?.text.toString();
                    item.km_user_id = edUserId.text.toString();

                    var checkRadioButton =
                        contentView.findViewById<RadioButton>(roleId.checkedRadioButtonId)
                    var roleText = checkRadioButton.text
                    when (roleText) {
                        "系统管理员" -> item.role_id = "10"
                        "仓库管理员" -> item.role_id = "20"
                        "现场人员" -> item.role_id = "30"
                    }
                    //测试
                    val formatter = SimpleDateFormat("YYYY-MM-dd HH:mm:ss") //设置时间格式
                    val curDate = Date(System.currentTimeMillis()) //获取当前时间
                    val createDate: String = formatter.format(curDate) //格式转换
                    item.created_by = item.created_by
                    item.created_time = createDate
                    item.updated_time = createDate
                    item.login_time = createDate
                    item.updated_by =
                        SharedPreferencesUtils.getPrefInt(BaseApp.app, ROLE_ID, 10).toString()
                    //todo 删除旧的人脸数据，更新最新的人脸数据

                    viewModel.update(item);//更新
                }
            }
            showAtLocation(binding.root, Gravity.CENTER, width / 2, 0);
            DbUtil.setPopupWindowTouchModal(this, false)
        }

    }

    //新建账号页面
    private fun showNewAccountPopWindow() {
        val roleID = SharedPreferencesUtils.getPrefInt(BaseApp.app, ROLE_ID, 10)
        //弹出结算界面
        popupWindowAdd = PopupWindow().apply {
            inAccountPage = true
            //入口参数配置
            val layoutInflater = LayoutInflater.from(requireActivity())
            contentView =
                layoutInflater.inflate(R.layout.pup_new_account, null)
            width = ViewGroup.LayoutParams.WRAP_CONTENT
            height = ViewGroup.LayoutParams.WRAP_CONTENT
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                isTouchModal = false
            }
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
            val fullscreenView = contentView.findViewById<View>(R.id.fullscreen_view)
            fullscreenView.setOnClickListener {
                LockManage.getInstance().tryOpenLock();
            }
            fullscreenView.setOnTouchListener { view, motionEvent ->
                LockManage.getInstance().tryOpenLock();
                false
            }

            btnClosePop.setOnClickListener {
                hidePopupWindow()
            }

            val btnSavePop = contentView.findViewById<Button>(R.id.btn_save)
            val edUsername = contentView.findViewById<EditText>(R.id.ed_UserName)
            val edPassWord = contentView.findViewById<EditText>(R.id.ed_PassWord)
            val edUserId = contentView.findViewById<EditText>(R.id.ed_UserId)
            val edCommitPassWord = contentView.findViewById<EditText>(R.id.ed_CommitPassword)
            tv_nfcId = contentView.findViewById<TextView>(R.id.tv_nfcId)
            ll_nfc_id = contentView.findViewById<LinearLayout>(R.id.ll_nfc_id)
            val tvTips = contentView.findViewById<TextView>(R.id.tv_tips)
            val roleId = contentView.findViewById<RadioGroup>(R.id.rg_role)
            val rbRole1 = contentView.findViewById<RadioButton>(R.id.rb_role1)
            val rbRole2 = contentView.findViewById<RadioButton>(R.id.rb_role2)
            rbRole1.isChecked = true

            //admin可以新建仓库管理员和现场人员，仓库管理员可以新建现场人员账号
            if (roleID == 20) {
                rbRole1.visibility = View.GONE
                rbRole2.isChecked = true
            }
            //调试
            if (DEBUG) {
                var tempStr = "TEST8A858A115F604010100${SnowFlake.getInstance().nextId()}"
                tv_nfcId?.setText(tempStr.substring(0, 36))
                changeNfcBackground(true)
            }

            btnSavePop.setOnClickListener {
                if (edPassWord.text.toString() == "" || edCommitPassWord.text.toString()
                    == "" || edUsername.text.toString() == ""
                    || edUserId.text.toString()
                    == "" || tv_nfcId?.text.toString().length == 0
                ) {
                    tvTips.setText("请输入必填项，请检查！")
                } else if (edPassWord.text.toString() != edCommitPassWord.text.toString()) {
                    tvTips.setText("密码不一致，请检查！")
                } else {
//                //获取账户信息
                    var user = AccountEntity();
                    user.nick_name = edUsername.text.toString();
                    user.password_digest = MD5Util.encrypt(edPassWord.text.toString());
                    user.real_name = edUsername.text.toString();
                    user.nfc_id = tv_nfcId?.text.toString();
                    user.km_user_id = edUserId.text.toString();

                    var checkRadioButton =
                        contentView.findViewById<RadioButton>(roleId.checkedRadioButtonId)
                    var roleText = checkRadioButton.text
                    when (roleText) {
                        "仓库管理员" -> user.role_id = "20"
                        "现场人员" -> user.role_id = "30"
                    }
                    //测试
                    val formatter = SimpleDateFormat("YYYY-MM-dd HH:mm:ss") //设置时间格式
                    val curDate = Date(System.currentTimeMillis()) //获取当前时间
                    val createDate: String = formatter.format(curDate) //格式转换

                    user.user_id = SnowFlake.getInstance().nextId().toString()
                    user.created_by =
                        SharedPreferencesUtils.getPrefInt(BaseApp.app, ROLE_ID, 10).toString()
                    user.created_time = createDate
                    user.updated_time = createDate
                    user.login_time = createDate
                    viewModel.add(user);
                }
            }
            showAtLocation(binding.root, Gravity.CENTER, width / 2, 0);
            DbUtil.setPopupWindowTouchModal(this, false)
        }
    }


    fun hidePopupWindow() {
        if (popupWindowAdd != null) {
            popupWindowAdd?.dismiss()
        }
        popupWindowAdd = null
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
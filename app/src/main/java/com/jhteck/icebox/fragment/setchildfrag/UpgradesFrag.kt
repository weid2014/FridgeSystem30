package com.jhteck.icebox.fragment.setchildfrag

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import com.hele.mrd.app.lib.base.BaseApp
import com.hele.mrd.app.lib.base.WithoutViewModelFragment
import com.jhteck.icebox.R
import com.jhteck.icebox.api.UPDATE_APK_ADDRESS
import com.jhteck.icebox.api.UPDATE_JSON_ADDRESS
import com.jhteck.icebox.databinding.AppFragmentSettingUpgradesBinding
import com.jhteck.icebox.utils.NetworkUtil
import com.jhteck.icebox.utils.SharedPreferencesUtils
import com.jhteck.icebox.utils.UpdateAppUtil
import com.jhteck.icebox.utils.VersionUtil
import com.jhteck.icebox.viewmodel.UpgradesViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 *@Description:(远程升级页面Fragment)
 *@author wade
 *@date 2023/6/28 21:59
 */
class UpgradesFrag : WithoutViewModelFragment<AppFragmentSettingUpgradesBinding>() {
    private var isEditAddress = false

    companion object {
        fun newInstance(): UpgradesFrag {
            return UpgradesFrag()
        }
    }


    override fun createViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): AppFragmentSettingUpgradesBinding {
        return AppFragmentSettingUpgradesBinding.inflate(layoutInflater)
    }

    override fun createViewModel(): UpgradesViewModel {
        return viewModels<UpgradesViewModel>().value
    }

    private val frag by lazy {
        parentFragment as UpgradesFrag
    }

    override fun initView() {
        binding.tvVersion.text = "Version:${VersionUtil.getVersionName(BaseApp.app)}"

        binding.edUpdateJsonAddress.setText(
            SharedPreferencesUtils.getPrefString(
                requireContext(), UPDATE_JSON_ADDRESS, getString(
                    R.string.update_json_address
                )
            )
        )
        binding.edUpdateApkAddress.setText(
            SharedPreferencesUtils.getPrefString(
                requireContext(),
                UPDATE_APK_ADDRESS, getString(
                    R.string.update_apk_address
                )
            )
        )
        binding.btnUpdateSetting.setOnClickListener {
            isEditVisable()
        }
        binding.btnSave.setOnClickListener {
            var updateJsonAddress = binding.edUpdateJsonAddress.text.toString().trim()
            var updateApkAddress = binding.edUpdateApkAddress.text.toString().trim()
            if (updateJsonAddress.isNotEmpty() && updateApkAddress.isNotEmpty()) {
                SharedPreferencesUtils.setPrefString(
                    requireContext(),
                    UPDATE_JSON_ADDRESS,
                    updateJsonAddress
                )
                SharedPreferencesUtils.setPrefString(
                    requireContext(),
                    UPDATE_APK_ADDRESS,
                    updateApkAddress
                )
                isEditVisable()
            } else {
                Toast.makeText(requireContext(), "请输入正确地址", Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnCancle.setOnClickListener {
            isEditVisable()
        }

        binding.llAddress.visibility = View.GONE
        binding.btnUpdate.setOnClickListener {
            checkNetworkStatus(requireContext())
        }
    }

    private fun isEditVisable(){
        isEditAddress = !isEditAddress
        if (isEditAddress) {
            binding.llAddress.visibility = View.VISIBLE
        } else {
            binding.llAddress.visibility = View.GONE
        }
    }

    fun checkNetworkStatus(context: Context) {
        //判断网络状态
        CoroutineScope(Dispatchers.Default).launch {

            if (NetworkUtil.isNetSystemUsable(context)) {
                if (NetworkUtil.isNetOnline()) {
                    //地址过后可能要换成内网地址
                    Toast.makeText(context,"当前网络状态:可用",Toast.LENGTH_SHORT).show()
                    try {
                        UpdateAppUtil().checkVision(context)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    Toast.makeText(context,"ping不通啊啊啊啊啊",Toast.LENGTH_SHORT).show()
                }
//            networkStatus.postValue(true)
            } else {
                Toast.makeText(context,"当前网络状态:不可用!!!!",Toast.LENGTH_SHORT).show()
            }


        }
    }

}
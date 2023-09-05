package com.jhteck.icebox.fragment

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import com.hele.mrd.app.lib.base.BaseApp
import com.hele.mrd.app.lib.base.BaseFragment
import com.jhteck.icebox.R
import com.jhteck.icebox.adapter.AppTabPagerAdapter
import com.jhteck.icebox.api.ROLE_ID
import com.jhteck.icebox.databinding.AppFragmentSettingBinding
import com.jhteck.icebox.fragment.setchildfrag.AccountFrag
import com.jhteck.icebox.fragment.setchildfrag.DeviceSettingFrag
import com.jhteck.icebox.fragment.setchildfrag.UpgradesFrag
import com.jhteck.icebox.utils.SharedPreferencesUtils
import com.jhteck.icebox.viewmodel.SettingViewModel

/**
 *@Description:(设置页面Fragment)
 *@author wade
 *@date 2023/6/28 21:59
 */
class SettingFrag : BaseFragment<SettingViewModel, AppFragmentSettingBinding>() {
    private val tabs = arrayListOf<TextView>()
    private var currentSelectItem = 0
    val accountFrag = AccountFrag.newInstance()
    companion object {
        fun newInstance(): SettingFrag {
            return SettingFrag()
        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): AppFragmentSettingBinding {
        return AppFragmentSettingBinding.inflate(layoutInflater)
    }

    override fun createViewModel(): SettingViewModel {
        return viewModels<SettingViewModel>().value
    }

    private val frag by lazy {
        parentFragment as SettingFrag
    }

    override fun initView() {
        //获取用户角色ID
        val roleID = SharedPreferencesUtils.getPrefInt(BaseApp.app, ROLE_ID, 10)


        when (roleID) {
            10, 20 -> {

                val upgradesFrag = UpgradesFrag.newInstance()
                val deviceSettingFrag = DeviceSettingFrag.newInstance()
                tabs.add(binding.tvAccount)
                tabs.add(binding.tvUpgrades)
                tabs.add(binding.tvDeviceSetting)
                binding.vpContentSetting.adapter =
                    AppTabPagerAdapter(
                        arrayListOf(accountFrag, upgradesFrag, deviceSettingFrag),
                        childFragmentManager,
                        lifecycle
                    )
                binding.vpContentSetting.isSaveEnabled = false
                binding.vpContentSetting.isUserInputEnabled = false
                binding.rlAccount.visibility = View.VISIBLE
                binding.tvAccount.setOnClickListener {
                    setupSelectItem(0)
                }
                binding.tvUpgrades.setOnClickListener {
                    setupSelectItem(1)

                }
                binding.tvDeviceSetting.setOnClickListener {
                    setupSelectItem(2)
                }
            }
            else -> {
                val upgradesFrag = UpgradesFrag.newInstance()
                val deviceSettingFrag = DeviceSettingFrag.newInstance()
                tabs.add(binding.tvUpgrades)
                tabs.add(binding.tvDeviceSetting)
                binding.vpContentSetting.adapter =
                    AppTabPagerAdapter(
                        arrayListOf(upgradesFrag, deviceSettingFrag),
                        childFragmentManager,
                        lifecycle
                    )
                binding.vpContentSetting.isSaveEnabled = false
                binding.vpContentSetting.isUserInputEnabled = false
                binding.rlAccount.visibility = View.GONE
                binding.tvUpgrades.setOnClickListener {
                    setupSelectItem(0)

                }
                binding.tvDeviceSetting.setOnClickListener {
                    setupSelectItem(1)
                }
                tabs[0].setBackgroundResource(R.drawable.radius_border_tab)
            }

        }
        setupSelectItem(0)

    }

    private fun setupSelectItem(position: Int) {
        if (position == currentSelectItem) {
            return
        }
        tabs[currentSelectItem].isSelected = false
        tabs[position].isSelected = true
        for (tab in tabs) {
            tab.background = resources.getDrawable(R.drawable.radius_border_tab_unselect)
        }
        tabs[position].background = resources.getDrawable(R.drawable.radius_border_tab)
        currentSelectItem = position
        binding.vpContentSetting.setCurrentItem(position, false)

    }

    override fun initObservables() {
        super.initObservables()

    }


}
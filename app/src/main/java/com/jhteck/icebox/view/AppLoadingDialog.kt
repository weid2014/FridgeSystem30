package com.jhteck.icebox.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.hele.mrd.app.lib.base.ext.toPx
import com.hele.mrd.app.lib.base.view.ILoading
import com.jhteck.icebox.databinding.AppDialogLoadingBinding

class AppLoadingDialog : ILoading<AppDialogLoadingBinding>() {

    companion object {

        private const val KEY_CONTENT = "key_content"

        fun newInstance(content: String?): ILoading<AppDialogLoadingBinding> {
            val bundle = Bundle()
            bundle.putString(KEY_CONTENT, content)
            val frag = AppLoadingDialog()
            frag.arguments = bundle
            return frag
        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): AppDialogLoadingBinding {
        return AppDialogLoadingBinding.inflate(inflater, container, false)
    }

    override fun tryLoadData() {
        binding.tvContent.text = arguments?.getString(KEY_CONTENT)
    }

    override fun dialogWidth(): Int {
        return 300.toPx().toInt()
    }

    override fun dialogHeight(): Int {
        return 300.toPx().toInt()
    }

    override fun initView() {
    }

}
package com.jhteck.icebox.viewmodel

import android.app.Application
import com.hele.mrd.app.lib.base.BaseViewModel
import com.jhteck.icebox.apiserver.ILoginApiService

/**
 *@Description:(操作记录ViewModel)
 *@author wade
 *@date 2023/6/28 22:04
 */
class OperationLogViewModel(application: Application) :BaseViewModel<ILoginApiService>(application){

    override fun createApiServiceType(): Class<ILoginApiService> {
        return ILoginApiService::class.java
    }


}
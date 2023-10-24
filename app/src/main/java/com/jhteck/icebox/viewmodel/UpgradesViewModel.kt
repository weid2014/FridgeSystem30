package com.jhteck.icebox.viewmodel

import android.content.Context
import android.os.SystemClock
import com.hele.mrd.app.lib.base.BaseViewModel
import com.jhteck.icebox.R
import com.jhteck.icebox.api.UPDATE_JSON_ADDRESS
import com.jhteck.icebox.api.UpdateInfoDto
import com.jhteck.icebox.apiserver.ILoginApiService
import com.jhteck.icebox.apiserver.RetrofitClient
import com.jhteck.icebox.utils.SharedPreferencesUtils
import com.jhteck.icebox.utils.ToastUtils
import constant.UiType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import model.UiConfig
import model.UpdateConfig
import update.UpdateAppUtils

class UpgradesViewModel(application: android.app.Application) :
    BaseViewModel<ILoginApiService>(application) {

    val Target = "UpgradesViewModel"
    override fun createApiServiceType(): Class<ILoginApiService> {
        return ILoginApiService::class.java
    }

    private var lastonclickTime = 0L;//全局变量
    fun checkVision(context: Context) {
        var time = SystemClock.uptimeMillis();//局部变量
        if (time - lastonclickTime <= 3000) {
        } else {
            lastonclickTime = time
            CoroutineScope(Dispatchers.IO).launch {
                showLoading("正在获取最新版本信息，请稍等...")
                try {
                    val updateInfo = RetrofitClient.getService().getUpdateInfo(
                        SharedPreferencesUtils.getPrefString(
                            context, UPDATE_JSON_ADDRESS, context.getString(
                                R.string.update_json_address
                            )
                        )
                    )
                    val packageInfo = context.packageManager
                        .getPackageInfo(context.packageName, 0)
                    val localVisionCode = packageInfo.versionCode
                    if (localVisionCode < updateInfo.versionCode) {
                        delay(1000)
                        UpdateApp(updateInfo, context)
                    } else {
                        toast("当前已是最新版本")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                finally {
                    hideLoading()
                }
            }
        }
    }

    fun UpdateApp(updateInfo: UpdateInfoDto, context: Context) {
        //APK的地址
//        val apkUrl = "https://file.lavandachen.com/jhwms/fridge_app/icu.apk";
        try {
            /* val apkUrl = SharedPreferencesUtils.getPrefString(
                 context,
                 UPDATE_APK_ADDRESS, context.getString(
                     R.string.update_apk_address
                 )
             )*/
            val apkUrl = updateInfo.url
            //更新弹窗标题
            var updateTitle = "${updateInfo.updateTitle} V${updateInfo.versionName}";
            //更新弹窗简介
            var updateContent = "${updateInfo.content}\n更多功能等你探索";

            UpdateAppUtils.init(context);
            //更新配置信息
            val updateConfig = UpdateConfig()
            //检查Wifi状态
            updateConfig.checkWifi = true
            //开启MD5校验
            updateConfig.needCheckMd5 = false
            //开启下载进度条
            updateConfig.alwaysShowDownLoadDialog = true
            //关闭通知栏进度条
            updateConfig.isShowNotification = false
            //关闭下载提示框
            updateConfig.showDownloadingToast = false

            //UI配置信息
            val uiConfig = UiConfig()
            uiConfig.uiType = UiType.PLENTIFUL
            uiConfig.updateLogoImgRes = R.mipmap.ic_kingmed_logo


            //功能Api
            UpdateAppUtils.getInstance()
                .apkUrl(apkUrl)
                .updateTitle(updateTitle)
                .updateContent(updateContent)
                .uiConfig(uiConfig)
                .updateConfig(updateConfig)
                /*.setMd5CheckResultListener(object : Md5CheckResultListener {
                    override fun onResult(result: Boolean) {
                        if (result == false) {
                            Toast.makeText(this@MainActivity, "Md5检验未通过，请晚些再更新！", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                })*/
                .update()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
package com.jhteck.icebox.utils

import android.content.Context
import com.jhteck.icebox.R
import com.jhteck.icebox.api.UPDATE_APK_ADDRESS
import com.jhteck.icebox.api.UPDATE_JSON_ADDRESS
import com.jhteck.icebox.api.UpdateInfoDto
import com.jhteck.icebox.apiserver.RetrofitClient
import constant.UiType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import model.UiConfig
import model.UpdateConfig
import update.UpdateAppUtils

/**
 *@Description:(升级app)
 *@author wade
 *@date 2023/1/11 21:53
 */
class UpdateAppUtil {


    fun checkVision(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
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
                }else{
                    ToastUtils.longToast(context,"已经是最新版本了")
                }
            } catch (e: Exception) {
                e.printStackTrace()
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
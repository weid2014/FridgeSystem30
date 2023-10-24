package com.jhteck.icebox

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.hele.mrd.app.lib.api.ApiManager
import com.hele.mrd.app.lib.base.BaseApp
import com.hele.mrd.app.lib.base.createLoading
import com.hele.mrd.app.lib.common.lifecycle.ActivityManager
import com.jhteck.icebox.api.*
import com.jhteck.icebox.apiserver.RetrofitClient
import com.jhteck.icebox.repository.entity.AccountEntity
import com.jhteck.icebox.utils.*
import com.jhteck.icebox.view.AppLoadingDialog
import com.tencent.bugly.crashreport.CrashReport
import es.dmoral.toasty.Toasty
import extension.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

/**
 *@Description:(用一句话描述)
 *@author wade
 *@date 2023/6/28 16:14
 */
class Application : BaseApp() {
    val Target = "Application";
    override fun setupApiManager(): ApiManager {
        return ApiManager.build {
            baseUrl(SharedPreferencesUtils.getPrefString(app, URL_REQUEST, URL_TEST))

            //                baseUrl("http://10.128.81.174:8086")
//            baseUrl("https://rfid.kingmed.com.cn")

            addConverterFactory(GsonConverterFactory.create(gson))
            interceptor { chain ->
                val originalRequest = chain.request()
                val newRequestBuilder = originalRequest.newBuilder()
//                appComponent.cacheManager.get<String>(KEY_ACCESS_TOKEN,isUser = true)?.let {
//                    newRequestBuilder.addHeader("authorization", "Bearer $it")
//                }
                /* if (DEBUG) {
                     newRequestBuilder.addHeader("authorization", "Bearer FEDCBA0123456788")
                 } else {

                 }*/
                newRequestBuilder.addHeader(
                    "authorization", "Bearer ${
                        SharedPreferencesUtils.getPrefString(
                            app, SNCODE,
                            SNCODE_TEST
                        )
                    }"
                )
                val newRequest = newRequestBuilder.build()

                val resp = chain.proceed(newRequest)

                if (resp.code == 401) {
                    appComponent.cacheManager.clearUserCache()
                    ActivityManager.killAll()
//                    startActivity(Intent(this@GanZhiApp,LoginActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                }

                resp
            }
        }
    }

    override fun createLoading(): createLoading {
        return {
            AppLoadingDialog.newInstance(it)
        }
    }

    override fun onCreate() {
        super.onCreate()
        //腾讯bugly捕获bug日志.如果是外网才初始化
        if(SharedPreferencesUtils.getPrefString(this,URL_REQUEST,URL_TEST).equals(URL_TEST)) {
            CrashReport.initCrashReport(applicationContext, "30a4125338", false);
        }

        /*registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                activity.window.decorView.setOnTouchListener { v, event ->
                    // 处理点击事件
                    Log.d("MyApplication", "Touch event detected: $event")
                    false
                }
            }

            override fun onActivityStarted(activity: Activity) {}

            override fun onActivityResumed(activity: Activity) {}

            override fun onActivityPaused(activity: Activity) {}

            override fun onActivityStopped(activity: Activity) {}

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

            override fun onActivityDestroyed(activity: Activity) {}
        })*/


        Toasty.Config.getInstance()
            .tintIcon(false) // optional (apply textColor also to the icon)
//            .setToastTypeface(@NonNull Typeface typeface) // optional
            .setTextSize(22) // optional
//            .allowQueue(boolean allowQueue) // optional (prevents several Toastys from queuing)
//            .setGravity(Gravity.BOTTOM , 0, 0) // optional (set toast gravity, offsets are optional)
//            .supportDarkTheme(boolean supportDarkTheme) // optional (whether to support dark theme or not)
//            .setRTL(boolean isRTL) // optional (icon is on the right)
            .apply(); // required

    }

}
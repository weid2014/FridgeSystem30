package com.jhteck.icebox

import android.content.Intent
import android.util.Log
import com.google.gson.Gson
import com.hele.mrd.app.lib.api.ApiManager
import com.hele.mrd.app.lib.base.BaseApp
import com.hele.mrd.app.lib.base.createLoading
import com.hele.mrd.app.lib.common.lifecycle.ActivityManager
import com.jhteck.icebox.api.*
import com.jhteck.icebox.apiserver.RetrofitClient
import com.jhteck.icebox.bean.SystemOperationErrorEnum
import com.jhteck.icebox.repository.entity.AccountEntity
import com.jhteck.icebox.repository.entity.SysOperationErrorEntity
import com.jhteck.icebox.service.MyService
import com.jhteck.icebox.utils.*
import com.jhteck.icebox.view.AppLoadingDialog
import com.tencent.bugly.crashreport.CrashReport
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 *@Description:(用一句话描述)
 *@author wade
 *@date 2023/6/28 16:14
 */
class Application : BaseApp(), Thread.UncaughtExceptionHandler {

    val Target = "Application";
    override fun setupApiManager(): ApiManager {
        return ApiManager.build {
            baseUrl(SharedPreferencesUtils.getPrefString(app, URL_REQUEST, URL_KM2))

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
                            URL_KM2
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
        if (!SharedPreferencesUtils.getPrefString(this, URL_REQUEST, URL_TEST).equals(URL_KM)) {
            CrashReport.initCrashReport(applicationContext, "30a4125338", false);
        }
        Thread.setDefaultUncaughtExceptionHandler(this)
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
        // 启动服务
//        startService(Intent(this, MyService::class.java))
    }


    override fun uncaughtException(p0: Thread, p1: Throwable) {

        val userInfoString = SharedPreferencesUtils.getPrefString(
            ContextUtils.getApplicationContext(),
            "loginUserInfo",
            null
        )

        var accountEntity = Gson().fromJson(userInfoString, AccountEntity::class.java)
        loginOperator(accountEntity)
//        stopService(Intent(this, MyService::class.java))
    }

    private fun loginOperator(accountEntity: AccountEntity) {
        GlobalScope.launch {
            try {
                var entity = SysOperationErrorEntity(
                    SnowFlake.getInstance().nextId().toInt(),
                    accountEntity.user_id,
                    accountEntity.nick_name,
                    accountEntity.role_id,
                    accountEntity.km_user_id,
                    accountEntity.real_name,
                    SystemOperationErrorEnum.REBOOT.v,
                    "网络状态",//todo 网络状态
                    "系统信息",//todo
                    "App版本",//todo
                    "串口信息",//
                    "冰箱信息",//
                    DateUtils.currentStringFormatTime(),
                    false
                );
                DbUtil.getDb().sysOperationErrorDao().insert(entity);//保存

                var logs = mutableListOf<SysOperationErrorEntity>()
                logs.add(entity);
                var rfidOperationBO = SysOperationErrorLogsBo(logs);

                var toJson = Gson().toJson(rfidOperationBO)
                Log.d(Target, "${toJson}")
                var res = RetrofitClient.getService().addSystemErrorLogs(rfidOperationBO)
                if (res.code() == 200) {
                    for (data in rfidOperationBO.logs) {
                        data.hasUpload = true;
                        DbUtil.getDb().sysOperationErrorDao().update(data);
                    }
                }

            } catch (e: Exception) {
                Log.i(Target, "${e.message}")
            }
            System.exit(0)
        }
    }
}
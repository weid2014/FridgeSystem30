package com.jhteck.icebox

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

        // object 对象表达式,创建一个匿名类，并重写 run() 方法
        /*object : Thread() {
            override fun run() {
                try {

                    var all = DbUtil.getDb().fridgesInfoDao().getAll()
                    if (all == null || all.size == 0) {
                        println("未激活")
                    } else {
                        SharedPreferencesUtils.setPrefString(
                            ContextUtils.getApplicationContext(),
                            SNCODE,
                            all.first().sncode
                        )//设备已激活
                    }
                    var accountDao = DbUtil.getDb().accountDao()
                    val adminUser = accountDao.findByRoleId("10")

                    if (adminUser == null) {
                        var user = AccountEntity();
                        user.id = 1;
                        user.user_id = SnowFlake.getInstance().nextId().toString();
                        user.km_user_id = "30812321";
                        user.real_name = "管理员";
                        user.nick_name = "admin";
                        user.role_id = "10";
                        user.password_digest = MD5Util.encrypt("Jinghe233");
//                        user.nfc_id = "1698A803CB9C2B04010100048804F1FEF453";
                        user.nfc_id = "1698A858A115F60401010004880432E54BD9";
//                        user.nfc_id = "1698A803CB9C2B04010100048804C0E4718C"
                        user.status = 0;
                        user.created_time = "${
                            DateUtils.formatDateToString(
                                Date(),
                                DateUtils.format_yyyyMMddhhmmssfff
                            )
                        }+08:00".replace(" ", "T")
                        accountDao.insertAll(user)
                        var users = accountDao.getAll()
                        for (u in users) {
                            println(u)
                        }
                    } else {
//                        DbUtil.getDb().userDao().delete(adminUser)
                    }
                    //wait wait wait
//                    synchronizedAccount()//同步账户


//                    if (users.count() == 1) {
                    *//*var keeper = AccountEntity();
                    keeper.id = 2;
                    keeper.user_id = SnowFlake.getInstance().nextId().toString();
                    keeper.km_user_id = SnowFlake.getInstance().nextId().toString();
                    keeper.real_name = "仓库管理员";
                    keeper.nick_name = "keeper";
                    keeper.role_id = "20";
                    keeper.password_digest = MD5Util.encrypt("Jinghe233");
                    keeper.status = 0;
                    keeper.created_time =
                        DateUtils.formatDateToString(Date(), DateUtils.format_yyyyMMddhhmmssfff)
                    accountDao.insertAll(keeper)

                    var scener = AccountEntity();
                   scener.id = 3;
                   scener.user_id = SnowFlake.getInstance().nextId().toString();
                   scener.km_user_id = SnowFlake.getInstance().nextId().toString();
                   scener.real_name = "现场人员";
                   scener.nick_name = "scener";
                   scener.role_id = "30";
                   scener.password_digest = MD5Util.encrypt("Jinghe233");
                   scener.status = 0;
                   scener.created_time =
                        DateUtils.formatDateToString(Date(), DateUtils.format_yyyyMMddhhmmssfff)
                    accountDao.insertAll(scener)*//*
//                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }.start()*/

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

    /**
     * 同步账户
     */
    private fun synchronizedAccount() {
        val accountDao = DbUtil.getDb().accountDao()
//        if (isNetAvailable()) {
            val users = accountDao.getAll();
            val uploadUsers = users.filter { user -> user.hasUpload == false }
            GlobalScope.launch(context = Dispatchers.IO) {
               try {
                   var accountService = RetrofitClient.getService();
                   //同步远端到本地账户
                   for (user in uploadUsers) {
                       if (user.status != 0) {
                           var response =
                               accountService.deleteAccount(user.user_id.toString())
                           if (response.code() == 200) {
                               Log.i(Target, user.nick_name + "---delete")
                               accountDao.delete(user)
                           }
                       } else {
                           var response = accountService.addAccount(user)
                           if (response.code() == 200) {
                               //                                        Log.i("Application",user.nick_name+"---delete")
                               user.hasUpload = true
                               accountDao.update(user)
                           }
                       }
                   }
                   var localUsers = accountDao.getAll();

                   var accountResponse = accountService.getAccounts()
                   if (accountResponse.code() == 200) {
                       var remoteAccounts = accountResponse.body()?.results
                       if (remoteAccounts?.accounts != null) {
                           for (u in localUsers) {
                               if ("10".equals(u.role_id)) continue//系统管理员不做任何修改
                               var userLists =
                                   remoteAccounts.accounts.filter { obj -> obj.user_id == u.user_id }
                               if (userLists == null && u.hasUpload) {
                                   Log.i(Target, u.nick_name + "---delete")
                                   accountDao.delete(u)//用户在远端被删除
                               } else {
                                   for (ru in userLists) {
                                       ru.id = u.id;
                                       ru.status = u.status;
                                       ru.hasUpload = u.hasUpload;
                                   }
                               }
                           }
                           localUsers = accountDao.getAll();
                           //更新本地账户
                           for (u in remoteAccounts.accounts) {
                               if (u.role_id=="10")continue;
                               val users = localUsers.filter { user -> user.user_id == u.user_id }
                               if (users != null && users.isNotEmpty()) continue;//存在的账户已经更新过
                               u.status = 0
                               u.hasUpload = true
                               accountDao.insertAll(u)//将账户同步到本地
                               Log.i(Target, u.nick_name + "---sync")
                           }
                       }

                   }
               }catch (e:Exception){
                   Log.i(Target, "${e}")
               }
//            }

        }
    }

    private fun isNetAvailable(): Boolean {
        return !NetworkUtil.isNetSystemUsable(ContextUtils.getApplicationContext())
               || !NetworkUtil.isNetOnline()
    }
}
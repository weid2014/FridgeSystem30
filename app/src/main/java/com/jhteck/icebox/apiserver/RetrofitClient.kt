package com.jhteck.icebox.apiserver

import android.util.Log
import com.hele.mrd.app.lib.base.BaseApp
import com.jhteck.icebox.api.*
import com.jhteck.icebox.utils.SharedPreferencesUtils
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


/**
 *@Description:(用一句话描述)
 *@author wade
 *@date 2022/12/12 0:11
 */
object RetrofitClient {


    fun getService(sncode: String =SharedPreferencesUtils.getPrefString(BaseApp.app, SNCODE, SNCODE_TEST),
                   baseUrl:String =SharedPreferencesUtils.getPrefString(BaseApp.app, URL_REQUEST, URL_KM1),
                   token:String=SharedPreferencesUtils.getPrefString(BaseApp.app, TOKEN,TOKEN_DEFAULT)): ILoginApiService {
        Log.d("RetrofitClient", "正在同步冰箱账号信息==${baseUrl}")
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        val okHttpClient = OkHttpClient.Builder()
            .callTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(Interceptor { chain ->
//                val request = chain.request().newBuilder()
//                    .removeHeader("authorization")
//                    .addHeader(
//                        "authorization",
//                        if (DEBUG) "Bearer $ICEBOX_SN_TEXT" else "Bearer $ICEBOX_SN"
//                    ).build()
                val request = chain.request().newBuilder()
                    .removeHeader("authorization")
                    .addHeader(
                        "authorization",
                        "Bearer $token"
                    ).build()
                Log.d("RetrofitClient", "request addHeader=${request.headers["authorization"]}")
                chain.proceed(request!!)
            }).build()
        //wait wait wait
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
        Log.d("RetrofitClient", "baseUrl=${retrofit.baseUrl()}")

        return retrofit.create(ILoginApiService::class.java)
    }

}
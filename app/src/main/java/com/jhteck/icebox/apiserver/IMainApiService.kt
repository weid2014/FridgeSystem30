package com.jhteck.icebox.apiserver

import com.hele.mrd.app.lib.api.ApiService
import com.jhteck.icebox.api.*
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface IMainApiService : ApiService {

    //单个查询frid
    @GET("api/f/rfids/{rfid}")
    suspend fun getRfid(@Path("rfid") rfid: String): Response<TestDao>;


}
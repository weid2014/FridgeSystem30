package com.jhteck.icebox.apiserver

import com.hele.mrd.app.lib.api.ApiService
import com.jhteck.icebox.api.*
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface IMainApiService : ApiService {

    //单个查询frid
    @GET("/api/f/rfids/{rfid}")
    suspend fun getRfid(@Path("rfid") rfid: String): Response<TestDao>;

    //批量查询rfid
    @POST("/api/f/rfids/")
    suspend fun getRfids(@Body body: RequestBody): Response<RfidDao>

    //全量上报
    @PUT("/api/f/rfids/sync")
    suspend fun syncRfids(@Body body: RequestBody): Response<RfidDao>

    //全量上报
    @PUT("/api/f/rfids/logs/sync")
    suspend fun syncRfidsLogs(@Body body: RfidOperationBO): Response<CommonResponse<RfidOperationReturnBO>>

}
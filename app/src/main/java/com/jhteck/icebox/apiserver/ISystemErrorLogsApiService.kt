package com.jhteck.icebox.apiserver

import com.hele.mrd.app.lib.api.ApiService
import com.jhteck.icebox.repository.entity.diule.Operation
import retrofit2.Response
import retrofit2.http.*

interface ISystemErrorLogsApiService : ApiService {


    /**
     * 更新
     */
    @PUT("api/f/v1/accounts")
    suspend fun update(@Body repertory: Operation): Response<Operation>;

    /**
     * 删除
     */
    @DELETE("api/f/v1/accounts/{id}")
    suspend fun delete(@Path("id") id:Int): Response<Operation>;

    /**
     * 添加
     */
    @DELETE("api/f/v1/accounts/{id}")
    suspend fun add(@Body repertory: Operation): Response<Operation>;
}
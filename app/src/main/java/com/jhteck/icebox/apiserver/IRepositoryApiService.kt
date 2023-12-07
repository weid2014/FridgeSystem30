package com.jhteck.icebox.apiserver

import com.hele.mrd.app.lib.api.ApiService
import com.jhteck.icebox.repository.entity.diule.Repertory
import retrofit2.Response
import retrofit2.http.*

/**
 * 库存后台请求
 */
interface IRepositoryApiService:ApiService {
    /**
     * 获取所有
     */
    @GET("api/f/accounts")
    suspend fun getAll(): Response<Repertory>;

    /**
     * 更新
     */
    @PUT("api/f/accounts")
    suspend fun update(@Body repertory: Repertory): Response<Repertory>;

    /**
     * 删除
     */
    @DELETE("api/f/accounts/{id}")
    suspend fun delete(@Path("id") id:Int): Response<Repertory>;

    /**
     * 添加
     */
    @DELETE("api/f/accounts/{id}")
    suspend fun add(@Body repertory: Repertory): Response<Repertory>;
}
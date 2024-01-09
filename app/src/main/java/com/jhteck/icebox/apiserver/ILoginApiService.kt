package com.jhteck.icebox.apiserver

import com.hele.mrd.app.lib.api.ApiService
import com.jhteck.icebox.api.*
import com.jhteck.icebox.repository.entity.FridgesInfoEntity
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*


interface ILoginApiService :IAccountService, ApiService {

    @POST("system/login")
//    @Headers("authorization: Bearer FEDCBA01CC000001")
    suspend fun login(@Body body: RequestBody): ApiResponse<LoginResponseDto>
    //批量查询rfid
    @POST("api/f/v1/rfids")
//    @Headers("authorization: Bearer FEDCBA01CC000001")
    suspend fun getRfids(@Body body: RequestBody): Response<RfidDao>

//    /**
//     * 添加用户
//     */
//    @POST("api/f/v1/accounts")
//    @Headers("authorization: Bearer FEDCBA01CC000001")
//    suspend fun addAccount(@Body body: RequestBody): Response<UserTestDao>;
//
//    /**
//     * 更新用户
//     */
//    @PUT("api/f/v1/accounts")
//    @Headers("authorization: Bearer FEDCBA01CC000001")
//    suspend fun updateAccount(@Body body: RequestBody): Response<UserTestDao>;
//
//    /**
//     * 删除用户
//     */
//    @DELETE("api/f/v1/accounts/{id}")
//    @Headers("authorization: Bearer FEDCBA0123456788")
//    suspend fun deleteAccount(@Path("id") id:Int):Response<DeleteUserDao>;

    @GET
    suspend fun getUpdateInfo(@Url url: String?): UpdateInfoDto

    //全量上报
    @PUT("api/f/v1/rfids/sync")
    suspend fun syncRfids(@Body body: RequestBody): Response<RfidDao>

}
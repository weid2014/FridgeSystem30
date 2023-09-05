package com.jhteck.icebox.apiserver

import com.hele.mrd.app.lib.api.ApiService
import com.jhteck.icebox.api.ApiResponse
import com.jhteck.icebox.api.LoginResponseDto
import com.jhteck.icebox.api.RfidDao
import com.jhteck.icebox.api.UpdateInfoDto
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url


interface ILoginApiService :IAccountService, ApiService {

    @POST("system/login")
//    @Headers("authorization: Bearer FEDCBA01CC000001")
    suspend fun login(@Body body: RequestBody): ApiResponse<LoginResponseDto>
    //批量查询rfid
    @POST("/api/f/rfids/")
//    @Headers("authorization: Bearer FEDCBA01CC000001")
    suspend fun getRfids(@Body body: RequestBody): Response<RfidDao>

//    /**
//     * 添加用户
//     */
//    @POST("/api/f/accounts")
//    @Headers("authorization: Bearer FEDCBA01CC000001")
//    suspend fun addAccount(@Body body: RequestBody): Response<UserTestDao>;
//
//    /**
//     * 更新用户
//     */
//    @PUT("/api/f/accounts")
//    @Headers("authorization: Bearer FEDCBA01CC000001")
//    suspend fun updateAccount(@Body body: RequestBody): Response<UserTestDao>;
//
//    /**
//     * 删除用户
//     */
//    @DELETE("/api/f/accounts/{id}")
//    @Headers("authorization: Bearer FEDCBA0123456788")
//    suspend fun deleteAccount(@Path("id") id:Int):Response<DeleteUserDao>;

    @GET
    suspend fun getUpdateInfo(@Url url: String?): UpdateInfoDto

}
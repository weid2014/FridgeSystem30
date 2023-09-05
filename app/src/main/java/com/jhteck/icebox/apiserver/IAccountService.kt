package com.jhteck.icebox.apiserver


import com.hele.mrd.app.lib.api.ApiService
import com.jhteck.icebox.api.*
import com.jhteck.icebox.repository.entity.AccountEntity
import com.jhteck.icebox.repository.entity.FridgesInfoEntity
import retrofit2.Response
import retrofit2.http.*

/**
 * 用户操作接口  Retrofit2
 */
interface IAccountService : ApiService {

    /**
     * 获取用户列表
     */
    @GET("/api/f/accounts")
    suspend fun getAccounts(): Response<CommonResponse<UsertoResults>>

    /**
     * 更新用户
     */
    @POST("/api/f/accounts")
    suspend fun updateAccount(@Body user: AccountEntity): Response<CommonResponse<AccountEntity>>;

    /**
     * 更新用户
     */
    @DELETE("/api/f/accounts/{id}")
    suspend fun deleteAccount(@Path("id") id: String): Response<CommonResponse<Object>>;

    /**
     * 添加用户
     */
    @POST("/api/f/accounts")
    suspend fun addAccount(@Body body: AccountEntity): Response<CommonResponse<AccountEntity>>;


    /**
     * 添加用户
     */
    @PUT("/api/f/accounts/logs/sync")
    suspend fun addAccountLogs(@Body body: AccountOperationBO): Response<CommonResponse<AccountOperationReturnBO>>;

    //全量上报
    @PUT("/api/f/rfids/logs/sync")
    suspend fun syncRfidsLogs(@Body body: RfidOperationBO): Response<CommonResponse<RfidOperationReturnBO>>

    //冰箱激活
    @POST("/api/f/fridges/active")
    suspend fun fridgesActive(@Body body: FridgesActiveBo): Response<CommonResponse<FridgesInfoEntity>>

    //获取冰箱信息
    @GET("/api/f/fridges/info")
    suspend fun fridgesInfo(): Response<CommonResponse<FridgesInfoEntity>>

    //更新冰箱信息
    @PUT("/api/f/fridges")
    suspend fun updatefridgesInfo(@Body body: FridgesActiveBo): Response<CommonResponse<FridgesInfoEntity>>

    /**
     * 增量上报冰箱的系统错误日志
     */
    @PUT("/api/f/fridges/system_error_logs/sync")
    suspend fun addSystemErrorLogs(@Body body: SysOperationErrorLogsBo): Response<CommonResponse<SysOperationErrorLogsResult>>
    /**
     * 增量上报冰箱的系统错误日志
     */
    @PUT("/api/f/fridges/operation_error_logs/sync")
    suspend fun addOperationErrorLogs(@Body body: OperationErrorLogsBo): Response<CommonResponse<OperationErrorLogsResult>>

    /**
     * 获取冰箱的系统错误日志
     */
    @GET("api/f/fridges/system_error_logs")
    suspend fun getSystemErrorLogs(): Response<CommonResponse<SystemOperationErrorLogsR>>

    /**
     * 获取冰箱的操作错误日志
     */
    @GET("api/f/fridges/operation_error_logs")
    suspend fun getOperationErrorLogs(): Response<CommonResponse<OperationErrorLogsR>>
}

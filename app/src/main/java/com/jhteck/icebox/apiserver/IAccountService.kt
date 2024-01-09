package com.jhteck.icebox.apiserver


import com.hele.mrd.app.lib.api.ApiService
import com.jhteck.icebox.api.*
import com.jhteck.icebox.repository.entity.AccountEntity
import com.jhteck.icebox.repository.entity.FridgesInfoEntity
import retrofit2.Response
import retrofit2.http.*
import java.util.*

/**
 * 用户操作接口  Retrofit2
 */
interface IAccountService : ApiService {

    /**
     * 获取用户列表
     */
    @GET("api/f/v1/accounts")
    suspend fun getAccounts(): Response<CommonResponse<UsertoResults>>

    /**
     * 账号用户验证
     */
    @POST("api/f/v1/accounts/validate/by_name")
    suspend fun validateByName(@Body userDao: UserDao): Response<CommonResponse<Any>>

    /**
     * 工号用户验证
     */
    @POST("api/f/v1/accounts/validate/by_number")
    suspend fun validateByNumber(@Body userDao: UserDao): Response<CommonResponse<Any>>

    /**
     * 更新用户
     */
    @PUT("api/f/v1/accounts/{id}")
    suspend fun updateAccount(@Path("id") id: Long,@Body user: AccountEntity): Response<CommonResponse<AccountEntity>>;

    /**
     * 删除用户
     */
    @DELETE("api/f/v1/accounts/{id}")
    suspend fun deleteAccount(@Path("id") id: String): Response<CommonResponse<Any>>;

    /**
     * 添加用户
     */
    @POST("api/f/v1/accounts")
    suspend fun addAccount(@Body body: AccountEntity): Response<CommonResponse<AccountEntity>>;


    /**
     * 添加用户日志
     */
    @PUT("api/f/v1/accounts/logs/sync")
    suspend fun addAccountLogs(@Body body: AccountOperationBO): Response<CommonResponse<AccountOperationReturnBO>>;

    //日志增量上报
    @PUT("api/f/v1/rfids/logs/sync")
    suspend fun syncRfidsLogs(@Body body: RfidOperationBO): Response<CommonResponse<RfidOperationReturnBO>>

    //冰箱激活
    @POST("api/f/v1/fridges/active")
    suspend fun fridgesActive(@Body body: FridgesActiveBo): Response<CommonResponse<FridgesInfoEntity>>

    //获取冰箱信息
    @GET("api/f/v1/fridges/info")
    suspend fun fridgesInfo(): Response<CommonResponse<FridgesInfoEntity>>

    //更新冰箱信息
    @PUT("api/f/v1/fridges")
    suspend fun updateFridgesInfo(@Body body: FridgesActiveBo): Response<CommonResponse<FridgesInfoEntity>>

    /**
     * 增量上报冰箱的系统错误日志
     */
    @PUT("api/f/v1/fridges/system_error_logs/sync")
    suspend fun addSystemErrorLogs(@Body body: SysOperationErrorLogsBo): Response<CommonResponse<SysOperationErrorLogsResult>>

    /**
     * 增量上报冰箱的系统错误日志
     */
    @PUT("api/f/v1/fridges/operation_error_logs/sync")
    suspend fun addOperationErrorLogs(@Body body: OperationErrorLogsBo): Response<CommonResponse<OperationErrorLogsResult>>

    /**
     * 获取冰箱的系统错误日志
     */
    @GET("api/f/v1/fridges/system_error_logs")
    suspend fun getSystemErrorLogs(): Response<CommonResponse<SystemOperationErrorLogsR>>

    /**
     * 获取冰箱的操作错误日志
     */
    @GET("api/f/v1/fridges/operation_error_logs")
    suspend fun getOperationErrorLogs(): Response<CommonResponse<OperationErrorLogsR>>
}

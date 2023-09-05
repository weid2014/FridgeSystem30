package com.jhteck.icebox.apiserver

import com.hele.mrd.app.lib.api.ApiService
import com.jhteck.icebox.api.RfidDao
import com.jhteck.icebox.repository.entity.diule.Repertory
import retrofit2.Response
import retrofit2.http.*

/**
 * 库存后台请求
 */
interface IRfidApiService:ApiService {
    /**
     * 获取所有
     */
    @GET("/api/f/rfids/{rfid}")
    suspend fun getRfid(@Path("rfid") rfid:String): Response<RfidDao>;

}
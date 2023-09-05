package com.jhteck.icebox.repository.dao

import androidx.room.Dao
import androidx.room.Query
import com.jhteck.icebox.repository.dao.base.BaseKotlinDao
import com.jhteck.icebox.repository.entity.RfidSourceInfo


/**
 * 扫描到的一些rfid保存到这个表中
 */
@Dao
interface RfidSourceInfoDao:BaseKotlinDao<RfidSourceInfo> {
    /**
     * 获取所有
     */
    @Query("SELECT * from t_rfid_source_info")
    override fun getAll(): List<RfidSourceInfo> ;

    /**
     * 根据Rrid 获取
     */
    @Query("SELECT * from t_rfid_source_info WHERE rfid =(:rfid) Limit 1")
    fun getByRfid(rfid: String): RfidSourceInfo;
}
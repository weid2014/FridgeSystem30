package com.jhteck.icebox.repository.dao

import androidx.room.Dao
import androidx.room.Query
import com.jhteck.icebox.repository.dao.base.BaseKotlinDao
import com.jhteck.icebox.repository.entity.AvailRfidEntity

import com.jhteck.icebox.repository.entity.diule.Rfid

@Dao
interface AvailRfidDao: BaseKotlinDao<AvailRfidEntity> {
    /**
     * 获取所有
     */
    @Query("SELECT * from t_avail_rfid")
    override fun getAll(): List<AvailRfidEntity> ;

    /**
     * 根据Rrid 获取
     */
    @Query("SELECT * from t_avail_rfid WHERE rfid =(:rfid) Limit 1")
    fun getByRfid(rfid: String): AvailRfidEntity;

    /**
     * 清空表
     */
    @Query("delete from t_avail_rfid")
    fun clean();
}
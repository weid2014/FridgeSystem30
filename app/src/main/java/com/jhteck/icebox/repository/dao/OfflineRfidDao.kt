package com.jhteck.icebox.repository.dao

import androidx.room.Dao
import androidx.room.Query
import com.jhteck.icebox.repository.dao.base.BaseKotlinDao
import com.jhteck.icebox.repository.entity.OfflineRfidEntity


/**
 * 离线数据
 */
@Dao
 interface OfflineRfidDao:BaseKotlinDao<OfflineRfidEntity>{
    /**
     * 获取所有
     */
    @Query("SELECT * from t_offline_rfid")
    override fun getAll(): List<OfflineRfidEntity>;
    /**
     * 获取所有
     */
    @Query("SELECT * from t_offline_rfid where rfid =(:rfid) limit 1")
     fun getByRfid(rfid:String): OfflineRfidEntity;

    /**
     * 清空表
     */
    @Query("delete from t_offline_rfid")
    fun clean();
}
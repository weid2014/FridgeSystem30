package com.jhteck.icebox.repository.dao

import androidx.room.Dao
import androidx.room.Query
import com.jhteck.icebox.repository.dao.base.BaseKotlinDao
import com.jhteck.icebox.repository.entity.RfidOperationEntity

@Dao
interface RfidOperationDao:BaseKotlinDao<RfidOperationEntity> {
    /**
     * 获取所有
     */
    @Query("SELECT * from t_rfid_operation")
    override fun getAll(): List<RfidOperationEntity> ;

    @Query("SELECT * from t_rfid_operation where id in (:ids)")
    fun getByIds(ids:LongArray ):List<RfidOperationEntity> ;
    /**
     * 清空表
     */
    @Query("delete from t_rfid_operation")
    fun clean();
}
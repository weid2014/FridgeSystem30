package com.jhteck.icebox.repository.dao.diule

import androidx.room.Dao
import androidx.room.Query
import com.jhteck.icebox.repository.dao.base.BaseKotlinDao
import com.jhteck.icebox.repository.entity.diule.Rfid

@Dao
interface RfidDao: BaseKotlinDao<Rfid> {

    /**
     * 获取所有
     */
    @Query("SELECT * from t_rfid")
    override fun getAll(): List<Rfid> ;

    /**
     * 根据Rrid 获取
     */
    @Query("SELECT * from t_rfid WHERE rfid =(:rfid) Limit 1")
     fun getByRfid(rfid: String): Rfid;
}
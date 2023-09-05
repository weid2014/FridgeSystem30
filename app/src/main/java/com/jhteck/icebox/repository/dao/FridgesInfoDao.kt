package com.jhteck.icebox.repository.dao

import androidx.room.Dao
import androidx.room.Query
import com.jhteck.icebox.repository.dao.base.BaseKotlinDao
import com.jhteck.icebox.repository.entity.AvailRfidEntity
import com.jhteck.icebox.repository.entity.FridgesInfoEntity

@Dao
interface FridgesInfoDao:BaseKotlinDao<FridgesInfoEntity> {

    /**
     * 根据id获取
     */
    @Query("SELECT * from t_fridges_info WHERE id =(:id) Limit 1")
    fun getById(id: String): FridgesInfoEntity;

    /**
     * 清空表
     */
    @Query("delete from t_fridges_info")
    fun clean();

    /**
     * 获取所有
     */
    @Query("SELECT * from t_fridges_info")
    override fun getAll(): List<FridgesInfoEntity> ;
}
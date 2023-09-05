package com.jhteck.icebox.repository.dao

import androidx.room.Dao
import androidx.room.Query
import com.jhteck.icebox.repository.dao.base.BaseKotlinDao
import com.jhteck.icebox.repository.entity.MaterialBatchEntity

@Dao
interface MaterialBatchDao : BaseKotlinDao<MaterialBatchEntity> {
    /**
     * 获取所有
     */
    @Query("SELECT * from t_material_batch")
    override fun getAll(): List<MaterialBatchEntity>;

    @Query("SELECT * from t_material_batch where id =(:id) Limit 1")
    fun getById(id: Int): MaterialBatchEntity;

    /**
     * 请空表
     */
    @Query("delete from t_material_batch")
    fun clean()
}
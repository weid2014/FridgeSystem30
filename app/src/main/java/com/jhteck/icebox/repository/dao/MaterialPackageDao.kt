package com.jhteck.icebox.repository.dao

import androidx.room.Dao
import androidx.room.Query
import com.jhteck.icebox.repository.dao.base.BaseKotlinDao
import com.jhteck.icebox.repository.entity.MaterialBatchEntity
import com.jhteck.icebox.repository.entity.MaterialPackageEntity

@Dao
interface MaterialPackageDao : BaseKotlinDao<MaterialPackageEntity> {
    /**
     * 获取所有
     */
    @Query("SELECT * from t_material_package")
    override fun getAll(): List<MaterialPackageEntity>;

    @Query("SELECT * from t_material_package where id =(:id) Limit 1")
    fun getById(id: Int): MaterialPackageEntity;

    /**
     * 请空表
     */
    @Query("delete from t_material_package")
    fun clean()
}
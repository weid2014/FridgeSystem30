package com.jhteck.icebox.repository.dao

import androidx.room.Dao
import androidx.room.Query
import com.jhteck.icebox.repository.dao.base.BaseJavaDao
import com.jhteck.icebox.repository.dao.base.BaseKotlinDao
import com.jhteck.icebox.repository.entity.SysOperationErrorEntity

@Dao
interface SysOperationErrorDao:BaseKotlinDao<SysOperationErrorEntity> {
    /**
     * 获取所有
     */
    @Query("SELECT * from t_sys_operation_error")
    override fun getAll(): List<SysOperationErrorEntity> ;
}
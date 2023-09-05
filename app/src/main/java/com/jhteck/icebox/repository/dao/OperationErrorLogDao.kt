package com.jhteck.icebox.repository.dao

import androidx.room.Dao
import androidx.room.Query
import com.jhteck.icebox.repository.dao.base.BaseKotlinDao

import com.jhteck.icebox.repository.entity.OperationErrorLogEntity


/**
 * 操作异常
 */
@Dao
interface OperationErrorLogDao : BaseKotlinDao<OperationErrorLogEntity> {
    /**
     * 获取所有
     */
    @Query("SELECT * from t_operation_error_log")
    override fun getAll(): List<OperationErrorLogEntity>;
}
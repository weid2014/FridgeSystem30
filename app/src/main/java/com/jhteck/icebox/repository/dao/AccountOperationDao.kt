package com.jhteck.icebox.repository.dao

import androidx.room.Dao
import androidx.room.Query
import com.jhteck.icebox.repository.dao.base.BaseKotlinDao
import com.jhteck.icebox.repository.entity.AccountOperationEntity
import com.jhteck.icebox.repository.entity.AvailRfidEntity

@Dao
interface AccountOperationDao : BaseKotlinDao<AccountOperationEntity> {
    /**
     * 获取所有
     */
    @Query("SELECT * from t_account_operation")
    override fun getAll(): List<AccountOperationEntity> ;

    /**
     * 清空表
     */
    @Query("delete from t_account_operation")
    fun clean();
}
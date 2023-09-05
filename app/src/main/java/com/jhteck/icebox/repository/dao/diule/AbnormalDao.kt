package com.jhteck.icebox.repository.dao.diule

import androidx.room.Dao
import androidx.room.Query
import com.jhteck.icebox.repository.dao.base.BaseKotlinDao
import com.jhteck.icebox.repository.entity.diule.Abnormal

/**
 * 其他异常记录Dao
 */
@Dao
interface AbnormalDao : BaseKotlinDao<Abnormal> {
    @Query("SELECT * FROM t_abnormal")
    override fun getAll(): List<Abnormal>;
}
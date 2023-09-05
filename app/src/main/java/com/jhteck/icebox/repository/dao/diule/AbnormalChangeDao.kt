package com.jhteck.icebox.repository.dao.diule

import androidx.room.Dao
import androidx.room.Query
import com.jhteck.icebox.repository.dao.base.BaseKotlinDao
import com.jhteck.icebox.repository.entity.diule.AbnormalChange

/**
 * 库存变更报错操作记录 DAO
 */
@Dao
interface AbnormalChangeDao: BaseKotlinDao<AbnormalChange> {
    @Query("SELECT * FROM t_abnormal_change")
    override fun  getAll():List<AbnormalChange>;
}
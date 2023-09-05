package com.jhteck.icebox.repository.dao.diule

import androidx.room.Dao
import androidx.room.Query
import com.jhteck.icebox.repository.dao.base.BaseKotlinDao
import com.jhteck.icebox.repository.entity.diule.Repertory


/**
 * 库存DAO
 */
@Dao
interface RepertoryDao : BaseKotlinDao<Repertory> {


    @Query("SELECT * FROM t_repertory")
    override fun getAll(): List<Repertory>;
}
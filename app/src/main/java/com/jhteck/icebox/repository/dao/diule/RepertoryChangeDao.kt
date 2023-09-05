package com.jhteck.icebox.repository.dao.diule

import androidx.room.Dao
import androidx.room.Query
import com.jhteck.icebox.repository.dao.base.BaseKotlinDao
import com.jhteck.icebox.repository.entity.diule.RepertoryChange

@Dao
interface RepertoryChangeDao: BaseKotlinDao<RepertoryChange> {
    @Query("SELECT * FROM t_repertory_change")
    override fun  getAll():List<RepertoryChange>;
}
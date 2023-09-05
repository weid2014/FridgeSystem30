package com.jhteck.icebox.repository.dao

import androidx.room.Dao
import androidx.room.Query
import com.jhteck.icebox.repository.dao.base.BaseKotlinDao
import com.jhteck.icebox.repository.entity.diule.Operation

@Dao
interface ExampleKotlinDao : BaseKotlinDao<Operation> {
    @Query("SELECT * FROM t_operation")
    override fun  getAll():List<Operation>;
}
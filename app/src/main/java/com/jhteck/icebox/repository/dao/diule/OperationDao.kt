package com.jhteck.icebox.repository.dao.diule

import androidx.room.*
import com.jhteck.icebox.repository.entity.diule.Operation



@Dao
interface OperationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg operations: Operation);

    @Delete
    fun delete(operation: Operation);

    @Update
    fun update(operation: Operation);

    @Query("SELECT * FROM t_operation")
    fun getAll():List<Operation>;
}
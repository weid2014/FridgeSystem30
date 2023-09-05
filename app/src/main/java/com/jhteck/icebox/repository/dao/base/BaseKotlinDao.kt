package com.jhteck.icebox.repository.dao.base

import androidx.room.*

@Dao
open interface BaseKotlinDao<T> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg operations: T);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(operation: T):Long;

    @Delete
    fun delete(operation: T);

    @Update
    fun update(operation: T);

    open fun getAll(): List<T>;

}
package com.jhteck.icebox.repository.dao.base;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Update;

import java.util.List;

@Dao
public interface BaseJavaDao<T> {

    List<T> getAll();

//    T findById(Integer id);

    List<T> loadAllByIds(int[] userIds);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(T... datas);

    @Delete
    void delete(T data);
    @Update
    void update(T data);
    @Update
    void updateAll(T... datas);
}

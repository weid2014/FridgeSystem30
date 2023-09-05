package com.jhteck.icebox.repository.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;


import com.jhteck.icebox.api.Material;
import com.jhteck.icebox.repository.dao.base.BaseJavaDao;
import com.jhteck.icebox.repository.entity.MaterialEntity;

import java.util.List;

@Dao
public interface MedicalDao extends BaseJavaDao<MaterialEntity> {
    @Query("SELECT * FROM t_material")
    List<MaterialEntity> getAll();

    @Query("SELECT * FROM t_material WHERE id IN (:ids)")
    List<MaterialEntity> loadAllByIds(int[] ids);
    @Query("SELECT * FROM t_material WHERE id =(:id) LIMIT 1")
    MaterialEntity getById(int id);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(MaterialEntity... medicals);
    @Delete
    void delete(MaterialEntity medical);
    /**
     * 清空表
     */
    @Query("delete from t_material")
    void clean();
}

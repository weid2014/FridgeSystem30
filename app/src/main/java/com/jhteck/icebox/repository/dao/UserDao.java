package com.jhteck.icebox.repository.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.jhteck.icebox.repository.dao.base.BaseJavaDao;
import com.jhteck.icebox.repository.entity.diule.User;

import org.jetbrains.annotations.Nullable;

import java.util.List;

@Dao
@Deprecated()
public interface UserDao extends BaseJavaDao<User> {
    @Query("SELECT * FROM t_user")
    @Override
    List<User> getAll();
    @Query("SELECT * FROM t_user WHERE user_id IN (:userIds)")
    @Override
    List<User> loadAllByIds(int[] userIds);

    @Query("SELECT * from t_user WHERE user_id=(:userId)")
    User findById(Integer userId);

    @Query("SELECT * FROM t_user WHERE real_name=(:real_name) LIMIT 1")
    User findByName(String real_name);

    @Query("SELECT * FROM t_user WHERE nfc_id=(:nfcId) LIMIT 1")
    User findByNfcId( String nfcId);
}


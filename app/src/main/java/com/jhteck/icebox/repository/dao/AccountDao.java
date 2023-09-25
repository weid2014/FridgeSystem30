package com.jhteck.icebox.repository.dao;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Update;

import com.jhteck.icebox.repository.dao.base.BaseJavaDao;
import com.jhteck.icebox.repository.entity.AccountEntity;

import java.util.List;

@Dao
public interface AccountDao extends BaseJavaDao<AccountEntity> {
    @Query("SELECT * FROM t_account")
    @Override
    List<AccountEntity> getAll();

    @Query("SELECT * FROM t_account WHERE user_id IN (:userIds)")
    @Override
    List<AccountEntity> loadAllByIds(int[] userIds);

    @Query("SELECT * from t_account WHERE user_id=(:userId)")
    AccountEntity findById(String userId);

    @Query("SELECT * FROM t_account WHERE nick_name=(:nick_name) LIMIT 1")
    AccountEntity findByName(String nick_name);

    @Query("SELECT * FROM t_account WHERE km_user_id=(:km_user_id) LIMIT 1")
    AccountEntity findByKmUserId(String km_user_id);

    @Query("SELECT * FROM t_account WHERE nfc_id=(:nfcId) LIMIT 1")
    AccountEntity findByNfcId(String nfcId);

    @Query("SELECT * from t_account WHERE role_id=(:roleId)")
    AccountEntity findByRoleId(String roleId);

    @Query("SELECT * FROM t_account WHERE faceUrl=(:faceUrl) LIMIT 1")
    AccountEntity findByFaceUrl(String faceUrl);

}
package com.jhteck.icebox.repository.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.jhteck.icebox.repository.dao.base.BaseJavaDao;
import com.jhteck.icebox.repository.entity.AccountEntity;

import java.util.List;

@Dao
public interface AccountDao extends BaseJavaDao<AccountEntity> {
    @Query("SELECT * FROM t_account")
    @Override
    List<AccountEntity> getAll();

    /**
     * 清空表
     */
    @Query("delete from t_account")
    void clean();

    @Query("SELECT * FROM t_account WHERE id IN (:userIds)")
    @Override
    List<AccountEntity> loadAllByIds(int[] userIds);

    @Query("SELECT * from t_account WHERE user_id=(:userId)")
    AccountEntity findById(String userId);

    @Query("SELECT * FROM t_account WHERE nick_name=(:nick_name) LIMIT 1")
    AccountEntity findByName(String nick_name);

    @Query("SELECT * FROM t_account WHERE real_name=(:real_name) LIMIT 1")
    AccountEntity findByRealName(String real_name);

    @Query("SELECT * FROM t_account WHERE user_name=(:user_name) LIMIT 1")
    AccountEntity findByUserName(String user_name);

    @Query("SELECT * FROM t_account WHERE nfc_id=(:nfcId) or fridge_nfc_1=(:nfcId) " +
            "or fridge_nfc_2=(:nfcId) or fridge_nfc_3=(:nfcId) or fridge_nfc_4=(:nfcId) or fridge_nfc_5=(:nfcId) LIMIT 1")
    AccountEntity findByNfcId(String nfcId);

    @Query("SELECT * from t_account WHERE role_id=(:roleId)")
    AccountEntity findByRoleId(String roleId);

    @Query("SELECT * from t_account WHERE role=(:role)")
    AccountEntity findByRole(int role);

    @Query("SELECT * FROM t_account WHERE faceUrl=(:faceUrl) LIMIT 1")
    AccountEntity findByFaceUrl(String faceUrl);

}
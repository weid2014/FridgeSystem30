package com.jhteck.icebox.repository.dao

import androidx.room.Dao
import androidx.room.Query
import com.jhteck.icebox.repository.dao.base.BaseKotlinDao
import com.jhteck.icebox.repository.entity.AvailRfidEntity
import com.jhteck.icebox.repository.entity.FaceAccountEntity

@Dao
interface FaceAccountDao: BaseKotlinDao<FaceAccountEntity> {
    /**
     * 获取所有
     */
    @Query("SELECT * from t_face_account")
    override fun getAll(): List<FaceAccountEntity> ;

    /**
     * 根据Rrid 获取
     */
    @Query("SELECT * from t_face_account WHERE faceUrl =(:faceUrl) Limit 1")
    fun getByFaceUrl(faceUrl: String): FaceAccountEntity;

    /**
     * 清空表
     */
    @Query("delete from t_face_account")
    fun clean();
}
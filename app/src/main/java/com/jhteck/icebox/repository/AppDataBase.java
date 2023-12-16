package com.jhteck.icebox.repository;

import androidx.room.Database;
import androidx.room.RoomDatabase;


import com.jhteck.icebox.repository.dao.AccountDao;
import com.jhteck.icebox.repository.dao.AccountOperationDao;
import com.jhteck.icebox.repository.dao.FaceAccountDao;
import com.jhteck.icebox.repository.dao.FridgesInfoDao;
import com.jhteck.icebox.repository.dao.OfflineRfidDao;
import com.jhteck.icebox.repository.dao.OperationErrorLogDao;
import com.jhteck.icebox.repository.dao.RfidOperationDao;
import com.jhteck.icebox.repository.dao.SysOperationErrorDao;
import com.jhteck.icebox.repository.dao.diule.AbnormalChangeDao;
import com.jhteck.icebox.repository.dao.diule.AbnormalDao;
import com.jhteck.icebox.repository.dao.AvailRfidDao;
import com.jhteck.icebox.repository.dao.MaterialBatchDao;
import com.jhteck.icebox.repository.dao.MedicalDao;
import com.jhteck.icebox.repository.dao.diule.OperationDao;
import com.jhteck.icebox.repository.dao.diule.RepertoryDao;
import com.jhteck.icebox.repository.dao.diule.RfidDao;
import com.jhteck.icebox.repository.dao.UserDao;
import com.jhteck.icebox.repository.entity.AccountEntity;
import com.jhteck.icebox.repository.entity.AccountOperationEntity;
import com.jhteck.icebox.repository.entity.FaceAccountEntity;
import com.jhteck.icebox.repository.entity.FridgesInfoEntity;
import com.jhteck.icebox.repository.entity.OfflineRfidEntity;
import com.jhteck.icebox.repository.entity.OperationErrorLogEntity;
import com.jhteck.icebox.repository.entity.RfidOperationEntity;
import com.jhteck.icebox.repository.entity.SysOperationErrorEntity;
import com.jhteck.icebox.repository.entity.diule.Abnormal;
import com.jhteck.icebox.repository.entity.diule.AbnormalChange;
import com.jhteck.icebox.repository.entity.AvailRfidEntity;
import com.jhteck.icebox.repository.entity.MaterialBatchEntity;
import com.jhteck.icebox.repository.entity.MaterialEntity;
import com.jhteck.icebox.repository.entity.diule.Operation;
import com.jhteck.icebox.repository.entity.diule.Repertory;
import com.jhteck.icebox.repository.entity.diule.Rfid;
import com.jhteck.icebox.repository.entity.diule.User;

@Database(entities = {User.class,
        Operation.class, Repertory.class,
        AbnormalChange.class, Abnormal.class,
        Rfid.class, AvailRfidEntity.class,
        MaterialBatchEntity.class, MaterialEntity.class, AccountEntity.class,
        AccountOperationEntity.class,
        RfidOperationEntity.class,
        FridgesInfoEntity.class,
        SysOperationErrorEntity.class,
        OperationErrorLogEntity.class,
        FaceAccountEntity.class,
        OfflineRfidEntity.class,
}, version = 35, exportSchema = false)
public abstract class AppDataBase extends RoomDatabase {

    public abstract UserDao userDao();

    public abstract RfidDao rfidDao();

    public abstract AvailRfidDao availRfidDao();

    public abstract MedicalDao medicaldao();

    public abstract MaterialBatchDao materialBatchDao();

    /**
     * 账户
     *
     * @return
     */
    public abstract AccountDao accountDao();

    /**
     * 人脸图片路径和账户对应
     * @return
     */
    public abstract FaceAccountDao faceAccountDao();

    /**
     * 用户操作日志
     *
     * @return
     */
    public abstract AccountOperationDao accountOperationDao();

    /**
     * rfid操作
     *
     * @return
     */
    public abstract RfidOperationDao rfidOperationDao();

    /**
     * 冰箱信息
     *
     * @return
     */
    public abstract FridgesInfoDao fridgesInfoDao();

    /**
     * 系统操作异常
     *
     * @return
     */
    public abstract SysOperationErrorDao sysOperationErrorDao();

    /**
     * 系统操作异常
     *
     * @return
     */
    public abstract OperationErrorLogDao operationErrorLogDao();

    /**
     * 离线数据表
     * @return
     */
    public abstract OfflineRfidDao offlineRfidDao();

}

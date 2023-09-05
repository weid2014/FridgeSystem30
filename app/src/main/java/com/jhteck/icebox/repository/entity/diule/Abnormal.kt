package com.jhteck.icebox.repository.entity.diule

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 其他异常记录
 */
@Entity(tableName = "t_abnormal")
data  class Abnormal(
    @PrimaryKey(autoGenerate = true) val id:Int,
    @ColumnInfo(name="updated_by") val updatedBy:String,//操作人（nickname
    @ColumnInfo(name="updated_time") val updatedTime:Int,//操作时间
    @ColumnInfo(name="error_code") val errorCode:String,//错误代码
    @ColumnInfo(name="error_type") val errorType:String,//错误类型
    @ColumnInfo(name="network") val network:String,//网络状态
    @ColumnInfo(name="system_info") val systemInfo:String,//系统信息os和rom版本
    @ColumnInfo(name="app_version") val appVersion:String,//app版本和uniapp版本
    @ColumnInfo(name="port_storage") val portStorage:String,//串口配置
    @ColumnInfo(name="rf_storage") val rfStorage:String,//token和冰箱信息
)


package com.jhteck.icebox.repository.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "t_offline_rfid")
  data  class OfflineRfidEntity(
    @PrimaryKey(autoGenerate = true) val id: Int?,//id
    @ColumnInfo(name="rifid") var rifd:String,//rfid
    @ColumnInfo(name = "created_at") val created_at: String,//创建时间
    //@ColumnInfo(name = "eas_supplier_name") val eas_supplier_name: String,//
    @ColumnInfo(name = "user_id") val user_id: String,//用户id
    @ColumnInfo(name = "role_id") val role_id: String,//角色id
    @ColumnInfo(name = "nick_name") val nick_name: String,//用户名称
    @ColumnInfo(name = "operator_status") val operator_status: Int,//操作结果  1 存入， 2 取出 3 暂存
    @ColumnInfo(name = "remain") val remain: Int,//余量
    )
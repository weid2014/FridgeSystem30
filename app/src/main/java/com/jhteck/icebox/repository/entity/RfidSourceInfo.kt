package com.jhteck.icebox.repository.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 原始的rfid  包括无网络时，都需要保存，上传后可删除
 */
@Entity(tableName = "t_rfid_source_info")
data class RfidSourceInfo(
    @PrimaryKey(autoGenerate = true) val id: Int,//id自增
    @ColumnInfo(name = "rfid") var rfid: String,//rfid
    @ColumnInfo(name = "status") var status: Byte=0//上传状态 0表示没有上传，1表示已上传
);
package com.jhteck.icebox.repository.entity.diule

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 用户操作记录
 */
@Entity(tableName = "t_operation")
data class Operation(
    @PrimaryKey(autoGenerate = true) val id:Long,//操作id
    @ColumnInfo(name="user_id") val userId:Long,//用户id
    @ColumnInfo val operation:Int,//操作类型
    @ColumnInfo(name="user_log_id") val userLogId:String,//操作编码
    @ColumnInfo val updateTime:Long//操作时间
)
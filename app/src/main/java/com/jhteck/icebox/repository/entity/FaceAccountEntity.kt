package com.jhteck.icebox.repository.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "t_face_account", indices = [Index(value = ["faceUrl"])])
data class FaceAccountEntity(
    @PrimaryKey(autoGenerate = true) var id: Int?,
    @ColumnInfo val user_id: String,// 新激活或已存在的冰箱ID
    @ColumnInfo var faceUrl: String, //
    @ColumnInfo var createTime: Long= System.currentTimeMillis(),
    @ColumnInfo var status: Int = 0,//是否可用，-1表示不可用
)
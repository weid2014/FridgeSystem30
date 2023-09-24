package com.jhteck.icebox.repository.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "t_face_account", indices = [Index(value = ["faceUrl"])])
data class FaceAccountEntity(
    @PrimaryKey(autoGenerate = true) var id: Int?,
    @ColumnInfo val user_id: String,// 新激活或已存在的冰箱ID
    @ColumnInfo val faceUrl: String, //
)
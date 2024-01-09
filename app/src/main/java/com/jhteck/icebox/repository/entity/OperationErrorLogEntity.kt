package com.jhteck.icebox.repository.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "t_operation_error_log")
data class OperationErrorLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    var account_id : Long,
    var role_id: String,
    var remain  : Int,
    var rfid : String,
    var error_code : Int,
    var log_at : String,
    var hasUpload: Boolean
)
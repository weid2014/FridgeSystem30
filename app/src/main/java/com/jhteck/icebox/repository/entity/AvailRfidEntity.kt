package com.jhteck.icebox.repository.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jhteck.icebox.api.Material
import com.jhteck.icebox.api.MaterialBatch

@Entity(tableName = "t_avail_rfid")
data class AvailRfidEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "cell_number") var cell_number: Int?,
    @ColumnInfo(name = "created_at") val created_at: String,
    @ColumnInfo(name = "eas_supplier_name") val eas_supplier_name: String,
    @ColumnInfo(name = "eas_supplier_number") val eas_supplier_number: String?,
    @ColumnInfo(name = "is_out_eas") val is_out_eas: Boolean?,

//    val material: Material,
//    val material_batch: MaterialBatch,
    @ColumnInfo(name = "remain") val remain: Int?,
    @ColumnInfo(name = "rfid") val rfid: String
)
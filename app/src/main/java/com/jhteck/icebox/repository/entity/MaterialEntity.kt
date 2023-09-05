package com.jhteck.icebox.repository.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "t_material")
data class MaterialEntity(
    @PrimaryKey(autoGenerate = true)  var id: Int,
    @ColumnInfo(name="eas_manufacturer") val eas_manufacturer: String?,
    @ColumnInfo(name="eas_material_desc") val eas_material_desc: String?,
    @ColumnInfo(name="eas_material_id") val eas_material_id: String?,
    @ColumnInfo(name="eas_material_name") val eas_material_name: String?,
    @ColumnInfo(name="eas_material_number") val eas_material_number: String?,
    @ColumnInfo(name="eas_unit_id") val eas_unit_id: String?,
    @ColumnInfo(name="eas_unit_name") val eas_unit_name: String?,
    @ColumnInfo(name="eas_unit_number") val eas_unit_number: String?
)

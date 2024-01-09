package com.jhteck.icebox.repository.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 批号
 */
@Entity(tableName = "t_material_batch")
data class MaterialBatchEntity(
    @PrimaryKey(autoGenerate = true)  var id: Int,
    @ColumnInfo(name="eas_lot") val eas_lot: String?,//批号
    @ColumnInfo(name="eas_specs") val eas_specs: String?,
    @ColumnInfo(name="expired_at") val expired_at: String?,
    @ColumnInfo(name="updated_at") val updated_at: String?,
    @ColumnInfo(name="eas_exp") val eas_exp: String?,
    @ColumnInfo(name="wms_manufacturer_date") val wms_manufacturer_date: String?
)

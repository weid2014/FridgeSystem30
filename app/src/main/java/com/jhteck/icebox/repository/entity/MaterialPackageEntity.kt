package com.jhteck.icebox.repository.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 批号
 */
@Entity(tableName = "t_material_package")
data class MaterialPackageEntity(
    @PrimaryKey(autoGenerate = true)  var id: Int,
    @ColumnInfo(name="unit_code") val unit_code: String?,//批号
    @ColumnInfo(name="unit_name") val unit_name: String?,
    @ColumnInfo(name="is_basic_unit") val is_basic_unit: Int?,
    @ColumnInfo(name="unit_qty") val unit_qty: Int?,
)

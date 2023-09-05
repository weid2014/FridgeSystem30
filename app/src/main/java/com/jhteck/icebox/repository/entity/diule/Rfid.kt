package com.jhteck.icebox.repository.entity.diule

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "t_rfid")
data class Rfid(
    @PrimaryKey(autoGenerate = true)  var id: Int,
    @ColumnInfo(name="created_at") var created_at: String,
    @ColumnInfo(name="eas_supplier_name") var eas_supplier_name: String,
    @ColumnInfo(name="eas_supplier_number") var eas_supplier_number: String,
    @ColumnInfo(name="is_out_eas") var is_out_eas: Boolean,
    @ColumnInfo(name="remain") var remain: Int,
    @ColumnInfo(name="rfid") var rfid: String,
    @ColumnInfo(name="eas_manufacturer") var eas_manufacturer: String,
    @ColumnInfo(name="eas_material_desc") var eas_material_desc: String,
    @ColumnInfo(name="eas_material_id") var eas_material_id: String,
    @ColumnInfo(name="eas_material_name") var eas_material_name: String,
    @ColumnInfo(name="eas_material_number") var eas_material_number: String,
    @ColumnInfo(name="eas_unit_id") var eas_unit_id: String,
    @ColumnInfo(name="eas_unit_name") var eas_unit_name: String,
    @ColumnInfo(name="eas_unit_number") var eas_unit_number: String,
    @ColumnInfo(name="eas_lot") var eas_lot: String,
    @ColumnInfo(name="eas_specs") var eas_specs: String,
    @ColumnInfo(name="expired_at") var expired_at: String
)

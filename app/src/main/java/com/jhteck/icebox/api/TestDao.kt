package com.jhteck.icebox.api

/**
 *@Description:(用一句话描述)
 *@author wade
 *@date 2023/7/5 23:52
 */
data class TestDao(
    val code: Int,
    val msg: String,
    val results: TestResults
)

data class TestResults(
    val created_at: String,
    val eas_supplier_name: String,
    val eas_supplier_number: String,
    val fridge: Fridge,
    val id: Int,
    val is_out_eas: Boolean,
    val material: TestMaterial,
    val material_batch: TestMaterialBatch,
    val remain: Int,
    val rfid: String
)

data class Fridge(
    val admin_name: String,
    val admin_password_digest: String,
    val cells: Int,
    val consume_count: Int,
    val deposit_count: Int,
    val device_alias: String,
    val id: Int,
    val last_sync_at: String,
    val location: String,
    val sncode: String,
    val store_count: Int,
    val style: Int,
    val temperature: Int,
    val warehouse: String
)

data class TestMaterial(
    val eas_manufacturer: String,
    val eas_material_desc: String,
    val eas_material_id: String,
    val eas_material_name: String,
    val eas_material_number: String,
    val eas_unit_id: String,
    val eas_unit_name: String,
    val eas_unit_number: String
)

data class TestMaterialBatch(
    val eas_lot: String,
    val eas_specs: String,
    val expired_at: String
)
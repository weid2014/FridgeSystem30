package com.jhteck.icebox.repository.entity.diule

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "t_repertory_change")
data class RepertoryChange(
    @PrimaryKey(autoGenerate = true) val id: Int,//id
    @ColumnInfo(name = "product_id") val productId: Int,//商品编码
    @ColumnInfo(name = "product_name") val productName: String,//商品名称
    @ColumnInfo(name = "product_specifications") val productSpecifications: String,//商品规格
    @ColumnInfo(name = "product_mfrs") val productMfrs: String,//生产厂家
    @ColumnInfo(name = "product_mfrs_unit") val productMfrsUnit: String,//单位
    @ColumnInfo(name = "product_remaining") val productRemaining: String,//试剂余量;非现场人员则100/上次余量
    @ColumnInfo(name = "product_batch") val productBatch: String,//商品批号
    @ColumnInfo(name = "product_validity_period") val productValidityPeriod: String,//有效时间
    @ColumnInfo(name = "product_code") val productCode: String,//单品码;rfid
    @ColumnInfo(name = "error_chg_type") val errorChgType: String,//异常变更类型;操作类型
    @ColumnInfo(name = "user_log_id") val userLogId: String,//操作编码;operation的字母类型+refrigerator的id+时间戳
    @ColumnInfo(name = "updated_by_id") val updatedById: String,//操作人id
    @ColumnInfo(name = "updated_by") val updatedBy: String,//操作人;nickname
    @ColumnInfo(name = "updated_by_role") val updatedByRole: String,//操作人权限;roleid
    @ColumnInfo(name = "updated_time") val updatedTime: String,//操作时间
)
package com.jhteck.icebox.repository.entity.diule

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 库存变更报错操作记录
 */
@Entity(tableName = "t_abnormal_change")
data class AbnormalChange(
    @PrimaryKey(autoGenerate = true) val id:Int,
    @ColumnInfo(name = "product_id") val productId: String,//编号
    @ColumnInfo(name = "product_name") val productName: String,
    @ColumnInfo(name = "product_specification") val productSpecification: String,//商品规格
    @ColumnInfo(name = "obtain_time") val obtainTime: String,//获取时间
    @ColumnInfo(name = "product_mfrs") val productMfrs: String,//生产厂家
    @ColumnInfo(name = "product_mfrs_unit") val productMfrsUnit: String,//生产单位
    @ColumnInfo(name = "product_remaining") val productRemaining: String,//试剂余量
    @ColumnInfo(name = "product_batch") val productBatch: String,//商品批号
    @ColumnInfo(name = "product_code") val productCode: String,//单品码
    @ColumnInfo(name = "error_chg_type") val errorChgType: String,//异常变更类型;操作类型
    @ColumnInfo(name = "updated_by") val updatedBy: String,//操作人;nickname
    @ColumnInfo(name = "updated_by_role") val updatedByRole: String,//操作人权限;roleid
    @ColumnInfo(name = "updated_time") val updatedTime: String,//单品码
)
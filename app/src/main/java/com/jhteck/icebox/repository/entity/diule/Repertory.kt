package com.jhteck.icebox.repository.entity.diule

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 *  "id": 62,
 *                 "rfid": "E1234567000000000000003E",
 *                 "remain": 100,
 *                 "eas_supplier_number": "01.19.17.0002",
 *                 "eas_supplier_name": "复星诊断科技（上海）有限公司",
 *                 "created_at": "2022-10-28T12:02:24.782+08:00",
 *                 "is_out_eas": false,
 *                 "material": {
 *                     "eas_material_id": "Xe8AAAAkKS5ECefw",
 *                     "eas_material_number": "01.03.0012.10037.00001",
 *                     "eas_material_name": "30040201CM01 NSE CLIA 神经元特异性烯醇化酶检测试剂盒（磁微粒化学发光法）",
 *                     "eas_unit_id": "Xe8AAAAAnWFbglxX",
 *                     "eas_unit_number": "He",
 *                     "eas_unit_name": "盒",
 *                     "eas_material_desc": "",
 *                     "eas_manufacturer": "厂商2"
 *                 },
 *                 "material_batch": {
 *                     "eas_lot": "20220304",
 *                     "eas_specs": "100T",
 *                     "expired_at": "2023-03-03T00:00:00.000+08:00"
 *                 }
 */

/**
 * 库存表
 */
@Entity(tableName = "t_repertory")
data class Repertory(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "product_id") val productId: String,//编号
    @ColumnInfo(name = "product_name") val productName: String,
    @ColumnInfo(name = "product_specification") val productSpecification: String,//商品规格
    @ColumnInfo(name = "obtain_time") val obtainTime: String,//获取时间
    @ColumnInfo(name = "product_mfrs") val productMfrs: String,//生产厂家
    @ColumnInfo(name = "product_mfrs_unit") val productMfrsUnit: String,//生产单位
    @ColumnInfo(name = "product_remaining") val remain: String,//试剂余量
    @ColumnInfo(name = "product_batch") val eas_supplier_number: String,//商品批号
    @ColumnInfo(name = "product_validity_period") val productValidityPeriod: String,//有效期限
    @ColumnInfo(name = "rfid") val rfid: String,//单品码
);
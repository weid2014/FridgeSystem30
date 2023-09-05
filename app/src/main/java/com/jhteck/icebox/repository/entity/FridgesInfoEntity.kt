package com.jhteck.icebox.repository.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "t_fridges_info")
data class FridgesInfoEntity(
    @PrimaryKey(autoGenerate = true) var f_id: Int,
    @ColumnInfo val id: String?,// 新激活或已存在的冰箱ID
    @ColumnInfo val sncode: String?, //冰箱序列号
    @ColumnInfo val device_alias: String?, //设备别名
    @ColumnInfo val admin_name: String?, //管理员账号名
    @ColumnInfo val location: String?, //库位
    @ColumnInfo val store_count: Integer?, //存料次数
    @ColumnInfo val consume_count: Integer?, //取料次数
    @ColumnInfo val deposit_count: Integer?, //暂存次数
    @ColumnInfo val temperature: Integer?, //冰箱温度，单位为 0.01 °C（即 100°C 数值为 10000）
    @ColumnInfo val style: Integer?, //类型： 0 - 普通冰箱； 1 - 常温冰箱；
    @ColumnInfo val cells: Integer?, //格子数量
    @ColumnInfo val last_sync_at: String?,//最后同步时间
)
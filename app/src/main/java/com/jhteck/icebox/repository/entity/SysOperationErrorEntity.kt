package com.jhteck.icebox.repository.entity

import android.provider.ContactsContract
import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * 系统操作错误
 */
@Entity(tableName = "t_sys_operation_error")
data class SysOperationErrorEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    var user_id: String,//用户ID（接口会校验是否存在） string 用户ID
    var nick_name:String,//string 用户名
    var role_id:String,//冰箱上的角色ID：10 - 系统管理员; 20 - 仓库管理员; 30 - 现场人员;
    var km_user_id:String,//金域用户ID
    var real_name:String, //姓名
    var error_code: Int,//错误类型，包括以下3种
//    10 - APP启动
//    20 - 智能锁异常
//30 - 天线异常
    var network: String,//网络状态
    var system_info: String,// 系统信息
    var app_version: String,//APP版本
    var port_storage: String,//串口信息
    var rf_storage: String,// 冰箱信息
    var log_at: String,// 日志时间
    var hasUpload: Boolean
)


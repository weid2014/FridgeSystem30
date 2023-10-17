package com.jhteck.icebox.api

import com.jhteck.icebox.repository.entity.*

data class ApiResponse<T>(
    val code: Int?,
    val msg: String?,
    val data: T?,
    val rows: T?,
)

data class CommonResponse<T>(
    val code: Int?,
    val msg: String?,
    val results: T?,
)

data class LoginResponseDto(
    val access_token: String? = null
)

data class RfidDao(
    val code: Int,
    val msg: String,
    val results: RfidResults
)

data class RfidResults(
    var avail_rfids: List<AvailRfid>,
    var unavail_rfids: List<String>?
)


data class AvailRfid(
    val id: Int,
    var cell_number: Int = 1,
    var created_at: String,
    var eas_supplier_name: String,
    var eas_supplier_number: String,
    var is_out_eas: Boolean,
    var material: Material,
    var material_batch: MaterialBatch,
    var remain: Int,
    var rfid: String,
    var fridge_id: String,
)

data class AntPowerDao(
    var antid: String,
    var power: String = "1",
)


data class Material(
    val id: Int,
    val eas_manufacturer: String,
    val eas_material_desc: String,
    val eas_material_id: String,
    val eas_material_name: String,
    val eas_material_number: String,
    val eas_unit_id: String,
    val eas_unit_name: String,
    val eas_unit_number: String
)

data class MaterialBatch(
    val id: Int,
    val eas_lot: String,
    val eas_specs: String,
    val expired_at: String
)

data class UserTestDao(
    val code: Int,
    val msg: String,
    val results: UserResults
)

data class UserResults(
    val consume_count: Int,
    val created_by: String,
    val created_time: String,
    val deposit_count: Int,
    val km_user_id: String,
    val login_time: String,
    val nfc_id: String,
    val nick_name: String,
    val password_digest: String,
    val real_name: String,
    val role_id: String,
    val store_count: Int,
    val updated_by: String,
    val updated_time: String,
    val user_id: String
)


data class requestUser(
    val created_by: String,
    val created_time: String,
    val km_user_id: String,
    val login_time: String,
    val nfc_id: String,
    val nick_name: String,
    val password_digest: String,
    val real_name: String,
    val role_id: String,
    val updated_by: String,
    val updated_time: String,
    val user_id: String
)

data class UserListDao(
    val consume_count: Int,
    val created_by: String,
    val created_time: String,
    val deposit_count: Int,
    val km_user_id: String,
    val login_time: String,
    val nfc_id: String,
    val nick_name: String,
    val password_digest: String,
    val real_name: String,
    val role_id: String,
    val store_count: Int,
    val updated_by: String,
    val updated_time: String,
    val user_id: String
)

data class UpdateInfoDto(
    val content: String,
    val date: String,
    val minSupport: Int,
    val mustUpdate: Boolean,
    val updateTitle: String,
    val url: String,
    val versionCode: Int,
    val versionName: String
)

data class GetUserResult(
    var code: Int,
    var msg: String,
    var results: UsertoResults
)

data class UsertoResults(
    var accounts: List<AccountEntity>
)


data class AccountOperationBO(
    var logs: List<AccountOperationEntity>
)

data class AccountOperationReturnBO(
    var unrecords: List<String>
)

data class RfidOperationBO(
    var logs: List<RfidOperationEntity>
)

data class RfidOperationReturnBO(
    var unrecords: List<RfidOperationEntity>
)

data class FridgesActiveBo(
    val admin_name: String,
    val admin_password_digest: String,
    val cells: Int,
    val device_alias: String,
    val location: String,
    val sncode: String,
    val style: Int,
    val temperature: Int
)

data class SysOperationErrorLogsBo(
    var logs: List<SysOperationErrorEntity>
)

data class SysOperationErrorLogsResult(
    var unrecords: List<SysOperationErrorEntity>
)

data class OperationErrorLogsBo(
    var logs: List<OperationErrorLogEntity>
)

data class OperationErrorLogsResult(
    var unrecords: List<OperationErrorLogEntity>
)

//冰箱的系统错误日志列表
data class SystemOperationErrorLogsR(
    var logs: List<SystemOperationErrorLogItem>
)

data class SystemOperationErrorLogItem(
    val id: Int,//日志ID
    val user: AccountEntity,
    val error_code: Int,//错误类型，包括以下3种
    val network: String,//日志ID
    val system_info: String,//日志ID
    val app_version: String,//日志ID
    val port_storage: String,//日志ID
    val rf_storage: String,//日志ID
    val log_at: String,//日志ID
)

//冰箱的系统错误日志列表
data class OperationErrorLogsR(
    var logs: List<OperationErrorLogItem>
)

data class OperationErrorLogItem(
    val id: Int,//日志ID
    val error_code: Int,//错误类型，包括以下3种
    val log_at: String,// 日志时间
    val user: AccountEntity,//用户快照对象
    val capture_item: AvailRfid//单品快照对象
)




package com.jhteck.icebox.api.request

/**
 * 请求用到的request body
 */

data class RequestRfidsDao(
    var rfids: List<String>
)

data class requestSync(
    val rfids: List<RfidSync>
)

data class RfidSync(
    val cell_number: Int,
    val remain: Int,
    val rfid: String,
    var last_fridge_first_sync_at:String?
)
package com.jhteck.icebox.api

const val DEBUG = true//测试模式
const val EXIST_HARD_DEVICE = true//是否存在硬件
const val IS_FIRST_RUN = "is_first_run"//是否第一次运行
const val LOCKED_SUCCESS = "全关锁成功提示"
const val LOCKED_SHUT = "锁一直未关提示"
const val INVENTORY_OVER = "结束盘点"
const val HFCard = "HFCard"
const val REPORT_ANT_POWER = "report_ant_power"
const val REPORT_ANT_POWER_30 = "report_ant_power_30"
const val ROLE_ID = "role_id"
const val BROADCAST_INTENT_FILTER = "com.jhteck.icebox.content"
const val TCP_MSG_KEY = "tcp_msg_key"
const val TCP_MSG_VALUE = "tcp_msg_value"
const val EXIT_APP_MSG = "exit_app_msg"
const val UPDATE_ACCOUNT_MSG = "update_account_msg"
const val SYNC_ACCOUNT_MSG = "sync_account_msg"

const val UPDATE_JSON_ADDRESS = "update_json_address"
const val UPDATE_APK_ADDRESS = "update_apk_address"
const val SNCODE = "sncode"
//const val SNCODE_TEST = "FEDCBA0123456788"

const val SNCODE_TEST = "FEDCBA01CC000001"
const val SNCODE_ORIGEN = "FEDCBA01CC000001"
const val URL_REQUEST = "url_request"
const val URL_TEST = "https://jh.test.lavandachen.com"
const val URL_KM = "http://10.128.81.174:8086"
const val NOT_HARD_DEVICE = false

const val SERIAL_PORT_LOCK = "serial_port_lock"
const val SERIAL_PORT_LOCK_DEFAULT = "/dev/ttyS8"
const val SERIAL_PORT_RFID = "serial_port_rfid"
const val SERIAL_PORT_RFID_DEFAULT = "/dev/ttyS2"
const val INVENTORY_TIME = "inventory_time"
const val INVENTORY_TIME_DEFAULT = 10 * 1000L

const val APP_ID = "3Xpdq6N7xzday4bVTdFxSzLVWhRLEgm5Pgei8HmNrWzZ"
const val SDK_KEY = "GK9tmQ23mNFZSgyQnnYYqKnCsW5iqBiKTnfmz1igFpJN"
const val ACTIVE_KEY = "85Q1-11LF-G138-3BJR"

/**
 * IR预览数据相对于RGB预览数据的横向偏移量，注意：是预览数据，一般的摄像头的预览数据都是 width > height
 */
const val HORIZONTAL_OFFSET = 0

/**
 * IR预览数据相对于RGB预览数据的纵向偏移量，注意：是预览数据，一般的摄像头的预览数据都是 width > height
 */
const val VERTICAL_OFFSET = 0


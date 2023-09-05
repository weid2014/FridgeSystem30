package com.jhteck.icebox.bean

/**
 * 用户操作日志
 */
enum class AccountOperationEnum(val v: Int, val desc: String) {
    UNKNOWN(-10, "未知"),
    NO_OPERATION(0, "无存取"),
    STORE(1, "存入"),
    CONSUME(2, "取出"),
    DEPOSIT(3, "暂存"),
    STORE_CONSUME(4, "存入和取出"),
    DESPOSIT_CONSUME(5, "暂存和取出")
    ;

    companion object {
        fun getEnumByV(index: Int): RoleEnum {
            for (value in RoleEnum.values()) {
                if (index == value.v) {
                    return value
                }
            }
            return RoleEnum.UNKNOWN
        }

        fun getEnumByDesc(desc: String): RoleEnum {
            for (value in RoleEnum.values()) {
                if (desc == value.desc) {
                    return value
                }
            }
            return RoleEnum.UNKNOWN
        }

        /**
         * 根据值获取描述信息
         */
        fun getDesc(index: Int): String {
            return getEnumByV(index).desc
        }

        /**
         * 根据描述信息获取值
         */
        fun getV(desc: String): Int {
            return getEnumByDesc(desc).v
        }
    }
}

/**
 * 操作异常
 */
enum class OperationErrorEnum(val v: Int, val desc: String) {
    UNKNOWN(-10, "未知"),
    REBOOT(10, "故障重启"),
    TIME_OUT(20, "操作超时"),
    NOT_MARK(30, " 未记暂存"),
    ;

    companion object {
        fun getEnumByV(index: Int): RoleEnum {
            for (value in RoleEnum.values()) {
                if (index == value.v) {
                    return value
                }
            }
            return RoleEnum.UNKNOWN
        }

        fun getEnumByDesc(desc: String): RoleEnum {
            for (value in RoleEnum.values()) {
                if (desc == value.desc) {
                    return value
                }
            }
            return RoleEnum.UNKNOWN
        }

        /**
         * 根据值获取描述信息
         */
        fun getDesc(index: Int): String {
            return getEnumByV(index).desc
        }

        /**
         * 根据描述信息获取值
         */
        fun getV(desc: String): Int {
            return getEnumByDesc(desc).v
        }
    }
}

/**
 * 操作异常
 */
enum class SystemOperationErrorEnum(val v: Int, val desc: String) {
    UNKNOWN(-10, "未知"),
    REBOOT(10, "APP启动"),
    TIME_OUT(20, "智能锁异常"),
    NOT_MARK(30, " 天线异常"),
    ;

    companion object {
        fun getEnumByV(index: Int): RoleEnum {
            for (value in RoleEnum.values()) {
                if (index == value.v) {
                    return value
                }
            }
            return RoleEnum.UNKNOWN
        }

        fun getEnumByDesc(desc: String): RoleEnum {
            for (value in RoleEnum.values()) {
                if (desc == value.desc) {
                    return value
                }
            }
            return RoleEnum.UNKNOWN
        }

        /**
         * 根据值获取描述信息
         */
        fun getDesc(index: Int): String {
            return getEnumByV(index).desc
        }

        /**
         * 根据描述信息获取值
         */
        fun getV(desc: String): Int {
            return getEnumByDesc(desc).v
        }
    }
}
package com.jhteck.icebox.bean

/**
 *@Description:(用一句话描述)
 *@author wade
 *@date 2023/7/8 16:16
 */
enum class RoleEnum(val v: Int, val desc: String) {
    UNKNOWN(-10, "未知"),
    ADMIN(10, "系统管理员"),
    KEEPER(20, "仓库管理员"),
    SCENER(30, "现场人员");

    companion object {
        fun getEnumByV(index: Int): RoleEnum {
            for (value in values()) {
                if (index == value.v) {
                    return value
                }
            }
            return RoleEnum.UNKNOWN
        }
        fun getEnumByDesc(desc: String): RoleEnum {
            for (value in values()) {
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
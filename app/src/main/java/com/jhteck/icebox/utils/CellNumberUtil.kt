package com.jhteck.icebox.utils

import android.util.Log

/**
 *@Description:(位置信息工具类)
 *@author wade
 *@date 2023/7/27 16:48
 */
object CellNumberUtil {
    fun getCellNumberText(cellNumber: Int): String {
        return when (cellNumber) {
             2 -> {
                return "1层左"
            }
            3 -> {
                return "1层中"
            }
            4 -> {
                return "1层右"
            }
            5 -> {
                return "2层左"
            }
            6 -> {
                return "2层中"
            }
            7 -> {
                return "2层右"
            }
            8 -> {
                return "3层左"
            }
            9 -> {
                return "3层中"
            }
            10 -> {
                return "3层右"
            }
            11 -> {
                return "4层左"
            }
            12 -> {
                return "4层中"
            }
            13 -> {
                return "4层右"
            }
            14 -> {
                return "5层左"
            }
            15 -> {
                return "5层中"
            }
            16 -> {
                return "5层右"
            }
            else -> {
                return "未分配"
            }
        }
    }

    fun getCellNumberTextByStr(cellNumberStr: String, leftRightStr: String): Int {
        Log.d("lalala", "cellNumberStr===${cellNumberStr}leftRightStr===${leftRightStr}")
        if (cellNumberStr == "第1层" && leftRightStr == "左") {
            return 2
        } else if (cellNumberStr == "第1层" && leftRightStr == "中") {
            return 3
        } else if (cellNumberStr == "第1层" && leftRightStr == "右") {
            return 4
        } else if (cellNumberStr == "第2层" && leftRightStr == "左") {
            return 5
        } else if (cellNumberStr == "第2层" && leftRightStr == "中") {
            return 6
        } else if (cellNumberStr == "第2层" && leftRightStr == "右") {
            return 7
        } else if (cellNumberStr == "第3层" && leftRightStr == "左") {
            return 8
        } else if (cellNumberStr == "第3层" && leftRightStr == "中") {
            return 9
        } else if (cellNumberStr == "第3层" && leftRightStr == "右") {
            return 10
        } else if (cellNumberStr == "第4层" && leftRightStr == "左") {
            return 11
        } else if (cellNumberStr == "第4层" && leftRightStr == "中") {
            return 12
        } else if (cellNumberStr == "第4层" && leftRightStr == "右") {
            return 13
        } else if (cellNumberStr == "第5层" && leftRightStr == "左") {
            return 14
        } else if (cellNumberStr == "第5层" && leftRightStr == "中") {
            return 15
        } else if (cellNumberStr == "第5层" && leftRightStr == "右") {
            return 16
        } else {
            return 2
        }
    }
}
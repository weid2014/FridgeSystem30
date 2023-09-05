package com.jhteck.icebox.utils

import android.content.Context
import android.content.Intent
import com.jhteck.icebox.api.BROADCAST_INTENT_FILTER
import com.jhteck.icebox.api.TCP_MSG_KEY
import com.jhteck.icebox.api.TCP_MSG_VALUE

/**
 *@Description:(广播工具)
 *@author wade
 *@date 2023/7/19 16:21
 */
object BroadcastUtil {
    fun sendMyBroadcast(context: Context,key: String?, value: String?) {
        val intent = Intent()
        intent.action = BROADCAST_INTENT_FILTER
        intent.putExtra(TCP_MSG_KEY, key)
        intent.putExtra(TCP_MSG_VALUE, value)
        context.sendBroadcast(intent)
    }
}
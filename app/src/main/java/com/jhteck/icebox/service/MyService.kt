package com.jhteck.icebox.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.jhteck.icebox.Lockmodel.LockManage
import com.jhteck.icebox.api.*
import com.jhteck.icebox.myinterface.MyCallback
import com.jhteck.icebox.nfcmodel.NfcManage
import com.jhteck.icebox.rfidmodel.RfidManage
import com.jhteck.icebox.utils.BroadcastUtil
import com.jhteck.icebox.utils.SharedPreferencesUtils
import java.util.*

/**
 *@Description:(后台service)
 *@author wade
 *@date 2023/6/30 13:39
 */

class MyService : Service() {
    private val TAG = "MyService"

    companion object {
        var isRunning = false
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(
            TAG,
            "MyTcpServerListener.getInstance().startServer(); - onStartCommand - startId = $startId, Thread = " + Thread.currentThread().name
        )
        if (isRunning) {
            // 如果服务已经在运行，就不执行任何操作
            return START_REDELIVER_INTENT
        }
        if (DEBUG)
            return START_REDELIVER_INTENT
        LogUpLoadManager.startUploadAsync();

        //wait wait wait
        NfcManage.getInstance().startNfcPort()

        RfidManage.getInstance().initReader()
        RfidManage.getInstance().linkDevice(
            true, SharedPreferencesUtils.getPrefString(
                this,
                SERIAL_PORT_RFID, SERIAL_PORT_RFID_DEFAULT
            )
        )

        LockManage.getInstance().initSerialByPort(
            SharedPreferencesUtils.getPrefString(
                this,
                SERIAL_PORT_LOCK, SERIAL_PORT_LOCK_DEFAULT
            )
        )

        /*MyTcpServerListener.getInstance().setOnResultListener {
            Log.d("tcpserver", "setOnResultListener MyTcpMsg${it.toString()}")
            if (it.type.equals(LOCKED_SUCCESS)) {
                sendContentBroadcast(LOCKED_SUCCESS, "lock")
            } else if (it.type.equals(HFCard)) {
                sendContentBroadcast(HFCard, it.content)
            } else {
                sendContentBroadcast(it.type, it.content)
            }
        }*/
        NfcManage.getInstance().setStringMyCallback(object : MyCallback<String> {
            override fun callback(result: String) {
                //返回卡号
                sendContentBroadcast(HFCard, result)
            }
        })
        RfidManage.getInstance().setAntPowerArrayCallback(object : MyCallback<String> {
            override fun callback(result: String) {
                sendContentBroadcast(REPORT_ANT_POWER_30, result)
            }
        })
        LockManage.getInstance().setLockCallback(object : MyCallback<String> {
            override fun callback(result: String) {
                Log.d(TAG, "lock result is $result")
                sendContentBroadcast(LOCKED_SUCCESS, "lock")
            }
        })
        isRunning = true
        return START_REDELIVER_INTENT
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        LockManage.getInstance().clearAll()
        NfcManage.getInstance().clearAll()
        RfidManage.getInstance().clearAll()
        Log.d(TAG, "MyService onDestroy")
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }


    /**
     * 发送广播
     * @param name
     */
    fun sendContentBroadcast(key: String?, value: String?) {
        // TODO Auto-generated method stub
        /* val intent = Intent()
         intent.action = BROADCAST_INTENT_FILTER
         intent.putExtra(TCP_MSG_KEY, key)
         intent.putExtra(TCP_MSG_VALUE, value)
         sendBroadcast(intent)*/
        BroadcastUtil.sendMyBroadcast(this, key, value)
    }
}
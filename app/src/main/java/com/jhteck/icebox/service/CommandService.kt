package com.jhteck.icebox.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class CommandService : Service() {
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //凌晨四点发送命令
        Log.d("CommandService", "onStartCommand")
        LogUpLoadManager.startUploadAsync();
        return START_NOT_STICKY
    }
}
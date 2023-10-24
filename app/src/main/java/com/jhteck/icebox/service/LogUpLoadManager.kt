package com.jhteck.icebox.service

import android.util.Log
import com.google.gson.Gson
import com.jhteck.icebox.api.AccountOperationBO
import com.jhteck.icebox.api.OperationErrorLogsBo
import com.jhteck.icebox.api.RfidOperationBO
import com.jhteck.icebox.api.SysOperationErrorLogsBo
import com.jhteck.icebox.apiserver.RetrofitClient
import com.jhteck.icebox.utils.ContextUtils
import com.jhteck.icebox.utils.DbUtil
import com.jhteck.icebox.utils.NetworkUtil
import extension.log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.stream.Collectors

class LogUpLoadManager {
    companion object instance {
        private val TAG = "LogUpLoadManager"

        fun startUploadAsync() {
            //操作日志上传
            startUploadOperationLogTask()
            //RFID日志上传
            startUpLoadRfidOperationLogTask()
            //系统操作异常
            startUpLoadSysOperationErrorLogsTask()
            //操作系统错误日志
            startUpLoadOperationErrorLogsTask()
        }

        /**
         * 操作日志上传
         */
        private fun startUploadOperationLogTask() {
            GlobalScope.launch(context = Dispatchers.IO) {
                val accountOperationDao = DbUtil.getDb().accountOperationDao();
                Log.i(TAG, "startUploadOperationLogTask")
                while (true) {
                    try {
                        if (isNetAvailable()) {
                            Thread.sleep(1200 * 1000);//十秒钟后尝试一次
                            continue
                        }
                        var all = accountOperationDao.getAll()
                        if (all.size == 0) {
                            Thread.sleep(1200 * 1000);
                            continue
                        }
                        var logs = all.stream().filter { t -> !t.hasUploaded }
                            .collect(Collectors.toList())
                        if (logs.size == 0) {
                            Thread.sleep(1200 * 1000);
                            continue
                        }
                        var accountOperationBO = AccountOperationBO(logs)

                        var toJson = Gson().toJson(accountOperationBO)
                        println(toJson)

                        var result =
                            RetrofitClient.getService().addAccountLogs(accountOperationBO)//发送到平台
                        if (result.code() == 200) {
                            for (item in logs) {
                                item.hasUploaded = true
                                DbUtil.getDb().accountOperationDao().update(item);//操作记录状态更新
                            }
                        }
                    } catch (e: Exception) {
                        Log.i(
                            TAG, e.message.toString()
                        )
                        try {
                            Thread.sleep(60 * 1000 * 30);//五分钟后尝试推送数据
                        } catch (e: Exception) {
                            Log.i(
                                TAG, e.message.toString()
                            )
                        }
                    }
                }
            }
        }

        /**
         * RFID日志上传
         */
        private fun startUpLoadRfidOperationLogTask() {
            GlobalScope.launch(context = Dispatchers.IO) {
                Log.i(TAG, "startUpLoadRfidOperationLogTask")
                val rfidOperationDao = DbUtil.getDb().rfidOperationDao();
                while (true) {
                    try {
                        if (isNetAvailable()) {
                            Thread.sleep(300 * 1000);//十秒钟后尝试一次
                            continue
                        }
                        var all = rfidOperationDao.getAll()
                        if (all.size == 0) {
                            Thread.sleep(1200 * 1000);
                            continue
                        }
                        var logs =
                            all.stream().filter { t -> t.isHasUpload == null || !t.isHasUpload }
                                .collect(Collectors.toList())
                        if (logs.size == 0) {
                            Thread.sleep(1200 * 1000);
                            continue
                        }
                        var rfidOperationBO = RfidOperationBO(logs);

                        var toJson = Gson().toJson(rfidOperationBO)
                        Log.d(TAG, "${toJson}")
                        var res = RetrofitClient.getService().syncRfidsLogs(rfidOperationBO)
                        if (res.code() == 200) {
                            for (data in rfidOperationBO.logs) {
                                data.isHasUpload = true;
                                DbUtil.getDb().rfidOperationDao().update(data);
                            }
                        }

                    } catch (e: Exception) {
                        Log.i(
                            TAG, e.message.toString()
                        )
                        try {
                            Thread.sleep(60 * 1000 * 30);//五分钟后尝试推送数据
                        } catch (e: Exception) {
                            Log.i(
                                TAG, e.message.toString()
                            )
                        }
                    }
                }
            }
        }

        private fun isNetAvailable(): Boolean {
            return false;
//            return !NetworkUtil.isNetSystemUsable(ContextUtils.getApplicationContext())
//                    || !NetworkUtil.isNetOnline()
        }

        /**
         * 系统操作异常
         */
        private fun startUpLoadSysOperationErrorLogsTask() {
            GlobalScope.launch(context = Dispatchers.IO) {
                Log.i(TAG, "startUpLoadSysOperationErrorLogsTask")
                val sysOperationErrorDao = DbUtil.getDb().sysOperationErrorDao();
                while (true) {
                    try {
                        if (isNetAvailable()) {
                            Thread.sleep(300 * 1000);//十秒钟后尝试一次
                            continue
                        }
                        var all = sysOperationErrorDao.getAll()
                        if (all.isEmpty()) {
                            Thread.sleep(1200 * 1000);
                            continue
                        }
                        var logs =
                            all.stream().filter { t -> t.hasUpload == null || !t.hasUpload }
                                .collect(Collectors.toList())
                        if (logs.size == 0) {
                            Thread.sleep(1200 * 1000);
                            continue
                        }
                        var rfidOperationBO = SysOperationErrorLogsBo(logs);

                        var toJson = Gson().toJson(rfidOperationBO)
                        Log.d(TAG, "${toJson}")
                        var res = RetrofitClient.getService().addSystemErrorLogs(rfidOperationBO)
                        if (res.code() == 200) {
                            for (data in rfidOperationBO.logs) {
                                data.hasUpload = true;
                                sysOperationErrorDao.update(data);
                            }
                        }
                    } catch (e: Exception) {
                        Log.i(TAG, e.message.toString())
                    }
                }
            }
        }

        /**
         * 系统操作异常
         */
        private fun startUpLoadOperationErrorLogsTask() {
            GlobalScope.launch(context = Dispatchers.IO) {
                Log.i(TAG, "startUpLoadOperationErrorLogsTask")
                val operationErrorDao = DbUtil.getDb().operationErrorLogDao();
                while (true) {
                    try {
                        if (isNetAvailable()) {
                            Thread.sleep(1200 * 1000);//十秒钟后尝试一次
                            continue
                        }
                        var all = operationErrorDao.getAll()
                        if (all.isEmpty()) {
                            Thread.sleep(1200 * 1000);
                            continue
                        }
                        var logs =
                            all.stream().filter { t -> t.hasUpload == null || !t.hasUpload }
                                .collect(Collectors.toList())
                        if (logs.size == 0) {
                            Thread.sleep(1200 * 1000);
                            continue
                        }
                        var rfidOperationBO = OperationErrorLogsBo(logs);

                        var toJson = Gson().toJson(rfidOperationBO)
                        Log.d(TAG, "${toJson}")
                        var res = RetrofitClient.getService().addOperationErrorLogs(rfidOperationBO)
                        if (res.code() == 200) {
                            for (data in rfidOperationBO.logs) {
                                data.hasUpload = true;
                                operationErrorDao.update(data);
                            }
                        }
                    } catch (e: Exception) {
                        Log.i(TAG, e.message.toString())
                    }
                }
            }
        }
    }
}
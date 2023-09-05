package com.jhteck.icebox.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.jhteck.icebox.api.*
import com.jhteck.icebox.nfcmodel.NfcManage
import com.jhteck.icebox.rfidmodel.RfidManage
import com.jhteck.icebox.tcpServer.MyTcpServerListener
import com.jhteck.icebox.utils.BroadcastUtil
import java.util.*

/**
 *@Description:(后台service)
 *@author wade
 *@date 2023/6/30 13:39
 */

class MyService : Service() {
    private val TAG = "MyService"

    //client 可以通过Binder获取Service实例
    inner class MyBinder : Binder() {
        val service: MyService
            get() = this@MyService
    }

    //通过binder实现调用者client与Service之间的通信
    private val binder = MyBinder()
    private val generator: Random = Random()
    override fun onBind(intent: Intent?): IBinder? {
        MyTcpServerListener.getInstance().setOnResultListener {
            Log.d("tcpserver", "setOnResultListener MyTcpMsg${it.toString()}")
            if (it.type.equals(LOCKED_SUCCESS)) {
                sendContentBroadcast(LOCKED_SUCCESS, "lock")
            } else if (it.type.equals(HFCard)) {
                sendContentBroadcast(HFCard, it.content)
            }else {
                sendContentBroadcast(it.type,it.content)
            }
        }
        return binder
    }

    override fun onCreate() {
        super.onCreate()
//        MyTcpServerListener.getInstance().startServer();
        Log.d(TAG, "MyService onCreate")
//        LogUpLoadManager.startUploadAsync();

//        NfcManage.getInstance().startNfcPort()
        /*RfidManage.getInstance().initReader()
        RfidManage.getInstance().linkDevice(true)
            RfidManage.getInstance().startStop(true)*/

    }

    override fun onUnbind(intent: Intent?): Boolean {
        return false
    }


    /**
     *  @param intent 启动时，启动组件传递过来的Intent，如Activity可利用Intent封装所需要的参数并传递给Service
     *  @param flags 表示启动请求时是否有额外数据，可选值有 0，START_FLAG_REDELIVERY，START_FLAG_RETRY
     *  0: 在正常创建Service的情况下，onStartCommand传入的flags为0。
     *
     *  START_FLAG_REDELIVERY:
     *  这个值代表了onStartCommand()方法的返回值为 START_REDELIVER_INTENT，
     *  而且在上一次服务被杀死前会去调用stopSelf()方法停止服务。
     *  其中START_REDELIVER_INTENT意味着当Service因内存不足而被系统kill后，
     *  则会重建服务，并通过传递给服务的最后一个 Intent调用 onStartCommand()，此时Intent时有值的。
     *
     *  START_FLAG_RETRY
     *  该flag代表当onStartCommand()调用后一直没有返回值时，会尝试重新去调用onStartCommand()。
     *
     *  @param startId 指明当前服务的唯一ID，与stopSelfResult(int startId)配合使用，stopSelfResult()可以更安全地根据ID停止服务。
     *
     *  @return
     *  START_STICKY:
     *  当Service因内存不足而被系统kill后，一段时间后内存再次空闲时，
     *  系统将会尝试重新创建此Service，一旦创建成功后将回调onStartCommand方法，
     *  但其中的Intent将是null，除非有挂起的Intent，如pendingintent，
     *  这个状态下比较适用于不执行命令、但无限期运行并等待作业的媒体播放器或类似服务
     *
     *
     *  START_NOT_STICKY:
     *  当Service因内存不足而被系统kill后，即使系统内存再次空闲时，
     *  系统也不会尝试重新创建此Service。除非程序中再次调用startService启动此Service，
     *  这是最安全的选项，可以避免在不必要时以及应用能够轻松重启所有未完成的作业时运行服务。
     *
     *  START_REDELIVER_INTENT:
     *  当Service因内存不足而被系统kill后，则会重建服务，
     *  并通过传递给服务的最后一个 Intent 调用 onStartCommand()，任何挂起 Intent均依次传递。
     *  与START_STICKY不同的是，其中的传递的Intent将是非空，是最后一次调用startService中的intent。
     *  这个值适用于主动执行应该立即恢复的作业（例如下载文件）的服务。
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(
            TAG,
            "MyTcpServerListener.getInstance().startServer(); - onStartCommand - startId = $startId, Thread = " + Thread.currentThread().name
        )

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        MyTcpServerListener.getInstance().startServer();
        Log.d(TAG, "MyService onDestroy")
    }

    //getRandomNumber是Service暴露出去供client调用的公共方法
    fun getRandomNumber(): Int {
        return generator.nextInt()
    }

    fun asyncSendMsg() {
        // 休息5秒，模拟异步任务
//        Thread {
//            try {
//                Thread.sleep(500)
//            } catch (e: InterruptedException) {
//                e.printStackTrace()
//            }
        //可以在子线程中直接发送广播
        sendContentBroadcast(LOCKED_SUCCESS, "lock")
//        }.start()
    }

    var isClick=true
    fun sendRfid() {
//        sendContentBroadcast(HFCard, "1698A858A115F60401010004880432E54BD9")
        Log.d(TAG,"sendRfid")
//        RfidManage.getInstance().linkDevice(true)
        if(isClick) {
            RfidManage.getInstance().linkDevice(true)
//            RfidManage.getInstance().startStop(true)
            isClick=!isClick
        }
    }

    fun sendExitMsg() {
        Thread {
            try {
                Thread.sleep(500)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            sendContentBroadcast(EXIT_APP_MSG, "")
        }.start()
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
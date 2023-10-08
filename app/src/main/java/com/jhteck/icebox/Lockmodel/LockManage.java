package com.jhteck.icebox.Lockmodel;

import android.util.Log;

import com.headleader.MrbBoard.ICallBack;
import com.headleader.MrbBoard.LockInfo;
import com.headleader.MrbBoard.MrbBoardHandler;
import com.jhteck.icebox.myinterface.MyCallback;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import ku.util.KuFunction;

/**
 * @author wade
 * @Description:(用一句话描述)
 * @date 2023/9/5 22:59
 */
public class LockManage {
    private String TAG = "LockManage";
    private static LockManage instance;
    private MyCallback<String> lockCallback;
    private MyCallback<String> versionCallback;
    private SendThread mSendThread;
    private int relay1 = 1;//继电器1
    private int relay2 = 2;//继电器2
    private int sideOpen = 1;//开锁
    private int sideRelesea = 0;//释放
    private int LockStatusOpen = 0;//开锁状态
    private int LockStatusClose = 1;//关锁状态
    private int sensorStatusClose = 0;//有物
    private int sensorStatusOpen = 1;//无物
    private int time = 0xFFFF;

    public void setLockCallback(MyCallback<String> lockCallback) {
        this.lockCallback = lockCallback;
    }

    public void setVersionCallback(MyCallback<String> versionCallback) {
        this.versionCallback = versionCallback;
    }

    private LockManage() {
        // 私有构造方法，防止外部实例化

    }

    public static synchronized LockManage getInstance() {
        if (instance == null) {
            instance = new LockManage();
        }
        return instance;
    }

    MySerial mySerial;

    public void initSerialByPort(String port) {
        initSerial();
        Log.d(TAG, "port=" + port);
        mySerial.port(port);
//        if (AppConstantsKt.NOT_HARD_DEVICE) return;//无硬件模式
        open();
        mSendThread = new SendThread();
        mSendThread.setSuspendFlag();
        mSendThread.start();
    }

    private void initSerial() {
        Log.d(TAG, "initSerial");
        if (Instances.serial == null) {
            Instances.serial = new MySerial();
            Instances.serial.baudrate(115200);
            mySerial = Instances.serial;
            mySerial.listener = new MySerial.Listener() {
                @Override
                public void onReceived(byte[] datas) {
//                    String strTemp = KuConvert.toHex(datas, " ");
//                    Instances.fragLog.AddLog(String.format("Serial Received: %s", strTemp));
//                    fragTest.handle(datas);
                    handler.handle(datas);
                }

                @Override
                public void onSent(byte[] datas) {
//                    String strTemp = KuConvert.toHex(datas, " ");
//                    Instances.fragLog.AddLog(String.format("Serial Sent: %s", strTemp));
                }
            };
            initHandler();
        }
        ArrayList<String> list = new ArrayList<>();
        try {
            for (String port : mySerial.list())
                list.add(port);
        } catch (Exception ex) {
//            Common.toast((KuFunction.getError(ex)));
        }
//        cboPort.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, list));

    }

    private boolean continueGetLockStatus = true;

    private void open() {
        try {
            mySerial.open();
        } catch (Exception ex) {
            continueGetLockStatus = false;
        }
    }

    public void close() {
        mySerial.close();
    }

    private Thread openLockThread = null;

    private int sendTime = 10;
    /**
     * 结算
     */
    private volatile boolean isCalculating = false;
    private Set<Integer> sensor1Status = new HashSet<>();
    private Set<Integer> sensor2Status = new HashSet<>();

    /**
     * 传感器是否变动
     *
     * @return
     */
    private boolean sensorChanged() {
        return sensor1Status.stream().count() == 2 || sensor2Status.stream().count() == 2;
    }

    private void stopAll() {
        Log.e(TAG, "stopAll: ");
        closeLock();
        updateCalculatingStatus(true);//进入结算页面
        clearThread();
        lockCallback.callback(1 + "");
    }

    /**
     * 尝试主动发启动时开锁
     */
    public void tryOpenLock() {
        sendTime = 10;
        if (openLockThread == null) {
            synchronized (this) {
                if (openLockThread == null) {
                    Log.i(TAG, "openLockThread:开启定时开锁模式");
                    openLockThread = new Thread(() -> {
                        while (sendTime > 0) {
                            try {
                                Log.i(TAG, "openLockThread: 定时开锁 +sendTime=" + sendTime);
                                if (sensorChanged() && lockInfo1.sensor == 0 && lockInfo2.sensor == 0) {
                                    Log.i(TAG, "openLockThread:tryOpenLock: 计入结算环节1");
                                    break;
                                }
                                if (sensorChanged() && isCalculating) {
                                    Log.i(TAG, "openLockThread:tryOpenLock: 计入结算环节");
                                    break;
                                }
//                                openLock();
                                sendTime--;
                                Thread.sleep(6000);
                            } catch (Exception e) {
                                Log.e(TAG, "openLockThread:定时开锁: " + e.getMessage());
                            }
                        }
                        openLockThread = null;
                        stopAll();
                        Log.i(TAG, "openLockThread:60秒内无操作，停止定时开锁");
                    });
                    openLockThread.start();
                }
            }
        }
    }

    public synchronized void updateCalculatingStatus(boolean flag) {
        isCalculating = flag;
    }

    public void preOpenLock() {
        updateCalculatingStatus(false);
        sensor1Status = new HashSet<>();
        sensor2Status = new HashSet<>();
    }

    public void openLock() {
        Log.d(TAG, "openLock sensor1=" + lockInfo1.sensor + "lockInfo2.sensor==" + lockInfo2.sensor
                + "lockInfo1.lock==0" + lockInfo1.lock + "lockInfo2.lock==" + lockInfo2.lock);
        //开锁
        try {
            Thread.sleep(100);
            SendData(handler.dataOfUnlock(relay1, time, sideOpen));
            Thread.sleep(100);
            SendData(handler.dataOfUnlock(relay2, time, sideOpen));
        } catch (Exception e) {

        }
        mSendThread.setResume();
    }

    public void getVersion() {
        SendData(handler.dataOfFirmwareVersion());
    }

    private class SendThread extends Thread {
        public boolean suspendFlag = true;// 控制线程的执行

        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                synchronized (this) {
                    while (suspendFlag) {
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                try {
                    Thread.sleep(1000);
                    getLockStutas();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        //线程暂停
        public void setSuspendFlag() {
            this.suspendFlag = true;
        }

        //唤醒线程
        public synchronized void setResume() {
            this.suspendFlag = false;
            notify();
        }
    }

    public void closeLock() {
        //关锁
        try {
            Thread.sleep(100);
            SendData(handler.dataOfUnlock(relay1, time, sideRelesea));
            Thread.sleep(100);
            SendData(handler.dataOfUnlock(relay2, time, sideRelesea));
        } catch (Exception e) {
            Log.e(TAG, "closeLock: " + e.getMessage());
        }
    }

    public void clearThread() {
        mSendThread.setSuspendFlag();
    }

    private LockInfo lockInfo1 = new LockInfo();
    private LockInfo lockInfo2 = new LockInfo();

    public void getLockStutas() {
        Log.d(TAG, "获取锁状态");
        try {
            Thread.sleep(100);
            SendData(handler.dataOfLockStatus(relay1));
            Thread.sleep(100);
            SendData(handler.dataOfLockStatus(relay2));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void SendData(byte[] datas) {
        try {
            Instances.serial.write(datas);
        } catch (Exception ex) {
            Log.e(TAG, KuFunction.getError(ex));
        }
    }

    public void handle(byte[] datas) {
        handler.handle(datas);
    }

    private MrbBoardHandler handler;

    private void initHandler() {
        handler = new MrbBoardHandler(new ICallBack() {
            @Override
            public void NormalHandle(String devID) {
                Log.e(TAG, "devID: " + devID);
                if (!devID.isEmpty())
                    SendData(handler.dataOfHeartBreak(devID));
            }

            @Override
            public void UnlockResult(boolean ret) {
                Log.e(TAG, ret ? "操作完成" : "操作失败");
            }

            @Override
            public void LockStatusResult(LockInfo info) {
                if (info.relay == 1) {
                    lockInfo1 = info;
                } else if (info.relay == 2) {
                    lockInfo2 = info;
                }
                Log.d(TAG, "lockInfo1 LockStatus=" + lockInfo1.lock + "--sensor=" + lockInfo1.sensor + "--relay=" + lockInfo1.relay);
                Log.d(TAG, "lockInfo2 LockStatus=" + lockInfo2.lock + "--sensor=" + lockInfo2.sensor + "--relay=" + lockInfo2.relay);

                sensor1Status.add(lockInfo1.sensor);
                sensor2Status.add(lockInfo2.sensor);
                if (lockInfo1.lock == 0 && lockInfo2.lock == 0 && sensorChanged() && lockInfo1.sensor == 0 && lockInfo2.sensor == 0) {
                    stopAll();
                } else if (sensorChanged() || lockInfo1.sensor == 1 || lockInfo2.sensor == 1) {
                    //假如有一个传感器有变化，重新几时
                    sendTime = 10;
                }
            }

            @Override
            public void UnlockExResult(boolean ret) {
                Log.e(TAG, ret ? "操作完成" : "操作失败");
            }

            @Override
            public void LockStatusExResult(LockInfo info) {
                Log.e(TAG, "LockStatusExResult");
            }

            @Override
            public void LockStatusExAllResult(int i) {
                Log.e(TAG, "LockStatusExAllResult=" + i);
            }

            @Override
            public void OnBoardUpdatedEx(int i) {
                Log.e(TAG, "OnBoardUpdatedEx=" + i);
            }

            @Override
            public void OnRelayExUpdated(int i) {
                Log.e(TAG, "OnRelayExUpdated=" + i);
            }

            @Override
            public void OnError(Exception e) {
                Log.e(TAG, "OnError" + e);
            }

            @Override
            public void SetIDResult(boolean ret) {
                Log.e(TAG, ret ? "操作完成" : "操作失败");
            }

            @Override
            public void FirmwareVersionResult(String ver) {
                Log.e(TAG, "FirmwareVersionResult" + ver);
                versionCallback.callback(ver);
            }

            @Override
            public void GetNetParamsResult() {
                Log.e(TAG, "GetNetParamsResult");
            }

            @Override
            public void SetNetParamsResult() {
                Log.e(TAG, "SetNetParamsResult");
            }
        });
    }

}

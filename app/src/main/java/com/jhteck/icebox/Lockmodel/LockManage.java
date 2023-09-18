package com.jhteck.icebox.Lockmodel;

import android.util.Log;

import com.headleader.MrbBoard.ICallBack;
import com.headleader.MrbBoard.LockInfo;
import com.headleader.MrbBoard.MrbBoardHandler;
import com.jhteck.icebox.myinterface.MyCallback;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import ku.util.KuConvert;
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
                    String strTemp = KuConvert.toHex(datas, " ");
//                    Instances.fragLog.AddLog(String.format("Serial Received: %s", strTemp));
//                    fragTest.handle(datas);
                    handler.handle(datas);
//                    Log.e(TAG,strTemp);
                }

                @Override
                public void onSent(byte[] datas) {
                    String strTemp = KuConvert.toHex(datas, " ");
//                    Instances.fragLog.AddLog(String.format("Serial Sent: %s", strTemp));
                    Log.e(TAG, strTemp);
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

    /**
     * 尝试主动发启动时开锁
     */
    public void tryOpenLock() {
        sendTime = 10;
        if (openLockThread == null) {
            synchronized (this) {
                if (openLockThread == null) {
                    Log.i(TAG, "开启定时开锁模式");
                    openLockThread = new Thread(() -> {
                        while (sendTime > 0) {
                            try {
                                Log.i(TAG, "openLockThread: 定时开锁");

                                if (sensorChanged() && isCalculating) {
                                    Log.i(TAG, "tryOpenLock: 计入结算环节");
                                    break;
                                }
                                openLock();
                                sendTime--;
                                Thread.sleep(6000);
                            } catch (Exception e) {
                                Log.e(TAG, "定时开锁: " + e.getMessage());
                            }
                        }
                        openLockThread = null;
                        Log.i(TAG, "60秒内无操作，停止定时开锁");
                    });
                    openLockThread.start();
                }
            }
        }
    }

    public synchronized void updateCalculatingStatus(boolean flag) {
        isCalculating = flag;
    }
    
    public  void preOpenLock() {
        updateCalculatingStatus(false);
        sensor1Status = new HashSet<>();
        sensor2Status = new HashSet<>();
    }

    public void openLock() {
        //开锁
        int relay = Integer.parseInt("1");
        int relay2 = Integer.parseInt("2");
        int time = 0xFFFF;
        try {
            SendData(handler.dataOfUnlock(relay, time, 1));
            Thread.sleep(600);
            SendData(handler.dataOfUnlock(relay2, time, 1));
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
                    Thread.sleep(2000);
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
        int relay = Integer.parseInt("1");
        int relay2 = Integer.parseInt("2");
        int time = 0xFFFF;
//        SendData(handler.dataOfUnlock(relay, time, 0));
//        SendData(handler.dataOfUnlock(relay2, time, 0));

        try {
            SendData(handler.dataOfUnlock(relay, time, 0));
            Thread.sleep(600);
            SendData(handler.dataOfUnlock(relay2, time, 0));
        } catch (Exception e) {
            Log.e(TAG, "closeLock: " + e.getMessage());
        }
        clearThread();

    }

    public void clearThread() {
        mSendThread.setSuspendFlag();
    }

    private boolean isRelay1 = true;
    private boolean isRelay2 = true;
    private LockInfo lockInfo1 = new LockInfo();
    private LockInfo lockInfo2 = new LockInfo();

    public void getLockStutas() {
        int relay = Integer.parseInt("1");
        int relay2 = Integer.parseInt("2");
        Log.d(TAG, "获取锁状态");
        try {
            isRelay1 = true;
            isRelay2 = false;
            lockInfo1 = new LockInfo();
            SendData(handler.dataOfLockStatus(relay));
            Thread.sleep(600);
            isRelay1 = false;
            isRelay2 = true;
            lockInfo2 = new LockInfo();
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
            public void ErrorHandle(Exception ex) {
                Log.e(TAG, KuFunction.getError(ex));
            }

            @Override
            public void UnlockResult(boolean ret) {
                Log.e(TAG, ret ? "操作完成" : "操作失败");
            }

            @Override
            public void LockStatusResult(LockInfo info) {
                   /* String strTemp = String.format(getString(R.string.lockstatetemplate),
                            info.lock, info.sensor);
                    runOnUiThread(() -> txtLockStatus.setText(strTemp));*/

                if (isRelay1) {
                    lockInfo1 = info;
                } else if (isRelay2) {
                    lockInfo2 = info;
                }
                Log.e(TAG, "LockStatus=" + info.lock + "--sensor=" + info.sensor);
                Log.e(TAG, "lockInfo1 LockStatus=" + lockInfo1.lock + "--sensor=" + lockInfo1.sensor);
                Log.e(TAG, "lockInfo2 LockStatus=" + lockInfo2.lock + "--sensor=" + lockInfo2.sensor);

                sensor1Status.add(lockInfo1.sensor);
                sensor2Status.add(lockInfo2.sensor);
                if (lockInfo1.lock == 1 && lockInfo2.lock == 1 && sensorChanged() && lockInfo1.sensor == 0 && lockInfo2.sensor == 0) {
                    clearThread();
                    updateCalculatingStatus(true);//进入结算页面
                    lockCallback.callback(info.lock + "");
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
            public void SetIDResult(boolean ret) {
                Log.e(TAG, ret ? "操作完成" : "操作失败");
            }

            @Override
            public void FirmwareVersionResult(String ver) {
                Log.e(TAG, "FirmwareVersionResult" + ver);
                versionCallback.callback(ver);
            }
        });
    }

}

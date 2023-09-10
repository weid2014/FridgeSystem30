package com.jhteck.icebox.Lockmodel;

import android.icu.text.SimpleDateFormat;
import android.util.Log;

import com.headleader.MrbBoard.ICallBack;
import com.headleader.MrbBoard.LockInfo;
import com.headleader.MrbBoard.MrbBoardHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import android_serialport_api.ComBean;
import android_serialport_api.MyFunc;
import android_serialport_api.SerialHelper;
import android_serialport_api.SerialPortFinder;
import ku.util.KuConvert;
import ku.util.KuFunction;

/**
 * @author wade
 * @Description:(用一句话描述)
 * @date 2023/9/5 22:59
 */
public class LockManage {
    private String TAG="LockManage";
    private static LockManage instance;

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

    public void initSerialByPort(String port){
        initSerial();
        mySerial.port(port);
        open();
    }

    private void initSerial() {
        Log.d(TAG,"initSerial");
        if (Instances.serial == null){
            Instances.serial = new MySerial();
            Instances.serial.baudrate(115200);
            mySerial = Instances.serial;
            mySerial.listener = new MySerial.Listener() {
                @Override
                public void onReceived( byte[] datas ) {
                    String strTemp = KuConvert.toHex(datas, " ");
//                    Instances.fragLog.AddLog(String.format("Serial Received: %s", strTemp));
//                    fragTest.handle(datas);
//                    Log.e(TAG,strTemp);
                }

                @Override
                public void onSent( byte[] datas ) {
                    String strTemp = KuConvert.toHex(datas, " ");
//                    Instances.fragLog.AddLog(String.format("Serial Sent: %s", strTemp));
                    Log.e(TAG,strTemp);
                }
            };
            initHandler();

        }
        ArrayList<String> list = new ArrayList<>();
        try {
            for (String port: mySerial.list())
                list.add(port);
        } catch (Exception ex) {
//            Common.toast((KuFunction.getError(ex)));
        }
//        cboPort.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, list));
    }

    private void open() {
        try {
            mySerial.open();
//            fragTest.enable( true);
//            btnOpen.setText(Common.getString(R.string.close));
        } catch (Exception ex) {
//            Common.toast(KuFunction.getError(ex));
        }
    }
    private void close() {
        mySerial.close();
//        fragTest.enable(false);
//        btnOpen.setText(Common.getString(R.string.open));
    }

    public void openLock(){
        //开锁
        int relay = Integer.parseInt("1");
//        int time = 0xFFFF;
        int time = 9999999*1000;
//        if (cboAutoRelease.getSelectedItemPosition() > 0)
//            time =(int) (Float.parseFloat(cboAutoRelease.getSelectedItem().toString()) * 100);
        SendData(handler.dataOfUnlock(relay,  time, 1));
    }

    public void closeLock(){
        //关锁
        int relay = Integer.parseInt("1");
        int time = 0xFFFF;
//        if (cboAutoRelease.getSelectedItemPosition() > 0)
//            time =(int) (Float.parseFloat(cboAutoRelease.getSelectedItem().toString()) * 100);
        SendData(handler.dataOfUnlock(relay,  time, 0));
    }

    public void getLockStutas(){
        int relay = Integer.parseInt("1");
        SendData(handler.dataOfLockStatus(relay));
    }

    private void SendData( byte[] datas ) {
        try{
//            if (mode == 0){
                Instances.serial.write(datas);
//            } else if (mode == 1){
//                Instances.socket.write(datas);
//            } else if (mode == 2){
//                Instances.usb.write(datas);
//            }
        } catch (Exception ex){
//            Common.toast(KuFunction.getError(ex));
            Log.e(TAG,KuFunction.getError(ex));
        }
    }
    public void handle(byte[] datas)     {
        handler.handle(datas);
    }

    private MrbBoardHandler handler;

    private void initHandler(){
        handler = new MrbBoardHandler(new ICallBack() {
            @Override
            public void NormalHandle( String devID ) {
                if (!devID.isEmpty())
                    SendData(handler.dataOfHeartBreak(devID));
            }
            @Override
            public void ErrorHandle( Exception ex ) {
                Log.e(TAG,KuFunction.getError(ex));
            }
            @Override
            public void UnlockResult( boolean ret ) {
                Log.e(TAG,ret ? "操作完成" : "操作失败");
            }
            @Override
            public void LockStatusResult(LockInfo info) {
                   /* String strTemp = String.format(getString(R.string.lockstatetemplate),
                            info.lock, info.sensor);
                    runOnUiThread(() -> txtLockStatus.setText(strTemp));*/
                Log.e(TAG,"LockStatusResult");
            }

            @Override
            public void UnlockExResult(boolean ret) {
                Log.e(TAG,ret ? "操作完成" : "操作失败");
            }

            @Override
            public void LockStatusExResult(LockInfo info) {
                    /*String strTemp = String.format(getString(R.string.lockstatetemplate),
                            info.lock, info.sensor);
                    runOnUiThread(() -> txtLockStatusEx.setText(strTemp));*/
                Log.e(TAG,"LockStatusExResult");
            }

            @Override
            public void SetIDResult( boolean ret ) {
                Log.e(TAG,ret ? "操作完成" : "操作失败");
            }
            @Override
            public void FirmwareVersionResult( String ver ) {
//                    runOnUiThread(() -> txtFirmwareVersion.setText(ver));
                Log.e(TAG,"FirmwareVersionResult");
            }
        });
    }

}

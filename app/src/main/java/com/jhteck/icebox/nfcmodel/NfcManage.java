package com.jhteck.icebox.nfcmodel;

import android.icu.text.SimpleDateFormat;
import android.util.Log;

import com.jhteck.icebox.api.AppConstantsKt;
import com.jhteck.icebox.myinterface.MyCallback;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import android_serialport_api.ComBean;
import android_serialport_api.MyFunc;
import android_serialport_api.SerialHelper;
import android_serialport_api.SerialPortFinder;

/**
 * @author wade
 * @Description:(用一句话描述)
 * @date 2023/9/5 22:59
 */
public class NfcManage {
    private String TAG="NfcManage";
    private static NfcManage instance;
    private MyCallback<String> stringMyCallback;

    public void setStringMyCallback(MyCallback<String> stringMyCallback) {
        this.stringMyCallback = stringMyCallback;
    }

    private NfcManage() {
        // 私有构造方法，防止外部实例化

    }

    public static synchronized NfcManage getInstance() {
        if (instance == null) {
            instance = new NfcManage();
        }
        return instance;
    }

    SerialControl serialCom;//串口
    DispQueueThread DispQueue;//刷新显示线程
    SimpleDateFormat m_sdfDate = new SimpleDateFormat("HH:mm:ss ");
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"); // 国际化标志时间格式类

    SerialPortFinder mSerialPortFinder;//串口设备搜索

    private byte[] gcardnum = new byte[4];  //防冲突的时候缓存卡片uid

    private byte[] recvdata = new byte[1500];   //发送接收缓存
    private int recvlen=0;

    private String PASSWORD_CRYPT_KEY = new String("1234567812345679");
    private String PASSWORD_IV = new String("00000000");
    private String str = new String("asas534534534");

    static private int openfileDialogId = 0;
    static private String openfilepath;

    static public int currentbar=0;

    //------------------------------------------显示消息
    private void ShowMessage(String sMsg) {
        StringBuilder sbMsg = new StringBuilder();
        /*sbMsg.append(editTextRecDisp.getText());
//        sbMsg.append(m_sdfDate.format(new Date()));
        sbMsg.append(sMsg);
        sbMsg.append("\r\n");
        editTextRecDisp.setText(sbMsg);
        editTextRecDisp.setSelection(sbMsg.length(), sbMsg.length());*/
        Log.d(TAG,sMsg);
    }

    //----------------------------------------------------串口控制类
    private class SerialControl extends SerialHelper {
        public SerialControl() {
        }

        @Override
        protected void onDataReceived(final ComBean ComRecData) {
            DispQueue.AddQueue(ComRecData);// 线程定时刷新显示(推荐)
        }
    }

    //----------------------------------------------------关闭串口
    private void CloseComPort(SerialHelper ComPort) {
        if (ComPort != null) {
            ComPort.stopSend();
            ComPort.close();
        }
    }

    public void startNfcPort(){

        DispQueue = new DispQueueThread();
        DispQueue.start();
        serialCom = new SerialControl();
        serialCom.setPort("/dev/ttyS0");
        serialCom.setBaudRate(Integer.parseInt(
                "9600"));
        Log.d(TAG,serialCom.getPort());
        Log.d(TAG,serialCom.getBaudRate()+"");
//        OpenComPort(serialCom);
        if (!AppConstantsKt.NOT_HARD_DEVICE)return;//无硬件模式
        OpenComPort(serialCom);
    }

    //----------------------------------------------------打开串口
    public void OpenComPort(SerialHelper ComPort) {
        try {
            ComPort.open();
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    //----------------------------------------------------刷新显示线程
    private class DispQueueThread extends Thread {
        private Queue<ComBean> QueueList = new LinkedList<ComBean>();
        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                final ComBean ComData;
                while ((ComData = QueueList.poll()) != null) {

                    /*runOnUiThread(new Runnable() {
                        public void run() {
                            DispRecData(ComData);
                        }
                    });*/
                    Log.d(TAG,DispRecData(ComData));

                    try {
                        Thread.sleep(100);// 显示性能高的话，可以把此数值调小。
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
        public synchronized void AddQueue(ComBean ComData) {
            QueueList.add(ComData);
        }
    }

    //----------------------------------------------------显示接收数据
    private String DispRecData(ComBean ComRecData) {
        StringBuilder sMsg = new StringBuilder();
        byte[] temp = new byte[20];
        long cardint=0;
        long wg26_1=0;
        long wg26_2=0;
        long wg34_1=0;
        long wg34_2=0;
        try {
            sMsg.append("recv: "+ MyFunc.ByteArrToHex(ComRecData.bRec));
            ShowMessage(sMsg.toString());

            if(solveRecv(ComRecData.bRec, temp)==0){    //主动刷卡的数据处理
                int len = temp[0];
                byte[] cardnum = new byte[4];
                System.arraycopy( temp,1, cardnum, 0, 4);   //只保留前面4个字节卡号

                sMsg.append("\n原始卡号："+MyFunc.ByteArrToHex(cardnum)+"\n");
                stringMyCallback.callback(MyFunc.ByteArrToHex(cardnum).replace(" ",""));
                cardint = Long.parseLong(MyFunc.ByteArrToHex(cardnum, 0, 4), 16);
                sMsg.append("正码转十进制："+String.format("%010d", cardint)+"\n");

                wg26_1 = Long.parseLong(MyFunc.ByteArrToHex(cardnum,1,1), 16);
                wg26_2 = Long.parseLong(MyFunc.ByteArrToHex(cardnum,2,2), 16);
                sMsg.append("正码转韦根26："+String.format("%03d,%05d", wg26_1, wg26_2)+"\n");

                wg34_1 = Long.parseLong(MyFunc.ByteArrToHex(cardnum,0,2), 16);
                wg34_2 = Long.parseLong(MyFunc.ByteArrToHex(cardnum,2,2), 16);
                sMsg.append("正码转韦根34："+String.format("%05d,%05d", wg34_1, wg34_2)+"\n");

                MyFunc.reverseByte(cardnum);
                cardint = Long.parseLong(MyFunc.ByteArrToHex(cardnum, 0, 4), 16);
                sMsg.append("反码转十进制："+String.format("%010d", cardint)+"\n");

                wg26_1 = Long.parseLong(MyFunc.ByteArrToHex(cardnum,1,1), 16);
                wg26_2 = Long.parseLong(MyFunc.ByteArrToHex(cardnum,2,2), 16);
                sMsg.append("反码转韦根26："+String.format("%03d,%05d", wg26_1, wg26_2)+"\n");

                wg34_1 = Long.parseLong(MyFunc.ByteArrToHex(cardnum,0,2), 16);
                wg34_2 = Long.parseLong(MyFunc.ByteArrToHex(cardnum,2,2), 16);
                sMsg.append("反码转韦根34："+String.format("%05d,%05d", wg34_1, wg34_2)+"\n");


//                runOnUiThread(new Runnable() {
//                    public void run() {
                        String data = getMifareSertorData();

                        //立刻切换回读卡
                        serialCom.sendSocket((byte)0x00, (byte)0x2E, (short) 0, recvdata, recvdata, 100);

                        sMsg.append(data);
                        ShowMessage(sMsg.toString());
//                    }
//                });
                return sMsg.toString();
            }

        } catch (Exception ex) {
            Log.d(TAG,ex.getMessage());
            return ex.getMessage();
        }
        return "abc";
    }

    //识别主动刷卡数据
    private int solveRecv(byte[] bRec, byte[] retRec) {
        int sta = -1;

        if((byte)bRec[0] == 0x02){

            byte len = bRec[1];
            if(len <= bRec.length){

                byte result = MyFunc.bccCalc(bRec, 1, len-3);
                if((byte)bRec[len-2] == (byte)result){
                    retRec[0] = (byte) (len-5);
                    System.arraycopy( bRec,3,retRec, 1,len-5);
                    sta = 0;
                }
            }
        }

        return sta;
    }

    //读Mifare扇区
    private String getMifareSertorData(){
        StringBuilder sMsg = new StringBuilder();
        byte[] rdata = new byte[500];
        byte[] sdata = new byte[500];
        int sendlen=0;
        byte[] snr = new byte[4];
//
//        //寻卡
//        sendlen = 0;
//        sdata[sendlen] = (byte)0x52;  sendlen++;
//        recvlen = serialCom.sendSocket((byte)0x00, (byte)0x40, (short) sendlen, sdata, rdata, 100);
//        if(recvlen<0)
//            return "";
//
//        //反冲突
//        sendlen = 0;
//        sdata[sendlen] = (byte)0x93;  sendlen++;
//        recvlen = serialCom.sendSocket((byte)0x00, (byte)0x41, (short) sendlen, sdata, rdata, 100);
//        if(recvlen<0)
//            return "";
//
//        System.arraycopy( rdata, 0 , snr, 0, 4);
//
//        //选卡
//        sendlen = 0;
//        sdata[sendlen] = (byte)0x93;  sendlen++;
//        System.arraycopy( snr, 0 , sdata, sendlen, 4);    sendlen += 4;
//        recvlen = serialCom.sendSocket((byte)0x00, (byte)0x42, (short) sendlen, sdata, rdata, 100);
//        if(recvlen<0)
//            return "";

//        //寻卡+防冲突+选卡
//        sendlen = 0;
//        sdata[sendlen] = (byte)0x52;  sendlen++;
//        recvlen = serialCom.sendSocket((byte)0x00, (byte)0x43, (short) sendlen, sdata, rdata, 200);
//        if(recvlen<0)
//            return "";
//
//        System.arraycopy( rdata, 4 , snr, 0, 4);
//        Log.d(TAG, "snr:"+MyFunc.ByteArrToHex(snr, 0, 4));
//
//        //验证卡片密码
//        sendlen = 0;
//        sdata[sendlen] = (byte)0x60;   sendlen++;//keyA
//        sdata[sendlen] = (byte)0x00;   sendlen++;//block0
//        sdata[sendlen] = (byte)0xFF;   sendlen++;//key
//        sdata[sendlen] = (byte)0xFF;   sendlen++;
//        sdata[sendlen] = (byte)0xFF;   sendlen++;
//        sdata[sendlen] = (byte)0xFF;   sendlen++;
//        sdata[sendlen] = (byte)0xFF;   sendlen++;
//        sdata[sendlen] = (byte)0xFF;   sendlen++;
//        System.arraycopy( snr, 0 , sdata, sendlen, 4);    sendlen += 4;
//        recvlen = serialCom.sendSocket((byte)0x00, (byte)0x50, (short) sendlen, sdata, rdata, 300);
//        if(recvlen<0)
//            return "";
//
//        //读块
//        sendlen = 0;
//        sdata[sendlen] = (byte)0x00;   sendlen++;//block0
//        recvlen = serialCom.sendSocket((byte)0x00, (byte)0x51, (short) sendlen, sdata, rdata, 100);
//        if(recvlen<0)
//            return "";
//        sMsg.append("block0:"+MyFunc.ByteArrToHex(rdata, 0, recvlen)+"\n");
//        Log.d(TAG, "block0:"+MyFunc.ByteArrToHex(rdata, 0, recvlen));
//
//        //读块
//        sendlen = 0;
//        sdata[sendlen] = (byte)0x01;   sendlen++;//block1
//        recvlen = serialCom.sendSocket((byte)0x00, (byte)0x51, (short) sendlen, sdata, rdata, 100);
//        if(recvlen<0)
//            return "";
//        sMsg.append("block1:"+MyFunc.ByteArrToHex(rdata, 0, recvlen)+"\n");
//        Log.d(TAG, "block1:"+MyFunc.ByteArrToHex(rdata, 0, recvlen));
//
//        //读块
//        sendlen = 0;
//        sdata[sendlen] = (byte)0x02;   sendlen++;//block2
//        recvlen = serialCom.sendSocket((byte)0x00, (byte)0x51, (short) sendlen, sdata, rdata, 100);
//        if(recvlen<0)
//            return "";
//        sMsg.append("block2:"+MyFunc.ByteArrToHex(rdata, 0, recvlen)+"\n");
//        Log.d(TAG, "block2:"+MyFunc.ByteArrToHex(rdata, 0, recvlen));
//
//        //读块
//        sendlen = 0;
//        sdata[sendlen] = (byte)0x03;   sendlen++;//block3
//        recvlen = serialCom.sendSocket((byte)0x00, (byte)0x51, (short) sendlen, sdata, rdata, 100);
//        if(recvlen<0)
//            return "";
//        sMsg.append( "block3:"+MyFunc.ByteArrToHex(rdata, 0, recvlen)+"\n");
//        Log.d(TAG, "block3:"+MyFunc.ByteArrToHex(rdata, 0, recvlen));

        //寻卡+反冲突+选卡+验证密码+读块
        sendlen = 0;
        sdata[sendlen] = (byte)0x60;   sendlen++;//keyA
        sdata[sendlen] = (byte)0x00;   sendlen++;//从0块开始读
        sdata[sendlen] = (byte)0x03;   sendlen++;//一共读3块
        sdata[sendlen] = (byte)0xFF;   sendlen++;//key
        sdata[sendlen] = (byte)0xFF;   sendlen++;
        sdata[sendlen] = (byte)0xFF;   sendlen++;
        sdata[sendlen] = (byte)0xFF;   sendlen++;
        sdata[sendlen] = (byte)0xFF;   sendlen++;
        sdata[sendlen] = (byte)0xFF;   sendlen++;
        recvlen = serialCom.sendSocket((byte)0x00, (byte)0x54, (short) sendlen, sdata, rdata, 500);
        Log.d(TAG, ""+recvlen);
        if(recvlen<=0)
            return "";
        sMsg.append( "block0:"+MyFunc.ByteArrToHex(rdata, 0, 16)+"\n");
        sMsg.append( "block1:"+MyFunc.ByteArrToHex(rdata, 16, 16)+"\n");
        sMsg.append( "block2:"+MyFunc.ByteArrToHex(rdata, 32, 16)+"\n");

        Log.d(TAG, MyFunc.ByteArrToHex(rdata, 0, recvlen));
        return sMsg.toString();
    }
}

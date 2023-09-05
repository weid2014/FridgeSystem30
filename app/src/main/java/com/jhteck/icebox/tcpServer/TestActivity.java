package com.jhteck.icebox.tcpServer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.work.tcp.api.PublicAPI;
import com.work.tcp.tcp.client.TcpConnConfig;
import com.work.tcp.tcp.client.XTcpClient;
import com.work.tcp.tcp.client.bean.TcpMsg;
import com.work.tcp.tcp.server.TcpServerConfig;
import com.work.tcp.tcp.server.XTcpServer;
import com.work.tcp.tcp.server.listener.TcpServerListener;
import com.work.tcp.utils.Data_syn;
import com.work.tcp.utils.StringValidationUtils;
import com.work.tcp.utils.TCPReceiveEnum;
import com.work.tcp.utils.XSocketLog;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wade
 * @Description:(官方demo，參考用)
 * @date 2023/6/28 17:44
 */
public class TestActivity extends AppCompatActivity implements TcpServerListener {

    private XTcpServer mXTcpServer;
    //获取设备客户端信息
    private XTcpClient mXTcpClient;
    List<XTcpClient> clientList = new ArrayList<>();
    public static String cabinetNum = "";//柜号
    public static String devId = "";//序列号
    public static String sendHead = "";
    public static String ss = "";
    public static String receiveHead = "";
    private String TAG="wade";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        XSocketLog.debug(true);
        //打开应用启动服务器
        if (mXTcpServer != null && mXTcpServer.isListening()) {
            mXTcpServer.stopServer();
            Log.e(TAG, " mXTcpServer.stopServer()");
        } else {
            //获取启动服务的端口号
            String port = "5460";
            if (StringValidationUtils.validateRegex(port, StringValidationUtils.RegexPort)) {
                if (mXTcpServer == null) {
                    //根据端口号创建服务器
                    mXTcpServer = XTcpServer.getTcpServer(Integer.parseInt(port));
                    mXTcpServer.addTcpServerListener(this);
                    mXTcpServer.config(new TcpServerConfig.Builder()
                            .setTcpConnConfig(new TcpConnConfig.Builder().create()).create());
                }
                mXTcpServer.startServer();
                Log.e(TAG, " mXTcpServer.startServer()");
            }
        }

    }

    private void start(Class activityClass) {
        //关闭已打开的界面（关闭出现在闪屏效果，去除关闭）
        startActivity(new Intent(this, activityClass));
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

    }

    //tcp server
    //创建连接
    public void onCreated(XTcpServer server) {
        Log.e(TAG, server + " created");
    }

    //监听
    public void onListened(XTcpServer server) {
        Log.e(TAG, server + " onListened");
    }

    //客户端请求
    public void onAccept(XTcpServer server, XTcpClient tcpClient) {
        Log.e(TAG, " 收到客户端连接请求" + tcpClient.getTargetInfo().getIp());
    }

    //发送消息
    public void onSended(XTcpServer server, XTcpClient tcpClient, TcpMsg tcpMsg) {
        Log.e(TAG, " 信息发送" + tcpClient + tcpMsg);
    }

    public void onReceive(XTcpServer server, XTcpClient tcpClient, TcpMsg tcpMsg) {
        Log.e(TAG, " 收到客户端" + tcpClient.getTargetInfo().getIp() + "的数据:" + Data_syn.Bytes2HexString(tcpMsg.getSourceDataBytes()));
        mXTcpClient = tcpClient;
        if (tcpMsg.getSourceDataBytes() != null) {
            //真实
            String s = Data_syn.Bytes2HexString(tcpMsg.getSourceDataBytes());
            Log.v(TAG, s);
            //如果长度小于头部，分包进行拼接
            if (s.length() < 14) {
                ss = ss + s;
            } else {
                if (!s.substring(0, 4).equalsIgnoreCase(TCPReceiveEnum.Receive_Magic.getReceive())) {
                    ss = ss + s;
                } else {
                    ss = s;
                }
            }
            //处理粘包
            int cursor = 0, subStrLen = TCPReceiveEnum.Receive_Magic.getReceive().length(), totalStrLen = ss.length();

            int count = 0; // 表示重复字符的个数

            while ((cursor + subStrLen) <= totalStrLen) {
                String tempStr = ss.substring(cursor, cursor + subStrLen); // 获取字符串中的子字符串

                if (tempStr.equals(TCPReceiveEnum.Receive_Magic.getReceive())) {
                    count++;

                    cursor += subStrLen;

                } else {
                    cursor++;
                }
            }
            if (count > 0) {
                //数据处理 count大于1说明有粘包数据
                String[] split = ss.split(TCPReceiveEnum.Receive_Magic.getReceive());
                for (int j = 0; j < split.length; j++) {
                    ss = TCPReceiveEnum.Receive_Magic.getReceive() + split[j];
                    if (j > 0) {
                        //设置设备柜号
                        cabinetNum = PublicAPI.GetCabinet(ss);
                        //设置设备序列号
                        devId = PublicAPI.GetSerialNum(ss);
                        sendHead = PublicAPI.GetSendHead(cabinetNum, devId);
                        receiveHead = PublicAPI.GetreceiveHead(cabinetNum, devId);
                        if (PublicAPI.CRC(ss)) {
                            //解析
                            if (PublicAPI.ReportHeart(ss, receiveHead)) {
                                mXTcpServer.sendMsg(Data_syn.hexStr2Bytes(PublicAPI.ResponsetHeart(sendHead)), tcpClient);
                                //心跳连接持续中
                                Log.v(TAG, cabinetNum + devId);
                            }

                        }
                    }
                }
            }
            //判断客户端连接是否存在，不存在则加入客户端列表  柜号不能为空
            if (!clientList.contains(mXTcpClient)) {
                clientList.add(mXTcpClient);
            }
        }
    }

    @Override
    public void onValidationFail(XTcpServer xTcpServer, XTcpClient xTcpClient, TcpMsg tcpMsg) {

    }

    //客户端连接断开
    public void onClientClosed(XTcpServer server, XTcpClient tcpClient, String msg, Exception e) {
        clientList.remove(tcpClient);
        Log.e(TAG, " 客户端连接断开" + tcpClient + msg + e);
    }

    //服务器端关闭
    public void onServerClosed(XTcpServer server, String msg, Exception e) {
        clientList.clear();
        Log.e(TAG, " tcpserver 关闭" + msg + e);
    }


    private void openLock(){
        String openLocks=PublicAPI.OpenLock(sendHead);
        mXTcpServer.sendMsg(Data_syn.hexStr2Bytes(openLocks),mXTcpClient);
        Log.e(TAG,"发送开锁指令:"+openLocks);
    }
}

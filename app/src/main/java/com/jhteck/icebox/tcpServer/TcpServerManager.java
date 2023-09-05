package com.jhteck.icebox.tcpServer;

import android.util.Log;

import com.work.tcp.tcp.client.TcpConnConfig;
import com.work.tcp.tcp.server.TcpServerConfig;
import com.work.tcp.tcp.server.XTcpServer;
import com.work.tcp.tcp.server.listener.TcpServerListener;
import com.work.tcp.utils.StringValidationUtils;

/**
 * @author wade
 * @Description:(tcpServer管理类)
 * @date 2023/6/28 17:12
 */
public class TcpServerManager {
    private XTcpServer mXTcpServer;

    public TcpServerManager(TcpServerListener listener){
        //获取启动服务的端口号
        String port = "5460";
        if (StringValidationUtils.validateRegex(port, StringValidationUtils.RegexPort)) {
            if (mXTcpServer == null) {
                //根据端口号创建服务器
                mXTcpServer = XTcpServer.getTcpServer(Integer.parseInt(port));
                mXTcpServer.addTcpServerListener(listener);
                mXTcpServer.config(new TcpServerConfig.Builder()
                        .setTcpConnConfig(new TcpConnConfig.Builder().create()).create());
            }
            mXTcpServer.startServer();
            Log.e("lalala", " mXTcpServer.startServer()");
        }
    }

    public void startTcpServer(){

    }
}

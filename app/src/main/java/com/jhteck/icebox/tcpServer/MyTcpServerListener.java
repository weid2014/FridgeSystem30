package com.jhteck.icebox.tcpServer;

import android.util.Log;

import com.google.gson.Gson;
import com.jhteck.icebox.api.AntPowerDao;
import com.jhteck.icebox.api.AppConstantsKt;
import com.jhteck.icebox.bean.MyTcpMsg;
import com.work.tcp.api.InventoryAPI;
import com.work.tcp.api.LockAPI;
import com.work.tcp.api.PublicAPI;
import com.work.tcp.api.RFIDConfigAPI;
import com.work.tcp.tcp.client.TcpConnConfig;
import com.work.tcp.tcp.client.XTcpClient;
import com.work.tcp.tcp.client.bean.TcpMsg;
import com.work.tcp.tcp.server.TcpServerConfig;
import com.work.tcp.tcp.server.XTcpServer;
import com.work.tcp.tcp.server.listener.TcpServerListener;
import com.work.tcp.utils.Data_syn;
import com.work.tcp.utils.StringValidationUtils;
import com.work.tcp.utils.TCPReceiveEnum;
import com.work.tcp.utils.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author wade
 * @Description:(实现TcpServerListener接口)
 * @date 2023/6/26 22:54
 */
public class MyTcpServerListener implements TcpServerListener {

    private XTcpServer mXTcpServer;
    //获取设备客户端信息
    private XTcpClient mXTcpClient;
    List<XTcpClient> clientList = new ArrayList<>();
    public static String cabinetNum = "";//柜号
    public static String devId = "";//序列号
    public static String sendHead = "";
    public static String ss = "";
    public static String receiveHead = "";
    private String TAG = "tcpserver";
    private OnResultListener onResultListener;
    private OnInventoryResult onInventoryResult;
    private Set<String> epcSet = new HashSet<>();

    public interface OnResultListener {
        void onResult(MyTcpMsg myTcpMsg);
    }

    public interface OnInventoryResult {
        void onInventoryResult(Set<String> epcSet);
    }


    public void setOnInventoryResult(OnInventoryResult onInventoryResult) {
        this.onInventoryResult = onInventoryResult;
    }

    public void setOnResultListener(OnResultListener resultListener) {
        this.onResultListener = resultListener;
    }

    public OnResultListener getResultListener() {
        return onResultListener;
    }

    private MyTcpServerListener() {
    }

    private static MyTcpServerListener myTcpServerListener = new MyTcpServerListener();

    public static MyTcpServerListener getInstance() {
        return myTcpServerListener;
    }


    public void startServer() {
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
                    mXTcpServer.addTcpServerListener(myTcpServerListener);
                    mXTcpServer.config(new TcpServerConfig.Builder()
                            .setTcpConnConfig(new TcpConnConfig.Builder().create()).create());
                }
                mXTcpServer.startServer();
                Log.i(TAG, " mXTcpServer.startServer()");
            }
        }
    }

    //创建连接
    @Override
    public void onCreated(XTcpServer xTcpServer) {
        String result = xTcpServer + " 创建连接";
        Log.i(TAG, result);
//        onResultListener.onResult(new TcpMsg());
    }

    //监听
    @Override
    public void onListened(XTcpServer xTcpServer) {
        String result = xTcpServer + " 监听";
        Log.i(TAG, result);
//        onResultListener.onResult(result);
    }

    //客户端请求
    @Override
    public void onAccept(XTcpServer xTcpServer, XTcpClient xTcpClient) {
        String result = " 收到客户端连接请求" + xTcpClient.getTargetInfo().getIp();
        Log.d(TAG, result);
//        onResultListener.onResult(result);
    }

    //发送消息
    @Override
    public void onSended(XTcpServer xTcpServer, XTcpClient xTcpClient, TcpMsg tcpMsg) {
        String result = " 信息发送" + xTcpClient + tcpMsg;
        Log.i(TAG, result);
//        onResultListener.onResult(result);
    }

    @Override
    public void onReceive(XTcpServer xTcpServer, XTcpClient xTcpClient, TcpMsg tcpMsg) throws JSONException {

        String result = " 收到客户端" + xTcpClient.getTargetInfo().getIp() + "的数据:" + Data_syn.Bytes2HexString(tcpMsg.getSourceDataBytes());
//        Log.d(TAG, result);

        mXTcpClient = xTcpClient;
        if (tcpMsg.getSourceDataBytes() != null) {
            //真实
            String s = Data_syn.Bytes2HexString(tcpMsg.getSourceDataBytes());
//            Log.v(TAG, s);
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
                                mXTcpServer.sendMsg(Data_syn.hexStr2Bytes(PublicAPI.ResponsetHeart(sendHead)), xTcpClient);
                                //心跳连接持续中
//                                onResultListener.onResult("心跳连接持续中");
//                                Log.v(TAG, cabinetNum + devId);
                            } else if (LockAPI.ReceiveLockedShut(ss, receiveHead)) {
                                //锁一直未关提示
                                result = AppConstantsKt.LOCKED_SHUT;
//                                onResultListener.onResult(new MyTcpMsg(AppConstantsKt.LOCKED_SHUT, ""));
                                Log.w(TAG, result);
                            } else if (LockAPI.ReceiveLockClose(ss, receiveHead)) {
                                Log.w(TAG, result);
                            } else if (LockAPI.ReceivelockedSucc(ss, receiveHead)) {
                                //全关锁成功提示
                                String resultStr = AppConstantsKt.LOCKED_SUCCESS;
                                onResultListener.onResult(new MyTcpMsg(AppConstantsKt.LOCKED_SUCCESS, ""));
                                Log.w(TAG, resultStr);
                            } else if (InventoryAPI.InventoryData(ss, receiveHead)) {
//                                mXTcpServer.sendMsg(Data_syn.hexStr2Bytes(PublicAPI.ResponsetHeart(ss)), xTcpClient);
                                //心跳连接持续中
                                String dataString = Data_syn.hexStringToString(ss.substring(24, ss.length() - 4));
                                JSONObject jsonObject = new JSONObject(dataString);
                                String epc = jsonObject.get("epc").toString();
                                String antid = jsonObject.get("antid").toString();
                                String rssi = jsonObject.get("rssi").toString();

                                Log.w(TAG, "盘点:epc=" + epc + "--antid=" + antid + "--rssi=" + rssi);
                                epcSet.add(epc);
                            } else if (InventoryAPI.InventoryOver(ss, receiveHead)) {
                                //结束盘点提示
                                String InventoryOverStr = "结束盘点=" + epcSet.size();
                                String InventoryOverStr2 = "结束盘点=" + epcSet.toString();
                                onResultListener.onResult(new MyTcpMsg(AppConstantsKt.INVENTORY_OVER, epcSet.size() + ""));
                                onInventoryResult.onInventoryResult(epcSet);
                                epcSet.clear();
                                Log.w(TAG, InventoryOverStr);
//                                mXTcpServer.sendMsg("结束盘点="+epcSet.size(),xTcpClient);
                            } else if (PublicAPI.ReportHFCard(ss, receiveHead)) {
                                //高频刷卡结果
                                String reportHFCard = "高频刷卡结果:" + ss;
                                mXTcpServer.sendMsg(ss, xTcpClient);
                                onResultListener.onResult(new MyTcpMsg(AppConstantsKt.HFCard, ss));
                                Log.w("lalala", reportHFCard);
                            }else if(RFIDConfigAPI.ReportQueryRfid(ss,receiveHead)){
                                String reportHFCard = "ReportQueryRfid结果:" + ss;
                                String dataString = Data_syn.hexStringToString(ss);
//                                JSONObject jsonObject = new JSONObject(dataString);
//                                mXTcpServer.sendMsg(ss, xTcpClient);
//                                onResultListener.onResult(new MyTcpMsg(AppConstantsKt.HFCard, ss));
                                Log.w("lalala", reportHFCard);
                                Log.w("lalala", "dataString="+dataString);
                            }else if (RFIDConfigAPI.ReportmodelBasis(ss, receiveHead)) {
                                String substring = Data_syn.hexStringToString(ss.substring(26, ss.length() - 4));
                                System.out.println(utils.currentTime() + "\u3000" + "查询模块基本信息成功,msg" + ss);
                                //json
                            /*
                            {
"min_power":0, //最小功率
"max_power":33,//最大功率
"ant_num":16, //天线数量
"freq_list":"0,1,2,3,4,5,6", //频段列表
"rfid_list":"0,1,2,3" //协议列表
}

                             */
                            } else if (RFIDConfigAPI.ReportmodelBaseband(ss, receiveHead)) {
                                System.out.println(utils.currentTime() + "\u3000" + "查询模块基带参数成功,msg" + ss);
                                int i = Data_syn.HexStringToInt(ss.substring(ss.length() - 12, ss.length() - 10));
                                int i2 = Data_syn.HexStringToInt(ss.substring(ss.length() - 10, ss.length() - 8));
                                int i3 = Data_syn.HexStringToInt(ss.substring(ss.length() - 8, ss.length() - 6));
                                int i4 = Data_syn.HexStringToInt(ss.substring(ss.length() - 6, ss.length() - 4));
                            } else if (RFIDConfigAPI.ReportmodelSpectrum(ss, receiveHead)) {
                                System.out.println(utils.currentTime() + "\u3000" + "查询模块频段成功,msg" + ss);
                                int i = Data_syn.HexStringToInt(ss.substring(26, 28));
                            } else if (RFIDConfigAPI.ReportmodelBasebandSucc(ss, receiveHead)) {
                                System.out.println(utils.currentTime() + "\u3000" + "设置模块基带参数成功,msg" + ss);
                            } else if (RFIDConfigAPI.ReportmodelSpectrumSucc(ss, sendHead)) {
                                System.out.println(utils.currentTime() + "\u3000" + "设置模块频段成功,msg" + ss);
                            } else if (RFIDConfigAPI.Reportantpower(ss, receiveHead)) {
                                String pow = Data_syn.hexStringToString(ss.substring(26, ss.length() - 4));
                                System.out.println("功率数据" + pow);
                                JSONObject jsonObject = new JSONObject(pow);
                                //map获取list数据的object类
                                Object o = jsonObject.get("power");
                                //object转String 再转list
                                JSONArray jsonArray = new JSONArray(o.toString());
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String antid = object.get("antid").toString();
                                    String po = object.get("power").toString();
                                }
                                onResultListener.onResult(new MyTcpMsg(AppConstantsKt.REPORT_ANT_POWER, pow));
                                System.out.println(utils.currentTime() + "\u3000" + "查询模块功率成功,msg" + ss);
                            } else if (RFIDConfigAPI.ReportantpowerSucc(ss, receiveHead)) {
                                System.out.println(utils.currentTime() + "\u3000" + "设置模块功率成功,msg" + ss);
                            } else if (RFIDConfigAPI.ReportbasebandvSucc(ss, receiveHead)) {
                                System.out.println(utils.currentTime() + "\u3000" + "查询模块基带版本信息成功,msg" + ss);
                                String basebandversionvalue = ss.substring(ss.length() - 12, ss.length() - 4);
                                String a = Integer.toString(Data_syn.HexStringToInt(basebandversionvalue.substring(0, 2)));
                                String b = Integer.toString(Data_syn.HexStringToInt(basebandversionvalue.substring(2, 4)));
                                String c = Integer.toString(Data_syn.HexStringToInt(basebandversionvalue.substring(4, 6)));
                                String d = Integer.toString(Data_syn.HexStringToInt(basebandversionvalue.substring(6, 8)));
                                System.out.println("模块基带版本:" + a + "." + b + "." + c + "." + d);
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
        String result = "onValidationFail---" + tcpMsg.toString();
        Log.e(TAG, result);
//        onResultListener.onResult(result);
    }

    //客户端连接断开
    @Override
    public void onClientClosed(XTcpServer xTcpServer, XTcpClient xTcpClient, String s, Exception e) {
        String result = "客户端连接断开,异常---" + s.toString();
        Log.e(TAG, result);
        startServer();
        String lockstate = LockAPI.SendQueryLockstate(sendHead);
        Log.d(TAG, lockstate);
//        onResultListener.onResult(result);
    }

    //服务器端关闭
    @Override
    public void onServerClosed(XTcpServer xTcpServer, String s, Exception e) {
        String result = "服务器端关闭---" + s.toString();
        Log.e(TAG, result);
//        String lockstate = LockAPI.SendQueryLockstate(sendHead);
//        Log.d(TAG, "服务器端关闭,异常:" + lockstate);
//        onResultListener.onResult(result);
    }

    public void openLock() {
        String openLocks = PublicAPI.OpenLock(sendHead);
        mXTcpServer.sendMsg(Data_syn.hexStr2Bytes(openLocks), mXTcpClient);
        Log.d(TAG, "发送开锁指令:" + openLocks);
    }

    /**
     * 开灯
     */
    public void sendOpenLamp() {

        String openLamp = LockAPI.SendOpenLamp(sendHead);
        mXTcpServer.sendMsg(Data_syn.hexStr2Bytes(openLamp), mXTcpClient);
    }

    /**
     * 关灯
     */
    public void sendCloseLamp() {
        String closeLamp = LockAPI.SendCloseLamp(sendHead);
        mXTcpServer.sendMsg(Data_syn.hexStr2Bytes(closeLamp), mXTcpClient);
    }

    /**
     * 全盘扫描
     */
    public void getFCLInventory() {
//        String getFCLInventory = InventoryAPI.GetFCLInventory(sendHead,"0001",Data_syn.IntToHexString(05));
        String getFCLInventory = "1799A858A115F60602000105fb23";
        mXTcpServer.sendMsg(Data_syn.hexStr2Bytes(getFCLInventory), mXTcpClient);
        Log.i(TAG, "全盘盘点指令:" + getFCLInventory);
    }

    public void getAntPower() {
        String queryRFIDCon = RFIDConfigAPI.Getantpower(sendHead,"01");
//        String queryRFIDCon = "1799A803CB9C2B170C000101c96c";
//        String queryRFIDCon = "1799A803CB9C2B170E0001012404";
        mXTcpServer.sendMsg(Data_syn.hexStr2Bytes(queryRFIDCon), mXTcpClient);
        Log.d(TAG, "getAntPower:" + queryRFIDCon);
    }

    public void setAntPower(List<AntPowerDao> antPowerList) {
        Log.d(TAG, "antPowerList:" + antPowerList);
        String modulevalue = "01";//模块数量：1/2 参数一字节2位 int转hex

        String tempStr="{\"power\":[{\"antid\":\"1\",\"power\":\"25\"},{\"antid\":\"2\",\"power\":\"25\"},{\"antid\":\"3\",\"power\":\"30\"},{\"antid\":\"4\",\"power\":\"30\"},{\"antid\":\"5\",\"power\":\"30\"},{\"antid\":\"6\",\"power\":\"30\"},{\"antid\":\"7\",\"power\":\"30\"},{\"antid\":\"8\",\"power\":\"30\"}]}";
        String tempStr2="[{\"antid\":\"1\",\"power\":\"25\"},{\"antid\":\"2\",\"power\":\"25\"},{\"antid\":\"3\",\"power\":\"30\"},{\"antid\":\"4\",\"power\":\"30\"},{\"antid\":\"5\",\"power\":\"30\"},{\"antid\":\"6\",\"power\":\"30\"},{\"antid\":\"7\",\"power\":\"30\"},{\"antid\":\"8\",\"power\":\"30\"}]";
        Map powermaps = new HashMap();//json
        List list=new ArrayList();
        for (int i=0;i<antPowerList.size();i++){
            Map powerMap=new HashMap();
            powerMap.put("antid",Integer.parseInt(antPowerList.get(i).getAntid()));
            powerMap.put("power",Integer.parseInt(antPowerList.get(i).getPower()));
            list.add(powerMap);
        }
        powermaps.put("power", list);
        String setantpowers = RFIDConfigAPI.Setantpower(sendHead, modulevalue, powermaps);
        mXTcpServer.sendMsg(Data_syn.hexStr2Bytes(setantpowers), mXTcpClient);
        System.out.println(utils.currentTime() + "\u3000" + "发送设置模块功率:" + setantpowers);
        Log.d(TAG, "powermaps:" + powermaps);
    }

}

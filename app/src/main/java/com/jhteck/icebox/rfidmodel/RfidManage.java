package com.jhteck.icebox.rfidmodel;

import android.util.Log;

import com.jhteck.icebox.api.AntPowerDao;
import com.jhteck.icebox.myinterface.MyCallback;
import com.naz.serial.port.ModuleManager;
import com.payne.connect.port.SerialPortHandle;
import com.payne.reader.Reader;
import com.payne.reader.base.Consumer;
import com.payne.reader.bean.config.AntennaCount;
import com.payne.reader.bean.config.Session;
import com.payne.reader.bean.receive.Failure;
import com.payne.reader.bean.receive.InventoryFailure;
import com.payne.reader.bean.receive.InventoryTag;
import com.payne.reader.bean.receive.InventoryTagEnd;
import com.payne.reader.bean.receive.OutputPower;
import com.payne.reader.bean.receive.Success;
import com.payne.reader.bean.receive.Version;
import com.payne.reader.bean.receive.WorkAntenna;
import com.payne.reader.bean.send.FastSwitchEightAntennaInventory;
import com.payne.reader.bean.send.InventoryConfig;
import com.payne.reader.bean.send.OutputPowerConfig;
import com.payne.reader.bean.send.PowerEightAntenna;
import com.payne.reader.process.ReaderImpl;
import com.payne.reader.util.ArrayUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author wade
 * @Description:(rfid扫描仪管理器)
 * @date 2023/9/5 22:59
 */
public class RfidManage {
    private String TAG = "RfidManage";
    private static RfidManage instance;
    private boolean mLoopInventory;
    private Set<String> rfidArrays;
    private Reader mReader;
    private MyCallback<String> antPowerArrayCallback;
    private MyCallback<String> versionCallback;

    public void setVersionCallback(MyCallback<String> versionCallback) {
        this.versionCallback = versionCallback;
    }

    public void setAntPowerArrayCallback(MyCallback<String> antPowerArrayCallback) {
        this.antPowerArrayCallback = antPowerArrayCallback;
    }


    public interface OnInventoryResult {
        void onInventoryResult(Set<String> rfids);
    }

    private OnInventoryResult rfidArraysRendEndCallback;


    public void setRfidArraysRendEndCallback(OnInventoryResult rfidArraysRendEndCallback) {
        this.rfidArraysRendEndCallback = rfidArraysRendEndCallback;
    }

    public void setRfidArraysRendEndCallback() {
        this.rfidArraysRendEndCallback = null;
    }

    public interface OnInventoryResultTest {
        void onInventoryResultTest(Set<String> rfids);
    }

    private OnInventoryResultTest rfidArraysRendEndCallbackTest;


    public void setRfidArraysRendEndCallbackTest(OnInventoryResultTest rfidArraysRendEndCallbackTest) {
        this.rfidArraysRendEndCallbackTest = rfidArraysRendEndCallbackTest;
    }

    public void setRfidArraysRendEndCallbackTest() {
        this.rfidArraysRendEndCallbackTest = null;
    }

    private RfidManage() {
        // 私有构造方法，防止外部实例化
    }

    public static synchronized RfidManage getInstance() {
        if (instance == null) {
            instance = new RfidManage();
        }
        return instance;
    }

    public void initReader() {
        mReader = ReaderImpl.create(AntennaCount.EIGHT_CHANNELS);
        mReader.setOriginalDataCallback(
                new Consumer<byte[]>() {
                    @Override
                    public void accept(byte[] onSend) throws Exception {
                        Log.e("gpenghui", "   发送: " + ArrayUtils.bytesToHexString(onSend, 0, onSend.length));
                    }
                },
                new Consumer<byte[]>() {
                    @Override
                    public void accept(byte[] onReceive) throws Exception {
                        Log.e("gpenghui", "===接收: " + ArrayUtils.bytesToHexString(onReceive, 0, onReceive.length));
                    }
                });
        FastSwitchEightAntennaInventory inventory = new FastSwitchEightAntennaInventory.Builder()
                .session(Session.S0)
                .build();

        InventoryConfig config = new InventoryConfig.Builder()
                .setInventory(inventory)
                .setOnInventoryTagSuccess(new Consumer<InventoryTag>() {
                    @Override
                    public void accept(final InventoryTag inventoryTag) throws Exception {
                        Log.e("gpenghui", "标签信息: " + inventoryTag.toString());
                        try {
                            rfidArrays.add(inventoryTag.getEpc().replace(" ", ""));
                        } catch (Exception ex) {
                            Log.e(TAG, ex.getMessage());
                        }
                        //盘存成功回调
                    }
                })
                .setOnInventoryTagEndSuccess(new Consumer<InventoryTagEnd>() {
                    @Override
                    public void accept(InventoryTagEnd inventoryTagEnd) throws Exception {
                        //一次盘存结束回调
                        Log.e("gpenghui", "一次盘存结束: " + inventoryTagEnd.toString());

                    }
                })
                .setOnFailure(new Consumer<InventoryFailure>() {
                    @Override
                    public void accept(InventoryFailure inventoryFailure) throws Exception {
                        //盘存失败回调
                        Log.e("gpenghui", "盘存失败: " + inventoryFailure.toString());
                    }
                })
                .build();
        mReader.setInventoryConfig(config);
    }

    /**
     * Connect the device
     * 连接设备
     *
     * @param link Connect or disconnect
     *             连接or断开连接
     */
    public void linkDevice(boolean link, String devicePath) {
        Log.d(TAG, "linkDevice devicePath=" + devicePath);
        ModuleManager.newInstance().setUHFStatus(true);
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ModuleManager.newInstance().setUHFStatus(false);

        if (link) {
            //开始连接设备，第一个参数是串口号，第二个参数是波特率，具体可参考api文档
            SerialPortHandle handle = new SerialPortHandle(devicePath, 115200);
            boolean linkSuccess = mReader.connect(handle);
            if (linkSuccess) {
                //UHF模块上电操作（如果模块已经上电，可以去掉上电操作）
                if (!ModuleManager.newInstance().setUHFStatus(true)) {
                    return;
                }
                Log.d(TAG, "link_success");
            } else {
                Log.d(TAG, "link_failed");
            }
        } else {
            mReader.disconnect();
            ModuleManager.newInstance().setUHFStatus(false);
            Log.d(TAG, "link_device");
        }
    }

    /**
     * Get the reader version number
     * 获取读写器版本号
     */
    public void getVersion() {
        if (!mReader.isConnected()) {
            //未连接，提示用户
            versionCallback.callback("未连接");
            return;
        }
        mReader.getFirmwareVersion(
                new Consumer<Version>() {
                    @Override
                    public void accept(final Version version) throws Exception {
                        //异步线程修改UI，需要先切换到主线程
                        versionCallback.callback("当前版本号:"+version.getVersion());
                    }
                },
                new Consumer<Failure>() {
                    @Override
                    public void accept(Failure failure) throws Exception {
                        //异步线程修改UI，需要先切换到主线程
                        versionCallback.callback("获取版本号失败");
                    }
                });
    }

    /**
     * Start or stop inventory
     * 开始或者停止盘存
     *
     * @param startInventory bool
     */
    public void  startStop(boolean startInventory,boolean isTest) {
        Log.d(TAG, "startStop");
        mLoopInventory = startInventory;
        if (startInventory) {
            if (!mReader.isConnected()) {
                //未连接，提示用户
                Log.d(TAG, "please_link_device");
                return;
            }
            rfidArrays = new HashSet<String>();
            mReader.startInventory(true);
        } else {
            mReader.stopInventory();
            //结束扫描后返回结果
            Log.e("gpenghui", "一次盘存结束: " + rfidArrays.toString());
            if(isTest){
                rfidArraysRendEndCallbackTest.onInventoryResultTest(rfidArrays);
            }else {
                rfidArraysRendEndCallback.onInventoryResult(rfidArrays);
            }
        }
    }

    //查询读写器当前工作天线
    public void getWorkAntenna() {
        if (!mReader.isConnected()) {
            Log.d(TAG, "please_link_device");
            return;
        }
        mReader.getWorkAntenna(new Consumer<WorkAntenna>() {
            @Override
            public void accept(WorkAntenna workAntenna) throws Exception {
                Log.d(TAG, "success=" + workAntenna.toString());
            }
        }, new Consumer<Failure>() {
            @Override
            public void accept(Failure failure) throws Exception {
                Log.d(TAG, "Failure=" + failure.toString());
            }
        });
    }

    //查询读写器当前输出功率
    public void getOutputPower() {
        if (!mReader.isConnected()) {
            Log.d(TAG, "please_link_device");
            return;
        }
        mReader.getOutputPower(new Consumer<OutputPower>() {
            @Override
            public void accept(OutputPower outputPower) throws Exception {
                Log.d(TAG, "success=" + outputPower.toString());
                antPowerArrayCallback.callback(Arrays.toString(outputPower.getOutputPower()));
            }
        }, null);
    }

    //2.4.3.2	设置每个天线的射频输出功率
    public void setOutputPower(List<AntPowerDao> antPowerList) {
        if (!mReader.isConnected()) {
            Log.d(TAG, "please_link_device");
            return;
        }
        byte powerA = 33;
        byte powerB = 33;
        byte powerC = 33;
        byte powerD = 33;
        byte powerE = 33;
        byte powerF = 33;
        byte powerG = 33;
        byte powerH = 33;
        for (int i = 0; i < antPowerList.size(); i++) {
            if (i == 0) {
                powerA = Byte.parseByte(antPowerList.get(i).getPower());
            } else if (i == 1) {
                powerB = Byte.parseByte(antPowerList.get(i).getPower());
            } else if (i == 2) {
                powerC = Byte.parseByte(antPowerList.get(i).getPower());
            } else if (i == 3) {
                powerD = Byte.parseByte(antPowerList.get(i).getPower());
            } else if (i == 4) {
                powerE = Byte.parseByte(antPowerList.get(i).getPower());
            } else if (i == 5) {
                powerF = Byte.parseByte(antPowerList.get(i).getPower());
            } else if (i == 6) {
                powerG = Byte.parseByte(antPowerList.get(i).getPower());
            } else if (i == 7) {
                powerH = Byte.parseByte(antPowerList.get(i).getPower());
            }
        }
        mReader.setOutputPower(
                OutputPowerConfig.outputPower(new PowerEightAntenna.Builder().powerA(powerA).powerB(powerB)
                        .powerC(powerC).powerD(powerD).powerE(powerE)
                        .powerF(powerF).powerG(powerG).powerH(powerH).build()),
                new Consumer<Success>() {
                    @Override
                    public void accept(Success success) throws Exception {

                    }
                }, new Consumer<Failure>() {
                    @Override
                    public void accept(Failure failure) throws Exception {

                    }
                }
        );
    }
    //清除所有对象
    public void clearAll() {
        if (mReader != null) {
            mReader.disconnect();
            mReader=null;
        }
        if (instance != null)
            instance = null;

    }
}

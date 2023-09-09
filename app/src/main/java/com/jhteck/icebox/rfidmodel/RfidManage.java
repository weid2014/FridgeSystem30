package com.jhteck.icebox.rfidmodel;

import android.util.Log;
import android.widget.ArrayAdapter;

import com.jhteck.icebox.api.AppConstantsKt;
import com.jhteck.icebox.bean.MyTcpMsg;
import com.jhteck.icebox.myinterface.MyCallback;
import com.naz.serial.port.ModuleManager;
import com.naz.serial.port.SerialPortFinder;
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
import com.payne.reader.bean.receive.WorkAntenna;
import com.payne.reader.bean.send.FastSwitchEightAntennaInventory;
import com.payne.reader.bean.send.InventoryConfig;
import com.payne.reader.bean.send.OutputPowerConfig;
import com.payne.reader.bean.send.PowerEightAntenna;
import com.payne.reader.process.ReaderImpl;
import com.payne.reader.util.ArrayUtils;
import com.work.tcp.utils.Data_syn;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author wade
 * @Description:(rfid扫描仪管理器)
 * @date 2023/9/5 22:59
 */
public class RfidManage {
    private String TAG = "RfidManage";
    private static RfidManage instance;

    private boolean mLoopInventory;
    private ArrayAdapter<String> mArrayAdapter;
    private Reader mReader;
    private String[] mDevicesPath;
    private boolean mKeyF4Pressing = false;
    private MyCallback<String> antPowerArrayCallback;

    public void setAntPowerArrayCallback(MyCallback<String> antPowerArrayCallback) {
        this.antPowerArrayCallback = antPowerArrayCallback;
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

    public void initSerialPort() {
        SerialPortFinder portFinder = new SerialPortFinder();
        String[] devices = portFinder.getAllDevices();
        mDevicesPath = portFinder.getAllDevicesPath();

    }

    public void initReader() {
        Log.d(TAG,"initReader");
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
                        //盘存成功回调
                        Log.e("gpenghui", "标签信息: " + inventoryTag.toString());
                        /*runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mArrayAdapter.add(inventoryTag.getEpc());
                            }
                        });*/
                    }
                })
                .setOnInventoryTagEndSuccess(new Consumer<InventoryTagEnd>() {
                    @Override
                    public void accept(InventoryTagEnd inventoryTagEnd) throws Exception {
                        //一次盘存结束回调
//                        Log.e("gpenghui", "一次盘存结束: " + inventoryTagEnd.toString());
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
    public void linkDevice(boolean link) {
        Log.d(TAG,"linkDevice");
//        initReader();
        ModuleManager.newInstance().setUHFStatus(true);
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ModuleManager.newInstance().setUHFStatus(false);

        if (link) {
            //Start to connect the device, the first parameter is the serial port number,
            // the second parameter is the baud rate, please refer to the api document for details
            //开始连接设备，第一个参数是串口号，第二个参数是波特率，具体可参考api文档
//            String devicePath = mDevicesPath[spSerialNumber.getSelectedItemPosition()];
            SerialPortHandle handle = new SerialPortHandle("/dev/ttyS8", 115200);
            boolean linkSuccess = mReader.connect(handle);
            if (linkSuccess) {
                //UHF module power-on operation (if the module has been powered on, you can remove the power-on operation)
                //UHF模块上电操作（如果模块已经上电，可以去掉上电操作）
                if (!ModuleManager.newInstance().setUHFStatus(true)) {
                    Log.d(TAG, "link_error");
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
    /*private void getVersion() {
        if (!mReader.isConnected()) {
            //Not connected, prompt the user
            //未连接，提示用户
            Toast.makeText(this, R.string.please_link_device, Toast.LENGTH_SHORT).show();
            return;
        }
        mReader.getFirmwareVersion(
                new Consumer<Version>() {
                    @Override
                    public void accept(final Version version) throws Exception {
                        //Asynchronous threads modify the UI, you need to switch to the main thread first
                        //异步线程修改UI，需要先切换到主线程
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvVersion.setText(getString(R.string.str_version) + version.getVersion());
                            }
                        });
                    }
                },
                new Consumer<Failure>() {
                    @Override
                    public void accept(Failure failure) throws Exception {
                        //Asynchronous threads modify the UI, you need to switch to the main thread first
                        //异步线程修改UI，需要先切换到主线程
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, R.string.get_version_error, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
    }*/

    /**
     * Start or stop inventory
     * 开始或者停止盘存
     *
     * @param startInventory bool
     */
    public void startStop(boolean startInventory) {
        Log.d(TAG, "startStop");
        mLoopInventory = startInventory;
        if (startInventory) {
            if (!mReader.isConnected()) {
                //Not connected, prompt the user
                //未连接，提示用户
//                Toast.makeText(this, R.string.please_link_device, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "please_link_device");
                return;
            }
//            btnInventory.setSelected(true);
//            btnInventory.setText(R.string.stop_inventory);
//            mArrayAdapter.clear();
            mReader.startInventory(true);
        } else {
//            btnInventory.setSelected(false);
//            btnInventory.setText(R.string.start_inventory);
            mReader.stopInventory();
        }
    }

    //查询读写器当前工作天线
    public void getWorkAntenna(){
        if (!mReader.isConnected()) {
            Log.d(TAG, "please_link_device");
            return;
        }
        mReader.getWorkAntenna(new Consumer<WorkAntenna>() {
            @Override
            public void accept(WorkAntenna workAntenna) throws Exception {
                Log.d(TAG, "success="+workAntenna.toString());
            }
        }, new Consumer<Failure>() {
            @Override
            public void accept(Failure failure) throws Exception {
                Log.d(TAG, "Failure="+failure.toString());
            }
        });
    }
    //查询读写器当前输出功率
    public void getOutputPower(){
        if (!mReader.isConnected()) {
            Log.d(TAG, "please_link_device");
            return;
        }
        mReader.getOutputPower(new Consumer<OutputPower>() {
            @Override
            public void accept(OutputPower outputPower) throws Exception {
                Log.d(TAG, "success="+outputPower.toString());
                antPowerArrayCallback.callback(Arrays.toString(outputPower.getOutputPower()));
            }
        }, new Consumer<Failure>() {
            @Override
            public void accept(Failure failure) throws Exception {

            }
        });
    }
    //2.4.3.2	设置每个天线的射频输出功率
    public void setOutputPower(){
        if (!mReader.isConnected()) {
            Log.d(TAG, "please_link_device");
            return;
        }
        mReader.setOutputPower(
                OutputPowerConfig.outputPower(new PowerEightAntenna.Builder().powerA((byte) 23).powerF((byte) 23).build()),
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

}

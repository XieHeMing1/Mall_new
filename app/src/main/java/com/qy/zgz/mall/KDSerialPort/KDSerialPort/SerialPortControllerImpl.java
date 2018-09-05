package com.qy.zgz.mall.KDSerialPort.KDSerialPort;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.kedie.serialportlibrary.Device;
import com.kedie.serialportlibrary.SerialPortFinder;
import com.kedie.serialportlibrary.SerialPortManager;
import com.kedie.serialportlibrary.SerialPortSendBean;
import com.kedie.serialportlibrary.listener.OnOpenSerialPortListener;
import com.kedie.serialportlibrary.listener.OnSerialPortDataListener;
import com.qy.zgz.mall.KDSerialPort.CMDUtils;
import com.qy.zgz.mall.KDSerialPort.ComBean;
import com.qy.zgz.mall.KDSerialPort.Common;
import com.qy.zgz.mall.KDSerialPort.DBPackageBean;
import com.qy.zgz.mall.KDSerialPort.MachineDevice;
import com.qy.zgz.mall.KDSerialPort.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * Created by Bill.T on 2018/4/26.
 */

public class SerialPortControllerImpl implements SerialPortController {

    static final InternalHandler sHandler = new InternalHandler();
    static final int onCoinOuting =1,
            onCoinOutSuccess=2,
            onCoinOutFail=3,
            onReceivedMomey=4,
            onReceivedMomeySuccess=5,
            onReceivedMomeyFail=6,
            onSendCompleteData=7,
            onSerialPortOpenFail=8,
            onSerialPortOpenSuccess=9,
            onMachieConnectedSuccess=10,
            onMachieCommectedFail=11;
    private static final Object lock = new Object();
    private static volatile SerialPortControllerImpl instance;
    protected final String TAG = "CQ";
    private DispQueueThread dispQueueThread;
    private SendQueueThread sendQueueThread;
    private SerialPortFinder spFinder;
    private static SerialPortListener serialPortListener;
    protected ArrayList<Device> mDevices;
    private List<MachineDevice> machineDeviceList;
    private List<DBPackageBean> packageBeans;
    private Map<String, SerialPortManager> mapManagers = new HashMap<String, SerialPortManager>();
    // 纸币器，币斗是否接连
    public boolean isOpenZBJ = false,isOpenBD=false;
    // 应出币数
    int ShouldOut = 0;
    // 实出币数
    int SctualOut = 0;
    // 出币故障
    boolean OutError = false;
    public byte[] ItlLastSendCmd, IctLastSendCmd, bdLastSendComd;
    private int iMomey = 0;
    // ITL开启使能之前需要检查纸币器是否还有未处理完的指，检查到已经处理完后状态改为false
    private boolean ITL_CHENC_02 = true;

    //ITL可用通道
    private byte[] INHIBITS = null;

    // 是否正在设置收钱通道
    private boolean SET_ITL_INHIBITS = false;

    // 上一次发送的命令
    private String CMD_TAG = "0x01";

    //清币命令
    private boolean isClearCoin=false;

    //成功出币
    public boolean isSuccessOutCoin=true;

    //是否已接受故障命令
    public boolean isReceiveError=false;

    private SerialPortControllerImpl(List<MachineDevice> machineDeviceList,List<DBPackageBean> packageBeans){

        this.machineDeviceList = machineDeviceList;
        this.packageBeans = packageBeans;

    }

    /**
     * 打开串口
     * device 串口设备文件
     * baudrate 波特率，一般是9600
     * parity 奇偶校验，0 None, 1 Odd, 2 Even
     * dataBits 数据位，5 - 8
     * stopBit 停止位，1 或 2
     */
    protected void SerialPortOpen() {
        init();
        for (final MachineDevice dev : machineDeviceList) {
            String deviceFile = "";
            SerialPortManager serialPortManager = new SerialPortManager();
            serialPortManager.setOnSerialPortDataListener(new OnSerialPortDataListener() {
                @Override
                public void onDataReceived(byte[] bytes, String deviceFile) {
                    ComBean comBean = new ComBean(deviceFile, bytes, bytes.length);
                    dispQueueThread.AddQueue(comBean);
                }

                @Override
                public void onDataSent(byte[] bytes, String deviceFile) {
                    MachineDevice machineDevice = queryMachineDevice(deviceFile);
                    if (machineDevice != null) {
                        if (machineDevice.getMachineKind() == 1) { //ICT
                            IctLastSendCmd = bytes;
                        } else if (machineDevice.getMachineKind() == 2) { //ITL
                            ItlLastSendCmd = bytes;
                        } else if (machineDevice.getMachineKind() == 3 ) { //币斗
                            bdLastSendComd = bytes;
                        }
                    }
                    sHandler.obtainMessage(onSendCompleteData,bytes).sendToTarget();

                    //serialPortListener.onSendCompleteData(bytes);
                }
            });
            serialPortManager.setOnOpenSerialPortListener(new OnOpenSerialPortListener() {
                @Override
                public void onSuccess(File device) {
                    //serialPortListener.onSerialPortOpenSuccess(device);
                    sHandler.obtainMessage(onSerialPortOpenSuccess,device).sendToTarget();
                }

                @Override
                public void onFail(File device, Status status) {
                    //serialPortListener.onSerialPortOpenFail(device);
                    sHandler.obtainMessage(onSerialPortOpenFail,device).sendToTarget();
                }
            });
            for (Device device : mDevices) {
                if (dev.getDevice().contains(device.getName())) {
                    deviceFile = "/dev/"+device.getName() + "_" + util.setMachineKind(dev.getMachineKind());
                    serialPortManager.openSerialPort(device.getFile(), dev.getBaudrate(), dev.getParity(), dev.getDataBits(), dev.getStopBit());
                    break;
                }
            }
            mapManagers.put(deviceFile, serialPortManager);
            initMachie(deviceFile);
        }
    }

    //初始化设备
    private void initMachie(String device) {
        try {
            String[] str = device.split("_");
            switch (str[1]) {
                case "ITL":
                    sendITLOpenCmdThread();
                    break;
                case "币斗":
                    sendBDOpendCmdThread();
                    break;
                case "ICT":
                    SerialPortSendData(CMDUtils.command_ict_0c(), device);
                    sendICTOpenCmdThread();
                    break;
            }
        }catch (Exception ex){

        }
    }

    // 纸币器是否连接
    private void sendITLOpenCmdThread() {
        new Thread() {
            @Override
            public void run() {
                final String device = getDevice("2");
                while (!isOpenZBJ) {
                    //serialPortListener.onMachieCommectedFail(device);
                    sHandler.obtainMessage(onMachieCommectedFail,device).sendToTarget();
                    SerialPortSendData(CMDUtils.command_itl_init(), device);
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    // 纸币器是否连接
    private void sendICTOpenCmdThread() {
        new Thread() {
            @Override
            public void run() {
                final String device = getDevice("1");
                while (!isOpenZBJ) {
                    //serialPortListener.onMachieCommectedFail(device);
                    sHandler.obtainMessage(onMachieCommectedFail,device).sendToTarget();
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    // 币斗是否连接
    private void sendBDOpendCmdThread() {
        new Thread() {
            @Override
            public void run() {
                final String device = getDevice("3");
                while (!isOpenBD) {
                    //serialPortListener.onMachieCommectedFail(device);
                    sHandler.obtainMessage(onMachieCommectedFail,device).sendToTarget();
                    SerialPortSendData(CMDUtils.command_bd_ma(), device);
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }


    private void init() {
        dispQueueThread = new DispQueueThread();
        sendQueueThread = new SendQueueThread();
        sendQueueThread.start();
        dispQueueThread.start();
        spFinder = new SerialPortFinder();
        mDevices = spFinder.getDevices();
    }

    public static void registerInstance(List<MachineDevice> machineDeviceList, List<DBPackageBean> packageBeans) {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new SerialPortControllerImpl(machineDeviceList,packageBeans);
                }
            }
        }
        kd.Ext.setSerialPortController(instance);
    }

    //是否接收到命令
private boolean isReceiveCmd=false;
    @Override
    public void bdSendOutCoin(int countCoin, String device, int type) {
        isReceiveError=false;
        isReceiveCmd=false;
        ShouldOut = countCoin;
        SctualOut = 0;
        byte[] outCmd = CMDUtils.command_qe(countCoin,type);


        SerialPortSendData(outCmd, device);

        new Thread(new Runnable() {
            @Override
            public void run() {
                int  count = 1;
                while(true){
                    try{
                        Thread.sleep(100);
                        if (isReceiveCmd){
                            return;
                        }
                        count++;
                        if(count == 180){
                            SerialPortSendData(CMDUtils.command_bd_jw(),device);
                            String[] vals = new String[]{String.valueOf(SctualOut),String.valueOf(ShouldOut),"-1"};
                            sHandler.obtainMessage(onCoinOutFail,vals).sendToTarget();
                            return;
                        }
                    }catch (Exception e){

                    }
                }
            }
        }).start();
    }

    @Override
    public void bdCleanError() {
        OutError = false;
        SerialPortSendData(CMDUtils.command_bd_yv(),getDevice("3"));
    }

    @Override
    public void bdContinueToCoin() {
        SerialPortSendData(CMDUtils.command_bd_vu(),getDevice("3"));
    }

    @Override
    public void bdStopToCoin() {
        SerialPortSendData(CMDUtils.command_zt(),getDevice("3"));
    }

    @Override
    public void bdCoinOuted() {
        SerialPortSendData(CMDUtils.command_bd_jw(),getDevice("3"));
    }

    @Override
    public String getDevice(String kind) {
        String device = null;
        for(MachineDevice machineDevice:machineDeviceList){
            if(machineDevice.getMachineKind() == Integer.parseInt(kind)){
                device = machineDevice.getDevice()+"_"+util.setMachineKind(machineDevice.getMachineKind());
                break;
            }
        }
        return device;
    }


    @Override
    public void sendGetMomeyCmd(String device) {
        if(device.contains("ITL")) {
            SerialPortSendData(CMDUtils.command_itl_07(), device);
        }
        else if(device.contains("ICT")){
            SerialPortSendData(CMDUtils.command_ict_02(),device);
        }
    }

    @Override
    public void sendOutMomeyCmd(String device) {
        if(device.contains("ITL")){
            SerialPortSendData(CMDUtils.command_itl_08(),device);
        }
        else if(device.contains("ICT")){
            SerialPortSendData(CMDUtils.command_ict_0f(),device);
        }
    }

    @Override
    public void go(SerialPortListener serialPortListener) {
        if(this.serialPortListener == null) {
            SerialPortOpen();
        }
        this.serialPortListener = serialPortListener;
        //initMachie(getDevice("3"));
        SerialPortSendData(CMDUtils.command_bd_ma(), getDevice("3"));
    }


    @Override
    public ArrayList<Device> getDevices() {
        return mDevices;
    }

    @Override
    public void outAllCoin() {
        isClearCoin=true;
        SerialPortSendData(CMDUtils.command_pi(), getDevice("3"));
    }

    @Override
    public boolean getIsSuccessOutCoin() {
        return isSuccessOutCoin;
    }

    @Override
    public void setIsSuccessOutCoin(boolean is) {
         isSuccessOutCoin=is;
    }

    @Override
    public void colseBanknote() {
//        Log.i("CQ_ITL_D","colseBanknote");
//        //throw new RuntimeException("colseBanknote");
//        kd.Ext.setColseBanknote(true);
        SerialPortSendData(CMDUtils.command_ict_5e(),getDevice("1"));
    }

    @Override
    public void enableBanknote() {
//        Log.i("CQ_ITL_D", "enableBanknote");
//        kd.Ext.setColseBanknote(false);
        SerialPortSendData(CMDUtils.command_ict_3e(), getDevice("1"));
//        if(!CMD_TAG.equals("0x01")) {
//            SerialPortSendData(CMDUtils.command_itl_0a(), getDevice("2"));
//        }
    }

    @Override
    public void outCoinStop() {
        SerialPortSendData(CMDUtils.command_bd_jw(),getDevice("3"));
    }

    @Override
    public void stopOutCoins() {
        SerialPortSendData(CMDUtils.command_zt(),getDevice("3"));
    }

    @Override
    public void setItlInhibits(List<DBPackageBean> dbPackageBean) {
//        this.packageBeans = dbPackageBean;
//        String device = getDevice("2");
//        SET_ITL_INHIBITS = true;
//        colseBanknote();
    }

    @Override
    public Boolean isOpenZBJ() {
        return isOpenZBJ;
    }

    /**
     * 发送数据
     *
     * @param bytes
     * @return
     */
    protected void SerialPortSendData(byte[] bytes, String mactype) {
        SerialPortManager sp = mapManagers.get(mactype);
        SerialPortSendBean serialPortBean = new SerialPortSendBean(sp, bytes);
        Log.i(TAG,mactype + "发送给设备==="+ Common.ByteArrToHex(bytes,true));
        sendQueueThread.AddQueue(serialPortBean);
    }

    //接收指令队列
    private class DispQueueThread extends Thread {
        private Queue<ComBean> QueueList = new LinkedList<ComBean>();

        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                final ComBean ComData;
                while ((ComData = QueueList.poll()) != null) {
                    byte[] compCmd;
                    MachineDevice machineDevice = queryMachineDevice(ComData.sComPort);
                    if (machineDevice != null) {
                        String devFile = machineDevice.getDevice() + "_" + util.setMachineKind(machineDevice.getMachineKind());
                        CMDUtils.add_not_complete_cmd(ComData.bRec, machineDevice.getMachineKind());
                        Log.i(TAG, devFile + "收到--------->" + Common.ByteArrToHex(ComData.bRec, true));
                        if (machineDevice.getMachineKind() == 1 ? CMDUtils.check_ict_cmd() : false) { //ICT
                            compCmd = CMDUtils.get_ict_omplete_cmd();
                            IctControl(compCmd, IctLastSendCmd, devFile);
                        } else if (machineDevice.getMachineKind() == 2 ? CMDUtils.check_itl_cmd() : false) { //ITL
                            compCmd = CMDUtils.get_itl_omplete_cmd();
                            ItlControl(compCmd, ItlLastSendCmd, devFile);
                        } else if (machineDevice.getMachineKind() == 3 ? CMDUtils.check_bd_cmd() : false) { //币斗
                            compCmd = CMDUtils.get_bd_omplete_cmd();
                            List<byte[]> listCmds = CMDUtils.bd_is_cmds(compCmd);
                            for (byte[] cmd : listCmds) {
                                SbjControl(cmd, bdLastSendComd, devFile);
                            }

                        } else {
                            Log.i(TAG, String.valueOf(machineDevice.getMachineKind()) + "收到无法解析命令--------->" + Common.ByteArrToHex(ComData.bRec, true));
                        }
                        //SPDataReceived(ComData.bRec,ComData.sComPort);
                    }
                    try {
                        Thread.sleep(20);
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

    //ICT纸巾机处理逻辑
    private void IctControl(byte[] getCmd,byte[] sendCmd, String devFile) {
        String GET = Common.ByteArrToHex(getCmd, true);
        String SNED = Common.ByteArrToHex(sendCmd, true);
        String ICT_TAG = "CQ_ICT";
        Log.i(ICT_TAG, "ICT纸巾机 ---- " + SNED + "----->" + GET);
        if (getCmd.length == 1) {
            //TODO 发送初始化命令后只要有返回侧表示ICT已经连接
            if (Common.ByteArrToHex(sendCmd,false).contains("0C")){
                isOpenZBJ = true;
                //serialPortListener.onMachieConnectedSuccess(devFile);
                sHandler.obtainMessage(onMachieConnectedSuccess,devFile).sendToTarget();
            }
            if (Common.ByteArrToHex(sendCmd,false).contains("0C") && Common.Byte2Hex(getCmd[0]).equals("5E")) {
                SerialPortSendData(CMDUtils.command_ict_3e(), devFile);
                //serialPortListener.onMachieConnectedSuccess(devFile);
                sHandler.obtainMessage(onMachieConnectedSuccess,devFile).sendToTarget();
            } else if (Common.ByteArrToHex(sendCmd,false).contains("02") && Common.Byte2Hex(getCmd[0]).equals("10")) {
                //serialPortListener.onReceivedMomeySuccess(iMomey, getDevice("3"));
                String[] vals = new String[]{String.valueOf(iMomey),getDevice("3")};
                sHandler.obtainMessage(onReceivedMomeySuccess,vals).sendToTarget();
            }

        } else if (getCmd.length > 1) {
            if (Common.Byte2Hex(getCmd[0]).equals("80") && Common.Byte2Hex(getCmd[1]).equals("8F")) {
                isOpenZBJ = true;
                SerialPortSendData(CMDUtils.command_ict_02(), devFile);
                //serialPortListener.onMachieConnectedSuccess(devFile);
                sHandler.obtainMessage(onMachieConnectedSuccess,devFile).sendToTarget();
            } else if (Common.Byte2Hex(getCmd[0]).equals("81") && Common.Byte2Hex(getCmd[1]).equals("3D") && getCmd.length == 4) {
                int mopey = 0;
                switch (Common.Byte2Hex(getCmd[2])) {
                    case "07":
                        mopey = 1;
                        break;
                    case "17":
                        mopey = 5;
                        break;
                    case "27":
                        mopey = 10;
                        break;
                    case "37":
                        mopey = 20;
                        break;
                    case "47":
                        mopey = 50;
                        break;
                    case "57":
                        mopey = 100;
                        break;
                }
                iMomey = mopey;
                if(OutError){
                    SerialPortSendData(CMDUtils.command_ict_0f(),devFile);
                }
                else {
                    //serialPortListener.onReceivedMomey(mopey, devFile);
                    String[] arg = new String[]{String.valueOf(mopey),devFile};
                    sHandler.obtainMessage(onReceivedMomey,arg).sendToTarget();
                }
            }
            else if (Common.Byte2Hex(getCmd[0]).equals("29") && Common.Byte2Hex(getCmd[1]).equals("2F")) {
                sHandler.obtainMessage(onReceivedMomeyFail,devFile).sendToTarget();
                //serialPortListener.onReceivedMomeyFail(devFile);
            }
        }
    }

    /**
     *  ITL纸币机处理逻辑
     * @param getCmd 收到的指令
     * @param snedCmd 发送的指令
     * @param device 设备
     */
    private void ItlControl(byte[] getCmd,byte[] snedCmd,String device) {
        String GET = Common.ByteArrToHex(getCmd,true);
        String SNED = Common.ByteArrToHex(snedCmd,true);
        String TAB_ITL = "CQ_ITL_D";
        Log.i("CQ_ITL_T","ITL纸币机处理逻辑---->"+SNED+"---"+GET);
        if((snedCmd.length == 6 ? Common.Byte2Hex(snedCmd[3]).equals("01") : false) &&
                (getCmd.length == 6 ? Common.Byte2Hex(getCmd[3]).equals("F0") : false)) {
            Log.i(TAB_ITL, "使纸币器复位--- 成功 -" + SNED + "----->" + GET);
            isOpenZBJ = true;
            CMD_TAG = "0x01";
            sHandler.obtainMessage(onMachieConnectedSuccess, device).sendToTarget();
            final String curDevice = device;
            new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.currentThread();
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    SerialPortSendData(CMDUtils.command_itl_07(), curDevice);
                }
            }.start();
        }
        else if(CMD_TAG.equals("0x01") && (getCmd.length == 8 ? Common.Byte2Hex(getCmd[4]).equals("F1") : false)){
            Log.i(TAB_ITL,"复位命令处理中-----" + SNED + "----->"+GET);
            CMD_TAG = "0x01";
            SerialPortSendData(CMDUtils.command_itl_07(), device);
        }
        else if(CMD_TAG.equals("0x01") && (getCmd.length == 7 ? Common.Byte2Hex(getCmd[4]).equals("E8") : false)){
            Log.i(TAB_ITL,"复位命令处理中-----" + SNED + "----->"+GET);
            CMD_TAG = "0x01";
            SerialPortSendData(CMDUtils.command_itl_is_connect(), device);
        }
        else if(CMD_TAG.equals("0x01") && (getCmd.length == 6 ? Common.Byte2Hex(getCmd[3]).equals("F0") : false)){
            Log.i(TAB_ITL,"复位命令流程完成-" + SNED + "----->"+GET);
            CMD_TAG = "poll";
            SerialPortSendData(CMDUtils.command_itl_05(), device);
        }
        else if((snedCmd.length == 6 ? Common.Byte2Hex(snedCmd[3]).equals("11") : false)
                && (getCmd.length == 6 ? Common.Byte2Hex(getCmd[3]).equals("F0") : false)) {//是否发送连接命令
            Log.i(TAB_ITL, "发送 0x11 号命令查找纸币器是否连接 --- 成功 -" + SNED + "----->"+GET);
            //SerialPortSendData(CMDUtils.command_itl_init(),device);
            SerialPortSendData(CMDUtils.command_itl_05(), device);
            //serialPortListener.onMachieConnectedSuccess(device);
        }
        else if((snedCmd.length == 6 ? Common.Byte2Hex(snedCmd[3]).equals("05") : false)
                && (getCmd.length > 30 ? Common.Byte2Hex(getCmd[3]).equals("F0") : false)) {//读取纸币器通道配置情况
            Log.i(TAB_ITL, "发送 0x05 号命令读取纸币器通道配置情况 --- 成功 -" + SNED + "----->"+GET);
            INHIBITS = getCmd;
            List<Integer> curVal = new ArrayList<Integer>();
            curVal.add(100);
            curVal.add(50);
            curVal.add(20);
            curVal.add(10);
            curVal.add(5);
            curVal.add(1);
            SerialPortSendData(CMDUtils.command_itl_02(CMDUtils.itl_get_inhibits(curVal,getCmd)), device);
            //SerialPortSendData(CMDUtils.command_itl_07(),device);
        }
        else if((snedCmd.length == 6 ? Common.Byte2Hex(snedCmd[3]).equals("05") : false)
                && (getCmd.length == 6 ? Common.Byte2Hex(getCmd[3]).equals("F0") : false)) {
            Log.i(TAB_ITL, "重复发送 0x05 --- 成功 -" + SNED + "----->"+GET);
            SerialPortSendData(CMDUtils.command_itl_05(),device);
        }
        else if((snedCmd.length == 8 ? Common.Byte2Hex(snedCmd[3]).equals("02") : false)
                && (getCmd.length == 6 ? Common.Byte2Hex(getCmd[3]).equals("F0") : false)) {//发送 0x02号命令设置允许识别哪几种纸币
            Log.i(TAB_ITL,"发送 0x02号命令设置允许识别哪几种纸币 --- 成功 -" + SNED + "----->"+GET);
            SerialPortSendData(CMDUtils.command_itl_07(),device);
            //SerialPortSendData(CMDUtils.command_itl_0a(),device);
        }
        else if((snedCmd.length == 6 ? Common.Byte2Hex(snedCmd[3]).equals("0A") : false)
                && (getCmd.length == 6 ? Common.Byte2Hex(getCmd[3]).equals("F0") : false) ){
            Log.i(TAB_ITL,"发送 0x0A 号命令允许纸币器识别纸币（使能）--- 成功 -" + SNED + "----->"+GET);
            SerialPortSendData(CMDUtils.command_itl_07(),device);
        }
        else if((snedCmd.length == 6 ? Common.Byte2Hex(snedCmd[3]).equals("07") : false)
                && (getCmd.length == 6 ? (Common.Byte2Hex(getCmd[2]).equals("01") && Common.Byte2Hex(getCmd[3]).equals("F0")) : false)){
            Log.i(TAB_ITL,"循环发送 Poll 命令，等待接收纸币 --- 成功 -" + SNED + "----->"+GET);
            if(kd.Ext.getColseBanknote() == true){
                SerialPortSendData(CMDUtils.command_itl_09(),device);
            }
            else {
                SerialPortSendData(CMDUtils.command_itl_07(), device);
            }
        }
        else if((snedCmd.length == 6 ? Common.Byte2Hex(snedCmd[3]).equals("07") : false)
                && CMDUtils.itl_is_refuse_ed(getCmd)){
            Log.i(TAB_ITL,"纸币正在拒钞中 -" + SNED + "----->"+GET);
            SerialPortSendData(CMDUtils.command_itl_07(),device);
        }
        else if((snedCmd.length == 6 ? Common.Byte2Hex(snedCmd[3]).equals("07") : false)
                && (CMDUtils.itl_is_refuse(getCmd))){//拒绝接收纸币执行完毕，钞票已经被拒收退出
            Log.i(TAB_ITL,"发送--0x09禁能纸币器命令 -" + SNED + "----->"+GET);
            SerialPortSendData(CMDUtils.command_itl_09(), device);
        }
        else if((snedCmd.length == 6 ? Common.Byte2Hex(snedCmd[3]).equals("07") : false)
                && CMDUtils.itl_is_disabled(getCmd)){
            ITL_CHENC_02 = false;
            Log.i(TAB_ITL,"收到 -- 纸币器关闭命令-" + SNED + "----->"+GET);
            SerialPortSendData(CMDUtils.command_itl_0a(),device);
        }
        else if((snedCmd.length == 6 ? Common.Byte2Hex(snedCmd[3]).equals("09") : false)
                && (getCmd.length == 6 ? Common.Byte2Hex(getCmd[3]).equals("F0") : false)){
            Log.i(TAB_ITL,"返回--0x09禁能纸币器命令 -" + SNED + "----->"+GET);
            if(kd.Ext.getColseBanknote() == false && !CMD_TAG.equals("0xEE")) {
                SerialPortSendData(CMDUtils.command_itl_0a(), device);
            }
            else if(SET_ITL_INHIBITS == true){
                List<Integer> curVal = new ArrayList<Integer>();
                for(DBPackageBean packageBean:packageBeans){
                    if(packageBean.getState().equals("1")){
                        curVal.add((int)packageBean.getPackagePrice());
                        Log.i(TAB_ITL,"重新设置收钱通道---"+String.valueOf(packageBean.getPackagePrice()));
                    }
                }

                SET_ITL_INHIBITS = false;
                kd.Ext.setColseBanknote(false);
                SerialPortSendData(CMDUtils.command_itl_02(CMDUtils.itl_get_inhibits(curVal,INHIBITS)),device);
            }
        }
        else if((snedCmd.length == 6 ? Common.Byte2Hex(snedCmd[3]).equals("07") : false)
                && (getCmd.length == 8 ? (Common.Byte2Hex(getCmd[3]).equals("F0") && Common.Byte2Hex(getCmd[4]).equals("EF") && Common.Byte2Hex(getCmd[5]).equals("00")) : false)) {
            Log.i(TAB_ITL, "OK， 有纸币进入 -" + SNED + "----->" + GET);
            SerialPortSendData(CMDUtils.command_itl_07(), device);
        }
        else if((snedCmd.length == 6 ? Common.Byte2Hex(snedCmd[3]).equals("07") : false)
                && (getCmd.length == 8 ? (Common.Byte2Hex(getCmd[3]).equals("F0") && Common.Byte2Hex(getCmd[4]).equals("EF") && !Common.Byte2Hex(getCmd[5]).equals("00")) : false)
                || (getCmd.length == 10 ? (Common.Byte2Hex(getCmd[3]).equals("F0") && Common.Byte2Hex(getCmd[6]).equals("EF") && !Common.Byte2Hex(getCmd[7]).equals("00")) : false)) {
            Log.i(TAB_ITL, "纸币器接收到第" + Common.Byte2Hex(getCmd[5]) + "通道面额的纸币一张 -" + SNED + "----->" + GET);
            //TODO 此处可以判断是否接收当前收到的金额
            int price = CMDUtils.itl_get_momey(getCmd);
            //SerialPortSendData(CMDUtils.command_itl_18(),device);
            if(OutError){
                SerialPortSendData(CMDUtils.command_itl_08(),device);
            }
            else {
                String[] arg = new String[]{String.valueOf(price),device};
                sHandler.obtainMessage(onReceivedMomey,arg).sendToTarget();
                //serialPortListener.onReceivedMomey(price, device);
            }
        }
        else if((snedCmd.length == 6 ? Common.Byte2Hex(snedCmd[3]).equals("08") : false)
                && (getCmd.length == 6 ? Common.Byte2Hex(getCmd[3]).equals("F0") : false)){
            Log.i(TAB_ITL,"发送0x08（拒收） --- 成功 -" + SNED + "----->"+GET);
            SerialPortSendData(CMDUtils.command_itl_07(),device);
        }
        else if((snedCmd.length == 6 ? Common.Byte2Hex(snedCmd[3]).equals("07") : false)
                && (getCmd.length == 7 ? Common.Byte2Hex(getCmd[4]).equals("CC") : false)){
            Log.i(TAB_ITL,"收到---Stacking(压币) -" + SNED + "----->"+GET);
            SerialPortSendData(CMDUtils.command_itl_07(),device);
        }
        else if((snedCmd.length == 6 ? Common.Byte2Hex(snedCmd[3]).equals("07") : false)
                && CMDUtils.itl_is_get_momey_success(getCmd)){
            int price = CMDUtils.itl_get_momey(getCmd);
            Log.i(TAB_ITL,"收钱成功"+String.valueOf(price) +"-" + SNED + "----->"+GET);
            //serialPortListener.onReceivedMomeySuccess(price,getDevice("3"));
            String[] vals = new String[]{String.valueOf(price),getDevice("3")};
            sHandler.obtainMessage(onReceivedMomeySuccess,vals).sendToTarget();
            SerialPortSendData(CMDUtils.command_itl_07(),device);

        }else if((snedCmd.length == 6 ? Common.Byte2Hex(snedCmd[3]).equals("07") : false)
                && (getCmd.length == 7 ? Common.Byte2Hex(getCmd[4]).equals("CC") : false)){
            Log.i(TAB_ITL,"Stacking， Stacked， 表示压币完成 -" + SNED + "----->"+GET);
            SerialPortSendData(CMDUtils.command_itl_07(),device);
        }
        else if((snedCmd.length == 6 ? Common.Byte2Hex(snedCmd[3]).equals("07") : false)
                && CMDUtils.itl_is_get_momey_end(getCmd)){
            Log.i(TAB_ITL,"收钱完成 -" + SNED + "----->"+GET);
            SerialPortSendData(CMDUtils.command_itl_09(),device);
            CMD_TAG = "0xEE";
        }
        else{
            //onSendCompleteData(getCmd);
            Log.i(TAB_ITL,"无处理指令-"+SNED+"----->"+GET);
        }
    }

    //售币机处理逻辑
    private void SbjControl(byte[] getCmd,byte[] sendCmd,String device) {
        String STAG = "CQ_SBJ";
        Log.i(STAG,device+"售币机--"+Common.ByteArrToHex(getCmd,true)+"---->"+Common.ByteArrToHex(sendCmd,true));
        String GET = Common.ByteArrToHex(getCmd,true);
        String SEND = Common.ByteArrToHex(sendCmd,true);

//        //清币
//        if (Common.Byte2Hex(sendCmd[2]).equals("50") &&Common.Byte2Hex(sendCmd[3]).equals("49")){
//            isClearCoin=true;
//        }//出币
//        else if (Common.Byte2Hex(sendCmd[2]).equals("51") &&Common.Byte2Hex(sendCmd[3]).equals("45")){
//
//            isClearCoin=false;
//        }

        if(CMDUtils.bd_is_ok_cmd(getCmd)){
            Log.i(STAG,"收到币斗OK指令---"+SEND+"----->"+GET);
            isOpenBD = true;
            int flag = Common.HexToInt(Common.Byte2Hex(getCmd[5]));
            if(flag == 3){
                OutError = true;
                //serialPortListener.onCoinOutFail(-1,-1,String.valueOf(flag));
                String[] vals = new String[]{"-1","-1",String.valueOf(flag)};
                sHandler.obtainMessage(onCoinOutFail,vals).sendToTarget();
            }
            else if (flag == 8){
                SerialPortSendData(CMDUtils.command_bd_ma(),device);
            }
            else{
                Log.i(STAG,"onMachieConnectedSuccess售币机");
                Itl_0x0a();
                sHandler.obtainMessage(onMachieConnectedSuccess,device).sendToTarget();
                //serialPortListener.onMachieConnectedSuccess(device);
            }
        }
        else if(CMDUtils.bd_is_out_coining(getCmd)){
            isReceiveCmd=true;
            Log.i(STAG,"收到出币命令---"+SEND+"----->"+GET);
            String sOut = Common.Byte2Hex(getCmd[6])+Common.Byte2Hex(getCmd[7])+Common.Byte2Hex(getCmd[8]);
            int count = Integer.parseInt(sOut,16);// Common.HexToInt(Common.Byte2Hex(getCmd[8]));
            // serialPortListener.onCoinOuting(count);
            sHandler.obtainMessage(onCoinOuting,count).sendToTarget();
        }
        else if(CMDUtils.bd_is_stop(getCmd) >= 0){
            isReceiveCmd=true;
            Log.e("停止命令:",Common.ByteArrToHex(getCmd,true)+"");
            Log.e("停止命令:",CMDUtils.bd_is_stop(getCmd)+"");
            SctualOut = CMDUtils.bd_is_stop(getCmd);
            Log.i(STAG,"收到出币停止命令,出币数"+String.valueOf(SctualOut)+"---"+SEND+"----->"+GET);

            if (isClearCoin){
                isClearCoin=false;
                SerialPortSendData(CMDUtils.command_bd_jw(),device);
                sHandler.obtainMessage(onCoinOutSuccess,SctualOut).sendToTarget();
            }
            else if(SctualOut < ShouldOut){
                SerialPortSendData(CMDUtils.command_bd_vu(),device);
            }

        }
        else if(CMDUtils.bd_is_out_end(getCmd)){
            isReceiveCmd=true;
            Log.i(STAG,"收到出币完成命令---"+SEND+"----->"+GET);
            //serialPortListener.onCoinOutSuccess(ShouldOut);
            sHandler.obtainMessage(onCoinOutSuccess,ShouldOut).sendToTarget();
            Itl_0x0a();
        }
        else if(CMDUtils.bd_is_out_error(getCmd)){
            isReceiveCmd=true;
            Log.e("故障命令:",Common.ByteArrToHex(getCmd,true)+"");
            Log.i(STAG,"收到出币故障命令---"+SEND+"----->"+GET);
            isSuccessOutCoin=false;
            if (isReceiveError){
                return;
            }
            isReceiveError=true;
            OutError = true;
            SerialPortSendData(CMDUtils.command_bd_jw(),device);
            int flag = Common.HexToInt(Common.Byte2Hex(getCmd[5]));
            String[] vals = new String[]{String.valueOf(SctualOut),String.valueOf(ShouldOut),String.valueOf(flag)};
            sHandler.obtainMessage(onCoinOutFail,vals).sendToTarget();
            //serialPortListener.onCoinOutFail(ShouldOut,SctualOut,String.valueOf(flag));
            sHandler.obtainMessage(onMachieCommectedFail,device).sendToTarget();

        }
        else {
            Log.i(STAG,"无处理---币斗指令---"+SEND+"----->"+GET);
        }
        //Log.i(TAG,"收到币斗指令---"+Common.ByteArrToHex(compCmd,true));
    }

    //itl在出完币或恢复故障后开启
    private void Itl_0x0a(){
        if(CMD_TAG.equals("0xEE")){
            CMD_TAG = "";
            SerialPortSendData(CMDUtils.command_itl_0a(),getDevice("2"));
        }
    }

    //发送指令队列
    private class SendQueueThread extends Thread {
        private Queue<SerialPortSendBean> queue = new LinkedList<SerialPortSendBean>();

        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                final SerialPortSendBean bean;
                while ((bean = queue.poll()) != null) {
                    try {
                        Thread.sleep(400);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    bean.serialPortManager.sendBytes(bean.bytes);
                    break;
                }
            }
        }
        public synchronized void AddQueue(SerialPortSendBean bean) {
            queue.add(bean);
        }
    }


    private MachineDevice queryMachineDevice(String device){
        MachineDevice device1 =null;
        for(MachineDevice machineDevice:machineDeviceList){
            if(machineDevice.getDevice().equals(device)){
                device1 = machineDevice;
                break;
            }
        }
        return device1;
    }

    final static class InternalHandler extends Handler{
        private  InternalHandler(){
            super(Looper.getMainLooper());
        }

        @Override
        @SuppressWarnings("unchecked")
        public void handleMessage(Message msg){

            switch (msg.what){
                case onCoinOuting :
                    serialPortListener.onCoinOuting((int)msg.obj);
                    break;
                case onCoinOutSuccess:
                    serialPortListener.onCoinOutSuccess((int)msg.obj);
                    break;
                case onCoinOutFail: {
                    String[] vals = (String[]) msg.obj;
                    serialPortListener.onCoinOutFail(Integer.parseInt(vals[0]), Integer.parseInt(vals[1]), vals[2]);
                    break;
                }
                case onReceivedMomey: {
                    String[] vals = (String[]) msg.obj;
                    serialPortListener.onReceivedMomey(Integer.parseInt(vals[0]), vals[1]);
                    break;
                }
                case onReceivedMomeySuccess:{
                    String[] vals = (String[]) msg.obj;
                    serialPortListener.onReceivedMomeySuccess(Integer.parseInt(vals[0]), vals[1]);
                    break;
                }
                case onReceivedMomeyFail:
                    serialPortListener.onReceivedMomeyFail((String) msg.obj);
                    break;
                case onSendCompleteData:
                    serialPortListener.onSendCompleteData((byte[]) msg.obj);
                    break;
                case onSerialPortOpenFail:
                    serialPortListener.onSerialPortOpenFail((File) msg.obj);
                    break;
                case onSerialPortOpenSuccess:
                    serialPortListener.onSerialPortOpenSuccess((File) msg.obj);
                    break;
                case onMachieConnectedSuccess:
                    serialPortListener.onMachieConnectedSuccess((String) msg.obj);
                    break;
                case onMachieCommectedFail:
                    serialPortListener.onMachieCommectedFail((String) msg.obj);
                    break;
            }
        }
    }
}

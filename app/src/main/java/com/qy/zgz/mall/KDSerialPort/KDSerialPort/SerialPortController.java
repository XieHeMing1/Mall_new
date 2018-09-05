package com.qy.zgz.mall.KDSerialPort.KDSerialPort;

import com.kedie.serialportlibrary.Device;
import com.qy.zgz.mall.KDSerialPort.DBPackageBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bill.T on 2018/4/26.
 */

public interface SerialPortController {

    /**
     *  发送出币命令
     * @param countCoin
     * @param device
     * @param type 0 时为选择左出币器售币，=1 时为选择双出币器售币，=2 时为选择右出币器售币
     */
    void bdSendOutCoin(int countCoin, String device, int type);

    /**
     *  币斗清理故障
     */
    void bdCleanError();

    /**
     *  继续出币
     */
    void bdContinueToCoin();

    /**
     *  暂停出币
     */
    void bdStopToCoin();

    /**
     *  结束本轮售币任务
     */
    void bdCoinOuted();

    /**
     *  获取设备对应的串口
     * @param kind
     */
    String getDevice(String kind);

    /**
     *  接收当前纸币
     */
    void sendGetMomeyCmd(String device);

    /**
     *  拒绝当前纸币
     */
    void sendOutMomeyCmd(String device);

    /**
     * 设备处理后的结果
     *
     * @param serialPortListener
     */
    void go(SerialPortListener serialPortListener);

    /**
     * 获取串口列表
     *
     * @return
     */
    ArrayList<Device> getDevices();

    /**
     * 清点币机所有币
     */
    void outAllCoin();


    /**
     * 获取出币正常状态
     *
     * @return
     */
    boolean getIsSuccessOutCoin();

    /**
     * 设置出币正常状态
     *
     * @return
     */
    void setIsSuccessOutCoin(boolean is);

    /**
     *  关闭纸币器
     */
    void colseBanknote();

    /**
     * 启用
     */
    void enableBanknote();

    /**
     *
     */
    void outCoinStop();

    /**
     * 暂停出币
     */
    void stopOutCoins();

    /**
     * 设置不识别哪几种纸币
     */
    void setItlInhibits(List<DBPackageBean> dbPackageBean);

    /**
     * 获取纸币器是否链接成功
     */
    Boolean isOpenZBJ();
}

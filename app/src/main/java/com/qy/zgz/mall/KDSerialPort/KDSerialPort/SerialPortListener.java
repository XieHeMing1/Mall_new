package com.qy.zgz.mall.KDSerialPort.KDSerialPort;

import java.io.File;

/**
 * Created by Bill.T on 2018/4/26.
 */

public interface SerialPortListener {
    /**
     * 币斗出币中
     *
     * @param count
     */
     void onCoinOuting(int count);

    /**
     * 币斗出币完成
     *
     * @param count
     */
    void onCoinOutSuccess(int count);

    /**
     * 币斗出币故障
     *
     * @param outCount 实出币数
     * @param count    应出币数
     */
    void onCoinOutFail(int outCount, int count, String errorCode);


    /**
     * 收到纸币
     *
     * @param amount  收到的纸币金额
     * @param macType 设备类型
     */
    void onReceivedMomey(int amount, String macType);

    /**
     * 收款成功
     *
     * @param amount  收到的纸币金额
     * @param macType 设备类型
     */
    void onReceivedMomeySuccess(int amount, String macType);

    /**
     * 收款失败
     *
     * @param macType
     */
    void onReceivedMomeyFail(String macType);

    /**
     * 发送到串口的数据
     *
     * @param bytes
     */
    void onSendCompleteData(byte[] bytes);

    /**
     * 串口开启失败事件
     */
    void onSerialPortOpenFail(File file);

    /**
     * 串口开启成功事件
     *
     * @param file
     */
    void onSerialPortOpenSuccess(File file);

    /**
     * 设备连接成功
     *
     * @param device
     */
    void onMachieConnectedSuccess(String device);

    /**
     * 设备连接失败
     * @param device
     */
    void onMachieCommectedFail(String device);
}

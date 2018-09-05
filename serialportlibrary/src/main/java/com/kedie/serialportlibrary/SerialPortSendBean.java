package com.kedie.serialportlibrary;

/**
 * Created by Bill.T on 2018/4/11.
 */

public class SerialPortSendBean {
    public SerialPortManager serialPortManager=null;
    public byte[] bytes=null;

    public SerialPortSendBean(SerialPortManager serialPortManager, byte[] bytes) {
        this.serialPortManager = serialPortManager;
        this.bytes = bytes;
    }

}

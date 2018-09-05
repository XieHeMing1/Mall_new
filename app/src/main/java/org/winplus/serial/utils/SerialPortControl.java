package org.winplus.serial.utils;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;

/**
 * 串口主控类 by hmx 2017/4/17
 */
public class SerialPortControl {
    private static SerialPortControl serialPortControl;
    public SerialPortFinder mSerialPortFinder = new SerialPortFinder();
    private SerialPort mSerialPort = null;
//    private static Context context;

    public static SerialPortControl getInstance() {
        if (serialPortControl == null) {
            serialPortControl = new SerialPortControl();
        }
        return serialPortControl;
    }

    public SerialPort getSerialPort() throws SecurityException, IOException, InvalidParameterException {
        if (mSerialPort == null) {
            mSerialPortFinder.getAllDevices();
            mSerialPortFinder.getAllDevicesPath();
                    /* Read serial port parameters */
//            SharedPreferences sp = context.getSharedPreferences("android_serialport_api.sample_preferences", 0);
//            String path = sp.getString("DEVICE", "");
            String path = "/dev/ttyS0";//串口位置
            int baudrate = 9600;//波特率
                    /* Check parameters */
            if ((path.length() == 0) || (baudrate == -1)) {
                throw new InvalidParameterException();
            }
                    /* Open the serial port */
            mSerialPort = new SerialPort(new File(path), baudrate, 0,8,1,'N');
        }
        return mSerialPort;
    }

    public void closeSerialPort() {
        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }
    }
}

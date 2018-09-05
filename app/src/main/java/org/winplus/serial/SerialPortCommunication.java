package org.winplus.serial;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import org.winplus.serial.utils.SerialPort;
import org.winplus.serial.utils.SerialPortControl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;

/**
 * Created by hmx on 2017/4/25.
 */

public class SerialPortCommunication {
    protected SerialPortControl serialPortControl;
    protected SerialPort mSerialPort;
    public OutputStream mOutputStream;
    public InputStream mInputStream;
    private ReadThread mReadThread;
    private Context context;
    private static SerialPortCommunication serialPortCommunication;
    private ReceivedCallback receivedCallback;


    /**
     * 数据回调接口
     */
    public interface ReceivedCallback {
        public void onReceived(byte[] buf);
    }

    /**
     * 数据发送接口
     * @return
     */
    public boolean send(byte[] buf){
        try {
            mOutputStream.write(buf);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    /**
     * 设置回调接口
     * @param receivedCallback
     */
    public void setReceivedCallback(ReceivedCallback receivedCallback){
        this.receivedCallback=receivedCallback;
    }

    public SerialPortCommunication(){
    }
    public SerialPortCommunication(Context context){
        this.context=context;
    }
    public static SerialPortCommunication getInstance(){
        if (serialPortCommunication==null){
            serialPortCommunication=new SerialPortCommunication();
        }
        return serialPortCommunication;
    }

    public static SerialPortCommunication getInstance(Context context){
        if (serialPortCommunication==null){
            serialPortCommunication=new SerialPortCommunication(context);
        }
        return serialPortCommunication;
    }

    /**
     * 串口输入读取线程
     */
    private class ReadThread extends Thread {

        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                int size;
                try {
                    byte[] buffer = new byte[10000];//数据接收缓冲区
                    if (mInputStream == null)
                        return;
                    size = mInputStream.read(buffer);//此处没有数据会堵塞
                    if (size > 0) {
                        if (receivedCallback!=null){
                            byte[] trueBuffer = new byte[size];//数据接收缓冲区
                            for (int i = 0; i < trueBuffer.length; i++) {
                                trueBuffer[i]=buffer[i];
                            }
                            receivedCallback.onReceived(trueBuffer);
//                            if(SystemUtil.verify(trueBuffer)){//数据有效性验证
//
//                            }
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void DisplayError(String str) {
        AlertDialog.Builder b = new AlertDialog.Builder(context);
        b.setTitle("Error");
        b.setMessage(str);
        b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ((Activity)context).finish();
            }
        });
        b.show();
    }


    public void InitSerialPort(){
        serialPortControl = SerialPortControl.getInstance();
        try {
            mSerialPort = serialPortControl.getSerialPort();
            mOutputStream = mSerialPort.getOutputStream();
            mInputStream = mSerialPort.getInputStream();
			/* Create a receiving thread */
            mReadThread = new ReadThread();
            mReadThread.start();
        } catch (SecurityException e) {
            DisplayError("你没有连续读/写权限端口");
        } catch (IOException e) {
            DisplayError("串行端口不能打开未知原因");
            e.printStackTrace();
        } catch (InvalidParameterException e) {
            DisplayError("请先配置串行端口");
        }
    }

    public void onDestroy() {
        if (mReadThread != null)
            mReadThread.interrupt();
        serialPortControl.closeSerialPort();
        mSerialPort = null;
    }

}

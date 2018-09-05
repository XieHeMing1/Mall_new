package org.winplus.serial;

import android.app.Activity;
import android.os.Bundle;

public abstract class SerialPortActivity extends Activity {
    private SerialPortCommunication serialPortCommunication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        serialPortCommunication = SerialPortCommunication.getInstance(this);
        serialPortCommunication.InitSerialPort();
    }

    @Override
    protected void onDestroy() {
        serialPortCommunication.onDestroy();
        super.onDestroy();
    }
}

package com.qy.zgz.mall.KDSerialPort.KDSerialPort;

import android.app.Application;
import android.content.Context;

import com.qy.zgz.mall.KDSerialPort.DBPackageBean;
import com.qy.zgz.mall.KDSerialPort.MachineDevice;

import java.lang.reflect.Method;
import java.util.List;


/**
 * Created by Bill.T on 2018/4/26.
 */

public final class kd {

    private kd(){}

    public static Application app() {
        if (Ext.app == null) {
            try {
                // 在IDE进行布局预览时使用
                Class<?> renderActionClass = Class.forName("com.android.layoutlib.bridge.impl.RenderAction");
                Method method = renderActionClass.getDeclaredMethod("getCurrentContext");
                Context context = (Context) method.invoke(null);
                Ext.app = new MockApplication(context);
            } catch (Throwable ignored) {
                throw new RuntimeException("please invoke x.Ext.init(app) on Application#onCreate()"
                        + " and register your Application in manifest.");
            }
        }
        return Ext.app;
    }


    public static class Ext{
        private static Application app;
        private static SerialPortController serialPortController;
        private static boolean ColseBanknote = false;//手动关闭纸币机

        private Ext(){}

        public static void init(Application app, List<MachineDevice> machineDeviceList, List<DBPackageBean> packageBeans){
            SerialPortControllerImpl.registerInstance(machineDeviceList,packageBeans);
            if(Ext.app == null){
                Ext.app = app;
            }
        }

        public static void setSerialPortController(SerialPortController serialPortController){
            if(Ext.serialPortController == null){
                Ext.serialPortController = serialPortController;
            }
        }

        public static boolean getColseBanknote(){
            return Ext.ColseBanknote;
        }

        public static void setColseBanknote(boolean flag){
            Ext.ColseBanknote = flag;
        }
    }

    public static SerialPortController sp(){
        return Ext.serialPortController;
    }

    private static class MockApplication extends Application {
        public MockApplication(Context baseContext) {
            this.attachBaseContext(baseContext);
        }
    }

}

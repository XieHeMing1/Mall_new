package com.qy.zgz.mall;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.qy.zgz.mall.KDSerialPort.DBPackageBean;
import com.qy.zgz.mall.KDSerialPort.KDSerialPort.kd;
import com.qy.zgz.mall.KDSerialPort.MachineDevice;
import com.qy.zgz.mall.lcb_game.MainControl;
import com.qy.zgz.mall.network.Constance;
import com.qy.zgz.mall.page.login.LoginActivity;
import com.qy.zgz.mall.utils.CrashHandler;
import com.qy.zgz.mall.utils.RootCmd;
import com.zhy.autolayout.config.AutoLayoutConifg;

import org.winplus.serial.SerialPortCommunication;
import org.xutils.DbManager;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 自定义application
 */
public class MyApplication extends Application implements MainControl.ReceiveHandle {

    private static MyApplication mInstance;

    List<Activity> acitvity_list = new ArrayList<Activity>();

    public DbManager db;

    private SerialPortCommunication serialPortCommunication;
    private MainControl mainControl;

    @Override
    public void onCreate() {
        super.onCreate();
        AutoLayoutConifg.getInstance().useDeviceSize();
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)
                .setBitmapsConfig(Bitmap.Config.ARGB_8888)
                .setDownsampleEnabled(true)
                .build();
        //Bugly.init(this,"e11bf9a25f",true);
        Fresco.initialize(this, config);
        mInstance = this;

        x.Ext.init(this);
        // 设置是否输出debug
        x.Ext.setDebug(true);

        List<MachineDevice> deviceList = new ArrayList<>();
        for (String[] dev : Constance.devices_array) {
            MachineDevice machineDevice = new MachineDevice();
            machineDevice.setDevice(dev[0]);
            machineDevice.setBaudrate(Integer.parseInt(dev[1]));
            machineDevice.setParity(Integer.parseInt(dev[2]));
            machineDevice.setDataBits(Integer.parseInt(dev[3]));
            machineDevice.setStopBit(Integer.parseInt(dev[4]));
            machineDevice.setMachineKind(Integer.parseInt(dev[5]));

            deviceList.add(machineDevice);
        }

        List<DBPackageBean> dbPackageBeansList = new ArrayList<>();
        int[] taocan = new int[]{1, 5, 10, 20, 50, 100};
        for (int db : taocan) {
            DBPackageBean dbPackageBean = new DBPackageBean();
            dbPackageBean.setPackageCoins(db);
            dbPackageBean.setPackagePrice(db);
            dbPackageBean.setPackageName(db + "币");
        }

        kd.Ext.init(this, deviceList, dbPackageBeansList);

        Logger.addLogAdapter(new AndroidLogAdapter());

        //数据库配置
        DbManager.DaoConfig daoConfig = new DbManager.DaoConfig()
                // 数据库的名字
                .setDbName("kmd")
                // 保存到指定路径
                .setDbDir(new
                        File(Environment.getExternalStorageDirectory().getPath() + "/hyppmm/"))
//                .setDbOpenListener(new DbManager.DbOpenListener() {
//                 @Override
//                    public void onDbOpened(DbManager db) {
//                        // 开启WAL, 对写入加速提升巨大
//                        db.getDatabase().enableWriteAheadLogging();
//                    }
//                })
                // 数据库的版本号
                .setDbVersion(1);
        //对于6.0级以上的系统，这里会报错，因为Application先于权限的获取
        //TODO 把数据库初始化放置其他地方
        db = x.getDb(daoConfig);

        //Crash处理
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);

        //修改权限
        if (RootCmd.haveRoot()) {
            if (0 == RootCmd.execRootCmdSilent("chmod -R 777 /dev/bus/usb")) {
            }
        }

        //TODO 柜子机初始化
        try {
            serialPortCommunication = SerialPortCommunication.getInstance(this);
            serialPortCommunication.InitSerialPort();
            mainControl = MainControl.getControl();
            mainControl.setReceiveHandle(this);
            mainControl.ID();
        }catch (Exception e) {
            Log.i("MyApplication_Test", "柜子机初始化 捕获异常");
        }


    }

    public static MyApplication getInstance() {
        return mInstance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // 安装tinker
//        Beta.installTinker();
    }


    /**
     * Activity关闭时，删除Activity列表中的Activity对象
     */
    public void removeActivity(Activity a) {
        acitvity_list.remove(a);
    }

    /**
     * 向Activity列表中添加Activity对象
     */
    public void addActivity(Activity a) {
        acitvity_list.add(a);
    }

    /**
     * 关闭Activity列表中的所有Activity
     */
    public void finishActivity() {
        for (Activity activity : acitvity_list) {
            if (null != activity) {
                activity.finish();
            }
        }
        //杀死该应用进程
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    //重启APP
    public void restartApp() {
        //启动页
        Intent intent = new Intent(mInstance, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mInstance.startActivity(intent);
        finishActivity();
    }

    @Override
    public void process(byte[] data) {

    }
}

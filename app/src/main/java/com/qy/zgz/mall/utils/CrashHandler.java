package com.qy.zgz.mall.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    public static final String TAG = "CrashHandler";
    private static CrashHandler INSTANCE = new CrashHandler();
    private Context mContext;

    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    public void init(Context context) {
        mContext = context;
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {

        ex.printStackTrace();
        //获取完整的信息
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ex.printStackTrace(new PrintStream(baos));
        String exception = baos.toString();
        writeTofile(exception);

        //重新启动
//        new Thread() {
//                @Override
//                public void run() {
//                    try {
//                        Thread.sleep(2000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    Intent intent = new Intent(mContext, MainActivity.class);
//                    @SuppressLint("WrongConstant")
//                    PendingIntent restartIntent = PendingIntent.getActivity(mContext, 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
//                    AlarmManager mgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
//                    mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent);
//                    android.os.Process.killProcess(android.os.Process.myPid());
//                }
//            }.start();
    }

    //将异常信息写到日志中去
    public void writeTofile(String message)
    {
        FileOutputStream fos=null;
        try {
             String publicDir = Environment.getExternalStorageDirectory().getPath();
            fos=new FileOutputStream(publicDir+"/hyppmmcrash.txt",true);
            fos.write((message+"\r\n").getBytes(),0,message.getBytes().length);
            String data=DateUtils.getDateToString(System.currentTimeMillis(),"yyyy-MM-dd HH:mm:ss");
            fos.write((data).getBytes(),0,data.getBytes().length);
        } catch (Exception e) {
            Log.e("crash",e.getMessage());
            e.printStackTrace();
        }
        finally {
            if (fos!=null)
            {
                try {
                    fos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
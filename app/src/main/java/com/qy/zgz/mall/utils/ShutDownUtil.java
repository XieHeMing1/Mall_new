package com.qy.zgz.mall.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * 系统关机 需要ROOT权限
 * Created by ZYB on 2018/5/16 0016.
 */

public class ShutDownUtil {


    /**
     * 用法
     *  ShutDownUtil.createSuProcess("reboot -p").waitFor()
     */
    static Process createSuProcess() throws IOException {
        File rootUser = new File("/system/xbin/ru");
        if(rootUser.exists()) {
            return Runtime.getRuntime().exec(rootUser.getAbsolutePath());
        } else {
            return Runtime.getRuntime().exec("su");
        }
    }

    public static Process createSuProcess(String cmd) throws IOException {

        DataOutputStream os = null;
        Process process = createSuProcess();

        try {
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit $?\n");
        } finally {
            if(os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                }
            }
        }

        return process;
    }

    public static int shutdown() {
        int r = 0;
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"su" , "-c" ,"reboot -p"});
            r = process.waitFor();
            java.lang.System.out.println("r:" + r );
        } catch (IOException e) {
            e.printStackTrace();
            r = -1;
        } catch (InterruptedException e) {
            e.printStackTrace();
            r = -1;
        }
        return r;
    }


    public static int reboot() {
        int r = 0;
        try {
            Process process = Runtime.getRuntime().exec("su -c reboot");
            r = process.waitFor();
            java.lang.System.out.println("r:" + r );
        } catch (IOException e) {
            e.printStackTrace();
            r = -1;
        } catch (InterruptedException e) {
            e.printStackTrace();
            r = -1;
        }
        return r;
    }


    public static void exitShutdown(Activity activity) {
        int currentVersion = android.os.Build.VERSION.SDK_INT;
        if (currentVersion > android.os.Build.VERSION_CODES.ECLAIR_MR1) {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(startMain);
            System.exit(0);
        } else {// android2.1
            ActivityManager am = (ActivityManager) activity.getSystemService(ACTIVITY_SERVICE);
            am.restartPackage(activity.getPackageName());
        }
    }

}

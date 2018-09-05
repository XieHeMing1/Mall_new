package com.qy.zgz.mall.vbar;

import android.app.Activity;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;

import com.qy.zgz.mall.utils.RootCmd;

/**
 * Created by LCB on 2018/3/16.
 */

public class VbarUtils {
    private static VbarUtils vbarUtils;
    private static Vbar vbar;
    private String result="";
    private boolean isScan=false;
    private static Handler handler;
//    private static TextView scan_result;
    private static Activity activity;
    private static HandlerThread handlerThread;
    private  ScanResultExecListener scanResultExecListener;
    public static VbarUtils getInstance(Activity aty){
        activity=aty;
//        scan_result=new TextView(aty);
        if (vbarUtils==null){
            vbarUtils=new VbarUtils();
        }
        if (vbar==null){
            vbar=new Vbar();
        }

        if (handlerThread==null){
            handlerThread=new HandlerThread("scan_thread");
        }

        if (!handlerThread.isAlive()) {
            handlerThread.start();
        }

        if (handler==null){
            handler=new Handler(handlerThread.getLooper()){
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                }
            };
        }

        return vbarUtils;
    }

    /**
     * 打开扫描器
     * @return
     */
    public  boolean openDevied(){
        //修改权限
        if (RootCmd.haveRoot()) {
            if (0==RootCmd.execRootCmdSilent("chmod -R 777 /dev/bus/usb")){
                Log.e("cmd","s");
                return vbar.vbarOpen();
            }else{
                Log.e("cmd","fauile"+RootCmd.execRootCmdSilent("chmod -R 777 /dev/bus/usb"));
            }
        }

        return false;



    }

    /**
     * 关闭扫描器
     * @return
     */
    public  void closeDevied(){
        result="";
        isScan=false;
         vbar.closeDev();
    }



   private Runnable scan_runable= new Runnable() {
        @Override
        public void run() {
            synchronized (this) {
//                scan_result.setText("");
                result = "";
                isScan = true;
                while (isScan) {
                    result = vbar.getResultsingle();
                    if (!TextUtils.isEmpty(result)) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                scanResultExecListener.scanResultExec(result);
//                                scan_result.setText(result);
                            }
                        });
                        isScan = false;
                    }
                    try {
                        Thread.sleep(0);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    /**
     * 获取扫描结果
     * @return
     */
    public  void getScanResult() {
        synchronized (this) {
//            scan_result.removeTextChangedListener(textWatcher);
//            scan_result.addTextChangedListener(textWatcher);
            if (openDevied()) {
                handler.removeCallbacks(scan_runable);
                handler.post(scan_runable);
            } else {
                Log.e("failure", "failure");

            }
        }
    }

    //扫码结果监听
    private  TextWatcher textWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (scanResultExecListener!=null&&!TextUtils.isEmpty(s.toString())){
            scanResultExecListener.scanResultExec(s.toString());
            }
        }
    };


    /**
     * 获取扫码后执行接口
     */
    public interface ScanResultExecListener{
        //获取扫码后执行
        public void scanResultExec(String result);
    }

    /**
     * 设置监听接口
     */
    public VbarUtils setScanResultExecListener(ScanResultExecListener stel){

        scanResultExecListener=stel;
        return vbarUtils;
    }

    /**
     * 停止扫描
     */
    public void stopScan(){
        handler.removeCallbacks(scan_runable);
    }

}

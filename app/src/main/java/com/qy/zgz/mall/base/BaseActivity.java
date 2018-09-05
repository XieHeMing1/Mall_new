package com.qy.zgz.mall.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.qy.zgz.mall.MyApplication;
import com.qy.zgz.mall.R;
import com.zhy.autolayout.utils.AutoUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.Map;


/**
 * Created by Administrator on 2016/8/15 0015.
 */
public abstract class BaseActivity extends Activity implements OnClickListener {

    protected MyApplication myApplication;
    protected Context mContext = this;
    private Toast toast;
    //判断activity是否已经销毁
    private boolean isDestroy = false;


    //加载框
    protected ProgressDialog progressDialog;


    private  Message message= null;

    //数据传输
    private  Bundle bundle = new Bundle();

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mContext = this;
        myApplication = (MyApplication) getApplication();
        // 去除头部
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//强制竖屏
        x.view().inject(this);
        toast = Toast.makeText(mContext, "", Toast.LENGTH_SHORT);//初始化一个toast，解决多次弹出toast冲突问题
        MyApplication.getInstance().addActivity(this);
        //初始化
        init(savedInstanceState);

    }

    /**
     * @param savedInstanceState
     * @return void
     * @description: 主要用于控件等初始化工作
     */
    protected abstract void init(Bundle savedInstanceState);


    /**
     * 重写setContentView，让子类传入的View上方再覆盖一层LoadingView
     */
    @Override
    public void setContentView(int layoutResID) {
        View view = LayoutInflater.from(mContext).inflate(layoutResID, null);
        super.setContentView(view);
    }

    /**
     * 居中提示框
     */
    public void CToast(String message) {
        synchronized (mContext) {
            toast.cancel();
            toast = Toast.makeText(mContext, message, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            //设置TOAST的字体大小
            LinearLayout layout = (LinearLayout) toast.getView();
//            layout.setBackgroundColor(Color.parseColor("#FFFFFF"));
            TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
//            v.setTextColor(Color.BLACK);
            v.setTextSize(AutoUtils.getPercentHeightSize(80));
            toast.show();
        }
    }

    /**
     * 底部提示框
     */
    public void SToast(String message) {
        synchronized (mContext) {
            toast.cancel();
            toast = Toast.makeText(mContext, message, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    /**
     * 底部提示框
     */
    public void SToast(int id) {
        synchronized (mContext) {
            toast.cancel();
            toast = Toast.makeText(mContext, getResources().getString(id), Toast.LENGTH_SHORT);
            toast.show();
            //    Toast.makeText(mContext, getResources().getString(id), Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 自定义布局提示框
     */
    public void SToast(View view) {
        synchronized (mContext) {
            toast.cancel();
            LinearLayout.LayoutParams patams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            Toast toast = new Toast(mContext);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.setDuration(Toast.LENGTH_LONG);
            LinearLayout linearLayout = new LinearLayout(mContext);
            linearLayout.setLayoutParams(patams);
            linearLayout.setBackgroundResource(R.color.transparent);
            linearLayout.addView(view);
            toast.setView(linearLayout);
            toast.show();
        }
    }

    /**
     * @param message
     * 显示加载框
     */
    public void showProgressDialog(String message){
        if (null==progressDialog){
            progressDialog=new ProgressDialog(mContext);
        }
        progressDialog.setMessage(message);
        progressDialog.show();
        //设置是否按返回键dismiss
        progressDialog.setCancelable(false);
    }

    /**
     * 隐藏加载框
     */
    public void dismissProgressDialog(){
        if (progressDialog!=null&&progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

    /**
     * @param flag
     * 设置是否按返回键来隐藏加载框
     */
    public void setDialogCancelable(Boolean flag) {
        if (progressDialog != null) {
            progressDialog.setCancelable(flag);
        }
    }

    /**
     * @param message 提示内容
     * @param cancel  取消的what
     * @param ok      确定的what
     */
//    public void getMyDialog(String message, final int cancel, final int ok) {
//        new AlertDialogUtils(mContext).builder()
//                .setTitle("提示")
//                .setMsg(message)
//                .setPositiveButton("确认", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        handler.sendEmptyMessage(ok);
//                    }
//                })
//                .setNegativeButton("取消", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        handler.sendEmptyMessage(cancel);
//                    }
//                }).show();
//
//
//    }


    /**
     * POST请求
     */
    public  void httpPostMap(String url, Map<String,Object> map, final int what) {
        showProgressDialog("请稍后...");
        RequestParams params = new RequestParams(url);
        params.setAsJsonContent(true);
        params.addHeader("Content-Type","application/json");
        Gson gson= new Gson();
        String req_Data=gson.toJson(map);
        params.setBodyContent(req_Data);
        params.addHeader("charset","utf-8");
        params.setConnectTimeout(10000);
        x.http().post(params, new Callback.CommonCallback<String>(){
            @Override
            public void onSuccess(String result) {
                JSONObject jresult= null;
                try {
                    jresult = new JSONObject(result);
                    if (jresult.get("code").toString().equals("0")) {
                        if (jresult.has("data")){
                            bundle.putString("data", jresult.get("data").toString());}
                        else {bundle.putString("data", "");}

                        bundle.putInt("what", what);
                        bundle.putString("allresult", jresult.toString());
                        message = Message.obtain();
                        message.setData(bundle);
                        handler.sendMessage(message);
                    }else{
                        SToast(jresult.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                dismissProgressDialog();
            }
        });
    }


    /**
     * Handler 处理事件 请求网络失败返回处理
     */
    protected abstract void ObjectMessage(Bundle msg);


    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (isDestroy) {
                return;
            }
            ObjectMessage(msg.getData());

            super.handleMessage(msg);

        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.getInstance().removeActivity(this);
        isDestroy = true;
    }










    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            dismissLoadingDialog();
            dismissProgressDialog();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 提供给titbar 的back 放回事件
     */
    public void backfinish(View view) {
        unkeyboard();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }


    /**
     * 隐藏键盘
     */
    public void unkeyboard() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager.isActive()) {
                inputMethodManager.hideSoftInputFromWindow(
                        ((Activity) mContext).getCurrentFocus()
                                .getWindowToken(), 0);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}

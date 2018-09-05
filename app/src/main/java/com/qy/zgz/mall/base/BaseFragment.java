package com.qy.zgz.mall.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.zhy.autolayout.utils.AutoUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;


/**
 * Created by Administrator on 2016/8/23 0023.
 */
public abstract  class BaseFragment extends Fragment implements View.OnClickListener {

    private static final int HTTP_ERROR = 999;
    private InputMethodManager inputMethodManager;
    private Context mContext;
    protected View baseView;

    protected KProgressHUD dia= null;


    private Message message= null;

    //数据传输
    private Bundle bundle = new Bundle();

    private Toast toast;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       baseView= x.view().inject(this,inflater,container);
        mContext=getActivity();
        toast=Toast.makeText(mContext,"",Toast.LENGTH_SHORT);
        init();
        return baseView;
    }


    /**
     * 初始化
     */
    protected abstract void init();


    /**
     * 隐藏键盘
     */
    public void unkeyboard() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager.isActive()) {
                inputMethodManager.hideSoftInputFromWindow(
                        ((Activity) mContext).getCurrentFocus()
                                .getWindowToken(), 0);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    /**
     * 重新加载。留给子类类重写，可重写，可不重写，提供统一的名字的重新加载设置界面的方法
     */
    public void reinit() {
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 提示框
     */
    public void SToast(String message) {
        synchronized (getActivity()) {
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }
    }

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            ObjectMessage(msg);
            //     dismissLoadingDialog();
            super.handleMessage(msg);

        }
    };

    /**
     * Handler 处理事件 HTTP_ERROR：999 请求网络失败返回处理
     */
    protected abstract void ObjectMessage(Message msg);


    /**
     * POST请求
     */
    public  void httpPostJson(String url, String jsonData, final int what) {
        dia = KProgressHUD.create(mContext).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("请稍后...").show();
        RequestParams params = new RequestParams(url);
        params.setAsJsonContent(true);
        params.addHeader("Content-Type","application/json");
        params.setBodyContent(jsonData);
        params.addHeader("charset","utf-8");
        params.setConnectTimeout(10000);
        x.http().post(params, new Callback.CommonCallback<String>(){
            @Override
            public void onSuccess(String result) {
                Gson gson= new Gson();
                JSONObject jresult=gson.fromJson(result, JSONObject.class);
                try {
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
                dismissDia();
            }
        });
    }


    //取消加载框
    protected void dismissDia()
    {
        try {
            if (dia!=null && dia.isShowing())
            {
                dia.dismiss();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
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


}

package com.qy.zgz.mall.dialogfragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.google.gson.JsonObject;
import com.qy.zgz.mall.Model.MemberInfo;
import com.qy.zgz.mall.R;
import com.qy.zgz.mall.network.Constance;
import com.qy.zgz.mall.network.NetworkCallback;
import com.qy.zgz.mall.network.NetworkRequest;
import com.qy.zgz.mall.network.XutilsCallback;
import com.qy.zgz.mall.page.index.PurchaseCoinActivity;
import com.qy.zgz.mall.utils.GsonUtil;
import com.qy.zgz.mall.utils.HttpUtils;
import com.qy.zgz.mall.utils.QRBitmapUtils;
import com.qy.zgz.mall.utils.SharePerferenceUtil;
import com.qy.zgz.mall.utils.SignParamUtil;
import com.qy.zgz.mall.widget.TisDialog;

import java.lang.ref.WeakReference;
import java.util.HashMap;

public class LoginDialogFragment extends android.support.v4.app.DialogFragment implements View.OnClickListener{

    private static final String TAG = "LoginDialogFragment";
    ImageView mIvQRCode;
    ImageView mIvRefresh;

    private String wx_qrcode;
    PurchaseCoinActivity mActivity;

    LoginDialogListener mListener;
    private LoginDialogHandler mHandler;

    public interface LoginDialogListener {
        public void onClickListener();
        public void onDismissListener();
    }

    public void setLoginDialogListener(LoginDialogListener listener) {
        mListener = listener;
    }

    private static LoginDialogFragment instance;

    public static LoginDialogFragment newInstance() {
        if (instance == null) {
            instance = new LoginDialogFragment();
        }
        return instance;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (PurchaseCoinActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.fragment_dialog_menber_center, container, false);
        mIvQRCode = view.findViewById(R.id.iv_menber_center_qrcode);
        mIvRefresh = view.findViewById(R.id.iv_menber_center_refresh_qrcode);
        mIvRefresh.setOnClickListener(this);
        mHandler = new LoginDialogHandler(this);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setBackgroundDrawableResource(android.R.color.transparent);
            }
            DisplayMetrics dm = new DisplayMetrics();
            mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
            dialog.getWindow().setLayout((int) (dm.widthPixels * 0.8), (int) (dm.heightPixels * 0.6));

            try {
                mIvQRCode.setImageBitmap(QRBitmapUtils.createQRCode(PurchaseCoinActivity.wx_qrcode, 450));
            } catch (Exception e) {
                Log.i(TAG, "CreateScanCode Exception = " + e.toString());
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        Log.i(TAG, "Dialog onCancel");

    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        Log.i(TAG, "Dialog onDismiss");
        if(mListener != null) {
            Log.i(TAG, "Dialog onDismissListener");
            mListener.onDismissListener();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Dialog onDestroy");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_menber_center_refresh_qrcode:
                CreateScanCode(SharePerferenceUtil.getInstance().getValue(Constance.MachineID, "").toString());
                if(mListener != null) {
                    mListener.onClickListener();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 生成授权微信登陆二维码
     */
    public void CreateScanCode(String MachineID) {
        if (TextUtils.isEmpty(MachineID)) {
            return;
        }
        HashMap<String, String> hashmap = new HashMap<String, String>();
        hashmap.put("MachineID", MachineID);
        hashmap.put("MenuName", "扫码登录");
        hashmap.put("sign", SignParamUtil.getSignStr(hashmap));
        HttpUtils.xPostJson(Constance.MEMBER_HOST + Constance.CreateScanCode, hashmap, new XutilsCallback<String>() {
            @Override
            public void onSuccessData(String result) {
                JsonObject jsonResult = GsonUtil.Companion.jsonToObject(result, JsonObject.class);
                if (jsonResult.has("return_Code") &&
                        jsonResult.get("return_Code").toString().equals("200")) {
                    wx_qrcode = jsonResult.get("Data").getAsString();
                    if (!TextUtils.isEmpty(wx_qrcode)) {
                        try {
                            mIvQRCode.setImageBitmap(QRBitmapUtils.createQRCode(wx_qrcode, 450));
                        } catch (Exception e) {
                            Log.i(TAG, "CreateScanCode Exception = " + e.toString());
                        }

                    }
                    Log.i(TAG, "wx_qrcode result = " + wx_qrcode);
                    String TmpGuid = jsonResult.get("Data2").getAsString();
                    mHandler.removeCallbacksAndMessages(null);
                    //开始循环接口，查看是否登录
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (TextUtils.isEmpty(SharePerferenceUtil.getInstance().getValue(Constance.member_Info, "").toString())) {
                                authorizedLogin(TmpGuid);
                            }
                        }

                    });

                    //刷新会员界面
//                    manage.beginTransaction().replace(R.id.main_fragment_content,new MemberCenterFragment()).commitAllowingStateLoss();


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

            }
        });
    }


    /**
     * 微信授权扫码登录
     */
    private void authorizedLogin(String TmpGuid) {
        if (!TextUtils.isEmpty(SharePerferenceUtil.getInstance().getValue(Constance.member_Info, "").toString())) {
            return;
        }
        HashMap<String, String> hashmap = new HashMap<String, String>();
        hashmap.put("TempGuid", TmpGuid);
        hashmap.put("sign", SignParamUtil.getSignStr(hashmap));

        HttpUtils.xPostJson(Constance.MEMBER_HOST + Constance.GetCustomerScanData, hashmap, new XutilsCallback<String>() {
            @Override
            public void onSuccessData(String result) {
                JsonObject jsonResult = GsonUtil.Companion.jsonToObject(result, JsonObject.class);
                if (jsonResult.has("return_Code") &&
                        jsonResult.get("return_Code").toString().equals("200") &&
                        jsonResult.getAsJsonObject("Data").get("Status").toString().equals("0")) {
                    wx_qrcode = "";
                    JsonObject data = jsonResult.getAsJsonObject("Data");
                    //临时保存会员信息
                    SharePerferenceUtil.getInstance()
                            .setValue(Constance.member_Info, data.toString());

                    String Wid = data.get("WechatID").getAsString();
                    String Bid = SharePerferenceUtil.getInstance().getValue(Constance.BranchID, "").toString();
                    String Vpn = SharePerferenceUtil.getInstance().getValue(Constance.Vpn, "").toString();

                    //显示登录信息
                    mActivity.showLoginInfo();
//                    instance.dismiss();
                    if(mListener != null) {
                        mListener.onClickListener();
                    }
                    //登录商城
//                    userLogin(Wid,Bid,Vpn);
                    if (!TextUtils.isEmpty(Bid)
                            && !TextUtils.isEmpty(Vpn)) {
                        //执行商城会员登录
                        userLogin(Wid, Bid, Vpn);
                    }

                    //登录提示
                    new TisDialog(mActivity).create()
                            .setMessage("登录成功!").show();


                } else {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            authorizedLogin(TmpGuid);
                        }

                    }, 2500);

                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        authorizedLogin(TmpGuid);
                    }

                }, 2500);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private static class LoginDialogHandler extends Handler {
        private WeakReference<LoginDialogFragment> mFragmentWeakReference;

        public LoginDialogHandler(LoginDialogFragment fragment) {
            mFragmentWeakReference = new WeakReference<LoginDialogFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            LoginDialogFragment dialog = mFragmentWeakReference.get();
            if (dialog != null && dialog.isVisible()) {
                // 可见的状态去处理信息应该没有问题

            }
        }
    }

    /**
     * 商城会员登录接口
     */
    private void userLogin(String wxopen_id, String branch_id, String vpn) {
        String MacineId = SharePerferenceUtil.getInstance().getValue(Constance.MachineID, "").toString();
        String Bname = SharePerferenceUtil.getInstance().getValue(Constance.BranchName, "").toString();
        if (TextUtils.isEmpty(MacineId) || TextUtils.isEmpty(Bname)) {
            return;
        }
        HashMap<String, String> map = new HashMap();
//        map.put("open_id","o4hYLwyyF2D0NDjO4aoSjvI47lL8");
//        map.put("branch_id","c5d96d6b-c8ae-48a0-a8a3-ad88edbcc2ab");
//        map.put("child_url","12341");
//        map.put("deviceid","1341234123");
//        map.put("branch_name","1234123");
        map.put("open_id", wxopen_id);
        map.put("branch_id", branch_id);
        map.put("child_url", vpn);
        map.put("deviceid", MacineId);
        map.put("branch_name", Bname);

        MemberInfo memberInfo = GsonUtil.Companion.jsonToObject(SharePerferenceUtil.getInstance().getValue(Constance.member_Info, "").toString(), MemberInfo.class);

        if (null != memberInfo) {
            map.put("cust_id", memberInfo.getId());
            map.put("mobile", memberInfo.getPhone());
        } else {
            map.put("cust_id", "");
            map.put("mobile", "");
        }

        NetworkRequest.getInstance().userLogin(map, new NetworkCallback<JsonObject>() {

            @Override
            public void onSuccess(JsonObject data) {
                SharePerferenceUtil.getInstance().setValue(Constance.user_accessToken, data.get("accessToken").getAsString());
                SharePerferenceUtil.getInstance().setValue(Constance.shop_id, data.get("shop_id").getAsString());

            }

            @Override
            public void onFailure(int code, String msg) {

            }
        });
    }

}

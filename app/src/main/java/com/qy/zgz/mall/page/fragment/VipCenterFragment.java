package com.qy.zgz.mall.page.fragment;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.gson.JsonObject;
import com.qy.zgz.mall.BaseFragment;
import com.qy.zgz.mall.Model.MemberInfo;
import com.qy.zgz.mall.R;
import com.qy.zgz.mall.network.Constance;
import com.qy.zgz.mall.network.NetworkCallback;
import com.qy.zgz.mall.network.NetworkRequest;
import com.qy.zgz.mall.network.XutilsCallback;
import com.qy.zgz.mall.page.index.VIPCenterActivity;
import com.qy.zgz.mall.utils.GsonUtil;
import com.qy.zgz.mall.utils.HttpUtils;
import com.qy.zgz.mall.utils.QRBitmapUtils;
import com.qy.zgz.mall.utils.SharePerferenceUtil;
import com.qy.zgz.mall.utils.SignParamUtil;
import com.qy.zgz.mall.widget.TisDialog;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;

public class VipCenterFragment extends BaseFragment {
    @BindView(R.id.iv_menber_center_qrcode)
    ImageView mIvQRCode;
    @BindView(R.id.iv_menber_center_refresh_qrcode)
    ImageView mIvRefresh;

    VIPCenterActivity mActivity;
    String mStrQRCode;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (VIPCenterActivity) context;
    }

    @Override
    public View getLayoutView(LayoutInflater inflater, @Nullable ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_menber_center, container, false);
        return view;
    }

    @Override
    public void initViews(View view) {
        Log.i("VIPFragment", "initViews ");
        CreateScanCode(SharePerferenceUtil.getInstance().getValue(Constance.MachineID, "").toString());

    }

    @OnClick({R.id.iv_menber_center_refresh_qrcode})
    public void onClick(View view){
        switch (view.getId()) {
            case R.id.iv_menber_center_refresh_qrcode:
                CreateScanCode(SharePerferenceUtil.getInstance().getValue(Constance.MachineID, "").toString());
                break;
        }
    }

    /**
     * 生成授权微信登陆二维码
     */
    public void CreateScanCode(String MachineID) {
        Log.i("VIPFragment", "CreateScanCode MachineID = " + MachineID);
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
                    Log.i("VIPFragment", "CreateScanCode result = " + result);
                    mStrQRCode = jsonResult.get("Data").getAsString();
                    if(!TextUtils.isEmpty(mStrQRCode)) {
                        try {
                            mIvQRCode.setImageBitmap(QRBitmapUtils.createQRCode(mStrQRCode, 450));
                        } catch (Exception e) {

                        }
                    }
                    Log.i("VIPFragment", "mStrQRCode = " + mStrQRCode);
                    String TmpGuid = jsonResult.get("Data2").getAsString();
                    mBaseFragmentHandler.removeCallbacksAndMessages(null);
                    //开始循环接口，查看是否登录
                    mBaseFragmentHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (TextUtils.isEmpty(SharePerferenceUtil.getInstance().getValue(Constance.member_Info, "").toString())) {
                                authorizedLogin(TmpGuid);
                            }
                        }

                    });
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
                    mStrQRCode = "";
                    JsonObject data = jsonResult.getAsJsonObject("Data");
                    //临时保存会员信息
                    SharePerferenceUtil.getInstance()
                            .setValue(Constance.member_Info, data.toString());

                    String Wid = data.get("WechatID").getAsString();
                    String Bid = SharePerferenceUtil.getInstance().getValue(Constance.BranchID, "").toString();
                    String Vpn = SharePerferenceUtil.getInstance().getValue(Constance.Vpn, "").toString();


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

//                    //显示登录信息
                    mActivity.showLoginInfo();
                } else {
                    mBaseFragmentHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            authorizedLogin(TmpGuid);
                        }

                    }, 2500);

                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                mBaseFragmentHandler.postDelayed(new Runnable() {
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

        MemberInfo memberInfo = GsonUtil.Companion.jsonToObject(SharePerferenceUtil.getInstance()
                .getValue(Constance.member_Info, "").toString(), MemberInfo.class);

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
                String token = SharePerferenceUtil.getInstance().getValue(Constance.user_accessToken, "").toString();
            }

            @Override
            public void onFailure(int code, String msg) {

            }
        });
    }
}

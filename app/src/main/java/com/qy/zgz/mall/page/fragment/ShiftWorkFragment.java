package com.qy.zgz.mall.page.fragment;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.qy.zgz.mall.BaseFragment;
import com.qy.zgz.mall.Dbsql.DBDao;
import com.qy.zgz.mall.Dbsql.DBReceiveMoneyRecord;
import com.qy.zgz.mall.KDSerialPort.KDSerialPort.SerialPortListener;
import com.qy.zgz.mall.KDSerialPort.KDSerialPort.kd;
import com.qy.zgz.mall.Model.LiPay;
import com.qy.zgz.mall.Model.MemberInfo;
import com.qy.zgz.mall.R;
import com.qy.zgz.mall.network.Constance;
import com.qy.zgz.mall.network.XutilsCallback;
import com.qy.zgz.mall.page.index.PurchaseCoinActivity;
import com.qy.zgz.mall.utils.GsonUtil;
import com.qy.zgz.mall.utils.HttpUtils;
import com.qy.zgz.mall.utils.SharePerferenceUtil;
import com.qy.zgz.mall.utils.SignParamUtil;
import com.qy.zgz.mall.utils.Utils;
import com.qy.zgz.mall.widget.TisDialog;

import org.xutils.common.Callback;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.OnClick;

public class ShiftWorkFragment extends BaseFragment implements SerialPortListener {
    @BindView(R.id.tv_shift_sequence)
    TextView mTvShiftSequence;
    @BindView(R.id.tv_shift_time)
    TextView mTvShiftTime;
    @BindView(R.id.tv_shift_order_state)
    TextView mTvOrderState;
    //初始币数
    @BindView(R.id.tv_shift_original_coin)
    TextView mTvOriginalCoin;
    //结算币数
    @BindView(R.id.tv_shift_final_coin)
    TextView mTvFinalCoin;
    //盘点币数
    @BindView(R.id.tv_shift_check_coin)
    TextView mTvCheckCoin;
    @BindView(R.id.tv_shift_sale_cash)
    TextView mTvSaleCash;
    @BindView(R.id.tv_shift_money_difference)
    TextView mTvMoneyDifference;
    @BindView(R.id.tv_shift_real_wx_account)
    TextView mTvWxAccount;
    @BindView(R.id.tv_shift_increase_coin)
    TextView mTvIncreaseCoin;
    @BindView(R.id.tv_shift_export_coin)
    TextView mTvExportCoin;
    @BindView(R.id.tv_shift_different_coin)
    TextView mTvDifferentCoin;
    //实收金额
    @BindView(R.id.tv_shift_real_receive_cash)
    TextView mTvReceiveCash;
    @BindView(R.id.tv_shift_real_receive_deposit)
    TextView mTvReceiveDeposit;
    @BindView(R.id.tv_shift_real_alipay_account)
    TextView mTvAlipayAccount;
    @BindView(R.id.btn_shift_clear_coins)
    Button mBtnClearCoins;
    @BindView(R.id.btn_shift_work)
    Button mBtnShiftWork;

    private static ShiftWorkFragment instance;
    PurchaseCoinActivity mActivity;

    private String ClearNum = "0";
    private String ClearID = "";

    public static ShiftWorkFragment newInstance() {
        if (instance == null) {
            instance = new ShiftWorkFragment();
        }
        return instance;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (PurchaseCoinActivity) context;
    }

    @Override
    public View getLayoutView(LayoutInflater inflater, @Nullable ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_shift_work, container, false);
        return view;
    }

    @Override
    public void initViews(View view) {
        String typeId = SharePerferenceUtil.getInstance().getValue("typeId", "").toString();
        //欢乐熊版本
        if (typeId.equals("25")) {
            mBtnClearCoins.setVisibility(View.GONE);
        }
        getDeviceInfo(SharePerferenceUtil.getInstance().getValue(Constance.mac_Address, "").toString());
    }

    @Override
    public void onResume() {
        super.onResume();
        kd.sp().go(this);
    }

    @OnClick({R.id.btn_shift_clear_coins, R.id.btn_change_shifts_jb})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_shift_clear_coins:
                if (mBtnClearCoins.isSelected()) {
                    mBtnClearCoins.setSelected(false);
                    mBtnClearCoins.setText("暂停");
                    mBtnShiftWork.setClickable(false);
                    mBtnShiftWork.setTextColor(getResources().getColor(R.color.color_999999));
                    //清币
                    kd.sp().bdCleanError();
                    kd.sp().outAllCoin();
                } else {
                    mBtnClearCoins.setSelected(true);
                    mBtnClearCoins.setText("清币");
                    mBtnShiftWork.setClickable(true);
                    mBtnShiftWork.setTextColor(getResources().getColor(R.color.color_blue));

                    machineChangeCleanCoin(mTvCheckCoin.getText().toString(), String.valueOf(false));
                    ///结束清币
                    kd.sp().bdCoinOuted();
                    kd.sp().bdCleanError();
                }
                break;
            case R.id.btn_change_shifts_jb:
                machineChange();
                break;
        }
    }

    /**
     * 获取机器信息
     */
    public void getDeviceInfo(String mac) {
        KProgressHUD dia = KProgressHUD.create(mActivity).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE).setLabel("请稍后...").show();
        HashMap<String, String> hashmap = new HashMap<String, String>();
        hashmap.put("MAC", mac);
        hashmap.put("MachineTypeID", "1");
        hashmap.put("sign", SignParamUtil.getSignStr(hashmap));
        HttpUtils.xPostJson(Constance.MEMBER_HOST + Constance.MacGetMachineClassInfo, hashmap, new XutilsCallback<String>() {

            @Override
            public void onSuccessData(String result) {
                JsonObject jsonObject = GsonUtil.Companion.jsonToObject(result, JsonObject.class);
                if (jsonObject.has("return_Code") && jsonObject.get("return_Code").getAsString().equals("200")) {

                    //保存机器信息
                    SharePerferenceUtil.getInstance().setValue(Constance.MachineClassTime, jsonObject.getAsJsonObject("Data").get("ClassTime").getAsString());
                    SharePerferenceUtil.getInstance().setValue(Constance.MachineClassID, jsonObject.getAsJsonObject("Data").get("ClassID").getAsString());
                    OrderUpdate();

                    String BranchID = SharePerferenceUtil.getInstance().getValue(Constance.BranchID, "").toString();
                    String Vpn = SharePerferenceUtil.getInstance().getValue(Constance.Vpn, "").toString();
                    String BranchName = SharePerferenceUtil.getInstance().getValue(Constance.BranchName, "").toString();
                    //判断是否少返回参数

                } else {
                    new TisDialog(mActivity).create().setMessage("获取机器信息失败," + jsonObject.get("result_Msg") + ",请刷新!").show();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                new TisDialog(mActivity).create().setMessage("获取机器信息失败，请刷新!").show();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                if (dia != null && dia.isShowing()) {
                    dia.dismiss();
                }
            }

        });
    }

    /**
     * 获取机器移动订单同步
     */
    private void OrderUpdate() {
        HashMap<String, String> hashmap = new HashMap<String, String>();
        hashmap.put("MachineID", SharePerferenceUtil.getInstance().getValue(Constance.MachineID, "").toString());
        hashmap.put("sign", SignParamUtil.getSignStr(hashmap));
        HttpUtils.xPostJson(Constance.MEMBER_HOST + Constance.OrderUpdate, hashmap, new XutilsCallback<String>() {
            @Override
            public void onSuccessData(String result) {
                JsonObject jsonObject = GsonUtil.Companion.jsonToObject(result, JsonObject.class);
                if (jsonObject.has("return_Code") && jsonObject.get("return_Code").getAsString().equals("200")) {
                    mTvOrderState.setVisibility(View.GONE);
                } else {
                    mTvOrderState.setText("订单同步失败，" + jsonObject.get("result_Msg") + "，请重新同步!");
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                mTvOrderState.setText("订单同步失败，请重新同步!");
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                getMachineChangeInfo();
            }
        }, 30000);
    }

    /**
     * 获取机器交班信息
     */
    private void getMachineChangeInfo() {
        KProgressHUD dia = KProgressHUD.create(mActivity).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE).setLabel("请稍后...").show();
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("UserID", Constance.machineUserID);
        hashMap.put("MachineID", SharePerferenceUtil.getInstance().getValue(Constance.MachineID, "").toString());
        hashMap.put("ClassTime", SharePerferenceUtil.getInstance().getValue(Constance.MachineClassTime, "").toString());
        hashMap.put("ClassID", SharePerferenceUtil.getInstance().getValue(Constance.MachineClassID, "").toString());
        hashMap.put("sign", SignParamUtil.getSignStr(hashMap));

        HttpUtils.xPostJson(Constance.MEMBER_HOST + Constance.GetHand2Check, hashMap, new XutilsCallback<String>() {
            @Override
            public void onSuccessData(String result) {
                JsonObject jsonObject = GsonUtil.Companion.jsonToObject(result, JsonObject.class);
                if (jsonObject.has("return_Code") && jsonObject.get("return_Code").getAsString().equals("200")) {
                    JsonObject data = jsonObject.getAsJsonObject("Data");
                    mTvOriginalCoin.setText(data.getAsJsonObject("HandInfo").get("InitToken").toString());
                    mTvExportCoin.setText(data.getAsJsonObject("HandInfo").get("OutToken").toString());
                    mTvFinalCoin.setText(data.getAsJsonObject("HandInfo").get("BalanceToken").toString());
                    mTvIncreaseCoin.setText(data.getAsJsonObject("HandInfo").get("AddToken").toString());
                    mTvDifferentCoin.setText("0");
                    mTvSaleCash.setText("0");

                    //实收金额，获取本地数据
                    List<DBReceiveMoneyRecord> list = DBDao.getInstance().queryCashBillByClass();
                    if (list != null && !list.isEmpty()) {
                        Double moneyAmount = 0.00;
                        for (DBReceiveMoneyRecord dbReceiveMoneyRecord : list) {
                            moneyAmount = moneyAmount + dbReceiveMoneyRecord.getMoney();
                        }
                        mTvReceiveCash.setText(moneyAmount.toString());
                    }

                    mTvMoneyDifference.setText("0");
                    String classTime = data.getAsJsonObject("HandInfo").get("ClassTime").toString();
                    mTvShiftSequence.setText(classTime.substring(0, classTime.indexOf("T")) + SharePerferenceUtil.getInstance().getValue(Constance.MachineClassNAME, ""));
                    mTvShiftTime.setText(data.getAsJsonObject("HandInfo").get("LastHandDate").toString().replace("T", " ") + "～" + data.getAsJsonObject("HandInfo").get("HandDate").toString().replace("T", " "));
                    mTvCheckCoin.setText(ClearNum);
                    mTvReceiveDeposit.setText("0");
                    JsonArray jsonArray = data.getAsJsonArray("HandEntryInfo");
                    for (int i = 0; i < jsonArray.size(); i++) {
                        String info = jsonArray.getAsJsonObject().get("PaymentType").toString();
                        if (info.equals("1")) {
                            mTvReceiveDeposit.setText(jsonArray.getAsJsonObject().get("ReceivableAmount").toString());
                        } else if (info.equals("6")) {
                            mTvWxAccount.setText(jsonArray.getAsJsonObject().get("ReceivableAmount").toString());
                        } else if (info.equals("7")) {
                            mTvAlipayAccount.setText(jsonArray.getAsJsonObject().get("ReceivableAmount").toString());
                        }
                    }

                }
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                if (dia != null && dia.isShowing()) {
                    dia.dismiss();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }
        });
    }

    /**
     * 机器交班清币
     */
    private void machineChangeCleanCoin(String Tokens, String IsFinish) {
        MemberInfo memberInfo = GsonUtil.Companion.jsonToObject(SharePerferenceUtil.getInstance().getValue(Constance.member_Info, "").toString(), MemberInfo.class);
        if (memberInfo == null && memberInfo.toString().equals("")) {
            return;
        }
        HashMap<String, String> hashMap = new HashMap<>();
        if (!TextUtils.isEmpty(ClearID)) {
            hashMap.put("ClearID", ClearID);
        }
        hashMap.put("UserID", Constance.machineUserID);

        hashMap.put("MachineID", SharePerferenceUtil.getInstance().getValue(Constance.MachineID, "").toString());
        String ct = SharePerferenceUtil.getInstance().getValue(Constance.MachineClassTime, "").toString();
        hashMap.put("ClassTime", ct);
        hashMap.put("ClassID", SharePerferenceUtil.getInstance().getValue(Constance.MachineClassID, "").toString());
        hashMap.put("Tokens", Tokens);
        hashMap.put("IsFinish", IsFinish);
        hashMap.put("CustID", memberInfo.getId());

        hashMap.put("sign", SignParamUtil.getSignStr(hashMap));

        HttpUtils.xPostJson(Constance.MEMBER_HOST + Constance.WriteClearCoin, hashMap, new XutilsCallback<String>() {
            @Override
            public void onSuccessData(String result) {
                JsonObject jsonObject = GsonUtil.Companion.jsonToObject(result, JsonObject.class);
                if (jsonObject.has("return_Code") && jsonObject.get("return_Code").getAsString().equals("200")) {
                    ClearID = jsonObject.get("Data").toString();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onFinished() {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }
        });
    }

    /**
     * 机器交班
     */
    private void machineChange() {
        if (Utils.isFastClick(1000)) {
            return;
        }
        KProgressHUD dia = KProgressHUD.create(mActivity).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE).setLabel("请稍后...").show();
        HashMap<String, String> hashMap = new HashMap<>();
        if (!TextUtils.isEmpty(ClearID)) {
            hashMap.put("ClearID", ClearID);
        }
        hashMap.put("BarCode", UUID.randomUUID().toString());
        hashMap.put("UserID", Constance.machineUserID);

        hashMap.put("MachineID", SharePerferenceUtil.getInstance().getValue(Constance.MachineID, "").toString());
        String ct = SharePerferenceUtil.getInstance().getValue(Constance.MachineClassTime, "").toString();
        hashMap.put("ClassTime", ct);
        hashMap.put("ClassID", SharePerferenceUtil.getInstance().getValue(Constance.MachineClassID, "").toString());

        if (!TextUtils.isEmpty(ClearID)) {
            hashMap.put("IsClear", String.valueOf(true));
            hashMap.put("CheckTokenQty", mTvCheckCoin.getText().toString());
        } else {
            hashMap.put("IsClear", String.valueOf(false));
            hashMap.put("CheckTokenQty", mTvFinalCoin.getText().toString());
        }

        List<LiPay> liPayList = new ArrayList<>();
        LiPay liPay = new LiPay("0", mTvReceiveCash.getText().toString(), "");
        liPayList.add(liPay);
        hashMap.put("LiPayType", GsonUtil.Companion.objectToJson(liPay));
        hashMap.put("sign", SignParamUtil.getSignStr(hashMap));

        HttpUtils.xPostJson(Constance.MEMBER_HOST + Constance.Hand2Check, hashMap, new XutilsCallback<String>() {
            @Override
            public void onSuccessData(String result) {
                if (dia != null && dia.isShowing()) {
                    dia.dismiss();
                }
                JsonObject jsonObject = GsonUtil.Companion.jsonToObject(result, JsonObject.class);
                if (jsonObject.has("return_Code") && jsonObject.get("return_Code").getAsString().equals("200")) {
                    JsonObject object = jsonObject.getAsJsonObject("Data2");
                    SharePerferenceUtil.getInstance().setValue(Constance.MachineClassTime, object.get("StartTime").toString());
                    SharePerferenceUtil.getInstance().setValue(Constance.MachineClassID, object.get("Id").toString());
                    getMachineChangeInfo();

                    new TisDialog(mActivity).create().setMessage("交班成功").show();
                } else {
                    new TisDialog(mActivity).create().setMessage(jsonObject.get("result_Msg").toString()).show();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                new TisDialog(mActivity).create().setMessage("交班失败").show();
            }

            @Override
            public void onFinished() {
                if (dia != null && dia.isShowing()) {
                    dia.dismiss();
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }
        });

    }


    //--------------------------出币处理--------------------------------------    //--------------------------出币处理--------------------------------------
    //是否成功打开串口
    private boolean isSuccessOpenSerial = false;

    @Override
    public void onReceivedMomeySuccess(int amount, String macType) {

    }

    @Override
    public void onMachieConnectedSuccess(String device) {
        //币斗
        if (device.contains(kd.sp().getDevice("3"))) {
            isSuccessOpenSerial = true;
            Log.e("MA", "su");
        }
    }

    @Override
    public void onCoinOuting(int count) {
        mTvCheckCoin.setText(1 + Integer.valueOf(mTvCheckCoin.getText().toString()) + "");
        mTvDifferentCoin.setText(Integer.valueOf(mTvCheckCoin.getText().toString()) - Integer.valueOf(mTvFinalCoin.getText().toString()) + "");
    }

    @Override
    public void onMachieCommectedFail(String device) {
        //币斗
        if (device.contains(kd.sp().getDevice("3"))) {
            isSuccessOpenSerial = false;
            Log.e("MA", "FA");
        }

    }

    @Override
    public void onSendCompleteData(byte[] bytes) {

    }

    @Override
    public void onReceivedMomeyFail(String macType) {

    }

    @Override
    public void onCoinOutFail(int outCount, int count, String errorCode) {
        if (count >= 0) {
            mBtnShiftWork.setTextColor(getResources().getColor(R.color.color_blue));
            mBtnShiftWork.setClickable(true);
            kd.sp().bdCleanError();
            mBtnClearCoins.setSelected(true);
            mBtnClearCoins.setText("清币");
            ClearNum = mTvCheckCoin.getText().toString();
            machineChangeCleanCoin(mTvCheckCoin.getText().toString(), String.valueOf(true));
        }
    }

    @Override
    public void onCoinOutSuccess(int count) {
        mBtnShiftWork.setTextColor(getResources().getColor(R.color.color_blue));
        mBtnShiftWork.setClickable(true);
        kd.sp().bdCleanError();
        mBtnClearCoins.setSelected(true);
        mBtnClearCoins.setText("清币");
        ClearNum = mTvCheckCoin.getText().toString();
        machineChangeCleanCoin(mTvCheckCoin.getText().toString(), String.valueOf(true));
    }

    @Override
    public void onReceivedMomey(int amount, String macType) {

    }

    @Override
    public void onSerialPortOpenSuccess(File file) {

    }

    @Override
    public void onSerialPortOpenFail(File file) {

    }
}

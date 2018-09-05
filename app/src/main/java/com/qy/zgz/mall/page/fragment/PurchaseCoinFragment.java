package com.qy.zgz.mall.page.fragment;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.qy.zgz.mall.BaseFragment;
import com.qy.zgz.mall.Dbsql.DBDao;
import com.qy.zgz.mall.Dbsql.DBOutCoinRecord;
import com.qy.zgz.mall.Dbsql.DBReceiveMoneyRecord;
import com.qy.zgz.mall.KDSerialPort.KDSerialPort.SerialPortListener;
import com.qy.zgz.mall.KDSerialPort.KDSerialPort.kd;
import com.qy.zgz.mall.Model.BuyCoins;
import com.qy.zgz.mall.Model.MemberInfo;
import com.qy.zgz.mall.R;
import com.qy.zgz.mall.adapter.PackageListAdapter;
import com.qy.zgz.mall.entities.GMSinfo;
import com.qy.zgz.mall.network.Constance;
import com.qy.zgz.mall.network.XutilsCallback;
import com.qy.zgz.mall.page.index.PurchaseCoinActivity;
import com.qy.zgz.mall.utils.GsonUtil;
import com.qy.zgz.mall.utils.HttpUtils;
import com.qy.zgz.mall.utils.LocalDefines;
import com.qy.zgz.mall.utils.SharePerferenceUtil;
import com.qy.zgz.mall.utils.SignParamUtil;
import com.qy.zgz.mall.widget.TisCashPayDialog;
import com.qy.zgz.mall.widget.TisDialog;
import com.qy.zgz.mall.widget.TisEditDialog;
import com.qy.zgz.mall.widget.TisOutCoinsDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class PurchaseCoinFragment extends BaseFragment implements SerialPortListener {
    private final static String TAG = "PurchaseCoinFragment";
    @BindView(R.id.rv_coin_type)
    RecyclerView mRvCoinList;
    @BindView(R.id.tv_fg_buy_coins_notaocan_tis)
    TextView mTvNoPackageTip;

    PurchaseCoinActivity mActivity;
    private PackageListAdapter mAdapter;
    private List<BuyCoins> mCoinListData = new ArrayList<>();
    private TisOutCoinsDialog mDialogOutCoins;
    private TisCashPayDialog mDialogCash;
    private String StockBillID = "";
    private List<BuyCoins> lastCashBuyCoins = new ArrayList<BuyCoins>(); //（现金购买时）需要更新的套餐信息

    //判断是否可以收现金
    private boolean mCanReceiveMoney = false;
    //是否成功打开串口
    public boolean mIsSuccessOpenSerial = false;

    private static PurchaseCoinFragment instance=null;

    private boolean isShowAutoBuy = false;//是否显示自由购买 ,1--显示，0--不显示

    public static PurchaseCoinFragment newInstance() {
        if(instance==null){
            instance= new PurchaseCoinFragment();
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
        View view = inflater.inflate(R.layout.fragment_purchase_coin_layout, container, false);
        return view;
    }

    @Override
    public void initViews(View view) {
        getPackageList();
    }

    @Override
    public void onResume() {
        super.onResume();
        kd.sp().go(this);
        UpdateServerByLocalData();
        getGMSSettingsInfoList();
    }

    private void initCoinList() {
        GridLayoutManager manager = new GridLayoutManager(mActivity, 3);
        mRvCoinList.setLayoutManager(manager);
//        mAdapter = new CoinTypeAdapter(this, mCoinListData, list);
        mAdapter = new PackageListAdapter(mActivity, mCoinListData);
        mAdapter.OnClickListener(new PackageListAdapter.OnClickListener() {
            @Override
            public void OnClickListener(int position) {
//                ToastUtil.showToast(PurchaseCoinActivity.this, mCoinListData.get(position).getPrice() + "元");
                String typeid = SharePerferenceUtil.getInstance().getValue("typeId", "").toString();
                //欢乐熊版本
                if (typeid == "25" && !LocalDefines.sIsLogin) {
                    TisDialog dialog = new TisDialog(mActivity).create().setMessage("请先登录!").show();
                    return;
                }

                boolean isSuccessOutCoin = kd.sp().getIsSuccessOutCoin();
                if (isSuccessOutCoin) {
                    TisDialog dialog = new TisDialog(mActivity).create().setMessage("设备没币,请移步到其他机器!!").show();
                    return;
                }

                if (mIsSuccessOpenSerial) {
                    kd.sp().bdCoinOuted();
                    kd.sp().bdCleanError();
                    if (LocalDefines.sIsLogin && mCoinListData.get(position).getIsMember()) {
                        new TisDialog(mActivity).create().setMessage("需要会员才能购买!").show();
                    }

                    switch (mCoinListData.get(position).getId()) {
                        case "-1":
                            new TisEditDialog(mActivity).create().setEditType(InputType.TYPE_CLASS_NUMBER)
                                    .setMessage("请输入购买金额")
                                    .setNegativeButton(new TisEditDialog.NegativeButtonListener() {
                                        @Override
                                        public void onClick(View v) {

                                        }
                                    }).setPositiveButton(new TisEditDialog.PositiveButtonListener() {
                                @Override
                                public void onClick(View v, String input) {
                                    if (input != null && Integer.valueOf(input) > 0) {
                                        autoMathPackageListNoType(input);
                                    } else {
                                        new TisDialog(mActivity).create().setMessage("金额不能为0或者空")
                                                .show();
                                    }
                                }
                            });
                            break;
                        default:
                            getPackageInfo(mCoinListData.get(position).getId(), position);
                            break;

                    }
                } else {
                    new TisDialog(mActivity).create().setMessage("设备故障,请联系管理员!").show();
                }
            }
        });
        mRvCoinList.setAdapter(mAdapter);
    }

    /**
     * 获取套餐列表
     */
    private void getPackageList() {
        MemberInfo memberInfo = GsonUtil.Companion.jsonToObject(SharePerferenceUtil.getInstance().getValue(Constance.member_Info, "").toString(), MemberInfo.class);
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("PackageType", "Pa01");
        hashMap.put("MachineID", SharePerferenceUtil.getInstance().getValue(Constance.MachineID, "").toString());
        hashMap.put("PageIndex", "1");

        if (null == memberInfo || TextUtils.isEmpty(memberInfo.getId())) {
//            hashMap.put("CustID", Constance.machineFLTUserID);
        } else {
            hashMap.put("CustID", memberInfo.getId());
        }

        hashMap.put("PageNum", "10");
        hashMap.put("sign", SignParamUtil.getSignStr(hashMap));

        HttpUtils.xPostJson(Constance.MEMBER_HOST + Constance.GetPackageList, hashMap, new XutilsCallback<String>() {
            @Override
            public void onSuccessData(String result) {
                Log.i(TAG, "getPackageList result = " + result);
                JsonObject jsonObject = GsonUtil.Companion.jsonToObject(result, JsonObject.class);
                if (jsonObject.has("return_Code") && jsonObject.get("return_Code").toString().equals("200")) {
                    mCoinListData = GsonUtil.Companion.jsonToList(jsonObject.get("Data").getAsJsonArray().toString(), BuyCoins.class);
                    Log.i(TAG, "getPackageList mCoinListData size = " + mCoinListData.size());
                    //自由购买
                    if (mCoinListData != null && mCoinListData.size() > 0) {
                        initCoinList();
                        mTvNoPackageTip.setVisibility(View.GONE);
                    } else {
                        mTvNoPackageTip.setVisibility(View.VISIBLE);
                    }

                } else {

                }
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.i(TAG, "getPackageList onCancelled = " + cex);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    /**
     * SerialPortListener 接口方法
     *
     * @param count
     */
    @Override
    public void onCoinOuting(int count) {
        //存入本地数据
        saveLocalOutCoinRecord(count);
        if (mDialogOutCoins != null) {
            mDialogOutCoins.dismiss();
        }
    }

    @Override
    public void onCoinOutSuccess(int count) {
        //存入本地数据
        saveLocalOutCoinRecord(count);
        if (mDialogOutCoins != null) {
            mDialogOutCoins.showContiune(View.GONE);
        }
        takeUpdateOutCoinLog(count);
        closeOutCoinDialog();
    }

    @Override
    public void onCoinOutFail(int outCount, int count, String errorCode) {
        if (count >= 0) {
            //存入本地数据
            saveLocalOutCoinRecord(count);
            takeFailUpdateOutCoinLog(outCount);
            if (mDialogOutCoins != null) {
                mDialogOutCoins.showContiune(View.GONE);
                mDialogOutCoins.showBug(View.VISIBLE);
            }
        }
    }

    @Override
    public void onReceivedMomey(int amount, String macType) {
        if (mCanReceiveMoney && mIsSuccessOpenSerial && kd.sp().getIsSuccessOutCoin()) {
            if (mDialogCash != null && mDialogCash.isShowing()) {
                lastCashBuyCoins.clear();

                try {
                    if (Double.valueOf(mDialogCash.getShouldPrice()) <= 0) {
                        kd.sp().sendOutMomeyCmd(macType);
                        mDialogCash.dismiss();
                        return;
                    }
                } catch (Exception e) {
                    kd.sp().sendOutMomeyCmd(macType);
                    mDialogCash.dismiss();
                    return;
                }

                mDialogCash.freshCountDown();
                //判断是否超出应收金额
                Double count = Double.valueOf(mDialogCash.getHadPrice()) + amount;
                if (count > Double.valueOf(mDialogCash.getShouldPrice())) {
                    autoMathPackageList(count.toString(), macType);
                } else {
                    //收钱指令
                    kd.sp().sendGetMomeyCmd(macType);
                }
            } else {
                //退钱指令
                kd.sp().sendOutMomeyCmd(macType);
            }
        } else {
            if (!mCanReceiveMoney) {
                new TisDialog(mActivity).create()
                        .setMessage("纸钞机异常，请联系管理员").show();
            } else {
                new TisDialog(mActivity).create()
                        .setMessage("币斗没币或异常，请联系管理员").show();
            }
        }
    }

    @Override
    public void onReceivedMomeySuccess(int amount, String macType) {
        Log.e(TAG, "onReceivedMomeySuccess");
        if (mDialogCash != null && mDialogCash.isShowing()) {

        }
        if (lastCashBuyCoins != null && !lastCashBuyCoins.isEmpty()) {
            Log.e("TAG", "onReceivedMomeySuccess 超出刷新");
            ArrayList<BuyCoins> list = new ArrayList<>();
            list.addAll(lastCashBuyCoins);
            mDialogCash.setInfo(list);
        }

        Double count = Double.valueOf(mDialogCash.getHadPrice()) + amount;
        try {
            mDialogCash.setHadPrice(count.toString());
        } catch (Exception e) {
            mDialogCash.setHadPrice(String.valueOf(amount));
        }

        //存入本地数据

        int id = saveLocalCashRecord(Double.valueOf(mDialogCash.getHadPrice()), mDialogCash.getLocalId());
        mDialogCash.setLocalId(id);

        if (Double.valueOf(mDialogCash.getHadPrice()) >= Double.valueOf(mDialogCash.getShouldPrice())) {
            mDialogCash.showOutSaveButton();
        }
    }

    @Override
    public void onReceivedMomeyFail(String macType) {
        if (mDialogCash != null && mDialogCash.isShowing()) {
            mDialogCash.freshCountDown();
        }
    }

    @Override
    public void onSendCompleteData(byte[] bytes) {

    }

    @Override
    public void onSerialPortOpenFail(File file) {

    }

    @Override
    public void onSerialPortOpenSuccess(File file) {

    }

    @Override
    public void onMachieConnectedSuccess(String device) {
        //币斗
        Log.e("MADV", " onMachieConnectedSuccess device " + device.toString());
        if (device.contains(kd.sp().getDevice("3"))) {
            Log.e("MA", "su");
            mIsSuccessOpenSerial = true;
        }
        //纸币机
        if (device.contains(kd.sp().getDevice("1")) || device.contains(kd.sp().getDevice("2"))) {
            mCanReceiveMoney = true;
            try {
                kd.sp().colseBanknote();
            } catch (Exception e) {

            }
        }
    }

    @Override
    public void onMachieCommectedFail(String device) {
        //币斗
        Log.e("MADV", " onMachieCommectedFail device " + device.toString());
        if (device.contains(kd.sp().getDevice("3"))) {
            mIsSuccessOpenSerial = false;
            Log.e("MA", "FA");
        }
        //纸币机
        if (device.contains(kd.sp().getDevice("1")) || device.contains(kd.sp().getDevice("2"))) {
            mCanReceiveMoney = false;
        }
    }

    /**
     * 存入本地数据库（出币数据）
     */
    private void saveLocalOutCoinRecord(int count) {
        MemberInfo memberInfo = GsonUtil.Companion.jsonToObject(SharePerferenceUtil.getInstance().getValue(Constance.member_Info, "").toString(), MemberInfo.class);
        //存入本地数据库
        DBOutCoinRecord outcointRecord = new DBOutCoinRecord();
        outcointRecord.setOutcount(count);
        outcointRecord.setIsError(1);
        outcointRecord.setStockBillID(StockBillID);
        if (memberInfo != null) {
            outcointRecord.setCustID(memberInfo.getId());
            outcointRecord.setCustName(memberInfo.getCustName());
        } else {
            outcointRecord.setCustID(Constance.machineFLTUserID);
            outcointRecord.setCustName("大众会员");
        }
        outcointRecord.setClassId(SharePerferenceUtil.getInstance().getValue(Constance.MachineClassID, "").toString());
        outcointRecord.setClassTime(SharePerferenceUtil.getInstance().getValue(Constance.MachineClassTime, "").toString());
        DBDao.getInstance().saveOutCoinsRecord(outcointRecord);
    }

    /**
     * 存入本地数据库（现金数据）
     */
    private int saveLocalCashRecord(double money, int localId) {
        MemberInfo memberInfo = GsonUtil.Companion.jsonToObject(SharePerferenceUtil.getInstance().getValue(Constance.member_Info, "").toString(), MemberInfo.class);
        DBReceiveMoneyRecord moneyRecord = new DBReceiveMoneyRecord();
        moneyRecord.setId(localId);
        moneyRecord.setMoney(money);
        moneyRecord.setIsError(1);
        moneyRecord.setClassId(SharePerferenceUtil.getInstance().getValue(Constance.MachineClassID, "").toString());
        moneyRecord.setClassTime(SharePerferenceUtil.getInstance().getValue(Constance.MachineClassTime, "").toString());
        if (memberInfo != null) {
            moneyRecord.setCustID(memberInfo.getId());
            moneyRecord.setCustName(memberInfo.getCustName());
        } else {
            moneyRecord.setCustID(Constance.machineFLTUserID);
            moneyRecord.setCustName("大众会员");
        }
        return DBDao.getInstance().saveOrUpdateCashRecord(moneyRecord);
    }

    /**
     * 提币更新数据接口
     *
     * @param num
     */
    private void takeUpdateOutCoinLog(int num) {
        MemberInfo memberInfo = GsonUtil.Companion.jsonToObject(SharePerferenceUtil.getInstance().getValue(Constance.member_Info, "").toString(), MemberInfo.class);
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("OutCoins", num + "");
        hashMap.put("StockBillID", StockBillID);

        if (null == memberInfo) {
            hashMap.put("CustID", Constance.machineFLTUserID);
            hashMap.put("IsSaveCard", String.valueOf("false"));
        } else {
            hashMap.put("CustID", memberInfo.getId());
            hashMap.put("IsSaveCard", String.valueOf("true"));
        }
        hashMap.put("sign", SignParamUtil.getSignStr(hashMap));

        HttpUtils.xPostJson(Constance.MEMBER_HOST + Constance.UpdateOutCoinLog, hashMap, new XutilsCallback<String>() {
            @Override
            public void onSuccessData(String result) {
                Log.i(TAG, "takeUpdateOutCoinLog result = " + result);
                JsonObject jsonObject = GsonUtil.Companion.jsonToObject(result, JsonObject.class);
                if (jsonObject.has("return_Code") && jsonObject.get("return_Code").toString().equals("200")) {
                    ArrayList<String> sbillList = new ArrayList<String>();
                    sbillList.add(StockBillID);
                    DBDao.getInstance().updateStateOutCoinsRecord(sbillList);
                } else {
                    Constance.maxOutCoinValue = 200;
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.i(TAG, "takeUpdateOutCoinLog onCancelled = " + cex);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onFinished() {
                StockBillID = "";
            }
        });
    }

    /**
     * 提币失败，更新数据接口
     *
     * @param num
     */
    private void takeFailUpdateOutCoinLog(int num) {
        MemberInfo memberInfo = GsonUtil.Companion.jsonToObject(SharePerferenceUtil.getInstance().getValue(Constance.member_Info, "").toString(), MemberInfo.class);
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("OutCoins", num + "");
        hashMap.put("StockBillID", StockBillID);

        if (null == memberInfo) {
            hashMap.put("CustID", Constance.machineFLTUserID);
            hashMap.put("IsSaveCard", String.valueOf("false"));
        } else {
            hashMap.put("CustID", memberInfo.getId());
            hashMap.put("IsSaveCard", String.valueOf("true"));
        }
        hashMap.put("sign", SignParamUtil.getSignStr(hashMap));

        HttpUtils.xPostJson(Constance.MEMBER_HOST + Constance.UpdateOutCoinLog, hashMap, new XutilsCallback<String>() {
            @Override
            public void onSuccessData(String result) {
                Log.i(TAG, "takeFailUpdateOutCoinLog result = " + result);
                JsonObject jsonObject = GsonUtil.Companion.jsonToObject(result, JsonObject.class);
                if (jsonObject.has("return_Code") && jsonObject.get("return_Code").toString().equals("200")) {
                    ArrayList<String> sbillList = new ArrayList<String>();
                    sbillList.add(StockBillID);
                    DBDao.getInstance().updateStateOutCoinsRecord(sbillList);

                    if (LocalDefines.sIsLogin) {
                        kd.sp().bdCleanError();
                        if (mDialogOutCoins != null) {
                            mDialogOutCoins.setBugText("机器没币，未出的币已经返还卡中！");
                        }
                        mActivity.VipLogout();
                        mActivity.showLoginInfo();

                        mBaseFragmentHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (mDialogOutCoins != null) {
                                    mDialogOutCoins.dismiss();
                                }
                            }
                        }, 8000);
                    } else {
                        if (mDialogOutCoins != null && !mDialogOutCoins.isShowing()) {
                            mDialogOutCoins.setBugText("机器没币，请联系管理员补币");
                            mDialogOutCoins.showClose();
                        }
                    }
                } else {
                    if (mDialogOutCoins != null && !mDialogOutCoins.isShowing()) {
                        mDialogOutCoins.setBugText("机器故障或没币，请联系管理员补币");
                        mDialogOutCoins.showClose();
                    }
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.i(TAG, "takeFailUpdateOutCoinLog onCancelled = " + cex);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onFinished() {
                StockBillID = "";
            }
        });
    }

    /**
     * 关闭显示出币界面
     */
    private void closeOutCoinDialog() {
//        exitMember()
        mBaseFragmentHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mDialogOutCoins != null) {
                    mDialogOutCoins.dismiss();
                }
            }
        }, 3000);
    }

    /**
     * 自动匹配套餐
     *
     * @param price
     * @param macType
     */
    private void autoMathPackageList(String price, String macType) {
        KProgressHUD dialog = KProgressHUD.create(mActivity).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE).setLabel("请稍后...").show();
        MemberInfo memberInfo = GsonUtil.Companion.jsonToObject(SharePerferenceUtil.getInstance().getValue(Constance.member_Info, "").toString(), MemberInfo.class);
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("PackageType", "Pa01");
        hashMap.put("MachineID", SharePerferenceUtil.getInstance().getValue(Constance.MachineID, "").toString());

        if (null == memberInfo) {
            hashMap.put("CustID", Constance.machineFLTUserID);
        } else {
            hashMap.put("CustID", memberInfo.getId());
        }

        hashMap.put("Amount", price);
        hashMap.put("sign", SignParamUtil.getSignStr(hashMap));

        HttpUtils.xPostJson(Constance.MEMBER_HOST + Constance.AutoMathPackageList, hashMap, new XutilsCallback<String>() {
            @Override
            public void onSuccessData(String result) {
                Log.i(TAG, "autoMathPackageList result = " + result);
                JsonObject jsonObject = GsonUtil.Companion.jsonToObject(result, JsonObject.class);
                if (jsonObject.has("return_Code") && jsonObject.get("return_Code").toString().equals("200")) {
                    lastCashBuyCoins = GsonUtil.Companion.jsonToList(jsonObject.get("Data").getAsJsonArray().toString(), BuyCoins.class);
                    //收钱指令
                    kd.sp().sendGetMomeyCmd(macType);
                } else {
                    //退钱指令
                    kd.sp().sendOutMomeyCmd(macType);
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.i(TAG, "autoMathPackageList onCancelled = " + cex);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onFinished() {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });
    }

    /**
     * 自动匹配套餐
     *
     * @param price
     */
    public void autoMathPackageListNoType(String price) {
        KProgressHUD dialog = KProgressHUD.create(mActivity).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE).setLabel("请稍后...").show();
        MemberInfo memberInfo = GsonUtil.Companion.jsonToObject(SharePerferenceUtil.getInstance().getValue(Constance.member_Info, "").toString(), MemberInfo.class);
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("PackageType", "Pa01");
        hashMap.put("MachineID", SharePerferenceUtil.getInstance().getValue(Constance.MachineID, "").toString());

        if (null == memberInfo) {
            hashMap.put("CustID", Constance.machineFLTUserID);
        } else {
            hashMap.put("CustID", memberInfo.getId());
        }

        hashMap.put("Amount", price);
        hashMap.put("sign", SignParamUtil.getSignStr(hashMap));

        HttpUtils.xPostJson(Constance.MEMBER_HOST + Constance.AutoMathPackageList, hashMap, new XutilsCallback<String>() {
            @Override
            public void onSuccessData(String result) {
                Log.i(TAG, "autoMathPackageList result = " + result);
                JsonObject jsonObject = GsonUtil.Companion.jsonToObject(result, JsonObject.class);
                if (jsonObject.has("return_Code") && jsonObject.get("return_Code").toString().equals("200")) {
                    lastCashBuyCoins = GsonUtil.Companion.jsonToList(jsonObject.get("Data").getAsJsonArray().toString(), BuyCoins.class);
                    Log.i(TAG, "getPackageList mCoinListData size = " + mCoinListData.size());
                    //收钱指令
//                    kd.sp().sendGetMomeyCmd(macType);
                    //TODO 跳转至 BuyCoinsDetailFragment，参考里面的代码

                } else {
                    //退钱指令
//                    kd.sp().sendOutMomeyCmd(macType);
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.i(TAG, "autoMathPackageList onCancelled = " + cex);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onFinished() {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });
    }

    /**
     * 获取套餐信息
     */
    public void getPackageInfo(String pid, int position) {
        MemberInfo memberInfo = GsonUtil.Companion.jsonToObject(SharePerferenceUtil.getInstance().getValue(Constance.member_Info, "").toString(), MemberInfo.class);
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("PackageID", pid);

        if (null == memberInfo /*|| TextUtils.isEmpty(memberInfo.getId())*/) {
//            hashMap.put("CustID", Constance.machineFLTUserID);
            if (LocalDefines.sIsLogin) {
                hashMap.put("CustID", memberInfo.getId());
            } else {
                hashMap.put("CustID", Constance.machineFLTUserID);
            }
        }

        hashMap.put("packageQty", "1");
        hashMap.put("sign", SignParamUtil.getSignStr(hashMap));

        HttpUtils.xPostJson(Constance.MEMBER_HOST + Constance.GetPackageSaleInfo, hashMap, new XutilsCallback<String>() {
            @Override
            public void onSuccessData(String result) {
                Log.i(TAG, "getPackageList result = " + result);
                JsonObject jsonObject = GsonUtil.Companion.jsonToObject(result, JsonObject.class);
                if (jsonObject.has("return_Code") && jsonObject.get("return_Code").toString().equals("200")) {
                    mCoinListData = GsonUtil.Companion.jsonToList(jsonObject.get("Data").getAsJsonArray().toString(), BuyCoins.class);
                    Log.i(TAG, "getPackageList mCoinListData size = " + mCoinListData.size());

                    //TODO 跳转至 BuyCoinsDetailFragment，参考里面的代码
                } else {

                }
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.i(TAG, "getPackageList onCancelled = " + cex);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    /**
     * 本地出币记录更新数据库记录
     */
    private void UpdateServerByLocalData() {
        List<DBOutCoinRecord> outCoinRecordList = DBDao.getInstance().queryErrorBill();
        if (outCoinRecordList == null || outCoinRecordList.isEmpty()) {
            return;
        }
        ArrayList<HashMap<String, Object>> outList = new ArrayList<HashMap<String, Object>>();
        for (DBOutCoinRecord dbOutCoinRecord : outCoinRecordList) {
            HashMap<String, Object> outHashmap = new HashMap<String, Object>();
            outHashmap.put("StockBillID", dbOutCoinRecord.getStockBillID());
            outHashmap.put("OutCoins", dbOutCoinRecord.getOutcount());
            outList.add(outHashmap);
        }

        HashMap<String, String> hashmap = new HashMap<String, String>();

        hashmap.put("MachineID", SharePerferenceUtil.getInstance().getValue(Constance.MachineID, "").toString());
        hashmap.put("LocalData", GsonUtil.Companion.objectToJson(outList));
        hashmap.put("sign", SignParamUtil.getSignStr(hashmap));

        HttpUtils.xPostJson(Constance.MEMBER_HOST + Constance.UpdateServerByLocalData, hashmap, new XutilsCallback<String>() {
            @Override
            public void onSuccessData(String result) {
                Log.i(TAG, "UpdateServerByLocalData result = " + result);
                JsonObject jsonObject = GsonUtil.Companion.jsonToObject(result, JsonObject.class);
                if (jsonObject.has("return_Code") && jsonObject.get("return_Code").toString().equals("200")) {
                    //修改本地出币数据状态
                    ArrayList<String> stockBillIDList = new ArrayList<>();
                    for (HashMap<String, Object> hashmap : outList) {
                        stockBillIDList.add(hashmap.get("StockBillID").toString());
                    }

                    DBDao.getInstance().updateStateOutCoinsRecord(stockBillIDList);
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.i(TAG, "UpdateServerByLocalData onCancelled = " + cex);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    /**
     * 获取一体机参数
     */
    private void getGMSSettingsInfoList() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("sign", SignParamUtil.getSignStr(hashMap));

        HttpUtils.xPostJson(Constance.MEMBER_HOST + Constance.GetGMSSettingsInfoList, hashMap, new XutilsCallback<String>() {
            @Override
            public void onSuccessData(String result) {
                Log.i(TAG, "getGMSSettingsInfoList result = " + result);
                JsonObject jsonObject = GsonUtil.Companion.jsonToObject(result, JsonObject.class);
                if (jsonObject.has("return_Code") && jsonObject.get("return_Code").toString().equals("200")) {
                    Log.i(TAG, "getGMSSettingsInfoList onSuccess = ");
                    String data = jsonObject.get("Data").getAsJsonArray().toString();
                    Log.i(TAG, "getGMSSettingsInfoList data = " + data);
                    List<GMSinfo> gmSinfos = GsonUtil.Companion.jsonToList(data, GMSinfo.class);
                    Log.i(TAG, "getGMSSettingsInfoList gmSinfos size = " + gmSinfos.size());
                    for (GMSinfo gmSinfo : gmSinfos) {
                        if (gmSinfo.getSettingKey().equals("GMSGetCoinLimit")) {
                            if (TextUtils.isEmpty(gmSinfo.getValue())) {
                                Constance.maxOutCoinValue = 200;
                            } else {
                                Constance.maxOutCoinValue = Integer.valueOf(gmSinfo.getValue());
                            }
                        }

                        if (gmSinfo.getSettingKey().equals("GMSAutoSale")) {
                            if (TextUtils.isEmpty(gmSinfo.getValue())) {
                                isShowAutoBuy = false;
                            } else {
                                if (gmSinfo.getValue().equals("1")) {
                                    isShowAutoBuy = true;
                                } else {
                                    isShowAutoBuy = false;
                                }
                            }
                        }

                    }
                } else {
                    Constance.maxOutCoinValue = 200;
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.i(TAG, "getGMSSettingsInfoList onCancelled = " + cex);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    /**
     * 显示出票界面
     */
    private void showOutCoinsDialog(int num) {
        if (num > 200) {
            mDialogOutCoins = new TisOutCoinsDialog(mActivity).create().setTotalNum(num + "")
                    .setNum("0").showContiune(View.VISIBLE).show();
        } else {
            mDialogOutCoins = new TisOutCoinsDialog(mActivity).create().setTotalNum(num + "")
                    .setNum("0").show();
        }

        kd.sp().bdSendOutCoin(num, kd.sp().getDevice("3"), 1);
    }

    //显示纸币付款提示窗
    private void showCashPayDialog(ArrayList<BuyCoins> buycoinsList) {
        mDialogCash = new TisCashPayDialog(mActivity).create()
                .setInfo(buycoinsList)
                .show();
    }


}

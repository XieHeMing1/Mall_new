package com.qy.zgz.mall.page.fragment;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.qy.zgz.mall.BaseFragment;
import com.qy.zgz.mall.Dbsql.DBDao;
import com.qy.zgz.mall.Dbsql.DBReceiveMoneyRecord;
import com.qy.zgz.mall.KDSerialPort.KDSerialPort.SerialPortListener;
import com.qy.zgz.mall.KDSerialPort.KDSerialPort.kd;
import com.qy.zgz.mall.Model.Exceptionhanding;
import com.qy.zgz.mall.R;
import com.qy.zgz.mall.network.Constance;
import com.qy.zgz.mall.network.XutilsCallback;
import com.qy.zgz.mall.page.index.PurchaseCoinActivity;
import com.qy.zgz.mall.page.money_purchase.error_handle.ExceptionHandingAdapter;
import com.qy.zgz.mall.utils.GsonUtil;
import com.qy.zgz.mall.utils.HttpUtils;
import com.qy.zgz.mall.utils.SharePerferenceUtil;
import com.qy.zgz.mall.utils.SignParamUtil;
import com.qy.zgz.mall.widget.TisDialog;
import com.qy.zgz.mall.widget.TisOutCoinsDialog;

import org.xutils.common.Callback;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.OnClick;

public class ExceptionHandingFragment extends BaseFragment implements SerialPortListener {
    @BindView(R.id.btn_exception_handing_orderupdate)
    Button mBtnOrderUpdate;
    @BindView(R.id.btn_exception_handing_rehandle)
    Button mBtnRehandle;
    @BindView(R.id.btn_exception_handing_refund)
    Button mBtnRefund;
    @BindView(R.id.rv_exception_handing_info)
    RecyclerView mRvInfo;
    @BindView(R.id.tv_exception_handing_pagenum)
    TextView mTvPagenum;

    private boolean isSuccessOpenSerial = false;

    private PurchaseCoinActivity mActivity;

    private int localCashItemNum = 0;
    //当前页数
    private int curPageNum = 1;
    //接口总页数
    private int orderPageNum = 1;
    //每页多少行
    private int row = 5;
    //接口总条数%row的余数
    private int orderRemainNum = 0;
    //总页数
    private int totalPageNum = 1;

    private String StockBillID = "";
    private String CustID = "";
    private String CustName = "";
    TisOutCoinsDialog outCoins_dialog =null;

    ExceptionHandingAdapter exceptionHandingAdapter = null;


    private static ExceptionHandingFragment instance;

    public static ExceptionHandingFragment newInstance() {
        if (instance == null) {
            instance = new ExceptionHandingFragment();
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
        View view = inflater.inflate(R.layout.fragment_exception_handing_2, container, false);
        return view;
    }

    @Override
    public void initViews(View view) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRvInfo.setLayoutManager(linearLayoutManager);
        mRvInfo.addItemDecoration(new DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL));
        String typeid = SharePerferenceUtil.getInstance().getValue("typeId", "").toString();
        //欢乐熊版本
        if (typeid == "25") {
            mBtnRehandle.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        kd.sp().go(this);
        //本地现金记录数
        if (DBDao.getInstance().queryCashErrorBill() != null && DBDao.getInstance().queryCashErrorBill().size() > 0) {
            localCashItemNum = DBDao.getInstance().queryCashErrorBill().size();
        }

        OrderAllUpdate();

    }

    @OnClick({R.id.btn_exception_handing_orderupdate, R.id.btn_exception_handing_rehandle, R.id.btn_exception_handing_refund,
            R.id.iv_exception_handing_next, R.id.iv_exception_handing_pre})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_exception_handing_orderupdate:
                break;
            case R.id.btn_exception_handing_rehandle:
                kd.sp().bdCleanError();
                if (!isSuccessOpenSerial || !kd.sp().getIsSuccessOutCoin()) {
                    new TisDialog(mActivity).create().setMessage("设备故障或没币，请先清除故障!").show();
                    return;
                }
                break;
            case R.id.btn_exception_handing_refund:
                for (Exceptionhanding exceptionhanding : exceptionHandingAdapter.getList()) {
                    if (exceptionhanding.getIscheck()) {
                        if (exceptionhanding.getErrType().equals("CashErr")) {
                            new TisDialog(mActivity).create().setMessage("该订单不能退款").show();
                        } else if (exceptionhanding.getErrType().equals("MobileErr")) {
                            OrderUpdate(exceptionhanding.getID(), exceptionhanding.getPOS(), true, exceptionhanding.getCustomerID(), exceptionhanding.getCustomerName());
                        } else if (exceptionhanding.getErrType().equals("OutCoinErr")) {
                            new TisDialog(mActivity).create().setMessage("该订单不能退款").show();
                        }
                    }
                }
                break;
            case R.id.iv_exception_handing_next:
                if (curPageNum >= totalPageNum) {
                    return;
                }
                curPageNum += 1;
                getErrorOrderList();
                break;
            case R.id.iv_exception_handing_pre:
                if (curPageNum <= 1) {
                    return;
                }
                curPageNum -= 1;
                getErrorOrderList();
                break;
            default:
                break;
        }
    }

    /**
     * 同步机器订单
     */
    private void OrderAllUpdate() {
        KProgressHUD dia = KProgressHUD.create(mActivity).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE).setLabel("正在同步订单,请稍后...").show();
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("MachineID", SharePerferenceUtil.getInstance().getValue(Constance.MachineID, "").toString());
        hashMap.put("sign", SignParamUtil.getSignStr(hashMap));
        HttpUtils.xPostJson(Constance.MEMBER_HOST + Constance.OrderUpdate, hashMap, new XutilsCallback<String>() {
            @Override
            public void onSuccessData(String result) {
                JsonObject jsonObject = GsonUtil.Companion.jsonToObject(result.toString(), JsonObject.class);
                if (jsonObject.has("return_Code") && jsonObject.get("return_Code").toString().equals("200")) {

                } else {
                    new TisDialog(mActivity).create().setMessage(jsonObject.get("result_Msg").toString()).show();

                }
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                //获取异常订单列表
                getErrorOrderList();
                if (dia != null && dia.isShowing()) {
                    dia.dismiss();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                new TisDialog(mActivity).create().setMessage("网络故障或超时").show();
            }
        }, 30000);
    }

    //获取异常订单方法(调用异常订单接口)
    private void getErrorOrderList() {
        if (curPageNum > orderPageNum) {
            getAbnormityList(orderPageNum);
        } else {
            getAbnormityList(curPageNum);
        }
    }

    /**
     * 获取机器获取异常记录
     */
    private void getAbnormityList(int pageNum) {
        KProgressHUD dia = KProgressHUD.create(mActivity).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE).setLabel("请稍后...").show();
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("MachineID", SharePerferenceUtil.getInstance().getValue(Constance.MachineID, "").toString());
        hashMap.put("SearchType", "0");
        hashMap.put("IsFinish", String.valueOf(false));
        hashMap.put("PageNum", pageNum + "");
        hashMap.put("PageRows", row + "");
        hashMap.put("sign", SignParamUtil.getSignStr(hashMap));
        HttpUtils.xPostJson(Constance.MEMBER_HOST + Constance.GetAbnormityList, hashMap, new XutilsCallback<String>() {
            @Override
            public void onSuccessData(String result) {
                JsonObject jsonObject = GsonUtil.Companion.jsonToObject(result, JsonObject.class);
                if (jsonObject.has("return_Code") && jsonObject.get("return_Code").toString().equals("200")) {
                    //显示数据
                    ArrayList<Exceptionhanding> showData = new ArrayList<>();
                    List<Exceptionhanding> exceData = GsonUtil.Companion.jsonToList(jsonObject.getAsJsonArray("Data").toString(), Exceptionhanding.class);
                    orderPageNum = Integer.valueOf(jsonObject.get("Data2").toString()) / row;
                    orderRemainNum = Integer.valueOf(jsonObject.get("Data2").toString()) % row;
                    if (orderRemainNum != 0) {
                        orderPageNum += 1;
                    }

                    //判断是否有现金异常
                    if (orderPageNum <= curPageNum) {
                        if (localCashItemNum > 0) {
                            if (orderPageNum == curPageNum) {
                                //合拼本地数据和接口数据
                                if (null != exceData) {
                                    showData.addAll(exceData);
                                    if (exceData.size() < row) {
                                        showData.addAll(getLocalCashShowDataList(row - exceData.size(), 0));
                                    }
                                } else {
                                    showData.addAll(getLocalCashShowDataList(row, 0));

                                }
                            } else {
                                int offset, diffPage;
                                if (orderRemainNum == 0) {
                                    offset = orderRemainNum;
                                } else {
                                    offset = row - orderRemainNum;
                                }
                                if ((curPageNum - 1 - orderPageNum) < 0) {
                                    diffPage = 0;
                                } else {
                                    diffPage = (curPageNum - 1 - orderPageNum);
                                }
                                if (getLocalCashShowDataList(row, offset + diffPage * row) != null) {
                                    showData.addAll(getLocalCashShowDataList(row, offset + diffPage * row));
                                }
                            }
                        } else {
                            if (null != exceData) {
                                showData.addAll(exceData);
                            }
                        }
                    } else {
                        if (null != exceData) {
                            showData.addAll(exceData);
                        }
                    }

                    exceptionHandingAdapter = new ExceptionHandingAdapter(mActivity, showData);
                    mRvInfo.setAdapter(exceptionHandingAdapter);

                    int cashPageItem;
                    if (orderRemainNum == 0) {
                        cashPageItem = localCashItemNum;
                    } else {
                        cashPageItem = localCashItemNum - row + orderRemainNum;
                    }

                    totalPageNum = orderPageNum + cashPageItem / row;
                    if (cashPageItem % row != 0) {
                        totalPageNum += 1;
                    }
                    mTvPagenum.setText(curPageNum + "/" + totalPageNum);
                } else {
                    new TisDialog(mActivity).create().setMessage(jsonObject.get("result_Msg").toString()).show();
                }
            }

            @Override
            public void onFinished() {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }
        }, 30000);
    }

    //获取本地现金异常数据
    private ArrayList<Exceptionhanding> getLocalCashShowDataList(int limit, int offset) {
        ArrayList<Exceptionhanding> showData = new ArrayList<Exceptionhanding>();
        List<DBReceiveMoneyRecord> localData = DBDao.getInstance().queryCashErrorBillByNum(limit, offset);
        for (DBReceiveMoneyRecord dbReceiveMoneyRecord : localData) {
            Exceptionhanding exceptionhanding = new Exceptionhanding();
            exceptionhanding.setDate(dbReceiveMoneyRecord.getCreatetime());
            exceptionhanding.setAmount(dbReceiveMoneyRecord.getMoney() + "");
            exceptionhanding.setErrType("CashErr");
            exceptionhanding.setCustomerID(dbReceiveMoneyRecord.getCustID());
            exceptionhanding.setCustomerName(dbReceiveMoneyRecord.getCustName());
            exceptionhanding.setCashErrorRecordId(dbReceiveMoneyRecord.getId());
            showData.add(exceptionhanding);
        }
        return showData;
    }

    /**
     * 获取机器移动订单同步
     */
    private void OrderUpdate(String OrderID, String OrderPos, boolean isRefund, String CustomerID, String CustomerName) {
        KProgressHUD dia = KProgressHUD.create(mActivity).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE).setLabel("请稍后...").show();
        HashMap hashMap = new HashMap<String, String>();
        hashMap.put("OrderID", OrderID);
        hashMap.put("sign", SignParamUtil.getSignStr(hashMap));
        HttpUtils.xPostJson(Constance.MEMBER_HOST + Constance.OrderUpdate, hashMap, new XutilsCallback<String>() {
            @Override
            public void onSuccessData(String result) {
                JsonObject jsonObject = GsonUtil.Companion.jsonToObject(result, JsonObject.class);
                if (jsonObject.has("return_Code") && jsonObject.get("return_Code").toString().equals("200")) {
                    if (isRefund) {
                        OrderRefund(OrderID);
                    } else {
                        orderReOperate(OrderPos, CustomerID, OrderID, CustomerName);

                    }
                } else {
                    new TisDialog(mActivity).create().setMessage(jsonObject.get("result_Msg").toString()).show();

                }
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

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                new TisDialog(mActivity).create().setMessage("网络故障").show();
            }
        });
    }

    /*
     * 移动订单退款
     */
    private void OrderRefund(String order) {
        if (null == exceptionHandingAdapter) {
            return;
        }
        KProgressHUD dia = KProgressHUD.create(mActivity).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE).setLabel("请稍后...").show();
        HashMap hashMap = new HashMap<String, String>();
        hashMap.put("UserID", Constance.machineUserID);
        hashMap.put("OrderID", order);
        hashMap.put("sign", SignParamUtil.getSignStr(hashMap));
        HttpUtils.xPostJson(Constance.MEMBER_HOST + Constance.OrderRefund, hashMap, new XutilsCallback<String>() {
            @Override
            public void onSuccessData(String result) {
                JsonObject jsonObject = GsonUtil.Companion.jsonToObject(result.toString(), JsonObject.class);
                if (jsonObject.has("return_Code") && jsonObject.get("return_Code").toString().equals("200")) {
                    new TisDialog(mActivity).create().setMessage(jsonObject.get("result_Msg").toString()).show();
                } else {
                    new TisDialog(mActivity).create().setMessage(jsonObject.get("result_Msg").toString()).show();
                }
            }

            @Override
            public void onFinished() {
                getErrorOrderList();
                if (dia != null && dia.isShowing()) {
                    dia.dismiss();
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                new TisDialog(mActivity).create().setMessage("网络异常!").show();
            }
        });
    }

    /**
     * 移动订单重新处理
     */
    private void orderReOperate(String order, String CustomerID, String OrderID, String CustomerName) {
        if (null == exceptionHandingAdapter) {
            return;
        }
        KProgressHUD dia = KProgressHUD.create(mActivity).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE).setLabel("请稍后...").show();
        HashMap hashMap = new HashMap<String, String>();
        hashMap.put("BarCode", UUID.randomUUID().toString());
        hashMap.put("UserID", Constance.machineUserID);
        hashMap.put("MachineID", SharePerferenceUtil.getInstance().getValue(Constance.MachineID, "").toString());
        hashMap.put("ClassTime", SharePerferenceUtil.getInstance().getValue(Constance.MachineClassTime, "").toString());
        hashMap.put("ClassID", SharePerferenceUtil.getInstance().getValue(Constance.MachineClassID, "").toString());
        hashMap.put("OrderNO", order);

        hashMap.put("sign", SignParamUtil.getSignStr(hashMap));
        HttpUtils.xPostJson(Constance.MEMBER_HOST + Constance.OrderReOperate, hashMap, new XutilsCallback<String>() {
            @Override
            public void onSuccessData(String result) {
                JsonObject jsonObject = GsonUtil.Companion.jsonToObject(result.toString(), JsonObject.class);
                if (jsonObject.has("return_Code") && jsonObject.get("return_Code").toString().equals("200")) {
                    if (CustomerID == Constance.machineFLTUserID) {
                        getNoFinishCoins(jsonObject.getAsJsonObject("Data").get("ReturnID").toString(), CustomerID, CustomerName);
                    } else {
                        getErrorOrderList();
                        new TisDialog(mActivity).create().setMessage("处理完成,币已存入卡中!").show();
                    }
                } else {
                    new TisDialog(mActivity).create().setMessage(jsonObject.get("result_Msg").toString()).show();
                }
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

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                new TisDialog(mActivity).create().setMessage("网络异常!").show();
            }
        });
    }

    /**
     * 重新处理出币记录
     */
    private void getNoFinishCoins(String StockBillID, String CustID, String CustName) {
        if (null == exceptionHandingAdapter) {
            return;
        }
        KProgressHUD dia = KProgressHUD.create(mActivity).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE).setLabel("请稍后...").show();
        HashMap<String, String> hashmap = new HashMap<String, String>();
        hashmap.put("StockBillID", StockBillID);
        hashmap.put("sign", SignParamUtil.getSignStr(hashmap));
        HttpUtils.xPostJson(Constance.MEMBER_HOST + Constance.GetNoFinishCoins, hashmap, new XutilsCallback<String>() {
            @Override
            public void onSuccessData(String result) {
                JsonObject jsonObject = GsonUtil.Companion.jsonToObject(result.toString(), JsonObject.class);
                if (jsonObject.has("return_Code") && jsonObject.get("return_Code").toString().equals("200")) {
                    outCoin(Integer.valueOf(jsonObject.get("Data").toString()), StockBillID, CustID, CustName);
                } else {
                    new TisDialog(mActivity).create().setMessage(jsonObject.get("result_Msg").toString()).show();
                }
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

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                new TisDialog(mActivity).create().setMessage("网络异常!").show();
            }
        });
    }

    /**
     * 出币
     */
    private void outCoin(int num, String billid, String cID, String cName) {
        StockBillID = billid;
        CustID = cID;
        CustName = cName;
        outCoins_dialog = new TisOutCoinsDialog(mActivity).create().setTotalNum(num + "")
                .setNum("0").show();
        kd.sp().bdSendOutCoin(num, kd.sp().getDevice("3"), 1);
    }

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

    }

    @Override
    public void onCoinOutSuccess(int count) {

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

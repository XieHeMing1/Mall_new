package com.qy.zgz.mall.page.fragment;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.gson.JsonObject;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.qy.zgz.mall.BaseFragment;

import com.qy.zgz.mall.Dbsql.DBDao;
import com.qy.zgz.mall.Dbsql.DBOutCoinRecord;
import com.qy.zgz.mall.KDSerialPort.KDSerialPort.kd;
import com.qy.zgz.mall.R;
import com.qy.zgz.mall.network.Constance;
import com.qy.zgz.mall.network.XutilsCallback;
import com.qy.zgz.mall.page.index.PurchaseCoinActivity;
import com.qy.zgz.mall.utils.GsonUtil;
import com.qy.zgz.mall.utils.HttpUtils;
import com.qy.zgz.mall.utils.SharePerferenceUtil;
import com.qy.zgz.mall.utils.ShutDownUtil;
import com.qy.zgz.mall.utils.SignParamUtil;
import com.qy.zgz.mall.utils.UnityDialog;
import com.qy.zgz.mall.widget.TisDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class ManagerSystemFragment extends BaseFragment {
    @BindView(R.id.fl_manager_fragment_container)
    FrameLayout mFlContainer;
    @BindView(R.id.iv_close_system)
    ImageView mIvCloseSystem;
    @BindView(R.id.iv_reboot_system)
    ImageView mIvRebootSystem;
    @BindView(R.id.iv_manager_exception_handing)
    ImageView mIvExceptionHanding;
    @BindView(R.id.iv_manager_shift_work)
    ImageView mIvChange;
    @BindView(R.id.iv_manager_clearbug)
    ImageView mIvClearBug;
    @BindView(R.id.iv_manager_exit_account)
    ImageView mIvExit;
    PurchaseCoinActivity mActivity;

    private static ManagerSystemFragment instance=null;

    public static ManagerSystemFragment newInstance() {
        if(instance==null){
            instance= new ManagerSystemFragment();
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
        View view = inflater.inflate(R.layout.activity_manager_system, container, false);
        return view;
    }

    @Override
    public void initViews(View view) {
        //更新出币记录
        UpdateServerByLoalData();
    }

    @OnClick({R.id.iv_close_system, R.id.iv_reboot_system, R.id.iv_manager_exception_handing,
            R.id.iv_manager_shift_work, R.id.iv_manager_clearbug, R.id.iv_manager_exit_account})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_close_system:
                new UnityDialog(mActivity).setHint("是否确定关机？")
                        .setCancel("取消",null)
                        .setConfirm("确定", new UnityDialog.OnConfirmDialogListener() {
                            @Override
                            public void confirm(UnityDialog unityDialog, String content) {
                                getMachineLogout();
                                unityDialog.dismiss();
                            }
                        });
                break;
            case R.id.iv_reboot_system:
                new UnityDialog(mActivity).setHint("是否确定重启？")
                        .setCancel("取消",null)
                        .setConfirm("确定", new UnityDialog.OnConfirmDialogListener() {
                            @Override
                            public void confirm(UnityDialog unityDialog, String content) {
                                ShutDownUtil.reboot();
                                unityDialog.dismiss();
                            }
                        });
                break;
            case R.id.iv_manager_exception_handing:
                break;
            case R.id.iv_manager_shift_work:
                replaceFragment(ShiftWorkFragment.newInstance());
                break;
            case R.id.iv_manager_clearbug:
                new UnityDialog(mActivity).setHint("是否清除出币故障？")
                        .setCancel("取消",null)
                        .setConfirm("确定", new UnityDialog.OnConfirmDialogListener() {
                            @Override
                            public void confirm(UnityDialog unityDialog, String content) {
                                kd.sp().bdCoinOuted();
                                kd.sp().bdCleanError();
                                kd.sp().setIsSuccessOutCoin(true);
                                unityDialog.dismiss();
                            }
                        });
                break;
            case R.id.iv_manager_exit_account:
                mActivity.VipLogout();
                mActivity.replaceFragment(PurchaseAndTakeCoinFragment.newInstance());
                break;
            default:
                break;
        }
    }

    private void replaceFragment(Fragment fragment) {
//        mflContaner.setVisibility(View.VISIBLE);
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
        transaction.replace(R.id.fl_manager_fragment_container, fragment);
        transaction.commit();
    }

    //本地出币记录更新数据库记录
    private void UpdateServerByLoalData() {
        KProgressHUD dia = KProgressHUD.create(mActivity).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE).setLabel("请稍后...").show();
        List<DBOutCoinRecord> outCoinRecordList = DBDao.getInstance().queryErrorBill();
        if(outCoinRecordList == null || outCoinRecordList.isEmpty()) {
            if(dia != null && dia.isShowing()) {
                dia.dismiss();
            }
            return;
        }

        List<HashMap<String, Object>> outList = new ArrayList<>();
        for(DBOutCoinRecord dbOutCoinRecord : outCoinRecordList) {
            HashMap<String, Object> outHashMap = new HashMap<>();
            outHashMap.put("StockBillID",dbOutCoinRecord.getStockBillID());
            outHashMap.put("OutCoins",dbOutCoinRecord.getOutcount());
            outList.add(outHashMap);
        }

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("MachineID", SharePerferenceUtil.getInstance().getValue(Constance.MachineID, "").toString());
        hashMap.put("LocalData", GsonUtil.Companion.objectToJson(outList));
        hashMap.put("sign", SignParamUtil.getSignStr(hashMap));

        HttpUtils.xPostJson(Constance.MEMBER_HOST+Constance.UpdateServerByLocalData,hashMap,new XutilsCallback<String>(){
            @Override
            public void onSuccessData(String result) {
                JsonObject jsonObject = GsonUtil.Companion.jsonToObject(result, JsonObject.class);
                if (jsonObject.has("return_Code") && jsonObject.get("return_Code").getAsString().equals("200")) {
                    List<String> sbillList = new ArrayList<String>();
                    for(HashMap<String, Object> sbillHashMap : outList) {
                        sbillList.add(sbillHashMap.get("StockBillID").toString());
                    }
                    DBDao.getInstance().updateStateOutCoinsRecord(sbillList);
                }else {
                   new TisDialog(mActivity).create().setMessage(jsonObject.get("result_Msg").toString()).show();
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                if(dia != null && dia.isShowing()) {
                    dia.dismiss();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                new TisDialog(mActivity).create().setMessage("网络异常").show();
            }
        });
    }

    /**
     * 更改机器登录状态
     */
    private void getMachineLogout() {
        KProgressHUD dia = KProgressHUD.create(mActivity).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE).setLabel("请稍后...").show();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("MachineID",SharePerferenceUtil.getInstance().getValue(Constance.MachineID,"").toString());
        hashMap.put("IsLogOut",String.valueOf(true));
        hashMap.put("sign",SignParamUtil.getSignStr(hashMap));
        HttpUtils.xPostJson(Constance.MEMBER_HOST+Constance.MachineLogout,hashMap,new XutilsCallback<String>(){
            @Override
            public void onSuccessData(String result) {

            }

            @Override
            public void onFinished() {
                if (dia != null && dia.isShowing()) {
                    dia.dismiss();
                }
                ShutDownUtil.shutdown();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }
        });
    }
}

package com.qy.zgz.mall.page.fragment;

import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonObject;
import com.qy.zgz.mall.BaseFragment;
import com.qy.zgz.mall.Model.MemberInfo;
import com.qy.zgz.mall.Model.PurchaseRecord;
import com.qy.zgz.mall.R;
import com.qy.zgz.mall.adapter.ConsumptionListAdapter;
import com.qy.zgz.mall.network.Constance;
import com.qy.zgz.mall.network.XutilsCallback;
import com.qy.zgz.mall.utils.GsonUtil;
import com.qy.zgz.mall.utils.HttpUtils;
import com.qy.zgz.mall.utils.SharePerferenceUtil;
import com.qy.zgz.mall.utils.SignParamUtil;
import com.zhy.autolayout.AutoLinearLayout;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

public class ConsumptionRecordFragment extends BaseFragment {
//
//    @BindView(R.id.ll_consumption_record_layout)
//    AutoLinearLayout mLlConsumptionRecordLayout;
    @BindView(R.id.rv_consumption)
    RecyclerView mRvConsumption;

    ConsumptionListAdapter mConSumptionListAdapter;
    List<PurchaseRecord> mConsumptionRecordList;
    public static final String TAG = "ConsumptionFragment";

    private static ConsumptionRecordFragment instance;
    public static ConsumptionRecordFragment newInstance() {
        if(instance == null) {
            instance = new ConsumptionRecordFragment();
        }
        return instance;
    }

    @Override
    public View getLayoutView(LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.fragment_consumption_record, container, false);
    }

    @Override
    public void initViews(View view) {
    }

    @Override
    public void onResume() {
        super.onResume();
        getConsumptionRecord();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mConSumptionListAdapter = null;
    }

    private void initConsumptionRecordList() {
        if (mConSumptionListAdapter != null) {
            mConSumptionListAdapter.notifyDataSetChanged();
        } else {
            mConSumptionListAdapter = new ConsumptionListAdapter(mAttachActivity, mConsumptionRecordList);
            mRvConsumption.setLayoutManager(new LinearLayoutManager(mAttachActivity, LinearLayoutManager.VERTICAL, false));
            mRvConsumption.setAdapter(mConSumptionListAdapter);
        }
    }

    /**
     * 获取会员消费记录
     */
    private void getConsumptionRecord() {
        MemberInfo memberInfo = GsonUtil.Companion.jsonToObject(SharePerferenceUtil.getInstance().getValue(Constance.member_Info, "").toString(), MemberInfo.class);

        if (memberInfo != null) {
            //本地有数据，网络获取
            HashMap<String, String> hashmap = new HashMap<String, String>();
            hashmap.put("IntMonth", "1");
            hashmap.put("PageNum", "1");
            hashmap.put("PageRows", "20");
            hashmap.put("MemberID", memberInfo.getId());
            hashmap.put("sign", SignParamUtil.getSignStr(hashmap));
            Log.i(TAG, "getConsumptionRecord memberInfo.getId() = " + memberInfo.getId());
            //临时数据，避免多次发送请求
            HttpUtils.xPostJson(Constance.MEMBER_HOST + Constance.GetCustomerConsume, hashmap, new XutilsCallback<String>() {
                @Override
                public void onSuccessData(String result) {
                    JsonObject jsonResult = GsonUtil.Companion.jsonToObject(result, JsonObject.class);
                    if (jsonResult != null && jsonResult.has("return_Code") &&
                            jsonResult.get("return_Code").toString().equals("200")) {
                        Log.i(TAG, "getConsumptionRecord result = " + result);
                        mConsumptionRecordList = GsonUtil.Companion.jsonToList(jsonResult.get("Data").getAsJsonArray().toString(), PurchaseRecord.class);
                        initConsumptionRecordList();
                    } else {
                    }
                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {

                }

                @Override
                public void onFinished() {

                }
            });
        } else {

        }
    }
}

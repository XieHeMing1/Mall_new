package com.qy.zgz.mall.page.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.qy.zgz.mall.Model.MyOrder;
import com.qy.zgz.mall.R;
import com.qy.zgz.mall.adapter.OrderListAdapter;
import com.qy.zgz.mall.network.Constance;
import com.qy.zgz.mall.network.NetworkCallback;
import com.qy.zgz.mall.network.NetworkRequest;
import com.qy.zgz.mall.utils.GsonUtil;
import com.qy.zgz.mall.utils.LocalDefines;
import com.qy.zgz.mall.utils.SharePerferenceUtil;

import java.util.HashMap;
import java.util.List;

public class EvalutionFragment extends Fragment {
    private static final String TAG = "EvalutionFragment";
    private static EvalutionFragment instance;

    public static EvalutionFragment newInstance() {
        if(instance == null) {
            instance = new EvalutionFragment();
        }
        return instance;
    }

    OrderListAdapter mAdapter;
    List<MyOrder> mDataList;
    private View mRootView;//缓存Fragment view
    RecyclerView mRvOrderList;
    TextView mTvNoRecord;

    private boolean mIsShouldLoadData = true; //是否需要从网上加载数据
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(mRootView==null){
            mRootView=inflater.inflate(R.layout.fragment_consumption_record_layout, null);
            mRvOrderList = (RecyclerView) mRootView.findViewById(R.id.rv_order_list);
            mTvNoRecord = (TextView) mRootView.findViewById(R.id.tv_no_record);
        }
        //缓存的rootView需要判断是否已经被加过parent， 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }
        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        consumptionOrderList(LocalDefines.WAIT_RATE);
        Log.i(TAG,"onResume");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"onDestroy");
    }

    /**
     * 获取订单列表
     */
    private void consumptionOrderList(String type){
        Log.i(TAG, " consumptionOrderList ");
        String token = SharePerferenceUtil.getInstance().getValue(Constance.user_accessToken, "").toString();
//        Log.i(TAG, " consumptionOrderList token = " + token);
        if (!mIsShouldLoadData) {
            return;
        }
//        Log.i(TAG, " consumptionOrderList token = " + token);
        HashMap<String, String> hashmap = new HashMap<String, String>();
        hashmap.put("accessToken", SharePerferenceUtil.getInstance().getValue(Constance.user_accessToken,"").toString());
        hashmap.put("page_size", "200");
        if(type != null) {
            hashmap.put("status", type);
        }
        NetworkRequest.getInstance().tradeList(hashmap, new NetworkCallback<JsonObject>() {
            @Override
            public void onSuccess(JsonObject data) {
                if (data==null){
//                    Log.i(TAG, "data == null ");
                    return;
                }
                Log.i(TAG, "data ! = null " + data.toString());
                mIsShouldLoadData = false;
                if(data.has("data") && data.get("data").equals("") || !data.has("list")) {
                    Log.i(TAG, "消费记录为空");
                    mTvNoRecord.setVisibility(View.VISIBLE);
                    return;
                }
                mDataList = GsonUtil.Companion.jsonToList(data.get("list").getAsJsonArray().toString(), MyOrder.class);
                initcConsumptionList();
            }

            @Override
            public void onFailure(int code, String msg) {
                Log.i(TAG, "onFailure code == " + code + " msg == " + msg);
            }
        });
    }

    private void  initcConsumptionList(){
        mTvNoRecord.setVisibility(View.GONE);
        if(mAdapter == null) {
            Log.i(TAG, "mAdapter == null");
//            mDataList = new ArrayList<>();
            if(mDataList != null && mDataList.size() > 0) {
                Log.i(TAG, "mDataList != null  mDataList size =  " + mDataList.size());
                mAdapter = new OrderListAdapter(getActivity(), mDataList);
                mRvOrderList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                mRvOrderList.setAdapter(mAdapter);
            }
        } else {
            Log.i(TAG, "mAdapter != null");
            mAdapter.notifyDataSetChanged();
        }
    }
}

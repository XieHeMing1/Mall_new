package com.qy.zgz.mall.page.fragment;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.qy.zgz.mall.Model.ShopCar;
import com.qy.zgz.mall.R;
import com.qy.zgz.mall.adapter.ShopCarAdapter;
import com.qy.zgz.mall.adapter.ShoppingCartAdapter;
import com.qy.zgz.mall.network.Constance;
import com.qy.zgz.mall.network.NetworkCallback;
import com.qy.zgz.mall.network.NetworkRequest;
import com.qy.zgz.mall.utils.GsonUtil;
import com.qy.zgz.mall.utils.SharePerferenceUtil;
import com.qy.zgz.mall.utils.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ShoppingCartBottomFragment extends BottomSheetDialogFragment {

    @BindView(R.id.rv_shop_car_list)
    RecyclerView mRvShopCartList;
    //结算
    @BindView(R.id.btn_shop_car_balance)
    Button mBtnBalance;
    //删除
    @BindView(R.id.tv_shop_car_del)
    TextView mTvDelete;
    //编辑
    @BindView(R.id.tv_shop_car_update)
    TextView mTvEdit;
    //全选
    @BindView(R.id.iv_shop_car_allsel)
    ImageView mIvCheckAll;
    @BindView(R.id.iv_shop_car_back)
    ImageView mIvBack;
    //合计
    @BindView(R.id.tv_shop_car_total)
    TextView mTvAmount;
    //商店名称
    @BindView(R.id.tv_shop_car_shopname)
    TextView mTvShopName;

    private static final String TAG ="ShoppingCart";

    private Unbinder mUnbinder;
    private Activity mActivity;
    private ShoppingCartAdapter mAdapter;
    private ArrayList<ShopCar> mShoppingCartDataList = new ArrayList<>();

    private static ShoppingCartBottomFragment instance;

    public static ShoppingCartBottomFragment newInstance() {
        if (instance == null) {
            instance = new ShoppingCartBottomFragment();
        }
        return instance;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop_car, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        mIvCheckAll.setSelected(false);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getCartInfo();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mUnbinder != null) {
            mUnbinder.unbind();
        }
    }

    @OnClick({R.id.btn_shop_car_balance, R.id.tv_shop_car_del, R.id.tv_shop_car_update,
            R.id.iv_shop_car_allsel, R.id.iv_shop_car_back})
    public void onClick(View view) {
        switch (view.getId()) {
            //编辑
            case R.id.tv_shop_car_update:
                if (mBtnBalance.getVisibility()==View.GONE){
                    mBtnBalance.setVisibility(View.VISIBLE);
                    mTvDelete.setVisibility(View.GONE);
                    mTvEdit.setTextColor(getResources().getColor(R.color.color_black));
                }else{
                    mBtnBalance.setVisibility(View.GONE);
                    mTvDelete.setVisibility(View.VISIBLE);
                    mTvEdit.setTextColor(getResources().getColor(R.color.color_red));
                }
                break;
            //全选
            case R.id.iv_shop_car_allsel:
                if (!mIvCheckAll.isSelected()){
                    for (int i = 0; i < mShoppingCartDataList.size(); i++) {
                        mShoppingCartDataList.get(i).setChecked(1);
                    }
                    mIvCheckAll.setImageResource(R.drawable.select);
                    mIvCheckAll.setSelected(true);
                }else{
                    for (int i = 0; i < mShoppingCartDataList.size(); i++) {
                        mShoppingCartDataList.get(i).setChecked(0);
                    }
                    mIvCheckAll.setImageResource(R.drawable.select_grey);
                    mIvCheckAll.setSelected(false);
                }
                mRvShopCartList.getAdapter().notifyDataSetChanged();
                break;
            //删除选中的
            case R.id.tv_shop_car_del:
                delCartItem();
                break;
            case R.id.btn_shop_car_balance:
                //更新购物车选中状态
                updateCart();
                break;
            case R.id.iv_shop_car_back:
                instance.dismiss();
                break;
            default:
                break;
        }
    }

    private void initShoppingCartList() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRvShopCartList.setLayoutManager(linearLayoutManager);
        //初始化数据
        mAdapter = new ShoppingCartAdapter(mActivity, mShoppingCartDataList);
        mRvShopCartList.setAdapter(mAdapter);
    }

    /**
     * 查看购物车
     */
    private void getCartInfo() {
        HashMap<String, String> hashmap = new HashMap<String, String>();
        hashmap.put("accessToken", SharePerferenceUtil.getInstance().getValue(Constance.user_accessToken, "").toString());
        hashmap.put("mode", "cart");
        hashmap.put("platform", "wap");
        NetworkRequest.getInstance().getCart(hashmap, new NetworkCallback<JsonObject>() {
            @Override
            public void onSuccess(JsonObject data) {
                Log.i(TAG, "getCart data = " + data);
                if (!data.has("list")) {
                    return;
                }

                List<ShopCar> list = GsonUtil.Companion.jsonToList(data.get("list").getAsJsonArray().toString(), ShopCar.class);
//                ArrayList<ShopCar> dataList = new ArrayList<>();
                String shopid = SharePerferenceUtil.getInstance().getValue(Constance.shop_id, "").toString();
                for (ShopCar shopCar : list) {
                    if (shopCar.getShop_id() == shopid) {
                        mShoppingCartDataList.add(shopCar);
                    }
                }

                //显示店铺名
                if (mShoppingCartDataList.size() > 0) {
                    mTvShopName.setText(mShoppingCartDataList.get(0).getShop_name());
                }
                initShoppingCartList();
            }

            @Override
            public void onFailure(int code, String msg) {

            }
        });
    }

    /**
     * 删除购物车商品
     */
    private void delCartItem() {
        HashMap<String, String> hashmap = new HashMap<String, String>();
        hashmap.put("accessToken", SharePerferenceUtil.getInstance().getValue(Constance.user_accessToken, "").toString());
        hashmap.put("mode", "cart");
        String cart_ids = "";
        ArrayList<ShopCar> datalist = new ArrayList<ShopCar>();
        if (mShoppingCartDataList != null && mShoppingCartDataList.size() > 0) {
            for (ShopCar shopCar : mShoppingCartDataList) {
                cart_ids += shopCar.getCart_id() + ",";
                cart_ids = cart_ids.substring(0, cart_ids.length() - 1);
                if (shopCar.getChecked() != 0) {
                    datalist.add(shopCar);
                }
            }
        }
        NetworkRequest.getInstance().delCart(hashmap, new NetworkCallback<JsonObject>() {
            @Override
            public void onSuccess(JsonObject data) {
                mIvCheckAll.setImageResource(R.drawable.select_grey);
                mIvCheckAll.setSelected(false);
                ArrayList<ShopCar> dataList = new ArrayList<>();
                for (ShopCar shopCar : mShoppingCartDataList) {
                    if (shopCar.getChecked() == 0) {
                        dataList.add(shopCar);
                    }
                }

                mShoppingCartDataList.clear();
                mShoppingCartDataList.addAll(dataList);
                mAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(int code, String msg) {

            }

            @Override
            public void onNetWorkFailure(Exception e) {
                super.onNetWorkFailure(e);
                ToastUtil.showToast(mActivity, "网络故障,请联系管理员!");
            }
        });
    }

    /**
     * 更新购物车商品
     */
    private void updateCart() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("accessToken", SharePerferenceUtil.getInstance().getValue(Constance.user_accessToken, "").toString());
        hashMap.put("mode", "cart");
        hashMap.put("obj_type", "item");

        String list = GsonUtil.Companion.objectToJson(mShoppingCartDataList);
        //选中的
        ArrayList<ShopCar> selList = new ArrayList<>();
        for (ShopCar shopCar : mShoppingCartDataList) {
            if (shopCar.getChecked() != 0) {
                selList.add(shopCar);
            }
        }
        JsonArray cartlist = GsonUtil.Companion.jsonToObject(list, JsonArray.class);
        if (cartlist == null) {
            return;
        }
        for (int i = 0; i < cartlist.size(); i++) {
            cartlist.get(i).getAsJsonObject().remove("title");
            cartlist.get(i).getAsJsonObject().remove("tickets");
            cartlist.get(i).getAsJsonObject().remove("total_tickets");
            cartlist.get(i).getAsJsonObject().remove("image_default_id");
            cartlist.get(i).getAsJsonObject().remove("shop_id");
            cartlist.get(i).getAsJsonObject().remove("item_id");
            cartlist.get(i).getAsJsonObject().addProperty("is_checked", cartlist.get(i).getAsJsonObject().get("checked").toString());
            cartlist.get(i).getAsJsonObject().addProperty("selected_promotion", "0");
            cartlist.get(i).getAsJsonObject().addProperty("totalQuantity", cartlist.get(i).getAsJsonObject().get("quantity").toString());
            cartlist.get(i).getAsJsonObject().remove("quantity");
            cartlist.get(i).getAsJsonObject().remove("checked");
        }

        hashMap.put("cart_params", cartlist.toString());

//        for (item in cartlist!!){
//            item.asJsonObject.remove("title")
//            item.asJsonObject.remove("tickets")
//            item.asJsonObject.remove("total_tickets")
//            item.asJsonObject.remove("image_default_id")
//            item.asJsonObject.remove("shop_id")
//            item.asJsonObject.remove("item_id")
//            item.asJsonObject.addProperty("is_checked",item.asJsonObject.get("checked").asString)
//            item.asJsonObject.addProperty("selected_promotion","0")
//            item.asJsonObject.addProperty("totalQuantity",item.asJsonObject.get("quantity").asString)
//            item.asJsonObject.remove("quantity")
//            item.asJsonObject.remove("checked")
//        }
//
//        hashmap.put("cart_params", cartlist.toString())

        if (selList.isEmpty()) {
            ToastUtil.showToast(mActivity, "请至少选择一件商品!");
            return;
        }

        NetworkRequest.getInstance().updateCart(hashMap, new NetworkCallback<JsonArray>() {
            @Override
            public void onSuccess(JsonArray data) {

            }

            @Override
            public void onFailure(int code, String msg) {

            }

            @Override
            public void onNetWorkFailure(Exception e) {
                super.onNetWorkFailure(e);
                ToastUtil.showToast(mActivity, "网络故障,请联系管理员!");
            }
        });
    }

    public interface ItemClickListener {
        void onItemViewClick(ScanResult scanResult);
    }


}

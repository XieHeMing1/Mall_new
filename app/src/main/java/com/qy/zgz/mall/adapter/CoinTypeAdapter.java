package com.qy.zgz.mall.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;
import com.qy.zgz.mall.KDSerialPort.KDSerialPort.kd;
import com.qy.zgz.mall.Model.BuyCoins;
import com.qy.zgz.mall.entities.CoinInfo;
import com.qy.zgz.mall.page.fragment.PurchaseCoinFragment;
import com.qy.zgz.mall.page.index.PurchaseCoinActivity;
import com.qy.zgz.mall.utils.LocalDefines;
import com.qy.zgz.mall.R;
import com.qy.zgz.mall.utils.SharePerferenceUtil;
import com.qy.zgz.mall.utils.Utils;
import com.qy.zgz.mall.widget.TisDialog;
import com.qy.zgz.mall.widget.TisEditDialog;
import com.zhy.autolayout.AutoRelativeLayout;

import java.util.ArrayList;
import java.util.List;

public class CoinTypeAdapter extends RecyclerView.Adapter<CoinTypeAdapter.ViewHolder> {

    private List<BuyCoins> mDataList;
    private List<CoinInfo> mTestList;
    private PurchaseCoinFragment mFragment;

    public OnClickListener mListener;

    public interface OnClickListener {
        public void OnClickListener(int position);
    }

    public void OnClickListener(OnClickListener listener) {
        this.mListener = listener;
    }

    public CoinTypeAdapter(PurchaseCoinFragment fragment, List<BuyCoins> dataList, List<CoinInfo> testList) {
        this.mFragment = fragment;
        this.mDataList = dataList;
        this.mTestList = testList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mFragment.getActivity()).inflate(R.layout.item_coin_type, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (mTestList != null && mTestList.size() > 0) {
            Log.i("CoinAdapter", "size = " + mDataList.size());
            CoinInfo coinInfo = mTestList.get(position);
            Log.i("CoinAdapter", "price = " + coinInfo.getPrice());
            switch (coinInfo.getPrice()) {
                case -1:
                    holder.mSdvCoinType.setImageURI(LocalDefines.getImgUriHead(mFragment.getActivity()) + R.drawable.ic_coin_custom_price);
                    break;
                case 5:
                    holder.mSdvCoinType.setImageURI(LocalDefines.getImgUriHead(mFragment.getActivity()) + R.drawable.ic_coin_5);
//                        holder.mTvCoinPrice.setText("￥5.00");
                    break;
                case 10:
                    holder.mSdvCoinType.setImageURI(LocalDefines.getImgUriHead(mFragment.getActivity()) + R.drawable.ic_coin_10);
//                        holder.mTvCoinPrice.setText("￥10.00");
                    break;
                case 20:
                    holder.mSdvCoinType.setImageURI(LocalDefines.getImgUriHead(mFragment.getActivity()) + R.drawable.ic_coin_20);
//                        holder.mTvCoinPrice.setText("￥20.00");
                    break;
                case 50:
                    holder.mSdvCoinType.setImageURI(LocalDefines.getImgUriHead(mFragment.getActivity()) + R.drawable.ic_coin_50);
//                        holder.mTvCoinPrice.setText("￥50.00");
                    break;
                case 100:
                    holder.mSdvCoinType.setImageURI(LocalDefines.getImgUriHead(mFragment.getActivity()) + R.drawable.ic_coin_100);
//                        holder.mTvCoinPrice.setText("￥100.00");
                    break;
                case 150:
                    holder.mSdvCoinType.setImageURI(LocalDefines.getImgUriHead(mFragment.getActivity()) + R.drawable.ic_coin_150);
//                        holder.mTvCoinPrice.setText("￥150.00");
                    break;
                default:
                    holder.mSdvCoinType.setImageURI(LocalDefines.getImgUriHead(mFragment.getActivity()) + R.drawable.ic_coin_10);
//                        holder.mTvCoinPrice.setText("￥10.00");
                    break;

            }

        }

        holder.mRlCoinType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.isFastClick(1000)) {
                    return;
                }
                String typeid = SharePerferenceUtil.getInstance().getValue("typeId", "").toString();
                //欢乐熊版本
                if (typeid == "25" && !LocalDefines.sIsLogin) {
                    TisDialog dialog = new TisDialog(mFragment.getActivity()).create().setMessage("请先登录!").show();
                    return;
                }

                boolean isSuccessOutCoin = kd.sp().getIsSuccessOutCoin();
                if (isSuccessOutCoin) {
                    TisDialog dialog = new TisDialog(mFragment.getActivity()).create().setMessage("设备没币,请移步到其他机器!!").show();
                    return;
                }

                if (mFragment.mIsSuccessOpenSerial) {
                    kd.sp().bdCoinOuted();
                    kd.sp().bdCleanError();
                    if (LocalDefines.sIsLogin && mDataList.get(position).getIsMember()) {
                        new TisDialog(mFragment.getActivity()).create().setMessage("需要会员才能购买!").show();
                    }

                    switch (mDataList.get(position).getId()) {
                        case "-1":
                            new TisEditDialog(mFragment.getActivity()).create().setEditType(InputType.TYPE_CLASS_NUMBER)
                                    .setMessage("请输入购买金额")
                                    .setNegativeButton(new TisEditDialog.NegativeButtonListener() {
                                        @Override
                                        public void onClick(View v) {

                                        }
                                    }).setPositiveButton(new TisEditDialog.PositiveButtonListener() {
                                @Override
                                public void onClick(View v, String input) {
                                    if (input != null && Integer.valueOf(input) > 0) {
                                        mFragment.autoMathPackageListNoType(input);
                                    } else {
                                        new TisDialog(mFragment.getActivity()).create().setMessage("金额不能为0或者空")
                                                .show();
                                    }
                                }
                            });
                            break;
                        default:
                            mFragment.getPackageInfo(mDataList.get(position).getId(), position);
                            break;

                    }
                } else {
                    new TisDialog(mFragment.getActivity()).create().setMessage("设备故障,请联系管理员!").show();
                }

                mListener.OnClickListener(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTestList == null ? 0 : mTestList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView mSdvCoinType;
        //        TextView mTvCoinPrice;
        AutoRelativeLayout mRlCoinType;

        public ViewHolder(View itemView) {
            super(itemView);
            mSdvCoinType = itemView.findViewById(R.id.sdv_coin_item);
//            mTvCoinPrice = itemView.findViewById(R.id.tv_coin_price);
            mRlCoinType = itemView.findViewById(R.id.rl_coin_type);
        }
    }
}

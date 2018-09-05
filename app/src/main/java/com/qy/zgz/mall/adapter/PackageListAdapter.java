package com.qy.zgz.mall.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.qy.zgz.mall.KDSerialPort.KDSerialPort.kd;
import com.qy.zgz.mall.Model.BuyCoins;
import com.qy.zgz.mall.R;
import com.qy.zgz.mall.entities.CoinInfo;
import com.qy.zgz.mall.page.index.PurchaseCoinActivity;
import com.qy.zgz.mall.utils.LocalDefines;
import com.qy.zgz.mall.utils.SharePerferenceUtil;
import com.qy.zgz.mall.utils.Utils;
import com.qy.zgz.mall.widget.TisDialog;
import com.qy.zgz.mall.widget.TisEditDialog;
import com.zhy.autolayout.AutoRelativeLayout;

import java.util.List;

public class PackageListAdapter extends RecyclerView.Adapter<PackageListAdapter.ViewHolder> {

    private List<BuyCoins> mDataList;
    private PurchaseCoinActivity mActivity;

    public PackageListAdapter.OnClickListener mListener;

    public interface OnClickListener {
        public void OnClickListener(int position);
    }

    public void OnClickListener(PackageListAdapter.OnClickListener listener) {
        this.mListener = listener;
    }

    public PackageListAdapter(PurchaseCoinActivity activity, List<BuyCoins> dataList) {
        this.mActivity = activity;
        this.mDataList = dataList;
    }

    @Override
    public PackageListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.item_package_into, parent, false);
        return new PackageListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PackageListAdapter.ViewHolder holder, int position) {
        BuyCoins buyCoins = mDataList.get(position);
        if (buyCoins.getId().equals("-1")) {
            holder.mRlCoinType.setBackgroundResource(R.drawable.ic_coin_custom_price);
        } else {
            Double price = Double.valueOf(buyCoins.getPackagePrice());
            Double coinCount = Double.valueOf(buyCoins.getCoins1());
            Double standardPoint = Double.valueOf(buyCoins.getStandardCoins());
            holder.mTvCoinCount.setText((coinCount + standardPoint) + "币");
            holder.mTvPackagePrice.setText("￥" + price);
            if (price <= 10) {
                holder.mRlCoinType.setBackgroundResource(R.drawable.ic_coin_10);
            } else if (price <= 20) {
                holder.mRlCoinType.setBackgroundResource(R.drawable.ic_coin_20);
            } else if (price <= 30) {
                holder.mRlCoinType.setBackgroundResource(R.drawable.ic_coin_30);
            } else if (price <= 50) {
                holder.mRlCoinType.setBackgroundResource(R.drawable.ic_coin_50);
            } else if (price <= 100) {
                holder.mRlCoinType.setBackgroundResource(R.drawable.ic_coin_100);
            } else if (price <= 150) {
                holder.mRlCoinType.setBackgroundResource(R.drawable.ic_coin_150);
            } else {
                holder.mRlCoinType.setBackgroundResource(R.drawable.ic_coin_200);
            }
        }

        holder.mRlCoinType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.isFastClick(1000)) {
                    return;
                }
                mListener.OnClickListener(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        AutoRelativeLayout mRlCoinType;
        TextView mTvCoinCount;
        TextView mTvPackagePrice;

        public ViewHolder(View itemView) {
            super(itemView);
            mRlCoinType = itemView.findViewById(R.id.rl_package_info);
            mTvCoinCount = itemView.findViewById(R.id.tv_coin_count);
            mTvPackagePrice = itemView.findViewById(R.id.tv_package_price);
        }
    }
}

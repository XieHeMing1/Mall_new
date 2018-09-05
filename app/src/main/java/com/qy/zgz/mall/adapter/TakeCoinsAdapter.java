package com.qy.zgz.mall.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.qy.zgz.mall.Model.TakeCoins;
import com.qy.zgz.mall.R;
import com.qy.zgz.mall.page.index.PurchaseCoinActivity;
import com.zhy.autolayout.AutoRelativeLayout;

import java.util.ArrayList;

public class TakeCoinsAdapter extends RecyclerView.Adapter<TakeCoinsAdapter.ViewHolder>{

    private PurchaseCoinActivity mActivity;
    private ArrayList<TakeCoins> mDataList;

    public TakeCoinsAdapter(PurchaseCoinActivity activity , ArrayList<TakeCoins> dataList) {
        mActivity = activity;
        mDataList = dataList;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.item_take_coins, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (mDataList.get(position).getCoinsValue().equals("-1")){
            holder.mTvCoinsType.setText("自定义");
        }
        else if(mDataList.get(position).getCoinsValue().equals("-2")){
            holder.mTvCoinsType.setText("全部");
        }
        else{
            holder.mTvCoinsType.setText(mDataList.get(position).getCoinsValue()+"币");
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListener != null) {
                    mListener.onItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTvCoinsType;

        public ViewHolder(View itemView) {
            super(itemView);
            mTvCoinsType = itemView.findViewById(R.id.item_take_coins_coin);
        }
    }

    private OnItemClickListener mListener;

    public void setOnClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
    public interface OnItemClickListener {
        public void onItemClick(int position);
    }
}

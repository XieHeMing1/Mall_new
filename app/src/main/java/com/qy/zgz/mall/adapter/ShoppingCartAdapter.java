package com.qy.zgz.mall.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.qy.zgz.mall.Model.ShopCar;
import com.qy.zgz.mall.R;

import java.util.ArrayList;

public class ShoppingCartAdapter extends RecyclerView.Adapter<ShoppingCartAdapter.ViewHolder> {
    private Activity mActivity;
    private ArrayList<ShopCar> mDataList;

    public ShoppingCartAdapter(Activity activity, ArrayList<ShopCar> dataList) {
        mActivity = activity;
        mDataList = dataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.item_shop_car, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTvGoodName.setText(mDataList.get(position).getTitle());
        holder.mTvTickets.setText(mDataList.get(position).getTickets() + "彩票");
        holder.mTvLotteryAmount.setText("小计:" + mDataList.get(position).getTotal_tickets() + "彩票");
        Glide.with(mActivity).load(mDataList.get(position).getImage_default_id()).into(holder.mIvGoodImg);
        holder.mTvGoodAmount.setText(mDataList.get(position).getQuantity());
        holder.mIvSelect.setSelected(mDataList.get(position).getChecked() == 0 ? false : true);

        //设置总价
        double carshoptotal = 0.00;
//        carshoptotal = mDataList.
        if (mDataList.get(position).getChecked() != 0) {
            carshoptotal = carshoptotal + Double.valueOf(mDataList.get(position).getTickets());
        }

        //TODO 彩票总价设置在哪里好
//        tv_shop_car_total.text="合计:"+carshoptotal.toString()+"彩票"
        holder.mBtnIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int num = Integer.valueOf(holder.mTvGoodAmount.getText().toString());
                if (num == 99) {
                    return;
                }
                holder.mTvGoodAmount.setText((num + 1) + "");
                mDataList.get(position).setQuantity(holder.mTvGoodAmount.getText().toString());
                mDataList.get(position).setTotal_tickets((Integer.valueOf(mDataList.get(position).getQuantity()) * Double.valueOf(mDataList.get(position).getTickets()))+ "");
//                =(list[position].quantity.toInt()*list[position].tickets.toDouble()).toString()
                notifyItemChanged(position);
            }
        });

        holder.mBtnDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int num = Integer.valueOf(holder.mTvGoodAmount.getText().toString());
                if (num == 1) {
                    return;
                }
                holder.mTvGoodAmount.setText((num - 1) + "");
                mDataList.get(position).setQuantity(holder.mTvGoodAmount.getText().toString());
                mDataList.get(position).setTotal_tickets((Integer.valueOf(mDataList.get(position).getQuantity()) * Double.valueOf(mDataList.get(position).getTickets()))+ "");
//                =(list[position].quantity.toInt()*list[position].tickets.toDouble()).toString()
                notifyItemChanged(position);
            }
        });

        holder.mIvSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (0!=mDataList.get(position).getChecked()){
                    mDataList.get(position).setChecked(0);
                    holder.mIvSelect.setImageResource(R.drawable.select_grey);
                }else{
                    mDataList.get(position).setChecked(1);
                    holder.mIvSelect.setImageResource(R.drawable.select);
                }
                notifyItemChanged(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mIvSelect;
        ImageView mIvGoodImg;
        TextView mTvGoodName;
        TextView mTvTickets;
        Button mBtnDecrease;
        Button mBtnIncrease;
        TextView mTvGoodAmount;
        TextView mTvLotteryAmount;

        public ViewHolder(View view) {
            super(view);
            mIvSelect = view.findViewById(R.id.iv_item_shop_car_sel);
            mIvGoodImg = view.findViewById(R.id.iv_item_shop_car_goodsimg);
            mTvGoodName = view.findViewById(R.id.tv_item_shop_car_goodname);
            mTvTickets = view.findViewById(R.id.tv_item_shop_car_tickets);
            mBtnDecrease = view.findViewById(R.id.btn_item_shop_car_reductnum);
            mBtnIncrease = view.findViewById(R.id.btn_item_shop_car_addnum);
            mTvGoodAmount = view.findViewById(R.id.tv_item_shop_car_num);
            mTvLotteryAmount = view.findViewById(R.id.tv_item_shop_car_total);
        }
    }
}

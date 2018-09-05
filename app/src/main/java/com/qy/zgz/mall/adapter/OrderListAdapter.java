package com.qy.zgz.mall.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.qy.zgz.mall.Model.MyOrder;
import com.qy.zgz.mall.Model.MyOrderContent;
import com.qy.zgz.mall.Model.MyOrderDetail;
import com.qy.zgz.mall.Model.MyOrderDetailImg;
import com.qy.zgz.mall.Model.MyOrderImg;
import com.qy.zgz.mall.R;

import java.util.ArrayList;
import java.util.List;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.OrderListViewHolder> {

    private Context mContext;
    private List<MyOrder> mDataList;
//    private ArrayList<MyOrderImg> mOrderList;
    public OnClickListener mListener;
    public interface OnClickListener{
        void OnClickListener(int position);
    }

    public void setOrderListClickerListener (OnClickListener listener) {
        mListener = listener;
    }

    public OrderListAdapter(Context context, List<MyOrder> dataList /*ArrayList<MyOrderImg> mOrderList*/){
        mContext = context;
        mDataList = dataList;
//        this.mOrderList = mOrderList;

    }
    @Override
    public OrderListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_order_detail, parent, false);
        return new OrderListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OrderListViewHolder holder, int position) {
//        Log.i("OrderListAdapter", " mDataList size = " + mDataList.size());
        MyOrder orderInfo = mDataList.get(position);
        MyOrderImg orderImg = mDataList.get(position).getOrder().get(0);
//        Log.i("OrderListAdapter", "orderInfo.getTitle() =  " + mOrderList.get(position).getTitle());
//        Log.i("OrderListAdapter", "orderInfo.getNum() =  " + mOrderList.get(position).getNum());
//        Log.i("OrderListAdapter", "orderInfo.getShop_id() =  " + mOrderList.get(position).getShop_id());
//        ArrayList<MyOrderImg> orderList = mDataList.get(position).getOrder();
//        Log.i("OrderListAdapter", "orderInfo.getShop_name() =  " + orderInfo.getShop_name());

        Log.i("OrderListAdapter", "orderImg.getPic_path() =  " + orderImg.getPic_path());
        holder.mTvGoodsName.setText(orderInfo.getShop_name() + "");
        holder.mTvTickets.setText(orderInfo.getTotal_tickets() + "");
        holder.mTvOrderNum.setText(orderInfo.getTotal_num() + "");
        Glide.with(mContext).load(orderImg.getPic_path() + "").into(holder.mIvGoodIcon);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    static class OrderListViewHolder extends RecyclerView.ViewHolder {
        ImageView mIvGoodIcon;
        TextView mTvGoodsName;
        TextView mTvOrderNum;
        TextView mTvTickets;
        public OrderListViewHolder(View view) {
            super(view);
            mIvGoodIcon = view.findViewById(R.id.tv_item_order_detail_goodsimg);
            mTvGoodsName = view.findViewById(R.id.tv_item_order_detail_goodsname);
            mTvOrderNum = view.findViewById(R.id.tv_item_order_detail_num);
            mTvTickets = view.findViewById(R.id.tv_item_order_detail_tickets);
        }
    }
}

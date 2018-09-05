package com.qy.zgz.mall.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.qy.zgz.mall.Model.PurchaseRecord;
import com.qy.zgz.mall.R;
import com.zhy.autolayout.AutoRelativeLayout;

import java.util.List;

public class ConsumptionListAdapter extends RecyclerView.Adapter<ConsumptionListAdapter.ViewHolder> {

    private List<PurchaseRecord> mDataList;
    private Context mContext;

    public ConsumptionListAdapter(Context context, List<PurchaseRecord> dataList) {
        mContext = context;
        mDataList = dataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_consumption_record, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.i("XHM_TEST", "onBindViewHolder mDataList size = " + mDataList.size());
        PurchaseRecord purchaseRecord = mDataList.get(position);
        holder.tv_purchase_record_date.setText(purchaseRecord.getDate());
        Log.i("XHM_TEST", "onBindViewHolder purchaseRecord.getDate() = " + purchaseRecord.getDate());
        holder.tv_purchase_record_type.setText(purchaseRecord.getOutTypeKey());
        Log.i("XHM_TEST", "onBindViewHolder purchaseRecord.getOutTypeKey() = " + purchaseRecord.getOutTypeKey());
        holder.tv_purchase_record_price.setText(purchaseRecord.getAmount());
        Log.i("XHM_TEST", "onBindViewHolder purchaseRecord.getAmount() = " + purchaseRecord.getAmount());
        holder.tv_purchase_record_coins.setText(purchaseRecord.getCoin1());
        Log.i("XHM_TEST", "onBindViewHolder purchaseRecord.getCoin1() = " + purchaseRecord.getCoin1());
        holder.tv_purchase_record_recoins.setText(purchaseRecord.getCoin2());
        Log.i("XHM_TEST", "onBindViewHolder purchaseRecord.getCoin2() = " + purchaseRecord.getCoin2());
        holder.tv_purchase_record_deposit.setText(purchaseRecord.getMoney());
        Log.i("XHM_TEST", "onBindViewHolder purchaseRecord.getMoney() = " + purchaseRecord.getMoney());
        holder.tv_purchase_record_ticket.setText(purchaseRecord.getTicket());
        Log.i("XHM_TEST", "onBindViewHolder purchaseRecord.getTicket() = " + purchaseRecord.getTicket());
        holder.tv_purchase_record_point.setText(purchaseRecord.getPoint());
        Log.i("XHM_TEST", "onBindViewHolder purchaseRecord.getPoint() = " + purchaseRecord.getPoint());
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_purchase_record_date, tv_purchase_record_type, tv_purchase_record_price,
                tv_purchase_record_coins, tv_purchase_record_recoins, tv_purchase_record_deposit,
                tv_purchase_record_ticket, tv_purchase_record_point;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_purchase_record_date = (TextView) itemView.findViewById(R.id.tv_purchase_record_date);
            tv_purchase_record_type = (TextView) itemView.findViewById(R.id.tv_purchase_record_type);
            tv_purchase_record_price = (TextView) itemView.findViewById(R.id.tv_purchase_record_price);
            tv_purchase_record_coins = (TextView) itemView.findViewById(R.id.tv_purchase_record_coins);
            tv_purchase_record_recoins = (TextView) itemView.findViewById(R.id.tv_purchase_record_recoins);
            tv_purchase_record_deposit = (TextView) itemView.findViewById(R.id.tv_purchase_record_deposit);
            tv_purchase_record_ticket = (TextView) itemView.findViewById(R.id.tv_purchase_record_ticket);
            tv_purchase_record_point = (TextView) itemView.findViewById(R.id.tv_purchase_record_point);
        }
    }
}

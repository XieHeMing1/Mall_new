package com.qy.zgz.mall.page.max;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.qy.zgz.mall.R;

import java.util.List;

/**
 * 闲情页adapter
 */
public class MaxListAdapter extends RecyclerView.Adapter {
    private List<String> listStr;
    public MaxListAdapter(List<String> listStr)
    {
        this.listStr=listStr;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_max_image,null);
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new MaxImageHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MaxImageHolder maxImageHolder=(MaxImageHolder)holder;

        maxImageHolder.showData(listStr.get(position));
    }

    @Override
    public int getItemCount() {
        return listStr.size();
    }

    public void setListStr(List<String> listStr)
    {
        this.listStr=listStr;
    }


}

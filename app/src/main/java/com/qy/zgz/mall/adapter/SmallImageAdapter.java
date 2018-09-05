package com.qy.zgz.mall.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.qy.zgz.mall.Model.Cinemadata;
import com.qy.zgz.mall.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 小图adapter
 */
public class SmallImageAdapter extends RecyclerView.Adapter {
    private List<Cinemadata> listStr=new ArrayList<>();

    public SmallImageAdapter(List<Cinemadata> listStr) {
        this.listStr = listStr;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new SmallImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SmallImageViewHolder imageViewHolder = (SmallImageViewHolder) holder;
        imageViewHolder.showData(listStr.get(position));
    }

    @Override
    public int getItemCount() {
        if(listStr==null)
        {
            return 0;
        }
        return listStr.size();
    }

    public void setListStr(List<Cinemadata> listStr) {
        this.listStr = listStr;
    }
}

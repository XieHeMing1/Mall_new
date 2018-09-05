package com.qy.zgz.mall.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.qy.zgz.mall.Model.BalanceCar
import com.qy.zgz.mall.R

/**
 * Created by LCB on 2018/3/26.
 */

public class BalanceAdapter(var mcontext: Context, var list: ArrayList<BalanceCar>) : RecyclerView.Adapter<BalanceAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        var holder= ViewHolder(View.inflate(mcontext, R.layout.item_balance_car, null))

        return holder

    }

    override fun getItemCount(): Int {
        if (list.size>2){
            return 2
        }
        return list.size

    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        Glide.with(mcontext).load(list[position].image_default_id).into(holder!!.iv_balance_goodsimg)

    }


    class ViewHolder( itemView: View) : RecyclerView.ViewHolder(itemView) {
        var iv_balance_goodsimg: ImageView =itemView!!.findViewById(R.id.iv_balance_goodsimg)


    }


}
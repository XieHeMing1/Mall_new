package com.qy.zgz.mall.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.qy.zgz.mall.Model.BalanceCar
import com.qy.zgz.mall.R

/**
 * Created by LCB on 2018/3/26.
 */

public class BalanceGoodsDetailAdapter(var mcontext: Context, var list: ArrayList<BalanceCar>) : RecyclerView.Adapter<BalanceGoodsDetailAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        var holder= ViewHolder(View.inflate(mcontext, R.layout.item_balance_goodsdetail_car, null))

        return holder

    }

    override fun getItemCount(): Int {
        return list.size

    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder!!.tv_item_balance_goodsdetail_count.text="x"+list[position].quantity
        holder!!.tv_item_balance_goodsdetail_ticket.text=list[position].price.get("tickets").asString
        holder!!.tv_item_balance_goodsdetail_title.text=list[position].title
        Glide.with(mcontext).load(list[position].image_default_id).into(holder!!.iv_balance_iv_item_balance_goodsdetail_goodsimggoodsimg)

    }


    class ViewHolder( itemView: View) : RecyclerView.ViewHolder(itemView) {
        var iv_balance_iv_item_balance_goodsdetail_goodsimggoodsimg: ImageView =itemView!!.findViewById(R.id.iv_item_balance_goodsdetail_goodsimg)
        var tv_item_balance_goodsdetail_count: TextView =itemView!!.findViewById(R.id.tv_item_balance_goodsdetail_count)
        var tv_item_balance_goodsdetail_ticket: TextView =itemView!!.findViewById(R.id.tv_item_balance_goodsdetail_ticket)
        var tv_item_balance_goodsdetail_title: TextView =itemView!!.findViewById(R.id.tv_item_balance_goodsdetail_title)


    }


}
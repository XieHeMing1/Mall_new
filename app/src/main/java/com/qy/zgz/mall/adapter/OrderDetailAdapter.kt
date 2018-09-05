package com.qy.zgz.mall.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.qy.zgz.mall.Model.MyOrderDetailImg
import com.qy.zgz.mall.R

/**
 * Created by LCB on 2018/3/26.
 */

public class OrderDetailAdapter(var mcontext: Context, var list: ArrayList<MyOrderDetailImg>) : RecyclerView.Adapter<OrderDetailAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        var holder= ViewHolder(View.inflate(mcontext, R.layout.item_order_detail, null))

        return holder

    }

    override fun getItemCount(): Int {
        return list.size

    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder!!.tv_item_order_detail_num.text="x"+list[position].num
        holder!!.tv_item_order_detail_tickets.text="彩票数: "+list[position].tickets_fee
        holder!!.tv_item_order_detail_goodsname.text=list[position].title
        Glide.with(mcontext).load(list[position].pic_path).into(holder!!.tv_item_order_detail_goodsimg)

    }

    class ViewHolder( itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tv_item_order_detail_goodsname: TextView =itemView!!.findViewById(R.id.tv_item_order_detail_goodsname)
        var tv_item_order_detail_goodsimg:ImageView=itemView!!.findViewById(R.id.tv_item_order_detail_goodsimg)
        var tv_item_order_detail_num: TextView =itemView!!.findViewById(R.id.tv_item_order_detail_num)
        var tv_item_order_detail_tickets: TextView =itemView!!.findViewById(R.id.tv_item_order_detail_tickets)

    }


}
package com.qy.zgz.mall.adapter

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.qy.zgz.mall.Model.MyOrder
import com.qy.zgz.mall.Model.MyOrderImg
import com.qy.zgz.mall.R
import com.qy.zgz.mall.page.index.MallActivity
import com.qy.zgz.mall.page.fragment.OrderDetailFragment
import com.zhy.autolayout.utils.AutoUtils

/**
 * Created by LCB on 2018/3/26.
 */

public class MyOrderAdapter(var mcontext: Context, var list: ArrayList<MyOrder>) : RecyclerView.Adapter<MyOrderAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        var holder= ViewHolder(View.inflate(mcontext, R.layout.item_myorder, null))

        return holder

    }

    override fun getItemCount(): Int {
        return list.size

    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder!!.tv_item_myorder_tickets.text=list[position].total_tickets+"彩票"
        holder!!.tv_item_myorder_shopname.text=list[position].shop_name
        holder!!.tv_item_myoder_detail.text="共"+list[position].total_num+"件"
        when {
            list[position].status == "WAIT_BUYER_PAY" -> holder!!.tv_item_myorder_status.text= "未付款"
            list[position].status == "WAIT_SELLER_SEND_GOODS" -> holder!!.tv_item_myorder_status.text= "已付款"
            list[position].status == "WAIT_BUYER_CONFIRM_GOODS" -> holder!!.tv_item_myorder_status.text= "已发货"
            else -> holder!!.tv_item_myorder_status.text= "已完成"
        }
        //设置布局管理器()
        val linearLayoutManager = LinearLayoutManager(mcontext)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        holder!!.rv_item_myorder_img.layoutManager=linearLayoutManager
        holder!!.rv_item_myorder_img.addItemDecoration(object :RecyclerView.ItemDecoration(){
            override fun getItemOffsets(outRect: Rect?, view: View?, parent: RecyclerView?, state: RecyclerView.State?) {
                outRect!!.left= AutoUtils.getPercentWidthSize(50)
            }
        })

        var adapter= MyOrderImgAdapter(mcontext, list[position].order
                ?: ArrayList<MyOrderImg>())
        holder!!.rv_item_myorder_img.adapter=adapter

        holder!!.tv_item_myoder_detail.setOnClickListener {
            var orderDetailFragment= OrderDetailFragment()
            var bundle=Bundle()
            bundle.putString("tid",list[position].tid)
            orderDetailFragment.arguments=bundle
            (mcontext as MallActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.main_fragment_content,orderDetailFragment).commit()
        }
    }


    class ViewHolder( itemView: View) : RecyclerView.ViewHolder(itemView) {
        var rv_item_myorder_img: RecyclerView =itemView!!.findViewById(R.id.rv_item_myorder_img)
        var tv_item_myorder_status: TextView =itemView!!.findViewById(R.id.tv_item_myorder_status)
        var tv_item_myorder_tickets: TextView =itemView!!.findViewById(R.id.tv_item_myorder_tickets)
        var btn_item_myorder_cancel: Button =itemView!!.findViewById(R.id.btn_item_myorder_cancel)
        var tv_item_myorder_shopname: TextView =itemView!!.findViewById(R.id.tv_item_myorder_shopname)
        var tv_item_myoder_detail: TextView =itemView!!.findViewById(R.id.tv_item_myoder_detail)


    }


}
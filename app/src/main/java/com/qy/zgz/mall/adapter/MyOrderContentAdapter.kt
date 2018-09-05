package com.qy.zgz.mall.adapter

import android.content.Context
import android.graphics.Rect
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.qy.zgz.mall.Model.MyOrder
import com.qy.zgz.mall.Model.MyOrderContent
import com.qy.zgz.mall.R
import com.zhy.autolayout.utils.AutoUtils

/**
 * Created by LCB on 2018/3/26.
 */

public class MyOrderContentAdapter(var mcontext: Context, var list: ArrayList<MyOrderContent>) : RecyclerView.Adapter<MyOrderContentAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {

        var holder= ViewHolder(View.inflate(mcontext, R.layout.item_myordercontent, null))

        return holder

    }

    override fun getItemCount(): Int {
        return list.size

    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {

        //设置布局管理器()
        val linearLayoutManager = LinearLayoutManager(mcontext)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        if ( holder!!.rv_myorder_content_info.layoutManager==null){
        holder!!.rv_myorder_content_info.layoutManager=linearLayoutManager
        holder!!.rv_myorder_content_info.addItemDecoration(object : RecyclerView.ItemDecoration(){
            override fun getItemOffsets(outRect: Rect?, view: View?, parent: RecyclerView?, state: RecyclerView.State?) {
                outRect!!.bottom= AutoUtils.getPercentHeightSize(50)
            }
        })
        }

        var adapter= MyOrderAdapter(mcontext, list[position].order
                ?: ArrayList<MyOrder>())
        holder!!.rv_myorder_content_info.adapter=adapter

        if(adapter.list==null||adapter.list.size==0){
           holder!!.tv_item_myorder_content_nodata.visibility=View.VISIBLE
        }else{
            holder!!.tv_item_myorder_content_nodata.visibility=View.GONE
        }

    }


    class ViewHolder( itemView: View) : RecyclerView.ViewHolder(itemView) {
        var rv_myorder_content_info: RecyclerView =itemView!!.findViewById(R.id.rv_myorder_content_info)
        var tv_item_myorder_content_nodata: TextView =itemView!!.findViewById(R.id.tv_item_myorder_content_nodata)


    }




}
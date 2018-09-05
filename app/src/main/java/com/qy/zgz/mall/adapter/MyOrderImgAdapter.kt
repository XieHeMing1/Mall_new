package com.qy.zgz.mall.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.qy.zgz.mall.Model.MyOrderImg
import com.qy.zgz.mall.R

/**
 * Created by LCB on 2018/3/26.
 */

public class MyOrderImgAdapter(var mcontext: Context, var list: ArrayList<MyOrderImg>) : RecyclerView.Adapter<MyOrderImgAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        var holder= ViewHolder(View.inflate(mcontext, R.layout.item_myorderimg, null))

        return holder

    }

    override fun getItemCount(): Int {
        if (list.size>2){
            return  2
        }
        return list.size

    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        Glide.with(mcontext).load(list[position].pic_path).into(holder!!.iv_item_myoderimg_img)

    }


    class ViewHolder( itemView: View) : RecyclerView.ViewHolder(itemView) {
        var iv_item_myoderimg_img: ImageView =itemView!!.findViewById(R.id.iv_item_myoderimg_img)

    }


}
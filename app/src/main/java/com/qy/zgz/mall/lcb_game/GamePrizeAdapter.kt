package com.qy.zgz.mall.lcb_game

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.qy.zgz.mall.R

import com.zhy.autolayout.AutoRelativeLayout

/**
 * Created by LCB on 2018/8/17.
 *
 */
public class GamePrizeAdapter(var mContext:Context, var list:ArrayList<String>,var isShowSelect:Boolean,var sumPrizeNum:Int): RecyclerView.Adapter<GamePrizeAdapter.ViewHolder>() {
    private  var lastSelect=0

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder? {
        var holder=ViewHolder(View.inflate(mContext, R.layout.item_game_prize, AutoRelativeLayout(mContext)))
        return  holder
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        if (isShowSelect){
            holder!!.iv_item_game_prize_img.isSelected = lastSelect==position
        }
    }

    override fun getItemCount(): Int {
        return  sumPrizeNum
    }

    class ViewHolder(itemView:View): RecyclerView.ViewHolder(itemView){
        var iv_item_game_prize_img:ImageView=itemView.findViewById(R.id.iv_item_game_prize_img)

    }

    public fun setLastSelect(ls:Int){
        lastSelect=ls
    }

    public fun getLastSelect():Int{
        return lastSelect
    }


}
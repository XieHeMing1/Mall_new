package com.qy.zgz.mall.page.money_purchase.take_coin

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.qy.zgz.mall.Model.TakeCoins
import com.qy.zgz.mall.R

/**
 * Created by LCB on 2018/3/26.
 */

public class TakeCoinsAdapter(var mcontext: Context, var list: ArrayList<TakeCoins>,var adapterItemClickListener:AdapterItemClickListener) : RecyclerView.Adapter<TakeCoinsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        var holder=ViewHolder(View.inflate(mcontext, R.layout.item_take_coins, null))

        return holder

    }

    override fun getItemCount(): Int {
        return list.size

    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        if (list[position].coinsValue=="-1"){
            holder!!.item_take_coins_coin.text="自定义"
        }
        else if(list[position].coinsValue=="-2"){
            holder!!.item_take_coins_coin.text="全部"
        }
        else{
            holder!!.item_take_coins_coin.text=list[position].coinsValue+"币"

        }

        holder!!.itemView.setOnClickListener {
            adapterItemClickListener.itemClick(position)
        }

    }


    class ViewHolder( itemView: View) : RecyclerView.ViewHolder(itemView) {
        var item_take_coins_coin: TextView =itemView!!.findViewById(R.id.item_take_coins_coin)

    }

     public interface AdapterItemClickListener{
        fun itemClick(position: Int)
    }


}
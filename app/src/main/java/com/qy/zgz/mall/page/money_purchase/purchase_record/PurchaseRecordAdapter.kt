package com.qy.zgz.mall.page.money_purchase.purchase_record

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.qy.zgz.mall.Model.PurchaseRecord
import com.qy.zgz.mall.R

/**
 * Created by LCB on 2018/3/26.
 */

public class PurchaseRecordAdapter(var mcontext: Context, var list: List<PurchaseRecord>) : RecyclerView.Adapter<PurchaseRecordAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        var holder=ViewHolder(View.inflate(mcontext, R.layout.item_purchase_record, null))

        return holder

    }

    override fun getItemCount(): Int {
        return list.size

    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {

        try {

                var date=list[position].Date.replace("T"," ")
                if(date.indexOf(".")!=-1){
                    date=date.substring(0,date.indexOf("."))
                }
                holder!!.tv_purchase_record_date.text=date
        }catch (e:Exception){
            holder!!.tv_purchase_record_date.text=list[position].Date
        }



        holder!!.tv_purchase_record_type.text=list[position].OutTypeKey
        holder!!.tv_purchase_record_price.text=list[position].Amount
        holder!!.tv_purchase_record_coins.text=list[position].Coin1
        holder!!.tv_purchase_record_recoins.text=list[position].u4
        holder!!.tv_purchase_record_deposit.text=list[position].Money
        holder!!.tv_purchase_record_ticket.text=list[position].Ticket
        holder!!.tv_purchase_record_point.text=list[position].Point

    }


    class ViewHolder( itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tv_purchase_record_date: TextView =itemView!!.findViewById(R.id.tv_purchase_record_date)
        var tv_purchase_record_type: TextView =itemView!!.findViewById(R.id.tv_purchase_record_type)
        var tv_purchase_record_coins: TextView =itemView!!.findViewById(R.id.tv_purchase_record_coins)
        var tv_purchase_record_recoins: TextView =itemView!!.findViewById(R.id.tv_purchase_record_recoins)
        var tv_purchase_record_deposit: TextView =itemView!!.findViewById(R.id.tv_purchase_record_deposit)
        var tv_purchase_record_point: TextView =itemView!!.findViewById(R.id.tv_purchase_record_point)
        var tv_purchase_record_price: TextView =itemView!!.findViewById(R.id.tv_purchase_record_price)
        var tv_purchase_record_ticket: TextView =itemView!!.findViewById(R.id.tv_purchase_record_ticket)

    }


}
package com.qy.zgz.mall.page.money_purchase.error_handle

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.qy.zgz.mall.Model.Exceptionhanding
import com.qy.zgz.mall.R
import com.zhy.autolayout.AutoLinearLayout

/**
 * Created by LCB on 2018/3/26.
 */

public class ExceptionHandingAdapter(var mcontext: Context, var list: ArrayList<Exceptionhanding>) : RecyclerView.Adapter<ExceptionHandingAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        var holder=ViewHolder(View.inflate(mcontext, R.layout.item_exception_handing, null))

        return holder

    }

    override fun getItemCount(): Int {
        return list.size

    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {

//        if (position%2==0){
//            holder!!.itemView.setBackgroundColor(ContextCompat.getColor(mcontext, R.color.color_lightblue))
//
//        }else{
//            holder!!.itemView.setBackgroundColor(ContextCompat.getColor(mcontext,R.color.coloer_wither))
//
//        }

        try {

            var date=list[position].Date.replace("T"," ")
            if(date.indexOf(".")!=-1){
                    date=date.substring(0,date.indexOf("."))
            }
            holder!!.tv_exception_handing_date.text=date

        }catch (e:Exception){
            holder!!.tv_exception_handing_date.text=list[position].Date
        }

        when(list[position].ErrType){
            "CashErr"->{
                holder!!.tv_exception_handing_error_msg.text="现金"
            }
            "MobileErr"->{
                holder!!.tv_exception_handing_error_msg.text="移动支付"
            }
            "OutCoinErr"->{
                holder!!.tv_exception_handing_error_msg.text="出币异常"
            }

        }

        holder!!.tv_exception_handing_custname.text=list[position].CustomerName
        holder!!.tv_exception_handing_orderstatus.text=list[position].Status
        holder!!.tv_exception_handing_ordernum.text=list[position].POS
        holder!!.tv_exception_handing_amount.text=list[position].Amount
        holder!!.tv_exception_handing_withoutcoin.text=list[position].DiffCoin

        holder!!.all_exception_handing_all.setOnClickListener {
            v->
            list.forEach {
                it.ischeck=false
            }

            list[position].ischeck=true
            notifyDataSetChanged()
        }

        if (list[position].ischeck){
            holder!!.all_exception_handing_all.setBackgroundColor(ContextCompat.getColor(mcontext,R.color.color_grey))
        }else{
            holder!!.all_exception_handing_all.setBackgroundColor(ContextCompat.getColor(mcontext,R.color.coloer_wither))
        }

    }


    class ViewHolder( itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tv_exception_handing_date: TextView =itemView!!.findViewById(R.id.tv_exception_handing_date)
        var tv_exception_handing_error_msg: TextView =itemView!!.findViewById(R.id.tv_exception_handing_error_msg)
        var tv_exception_handing_custname: TextView =itemView!!.findViewById(R.id.tv_exception_handing_custname)
        var tv_exception_handing_orderstatus: TextView =itemView!!.findViewById(R.id.tv_exception_handing_orderstatus)
        var tv_exception_handing_ordernum: TextView =itemView!!.findViewById(R.id.tv_exception_handing_ordernum)
        var tv_exception_handing_amount: TextView =itemView!!.findViewById(R.id.tv_exception_handing_amount)
        var tv_exception_handing_withoutcoin: TextView =itemView!!.findViewById(R.id.tv_exception_handing_withoutcoin)
        var all_exception_handing_all: AutoLinearLayout =itemView!!.findViewById(R.id.all_exception_handing_all)


    }


}
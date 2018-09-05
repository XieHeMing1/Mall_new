package com.qy.zgz.mall.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.qy.zgz.mall.Model.ShopCar
import com.qy.zgz.mall.R


/**
 * Created by LCB on 2018/3/26.
 */

public class ShopCarAdapter(var mcontext: Context, var list: ArrayList<ShopCar>,var tv_shop_car_total:TextView) : RecyclerView.Adapter<ShopCarAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        var holder= ViewHolder(View.inflate(mcontext, R.layout.item_shop_car, null))

        return holder

    }

    override fun getItemCount(): Int {
        return list.size

    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {

        holder!!.tv_item_shop_car_goodname.setText(list[position].title)
        holder!!.tv_item_shop_car_tickets.setText(list[position].tickets+"彩票")
        holder!!.tv_item_shop_car_total.setText("小计:"+list[position].total_tickets+"彩票")
        Glide.with(mcontext).load(list[position].image_default_id).into(holder!!.iv_item_shop_car_goodsimg)
        holder!!.tv_item_shop_car_num.text=list[position].quantity
        holder!!.iv_item_shop_car_sel.isSelected = 0 != list[position].checked

        //设置总价
        val carshoptotal:Double= list
                .filter { 0!= it.checked }
                .sumByDouble { it.total_tickets.toDouble() }

        tv_shop_car_total.text="合计:"+carshoptotal.toString()+"彩票"

        holder!!.btn_item_shop_car_addnum.setOnClickListener {
            v->
            var num=holder!!.tv_item_shop_car_num.text.toString().toInt()
            if (num==99){
                return@setOnClickListener
            }
            holder!!.tv_item_shop_car_num.text=(num+1).toString()
            list[position].quantity=holder!!.tv_item_shop_car_num.text.toString()
            list[position].total_tickets=(list[position].quantity.toInt()*list[position].tickets.toDouble()).toString()
            notifyItemChanged(position)
        }

        holder!!.btn_item_shop_car_reductnum.setOnClickListener {
            v->
            var num=holder!!.tv_item_shop_car_num.text.toString().toInt()
            if (num==1){
                return@setOnClickListener
            }
            holder!!.tv_item_shop_car_num.text=(num-1).toString()
            list[position].quantity=holder!!.tv_item_shop_car_num.text.toString()
            list[position].total_tickets=(list[position].quantity.toInt()*list[position].tickets.toDouble()).toString()
            notifyItemChanged(position)
        }

        holder!!.iv_item_shop_car_sel.setOnClickListener{
            v->
            if (0!=list[position].checked){
                list[position].checked = 0
            }else{
                list[position].checked = 1
            }
            notifyItemChanged(position)
        }

        if (0!=list[position].checked){
            holder!!.iv_item_shop_car_sel.setImageResource(R.drawable.select)
        }else{
            holder!!.iv_item_shop_car_sel.setImageResource(R.drawable.select_grey)
        }
    }


    class ViewHolder( itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tv_item_shop_car_goodname:TextView=itemView!!.findViewById(R.id.tv_item_shop_car_goodname)
        var tv_item_shop_car_tickets:TextView=itemView!!.findViewById(R.id.tv_item_shop_car_tickets)
        var tv_item_shop_car_total:TextView=itemView!!.findViewById(R.id.tv_item_shop_car_total)
        var iv_item_shop_car_goodsimg:ImageView=itemView!!.findViewById(R.id.iv_item_shop_car_goodsimg)
        var iv_item_shop_car_sel:ImageView=itemView!!.findViewById(R.id.iv_item_shop_car_sel)
        var tv_item_shop_car_num:TextView=itemView!!.findViewById(R.id.tv_item_shop_car_num)

        var btn_item_shop_car_addnum:Button=itemView!!.findViewById(R.id.btn_item_shop_car_addnum)
        var btn_item_shop_car_reductnum:Button=itemView!!.findViewById(R.id.btn_item_shop_car_reductnum)


    }


}

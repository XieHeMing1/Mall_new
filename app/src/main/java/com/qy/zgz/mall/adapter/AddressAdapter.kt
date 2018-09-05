package com.qy.zgz.mall.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.qy.zgz.mall.Model.Address
import com.qy.zgz.mall.R

/**
 * Created by LCB on 2018/3/26.
 */

public class AddressAdapter(var mcontext: Context, var list: ArrayList<Address>) : RecyclerView.Adapter<AddressAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        var holder= ViewHolder(View.inflate(mcontext, R.layout.item_address, null))

        return holder

    }

    override fun getItemCount(): Int {
        return list.size

    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder!!.item_tv_address_address.text=list[position].area+list[position].addr
        holder!!.item_tv_address_phone.text=list[position].mobile
        holder!!.item_tv_address_username.text=list[position].name
        if (list[position].def_addr == "1"){
        holder!!.item_tv_address_default.visibility=View.VISIBLE
        }else{
            holder!!.item_tv_address_default.visibility=View.INVISIBLE
        }
    }


    class ViewHolder( itemView: View) : RecyclerView.ViewHolder(itemView) {
        var item_tv_address_default:TextView=itemView!!.findViewById(R.id.item_tv_address_default)
        var item_tv_address_username:TextView=itemView!!.findViewById(R.id.item_tv_address_username)
        var item_tv_address_phone:TextView=itemView!!.findViewById(R.id.item_tv_address_phone)
        var item_tv_address_address:TextView=itemView!!.findViewById(R.id.item_tv_address_address)

    }


}
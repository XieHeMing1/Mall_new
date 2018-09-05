package com.qy.zgz.mall.page.money_purchase

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.text.InputType
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.gson.JsonObject
import com.kaopiz.kprogresshud.KProgressHUD
import com.qy.zgz.mall.KDSerialPort.KDSerialPort.kd
import com.qy.zgz.mall.Model.BuyCoins
import com.qy.zgz.mall.Model.MemberInfo
import com.qy.zgz.mall.R
import com.qy.zgz.mall.network.Constance
import com.qy.zgz.mall.network.XutilsCallback
import com.qy.zgz.mall.utils.*
import com.qy.zgz.mall.widget.TisDialog
import com.qy.zgz.mall.widget.TisEditDialog
import com.zhy.autolayout.AutoLinearLayout
import com.zhy.autolayout.AutoRelativeLayout
import org.xutils.common.Callback
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by LCB on 2018/3/26.
 */

public class BuyCoinsAdapter(var mcontext: Context, var list: ArrayList<BuyCoins>) : RecyclerView.Adapter<BuyCoinsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        var holder=ViewHolder(View.inflate(mcontext, R.layout.item_buy_coins, null))

        return holder

    }

    override fun getItemCount(): Int {
        return list.size

    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        //自由购买
        when(list[position].Id){
            "-1"->{
                holder!!.all_item_buy_coins_bottom.visibility=View.GONE
                holder!!.arl_item_buy_coins_top.visibility=View.GONE
                holder!!.tv_item_buy_coins_freedom.text = "自由\n购买"
                holder!!.tv_item_buy_coins_freedom.visibility=View.VISIBLE
            }
            else->{
                holder!!.all_item_buy_coins_bottom.visibility=View.VISIBLE
                holder!!.arl_item_buy_coins_top.visibility=View.VISIBLE
                holder!!.tv_item_buy_coins_freedom.visibility=View.GONE
                holder!!.tv_item_buy_coins_price.text="¥"+list[position].PackagePrice
                holder!!.tv_item_buy_coins_coin.text=(list[position].Coins1.toDouble().toInt()+list[position].StandardCoins.toDouble().toInt()).toString()+"币"
                holder!!.iv_item_buy_coins_bgcolor.setImageResource(R.drawable.bg_item_buy_coin_yellow)

            }
        }


        if (list[position].IsMember){
            holder!!.iv_item_buy_coins_taocanvip.visibility=View.VISIBLE
        }else{
            holder!!.iv_item_buy_coins_taocanvip.visibility=View.GONE
        }


        holder!!.itemView.setOnClickListener {
            if (Utils.isFastClick(1000)){
                return@setOnClickListener
            }
            val typeid = SharePerferenceUtil.getInstance().getValue("typeId", "")!!.toString()
            //欢乐熊版本
            if (typeid == "25" && !(mcontext as BuyCoinsActivity).isMemberLogining()) {
                var dialog = TisDialog(mcontext).create().setMessage("请先登录!").show()

                return@setOnClickListener
            }

            if(!kd.sp().isSuccessOutCoin) {
                var dialog = TisDialog(mcontext).create().setMessage("设备没币,请移步到其他机器!").show()

                return@setOnClickListener
            }

            if ((mcontext as BuyCoinsActivity).isSuccessOpenSerial) {
                kd.sp().bdCoinOuted()
                kd.sp().bdCleanError()
                if (!(mcontext as BuyCoinsActivity).isMemberLogining()&&list[position].IsMember){
                        var dialog=TisDialog(mcontext).create().setMessage("需要会员才能购买!").show()
                        return@setOnClickListener
                }

                when(list[position].Id){
                //自由购买
                    "-1"->{
                        TisEditDialog(mcontext).create().setEditType(InputType.TYPE_CLASS_NUMBER)
                                .setMessage("请输入购买金额")
                                .setNegativeButton {

                                }.setPositiveButton { v, input ->
                            if (input.isNotEmpty()&&input.toInt()>0){
                                autoMathPackageList(input)
                            }else{
                             TisDialog(mcontext).create().setMessage("金额不能为0或者空")
                                     .show()
                            }

                        }.show()

                    }
                    //其他
                    else->{

                        getPackageSaleInfo(list[position].Id,position)
                    }
                }

            }else{
                var dialog=TisDialog(mcontext).create().setMessage("设备故障,请联系管理员!").show()

            }


        }
    }


    class ViewHolder( itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tv_item_buy_coins_price: TextView =itemView!!.findViewById(R.id.tv_item_buy_coins_price)
        var tv_item_buy_coins_coin: TextView =itemView!!.findViewById(R.id.tv_item_buy_coins_coin)
        var iv_item_buy_coins_bgcolor: ImageView =itemView!!.findViewById(R.id.iv_item_buy_coins_bgcolor)
        var iv_item_buy_coins_taocanvip: ImageView =itemView!!.findViewById(R.id.iv_item_buy_coins_taocanvip)

        var arl_item_buy_coins_top: AutoRelativeLayout =itemView!!.findViewById(R.id.arl_item_buy_coins_top)
        var all_item_buy_coins_bottom: AutoLinearLayout =itemView!!.findViewById(R.id.all_item_buy_coins_bottom)
        var tv_item_buy_coins_freedom: TextView =itemView!!.findViewById(R.id.tv_item_buy_coins_freedom)



    }


    /**
     * 获取套餐列表
     */
    fun  getPackageSaleInfo(pid:String,position:Int){
        var memberInfo= GsonUtil.jsonToObject(SharePerferenceUtil.getInstance().getValue(Constance.member_Info,"").toString(), MemberInfo::class.java)

        var hashmap= HashMap<String,String>()
        hashmap.put("PackageID", pid)
        try {
            if((mcontext as BuyCoinsActivity).isMemberLogining()){
                hashmap.put("CustID",memberInfo!!.Id)
            }else{
                hashmap.put("CustID",Constance.machineFLTUserID)
            }
        }catch (e:Exception){

        }

        hashmap.put("packageQty","1")
        hashmap.put("sign", SignParamUtil.getSignStr(hashmap))
        HttpUtils.xPostJson(Constance.MEMBER_HOST+ Constance.GetPackageSaleInfo,hashmap,object : XutilsCallback<String>(){
            override fun onSuccessData(result: String) {
                var rjson= GsonUtil.jsonToObject(result, JsonObject::class.java)
                if (rjson!!.has("return_Code")&& rjson!!.get("return_Code").asString == "200") {
                    var buyCoinsDetailFragment=BuyCoinsDetailFragment()
                    var bundle=Bundle()
                    var buyList=ArrayList<BuyCoins>()
                    var buy=GsonUtil.jsonToObject(rjson.get("Data").toString(),BuyCoins::class.java)
                    if (null!=buy){
                        buy!!.BuyQty="1"
                        buyList.add(buy)
                    }
                    bundle.putSerializable("BuyCoins",buyList)
                    buyCoinsDetailFragment.arguments=bundle
//                    (mcontext as BuyCoinsActivity).countDownTimer.start()
                    (mcontext as BuyCoinsActivity).supportFragmentManager.beginTransaction().replace(R.id.fl_buy_coins_fragment, buyCoinsDetailFragment).commit()

                }else{
                    var dialog=TisDialog(mcontext).create().setMessage(rjson!!.get("result_Msg").asString).show()
                }

            }
            override fun onCancelled(cex: Callback.CancelledException?) {
            }

            override fun onError(ex: Throwable?, isOnCallback: Boolean) {
            }

            override fun onFinished() {
            }
        })
    }


    /**
     * 自动匹配套餐
     */
    fun autoMathPackageList(price:String) {
        val dia = KProgressHUD.create(mcontext).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("请稍后...").show()
        val memberInfo = GsonUtil.jsonToObject(SharePerferenceUtil.getInstance().getValue(Constance.member_Info, "")!!.toString(), MemberInfo::class.java)

        val hashmap = HashMap<String, String>()

        hashmap.put("PackageType", "Pa01")
        hashmap.put("MachineID", SharePerferenceUtil.getInstance().getValue(Constance.MachineID, "")!!.toString())

        if (null == memberInfo) {
            hashmap.put("CustID", Constance.machineFLTUserID)
        } else {
            hashmap.put("CustID", memberInfo.Id)
        }

        hashmap.put("Amount", price)
        hashmap.put("sign", SignParamUtil.getSignStr(hashmap))
        HttpUtils.xPostJson(Constance.MEMBER_HOST + Constance.AutoMathPackageList, hashmap, object : XutilsCallback<String>() {
            override fun onSuccessData(result: String) {
                var rjson = GsonUtil.jsonToObject(result, JsonObject::class.java)
                if (rjson!!.has("return_Code") && rjson.get("return_Code").asString == "200"
                        && rjson.get("Data").asJsonArray.size()>0) {


                    var buyCoinsDetailFragment=BuyCoinsDetailFragment()
                    var bundle=Bundle()
                    var buyList=ArrayList<BuyCoins>()
                    buyList=GsonUtil.jsonToList(rjson.get("Data").asJsonArray.toString(),BuyCoins::class.java) as ArrayList<BuyCoins>
                    bundle.putSerializable("BuyCoins",buyList)
                    buyCoinsDetailFragment.arguments=bundle
                    (mcontext as BuyCoinsActivity).supportFragmentManager.beginTransaction().replace(R.id.fl_buy_coins_fragment, buyCoinsDetailFragment).commit()

                } else {
                    val dialog = TisDialog(mcontext).create().setMessage("未匹配到套餐").show()
                }
            }

            override fun onError(ex: Throwable, isOnCallback: Boolean) {
                val dialog = TisDialog(mcontext).create().setMessage("网络异常!").show()

            }

            override fun onCancelled(cex: Callback.CancelledException) {

            }

            override fun onFinished() {
                if (dia != null && dia.isShowing) {
                    dia.dismiss()
                }
            }


        })
    }

}
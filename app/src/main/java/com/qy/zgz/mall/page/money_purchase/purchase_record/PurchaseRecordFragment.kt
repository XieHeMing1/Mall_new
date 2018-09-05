package com.qy.zgz.mall.page.money_purchase.purchase_record

import android.os.Message
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Button
import com.google.gson.JsonObject
import com.qy.zgz.mall.Model.MemberInfo
import com.qy.zgz.mall.Model.PurchaseRecord
import com.qy.zgz.mall.R
import com.qy.zgz.mall.base.BaseFragment
import com.qy.zgz.mall.network.Constance
import com.qy.zgz.mall.network.XutilsCallback
import com.qy.zgz.mall.utils.GsonUtil
import com.qy.zgz.mall.utils.HttpUtils
import com.qy.zgz.mall.utils.SharePerferenceUtil
import com.qy.zgz.mall.utils.SignParamUtil
import org.xutils.common.Callback
import org.xutils.view.annotation.ContentView
import org.xutils.view.annotation.ViewInject
import java.util.HashMap
import kotlin.collections.ArrayList

/**
 *消费记录
 */
@ContentView(R.layout.fragment_purchase_record)
class PurchaseRecordFragment : BaseFragment() {

    @ViewInject(R.id.btn_fg_purchase_record_buy)
    lateinit var btn_fg_purchase_record_buy :Button

    @ViewInject(R.id.btn_fg_purchase_record_take)
    lateinit var btn_fg_purchase_record_take :Button

    @ViewInject(R.id.btn_fg_purchase_record_all)
    lateinit var btn_fg_purchase_record_all :Button

    @ViewInject(R.id.btn_fg_purchase_record_exchange)
    lateinit var btn_fg_purchase_record_exchange :Button

    @ViewInject(R.id.rv_purchase_record_info)
    lateinit var rv_purchase_record_info :RecyclerView

    var purchaseRecordAdapter:PurchaseRecordAdapter?=null

    override fun init() {
        //点击监听器设置
        btn_fg_purchase_record_exchange.setOnClickListener(this)
        btn_fg_purchase_record_all.setOnClickListener(this)
        btn_fg_purchase_record_buy.setOnClickListener(this)
        btn_fg_purchase_record_take.setOnClickListener(this)
        //初始化设置
        btn_fg_purchase_record_all.isSelected=true
        btn_fg_purchase_record_all.setTextColor(ContextCompat.getColor(context,R.color.coloer_wither))
        //设置布局管理器()
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        rv_purchase_record_info.layoutManager = linearLayoutManager
        rv_purchase_record_info.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        var initdata:ArrayList<PurchaseRecord>
        initdata=ArrayList<PurchaseRecord>()
//        initdata.add(0,PurchaseRecord("日期","业务类型","金额","游戏币","代币","预存款","彩票","积分"))
        purchaseRecordAdapter=PurchaseRecordAdapter(context,initdata)
        rv_purchase_record_info.adapter=purchaseRecordAdapter


        getMemberConsume()
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            //兑换礼品记录
            R.id.btn_fg_purchase_record_exchange->{
//                adjustButtonBg(btn_fg_purchase_record_exchange)
            }
            //全部记录
            R.id.btn_fg_purchase_record_all->{
                adjustButtonBg(btn_fg_purchase_record_all)

            }
            //购币记录
            R.id.btn_fg_purchase_record_buy->{
//                adjustButtonBg(btn_fg_purchase_record_buy)
            }
            //提币记录
            R.id.btn_fg_purchase_record_take->{
//                adjustButtonBg(btn_fg_purchase_record_take)
            }
        }
    }

    override fun ObjectMessage(msg: Message?) {
    }

    //调整按钮组背景
    fun adjustButtonBg(v:Button){
        btn_fg_purchase_record_all.isSelected=false
        btn_fg_purchase_record_buy.isSelected=false
        btn_fg_purchase_record_take.isSelected=false
        btn_fg_purchase_record_exchange.isSelected=false
        btn_fg_purchase_record_all.setTextColor(ContextCompat.getColor(context,R.color.color_blue))
        btn_fg_purchase_record_buy.setTextColor(ContextCompat.getColor(context,R.color.color_blue))
        btn_fg_purchase_record_take.setTextColor(ContextCompat.getColor(context,R.color.color_blue))
        btn_fg_purchase_record_exchange.setTextColor(ContextCompat.getColor(context,R.color.color_blue))
        v.isSelected=true
        v.setTextColor(ContextCompat.getColor(context,R.color.coloer_wither))
    }


    /**
     * 获取会员消费记录
     */
    fun getMemberConsume(){
        var memberInfo: MemberInfo? = GsonUtil.jsonToObject(SharePerferenceUtil.getInstance().getValue(Constance.member_Info,"").toString(), MemberInfo::class.java) ?: return

        var hashmap= HashMap<String,String>()
        hashmap.put("IntMonth","1")
        hashmap.put("PageNum","1");
        hashmap.put("PageRows","20");
        hashmap.put("MemberID",memberInfo!!.Id)
        hashmap.put("sign", SignParamUtil.getSignStr(hashmap))

        HttpUtils.xPostJson(Constance.MEMBER_HOST+ Constance.GetCustomerConsume,hashmap,object : XutilsCallback<String>(){
            override fun onSuccessData(result: String?) {

                var rjson= GsonUtil.jsonToObject(result!!.toString(), JsonObject::class.java)
                if (rjson!!.has("return_Code")&&
                        rjson!!.get("return_Code").asString == "200") {

                    var consume_data=GsonUtil.jsonToList(rjson!!.getAsJsonArray("Data").toString(),PurchaseRecord::class.java) as ArrayList<PurchaseRecord>

                    if (null==consume_data){
                        consume_data= ArrayList<PurchaseRecord>()

                    }

//                    consume_data.add(0,PurchaseRecord("日期","业务类型","金额","游戏币","代币","预存款","彩票","积分"))
                    purchaseRecordAdapter=PurchaseRecordAdapter(context,consume_data)
                    rv_purchase_record_info.adapter=purchaseRecordAdapter

                }else{

                }
            }

            override fun onCancelled(cex: Callback.CancelledException?) {
            }

            override fun onFinished() {
            }

            override fun onError(ex: Throwable?, isOnCallback: Boolean) {

            }

        })
    }

}
package com.qy.zgz.mall.page.fragment

import android.os.Message
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.gson.JsonObject
import com.qy.zgz.mall.Model.MyOrder
import com.qy.zgz.mall.Model.MyOrderContent
import com.qy.zgz.mall.R
import com.qy.zgz.mall.adapter.MyOrderContentAdapter
import com.qy.zgz.mall.adapter.ViewPagerSnapHelper
import com.qy.zgz.mall.base.BaseFragment
import com.qy.zgz.mall.network.Constance
import com.qy.zgz.mall.network.NetworkCallback
import com.qy.zgz.mall.network.NetworkRequest
import com.qy.zgz.mall.page.index.MallActivity
import com.qy.zgz.mall.utils.GsonUtil
import com.qy.zgz.mall.utils.SharePerferenceUtil
import com.zhy.autolayout.AutoRelativeLayout
import org.xutils.view.annotation.ContentView
import org.xutils.view.annotation.ViewInject
import java.util.*
import kotlin.collections.ArrayList

@ContentView(R.layout.fragment_my_order)
class MyOrderFragment : BaseFragment() {

    @ViewInject(R.id.iv_my_order_back)
    lateinit var iv_my_order_back:ImageView


    @ViewInject(R.id.rv_myorder_content)
    lateinit var rv_myorder_content:RecyclerView

    @ViewInject(R.id.tv_my_order_contect_all)
    lateinit var tv_my_order_contect_all:TextView

    @ViewInject(R.id.tv_my_order_contect_unpay)
    lateinit var tv_my_order_contect_unpay:TextView

    @ViewInject(R.id.tv_my_order_contect_unsend)
    lateinit var tv_my_order_contect_unsend:TextView

    @ViewInject(R.id.tv_my_order_contect_unget)
    lateinit var tv_my_order_contect_unget:TextView

    @ViewInject(R.id.tv_my_order_contect_unassess)
    lateinit var tv_my_order_contect_unassess:TextView

    @ViewInject(R.id.arl_my_order_contect_all)
    lateinit var arl_my_order_contect_all: AutoRelativeLayout

    @ViewInject(R.id.arl_my_order_contect_unpay)
    lateinit var arl_my_order_contect_unpay: AutoRelativeLayout

    @ViewInject(R.id.arl_my_order_contect_unsend)
    lateinit var arl_my_order_contect_unsend: AutoRelativeLayout

    @ViewInject(R.id.arl_my_order_contect_unget)
    lateinit var arl_my_order_contect_unget: AutoRelativeLayout

    @ViewInject(R.id.arl_my_order_contect_unassess)
    lateinit var arl_my_order_contect_unassess: AutoRelativeLayout



    var myOrderContentAdapter: MyOrderContentAdapter?=null

    var viewPagerSnapHelper: ViewPagerSnapHelper?=null

    val WAIT_BUYER_PAY="WAIT_BUYER_PAY"
    val WAIT_SELLER_SEND_GOODS="WAIT_SELLER_SEND_GOODS"
    val WAIT_BUYER_CONFIRM_GOODS="WAIT_BUYER_CONFIRM_GOODS"
    val WAIT_RATE="WAIT_RATE"


    override fun init() {
        iv_my_order_back.setOnClickListener(this)
        arl_my_order_contect_all.setOnClickListener(this)
        arl_my_order_contect_unpay.setOnClickListener(this)
        arl_my_order_contect_unsend.setOnClickListener(this)
        arl_my_order_contect_unget.setOnClickListener(this)
        arl_my_order_contect_unassess.setOnClickListener(this)
        //设置布局管理器()
        val linearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        rv_myorder_content.layoutManager = linearLayoutManager
        //初始化数据
        var contectList=ArrayList<MyOrderContent>()
        contectList.add(MyOrderContent(0,ArrayList<MyOrder>()))
        contectList.add(MyOrderContent(1,ArrayList<MyOrder>()))
        contectList.add(MyOrderContent(2,ArrayList<MyOrder>() ))
        contectList.add(MyOrderContent(3,ArrayList<MyOrder>() ))
        contectList.add(MyOrderContent(4,ArrayList<MyOrder>()))
        myOrderContentAdapter= MyOrderContentAdapter(activity, contectList)
        //滑动item监听
        viewPagerSnapHelper= ViewPagerSnapHelper(1).setPageListener {
            position ->

            adjustSelColor(myOrderContentAdapter!!.list[position].index)

        }
        viewPagerSnapHelper!!.attachToRecyclerView(rv_myorder_content)
        //设置数据
        rv_myorder_content.adapter=myOrderContentAdapter

        if(arguments!=null&&arguments.containsKey("index")){
            viewPagerSnapHelper!!.smoothMoveToPosition(arguments.getInt("index"))
        }

        //订单列表
        tradeList()
        tradeListByType(WAIT_BUYER_PAY)
        tradeListByType(WAIT_SELLER_SEND_GOODS)
        tradeListByType(WAIT_BUYER_CONFIRM_GOODS)
        tradeListByType(WAIT_RATE)
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            //返回
            R.id.iv_my_order_back->{
//                (activity as MallActivity).arl_main_bottom_content.visibility=View.VISIBLE
//                (activity as MallActivity).main_fragment_content.visibility=View.GONE
                (activity as MallActivity).supportFragmentManager.beginTransaction().replace(R.id.main_fragment_content, MenberCenterLoginingFragment()).commit()
            }
            R.id.arl_my_order_contect_unpay->{
                viewPagerSnapHelper!!.smoothMoveToPosition(1)

            }
            R.id.arl_my_order_contect_all->{
                viewPagerSnapHelper!!.smoothMoveToPosition(0)

            }
            R.id.arl_my_order_contect_unsend->{
                viewPagerSnapHelper!!.smoothMoveToPosition(2)

            }
            R.id.arl_my_order_contect_unget->{
                viewPagerSnapHelper!!.smoothMoveToPosition(3)

            }
            R.id.arl_my_order_contect_unassess->{
                viewPagerSnapHelper!!.smoothMoveToPosition(4)

            }

        }
    }

    override fun ObjectMessage(msg: Message?) {
    }

    /**
     * 订单列表
     */
    fun tradeList(){
        Log.i("XHM_TEST", "accessToken = " + SharePerferenceUtil.getInstance().getValue(Constance.user_accessToken,"").toString());
        var hashmap=HashMap<String,String>()
        hashmap.put("accessToken", SharePerferenceUtil.getInstance().getValue(Constance.user_accessToken,"").toString())

        hashmap.put("page_size", "200")
        NetworkRequest.getInstance().tradeList(hashmap,object :NetworkCallback<JsonObject>(){
            override fun onSuccess(data: JsonObject?) {
                if (data==null){
                    return
                }
                var shopid= SharePerferenceUtil.getInstance().getValue(Constance.shop_id,"").toString()
                var rjson=GsonUtil.jsonToList(data.get("list").asJsonArray.toString(),MyOrder::class.java) as ArrayList<MyOrder>
                rjson=rjson.filter { it.shop_id==shopid} as ArrayList<MyOrder>
//                var contectList=ArrayList<MyOrderContent>()
//                contectList.add(MyOrderContent(0,rjson))
//                contectList.add(MyOrderContent(1,rjson.filter { it.status=="WAIT_BUYER_PAY" } as ArrayList<MyOrder> ))
//                contectList.add(MyOrderContent(2,rjson.filter { it.status=="WAIT_SELLER_SEND_GOODS" } as ArrayList<MyOrder> ))
//                contectList.add(MyOrderContent(3,rjson.filter { it.status=="WAIT_BUYER_CONFIRM_GOODS" } as ArrayList<MyOrder> ))
//                contectList.add(MyOrderContent(4,rjson.filter { it.status=="WAIT_RATE" } as ArrayList<MyOrder> ))
//                myOrderContentAdapter=MyOrderContentAdapter(activity,contectList)
                myOrderContentAdapter!!.list[0].order = rjson
                myOrderContentAdapter!!.notifyItemChanged(0)

            }

            override fun onFailure(code: Int, msg: String?) {
            }

        })
    }


    /**
     * 订单列表通过类型
     */
    fun tradeListByType(type:String){
        var hashmap=HashMap<String,String>()
        hashmap.put("accessToken", SharePerferenceUtil.getInstance().getValue(Constance.user_accessToken,"").toString())
        hashmap.put("page_size", "200")
        hashmap.put("status", type)
        NetworkRequest.getInstance().tradeList(hashmap,object :NetworkCallback<JsonObject>(){
            override fun onSuccess(data: JsonObject?) {
                if (data==null){
                    return
                }
                var shopid= SharePerferenceUtil.getInstance().getValue(Constance.shop_id,"").toString()
                var rjson=GsonUtil.jsonToList(data.get("list").asJsonArray.toString(),MyOrder::class.java) as ArrayList<MyOrder>
                rjson=rjson.filter { it.shop_id==shopid} as ArrayList<MyOrder>
                when(type){
                    WAIT_BUYER_PAY->{
                        myOrderContentAdapter!!.list[1].order = rjson
                        myOrderContentAdapter!!.notifyItemChanged(1)
                    }
                    WAIT_SELLER_SEND_GOODS->{
                        myOrderContentAdapter!!.list[2].order = rjson
                        myOrderContentAdapter!!.notifyItemChanged(2)
                    }
                    WAIT_BUYER_CONFIRM_GOODS->{
                        myOrderContentAdapter!!.list[3].order = rjson
                        myOrderContentAdapter!!.notifyItemChanged(3)
                    }
                    WAIT_RATE->{
                        myOrderContentAdapter!!.list[4].order = rjson
                        myOrderContentAdapter!!.notifyItemChanged(4)
                    }
                }


            }

            override fun onFailure(code: Int, msg: String?) {
            }

        })
    }


    /**
     * 调整选项卡文字颜色
     */
    fun adjustSelColor(index:Int){
        tv_my_order_contect_all.setTextColor(ContextCompat.getColor(activity, R.color.color_black))
        tv_my_order_contect_unpay.setTextColor(ContextCompat.getColor(activity, R.color.color_black))
        tv_my_order_contect_unsend.setTextColor(ContextCompat.getColor(activity, R.color.color_black))
        tv_my_order_contect_unget.setTextColor(ContextCompat.getColor(activity, R.color.color_black))
        tv_my_order_contect_unassess.setTextColor(ContextCompat.getColor(activity, R.color.color_black))
        when (index) {
            //全部
            0 -> {
                tv_my_order_contect_all.setTextColor(ContextCompat.getColor(activity, R.color.color_green))
            }
            //待付款
            1 -> {
                tv_my_order_contect_unpay.setTextColor(ContextCompat.getColor(activity, R.color.color_green))
            }
            //待发货
            2-> {
                tv_my_order_contect_unsend.setTextColor(ContextCompat.getColor(activity, R.color.color_green))
            }
            //待收货
            3 -> {
                tv_my_order_contect_unget.setTextColor(ContextCompat.getColor(activity, R.color.color_green))
            }
            //待评价
            4 -> {
                tv_my_order_contect_unassess.setTextColor(ContextCompat.getColor(activity, R.color.color_green))
            }
        }
    }




}
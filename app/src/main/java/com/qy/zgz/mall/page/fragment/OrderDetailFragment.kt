package com.qy.zgz.mall.page.fragment


import android.os.Message
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.qy.zgz.mall.Model.MyOrderDetail
import com.qy.zgz.mall.Model.MyOrderDetailImg
import com.qy.zgz.mall.R
import com.qy.zgz.mall.adapter.OrderDetailAdapter
import com.qy.zgz.mall.base.BaseFragment
import com.qy.zgz.mall.network.Constance
import com.qy.zgz.mall.network.NetworkCallback
import com.qy.zgz.mall.network.NetworkRequest
import com.qy.zgz.mall.page.index.MallActivity
import com.qy.zgz.mall.utils.DateUtils
import com.qy.zgz.mall.utils.SharePerferenceUtil
import org.xutils.view.annotation.ContentView
import org.xutils.view.annotation.ViewInject

@ContentView(R.layout.fragment_order_detail)
class OrderDetailFragment : BaseFragment() {

    @ViewInject(R.id.tv_order_detail_back)
    lateinit var tv_order_detail_back:TextView

    @ViewInject(R.id.rv_order_detail_goodinfo)
    lateinit var rv_order_detail_goodinfo:RecyclerView

    @ViewInject(R.id.tv_order_detail_status)
    lateinit var tv_order_detail_status:TextView

    @ViewInject(R.id.tv_order_detail_ordernum)
    lateinit var tv_order_detail_ordernum:TextView

    @ViewInject(R.id.tv_order_detail_creattime)
    lateinit var tv_order_detail_creattime:TextView

    @ViewInject(R.id.tv_order_detail_address)
    lateinit var tv_order_detail_address:TextView

    @ViewInject(R.id.tv_order_detail_username)
    lateinit var tv_order_detail_username:TextView

    @ViewInject(R.id.tv_order_detail_phone)
    lateinit var tv_order_detail_phone:TextView

    @ViewInject(R.id.tv_order_detail_tickets)
    lateinit var tv_order_detail_tickets:TextView

    @ViewInject(R.id.tv_order_detail_shopname)
    lateinit var tv_order_detail_shopname:TextView




    var orderDetailAdapter: OrderDetailAdapter?=null


    override fun init() {
        tv_order_detail_back.setOnClickListener(this)
        //设置布局管理器()
        var linearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        rv_order_detail_goodinfo.layoutManager = linearLayoutManager
        rv_order_detail_goodinfo.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))


        if(arguments!=null&&arguments.containsKey("tid")){
            tradeGet(arguments.get("tid").toString())
        }
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.tv_order_detail_back->{
                (activity as MallActivity).supportFragmentManager.beginTransaction().replace(R.id.main_fragment_content, MyOrderFragment()).commit()
            }
        }
    }

    override fun ObjectMessage(msg: Message?) {
    }

    /**
     * 获取订单详情
     */
    fun tradeGet(tid:String){
        var hashmap=HashMap<String,String>()
        hashmap.put("accessToken", SharePerferenceUtil.getInstance().getValue(Constance.user_accessToken,"").toString())
        hashmap.put("tid",tid)
        NetworkRequest.getInstance().tradeGet(hashmap,object :NetworkCallback<MyOrderDetail>(){
            override fun onSuccess(data: MyOrderDetail) {
                //订单号信息
                when {
                    data.status=="WAIT_BUYER_PAY" -> tv_order_detail_status.text="等待买家付款"
                    data.status=="WAIT_SELLER_SEND_GOODS" -> tv_order_detail_status.text="待发货"
                    data.status=="WAIT_BUYER_CONFIRM_GOODS" -> tv_order_detail_status.text="待收货"
                    data.status=="WAIT_RATE" -> tv_order_detail_status.text="待评价"
                    else -> tv_order_detail_status.text="已完成"
                }
                tv_order_detail_ordernum.text="订单号："+data.tid
                tv_order_detail_creattime.text="创建时间："+DateUtils.getDateToString(data.created_time.toLong()*1000,"yyyy-MM-dd HH:mm:ss")

                //地址
                var district=data.receiver_district
                if (district==null||district=="null"){
                    district=""
                }

                tv_order_detail_phone.text=data.receiver_mobile
                tv_order_detail_username.text=data.receiver_name
                tv_order_detail_address.text="收货地址："+data.receiver_state+"-"+
                        data.receiver_city+"-"+district+"  "+data.receiver_address

                //店铺名字
                tv_order_detail_shopname.text=data.shop_name
                //价格
                tv_order_detail_tickets.text=data.total_tickets_fee

                //商品
                orderDetailAdapter= OrderDetailAdapter(activity, data.orders
                        ?: ArrayList<MyOrderDetailImg>())
                rv_order_detail_goodinfo.adapter=orderDetailAdapter


            }

            override fun onFailure(code: Int, msg: String?) {
            }
        })
    }
}

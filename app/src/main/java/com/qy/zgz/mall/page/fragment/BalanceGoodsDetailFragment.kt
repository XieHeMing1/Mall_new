package com.qy.zgz.mall.page.fragment

import android.os.Message
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import com.qy.zgz.mall.Model.BalanceCar
import com.qy.zgz.mall.R
import com.qy.zgz.mall.adapter.BalanceGoodsDetailAdapter
import com.qy.zgz.mall.base.BaseFragment
import com.qy.zgz.mall.page.index.MallActivity
import org.xutils.view.annotation.ContentView
import org.xutils.view.annotation.ViewInject



/**
 * 结算商品详情
 */
@ContentView(R.layout.fragment_balance_goods_detail)
class BalanceGoodsDetailFragment : BaseFragment() {

    @ViewInject(R.id.iv_balance_goodsdetail_back)
    lateinit var iv_balance_goodsdetail_back:ImageView

    @ViewInject(R.id.rv_balance_goodsdetail_info)
    lateinit var rv_balance_goodsdetail_info:RecyclerView

    var carlist:ArrayList<BalanceCar>?=null

    var balanceGoodsDetailAdapter: BalanceGoodsDetailAdapter?=null


    override fun init() {
        iv_balance_goodsdetail_back.setOnClickListener(this)
        //设置布局管理器
        val linearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        rv_balance_goodsdetail_info.layoutManager = linearLayoutManager
        //添加Android自带的分割线
        rv_balance_goodsdetail_info.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
    }

    override fun onResume() {
        if(arguments!=null&&arguments.containsKey("carlist")){
            try {
                carlist=arguments.getSerializable("carlist") as ArrayList<BalanceCar>
                if (carlist!=null){
                    balanceGoodsDetailAdapter= BalanceGoodsDetailAdapter(activity, carlist!!)
                    rv_balance_goodsdetail_info.adapter=balanceGoodsDetailAdapter
                }
            }catch (e:Exception){

            }

        }

        super.onResume()
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.iv_balance_goodsdetail_back->{
                (activity as MallActivity).supportFragmentManager.popBackStack()
            }
        }
    }

    override fun ObjectMessage(msg: Message?) {
    }


}
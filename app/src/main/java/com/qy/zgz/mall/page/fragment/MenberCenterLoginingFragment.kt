package com.qy.zgz.mall.page.fragment

import android.os.Bundle
import android.os.Message
import android.view.View
import android.widget.TextView
import com.google.gson.JsonObject
import com.qy.zgz.mall.R
import com.qy.zgz.mall.base.BaseFragment
import com.qy.zgz.mall.network.Constance
import com.qy.zgz.mall.page.index.MallActivity
import com.qy.zgz.mall.utils.GsonUtil
import com.qy.zgz.mall.utils.SharePerferenceUtil
import com.zhy.autolayout.AutoLinearLayout
import org.xutils.view.annotation.ContentView
import org.xutils.view.annotation.ViewInject


@ContentView(R.layout.fragment_menber_center_logining)
class MenberCenterLoginingFragment : BaseFragment() {

    @ViewInject(R.id.tv_menber_center_login_userlevel)
    private lateinit var tv_menber_center_login_userlevel:TextView

    @ViewInject(R.id.tv_menber_center_login_userno)
    private lateinit var tv_menber_center_login_userno:TextView

    @ViewInject(R.id.tv_menber_center_login_username)
    private lateinit var tv_menber_center_login_username:TextView

    @ViewInject(R.id.tv_menber_center_login_tickets)
    private lateinit var tv_menber_center_login_tickets:TextView

    @ViewInject(R.id.tv_menber_center_login_coins)
    private lateinit var tv_menber_center_login_coins:TextView

    @ViewInject(R.id.tv_menber_center_login_point)
    private lateinit var tv_menber_center_login_point:TextView


    @ViewInject(R.id.tv_menber_center_login_recoin)
    private lateinit var tv_menber_center_login_recoin:TextView

    @ViewInject(R.id.tv_menber_center_login_deposit)
    private lateinit var tv_menber_center_login_deposit:TextView

    @ViewInject(R.id.tv_menber_center_login_back)
    private lateinit var tv_menber_center_login_back:TextView

    @ViewInject(R.id.tv_menber_center_login_findallorder)
    private lateinit var tv_menber_center_login_findallorder:TextView

    @ViewInject(R.id.all_menber_center_login_unpay)
    private lateinit var all_menber_center_login_unpay: AutoLinearLayout

    @ViewInject(R.id.all_menber_center_login_unsend)
    private lateinit var all_menber_center_login_unsend:AutoLinearLayout

    @ViewInject(R.id.all_menber_center_login_unreceive)
    private lateinit var all_menber_center_login_unreceive:AutoLinearLayout

    @ViewInject(R.id.all_menber_center_login_unassess)
    private lateinit var all_menber_center_login_unassess:AutoLinearLayout


    override fun init() {
        tv_menber_center_login_back.setOnClickListener(this)
        tv_menber_center_login_findallorder.setOnClickListener(this)
        all_menber_center_login_unpay.setOnClickListener(this)
        all_menber_center_login_unsend.setOnClickListener(this)
        all_menber_center_login_unreceive.setOnClickListener(this)
        all_menber_center_login_unassess.setOnClickListener(this)
    }

    override fun onResume() {
        initData()
        super.onResume()
    }

    override fun onClick(v: View?) {
        when(v!!.id) {
            //返回
            R.id.tv_menber_center_login_back -> {
                (activity as MallActivity).arl_main_bottom_content.visibility = View.VISIBLE
                (activity as MallActivity).main_fragment_content.visibility = View.GONE

            }
            //查看全部订单
            R.id.tv_menber_center_login_findallorder->{
                var myOrderFragment= MyOrderFragment()
                (activity as MallActivity).supportFragmentManager!!.beginTransaction().replace(R.id.main_fragment_content, myOrderFragment).commit()
            }
            //待付款
            R.id.all_menber_center_login_unpay->{
                var myOrderFragment= MyOrderFragment()
                var bundle= Bundle()
                bundle.putInt("index",1)
                myOrderFragment.arguments=bundle
                (activity as MallActivity).supportFragmentManager!!.beginTransaction().replace(R.id.main_fragment_content, myOrderFragment).commit()

            }
            //待发货
            R.id.all_menber_center_login_unsend->{
                var myOrderFragment= MyOrderFragment()
                var bundle= Bundle()
                bundle.putInt("index",2)
                myOrderFragment.arguments=bundle
                (activity as MallActivity).supportFragmentManager!!.beginTransaction().replace(R.id.main_fragment_content, myOrderFragment).commit()
            }
            //待收获
            R.id.all_menber_center_login_unreceive->{
                var myOrderFragment= MyOrderFragment()
                var bundle= Bundle()
                bundle.putInt("index",3)
                myOrderFragment.arguments=bundle
                (activity as MallActivity).supportFragmentManager!!.beginTransaction().replace(R.id.main_fragment_content, myOrderFragment).commit()
            }
            //待评价
            R.id.all_menber_center_login_unassess->{
                var myOrderFragment= MyOrderFragment()
                var bundle= Bundle()
                bundle.putInt("index",4)
                myOrderFragment.arguments=bundle
                (activity as MallActivity).supportFragmentManager!!.beginTransaction().replace(R.id.main_fragment_content, myOrderFragment).commit()
            }

        }

    }

    override fun ObjectMessage(msg: Message?) {
    }

    /**
     * 初始化数据
     */
    fun initData(){
        try {
            var userdata=GsonUtil.jsonToObject(SharePerferenceUtil.getInstance().getValue(Constance.member_Info,"").toString(),JsonObject::class.java)
            tv_menber_center_login_username.text="会员名称 : "+userdata!!.get("CustName").asString
            tv_menber_center_login_userno.text="会员编号 : "+userdata!!.get("Number").asString
            tv_menber_center_login_userlevel.text="会员等级 : "+userdata!!.get("LevelName").asString
            tv_menber_center_login_tickets.text=userdata!!.get("Tickets")?.asString
            tv_menber_center_login_coins.text=userdata!!.get("Coins")?.asString
            tv_menber_center_login_point.text=userdata!!.get("Point")?.asString
            tv_menber_center_login_deposit.text=userdata!!.get("Money")?.asString
            tv_menber_center_login_recoin.text="0"
        }catch (e:Exception){
            e.printStackTrace()
        }

    }

}

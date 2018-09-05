package com.qy.zgz.mall.page.money_purchase

import android.graphics.Rect
import android.os.Message
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import com.google.gson.JsonObject
import com.qy.zgz.mall.Model.BuyCoins
import com.qy.zgz.mall.Model.MemberInfo
import com.qy.zgz.mall.R
import com.qy.zgz.mall.base.BaseFragment
import com.qy.zgz.mall.network.Constance
import com.qy.zgz.mall.network.XutilsCallback
import com.qy.zgz.mall.utils.GsonUtil
import com.qy.zgz.mall.utils.HttpUtils
import com.qy.zgz.mall.utils.SharePerferenceUtil
import com.qy.zgz.mall.utils.SignParamUtil
import com.qy.zgz.mall.widget.TisDialog
import com.zhy.autolayout.utils.AutoUtils
import org.xutils.common.Callback
import org.xutils.view.annotation.ContentView
import org.xutils.view.annotation.ViewInject
import java.util.*

/**
 * 购买套餐
 */
@ContentView(R.layout.fragment_buy_coins)
class BuyCoinsFragment : BaseFragment() {

    @ViewInject(R.id.rv_fg_buy_coins_info)
    lateinit var rv_fg_buy_coins_info:RecyclerView

    @ViewInject(R.id.tv_fg_buy_coins_notaocan_tis)
    lateinit var tv_fg_buy_coins_notaocan_tis:TextView


    var buyCoinsAdapter:BuyCoinsAdapter?=null

    override fun init() {

        //配置显示的设置
        rv_fg_buy_coins_info.layoutManager = GridLayoutManager(context, 4)
        rv_fg_buy_coins_info.addItemDecoration(object:RecyclerView.ItemDecoration(){
            override fun getItemOffsets(outRect: Rect?, view: View?, parent: RecyclerView?, state: RecyclerView.State?) {
                outRect!!.set(AutoUtils.getPercentWidthSize(50),AutoUtils.getPercentHeightSize(50),AutoUtils.getPercentWidthSize(50),AutoUtils.getPercentHeightSize(50))
            }
        })

        getPackageList()

    }

    override fun onClick(v: View?) {

    }

    override fun ObjectMessage(msg: Message?) {

    }

    override fun onResume() {
        super.onResume()
        //开启纸币器

    }

    /**
     * 获取套餐列表
     */
    fun  getPackageList(){
        var memberInfo=GsonUtil.jsonToObject(SharePerferenceUtil.getInstance().getValue(Constance.member_Info,"").toString(), MemberInfo::class.java)

        var hashmap=HashMap<String,String>()
        hashmap.put("MachineID",SharePerferenceUtil.getInstance().getValue(Constance.MachineID,"").toString())
        hashmap.put("PackageType","Pa01")
        hashmap.put("PageIndex","1")
        if(memberInfo==null||TextUtils.isEmpty(memberInfo!!.Id)){
//            hashmap.put("CustID",Constance.machineFLTUserID)
        }else{
            hashmap.put("CustID",memberInfo!!.Id)
        }

        hashmap.put("PageNum","10")
        hashmap.put("sign",SignParamUtil.getSignStr(hashmap))
        HttpUtils.xPostJson(Constance.MEMBER_HOST+Constance.GetPackageList,hashmap,object :XutilsCallback<String>(){
            override fun onSuccessData(result: String) {
                var rjson=GsonUtil.jsonToObject(result, JsonObject::class.java)
                if (rjson!!.has("return_Code")&& rjson!!.get("return_Code").asString == "200") {

                    var dataList=GsonUtil.jsonToList(rjson!!.getAsJsonArray("Data").toString(),BuyCoins::class.java) as ArrayList<BuyCoins>

                    //自由购买
                    if((context as BuyCoinsActivity).isShowAutoBuy){
                        var freedomBuy=BuyCoins("-1")
                        dataList.add(0,freedomBuy)
                    }

                    buyCoinsAdapter=BuyCoinsAdapter(context, dataList)
                    rv_fg_buy_coins_info.adapter=buyCoinsAdapter

                    if(buyCoinsAdapter==null||buyCoinsAdapter!!.list.size==0){
                        tv_fg_buy_coins_notaocan_tis.visibility=View.VISIBLE
                    }else{
                        tv_fg_buy_coins_notaocan_tis.visibility=View.GONE
                    }

                }else{

                }

            }
            override fun onCancelled(cex: Callback.CancelledException?) {
            }

            override fun onError(ex: Throwable?, isOnCallback: Boolean) {
                if (isAdded){
                    var dialog=TisDialog(context).create().setMessage("网络异常").show()
                }

            }

            override fun onFinished() {
            }
        })



    }



}
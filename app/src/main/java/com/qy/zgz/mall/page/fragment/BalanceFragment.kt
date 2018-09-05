package com.qy.zgz.mall.page.fragment



import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.os.Message
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.google.gson.JsonObject
import com.kaopiz.kprogresshud.KProgressHUD
import com.qy.zgz.mall.Model.Address
import com.qy.zgz.mall.Model.BalanceCar
import com.qy.zgz.mall.R
import com.qy.zgz.mall.adapter.BalanceAdapter
import com.qy.zgz.mall.base.BaseFragment
import com.qy.zgz.mall.network.Constance
import com.qy.zgz.mall.network.NetworkCallback
import com.qy.zgz.mall.network.NetworkRequest
import com.qy.zgz.mall.page.index.MallActivity
import com.qy.zgz.mall.utils.GsonUtil
import com.qy.zgz.mall.utils.SharePerferenceUtil
import com.qy.zgz.mall.utils.UnityDialog
import com.qy.zgz.mall.widget.MyTextWatch
import com.zhy.autolayout.AutoLinearLayout
import com.zhy.autolayout.utils.AutoUtils
import org.xutils.view.annotation.ContentView
import org.xutils.view.annotation.ViewInject
import java.lang.Exception


@ContentView(R.layout.fragment_balance)
class BalanceFragment : BaseFragment() {
    companion object {
        //结算fragment标识
        val balanceFragmentTag:String="balanceFragmentTag"

        //请求地址fragment标识
        val addressFragmentReqCode:Int=0
    }


    @ViewInject(R.id.btn_balance_balance)
    lateinit var btn_balance_balance:Button

    @ViewInject(R.id.iv_balance_back)
    lateinit var iv_balance_back:ImageView

    @ViewInject(R.id.rv_balance_list)
    lateinit var rv_balance_list: RecyclerView

    @ViewInject(R.id.tv_balance_payway)
    lateinit var tv_balance_payway: TextView

    @ViewInject(R.id.tv_balance_username)
    lateinit var tv_balance_username: TextView

    @ViewInject(R.id.tv_balance_phone)
    lateinit var tv_balance_phone: TextView

    @ViewInject(R.id.tv_balance_address)
    lateinit var tv_balance_address: TextView

    @ViewInject(R.id.tv_balance_tickets)
    lateinit var tv_balance_tickets: TextView

    @ViewInject(R.id.tv_balance_total_tickets)
    lateinit var tv_balance_total_tickets: TextView

    @ViewInject(R.id.tv_balance_quantity)
    lateinit var tv_balance_quantity: TextView

    @ViewInject(R.id.all_balance_address_info)
    lateinit var all_balance_address_info: AutoLinearLayout

    @ViewInject(R.id.tv_balance_nonaddress)
    lateinit var tv_balance_nonaddress: TextView//没有地址时显示

    @ViewInject(R.id.all_balance_hasaddress)
    lateinit var all_balance_hasaddress:AutoLinearLayout//有地址时显示

    @ViewInject(R.id.tv_balance_shopname)
    lateinit var tv_balance_shopname: TextView

    @ViewInject(R.id.et_balance_leave_words)
    lateinit var et_balance_leave_words: EditText

    @ViewInject(R.id.tv_balance_leave_words)
    lateinit var tv_balance_leave_words: TextView


    var balanceAdapter: BalanceAdapter?=null

    //商品详情数据
    var cart_bundle=Bundle()

    //商品详情数据
    var address:Address?=null

    //结算模式
    var mode=""

    //订单类型
    var tradeType=""

    override fun init() {

        if(null!=arguments&&arguments.containsKey("fastbuy")){
            mode="fastbuy"
            cartCheckout( "fastbuy")
        }else{
            mode="cart"
            cartCheckout( "cart")

        }

        //设置点击监听器
        btn_balance_balance.setOnClickListener(this)
        iv_balance_back.setOnClickListener(this)
        tv_balance_quantity.setOnClickListener(this)
        all_balance_address_info.setOnClickListener(this)
        et_balance_leave_words.addTextChangedListener(MyTextWatch.getInstance(context))
        //设置布局管理器(横向)
        val linearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        rv_balance_list.layoutManager = linearLayoutManager
        rv_balance_list.addItemDecoration(object :RecyclerView.ItemDecoration(){
            override fun getItemOffsets(outRect: Rect?, view: View?, parent: RecyclerView?, state: RecyclerView.State?) {
                outRect!!.left=AutoUtils.getPercentWidthSize(50)
            }
        })

        tv_balance_shopname.text=SharePerferenceUtil.getInstance().getValue("type_shop_name","...").toString()
    }



    override fun onClick(v: View?) {
        when(v!!.id){
            //结算
            R.id.btn_balance_balance->{
                UnityDialog(activity)
                        .setHint("您是否确认购买?")
                        .setCancel("取消", null)
                        .setConfirm("确定") { unityDialog, content ->
                            cartCartPay()
                            unityDialog.dismiss()
                        }.show()
            }
            //返回
            R.id.iv_balance_back->{
                (activity as MallActivity).supportFragmentManager.beginTransaction()
                        .replace(R.id.main_fragment_content, ShopCarFragment()).commit()
            }
            //商品详情
            R.id.tv_balance_quantity->{
                var balanceGoodsDetailFragment= BalanceGoodsDetailFragment()
                balanceGoodsDetailFragment.arguments=cart_bundle
                switchContent(this,balanceGoodsDetailFragment)

            }
            //选择地址
            R.id.all_balance_address_info->{
                var addressFragment= AddressFragment()
                addressFragment.setTargetFragment(this, addressFragmentReqCode)
                switchContent(this,addressFragment)
            }
        }
    }

    //调用基类的访问接口方法返回结果处理
    override fun ObjectMessage(msg: Message?) {
    }

    /**
     * 查看结算购物车列表
     */
    fun cartCheckout(mode:String){
        var hashmap=HashMap<String,String>()
        hashmap.put("accessToken",SharePerferenceUtil.getInstance().getValue(Constance.user_accessToken,"").toString())
        hashmap.put("mode",mode)
        hashmap.put("shop_id",SharePerferenceUtil.getInstance().getValue(Constance.shop_id,"").toString())
        NetworkRequest.getInstance().checkOutCart(hashmap,object : NetworkCallback<JsonObject>(){
            override fun onSuccess(data: JsonObject?) {
                if (data==null||data.isJsonNull){
                    return
                }
                tv_balance_payway.text=data.get("payType").asJsonObject.get("name").asString
                //地址
                if (!data.get("default_address").isJsonNull){
                    address=GsonUtil.jsonToObject(data.get("default_address").asJsonObject.toString(),Address::class.java)
                    tv_balance_username.text=address!!.name
                    tv_balance_address.text=address!!.area+" "+address!!.addr
                    tv_balance_phone.text=address!!.mobile
                    all_balance_hasaddress.visibility=View.VISIBLE
                    tv_balance_nonaddress.visibility=View.GONE
                }


                //商品信息
                var rjson=data.get("cartInfo").asJsonObject.getAsJsonArray("resultCartData")

                if (rjson!=null&&rjson.size()>0){
                    //价格
                    tv_balance_tickets.text=rjson[0].asJsonObject.get("cartCount").asJsonObject.get("total_tickets_fee").asString
                    tv_balance_total_tickets.text=(rjson[0].asJsonObject.get("cartCount").asJsonObject.get("total_tickets_fee").asString.toInt()-rjson[0].asJsonObject.get("cartCount").asJsonObject.get("total_discount").asString.toInt()).toString()
                    tv_balance_quantity.text="共"+rjson[0].asJsonObject.get("cartCount").asJsonObject.get("itemnum").asString+"件"
                    tradeType=rjson[0].asJsonObject.get("cartCount").asJsonObject.get("tradeType").asString
                    //商品
                    var goods=rjson[0].asJsonObject.getAsJsonArray("items").toString()
                    var carList=GsonUtil.jsonToList(goods,BalanceCar::class.java) as ArrayList<BalanceCar>
                    cart_bundle.putSerializable("carlist",carList)
                    balanceAdapter= BalanceAdapter(activity, carList)
                    rv_balance_list.adapter=balanceAdapter

                    //判断是否虚拟商品
                    if (mode == "fastbuy" && carList[0].item_type!="default"){
                        tv_balance_leave_words.text = "手机号码 :"
                        et_balance_leave_words.hint="请填写手机号码"
                    }else{
                        tv_balance_leave_words.text = "买家留言 :"
                        et_balance_leave_words.hint="选填:内容已经和商家达成一致"
                    }
                }
            }

            override fun onFailure(code: Int, msg: String?) {
            }

            override fun onNetWorkFailure(e: Exception?) {
                CToast("网络故障，请重新尝试或联系管理人员！")
                super.onNetWorkFailure(e)
            }
        })
    }

    /**
     * 调整当前地址
     */
    fun initSelAddress(arguments:Bundle){
        if(null!=arguments&&arguments.containsKey("sel_address")){
            address=arguments.getSerializable("sel_address") as Address
            tv_balance_username.text=address!!.name
            tv_balance_address.text=address!!.area+" "+address!!.addr
            tv_balance_phone.text=address!!.mobile
            all_balance_hasaddress.visibility=View.VISIBLE
            tv_balance_nonaddress.visibility=View.GONE


        }
        else if(null!=arguments&&arguments.containsKey("back")){

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode!= Activity.RESULT_OK){
            return
        }

        if(null!=data){
            initSelAddress(data.extras)
        }
    }


    //控制FRAGMENT切换
    fun switchContent(from: Fragment, to:Fragment)
    {
        if (!to.isAdded) {    // 先判断是否被add过
            (activity as MallActivity).supportFragmentManager.beginTransaction().hide(from).add(R.id.main_fragment_content, to).addToBackStack(null).commit(); // 隐藏当前的fragment，add下一个到Activity中
        } else {
            (activity as MallActivity).supportFragmentManager.beginTransaction().hide(from).show(to).addToBackStack(null).commit(); // 隐藏当前的fragment，显示下一个
        }
    }

    /**
     * 购物车结算
     */
    fun cartCartPay(){
        if (null==balanceAdapter||balanceAdapter!!.list.size==0||tradeType.isNullOrEmpty()){
            CToast("商品列表为空，请刷新!")
            return
        }
        //判断是否虚拟商品
        if (mode == "fastbuy" && balanceAdapter!!.list[0].item_type!="default"
                 &&  et_balance_leave_words.text.isNullOrEmpty()){
            CToast("手机号码不能为空!")
            return
        }

        if (null==address){
            CToast("请选择收货地址!")
            return
        }
        var hashmap=HashMap<String,String>()
        hashmap.put("accessToken",SharePerferenceUtil.getInstance().getValue(Constance.user_accessToken,"").toString())
        hashmap.put("addr_id",address!!.addr_id)
        hashmap.put("mode",mode)
        hashmap.put("tradeType",tradeType)
        hashmap.put("mark",et_balance_leave_words.text.toString())
        hashmap.put("shop_id",SharePerferenceUtil.getInstance().getValue(Constance.shop_id,"").toString())
        //显示加载
        val dia = KProgressHUD.create(activity).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("请稍后...").show()
        NetworkRequest.getInstance().cartCartPay(hashmap,object :NetworkCallback<JsonObject>(){
            override fun onSuccess(data: JsonObject?) {
                if (data==null){
                    return
                }
                if(data!!.get("return_code").asString=="SUCCESS"){
                    CToast("结算成功!")
                    //刷新用户界面
                    try {

                        (activity as MallActivity).scanCardLoginById()

                    }catch (e:Exception){

                    }
                }else{

                    CToast(data!!.get("return_msg").asString)

                }

            }

            override fun onFailure(code: Int, msg: String?) {
                CToast(msg)
            }

            override fun onNetWorkFailure(e: Exception?) {

                CToast("网络错误!")
                super.onNetWorkFailure(e)
            }

            override fun onCompleted() {
                if (dia != null && dia.isShowing) {
                    dia.dismiss()
                }
                (activity as MallActivity).supportFragmentManager.beginTransaction()
                        .replace(R.id.main_fragment_content, MenberCenterLoginingFragment()).commit()

                super.onCompleted()
            }

        })
    }
}
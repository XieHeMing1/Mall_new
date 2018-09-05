package com.qy.zgz.mall.page.fragment


import android.os.Message
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.qy.zgz.mall.Model.ShopCar
import com.qy.zgz.mall.R
import com.qy.zgz.mall.adapter.ShopCarAdapter
import com.qy.zgz.mall.base.BaseFragment
import com.qy.zgz.mall.network.Constance
import com.qy.zgz.mall.network.NetworkCallback
import com.qy.zgz.mall.network.NetworkRequest
import com.qy.zgz.mall.page.index.MallActivity
import com.qy.zgz.mall.utils.GsonUtil
import com.qy.zgz.mall.utils.SharePerferenceUtil
import org.xutils.view.annotation.ContentView
import org.xutils.view.annotation.ViewInject
import java.lang.Exception

@ContentView(R.layout.fragment_shop_car)
class ShopCarFragment : BaseFragment() {


    @ViewInject(R.id.rv_shop_car_list)
    lateinit var rv_shop_car_list:RecyclerView

    @ViewInject(R.id.btn_shop_car_balance)
    lateinit var btn_shop_car_balance:Button

    @ViewInject(R.id.tv_shop_car_del)
    lateinit var tv_shop_car_del:TextView

    @ViewInject(R.id.tv_shop_car_update)
    lateinit var tv_shop_car_update:TextView

    @ViewInject(R.id.iv_shop_car_allsel)
    lateinit var iv_shop_car_allsel:ImageView

    @ViewInject(R.id.iv_shop_car_back)
    lateinit var iv_shop_car_back:ImageView

    @ViewInject(R.id.tv_shop_car_total)
    lateinit var tv_shop_car_total:TextView

    @ViewInject(R.id.tv_shop_car_shopname)
    lateinit var tv_shop_car_shopname:TextView



    var shopCarAdapter: ShopCarAdapter?=null

    private var shopCarDataList=ArrayList<ShopCar>()

    override fun onResume() {
        getCartInfo()
        super.onResume()
    }
    override fun init() {
        initView()

    }

     fun initView(){
         iv_shop_car_allsel.isSelected=false
         btn_shop_car_balance.setOnClickListener(this)
         tv_shop_car_del.setOnClickListener(this)
         tv_shop_car_update.setOnClickListener(this)
         iv_shop_car_allsel.setOnClickListener(this)
         iv_shop_car_back.setOnClickListener(this)
         //设置布局管理器
         var linearLayoutManager = LinearLayoutManager(context);
         linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
         rv_shop_car_list!!.setLayoutManager(linearLayoutManager);
         //初始化数据
         shopCarDataList.clear()
         shopCarAdapter= ShopCarAdapter(activity, shopCarDataList, tv_shop_car_total)
         rv_shop_car_list.adapter=shopCarAdapter

     }

    override fun ObjectMessage(msg: Message?) {
    }


    override fun onClick(v: View?) {
        when(v!!.id){
            //编辑
            R.id.tv_shop_car_update->{
                if (btn_shop_car_balance.visibility==View.GONE){
                    btn_shop_car_balance.visibility=View.VISIBLE
                    tv_shop_car_del.visibility=View.GONE
                    tv_shop_car_update.setTextColor(resources.getColor(R.color.color_black))
                }else{
                    btn_shop_car_balance.visibility=View.GONE
                    tv_shop_car_del.visibility=View.VISIBLE
                    tv_shop_car_update.setTextColor(resources.getColor(R.color.color_red))
                }

            }
            //全选
            R.id.iv_shop_car_allsel->{
                if (!iv_shop_car_allsel.isSelected){

                    for (data in shopCarAdapter!!.list){
                        data.checked=1
                    }
                    iv_shop_car_allsel.setImageResource(R.drawable.select)
                    iv_shop_car_allsel.isSelected=true
                }else{
                    for (data in shopCarAdapter!!.list){
                        data.checked=0
                    }
                    iv_shop_car_allsel.setImageResource(R.drawable.select_grey)
                    iv_shop_car_allsel.isSelected=false
                }
                rv_shop_car_list.adapter.notifyDataSetChanged()

            }
            //删除选中的
            R.id.tv_shop_car_del->{

                delCartItem()

            }
            //结算
            R.id.btn_shop_car_balance->{
                //更新购物车选中状态
                updateCart()

            }
            //返回
            R.id.iv_shop_car_back->{
                (activity as MallActivity).arl_main_bottom_content.visibility=View.VISIBLE
                (activity as MallActivity).main_fragment_content.visibility=View.GONE

            }

        }
    }

    /**
     * 查看购物车
     */
    fun getCartInfo(){
        var hashmap=HashMap<String,String>()
        hashmap.put("accessToken", SharePerferenceUtil.getInstance().getValue(Constance.user_accessToken, "").toString())
        hashmap.put("mode","cart")
        hashmap.put("platform","wap")
        NetworkRequest.getInstance().getCart(hashmap,object : NetworkCallback<JsonObject>() {
            override fun onSuccess(data: JsonObject?) {
                if(!data!!.has("list")){
                    return
                }
                var rjson=GsonUtil.jsonToList(data.getAsJsonArray("list").toString(),ShopCar::class.java) as ArrayList<ShopCar>
                var dataList=ArrayList<ShopCar>()
                var shopid=SharePerferenceUtil.getInstance().getValue(Constance.shop_id,"").toString()
                //将符合条件的放到datalist里面
                rjson.filterTo(dataList) { it.shop_id == shopid }
                //显示店铺名
                if(dataList.size>0){
                tv_shop_car_shopname.text= dataList[0].shop_name
                }
                shopCarAdapter= ShopCarAdapter(activity, dataList, tv_shop_car_total)
                rv_shop_car_list.adapter=shopCarAdapter
            }

            override fun onFailure(code: Int, msg: String) {
            }
        })
    }

    /**
     * 删除购物车商品
     */
    fun delCartItem(){

        var hashmap=HashMap<String,String>()
        hashmap.put("accessToken", SharePerferenceUtil.getInstance().getValue(Constance.user_accessToken, "").toString())
        hashmap.put("mode","cart")
        var cart_ids=""
        var datalist=ArrayList<ShopCar>()
        shopCarAdapter!!.list
                .filterTo(datalist){ 0!= it.checked }
                .forEach { cart_ids += it.cart_id+"," }
        cart_ids=cart_ids.removeSuffix(",")
        hashmap.put("cart_id",cart_ids)
        NetworkRequest.getInstance().delCart(hashmap,object :NetworkCallback<JsonObject>(){
            override fun onSuccess(data: JsonObject?) {

                iv_shop_car_allsel.setImageResource(R.drawable.select_grey)
                iv_shop_car_allsel.isSelected=false
                shopCarAdapter!!.list=shopCarAdapter!!.list
                        .filter{ 0 == it.checked } as ArrayList<ShopCar>
                rv_shop_car_list.adapter.notifyDataSetChanged()

            }

            override fun onFailure(code: Int, msg: String?) {
            }
            override fun onNetWorkFailure(e: Exception?) {
                CToast("网络故障,请联系管理员!")
                super.onNetWorkFailure(e)
            }
        })
    }


    /**
     * 更新购物车商品
     */
    fun updateCart(){
        var hashmap=HashMap<String,String>()
        hashmap.put("accessToken", SharePerferenceUtil.getInstance().getValue(Constance.user_accessToken, "").toString())
        hashmap.put("mode","cart")
        hashmap.put("obj_type","item")


        var list=GsonUtil.objectToJson(shopCarAdapter!!.list)
        //选中的
        var selList=shopCarAdapter!!.list.filter { it.checked!=0 }
        var cartlist: JsonArray? = GsonUtil.jsonToObject(list,JsonArray::class.java) ?: return
        for (item in cartlist!!){
            item.asJsonObject.remove("title")
            item.asJsonObject.remove("tickets")
            item.asJsonObject.remove("total_tickets")
            item.asJsonObject.remove("image_default_id")
            item.asJsonObject.remove("shop_id")
            item.asJsonObject.remove("item_id")
            item.asJsonObject.addProperty("is_checked",item.asJsonObject.get("checked").asString)
            item.asJsonObject.addProperty("selected_promotion","0")
            item.asJsonObject.addProperty("totalQuantity",item.asJsonObject.get("quantity").asString)
            item.asJsonObject.remove("quantity")
            item.asJsonObject.remove("checked")
        }

        hashmap.put("cart_params", cartlist.toString())

        if (selList.isEmpty()){
            CToast("请至少选择一件商品!")
            return
        }

        NetworkRequest.getInstance().updateCart(hashmap,object :NetworkCallback<JsonArray>(){
            override fun onSuccess(data: JsonArray?) {
                (activity as MallActivity).supportFragmentManager!!.beginTransaction().replace(R.id.main_fragment_content, BalanceFragment()).commit()
            }

            override fun onFailure(code: Int, msg: String?) {
                CToast(msg)
            }

            override fun onNetWorkFailure(e: Exception?) {
                CToast("网络故障,请联系管理员!")
                super.onNetWorkFailure(e)
            }

        })
    }
}

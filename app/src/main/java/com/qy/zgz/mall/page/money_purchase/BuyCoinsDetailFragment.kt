package com.qy.zgz.mall.page.money_purchase

import android.os.Message
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.gson.JsonObject
import com.kaopiz.kprogresshud.KProgressHUD
import com.qy.zgz.mall.Model.BuyCoins
import com.qy.zgz.mall.Model.MemberInfo
import com.qy.zgz.mall.Model.SalePackages
import com.qy.zgz.mall.R
import com.qy.zgz.mall.base.BaseFragment
import com.qy.zgz.mall.network.Constance
import com.qy.zgz.mall.network.XutilsCallback
import com.qy.zgz.mall.utils.GsonUtil
import com.qy.zgz.mall.utils.HttpUtils
import com.qy.zgz.mall.utils.SharePerferenceUtil
import com.qy.zgz.mall.utils.SignParamUtil
import com.qy.zgz.mall.widget.TisDialog
import com.qy.zgz.mall.widget.TisPayDialog
import com.zhy.autolayout.AutoLinearLayout
import org.xutils.common.Callback
import org.xutils.view.annotation.ContentView
import org.xutils.view.annotation.ViewInject

/**
 * 购币套餐详情
 */
@ContentView(R.layout.fragment_buy_coins_detail)
class BuyCoinsDetailFragment : BaseFragment() {

    @ViewInject(R.id.tv_fg_buy_coins_detail_coins)
    lateinit var tv_fg_buy_coins_detail_coins:TextView

    @ViewInject(R.id.tv_fg_buy_coins_detail_price)
    lateinit var tv_fg_buy_coins_detail_price:TextView

    @ViewInject(R.id.iv_fg_buy_coins_detail_wxpay)
    lateinit var iv_fg_buy_coins_detail_wxpay:ImageView

    @ViewInject(R.id.iv_fg_buy_coins_detail_zfbpay)
    lateinit var iv_fg_buy_coins_detail_zfbpay:ImageView

    @ViewInject(R.id.iv_fg_buy_coins_detail_cashpay)
    lateinit var iv_fg_buy_coins_detail_cashpay:ImageView

    @ViewInject(R.id.all_fragment_buy_coins_detail_zfbpaytis)
    lateinit var all_fragment_buy_coins_detail_zfbpaytis:AutoLinearLayout



    var buyCoinList=ArrayList<BuyCoins>()

    override fun init() {
        var typeid=SharePerferenceUtil.getInstance().getValue("typeId","").toString()
        if (typeid=="7"){
            //天河城店铺
            iv_fg_buy_coins_detail_wxpay.visibility=View.GONE
            iv_fg_buy_coins_detail_cashpay.visibility=View.GONE
            all_fragment_buy_coins_detail_zfbpaytis.visibility=View.VISIBLE
        }else{
            iv_fg_buy_coins_detail_wxpay.visibility=View.VISIBLE
            iv_fg_buy_coins_detail_cashpay.visibility=View.VISIBLE
            all_fragment_buy_coins_detail_zfbpaytis.visibility=View.GONE
        }

        iv_fg_buy_coins_detail_cashpay.setOnClickListener(this)
        iv_fg_buy_coins_detail_zfbpay.setOnClickListener(this)
        iv_fg_buy_coins_detail_wxpay.setOnClickListener(this)

        if (null!=arguments&&arguments.containsKey("BuyCoins")){
            buyCoinList= arguments.getSerializable("BuyCoins") as ArrayList<BuyCoins>
            if (buyCoinList!=null&& !buyCoinList.isEmpty()){
                var coins=0
                var price=0.00
                buyCoinList.forEach {
                    coins+=(it!!.Coins1.toDouble().toInt()+it!!.StandardCoins.toDouble().toInt())*it!!.BuyQty.toDouble().toInt()
                    price+=it!!.PackagePrice.toDouble()*it!!.BuyQty.toDouble().toInt()
                }
                tv_fg_buy_coins_detail_coins.text=coins.toString()+"币"
                tv_fg_buy_coins_detail_price.text=price.toString()
            }
        }

    }

    override fun onClick(v: View?) {
        if (null==buyCoinList&&buyCoinList!!.size==0){
            return
        }

        when(v!!.id){
            R.id.iv_fg_buy_coins_detail_cashpay->{

                (context as BuyCoinsActivity).showCashPayDialog(buyCoinList)

            }
            R.id.iv_fg_buy_coins_detail_zfbpay->{
                if (buyCoinList!=null&&buyCoinList.isNotEmpty()){
                    GetQrCode("7", buyCoinList!!)
                }
            }
            R.id.iv_fg_buy_coins_detail_wxpay->{
                if (buyCoinList!=null&&buyCoinList.isNotEmpty()){
                    GetQrCode("6", buyCoinList!!)
                }
            }
        }
    }

    override fun ObjectMessage(msg: Message?) {
    }




    /**
     * 生成支付二维码
     */
    fun GetQrCode(payType:String,buyList: ArrayList<BuyCoins>){
            var hashmap = HashMap<String, String>()
            hashmap.put("MachineID", SharePerferenceUtil.getInstance().getValue(Constance.MachineID, "").toString())
            hashmap.put("UserID", Constance.machineUserID)
            hashmap.put("PayType", payType)
            hashmap.put("Name", "购买套餐")
            hashmap.put("Note", SharePerferenceUtil.getInstance().getValue(Constance.MachineClassNAME,"").toString()+"购币")

            var memberInfo=GsonUtil.jsonToObject(SharePerferenceUtil.getInstance().getValue(Constance.member_Info,"").toString(), MemberInfo::class.java)
            if(null!=memberInfo){
                hashmap.put("CustID",memberInfo!!.Id)
            }

            var saleList = ArrayList<SalePackages>()
            var amount=0.00
            buyList.forEach {
                amount+=it!!.PackagePrice.toDouble()*it!!.BuyQty.toDouble().toInt()
                var salePackages = SalePackages(it!!.Id, it.BuyQty.toDouble().toInt().toString(), it!!.PackagePrice, it!!.PackagePrice)
                saleList.add(salePackages)
            }

            hashmap.put("Amount",amount.toString())

            hashmap.put("Packages", GsonUtil.objectToJson(saleList))


            hashmap.put("sign", SignParamUtil.getSignStr(hashmap))

            var dia = KProgressHUD.create(context).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("请稍后...").show()
            HttpUtils.xPostJson(Constance.MEMBER_HOST + Constance.GetQrCode, hashmap, object : XutilsCallback<String>() {
                override fun onSuccessData(result: String) {
                    var rjson = GsonUtil.jsonToObject(result, JsonObject::class.java)
                    if (rjson!!.has("return_Code") && rjson!!.get("return_Code").asString == "200") {

                        var dialog = TisPayDialog(context).create()
                                .setBg(payType)
                                .setOrderNo(rjson!!.getAsJsonObject("Data").get("OrderNo").asString, payType,buyList)
                                .setImageViewUrl(rjson!!.getAsJsonObject("Data").get("QrCode").asString)
                                .show()
                    } else {
                        var dialog = TisDialog(context).create().setMessage(rjson!!.get("result_Msg").toString()).show()
                    }

                }

                override fun onCancelled(cex: Callback.CancelledException?) {
                }

                override fun onError(ex: Throwable?, isOnCallback: Boolean) {
                    var dialog = TisDialog(context).create().setMessage("网络错误").show()
                }

                override fun onFinished() {
                    if (dia != null && dia.isShowing) {
                        dia.dismiss()
                    }
                }
            })
    }


    /**
     * 验证移动支付是否已付款
     */
    fun checkIsPayStatus(){
        var hashmap=HashMap<String,String>()

        hashmap.put("OrderNo","")

        hashmap.put("sign", SignParamUtil.getSignStr(hashmap))
        HttpUtils.xPostJson(Constance.MEMBER_HOST+ Constance.CheckIsPayStatus,hashmap,object : XutilsCallback<String>(){
            override fun onSuccessData(result: String) {
                var rjson= GsonUtil.jsonToObject(result, JsonObject::class.java)
                if (rjson!!.has("return_Code")&& rjson!!.get("return_Code").asString == "200") {

                }else{

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
}
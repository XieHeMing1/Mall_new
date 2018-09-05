package com.qy.zgz.mall.page.fragment

import android.graphics.Color
import android.os.Message
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.google.gson.JsonObject
import com.qy.zgz.mall.Model.Address
import com.qy.zgz.mall.Model.Province
import com.qy.zgz.mall.R
import com.qy.zgz.mall.base.BaseFragment
import com.qy.zgz.mall.network.Constance
import com.qy.zgz.mall.network.NetworkCallback
import com.qy.zgz.mall.network.NetworkRequest
import com.qy.zgz.mall.page.index.AddressAddDialog
import com.qy.zgz.mall.page.index.MallActivity
import com.qy.zgz.mall.utils.GsonUtil
import com.qy.zgz.mall.utils.SharePerferenceUtil
import com.qy.zgz.mall.widget.MyTextWatch
import org.xutils.view.annotation.ContentView
import org.xutils.view.annotation.ViewInject
import java.util.*
import kotlin.collections.HashMap


/**
 * 添加地址
 */
@ContentView(R.layout.fragment_address_add)
class AddressAddFragment : BaseFragment() {

    @ViewInject(R.id.iv_address_add_back)
    lateinit var iv_address_add_back: ImageView

    @ViewInject(R.id.tv_address_add_regininfo)
    lateinit var tv_address_add_regininfo: TextView

    @ViewInject(R.id.iv_address_add_defalut_add)
    lateinit var iv_address_add_defalut_add: ImageView

    @ViewInject(R.id.et_address_add_zip)
    lateinit var et_address_add_zip: EditText

    @ViewInject(R.id.et_address_add_userphone)
    lateinit var et_address_add_userphone: EditText

    @ViewInject(R.id.et_address_add_username)
    lateinit var et_address_add_username: EditText

    @ViewInject(R.id.et_address_add_areadetail)
    lateinit var et_address_add_areadetail: EditText

    @ViewInject(R.id.btn_address_add_finish)
    lateinit var btn_address_add_finish: Button

    var t_proIndex=-1
    var t_cityIndex=-1
    var t_ditIndex=-1

    companion object {
        var addressAddDialog: AddressAddDialog?=null
    }

    //地区数据
    var region_datalist=ArrayList<Province>()

    override fun init() {
        btn_address_add_finish.setOnClickListener(this)
        iv_address_add_back.setOnClickListener(this)
        tv_address_add_regininfo.setOnClickListener(this)
        iv_address_add_defalut_add.setOnClickListener(this)
        iv_address_add_defalut_add.isSelected=false
        et_address_add_areadetail.addTextChangedListener(MyTextWatch.getInstance(context))
        et_address_add_zip.addTextChangedListener(MyTextWatch.getInstance(context))
        et_address_add_userphone.addTextChangedListener(MyTextWatch.getInstance(context))
        et_address_add_username.addTextChangedListener(MyTextWatch.getInstance(context))

        if (arguments!=null&&arguments.containsKey("edit_address")){
            var address=arguments.getSerializable("edit_address") as Address
            et_address_add_username.setText(address.name)
            et_address_add_userphone.setText(address.mobile)
            et_address_add_zip.setText(address.zip)
            et_address_add_areadetail.setText(address.addr)
            tv_address_add_regininfo.setText(address.area)
            iv_address_add_defalut_add.isSelected = address.def_addr=="1"

        }

        regionJson()
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            //返回
            R.id.iv_address_add_back->{
                (activity as MallActivity).supportFragmentManager.popBackStack()
            }
            //选择城市地址
            R.id.tv_address_add_regininfo->{
                addressAddDialog = AddressAddDialog(context).create().setAddressData(region_datalist)
                        .setComfireListence({
                            proIndex,cityIndex,ditIndex->
                             t_proIndex=proIndex
                             t_cityIndex=cityIndex
                             t_ditIndex=ditIndex

                            if (t_proIndex <= 0 || t_proIndex >= region_datalist.size) {
                                t_proIndex = Math.abs(t_proIndex) % region_datalist.size
                            }

                            if (t_cityIndex <= 0 || t_cityIndex >= region_datalist[t_proIndex].children!!.size) {
                                t_cityIndex = Math.abs(t_cityIndex) % region_datalist[t_proIndex].children!!.size
                            }

                            var dit=region_datalist[t_proIndex].children!![t_cityIndex].children
                            var dit_area=""
                            if (dit==null||dit!!.size==0){
                                dit_area=""
                                t_ditIndex=-1
                            }else if (t_ditIndex <= 0 || t_ditIndex >= region_datalist.get(t_proIndex).children!![t_cityIndex].children!!.size) {
                                t_ditIndex = Math.abs(t_ditIndex) % region_datalist.get(t_proIndex).children!![t_cityIndex].children!!.size
                                dit_area="-"+dit!![t_ditIndex].value
                            }else{
                                dit_area="-"+dit!![t_ditIndex].value
                            }

                            tv_address_add_regininfo.setTextColor(Color.BLACK)
                            tv_address_add_regininfo.text=region_datalist[t_proIndex].value+"-"+
                                    region_datalist[t_proIndex].children!![t_cityIndex].value+dit_area
                        }
                ).show()
            }
            //设置默认值
            R.id.iv_address_add_defalut_add->{
                iv_address_add_defalut_add.isSelected = !iv_address_add_defalut_add.isSelected
            }
            //提交添加地址
            R.id.btn_address_add_finish->{
                if (arguments!=null&&arguments.containsKey("edit_address")){
                    var address=arguments.getSerializable("edit_address") as Address
                    editAddress(address.addr_id,address.region_id)
                }else{
                    addAddress()
                }

            }

        }
    }


    override fun ObjectMessage(msg: Message?) {
    }

    /**
     * 获取地区JSON数据
     */
    fun regionJson(){
        NetworkRequest.getInstance().regionJson(object:NetworkCallback<JsonObject>(){
            override fun onSuccess(data: JsonObject?) {
                if (data==null){
                    return
                }
                region_datalist= GsonUtil.jsonToList(data!!.get("region").asJsonArray.toString(),Province::class.java) as ArrayList<Province>

            }

            override fun onFailure(code: Int, msg: String?) {
            }

        })
    }

    /**
     * 添加地址
     */
    fun addAddress(){
        var hashmap=HashMap<String,String>()
        hashmap.put("accessToken",SharePerferenceUtil.getInstance().getValue(Constance.user_accessToken,"").toString())

        var dit_id=""
        if (t_ditIndex!=-1){
            dit_id=","+region_datalist[t_proIndex].children!![t_cityIndex].children!![t_ditIndex].id
        }
        if(t_proIndex==-1||t_cityIndex==-1){
            hashmap.put("area","")
        }else {
            hashmap.put("area", region_datalist[t_proIndex].id + "," + region_datalist[t_proIndex].children!![t_cityIndex].id + dit_id)
        }
        hashmap.put("addr",et_address_add_areadetail.text.toString())
        hashmap.put("name",et_address_add_username.text.toString())
        hashmap.put("mobile",et_address_add_userphone.text.toString())
        hashmap.put("zip",et_address_add_zip.text.toString())
        var def_addr=if(iv_address_add_defalut_add.isSelected){
            1
        }else{
            0
        }
        hashmap.put("def_addr",def_addr.toString())


        NetworkRequest.getInstance().memberAddressCreate(hashmap,object:NetworkCallback<JsonObject>(){
            override fun onSuccess(data: JsonObject?) {
                (activity as MallActivity).supportFragmentManager.popBackStack()
            }

            override fun onFailure(code: Int, msg: String?) {
                CToast(msg)
            }

        })
    }


    /**
     * 编辑地址
     */
    fun editAddress(addr_id:String,area:String){
        var hashmap=HashMap<String,String>()
        hashmap.put("accessToken",SharePerferenceUtil.getInstance().getValue(Constance.user_accessToken,"").toString())
        hashmap.put("addr_id",addr_id)
        var dit_id=""
        if (t_ditIndex!=-1){
            dit_id=","+region_datalist[t_proIndex].children!![t_cityIndex].children!![t_ditIndex].id
        }
        if(t_proIndex==-1||t_cityIndex==-1){
            hashmap.put("area",area)
        }else {
            hashmap.put("area", region_datalist[t_proIndex].id + "," + region_datalist[t_proIndex].children!![t_cityIndex].id + dit_id)
        }
        hashmap.put("addr",et_address_add_areadetail.text.toString())
        hashmap.put("name",et_address_add_username.text.toString())
        hashmap.put("mobile",et_address_add_userphone.text.toString())
        hashmap.put("zip",et_address_add_zip.text.toString())
        var def_addr=if(iv_address_add_defalut_add.isSelected){
            1
        }else{
            0
        }
        hashmap.put("def_addr",def_addr.toString())


        NetworkRequest.getInstance().memberAddressUpdate(hashmap,object:NetworkCallback<JsonObject>(){
            override fun onSuccess(data: JsonObject?) {
                (activity as MallActivity).supportFragmentManager.popBackStack()
            }

            override fun onFailure(code: Int, msg: String?) {
                CToast(msg)
            }

        })
    }

    override fun onDestroy() {
        if (addressAddDialog !=null&& addressAddDialog!!.isShowing){
            addressAddDialog!!.dismiss()
        }
        super.onDestroy()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden){
            if (addressAddDialog !=null&& addressAddDialog!!.isShowing){
                addressAddDialog!!.dismiss()
            }
        }
    }
}

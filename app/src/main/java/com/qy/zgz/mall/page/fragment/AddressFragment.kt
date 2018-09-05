package com.qy.zgz.mall.page.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Message
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.Button
import android.widget.ImageView
import com.google.gson.JsonObject
import com.qy.zgz.mall.Model.Address
import com.qy.zgz.mall.R
import com.qy.zgz.mall.adapter.AddressAdapter
import com.qy.zgz.mall.base.BaseFragment
import com.qy.zgz.mall.network.Constance
import com.qy.zgz.mall.network.NetworkCallback
import com.qy.zgz.mall.network.NetworkRequest
import com.qy.zgz.mall.page.index.MallActivity
import com.qy.zgz.mall.utils.GsonUtil
import com.qy.zgz.mall.utils.SharePerferenceUtil
import com.yanzhenjie.recyclerview.swipe.*
import com.zhy.autolayout.utils.AutoUtils
import org.xutils.view.annotation.ContentView
import org.xutils.view.annotation.ViewInject
import java.lang.Exception
import java.util.*

/**
 * 地址管理
 */
@ContentView(R.layout.fragment_address)
class AddressFragment : BaseFragment() {

    @ViewInject(R.id.iv_address_back)
    lateinit var iv_address_back:ImageView

    @ViewInject(R.id.rv_address_info)
    lateinit var rv_address_info: SwipeMenuRecyclerView

    @ViewInject(R.id.btn_address_add)
    lateinit var btn_address_add: Button


    var addressAdapter: AddressAdapter?=null

    override fun init() {
        //设置点击监听器
        iv_address_back.setOnClickListener(this)
        btn_address_add.setOnClickListener(this)

        //设置布局管理器
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        rv_address_info.layoutManager = linearLayoutManager
        //添加Android自带的分割线
        rv_address_info.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        // 创建菜单：
        var mSwipeMenuCreator = SwipeMenuCreator { swipeLeftMenu, swipeRightMenu, viewType ->
            var defalutItem = SwipeMenuItem(context)
            defalutItem.text="设为默认"
            defalutItem.textSize=AutoUtils.getPercentWidthSize(45)
            defalutItem.height = MATCH_PARENT
            defalutItem.width = AutoUtils.getPercentWidthSize(400)
            defalutItem.setTextColorResource(R.color.coloer_wither)
            defalutItem.setBackground(R.color.color_grey)
            swipeRightMenu.addMenuItem(defalutItem); // 在Item右侧添加一个菜单。

            var editItem = SwipeMenuItem(context)
            editItem.text="编辑"
            editItem.textSize=AutoUtils.getPercentWidthSize(45)
            editItem.height = MATCH_PARENT
            editItem.width= AutoUtils.getPercentWidthSize(200)
            editItem.setTextColorResource(R.color.coloer_wither)
            editItem.setBackground(R.color.primary_dark)
            swipeRightMenu.addMenuItem(editItem); // 在Item右侧添加一个菜单。

            var delItem = SwipeMenuItem(context)
            // 各种文字和图标属性设置。
            delItem.text="删除"
            delItem.textSize=AutoUtils.getPercentWidthSize(45)
            delItem.height = MATCH_PARENT
            delItem.width= AutoUtils.getPercentWidthSize(200)
            delItem.setTextColorResource(R.color.coloer_wither)
            delItem.setBackground(R.color.color_red)
            swipeRightMenu.addMenuItem(delItem); // 在Item右侧添加一个菜单。

            // 注意：哪边不想要菜单，那么不要添加即可。
        }

        rv_address_info.setSwipeMenuCreator(mSwipeMenuCreator)
        // 侧滑菜单点击监听。
        rv_address_info.setSwipeMenuItemClickListener(object :SwipeMenuItemClickListener{
            override fun onItemClick(menuBridge: SwipeMenuBridge) {
                // 任何操作必须先关闭菜单，否则可能出现Item菜单打开状态错乱。
                menuBridge.closeMenu()
                when(menuBridge.position){
                    //设置默认地址
                    0->{
                        defaultAddress(addressAdapter!!.list[menuBridge.adapterPosition].addr_id)
                    }
                    //编辑地址
                    1->{
                        var bundel=Bundle()
                        bundel.putSerializable("edit_address",addressAdapter!!.list[menuBridge.adapterPosition])
                        switchContent(this@AddressFragment, AddressAddFragment(),bundel, FragmentManager.OnBackStackChangedListener {onResume() })
                    }
                    //删除地址
                    2->{
                        if (null!=addressAdapter){
                            delAddress(addressAdapter!!.list[menuBridge.adapterPosition].addr_id)
                        }

                    }
                }
            }
        }
        )
        //设置列表点击监听器
        rv_address_info.setSwipeItemClickListener { itemView, position ->
            var bundle=Bundle()
            if (null!=addressAdapter){
                bundle.putSerializable("sel_address",addressAdapter!!.list[position])
            }
            val i = Intent()
            i.putExtras(bundle)
            targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, i)
            (activity as MallActivity).supportFragmentManager.popBackStack()
        }


    }

    override fun onResume() {
        super.onResume()
        if (isAdded){
            findAddress()
        }
    }

     override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.iv_address_back->{
                var bundle=Bundle()
                bundle.putString("back","back")
                val i = Intent()
                i.putExtras(bundle)
                targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, i)
                (activity as MallActivity).supportFragmentManager.popBackStack()
            }
            R.id.btn_address_add->{
                switchContent(this, AddressAddFragment(),null, FragmentManager.OnBackStackChangedListener {onResume() })
            }
        }
    }

    override fun ObjectMessage(msg: Message?) {
    }

    /**
     * 查看地址
     */
    fun findAddress(){
        var hashmap=HashMap<String,String>()
        hashmap.put("accessToken", SharePerferenceUtil.getInstance().getValue(Constance.user_accessToken, "")!!.toString())

        NetworkRequest.getInstance().memberAddressList(hashmap,object :NetworkCallback<JsonObject>(){
            override fun onSuccess(data: JsonObject) {
                if (data.isJsonNull||!data.has("list")){
                    return
                }
                var rjson=GsonUtil.jsonToList(data.get("list").asJsonArray.toString(),Address::class.java) as ArrayList<Address>
                addressAdapter= AddressAdapter(activity, rjson)
                rv_address_info.adapter=addressAdapter
            }

            override fun onFailure(code: Int, msg: String?) {
            }

            override fun onNetWorkFailure(e: Exception?) {
                CToast("网络错误，请联系管理人员！")
                super.onNetWorkFailure(e)
            }
        })
    }

    /**
     * 删除地址
     */
    fun delAddress(addr_id:String){
        var hashmap=HashMap<String,String>()
        hashmap.put("accessToken", SharePerferenceUtil.getInstance().getValue(Constance.user_accessToken, "")!!.toString())
        hashmap.put("addr_id", addr_id)
        NetworkRequest.getInstance().memberAddressDelete(hashmap,object :NetworkCallback<JsonObject>(){
            override fun onSuccess(data: JsonObject) {
              if (null!=addressAdapter){
                  addressAdapter!!.list= addressAdapter!!.list.filter { it.addr_id!=addr_id } as ArrayList<Address>
                  addressAdapter!!.notifyDataSetChanged()
              }

            }

            override fun onFailure(code: Int, msg: String?) {
            }

            override fun onNetWorkFailure(e: Exception?) {
                CToast("网络错误，请联系管理人员！")
                super.onNetWorkFailure(e)
            }
        })
    }


    /**
     * 设置默认地址
     */
    fun defaultAddress(addr_id:String){
        var hashmap=HashMap<String,String>()
        hashmap.put("accessToken", SharePerferenceUtil.getInstance().getValue(Constance.user_accessToken, "")!!.toString())
        hashmap.put("addr_id", addr_id)
        NetworkRequest.getInstance().memberAddressSetdefault(hashmap,object :NetworkCallback<JsonObject>(){
            override fun onSuccess(data: JsonObject) {
                if (null!=addressAdapter){
                    addressAdapter!!.list.forEach {
                        if (it.addr_id==addr_id){
                            it.def_addr="1"
                        } else{
                            it.def_addr="0"
                        }
                    }
                    addressAdapter!!.notifyDataSetChanged()
                }
            }

            override fun onFailure(code: Int, msg: String?) {
            }

            override fun onNetWorkFailure(e: Exception?) {
                CToast("网络错误，请联系管理人员！")
                super.onNetWorkFailure(e)
            }
        })
    }


    //控制FRAGMENT切换
    fun switchContent(from: Fragment, to: Fragment, bundle: Bundle?,backStackChangedListener: FragmentManager.OnBackStackChangedListener?)
    {
        val mgrFragment =  (context as MallActivity).supportFragmentManager
        if (null!=bundle){
            to.arguments=bundle
        }
        if (null!=backStackChangedListener){
            mgrFragment.addOnBackStackChangedListener(backStackChangedListener)
        }

        if (!to.isAdded) {    // 先判断是否被add过
            mgrFragment.beginTransaction().hide(from).add(R.id.main_fragment_content, to).addToBackStack(null).commit(); // 隐藏当前的fragment，add下一个到Activity中
        } else {
            mgrFragment.beginTransaction().hide(from).show(to).addToBackStack(null).commit(); // 隐藏当前的fragment，显示下一个
        }
    }



}
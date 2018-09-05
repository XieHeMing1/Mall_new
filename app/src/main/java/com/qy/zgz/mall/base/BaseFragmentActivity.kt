package com.zk.zk_online.base

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.app.FragmentActivity
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.kaopiz.kprogresshud.KProgressHUD
import com.qy.zgz.mall.MyApplication
import org.json.JSONException
import org.json.JSONObject
import org.xutils.common.Callback
import org.xutils.http.RequestParams
import org.xutils.x

/**
 * Created by ZYB on 2017/11/5 0005.
 */
abstract class BaseFragmentActivity :FragmentActivity(),View.OnClickListener
{
    private var toast: Toast? = null

    private var mContext: Context? = null

    internal var dia: KProgressHUD? = null

    private var hint: String? = null

    private var bundle = Bundle()

    private var message: Message? = null

    //是否显示加载框
    private var isDialogshow = true;


    internal var handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            getMessage(msg!!.data);
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = this
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        // 去除头部
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT// 强制竖屏
        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT)// 初始化一个toast，解决多次弹出toast冲突问题
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            //透明导航栏
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        MyApplication.getInstance().addActivity(this)
        init(savedInstanceState)
    }

    abstract fun init(savedInstanceState: Bundle?)

    abstract override fun onClick(v: View?)

    /* @Subscribe(threadMode = ThreadMode.MainThread)
     public abstract fun getMessage(bundle: Bundle)*/
    abstract fun getMessage(bundle: Bundle)

    override fun onResume() {
        super.onResume()
//        EventBus.getDefault().register(mContext)

    }

    /*  override fun onPause() {
         super.onPause()
 //        EventBus.getDefault().unregister(mContext)
     }*/


    /**
     * 提示框
     */
    fun SToast(message: String) {
        if (!TextUtils.isEmpty(message.toString())) {
            synchronized(this) {
                toast!!.cancel()
                toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
                toast!!.show()
            }
        }
    }

    /**
     * POST请求
     */
    fun httpPostMap(url: String, map: Map<String, Any>, what: Int) {
        val params = RequestParams(url)
        params.isAsJsonContent = true
        params.addHeader("Content-Type", "application/json")
        val gson = Gson()
        val req_Data = gson.toJson(map)
        params.bodyContent = req_Data
        params.addHeader("charset", "utf-8")
        params.connectTimeout = 10000
        x.http().post(params, object : Callback.CommonCallback<String> {
            override fun onSuccess(result: String) {
                var jresult: JSONObject? = null
                try {
                    jresult = JSONObject(result)
                    if (jresult.get("code").toString() == "0") {
                        if (jresult.has("data")) {
                            bundle.putString("data", jresult.get("data").toString())
                        } else {
                            bundle.putString("data", "")
                        }

                        bundle.putInt("what", what)
                        bundle.putString("allresult", jresult.toString())
                        message = Message.obtain()
                        message!!.setData(bundle)
                        handler.sendMessage(message)
                    } else {
                        SToast(jresult.getString("message"))
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }


            }

            override fun onError(ex: Throwable, isOnCallback: Boolean) {

            }

            override fun onCancelled(cex: Callback.CancelledException) {

            }

            override fun onFinished() {
            }
        })
    }


    /**
     * 提示框
     */
    fun SToast(id: Int) {
        synchronized(this) {
            toast!!.cancel()
            toast = Toast.makeText(this, resources.getString(id), Toast.LENGTH_SHORT)
            toast!!.show()
        }
    }

    /**
     * 居中提示框
     */
    fun CToast(message: String) {
        synchronized(this) {
            toast!!.cancel()
            toast = Toast.makeText(mContext, message, Toast.LENGTH_SHORT)
            toast!!.setGravity(Gravity.CENTER, 0, 0)
            toast!!.show()
        }
    }




    /**
     * 提供给titbar 的back 放回事件
     */
    open fun backfinish(view: View) {
        finish()
    }

    /**
     * 隐藏键盘
     */
    fun unkeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (inputMethodManager.isActive) {
            inputMethodManager.hideSoftInputFromWindow((this as Activity).currentFocus!!.windowToken, 0)
        }
    }




    //取消加载框
    fun dismissDia()
    {
        try {
            if (dia!=null && dia!!.isShowing)
            {
                dia!!.dismiss()
            }
        }
        catch (e:Exception)
        {
            e.printStackTrace()
        }
    }

    //接口错误
    open fun onSystemError(what:Int,errormsg:String,allresult:JsonObject){
        SToast(errormsg)
    }

    fun setShowPro(b: Boolean) {
        isDialogshow = b;
    }

    override fun onDestroy() {
        MyApplication.getInstance().removeActivity(this)
        super.onDestroy()
    }
}
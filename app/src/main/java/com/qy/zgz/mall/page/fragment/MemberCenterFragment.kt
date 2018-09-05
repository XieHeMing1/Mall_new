package com.qy.zgz.mall.page.fragment

import android.os.Message
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import com.qy.zgz.mall.R
import com.qy.zgz.mall.base.BaseFragment
import com.qy.zgz.mall.network.Constance
import com.qy.zgz.mall.page.index.MallActivity
import com.qy.zgz.mall.utils.QRBitmapUtils
import com.qy.zgz.mall.utils.SharePerferenceUtil
import com.qy.zgz.mall.utils.Utils
import org.xutils.view.annotation.ContentView
import org.xutils.view.annotation.ViewInject

@ContentView(R.layout.fragment_menber_center)
class MemberCenterFragment : BaseFragment() {

    @ViewInject(R.id.iv_menber_center_qrcode)
    lateinit var iv_menber_center_qrcode:ImageView;

    @ViewInject(R.id.iv_menber_center_refresh_qrcode)
    lateinit var iv_menber_center_refresh_qrcode:ImageView;



    override fun init() {
        iv_menber_center_refresh_qrcode.setOnClickListener(this)
    }

    override fun onResume() {
        if(!TextUtils.isEmpty(MallActivity.wx_qrcode)){
            iv_menber_center_qrcode.setImageBitmap(QRBitmapUtils.createQRCode(MallActivity.wx_qrcode,300))
        }
        super.onResume()
    }

    override fun ObjectMessage(msg: Message?) {
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            //刷新
            R.id.iv_menber_center_refresh_qrcode->{
                if (!Utils.isFastClick(1000)){
                    try {
                        (context as MallActivity).CreateScanCode(SharePerferenceUtil.getInstance()
                                .getValue(Constance.MachineID, "")!!.toString())
                    }catch (e:Exception){

                    }
                }
            }
        }

    }

}

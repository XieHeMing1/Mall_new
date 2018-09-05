package com.qy.zgz.mall.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.zxing.WriterException;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.qy.zgz.mall.Model.BuyCoins;
import com.qy.zgz.mall.Model.MemberInfo;
import com.qy.zgz.mall.R;
import com.qy.zgz.mall.network.Constance;
import com.qy.zgz.mall.network.XutilsCallback;
import com.qy.zgz.mall.page.money_purchase.BuyCoinsActivity;
import com.qy.zgz.mall.utils.GsonUtil;
import com.qy.zgz.mall.utils.HttpUtils;
import com.qy.zgz.mall.utils.InputUtils;
import com.qy.zgz.mall.utils.QRBitmapUtils;
import com.qy.zgz.mall.utils.SharePerferenceUtil;
import com.qy.zgz.mall.utils.SignParamUtil;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;


/**
 * Created by ZYB on 2017/11/21 0021.
 */

public class TisPayDialog {

    private Dialog dialog;
    private View contentview;
    private Context mcontext;

    private TextView tv_dialog_window_pay_tis_countdown;

    private ImageView iv_dialog_window_pay_tis_qrcode;

    private AutoLinearLayout all_dialog_window_pay_tis_bg;

    private ImageView iv_close;

    private Button btn_dialog_window_pay_tis_out;

    private Button btn_dialog_window_pay_tis_save;

    private AutoLinearLayout all_dialog_window_pay_tis_group;


    //订单号
    private String OrderNo="";

    //zhifu类型
    private String paytype="";

    //套餐ID
    private ArrayList<BuyCoins> taocanInfoList;

    //币数
    private int coinsnum=0;


    private Handler payHandler;

    private CountDownTimer dialog_countDown;

    public TisPayDialog(Context context)
    {
        mcontext=context;
        dialog=new Dialog(mcontext);
        payHandler=new Handler();
        OrderNo="";
        paytype="";
    }

    public TisPayDialog create()
    {
        contentview= LayoutInflater.from(mcontext).inflate(R.layout.dialog_window_pay_tis,null);
        AutoUtils.auto(contentview);
        tv_dialog_window_pay_tis_countdown=contentview.findViewById(R.id.tv_dialog_window_pay_tis_countdown);
        iv_dialog_window_pay_tis_qrcode=contentview.findViewById(R.id.iv_dialog_window_pay_tis_qrcode);
        all_dialog_window_pay_tis_bg=contentview.findViewById(R.id.all_dialog_window_pay_tis_bg);
        iv_close=contentview.findViewById(R.id.iv_close);
        btn_dialog_window_pay_tis_out=contentview.findViewById(R.id.btn_dialog_window_pay_tis_out);
        btn_dialog_window_pay_tis_save=contentview.findViewById(R.id.btn_dialog_window_pay_tis_save);
        all_dialog_window_pay_tis_group=contentview.findViewById(R.id.all_dialog_window_pay_tis_group);


        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        btn_dialog_window_pay_tis_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_countDown.cancel();
                SalePackage(coinsnum,false);
                dismiss();
            }
        });

        btn_dialog_window_pay_tis_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_countDown.cancel();
                SalePackage(coinsnum,true);
                dismiss();
            }
        });

        return this;
    }




    public TisPayDialog setMessage(String message)
    {
        tv_dialog_window_pay_tis_countdown.setText(message);
        return this;
    }

    public TisPayDialog setOrderNo(String orderNo,String ptype,ArrayList<BuyCoins> buyCoinsList)
    {
        OrderNo=orderNo;
        paytype=ptype;
        taocanInfoList=buyCoinsList;
        coinsnum=0;
        if (null!=buyCoinsList && !buyCoinsList.isEmpty()){
            for(BuyCoins buyCoins:buyCoinsList){
                coinsnum+=((int)Double.parseDouble(buyCoins.getStandardCoins())+(int)(Double.parseDouble(buyCoins.getCoins1())))*(int)Double.parseDouble(buyCoins.getBuyQty());

            }
        }

        return this;
    }

    public TisPayDialog setBg(String dawableid)
    {
        if (dawableid.equals("6")){
            all_dialog_window_pay_tis_bg.setBackgroundResource(R.drawable.bg_wxpay);
        }else if (dawableid.equals("7")){
            all_dialog_window_pay_tis_bg.setBackgroundResource(R.drawable.bg_zfbpay);
        }

        return this;
    }

    public TisPayDialog setImageViewUrl(String url)
    {
        try {
            iv_dialog_window_pay_tis_qrcode.setImageBitmap(QRBitmapUtils.createQRCode(url,200));
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return this;
    }


    public TisPayDialog show()
    {
        if (dialog!=null && contentview!=null)
        {
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(contentview);
            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.gravity= Gravity.CENTER;
            params.width= AutoUtils.getPercentWidthSize(1600);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (dialog_countDown!=null){
                        dialog_countDown.cancel();
                    }
                    payHandler.removeCallbacks(checkRunable);
                    payHandler.removeCallbacksAndMessages(null);
                    InputUtils.Companion.closeInput(mcontext);
                }
            });
            dialog.getWindow().setAttributes(params);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

            dialog_countDown= new CountDownTimer(120000,1000) {

                @Override
                public void onTick(long millisUntilFinished) {
                    tv_dialog_window_pay_tis_countdown.setText(millisUntilFinished/1000+"秒后自动关闭");
                    try {
                        ((Activity)mcontext).onUserInteraction();
                    }catch (Exception e){

                    }
                }

                @Override
                public void onFinish() {
                    dismiss();
                }
            }.start();

            payHandler.post(checkRunable);

        }
        return  this;
    }

    public void dismiss()
    {
        if (dialog!=null && dialog.isShowing())
        {
            dialog.dismiss();
        }
    }

    public Boolean isShowing()
    {
        return dialog.isShowing();
    }


    Runnable checkRunable=new Runnable() {
        @Override
        public void run() {
            if (dialog!=null&&dialog.isShowing()){
            checkIsPayStatus(coinsnum);
            }
        }
    };


    /**
     * 验证移动支付是否已付款
     */
    public void checkIsPayStatus(int num){
        HashMap<String,String> hashmap=new HashMap<String,String>();

        hashmap.put("OrderNo",OrderNo);

        hashmap.put("sign", SignParamUtil.getSignStr(hashmap));
        HttpUtils.xPostJson(Constance.MEMBER_HOST+ Constance.CheckIsPayStatus,hashmap,new
        XutilsCallback<String> (){
            @Override
            public void  onSuccessData( String result) {
                JsonObject rjson= GsonUtil.Companion.jsonToObject(result, JsonObject.class);
                if (rjson.has("return_Code")&& rjson.get("return_Code").toString().equals("200")) {

                    if (!TextUtils.isEmpty(rjson.get("result_Msg").getAsString())){
                        payHandler.postDelayed(checkRunable,1500);
                    }else{
                        String typeid=SharePerferenceUtil.getInstance().getValue("typeId","").toString();
                        //欢乐熊版本
                        if (typeid.equals("25")){
                            SalePackage(num,true);
                            dismiss();
                            return;
                        }

                        //其他版本
                        MemberInfo memberInfo=GsonUtil.Companion.jsonToObject(SharePerferenceUtil.getInstance().getValue(Constance.member_Info,"").toString(), MemberInfo.class);
                        if (null==memberInfo){
                            SalePackage(num,false);
                            dismiss();
                        }else {

                        dialog_countDown.cancel();
                        all_dialog_window_pay_tis_group.setVisibility(View.VISIBLE);
                        dialog_countDown= new CountDownTimer(30000,1000) {

                            @Override
                            public void onTick(long millisUntilFinished) {
                                tv_dialog_window_pay_tis_countdown.setText(millisUntilFinished/1000+"秒后自动选择出币");
                                try {
                                    ((Activity)mcontext).onUserInteraction();
                                }catch (Exception e){

                                }
                            }

                            @Override
                            public void onFinish() {
                                SalePackage(num,false);
                                dismiss();
                            }
                        }.start();
                        }

                    }

                }else{
                    payHandler.postDelayed(checkRunable,1500);
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                payHandler.postDelayed(checkRunable,1500);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }



        });
    }


    /**
     * 销售套餐
     */
    public void SalePackage(int num,boolean IsSaveCard){
        KProgressHUD dia = KProgressHUD.create(mcontext).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("请稍后...").show();
        MemberInfo memberInfo=GsonUtil.Companion.jsonToObject(SharePerferenceUtil.getInstance().getValue(Constance.member_Info,"").toString(), MemberInfo.class);

        HashMap<String,String> hashmap=new HashMap<String,String>();

        hashmap.put("BarCode", UUID.randomUUID().toString());
        hashmap.put("UserID", Constance.machineUserID);
        hashmap.put("MachineID", SharePerferenceUtil.getInstance().getValue(Constance.MachineID,"").toString());

        hashmap.put("ClassTime",SharePerferenceUtil.getInstance().getValue(Constance.MachineClassTime,"").toString());
        hashmap.put("ClassID", SharePerferenceUtil.getInstance().getValue(Constance.MachineClassID,"").toString());

        HashMap<String,String> ha=new HashMap<>();
        if (null!=taocanInfoList){
            for (BuyCoins buyCoins:taocanInfoList){
                ha.put(buyCoins.getId(),buyCoins.getBuyQty()+"");
            }
        }

        hashmap.put("PackageInfo",  GsonUtil.Companion.objectToJson(ha));

        hashmap.put("IsScan", "0");
        hashmap.put("IsReOperate", "false");
        hashmap.put("OrderNO", OrderNo);


        if (null==memberInfo){
            hashmap.put("CustID", Constance.machineFLTUserID);
            hashmap.put("IsSaveCard",  false+"");
        }else{
            hashmap.put("CustID",  memberInfo.getId());
            if (num>Constance.maxOutCoinValue){
                hashmap.put("IsSaveCard",  true+"");
            }else{
                hashmap.put("IsSaveCard",  IsSaveCard+"");
            }
        }

        hashmap.put("PayType", paytype);

        hashmap.put("PreferAmount", "0");

        hashmap.put("IsHandwork",  false+"");

        hashmap.put("RemainAmount",  "0");

        hashmap.put("sign", SignParamUtil.getSignStr(hashmap));
        HttpUtils.xPostJson(Constance.MEMBER_HOST+ Constance.SalePackage,hashmap,new
                XutilsCallback<String> (){
                    @Override
                    public void  onSuccessData( String result) {
                        JsonObject rjson= GsonUtil.Companion.jsonToObject(result, JsonObject.class);
                        if (rjson.has("return_Code")&& rjson.get("return_Code").toString().equals("200")) {

                            if (Boolean.parseBoolean(hashmap.get("IsSaveCard").toString())){
                                ((BuyCoinsActivity)mcontext).updateMenterInfo();
                                if (num>Constance.maxOutCoinValue&&!IsSaveCard){
                                TisDialog dialog=new TisDialog(mcontext).create().setMessage("出币数超过单次出币最大值，币已存入卡中,是否提币?").setBtnShow()
                                        .setNegativeButtonListener(new TisDialog.NegativeButtonListener() {
                                            @Override
                                            public void onClick(View v) {

                                                ((BuyCoinsActivity)mcontext).exitMember();
                                            }
                                        })
                                        .setPositiveButtonListener(new TisDialog.PositiveButtonListener() {
                                            @Override
                                            public void onClick(View v) {
                                                ((BuyCoinsActivity)mcontext).goTakeCoin();
                                            }
                                        })
                                        .show();
                                }else{
                                   new TisDialog(mcontext).create().setMessage("币已成功存入卡中").show();
                                }
                            }else {
                                ((BuyCoinsActivity)mcontext).setStockBillID(rjson.getAsJsonObject("Data").get("ReturnID").getAsString());
                                ((BuyCoinsActivity)mcontext).outCoins(num);
                            }

                        }else{
                            TisDialog dialog=new TisDialog(mcontext).create().setMessage(rjson.get("result_Msg").getAsString()).show();
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        TisDialog dialog=new TisDialog(mcontext).create().setMessage("网络异常,请联系管理员!").show();

                    }

                    @Override
                    public void onCancelled(CancelledException cex) {

                    }

                    @Override
                    public void onFinished() {
                        if (dia != null && dia.isShowing()) {
                            dia.dismiss();
                        }
                    }



                });
    }
}

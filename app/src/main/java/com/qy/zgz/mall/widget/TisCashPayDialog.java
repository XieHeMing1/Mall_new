package com.qy.zgz.mall.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.qy.zgz.mall.Dbsql.DBDao;
import com.qy.zgz.mall.KDSerialPort.KDSerialPort.kd;
import com.qy.zgz.mall.Model.BuyCoins;
import com.qy.zgz.mall.Model.MemberInfo;
import com.qy.zgz.mall.R;
import com.qy.zgz.mall.network.Constance;
import com.qy.zgz.mall.network.XutilsCallback;
import com.qy.zgz.mall.page.money_purchase.BuyCoinsActivity;
import com.qy.zgz.mall.utils.DateUtils;
import com.qy.zgz.mall.utils.GsonUtil;
import com.qy.zgz.mall.utils.HttpUtils;
import com.qy.zgz.mall.utils.InputUtils;
import com.qy.zgz.mall.utils.SharePerferenceUtil;
import com.qy.zgz.mall.utils.SignParamUtil;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.utils.AutoUtils;

import org.xutils.common.Callback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;


/**
 * Created by ZYB on 2017/11/21 0021.
 */

public class TisCashPayDialog {

    private Dialog dialog;
    private View contentview;
    private Context mcontext;

    private TextView tv_dialog_window_cashpay_tis_countdown;


    private ImageView iv_close;

    private Button btn_dialog_window_cashpay_tis_out;

    private Button btn_dialog_window_cashpay_tis_save;

    private AutoLinearLayout all_dialog_window_cashpay_tis_group;

    private TextView tv_dialog_window_cashpay_tis_shouldpay;

    private TextView tv_dialog_window_cashpay_tis_hadpay;

    private TextView tv_dialog_window_cashpay_tis_price;

    private TextView tv_dialog_window_cashpay_tis_coins;

    //套餐信息
    private ArrayList<BuyCoins> buyCoinsInfoList;

    //币数
    private int coinsnum=0;

    private Handler payHandler;

    private CountDownTimer dialog_countDown;

    //当前本地数据ID
    private  int localId=-1;

    //是否匹配套餐(手动关闭时)
    private boolean isMachTaoCan=true;

    private UpdateErrorListListener updateErrorListListener;

    public TisCashPayDialog(Context context)
    {
        mcontext=context;
        dialog=new Dialog(mcontext);
        payHandler=new Handler();
        localId=-1;
        isMachTaoCan=true;
    }

    public TisCashPayDialog create()
    {
        contentview= LayoutInflater.from(mcontext).inflate(R.layout.dialog_window_pay_cash_tis,null);
        AutoUtils.auto(contentview);
        tv_dialog_window_cashpay_tis_countdown=contentview.findViewById(R.id.tv_dialog_window_cashpay_tis_countdown);
        iv_close=contentview.findViewById(R.id.iv_close);
        btn_dialog_window_cashpay_tis_out=contentview.findViewById(R.id.btn_dialog_window_cashpay_tis_out);
        btn_dialog_window_cashpay_tis_save=contentview.findViewById(R.id.btn_dialog_window_cashpay_tis_save);
        all_dialog_window_cashpay_tis_group=contentview.findViewById(R.id.all_dialog_window_cashpay_tis_group);
        tv_dialog_window_cashpay_tis_shouldpay=contentview.findViewById(R.id.tv_dialog_window_cashpay_tis_shouldpay);
        tv_dialog_window_cashpay_tis_hadpay=contentview.findViewById(R.id.tv_dialog_window_cashpay_tis_hadpay);
        tv_dialog_window_cashpay_tis_coins=contentview.findViewById(R.id.tv_dialog_window_cashpay_tis_coins);
        tv_dialog_window_cashpay_tis_price=contentview.findViewById(R.id.tv_dialog_window_cashpay_tis_price);

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TisDialog(mcontext).create()
                        .setMessage("是否取消支付,取消后已投入的钱不会退还")
                        .setBtnShow()
                        .setCountDownEvent(new TisDialog.CountDownEvent() {
                            @Override
                            public void handCountDownEvent() {
                                dialog_countDown.cancel();
                            }
                        })
                        .setNegativeButtonListener(new TisDialog.NegativeButtonListener() {
                            @Override
                            public void onClick(View v) {
                                dialog_countDown.start();
                            }
                        })
                        .setPositiveButtonListener(new TisDialog.PositiveButtonListener() {
                            @Override
                            public void onClick(View v) {
                                if (dialog.isShowing()&&Double.parseDouble(getHadPrice())>=Double.parseDouble(getShouldPrice())){
                                    SalePackage(coinsnum,false);
                                    //更新异常订单列表
                                    if (null==buyCoinsInfoList && null!=updateErrorListListener){
                                        updateErrorListListener.update();
                                    }
                                }else {
                                    if (dialog.isShowing()&&isMachTaoCan){
                                        autoMathPackageListByHand(Double.parseDouble(getHadPrice())+"");
                                    }else{
                                        //更新异常订单列表
                                        if (null!=updateErrorListListener){
                                            updateErrorListListener.update();
                                        }
                                    }
                                }



                                dismiss();
                            }
                        })
                        .show();
            }
        });

        btn_dialog_window_cashpay_tis_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_countDown.cancel();
                SalePackage(coinsnum,false);
                dismiss();
            }
        });

        btn_dialog_window_cashpay_tis_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_countDown.cancel();
                SalePackage(coinsnum,true);
                dismiss();
            }
        });

        return this;
    }

    //设置是否匹配套餐(手动关闭时)
    public TisCashPayDialog setIsMachTaoCan(boolean b)
    {
        isMachTaoCan=b;

        return this;
    }

    //更新ID
    public TisCashPayDialog setLocalId(int id)
    {
        localId=id;

        return this;
    }

    //获取ID
    public int getLocalId()
    {
        return localId;
    }

    //显示存出币按钮
    public TisCashPayDialog showOutSaveButton()
    {
        all_dialog_window_cashpay_tis_group.setVisibility(View.VISIBLE);
        iv_close.setVisibility(View.GONE);
        try{
            if (((BuyCoinsActivity)mcontext).isMemberLogining()){
                btn_dialog_window_cashpay_tis_save.setVisibility(View.VISIBLE);
            }else{
                btn_dialog_window_cashpay_tis_save.setVisibility(View.GONE);
            }
        }catch (Exception e){

        }


        return this;
    }

    //获取已付款
    public String getHadPrice()
    {
        return tv_dialog_window_cashpay_tis_hadpay.getText().toString();
    }

    //更新已付款
    public TisCashPayDialog setHadPrice(String price)
    {
        tv_dialog_window_cashpay_tis_hadpay.setText(price);

        return this;
    }


    //获取应付款
    public String getShouldPrice()
    {
        return tv_dialog_window_cashpay_tis_shouldpay.getText().toString();
    }

    //更新已付款
    public TisCashPayDialog setShouldPrice(String price)
    {
        tv_dialog_window_cashpay_tis_shouldpay.setText(price);

        return this;
    }


    //刷新定时器
    public TisCashPayDialog freshCountDown()
    {
       if (null!=dialog_countDown){
           dialog_countDown.cancel();
           dialog_countDown.start();
       }

        return this;
    }



    public TisCashPayDialog setMessage(String message)
    {
        tv_dialog_window_cashpay_tis_countdown.setText(message);
        return this;
    }

    public TisCashPayDialog setInfo(ArrayList<BuyCoins> buyCoinsList)
    {
        if (null!=buyCoinsList && !buyCoinsList.isEmpty()){
            //赋值
            buyCoinsInfoList=buyCoinsList;
            coinsnum=0;
            double sumPrice=0;
            for (BuyCoins buyCoins:buyCoinsList){
                coinsnum+=((int)Double.parseDouble(buyCoins.getStandardCoins())+(int)(Double.parseDouble(buyCoins.getCoins1())))*(int)Double.parseDouble(buyCoins.getBuyQty());
                sumPrice+=Double.parseDouble(buyCoins.getPackagePrice())*(int)Double.parseDouble(buyCoins.getBuyQty());
            }

            tv_dialog_window_cashpay_tis_price.setText("¥"+sumPrice);
            tv_dialog_window_cashpay_tis_coins.setText(coinsnum+"币");

            setShouldPrice(sumPrice+"");

        }
        return this;
    }



    public TisCashPayDialog show()
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
                    payHandler.removeCallbacksAndMessages(null);
                    InputUtils.Companion.closeInput(mcontext);
                    //设置不可收钱
                    try{
                        kd.sp().colseBanknote();
                    }catch (Exception e){

                    }
                }
            });
            dialog.getWindow().setAttributes(params);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            //设置可收钱
            try{
                kd.sp().enableBanknote();
            }catch (Exception e){

            }

            dialog.show();

            dialog_countDown= new CountDownTimer(120000,1000) {

                @Override
                public void onTick(long millisUntilFinished) {
                    tv_dialog_window_cashpay_tis_countdown.setText("("+DateUtils.timeParse(millisUntilFinished)+")");
                    try {
                        ((Activity)mcontext).onUserInteraction();
                    }catch (Exception e){

                    }
                }

                @Override
                public void onFinish() {
                    if (dialog.isShowing()&&Double.parseDouble(getHadPrice())>=Double.parseDouble(getShouldPrice())){
                        SalePackage(coinsnum,false);
                    }
                    dismiss();
                }
            }.start();

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


    /**
     * 销售套餐
     */
    public void SalePackage(int num,boolean IsSaveCard){
        if (null==buyCoinsInfoList){
            return;
        }
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
        for (BuyCoins buyCoins:buyCoinsInfoList){
            ha.put(buyCoins.getId(),buyCoins.getBuyQty()+"");
        }
        hashmap.put("PackageInfo",  GsonUtil.Companion.objectToJson(ha));

        hashmap.put("IsScan", "0");
        hashmap.put("IsReOperate", "false");

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

        hashmap.put("PayType", "0");

        hashmap.put("PreferAmount", "0");

        hashmap.put("IsHandwork",  false+"");

        hashmap.put("RemainAmount",String.format("%.2f",Double.parseDouble(getHadPrice())-Double.parseDouble(getShouldPrice())));



        hashmap.put("sign", SignParamUtil.getSignStr(hashmap));
        HttpUtils.xPostJson(Constance.MEMBER_HOST+ Constance.SalePackage,hashmap,new
                XutilsCallback<String> (){
                    @Override
                    public void  onSuccessData( String result) {
                        JsonObject rjson= GsonUtil.Companion.jsonToObject(result, JsonObject.class);
                        if (rjson.has("return_Code")&& rjson.get("return_Code").toString().equals("200")) {

                           //修改本地现金数据状态
                            List<Integer> id=new ArrayList<>();
                            id.add(localId);
                             DBDao.getInstance().updateStateReceiveMoneyRecord(id);

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


    /**
     * 自动匹配套餐(手动取消匹配)
     */
    public void autoMathPackageListByHand(String price) {
        buyCoinsInfoList=null;
        KProgressHUD dia = KProgressHUD.create(mcontext).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("请稍后...").show();
        MemberInfo memberInfo = GsonUtil.Companion.jsonToObject(SharePerferenceUtil.getInstance().getValue(Constance.member_Info, "").toString(), MemberInfo.class);

        HashMap hashmap =new HashMap<String, String>();

        hashmap.put("PackageType", "Pa01");
        hashmap.put("MachineID", SharePerferenceUtil.getInstance().getValue(Constance.MachineID, "").toString());

        if (null == memberInfo) {
            hashmap.put("CustID", Constance.machineFLTUserID);
        } else {
            hashmap.put("CustID", memberInfo.getId());
        }

        hashmap.put("Amount", price);
        hashmap.put("sign", SignParamUtil.getSignStr(hashmap));
        HttpUtils.xPostJson(Constance.MEMBER_HOST + Constance.AutoMathPackageList, hashmap, new XutilsCallback<String>() {
            @Override
            public void onSuccessData(String result) {
                JsonObject rjson = GsonUtil.Companion.jsonToObject(result, JsonObject.class);
                if (rjson.has("return_Code") && rjson.get("return_Code").toString().equals("200")
                        && rjson.get("Data").getAsJsonArray().size()>0) {

                    buyCoinsInfoList=(ArrayList<BuyCoins>) GsonUtil.Companion.jsonToList(rjson.get("Data").getAsJsonArray().toString(),BuyCoins.class);
                    setInfo(buyCoinsInfoList);
                    SalePackage(coinsnum,false);

                } else {
                   new TisDialog(mcontext).create().setMessage("未匹配到套餐!").show();
                }
            }

            @Override
            public void onError( Throwable ex, boolean isOnCallback) {
                new TisDialog(mcontext).create().setMessage("未匹配到套餐!").show();
            }

            @Override
            public void onCancelled(Callback.CancelledException cex) {

            }

            @Override
            public void onFinished() {
                if (dia != null && dia.isShowing()) {
                    dia.dismiss();
                }
            }


        });
    }

    public interface UpdateErrorListListener{
        void update();
    }

    public TisCashPayDialog setUpdateErrorListListener(UpdateErrorListListener listener){
        updateErrorListListener=listener;
        return this;
    }
}

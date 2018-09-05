package com.qy.zgz.mall.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.qy.zgz.mall.KDSerialPort.KDSerialPort.kd;
import com.qy.zgz.mall.R;
import com.qy.zgz.mall.page.money_purchase.BuyCoinsActivity;
import com.qy.zgz.mall.utils.InputUtils;
import com.qy.zgz.mall.utils.Utils;
import com.zhy.autolayout.utils.AutoUtils;



/**
 * Created by ZYB on 2017/11/21 0021.
 */

public class TisOutCoinsDialog {

    private Dialog dialog;
    private View contentview;
    private Context mcontext;

    private TextView tv_outcoins_tis_title;

    private TextView tv_outcoins_tis_num;

    private TextView tv_outcoins_tis_isbug;
    private TextView tv_outcoins_tis_totalnum;

    private TextView tv_close;


    private Button btn_outcoins_tis_continue;

    private Handler handler;

    public TisOutCoinsDialog(Context context)
    {
        mcontext=context;
        dialog=new Dialog(mcontext);
        handler=new Handler();
    }

    public TisOutCoinsDialog create()
    {
        contentview= LayoutInflater.from(mcontext).inflate(R.layout.dialog_window_outcoins_tis,null);
        AutoUtils.auto(contentview);
        tv_outcoins_tis_title=contentview.findViewById(R.id.tv_outcoins_tis_title);
        tv_outcoins_tis_num=contentview.findViewById(R.id.tv_outcoins_tis_num);
        tv_outcoins_tis_totalnum=contentview.findViewById(R.id.tv_outcoins_tis_totalnum);
        tv_outcoins_tis_isbug=contentview.findViewById(R.id.tv_outcoins_tis_isbug);
        btn_outcoins_tis_continue=contentview.findViewById(R.id.btn_outcoins_tis_continue);
        tv_outcoins_tis_isbug.setVisibility(View.GONE);
        tv_close=contentview.findViewById(R.id.tv_close);

        btn_outcoins_tis_continue.setSelected(true);

        tv_outcoins_tis_isbug.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                try{
                    //结束本轮出币
                    kd.sp().bdCleanError();
                    kd.sp().bdCoinOuted();
                    ((BuyCoinsActivity)mcontext).exitMember();
                }catch (Exception e){

                }
                dismiss();
                return false;
            }
        });

        tv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    //结束本轮出币
                    kd.sp().bdCleanError();
                    kd.sp().bdCoinOuted();
                    ((BuyCoinsActivity)mcontext).exitMember();
                }catch (Exception e){

                }
                dismiss();

            }
        });

        btn_outcoins_tis_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    if (Utils.isFastClick(800)){
                        return;
                    }
                try{
                    //继续出币
//                    kd.sp().bdCleanError();
                    if (btn_outcoins_tis_continue.isSelected()){
                        btn_outcoins_tis_continue.setText("继续出币");
                        btn_outcoins_tis_continue.setSelected(false);
                        kd.sp().bdStopToCoin();
                    }else{
                        btn_outcoins_tis_continue.setText("停止出币");
                        btn_outcoins_tis_continue.setSelected(true);
                        kd.sp().bdContinueToCoin();
                    }

                }catch (Exception e){

                }
            }
        });

        return this;
    }

    public TisOutCoinsDialog showContiune(int vis){
        btn_outcoins_tis_continue.setVisibility(vis);
        return this;
    }

    public TisOutCoinsDialog showClose(){
        tv_close.setVisibility(View.VISIBLE);
        return this;
    }

    public TisOutCoinsDialog setNum(String num){
        tv_outcoins_tis_num.setText(num);
        return this;
    }

    public String getNum(){

        return tv_outcoins_tis_num.getText().toString();
    }

    public TisOutCoinsDialog setTotalNumVisibility(int vis){
        tv_outcoins_tis_totalnum.setVisibility(vis);
        return this;
    }

    public TisOutCoinsDialog setTotalNum(String Totalnum){
        tv_outcoins_tis_totalnum.setText(Totalnum);
        return this;
    }

    public TisOutCoinsDialog setBugText(String text){
        tv_outcoins_tis_isbug.setText(text);
        return this;
    }

    public TisOutCoinsDialog showBug(int vis){
        tv_outcoins_tis_isbug.setVisibility(vis);
//        btn_outcoins_tis_continue.setVisibility(vis);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return this;
    }

    public TisOutCoinsDialog refresh(){
        if (dialog!=null){
            dialog.notify();
        }
        return this;
    }


    public TisOutCoinsDialog show()
    {
        if (dialog!=null && contentview!=null)
        {
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(contentview);
            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.gravity= Gravity.CENTER;
            params.width= WindowManager.LayoutParams.WRAP_CONTENT;
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    InputUtils.Companion.closeInput(mcontext);
                }
            });
            dialog.getWindow().setAttributes(params);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

            handler.post(runnable);

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
     * 模拟用户点击,防止退出登录
     */
    private  Runnable runnable=new Runnable() {
        @Override
        public void run() {
            try {
                ((BuyCoinsActivity)mcontext).onUserInteraction();
            }catch (Exception e){

            }
            if (dialog!=null&&dialog.isShowing()){
                startAnalogyUserClick();
            }
        }
    };

    /**
     * 开始模拟用户点击
     */
    private void startAnalogyUserClick(){
        handler.postDelayed(runnable,1000);
    }

}

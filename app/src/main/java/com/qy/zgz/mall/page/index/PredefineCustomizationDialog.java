package com.qy.zgz.mall.page.index;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.qy.zgz.mall.R;
import com.qy.zgz.mall.network.Constance;
import com.qy.zgz.mall.utils.InputUtils;
import com.zhy.autolayout.utils.AutoUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * Created by LCB on 2018/2/4.
 */

public class PredefineCustomizationDialog implements View.OnTouchListener{

        private Dialog dialog;
        private View contentview;
        private Context mcontext;
        private ImageView imageView;
        private Button submit;
        private EditText info;
        private EditText phone;
        private ImageView tv_close;
        private TextView tv_remark;
        private String shopid="-1";

        private TextView tv_predefine_customer_waya;
        private TextView tv_predefine_customer_wayb;

        public PredefineCustomizationDialog(Context context)
        {
            mcontext=context;
            dialog=new Dialog(mcontext,R.style.dialogstyle);
            Constance.lastTouchTime=System.currentTimeMillis();

        }

        public PredefineCustomizationDialog create()
        {
            contentview= LayoutInflater.from(mcontext).inflate(R.layout.predefine_customer,null);
            AutoUtils.auto(contentview);
            imageView=contentview.findViewById(R.id.iv_predefine_qrimg);
            submit=contentview.findViewById(R.id.btn_predefine_customer_submit);
            info=contentview.findViewById(R.id.et_predefine_customer_info);
            phone=contentview.findViewById(R.id.et_predefine_customer_phone);
            tv_close=contentview.findViewById(R.id.tv_close);
            tv_remark=contentview.findViewById(R.id.tv_predefine_customer_remark);
            tv_predefine_customer_waya=contentview.findViewById(R.id.tv_predefine_customer_waya);
            tv_predefine_customer_wayb=contentview.findViewById(R.id.tv_predefine_customer_wayb);

            //设置点击监听器
            phone.addTextChangedListener(textWatcher);
            info.addTextChangedListener(textWatcher);
            contentview.setOnTouchListener(this);
            phone.setOnTouchListener(this);
            info.setOnTouchListener(this);
            tv_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Constance.lastTouchTime=System.currentTimeMillis();
                    dismiss();
                }
            });

            tv_predefine_customer_waya.setText("方法一：扫一扫二维码，\n" +
                    "马上联系客服预约定制");

            tv_predefine_customer_wayb.setText("方法二：在下方表格,填\n" +
                    "写您的愿望礼物");

            Spanned sp = Html.fromHtml("<Font size='36px'>说明：<br/>1.如果您对我们的服务有任何问题，也请您留下您宝贵的意见或建议，我们将会用来<br/>改善我们的软件与服务." +
                    "<br/>2.如有订单相关或紧急问题请拨打 </Font><Font color='#EA2329' size='36px'>182-0764-0625</Font>");
            tv_remark.setText(sp);

            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        Constance.lastTouchTime=System.currentTimeMillis();
                        RequestParams params=new RequestParams(Constance.HOST+ Constance.PREDEFINEGIFT);
                        params.setAsJsonContent(true);
                        params.addHeader("charset","utf-8");
                        params.addHeader("Content-Type","application/json");
                        params.setConnectTimeout(10000);
                        params.addBodyParameter("shop_id",shopid);
                        params.addBodyParameter("contack",phone.getText().toString().trim());
                        params.addBodyParameter("content",info.getText().toString());
                        x.http().post(params, new Callback.CommonCallback<String>() {
                            @Override
                            public void onSuccess(String result) {
                                try {
                                    JSONObject s=new JSONObject(result);
                                    if ("0".equals(s.getString("errorcode"))){
                                        dismiss();
                                        Toast toast=Toast.makeText(mcontext,"",Toast.LENGTH_SHORT);
                                        toast.setText("提交成功,请在商城公众号查看");
                                        toast.setGravity(Gravity.CENTER,0,0);
                                        toast.show();
                                    }else{
                                    Toast toast=Toast.makeText(mcontext,"",Toast.LENGTH_SHORT);
                                    toast.setText(s.getString("msg"));
                                    toast.setGravity(Gravity.CENTER,0,0);
                                    toast.show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(Throwable ex, boolean isOnCallback) {
                                Toast toast=Toast.makeText(mcontext,"",Toast.LENGTH_SHORT);
                                toast.setText("请检查网络");
                                toast.setGravity(Gravity.CENTER,0,0);
                                toast.show();
                            }

                            @Override
                            public void onCancelled(CancelledException cex) {

                            }

                            @Override
                            public void onFinished() {

                            }
                        });
                }
            });

            return this;
        }

        public PredefineCustomizationDialog show()
        {
            if (dialog!=null && contentview!=null)
            {
                dialog.setContentView(contentview);
                WindowManager.LayoutParams params=dialog.getWindow().getAttributes();
                params.gravity= Gravity.CENTER;
//                params.width=AutoUtils.getPercentWidthSize(1800);
                params.width=WindowManager.LayoutParams.WRAP_CONTENT;
                dialog.getWindow().setAttributes(params);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setCancelable(true);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        //关闭键盘
                        InputUtils.Companion.closeInput(mcontext);
                    }
                });
                dialog.show();


            }
            return  this;
        }

        public void dismiss()
        {
            Constance.lastTouchTime=System.currentTimeMillis();
            if (dialog!=null && dialog.isShowing())
            {
                dialog.dismiss();
            }
        }

        public Boolean isShowing()
        {
            return dialog.isShowing();
        }

        public PredefineCustomizationDialog setWebUrl(String url,String shop_id){
            try {
                if (TextUtils.isEmpty(url)){
                    url=" ";
                }
                this.shopid=shop_id;
//                imageView.setImageBitmap(QRBitmapUtils.createQRCode(url,AutoUtils.getPercentWidthSize(800)));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return  this;
        }

    //监听输入框字体变化
    TextWatcher textWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Constance.lastTouchTime=System.currentTimeMillis();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Constance.lastTouchTime=System.currentTimeMillis();
        return false;
    }
}

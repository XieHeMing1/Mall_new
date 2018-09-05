package com.qy.zgz.mall.page.index;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.bigkoo.pickerview.adapter.ArrayWheelAdapter;
import com.contrarywind.listener.OnItemSelectedListener;
import com.qy.zgz.mall.Model.City;
import com.qy.zgz.mall.Model.District;
import com.qy.zgz.mall.Model.Province;
import com.qy.zgz.mall.R;
import com.qy.zgz.mall.network.Constance;
import com.qy.zgz.mall.widget.MyWheelView;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.ArrayList;

/**
 * Created by LCB on 2018/2/4.
 */

public class AddressAddDialog implements View.OnTouchListener{

        private Dialog dialog;
        private View contentview;
        private Context mcontext;
        private Button submit;
        private Button cancel;
        private MyWheelView wv_pro;
        private MyWheelView wv_city;
        private MyWheelView wv_dit;
        private ComfireListence comfireListence;

        private ArrayList<Province> regin_data ;
        private MyWheelView.OnWheelTouchListener listener=new MyWheelView.OnWheelTouchListener() {
            @Override
            public void onTouch() {
                Log.w("touched","touched");
                ((MallActivity)mcontext).onUserInteraction();
            }
        };


        public AddressAddDialog(Context context)
        {
            mcontext=context;
            dialog=new Dialog(mcontext,R.style.dialogstyle);
            Constance.lastTouchTime=System.currentTimeMillis();

        }

        public AddressAddDialog create()
        {
            contentview= LayoutInflater.from(mcontext).inflate(R.layout.dialog_address_add,null);
            AutoUtils.auto(contentview);
            submit=contentview.findViewById(R.id.btn_dialog_submit);
            cancel=contentview.findViewById(R.id.btn_dialog_cancel);
            wv_pro=contentview.findViewById(R.id.wv_pro);
            wv_city=contentview.findViewById(R.id.wv_city);
            wv_dit=contentview.findViewById(R.id.wv_dit);
            wv_pro.setTextSize(AutoUtils.getPercentHeightSize(120));
            wv_city.setTextSize(AutoUtils.getPercentHeightSize(120));
            wv_dit.setTextSize(AutoUtils.getPercentHeightSize(120));
            wv_pro.setListener(listener);
            wv_city.setListener(listener);
            wv_dit.setListener(listener);


            //设置点击监听器
            contentview.setOnTouchListener(this);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Constance.lastTouchTime=System.currentTimeMillis();
                    dismiss();
                }
            });


            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Constance.lastTouchTime=System.currentTimeMillis();
                    dismiss();
                    if (comfireListence!=null) {
                        comfireListence.comfire(wv_pro.getCurrentItem(), wv_city.getCurrentItem(), wv_dit.getCurrentItem());
                    }
                }
            });

            return this;
        }

        public AddressAddDialog show()
        {
            if (dialog!=null && contentview!=null)
            {
                dialog.setContentView(contentview);
                WindowManager.LayoutParams params=dialog.getWindow().getAttributes();
                params.gravity= Gravity.BOTTOM;
//                params.width=AutoUtils.getPercentWidthSize(1800);
                params.width=WindowManager.LayoutParams.WRAP_CONTENT;
                dialog.getWindow().setAttributes(params);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setCancelable(true);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {

                    }
                });
                if (dialog.isShowing())
                    dialog.dismiss();
                dialog.show();


            }
            return  this;
        }

    public AddressAddDialog setAddressData(ArrayList<Province> reginData)
    {
        regin_data=reginData;
        if (regin_data.size()==0){
            return this;
        }
        ArrayList<String> pro_data=new ArrayList<String>();
        ArrayList<String> city_data=new ArrayList<String>();
        ArrayList<String> dit_data=new ArrayList<String>();
        for ( Province it:regin_data){

          pro_data.add(it.getValue());
        }
        //
//        for ( Province it:regin_data){
            if (regin_data.get(0).getChildren()==null){
                regin_data.get(0).setChildren(new ArrayList<City>());
            }
            for ( City itc:regin_data.get(0).getChildren()){

                city_data.add(itc.getValue());
            }
//        }

        //
//        for ( Province it:regin_data){
//            if (it.getChildren()==null){
//                it.setChildren(new ArrayList<City>());
//            }
//            for ( City itc:it.getChildren()){
                if (regin_data.get(0).getChildren().get(0).getChildren()==null){
                    regin_data.get(0).getChildren().get(0).setChildren(new ArrayList<District>());
                }

                for ( District itd:regin_data.get(0).getChildren().get(0).getChildren()){
                    dit_data.add(itd.getValue());

                }

//            }
//        }

        //添加一个默认数据
        if (city_data.size()==0){
            city_data.add("");
        }
        if (dit_data.size()==0){
            dit_data.add("");
        }

        wv_pro.setAdapter(new ArrayWheelAdapter(pro_data));
        wv_city.setAdapter(new ArrayWheelAdapter(city_data));
        wv_dit.setAdapter(new ArrayWheelAdapter(dit_data));
        wv_pro.setInitPosition(0);
        wv_city.setInitPosition(0);
        wv_dit.setInitPosition(0);
        wv_pro.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
               updateCity(index);
            }
        });

        wv_city.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                updateDit(wv_pro.getCurrentItem(),index);
            }
        });


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



    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Constance.lastTouchTime=System.currentTimeMillis();
        try {
            ((MallActivity)mcontext).onUserInteraction();
        }catch (Exception e){

        }

        return false;
    }

    //确认地址监听
     interface  ComfireListence{
        void comfire(int proIndex,int cityIndex,int ditIndex);
    }

    public AddressAddDialog setComfireListence(ComfireListence comfireListence){
        this.comfireListence=comfireListence;
        return this;
    }

    /**
     * 更新显示城市列表
     */
    private void updateCity(int proIndex){
        ArrayList<String> city_data=new ArrayList<String>();
        ArrayList<String> dit_data=new ArrayList<String>();

        if (proIndex<=0||proIndex>=regin_data.size()){
            proIndex=Math.abs(proIndex)%regin_data.size();
        }

        //城市
        if (regin_data.get(proIndex).getChildren()==null){
            regin_data.get(proIndex).setChildren(new ArrayList<City>());
        }
        for ( City itc:regin_data.get(proIndex).getChildren()){

            city_data.add(itc.getValue());
        }

        //区域
        if (regin_data.get(proIndex).getChildren().get(0).getChildren()==null){
            regin_data.get(proIndex).getChildren().get(0).setChildren(new ArrayList<District>());
        }

        for ( District itd:regin_data.get(proIndex).getChildren().get(0).getChildren()){
            dit_data.add(itd.getValue());
        }

        //添加一个默认数据
        if (city_data.size()==0){
            city_data.add("");
        }
        if (dit_data.size()==0){
            dit_data.add("");
        }

        wv_city.setAdapter(new ArrayWheelAdapter(city_data));
        wv_dit.setAdapter(new ArrayWheelAdapter(dit_data));
        wv_city.setInitPosition(0);
        wv_dit.setInitPosition(0);
    }

    /**
     * 更新显示区域列表
     */
    private void updateDit(int proIndex,int cityIndex){
        ArrayList<String> dit_data=new ArrayList<String>();

        if (proIndex<=0||proIndex>=regin_data.size()){
            proIndex=Math.abs(proIndex)%regin_data.size();
        }

        if (cityIndex<=0||cityIndex>=regin_data.get(proIndex).getChildren().size()){
            cityIndex=Math.abs(cityIndex)%regin_data.get(proIndex).getChildren().size();
        }

        //区域
        if (regin_data.get(proIndex).getChildren().get(cityIndex).getChildren()==null){
            regin_data.get(proIndex).getChildren().get(cityIndex).setChildren(new ArrayList<District>());
        }

        for ( District itd:regin_data.get(proIndex).getChildren().get(cityIndex).getChildren()){
            dit_data.add(itd.getValue());
        }

        //添加一个默认数据

        if (dit_data.size()==0){
            dit_data.add("");
        }

        wv_dit.setAdapter(new ArrayWheelAdapter(dit_data));
        wv_dit.setInitPosition(0);
    }
}

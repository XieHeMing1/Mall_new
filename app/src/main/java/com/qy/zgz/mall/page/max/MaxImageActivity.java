package com.qy.zgz.mall.page.max;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.zxing.WriterException;
import com.qy.zgz.mall.Model.Cinemadata;
import com.qy.zgz.mall.MyApplication;
import com.qy.zgz.mall.R;
import com.qy.zgz.mall.network.Constance;
import com.qy.zgz.mall.network.NetworkCallback;
import com.qy.zgz.mall.network.NetworkRequest;
import com.qy.zgz.mall.page.index.MallActivity;
import com.qy.zgz.mall.utils.FileManager;
import com.qy.zgz.mall.utils.QRBitmapUtils;
import com.qy.zgz.mall.utils.SharePerferenceUtil;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.HashMap;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 详情页
 */
public class MaxImageActivity extends Activity implements TextToSpeech.OnInitListener{
    private final int TIMEOUT=60*1000;
    @BindView(R.id.tv_text)
    public TextView tvText;
    @BindView(R.id.rlv_max)
    public RecyclerView rlvMax;
    @BindView(R.id.iv_qcode)
    public SimpleDraweeView ivQcode;
    @BindView(R.id.tv_scan)
    public TextView tvScan;

    @BindView(R.id.rl_image)
    RelativeLayout rlImage;

    @BindView(R.id.ll_detail)
    public AutoLinearLayout llDetail;
    private MaxListAdapter listAdapter;
    private Handler handler = new Handler();
    private Toast toast;
    private Context mContext=this;
    private String sku_id="";

    @BindView(R.id.btn_max_image_addcar)
    public Button btn_max_image_addcar;

    public TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题

        MyApplication.getInstance().addActivity(this);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_max_image);
        ButterKnife.bind(this);
        mContext=this;
        toast = Toast.makeText(mContext, "", Toast.LENGTH_SHORT);//初始化一个toast，解决多次弹出toast冲突问题
        handler.postDelayed(runnable, TIMEOUT);


        Cinemadata cinemadata = (Cinemadata) getIntent().getSerializableExtra("cinema");
//        if(cinemadata != null) {
//            Log.i("MaxActivity", "cinemadata != null" + cinemadata);
//            return;
//        }else {
//            Log.i("MaxActivity", "cinemadata == null");
//        }
        listAdapter = new MaxListAdapter(cinemadata.getThumb());
        rlvMax.setLayoutManager(new LinearLayoutManager(this));
        rlvMax.setAdapter(listAdapter);

        if (!TextUtils.isEmpty(cinemadata.getSku_id())){
            sku_id=cinemadata.getSku_id();
        }

        if (cinemadata.getTitle() != null && !cinemadata.getTitle().isEmpty()) {
            tvText.setText(cinemadata.getTitle());
        } else {
            tvText.setVisibility(View.GONE);
        }
        if (cinemadata.gettickets() != null && !cinemadata.getTitle().isEmpty()) {
            tvScan.setText(cinemadata.gettickets());
        } else {
            tvScan.setVisibility(View.GONE);
        }

        String fileName = FileManager.getInstance().getFileName(cinemadata.getQcode());

        if (!TextUtils.isEmpty(cinemadata.getQcode())){
            try {
                Bitmap temp = QRBitmapUtils.createQRCode(cinemadata.getQcode(),300);
                ivQcode.setImageBitmap(temp);
            } catch (WriterException e) {
                e.printStackTrace();
                ivQcode.setImageURI(Uri.parse(cinemadata.getQcode()));
            }finally {
                //ivQcode.setImageURI(Uri.parse(cinemadata.getQcode()));
            }
        }

//        if (FileManager.getInstance().isFileExists(fileName)|| !TextUtils.isEmpty(fileName)) {
//            String url = "file://" + FileManager.getInstance().getDestFileDir() + fileName;
//            ivQcode.setImageURI(Uri.parse(url));
//        } else {
//
//            try {
//                Bitmap temp = QRBitmapUtils.createQRCode(cinemadata.getQcode(),200);
//                ivQcode.setImageBitmap(temp);
//            } catch (WriterException e) {
//                e.printStackTrace();
//                ivQcode.setImageURI(Uri.parse(cinemadata.getQcode()));
//            }finally {
//                //ivQcode.setImageURI(Uri.parse(cinemadata.getQcode()));
//            }
//
//        }

        //判断是否禁用加入购物车
        if (cinemadata.getItem_type().equals("default")){
            btn_max_image_addcar.setEnabled(true);
            btn_max_image_addcar.setBackgroundResource(R.drawable.shape_green_left_15px);
        }else{
            btn_max_image_addcar.setEnabled(false);
            btn_max_image_addcar.setBackgroundResource(R.drawable.shape_grey_left_15px);

        }


       rlvMax.setOnTouchListener(new View.OnTouchListener() {
           @Override
           public boolean onTouch(View view, MotionEvent motionEvent) {
               if(motionEvent.getAction()== KeyEvent.ACTION_UP)
               {
                   handler.removeCallbacks(runnable);
                   handler.postDelayed(runnable,TIMEOUT);
               }
               return false;
           }
       });

        tts=new TextToSpeech(this,this);
        tts.setSpeechRate(0.5f);


    }

    @OnClick({R.id.arl_back, R.id.tv_close})
    public void close(View v) {
        switch (v.getId()) {
            case R.id.arl_back:
            case R.id.tv_close:
                goFinish();
                break;

        }
    }


    @OnClick({R.id.btn_max_image_buy_now,R.id.btn_max_image_addcar})
    public void onClick(View v) {
        switch (v.getId()) {
            //立即购买
            case R.id.btn_max_image_buy_now:
                if (isAbleAddCar()) {
//                    CToast("该功能暂未开放");
                   addCartNow();
                }
                break;
            //加入购物车
            case R.id.btn_max_image_addcar:
                if (isAbleAddCar()){
                    //调用加入购物车接口
                    addCart();
                }
                break;
        }
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            goFinish();
        }
    };

    /**
     * 是否可以加入购物车和结算页面
     */
    private boolean isAbleAddCar(){
        if (TextUtils.isEmpty(SharePerferenceUtil.getInstance()
                .getValue(Constance.member_Info,"").toString())){
            CToast("请登录");
            MallActivity.isUnLogin=false;
            tts.speak("请登录",TextToSpeech.QUEUE_FLUSH,null);
            goFinish();
            return false;
        }
        else if(TextUtils.isEmpty(SharePerferenceUtil.getInstance()
                .getValue(Constance.user_accessToken,"").toString())){

            CToast("请关注公众号绑卡或到前台添加手机号码,再重新登录!");
            return false;
        }else{
            return true;
        }

    }

    /**
     * 居中提示框
     */
    public void CToast(String message) {
        synchronized (mContext) {
            toast.cancel();
            toast = Toast.makeText(mContext, message, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            LinearLayout layout = (LinearLayout) toast.getView();
            TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
            v.setTextSize(AutoUtils.getPercentHeightSize(80));
            toast.show();
        }
    }

    /**
     * finish页面
     */
    private void goFinish(){
        MallActivity.isInit=false;
        MaxImageActivity.this.finish();
        // 定义出入场动画
        overridePendingTransition(R.anim.out_to_right_abit,R.anim.out_to_right);
    }

    /**
     * 加入购物车
     */
    private void addCart(){
        HashMap<String,String> hashMap=new HashMap<>();
        hashMap.put("accessToken",SharePerferenceUtil.getInstance().getValue(Constance.user_accessToken,"").toString());
        hashMap.put("quantity","1");
        hashMap.put("sku_id",sku_id);
        hashMap.put("mode","cart");
        NetworkRequest.getInstance().addCart(hashMap, new NetworkCallback<JsonObject>() {
            @Override
            public void onSuccess(JsonObject data) {
                CToast("添加成功,请到购物车中查看!");
            }

            @Override
            public void onFailure(int code, String msg) {

            }

            public void onNetWorkFailure(Exception e) {
                CToast("网络故障,请联系管理员!");
                super.onNetWorkFailure(e);
            }
        });

    }



    /**
     * 立即购买加入购物车
     */
    private void addCartNow(){
        HashMap<String,String> hashMap=new HashMap<>();
        hashMap.put("accessToken",SharePerferenceUtil.getInstance().getValue(Constance.user_accessToken,"").toString());
        hashMap.put("quantity","1");
        hashMap.put("sku_id",sku_id);
        hashMap.put("mode","fastbuy");
        NetworkRequest.getInstance().addCart(hashMap, new NetworkCallback<JsonObject>() {
            @Override
            public void onSuccess(JsonObject data) {
                //跳到结算页面
                MallActivity.isGoMall = true;
                goFinish();
            }

            @Override
            public void onFailure(int code, String msg) {

            }

            public void onNetWorkFailure(Exception e) {
                CToast("网络故障,请联系管理员!");
                super.onNetWorkFailure(e);
            }
        });

    }

    /**
     * 更新购物车商品
     */
    private void updateCart(String cart_id){
        HashMap hashmap=new HashMap<String,String>();
        hashmap.put("accessToken", SharePerferenceUtil.getInstance().getValue(Constance.user_accessToken, "").toString());
        hashmap.put("mode","fastbuy");
        hashmap.put("obj_type","item");

        JsonArray cartlist=new JsonArray();
        JsonObject goods=new JsonObject();
        goods.addProperty("is_checked","1");
        goods.addProperty("selected_promotion","0");
        goods.addProperty("totalQuantity","1");
        goods.addProperty("cart_id",cart_id);
        cartlist.add(goods);

        hashmap.put("cart_params", cartlist.toString());


        NetworkRequest.getInstance().updateCart(hashmap,new NetworkCallback<JsonArray>(){
            @Override
            public void onSuccess(JsonArray data) {

            }

            @Override
            public void onFailure(int code, String msg) {

            }
            @Override
            public void onNetWorkFailure(Exception e) {
                CToast("网络故障,请联系管理员!");
                super.onNetWorkFailure(e);
            }

        });
    }


    @Override
    protected void onDestroy() {
        if (tts!=null) {
            tts.shutdown();
        }
        MyApplication.getInstance().removeActivity(this);
        super.onDestroy();
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            //设置朗读语言
            int result = tts.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED
                    || result == TextToSpeech.ERROR) {
                Log.e("tts","数据丢失或语言不支持");

            }
            else if (result == TextToSpeech.LANG_AVAILABLE) {
                Log.e("tts","语言支持");

            }

        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

}

package com.qy.zgz.mall.page.login;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.JsonObject;
import com.qy.zgz.mall.Model.User;
import com.qy.zgz.mall.R;
import com.qy.zgz.mall.network.Constance;
import com.qy.zgz.mall.network.NetworkCallback;
import com.qy.zgz.mall.network.NetworkRequest;
import com.qy.zgz.mall.network.XutilsCallback;
import com.qy.zgz.mall.page.index.HomePageActivity;
import com.qy.zgz.mall.page.index_function.IndexFuncitonActivity;
import com.qy.zgz.mall.service.AutoInstallAccessibilityService;
import com.qy.zgz.mall.utils.AccessibilityServiceUtils;
import com.qy.zgz.mall.utils.DeviceUtil;
import com.qy.zgz.mall.utils.GsonUtil;
import com.qy.zgz.mall.utils.HttpUtils;
import com.qy.zgz.mall.utils.SharePerferenceUtil;
import com.qy.zgz.mall.utils.SignParamUtil;
import com.qy.zgz.mall.utils.ToastUtil;
import com.qy.zgz.mall.utils.UnityDialog;
import com.qy.zgz.mall.utils.Utils;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

/**
 * 登录页面
 */
public class LoginActivity extends RxAppCompatActivity {
    @BindView(R.id.et_accout)
    public EditText etAccout;

    @BindView(R.id.et_pwd)
    public EditText etPwd;

    @BindView(R.id.iv_login)
    public SimpleDraweeView ivLogin;

    @BindView(R.id.v_login_gosetting)
    public View v_login_gosetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        //获取物理MAC地址
        String mac=DeviceUtil.getMacAddress(this);
        if (mac!=null&&TextUtils.isEmpty(SharePerferenceUtil.getInstance().getValue(Constance.mac_Address,"").toString())){
            SharePerferenceUtil.getInstance().setValue(Constance.mac_Address,mac);
        }

        String typeId= (String) SharePerferenceUtil.getInstance().getValue("typeId","");
        String cinemaid=(String) SharePerferenceUtil.getInstance().getValue("cinemaid","");
        String BranchID=SharePerferenceUtil.getInstance().getValue(Constance.BranchID, "").toString();
        String Vpn= SharePerferenceUtil.getInstance().getValue(Constance.Vpn, "").toString();
        String BranchName=SharePerferenceUtil.getInstance().getValue(Constance.BranchName,"").toString();

        Intent intent;
        if(typeId!=null && !typeId.equals("")&& !TextUtils.isEmpty(BranchID)&& !TextUtils.isEmpty(Vpn)&& !TextUtils.isEmpty(BranchName))
        {
            intent=new Intent(LoginActivity.this, HomePageActivity.class);
            intent.putExtra("cinemaType",typeId);
            intent.putExtra("cinemaid",cinemaid);
            LoginActivity.this.startActivity(intent);
            LoginActivity.this.finish();
        }

        //判断是否开启智能安装服务
       if (!AccessibilityServiceUtils.Companion.getInstant().isAccessibilitySettingsOn(AutoInstallAccessibilityService.class.getCanonicalName(),this)) {
           new UnityDialog(this)
                   .setHint("是否开启智能安装？")
                   .setCancel("取消", null)
                   .setConfirm("确定", new UnityDialog.OnConfirmDialogListener() {
                       @Override
                       public void confirm(UnityDialog unityDialog, String content) {
                           unityDialog.dismiss();
                           AccessibilityServiceUtils.Companion.getInstant().openAccessibility(AutoInstallAccessibilityService.class.getCanonicalName(),LoginActivity.this);
                       }
                   }).show();
       }
        ivLogin.setImageURI(Uri.parse("res://"+this.getPackageName() + "/" + R.drawable.login_bg));
        if(Build.VERSION.SDK_INT>=23)
        {
            try{
                int i= ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if(i!= PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }

    }

    @OnLongClick({R.id.v_login_gosetting})
    public boolean longClick(View v){
        switch (v.getId()){
            case R.id.v_login_gosetting:
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                startActivity(intent);
                break;

        }

        return false;
    }

    @OnClick({R.id.btn_login})
    public void login(View v)
    {
        if(Utils.isFastClick(1000)){
            return;
        }

        String accout=etAccout.getText().toString();
        String pwd=etPwd.getText().toString();
        if(accout.equals(""))
        {
            ToastUtil.showToast(LoginActivity.this,"请输入账号");
            return;
        }
        else if(pwd.equals(""))
        {
            ToastUtil.showToast(LoginActivity.this,"请输入密码");
            return;
        }
//
        NetworkRequest.getInstance().login(accout,pwd,new NetworkCallback<User>() {
            @Override
            public void onSuccess(User data) {
                Log.i("LoginTest", "login data = " + data);
                SharePerferenceUtil.getInstance().setValue("account",accout);
                SharePerferenceUtil.getInstance().setValue("typeId",data.getTypeid());
//                SharePerferenceUtil.getInstance().setValue("typeId","7");
                SharePerferenceUtil.getInstance().setValue("cinemaid",data.getCinemaid());
                SharePerferenceUtil.getInstance().setValue("type_shop_name",data.getShop_name());
                SharePerferenceUtil.getInstance().setValue(Constance.MEMBER_HOST_TAG,data.getIp().replace("\\",""));
                Constance.MEMBER_HOST=SharePerferenceUtil.getInstance().getValue(Constance.MEMBER_HOST_TAG,"").toString();

//                Intent intent=new Intent(LoginActivity.this, IndexFuncitonActivity.class);
//                intent.putExtra("cinemaType",data.getTypeid());
//                intent.putExtra("cinemaid",data.getCinemaid());
//                LoginActivity.this.startActivity(intent);
//                LoginActivity.this.finish();

                //获取机器信息
                getDeviceInfo(SharePerferenceUtil.getInstance().getValue(Constance.mac_Address,"").toString(),data);
            }

            @Override
            public void onFailure(int code, String msg) {
                ToastUtil.showToast(LoginActivity.this,msg);
            }

        });
    }


    /**
     * 获取机器信息
     */
    public void getDeviceInfo(String mac,User user){

        HashMap<String,String> hashmap=new HashMap<String,String>();
        hashmap.put("MAC",mac);
        hashmap.put("MachineTypeID","1");
        hashmap.put("sign", SignParamUtil.getSignStr(hashmap));
        HttpUtils.xPostJson(Constance.MEMBER_HOST+Constance.MacGetMachineClassInfo,hashmap,new XutilsCallback<String>(){

            @Override
            public void onSuccessData(String result) {
                JsonObject rjson= GsonUtil.Companion.jsonToObject(result, JsonObject.class);
                if (rjson.has("return_Code")&& rjson.get("return_Code").getAsString().equals("200")){

                    String MachineID= rjson.getAsJsonObject("Data").get("MachineID").getAsString();
                    //保存机器ID
                    SharePerferenceUtil.getInstance().setValue(Constance.MachineID, MachineID);
                    SharePerferenceUtil.getInstance().setValue(Constance.MachineClassTime, rjson.getAsJsonObject("Data").get("ClassTime").getAsString());
                    SharePerferenceUtil.getInstance().setValue(Constance.MachineClassID, rjson.getAsJsonObject("Data").get("ClassID").getAsString());
                    SharePerferenceUtil.getInstance().setValue(Constance.MachineClassNAME, rjson.getAsJsonObject("Data").get("MachineName").getAsString());

                    try {
                        JsonObject datas=rjson.getAsJsonObject("Data2");
                        if (datas.has("BranchID")) {
                            SharePerferenceUtil.getInstance().setValue(Constance.BranchID, datas.get("BranchID").getAsString());
                        }
                        if (datas.has("Vpn")) {
                            SharePerferenceUtil.getInstance().setValue(Constance.Vpn, datas.get("Vpn").getAsString());
                        }
                        if (datas.has("Name")) {
                            SharePerferenceUtil.getInstance().setValue(Constance.BranchName, datas.get("Name").getAsString());
                        }
                    }catch (Exception e){

                    }
//                    SharePerferenceUtil.getInstance().setValue("typeId",user.getTypeid());
//                    SharePerferenceUtil.getInstance().setValue("cinemaid",user.getCinemaid());

                    String BranchID=SharePerferenceUtil.getInstance().getValue(Constance.BranchID, "").toString();
                    String Vpn= SharePerferenceUtil.getInstance().getValue(Constance.Vpn, "").toString();
                    String BranchName=SharePerferenceUtil.getInstance().getValue(Constance.BranchName,"").toString();
                    //判断是否少返回参数
                    if (!TextUtils.isEmpty(BranchID)&& !TextUtils.isEmpty(Vpn)&& !TextUtils.isEmpty(BranchName)){

                        Intent intent=new Intent(LoginActivity.this, HomePageActivity.class);
                        intent.putExtra("cinemaType",user.getTypeid());
                        intent.putExtra("cinemaid",user.getCinemaid());
                        intent.putExtra("shop_name",user.getShop_name());
                        LoginActivity.this.startActivity(intent);
                        LoginActivity.this.finish();
                    }else{
                        ToastUtil.showToast(LoginActivity.this,"返回信息缺失！");
                    }

                }else{
                    ToastUtil.showToast(LoginActivity.this,"机器未登记！");
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ToastUtil.showToast(LoginActivity.this,"访问链接："+Constance.MEMBER_HOST+Constance.MacGetMachineClassInfo+",ip地址错误！");
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }

        });

    }

}

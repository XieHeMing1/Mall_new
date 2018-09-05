package com.qy.zgz.mall.page.fragment;

import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.gson.JsonObject;
import com.qy.zgz.mall.BaseFragment;
import com.qy.zgz.mall.Model.MemberInfo;
import com.qy.zgz.mall.R;
import com.qy.zgz.mall.network.Constance;
import com.qy.zgz.mall.network.XutilsCallback;
import com.qy.zgz.mall.page.index.PurchaseCoinActivity;
import com.qy.zgz.mall.utils.GsonUtil;
import com.qy.zgz.mall.utils.HttpUtils;
import com.qy.zgz.mall.utils.SharePerferenceUtil;
import com.qy.zgz.mall.utils.SignParamUtil;
import com.qy.zgz.mall.utils.ToastUtil;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;

public class ModifyPwdFragment extends BaseFragment {
    /**
     * 修改密码布局
     */
    @BindView(R.id.ll_modify_pwd_layout)
    LinearLayout mLlModifyPwd;
    @BindView(R.id.et_pwdupdate_oldpwd)
    EditText mEtOldPwd;
    @BindView(R.id.et_pwdupdate_npwd)
    EditText mEtNewPwd;
    @BindView(R.id.et_pwdupdate_cpwd)
    EditText mEtConfirmPwd;
    @BindView(R.id.btn_pwdupdate_cancel)
    Button mBtnCancel;
    @BindView(R.id.btn_pwdupdate_confirm)
    Button mBtnConfirm;

    private static ModifyPwdFragment instance;

    public static ModifyPwdFragment newInstance() {
        if(instance == null) {
            instance = new ModifyPwdFragment();
        }
        return instance;
    }

    @Override
    public View getLayoutView(LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.fragment_modify_pwd, container, false);
    }

    @Override
    public void initViews(View view) {

    }

    @OnClick({R.id.btn_pwdupdate_cancel, R.id.btn_pwdupdate_confirm})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_pwdupdate_cancel:
                clearEditText();
                break;
            case R.id.btn_pwdupdate_confirm:
                updatePwd(mEtOldPwd.getText().toString(), mEtNewPwd.getText().toString(), mEtConfirmPwd.getText().toString());
                break;
            default:
                break;
        }
    }

    /**
     * 修改密码
     *
     * @param oldPwd
     * @param newPwd
     * @param confirmPwd
     */
    private void updatePwd(String oldPwd, String newPwd, String confirmPwd) {
        MemberInfo memberInfo = GsonUtil.Companion.jsonToObject(SharePerferenceUtil.getInstance().getValue(Constance.member_Info, "").toString(), MemberInfo.class);
        if (memberInfo != null) {
            HashMap<String, String> hashmap = new HashMap<String, String>();
            hashmap.put("NewPassword", newPwd);
            hashmap.put("ConfirmPassword", confirmPwd);
            hashmap.put("OldPassword", oldPwd);
            hashmap.put("CustID", memberInfo.getId());
            hashmap.put("UserID", Constance.machineUserID);
            hashmap.put("sign", SignParamUtil.getSignStr(hashmap));
            HttpUtils.xPostJson(Constance.MEMBER_HOST + Constance.UpdateCustomerPwd, hashmap, new XutilsCallback<String>() {
                @Override
                public void onSuccessData(String result) {
                    JsonObject jsonResult = GsonUtil.Companion.jsonToObject(result, JsonObject.class);
                    if (jsonResult != null && jsonResult.has("return_Code") &&
                            jsonResult.get("return_Code").toString().equals("200")) {
                        ToastUtil.showToast(mAttachActivity, "修改成功");
                    } else {
                        ToastUtil.showToast(mAttachActivity, jsonResult.get("result_Msg").toString());
                    }
                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {

                }

                @Override
                public void onFinished() {

                }
            });
        }
    }

    private void clearEditText() {
        mEtConfirmPwd.setText("");
        mEtNewPwd.setText("");
        mEtOldPwd.setText("");
    }
}

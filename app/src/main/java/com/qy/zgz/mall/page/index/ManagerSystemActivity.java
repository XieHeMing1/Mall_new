package com.qy.zgz.mall.page.index;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.qy.zgz.mall.BaseActivity;
import com.qy.zgz.mall.R;

import butterknife.BindView;
import butterknife.OnClick;

public class ManagerSystemActivity extends BaseActivity {
    @BindView(R.id.fl_manager_fragment_container)
    FrameLayout mFlContainer;
    @BindView(R.id.iv_close_system)
    ImageView mIvCloseSystem;
    @BindView(R.id.iv_reboot_system)
    ImageView mIvRebootSystem;
    @BindView(R.id.iv_manager_exception_handing)
    ImageView mIvExceptionHanding;
    @BindView(R.id.iv_manager_shift_work)
    ImageView mIvChange;
    @BindView(R.id.iv_manager_clearbug)
    ImageView mIvClearBug;
    @BindView(R.id.iv_manager_exit_account)
    ImageView mIvExit;

    @Override
    public void createView() {
        setContentView(R.layout.activity_manager_system);
    }

    @Override
    public void afterCreate(@Nullable Bundle savedInstanceState, @Nullable Intent intent) {

    }

    @OnClick({R.id.iv_close_system, R.id.iv_reboot_system, R.id.iv_manager_exception_handing,
            R.id.iv_manager_shift_work, R.id.iv_manager_clearbug, R.id.iv_manager_exit_account})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_close_system:
                break;
            case R.id.iv_reboot_system:
                break;
            case R.id.iv_manager_exception_handing:
                break;
            case R.id.iv_manager_shift_work:
                break;
            case R.id.iv_manager_clearbug:
                break;
            case R.id.iv_manager_exit_account:
                break;
        }
    }
}

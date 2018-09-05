package com.qy.zgz.mall.page.red_packet;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.qy.zgz.mall.R;
import com.qy.zgz.mall.utils.FileManager;
import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 红包
 */
public class RedPacketActivity extends Activity {
    @BindView(R.id.iv_qcode)
    public SimpleDraweeView ivQcode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_red_packet);
        ButterKnife.bind(this);

        String qcode=getIntent().getStringExtra("qcode");
        String fileName= FileManager.getInstance().getFileName(qcode);
        if(FileManager.getInstance().isFileExists(fileName))
        {
            String url="file://" + FileManager.getInstance().getDestFileDir() + fileName;
            ivQcode.setImageURI(Uri.parse(url));
        }
        else
        {
            if(qcode==null || qcode.equals(""))
            {
                return;
            }
            ivQcode.setImageURI(Uri.parse(qcode));
        }
    }

    @OnClick({R.id.iv_close,R.id.arl_back})
    public void close(View v)
    {
        this.finish();
    }
}

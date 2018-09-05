package com.qy.zgz.mall.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import java.io.File;

/**
 * Created by Administrator on 2017/12/27 0027.
 */

public class AntoUtil {
    private static String mUrl;
    private static Context mContext;

    /**
     * 外部传进来的url以便定位需要安装的APK
     *
     * @param url
     */
    public static void setUrl(String url) {
        mUrl = url;
    }

    /**
     * 安装
     *
     * @param context
     *            接收外部传进来的context
     */
    public static void install(Activity context) {
        mContext = context;
        update(context);
//        new UnityDialog(context)
//                .setHint("有新的版本，是否更新？")
//                .setCancel("取消",null)
//                .setConfirm("更新", new UnityDialog.OnConfirmDialogListener() {
//                    @Override
//                    public void confirm(UnityDialog unityDialog, String content) {
//                        unityDialog.dismiss();
//                        update(context);
//                    }
//                }).show();
        // 核心是下面几句代码

    }

    public static  void update(Activity activity)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        //判断是否是AndroidN以及更高的版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);


            Uri contentUri = FileProvider.getUriForFile(activity, activity.getPackageName()+"fileProvider", new File(mUrl));

            // 由于没有在Activity环境下启动Activity,设置下面的标签
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(new File(mUrl)), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        mContext.startActivity(intent);
    }
}
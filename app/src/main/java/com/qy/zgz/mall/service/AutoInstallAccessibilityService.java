package com.qy.zgz.mall.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.qy.zgz.mall.network.Constance;
import com.qy.zgz.mall.utils.SharePerferenceUtil;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LCB on 2018/3/8.
 * 智能安装服务
 */

public class AutoInstallAccessibilityService extends AccessibilityService {

    private static final String TAG = "AccessibilityService";
    private Map<Integer, Boolean> handleMap = new HashMap<>();
    private String[] packageNames = {"com.lenovo.security","com.lenovo.safecenter","com.android.packageinstaller"};

    /**
     * 此方法是accessibility service的配置信息 写在java类中是为了向下兼容
     */
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        AccessibilityServiceInfo mAccessibilityServiceInfo = new AccessibilityServiceInfo();
        // 响应事件的类型，这里是全部的响应事件（长按，单击，滑动等）
        mAccessibilityServiceInfo.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        // 反馈给用户的类型，这里是语音提示
        mAccessibilityServiceInfo.feedbackType = AccessibilityServiceInfo.FEEDBACK_SPOKEN;
        // 过滤的包名
        mAccessibilityServiceInfo.packageNames = packageNames;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        installApplication(event);
//        AccessibilityNodeInfo nodeInfo = event.getSource();
//        boolean isAutoInstall= (boolean) SharePerferenceUtil.getInstance().getValue(Constance.auto_Install,false);
//        if (!isAutoInstall){
//
//            return;
//        }
//
//        if (nodeInfo != null && isContainInPackages(event.getPackageName().toString())) {
//            int eventType = event.getEventType();
//
//            if (eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED || eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
//
//                if (handleMap.get(event.getWindowId()) == null) {
//                    boolean handled = iterateNodesAndHandle(nodeInfo);
//                    if (handled) {
//                        handleMap.put(event.getWindowId(), true);
//                    }
//                }
//            }
//
//        }
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public boolean onUnbind(Intent intent) {
        // 服务断开后，在偏好设置文件中将"isAllowAutoInstallation"的值设为false
        SharePerferenceUtil.getInstance().setValue(Constance.auto_Install,false);
        return super.onUnbind(intent);
    }

    //遍历节点，模拟点击安装按钮
    private boolean iterateNodesAndHandle(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo != null) {
            int childCount = nodeInfo.getChildCount();
            if ("android.widget.Button".equals(nodeInfo.getClassName())) {

                String nodeCotent = nodeInfo.getText().toString();
                Log.d(TAG, "content is: " + nodeCotent);
                if ("打开".equals(nodeCotent) ||"安装".equals(nodeCotent)){
                    SharePerferenceUtil.getInstance().setValue(Constance.auto_Install,false);
                }
                if ("下一步".equals(nodeCotent) ||"打开".equals(nodeCotent) ||"安装".equals(nodeCotent) || "完成".equals(nodeCotent) || "确定".equals(nodeCotent)) {
                    nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    return true;
                }
            }
            //遇到ScrollView的时候模拟滑动一下
            else if ("android.widget.ScrollView".equals(nodeInfo.getClassName())) {
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
            }
            for (int i = 0; i < childCount; i++) {
                AccessibilityNodeInfo childNodeInfo = nodeInfo.getChild(i);
                if (iterateNodesAndHandle(childNodeInfo)) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 判断包名
     *
     * @param str
     *            当前界面包名
     * @return
     */
    private boolean isContainInPackages(String str) {
        boolean flag = false;
        for (int i = 0; i < packageNames.length; i++) {
            if ((packageNames[i]).equals(str)) {
                flag = true;
                return flag;
            }
        }
        return flag;
    }



    /**
     * 查找关键字并执行点击按钮的操作
     *
     * @param event
     */
    @SuppressLint("NewApi")
    private void installApplication(AccessibilityEvent event) {
        String[] labels = new String[]{"确定", "安装", "下一步", "打开","完成"};
        boolean isAutoInstall = (boolean) SharePerferenceUtil.getInstance().getValue(Constance.auto_Install,false);
        // 若是true，是我们自己下载的app，可以执行自动安装，否则为普通安装
        if (isAutoInstall == true) {
            if (getRootInActiveWindow() != null && isContainInPackages(event.getPackageName().toString())) {
               for (String label:labels){
                   findNodesByText(event, label);
               }
//                // 得到“下一步”节点
//                findNodesByText(event, "下一步");
//                // 得到“安装”节点
//                findNodesByText(event, "安装");
//
//                // 得到“完成”节点
//                findNodesByText(event, "完成");
//                // 得到“打开”节点
//                findNodesByText(event, "打开");

            }
        }
    }

    /**
     * 根据文字寻找节点
     *
     * @param event
     * @param text
     *            文字
     */

    @SuppressLint("NewApi")
    private void findNodesByText(AccessibilityEvent event, String text) {
        List<AccessibilityNodeInfo> nodes = getRootInActiveWindow().findAccessibilityNodeInfosByText(text);
        if (nodes != null && !nodes.isEmpty()) {
            for (AccessibilityNodeInfo info : nodes) {
                if (info.isClickable()) {// 只有根据节点信息是下一步，安装，完成，打开，且是可以点击的时候，才执行后面的点击操作
                    if (text.equals("完成") || text.equals("打开")) {
                        Log.d(TAG,info.getText().toString());
                        // 如果安装完成，点击任意一个按钮把允许自动装的值改为false
                        SharePerferenceUtil.getInstance().setValue(Constance.auto_Install,false);
                        File file = new File(Constance.auto_Installpageage_path);
                        if (file.exists()) {
                            file.delete();
                        }
                        info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    } else {
                        info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }

                }

            }
        }

    }
}
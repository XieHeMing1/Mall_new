package com.qy.zgz.mall.KDSerialPort;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Administrator on 2018/3/30.
 */

public class util {

    /**
     * 获取版本号
     */
    public static int getVersionCode(Context context) {
        int version = 0;
        try {
            PackageInfo packageInfo = context.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            version = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    /**
     * 获取版本名称
     */
    public static String getVersion(Context context) {
        String version = "";
        try {
            PackageInfo packageInfo = context.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            version = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return version;
    }

    /**
     * 获取包名
     */
    public static String getPackageName(Context context) {
        String packageName = "";
        try {
            PackageInfo packageInfo = context.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            packageName = packageInfo.packageName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return packageName;
    }


    /**
     * 返回非空指针异常的字符
     */
    public static String getStringWithoutNull(String str) {
        if (isEmptyOrNull(str)) {
            return "";
        } else {
            return str;
        }
    }

    /**
     * 判断非空
     */
    public static boolean isEmptyOrNull(String str) {
        if (str == null) {
            return true;
        } else if (TextUtils.isEmpty(str)) {
            return true;
        } else if (str.equals("null")) {
            return true;
        } else {
            return false;
        }
    }









    /**
     * 获取Mac地址
     */
    public static String getMac() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) {
                    continue;
                }

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return null;
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }




    /**
     * 获取某个日期的前几天的日期
     *
     * @param day 日期字符串例如 2015-3-10
     * @param Num 需要减少的天数例如 7
     */
    public static String getDateBeforeNum(String day, int Num) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date nowDate = null;
        try {
            nowDate = df.parse(day);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //如果需要向后计算日期 -改为+
        Date newDate2 = new Date(nowDate.getTime() - (long) Num * 24 * 60 * 60 * 1000);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateOk = simpleDateFormat.format(newDate2);
        return dateOk;
    }

    /**
     * 获取今天的时间
     *
     * @param df 时间格式
     */
    public static String getToday(SimpleDateFormat df) {
        Date date = new Date();
        return df.format(date);
    }

    /**
     * 获取明天的日期
     *
     * @param df 时间格式
     */
    public static String getTomorrow(SimpleDateFormat df) {
        String value = "";
        try {
            Date date = new Date();
            String nowDate = df.format(date);
            Calendar cal = Calendar.getInstance();
            cal.setTime(df.parse(nowDate));
            cal.add(Calendar.DAY_OF_YEAR, +1);
            value = df.format(cal.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     * 获取当前时间
     */
    public static String getCurrentTime() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentTime = sdf.format(date);
        return currentTime;
    }

    /**
     * 根据simepleDateFormat的格式来获取当前时间
     *
     * @param format 时间格式
     */
    public static String getCurrentTime(SimpleDateFormat format) {
        Date date = new Date();
        String time = format.format(date);
        return time;
    }

    /**
     * 获取明天的日期
     */
    public static String getTomorrowTime() {
        Date date = new Date();//取时间
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(calendar.DATE, 1);//把日期往后增加一天.整数往后推,负数往前移动
        date = calendar.getTime(); //这个时间就是日期往后推一天的结果
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(date);
    }

    /**
     * 获取obj中的所有方法
     *
     * @param obj
     * @return
     */
    public static List<Method> getAllMethods(Object obj) {
        List<Method> methods = new ArrayList<>();
        Class<?> clazz = obj.getClass();
        while (!clazz.getName().equals("java.lang.Object")) {
            methods.addAll(Arrays.asList(clazz.getDeclaredMethods()));
            clazz = clazz.getSuperclass();
        }
        return methods;
    }

    /**
     * 将一个类用属性名为Key，值为Value的方式存入map
     *
     * @param obj
     * @return
     */
    public static Map<String, Object> convert2Map(Object obj) {
        Map<String, Object> map = new HashMap<>();
        List<Method> methods = getAllMethods(obj);
        for (Method m : methods) {
            String methodName = m.getName();
            if (methodName.startsWith("get")) {
                //获取属性名
                String propertyName = methodName.substring(3);
                try {
                    map.put(propertyName, m.invoke(obj));
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return map;
    }

    /**
     * 将一个对象转换成可存入数据库的ContentValues对象
     *
     * @param object 对象
     */
    public static ContentValues convert2ContentValues(Object object) {
        ContentValues values = new ContentValues();
        List<Method> methods = getAllMethods(object);
        for (Method m : methods) {
            String methodName = m.getName();
            if (methodName.startsWith("get")) {
                //获取属性名
                String propertyName = methodName.substring(3);
                try {
                    values.put(propertyName, (String) m.invoke(object));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return values;
    }

    /**
     * 判断是否已经联网
     *
     * @param context 上下文
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
        } else {
            NetworkInfo[] info = cm.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 保留2位小数
     *
     * @param value double类型的数
     */
    public static String getDecimalPoint2(double value) {
        return String.format("%.2f", value);
    }

    /**
     * 获取设备类型代表数字对应的名称
     */
    public static String setMachineKind(int machineKind) {
        String value = "";
        if (machineKind == 1) {
            value = "ICT";
        } else if (machineKind == 2) {
            value = "ITL";
        } else if (machineKind == 3) {
            value = "币斗";
        } else {
            value = "test";
        }
        return value;
    }



}

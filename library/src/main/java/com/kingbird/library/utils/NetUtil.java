package com.kingbird.library.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;

import com.kuaifa.ad.value.NetworkConnectionType;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import static com.kingbird.library.utils.Config.CONSTANT_ONE_HUNDRED;

/**
 * 网络工具类
 *
 * @author panyingdao
 * @date 2018-1-22.
 */
public class NetUtil {

    /**
     * 没有网络
     */
    public static final int NETWORK_NONE = 0;
    /**
     * WIFI
     */
    public static final int NETWORK_WIFI = 1;
    /**
     * 2G
     */
    public static final int NETWORK_2G = 2;
    /**
     * 3G
     */
    public static final int NETWORK_3G = 3;
    /**
     * 4G
     */
    public static final int NETWORK_4G = 4;
    /**
     * 手机流量
     */
    public static final int NETWORK_MOBILE = 5;

    private NetUtil() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 判断网络是否连接
     *
     * @param context 内容
     * @return 返回
     */
    public static boolean isConnected(Context context) {

        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (null != connectivity) {

            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (null != info && info.isConnected()) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {

                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 检测网络是否连接
     * Context context 对象
     */
    public static boolean isNetConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            assert cm != null;
            Network[] networks = cm.getAllNetworks();
            NetworkInfo networkInfo;
            for (Network mNetwork : networks) {
                networkInfo = cm.getNetworkInfo(mNetwork);
                if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                    return true;
                }
            }
        } else {
            if (cm != null) {
                NetworkInfo[] infos = cm.getAllNetworkInfo();
                if (infos != null) {
                    for (NetworkInfo ni : infos) {
                        if (ni.isConnected()) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

//    /**
//     * 获取当前的运营商
//     *
//     * @param context
//     * @return 运营商名字
//     */
//    public static String getOperator(Context context) {
//
//        String providersName = "";
//        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return TODO;
//        }
//        assert telephonyManager != null;
//        String imsi = telephonyManager.getSubscriberId();
//        Plog.e("qweqwes", "运营商代码" + imsi);
//        if (imsi != null) {
//            if (imsi.startsWith("46000") || imsi.startsWith("46002") || imsi.startsWith("46007")) {
//                providersName = "MOBILE";
//            } else if (imsi.startsWith("46001") || imsi.startsWith("46006")) {
//                providersName = "UNICOM";
//            } else if (imsi.startsWith("46003")) {
//                providersName = "TELECOM";
//            }
//            return providersName;
//        } else {
//            return "UNKNOWN";
//        }
//    }


    /**
     * 判断是否是wifi连接
     */
    public static boolean isWifi(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm != null && cm.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI;

    }

    /**
     * 打开网络设置界面
     */
    public static void openSetting(Activity activity) {
        Intent intent = new Intent("/");
        ComponentName cm = new ComponentName("com.android.settings",
                "com.android.settings.WirelessSettings");
        intent.setComponent(cm);
        intent.setAction("android.intent.action.VIEW");
        activity.startActivityForResult(intent, 0);
    }

    /**
     * 获取应用程序名称
     */
    public static void getAppName(Context context, String apkPath) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo info = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
            ApplicationInfo appInfo = info.applicationInfo;
            String appName = pm.getApplicationLabel(appInfo).toString();
            Plog.e("app名", appName);
            //得到安装包名称
            String packageName = appInfo.packageName;
            Plog.e("包名", packageName);
            //得到版本信息
            String version = info.versionName;
            Plog.e("版本号", version);
            //得到图标信息
            Drawable icon = pm.getApplicationIcon(appInfo);
            Plog.e("图标", icon);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新apk信息获取
     */
    public static String getApkInfo(Context context, String apkPath) {
        String packageName = null;
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
        if (info != null) {
            ApplicationInfo appInfo = info.applicationInfo;
            //得到安装包名称
            packageName = appInfo.packageName;
            //获取安装包的版本号
//            String version = info.versionName;
            Plog.e("getApkPackageName: " + packageName);
        }
        return packageName;
    }

    /**
     * 更新apk版本
     */
    public static String getApkVersion(Context context, String apkPath) {
        String version = null;
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
        if (info != null) {
            //获取安装包的版本号
            String version2 = info.versionName;
            version = version2.substring(version2.lastIndexOf(".") + 1);
            Plog.e("getApkVersion: " + "-------" + version2);
        }
        return version;
    }

    /**
     * 获取MAC地址
     *
     * @return 返回值
     */
    public static String getMac() {
        StringBuilder macSerial = new StringBuilder();
        try {
            Process pp = Runtime.getRuntime().exec(
                    "cat /sys/class/net/wlan0/address");
            //读取MAC地址
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            //读取MAC文件（按行读取）
            LineNumberReader input = new LineNumberReader(ir);

            String line;
            while ((line = input.readLine()) != null) {
                macSerial.append(line.trim());
            }
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return macSerial.toString().replace(":", "");
    }

    /**
     * 获取CPU序列号
     *
     * @return CPU序列号(16位)
     * 读取失败为"0000000000000000"
     */
    public static String getCpuSerial() {
        String str, strCpu, cpuAddress = "0000000000000000";
        try {
            //读取CPU信息
            Process pp = Runtime.getRuntime().exec("cat /proc/cpuinfo");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            //查找CPU序列号
            for (int i = 1; i < CONSTANT_ONE_HUNDRED; i++) {
                str = input.readLine();
                if (str != null) {
                    //查找到序列号所在行
                    if (str.contains("Serial")) {
                        //提取序列号
                        strCpu = str.substring(str.indexOf(":") + 1,
                                str.length());
                        //去空格
                        cpuAddress = strCpu.trim();
                        break;
                    }
                } else {
                    //文件结尾
                    break;
                }
            }
        } catch (IOException ex) {
            //赋予默认值
            ex.printStackTrace();
        }
        return cpuAddress;
    }

    /**
     * 获取运营商名字
     *
     * @return int
     */
    public static String getOperatorName(Context context) {
        /*
         * getSimOperatorName()就可以直接获取到运营商的名字
         * 也可以使用IMSI获取，getSimOperator()，然后根据返回值判断，例如"46000"为移动
         * IMSI相关链接：http://baike.baidu.com/item/imsi
         */
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        // getSimOperatorName就可以直接获取到运营商的名字
        assert telephonyManager != null;
        return telephonyManager.getSimOperatorName();
    }

    /**
     * 获取当前网络连接的类型
     *
     * @return int
     */
    public static NetworkConnectionType getNetworkState(Context context) {
        // 获取网络服务
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // 为空则认为无网络
//        if (null == connManager) {
//            return NETWORK_NONE;
//        }
        // 获取网络类型，如果为空，返回无网络
        assert connManager != null;
        @SuppressLint("MissingPermission") NetworkInfo activeNetInfo = connManager.getActiveNetworkInfo();
        if (activeNetInfo == null || !activeNetInfo.isAvailable()) {
            return NetworkConnectionType.WIFI;
        }
        // 判断是否为WIFI
        @SuppressLint("MissingPermission") NetworkInfo wifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (null != wifiInfo) {
            NetworkInfo.State state = wifiInfo.getState();
            if (null != state) {
                if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
                    return NetworkConnectionType.WIFI;
                }
            }
        }
        // 若不是WIFI，则去判断是2G、3G、4G网
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        assert telephonyManager != null;
        int networkType = telephonyManager.getNetworkType();
        switch (networkType) {
            /*
             GPRS : 2G(2.5) General Packet Radia Service 114kbps
             EDGE : 2G(2.75G) Enhanced Data Rate for GSM Evolution 384kbps
             UMTS : 3G WCDMA 联通3G Universal Mobile Telecommunication System 完整的3G移动通信技术标准
             CDMA : 2G 电信 Code Division Multiple Access 码分多址
             EVDO_0 : 3G (EVDO 全程 CDMA2000 1xEV-DO) Evolution - Data Only (Data Optimized) 153.6kps - 2.4mbps 属于3G
             EVDO_A : 3G 1.8mbps - 3.1mbps 属于3G过渡，3.5G
             1xRTT : 2G CDMA2000 1xRTT (RTT - 无线电传输技术) 144kbps 2G的过渡,
             HSDPA : 3.5G 高速下行分组接入 3.5G WCDMA High Speed Downlink Packet Access 14.4mbps
             HSUPA : 3.5G High Speed Uplink Packet Access 高速上行链路分组接入 1.4 - 5.8 mbps
             HSPA : 3G (分HSDPA,HSUPA) High Speed Packet Access
             IDEN : 2G Integrated Dispatch Enhanced Networks 集成数字增强型网络 （属于2G，来自维基百科）
             EVDO_B : 3G EV-DO Rev.B 14.7Mbps 下行 3.5G
             LTE : 4G Long Term Evolution FDD-LTE 和 TDD-LTE , 3G过渡，升级版 LTE Advanced 才是4G
             EHRPD : 3G CDMA2000向LTE 4G的中间产物 Evolved High Rate Packet Data HRPD的升级
             HSPAP : 3G HSPAP 比 HSDPA 快些
             */
            // 2G网络
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return NetworkConnectionType.G2;
            // 3G网络
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return NetworkConnectionType.G3;
            // 4G网络
            case TelephonyManager.NETWORK_TYPE_LTE:
                return NetworkConnectionType.G4;
            default:
                return NetworkConnectionType.WIFI;
        }
    }
}

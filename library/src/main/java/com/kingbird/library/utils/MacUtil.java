package com.kingbird.library.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * 类具体作用
 *
 * @author Pan yingdao
 * @date 2019/3/14/014.
 */
public class MacUtil {

    public static String getMac(Context context) {

        String strMac;
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                Plog.e("=====", "6.0以下");
                strMac = getLocalMacAddressFromWifiInfo(context);
                Plog.e("mac地址", strMac);
                return strMac.replace(":", "");
            } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N
                    && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Plog.e("=====", "6.0以上7.0以下");
                strMac = getMacFromHardware();
                return strMac.replace(":", "");
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Plog.e("=====", "7.0以上");
                if (!TextUtils.isEmpty(getMacAddress())) {
                    Plog.e("=====", "7.0以上1");
                    strMac = getMacAddress();
                    return strMac.replace(":", "");
                } else if (!TextUtils.isEmpty(getMachineHardwareAddress())) {
                    Plog.e("=====", "7.0以上2");
                    strMac = getMachineHardwareAddress();
                    return strMac.replace(":", "");
                } else {
                    Plog.e("=====", "7.0以上3");
                    strMac = getLocalMacAddressFromBusybox();
                    return strMac.replace(":", "");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Plog.e("MAC地址获取失败原因=", e.toString());
        }

//        return "02:00:00:00:00:00".replace(":", "");
        //2019-6-14 修改
        return "FF:FF:FF:FF:FF:FF".replace(":", "");

    }


    /**
     * 根据wifi信息获取本地mac
     */
    @SuppressLint("HardwareIds")
    private static String getLocalMacAddressFromWifiInfo(Context context) {
        String mac;
        if (MacUtil.isWifiAvailable(context)) {
            WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            assert wifi != null;
            WifiInfo winfo = wifi.getConnectionInfo();

            mac = winfo.getMacAddress();
        } else {
            mac = getMac();
            Plog.e("WiFi没有打开", mac);
        }
        return mac;
    }

    /**
     * wifi是否打开检查
     */
    private static boolean isWifiAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected() && networkInfo
                .getType() == ConnectivityManager.TYPE_WIFI);
    }

    /**
     * 获取MAC地址
     *
     * @return 返回值
     */
    private static String getMac() {
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
     * 遍历循环所有的网络接口，找到接口是 wlan0
     * 必须的权限 <uses-permission android:name="android.permission.INTERNET" />
     *
     * @return
     */
    private static String getMacFromHardware() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) {
                    continue;
                }

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
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
        } catch (Exception e) {
            e.printStackTrace();
        }
//        return "02:00:00:00:00:00";
        return "FF:FF:FF:FF:FF:FF";
    }

    /**
     * android 6.0及以上、7.0以下 获取mac地址
     */
    private static String getMacAddress(Context context) {

        // 如果是6.0以下，直接通过wifimanager获取
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            String macAddress0 = getMacAddress0(context);
            if (!TextUtils.isEmpty(macAddress0)) {
                return macAddress0;
            }
        }
        String str = "";
        String macSerial = "";
        try {
            Process pp = Runtime.getRuntime().exec(
                    "cat /sys/class/net/wlan0/address");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            for (; null != str; ) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();
                    break;
                }
            }
        } catch (Exception ex) {
            Plog.e("----->" + "NetInfoManager", "getMacAddress:" + ex.toString());
        }
        if ("".equals(macSerial)) {
            try {
                return loadFileAsString()
                        .toUpperCase().substring(0, 17);
            } catch (Exception e) {
                e.printStackTrace();
                Plog.e("----->" + "NetInfoManager",
                        "getMacAddress:" + e.toString());
            }

        }
        return macSerial;
    }

    @SuppressLint("HardwareIds")
    private static String getMacAddress0(Context context) {
        if (isAccessWifiStateAuthorized(context)) {
            WifiManager wifiMgr = (WifiManager) context.getApplicationContext()
                    .getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo;
            try {
                assert wifiMgr != null;
                wifiInfo = wifiMgr.getConnectionInfo();
                return wifiInfo.getMacAddress();
            } catch (Exception e) {
                Plog.e("----->" + "NetInfoManager",
                        "getMacAddress0:" + e.toString());
            }

        }
        return "";

    }

    /**
     * Check whether accessing wifi state is permitted
     */
    private static boolean isAccessWifiStateAuthorized(Context context) {
        if (PackageManager.PERMISSION_GRANTED == context
                .checkCallingOrSelfPermission("android.permission.ACCESS_WIFI_STATE")) {
            Plog.e("----->" + "NetInfoManager", "isAccessWifiStateAuthorized:"
                    + "access wifi state is enabled");
            return true;
        } else {
            return false;
        }
    }

    private static String loadFileAsString() throws Exception {
        FileReader reader = new FileReader("/sys/class/net/eth0/address");
        String text = loadReaderAsString(reader);
        reader.close();
        return text;
    }

    private static String loadReaderAsString(Reader reader) throws Exception {
        StringBuilder builder = new StringBuilder();
        char[] buffer = new char[4096];
        int readLength = reader.read(buffer);
        while (readLength >= 0) {
            builder.append(buffer, 0, readLength);
            readLength = reader.read(buffer);
        }
        return builder.toString();
    }

    /**
     * 根据IP地址获取MAC地址
     */
    private static String getMacAddress() {
        String strMacAddr = null;
        try {
            // 获得IpD地址
            InetAddress ip = getLocalInetAddress();
            byte[] b = NetworkInterface.getByInetAddress(ip)
                    .getHardwareAddress();
            StringBuilder buffer = new StringBuilder();
            for (int i = 0; i < b.length; i++) {
                if (i != 0) {
                    buffer.append(':');
                }
                String str = Integer.toHexString(b[i] & 0xFF);
                buffer.append(str.length() == 1 ? 0 + str : str);
            }
            strMacAddr = buffer.toString().toUpperCase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strMacAddr;
    }

    /**
     * 获取移动设备本地IP
     */
    private static InetAddress getLocalInetAddress() {
        InetAddress ip = null;
        try {
            // 列举
            Enumeration<NetworkInterface> enNetinterface = NetworkInterface
                    .getNetworkInterfaces();
            // 是否还有元素
            while (enNetinterface.hasMoreElements()) {
                NetworkInterface ni = enNetinterface
                        // 得到下一个元素
                        .nextElement();
                // 得到一个ip地址的列举
                Enumeration<InetAddress> enIp = ni.getInetAddresses();
                while (enIp.hasMoreElements()) {
                    ip = enIp.nextElement();
                    if (!ip.isLoopbackAddress() && !ip.getHostAddress().contains(":")) {
                        break;
                    } else {
                        ip = null;
                    }
                }

                if (ip != null) {
                    break;
                }
            }
        } catch (SocketException e) {

            e.printStackTrace();
        }
        return ip;
    }

//    /**
//     * 获取本地IP
//     */
//    private static String getLocalIpAddress() {
//        try {
//            for (Enumeration<NetworkInterface> en = NetworkInterface
//                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
//                NetworkInterface intf = en.nextElement();
//                for (Enumeration<InetAddress> enumIpAddr = intf
//                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
//                    InetAddress inetAddress = enumIpAddr.nextElement();
//                    if (!inetAddress.isLoopbackAddress()) {
//                        return inetAddress.getHostAddress();
//                    }
//                }
//            }
//        } catch (SocketException ex) {
//            ex.printStackTrace();
//        }
//        return null;
//    }

    /**
     * android 7.0及以上 （2）扫描各个网络接口获取mac地址
     * 获取设备HardwareAddress地址
     */
    private static String getMachineHardwareAddress() {
        Enumeration<NetworkInterface> interfaces = null;
        try {
            interfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        String hardWareAddress = null;
        NetworkInterface iF;
        if (interfaces == null) {
            return null;
        }
        while (interfaces.hasMoreElements()) {
            iF = interfaces.nextElement();
            try {
                hardWareAddress = bytesToString(iF.getHardwareAddress());
                if (hardWareAddress != null) {
                    break;
                }
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
        return hardWareAddress;
    }

    /***
     * byte转为String
     */
    private static String bytesToString(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        StringBuilder buf = new StringBuilder();
        for (byte b : bytes) {
            buf.append(String.format("%02X:", b));
        }
        if (buf.length() > 0) {
            buf.deleteCharAt(buf.length() - 1);
        }
        return buf.toString();
    }

    /**
     * android 7.0及以上 （3）通过busybox获取本地存储的mac地址
     * 根据busybox获取本地Mac
     */
    private static String getLocalMacAddressFromBusybox() {
        String result;
        String mac;
        result = callCmd();
        // 如果返回的result == null，则说明网络不可取
        // 对该行数据进行解析
        // 例如：eth0 Link encap:Ethernet HWaddr 00:16:E8:3E:DF:67
        if (result.length() > 0 && result.contains("HWaddr")) {
            mac = result.substring(result.indexOf("HWaddr") + 6,
                    result.length() - 1);
            result = mac;
        }
        return result;
    }

    private static String callCmd() {
        StringBuilder result = new StringBuilder();
        String line;
        try {
            Process proc = Runtime.getRuntime().exec("busybox ifconfig");
            InputStreamReader is = new InputStreamReader(proc.getInputStream());
            BufferedReader br = new BufferedReader(is);

            while ((line = br.readLine()) != null
                    && !line.contains("HWaddr")) {
                result.append(line);
            }

            result = new StringBuilder(line);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    @SuppressLint("HardwareIds")
    public static String getCpuId(Activity activity) {
        String deviceId = "0000000000000000";
        TelephonyManager tm = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
        if (null != tm) {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
            } else {
                if (tm.getDeviceId() != null) {
                    deviceId = tm.getDeviceId();
                } else {
                    deviceId = Settings.Secure.getString(activity.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                }
            }
            Plog.e("cpuId--->", deviceId);
        }
        return deviceId;
    }

}

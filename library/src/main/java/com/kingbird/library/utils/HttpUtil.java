package com.kingbird.library.utils;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 *  网络请求工具类
 *
 * @author Pan yingdao
 * @date 2017/2/19.
 */

public class HttpUtil {
    /**
     * 原生http网络请求
     */
    public static void sendOkHttpRequest(String address, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }

}

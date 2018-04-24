package com.coolweather.android.coolweather.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Http工具类
 */

public class HttpUtil {

    /**
     * 发送 Http 请求
     */
    public static void sendOkHttpRequest(String address,okhttp3.Callback callback){
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(address)
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }
}

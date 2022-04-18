package com.example.cloudreveapp.common;

import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Dns;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class http {


    public static Response DoPost(String url, Headers hds, RequestBody body) throws Exception {

        //第一步创建OKHttpClient
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
       // loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        OkHttpClient client = new OkHttpClient.Builder().dns(new EngDNS())
                .addInterceptor(loggingInterceptor)
                .build();

        //第三步创建Rquest
        Request request = new Request.Builder()
                .url(url)
                .headers(hds)
                .post(body)
                .build();
        //第四步创建call回调对象
        final Call call = client.newCall(request);
        //第五步发起请求
        String res = null;

        try {
            Response response = call.execute();

            Log.i("response", response.body().toString());

            return response;
        } catch (Exception e) {

            Log.i("Exception", e.toString());
            e.printStackTrace();
            throw   e ;
        }
    }

    public static String  DoPostGetString(String url, Headers hds, RequestBody body) {

        //第一步创建OKHttpClient
        OkHttpClient client = new OkHttpClient.Builder()
                .build();

        //第三步创建Rquest
        Request request = new Request.Builder()
                .url(url)
                .headers(hds)
                .post(body)
                .build();
        //第四步创建call回调对象
        final Call call = client.newCall(request);
        //第五步发起请求
        String res = null;

        try {
            Response response = call.execute();
            res = response.body().string();
            Log.i("response", res.toString());

        } catch (IOException e) {
            Log.i("Exception", e.toString());
            e.printStackTrace();

        }
        return res;
    }

    public static String DoGet(String url, Headers hds) {

        //第一步创建OKHttpClient
        OkHttpClient client = new OkHttpClient.Builder()
                .build();

        //第三步创建Rquest
        Request request = new Request.Builder()
                .url(url)
                .headers(hds)
                .get()
                .build();
        //第四步创建call回调对象
        final Call call = client.newCall(request);
        //第五步发起请求
        String res = null;
        try {
            Response response = call.execute();
            res = response.body().string();
            Log.i("response", res.toString());
        } catch (IOException e) {

            Log.i("Exception", e.toString());
            e.printStackTrace();
        }
        return res;
    }
}

class EngDNS implements Dns {
    @Override
    public List<InetAddress> lookup(String hostname) throws UnknownHostException {
        if (TextUtils.isEmpty(hostname)) {
            return Dns.SYSTEM.lookup(hostname);
        } else {
            try {
                List<InetAddress> inetAddressList = new ArrayList<>();
                //获取所有IP地址
                InetAddress[] inetAddresses = InetAddress.getAllByName(hostname);
                //遍历这里面所有的地址，哪些式IPV4的
                for (InetAddress inetAddress : inetAddresses) {

                    Log.i("http.dns", " http dns result: " + inetAddress.toString());
                    //ipv6 first
                    if (inetAddress instanceof Inet6Address) {
                        inetAddressList.add(0, inetAddress);
                    } else {
                        inetAddressList.add(inetAddress);
                    }
                }
                return inetAddressList;
            } catch (NullPointerException ex) {
                Log.e("http.dns", "expection " + ex);
                return Dns.SYSTEM.lookup(hostname);
            }
        }
    }
}
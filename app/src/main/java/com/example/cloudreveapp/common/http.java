package com.example.cloudreveapp.common;

import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class http {

    public static byte[] DoPost(String url, Headers hds, RequestBody body) {

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
        byte[] res = null;


        try {
            Response response = call.execute();
            res = response.body().bytes();
            Log.i("response", res.toString());
        } catch (IOException e) {

            Log.i("Exception", e.toString());
            e.printStackTrace();
        }
        return res;
    }

    public static byte[] DoGet(String url, Headers hds) {

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
        byte[] res = null;
        try {
            Response response = call.execute();
            res = response.body().bytes();
            Log.i("response", res.toString());
        } catch (IOException e) {

            Log.i("Exception", e.toString());
            e.printStackTrace();
        }
        return res;
    }
}

package com.example.cloudreveapp.common;

import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Dns;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;


public class http {
    /**
     * 信任所有https-ssl证书
     * 航信https-ssl证书是自建的(无耻，不舍得花钱购买)
     * @return
     */
    public static OkHttpClient getUnsafeOkHttpClient( HttpLoggingInterceptor loggingInterceptor) {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final javax.net.ssl.SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder().
                    dns(new engDNS()).
                    addInterceptor(loggingInterceptor);
            builder.sslSocketFactory(sslSocketFactory);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            OkHttpClient okHttpClient = builder
                    .connectTimeout(10, TimeUnit.SECONDS)//设置连接超时时间
                    //.readTimeout(20, TimeUnit.SECONDS)//设置读取超时时间

                    .build();
            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static Response DoPost(String url, Headers hds, RequestBody body) throws Exception {

        //第一步创建OKHttpClient
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
       // loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);

        // not support unsafe TLS
        //  OkHttpClient client = new OkHttpClient.Builder().dns(new engDNS())
        //    .addInterceptor(loggingInterceptor)
        //    .build();

        // support unsafe TLS
        OkHttpClient client=  getUnsafeOkHttpClient(loggingInterceptor);

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

    public static Response DoPut(String url, Headers hds, RequestBody body) throws Exception {

        //第一步创建OKHttpClient
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        // loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);

        // not support unsafe TLS
        //  OkHttpClient client = new OkHttpClient.Builder().dns(new engDNS())
        //    .addInterceptor(loggingInterceptor)
        //    .build();

        // support unsafe TLS
        OkHttpClient client=  getUnsafeOkHttpClient(loggingInterceptor);

        //第三步创建Rquest
        Request request = new Request.Builder()
                .url(url)
                .headers(hds)
                .put(body)
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
        // loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);

        // not support unsafe TLS
        //  OkHttpClient client = new OkHttpClient.Builder().dns(new engDNS())
        //    .addInterceptor(loggingInterceptor)
        //    .build();
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        // support unsafe TLS
        //第一步创建OKHttpClient
        OkHttpClient client=  getUnsafeOkHttpClient(loggingInterceptor);

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

    public static String DoGetString(String url, Headers hds) {

        // loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);

        // not support unsafe TLS
        //  OkHttpClient client = new OkHttpClient.Builder().dns(new engDNS())
        //    .addInterceptor(loggingInterceptor)
        //    .build();
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        // support unsafe TLS
        //第一步创建OKHttpClient
        OkHttpClient client=  getUnsafeOkHttpClient(loggingInterceptor);

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


    public static Response DoGet(String url, Headers hds ) throws Exception {

        // loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);

        // not support unsafe TLS
        //  OkHttpClient client = new OkHttpClient.Builder().dns(new engDNS())
        //    .addInterceptor(loggingInterceptor)
        //    .build();
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        // support unsafe TLS
        //第一步创建OKHttpClient
        OkHttpClient client=  getUnsafeOkHttpClient(loggingInterceptor);

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

            Log.i("response", response.body().toString());

            return response;
        } catch (Exception e) {

            Log.i("Exception", e.toString());
            e.printStackTrace();
            throw   e ;
        }
    }
}

class engDNS implements Dns {
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
            } catch ( Exception ex) {
                Log.e("http.dns", "expection " + ex);
                return Dns.SYSTEM.lookup(hostname);
            }
        }
    }
}
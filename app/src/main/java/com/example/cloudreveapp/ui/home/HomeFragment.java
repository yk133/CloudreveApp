package com.example.cloudreveapp.ui.home;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.DOWNLOAD_SERVICE;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.JsResult;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.cloudreveapp.R;
import com.example.cloudreveapp.common.Common;
import com.example.cloudreveapp.databinding.FragmentHomeBinding;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    private void openImageChooserActivity() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, "Image Chooser"), FILE_CHOOSER_RESULT_CODE);
    }

    // ???????????????
    private void downloadByBrowser(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    @SuppressLint("JavascriptInterface")
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        webView = (WebView) view.findViewById(R.id.webview);
        Map<String, String> header = new HashMap<>();
        header.put("cookie", Common.loginCookie);

        WebSettings webSettings=webView.getSettings();
        webSettings.setJavaScriptEnabled(true);//????????????js
        webSettings.setAllowFileAccess(true );
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true );
        webSettings.setAllowFileAccessFromFileURLs(true );
        webSettings.setSavePassword(true );;
        WebSettings settings = webView.getSettings();
        settings.setDomStorageEnabled(true);
//        webView.loadUrl("file:///android_asset/test.html");//??????asset????????????html

        //??????webview??????html??????
//        webView.loadDataWithBaseURL(null,"<html><head><title> ????????? </title></head>" +
//                "<body><h2>??????webview?????? html??????</h2></body></html>", "text/html" , "utf-8", null);

        webView.addJavascriptInterface(this,"android");//??????js?????? ??????html?????????????????????
        webView.setWebChromeClient(webChromeClient);
        webView.setWebViewClient(webViewClient);

        webView.setWebContentsDebuggingEnabled(true);

        synCookies(Common.UserHostURL);
        webView.loadUrl(Common.UserHostURL,header);//??????url

        webView.setWebChromeClient(new WebChromeClient() {

            // For Android >= 5.0
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                uploadMessageAboveL = filePathCallback;
                Log.i("File",fileChooserParams.toString());

                openImageChooserActivity();
                return true;
            }

        });

        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                downloadByBrowser(url);
            }
        });

        webView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    //???????????????????????????????????????
                    if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
                        //??????
                        webView.goBack();
                        return true;
                    }
                }
                return false;
            }
        });

        /**
         * LOAD_CACHE_ONLY: ?????????????????????????????????????????????
         * LOAD_DEFAULT: ??????????????????cache-control????????????????????????????????????
         * LOAD_NO_CACHE: ??????????????????????????????????????????.
         * LOAD_CACHE_ELSE_NETWORK????????????????????????????????????????????????no-cache?????????????????????????????????
         */
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);//??????????????????????????????????????????.

        //??????????????????
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_CHOOSER_RESULT_CODE) {
            if (null == uploadMessage && null == uploadMessageAboveL) return;
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (uploadMessageAboveL != null) {
                onActivityResultAboveL(requestCode, resultCode, data);
            } else if (uploadMessage != null) {
                uploadMessage.onReceiveValue(result);
                uploadMessage = null;
            }
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    private WebView webView;

    private ValueCallback<Uri> uploadMessage;
    private ValueCallback<Uri[]> uploadMessageAboveL;
    private final static int FILE_CHOOSER_RESULT_CODE = 10000;


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent intent) {
        if (requestCode != FILE_CHOOSER_RESULT_CODE || uploadMessageAboveL == null)
            return;
        Uri[] results = null;
        if (resultCode == RESULT_OK) {
            if (intent != null) {
                String dataString = intent.getDataString();
                ClipData clipData = intent.getClipData();
                if (clipData != null) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
                    }
                }
                if (dataString != null)
                    results = new Uri[]{Uri.parse(dataString)};
            }
        }
        uploadMessageAboveL.onReceiveValue(results);
        uploadMessageAboveL = null;
    }

    //WebViewClient????????????WebView?????????????????????????????????
    private WebViewClient webViewClient=new WebViewClient(){
        @Override
        public void onPageFinished(WebView view, String url) {//??????????????????
            //progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {//??????????????????
            // progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.i("ansen","load url:"+url);
            return super.shouldOverrideUrlLoading(view, url);
        }

        // Handle API until level 21
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            //API 21?????????????????????????????????
            return super.shouldInterceptRequest(view, url);
        }

        // Handle API 21+
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();
            //return getNewResponse(url, request.getRequestHeaders());
            return super.shouldInterceptRequest(view, url);
        }

        private WebResourceResponse getNewResponse(String url, Map<String, String> headers) {

            try {
                OkHttpClient httpClient = new OkHttpClient();

                Request.Builder builder = new Request.Builder()
                        .url(url.trim())
                        .addHeader("cookie", Common.loginCookie);

                Set<String> keySet = headers.keySet();
                for (String key : keySet) {
                    builder.addHeader(key, headers.get(key));
                }

                Request request = builder.build();

                final Response response = httpClient.newCall(request).execute();

                String conentType = response.header("Content-Type", response.body().contentType().type());
                String temp = conentType.toLowerCase();
                if (temp.contains("charset=utf-8")) {
                    conentType = conentType.replaceAll("(?i)" + "charset=utf-8", "");//???????????????????????????
                }
                if (conentType.contains(";")) {
                    conentType = conentType.replaceAll(";", "");
                    conentType = conentType.trim();
                }

                return new WebResourceResponse(
                        conentType,
                        response.header("Content-Encoding", "utf-8"),
                        response.body().byteStream()
                );

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }


    };


    //WebChromeClient????????????WebView??????Javascript????????????????????????????????????title??????????????????
    private WebChromeClient webChromeClient=new WebChromeClient(){
        //?????????js???alert???????????????????????????????????????dialog??????
        @Override
        public boolean onJsAlert(WebView webView, String url, String message, JsResult result) {
            AlertDialog.Builder localBuilder = new AlertDialog.Builder(webView.getContext());
            localBuilder.setMessage(message).setPositiveButton("??????",null);
            localBuilder.setCancelable(false);
            localBuilder.create().show();

            //??????:
            //????????????????????????:result.confirm()??????:
            //???????????????????????????????????????WebCore??????
            //??????????????????????????????
            result.confirm();
            return true;
        }

        //??????????????????
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            Log.i("ansen","????????????:"+title);
        }

        //??????????????????
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            // progressBar.setProgress(newProgress);

        }

    };

    /**
     * ??????set cookie
     */
    public void synCookies(String url) {
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.acceptCookie();
        cookieManager.removeSessionCookie();// ??????
        cookieManager.removeAllCookie();

        //cloudreve-session=XXXXXXX; Path=/; Expires=Sun, 24 Apr 2022 07:24:58 GMT; Max-Age=604800; HttpOnly

        String cookie = Common.loginCookie;
        if (cookie.isEmpty()) {
            Log.e("Cookes", "cookie is empty!!");
            return;
        }

        Log.i("Cookes", "set cookie is "+cookie);

        cookieManager.setCookie(url, "cloudreve-session=" + getCookieName(cookie, "cloudreve-session"));
        cookieManager.setCookie(url, "path_tmp=" + getCookieName(cookie, "Path"));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.flush();
        }
    }

    String getCookieName(String cookie,String name  ) {
        String cks[] = cookie.split("; ");
//        if (cks.length < 5) {
//            Log.e("Cookes", "cookie split cks size is " + cks.length);
//            return "";
//        }
        for (String n : cks) {
            String kv[] = n.split("=");
            if (kv.length < 2) {
                continue;
            }
            if (kv[0].equals(name)) {
                return kv[1];
            }
        }
        return "";
    }

}
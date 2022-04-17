package com.example.cloudreveapp;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.cloudreveapp.common.Common;
import com.example.cloudreveapp.databinding.ActivityMainBinding;
import com.example.cloudreveapp.task.fileSyncTask;
import com.example.cloudreveapp.ui.dialog.loadingDialog;
import com.example.cloudreveapp.ui.login.LoginAndSetting;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.webkit.JsResult;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.lang.String;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private WebView webView;

    private ValueCallback<Uri> uploadMessage;
    private ValueCallback<Uri[]> uploadMessageAboveL;
    private final static int FILE_CHOOSER_RESULT_CODE = 10000;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent intent) {
        if (requestCode != FILE_CHOOSER_RESULT_CODE || uploadMessageAboveL == null)
            return;
        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
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

    // 0:第一次，1：只输入了host，2： host username pwd 都有，3 可正常登录。
    int checkUserLocalState() {
        //获取SharedPreferences对象
        Context ctx = MainActivity.this;
        SharedPreferences sp = ctx.getSharedPreferences("SP", MODE_PRIVATE);
        //存入数据
//        SharedPreferences.Editor editor = sp.edit();
//        editor.putString("account", "alllens");
//         editor.commit();

        String host = sp.getString(Common.CONST_HOST, "");
        String userName = sp.getString(Common.CONST_USER_NAME, "");
        String userPwd = sp.getString(Common.CONST_USER_PWD, "");

        if (host.equals(Common.EmptyString)) {
            return 0;
        }

        // todo check login
        // if(ok){
        //}
        //else {
        // return 1;
        // }

        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Common.init();

        if(!Common.isLoginTag) {

            // todo set cookies
            Log.i("TAG", "------------------------info first in");
            Intent intent = new Intent(this, LoginAndSetting.class);
            startActivity(intent );
            return ;
        }

        loadingDialog a = new loadingDialog(this);
        a.show();
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(1100);
                    a.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
        int PERMISSION_REQUEST = 1;

        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            String[] PERMISSIONS = {android.Manifest.permission.READ_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST);
        }


        Log.i("TAG11"," this is login msag ,cookie"+Common.loginCookie);
        fileSyncTask fst = new fileSyncTask(Common.SyncPaths, Common.fileTypes,
                Common.UserHostURL,Common.loginCookie );
        fst.start();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_sync, R.id.navigation_settings)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        //setContentView(R.layout.activity_main);

       // progressBar= (ProgressBar)findViewById(R.id.progressbar);//进度条

//        webView = (WebView) findViewById(R.id.webview);
//
//        WebSettings webSettings=webView.getSettings();
//        webSettings.setJavaScriptEnabled(true);//允许使用js
//        webSettings.setAllowFileAccess(true );
//        webSettings.setJavaScriptCanOpenWindowsAutomatically(true );
//        webSettings.setAllowFileAccessFromFileURLs(true );
//        webSettings.setSavePassword(true );
////        webView.loadUrl("file:///android_asset/test.html");//加载asset文件夹下html
//        webView.loadUrl("https://yuyuyu123.dynv6.net");//加载url
//
//
//        //使用webview显示html代码
////        webView.loadDataWithBaseURL(null,"<html><head><title> 欢迎您 </title></head>" +
////                "<body><h2>使用webview显示 html代码</h2></body></html>", "text/html" , "utf-8", null);
//
//        webView.addJavascriptInterface(this,"android");//添加js监听 这样html就能调用客户端
//        webView.setWebChromeClient(webChromeClient);
//        webView.setWebViewClient(webViewClient);
//        WebSettings settings = webView.getSettings();
//        settings.setDomStorageEnabled(true);
//
//        webView.setWebChromeClient(new WebChromeClient() {
//
//            // For Android >= 5.0
//            @Override
//            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
//                uploadMessageAboveL = filePathCallback;
//                Log.i("File",fileChooserParams.toString());
//
//                openImageChooserActivity();
//                return true;
//            }
//
//        });
//
//
//        /**
//         * LOAD_CACHE_ONLY: 不使用网络，只读取本地缓存数据
//         * LOAD_DEFAULT: （默认）根据cache-control决定是否从网络上取数据。
//         * LOAD_NO_CACHE: 不使用缓存，只从网络获取数据.
//         * LOAD_CACHE_ELSE_NETWORK，只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据。
//         */
//        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);//不使用缓存，只从网络获取数据.
//
//        //支持屏幕缩放
//        webSettings.setSupportZoom(true);
//        webSettings.setBuiltInZoomControls(true);

    }

    /**
     * 使用系统的下载服务
     *
     * @param url                下载地址
     * @param contentDisposition attachment;filename=测试专用.wps;filename*=utf-8''测试专用.wps
     * @param mimeType           application/octet-stream
     */
    private void downloadBySystem(String url, String contentDisposition, String mimeType) {
        Log.d("TAG", "downloadBySystem：url=" + url + "，contentDisposition="
                + contentDisposition + "，mimeType=" + mimeType);
        // 指定下载地址
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        // 允许媒体扫描，根据下载的文件类型被加入相册、音乐等媒体库
        request.allowScanningByMediaScanner();
        // 设置通知的显示类型，下载进行时和完成后显示通知
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        // 设置通知栏的标题，如果不设置，默认使用文件名
        // request.setTitle("This is title");
        // 设置通知栏的描述
        // request.setDescription("This is description");
        // 允许在计费流量下下载
        request.setAllowedOverMetered(false);
        // 允许该记录在下载管理界面可见
        request.setVisibleInDownloadsUi(false);
        // 允许漫游时下载
        request.setAllowedOverRoaming(true);
        // 允许下载的网路类型
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        // 设置下载文件保存的路径和文件名
        String fileName = URLUtil.guessFileName(url, contentDisposition, mimeType);
        Log.d("TAG", "fileName：" + fileName);

        //storage/emulated/0/Android/data/项目名/files
        // request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
        // request.setDestinationInExternalPublicDir(ConstantPath.getCommonPath(mContext), fileName);

        //Android/data/项目名/files/storage/emulated/0/Android/data/项目名/files
        // request.setDestinationInExternalFilesDir(this, ConstantPath.getCommonPath(mContext), fileName);

//        //另外可选一下方法，自定义下载路径
//        Uri mDestinationUri = Uri.withAppendedPath(Uri.fromFile(
//                new File(ConstantPath.getRootPath(ConstantPath.ANDROIDMOBILE))), fileName);
//        // Uri mDestinationUri = Uri.withAppendedPath(Uri.fromFile(
//        //                new File(ConstantPath.getCommonPath(mContext))), fileName);
//        request.setDestinationUri(mDestinationUri);

        final DownloadManager downloadManager = (DownloadManager)  getSystemService(DOWNLOAD_SERVICE);
        // 添加一个下载任务
        long downloadId = downloadManager.enqueue(request);
        Log.d("TAG", "downloadId：" + downloadId);
    }

    //WebViewClient主要帮助WebView处理各种通知、请求事件
    private WebViewClient webViewClient=new WebViewClient(){
        @Override
        public void onPageFinished(WebView view, String url) {//页面加载完成
            //progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {//页面开始加载
           // progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.i("ansen","load url:"+url);
            return super.shouldOverrideUrlLoading(view, url);
        }

    };


    //WebChromeClient主要辅助WebView处理Javascript的对话框、网站图标、网站title、加载进度等
    private WebChromeClient webChromeClient=new WebChromeClient(){
        //不支持js的alert弹窗，需要自己监听然后通过dialog弹窗
        @Override
        public boolean onJsAlert(WebView webView, String url, String message, JsResult result) {
            AlertDialog.Builder localBuilder = new AlertDialog.Builder(webView.getContext());
            localBuilder.setMessage(message).setPositiveButton("确定",null);
            localBuilder.setCancelable(false);
            localBuilder.create().show();

            //注意:
            //必须要这一句代码:result.confirm()表示:
            //处理结果为确定状态同时唤醒WebCore线程
            //否则不能继续点击按钮
            result.confirm();
            return true;
        }

        //获取网页标题
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            Log.i("ansen","网页标题:"+title);
        }

        //加载进度回调
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
           // progressBar.setProgress(newProgress);

        }

    };

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//
//    @Override
//    public boolean onSupportNavigateUp() {
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
//        return NavigationUI.navigateUp(navController, appBarConfiguration)
//                || super.onSupportNavigateUp();
//    }


}
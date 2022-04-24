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
import com.example.cloudreveapp.ui.sync.SyncFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.leon.lfilepickerlibrary.LFilePicker;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.String;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private WebView webView;

    private ValueCallback<Uri> uploadMessage;
    private ValueCallback<Uri[]> uploadMessageAboveL;
    private final static int FILE_CHOOSER_RESULT_CODE = 10000;


    public void btn_1(View v) {
        //创建对象
        new LFilePicker()
                .withActivity(MainActivity.this)
                .withRequestCode(Common.REQUESTCODE_FROM_ACTIVITY)
                .withStartPath("/storage/emulated/0/")
                .withChooseMode(false)
                .start();
    }

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

        if (requestCode == Common.REQUESTCODE_FROM_ACTIVITY) {

            if (resultCode == RESULT_OK) {
                //如果是文件夹选择模式，需要获取选择的文件夹路径
                String path = data.getStringExtra("path");
                Toast.makeText(getApplicationContext(), "选中的路径为" + path, Toast.LENGTH_SHORT).show();
                //Uri file = data.getData();
                Log.e("filePath", "select filePath is " + path);

                SharedPreferences sp = getSharedPreferences(Common.LocalStorageName, MODE_PRIVATE);
                Set<String> hs = sp.getStringSet(Common.SYNC_PATHS, new HashSet<String>());

                SharedPreferences.Editor ed = sp.edit();

                Set<String> hsNew = new HashSet<>();
                for(String x :hs) {
                    hsNew.add(x);
                }
                hsNew.add(path);
                ed.putStringSet(Common.SYNC_PATHS, hsNew);

                ed.apply();

            }
        }

//        if (requestCode==     FilePickerManager.REQUEST_CODE){
//            if (resultCode == Activity.RESULT_OK) {
//                ArrayList<String> list = (ArrayList<String>) FilePickerManager.obtainData();
//                // do your work
//                for (String file : list) {
//                    Log.e("filePath", "select filePath is " + file + "  *" + file.toString());
//                }
//            } else {
//
//            }
//        }
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


    @RequiresApi(api = Build.VERSION_CODES.S)
    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Common.init();
        int PERMISSION_REQUEST = 1;

        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            String[] PERMISSIONS = {android.Manifest.permission.READ_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST);
        }

        if (!Common.isLoginTag) {

            // todo set cookies
            Log.i("TAG", "------------------------info first in");
            Intent intent = new Intent(this, LoginAndSetting.class);
            startActivity(intent);
            return;
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


        String [] syncPaths=getSyncPaths();
        String msg ="";
        for(String x:syncPaths){
            msg+=x+"\n";
        }
        Toast.makeText(getApplicationContext(), "开始同步文件夹 "+msg,
                Toast.LENGTH_SHORT).show();
        fileSyncTask fst = new fileSyncTask(syncPaths, Common.NotSyncPaths, Common.fileTypes,
                Common.UserHostURL, Common.loginCookie);
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

        final DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        // 添加一个下载任务
        long downloadId = downloadManager.enqueue(request);
        Log.d("TAG", "downloadId：" + downloadId);
    }

    //WebViewClient主要帮助WebView处理各种通知、请求事件
    private WebViewClient webViewClient = new WebViewClient() {
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
            Log.i("ansen", "load url:" + url);
            return super.shouldOverrideUrlLoading(view, url);
        }

    };


    //WebChromeClient主要辅助WebView处理Javascript的对话框、网站图标、网站title、加载进度等
    private WebChromeClient webChromeClient = new WebChromeClient() {
        //不支持js的alert弹窗，需要自己监听然后通过dialog弹窗
        @Override
        public boolean onJsAlert(WebView webView, String url, String message, JsResult result) {
            AlertDialog.Builder localBuilder = new AlertDialog.Builder(webView.getContext());
            localBuilder.setMessage(message).setPositiveButton("确定", null);
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
            Log.i("ansen", "网页标题:" + title);
        }

        //加载进度回调
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            // progressBar.setProgress(newProgress);

        }

    };

    String[] getSyncPaths() {

        SharedPreferences sp = getSharedPreferences(Common.LocalStorageName, MODE_PRIVATE);
        Set<String> hs = sp.getStringSet(Common.SYNC_PATHS, new HashSet<String>());

        String[] res = new String[hs.size()];
        res = hs.toArray(res);

        return res;
    }

}


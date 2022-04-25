package com.example.cloudreveapp.task;

import android.content.Context;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.example.cloudreveapp.common.Common;
import com.example.cloudreveapp.common.systemUtil;
import com.example.cloudreveapp.common.utils;
import com.example.cloudreveapp.proto.SearchFile;
import com.example.cloudreveapp.rpc.file;
import com.example.cloudreveapp.ui.login.LoginAndSetting;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class fileInfo {
    String Path, MD5;

    fileInfo(String Path, String MD5) {
        this.Path = Path;
        this.MD5 = MD5;
    }
}

public class fileSyncTask extends  Thread {
    String[] paths;
    String[] notInPaths;
    String[] fileTypes;
    String url;
    String cookies;
    int batchSize = 200;


    // SavedCache key: file path, value: md5
    private static Map<String, String> SavedCache = new HashMap<String, String>();

    public fileSyncTask(String[] inPaths, String[] notInPaths, String[] inFileTypes, String url, String cookies) {
        this.paths = inPaths;
        this.fileTypes = inFileTypes;
        this.url = url;
        this.cookies = cookies;
        this.notInPaths = notInPaths;
        SavedCache.clear();
    }

    @Override
    public void run() {


        fatch();

    }

    void fatch() {
        List<fileInfo> fs = getFilePaths();
        int index = 1;

        Log.i("TAG", "fs size " + fs);
        List<fileInfo> batchList = new ArrayList<>();
        for (fileInfo x : fs) {
            Log.i("TAG11", " file " + x.Path);
        }

        for (fileInfo fi : fs) {
            if (index % batchSize == 0) {
                // check file exists in cloud
                checkFileAndUploadBatch(batchList);


                batchList.clear();
            }
            batchList.add(fi);
        }
        if (batchList.size() > 0) checkFileAndUploadBatch(batchList);

    }

    List<fileInfo> getFilePaths() {

        List<fileInfo> fileList = new ArrayList<fileInfo>();
        for (String path : paths) {
            List<fileInfo> fl = getAllFiles(path, fileTypes);

            for (fileInfo x : fl) {
                int flag = 0;
                for (String n : notInPaths) if (x.Path.contains(n)) flag++;
                if (flag == 0) fileList.add(x);
            }
        }

        return fileList;
    }

    /**
     * 获取指定目录内所有文件路径
     *
     * @param dirPath 需要查询的文件目录
     * @param types   查询类型，比如mp3什么的
     */
    public static List<fileInfo> getAllFiles(String dirPath, String[] types) {

        List<fileInfo> fileList = new ArrayList<fileInfo>();
        File f = new File(dirPath);
        if (!f.exists()) {//判断路径是否存在
            return fileList;
        }

        File[] files = f.listFiles();

        if (files == null) {
            return fileList;
        }

        for (File fs : files) {//遍历目录
            if (fs.isFile()) {

                for (String type : types) {
                    if (fs.getName().endsWith(type)) {
                        String filePath = fs.getAbsolutePath();//获取文件路径
                        String md5 = utils.getFileMD5(fs);
                        fileInfo fi = new fileInfo(filePath, md5);
                        fileList.add(fi);
                    }
                }
            } else if (fs.isDirectory()) {//子目录
                List<fileInfo> fl = getAllFiles(fs.getAbsolutePath(), types);
                fileList.addAll(fl);
            }

        }
        return fileList;
    }

    // checkFileAndUpload check and update
    List<fileInfo> checkFileAndUploadBatch(List<fileInfo> fi) {

        List<fileInfo> fileNotInList = new ArrayList<>();
        if (fi == null) {
            Log.w("File", "checkFileAndUpload fi is null ");
            return fileNotInList;
        }
        JSONArray ja = new JSONArray();
        for (fileInfo f : fi) {
            ja.put(f.MD5);
        }

        try {

            JSONObject body = new JSONObject();
            body.put("type", "md5");
            body.put("md5", ja);
            List<SearchFile> res = file.SearchFileByMD5s(Common.UserHostURL, body);
            HashMap<String, SearchFile> mp = new HashMap<>();

            for (SearchFile searchFile : res) {
                //Log.i("file", "search file res " + xx.toString());
                mp.put(searchFile.MD5, searchFile);
            }
            for (fileInfo f : fi) {
                if (mp.get(f.MD5) == null) {
                    fileNotInList.add(f);
                }
            }

            needUpload(fileNotInList);
            if (fileNotInList.size() >  0) {
                Message msg = new Message();
                msg.obj = "总共完成同步文件 " + fileNotInList.size()+" 个";
                // 把消息发送到主线程，在主线程里现实Toast
                Common.handler.sendMessage(msg);
            }
        } catch (Exception e) {
            Log.e("file", e.toString());

        }

        return fileNotInList;
    }

    void needUpload(List<fileInfo> uploadList) {
        String brand = systemUtil.getDeviceBrand();
        String model = systemUtil.getSystemModel();
        String deviceName = brand + " " + model;
        Log.i("fileUpload", "DeviceName" + deviceName);


        for (fileInfo f : uploadList) {

            Log.i("fileUpload", "make file " + f.Path);
            try {
                // todo  if f.session is exist ,must delete it on cloud first


                // upload
                if (!file.UploadFile(Common.UserHostURL, f.Path,deviceName)) {
                    Log.w("fileUpload", "UploadFile result is false ");
                    continue;
                }

                int num = (int) (Math.random() * 100);
                if (num%3==0) {
                    Message msg = new Message();
                    msg.obj = "已同步文件： " + f.Path;
                    // 把消息发送到主线程，在主线程里现实Toast
                    Common.handler.sendMessage(msg);
                }
            } catch (Exception e) {
                Log.e("fileUpload", "Exception" + e.toString());
            }
        }

    }

}

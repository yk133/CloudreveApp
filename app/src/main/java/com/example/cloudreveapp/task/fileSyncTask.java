package com.example.cloudreveapp.task;

import android.util.Log;

import com.example.cloudreveapp.common.Common;
import com.example.cloudreveapp.common.utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class FileInfo {
    String Path, MD5;

    FileInfo(String Path, String MD5) {
        this.Path = Path;
        this.MD5 = MD5;
    }
}

public class fileSyncTask extends  Thread {
    String[] paths;
    String[] notInList;
    String[] fileTypes;
    String url;
    String cookies;
    int batchSize = 200;


    // SavedCache key: file path, value: md5
    private static Map<String, String> SavedCache = new HashMap<String, String>();

    public fileSyncTask(String[] inPaths, String[] inFileTypes, String url, String cookies) {
        this.paths = inPaths;
        this.fileTypes = inFileTypes;
        this.url = url;
        this.cookies = cookies;
        this.notInList = new String[]{
          "/Pictures/weibo/"
        };
        SavedCache.clear();

    }

    @Override
    public void run() {

        fatch();

    }

    void fatch() {
        List<FileInfo> fs = getFilePaths();
        int index = 1;

        Log.i("TAG", "fs size "+fs );
        List<FileInfo> batchList = new ArrayList<>();
        for(FileInfo x : fs){
            Log.i("TAG11"," file "+x.Path);
        }

        for (FileInfo fi : fs) {
            if (index % batchSize == 0) {
                // check file is exists
                checkFileAndUpload(batchList);
            } else {
                batchList.clear();
            }
            batchList.add(fi);
        }
    }

    List<FileInfo> getFilePaths() {

        List<FileInfo> fileList = new ArrayList<FileInfo>();
        for (String path : paths) {
            List<FileInfo> fl = getAllFiles(path, fileTypes);
            if(fl!=null) {
                for (FileInfo x : fl) {
                    int flag = 0;
                    for (String n : notInList) if (x.Path.contains(n)) flag++;
                    if (flag == 0) fileList.add(x);
                }
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
    public static List<FileInfo> getAllFiles(String dirPath, String[] types) {

        List<FileInfo> fileList = new ArrayList<FileInfo>();
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
                        FileInfo fi = new FileInfo(filePath, md5);
                        fileList.add(fi);
                    }
                }
            } else if (fs.isDirectory()) {//子目录
                List<FileInfo> fl = getAllFiles(fs.getAbsolutePath(), types);
                fileList.addAll(fl);
            }

        }
        return fileList;
    }

    void checkFileAndUpload(List<FileInfo>  fi) {

    }

}

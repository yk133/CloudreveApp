package com.example.cloudreveapp.rpc;

import android.util.Log;

import com.example.cloudreveapp.common.Common;
import com.example.cloudreveapp.common.http;
import com.example.cloudreveapp.proto.SearchFile;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class directory {

    static String CreateDirURL = "/api/v3/directory";

    // CreateAbsDirectoryIfNotExist if dir exist or create success will return ture
    //curl 'http://127.0.0.1:5212/api/v3/directory' \
    //  -X 'PUT' \
    //  -H 'Content-Type: application/json' \
    //  -H 'Cookie: cloudreve-session=xxxxxxxxxx' \
    //  -d '{"path":"/ddd"}'
    static public boolean CreateAbsDirectoryIfNotExist(String dirName, String host) throws Exception {

        okhttp3.Headers.Builder headersbuilder = new okhttp3.Headers.Builder();
        headersbuilder.add("content-type", "application/json");
        headersbuilder.add("cookie", Common.loginCookie);
        try {
            MediaType JSON = MediaType.parse("application/json;charset=utf-8");
            JSONObject body = new JSONObject();
            body.put("path", dirName);
            RequestBody requestBody = RequestBody.create(JSON, String.valueOf(body));
            okhttp3.Response response = http.DoPut(host + CreateDirURL,
                    headersbuilder.build(), requestBody);
            String result = response.body().string();
            JSONObject jsonAll = new JSONObject(result);
            int code = (Integer) jsonAll.get("code");
            if (code != 0) {
                String msg = (String) jsonAll.get("msg");
                String info = CreateDirURL + "fail : code " + code + " msg: " + msg;
                Log.e("file", info);
                if (code == 40004 && msg.equals("同名目录已存在")) {
                    return true;
                }
                throw new Exception(info);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        return true;
    }

    // CreateAbsDirectoryIfNotExist if dir exist or create success will return ture
    //curl 'http://127.0.0.1:5212/api/v3/directory%2FAndroid%20Android%20SDK%20built%20for%20x86' \
    //  -H 'Cookie: cloudreve-session=xxxxxxxxxx' \

    static public boolean GetDirInfo(String dirName, String host) throws Exception {
        String encodeDirName = URLEncoder.encode(dirName, "UTF-8");
        Log.i("Dir","GetDirInfo dirName "+ encodeDirName);

        okhttp3.Headers.Builder headersbuilder = new okhttp3.Headers.Builder();
        headersbuilder.add("content-type", "application/json");
        headersbuilder.add("cookie", Common.loginCookie);
        try {
            MediaType JSON = MediaType.parse("application/json;charset=utf-8");
            JSONObject body = new JSONObject();
            body.put("path", dirName);
            RequestBody requestBody = RequestBody.create(JSON, String.valueOf(body));
            okhttp3.Response response = http.DoPut(host + CreateDirURL,
                    headersbuilder.build(), requestBody);
            String result = response.body().string();
            JSONObject jsonAll = new JSONObject(result);
            int code = (Integer) jsonAll.get("code");
            if (code != 0) {
                String msg = (String) jsonAll.get("msg");
                String info = CreateDirURL + "fail : code " + code + " msg: " + msg;
                Log.e("file", info);
                if (code == 40004 && msg.equals("同名目录已存在")) {
                    return true;
                }
                throw new Exception(info);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        return true;
    }

}


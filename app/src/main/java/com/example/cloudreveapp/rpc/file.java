package com.example.cloudreveapp.rpc;

import android.content.Context;
import android.util.Log;

import com.example.cloudreveapp.common.Common;
import com.example.cloudreveapp.common.http;
import com.example.cloudreveapp.proto.SearchFile;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class file {

    static final String SearchFileByMD5sURL = "/api/v3/file/md5_search";

    static public List<SearchFile> SearchFileByMD5s(String host, String cookie, JSONObject body) throws Exception {

        List<SearchFile> res = new ArrayList<SearchFile>();
        okhttp3.Headers.Builder headersbuilder = new okhttp3.Headers.Builder();
        headersbuilder.add("content-type", "application/json");
        headersbuilder.add("cookie", cookie);
        try {
            MediaType JSON = MediaType.parse("application/json;charset=utf-8");

            RequestBody requestBody = RequestBody.create(JSON, String.valueOf(body));
            okhttp3.Response response = http.DoPost(host + SearchFileByMD5sURL,
                    headersbuilder.build(), requestBody);
            String result = response.body().string();
            JSONObject jsonAll = new JSONObject(result);
            int code = (Integer) jsonAll.get("code");
            if (code != 0) {
                String msg = (String) jsonAll.get("msg");
                String info = SearchFileByMD5sURL +
                        "fail : code " + code + " msg: " + msg;
                Log.e("file", info);
                throw new Exception(info);
            }
            if (!jsonAll.isNull("data")) {
                JSONArray data = jsonAll.getJSONArray("data");
                Log.e("file", "data size " + data.length());
                for (int i = 0; i < data.length(); i++) {
                    JSONObject x = data.getJSONObject(i);
                    String fileName = (String) x.get("name");
                    String md5 = (String) x.get("md5");
                    int size = (int) x.get("size");
                    SearchFile s = new SearchFile(fileName, md5, size);
                    res.add(s);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        return res;
    }
}

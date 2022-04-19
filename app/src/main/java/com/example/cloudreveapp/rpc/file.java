package com.example.cloudreveapp.rpc;

import android.os.Environment;
import android.util.Log;

import com.example.cloudreveapp.common.Common;
import com.example.cloudreveapp.common.http;
import com.example.cloudreveapp.proto.SearchFile;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class file {

    static final String SearchFileByMD5sURL = "/api/v3/file/md5_search";
    static final String UploadURL = "/api/v3/file/upload";
    static final String EnvPathSDCARD = Environment.getExternalStorageDirectory().getPath();;

    static public List<SearchFile> SearchFileByMD5s(String host, JSONObject body) throws Exception {

        List<SearchFile> res = new ArrayList<SearchFile>();
        okhttp3.Headers.Builder headersbuilder = new okhttp3.Headers.Builder();
        headersbuilder.add("content-type", "application/json");
        headersbuilder.add("cookie", Common.loginCookie);
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

    //curl 'http://127.0.0.1:5212/api/v3/file/upload' \
    //  -X 'PUT' \
    //  -H 'Content-Type: application/json' \
    //  -H 'Cookie: cloudreve-session=xxxxxxxxxxx' \
    //  -d '{"path":"/rrr","size":22129,"name":"xxxxxx.png","policy_id":"XMCg","last_modified":1613999566839}'
    static public boolean UploadFile(String host, String filePath,String rootName) throws Exception {
        File f = new File(filePath);
        if (!f.exists()) {
            Log.w("UploadFile","f is not exist");
            return false;
        }
        String fatherPath = f.getParent();
        String path = fatherPath.replace(EnvPathSDCARD, "");
        path = "/"+rootName+path;
        Log.i("UploadFile","path "+path);

        if(!directory.CreateAbsDirectoryIfNotExist(path,host)){
            return false ;
        }

        String getPolicy = directory.getPolicy();

        okhttp3.Headers.Builder headersbuilder = new okhttp3.Headers.Builder();
        headersbuilder.add("content-type", "application/json");
        headersbuilder.add("cookie", Common.loginCookie);
        try {
            MediaType JSON = MediaType.parse("application/json;charset=utf-8");
            JSONObject body = new JSONObject();

            body.put("path", path);
            body.put("size", f.length());
            body.put("name", f.getName());
            body.put("policy_id", "");
            body.put("last_modified", f.lastModified());
            RequestBody requestBody = RequestBody.create(JSON, String.valueOf(body));
            okhttp3.Response response = http.DoPut(host + UploadURL,
                    headersbuilder.build(), requestBody);
            String result = response.body().string();
            JSONObject jsonAll = new JSONObject(result);
            int code = (Integer) jsonAll.get("code");
            if (code != 0) {
                String msg = (String) jsonAll.get("msg");
                String info = UploadURL +
                        "fail : code " + code + " msg: " + msg;
                Log.e("file", info);
                throw new Exception(info);
            }
            if (!jsonAll.isNull("data")) {
                JSONObject data = jsonAll.getJSONObject("data");
                if (!data.isNull("sessionID") && !data.isNull("chunkSize")) {
                    String sessionID = data.getString("sessionID");
                    int chunkSize = data.getInt("chunkSize");
                    UploadFileSession(host, f, sessionID, chunkSize);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        return true;
    }


    //curl 'http://127.0.0.1:5212/api/v3/file/upload/xxxxx/0' \
    //  -H 'Content-Type: application/octet-stream' \
    //  -H 'Cookie: cloudreve-session=xxxxxxxxxxx' \
    //  -d xxxxxx
    static public boolean UploadFileSession(String host, File f, String sessionID, int chunkSize) throws Exception {

        okhttp3.Headers.Builder headersbuilder = new okhttp3.Headers.Builder();
        headersbuilder.add("content-type", "application/octet-stream");
        headersbuilder.add("cookie", Common.loginCookie);
        try {
            MediaType FILE = MediaType.parse("application/octet-stream");
            RequestBody requestBody = RequestBody.create(FILE, f);
            okhttp3.Response response = http.DoPost(host + UploadURL + "/" + sessionID + "/" + chunkSize,
                    headersbuilder.build(), requestBody);
            String result = response.body().string();
            JSONObject jsonAll = new JSONObject(result);
            int code = (Integer) jsonAll.get("code");
            if (code != 0) {
                String msg = (String) jsonAll.get("msg");
                String info = UploadURL +
                        "fail : code " + code + " msg: " + msg;
                Log.e("file", info);
                throw new Exception(info);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        return true;
    }

}

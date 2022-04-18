package com.example.cloudreveapp.rpc;

import android.util.Log;

import com.example.cloudreveapp.common.Common;
import com.example.cloudreveapp.common.http;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

public class login {

    static final String LoginURL = "/api/v3/user/session";

    // POST / GET 都可
    //curl '$HOST/api/v3/user/session' \
    //  -H 'content-type: application/json' \
    //  -d '{"userName": "xxxxxxxxxx","Password":"xxxxxx","captchaCode":""}'
    static public String[] checkLoginOK(String host,String userName,String userPwd ) throws Exception {
        String url = host + LoginURL;

        okhttp3.Headers.Builder headersbuilder = new okhttp3.Headers.Builder();
        headersbuilder.add("content-type", "application/json");
        String cookie = "";
        JSONObject body = new JSONObject();
        try {
            MediaType JSON = MediaType.parse("application/json;charset=utf-8");
            body.put("userName", userName);
            body.put("Password", userPwd);
            body.put("captchaCode", userName);
            RequestBody requestBody = RequestBody.create(JSON, String.valueOf(body));
            Response response = http.DoPost(url, headersbuilder.build(), requestBody);

            String result = response.body().string();

            JSONObject jsonAll = new JSONObject(result);

            int code = (Integer) jsonAll.get("code");
            if (code != 0) {
                String msg = (String) jsonAll.get("msg");
                Log.e("Login", "login fail : code " + code + " msg: " + msg);
                return new String[]{msg};
            }

            Headers rh = response.headers();
            cookie = rh.get("set-cookie");
            try {
                JSONObject data = jsonAll.getJSONObject("data");
                JSONObject policy = data.getJSONObject("policy");
                String upUrl = (String) policy.get("upUrl");
                if (!upUrl.isEmpty()) {
                    Common.upLoadURL = upUrl;
                }
            }catch (JSONException je){
                je.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        return new String[]{"success", cookie};
    }
}

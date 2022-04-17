package com.example.cloudreveapp.common;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

import androidx.annotation.RequiresApi;

import com.example.cloudreveapp.ui.login.LoginAndSetting;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Common {

    //const
    static public final String EmptyString = "";
    static public final String CONST_HOST = "HOST";
    static public final String CONST_USER_NAME = "USER_NAME";
    static public final String CONST_USER_PWD = "USER_PWD";

    // var
    static public boolean isLoginTag = false;
    static public String loginCookie = "";
    static public String upLoadURL = "/api/v3/file/upload";
    static public String UserHostURL = "";
    static public String UserName = "";
    static public String UserPwd = "";

    static public String[] SyncPaths;
    static public String[] fileTypes;

    @RequiresApi(api = Build.VERSION_CODES.S)
    public static void init() {

        String sdcardPath = Environment.getExternalStorageDirectory().getPath();
        String paths[] = new String[]{
                sdcardPath + File.separator + "tencent/MicroMsg/WeiXin/",
                sdcardPath + File.separator + "Download/",
                sdcardPath + File.separator + "DCIM/",
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath()
        };

        SyncPaths = paths;
        //Types
        String ft[] = new String[]{
                // pictures
                ".jpg",
                ".jpeg",
                ".png",
                ".bmp",
                ".gif",
                // movies
                ".mov",
                ".rmvb",
                ".mp4",
                ".avi",
                // audio
                ".mp3",
                ".ogg",
                ".wav",
                // text
                ".pdf",
                ".txt",
                ".xls",
                ".ppt",
                ".pptx",
                ".doc",
                ".docx",
                ".csv",
                ".md"};

        fileTypes = ft;
    }

}

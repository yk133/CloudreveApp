package com.example.cloudreveapp.common;

import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.cloudreveapp.MainActivity;
import com.example.cloudreveapp.proto.Policy;

import java.io.File;

public class Common {

    //const
    static public final String EmptyString = "";
    static public final String CONST_HOST = "HOST";
    static public final String CONST_USER_NAME = "USER_NAME";
    static public final String CONST_USER_PWD = "USER_PWD";
    static public final int REQUESTCODE_FROM_ACTIVITY = 1001;

    public static String LocalStorageName = "SP";
    public static String SYNC_PATHS = "SYNC_PATHS";
    public static String SYNC_FILE_TYPE = "SYNC_FILE_TYPE";


    // var
    public static  Handler handler;
    static public boolean isLoginTag = false;
    static public String loginCookie = "";
    static public String upLoadURL = "/api/v3/file/upload";
    static public String UserHostURL = "";
    static public String UserName = "";
    static public String UserPwd = "";

    // do not used
    static public String[] DefaultSyncPaths;
    static public String[] DefaultNotSyncPaths;
    static public String[] defaultFileTypes;

    static public Policy DirPolicy = null;

    @RequiresApi(api = Build.VERSION_CODES.S)
    public static void init() {

        String sdcardPath = Environment.getExternalStorageDirectory().getPath();
        DefaultSyncPaths = new String[]{
                sdcardPath + File.separator + "tencent/MicroMsg/WeiXin/",
                sdcardPath + File.separator + "Download/",
                sdcardPath + File.separator + "DCIM/",
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath()
        };

        DefaultNotSyncPaths = new String[]{
        };

        //Types
        defaultFileTypes = new String[]{
                // pictures
                ".jpg",
                ".jpeg",
                ".png",
                ".bmp",
                ".gif",
                ".mp", ".tif", ".pcx", ".tga", ".exif", ".fpx", ".svg", ".psd", ".cdr", ".pcd", ".dxf", ".ufo", ".eps", ".ai", ".raw", ".WMF", ".webp",  ".apng",
                // movies
                ".mov",
                ".rmvb",
                ".mp4",
                ".avi",".flv",".rm",".m3u8",
                // audio
                ".mp3",
                ".ogg",
                ".wav", ".awb",
                // text
                ".pdf",
                ".txt",
                ".xls",
                ".ppt",
                ".pptx",
                ".doc",
                ".docx",
                ".csv", ".bat", ".c", ".cpp", ".go", ".java", ".cmd",
                ".md"};

    }

}
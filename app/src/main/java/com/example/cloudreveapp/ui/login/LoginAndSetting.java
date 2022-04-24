package com.example.cloudreveapp.ui.login;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;

import com.example.cloudreveapp.MainActivity;
import com.example.cloudreveapp.R;
import com.example.cloudreveapp.common.Common;
import com.example.cloudreveapp.rpc.login;

import android.text.InputType;
import android.widget.*;

import androidx.core.app.ActivityCompat;


public class LoginAndSetting extends Activity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {


    //布局内的控件
    private EditText et_host;
    private EditText et_name;
    private EditText et_password;
    private Button mLoginBtn;
    private ImageView iv_see_password;

    private Dialog mLoadingDialog; //显示正在加载的对话框



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_and_setting);
        initViews();
        setupEvents();
        initData();

    }

    private void initData() {


        //判断用户第一次登陆
        if (firstLogin()) {
//            checkBox_password.setChecked(false);//取消记住密码的复选框
//            checkBox_login.setChecked(false);//取消自动登录的复选框
        }

    }

    /**
     * 把本地保存的数据设置数据到输入框中
     */
    public void setTextNameAndPassword() {
        et_name.setText("" + getLocalName());
        et_password.setText("" + getLocalPassword());
    }

    /**
     * 设置数据到输入框中
     */
    public void setTextName() {
        et_name.setText("" + getLocalName());
    }


    /**
     * 获得保存在本地的用户名
     */
    public String getLocalName() {
        Context ctx = LoginAndSetting.this;
        SharedPreferences sp = ctx.getSharedPreferences(Common.LocalStorageName, MODE_PRIVATE);
        String userName = sp.getString(Common.CONST_USER_NAME, "");

        return userName;
    }


    /**
     * 获得保存在本地的密码
     */
    public String getLocalPassword() {
        Context ctx = LoginAndSetting.this;
        SharedPreferences sp = ctx.getSharedPreferences(Common.LocalStorageName, MODE_PRIVATE);

        String userPwd = sp.getString(Common.CONST_USER_PWD, "");
        return userPwd;

    }


    private void initViews() {
        mLoginBtn = (Button) findViewById(R.id.btn_login);
        et_host = (EditText) findViewById(R.id.et_host);
        et_name = (EditText) findViewById(R.id.et_account);
        et_password = (EditText) findViewById(R.id.et_password);
        iv_see_password = (ImageView) findViewById(R.id.iv_see_password);
    }

    private void setupEvents() {
        mLoginBtn.setOnClickListener(this);
        iv_see_password.setOnClickListener(this);

    }

    /**
     * 第一次自动登陆
     */
    private boolean firstLogin() {
        getPermission();

        Context ctx = LoginAndSetting.this;
        SharedPreferences sp = ctx.getSharedPreferences(Common.LocalStorageName, MODE_PRIVATE);

        String userHost = sp.getString(Common.CONST_HOST, "");
        String userName = sp.getString(Common.CONST_USER_NAME, "");
        String userPwd = sp.getString(Common.CONST_USER_PWD, "");
        if (userHost.length() + userName.length() + userPwd.length() == 0) {
            return true;
        }
        Log.i("firstLogin", "host " + userHost+", userName "+userName);

        this.et_host.setText(userHost);
        this.et_name.setText(userName);
        this.et_password.setText(userPwd);

        showLoading();//显示加载框
        Thread loginRunnable = new Thread() {

            @Override
            public void run() {
                super.run();
                setLoginBtnClickable(false);//点击登录后，设置登录按钮不可点击状态

                try {
                    //判断账号和密码
                    String msg[] = login.checkLoginOK(userHost, userName, userPwd);
                    if (msg == null) {
                        showToast("自动登录失败，请手动登录");
                        setLoginBtnClickable(true);

                    } else if (msg[0].equals("success")) {
                        showToast("自动登录成功");
                        saveUserInfo();//记录下当前用户信息
                        Common.loginCookie=msg[1];
                        setLoginBtnClickable(true);  //这里解放登录按钮，设置为可以点击
                        hideLoading();//隐藏加载框
                        Common.isLoginTag = true;
                        Intent intent = new Intent(ctx, MainActivity.class);
                        startActivity(intent);
                        finish();
                        return;
                    } else {
                        showToast(msg[0]);

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    showToast("登录失败，请检查网络后再试");
                }
                setLoginBtnClickable(true);
            }
        };
        loginRunnable.start();
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                login( ); //登陆
                break;
            case R.id.iv_see_password:
                setPasswordVisibility();    //改变图片并设置输入框的文本可见或不可见
                break;

        }
    }

    void getPermission(){
        int PERMISSION_REQUEST = 1;

        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            String[] PERMISSIONS = {android.Manifest.permission.READ_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST);
        }
    }


    /**
     * login
     */
    private void login( ) {
        getPermission();

        Context ctx = LoginAndSetting.this;
        //先做一些基本的判断，比如输入的用户命为空，密码为空，网络不可用多大情况，都不需要去链接服务器了，而是直接返回提示错误
        if (getHost().isEmpty()) {
            showToast("你输入的服务器地址为空！");
            return;
        }

        if (getUserName().isEmpty()) {
            showToast("你输入的账号为空！");
            return;
        }

        if (getPassword().isEmpty()) {
            showToast("你输入的密码为空！");
            return;
        }
        //登录一般都是请求服务器来判断密码是否正确，要请求网络，要子线程
        showLoading();//显示加载框
        Thread loginRunnable = new Thread() {

            @Override
            public void run() {
                super.run();
                setLoginBtnClickable(false);//点击登录后，设置登录按钮不可点击状态

                try {
                    //判断账号和密码
                    String msg[] = login.checkLoginOK(getHost(), getUserName(), getPassword());
                    if (msg == null) {
                        showToast("内部错误，请重新尝试！");
                    } else if (msg[0].equals("success")) {
                        showToast("登录成功");
                        saveUserInfo();//记录下当前用户信息
                        Common.loginCookie = msg[1];
                        setLoginBtnClickable(true);  //这里解放登录按钮，设置为可以点击
                        hideLoading();//隐藏加载框
                        Common.isLoginTag = true;

                        Intent intent = new Intent(ctx, MainActivity.class);
                        startActivity(intent);
                        finish();
                        return;
                    } else {
                        showToast(msg[0]);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    showToast("登录失败，请检查网络后再试");
                }
                setLoginBtnClickable(true);
            }
        };
        loginRunnable.start();

    }



    /**
     * 保存用户账号
     */
    public void saveUserInfo() {
        Context ctx = LoginAndSetting.this;
        SharedPreferences sp = ctx.getSharedPreferences(Common.LocalStorageName, MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();

        ed.putString(Common.CONST_HOST, getHost());
        ed.putString(Common.CONST_USER_NAME, getUserName());
        ed.putString(Common.CONST_USER_PWD, getPassword());
        ed.apply();

        Common.UserHostURL = getHost();
        Common.UserName = getUserName();
        Common.UserPwd = getPassword();

    }

    /**
     * 设置密码可见和不可见的相互转换
     */
    private void setPasswordVisibility() {
        if (iv_see_password.isSelected()) {
            iv_see_password.setSelected(false);
            //密码不可见
            et_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        } else {
            iv_see_password.setSelected(true);
            //密码可见
            et_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }

    }

    /**
     * 获取Host
     */
    public String getHost() {
        return et_host.getText().toString().trim();//去掉空格
    }

    /**
     * 获取账号
     */
    public String getUserName() {
        return et_name.getText().toString().trim();//去掉空格
    }

    /**
     * 获取密码
     */
    public String getPassword() {
        return et_password.getText().toString().trim();//去掉空格
    }



    /**
     * 是否可以点击登录按钮
     *
     * @param clickable
     */
    public void setLoginBtnClickable(boolean clickable) {
        mLoginBtn.setClickable(clickable);
    }


    /**
     * 显示加载的进度款
     */
    public void showLoading() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new Dialog(this);
        }
        mLoadingDialog.show();
    }


    /**
     * 隐藏加载的进度框
     */
    public void hideLoading() {
        if (mLoadingDialog != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLoadingDialog.hide();
                }
            });

        }
    }


    /**
     * 监听回退键
     */
    @Override
    public void onBackPressed() {
        if (mLoadingDialog != null) {
            if (mLoadingDialog.isShowing()) {
                mLoadingDialog.cancel();
            } else {
                finish();
            }
        } else {
            finish();
        }

    }

    /**
     * 页面销毁前回调的方法
     */
    protected void onDestroy() {
        if (mLoadingDialog != null) {
            mLoadingDialog.cancel();
            mLoadingDialog = null;
        }
        super.onDestroy();
    }


    public void showToast(String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LoginAndSetting.this, msg, Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

    }
}

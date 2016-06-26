package com.wuwo.im.activity;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.wuwo.im.config.WowuApp;
import com.wuwo.im.util.MyToast;
import com.wuwo.im.util.UpdateManager;
import com.wuwo.im.util.UtilsTool;

import org.json.JSONException;
import org.json.JSONObject;

import im.wuwo.com.wuwo.R;

public class LoginActivity extends BaseLoadActivity {

    //    Context mContext = LoginActivity.this;
    private AutoCompleteTextView wohu_phoneNum;
    private EditText wohu_password;
    private Button imap_login_userlogin;
    private CheckBox login_save_pwd;
//    private CheckBox login_auto;
    private ProgressDialog mdialog;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private UpdateManager manager;

//    private LoadserverdataService loadDataService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO 自动生成的方法存根
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 去掉信息栏
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制为横屏

//      防止键盘自动弹出
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        setContentView(R.layout.activity_login_material_design);
        settings = this.getSharedPreferences(WowuApp.PREFERENCE_KEY, MODE_PRIVATE);
        editor = settings.edit();


//        loadDataService=new LoadserverdataService(this);
        initView();
//      validateDevice();

//        // 检查文件更新
//        manager = new UpdateManager(mContext);
//        manager.checkUpdateMe();
    }

    @SuppressLint("NewApi")
    private void initView() {
        // TODO 自动生成的方法存根
        wohu_phoneNum = (AutoCompleteTextView) this.findViewById(R.id.imap_login_uername);
        wohu_password = (EditText) this.findViewById(R.id.imap_login_password);
        imap_login_userlogin = (Button) this.findViewById(R.id.imap_login_userlogin);
        login_save_pwd = (CheckBox) this.findViewById(R.id.login_save_pwd);
//        login_auto = (CheckBox) this.findViewById(R.id.login_auto);
//         TextView login_by_gesture= (TextView) this.findViewById(R.id.login_by_gesture);
        TextView user_register = (TextView) this.findViewById(R.id.user_register);

        imap_login_userlogin.setOnClickListener(this);
        login_save_pwd.setOnClickListener(this);
//        login_auto.setOnClickListener(this);
        user_register.setOnClickListener(this);
        findViewById(R.id.return_back).setOnClickListener(this);


//         如果是设置保存了密码，则自动注入用户名及密码
        Boolean login_save_pwd_check = settings.getBoolean("login_save_pwd_check", true);
        login_save_pwd.setChecked(login_save_pwd_check);
//        Boolean login_auto_check = settings.getBoolean("login_auto_check", false);
//        login_auto.setChecked(login_auto_check);

        String m_username = settings.getString("PhoneNumber", "");
        String m_password = settings.getString("Password", "");
        wohu_phoneNum.setText(m_username);
        if (login_save_pwd_check) {
            {
                WowuApp.token= settings.getString("token", "");
                WowuApp.PhoneNumber = settings.getString("PhoneNumber", "");
                WowuApp.Password = settings.getString("Password", "");
                WowuApp.Name = settings.getString("Name", "");

                if(!WowuApp.PhoneNumber.equals("") &&!WowuApp.Password.equals("") ){
                    startLogin();
                }

            }
            wohu_password.setText(m_password);
        }
    }

    @Override
    public void onClick(View v) {
        // TODO 自动生成的方法存根
        switch (v.getId()) {
            case R.id.imap_login_userlogin:
                WowuApp.PhoneNumber= wohu_phoneNum.getText().toString();
                WowuApp.Password = wohu_password.getText().toString();
                startLogin();
                break;
            case R.id.login_by_gesture:
                Intent intent2 = new Intent();
                intent2.setClass(mContext, RegisterStepOneActivity.class);
                startActivity(intent2);
//                finish();
                break;
            case R.id.user_register:
                Intent intent3 = new Intent();
                intent3.setClass(mContext, RegisterStepOneActivity.class);
                startActivity(intent3);
//            finish();
                break;
            case R.id.return_back:
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            default:
                break;
        }

    }

    /**
     *
     */
    private void startLogin() {
        Message msg = new Message();
        msg.what = Loading;
        mHandler.sendMessage(msg);
        try {
            JSONObject json = new JSONObject();
//                    json.put("PhoneNumber", "15000659340");
//                    json.put("Password", "123456");
            json.put("PhoneNumber", WowuApp.PhoneNumber);
            json.put("Password", WowuApp.Password);
            loadDataService.loadPostJsonRequestData(WowuApp.JSON, WowuApp.LoginURL, json.toString(), R.id.imap_login_userlogin);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //此处是连接网络的返回值
    @Override
    public void loadServerData(String response, int flag) {
//        MyToast.show(mContext, "返回的结果为：：：：" + response);
        Log.i("获取登陆返回值为：：：", response.toString());
        switch (flag) {
            case R.id.imap_login_userlogin:
                try {
                    JSONObject json = new JSONObject(response);
                    WowuApp.token = json.optString("token");

//                    记录下登录者的信息
                    editor.putString("token",WowuApp.token);
                    editor.putString("PhoneNumber",WowuApp.PhoneNumber);
                    editor.putString("Password",WowuApp.Password);
                    editor.putInt("Gender",WowuApp.Gender);
                    editor.putString("Name",WowuApp.Name);
                    editor.putBoolean("login_save_pwd_check",true); //登录成功后下次点开后可自动登录
                    editor.commit();

//                  跳转到主界面
                    Intent temp = new Intent(this, MainActivity.class);
                    startActivity(temp);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//                finish();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void loadDataFailed(String response,int flag) {
        MyToast.show(mContext,response.toString());
        if(pg!=null)pg.dismiss();
    }


    private ProgressDialog pg;
    private final int Loading = 1;
    private final int END = 2;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Loading:
                    pg = UtilsTool.initProgressDialog(mContext, "正在登录...");
                    pg.show();
                    break;
                case END:
                    if(pg!=null)pg.dismiss();
                    break;
            }
            super.handleMessage(msg);
        }
    };


}

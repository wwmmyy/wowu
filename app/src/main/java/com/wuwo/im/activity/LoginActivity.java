package com.wuwo.im.activity;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;
import com.wuwo.im.config.WowuApp;
import com.wuwo.im.util.MyToast;
import com.wuwo.im.util.UpdateManager;
import com.wuwo.im.util.UtilsTool;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import im.wuwo.com.wuwo.R;

public class LoginActivity extends Activity implements OnClickListener {

    Context mContext = LoginActivity.this;
    private AutoCompleteTextView imap_login_uername;
    private EditText imap_login_password;
    private Button imap_login_userlogin;
    private CheckBox login_save_pwd;
    private CheckBox login_auto;
    private ProgressDialog mdialog;
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    UpdateManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO 自动生成的方法存根
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 去掉信息栏
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制为横屏

//      防止键盘自动弹出
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        setContentView(R.layout.activity_login_material_design);
        settings = this.getSharedPreferences(WowuApp.PREFERENCE_KEY, MODE_PRIVATE);
        editor = settings.edit();

//        Bundle extras = getIntent().getExtras(); 
//        if(extras!=null){
//            username_fromserver = extras.getString("username"); 
//        }


        initView();
//      validateDevice();


        startLogin();


//        // 检查文件更新
//        manager = new UpdateManager(mContext);
//        manager.checkUpdateMe();
    }

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private void startLogin() {


//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try{
//                OkHttpClient okHttpClient = new OkHttpClient();
//                JSONObject json=new JSONObject();
//                json.put("PhoneNumber", "15000659340");
//                json.put("Password", "123456");
//                    RequestBody body = RequestBody.create(JSON, json.toString());
//                    Request request = new Request.Builder()
//                            .addHeader("content-type", "application/json")
//                            .url("http://139.196.85.20/Account/Login")
//                            .post(body)
//                            .build();
//                    Response response = okHttpClient.newCall(request).execute();
//                    String result = response.body().string();
////                    MyToast.show(mContext,"返回值::::"+result.toString());
//                    Log.e("返回值::::",result.toString());
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//            }
//        }).start();


        try {

            JSONObject json = new JSONObject();
            json.put("PhoneNumber", "15000659340");
            json.put("Password", "123456");

            OkHttpUtils
                    .postString()
                    .addHeader("content-type", "application/json")
                    .url("http://139.196.85.20/Account/Login")
                    .mediaType(JSON)
                    .content(json.toString())
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Request request, Exception e) {
                            e.printStackTrace();
                            MyToast.show(mContext, "返回值失败" + request.toString());
                        }

                        @Override
                        public void onResponse(String response) {
                            MyToast.show(mContext, "返回的结果为：：：：" + response);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }


/*                OkHttpUtils
                .get()
                .addHeader("content-type", "application/json;")
//                .addHeader("Content-Disposition", "application/json;")
                .url("http://139.196.110.136:7777/Disposition/DispositionList")
//                .addParams("type", "smartplan")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Request request, Exception e) {
                        MyToast.show(mContext,"返回值失败");
                    }
                    @Override
                    public void onResponse(String response) {
                        MyToast.show(mContext,"返回的结果为：：：："+response);
                    }
                });*/
    }


    @SuppressLint("NewApi")
    private void initView() {
        // TODO 自动生成的方法存根
        imap_login_uername = (AutoCompleteTextView) this.findViewById(R.id.imap_login_uername);
        imap_login_password = (EditText) this.findViewById(R.id.imap_login_password);
        imap_login_userlogin = (Button) this.findViewById(R.id.imap_login_userlogin);
        login_save_pwd = (CheckBox) this.findViewById(R.id.login_save_pwd);
        login_auto = (CheckBox) this.findViewById(R.id.login_auto);
//         TextView login_by_gesture= (TextView) this.findViewById(R.id.login_by_gesture);
        TextView user_register = (TextView) this.findViewById(R.id.user_register);

//         imap_login_uername.setOnClickListener(this);
//         imap_login_password.setOnClickListener(this);
        imap_login_userlogin.setOnClickListener(this);
        login_save_pwd.setOnClickListener(this);
        login_auto.setOnClickListener(this);
//         login_by_gesture.setOnClickListener(this);
        user_register.setOnClickListener(this);
        findViewById(R.id.return_back).setOnClickListener(this);


//         如果是设置保存了密码，则自动注入用户名及密码
        Boolean login_save_pwd_check = settings.getBoolean("login_save_pwd_check", false);
        login_save_pwd.setChecked(login_save_pwd_check);
        Boolean login_auto_check = settings.getBoolean("login_auto_check", false);
        login_auto.setChecked(login_auto_check);

        String m_username = settings.getString("loginName", "");
        String m_password = settings.getString("password", "");
        imap_login_uername.setText(m_username);
        if (login_save_pwd_check) {

            imap_login_password.setText(m_password);
            if (login_auto_check) {
                 /*Toast.makeText(mContext, "正在登录...", Toast.LENGTH_SHORT).show();
                  Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
      			startActivity(intent); */   
                /*mdialog = UtilsTool.initProgressDialog(mContext,"正在登录.....");
                mdialog.show();
            	UserLoginResult userloginresult = new UserLoginResult();
                userloginresult.execute(mdialog, imap_login_uername.getText().toString(), imap_login_password.getText().toString());         

                String username = settings.getString("username", "");
                String password = settings.getString("password", "");
                String loginName = settings.getString("loginName", "");
                String userid = settings.getString("userid", "");
                String userRole = settings.getString("userRole", "");
                String userOrganzation = settings.getString("userOrganzation", "");
                Boolean login_save_pwd_check1 = settings.getBoolean("login_save_pwd_check", false);
                Boolean login_auto_check1 = settings.getBoolean("login_auto_check", false);
                
                editor.putString("username",username);
                editor.putString("password",password);
                editor.putString("loginName",loginName);
                editor.putString("userid",userid);
                editor.putString("userRole",userRole);
                editor.putString("userOrganzation",userOrganzation);
//              保存密码是否保存勾选状态
                editor.putBoolean("login_save_pwd_check", login_save_pwd_check1);     
                editor.putBoolean("login_auto_check", login_auto_check1);
                editor.commit();

                Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
      			startActivity(intent);*/
            }
        }


//        imap_login_uername.setText(DistAndroidApp.username);
//         if(!DistAndroidApp.passwd.endsWith("")){
//             imap_login_password.setText(DistAndroidApp.passwd);
//         }
//        TextView login_by_text=(TextView)findViewById(R.id.login_by_text);
//        login_by_text.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // TODO 自动生成的方法存根
//                imap_login_uername.setText(""); 
//                username_fromserver=null;
//            }
//        });
//        
//        
//        if(username_fromserver!=null){
////          将从服务器已登录的用户名赋值到输入框中
//            imap_login_uername.setText(username_fromserver); 
////            使底部切换用户按钮可见登录可见
//            login_by_text.setVisibility(View.VISIBLE);
//            
//        }


    }


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
//    	manager.closeDialog();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        // TODO 自动生成的方法存根
        switch (v.getId()) {
            case R.id.imap_login_userlogin:
//                if (imap_login_uername.getText().toString().trim().equals("")
////                    ||imap_login_password.getText().toString().trim().equals("")
//                        ) {
//                    MyToast.show(mContext, "用户名或密码不能为空", Toast.LENGTH_LONG);
////                Intent  temp=new Intent(this,CharacterChooseActivity.class);
////                startActivity(temp);
//
//                } else {
//                    mdialog = UtilsTool.initProgressDialog(mContext, "正在登陆.....");
//                    mdialog.show();
////                UserLoginResult userloginresult = new UserLoginResult();
////                userloginresult.execute(mdialog, imap_login_uername.getText().toString(), imap_login_password.getText().toString());
//
//                    Intent temp = new Intent(this, MainActivity.class);
//                    startActivity(temp);
//                    mdialog.dismiss();
//                    finish();
//                }

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
     * 用户登录结果
     *
     * @author wmy
     */
    class UserLoginResult extends AsyncTask<Object, Void, String> {// 在后台初始化加载在线资源界面
        ProgressDialog dialog;
        //BaseAdapter adapter;
        String muername;
        String mpassword;

        @Override
        protected String doInBackground(Object... params) {
            dialog = (ProgressDialog) params[0];
            muername = (String) params[1];
            mpassword = (String) params[2];
            String result = null;

//            由于服务端接口采用了oauth token验证，因此必须获取token注入后才能登陆
//            if(MakeToken.getToken(muername,mpassword)){
            String url = WowuApp.serverAbsolutePath + "/mobile/app-mobileLogin.action";
            Map<String, String> map = new HashMap<String, String>();
//                map.put("type", "user");
//               
            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            map.put("deviceName", tm.getDeviceSoftwareVersion() + ".");
//                System.out.println("获取到的设备名称为：：：：："+tm.getDeviceSoftwareVersion());
            map.put("loginName", muername);
            map.put("password", mpassword);
            map.put("appType", "imeeting");

//              为了记录终端用户的登录状态，login表示用户登录， exit表示用户退出
            map.put("action", "loginaa");
//                map.put("appidentify", "com.dist.iportal");            
            try {
                result = UtilsTool.getStringFromServer(url, map);
            } catch (Exception e) {
                // TODO 自动生成的 catch 块
                e.printStackTrace();
                return result;
            }
//            }else{
//                result ="fail";
//            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            //                  Toast.makeText(context, "连接服务器成功，result:::::::::"+result, 1).show();
            if (dialog != null) dialog.dismiss();
            if (result == null) {
                MyToast.show(mContext, "连接服务器异常，无法提交申请", 1);
                return;
            } else if (result.equals("fail")) {
                MyToast.show(mContext, "连接服务器异常", Toast.LENGTH_LONG);
            } else {
                try {
//                    Log.d("::::::::::::::::::::登陆获取到的验证返回值为：：：：：",result);
                    JSONObject obj = new JSONObject(result);
//                    int tempresult =Integer.parseInt(obj.optString("result"));                   
                    int tempresult = -1;
                    if (!TextUtils.isEmpty(obj.optString("result"))) {
                        tempresult = Integer.parseInt(obj.optString("result"));
                    }
                    // 返回：0待审核；1正常；2挂失；3禁用；// 登陆成功后，在后台启动服务定期检测设备可用性
                    Intent intent1 = new Intent();

                    switch (tempresult) {

                        case 1://说明设备状态正常

                            if (obj.optBoolean("state")) {//说明用户成功登陆
                                WowuApp.userName = obj.optString("userName");
                                WowuApp.userId = obj.optString("userId");//表示设备的用户id

                                editor.putString("username", WowuApp.userName);
                                editor.putString("password", mpassword);
                                editor.putString("loginName", imap_login_uername.getText().toString());
                                editor.putString("userid", obj.optString("userId"));
                                editor.putString("userRole", obj.optString("userRole"));
                                editor.putString("userOrganzation", obj.optString("userOrganzation"));
//                          保存密码是否保存勾选状态
                                editor.putBoolean("login_save_pwd_check", login_save_pwd.isChecked());
                                editor.putBoolean("login_auto_check", login_auto.isChecked());
                                editor.commit();

//                            //2跳转到主页面
//                            intent1.setClass(mContext, HomeActivity.class);
//                            startActivity(intent1);
//                            finish();
                            } else {
                                Toast.makeText(mContext, "登录名或密码错误，请重新登陆", Toast.LENGTH_LONG).show();
                                imap_login_uername.setText("");
                                imap_login_password.setText("");
                            }


                            break;

                        default:
//                        //2跳转到主页面
//                        intent1.setClass(mContext, DeviceReActivity.class);
//                        startActivity(intent1);
//                        finish();
                            break;
                    }
                } catch (Exception e) {
                    // TODO 自动生成的 catch 块
                    e.printStackTrace();
                }

            }

        }
    }


    /**
     * @Title: sendToServer
     * @Description:设备状态验证
     * @param
     * @return void
     * @throws
     */
//    public void validateDevice() {
//        final String requestURL = WowuApp.serverAbsolutePath + "/mobilevalidate/app-mobileDeviceValidate.action";
//
//        Thread t = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                //                    if (file != null) {
//          
//                try {
//                  
//                    //请求普通信息
//                    Map<String, String> map = new HashMap<String, String>();
//                    map.put("deviceNumber", WowuApp.ALL_deviceNumber);                       
//                    String totalresult = UtilsTool.getInfoFromServer(requestURL, map );
////                    Log.d("获取到的服务器返回验证信息为：", totalresult);
//                    Message msg2 = new Message();
//                    if (totalresult != null && !totalresult.equals("")) {
//                        JSONObject obj = new JSONObject(totalresult);
//                        String result=obj.optString("result");
//                        if (result != null && result.trim().equals("-1")) {
//                            msg2.what = END;
//                        } else {
//                            msg2.what = WRONG;
//                        }
//                    } else {
//                        msg2.what = WRONG;
//                    }
//
//                    mHandler.sendMessage(msg2);
//                } catch (Exception e) {
//                    // TODO 自动生成的 catch 块
//                    e.printStackTrace();
//                }
//
//                //                    }
//            }
//        });
//        t.start();
//    }

    private final int END = 2;
    private final int WRONG = 3;

    /**
     * 上传到服务器是加载进度框
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case END:

//                //2跳转到主页面
//                Intent intent1 = new Intent();
//                intent1.setClass(mContext, DeviceReActivity.class);
//                startActivity(intent1);
//                finish();
                    break;

                case WRONG:
//               MyToast.show(mContext, "无法连接到服务器", Toast.LENGTH_LONG);
                    break;
            }

            super.handleMessage(msg);
        }
    };


}

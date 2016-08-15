package com.wuwo.im.activity;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.widget.Toast;

import com.easemob.redpacketsdk.RPCallback;
import com.easemob.redpacketsdk.RedPacket;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.chatuidemo.db.DemoDBManager;
import com.hyphenate.chatuidemo.db.UserDao;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.wuwo.im.config.WowuApp;
import com.wuwo.im.util.MyToast;
import com.wuwo.im.util.UpdateManager;
import com.wuwo.im.util.UtilsTool;
import com.zhy.http.okhttp.OkHttpUtils;

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
    private static final String TAG = "LoginActivity";

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
                OkHttpUtils.token= settings.getString("token", "");
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
        if (UtilsTool.checkNet(mContext)) {
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
        } else {
            String CacheJsonString = DemoDBManager.getInstance().getCacheJson(UserDao.CACHE_MAIN_LOCAL);
            Intent intent =null;
            if (CacheJsonString!=null && CacheJsonString.length()>0){
                //说明之前有缓存
                intent = new Intent(mContext, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        }
    }


    //此处是连接网络的返回值
    @Override
    public void loadServerData(String response, int flag) {
//        MyToast.show(mContext, "返回的结果为：：：：" + response);
        Log.i("获取登陆返回值为：：：", response.toString());
/*        {"token":"26aea8b5bde947af89c5255b2a08688e04f3856121a24a20bccfad6a397f3b6b","easemobId":"e6026dba-35f4-11e6-8ecc-01a33478b711",
                "name":"wmy","uid":"637e5acb638f46f5873ec86f0b4b49ce","icon":"http://xzxj.oss-cn-shanghai.aliyuncs.com/user/35b5090b-aaf4-4c6d-b46f-e42073e11f4ex128.jpg"}*/

        switch (flag) {
            case R.id.imap_login_userlogin:
                try {
                    JSONObject json = new JSONObject(response);
                    OkHttpUtils.token = json.optString("token");
                    WowuApp.UserId= json.optString("uid");
                    WowuApp.iconPath= json.optString("icon");
                    WowuApp.Name=json.optString("name");

//                    记录下登录者的信息
                    editor.putString("UserId",WowuApp.UserId);
                    editor.putString("token",OkHttpUtils.token);
                    editor.putString("PhoneNumber",WowuApp.PhoneNumber);
                    editor.putString("Password",WowuApp.Password);
                    editor.putInt("Gender",WowuApp.Gender);
                    editor.putString("Name",json.optString("name"));
                    editor.putString("iconPath",json.optString("icon"));
                    editor.putBoolean("login_save_pwd_check",true); //登录成功后下次点开后可自动登录
                    editor.commit();

                    loginHuanXin(WowuApp.UserId,WowuApp.hx_pwd);
//                    loginHuanXin(WowuApp.PhoneNumber,WowuApp.hx_pwd);

//                  跳转到主界面
//                    Intent temp = new Intent(this, MainActivity.class);
//                    startActivity(temp);
//                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//                finish();

                } catch (JSONException e) {
                    if(LoginActivity.this.pd !=null) LoginActivity.this.pd.dismiss();
                    e.printStackTrace();
                }
                break;
        }

    }






    //      登陆环信
    public void loginHuanXin(final String currentUsername, String currentPassword) {
        if (!EaseCommonUtils.isNetWorkConnected(this)) {
            Toast.makeText(this, R.string.network_isnot_available, Toast.LENGTH_SHORT).show();
            return;
        }

        // After logout，the DemoDB may still be accessed due to async callback, so the DemoDB will be re-opened again.
        // close it before login to make sure DemoDB not overlap
        DemoDBManager.getInstance().closeDB();

        // reset current user name before login
        DemoHelper.getInstance().setCurrentUserName(currentUsername);

        final long start = System.currentTimeMillis();
        // call login method
        Log.d(TAG, "EMClient.getInstance().login");
        EMClient.getInstance().login(currentUsername, currentPassword, new EMCallBack() {

            @Override
            public void onSuccess() {
                Log.d(TAG, "login: onSuccess");
                Log.d("登录环信：：：", "登录聊天服务器成功！");
                if (!LoginActivity.this.isFinishing() && pd.isShowing()) {
                    pd.dismiss();
                }

                // ** manually load all local groups and conversation  两个方法是为了保证进入主页面后本地会话和群组都 load 完毕。
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();

                // update current user's display name for APNs
                boolean updatenick = EMClient.getInstance().updateCurrentUserNick(
                        WowuApp.Name);
                if (!updatenick) {
                    Log.e("LoginActivity", "update current user nick fail");
                }

                // get user's info (this should be get from App's server or 3rd party service)
                DemoHelper.getInstance().getUserProfileManager().asyncGetCurrentUserInfo();

                RedPacket.getInstance().initRPToken(currentUsername, currentUsername, EMClient.getInstance().getChatConfig().getAccessToken(), new RPCallback() {
                    @Override
                    public void onSuccess() {

//                        Intent temp = new Intent(LoginActivity.this, MainActivity.class);
//                        startActivity(temp);
//                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }

                    @Override
                    public void onError(String s, String s1) {

                    }
                });
                // enter main activity
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }

            @Override
            public void onProgress(int progress, String status) {
                Log.d(TAG, "login: onProgress");

            }

            @Override
            public void onError(final int code, final String message) {
                Log.d(TAG, "login: onError: " + code);
                Log.d("登录环信：：：", "登录聊天服务器失败！");
                runOnUiThread(new Runnable() {
                    public void run() {
                        pd.dismiss();
                        Toast.makeText(getApplicationContext(), getString(R.string.Login_failed) + message,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


    @Override
    public void onBackPressed() {

        this.finish();
    }

    @Override
    public void loadDataFailed(String response,int flag) {
        MyToast.show(mContext,response.toString());
        if(pd !=null) pd.dismiss();
    }


    private ProgressDialog pd;
    private final int Loading = 1;
    private final int END = 2;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Loading:
               /*     pd = UtilsTool.initProgressDialog(mContext, "正在登录...");
                    pd.show();*/

                    pd   = new ProgressDialog(LoginActivity.this);
                    pd.setCanceledOnTouchOutside(false);
                    pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            Log.d(TAG, "EMClient.getInstance().onCancel");
                        }
                    });
                    pd.setMessage(getString(R.string.Is_landing));
                    pd.show();


                    break;
                case END:
//                    if(LoginActivity.this.pd !=null) LoginActivity.this.pd.dismiss();
                    break;
            }
            super.handleMessage(msg);
        }
    };


}

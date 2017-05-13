package com.wuwo.im.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.easemob.redpacketsdk.RPCallback;
import com.easemob.redpacketsdk.RedPacket;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.chatuidemo.db.DemoDBManager;
import com.hyphenate.chatuidemo.db.UserDao;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.util.DeviceUuidFactory;
import com.wuwo.im.config.WowuApp;
import com.wuwo.im.service.LocationService;
import com.wuwo.im.util.LogUtils;
import com.wuwo.im.util.MyToast;
import com.wuwo.im.util.UtilsTool;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import im.imxianzhi.com.imxianzhi.R;

public class LoadingActivity extends BaseLoadActivity {

    private RelativeLayout iv_start;
    private Context mContext = LoadingActivity.this;
    private static final int sleepTime = 2000;
    private SharedPreferences settings;
    String m_username;
    String m_password;
    private static final String TAG = "LoadingActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 去掉信息栏
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_loading);
        settings = this.getSharedPreferences(WowuApp.PREFERENCE_KEY, MODE_PRIVATE);

//        //启动定位服务
//        initLocationService();
        iv_start = (RelativeLayout) findViewById(R.id.iv_start);
//        iv_start = (ImageView) findViewById(R.id.iv_start);
//        iv_start.setImageResource(R.drawable.start);

        m_username = settings.getString("PhoneNumber", "");
        m_password = settings.getString("Password", "");

        //说明之前已经登录过的
        if (!m_username.equals("") && !m_password.equals("")) {
            String tokenString = null;
            try {
                tokenString = DemoDBManager.getInstance().getCacheJson(UserDao.CACHE_MAIN_TOKEN);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //      说明之前已经登录过且保存有token
            if(tokenString!=null){
                OkHttpUtils.token= tokenString;
                WowuApp.UserId =  settings.getString("UserId", "");
                WowuApp.iconPath =  settings.getString("iconPath", "");
                WowuApp.Name =  settings.getString("Name", "");
                WowuApp.PhoneNumber =  settings.getString("PhoneNumber", "");;
                WowuApp.Password =  settings.getString("Password", "");

                Intent intent = new Intent(mContext, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();

            }else{
                //跳转到登录界面
                Intent intent = new Intent(mContext, LoginChooseActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        }else{
            //跳转到登录界面
            Intent intent = new Intent(mContext, LoginChooseActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }

//        if (!m_username.equals("") && !m_password.equals("")) {
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    startLogin(m_username, m_password);
//                }
//            }).start();
//        }


    /*    final ScaleAnimation scaleAnim = new ScaleAnimation(1.0f, 1.1f, 1.0f, 1.1f,
                Animation.RELATIVE_TO_SELF, 0.2f, Animation.RELATIVE_TO_SELF,
                0.2f);
        scaleAnim.setFillAfter(true);
        scaleAnim.setDuration(5000);
        scaleAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (!m_username.equals("") && !m_password.equals("")) {
                } else {

                    if (UtilsTool.checkNet(mContext)) {
                        startActivity();
                    } else {
                        Toast.makeText(mContext, "没有网络连接!", Toast.LENGTH_LONG).show();

                        String CacheJsonString = DemoDBManager.getInstance().getCacheJson(UserDao.CACHE_MAIN_LOCAL);
                        Intent intent = null;
                        if (CacheJsonString != null && CacheJsonString.length() > 0) {
                            //说明之前有缓存
                            intent = new Intent(mContext, LoginActivity.class);
                        } else {
                            intent = new Intent(mContext, LoginChooseActivity.class);
                        }
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    }
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        iv_start.startAnimation(scaleAnim);*/


    }

    private void startLogin(String PhoneNumber, String Password) {
        if (UtilsTool.checkNet(mContext)) {
            try {


                JSONObject json = new JSONObject();
//                json.put("PhoneNumber", WowuApp.PhoneNumber);
//                json.put("Password", WowuApp.Password);
                json.put("PhoneNumber", PhoneNumber);
                json.put("Password", Password);

                json.put("PhoneModel", "Android");//手机型号，如Android
                json.put("PhoneVersion", android.os.Build.VERSION.RELEASE);//手机操作系统版本
                json.put("DeviceVersion", android.os.Build.MODEL);//设备版本
                DeviceUuidFactory tempdevice = new DeviceUuidFactory(mContext);
                json.put("DeviceUUID", tempdevice.getDeviceUuid());//设备唯一标识
                json.put("Version", getVersionCode(mContext));//APP版本号

                loadDataService.loadPostJsonRequestData(WowuApp.JSON, WowuApp.LoginURL, json.toString(), R.id.imap_login_userlogin);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            String CacheJsonString = DemoDBManager.getInstance().getCacheJson(UserDao.CACHE_MAIN_LOCAL);
            Intent intent = null;
            if (CacheJsonString != null && CacheJsonString.length() > 0) {
                //说明之前有缓存
                intent = new Intent(mContext, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            } else {
//                MyToast.show(mContext, "用户名或密码有误");
                Message msg = new Message();
                msg.what = FAILED;
                msg.obj = "用户名或密码有误";
                mHandler.sendMessage(msg);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        new Thread(new Runnable() {
            public void run() {
                if (DemoHelper.getInstance().isLoggedIn()) {
                    // auto login mode, make sure all group and conversation is loaed before enter the main screen
                    long start = System.currentTimeMillis();
                    EMClient.getInstance().groupManager().loadAllGroups();
                    EMClient.getInstance().chatManager().loadAllConversations();

                    RedPacket.getInstance().initRPToken(DemoHelper.getInstance().getCurrentUsernName(), DemoHelper.getInstance().getCurrentUsernName(), EMClient.getInstance().getChatConfig().getAccessToken(), new RPCallback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError(String s, String s1) {

                        }
                    });
                }
            }
        }).start();
    }

    private void initLocationService() {
        // 启动终端定位服务 1
        Intent startServiceIntent = new Intent(getApplicationContext(),
                LocationService.class);
        startServiceIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startService(startServiceIntent);

    }

    private void startActivity() {
        Intent intent = null;
        SharedPreferences settings = this.getSharedPreferences(WowuApp.PREFERENCE_KEY, MODE_PRIVATE);
        if (settings.getBoolean("firstLogin", true)) {
            intent = new Intent(mContext, LoginChooseActivity.class);//WelcomeActivity
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("firstLogin", false);
            editor.commit();
        } else {
            if (settings.getBoolean("login_save_pwd_check", false)) {//说明之前已经成功登录过
                intent = new Intent(mContext, LoginActivity.class);
            } else {
                intent = new Intent(mContext, LoginChooseActivity.class);
            }
        }
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void loadServerData(String response, int flag) {
        switch (flag) {
            case R.id.imap_login_userlogin:
                try {
                    JSONObject json = new JSONObject(response);
                    OkHttpUtils.token = json.optString("token");
                    DemoDBManager.getInstance().saveCacheJson(UserDao.CACHE_MAIN_TOKEN,  OkHttpUtils.token);

                    WowuApp.UserId = json.optString("uid");
                    WowuApp.iconPath = json.optString("icon");
                    WowuApp.Name = json.optString("name");

//                    记录下登录者的信息
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("UserId", WowuApp.UserId);
                    editor.putString("token", OkHttpUtils.token);
                    editor.putString("PhoneNumber", WowuApp.PhoneNumber);
                    editor.putString("Password", WowuApp.Password);
                    editor.putInt("Gender", WowuApp.Gender);
                    editor.putString("Name", json.optString("name"));
                    editor.putString("iconPath", json.optString("icon"));
                    editor.putBoolean("login_save_pwd_check", true); //登录成功后下次点开后可自动登录
                    editor.commit();

                    loginHuanXin(WowuApp.UserId, WowuApp.hx_pwd);
//                    loginHuanXin(WowuApp.PhoneNumber,WowuApp.hx_pwd);

//                  跳转到主界面
//                    Intent temp = new Intent(this, MainActivity.class);
//                    startActivity(temp);
//                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//                finish();

                } catch (JSONException e) {
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
        LogUtils.i(TAG, "EMClient.getInstance().login");
        EMClient.getInstance().login(currentUsername, currentPassword, new EMCallBack() {

            @Override
            public void onSuccess() {
                LogUtils.i(TAG, "login: onSuccess");
                LogUtils.i("LoginActivity登录环信：", "登录聊天服务器成功！");

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
                Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }

            @Override
            public void onProgress(int progress, String status) {
                LogUtils.i(TAG, "login: onProgress");

            }

            @Override
            public void onError(final int code, final String message) {
                LogUtils.i(TAG, "login: onError: " + code);
                LogUtils.i("LoginActivity登录环信：", "登录聊天服务器失败！");
                runOnUiThread(new Runnable() {
                    public void run() {
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
    public void loadDataFailed(String response, int flag) {
        if (UtilsTool.checkNet(mContext)) {
            startActivity();
        } else {
            Toast.makeText(mContext, "没有网络连接!", Toast.LENGTH_LONG).show();

            String CacheJsonString = DemoDBManager.getInstance().getCacheJson(UserDao.CACHE_MAIN_LOCAL);
            Intent intent = null;
            if (CacheJsonString != null && CacheJsonString.length() > 0) {
                //说明之前有缓存
                intent = new Intent(mContext, LoginActivity.class);
            } else {
                intent = new Intent(mContext, LoginChooseActivity.class);
            }
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }

    }

    private final int FAILED = 3;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FAILED:
                    MyToast.show(mContext, msg.obj + ";");
                    break;
            }
            super.handleMessage(msg);
        }
    };

    public String getVersionCode(Context context) {
        String versionCode = "0";
        try {
            // 获取软件版本号，对应AndroidManifest.xml下android:versionCode
            versionCode = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
            return versionCode;
        }
        return versionCode;
    }
}

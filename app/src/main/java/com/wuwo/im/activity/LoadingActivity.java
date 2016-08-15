package com.wuwo.im.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.easemob.redpacketsdk.RPCallback;
import com.easemob.redpacketsdk.RedPacket;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.chatuidemo.db.DemoDBManager;
import com.hyphenate.chatuidemo.db.UserDao;
import com.wuwo.im.config.WowuApp;
import com.wuwo.im.service.LocationService;
import com.wuwo.im.util.UtilsTool;

import im.wuwo.com.wuwo.R;

public class LoadingActivity extends com.wuwo.im.activity.BaseActivity {

    private ImageView iv_start;
    private Context mContext = LoadingActivity.this;
    private static final int sleepTime = 2000;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 去掉信息栏
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_loading);

        iv_start = (ImageView) findViewById(R.id.iv_start);
        iv_start.setImageResource(R.drawable.start);

//        // 闪屏的核心代码
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                // 判断是否第一次安装软件
//                SharedPreferences mSettings = getSharedPreferences(  .PREFERENCE_KEY, MODE_PRIVATE);
//                Boolean mFirstTimeSeting = mSettings.getBoolean("mFirstTimeSeting", true);
//                if (mFirstTimeSeting) {
//                    Intent intent = new Intent(mcontext, WelcomeActivity.class);
//                    startActivity(intent);
//                } else{
//                    Intent intent2 = new Intent();
//                    intent2.setClass(mcontext, WelcomeActivity.class);//LoginActivity.class
//                    startActivity(intent2);
//                }
//                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
//                LoadingActivity.this.finish(); // 结束启动动画界面
//            }
//        }, 2000); // 启动动画持续3秒钟


        final ScaleAnimation scaleAnim = new ScaleAnimation(1.0f, 1.2f, 1.0f, 1.2f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        scaleAnim.setFillAfter(true);
        scaleAnim.setDuration(3000);
        scaleAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (UtilsTool.checkNet(mContext)) {
                    startActivity();
                } else {
                    Toast.makeText(mContext, "没有网络连接!", Toast.LENGTH_LONG).show();

                    String CacheJsonString = DemoDBManager.getInstance().getCacheJson(UserDao.CACHE_MAIN_LOCAL);
                    Intent intent =null;
                    if (CacheJsonString!=null && CacheJsonString.length()>0){
                        //说明之前有缓存
                         intent = new Intent(mContext, LoginActivity.class);
                    }else{
                         intent = new Intent(mContext, LoginChooseActivity.class);
                    }
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        iv_start.startAnimation(scaleAnim);


        initLocationService();

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
            intent = new Intent(mContext,LoginChooseActivity.class);//WelcomeActivity
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("firstLogin", false);
            editor.commit();
        } else {
            if(settings.getBoolean("login_save_pwd_check", false)){//说明之前已经成功登录过
                intent = new Intent(mContext, LoginActivity.class);
            }else{
                intent = new Intent(mContext, LoginChooseActivity.class);
            }
        }
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

}

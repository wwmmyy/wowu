package com.wuwo.im.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wuwo.im.config.WowuApp;
import com.wuwo.im.util.MyToast;
import com.wuwo.im.util.UtilsTool;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import im.wuwo.com.wuwo.R;
/** 
*desc VersionIntroActivity
*@author 王明远
*@日期： 2016/6/9 0:07
*@版权:Copyright    All rights reserved.
*/

public class VersionIntroActivity extends Activity implements OnClickListener {
    Context mContext = VersionIntroActivity.this;
    String titlename;
    TextView feed_back_send;
    TextView feed_back_content;
    SharedPreferences mSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO 自动生成的方法存根
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        //            WindowManager.LayoutParams.FLAG_FULLSCREEN);// 去掉信息栏
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_version_intro);

        mSettings = getSharedPreferences("com.dist.iportal.password",
                Context.MODE_PRIVATE);

        feed_back_send = (TextView) findViewById(R.id.tx_top_right);
        feed_back_content = (TextView) findViewById(R.id.feed_back_edit);
        feed_back_send.setOnClickListener(this);
        feed_back_content.setOnClickListener(this);

//        feed_back_send.setText("发送");
        ((TextView) findViewById(R.id.top_title)).setText("版本预告");

        
        ImageView   return_back0_feedback = (ImageView) findViewById(R.id.return_back);
        return_back0_feedback.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        // TODO 自动生成的方法存根
        switch (v.getId()) {
        case R.id.tx_top_right:
            sendToServer();
            break;
        case R.id.return_back:
            VersionIntroActivity.this.finish();
            break;
        default:
            break;
        }

    }

    /**
     * @Title: sendToServer
     * @Description: 上传到服务器
     * @param
     * @return void
     * @throws
     */
    public void sendToServer() {
        final String requestURL = WowuApp.serverAbsolutePath + "/mobile/app-feedBack.action";

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //                    if (file != null) {
                Message msg = new Message();
                msg.what = Loading;
                mHandler.sendMessage(msg);
                try {

                    //请求普通信息
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("userId", mSettings.getString("userid", ""));//用户登录以后获取用户的userID并保存
                    map.put("content", feed_back_content.getText().toString());
//                    map.put("appidentify", "com.dist.iportal");                    
                    String totalresult = UtilsTool.getStringFromServer(requestURL, map );
                    Message msg2 = new Message();

                    if (totalresult != null && !totalresult.equals("")) {
                        JSONObject obj = new JSONObject(totalresult);
                        if (obj.optBoolean("state")) {
                            msg2.what = END;
                        } else {
                            msg2.what = WRONG;
                        }
                    } else {
                        msg2.what = WRONG;
                    }

                    mHandler.sendMessage(msg2);
                } catch (Exception e) {
                    // TODO 自动生成的 catch 块
                    e.printStackTrace();
                }

                //                    }
            }
        });
        t.start();
    }

    ProgressDialog pg;
    private final int Loading = 1;
    private final int END = 2;
    private final int WRONG = 3;

    /**
     * 上传到服务器是加载进度框
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case Loading:
                //pg = UtilsTool.initProgressDialog(mContext, "正在上传.....");
            	pg = UtilsTool.initProgressDialog(mContext, "正在上传.....");
                pg.show();
                break;
            case END:
                pg.dismiss();
                MyToast.show(getApplicationContext(), "意见反馈成功", Toast.LENGTH_LONG);
                finish();
                break;

            case WRONG:
                pg.dismiss();
                MyToast.show(getApplicationContext(), "意见反馈失败", Toast.LENGTH_LONG);
                finish();
                break;
            }

            super.handleMessage(msg);
        }
    };

}

package com.wuwo.im.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.wuwo.im.util.MyToast;
import com.wuwo.im.util.UtilsTool;

import im.wuwo.com.wuwo.R;
/** 
*desc VersionIntroActivity
*@author 王明远
*@日期： 2016/6/9 0:07
*@版权:Copyright    All rights reserved.
*/

public class VersionIntroActivity extends BaseLoadActivity {
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

        findViewById(R.id.return_back).setOnClickListener(this);

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
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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

    @Override
    public void loadServerData(String response, int flag) {

    }

    @Override
    public void loadDataFailed(String response, int flag) {

    }
}

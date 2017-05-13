package com.wuwo.im.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wuwo.im.config.WowuApp;
import com.wuwo.im.util.MyToast;
import com.wuwo.im.util.UtilsTool;

import java.util.ArrayList;

import im.imxianzhi.com.imxianzhi.R;
/** 
*desc  发短信分享给好友
*@author 王明远
*@日期： 2016/9/20 23:07
*@版权:Copyright
*/

public class ShareToContractBySmsActivity extends BaseLoadActivity {
    Context mContext = ShareToContractBySmsActivity.this;
    TextView feed_back_send,tv_name_num;
    EditText feed_back_content;
    SharedPreferences mSettings;
    String name,num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO 自动生成的方法存根
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        //            WindowManager.LayoutParams.FLAG_FULLSCREEN);// 去掉信息栏
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_to_contract_by_sms);

        mSettings = getSharedPreferences(WowuApp.PREFERENCE_KEY, MODE_PRIVATE);

//        android:hint="我在先知先觉，先知号：20516878。#快来和我一起加入先知先觉，发现更多附近新奇#http://weixin.imxianzhi.com/share?download=true"

        name=getIntent().getStringExtra("name");
        num=getIntent().getStringExtra("num");

        feed_back_send = (TextView) findViewById(R.id.tx_top_right);
        feed_back_content = (EditText) findViewById(R.id.et_sendsms_edit);
        tv_name_num = (TextView) findViewById(R.id.tv_name_num);
        tv_name_num.setText(name+":"+num);

        feed_back_send.setOnClickListener(this);


        feed_back_content.setText("我在先知先觉，先知号："+WowuApp.XianZhiNumber+"#快来和我一起加入先知先觉，发现更多附近新奇"+WowuApp.shareURL);

//        feed_back_send.setText("发送");
        ((TextView) findViewById(R.id.top_title)).setText("短信发送");


         findViewById(R.id.return_back).setOnClickListener(this);
        findViewById(R.id.bt_share_sms_submit).setOnClickListener(this);
    }



    private void sendsms(final String phoneNum, final String text){
        new Thread(new Runnable() {
            @Override
            public void run() {
                SmsManager sm = SmsManager.getDefault();
                ArrayList<String> parts = sm.divideMessage(text);
                sm.sendMultipartTextMessage(phoneNum, null, parts, null, null);
            }
        }).start();

    }



    @Override
    public void onClick(View v) {
        // TODO 自动生成的方法存根
        switch (v.getId()) {
            case R.id.tx_top_right:
                sendsms(num,feed_back_content.getText().toString());
                break;
            case R.id.return_back:
                ShareToContractBySmsActivity.this.finish();
               overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.bt_share_sms_submit:
                sendsms(num,feed_back_content.getText().toString());
                MyToast.show(mContext,"已发送~");
                this.finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                 break;
            default:
                break;
        }

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

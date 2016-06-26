package com.wuwo.im.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wuwo.im.config.ExitApp;
import com.wuwo.im.config.WowuApp;
import com.wuwo.im.util.MyToast;
import com.wuwo.im.util.UtilsTool;

import org.json.JSONException;
import org.json.JSONObject;

import im.wuwo.com.wuwo.R;

/**
 * desc 性格测试结果
 *
 * @author 王明远
 * @日期： 2016/6/24 22:53
 * @版权:Copyright All rights reserved.
 */

public class CharacterTResultActivity extends BaseLoadActivity {
    Context mContext = this;
    SimpleDraweeView portal_news_img;
    TextView tv_user_type, tv_user_typeintro, tv_user_typename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ExitApp.getInstance().addOpenedActivity(this);
        setContentView(R.layout.activity_character_tresult);
        loadDataService.loadGetJsonRequestData(WowuApp.GetDispositionInfoURL, R.id.bt_jingque);
        initView();
    }

    private void initView() {
        findViewById(R.id.tv_user_types_enter).setOnClickListener(this);
        findViewById(R.id.tv_user_typeshare).setOnClickListener(this);

        pg = UtilsTool.initProgressDialog(mContext, "正在连接.....");
        pg.show();

        tv_user_type = (TextView) findViewById(R.id.tv_user_type);
        tv_user_typeintro = (TextView) findViewById(R.id.tv_user_typeintro);
        tv_user_typename = (TextView) findViewById(R.id.tv_user_typename);

        portal_news_img = (SimpleDraweeView) findViewById(R.id.user_label_pic);
        portal_news_img.setImageURI(Uri.parse("http://www.gog.com.cn/pic/0/10/91/11/10911138_955870.jpg"));

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_user_types_enter:
                Intent intent2 = new Intent(mContext, MainActivity.class);
                startActivity(intent2);
                this.finish();
                break;
 /*           case R.id.return_back:
                this.finish();
                break;*/
        }

    }

    @Override
    public void loadServerData(String response, int flag) {
        switch (flag) {
            case R.id.bt_jingque:
                Log.d("test result:返回的结果为：：：：", response);
//             {"PhotoUrl":null,"Celebrity":"莎士比亚","CelebrityDescription":"英国文学史上最杰出的戏剧家","Name":"INFP","Title":"化解者"}
                Message msg = Message.obtain();
                msg.what = REFRESH;
                msg.obj = response;
                mHandler.sendMessage(msg);
                break;
        }
    }


    @Override
    public void loadDataFailed(String response, int flag) {
        MyToast.show(mContext, response);
    }


    private final int REFRESH = 1;
    private ProgressDialog pg;
    private final int Loading = 3;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Loading:
                    pg = UtilsTool.initProgressDialog(mContext, "正在连接.....");
                    pg.show();
                    break;
                case REFRESH:
                    if (pg != null && pg.isShowing()) pg.dismiss();
//                 {"PhotoUrl":null,"Celebrity":"莎士比亚","CelebrityDescription":"英国文学史上最杰出的戏剧家","Name":"INFP","Title":"化解者"}
                    try {
                        JSONObject json = new JSONObject(msg.obj.toString());
                        portal_news_img.setImageURI(Uri.parse(json.optString("PhotoUrl", "http://www.gog.com.cn/pic/0/10/91/11/10911138_955870.jpg")));
                        tv_user_type.setText(json.optString("Name") + " " + json.optString("Title"));
                        tv_user_typeintro.setText(json.optString("CelebrityDescription"));
                        tv_user_typename.setText(json.optString("Celebrity"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };


}

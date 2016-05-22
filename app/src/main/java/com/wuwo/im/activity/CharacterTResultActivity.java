package com.wuwo.im.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import im.wuwo.com.wuwo.R;

public class CharacterTResultActivity extends BaseActivity implements View.OnClickListener {
    Context mContext=this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_tresult);
        initView();
    }

    private void initView() {
        findViewById(R.id.tv_user_types_enter).setOnClickListener(this);
        ( (TextView)findViewById(R.id.top_title)).setText("性格");


        SimpleDraweeView portal_news_img = (SimpleDraweeView) findViewById(R.id.user_label_pic);
        portal_news_img.setImageURI(Uri.parse("http://www.gog.com.cn/pic/0/10/91/11/10911138_955870.jpg"));

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_user_types_enter:
                Intent intent2 = new Intent(mContext, MainActivity.class);
                startActivity(intent2);
                break;
        }

    }
}

package com.wuwo.im.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wuwo.im.config.WowuApp;
import com.wuwo.im.view.RangeSeekBar;

import im.wuwo.com.wuwo.R;

public class MyCharacterResultActivity extends BaseLoadActivity  {

    RangeSeekBar rs_character;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_character_result);
        initView();
    }

    private void initView() {
        findViewById(R.id.tv_character_re_test).setOnClickListener(this);
        findViewById(R.id.return_back).setOnClickListener(this);

        SimpleDraweeView draweeView = (SimpleDraweeView) findViewById(R.id.user_login_pic);
        draweeView.setImageURI(Uri.parse(WowuApp.iconPath));//"http://www.gog.com.cn/pic/0/10/91/11/10911138_955870.jpg"

        rs_character= (RangeSeekBar) findViewById(R.id.rs_character);


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.return_back:
                this.finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.tv_character_re_test:
//                Intent temp2 = new Intent(this, CharacterTestActivity.class);
                Intent temp2 = new Intent(this, CharacterChooseActivity.class);
                this.startActivity(temp2);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
        }

    }

    @Override
    public void loadServerData(String response, int flag) {

    }

    @Override
    public void loadDataFailed(String response, int flag) {

    }
}

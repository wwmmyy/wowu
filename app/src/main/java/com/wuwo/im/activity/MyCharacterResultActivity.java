package com.wuwo.im.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import im.wuwo.com.wuwo.R;

public class MyCharacterResultActivity extends BaseLoadActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_character_result);
        initView();
    }

    private void initView() {
        findViewById(R.id.tv_character_re_test).setOnClickListener(this);
        findViewById(R.id.return_back).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.return_back:
                this.finish();
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

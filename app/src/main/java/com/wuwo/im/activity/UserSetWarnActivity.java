package com.wuwo.im.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import im.wuwo.com.wuwo.R;

public class UserSetWarnActivity extends BaseLoadActivity {
    TextView tv_set_pwd, top_title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_set_warn);


        top_title = (TextView) findViewById(R.id.top_title);
        top_title.setText("消息提醒");

        findViewById(R.id.return_back).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_set_pwd_finish:
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.return_back:
                    finish();
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

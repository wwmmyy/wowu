package com.wuwo.im.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.wuwo.im.config.ExitApp;

import im.wuwo.com.wuwo.R;


public class RegisterActivity extends BaseActivity implements View.OnClickListener {
    EditText register_phone_pwd;
    EditText register_phone_sms;
    EditText user_register_phone_num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ExitApp.getInstance().addOpenedActivity(this);
        initView();
    }

    private void initView() {

        register_phone_pwd = (EditText) findViewById(R.id.register_phone_pwd);
        register_phone_sms = (EditText) findViewById(R.id.register_phone_sms);
        user_register_phone_num = (EditText) findViewById(R.id.user_register_phone_num);

        findViewById(R.id.return_back).setOnClickListener(this);
        findViewById(R.id.register_phone_but).setOnClickListener(this);
        findViewById(R.id.register_next).setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.return_back:
                this.finish();
                break;
            case R.id.register_phone_but:
                break;
            case R.id.register_next:
                Intent temp = new Intent(this, RegisterNextActivity.class);
                this.startActivity(temp);
                break;
        }
    }
}

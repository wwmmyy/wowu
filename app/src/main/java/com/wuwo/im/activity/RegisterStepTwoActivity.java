package com.wuwo.im.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.wuwo.im.config.ExitApp;

import im.wuwo.com.wuwo.R;

public class RegisterStepTwoActivity extends BaseActivity implements View.OnClickListener {
    Context mContext = RegisterStepTwoActivity.this;
    private EditText et_register_two_yanzhengma;
    private TextView tv_register_two_sure, tv_registersteptwo_phone_num;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ExitApp.getInstance().addOpenedActivity(this);
        setContentView(R.layout.activity_register_step_two);
        initView();

    }

    private void initView() {

        et_register_two_yanzhengma = (EditText) findViewById(R.id.et_register_two_yanzhengma);
        tv_registersteptwo_phone_num = (TextView) findViewById(R.id.tv_registersteptwo_phone_num);
        findViewById(R.id.tv_register_two_sure).setOnClickListener(this);
        findViewById(R.id.tv_registersteptwo_change_phone_num).setOnClickListener(this);
        findViewById(R.id.return_back).setOnClickListener(this);

    }

    Intent temp2 = null;
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.return_back:
                this.finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.tv_register_two_sure:
                temp2 = new Intent(this, RegisterStepThreeActivity.class);
                this.startActivity(temp2);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
        }
    }
}

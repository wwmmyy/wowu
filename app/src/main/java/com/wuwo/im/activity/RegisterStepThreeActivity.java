package com.wuwo.im.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.wuwo.im.config.ExitApp;

import im.wuwo.com.wuwo.R;

public class RegisterStepThreeActivity extends BaseActivity implements View.OnClickListener {
    Context mContext = RegisterStepThreeActivity.this;
    private EditText et_register_password;
    private TextView tv_register_three_sure;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ExitApp.getInstance().addOpenedActivity(this);
        setContentView(R.layout.activity_register_step_three);

        initView();

    }

    private void initView() {
        et_register_password = (EditText) findViewById(R.id.et_register_password);
        tv_register_three_sure = (TextView) findViewById(R.id.tv_register_three_sure);
        tv_register_three_sure.setOnClickListener(this);
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
            case R.id.tv_register_three_sure:
                temp2 = new Intent(this, RegisterStepFourActivity.class);
                this.startActivity(temp2);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
        }
    }
}

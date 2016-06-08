package com.wuwo.im.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.wuwo.im.config.WowuApp;
import com.wuwo.im.service.LoadserverdataService;

import im.wuwo.com.wuwo.R;
/**
*desc RegisterStepThreeActivity
*@author 王明远
*@日期： 2016/6/9 0:07
*@版权:Copyright    All rights reserved.
*/

public class RegisterStepThreeActivity extends BaseLoadActivity  {
//    Context mContext = RegisterStepThreeActivity.this;
    private EditText et_register_password;
    private TextView tv_register_three_sure;
    private LoadserverdataService loadDataService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ExitApp.getInstance().addOpenedActivity(this);
        setContentView(R.layout.activity_register_step_three);
//        loadDataService = new LoadserverdataService(this);
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
                 WowuApp.Password=et_register_password.getText().toString();
                temp2 = new Intent(this, RegisterStepFourActivity.class);
                this.startActivity(temp2);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
        }
    }

    @Override
    public void loadServerData(String response) {

    }

    @Override
    public void loadDataFailed(String response) {

    }
}

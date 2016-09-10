package com.wuwo.im.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.wuwo.im.config.WowuApp;

import im.wuwo.com.wuwo.R;
/**
 * 添加密码
*desc RegisterStepThreeActivity
*@author 王明远
*@日期： 2016/6/9 0:07
*@版权:Copyright    All rights reserved.
*/

public class RegisterStepThreeActivity extends BaseLoadActivity  {
//    Context mContext = RegisterStepThreeActivity.this;
    private EditText et_register_password;
    private TextView tv_register_three_sure;
//    private LoadserverdataService loadDataService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ExitApp.getInstance().addOpenedActivity(this);
        setContentView(R.layout.activity_register_step_three);
//        loadDataService = new LoadserverdataService(this);
        initView();

    }

    private void initView() {
        findViewById(R.id.iv_top_title).setVisibility(View.VISIBLE);
        findViewById(R.id.top_title).setVisibility(View.GONE);

        et_register_password = (EditText) findViewById(R.id.et_register_password);
        findViewById(R.id.return_back).setOnClickListener(this);


        tv_register_three_sure= (TextView)findViewById(R.id.tv_register_three_sure);
        tv_register_three_sure.setOnClickListener(this);
        tv_register_three_sure.getBackground().setAlpha(50);//0~255透明度值
        tv_register_three_sure.setTextColor(tv_register_three_sure.getTextColors().withAlpha(50));
        et_register_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if(et_register_password.getText().toString().equals("")){
                    tv_register_three_sure.getBackground().setAlpha(50);//0~255透明度值
                    tv_register_three_sure.setTextColor(tv_register_three_sure.getTextColors().withAlpha(50));
                }else{
                    tv_register_three_sure.getBackground().setAlpha(255);//0~255透明度值
                    tv_register_three_sure.setTextColor(tv_register_three_sure.getTextColors().withAlpha(255));
                }
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
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
    public void loadServerData(String response,int flag) {

    }

    @Override
    public void loadDataFailed(String response,int flag) {

    }
}

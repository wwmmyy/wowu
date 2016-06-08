package com.wuwo.im.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.wuwo.im.config.WowuApp;
import com.wuwo.im.service.LoadserverdataService;
import com.wuwo.im.util.MyToast;

import org.json.JSONObject;

import im.wuwo.com.wuwo.R;
/**
*desc RegisterStepTwoActivity
*@author 王明远
*@日期： 2016/6/9 0:07
*@版权:Copyright    All rights reserved.
*/

public class RegisterStepTwoActivity extends BaseLoadActivity  {
//    Context mContext = RegisterStepTwoActivity.this;
    private EditText et_register_two_yanzhengma;
    private TextView tv_register_two_sure, tv_registersteptwo_phone_num;
    private LoadserverdataService loadDataService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ExitApp.getInstance().addOpenedActivity(this);
        setContentView(R.layout.activity_register_step_two);
//        loadDataService = new LoadserverdataService(this);
        initView();

    }

    private void initView() {

        et_register_two_yanzhengma = (EditText) findViewById(R.id.et_register_two_yanzhengma);
        tv_registersteptwo_phone_num = (TextView) findViewById(R.id.tv_registersteptwo_phone_num);
        findViewById(R.id.tv_register_two_sure).setOnClickListener(this);
        findViewById(R.id.tv_registersteptwo_change_phone_num).setOnClickListener(this);
        findViewById(R.id.return_back).setOnClickListener(this);

    }


    public void ValidateSms() {
        WowuApp.SmsValidateCode = et_register_two_yanzhengma.getText().toString();
//        try {
//            JSONObject json = new JSONObject();
//            json.put("PhoneNumber", "18565398524");
//            json.put("Type", "0");
//            json.put("SmsValidateCode", et_register_two_yanzhengma.getText().toString());
//            OkHttpUtils
//                    .postString()
//                    .addHeader("content-type", "application/json")
//                    .url(WowuApp.ValidateCodeURL)
//                    .mediaType(WowuApp.JSON)
//                    .content(json.toString())
//                    .build()
//                    .execute(new StringCallback() {
//                        @Override
//                        public void onError(Request request, Exception e) {
//                            e.printStackTrace();
//                            MyToast.show(mContext, "返回值失败" + request.toString());
//                            Log.i("返回值失败", request.toString());
//                        }
//
//                        @Override
//                        public void onResponse(String response) {
//                            MyToast.show(mContext, "返回的结果为：：：：" + response);
//                            Log.i("返回的结果为", response.toString());
//
//                            temp2 = new Intent(mContext, RegisterStepThreeActivity.class);
//                            mContext.startActivity(temp2);
//                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//                        }
//                    });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        try {
            JSONObject json = new JSONObject();
            json.put("PhoneNumber", "18565398524");
            json.put("Type", "0");
            json.put("SmsValidateCode", et_register_two_yanzhengma.getText().toString());
            loadDataService.loadPostJsonRequestData(WowuApp.JSON, WowuApp.ValidateCodeURL, json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }


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
                ValidateSms();
                break;
        }
    }

    @Override
    public void loadServerData(String response) {
        MyToast.show(mContext, "返回的结果为：：：：" + response);
        Log.i("返回的结果为", response.toString());

        temp2 = new Intent(mContext, RegisterStepThreeActivity.class);
        mContext.startActivity(temp2);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void loadDataFailed(String request) {
        MyToast.show(mContext, "返回值失败" + request.toString());
        Log.i("返回值失败", request.toString());
    }
}

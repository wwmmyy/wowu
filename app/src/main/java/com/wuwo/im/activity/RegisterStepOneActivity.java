package com.wuwo.im.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.wuwo.im.config.WowuApp;
import com.wuwo.im.service.LoadserverdataService;
import com.wuwo.im.util.MyToast;

import org.json.JSONObject;

import im.wuwo.com.wuwo.R;

/**
*desc RegisterStepOneActivity
*@author 王明远
*@日期： 2016/6/9 0:07
*@版权:Copyright    All rights reserved.
*/

public class RegisterStepOneActivity extends BaseLoadActivity  {
    EditText register_phone_pwd;
    EditText register_phone_sms;
    EditText et_register_phone_num;
//    Context mContext=this;
    private LoadserverdataService loadDataService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_one);
//        ExitApp.getInstance().addOpenedActivity(this);
//        loadDataService=new LoadserverdataService(this);
        initView();
    }

    private void initView() {

//        register_phone_pwd = (EditText) findViewById(R.id.register_phone_pwd);
//        register_phone_sms = (EditText) findViewById(R.id.register_phone_sms);
        et_register_phone_num = (EditText) findViewById(R.id.et_register_phone_num);

        findViewById(R.id.return_back).setOnClickListener(this);
        findViewById(R.id.tv_register_one_sure).setOnClickListener(this);
        findViewById(R.id.user_register_police).setOnClickListener(this);
        findViewById(R.id.user_register_tiaokuan).setOnClickListener(this);
    }





    public void ValidateSms(){
       WowuApp.PhoneNumber=et_register_phone_num.getText().toString();
//        try {
//            JSONObject json = new JSONObject();
//            json.put("PhoneNumber",et_register_phone_num.getText().toString());
//            json.put("Type", "0");
//            OkHttpUtils
//                    .postString()
//                    .addHeader("content-type", "application/json")
//                    .url(WowuApp.SmsValidateURL)
//                    .mediaType(WowuApp.JSON)
//                    .content(json.toString())
//                    .build()
//                    .execute(new StringCallback() {
//                        @Override
//                        public void onError(Request request, Exception e) {
//                            e.printStackTrace();
//                            MyToast.show(mContext, "返回值失败" + request.toString());
//                        }
//
//                        @Override
//                        public void onResponse(String response) {
//                            MyToast.show(mContext, "返回的结果为：：：：" + response);
//                        }
//                    });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        try{
            JSONObject json = new JSONObject();
            json.put("PhoneNumber",et_register_phone_num.getText().toString());
            json.put("Type", "0");
            loadDataService.loadPostJsonRequestData(WowuApp.JSON,WowuApp.SmsValidateURL,json.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
    }





    Intent temp2=null;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.return_back:
                this.finish();
                break;
            case R.id.tv_register_one_sure:
                ValidateSms();
                 temp2 = new Intent(this, RegisterStepTwoActivity.class);
                this.startActivity(temp2);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.user_register_tiaokuan:
                temp2 = new Intent(this, RegisterPolicyActivity.class);
                temp2.putExtra("polcytye",0);
                this.startActivity(temp2);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;

            case R.id.user_register_police:
                 temp2 = new Intent(this,RegisterPolicyActivity.class);
                temp2.putExtra("polcytye",1);
                this.startActivity(temp2);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
        }
    }

    @Override
    public void loadServerData(String response) {
        MyToast.show(mContext, "返回的结果为：：：：" + response);
    }

    @Override
    public void loadDataFailed(String response) {
        MyToast.show(mContext, "返回值失败" + response.toString());
    }
}

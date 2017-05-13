package com.wuwo.im.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.wuwo.im.config.WowuApp;
import com.wuwo.im.util.LogUtils;
import com.wuwo.im.util.MyToast;

import org.json.JSONObject;

import im.imxianzhi.com.imxianzhi.R;

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
//    private LoadserverdataService loadDataService;
    TextView tv_register_one_sure;

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
        findViewById(R.id.iv_top_title).setVisibility(View.VISIBLE);
        findViewById(R.id.top_title).setVisibility(View.GONE);


        findViewById(R.id.return_back).setOnClickListener(this);
        tv_register_one_sure= (TextView)findViewById(R.id.tv_register_one_sure);
        tv_register_one_sure.setOnClickListener(this);
        tv_register_one_sure.getBackground().setAlpha(50);//0~255透明度值
        tv_register_one_sure.setTextColor(tv_register_one_sure.getTextColors().withAlpha(50));
        findViewById(R.id.user_register_police).setOnClickListener(this);
        findViewById(R.id.user_register_tiaokuan).setOnClickListener(this);


        et_register_phone_num.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if(et_register_phone_num.getText().toString().equals("")){
                    tv_register_one_sure.getBackground().setAlpha(50);//0~255透明度值
                    tv_register_one_sure.setTextColor(tv_register_one_sure.getTextColors().withAlpha(50));
                }else{
                    tv_register_one_sure.getBackground().setAlpha(255);//0~255透明度值
                    tv_register_one_sure.setTextColor(tv_register_one_sure.getTextColors().withAlpha(255));
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
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
                    loadDataService.loadPostJsonRequestData(WowuApp.JSON,WowuApp.SmsValidateURL,json.toString(),R.id.tv_register_one_sure);
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
                break;
            case R.id.user_register_tiaokuan:
//                temp2 = new Intent(this, RegisterPolicyActivity.class);
//                temp2.putExtra("polcytye",0);

                temp2 = new Intent(this, WebviewDetailActivity.class);
                temp2.putExtra("url",WowuApp.TiaoKuanURL);
                temp2.putExtra("titlename","条款");

                this.startActivity(temp2);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;

            case R.id.user_register_police:
//                 temp2 = new Intent(this,RegisterPolicyActivity.class);
//                temp2.putExtra("polcytye",1);
                temp2 = new Intent(this, WebviewDetailActivity.class);
                temp2.putExtra("url",WowuApp.YinSiZhengCeURL);
                temp2.putExtra("titlename","隐私权政策");
                this.startActivity(temp2);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
        }
    }

    @Override
    public void loadServerData(String response,int flag) {
//        MyToast.show(mContext, "返回的结果为：：：：" + response);

        temp2 = new Intent(this, RegisterStepTwoActivity.class);
        this.startActivity(temp2);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }


    private final int LoadingError = 1;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LoadingError:
                    MyToast.show(mContext,  msg.obj+"");
                    break;
            }
            super.handleMessage(msg);
        }
    };


    @Override
    public void loadDataFailed(String response,int flag) {
//        MyToast.show(mContext,  response.toString()+".");
//        MyToast.show(mContext,  "服务器处理异常");
        Message msg = new Message();
        msg.what = LoadingError;
        msg.obj=response+";";
        mHandler.sendMessage(msg);
        LogUtils.i("RegisterStepOneActivity", "：：" + response);
    }
}

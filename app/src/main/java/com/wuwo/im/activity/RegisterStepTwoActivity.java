package com.wuwo.im.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.wuwo.im.config.WowuApp;
import com.wuwo.im.util.MyToast;

import org.json.JSONObject;

import im.wuwo.com.wuwo.R;

/**
 * 主要是填写获取到的验证码验证是否正确
 * desc RegisterStepTwoActivity
 *
 * @author 王明远
 * @日期： 2016/6/9 0:07
 * @版权:Copyright All rights reserved.
 */

public class RegisterStepTwoActivity extends BaseLoadActivity {
    //    Context mContext = RegisterStepTwoActivity.this;
    private EditText et_register_two_yanzhengma;
    private TextView tv_register_two_sure, tv_registersteptwo_phone_num;
//    private LoadserverdataService loadDataService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ExitApp.getInstance().addOpenedActivity(this);
        setContentView(R.layout.activity_register_step_two);
         initView();

    }

    private void initView() {

        et_register_two_yanzhengma = (EditText) findViewById(R.id.et_register_two_yanzhengma);
        tv_registersteptwo_phone_num = (TextView) findViewById(R.id.tv_registersteptwo_phone_num);
         findViewById(R.id.tv_registersteptwo_change_phone_num).setOnClickListener(this);
        findViewById(R.id.return_back).setOnClickListener(this);

        tv_registersteptwo_phone_num.setText(WowuApp.PhoneNumber);

        tv_register_two_sure= (TextView)findViewById(R.id.tv_register_two_sure);
        tv_register_two_sure.setOnClickListener(this);
        tv_register_two_sure.getBackground().setAlpha(50);//0~255透明度值
        tv_register_two_sure.setTextColor(tv_register_two_sure.getTextColors().withAlpha(50));
        et_register_two_yanzhengma.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if(et_register_two_yanzhengma.getText().toString().equals("")){
                    tv_register_two_sure.getBackground().setAlpha(50);//0~255透明度值
                    tv_register_two_sure.setTextColor(tv_register_two_sure.getTextColors().withAlpha(50));
                }else{
                    tv_register_two_sure.getBackground().setAlpha(255);//0~255透明度值
                    tv_register_two_sure.setTextColor(tv_register_two_sure.getTextColors().withAlpha(255));
                }
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });


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
            json.put("PhoneNumber", WowuApp.PhoneNumber);
            json.put("Type", "0");
            json.put("SmsValidateCode", et_register_two_yanzhengma.getText().toString());
            loadDataService.loadPostJsonRequestData(WowuApp.JSON, WowuApp.ValidateCodeURL, json.toString(), R.id.tv_register_two_sure);
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
    public void loadServerData(String response, int flag) {
        MyToast.show(mContext, "返回的结果为：：：：" + response);
        Log.i("返回的结果为", response.toString());

        temp2 = new Intent(mContext, RegisterStepThreeActivity.class);
        mContext.startActivity(temp2);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void loadDataFailed(String response,int flag) {
        MyToast.show(mContext, "返回值失败" + response.toString());
        Log.i("返回值失败", response.toString());





    }
}

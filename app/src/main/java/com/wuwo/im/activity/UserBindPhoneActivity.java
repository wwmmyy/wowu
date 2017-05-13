package com.wuwo.im.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wuwo.im.config.WowuApp;
import com.wuwo.im.util.LogUtils;
import com.wuwo.im.util.MyToast;

import org.json.JSONObject;

import im.imxianzhi.com.imxianzhi.R;

public class UserBindPhoneActivity extends BaseLoadActivity {
    int currentStep = 1;
    LinearLayout ll_getpwd_step1, ll_getpwd_step2, ll_getpwd_step3, ll_getpwd_step4;
    EditText  et_my_number_step2,et_validate_code_step2, et_new_pwd_step3, et_new_pwd2_step3,et_bind_number_step3;
    TextView tv_bind_phonenum_step1,tv_bind_phonenum_step2,tv_bind_phonenum_start, top_title;

    String[] tiles = { "手机绑定","修改绑定手机号（1/3）", "修改绑定手机号（2/3）", "修改绑定手机号（3/3）"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_bind_phone);

        initView();
    }

    private void initView() {
        ll_getpwd_step1 = (LinearLayout) findViewById(R.id.ll_getpwd_step1);
        ll_getpwd_step2 = (LinearLayout) findViewById(R.id.ll_getpwd_step2);
        ll_getpwd_step3 = (LinearLayout) findViewById(R.id.ll_getpwd_step3);
        ll_getpwd_step4 = (LinearLayout) findViewById(R.id.ll_getpwd_step4);


        tv_bind_phonenum_step1 = (TextView) findViewById(R.id.tv_bind_phonenum_step1);
        tv_bind_phonenum_step2 = (TextView) findViewById(R.id.tv_bind_phonenum_step2);
        et_my_number_step2 = (EditText) findViewById(R.id.et_my_number_step2);

        et_validate_code_step2 = (EditText) findViewById(R.id.et_validate_code_step2);
        et_new_pwd_step3 = (EditText) findViewById(R.id.et_new_pwd_step3);
        et_new_pwd2_step3 = (EditText) findViewById(R.id.et_new_pwd2_step3);
        et_bind_number_step3= (EditText) findViewById(R.id.et_bind_number_step3);

        tv_bind_phonenum_step1.setText(WowuApp.PhoneNumber);
        tv_bind_phonenum_step2.setText(WowuApp.PhoneNumber);


        findViewById(R.id.return_back).setOnClickListener(this);
        findViewById(R.id.tv_bind_phonenum_start).setOnClickListener(this);
        findViewById(R.id.tv_forget_pwd).setOnClickListener(this);
        findViewById(R.id.tv_forget_pwd3).setOnClickListener(this);
        findViewById(R.id.tv_getpwd_step2).setOnClickListener(this);
        findViewById(R.id.tv_getpwd_step3).setOnClickListener(this);
        findViewById(R.id.tv_bind_step4).setOnClickListener(this);
        findViewById(R.id.tv_reget_validate_code).setOnClickListener(this);


        top_title = (TextView) findViewById(R.id.top_title);
        top_title.setText(tiles[0]);

    }


    public void setStep(int step) {
        switch (step) {
            case 1:
                ll_getpwd_step1.setVisibility(View.VISIBLE);
                ll_getpwd_step4.setVisibility(View.GONE);
                ll_getpwd_step2.setVisibility(View.GONE);
                ll_getpwd_step3.setVisibility(View.GONE);
                top_title.setText(tiles[0]);
                currentStep = 1;
                break;
            case 2:
                ll_getpwd_step1.setVisibility(View.GONE);
                ll_getpwd_step4.setVisibility(View.GONE);
                ll_getpwd_step2.setVisibility(View.VISIBLE);
                ll_getpwd_step3.setVisibility(View.GONE);
                top_title.setText(tiles[1]);
                currentStep = 2;
                break;
            case 3:
                ll_getpwd_step1.setVisibility(View.GONE);
                ll_getpwd_step4.setVisibility(View.GONE);
                ll_getpwd_step2.setVisibility(View.GONE);
                ll_getpwd_step3.setVisibility(View.VISIBLE);
                top_title.setText(tiles[2]);
                currentStep = 3;
                break;
            case 4:
                ll_getpwd_step1.setVisibility(View.GONE);
                ll_getpwd_step2.setVisibility(View.GONE);
                ll_getpwd_step3.setVisibility(View.GONE);
                ll_getpwd_step4.setVisibility(View.VISIBLE);
                top_title.setText(tiles[3]);
                currentStep = 4;
                break;
        }


    }

    Intent intent = new Intent();
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.tv_bind_phonenum_start:
                setStep(2);
                break;
            case R.id.tv_forget_pwd:
            case R.id.tv_forget_pwd3:
                intent.setClass(mContext, UserForgetPasswordActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
//            case R.id.tv_getpwd_step1://输入手机号，发送给服务器获取验证码
//                getValidateCode();
//                break;
            case R.id.tv_getpwd_step2://验证输入的手机密码
                if( et_my_number_step2.getText().toString().equals(WowuApp.Password)){
                    setStep(3);
                }else{
                    MyToast.show(mContext,"输入密码错误");
                }
                break;
            case R.id.tv_getpwd_step3: //绑定新的手机号
                setNewBindPhoneNum();
                break;
            case R.id.tv_reget_validate_code://重新获取验证码
                getValidateCode();
                break;
            case R.id.tv_set_pwd_finish:
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.return_back:
                if (currentStep == 1) {
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                } else {
                    currentStep--;
                    setStep(currentStep);
                }
                break;
        }
    }

//    private void ValidateCode() {
//        if(! et_validate_code_step2.getText().toString().equals("")) {
//            try {
//                JSONObject json = new JSONObject();
//                json.put("PhoneNumber", et_my_number_step1.getText().toString());
//                json.put("Type", "1");
//                json.put("SmsValidateCode", et_validate_code_step2.getText().toString());
//                loadDataService.loadPostJsonRequestData(WowuApp.JSON, WowuApp.ValidateCodeURL, json.toString(), R.id.tv_getpwd_step2);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }else{
//            MyToast.show(mContext, "输入验证码不能为空");
//        }
//    }

    /**
     * 此处将心绑定的手机号提交给服务器
     */
    private void setNewBindPhoneNum() {
             try {
                JSONObject json = new JSONObject();
                json.put("PhoneNumber",   et_bind_number_step3.getText().toString());
                json.put("SmsValidateCode", et_validate_code_step2.getText().toString());
                json.put("NewPassword", et_new_pwd_step3.getText().toString());
                loadDataService.loadPostJsonRequestData(WowuApp.JSON, WowuApp.FindPasswordURL, json.toString(), R.id.tv_getpwd_step3);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    private void getValidateCode() {
        if (!et_validate_code_step2.getText().toString().equals("")) {
            try {
                JSONObject json = new JSONObject();
                json.put("PhoneNumber", et_validate_code_step2.getText().toString());
                json.put("Type", "1");
                loadDataService.loadPostJsonRequestData(WowuApp.JSON, WowuApp.SmsValidateURL, json.toString(), R.id.tv_reget_validate_code);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            MyToast.show(mContext, "手机号不能为空");
        }
    }

    @Override
    public void loadServerData(String response, int flag) {

        switch (flag) {
            case R.id.tv_reget_validate_code://输入手机号，发送给服务器获取验证码
                setStep(2);
                break;
            case R.id.tv_getpwd_step2://确认验证码
                setStep(3);
                break;
            case R.id.tv_getpwd_step3://设置新密码
                setStep(4);
                break;
        }


    }

    @Override
    public void loadDataFailed(String response, int flag) {
        MyToast.show(mContext, "返回值失败" + response.toString());
        LogUtils.i("返回值失败", response.toString());
    }

}

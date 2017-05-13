package com.wuwo.im.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wuwo.im.config.WowuApp;
import com.wuwo.im.util.LogUtils;
import com.wuwo.im.util.MyToast;

import org.json.JSONObject;

import im.imxianzhi.com.imxianzhi.R;

public class UserForgetPasswordActivity extends BaseLoadActivity {
    int currentStep = 1;
    LinearLayout ll_getpwd_step1, ll_getpwd_step2, ll_getpwd_step3, ll_getpwd_step4;
    EditText et_my_number_step1, et_validate_code_step2, et_new_pwd_step3, et_new_pwd2_step3;
    TextView tv_getpwd_phonenum_step2, top_title,tv_reget_validate_code;
    private TimeCount timeCount;
    String[] tiles = {"找回密码（1/3）", "找回密码（2/3）", "找回密码（3/3）", "找回密码"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_forget_password);

        initView();
    }

    private void initView() {
        ll_getpwd_step1 = (LinearLayout) findViewById(R.id.ll_getpwd_step1);
        ll_getpwd_step2 = (LinearLayout) findViewById(R.id.ll_getpwd_step2);
        ll_getpwd_step3 = (LinearLayout) findViewById(R.id.ll_getpwd_step3);
        ll_getpwd_step4 = (LinearLayout) findViewById(R.id.ll_getpwd_step4);


        et_my_number_step1 = (EditText) findViewById(R.id.et_my_number_step1);
        tv_getpwd_phonenum_step2 = (TextView) findViewById(R.id.tv_getpwd_phonenum_step2);
        et_validate_code_step2 = (EditText) findViewById(R.id.et_validate_code_step2);
        et_new_pwd_step3 = (EditText) findViewById(R.id.et_new_pwd_step3);
        et_new_pwd2_step3 = (EditText) findViewById(R.id.et_new_pwd2_step3);
        tv_reget_validate_code = (TextView) findViewById(R.id.tv_reget_validate_code);

        findViewById(R.id.return_back).setOnClickListener(this);
        findViewById(R.id.tv_getpwd_step1).setOnClickListener(this);
        findViewById(R.id.tv_getpwd_step2).setOnClickListener(this);
        findViewById(R.id.tv_getpwd_step3).setOnClickListener(this);
        findViewById(R.id.tv_set_pwd_finish).setOnClickListener(this);
        findViewById(R.id.tv_reget_validate_code).setOnClickListener(this);


        top_title = (TextView) findViewById(R.id.top_title);
        top_title.setText(tiles[0]);
    }

    private void initData() {
        timeCount = new TimeCount(60000, 1000);
    }
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        public void onFinish() {
            tv_reget_validate_code.setText("获取验证码");
            tv_reget_validate_code.setClickable(true);
        }

        public void onTick(long millisUntilFinished) {
            tv_reget_validate_code.setClickable(false);
            tv_reget_validate_code.setText("重新获取(" + millisUntilFinished / 1000+")");
        }
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
                initData();
                if (tv_reget_validate_code.isClickable()) {
                    // TODO run your logic that you want to do
                    timeCount.start();
                }
                ll_getpwd_step1.setVisibility(View.GONE);
                ll_getpwd_step4.setVisibility(View.GONE);
                ll_getpwd_step2.setVisibility(View.VISIBLE);
                ll_getpwd_step3.setVisibility(View.GONE);
                top_title.setText(tiles[1]);
                tv_getpwd_phonenum_step2.setText(et_my_number_step1.getText().toString());
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


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.tv_getpwd_step1://输入手机号，发送给服务器获取验证码
                getValidateCode();
                break;
            case R.id.tv_getpwd_step2://确认验证码
                ValidateCode();
                break;
            case R.id.tv_getpwd_step3://设置新密码     //    POST找回密码{ "NewPassword": "sample string 1", "SmsValidateCode": "sample string 2","PhoneNumber":"sample string 3"}

                setNewPwd();
                break;
            case R.id.tv_reget_validate_code://重新获取验证码
//                if (tv_reget_validate_code.isClickable()) {
//                    // TODO run your logic that you want to do
//                    timeCount.start();
//                }
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

    private void ValidateCode() {
        if(! et_validate_code_step2.getText().toString().equals("")) {
            try {
                JSONObject json = new JSONObject();
                json.put("PhoneNumber", et_my_number_step1.getText().toString());
                json.put("Type", "1");
                json.put("SmsValidateCode", et_validate_code_step2.getText().toString());
                loadDataService.loadPostJsonRequestData(WowuApp.JSON, WowuApp.ValidateCodeURL, json.toString(), R.id.tv_getpwd_step2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            MyToast.show(mContext, "输入验证码不能为空");
        }
    }

    private void setNewPwd() {
        if (!et_new_pwd_step3.getText().toString().equals("") && et_new_pwd_step3.getText().toString().equals(et_new_pwd2_step3.getText().toString())) {
            try {
                JSONObject json = new JSONObject();
                json.put("PhoneNumber", et_my_number_step1.getText().toString());
                json.put("SmsValidateCode", et_validate_code_step2.getText().toString());
                json.put("NewPassword", et_new_pwd_step3.getText().toString());
                loadDataService.loadPostJsonRequestData(WowuApp.JSON, WowuApp.FindPasswordURL, json.toString(), R.id.tv_getpwd_step3);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            MyToast.show(mContext, "新密码不一致，请重新输入");
        }
    }

    private void getValidateCode() {
        if (!et_my_number_step1.getText().toString().equals("")) {
            try {
                JSONObject json = new JSONObject();
                json.put("PhoneNumber", et_my_number_step1.getText().toString());
                json.put("Type", "1");
                loadDataService.loadPostJsonRequestData(WowuApp.JSON, WowuApp.SmsValidateURL, json.toString(), R.id.tv_getpwd_step1);
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
            case R.id.tv_getpwd_step1://输入手机号，发送给服务器获取验证码
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



        MyToast.show(mContext,  response.toString()+";");


        LogUtils.i("返回值失败", response.toString());
    }

}

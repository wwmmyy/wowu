package com.wuwo.im.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wuwo.im.config.WowuApp;
import com.wuwo.im.util.MyToast;

import org.json.JSONObject;

import im.imxianzhi.com.imxianzhi.R;

public class UserModifyPasswdActivity extends BaseLoadActivity {

    int currentStep = 0;
    LinearLayout ll_modify_pwd, ll_modify_pwd_finish;
    EditText et_new_pwd, et_new_pwd2, et_old_pwd;
    TextView tv_set_pwd, top_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_modify_passwd);
        initView();
    }

    private void initView() {
        ll_modify_pwd = (LinearLayout) findViewById(R.id.ll_modify_pwd);
        ll_modify_pwd_finish = (LinearLayout) findViewById(R.id.ll_modify_pwd_finish);
        ll_modify_pwd.setOnClickListener(this);
        ll_modify_pwd_finish.setOnClickListener(this);

        et_old_pwd = (EditText) findViewById(R.id.et_old_pwd);
        et_new_pwd = (EditText) findViewById(R.id.et_new_pwd);
        et_new_pwd2 = (EditText) findViewById(R.id.et_new_pwd2);
        top_title = (TextView) findViewById(R.id.top_title);
        top_title.setText("密码修改");

        findViewById(R.id.tv_forget_pwd).setOnClickListener(this);
        findViewById(R.id.tv_save_pwd).setOnClickListener(this);
        findViewById(R.id.tv_set_pwd_finish).setOnClickListener(this);
        findViewById(R.id.return_back).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.tv_forget_pwd:
                Intent intent3 = new Intent();
                intent3.setClass(mContext, UserForgetPasswordActivity.class);
                startActivity(intent3);
                break;
            case R.id.tv_save_pwd:
//                这里应该是先连接网络，将修改密码返回给服务器后再显示下一个界面
                if(et_new_pwd.getText().toString().equals(et_new_pwd2.getText().toString())){
                    try {
                        JSONObject json = new JSONObject();
                        json.put("OldPassword",et_old_pwd.getText().toString());
                        json.put("NewPassword",et_new_pwd.getText().toString());
                        loadDataService.loadPostJsonRequestData(WowuApp.JSON, WowuApp.ChangePasswordURL, json.toString(), R.id.tv_save_pwd);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{
                    MyToast.show(mContext,"新密码不一致，请重新输入");
                }
//                currentStep=1;
//                setStep(currentStep);

                break;
            case R.id.tv_set_pwd_finish:
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.return_back:
                if(currentStep==1){
                    currentStep=0;
                    setStep(currentStep);
                }else{
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
                break;
        }

    }


    public void setStep(int step) {
        if (step == 0) {
            ll_modify_pwd.setVisibility(View.VISIBLE);
            ll_modify_pwd_finish.setVisibility(View.GONE);
        }else if(step == 1){
            ll_modify_pwd.setVisibility(View.GONE);
            ll_modify_pwd_finish.setVisibility(View.VISIBLE);
        }


    }

    @Override
    public void loadServerData(String response, int flag) {

        switch (flag){
            case R.id.tv_save_pwd:
                Log.e("获取到的返回结果为：：",response);
            currentStep=1;
            setStep(currentStep);
            break;
        }

    }

    @Override
    public void loadDataFailed(String response, int flag) {
        switch (flag){
            case R.id.tv_save_pwd:
                MyToast.show(mContext,response);
                break;
        }
    }
}

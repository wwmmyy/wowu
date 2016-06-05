package com.wuwo.im.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import com.wuwo.im.config.ExitApp;

import im.wuwo.com.wuwo.R;

public class LoginChooseActivity extends BaseActivity implements View.OnClickListener {
    private TextView tv_welcome_choose;
    Context mContext = LoginChooseActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_choose);
        ExitApp.getInstance().addOpenedActivity(this);
        initView();
    }

    private void initView() {


        findViewById(R.id.tv_welcome_register).setOnClickListener(this);
        findViewById(R.id.tv_welcome_sure).setOnClickListener(this);
        tv_welcome_choose = (TextView) this.findViewById(R.id.tv_welcome_choose);
        AlphaAnimation alp = new AlphaAnimation(0.0f, 1.0f);
        alp.setDuration(4000);
        tv_welcome_choose.setAnimation(alp);
        alp.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                tv_welcome_choose.setVisibility(View.VISIBLE);
            }
        });


    }

    Intent intent=null;
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tv_welcome_register:
                  intent = new Intent(mContext, RegisterStepOneActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.tv_welcome_sure:
                  intent = new Intent(mContext, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//                finish();
                break;
        }

    }
}

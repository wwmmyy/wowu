package com.wuwo.im.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.net.grandcentrix.tray.AppPreferences;

import im.imxianzhi.com.imxianzhi.R;

public class UserPaySuccessActivity extends BaseLoadActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_pay_success);
       int vipType = getIntent().getIntExtra("vipType", 1);


        AppPreferences appPreferences = new AppPreferences(mContext.getApplicationContext());
        appPreferences.put("IsVip",true);
        appPreferences.put("vipType",vipType);
        appPreferences.put("IsVip_state_changed",true);//增加这一个是为了，在vip状态变动后，附近的人页面获得状态刷新页面
//        appPreferences.put("IsVip",false);

        initView();

    }

    private void initView() {
        findViewById(R.id.return_back).setOnClickListener(this);
        findViewById(R.id.tv_set_pwd_finish).setOnClickListener(this);
        TextView top_title = (TextView) findViewById(R.id.top_title);
        top_title.setText("会员");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.tv_set_pwd_finish:
//                finish();
//                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                Bundle bundle = new Bundle();
                bundle.putBoolean("payState",true);//给 bundle 写入数据
                Intent mIntent = new Intent();
                mIntent.putExtras(bundle);
                setResult(RESULT_OK, mIntent);
                finish();//此处一定要调用finish()方法
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                break;
            case R.id.return_back:
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
        }
    }

    @Override
    public void loadServerData(String response, int flag) {

    }

    @Override
    public void loadDataFailed(String response, int flag) {

    }
}

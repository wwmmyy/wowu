package com.wuwo.im.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import im.wuwo.com.wuwo.R;

public class OwnerInfoUpdateTextActivity extends AppCompatActivity implements View.OnClickListener {
    TextView tv_item_title, tv_item_sure;
    EditText et_item_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_info_update_text);

        initView();

    }

    private void initView() {

        tv_item_title = (TextView) findViewById(R.id.tv_item_title);
        tv_item_sure = (TextView) findViewById(R.id.tv_item_sure);
        et_item_info = (EditText) findViewById(R.id.et_item_info);

        ((TextView) findViewById(R.id.top_title)).setText("资料编辑");
        findViewById(R.id.return_back).setOnClickListener(this);
        findViewById(R.id.tv_item_sure).setOnClickListener(this);



        Bundle b=getIntent().getExtras(); //data为B中回传的Intent
        tv_item_title.setText(b.getString("title"));
        et_item_info.setText(b.getString("info"));

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_item_sure:
                Bundle bundle = new Bundle();
                bundle.putString("info", et_item_info.getText().toString());//给 bundle 写入数据
                Intent mIntent = new Intent();
                mIntent.putExtras(bundle);
                setResult(RESULT_OK, mIntent);
                finish();//此处一定要调用finish()方法
                break;
            case R.id.return_back:
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;

        }
    }
}

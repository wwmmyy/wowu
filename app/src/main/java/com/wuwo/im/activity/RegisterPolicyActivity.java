package com.wuwo.im.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import im.imxianzhi.com.imxianzhi.R;
/** 
*desc
*@author 王明远
*@日期： 2016/6/9 0:08
*@版权:Copyright    All rights reserved.
*/

public class RegisterPolicyActivity extends BaseActivity implements OnClickListener {


    TextView tv_register_policy_titleone;
    TextView tv_register_policy_contentone;
    TextView tv_register_policy_title_two;
    TextView tv_register_policy_content_two;
    private int polcytye;


    String polcyContent[] = {"引言", "聚类科技重视用户的隐私，聚类科技重视用户的隐私，聚类科技重视用户的隐私，聚类科技重视用户的隐私，聚类科技重视用户的隐私，聚类科技重视用户的隐私，聚类科技重视用户的隐私，聚类科技重视用户的隐私，聚类科技重视用户的隐私，",
            "我们可能收集的信息",
            "我们提供服务时可能会收集信息，我们提供服务时可能会收集信息，我们提供服务时可能会收集信息，我们提供服务时可能会收集信息，我们提供服务时可能会收集信息，我们提供服务时可能会收集信息，我们提供服务时可能会收集信息，我们提供服务时可能会收集信息"};

    String tiaokuanContent[] = {"服务条款的确认和接纳",
            "本站及APP的各项内容，本站及APP的各项内容，本站及APP的各项内容，本站及APP的各项内容，本站及APP的各项内容，本站及APP的各项内容，本站及APP的各项内容，本站及APP的各项内容，本站及APP的各项内容，本站及APP的各项内容，",
            "用户同意",
            "（1）提供及时、详尽准确的用户资料\n（2）提供及时、详尽准确的用户资料提供及时、详尽准确的用户资料提供及时、详尽准确的用户资料\n（3）提供及时、详尽准确的用户资料提供及时、详尽准确的用户资料提供及时、详尽准确的用户资料\n"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_policy);
        polcytye = this.getIntent().getIntExtra("polcytye", 0);

        tv_register_policy_titleone = (TextView) findViewById(R.id.tv_register_policy_titleone);
        tv_register_policy_contentone = (TextView) findViewById(R.id.tv_register_policy_contentone);
        tv_register_policy_title_two = (TextView) findViewById(R.id.tv_register_policy_title_two);
        tv_register_policy_content_two = (TextView) findViewById(R.id.tv_register_policy_content_two);

        findViewById(R.id.return_back).setOnClickListener(this);


        initContent();
    }

    private void initContent() {
        if (polcytye == 0) {
            tv_register_policy_titleone.setText(polcyContent[0]);
            tv_register_policy_contentone.setText(polcyContent[1]);
            tv_register_policy_title_two.setText(polcyContent[2]);
            tv_register_policy_content_two.setText(polcyContent[3]);
            ( (TextView) findViewById(R.id.top_title)).setText("隐私权政策");

        } else {
            tv_register_policy_titleone.setText(tiaokuanContent[0]);
            tv_register_policy_contentone.setText(tiaokuanContent[1]);
            tv_register_policy_title_two.setText(tiaokuanContent[2]);
            tv_register_policy_content_two.setText(tiaokuanContent[3]);
            ( (TextView) findViewById(R.id.top_title)).setText("条款");
        }
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.return_back:
                this.finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
        }

    }
}

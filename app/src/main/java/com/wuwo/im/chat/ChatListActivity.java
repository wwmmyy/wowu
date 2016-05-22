package com.wuwo.im.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.wuwo.im.activity.BaseActivity;

import im.wuwo.com.wuwo.R;
/** 
*desc 聊天的对象列表
*@author 王明远
*@日期： 2016/5/10 23:23
*@版权:Copyright 上海数慧系统有限公司  All rights reserved.
*/

public class ChatListActivity extends BaseActivity implements OnClickListener {

    Context mcontext=ChatListActivity.this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO 自动生成的方法存根
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        //            WindowManager.LayoutParams.FLAG_FULLSCREEN);// 去掉信息栏
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat_list);

        ImageView back_news1 = (ImageView) findViewById(R.id.return_back0_sixinlist);
        LinearLayout sixinlist_item1 = (LinearLayout) findViewById(R.id.sixinlist_item1);
        LinearLayout sixinlist_item2 = (LinearLayout) findViewById(R.id.sixinlist_item2);
        LinearLayout sixinlist_item3 = (LinearLayout) findViewById(R.id.sixinlist_item3);
        sixinlist_item1.setOnClickListener(this);
        sixinlist_item2.setOnClickListener(this);
        sixinlist_item3.setOnClickListener(this);
        back_news1.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // TODO 自动生成的方法存根
        switch (v.getId()) {
        case R.id.sixinlist_item1:
        case R.id.sixinlist_item2:
        case R.id.sixinlist_item3:
            Intent intent2 = new Intent();
            intent2.setClass(mcontext, ChatedActivty.class);
            startActivity(intent2);

            break;
        case R.id.return_back0_sixinlist:
            ChatListActivity.this.finish();

            break;

        default:
            break;
        }
    }

}

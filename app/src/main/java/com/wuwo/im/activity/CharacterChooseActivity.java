package com.wuwo.im.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.wuwo.im.adapter.CommRecyclerAdapter;
import com.wuwo.im.adapter.CommRecyclerViewHolder;
import com.wuwo.im.chat.ChatListActivity;

import java.util.Arrays;

import im.wuwo.com.wuwo.R;
/**
*@author 王明远
*@日期： 2016/5/21 12:24
*@版权:Copyright    All rights reserved.
*/

public class CharacterChooseActivity extends BaseActivity {
    Context mContext=this;
    RecyclerView mRecyclerView;
    CommRecyclerAdapter messageRAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_choose);

        initTop();

        String[] data = { "Staggered", "LayoutManager", "GridLayout", "Adapter", "ViewHolder",
                "LinearLayout", "CardView", "ListView", "TextView",
                "Vertical", "Horizontal", "RecyclerView" };

        initAdapter();
        messageRAdapter.setData(Arrays.asList(data));

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);


//        //如果布局大小一致有利于优化
        mRecyclerView.setHasFixedSize(true);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext,4);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        //初始化适配器并绑定适配器
        mRecyclerView.setAdapter(messageRAdapter);
    }

    private void initTop() {
        ((TextView)findViewById(R.id.top_title)).setText("性格类型选择");
        findViewById(R.id.return_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharacterChooseActivity.this.finish();
            }
        });
        findViewById(R.id.bt_jingque).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(mContext, CharacterTestActivity.class);
                startActivity(intent2);
            }
        });

    }


    public void initAdapter() {
        messageRAdapter = new CommRecyclerAdapter<String>(mContext, R.layout.item_chacter_choose) {
            @Override
            public void convert(CommRecyclerViewHolder viewHolder, String mainMessage) {
                //对对应的View进行赋值
                viewHolder.setText(R.id.tv_choose, mainMessage);

//                SimpleDraweeView portal_news_img = (SimpleDraweeView) viewHolder.getView(R.id.news_label_pic);
//                portal_news_img.setImageURI(Uri.parse("http://www.gog.com.cn/pic/0/10/91/11/10911138_955870.jpg"));
            }

            @Override
            public int getItemViewType(int position) {
                return super.getItemViewType(position);
            }
        };
        //设置item点击事件
        messageRAdapter.setOnItemClick(new CommRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Intent intent2 = new Intent(mContext, ChatListActivity.class);
                //        intent2.putExtra("content", Stringlist.get(tempPosition-1).getContent());
//                intent2.putExtra("url", DistApp.serverAbsolutePath + "/snews!mobileNewsdetail.action?news.id=4028816f4d4be502014d4c0e22dc003d");
//                intent2.putExtra("name", "消息通知");
                startActivity(intent2);
            }
        });
    }
}

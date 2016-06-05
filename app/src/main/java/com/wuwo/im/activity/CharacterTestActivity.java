package com.wuwo.im.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.wuwo.im.adapter.CommRecyclerAdapter;
import com.wuwo.im.adapter.CommRecyclerViewHolder;
import com.wuwo.im.chat.ChatListActivity;
import com.wuwo.im.config.ExitApp;

import java.util.Arrays;

import im.wuwo.com.wuwo.R;

public class CharacterTestActivity extends AppCompatActivity {
    Context mContext=this;
    RecyclerView mRecyclerView;
    CommRecyclerAdapter messageRAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ExitApp.getInstance().addOpenedActivity(this);
        setContentView(R.layout.activity_character_test);
        initTop();

        String[] data = { "A.重视自我隐私的人", "B.非常坦率开放的人",
                "C.Vertical", "D.Horizontal", "E.RecyclerView" };

        initAdapter();
        messageRAdapter.setData(Arrays.asList(data));

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_question);

//        mRecyclerView.addItemDecoration(new RecycleViewDivider(mContext, LinearLayoutManager.VERTICAL));


////        //如果布局大小一致有利于优化
//        mRecyclerView.setHasFixedSize(true);

//        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext,4);
//        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        mRecyclerView.setLayoutManager(gridLayoutManager);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        //分割线
//        mRecyclerView.addItemDecoration(new RecycleViewDivider(mContext, LinearLayoutManager.HORIZONTAL));
        //初始化适配器并绑定适配器
        mRecyclerView.setAdapter(messageRAdapter);
    }

    private void initTop() {
        findViewById(R.id.return_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharacterTestActivity.this.finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        TextView tx_top_right= (TextView) findViewById(R.id.tx_top_right);
        tx_top_right.setVisibility(View.VISIBLE);
//        tx_top_right.setTextColor(getResources().getColor(R.color.white));
        tx_top_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(mContext, CharacterTResultActivity.class);
                startActivity(intent2);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });


    }


    public void initAdapter() {
        messageRAdapter = new CommRecyclerAdapter<String>(mContext, R.layout.item_chacter_test) {
            @Override
            public void convert(CommRecyclerViewHolder viewHolder, String mainMessage) {
                //对对应的View进行赋值
                viewHolder.setText(R.id.tv_test_choose, mainMessage);

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
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }
}


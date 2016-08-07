package com.wuwo.im.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wuwo.im.adapter.CommRecyclerAdapter;
import com.wuwo.im.adapter.CommRecyclerViewHolder;
import com.wuwo.im.bean.UserCharacter;
import com.wuwo.im.config.ExitApp;
import com.wuwo.im.config.WowuApp;
import com.wuwo.im.util.MyToast;
import com.wuwo.im.util.UtilsTool;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import im.wuwo.com.wuwo.R;

/**
 * @author 王明远
 * @日期： 2016/5/21 12:24
 * @版权:Copyright All rights reserved.
 */
public class CharacterChooseActivity extends BaseLoadActivity {
    Context mContext = this;
    RecyclerView mRecyclerView;
    CommRecyclerAdapter messageRAdapter;

    private ArrayList<UserCharacter> CharacterList = new ArrayList<UserCharacter>(); //记录所有的最新消息


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_choose);
        ExitApp.getInstance().addOpenedActivity(this);
        initTop();

        initAdapter();
        messageRAdapter.setData(CharacterList);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

//        //如果布局大小一致有利于优化
        mRecyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 4);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        //初始化适配器并绑定适配器
        mRecyclerView.setAdapter(messageRAdapter);

        pg = UtilsTool.initProgressDialog(mContext, "正在连接.....");
        pg.show();
        loadDataService.loadGetJsonRequestData(WowuApp.DispositionListURL, R.id.bt_jingque);
    }

    private void initTop() {
        ((TextView) findViewById(R.id.top_title)).setText("性格类型选择");
        findViewById(R.id.return_back).setOnClickListener(this);
        findViewById(R.id.return_back).setVisibility(View.GONE);//暂时将后退键隐藏掉
        findViewById(R.id.bt_jingque).setOnClickListener(this);
        findViewById(R.id.bt_kuaisu).setOnClickListener(this);
        findViewById(R.id.choose_sure).setOnClickListener(this);
    }


    public void initAdapter() {
        messageRAdapter = new CommRecyclerAdapter<UserCharacter>(mContext, R.layout.item_chacter_choose) {
            @Override
            public void convert(CommRecyclerViewHolder viewHolder, UserCharacter mainMessage) {
                //对对应的View进行赋值
                viewHolder.setText(R.id.tv_choose, mainMessage.getName());

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
                CommRecyclerViewHolder holder = (CommRecyclerViewHolder) view.getTag();

                if(lastView!=null){
                    lastView.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));
                    ((TextView)lastView).setTextColor(mContext.getResources().getColor(R.color.white));
                }
                lastView= holder.getView(R.id.tv_choose);
                lastView.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                ((TextView)lastView).setTextColor(mContext.getResources().getColor(R.color.colorPrimary));

                slelectedCharacter=CharacterList.get(position);
            }
        });
    }


    View lastView=null;
    UserCharacter  slelectedCharacter=new UserCharacter();

    public static final int JINGJIAN = 1;
    public static final int JINGQUE = 2;
    Intent intent2 = null;

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.bt_jingque:
                intent2 = new Intent(mContext, CharacterTestActivity.class);
                intent2.putExtra("tetsType", JINGQUE);
                startActivity(intent2);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.bt_kuaisu:
                intent2 = new Intent(mContext, CharacterTestActivity.class);
                intent2.putExtra("tetsType", JINGJIAN);
                startActivity(intent2);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.return_back:
                CharacterChooseActivity.this.finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.choose_sure:

                Message msg = Message.obtain();
                msg.what = Loading;
                mHandler.sendMessage(msg);

                     try{
                        JSONObject json = new JSONObject();
                        json.put("dispositionId",slelectedCharacter.getId());
                        loadDataService.loadPostJsonRequestData(WowuApp.JSON,WowuApp.SetDispositionURL+"?dispositionId="+slelectedCharacter.getId(),json.toString(),R.id.choose_sure);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                break;


        }
    }

    @Override
    public void loadServerData(String response, int flag) {
        switch (flag) {

//            [{"Name":"ISFJ","Title":"保护者","Description":"SJ护卫者","Qualities":null,"DisplayIndex":16,"Id":"6951e3af-2660-42de-8b96-e00a78e73c87"},
            case R.id.bt_jingque:
                Gson gson = new GsonBuilder().create();
                if (response != null) {
                    java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<List<UserCharacter>>() {
                    }.getType();
                    CharacterList = gson.fromJson(response, type);
                    Message msg = Message.obtain();
                    msg.what = END;
                    mHandler.sendMessage(msg);
                }
                break;
            case R.id.choose_sure:
                if (pg != null) pg.dismiss();
                    intent2 = new Intent(mContext, MainActivity.class);
                    startActivity(intent2);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                break;
        }
        Log.d("返回的结果为：：：：", response);
    }

    @Override
    public void loadDataFailed(String response, int flag) {
        MyToast.show(mContext, "连接失败" + response.toString());
        if (pg != null) pg.dismiss();
    }


    private ProgressDialog pg;
    private final int Loading = 1;
    private final int END = 2;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Loading:
                    pg = UtilsTool.initProgressDialog(mContext, "正在连接.....");
                    pg.show();
                    break;
                case END:
                    if (pg != null) pg.dismiss();
                    messageRAdapter.clearDate();
                    messageRAdapter.setData(CharacterList);
                    if(CharacterList!=null &&CharacterList.size()>0){
                        slelectedCharacter=CharacterList.get(0);
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };
}

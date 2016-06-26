package com.wuwo.im.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wuwo.im.adapter.CommRecyclerAdapter;
import com.wuwo.im.adapter.CommRecyclerViewHolder;
import com.wuwo.im.bean.LocalUser;
import com.wuwo.im.config.WowuApp;
import com.wuwo.im.view.PullLoadMoreRecyclerView;
import com.wuwo.im.view.SearchView;

import org.json.JSONException;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import im.wuwo.com.wuwo.R;
/**
 *desc OwnerInfoEditActivity
 *@author 王明远
 *@日期： 2016/6/9 0:08
 *@版权:Copyright    All rights reserved.  http://xzxj.oss-cn-shanghai.aliyuncs.com/user/7fe11e87-cd32-4684-8220-73c8bfc98431.jpg
 */

public class OwnerInfoEditActivity extends BaseLoadActivity  {

    Gson gson = new GsonBuilder().create();
    SharedPreferences mSettings;
    SharedPreferences.Editor editor;
    private PullLoadMoreRecyclerView mPullLoadMoreRecyclerView;
    private int mCount = 0;
    ArrayList<LocalUser.DataBean> meeting_userlist = new ArrayList<LocalUser.DataBean>(); //记录所有的最新消息
    private SearchView search_view;
    private String searchinfo;
    CommRecyclerAdapter messageRAdapter;

    mHandlerWeak mtotalHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_info_edit);
        initView();

    }

    private void initView() {
        findViewById(R.id.return_back).setOnClickListener(this);
        mPullLoadMoreRecyclerView = (PullLoadMoreRecyclerView)  findViewById(R.id.pullLoadMoreRecyclerView);
        mPullLoadMoreRecyclerView.setRefreshing(false);
        mPullLoadMoreRecyclerView.setIsLoadMore(false);
        mPullLoadMoreRecyclerView.setHorizontalLinearLayout();//
        mtotalHandler = new mHandlerWeak(this);

        loadData();
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

    private final int LOAD_DATA=1;
    //    从网络加载流转日志数据并展示出来
    private void loadData() {
        loadDataService.loadGetJsonRequestData( WowuApp.GetFriendsURL+"?lon=" + WowuApp.longitude + "&lat=" + WowuApp.latitude+ "&userId=" + "437ca552-ca12-4542-98e0-b2011399b849"+ "&PhoneNumber="+WowuApp.PhoneNumber ,LOAD_DATA);
    }


    public static final int DOWNLOADED_LocalUser= 0;
    public static final int DOWNLOADED_ERROR = 1;



    @Override
    public void loadServerData(String response, int flag) {
        switch (flag){
            case LOAD_DATA:
                try {
                    if (response != null) {
                        setLoadInfo(response);
                    }
                    Message msg = new Message();
                    msg.what = DOWNLOADED_LocalUser;
                    mtotalHandler.sendMessage(msg);
                } catch (Exception e) {
//                                    e.printStackTrace();
                    Message msg = new Message();
                    msg.what = DOWNLOADED_ERROR;
                    mtotalHandler.sendMessage(msg);
                }
                break;
        }



    }

    @Override
    public void loadDataFailed(String response, int flag) {

        switch (flag){
            case LOAD_DATA:
                Message msg = new Message();
                msg.what = DOWNLOADED_ERROR;
                mtotalHandler.sendMessage(msg);
                break;
        }
    }






    private static class mHandlerWeak extends Handler {
        private WeakReference<OwnerInfoEditActivity> activity = null;

        public mHandlerWeak(OwnerInfoEditActivity act) {
            super();
            this.activity = new WeakReference<OwnerInfoEditActivity>(act);
        }

        @Override
        public void handleMessage(Message msg) {
            OwnerInfoEditActivity act = activity.get();
            if (null == act) {
                return;
            }
            switch (msg.what) {
                // 正在下载
                case DOWNLOADED_LocalUser:
                    if (act.messageRAdapter == null) {
                        act.initAdapter();
                        act.messageRAdapter.setData( act.getLoadInfo());
                        act.messageRAdapter.setData( act.getLoadInfo());
                        act.messageRAdapter.setData( act.getLoadInfo());
                        act.messageRAdapter.setData( act.getLoadInfo());
                        act.messageRAdapter.setData( act.getLoadInfo());
                        act.messageRAdapter.setData( act.getLoadInfo());

                        act.mPullLoadMoreRecyclerView.setAdapter(act.messageRAdapter);
                    } else {
                        act.messageRAdapter.setData(act.getLoadInfo());
                    }
                    act.mPullLoadMoreRecyclerView.setRefreshing(false);
                    act.mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();
                    break;
                case DOWNLOADED_ERROR:
//                    MyToast.show(mContext, "服务器返回值异常", Toast.LENGTH_LONG);
                    act.mPullLoadMoreRecyclerView.setRefreshing(false);
                    break;
            }
        }
    }


    public ArrayList<?> getLoadInfo() {
        return meeting_userlist;
    }


    public void setLoadInfo(String totalresult) throws JSONException {
        Gson gson = new GsonBuilder().create();
        if (totalresult != null) {
            java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<List<LocalUser.DataBean>>() {
            }.getType();
            meeting_userlist = gson.fromJson(totalresult, type);
        }

    }

    public CommRecyclerAdapter initAdapter() {
        messageRAdapter = new CommRecyclerAdapter<LocalUser.DataBean>(mContext, R.layout.item_userpic_recycler) {
            @Override
            public void convert(CommRecyclerViewHolder viewHolder, LocalUser.DataBean mainMessage) {
                SimpleDraweeView portal_news_img = (SimpleDraweeView) viewHolder.getView(R.id.user_info_pic);
//                portal_news_img.setImageURI(Uri.parse(mainMessage.getPhotoUrl()));
                portal_news_img.setImageURI(Uri.parse("http://xzxj.oss-cn-shanghai.aliyuncs.com/user/7fe11e87-cd32-4684-8220-73c8bfc98431.jpg"));

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
                Intent intent2 = new Intent(mContext, OwnerInfoEditActivity.class);
//                //        intent2.putExtra("content", LocalUser.DataBeanlist.get(tempPosition-1).getContent());
//                intent2.putExtra("url", DistApp.serverAbsolutePath + "/snews!mobileNewsdetail.action?news.id=4028816f4d4be502014d4c0e22dc003d");
//                intent2.putExtra("name", "消息通知");
                startActivity(intent2);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        return null;
    }
}

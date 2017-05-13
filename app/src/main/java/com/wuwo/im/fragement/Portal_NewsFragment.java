package com.wuwo.im.fragement;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wuwo.im.adapter.CommRecyclerAdapter;
import com.wuwo.im.adapter.CommRecyclerViewHolder;
import com.wuwo.im.bean.newsMessage;
import com.wuwo.im.config.WowuApp;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import im.imxianzhi.com.imxianzhi.R;

/** 
*desc 最新消息记录，此部分后面会用环信的代替
*@author 王明远
*@日期： 2016/6/25 12:22
*@版权:Copyright 上海数慧系统有限公司  All rights reserved.
*/

@SuppressLint("ValidFragment")
public class Portal_NewsFragment extends BasePortal_TabFragment {

    private ArrayList<newsMessage> meeting_userlist = new ArrayList<newsMessage>(); //记录所有的最新消息
    @Override
    public void setLoadInfo(String totalresult) throws JSONException {

        Gson gson = new GsonBuilder().create();
        if (totalresult != null) {
            java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<List<newsMessage>>() {
            }.getType();
            meeting_userlist = gson.fromJson(totalresult, type);
        }
    }

    @Override
    public ArrayList<?> getLoadInfo() {
        ArrayList<newsMessage> temp = (ArrayList<newsMessage>) meeting_userlist.clone();
        return temp;
    }

    @Override
    public CommRecyclerAdapter initAdapter() {
        messageRAdapter = new CommRecyclerAdapter<newsMessage>(getActivity(), R.layout.item_recycler_cardview) {
            @Override
            public void convert(CommRecyclerViewHolder viewHolder, newsMessage mainMessage) {
                //对对应的View进行赋值
//                viewHolder.setText(R.id.portal_news_title, mainMessage.getTitle());

                SimpleDraweeView portal_news_img = (SimpleDraweeView) viewHolder.getView(R.id.portal_news_img);
                portal_news_img.setImageURI(Uri.parse("http://www.gog.com.cn/pic/0/10/91/11/10911138_955870.jpg"));
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


//                Intent intent2 = new Intent(mContext, NewsOneDetailActivity.class);
//                //        intent2.putExtra("content", newsMessagelist.get(tempPosition-1).getContent());
//                intent2.putExtra("url", DistApp.serverAbsolutePath + "/snews!mobileNewsdetail.action?news.id=4028816f4d4be502014d4c10c40d0042");
//                intent2.putExtra("name", "新闻");
//                startActivity(intent2);
            }
        });
        return null;
    }

    @Override
    public JSONObject postJsonObject() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("PhoneNumber", WowuApp.PhoneNumber);
        json.put("Type", "0");
        return json;
    }

    @Override
    public String postURL() {
        return null;
    }

    @Override
    public String getURL() {
        return OkHttpUtils.GetUserInfoURL+"?userId="+WowuApp.UserId;
    }

    @Override
    public boolean loadMore() {
        return true;
    }

    @Override
    public String getFragmentName() {
        return "Portal_Local1Fragment";
    }


}

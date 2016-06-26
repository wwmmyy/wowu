package com.wuwo.im.fragement;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wuwo.im.activity.UserInfoEditActivity;
import com.wuwo.im.adapter.CommRecyclerAdapter;
import com.wuwo.im.adapter.CommRecyclerViewHolder;
import com.wuwo.im.bean.LocalUser;
import com.wuwo.im.config.WowuApp;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import im.wuwo.com.wuwo.R;

/**
 *  {"Data":[{"UserId":"437ca552-ca12-4542-98e0-b2011399b849","Name":"ran","Disposition":"ESTP创业者","Description":null,"Age":0,"Gender":1,"Distance":"7074.1km","Before":"19小时","PhotoUrl":"http://xzxj.oss-cn-shanghai.aliyuncs.com/user/cd5b98f6-439b-4e8c-be3f-a9cd437a34be.jpg","EasemobId":"296fb1da-338d-11e6-8c1d-87ddb1f2ae6f"},{"UserId":"a3a58155-34e2-4c6c-8201-135b830411dd","Name":"today","Disposition":"ISFP创作者","Description":null,"Age":0,"Gender":0,"Distance":"7074.1km","Before":"10分钟","PhotoUrl":"http://xzxj.oss-cn-shanghai.aliyuncs.com/user/66e1b7e7-ea6c-4635-86ce-d62c43162431.jpg","EasemobId":"d9a27a4a-32cc-11e6-88d3-0bca4779fbf6"}],"Total":4,"PageCount":1}
 *desc  附件的人
*@author 王明远
*@日期： 2016/6/25 12:04
*@版权:Copyright 上海数慧系统有限公司  All rights reserved.
*/

@SuppressLint("ValidFragment")
public class Portal_LocalFragment extends BasePortal_TabFragment {

    private ArrayList<LocalUser.DataBean> meeting_userlist = new ArrayList<LocalUser.DataBean>(); //记录所有的最新消息
    @Override
    public void setLoadInfo(String totalresult) throws JSONException {


        Gson gson = new GsonBuilder().create();
        if (totalresult != null) {
//            java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<List<DataBean>>() {
            java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<LocalUser>() {
            }.getType();

            LocalUser  temp = gson.fromJson(totalresult, type);
            meeting_userlist=  temp.getData();
//            meeting_userlist = gson.fromJson(totalresult, type);
        }

//        if (totalresult != null) {
//            //                    int size = result.getPropertyCount();
//            JSONObject obj = new JSONObject(totalresult);
//            if (obj.optBoolean("success")) {
//                meeting_userlist.clear();
//                String result = obj.optString("result");
//                //Log.d("收文获得的返回值列表为：",result + "");
//                if (result != null) {
//                    JSONArray attachlist = new JSONArray(result);
//                    for (int i = 0; i < attachlist.length(); i++) {
//                        JSONObject attachjson = attachlist.getJSONObject(i);
//                        DataBean tempmail = new DataBean();
//                        tempmail.setId(attachjson.optString("id"));
//                        tempmail.setTitle(attachjson.optString("title"));
//                        tempmail.setContent(attachjson.optString("name"));
//                        tempmail.setTime0(attachjson.optString("publishdate"));
//                        tempmail.setCreater(attachjson.optString("typename"));
//                        tempmail.setProjectId(attachjson.optString("id"));
//                        tempmail.setWidgetsLabel(attachjson.optString("typename"));
//                        tempmail.setShowurl(attachjson.optString("extension"));
//                        meeting_userlist.add(tempmail);
//                    }
//                }
//            }
//        }
    }

    @Override
    public ArrayList<?> getLoadInfo() {
//        ArrayList<DataBean> temp = (ArrayList<DataBean>) meeting_userlist.clone();
//        return temp;

        return meeting_userlist;
    }

    @Override
    public CommRecyclerAdapter initAdapter() {
        messageRAdapter = new CommRecyclerAdapter<LocalUser.DataBean>(getActivity(), R.layout.item_local_view) {
            @Override
            public void convert(CommRecyclerViewHolder viewHolder, LocalUser.DataBean mainMessage) {
                //对对应的View进行赋值
                viewHolder.setText(R.id.title, mainMessage.getName());



                viewHolder.setText(R.id.project_code, mainMessage.getDistance()+" | "+ mainMessage.getBefore());
                viewHolder.setText(R.id.yewu_type, mainMessage.getDisposition());
                viewHolder.setText(R.id.yewu_type, mainMessage.getDisposition());

                SimpleDraweeView portal_news_img = (SimpleDraweeView) viewHolder.getView(R.id.news_label_pic);
                portal_news_img.setImageURI(Uri.parse(mainMessage.getPhotoUrl()));
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


                Intent intent2 = new Intent(mContext, UserInfoEditActivity.class);
                //        intent2.putExtra("content", DataBeanlist.get(tempPosition-1).getContent());
//                intent2.putExtra("url", DistApp.serverAbsolutePath + "/snews!mobileNewsdetail.action?news.id=4028816f4d4be502014d4c0e22dc003d");
//                intent2.putExtra("name", "消息通知");
                startActivity(intent2);
                mContext.overridePendingTransition(0, 0);
            }
        });
        return null;
    }

/*    @Override
    public PostFormBuilder httpBuilder() {
        return  OkHttpUtils
                .post()
//                .addHeader("content-type", "application/json;")
//                .addHeader("Content-Disposition", "application/json;")
//                .url(WowuApp.serverAbsolutePath)
                .url("http://58.246.138.178:8000/DistMobile/mobileMeeting!getAllMeeting.action")
                .addParams("type", "smartplan")
                .addParams("action", "getlawrulelist");
    }*/


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
        return WowuApp.GetNearbyUserURL+"?lon=" + WowuApp.longitude + "&lat=" + WowuApp.latitude;

//        return WowuApp.GetNearbyUserURL+"?lon=31.196694&lat=121.716728";
    }

    @Override
    public boolean loadMore() { 
        return true;
    }

    @Override
    public String getFragmentName() {
        return "Portal_LocalFragment";
    }


}

package com.wuwo.im.fragement;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;

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
        }

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
                TextView genderm= (TextView) viewHolder.getView(R.id.tvage_gender_male);
                TextView genderw= (TextView) viewHolder.getView(R.id.tvage_gender_female);
                if(mainMessage.getGender()==0){
                    genderm.setVisibility(View.VISIBLE);
                    genderw.setVisibility(View.GONE);
                    genderm.setText(mainMessage.getAge()+"");
                }else{
                    genderm.setVisibility(View.GONE);
                    genderw.setVisibility(View.VISIBLE);
                    genderw.setText(mainMessage.getAge()+"");
                }




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
//                CommRecyclerViewHolder holder = (CommRecyclerViewHolder) view.getTag();
//                holder.getView(R.id.tv_choose);
                Intent intent2 = new Intent(mContext, UserInfoEditActivity.class);
                intent2.putExtra("localUser", (LocalUser.DataBean)(messageRAdapter.getData().get(position)));
                startActivity(intent2);
                mContext.overridePendingTransition(0, 0);
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
        return WowuApp.GetNearbyUserURL;//+"?lon=" + WowuApp.longitude + "&lat=" + WowuApp.latitude
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

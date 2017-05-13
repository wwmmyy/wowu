package com.wuwo.im.fragement;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hyphenate.chatuidemo.db.DemoDBManager;
import com.hyphenate.chatuidemo.db.UserDao;
import com.hyphenate.easeui.utils.SharedPreferencesUtil;
import com.net.grandcentrix.tray.AppPreferences;
import com.wuwo.im.activity.UserInfoEditActivity;
import com.wuwo.im.adapter.CommRecyclerAdapter;
import com.wuwo.im.adapter.CommRecyclerViewHolder;
import com.wuwo.im.bean.LocalUser;
import com.wuwo.im.config.WowuApp;
import com.wuwo.im.util.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import im.imxianzhi.com.imxianzhi.R;

/**
 * {"Data":[{"UserId":"437ca552-ca12-4542-98e0-b2011399b849","Name":"ran","Disposition":"ESTP创业者","Description":null,"Age":0,"Gender":1,"Distance":"7074.1km","Before":"19小时","PhotoUrl":"http://xzxj.oss-cn-shanghai.aliyuncs.com/user/cd5b98f6-439b-4e8c-be3f-a9cd437a34be.jpg","EasemobId":"296fb1da-338d-11e6-8c1d-87ddb1f2ae6f"},{"UserId":"a3a58155-34e2-4c6c-8201-135b830411dd","Name":"today","Disposition":"ISFP创作者","Description":null,"Age":0,"Gender":0,"Distance":"7074.1km","Before":"10分钟","PhotoUrl":"http://xzxj.oss-cn-shanghai.aliyuncs.com/user/66e1b7e7-ea6c-4635-86ce-d62c43162431.jpg","EasemobId":"d9a27a4a-32cc-11e6-88d3-0bca4779fbf6"}],"Total":4,"PageCount":1}
 * desc  附件的人
 *
 * @author 王明远
 * @日期： 2016/6/25 12:04
 * @版权:Copyright 上海数慧系统有限公司  All rights reserved.
 */

@SuppressLint("ValidFragment")
public class Portal_LocalFragment extends BasePortal_TabFragment {

    private List<LocalUser.DataBean> local_userlist = new ArrayList<LocalUser.DataBean>(); //记录所有的最新消息

    @Override
    public void setLoadInfo(final String totalresult) throws JSONException {

        Gson gson = new GsonBuilder().create();
//            java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<List<DataBean>>() {
        java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<LocalUser>() {
        }.getType();

        LogUtils.i("Portal_LocalFragment:", totalresult + " ;");

        if (totalresult != null) {
            LocalUser temp = gson.fromJson(totalresult, type);
            local_userlist = temp.getData();
//          缓存到数据库
            new Thread(new Runnable() {
                @Override
                public void run() {
                    DemoDBManager.getInstance().saveCacheJson(UserDao.CACHE_MAIN_LOCAL, totalresult);
                    LogUtils.i("Portal_LocalFragment:", "保存数据库缓存");

                    try {
                        for (LocalUser.DataBean mainMessage : local_userlist) {
                            if (mainMessage.getUserId() != null) {
                                SharedPreferencesUtil.saveData(getActivity(), SharedPreferencesUtil.usersNickName, mainMessage.getUserId(), mainMessage.getRemarkName() == null ? mainMessage.getName() : mainMessage.getRemarkName());
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }).start();
            ;
        } else {
            if (mCount == 0) {//说明不是下拉更多请求
//            读取缓存信息
                String CacheJsonString = DemoDBManager.getInstance().getCacheJson(UserDao.CACHE_MAIN_LOCAL);
                LocalUser temp = gson.fromJson(CacheJsonString, type);
                local_userlist = temp.getData();
                LogUtils.i("Portal_LocalFragment:", "获取数据库缓存" + CacheJsonString.length() + "内容条数：" + local_userlist.size());
            } else {
                local_userlist.clear();
            }
        }
    }

//    @Override
//    public void onResume() {
//        LogUtils.i("Portal_LocalFragment:","onResume()");
//        //      当用户刷新数据时，更新定位频率
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Intent intentLocation = new Intent(WowuApp.RESET_LOCATION_TIME);
//                intentLocation.putExtra("locationNoChangLength", 3 * 1000 + "");
//                mContext.sendBroadcast(intentLocation);
//            }
//        }).start();
//        super.onResume();
//    }


//    @Override
//    public void onStop() {
//        super.onStop();
//
//        LogUtils.i("MainActivity:","onPause()");
//        //      当用户刷新数据时，更新定位频率  当用户切换到桌面等时，将更新频率延长为10分钟
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Intent intentLocation = new Intent(WowuApp.RESET_LOCATION_TIME);
//                intentLocation.putExtra("locationNoChangLength", 10*60 * 1000 + "");
//                mContext.sendBroadcast(intentLocation);
//            }
//        }).start();
//
//    }


    @Override
    public ArrayList<?> getLoadInfo() {
//        ArrayList<DataBean> temp = (ArrayList<DataBean>) local_userlist.clone();
//        return temp;

        return (ArrayList<?>) local_userlist;
    }

    @Override
    public CommRecyclerAdapter initAdapter() {
        messageRAdapter = new CommRecyclerAdapter<LocalUser.DataBean>(getActivity(), R.layout.item_local_view) {
            @Override
            public void convert(CommRecyclerViewHolder viewHolder, LocalUser.DataBean mainMessage) {
                //对对应的View进行赋值
                viewHolder.setText(R.id.title, mainMessage.getRemarkName() != null ? mainMessage.getRemarkName() : mainMessage.getName());
                viewHolder.setText(R.id.project_code, mainMessage.getDistance());//+" | "+ mainMessage.getBefore()
                viewHolder.setText(R.id.yewu_type, mainMessage.getAge() == 0 ? mainMessage.getDisposition() : mainMessage.getAge() + " | " + mainMessage.getDisposition());
                viewHolder.setText(R.id.tv_descri, mainMessage.getDescription());


//                SharedPreferencesUtil.saveData(getActivity(),SharedPreferencesUtil.usersNickName,mainMessage.getUserId(),mainMessage.getRemarkName() == null? mainMessage.getName():mainMessage.getRemarkName()) ;

//                TextView genderm= (TextView) viewHolder.getView(R.id.tvage_gender_male);
//                TextView genderw= (TextView) viewHolder.getView(R.id.tvage_gender_female);
                ImageView yewu_code_m = (ImageView) viewHolder.getView(R.id.yewu_code_m);
                ImageView yewu_code_w = (ImageView) viewHolder.getView(R.id.yewu_code_w);

                ImageView tv_isvip = (ImageView) viewHolder.getView(R.id.tv_isvip);
                if (mainMessage.isIsVip()) {
                    tv_isvip.setVisibility(View.VISIBLE);
                    if (mainMessage.getVipType() == 5) {
                        tv_isvip.setImageResource(R.drawable.svip);
                    } else {
                        tv_isvip.setImageResource(R.drawable.vip);
                    }
                } else {
                    tv_isvip.setVisibility(View.GONE);
//                    tv_isvip.setImageResource(R.drawable.no_vip);
                }

                SimpleDraweeView portal_news_img = (SimpleDraweeView) viewHolder.getView(R.id.news_label_pic);
                if (mainMessage.getPhotoUrl() != null) {
                    portal_news_img.setImageURI(Uri.parse(mainMessage.getPhotoUrl()));
                }
                GenericDraweeHierarchy hierarchy = portal_news_img.getHierarchy();


                if (mainMessage.getGender() == 1) {
//                    genderm.setVisibility(View.VISIBLE);
//                    genderw.setVisibility(View.GONE);
//                    genderm.setText(mainMessage.getAge()+"");
                    yewu_code_m.setVisibility(View.VISIBLE);
                    yewu_code_w.setVisibility(View.GONE);
//                    hierarchy.setControllerOverlay(getResources().getDrawable(R.drawable.overlay_male));
                    hierarchy.setControllerOverlay(getResources().getDrawable(R.drawable.overlay_male1));

                } else {
//                    genderm.setVisibility(View.GONE);
//                    genderw.setVisibility(View.VISIBLE);
//                    genderw.setText(mainMessage.getAge()+"");
                    yewu_code_m.setVisibility(View.GONE);
                    yewu_code_w.setVisibility(View.VISIBLE);
                    hierarchy.setControllerOverlay(getResources().getDrawable(R.drawable.overlay_fmale1));
                }
                final String userId = mainMessage.getUserId();
                yewu_code_w.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadSanguan(userId);
                    }
                });
                yewu_code_m.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadSanguan(userId);
                    }
                });

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
                intent2.putExtra("localUser", (LocalUser.DataBean) (messageRAdapter.getData().get(position)));
                startActivity(intent2);
                mContext.overridePendingTransition(0, 0);
            }
        });
        return null;
    }

    private boolean refreshState = false;
    @Override
    public void onResume() {
        super.onResume();
        if (refreshState) {
            AppPreferences appPreferences = new AppPreferences(mContext.getApplicationContext());
//如果用户性格类型改变需要刷新用户性格
            if (appPreferences.getBoolean("characterChanged", false) || appPreferences.getBoolean("IsVip_state_changed", false)) {
                appPreferences.put("characterChanged", false);
                appPreferences.put("IsVip_state_changed", false);
                refresh();
            }
            refreshState = false;
        }
    }

    @Override
    public void onPause() {
        refreshState = true;
        super.onPause();
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

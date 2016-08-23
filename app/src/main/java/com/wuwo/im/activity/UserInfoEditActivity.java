package com.wuwo.im.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chatuidemo.ui.ChatActivity;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.ui.EaseShowBigImageActivity;
import com.hyphenate.easeui.widget.EaseAlertDialog;
import com.wuwo.im.adapter.CommRecyclerAdapter;
import com.wuwo.im.adapter.CommRecyclerViewHolder;
import com.wuwo.im.bean.LocalUser;
import com.wuwo.im.bean.UserInfoDetail;
import com.wuwo.im.config.WowuApp;
import com.wuwo.im.util.MyToast;
import com.wuwo.im.util.UtilsTool;
import com.wuwo.im.view.SearchView;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import im.wuwo.com.wuwo.R;

/**
 * desc UserInfoEditActivity
 *
 * @author 王明远
 * @日期： 2016/6/9 0:08
 * @版权:Copyright All rights reserved.  http://xzxj.oss-cn-shanghai.aliyuncs.com/user/7fe11e87-cd32-4684-8220-73c8bfc98431.jpg
 */

public class UserInfoEditActivity extends BaseLoadActivity {
    Gson gson = new GsonBuilder().create();
    SharedPreferences mSettings;
    SharedPreferences.Editor editor;
    private RecyclerView mPicRecyclerView;
    private int mCount = 0;
    //    ArrayList<UserInfoDetail> meeting_userlist = new ArrayList<UserInfoDetail>(); //记录所有的最新消息
    private SearchView search_view;
    private String searchinfo;
    CommRecyclerAdapter userPicAdapter;

    mHandlerWeak mtotalHandler;

    RadioButton rb_guanzhu;
    UserInfoDetail mUserDetail = new UserInfoDetail();
    ArrayList<UserInfoDetail> mUserDetailList = new ArrayList<UserInfoDetail>(); //记录所有的最新消息


    RecyclerView rlist_view_content;
    CommRecyclerAdapter contentRAdapter;

    //    {"UserId":"28cf1590d726489f8b0fade0270ea951","Name":"啦啦啦","Disposition":"ISTJ检查者","Description":null,"Age":0,"Gender":1,"Distance":"11847.1km","Before":"2天","PhotoUrl":"http://xzxj.oss-cn-shanghai.aliyuncs.com/user/6b6283b3-d3bb-402b-8e31-732fd1c9c5e1.jpg","EasemobId":"c14dd04a-375c-11e6-9fdd-afd893d9f6d1"}
    private LocalUser.DataBean mLocalUser;
    private String  userType ;//用来标识当user从联系人跳转过来时，底部按钮不现实关注”
    private String  chatUserId ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info_edit);
        if (getIntent() != null) {
            mLocalUser = (LocalUser.DataBean) getIntent().getSerializableExtra("localUser");
            userType =  getIntent().getStringExtra("userType");

            chatUserId =  getIntent().getStringExtra("chatUserId");

            if(chatUserId==null){
                chatUserId=mLocalUser.getUserId();
            }



//            ((TextView) findViewById(R.id.contact_gender)).setText(mLocalUser.getGender() == null ? "男" : (Integer.parseInt(mLocalUser.getGender() + "") == 1 ? "男" : "女"));

            if(mLocalUser!=null){
                ((TextView) findViewById(R.id.contact_gender)).setText(mLocalUser.getGender() == 1 ?  "男" : "女");
                ((TextView) findViewById(R.id.tv_username)).setText(mLocalUser.getName());
                ((TextView) findViewById(R.id.tv_dis_time)).setText(mLocalUser.getDistance() + "|" + mLocalUser.getBefore());
            }

        }

        initView();

    }

    private void initView() {
        findViewById(R.id.return_back).setOnClickListener(this);
        mPicRecyclerView = (RecyclerView) findViewById(R.id.pullLoadMoreRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mPicRecyclerView.setLayoutManager(linearLayoutManager);
        mtotalHandler = new mHandlerWeak(this);


        findViewById(R.id.rb_chat).setOnClickListener(this);
        rb_guanzhu = (RadioButton) findViewById(R.id.rb_guanzhu);
        rb_guanzhu.setOnClickListener(this);

        //如果user来自联系人列表，则将关注隐藏
        if(userType!=null && userType.equals("contact")){
            rb_guanzhu.setVisibility(View.GONE);
        }


        loadData();

        rlist_view_content = (RecyclerView) findViewById(R.id.rlist_view_content);
        LinearLayoutManager mlinearLayoutManager = new LinearLayoutManager(mContext);
        mlinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rlist_view_content.setLayoutManager(mlinearLayoutManager);
        rlist_view_content.setVerticalScrollBarEnabled(true);

        initContentAdapter();
        rlist_view_content.setAdapter(contentRAdapter);


    }

    Intent intent2;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.return_back:
                this.finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.rb_chat:
                intent2 = new Intent(mContext, ChatActivity.class);
                intent2.putExtra(EaseConstant.EXTRA_USER_ID, mLocalUser.getUserId());
                intent2.putExtra(EaseConstant.EXTRA_USER_NAME, mLocalUser.getName());
                startActivity(intent2);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.rb_guanzhu:
                if (rb_guanzhu.getText().toString().equals("关注")) {
                    new EaseAlertDialog(UserInfoEditActivity.this, null, "关注TA可查看并接收TA的全部动态，确定关注TA？", null, new EaseAlertDialog.AlertDialogUser() {

                        @Override
                        public void onResult(boolean confirmed, Bundle bundle) {
                            if (confirmed) {
//                            loadDataService.loadGetJsonRequestData( OkHttpUtils.GetUserInfoURL+"?userId=" + mLocalUser.getUserId() ,LOAD_DATA);
                                try {
                                    JSONObject json = new JSONObject();
                                    json.put("userId", WowuApp.UserId);
                                    loadDataService.loadPostJsonRequestData(WowuApp.JSON, WowuApp.FocusFriendsURL + "?userId=" + mLocalUser.getUserId(), json.toString(), R.id.rb_guanzhu);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }, true).show();

  //发送一句对话给他，让他去关注你
//                    发送文本消息
//创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此
                    EMMessage message = EMMessage.createTxtSendMessage(WowuApp.Name+"关注了你哦", mLocalUser.getUserId());
//发送消息
                    EMClient.getInstance().chatManager().sendMessage(message);

                } else {
                    //进行取消关注逻辑判断
                    new EaseAlertDialog(UserInfoEditActivity.this, null, "确定取消关注TA吗？", null, new EaseAlertDialog.AlertDialogUser() {

                        @Override
                        public void onResult(boolean confirmed, Bundle bundle) {
                            if (confirmed) {
//                            loadDataService.loadGetJsonRequestData( OkHttpUtils.GetUserInfoURL+"?userId=" + mLocalUser.getUserId() ,LOAD_DATA);
                                try {
                                    JSONObject json = new JSONObject();
                                    json.put("userId", WowuApp.UserId);
                                    loadDataService.loadPostJsonRequestData(WowuApp.JSON, WowuApp.RemoveFocusURL + "?userId=" + mLocalUser.getUserId(), json.toString(), R.id.rb_guanzhu);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }, true).show();
                }

                break;
        }
    }

    private final int LOAD_DATA = 1;

    private void loadData() {
        loadDataService.loadGetJsonRequestData(OkHttpUtils.GetUserInfoURL + "?userId=" + chatUserId, LOAD_DATA);//mLocalUser.getUserId()
    }

    public static final int DOWNLOADED_LocalUser = 0;
    public static final int DOWNLOADED_ERROR = 1;

    @Override
    public void loadServerData(String response, int flag) {
        switch (flag) {
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
            case R.id.rb_guanzhu:
                if (rb_guanzhu.getText().toString().equals("关注")) {
                    MyToast.show(mContext, "关注成功");
                    rb_guanzhu.setText("取消关注");
                } else {
                    MyToast.show(mContext, "取消关注成功");
                    rb_guanzhu.setText("关注");
                }

                break;
        }


    }

    @Override
    public void loadDataFailed(String response, int flag) {
        switch (flag) {
            case LOAD_DATA:
                Message msg = new Message();
                msg.what = DOWNLOADED_ERROR;
                mtotalHandler.sendMessage(msg);
                break;
            case R.id.rb_guanzhu:
                MyToast.show(mContext, "关注失败");
                break;
        }
    }


    private static class mHandlerWeak extends Handler {
        private WeakReference<UserInfoEditActivity> activity = null;

        public mHandlerWeak(UserInfoEditActivity act) {
            super();
            this.activity = new WeakReference<UserInfoEditActivity>(act);
        }

        @Override
        public void handleMessage(Message msg) {
            UserInfoEditActivity act = activity.get();
            if (null == act) {
                return;
            }
            switch (msg.what) {
                // 正在下载
                case DOWNLOADED_LocalUser:
                    if (act.userPicAdapter == null) {
                        act.initTopPicAdapter();

                        act.userPicAdapter.setData(act.getUserInfoDetail().getPhotos());

                        act.contentRAdapter.setData(act.mUserDetailList);

                        act.mPicRecyclerView.setAdapter(act.userPicAdapter);

                        if (act.mUserDetail.isFoucs()) {
                            act.rb_guanzhu.setText("取消关注");
                        } else {
                            act.rb_guanzhu.setText("关注");
                        }


                    } else {
                        act.userPicAdapter.setData(act.getUserInfoDetail().getPhotos());
                    }
                    break;
                case DOWNLOADED_ERROR:
//                    MyToast.show(mContext, "服务器返回值异常", Toast.LENGTH_LONG);
                    break;
            }
        }
    }


    public void setLoadInfo(String totalresult) throws JSONException {
        Gson gson = new GsonBuilder().create();
        if (totalresult != null) {
            java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<UserInfoDetail>() {
            }.getType();

            mUserDetail = gson.fromJson(totalresult, type);
            mUserDetailList.add(mUserDetail);
        }

    }


    public UserInfoDetail getUserInfoDetail() {
        return mUserDetail;
    }

    private CommRecyclerAdapter initContentAdapter() {
        contentRAdapter = new CommRecyclerAdapter<UserInfoDetail>(mContext, R.layout.activity_user_info_edit_content_item) {
            @Override
            public void convert(CommRecyclerViewHolder viewHolder, UserInfoDetail mainMessage) {


                viewHolder.setText(R.id.tv_xianzhi_huiyuan, "VIP" + mainMessage.getVipLevel());
//                viewHolder.setText(R.id.ratingBar, );
                viewHolder.setText(R.id.tv_xianzhihao, mainMessage.getUserNumber());
                viewHolder.setText(R.id.tv_zhongwen_nicheng, mainMessage.getName());

                viewHolder.setText(R.id.ln_yingwen_nicheng, mainMessage.getEnglishName() + "");
                viewHolder.setText(R.id.ln_birthday, mainMessage.getBirthday() + "");
                viewHolder.setText(R.id.ln_feeling, mainMessage.getMaritalStatus() + "");
                viewHolder.setText(R.id.ln_jiaxiang, mainMessage.getDailyAddress());
                viewHolder.setText(R.id.ln_geren_jieshao, mainMessage.getDescription());
                viewHolder.setText(R.id.ln_shenfenbiaoqian, mainMessage.getDisposition());


                if (mainMessage.getJob() != null) {
                    viewHolder.setText(R.id.ln_zhiye, mainMessage.getJob());
                } else {
                    viewHolder.getView(R.id.rt_zhiye).setVisibility(View.GONE);
                }

                if (mainMessage.getSchool() != null) {
                    viewHolder.setText(R.id.ln_school, mainMessage.getSchool());
                } else {
                    viewHolder.getView(R.id.rt_school).setVisibility(View.GONE);
                }


                if (mainMessage.getLifeAddress() != null) {
                    viewHolder.setText(R.id.tv_shenghuodidian, mainMessage.getLifeAddress());
                } else {
                    viewHolder.getView(R.id.rt_shenghuodidian).setVisibility(View.GONE);
                }

                if (mainMessage.getJobAddress() != null) {
                    viewHolder.setText(R.id.tv_gongzuodidian, mainMessage.getJobAddress());
                } else {
                    viewHolder.getView(R.id.rt_gongzuodidian).setVisibility(View.GONE);
                }

                if (mainMessage.getVisitedAttractions() != null) {
                    viewHolder.setText(R.id.ln_changchumodi, mainMessage.getVisitedAttractions());
                } else {
                    viewHolder.getView(R.id.rt_changchumodi).setVisibility(View.GONE);
                }

                if (mainMessage.getVisitedAttractions() != null) {
                    viewHolder.setText(R.id.tv_quguo_jingdian, mainMessage.getVisitedAttractions());
                } else {
                    viewHolder.getView(R.id.rt_quguo_jingdian).setVisibility(View.GONE);
                }


            }

            @Override
            public int getItemViewType(int position) {
                return super.getItemViewType(position);
            }
        };
        return null;
    }


    private CommRecyclerAdapter initTopPicAdapter() {
        userPicAdapter = new CommRecyclerAdapter<UserInfoDetail.PhotosBean>(mContext, R.layout.item_userpic_recycler) {
            @Override
            public void convert(CommRecyclerViewHolder viewHolder, UserInfoDetail.PhotosBean mainMessage) {
                SimpleDraweeView portal_news_img = (SimpleDraweeView) viewHolder.getView(R.id.user_info_pic);
                if (mainMessage.getFullUrl() != null) {
                    portal_news_img.setImageURI(Uri.parse(mainMessage.getFullUrl()));
                }

//                portal_news_img.setImageURI(Uri.parse("http://xzxj.oss-cn-shanghai.aliyuncs.com/user/7fe11e87-cd32-4684-8220-73c8bfc98431.jpg"));

            }

            @Override
            public int getItemViewType(int position) {
                return super.getItemViewType(position);
            }
        };


        //设置item点击事件
        userPicAdapter.setOnItemClick(new CommRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                Intent intent2 = new Intent(mContext, UserInfoEditActivity.class);
////                //        intent2.putExtra("content", LocalUser.DataBeanlist.get(tempPosition-1).getContent());
////                intent2.putExtra("url", DistApp.serverAbsolutePath + "/snews!mobileNewsdetail.action?news.id=4028816f4d4be502014d4c0e22dc003d");
////                intent2.putExtra("name", "消息通知");
//                startActivity(intent2);
//                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);


                if(userPicAdapter.getData().size()==1){
                    onBubbleClick(mContext,((UserInfoDetail.PhotosBean)userPicAdapter.getData().get(position)).getFullUrl(),((UserInfoDetail.PhotosBean)userPicAdapter.getData().get(position)).getId());
                }else{
                    ArrayList<String> tempimageUrls = new ArrayList<String>();
                    for (int i = 0; i < userPicAdapter.getData().size(); i++) {
                        tempimageUrls.add(((UserInfoDetail.PhotosBean) userPicAdapter.getData().get(i)).getFullUrl());
                    }
                    imageBrower(position,tempimageUrls);
                }
            }
        });
        return null;
    }


    /**
     * 打开图片查看器
     *
     * @param position
     * @param urls2
     */
    protected void imageBrower(int position, ArrayList<String> urls2) {
        Intent intent = new Intent(mContext, PhotoViewImagePagerActivity.class);
        // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
        intent.putExtra(PhotoViewImagePagerActivity.EXTRA_IMAGE_URLS, urls2);
        intent.putExtra(PhotoViewImagePagerActivity.EXTRA_IMAGE_INDEX, position);
        startActivity(intent);
    }



    protected void onBubbleClick(Context context,String url,String name) {
//        String url = "http://img1.imgtn.bdimg.com/it/u=4033491868,3189899599&fm=21&gp=0.jpg";
//        String url = "http://xzxj.oss-cn-shanghai.aliyuncs.com/user/97f28a44-2bab-4140-963a-b5277351ae74.jpg";

        Log.i("为什么不显示呢，你麻痹","：："+WowuApp.tempPicPath+name+"."+UtilsTool.getFileType(url));

        Intent intent = new Intent(context, EaseShowBigImageActivity.class);
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+WowuApp.tempPicPath+name+"."+UtilsTool.getFileType(url));
        if (file.exists()) {
            Uri uri = Uri.fromFile(file);
            intent.putExtra("uri", uri);
        }else {
        // The local full size pic does not exist yet.
        // ShowBigImage needs to download it from the server
        // first
        intent.putExtra("secret",url);
        intent.putExtra("remotepath",url);
//        intent.putExtra("localUrl", WowuApp.tempPicPath+UtilsTool.getFileName(url));
        intent.putExtra("localUrl", WowuApp.tempPicPath+name+"."+UtilsTool.getFileType(url));
        intent.putExtra("loadType","wuho");

        }
         context.startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

    }

}

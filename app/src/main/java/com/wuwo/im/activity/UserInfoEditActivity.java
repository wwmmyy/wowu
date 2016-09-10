package com.wuwo.im.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.TextView;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
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
import com.wuwo.im.bean.SanGuan;
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

    private final int SANGUAN_PICK = 200;
    private final int SANGUAN_PICK2 = 202;
    public static final int DOWNLOADED = 203;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.FILL_PARENT);  //设置全屏
        setContentView(R.layout.activity_user_info_edit);
        if (getIntent() != null) {
            mLocalUser = (LocalUser.DataBean) getIntent().getSerializableExtra("localUser");
            userType =  getIntent().getStringExtra("userType");

            chatUserId =  getIntent().getStringExtra("chatUserId");

            if(chatUserId==null){
                chatUserId=mLocalUser.getUserId();
            }

            if(mLocalUser!=null){
//                ((TextView) findViewById(R.id.contact_gender)).setText(mLocalUser.getGender() == 1 ?  "男" : "女");
                SimpleDraweeView user_pic = (SimpleDraweeView) findViewById(R.id.user_pic);
                user_pic.setImageURI(Uri.parse(mLocalUser.getPhotoUrl()));

                ((TextView) findViewById(R.id.tv_name)).setText(mLocalUser.getName());
                ((TextView) findViewById(R.id.tv_user_character)).setText(mLocalUser.getAge()+"|"+mLocalUser.getDisposition());
                ((TextView) findViewById(R.id.tv_dis_time)).setText(mLocalUser.getDistance() + "|" + mLocalUser.getBefore());
            }
        }
        initView();

    }

    private void initView() {



        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });






        findViewById(R.id.return_back).setOnClickListener(this);
        mPicRecyclerView = (RecyclerView) findViewById(R.id.pullLoadMoreRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mPicRecyclerView.setLayoutManager(linearLayoutManager);
        mtotalHandler = new mHandlerWeak(this);


        findViewById(R.id.rb_chat).setOnClickListener(this);
        rb_guanzhu = (RadioButton) findViewById(R.id.rb_guanzhu);
        rb_guanzhu.setOnClickListener(this);
        findViewById(R.id.iv_sanguan_pick).setOnClickListener(this);


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



        loadSanGuan(SANGUAN_PICK);

    }

    Intent intent2;


    SanGuan mSanGuan = new SanGuan();

    private ProgressDialog pd;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.return_back:
                this.finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;

            case R.id.iv_sanguan_pick:
                if (mSanGuan != null && mSanGuan.getTitle() != null) { //说明起始加载三观配内容成功
                    showSanguanPickDialog();
                } else {

                    pd = UtilsTool.initProgressDialog(mContext, "请稍后...");
                    pd.show();
                    loadSanGuan(SANGUAN_PICK2);
                }


                break;
            case R.id.rb_chat:
                intent2 = new Intent(mContext, ChatActivity.class);
                intent2.putExtra(EaseConstant.EXTRA_USER_ID, mLocalUser.getUserId());
                intent2.putExtra(EaseConstant.EXTRA_USER_NAME, mLocalUser.getName());
                intent2.putExtra(EaseConstant.EXTRA_USER_ICONPATH, mLocalUser.getPhotoUrl());
                intent2.putExtra(EaseConstant.EXTRA_CHAT_USER_ID, mLocalUser.getUserId());
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

    /**
     * 加载用户个人资料信息
     */
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
            case SANGUAN_PICK:
//            {"Successfully":1,"Title":"三观貌合神离","Description":"双方可以谈论很多共同的话题，相互交流能够激发对方隐藏的盲点和攻击点，彼此印象深刻惺惺相惜相见恨晚。但是需要意识到各自的目的和行动是相反的，一个南辕一个北辙，不能邯郸学步，否则便开始误解，渐渐感到不能信任彼此。","UserName1":"wmy","UserPhotoUrl1":"http://xzxj.oss-cn-shanghai.aliyuncs.com/user/ae95c3fd-8577-4f39-8d16-0a10e0ad51c7x480.jpg","UserAge1":1,"UserGender1":0,"UserName2":"maggie","UserPhotoUrl2":"http://xzxj.oss-cn-shanghai.aliyuncs.com/user/2b92e72d-5eb3-4253-9f47-d2f5a999fe10x480.jpg","UserAge2":0,"UserGender2":0}


                if (response != null) {
                    java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<SanGuan>() {
                    }.getType();
                    mSanGuan = gson.fromJson(response, type);
                }
                Log.i("ChatFragment:w:", response + ":::::=" + chatUserId);
                break;
            case SANGUAN_PICK2:
//            {"Successfully":1,"Title":"三观貌合神离","Description":"双方可以谈论很多共同的话题，相互交流能够激发对方隐藏的盲点和攻击点，彼此印象深刻惺惺相惜相见恨晚。但是需要意识到各自的目的和行动是相反的，一个南辕一个北辙，不能邯郸学步，否则便开始误解，渐渐感到不能信任彼此。","UserName1":"wmy","UserPhotoUrl1":"http://xzxj.oss-cn-shanghai.aliyuncs.com/user/ae95c3fd-8577-4f39-8d16-0a10e0ad51c7x480.jpg","UserAge1":1,"UserGender1":0,"UserName2":"maggie","UserPhotoUrl2":"http://xzxj.oss-cn-shanghai.aliyuncs.com/user/2b92e72d-5eb3-4253-9f47-d2f5a999fe10x480.jpg","UserAge2":0,"UserGender2":0}
                if (pd != null) {
                    pd.dismiss();
                }
                if (response != null) {
                    java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<SanGuan>() {
                    }.getType();
                    mSanGuan = gson.fromJson(response, type);
                }
                Log.i("ChatFragment:w:", response + ":::::=" + chatUserId);

                Message msg = new Message();
                msg.what = DOWNLOADED;
                mtotalHandler.sendMessage(msg);
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
                case DOWNLOADED:
                    act.showSanguanPickDialog();
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


//                viewHolder.setText(R.id.tv_xianzhi_huiyuan, "VIP" + mainMessage.getVipLevel());
                if(mainMessage.isIsVip()){
                    viewHolder.setText(R.id.tv_xianzhi_huiyuan, "会员用户" );
                }else{
                    viewHolder.setText(R.id.tv_xianzhi_huiyuan, "普通用户" );
                }
//                viewHolder.setText(R.id.ratingBar, );
                viewHolder.setText(R.id.tv_xianzhihao, mainMessage.getUserNumber());
                viewHolder.setText(R.id.tv_zhongwen_nicheng, mainMessage.getName());

                viewHolder.setText(R.id.ln_yingwen_nicheng, mainMessage.getEnglishName() + "");
//                viewHolder.setText(R.id.ln_birthday, mainMessage.getBirthday() + "");
//                viewHolder.setText(R.id.ln_feeling, mainMessage.getMaritalStatus() + "");
                viewHolder.setText(R.id.ln_jiaxiang, mainMessage.getHome());
                viewHolder.setText(R.id.ln_geren_jieshao, mainMessage.getDescription());
//                viewHolder.setText(R.id.ln_shenfenbiaoqian, mainMessage.getDisposition());



                //                @"保密", @"单身",@"恋爱中", @"已婚", @"同性",
                viewHolder.setText(R.id.ln_feeling, mainMessage.getMaritalStatus() + "");


                switch (mainMessage.getMaritalStatus()) {
                    case 0:
                        viewHolder.setText(R.id.ln_feeling, "保密");
                        break;
                    case 1:
                        viewHolder.setText(R.id.ln_feeling, "单身");
                        break;
                    case 2:
                        viewHolder.setText(R.id.ln_feeling, "恋爱中");
                        break;
                    case 3:
                        viewHolder.setText(R.id.ln_feeling, "已婚");
                        break;
                    case 4:
                        viewHolder.setText(R.id.ln_feeling, "同性");
                        break;
                }


                if (mainMessage.getEnglishName() != null && !mainMessage.getEnglishName().equals("null")) {
                    viewHolder.setText(R.id.ln_yingwen_nicheng, mainMessage.getEnglishName() + "");
                } else {
                    viewHolder.getView(R.id.rt_yingwen_nicheng).setVisibility(View.GONE);
                }


                if (mainMessage.getBirthday() != null && !mainMessage.getBirthday().equals("null")) {
                    viewHolder.setText(R.id.ln_birthday, mainMessage.getBirthday() + "");
                } else {
                    viewHolder.getView(R.id.ln_birthday).setVisibility(View.GONE);
                }



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

                if (mainMessage.getDailyAddress() != null) {
                    viewHolder.setText(R.id.ln_changchumodi, mainMessage.getDailyAddress());
                } else {
//                    viewHolder.getView(R.id.rt_changchumodi).setVisibility(View.GONE);
                }

/*                if (mainMessage.getVisitedAttractions() != null) {
                    viewHolder.setText(R.id.tv_quguo_jingdian, mainMessage.getVisitedAttractions());
                } else {
//                    viewHolder.getView(R.id.rt_quguo_jingdian).setVisibility(View.GONE);
                }*/


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













    private void loadSanGuan(final int SANGUAN_PICK0) {
         new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject json = new JSONObject();
                    json.put("userId", chatUserId);
                    loadDataService.loadPostJsonRequestData(WowuApp.JSON, WowuApp.MatchURL + "?userId=" + chatUserId, json.toString(), SANGUAN_PICK0);

                    Log.i("ChatFragment::", WowuApp.MatchURL + "?userId=" + chatUserId + "toChatUsername::" + chatUserId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }





    /**
     * 用户三观配显示，该模块后面要移到会话模块，暂时在这里测试
     */
    private void showSanguanPickDialog() {
        View view =  getLayoutInflater().inflate(com.hyphenate.easeui.R.layout.dialog_sanguan_pick, null);
        final Dialog dialog = new Dialog(mContext, com.hyphenate.easeui.R.style.transparentFrameWindowStyle);
        dialog.setContentView(view, new ViewGroup.LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();
        view.findViewById(com.hyphenate.easeui.R.id.iv_sanguan_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });
        view.findViewById(com.hyphenate.easeui.R.id.tv_sanguan_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });

        TextView tv_sanguan_user1 = (TextView) view.findViewById(com.hyphenate.easeui.R.id.tv_sanguan_user1);
        TextView tv_sanguan_user2 = (TextView) view.findViewById(com.hyphenate.easeui.R.id.tv_sanguan_user2);
        tv_sanguan_user2.setText(mContext.getSharedPreferences(WowuApp.PREFERENCE_KEY, mContext.MODE_PRIVATE).getString("Name", "http://#"));
        if (mSanGuan != null) {
            tv_sanguan_user1.setText(mSanGuan.getUserName2());
        }


        TextView genderm = (TextView) view.findViewById(com.hyphenate.easeui.R.id.tvage_gender1_male);
//        TextView genderw = (TextView) view.findViewById(com.hyphenate.easeui.R.id.tvage_gender1_female);
//        if (mSanGuan.getUserGender2() == 0) {
//            genderm.setVisibility(View.VISIBLE);
//            genderw.setVisibility(View.GONE);
//            genderm.setText(mSanGuan.getUserAge2() + "");
//        } else {
//            genderm.setVisibility(View.GONE);
//            genderw.setVisibility(View.VISIBLE);
//            genderw.setText(mSanGuan.getUserAge2() + "");
//        }

        genderm.setText(mSanGuan.getUserAge2() + " | " + mSanGuan.getUserDisposition2());

        TextView genderm2 = (TextView) view.findViewById(com.hyphenate.easeui.R.id.tvage_gender2_male);
//        TextView genderw2 = (TextView) view.findViewById(com.hyphenate.easeui.R.id.tvage_gender2_female);
//        if (mSanGuan.getUserGender1() == 0) {
//            genderm2.setVisibility(View.VISIBLE);
//            genderw2.setVisibility(View.GONE);
//            genderm2.setText(mSanGuan.getUserAge1() + "");
//        } else {
//            genderm2.setVisibility(View.GONE);
//            genderw2.setVisibility(View.VISIBLE);
//            genderw2.setText(mSanGuan.getUserAge1() + "");
//        }

        genderm2.setText(mSanGuan.getUserAge1() + " | " + mSanGuan.getUserDisposition1());


/*        TextView genderm1 = (TextView) view.findViewById(com.hyphenate.easeui.R.id.tvage_gender2_male);
        TextView genderw1 = (TextView) view.findViewById(com.hyphenate.easeui.R.id.tvage_gender2_female);
        if (mContext.getSharedPreferences(WowuApp.PREFERENCE_KEY, mContext.MODE_PRIVATE).getInt("Gender", 0) == 0) {
            genderm1.setVisibility(View.VISIBLE);
            genderw1.setVisibility(View.GONE);
            genderm1.setText(mContext.getSharedPreferences(WowuApp.PREFERENCE_KEY, mContext.MODE_PRIVATE).getInt("Gender", 0) + "");
        } else {
            genderm1.setVisibility(View.GONE);
            genderw1.setVisibility(View.VISIBLE);
            genderw1.setText(mContext.getSharedPreferences(WowuApp.PREFERENCE_KEY, mContext.MODE_PRIVATE).getInt("Gender", 0) + "");
        }*/

        TextView tv_sanguan_result = (TextView) view.findViewById(com.hyphenate.easeui.R.id.tv_sanguan_result);
        TextView tv_sanguan_shuoming = (TextView) view.findViewById(com.hyphenate.easeui.R.id.tv_sanguan_shuoming);


        if (mSanGuan != null) {
            tv_sanguan_result.setText(mSanGuan.getTitle());
            tv_sanguan_shuoming.setText(mSanGuan.getDescription());


            SimpleDraweeView draweeView = (SimpleDraweeView) view.findViewById(com.hyphenate.easeui.R.id.sdv_sanguan_pic2 );
//        if (toChatUsername != null && EaseImageUtils.usersPhotoUrl.get(toChatUsername) != null) {
//            draweeView.setImageURI(Uri.parse(EaseImageUtils.usersPhotoUrl.get(toChatUsername)));//"http://w messageList.getItem(0).getTo()
//        } else if (iconPath != null) {
//            draweeView.setImageURI(Uri.parse(iconPath));//"http://w messageList.getItem(0).getTo()
//        }


            draweeView.setImageURI(Uri.parse(mSanGuan.getUserPhotoUrl1()));
            SimpleDraweeView draweeView2 = (SimpleDraweeView) view.findViewById(com.hyphenate.easeui.R.id.sdv_sanguan_pic1);
//        draweeView2.setImageURI(Uri.parse( EaseImageUtils.usersPhotoUrl.get( mContext.getSharedPreferences(WowuApp.PREFERENCE_KEY,mContext.MODE_PRIVATE).getString("UserId", "http://#"))));//"http://w messageList.getItem(0).getFrom() WowuApp.UserId EMClient.getInstance().getCurrentUser()
//        draweeView2.setImageURI(Uri.parse(WowuApp.iconPath));
            draweeView2.setImageURI(Uri.parse(mSanGuan.getUserPhotoUrl2()));
            GenericDraweeHierarchy hierarchy2 = draweeView.getHierarchy();
            GenericDraweeHierarchy hierarchy1 = draweeView2.getHierarchy();

            if (mSanGuan.getUserGender1() == 1) {
                hierarchy1.setControllerOverlay(getResources().getDrawable(R.drawable.overlay_male));
            } else {
                hierarchy1.setControllerOverlay(getResources().getDrawable(R.drawable.overlay_fmale));
            }

            if (mSanGuan.getUserGender2() == 1) {
                hierarchy2.setControllerOverlay(getResources().getDrawable(R.drawable.overlay_male));
            } else {
                hierarchy2.setControllerOverlay(getResources().getDrawable(R.drawable.overlay_fmale));
            }
        }


        // 设置显示动画
        window.setWindowAnimations(com.hyphenate.easeui.R.style.main_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y =  getWindowManager().getDefaultDisplay().getHeight();
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.MATCH_PARENT;//WRAP_CONTENT

        // 设置显示位置
        dialog.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }




}

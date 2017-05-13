package com.wuwo.im.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.hyphenate.exceptions.HyphenateException;
import com.net.grandcentrix.tray.AppPreferences;
import com.squareup.okhttp.Request;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.wuwo.im.adapter.CommRecyclerAdapter;
import com.wuwo.im.adapter.CommRecyclerViewHolder;
import com.wuwo.im.adapter.MyViewPagerAdapter;
import com.wuwo.im.bean.CharacterPopInfo;
import com.wuwo.im.bean.CharacterTongLei;
import com.wuwo.im.bean.LocalUser;
import com.wuwo.im.bean.SanGuan;
import com.wuwo.im.bean.UserInfoDetail;
import com.wuwo.im.config.WowuApp;
import com.wuwo.im.util.LogUtils;
import com.wuwo.im.util.MyToast;
import com.wuwo.im.util.UtilsTool;
import com.wuwo.im.view.SearchView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import im.imxianzhi.com.imxianzhi.R;

/**
 * desc UserInfoEditActivity
 *
 * @author 王明远
 * @日期： 2016/6/9 0:08
 * @版权:Copyright All rights reserved.  http://xzxj.oss-cn-shanghai.aliyuncs.com/user/7fe11e87-cd32-4684-8220-73c8bfc98431.jpg
 */

public class UserInfoEditActivity extends BaseLoadActivity {

    private final String TAG = "UserInfoEditActivity:";
    private Gson gson = new GsonBuilder().create();
    private SharedPreferences mSettings;
    private SharedPreferences.Editor editor;
    private RecyclerView mPicRecyclerView;
    private int mCount = 0;
    //    ArrayList<UserInfoDetail> meeting_userlist = new ArrayList<UserInfoDetail>(); //记录所有的最新消息
    private SearchView search_view;
    private String searchinfo;
    private CommRecyclerAdapter userPicAdapter;

    private mHandlerWeak mtotalHandler;

    private TextView rb_guanzhu;
    private UserInfoDetail mUserDetail = new UserInfoDetail();
    private ArrayList<UserInfoDetail> mUserDetailList = new ArrayList<UserInfoDetail>(); //记录所有的最新消息
    private ArrayList<CharacterPopInfo> mCharacterPuPopList = new ArrayList<CharacterPopInfo>();

    private RecyclerView rlist_view_content;
    private CommRecyclerAdapter contentRAdapter;

    //    {"UserId":"28cf1590d726489f8b0fade0270ea951","Name":"啦啦啦","Disposition":"ISTJ检查者","Description":null,"Age":0,"Gender":1,"Distance":"11847.1km","Before":"2天","PhotoUrl":"http://xzxj.oss-cn-shanghai.aliyuncs.com/user/6b6283b3-d3bb-402b-8e31-732fd1c9c5e1.jpg","EasemobId":"c14dd04a-375c-11e6-9fdd-afd893d9f6d1"}
    private LocalUser.DataBean mLocalUser;
    private String userType;//用来标识当user从联系人跳转过来时，底部按钮不现实关注”
    private String chatUserId;

    private final int SANGUAN_PICK = 200;
    private final int SANGUAN_PICK2 = 202;
    public static final int DOWNLOADED = 203;
    public static final int REFERSH_DATA = 206;
    private TextView tv_name, tv_user_character, tv_dis_time, rb_chat;
    private LinearLayout ln_guanzhu, rg, ln_chat;
    private ImageView iv_sanguan_pick;
    private View v_line;
    private SimpleDraweeView user_pic;
    private final int TV_NAME = 300;
    ProgressDialog pg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.FILL_PARENT);  //设置全屏
        setContentView(R.layout.activity_user_info_edit);
        mSettings = mContext.getSharedPreferences(WowuApp.PREFERENCE_KEY,
                android.content.Context.MODE_PRIVATE);


        tv_name = ((TextView) findViewById(R.id.tv_name));
        tv_name.setOnClickListener(this);
        tv_dis_time = ((TextView) findViewById(R.id.tv_dis_time));
        tv_user_character = ((TextView) findViewById(R.id.tv_user_character));
//        tv_user_character.setOnClickListener(this);
        user_pic = (SimpleDraweeView) findViewById(R.id.user_pic);
        findViewById(R.id.tv_user_character_lin).setOnClickListener(this);


        if (getIntent() != null) {
            mLocalUser = (LocalUser.DataBean) getIntent().getSerializableExtra("localUser");
            userType = getIntent().getStringExtra("userType");
            chatUserId = getIntent().getStringExtra("chatUserId");
            String photoUrl = getIntent().getStringExtra("PhotoUrl");

            if (photoUrl != null) {
                user_pic.setImageURI(Uri.parse(photoUrl));
            }

            if (chatUserId == null && mLocalUser != null) {
                chatUserId = mLocalUser.getUserId();
            }

            if (mLocalUser != null) {
//                ((TextView) findViewById(R.id.contact_gender)).setText(mLocalUser.getGender() == 1 ?  "男" : "女");
                if (mLocalUser.getPhotoUrl() != null) {
                    user_pic.setImageURI(Uri.parse(mLocalUser.getPhotoUrl()));
                }
                tv_name.setText(mLocalUser.getName());
                tv_user_character.setText(mLocalUser.getAge() == 0 ? mLocalUser.getDisposition() : mLocalUser.getDisposition());
//                mLocalUser.getAge() + " | " +
                tv_dis_time.setText(mLocalUser.getDistance() + "岁 |" + mLocalUser.getBefore());
            }
        }
        initView();
        pg = UtilsTool.initProgressDialog(mContext, "正在上传.....");
        pg.show();
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
        mtotalHandler = new mHandlerWeak(this);

        findViewById(R.id.rb_chat).setOnClickListener(this);
        rb_guanzhu = (TextView) findViewById(R.id.rb_guanzhu);
        rb_guanzhu.setOnClickListener(this);
        iv_sanguan_pick = (ImageView) findViewById(R.id.iv_sanguan_pick_bak);
        iv_sanguan_pick.setOnClickListener(this);
        findViewById(R.id.iv_sanguan_pick_lin).setOnClickListener(this);

        ln_guanzhu = (LinearLayout) findViewById(R.id.ln_guanzhu);
        rg = (LinearLayout) findViewById(R.id.rg);

        ln_chat = (LinearLayout) findViewById(R.id.ln_chat);
        rb_chat = (TextView) findViewById(R.id.rb_chat);

        v_line = (View) findViewById(R.id.v_line);
        //如果user来自联系人列表，则将关注隐藏
        if (userType != null && userType.equals("contact")) {
//            rb_guanzhu.setVisibility(View.GONE);
            ln_guanzhu.setVisibility(View.GONE);
            v_line.setVisibility(View.GONE);
        }
        if (chatUserId.equals(WowuApp.UserId)) {
           /* ln_guanzhu.setVisibility(View.GONE);
            v_line.setVisibility(View.GONE);*/
            rg.setVisibility(View.GONE);
        }

        loadData();

        rlist_view_content = (RecyclerView) findViewById(R.id.rlist_view_content);
        LinearLayoutManager mlinearLayoutManager = new LinearLayoutManager(mContext);
        mlinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rlist_view_content.setLayoutManager(mlinearLayoutManager);
        rlist_view_content.setVerticalScrollBarEnabled(true);
        initContentAdapter();
        rlist_view_content.setAdapter(contentRAdapter);


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setSmoothScrollbarEnabled(true);
        layoutManager.setAutoMeasureEnabled(true);
        rlist_view_content.setLayoutManager(layoutManager);
        rlist_view_content.setHasFixedSize(true);
        rlist_view_content.setNestedScrollingEnabled(false);


        loadSanGuan(SANGUAN_PICK);

    }

    Intent intent2;


    SanGuan mSanGuan = new SanGuan();

    private ProgressDialog pd;
    // 用于标记性格类型弹出按钮是否被点击还没有返回值呢
    private boolean POP_DIALOG_TAPED = false;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.return_back:
                this.finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;

            case R.id.iv_sanguan_pick_lin:
            case R.id.iv_sanguan_pick_bak:
                if (mSanGuan != null && mSanGuan.getTitle() != null) { //说明起始加载三观配内容成功
                    showSanguanPickDialog();
                } else {
                    pd = UtilsTool.initProgressDialog(mContext, "请稍后...");
                    pd.show();
                    loadSanGuan(SANGUAN_PICK2);
                }


                break;
            case R.id.rb_chat:
                if (mLocalUser == null) {
                    MyToast.show(mContext, "用户信息异常，无法聊天");
                    break;
                }
                intent2 = new Intent(mContext, ChatActivity.class);
                intent2.putExtra(EaseConstant.EXTRA_USER_ID, mLocalUser.getUserId());
                intent2.putExtra(EaseConstant.EXTRA_USER_NAME, mLocalUser.getName());
                intent2.putExtra(EaseConstant.EXTRA_USER_ICONPATH, mLocalUser.getPhotoUrl());
                intent2.putExtra(EaseConstant.EXTRA_CHAT_USER_ID, mLocalUser.getUserId());
//                mUserDetail

                startActivity(intent2);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.rb_guanzhu:


                if (currentMenu == BLACK_MENU) {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
//                                EMClient.getInstance().contactManager().removeUserFromBlackList(act.mUserDetail.getEasemobId());
                                EMClient.getInstance().contactManager().removeUserFromBlackList(mUserDetail.getId());//需异步处理
                            } catch (HyphenateException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                    try {
                        JSONObject json = new JSONObject();
                        json.put("id", mUserDetail.getId());//APP版本号
                        loadDataService.loadPostJsonRequestData(WowuApp.JSON, WowuApp.RemoveBlacklistURL + "?id=" + mUserDetail.getBlackId(), json.toString(), R.id.rb_guanzhu);//解除黑名單
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (currentMenu == FRIEND_MENU) {
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

                } else if (currentMenu == STRANGER_MENU) {
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

                    try {
                        //发送一句对话给他，让他去关注你
//                    发送文本消息
//创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此
                        EMMessage message = EMMessage.createTxtSendMessage(WowuApp.Name + "关注了你哦", mLocalUser.getUserId());
                        EMClient.getInstance().chatManager().sendMessage(message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }


                break;

            case R.id.tv_user_character_lin:
                if (mCharacterPuPopList != null && mCharacterPuPopList.size() > 0) {
                    showCharacterInfoDialog();
                } else if (!POP_DIALOG_TAPED) {
                    POP_DIALOG_TAPED = true;
                    if (mUserDetail != null) {
                        LogUtils.i(TAG, mUserDetail.getId() + "\n chatUserId:" + chatUserId);
                        loadPopInfo(WowuApp.GetDispositionNicknamesURL + "?id=" + mUserDetail.getDispositionId(), POP_DIALOG);
                    } else {
                        MyToast.show(mContext, "获取人物信息失败");
                    }
                }

                break;

            case R.id.tv_name:
                Intent intent = new Intent(mContext, OwnerInfoUpdateTextActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("title", "备注名");
                bundle.putString("info", tv_name.getText().toString());
                intent.putExtras(bundle);
                startActivityForResult(intent, TV_NAME);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && data.getExtras() != null && resultCode == RESULT_OK) {
            Bundle b = data.getExtras(); //data为B中回传的Intent
            switch (requestCode) {
                case TV_NAME:
                    String tempBeiZhu = b.getString("info");
                    tv_name.setText(tempBeiZhu);
                    setRemarkName(tempBeiZhu);
                    break;
            }
        }
    }

    private void setRemarkName(String tempBeiZhu) {
        try {
            JSONObject json = new JSONObject();
            json.put("FriendId", mUserDetail.getId());//APP版本号
            json.put("RemarkName", tempBeiZhu);//APP版本号
            loadDataService.loadPostJsonRequestData(WowuApp.JSON, WowuApp.SetRemarkNameURL, json.toString(), TV_NAME);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 加载用户个人资料信息
     */
    private void loadData() {
        AppPreferences appPreferences = new AppPreferences(getApplicationContext()); // this Preference comes for free from the library
        loadDataService.loadGetJsonRequestData(OkHttpUtils.GetUserInfoURL + "?userId=" + chatUserId + "&lon=" + appPreferences.getString("longitude", "0") + "&lat=" + appPreferences.getString("latitude", "0"), LOAD_DATA);//mLocalUser.getUserId()
    }

    private final int LOAD_DATA = 1;

    private final int ADDTOBLACK = 1111;
    public static final int LOADING = 66;
    public static final int DOWNLOADED_LocalUser = 0;
    public static final int DOWNLOADED_ERROR = 1112;
    public static final int POP_XINGGE_DIALOG = 1113;
    public static final int DOWNLOADED_VISIABLE = 3;
    public static final int DOWNLOADED_GONE = 4;
    public static final int POP_DIALOG = 37;
    public static final int PRE_POP_DIALOG = 38;
    public static final int REMOVEBLACK_LOAD_DATA = 67;

    @Override
    public void loadServerData(String response, int flag) {
        LogUtils.i(TAG, response + ": :=");
        switch (flag) {
            case REMOVEBLACK_LOAD_DATA:
                try {
                    if (response != null) {
                        setLoadInfo(response);
                    }
                    if (mUserDetail != null) {
                        Message msg = new Message();
                        msg.what = REMOVEBLACK_LOAD_DATA;
                        mtotalHandler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    Message msg = new Message();
                    msg.what = DOWNLOADED_ERROR;
                    msg.obj = "服务器返回异常,请重试";
                    mtotalHandler.sendMessage(msg);
                }
                break;
            case LOAD_DATA:
                try {
                    if (response != null) {
                        setLoadInfo(response);
                    }
                    if (mUserDetail != null) {
                        Message msg = new Message();
                        msg.what = DOWNLOADED_LocalUser;
                        mtotalHandler.sendMessage(msg);
                    }
                } catch (Exception e) {
//                                    e.printStackTrace();
                    Message msg = new Message();
                    msg.what = DOWNLOADED_ERROR;
                    msg.obj = "服务器返回异常,请重试";
                    mtotalHandler.sendMessage(msg);
                }
                break;
            case R.id.rb_guanzhu:
                if (currentMenu == STRANGER_MENU) {
                    Message msg = new Message();
                    msg.what = DOWNLOADED_VISIABLE;
                    mtotalHandler.sendMessage(msg);
                } else if (currentMenu == FRIEND_MENU) {
                    Message msg = new Message();
                    msg.what = DOWNLOADED_GONE;
                    mtotalHandler.sendMessage(msg);
                } else if (currentMenu == BLACK_MENU) {//说明解除黑名单成功，
//                    发送数据为的是调用环信的接口，解除黑名单

//                    偷个懒，直接解除黑名单了
                    Message msg = new Message();
                    msg.what = BLACK_MENU;
                    msg.obj = response;
                    mtotalHandler.sendMessage(msg);


//      这种方法是从服务器上获取之前的用户的关系状态，重新加载过来
//                    Message msg = new Message();
//                    msg.what = LOADING;//BLACK_MENU;
//                    msg.obj = response;
//                    mtotalHandler.sendMessage(msg);
//                    AppPreferences appPreferences = new AppPreferences(getApplicationContext()); // this Preference comes for free from the library
//                    loadDataService.loadGetJsonRequestData(OkHttpUtils.GetUserInfoURL + "?userId=" + chatUserId + "&lon=" + appPreferences.getString("longitude", "0") + "&lat=" + appPreferences.getString("latitude", "0"), REMOVEBLACK_LOAD_DATA);//mLocalUser.getUserId()


                }

                break;
            case SANGUAN_PICK:
//            {"Successfully":1,"Title":"三观貌合神离","Description":"双方可以谈论很多共同的话题，相互交流能够激发对方隐藏的盲点和攻击点，彼此印象深刻惺惺相惜相见恨晚。但是需要意识到各自的目的和行动是相反的，一个南辕一个北辙，不能邯郸学步，否则便开始误解，渐渐感到不能信任彼此。","UserName1":"wmy","UserPhotoUrl1":"http://xzxj.oss-cn-shanghai.aliyuncs.com/user/ae95c3fd-8577-4f39-8d16-0a10e0ad51c7x480.jpg","UserAge1":1,"UserGender1":0,"UserName2":"maggie","UserPhotoUrl2":"http://xzxj.oss-cn-shanghai.aliyuncs.com/user/2b92e72d-5eb3-4253-9f47-d2f5a999fe10x480.jpg","UserAge2":0,"UserGender2":0}

                if (response != null) {
                    java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<SanGuan>() {
                    }.getType();
                    mSanGuan = gson.fromJson(response, type);
                }
                LogUtils.i(TAG, response + ":::::=" + chatUserId);
                break;
            case SANGUAN_PICK2:
//            {"Successfully":1,"Title":"三观貌合神离","Description":"双方可以谈论很多共同的话题，相互交流能够激发对方隐藏的盲点和攻击点，彼此印象深刻惺惺相惜相见恨晚。但是需要意识到各自的目的和行动是相反的，一个南辕一个北辙，不能邯郸学步，否则便开始误解，渐渐感到不能信任彼此。","UserName1":"wmy","UserPhotoUrl1":"http://xzxj.oss-cn-shanghai.aliyuncs.com/user/ae95c3fd-8577-4f39-8d16-0a10e0ad51c7x480.jpg","UserAge1":1,"UserGender1":0,"UserName2":"maggie","UserPhotoUrl2":"http://xzxj.oss-cn-shanghai.aliyuncs.com/user/2b92e72d-5eb3-4253-9f47-d2f5a999fe10x480.jpg","UserAge2":0,"UserGender2":0}
//                if (pd != null) {
//                    pd.dismiss();
//                }
                if (response != null) {
                    java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<SanGuan>() {
                    }.getType();
                    mSanGuan = gson.fromJson(response, type);
                }
                LogUtils.i(TAG, response + ":::::=" + chatUserId);

                if (mSanGuan != null) {
                    Message msg = new Message();
                    msg.what = DOWNLOADED;
                    mtotalHandler.sendMessage(msg);
                }
//            {
//                Message msg = new Message();
//                msg.what = DOWNLOADED_ERROR;
//                msg.obj = "服务器返回异常,请重试";
//                mtotalHandler.sendMessage(msg);
//            }
                break;
            case ADDTOBLACK:
                Message msg = new Message();
                msg.what = DOWNLOADED_ERROR;
                msg.obj = "添加黑名单成功";
                mtotalHandler.sendMessage(msg);
                break;

            case PRE_POP_DIALOG:
                if (response != null) {
                    java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<List<CharacterTongLei>>() {
                    }.getType();
                    ArrayList<CharacterTongLei> temptongleiList = gson.fromJson(response, type);
                    if (temptongleiList != null) {
                        mCharacterPuPopList.clear();
                        for (int i = 0; i < temptongleiList.size(); i++) {
                            CharacterPopInfo temp = new CharacterPopInfo();
                            temp.setPhotoUrl(temptongleiList.get(i).getHeroPhotoUrl());
                            temp.setText(temptongleiList.get(i).getDetail());
                            temp.setId(temptongleiList.get(i).getId());
                            temp.setTitle(temptongleiList.get(i).getNickName());
                            mCharacterPuPopList.add(temp);
                        }
                    }
                }
                break;
            case POP_DIALOG:

                POP_DIALOG_TAPED = false;
//              此种是跳到一个新的activity，但是这种动画效果不好
                if (response != null) {
                    java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<List<CharacterTongLei>>() {
                    }.getType();
                    ArrayList<CharacterTongLei> temptongleiList = gson.fromJson(response, type);
                    if (temptongleiList != null) {
                        mCharacterPuPopList.clear();
                        for (int i = 0; i < temptongleiList.size(); i++) {
                            CharacterPopInfo temp = new CharacterPopInfo();
                            temp.setPhotoUrl(temptongleiList.get(i).getHeroPhotoUrl());
                            temp.setText(temptongleiList.get(i).getDetail());
                            temp.setId(temptongleiList.get(i).getId());
                            temp.setTitle(temptongleiList.get(i).getNickName());
                            mCharacterPuPopList.add(temp);
                        }
                        //                 由于弹出效果不好，换用另一种方式展现
//                        showCharacterInfoDialog();
                        Message msg1 = new Message();
                        msg1.what = POP_XINGGE_DIALOG;
                        mtotalHandler.sendMessage(msg1);

                    } else {
                        Message msg1 = new Message();
                        msg1.what = DOWNLOADED_ERROR;
                        msg1.obj = "获取性格弹出信息失败，请重试";
                        mtotalHandler.sendMessage(msg1);
                    }

                } else {
                    Message msg1 = new Message();
                    msg1.what = DOWNLOADED_ERROR;
                    msg1.obj = "获取性格弹出信息失败，请重试";
                    mtotalHandler.sendMessage(msg1);
                }
                break;
            case TV_NAME:
                Message msg5 = new Message();
                msg5.what = REFERSH_DATA;
                mUserDetail.setRemarkName(tv_name.getText().toString());
                mUserDetailList.remove(0);
                mUserDetailList.add(mUserDetail);
                mtotalHandler.sendMessage(msg5);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_userinfo, menu);
        return true;
    }

    private void loadPopInfo(String url, int popDialog) {
        loadDataService.loadGetJsonRequestData(url, popDialog);//后面需要改过来
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//            String msg = "";
//            switch (item.getItemId()) {
//                case R.id.webview:
//                    msg += "博客跳转";
//                    break;
//                case R.id.weibo:
//                    msg += "微博跳转";
//                    break;
//                case R.id.action_settings:
//                    msg += "设置";
//                    break;
//            }
//            if (!msg.equals("")) {
//        Toast.makeText(mContext, "微博跳转", Toast.LENGTH_SHORT).show();
//        showCharacterRetestDialog();
//        showCharacteShareDialog();
////            }
//        showSanguanPickDialog();
        if (userType != null && userType.equals("contact") || (mUserDetail != null && mUserDetail.isFoucs())) {
            showUserinfoSetDialog(1);
        } else {
            showUserinfoSetDialog(0);
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void loadDataFailed(String response, int flag) {
        if(response!=null &&  response.equals("401")){
            Intent intent = new Intent(OkHttpUtils.TOKEN_OUTDATE);
            intent.putExtra("token_out_date",  "token_out_date");
            mContext.sendBroadcast(intent);
            return;
        }

        Message msg = new Message();
        msg.what = DOWNLOADED_ERROR;
        switch (flag) {
            case REMOVEBLACK_LOAD_DATA:
            case LOAD_DATA:
                msg.obj = "服务器返回异常,请重试";
                mtotalHandler.sendMessage(msg);
                break;
            case R.id.rb_guanzhu:
                msg.obj = "关注失败";
                mtotalHandler.sendMessage(msg);
                break;
            case ADDTOBLACK:
                msg.obj = "添加黑名单失败";
                mtotalHandler.sendMessage(msg);
                break;
            case TV_NAME:
                Message msg2 = new Message();
                msg2.what = DOWNLOADED_ERROR;
                msg2.obj = "修改备注名失败";
                mtotalHandler.sendMessage(msg2);
                break;
            case POP_DIALOG:
                POP_DIALOG_TAPED = false;

                msg.obj = "获取信息失败";
                mtotalHandler.sendMessage(msg);
                break;
        }
    }

    public static final int OUT_BLACK_MENU = 10;
    public static final int BLACK_MENU = 11;
    public static final int FRIEND_MENU = 12;
    public static final int STRANGER_MENU = 13;

    public int currentMenu = STRANGER_MENU;//用于标记当前所显示的菜单类型


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

                case LOADING:
                    act.pg = UtilsTool.initProgressDialog(act.mContext, "加载中.....");
                    act.pg.show();
                    MyToast.show(act.mContext, msg.obj + ";");
                    break;
                // 正在下载

                case REMOVEBLACK_LOAD_DATA:
                    if (act.pg != null && act.pg.isShowing()) {
                        act.pg.dismiss();
                    }

                    if (act.mUserDetail.isInBlackList()) {
                        showBottomMenu(act, BLACK_MENU);
                    } else if (act.mUserDetail.isFoucs()) {
//                        act.rb_guanzhu.setText("取消关注");
//                        act.ln_guanzhu.setVisibility(View.GONE);
//                        act.v_line.setVisibility(View.GONE);
                        showBottomMenu(act, FRIEND_MENU);
                    } else {
//                        act.rb_guanzhu.setText("关注");
//                        act.ln_guanzhu.setVisibility(View.VISIBLE);
//                        act.v_line.setVisibility(View.VISIBLE);
                        showBottomMenu(act, STRANGER_MENU);
                    }
                    break;
                case DOWNLOADED_LocalUser:
//                    if (act.userPicAdapter == null) {
//                        act.initTopPicAdapter();

//                        act.userPicAdapter.setData(act.getUserInfoDetail().getPhotos());

                    if (act.pg != null && act.pg.isShowing()) {
                        act.pg.dismiss();
                    }

                    act.contentRAdapter.setData2(act.mUserDetailList);

//                        act.mPicRecyclerView.setAdapter(act.userPicAdapter);


                    if (act.mUserDetail.isInBlackList()) {
                        showBottomMenu(act, BLACK_MENU);
                    } else if (act.mUserDetail.isFoucs()) {
//                        act.rb_guanzhu.setText("取消关注");
//                        act.ln_guanzhu.setVisibility(View.GONE);
//                        act.v_line.setVisibility(View.GONE);
                        showBottomMenu(act, FRIEND_MENU);
                    } else {
//                        act.rb_guanzhu.setText("关注");
//                        act.ln_guanzhu.setVisibility(View.VISIBLE);
//                        act.v_line.setVisibility(View.VISIBLE);
                        showBottomMenu(act, STRANGER_MENU);
                    }


//                    如果是自己则隐藏底部栏
                    if (act.mUserDetail.getId().equals(WowuApp.UserId)) {
                        act.ln_guanzhu.setVisibility(View.GONE);
                        act.v_line.setVisibility(View.GONE);
                    }

                    act.tv_name.setText(act.mUserDetail.getRemarkName() == null ? act.mUserDetail.getName() : act.mUserDetail.getRemarkName());
                    act.tv_user_character.setText(act.mUserDetail.getDisposition());//act.mUserDetail.getAge() == 0 ? act.mUserDetail.getDisposition() : act.mUserDetail.getAge() + " | " +
                    if (act.mUserDetail.getDistance() != null && act.mUserDetail.getBefore() != null) {
//                        act.tv_dis_time.setText(act.mUserDetail.getDistance() + "|" + act.mUserDetail.getBefore());
                        act.tv_dis_time.setText(act.mUserDetail.getAge() + "岁 | " + act.mUserDetail.getDistance());


                    }
                    if (act.mUserDetail.getIcon() != null && act.mUserDetail.getIcon().getUrl() != null) {
                        act.user_pic.setImageURI(Uri.parse(act.mUserDetail.getIcon().getUrl()));
                    }

                    if (act.mUserDetail.getGender() == 1) {
//                        act.iv_sanguan_pick.setImageResource(R.drawable.mate_blue);
                    } else {
                        act.iv_sanguan_pick.setImageResource(R.drawable.mate_red);
                    }
                    break;
                case DOWNLOADED_VISIABLE:
                    MyToast.show(act.mContext, "关注成功");
//                    act.rb_guanzhu.setText("取消关注");
//                    act.ln_guanzhu.setVisibility(View.GONE);
//                    act.v_line.setVisibility(View.GONE);
                    showBottomMenu(act, FRIEND_MENU);
                    break;
                case DOWNLOADED_GONE:
                    MyToast.show(act.mContext, "取消关注成功");
//                    act.rb_guanzhu.setText("关注");
//                    act.rb_guanzhu.setVisibility(View.VISIBLE);
//                    act.v_line.setVisibility(View.VISIBLE);
                    showBottomMenu(act, STRANGER_MENU);
                    break;
                case BLACK_MENU:
                    MyToast.show(act.mContext, "解除黑名单成功");
//                    act.rb_guanzhu.setText("关注");
//                    act.rb_guanzhu.setVisibility(View.VISIBLE);
//                    act.v_line.setVisibility(View.VISIBLE);
                    showBottomMenu(act, OUT_BLACK_MENU);
                    break;


                case DOWNLOADED:
                    if (act.pg != null && act.pg.isShowing()) {
                        act.pg.dismiss();
                    }
                    if (act != null && act.mSanGuan != null) {
                        if (act.mSanGuan.getSuccessfully() != 2) {
                            act.showSanguanPickDialog();
                        } else {
//                            MyToast.show(act.mContext,  "普通会员每天只可配对三次");
                            UtilsTool temp = new UtilsTool();
                            temp.showSetVipDialog(act);
                        }
                    } else {
                        MyToast.show(act.mContext, "获取失败，请重试");
                    }
                    break;
                case REFERSH_DATA:
                    MyToast.show(act.mContext, "修改备注名成功");
                    act.contentRAdapter.notifyDataSetChanged();
                    break;
                case DOWNLOADED_ERROR:
                    if (act.pg != null && act.pg.isShowing()) {
                        act.pg.dismiss();
                    }
                    MyToast.show(act.mContext, msg.obj + ";");
                    break;
                case POP_XINGGE_DIALOG:
                    if (act != null) {
                        act.showCharacterInfoDialog();
                    }
                    break;
            }
        }

        private void showBottomMenu(final UserInfoEditActivity act, int type) {
            act.currentMenu = type;
            switch (type) {
                case STRANGER_MENU:
                    act.rb_guanzhu.setText("关注");
                    act.ln_guanzhu.setVisibility(View.VISIBLE);
                    act.v_line.setVisibility(View.VISIBLE);
                    act.ln_chat.setVisibility(View.VISIBLE);
                    break;
                case FRIEND_MENU:
                    act.rb_guanzhu.setText("取消关注");
                    act.ln_guanzhu.setVisibility(View.GONE);
                    act.v_line.setVisibility(View.GONE);
                    act.ln_chat.setVisibility(View.VISIBLE);
                    break;
                case BLACK_MENU:
                    act.rb_guanzhu.setText("解除黑名单");
                    act.ln_guanzhu.setVisibility(View.VISIBLE);
                    act.v_line.setVisibility(View.GONE);
                    act.ln_chat.setVisibility(View.GONE);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
//                                EMClient.getInstance().contactManager().addUserToBlackList(act.mUserDetail.getEasemobId(), true);//需异步处理
                                EMClient.getInstance().contactManager().addUserToBlackList(act.mUserDetail.getId(), true);//需异步处理
                            } catch (HyphenateException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                    break;
                case OUT_BLACK_MENU:
                    act.rb_guanzhu.setText("关注");
                    act.ln_guanzhu.setVisibility(View.VISIBLE);
                    act.v_line.setVisibility(View.VISIBLE);
                    act.ln_chat.setVisibility(View.VISIBLE);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
//                                EMClient.getInstance().contactManager().removeUserFromBlackList(act.mUserDetail.getEasemobId());
                                EMClient.getInstance().contactManager().removeUserFromBlackList(act.mUserDetail.getId());//需异步处理
                            } catch (HyphenateException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    break;
            }
        }
    }


    public void setLoadInfo(String totalresult) throws JSONException {
        Gson gson = new GsonBuilder().create();
        if (totalresult != null) {
            java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<UserInfoDetail>() {
            }.getType();
            mUserDetailList.clear();
            mUserDetail = gson.fromJson(totalresult, type);
            mUserDetailList.add(mUserDetail);


            if (mLocalUser == null && mUserDetail != null) {
                mLocalUser = new LocalUser.DataBean();
                mLocalUser.setUserId(mUserDetail.getId());
                mLocalUser.setName(mUserDetail.getName());
                mLocalUser.setPhotoUrl(mUserDetail.getIcon().getUrl());
            }

//            提前加载性格类型信息，实现快速响应
            if (mUserDetail != null) {
                loadPopInfo(WowuApp.GetDispositionNicknamesURL + "?id=" + mUserDetail.getDispositionId(), PRE_POP_DIALOG);
            }

        }

    }


    public UserInfoDetail getUserInfoDetail() {
        return mUserDetail;
    }

    private CommRecyclerAdapter initContentAdapter() {
        contentRAdapter = new CommRecyclerAdapter<UserInfoDetail>(mContext, R.layout.activity_user_info_edit_content_item) {
            @Override
            public void convert(CommRecyclerViewHolder viewHolder, UserInfoDetail mainMessage) {
                mPicRecyclerView = (RecyclerView) viewHolder.getView(R.id.pullLoadMoreRecyclerView);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
                linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                mPicRecyclerView.setLayoutManager(linearLayoutManager);
                initTopPicAdapter();
                mPicRecyclerView.setAdapter(userPicAdapter);
                userPicAdapter.setData(mainMessage.getPhotos());


                if (mainMessage.isIsVip()) {
//                    viewHolder.getView(R.id.tv_xianzhi_huiyuan).setVisibility(View.GONE);
//                    viewHolder.getView(R.id.iv_xianzhi_huiyuan).setVisibility(View.VISIBLE);
                    if( mainMessage.getVipLevel()==5 ){
                        ((ImageView)viewHolder.getView(R.id.iv_xianzhi_huiyuan)).setImageResource(R.drawable.svip);
                    }else{
                        ((ImageView)viewHolder.getView(R.id.iv_xianzhi_huiyuan)).setImageResource(R.drawable.vip);
                    }

                } else {
//                    viewHolder.setText(R.id.tv_xianzhi_huiyuan, "普通用户");
//                    viewHolder.getView(R.id.tv_xianzhi_huiyuan).setVisibility(View.VISIBLE);
//                    viewHolder.getView(R.id.iv_xianzhi_huiyuan).setVisibility(View.GONE);
                    ((ImageView)viewHolder.getView(R.id.iv_xianzhi_huiyuan)).setImageResource(R.drawable.no_vip);
                }
                viewHolder.setText(R.id.tv_beizhu, mainMessage.getRemarkName());
                final String tempTag = mainMessage.getRemarkName();
                viewHolder.getView(R.id.rt_beizhu).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, OwnerInfoUpdateTextActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("title", "备注名");
                        bundle.putString("info", tempTag);
                        intent.putExtras(bundle);
                        startActivityForResult(intent, TV_NAME);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                });


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


                if (userPicAdapter.getData().size() == 1) {
                    onBubbleClick(mContext, ((UserInfoDetail.PhotosBean) userPicAdapter.getData().get(position)).getFullUrl(), ((UserInfoDetail.PhotosBean) userPicAdapter.getData().get(position)).getId());
                } else {
                    ArrayList<String> tempimageUrls = new ArrayList<String>();
                    for (int i = 0; i < userPicAdapter.getData().size(); i++) {
                        tempimageUrls.add(((UserInfoDetail.PhotosBean) userPicAdapter.getData().get(i)).getFullUrl());
                    }
                    imageBrower(position, tempimageUrls);
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


    protected void onBubbleClick(Context context, String url, String name) {
//        String url = "http://img1.imgtn.bdimg.com/it/u=4033491868,3189899599&fm=21&gp=0.jpg";
//        String url = "http://xzxj.oss-cn-shanghai.aliyuncs.com/user/97f28a44-2bab-4140-963a-b5277351ae74.jpg";

        LogUtils.i(TAG, "：：" + WowuApp.tempPicPath + name + "." + UtilsTool.getFileType(url));

        Intent intent = new Intent(context, EaseShowBigImageActivity.class);
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + WowuApp.tempPicPath + name + "." + UtilsTool.getFileType(url));
        if (file.exists()) {
            Uri uri = Uri.fromFile(file);
            intent.putExtra("uri", uri);
        } else {
            // The local full size pic does not exist yet.
            // ShowBigImage needs to download it from the server
            // first
            intent.putExtra("secret", url);
            intent.putExtra("remotepath", url);
//        intent.putExtra("localUrl", WowuApp.tempPicPath+UtilsTool.getFileName(url));
            intent.putExtra("localUrl", WowuApp.tempPicPath + name + "." + UtilsTool.getFileType(url));
            intent.putExtra("loadType", "wuho");

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

                    LogUtils.i(TAG, WowuApp.MatchURL + "?userId=" + chatUserId + "toChatUsername::" + chatUserId);
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
        View view = getLayoutInflater().inflate(com.hyphenate.easeui.R.layout.dialog_sanguan_pick, null);
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
        tv_sanguan_user1.setText(mContext.getSharedPreferences(WowuApp.PREFERENCE_KEY, mContext.MODE_PRIVATE).getString("Name", "http://#"));
        if (mSanGuan != null) {
            tv_sanguan_user1.setText(mSanGuan.getUserName1());
            tv_sanguan_user2.setText(mSanGuan.getUserName2());
        }

        ImageView iv_link = (ImageView) view.findViewById(com.hyphenate.easeui.R.id.iv_link);
        if (mSanGuan.getUserGender2() == 1) {
            iv_link.setImageResource(R.drawable.btn_sanguanpei_blue);
        } else {
//            act.iv_sanguan_pick.setImageResource(R.drawable.mate_red);
        }


        TextView genderm1 = (TextView) view.findViewById(com.hyphenate.easeui.R.id.tvage_gender1_male);
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
        genderm1.setText(mSanGuan.getUserAge1() == 0 ? mSanGuan.getUserDisposition1() : mSanGuan.getUserAge1() + " | " + mSanGuan.getUserDisposition1());

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

        genderm2.setText(mSanGuan.getUserAge2() == 0 ? mSanGuan.getUserDisposition2() : mSanGuan.getUserAge2() + " | " + mSanGuan.getUserDisposition2());


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


            SimpleDraweeView draweeView1 = (SimpleDraweeView) view.findViewById(com.hyphenate.easeui.R.id.sdv_sanguan_pic1);
//        if (toChatUsername != null && EaseImageUtils.usersPhotoUrl.get(toChatUsername) != null) {
//            draweeView.setImageURI(Uri.parse(EaseImageUtils.usersPhotoUrl.get(toChatUsername)));//"http://w messageList.getItem(0).getTo()
//        } else if (iconPath != null) {
//            draweeView.setImageURI(Uri.parse(iconPath));//"http://w messageList.getItem(0).getTo()
//        }


            if (mSanGuan.getUserPhotoUrl1() != null) {
                draweeView1.setImageURI(Uri.parse(mSanGuan.getUserPhotoUrl1()));
            }
            SimpleDraweeView draweeView2 = (SimpleDraweeView) view.findViewById(com.hyphenate.easeui.R.id.sdv_sanguan_pic2);
//        draweeView2.setImageURI(Uri.parse( EaseImageUtils.usersPhotoUrl.get( mContext.getSharedPreferences(WowuApp.PREFERENCE_KEY,mContext.MODE_PRIVATE).getString("UserId", "http://#"))));//"http://w messageList.getItem(0).getFrom() WowuApp.UserId EMClient.getInstance().getCurrentUser()
//        draweeView2.setImageURI(Uri.parse(WowuApp.iconPath));
            if (mSanGuan.getUserPhotoUrl1() != null) {
                draweeView2.setImageURI(Uri.parse(mSanGuan.getUserPhotoUrl2()));
            }
            GenericDraweeHierarchy hierarchy1 = draweeView1.getHierarchy();
            GenericDraweeHierarchy hierarchy2 = draweeView2.getHierarchy();

            if (mSanGuan.getUserGender1() == 1) {
                hierarchy1.setControllerOverlay(getResources().getDrawable(R.drawable.overlay_male1));
            } else {
                hierarchy1.setControllerOverlay(getResources().getDrawable(R.drawable.overlay_fmale1));
            }

            if (mSanGuan.getUserGender2() == 1) {
                hierarchy2.setControllerOverlay(getResources().getDrawable(R.drawable.overlay_male1));
            } else {
                hierarchy2.setControllerOverlay(getResources().getDrawable(R.drawable.overlay_fmale1));
            }
        }


        // 设置显示动画
        window.setWindowAnimations(com.hyphenate.easeui.R.style.main_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = getWindowManager().getDefaultDisplay().getHeight();
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.MATCH_PARENT;//WRAP_CONTENT

        // 设置显示位置
        dialog.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        dialog.setCanceledOnTouchOutside(true);
        if (!((Activity) mContext).isFinishing()) {
            //show dialog
            dialog.show();
        }
    }


    private void showUserinfoSetDialog(int type) {
        View view = getLayoutInflater().inflate(R.layout.pop_userinfo_set, null);
        final Dialog dialog = new Dialog(mContext, R.style.transparentFrameWindowStyle);//
        dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();

        view.findViewById(R.id.home_member_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });

        if (type == 0) {
            view.findViewById(R.id.rt_character_history).setVisibility(View.GONE);
            view.findViewById(R.id.rt_v_line).setVisibility(View.GONE);
//            view.findViewById(R.id.rt_character_retest).setVisibility(View.GONE);
        }
        view.findViewById(R.id.rt_character_history).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
//                Intent intent = new Intent(mContext, CharacterTestHistoryActivity.class);
//                startActivity(intent);
//                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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
        });

        RelativeLayout rt_character_retest = (RelativeLayout) view.findViewById(R.id.rt_character_retest);
        if (mUserDetail.isInBlackList()) {
            rt_character_retest.setVisibility(View.GONE);
        }

        rt_character_retest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
//                Intent  intent2 = new Intent(mContext, CharacterTestActivity.class);
//                intent2.putExtra("tetsType",  2);
//                intent2.putExtra("registerMode",false);
//                startActivity(intent2);
//                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                addToBlack();

            }
        });
        view.findViewById(R.id.rt_character_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
                Intent intent = new Intent(mContext, UserJuBaoActivity.class);
                intent.putExtra("userId", chatUserId);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            }
        });

        // 设置显示动画
        window.setWindowAnimations(R.style.main_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = getWindowManager().getDefaultDisplay().getHeight();
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.MATCH_PARENT;//WRAP_CONTENT
        wl.gravity = Gravity.BOTTOM;

//        window.setAttributes(wl);
        window.setGravity(Gravity.BOTTOM);
        // 设置显示位置
        dialog.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        dialog.setCanceledOnTouchOutside(true);
        if (!((Activity) mContext).isFinishing()) {
            //show dialog
            dialog.show();
        }
    }

    private void addToBlack() {
        if (mUserDetail != null) {

            if (mUserDetail != null) {
                mUserDetail.setInBlackList(true);
                Message msg = new Message();
                msg.what = DOWNLOADED_LocalUser;
                mtotalHandler.sendMessage(msg);
            }

            try {
                JSONObject json = new JSONObject();
                json.put("userId", mUserDetail.getId());//APP版本号
                loadDataService.loadPostJsonRequestData(WowuApp.JSON, WowuApp.MoveToBlacklistURL + "?userId=" + mUserDetail.getId(), json.toString(), ADDTOBLACK);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 性格谱弹出窗
     */
    int currentItem = 0;
//    ImageView[] imgs;

    private void showCharacterInfoDialog() {
        currentItem = 0;
        View view = getLayoutInflater().inflate(R.layout.dialog_character_info, null);
        final Dialog dialog = new Dialog(mContext, com.hyphenate.easeui.R.style.Dialog_Fullscreen2);
//        final Dialog dialog = new Dialog(mContext, com.hyphenate.easeui.R.style.transparentFrameWindowStyle);
        dialog.setContentView(view, new ViewGroup.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        Window window = dialog.getWindow();

        final int popSize = mCharacterPuPopList.size();
        final ViewPager viewPager = (ViewPager) view.findViewById(R.id.main_vp_container);
        final TextView ib_character_qiehuan_info = (TextView) view.findViewById(R.id.ib_character_qiehuan_info);

        ArrayList<View> views = new ArrayList<View>();
        LayoutInflater inflater = getLayoutInflater();
        ib_character_qiehuan_info.setText(1 + "/" + popSize);
        for (int i = 0; i < popSize; i++) {
            View view1 = inflater.inflate(R.layout.item_xinggepu, null);
            SimpleDraweeView character_label_pic = (SimpleDraweeView) view1.findViewById(R.id.character_label_pic0);
            TextView tv_character_part1 = (TextView) view1.findViewById(R.id.tv_character_part0);
            TextView tv_character_pop_title = (TextView) view1.findViewById(R.id.tv_character_pop_title);
            tv_character_pop_title.setText(mCharacterPuPopList.get(i).getTitle());
            tv_character_part1.setText(mCharacterPuPopList.get(i).getText());
            if (mCharacterPuPopList.get(i).getPhotoUrl() != null) {
                character_label_pic.setImageURI(Uri.parse(mCharacterPuPopList.get(i).getPhotoUrl()));
            }
            views.add(view1);
        }
        viewPager.setAdapter(new MyViewPagerAdapter(views));
        viewPager.setCurrentItem(0);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
                currentItem=position;
                ib_character_qiehuan_info.setText(position + 1 + "/" + popSize);
//                    }
//                });
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });


//        view.findViewById(R.id.tv_sanguan_close).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//                dialog.dismiss();
//            }
//        });
        view.findViewById(R.id.tv_return_pop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.ib_character_qiehuan_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentItem > 0) {
                    currentItem--;
                    viewPager.setCurrentItem(currentItem);
                }
            }
        });
        view.findViewById(R.id.ib_character_qiehuan_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentItem < popSize - 1) {
                    currentItem++;
                    viewPager.setCurrentItem(currentItem);
                }
            }
        });


//      分享到朋友圈
        view.findViewById(R.id.iv_pop_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        wechatShare(SendMessageToWX.Req.WXSceneTimeline, mCharacterPuPopList.get(currentItem).getId(),
                                mCharacterPuPopList.get(currentItem).getPhotoUrl(), mCharacterPuPopList.get(currentItem).getTitle());

                        LogUtils.i(TAG, ":　" +  mCharacterPuPopList.get(currentItem).getId()+":"+
                                mCharacterPuPopList.get(currentItem).getPhotoUrl()+":"+ mCharacterPuPopList.get(currentItem).getTitle());
                    }
                }).start();
            }
        });

        // 设置显示动画
        window.setWindowAnimations(com.hyphenate.easeui.R.style.main_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = getWindowManager().getDefaultDisplay().getHeight();
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.MATCH_PARENT;//WRAP_CONTENT

        // 设置显示位置
        dialog.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        dialog.setCanceledOnTouchOutside(true);
        if (!((Activity) mContext).isFinishing()) {
            //show dialog
            dialog.show();
        }

    }


    /**
     * 下面的内容为弹出分享到朋友圈
     */

    private final int LoadingError = 1;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LoadingError:
                    MyToast.show(mContext, "当前版本不支持分享功能");
                    break;
            }
            super.handleMessage(msg);
        }
    };
    private IWXAPI wxApi;
    private void wechatShare(final int flag, final String id, String photoUrl, final String title) {
        wxApi = WXAPIFactory.createWXAPI(mContext, WowuApp.WeChat_APP_ID, false);
        wxApi.registerApp(WowuApp.WeChat_APP_ID);
        if (!wxApi.isWXAppSupportAPI()) {
            Message msg = new Message();
            msg.what = LoadingError;
            mHandler.sendMessage(msg);
            return;
        }
        UtilsTool temp=new UtilsTool();
        temp.wechatShare(mContext,wxApi,  flag, id,  photoUrl,title,"2");
    }



}

package com.wuwo.im.activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hyphenate.chatuidemo.db.DemoDBManager;
import com.hyphenate.chatuidemo.db.UserDao;
import com.hyphenate.easeui.ui.EaseShowBigImageActivity;
import com.wuwo.im.adapter.CommRecyclerAdapter;
import com.wuwo.im.adapter.CommRecyclerViewHolder;
import com.wuwo.im.adapter.PicAdapter;
import com.wuwo.im.bean.UserInfoDetail;
import com.wuwo.im.config.ExitApp;
import com.wuwo.im.config.WowuApp;
import com.wuwo.im.util.MyToast;
import com.wuwo.im.util.UtilsTool;
import com.wuwo.im.view.SearchView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.service.LoadserverdataService;
import com.zhy.http.okhttp.service.loadServerDataListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;

import im.wuwo.com.wuwo.R;

/**
 * desc OwnerInfoEditActivity
 *
 * @author 王明远
 * @日期： 2016/6/9 0:08
 * @版权:Copyright All rights reserved.  http://xzxj.oss-cn-shanghai.aliyuncs.com/user/7fe11e87-cd32-4684-8220-73c8bfc98431.jpg
 */

public class OwnerInfoEditActivity extends BaseLoadActivity implements
        DatePickerDialog.OnDateSetListener, View.OnClickListener, loadServerDataListener, PicAdapter.OnItemClickLitener {
    LoadserverdataService loadDataService;
    Context mContext = null;

    Gson gson = new GsonBuilder().create();
    SharedPreferences mSettings;
    SharedPreferences.Editor editor;
    private RecyclerView mPicRecyclerView;
    private int mCount = 0;
    //    ArrayList<UserInfoDetail> meeting_userlist = new ArrayList<UserInfoDetail>(); //记录所有的最新消息
    private SearchView search_view;
    private String searchinfo;
    //    CommRecyclerAdapter userPicAdapter;
    PicAdapter userPicAdapter;

    mHandlerWeak mtotalHandler;

    private SharedPreferences settings;

    UserInfoDetail mUserDetail = new UserInfoDetail();
    ArrayList<UserInfoDetail> mUserDetailList = new ArrayList<UserInfoDetail>(); //记录所有的最新消息
    ArrayList<UserInfoDetail.PhotosBean> removedList = new ArrayList<UserInfoDetail.PhotosBean>(); //记录所有的最新消息


    RecyclerView rlist_view_content;
    CommRecyclerAdapter contentRAdapter;
    SimpleDraweeView user_pic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        mContext = this;
        loadDataService = new LoadserverdataService(this);
        ExitApp.getInstance().addOpenedActivity(this);
        setContentView(R.layout.activity_owner_info_edit);

//        Intent intent = getIntent();
//        if(getIntent()!=null){
//            mUserDetail = (UserInfoDetail)intent.getSerializableExtra("UserDetail");
//        }
        settings = this.getSharedPreferences(WowuApp.PREFERENCE_KEY, MODE_PRIVATE);

        initView();

    }

    private void initView() {
        findViewById(R.id.return_back).setOnClickListener(this);
        findViewById(R.id.tx_top_right).setOnClickListener(this);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });


        mPicRecyclerView = (RecyclerView) findViewById(R.id.pullLoadMoreRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mPicRecyclerView.setLayoutManager(linearLayoutManager);
        mtotalHandler = new mHandlerWeak(this);

        loadData();

        rlist_view_content = (RecyclerView) findViewById(R.id.rlist_view_content);
        LinearLayoutManager mlinearLayoutManager = new LinearLayoutManager(mContext);
        mlinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rlist_view_content.setLayoutManager(mlinearLayoutManager);
        rlist_view_content.setVerticalScrollBarEnabled(true);

//        initTopPicAdapter();

        user_pic = (SimpleDraweeView) findViewById(R.id.user_pic);
        user_pic.setOnClickListener(this);


        userPicAdapter = new PicAdapter(mContext, null);
        userPicAdapter.setOnItemClickLitener(this);
        initContentAdapter();
        rlist_view_content.setAdapter(contentRAdapter);
        mPicRecyclerView.setAdapter(userPicAdapter);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.return_back:
                this.finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;

            case R.id.tx_top_right:
                pd = UtilsTool.initProgressDialog(mContext, "请稍后...");
                pd.show();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        upload2Server();
                    }
                }).start();
                break;
            case R.id.user_pic://点击展示头像


                if (((UserInfoDetail.PhotosBean) userPicAdapter.getList().get(currentPosition)).getLocalPath() != null) {
//                Log.i("为什么不显示呢，本地", "：：" + ((UserInfoDetail.PhotosBean) userPicAdapter.getList().get(position)).getLocalPath());
                    Intent intent = new Intent(mContext, EaseShowBigImageActivity.class);
                    File file = new File(((UserInfoDetail.PhotosBean) userPicAdapter.getList().get(currentPosition)).getLocalPath());
                    if (file.exists()) {
                        Uri uri = Uri.fromFile(file);
                        intent.putExtra("uri", uri);
                    }
                    mContext.startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }else{

                    ArrayList<String> tempimageUrls = new ArrayList<String>();
                    if (currentPosition == -1) {//说明没有换过头像
                        tempimageUrls.add(mUserDetailList.get(0).getIcon().getUrl());
                    }
                    for (int i = 0; i < userPicAdapter.getList().size(); i++) {
                        tempimageUrls.add(((UserInfoDetail.PhotosBean) userPicAdapter.getList().get(i)).getFullUrl());
                    }
                    imageBrower(currentPosition, tempimageUrls);
                }


                break;
        }
    }


    //用于表示默认的头像展示
    private int currentPosition = -1;

    private final int LOAD_DATA = 1;

    private void loadData() {

        if (UtilsTool.checkNet(mContext)) {
            loadDataService.loadGetJsonRequestData(OkHttpUtils.GetUserInfoURL + "?userId=" + WowuApp.UserId, LOAD_DATA);
        } else {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
//            读取缓存信息
                        String CacheJsonString = DemoDBManager.getInstance().getCacheJson(UserDao.CACHE_MAIN_OWNERINFO);
                        if (CacheJsonString != null) {
                            setLoadInfo(CacheJsonString);
                        }
                        Message msg = new Message();
                        msg.what = DOWNLOADED_LocalUser;
                        mtotalHandler.sendMessage(msg);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

    }


    public static final int DOWNLOADED_LocalUser = 0;
    public static final int DOWNLOADED_ERROR = 1;
    public static final int REFERSH_DATA = 2;
    public static final int ADD_ATTACHMENT = 1000;

    @Override
    public void loadServerData(final String response, int flag) {
        switch (flag) {
            case LOAD_DATA:
                try {
                    if (response != null) {
                        setLoadInfo(response);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                DemoDBManager.getInstance().saveCacheJson(UserDao.CACHE_MAIN_OWNERINFO, response);
                            }
                        }).start();
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
            case R.id.tx_top_right:
//                MyToast.show(mContext, "修改成功");
                if (pd != null) {
                    pd.dismiss();
                }
                WowuApp.iconPath = mUserDetail.getIcon().getUrl();
                editor = getSharedPreferences(WowuApp.PREFERENCE_KEY, MODE_PRIVATE).edit();
                editor.putString("iconPath", WowuApp.iconPath);
                editor.commit();
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
            case R.id.tx_top_right:
                MyToast.show(mContext, response);
                if (pd != null) {
                    pd.dismiss();
                }
                break;
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        if (position == userPicAdapter.getList().size()) {
            addAttachment();
        } else {


            if (((UserInfoDetail.PhotosBean) userPicAdapter.getList().get(position)).getLocalPath() != null) {
//                Log.i("为什么不显示呢，本地", "：：" + ((UserInfoDetail.PhotosBean) userPicAdapter.getList().get(position)).getLocalPath());
                Intent intent = new Intent(mContext, EaseShowBigImageActivity.class);
                File file = new File(((UserInfoDetail.PhotosBean) userPicAdapter.getList().get(position)).getLocalPath());
                if (file.exists()) {
                    Uri uri = Uri.fromFile(file);
                    intent.putExtra("uri", uri);
                }
                mContext.startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            } else {
/*                //                onBubbleClick(mContext, ((UserInfoDetail.PhotosBean) userPicAdapter.getList().get(position)).getFullUrl(), ((UserInfoDetail.PhotosBean) userPicAdapter.getList().get(position)).getId());
                ArrayList<String> tempimageUrls = new ArrayList<String>();
                for (int i = 0; i < userPicAdapter.getList().size(); i++) {
                    tempimageUrls.add(((UserInfoDetail.PhotosBean) userPicAdapter.getList().get(i)).getFullUrl());
                }
                imageBrower(position, tempimageUrls);*/
                currentPosition=position;
                user_pic.setImageURI(Uri.parse(((UserInfoDetail.PhotosBean) userPicAdapter.getList().get(position)).getFullUrl()));
            }
        }
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
//        Log.i("为什么不显示呢，你麻痹", "：：" + WowuApp.tempPicPath + name + "." + UtilsTool.getFileType(url));

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


    @Override
    public void onItemLongClick(View view, final int position) {

        if (position == userPicAdapter.getList().size()) {
//            addAttachment();
        } else {
            final UserInfoDetail.PhotosBean removedInfos = (UserInfoDetail.PhotosBean) userPicAdapter.getItem(position);
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    this);
            builder.setTitle("附件删除");
            builder.setIcon(getResources().getColor(
                    android.R.color.transparent));
            builder.setMessage("是否删除当前附件");
            builder.setNegativeButton("确定",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                            int which) {
//                            如果是删除之前已经上传的图片，则将删除的图片信息保存下来
                            if (removedInfos.getLocalPath() == null) {
                                removedList.add(removedInfos);
                            }
                            userPicAdapter.removeItem(position);
                        }
                    });
            builder.setPositiveButton("取消", null);
            builder.create().show();
        }
    }


    private void addAttachment() {
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("image/*");

//        Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
        Intent intentFromGallery = new Intent();
        if (Build.VERSION.SDK_INT < 19) {
            intentFromGallery = new Intent(Intent.ACTION_GET_CONTENT);
            intentFromGallery.setType("image/*");
        } else {
            intentFromGallery = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }

        try {
            startActivityForResult(Intent.createChooser(intentFromGallery, "请选择要上传的图片"), ADD_ATTACHMENT);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "无法选择文件，请先安装文件管理器", Toast.LENGTH_SHORT).show();
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
//                    if (act.userPicAdapter == null) {
////                        act.initTopPicAdapter();
//                        act.userPicAdapter.setData(act.getUserInfoDetail().getPhotos());
//                        act.contentRAdapter.setData(act.mUserDetailList);
////                        act.mPicRecyclerView.setAdapter(act.userPicAdapter);
//                    } else {
//                        act.userPicAdapter.setData(act.getUserInfoDetail().getPhotos());
//                    }
                    act.userPicAdapter.setData(act.getUserInfoDetail().getPhotos());
                    act.contentRAdapter.setData(act.mUserDetailList);

                    if (act.mUserDetailList != null && act.mUserDetailList.size() > 0) {
                        act.user_pic.setImageURI(Uri.parse(act.mUserDetailList.get(0).getIcon().getUrl()));
                    }


                    act.mPicRecyclerView.smoothScrollToPosition(act.getUserInfoDetail().getPhotos().size());

                    break;
                case ADD_ATTACHMENT:
                    UserInfoDetail.PhotosBean temp = new UserInfoDetail.PhotosBean();
                    temp.setLocalPath(msg.obj + "");
                    act.userPicAdapter.addItem(temp);
                    break;
                case REFERSH_DATA:
                    act.contentRAdapter.notifyDataSetChanged();
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


//    public static final int ln_yingwen_nicheng = 100;
    public static final int tv_zhongwen_nicheng1 = 101;
    public static final int ln_geren_jieshao = 102;
//    public static final int ln_shenfenbiaoqian = 103;
    public static final int ln_zhiye = 104;
    public static final int ln_school = 105;
    public static final int tv_shenghuodidian = 106;
    public static final int ln_gongzuodidian = 107;
    public static final int ln_changchumodi = 108;
//    public static final int tv_quguo_jingdian = 109;
    public static final int ln_jiaxiang = 110;


    private CommRecyclerAdapter initContentAdapter() {
        contentRAdapter = new CommRecyclerAdapter<UserInfoDetail>(mContext, R.layout.activity_owner_info_edit_content_item) {
            @Override
            public void convert(CommRecyclerViewHolder viewHolder, UserInfoDetail mainMessage) {

                int temp_huiyuan=0;
                if( mainMessage.getUserNumber()!=null){
                    temp_huiyuan++;
                    viewHolder.setText(R.id.tv_xianzhihao, mainMessage.getUserNumber());}

                if( mainMessage.isIsVip()){
                    temp_huiyuan++;
                    viewHolder.setText(R.id.tv_xianzhi_huiyuan, "会员用户");}

                viewHolder.setText(R.id.tv_huiyuan_info,temp_huiyuan+"/2");


                int temp_jiben=0;
                if( mainMessage.getName()!=null){
                    temp_jiben++;
                    viewHolder.setText(R.id.tv_zhongwen_nicheng1, mainMessage.getName());}


                if(  mainMessage.getBirthday()!=null){
                    temp_jiben++;
                    viewHolder.setText(R.id.ln_birthday, mainMessage.getBirthday() + "");}

                if(mainMessage.getHome()!=null){
                    temp_jiben++;
                    viewHolder.setText(R.id.ln_jiaxiang, mainMessage.getHome());}

                if( mainMessage.getDescription()!=null){
                    temp_jiben++;
                    viewHolder.setText(R.id.ln_geren_jieshao, mainMessage.getDescription());}

                if( mainMessage.getJob()!=null){
                    temp_jiben++;
                    viewHolder.setText(R.id.ln_zhiye, mainMessage.getJob() + "");}

                if( mainMessage.getSchool()!=null){
                    temp_jiben++;
                    viewHolder.setText(R.id.ln_school, mainMessage.getSchool());}


//                @"保密", @"单身",@"恋爱中", @"已婚", @"同性",
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
                if( mainMessage.getMaritalStatus()!=0){
                    temp_jiben++;}

                viewHolder.setText(R.id.tv_jiben_info,temp_jiben+"/7");

                int temp_qita=0;
                if( mainMessage.getLifeAddress()!=null){
                    temp_qita++;
                    viewHolder.setText(R.id.tv_shenghuodidian, mainMessage.getLifeAddress());}
                if( mainMessage.getJobAddress()!=null){
                    temp_qita++;
                    viewHolder.setText(R.id.ln_gongzuodidian, mainMessage.getJobAddress());}
                if( mainMessage.getDailyAddress()!=null){
                    temp_qita++;
                    viewHolder.setText(R.id.ln_changchumodi, mainMessage.getDailyAddress());}

                viewHolder.setText(R.id.tv_qita_info,temp_qita+"/3");


                final String description = mainMessage.getDescription();
                final String name = mainMessage.getName();
                final String englishName = mainMessage.getEnglishName();
                final String tag = mainMessage.getTag();
                final String job = mainMessage.getJob();
                final String school = mainMessage.getSchool();
                final String jobAddress = mainMessage.getJobAddress();
                final String lifeAddress = mainMessage.getLifeAddress();
                final String dailyAddress = mainMessage.getDailyAddress();
                final String visitedAttractions = mainMessage.getVisitedAttractions();
                final String Home = mainMessage.getHome();

                viewHolder.getView(R.id.ln_feeling).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showFeelDialog();
                    }
                });

/*                viewHolder.getView(R.id.ln_yingwen_nicheng).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(OwnerInfoEditActivity.this, OwnerInfoUpdateTextActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("title", "英文昵称");
                        bundle.putString("info", englishName);
                        intent.putExtras(bundle);
                        startActivityForResult(intent, ln_yingwen_nicheng);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                });*/

                viewHolder.getView(R.id.tv_zhongwen_nicheng1).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(OwnerInfoEditActivity.this, OwnerInfoUpdateTextActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("title", "中文昵称");
                        bundle.putString("info", name);
                        intent.putExtras(bundle);
                        startActivityForResult(intent, tv_zhongwen_nicheng1);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                });
                viewHolder.getView(R.id.ln_geren_jieshao).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(OwnerInfoEditActivity.this, OwnerInfoUpdateTextActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("title", "个人介绍");
                        bundle.putString("info", description);
                        intent.putExtras(bundle);
                        startActivityForResult(intent, ln_geren_jieshao);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                });
/*                viewHolder.getView(R.id.ln_shenfenbiaoqian).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(OwnerInfoEditActivity.this, OwnerInfoUpdateTextActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("title", "身份标签");
                        bundle.putString("info", tag);
                        intent.putExtras(bundle);
                        startActivityForResult(intent, ln_shenfenbiaoqian);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                });*/

                viewHolder.getView(R.id.ln_zhiye).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(OwnerInfoEditActivity.this, OwnerInfoUpdateTextActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("title", "职业");
                        bundle.putString("info", job);
                        intent.putExtras(bundle);
                        startActivityForResult(intent, ln_zhiye);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                });

                viewHolder.getView(R.id.ln_school).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(OwnerInfoEditActivity.this, OwnerInfoUpdateTextActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("title", "学校");
                        bundle.putString("info", school);
                        intent.putExtras(bundle);
                        startActivityForResult(intent, ln_school);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                });

                viewHolder.getView(R.id.tv_shenghuodidian).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(OwnerInfoEditActivity.this, OwnerInfoUpdateTextActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("title", "生活地点");
                        bundle.putString("info", lifeAddress);
                        intent.putExtras(bundle);
                        startActivityForResult(intent, tv_shenghuodidian);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                });
                viewHolder.getView(R.id.ln_gongzuodidian).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(OwnerInfoEditActivity.this, OwnerInfoUpdateChooseActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("title", "工作地点");
                        bundle.putString("info", jobAddress);
                        intent.putExtras(bundle);
                        startActivityForResult(intent, ln_gongzuodidian);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);


                    }
                });
                viewHolder.getView(R.id.ln_changchumodi).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(OwnerInfoEditActivity.this, OwnerInfoUpdateChooseActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("title", "常出没地");
                        bundle.putString("info", dailyAddress);
                        intent.putExtras(bundle);
                        startActivityForResult(intent, ln_changchumodi);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                });

/*                viewHolder.getView(R.id.tv_quguo_jingdian).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Intent intent = new Intent(OwnerInfoEditActivity.this, OwnerInfoUpdateTextActivity.class);
                        Intent intent = new Intent(OwnerInfoEditActivity.this, OwnerInfoUpdateChooseActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("title", "去过景点");
                        bundle.putString("info", visitedAttractions);
                        intent.putExtras(bundle);
                        startActivityForResult(intent, tv_quguo_jingdian);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                });*/

                viewHolder.getView(R.id.ln_jiaxiang).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Intent intent = new Intent(OwnerInfoEditActivity.this, OwnerInfoUpdateTextActivity.class);
                        Intent intent = new Intent(OwnerInfoEditActivity.this, OwnerInfoUpdateChooseActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("title", "家乡");
                        bundle.putString("info", Home);
                        intent.putExtras(bundle);
                        startActivityForResult(intent, ln_jiaxiang);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                });


                viewHolder.getView(R.id.ln_birthday).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDatePickerDialog();

                    }
                });


            }

            @Override
            public int getItemViewType(int position) {
                return super.getItemViewType(position);
            }
        };
        return null;
    }


    public void showFeelDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("选择情感状态");

        String[] dialogItems = new String[]{"保密", "单身", "恋爱中", "已婚", "同性"};
        builder.setItems(dialogItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                mUserDetail.setMaritalStatus(which);
                mUserDetailList.remove(0);
                mUserDetailList.add(mUserDetail);

                Message msg = new Message();
                msg.what = REFERSH_DATA;
                mtotalHandler.sendMessage(msg);
            }
        });

        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == ADD_ATTACHMENT && null != data) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                String photoPath = "";
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        //int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        int columnIndex = cursor.getColumnIndexOrThrow(filePathColumn[0]);
                        photoPath = cursor.getString(columnIndex);
                    }

                } else {
                    if (selectedImage != null) {
                        String tmpPath = selectedImage.getPath();
                        if (tmpPath != null && (tmpPath.endsWith(".jpg") || tmpPath.endsWith(".png") || tmpPath.endsWith(".gif") || tmpPath.endsWith(".jpeg"))) {
                            photoPath = tmpPath;
                        }
                    }
                }
                 if(cursor!=null)cursor.close();

                Message msg = new Message();
                msg.what = ADD_ATTACHMENT;
                msg.obj = photoPath;
                mtotalHandler.sendMessage(msg);

            }
        }












        if (data != null && data.getExtras() != null && resultCode == RESULT_OK) {
            Bundle b = data.getExtras(); //data为B中回传的Intent
            Message msg = new Message();
            msg.what = REFERSH_DATA;

            switch (requestCode) {
/*                case ln_yingwen_nicheng:
                    MyToast.show(mContext, b.getString("info"));
                    mUserDetail.setEnglishName(b.getString("info"));

                    mUserDetailList.remove(0);
                    mUserDetailList.add(mUserDetail);
                    mtotalHandler.sendMessage(msg);

                    break;*/
                case tv_zhongwen_nicheng1:
                    MyToast.show(mContext, b.getString("info"));

                    mUserDetail.setName(b.getString("info"));
                    mUserDetailList.remove(0);
                    mUserDetailList.add(mUserDetail);
                    mtotalHandler.sendMessage(msg);
                    break;
                case ln_geren_jieshao:
                    MyToast.show(mContext, b.getString("info"));

                    mUserDetail.setDescription(b.getString("info"));
                    mUserDetailList.remove(0);
                    mUserDetailList.add(mUserDetail);
                    mtotalHandler.sendMessage(msg);
                    break;
/*                case ln_shenfenbiaoqian:
                    MyToast.show(mContext, b.getString("info"));

                    mUserDetail.setTag(b.getString("info"));
                    mUserDetailList.remove(0);
                    mUserDetailList.add(mUserDetail);
                    mtotalHandler.sendMessage(msg);
                    break;*/

                case ln_zhiye:
                    MyToast.show(mContext, b.getString("info"));

                    mUserDetail.setJob(b.getString("info"));
                    mUserDetailList.remove(0);
                    mUserDetailList.add(mUserDetail);
                    mtotalHandler.sendMessage(msg);
                    break;

                case ln_school:
                    MyToast.show(mContext, b.getString("info"));

                    mUserDetail.setSchool(b.getString("info"));
                    mUserDetailList.remove(0);
                    mUserDetailList.add(mUserDetail);
                    mtotalHandler.sendMessage(msg);
                    break;

                case tv_shenghuodidian:
                    MyToast.show(mContext, b.getString("info"));

                    mUserDetail.setLifeAddress(b.getString("info"));
                    mUserDetailList.remove(0);
                    mUserDetailList.add(mUserDetail);
                    mtotalHandler.sendMessage(msg);
                    break;
                case ln_gongzuodidian:
                    MyToast.show(mContext, b.getString("info"));

                    mUserDetail.setJobAddress(b.getString("info"));
                    mUserDetailList.remove(0);
                    mUserDetailList.add(mUserDetail);
                    mtotalHandler.sendMessage(msg);
                    break;
                case ln_changchumodi:
                    MyToast.show(mContext, b.getString("info"));

                    mUserDetail.setDailyAddress(b.getString("info"));
                    mUserDetailList.remove(0);
                    mUserDetailList.add(mUserDetail);
                    mtotalHandler.sendMessage(msg);
                    break;
/*                case tv_quguo_jingdian:
                    MyToast.show(mContext, b.getString("info"));

                    mUserDetail.setVisitedAttractions(b.getString("info"));
                    mUserDetailList.remove(0);
                    mUserDetailList.add(mUserDetail);
                    mtotalHandler.sendMessage(msg);
                    break;*/
                case ln_jiaxiang:
                    MyToast.show(mContext, b.getString("info"));
                    mUserDetail.setHome(b.getString("info"));
                    mUserDetailList.remove(0);
                    mUserDetailList.add(mUserDetail);
                    mtotalHandler.sendMessage(msg);
                    break;

            }
        }


//        MyToast.show(mContext,"获取到了返回值");


    }

    private ProgressDialog pd;

    /**
     * 将编辑结果上传保存到服务器
     */
    private void upload2Server() {


/*        { 
                "Photos": [
            {
                "PhotoId": "sample string 1",
                    "Base64Image": "sample string 2"
            },
            {
                "PhotoId": "sample string 1",
                    "Base64Image": "sample string 2"
            }
            ]
        }*/
        try {
            JSONObject json = new JSONObject();
            json.put("Name", mUserDetail.getName());
            json.put("EnglishName", mUserDetail.getEnglishName());
            json.put("Birthday", mUserDetail.getBirthday());
            // json.put("Birthday", new Date());//mUserDetail.getBirthday()
            json.put("Home", mUserDetail.getHome());
            json.put("Home", mUserDetail.getHome());
            json.put("Description", mUserDetail.getDescription());
            json.put("MaritalStatus", 1);//mUserDetail.getMaritalStatus()
            json.put("Job", mUserDetail.getJob());
            json.put("Company", mUserDetail.getCompany());
            json.put("School", mUserDetail.getSchool());
            json.put("JobAddress", mUserDetail.getJobAddress());
            json.put("LifeAddress", mUserDetail.getLifeAddress());
            json.put("DailyAddress", mUserDetail.getDailyAddress());

            json.put("userId", WowuApp.UserId);
            json.put("lon", settings.getString("latitude", ""));
            json.put("lat", settings.getString("longitude", ""));


//            Log.i("上传到服务器的用户编辑资料为0：：：：：", json.toString());

//
            if (userPicAdapter.getList() != null && userPicAdapter.getList().size() > 0) {
                JSONArray jarry = new JSONArray();
                for (int i = 0; i < userPicAdapter.getList().size(); i++) {
                    if (((UserInfoDetail.PhotosBean) userPicAdapter.getList().get(i)).getLocalPath() != null) {//说明是刚增加的图片

                        JSONObject tempjson0 = new JSONObject();
//                        tempjson.put("PhotoId", ((UserInfoDetail.PhotosBean) userPicAdapter.getList().get(i)).getId());
                        tempjson0.put("Base64Image", UtilsTool.bitmaptoString(BitmapFactory.decodeFile(
                                ((UserInfoDetail.PhotosBean) userPicAdapter.getList().get(i)).getLocalPath()
                        )));
                        jarry.put(tempjson0);
                    } else {//说明是之前已经有过的图片

                        JSONObject tempjson = new JSONObject();
                        tempjson.put("PhotoId", ((UserInfoDetail.PhotosBean) userPicAdapter.getList().get(i)).getId());
//                    tempjson.put("Base64Image", mUserDetail.getPhotos().get(i).g);
                        jarry.put(tempjson);
                    }
                }
                json.put("Photos", jarry);//.toString()

            }

//            Log.i("上传到服务器的用户编辑资料为：：：：：", json.toString());
//            UtilsTool.saveStringToSD(json.toString());
            loadDataService.loadPostJsonRequestData(WowuApp.JSON, WowuApp.UpdateUserInfoURL, json.toString(), R.id.tx_top_right);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
//        overridePendingTransition(0, R.anim.slide_out_to_left);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }


    public void showDatePickerDialog() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, this, year, month, day);
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        Message msg = new Message();
        msg.what = REFERSH_DATA;
        mUserDetail.setBirthday(year + " " + month + " " + day);
        mUserDetailList.remove(0);
        mUserDetailList.add(mUserDetail);
        mtotalHandler.sendMessage(msg);
    }


}

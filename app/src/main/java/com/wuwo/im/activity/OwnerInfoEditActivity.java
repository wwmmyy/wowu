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
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

public class OwnerInfoEditActivity extends FragmentActivity implements
        DatePickerDialog.OnDateSetListener , View.OnClickListener, loadServerDataListener, PicAdapter.OnItemClickLitener  {
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


    UserInfoDetail mUserDetail = new UserInfoDetail();
    ArrayList<UserInfoDetail> mUserDetailList = new ArrayList<UserInfoDetail>(); //记录所有的最新消息
    ArrayList<UserInfoDetail.PhotosBean> removedList = new ArrayList<UserInfoDetail.PhotosBean>(); //记录所有的最新消息


    RecyclerView rlist_view_content;
    CommRecyclerAdapter contentRAdapter;

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

        initView();

    }

    private void initView() {
        findViewById(R.id.return_back).setOnClickListener(this);
        findViewById(R.id.tx_top_right).setOnClickListener(this);

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
                upload2Server();

                break;
        }
    }

    private final int LOAD_DATA = 1;

    private void loadData() {
        loadDataService.loadGetJsonRequestData(OkHttpUtils.GetUserInfoURL + "?userId=" + WowuApp.UserId, LOAD_DATA);
    }


    public static final int DOWNLOADED_LocalUser = 0;
    public static final int DOWNLOADED_ERROR = 1;
    public static final int REFERSH_DATA = 2;
    public static final int ADD_ATTACHMENT = 1000;

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
            case R.id.tx_top_right:
                if (pd != null) {
                    pd.dismiss();
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
            case R.id.tx_top_right:
                MyToast.show(mContext,response);
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
                Log.i("为什么不显示呢，本地", "：：" + ((UserInfoDetail.PhotosBean) userPicAdapter.getList().get(position)).getLocalPath());
                Intent intent = new Intent(mContext, EaseShowBigImageActivity.class);
                File file = new File(((UserInfoDetail.PhotosBean) userPicAdapter.getList().get(position)).getLocalPath());
                if (file.exists()) {
                    Uri uri = Uri.fromFile(file);
                    intent.putExtra("uri", uri);
                }
                mContext.startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            } else {
                onBubbleClick(mContext, ((UserInfoDetail.PhotosBean) userPicAdapter.getList().get(position)).getFullUrl(), ((UserInfoDetail.PhotosBean) userPicAdapter.getList().get(position)).getId());
            }
        }
    }


    protected void onBubbleClick(Context context, String url, String name) {
//        String url = "http://img1.imgtn.bdimg.com/it/u=4033491868,3189899599&fm=21&gp=0.jpg";
//        String url = "http://xzxj.oss-cn-shanghai.aliyuncs.com/user/97f28a44-2bab-4140-963a-b5277351ae74.jpg";

        Log.i("为什么不显示呢，你麻痹", "：：" + WowuApp.tempPicPath + name + "." + UtilsTool.getFileType(url));

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
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), ADD_ATTACHMENT);
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


    public static final int ln_yingwen_nicheng = 100;
    public static final int tv_zhongwen_nicheng1 = 101;
    public static final int ln_geren_jieshao = 102;
    public static final int ln_shenfenbiaoqian = 103;
    public static final int ln_zhiye = 104;
    public static final int ln_school = 105;
    public static final int tv_shenghuodidian = 106;
    public static final int ln_gongzuodidian = 107;
    public static final int ln_changchumodi = 108;
    public static final int tv_quguo_jingdian = 109;
    public static final int ln_jiaxiang = 110;


    private CommRecyclerAdapter initContentAdapter() {
        contentRAdapter = new CommRecyclerAdapter<UserInfoDetail>(mContext, R.layout.activity_owner_info_edit_content_item) {
            @Override
            public void convert(CommRecyclerViewHolder viewHolder, UserInfoDetail mainMessage) {

                viewHolder.setText(R.id.tv_zhongwen_nicheng1, mainMessage.getName());
                viewHolder.setText(R.id.ln_yingwen_nicheng, mainMessage.getEnglishName() + "");
                viewHolder.setText(R.id.ln_birthday, mainMessage.getBirthday() + "");
                viewHolder.setText(R.id.ln_feeling, mainMessage.getMaritalStatus() + "");
                viewHolder.setText(R.id.ln_jiaxiang, mainMessage.getHome());
                viewHolder.setText(R.id.ln_geren_jieshao, mainMessage.getDescription());
                viewHolder.setText(R.id.ln_shenfenbiaoqian, mainMessage.getDisposition());
                viewHolder.setText(R.id.ln_zhiye, mainMessage.getJob() + "");
                viewHolder.setText(R.id.ln_school, mainMessage.getSchool());
                viewHolder.setText(R.id.tv_shenghuodidian, mainMessage.getLifeAddress());
                viewHolder.setText(R.id.ln_gongzuodidian, mainMessage.getJobAddress());
                viewHolder.setText(R.id.ln_changchumodi, mainMessage.getDailyAddress());
                viewHolder.setText(R.id.tv_quguo_jingdian, mainMessage.getVisitedAttractions());


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


                viewHolder.getView(R.id.ln_yingwen_nicheng).setOnClickListener(new View.OnClickListener() {
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
                });

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
                viewHolder.getView(R.id.ln_shenfenbiaoqian).setOnClickListener(new View.OnClickListener() {
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
                });

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

                viewHolder.getView(R.id.tv_quguo_jingdian).setOnClickListener(new View.OnClickListener() {
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
                });

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
                cursor.close();

                Message msg = new Message();
                msg.what = ADD_ATTACHMENT;
                msg.obj = photoPath;
                mtotalHandler.sendMessage(msg);


//                Attachment affInfos = Attachment.GetFileInfo(photoPath);
//                Attach_adapter.appendToList(affInfos);
//                int a = Attach_adapter.getList().size();
//                int count = (int) Math.ceil(a / 2.0);
//                Attach_gridView.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
//                        LinearLayout.LayoutParams.MATCH_PARENT,
//                        (int) (120 * 1.5 * count)));

            }
        }


        if (data != null && data.getExtras() != null && resultCode == RESULT_OK) {
            Bundle b = data.getExtras(); //data为B中回传的Intent
            Message msg = new Message();
            msg.what = REFERSH_DATA;

            switch (requestCode) {
                case ln_yingwen_nicheng:
                    MyToast.show(mContext, b.getString("info"));
                    mUserDetail.setEnglishName(b.getString("info"));

                    mUserDetailList.remove(0);
                    mUserDetailList.add(mUserDetail);
                    mtotalHandler.sendMessage(msg);

                    break;
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
                case ln_shenfenbiaoqian:
                    MyToast.show(mContext, b.getString("info"));

                    mUserDetail.setTag(b.getString("info"));
                    mUserDetailList.remove(0);
                    mUserDetailList.add(mUserDetail);
                    mtotalHandler.sendMessage(msg);
                    break;

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
                case tv_quguo_jingdian:
                    MyToast.show(mContext, b.getString("info"));

                    mUserDetail.setVisitedAttractions(b.getString("info"));
                    mUserDetailList.remove(0);
                    mUserDetailList.add(mUserDetail);
                    mtotalHandler.sendMessage(msg);
                    break;
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
           // json.put("Birthday", new Date());//mUserDetail.getBirthday()
            json.put("Home", mUserDetail.getHome());
            json.put("Description", mUserDetail.getDescription());
            json.put("MaritalStatus", 1);//mUserDetail.getMaritalStatus()
            json.put("Job", mUserDetail.getJob());
            json.put("Company", mUserDetail.getCompany());
            json.put("School", mUserDetail.getSchool());
            json.put("JobAddress", mUserDetail.getJobAddress());
            json.put("LifeAddress", mUserDetail.getLifeAddress());
            json.put("DailyAddress", mUserDetail.getDailyAddress());

            Log.i("上传到服务器的用户编辑资料为0：：：：：", json.toString());

//
            if (userPicAdapter.getList() != null && userPicAdapter.getList().size() > 0) {
                JSONArray jarry = new JSONArray();
                for (int i = 0; i < userPicAdapter.getList().size(); i++) {
                    if (((UserInfoDetail.PhotosBean) userPicAdapter.getList().get(i)).getLocalPath() != null) {//说明是刚增加的图片

                        JSONObject tempjson = new JSONObject();
//                        tempjson.put("PhotoId", ((UserInfoDetail.PhotosBean) userPicAdapter.getList().get(i)).getId());
                        json.put("Base64Image", UtilsTool.bitmaptoString(BitmapFactory.decodeFile(
                                ((UserInfoDetail.PhotosBean) userPicAdapter.getList().get(i)).getLocalPath()
                        )));
                        jarry.put(tempjson);
                    } else {//说明是之前已经有过的图片

                        JSONObject tempjson = new JSONObject();
                        tempjson.put("PhotoId", ((UserInfoDetail.PhotosBean) userPicAdapter.getList().get(i)).getId());
//                    tempjson.put("Base64Image", mUserDetail.getPhotos().get(i).g);
                        jarry.put(tempjson);
                    }
                }
                json.put("Photos", jarry.toString());

            }

            pd = UtilsTool.initProgressDialog(mContext, "请稍后...");
            pd.show();
            Log.i("上传到服务器的用户编辑资料为：：：：：", json.toString());
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
        mUserDetail.setBirthday(year +"年"+ month +"月"+day+"日");
        mUserDetailList.remove(0);
        mUserDetailList.add(mUserDetail);
        mtotalHandler.sendMessage(msg);
    }


}

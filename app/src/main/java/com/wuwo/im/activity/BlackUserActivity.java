package com.wuwo.im.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wuwo.im.adapter.CommRecyclerAdapter;
import com.wuwo.im.adapter.CommRecyclerViewHolder;
import com.wuwo.im.bean.BlackUser;
import com.wuwo.im.bean.BlackUser.DataBean;
import com.wuwo.im.bean.Characters;
import com.wuwo.im.config.WowuApp;
import com.wuwo.im.util.LogUtils;
import com.wuwo.im.util.MyToast;
import com.wuwo.im.view.PullLoadMoreRecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import im.imxianzhi.com.imxianzhi.R;

/**
 * 黑名单
 * activity_black_user
 */
public class BlackUserActivity extends BaseLoadActivity {

    private final String TAG = "BlackUserActivity";
    private PullLoadMoreRecyclerView mPullLoadMoreRecyclerView;
    private int mCount = 0;
    private ArrayList<BlackUser.DataBean> BlackUser_List = new ArrayList<BlackUser.DataBean>(); //记录所有的最新消息
    private CommRecyclerAdapter messageRAdapter;

    private mHandlerWeak mtotalHandler;
    private TextView tx_top_right;
    private SharedPreferences mSettings;
    private Characters mCharacter = new Characters();
    public static final int LOAD_RECOMMEND_DATA = 111;
    private TextView tv_character_type_set;

    //    private String mCurrentSelectId;
//用于记录当前点击添加的好友的id
//    String currentClickfriendId = null;
    int CurrentSelect = -1;
    SimpleDraweeView lastView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_list);
//        UtilsTool.hideSoftKeyboard(BlackUserActivity.this);
        mSettings = mContext.getSharedPreferences(WowuApp.PREFERENCE_KEY,
                android.content.Context.MODE_PRIVATE);

        initViews();
        loadData();
//            loadDataService.loadGetJsonRequestData(WowuApp.GetDispositionInfoURL, LOAD_RECOMMEND_DATA);
    }

    private void initViews() {
        findViewById(R.id.return_back).setOnClickListener(this);
        ((TextView) findViewById(R.id.top_title)).setText("黑名单");

        tx_top_right = (TextView) findViewById(R.id.tx_top_right);
        tx_top_right.setText("编辑");
        tx_top_right.setTextColor(getResources().getColor(R.color.colorPrimary));
        tx_top_right.setOnClickListener(this);


        tv_character_type_set = ((TextView) findViewById(R.id.tv_character_type_set));
        tv_character_type_set.setOnClickListener(this);
        tv_character_type_set.setText("解除");


        mPullLoadMoreRecyclerView = (PullLoadMoreRecyclerView) findViewById(R.id.pullLoadMoreRecyclerView);
        mPullLoadMoreRecyclerView.setRefreshing(true);
//        mPullLoadMoreRecyclerView.setLinearLayout();//setGridLayout  setStaggeredGridLayout
        mPullLoadMoreRecyclerView.setGridLayout(3);
//        mPullLoadMoreRecyclerView.setStaggeredGridLayout(2);
        mPullLoadMoreRecyclerView.setOnPullLoadMoreListener(new PullLoadMoreListener());

        mtotalHandler = new mHandlerWeak(this);
//        loadData();

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Gson gson = new GsonBuilder().create();
//                java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<Characters>() {
//                }.getType();
//                if(mSettings.getString("characterInfo",null) !=null){
//                    mCharacter = gson.fromJson(mSettings.getString("characterInfo",""), type);
//                }
//            }
//        }).start();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.return_back:
                this.finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.tx_top_right:
                if (tx_top_right.getText().toString().equals("编辑")) {
                    tx_top_right.setText("取消");
                    tv_character_type_set.setVisibility(View.VISIBLE);
                } else {
                    tx_top_right.setText("编辑");
                    tv_character_type_set.setVisibility(View.GONE);
                    CurrentSelect = -1;
                    if (messageRAdapter != null) messageRAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.tv_character_type_set:

//                if (CurrentSelect == -1) {
                if (tempSelect.isEmpty()) {
                    MyToast.show(mContext, "请先选择头像");
                } else {
                    try {
                        JSONObject json = new JSONObject();
                        json.put("id", BlackUser_List.get(CurrentSelect).getUserId());//APP版本号

                        StringBuilder ids = new StringBuilder();
                        Iterator iter = tempSelect.entrySet().iterator();
                        while (iter.hasNext()) {
                            Map.Entry entry = (Map.Entry) iter.next();
//                            Object key = entry.getKey();
                            ids.append(BlackUser_List.get(Integer.parseInt(entry.getKey() + "")).getId() + ",");
                        }
                        String idsStr = ids.toString();
                        LogUtils.i(TAG, "：：ids:" + idsStr);
                        LogUtils.i(TAG, "：：ids:" + idsStr.substring(0, idsStr.length() - 1));
                        LogUtils.i(TAG, "：：ids:" + idsStr.substring(0, idsStr.length() - 1));
                        loadDataService.loadPostJsonRequestData(WowuApp.JSON, WowuApp.RemoveBlacklistURL + "?id=" + idsStr.substring(0, idsStr.length() - 1), json.toString(), SELECTED_USER);//BlackUser_List.get(CurrentSelect).getId()
                        tempSelect.clear();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    Gson gson = new GsonBuilder().create();
    private final int LOAD_DATA = 1;


    //    从网络加载流转日志数据并展示出来
    private void loadData() {
        loadDataService.loadGetJsonRequestData(WowuApp.BlacklistURL + "?pageIndex=" + mCount, LOAD_DATA);
    }

    public static final int DOWNLOADED_NEWSMESSAGE = 0;
    public static final int DOWNLOADED_ERROR = 1;
    public static final int REFRESH_DATA = 2;
    public static final int SEARCH_DATA = 3;
    public static final int REFRESH_VIEW = 4;
    private final int SELECTED_USER = 5;
    public static final int JIECHU_SUCCESS = 11;


    private static class mHandlerWeak extends Handler {
        private WeakReference<BlackUserActivity> activity = null;

        public mHandlerWeak(BlackUserActivity act) {
            super();
            this.activity = new WeakReference<BlackUserActivity>(act);
        }

        @Override
        public void handleMessage(Message msg) {
            BlackUserActivity act = activity.get();
            if (null == act) {
                return;
            }
            switch (msg.what) {
                // 正在下载
                case DOWNLOADED_NEWSMESSAGE:
                    if (act.messageRAdapter == null) {
                        act.initAdapter();
                        act.messageRAdapter.setData(act.getLoadInfo());
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
                    MyToast.show(act.mContext, "解除关系失败");
//                    mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();
                    break;
                case REFRESH_DATA:
                    act.setRefresh();
                    act.loadData();
                    break;
                case REFRESH_VIEW:
                    act.messageRAdapter.notifyDataSetChanged();
                    break;
                case JIECHU_SUCCESS:
                    MyToast.show(act.mContext, "添加成功");
                    break;

                default:
                    break;
            }
        }
    }


    class PullLoadMoreListener implements PullLoadMoreRecyclerView.PullLoadMoreListener {
        @Override
        public void onRefresh() {
//            setRefresh();
//            loadData();
            Message msg = new Message();
            msg.what = REFRESH_DATA;
            mtotalHandler.sendMessage(msg);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mPullLoadMoreRecyclerView.setRefreshing(false);
//                    MyToast.show(mContext, "刷新超时了");
                }
            }, 3000);
        }

        @Override
        public void onLoadMore() {
            if (loadMore()) {
                mCount = mCount + 1;
                loadData();
            } else {
                mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();
            }
        }
    }

    private void setRefresh() {
//        mXinWen_RecyclerViewAdapter.getDataList().clear();
        if (messageRAdapter != null) messageRAdapter.clearDate();
        mCount = 0;
    }


    public void setLoadInfo(String totalresult) throws JSONException {
        Gson gson = new GsonBuilder().create();
        if (totalresult != null) {
            java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<BlackUser>() {
            }.getType();
            BlackUser temp = gson.fromJson(totalresult, type);
            BlackUser_List = (ArrayList<DataBean>) temp.getData();
        }
    }


    public ArrayList<?> getLoadInfo() {
        return BlackUser_List;
    }


    HashMap<String, SimpleDraweeView> tempSelect = new HashMap<String, SimpleDraweeView>();

    public CommRecyclerAdapter initAdapter() {
        messageRAdapter = new CommRecyclerAdapter<BlackUser.DataBean>(mContext, R.layout.item_blackuser_cardview) {
            @Override
            public void convert(CommRecyclerViewHolder viewHolder, BlackUser.DataBean mainMessage) {
//                tv_blcakuser_intro  tv_blcakuser_name  iv_blcakuser_img
                //对对应的View进行赋值
                SimpleDraweeView iv_blcakuser_img = (SimpleDraweeView) viewHolder.getView(R.id.iv_blcakuser_img);
                if (mainMessage.getPhotoUrl() != null) {
                    iv_blcakuser_img.setImageURI(Uri.parse(mainMessage.getPhotoUrl()));
                }
                viewHolder.setText(R.id.tv_blcakuser_name, mainMessage.getName());
                viewHolder.setText(R.id.tv_blcakuser_intro, mainMessage.getDisposition());
//                 viewHolder.setText(R.id.contact_userinfo, (mainMessage.getDescription() + "").equals("null") ? "这人很懒，什么都没写" : mainMessage.getDescription() + "");
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
                if (tx_top_right.getText().toString().equals("编辑")) {
                    CurrentSelect = -1;
                    tempSelect.clear();
                    Intent intent2 = new Intent(mContext, UserInfoEditActivity.class);
                    intent2.putExtra("chatUserId", ((BlackUser.DataBean) messageRAdapter.getData().get(position)).getUserId());
                    intent2.putExtra("PhotoUrl", ((BlackUser.DataBean) messageRAdapter.getData().get(position)).getPhotoUrl());
                    startActivity(intent2);
                    BlackUserActivity.this.overridePendingTransition(0, 0);
                } else {

                    if (tempSelect.containsKey(position + "")) {
//                        if (lastView != null) {
//                            lastView.getHierarchy().setControllerOverlay(null);
//                        }

                        tempSelect.get(position + "").getHierarchy().setControllerOverlay(null);
                        tempSelect.remove(position + "");
                    } else {
                        CommRecyclerViewHolder holder = (CommRecyclerViewHolder) view.getTag();
                        lastView = holder.getView(R.id.iv_blcakuser_img);
                        GenericDraweeHierarchy hierarchy = lastView.getHierarchy();
                        hierarchy.setControllerOverlay(getResources().getDrawable(R.drawable.blackuser_bg));
                        CurrentSelect = position;
                        tempSelect.put(position + "", (SimpleDraweeView) holder.getView(R.id.iv_blcakuser_img));
                    }
                }
            }
        });
        return null;
    }


    @Override
    public void loadServerData(final String response, int flag) {
        LogUtils.i(TAG, "：：" + response);
        switch (flag) {
            case LOAD_DATA:
//                LogUtils.i("获取好友推荐列表", "：：" + response);

//                UtilsTool.saveStringToSD(json.toString());
                try {
                    if (response != null) {
                        setLoadInfo(response);
                    }
                    Message msg = new Message();
                    msg.what = DOWNLOADED_NEWSMESSAGE;
                    mtotalHandler.sendMessage(msg);
                } catch (Exception e) {
//                                    e.printStackTrace();
                    Message msg = new Message();
                    msg.what = DOWNLOADED_ERROR;
                    mtotalHandler.sendMessage(msg);
                }
                break;

            case R.id.iv_character_set:
//                MyToast.show(mContext, "添加成功");

/*                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //将已经添加过的人员进行标记，更新列表
                        for (int i = 0; i < BlackUser_List.size(); ) {
                            if (BlackUser_List.get(i).getUserId().equals(currentClickfriendId)) {
                                BlackUser.DataBean temp = BlackUser_List.get(i);
                                temp.setAddState(true);
                                BlackUser_List.remove(i);
                                BlackUser_List.add(i, temp);
                                Message msg = new Message();
                                msg.what = REFRESH_VIEW;
                                mtotalHandler.sendMessage(msg);
                                return;
                            }
                        }
                    }
                }).start();*/

                Message msgx = new Message();
                msgx.what = JIECHU_SUCCESS;
                mtotalHandler.sendMessage(msgx);
                break;

            case SELECTED_USER:
                Message msg = new Message();
                msg.what = REFRESH_DATA;
                mtotalHandler.sendMessage(msg);
                break;

/*            case  LOAD_RECOMMEND_DATA:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new GsonBuilder().create();
                        java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<Characters>() {
                        }.getType();
                        if(response !=null){
                            mCharacter = gson.fromJson(response, type);
                            SharedPreferences.Editor editor = mSettings.edit();
                            editor.putString("characterInfo", response);
                            if(mCharacter.getPhotoUrl() !=null){
                                editor.putString("characterUrl", mCharacter.getPhotoUrl());
                            }
                            editor.commit();
                        }
                    }
                }).start();
                break;*/

        }
    }

    @Override
    public void loadDataFailed(String response, int flag) {
        LogUtils.i(TAG, "：：" + response);

        switch (flag) {
            case SELECTED_USER:

                Message msg = new Message();
                msg.what = DOWNLOADED_ERROR;
                mtotalHandler.sendMessage(msg);

                break;

        }

//        MyToast.show(mContext, response);
//        if(LOAD_RECOMMEND_DATA ==flag){
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    Gson gson = new GsonBuilder().create();
//                    java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<Characters>() {
//                    }.getType();
//                    if(mSettings.getString("characterInfo",null) !=null){
//                        mCharacter = gson.fromJson(mSettings.getString("characterInfo",""), type);
//                    }
//                }
//            }).start();
//        }
    }


    public boolean loadMore() {
        return false;
    }


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


//    public void refresh() {
//        if (loadDataService != null) {
//            loadDataService.loadGetJsonRequestData(WowuApp.BlacklistURL + "?pageIndex=0", LOAD_RECOMMEND_DATA);
//        }
//    }

}

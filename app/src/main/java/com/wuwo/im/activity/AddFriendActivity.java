package com.wuwo.im.activity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Request;
import com.wuwo.im.adapter.CommRecyclerAdapter;
import com.wuwo.im.adapter.CommRecyclerViewHolder;
import com.wuwo.im.bean.newsMessage;
import com.wuwo.im.util.MyToast;
import com.wuwo.im.view.PullLoadMoreRecyclerView;
import com.wuwo.im.view.SearchView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import im.wuwo.com.wuwo.R;

/**
 * 添加密码
 * desc RegisterStepThreeActivity
 *
 * @author 王明远
 * @日期： 2016/6/9 0:07
 * @版权:Copyright All rights reserved.
 */

public class AddFriendActivity extends BaseLoadActivity {

    private PullLoadMoreRecyclerView mPullLoadMoreRecyclerView;
    private int mCount = 1;
    private ArrayList<newsMessage> meeting_userlist = new ArrayList<newsMessage>(); //记录所有的最新消息
    //    private XinWen_RecyclerViewAdapter mXinWen_RecyclerViewAdapter;
    private SearchView search_view;
    private String searchinfo;
    CommRecyclerAdapter messageRAdapter;

    mHandlerWeak mtotalHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfriend_pullloadmore_recyclerview);
        initViews();

    }


    private int searchInfoTag;//由于采用的是快速搜素方法，假如用户连续输入，而网络延迟，可能搜索过程中产生的结果并不是用户想要的这是就要过滤掉

    private void initViews() {
        findViewById(R.id.return_back).setOnClickListener(this);

        ((TextView)findViewById(R.id.top_title)).setText("添加好友");


        mPullLoadMoreRecyclerView = (PullLoadMoreRecyclerView) findViewById(R.id.pullLoadMoreRecyclerView);
        mPullLoadMoreRecyclerView.setRefreshing(true);
        mPullLoadMoreRecyclerView.setLinearLayout();//setGridLayout  setStaggeredGridLayout
////      mPullLoadMoreRecyclerView.setGridLayout(3);
//        mPullLoadMoreRecyclerView.setStaggeredGridLayout(2);
        mPullLoadMoreRecyclerView.setOnPullLoadMoreListener(new PullLoadMoreListener());

        search_view = (SearchView) findViewById(R.id.search_view);
        search_view.setVisibility(View.VISIBLE);
//       loadData();
        mtotalHandler = new mHandlerWeak(this);

        findViewById(R.id.tv_contact_add).setOnClickListener(this);
        findViewById(R.id.tv_weixin_add).setOnClickListener(this);
        findViewById(R.id.tv_qq_add).setOnClickListener(this);

        search_view.setSearchListener(new SearchView.searchListener() {
            @Override
            public void searchInfo(String info) {
                searchInfoTag = searchInfoTag + 1;
                searchinfo = info;
                if (info != null && !info.equals("")) {
                    searchData(info, searchInfoTag);
                } else {
                    boolean temp = search_view.resertSearch();
                    if (messageRAdapter != null) {//说明已经加载过搜索
                        setRefresh();
                        messageRAdapter.notifyDataSetChanged();
                    }
                }
            }
        });


        loadData();
    }

    Intent temp2 = null;

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.return_back:
                this.finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.tv_contact_add:
                Intent txl = new Intent(mContext, FromContractsActivity.class);
                startActivity(txl);

                break;
            case R.id.tv_weixin_add:
                showWeiXinShareDialog();
                break;
            case R.id.tv_qq_add:
                showQQShareDialog();
                break;

        }
    }

    //    /从网络加载流转日志数据并展示出来
    private void searchData(String info, final int searchInfoTag) {
        OkHttpUtils
                .post()
                .url("http://58.246.138.178:8081/DistMobile/mobileMeeting!getAllMeeting.action")
                .addParams("MeetingId", "4028826f505a3b0f01506553b0c80c3a")
                .addParams("page", mCount + "")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Request request, Exception e) {
                        MyToast.show(mContext, "获取服务器信息失败", Toast.LENGTH_LONG);
                        mPullLoadMoreRecyclerView.setRefreshing(false);
                        mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();
                    }

                    @Override
                    public void onResponse(String response) {
                        if (response != null) {
                            java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<List<newsMessage>>() {
                            }.getType();

                            meeting_userlist = gson.fromJson(response, type);
                            //在view界面上展示结果
                            Message msg = new Message();
                            msg.what = DOWNLOADED_NEWSMESSAGE;
                            msg.arg1 = searchInfoTag;
                            mtotalHandler.sendMessage(msg);

                        }
                    }
                });
    }

    Gson gson = new GsonBuilder().create();


    private final int LOAD_DATA = 1;

    //    从网络加载流转日志数据并展示出来
    private void loadData() {


/*        try {
            JSONObject json = new JSONObject();
            json.put("type","smartplan");
            json.put("action", "getlawrulelist");
            json.put("page",mCount + "");
            loadDataService.loadPostJsonRequestData(WowuApp.JSON, "http://58.246.138.178:8000/DistMobile/mobileMeeting!getAllMeeting.action", json.toString(), LOAD_DATA);
        } catch (Exception e) {
            e.printStackTrace();
        }*/


        new Thread(new Runnable() {
            @Override
            public void run() {

                OkHttpUtils
                        .post()
//                        .url(DistApp.SUNBO_BASE_URL)
                        .url("http://58.246.138.178:8000/DistMobile/mobileMeeting!getAllMeeting.action")
                        .addParams("type", "smartplan")
                        .addParams("action", "getlawrulelist")
//                        .addParams("action", "getofficalList")
                        .addParams("page", mCount + "")
                        .addParams("page", mCount + "")
                        .build()
                        .execute(new StringCallback() {
                            @Override
                            public void onError(Request request, Exception e) {
//                              MyToast.show(mContext, "获取服务器信息失败", Toast.LENGTH_LONG);
                                Message msg = new Message();
                                msg.what = DOWNLOADED_ERROR;
                                mtotalHandler.sendMessage(msg);
                            }

                            @Override
                            public void onResponse(String totalresult) {
                                try {
                                    if (totalresult != null) {
                                        setLoadInfo(totalresult);
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
                            }
                        });
            }
        }).start();
    }

    public static final int DOWNLOADED_NEWSMESSAGE = 0;
    public static final int DOWNLOADED_ERROR = 1;
    public static final int REFRESH_DATA = 2;


    @Override
    public void loadServerData(String response, int flag) {

    }

    @Override
    public void loadDataFailed(String response, int flag) {

    }


    private static class mHandlerWeak extends Handler {
        private WeakReference<AddFriendActivity> activity = null;

        public mHandlerWeak(AddFriendActivity act) {
            super();
            this.activity = new WeakReference<AddFriendActivity>(act);
        }

        @Override
        public void handleMessage(Message msg) {
            AddFriendActivity act = activity.get();
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
//                    mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();
                    break;
                case REFRESH_DATA:
                    act.setRefresh();
                    act.loadData();
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
        mCount = 1;
    }


    public void setLoadInfo(String totalresult) throws JSONException {


        Gson gson = new GsonBuilder().create();
        if (totalresult != null) {
            java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<List<newsMessage>>() {
            }.getType();
            meeting_userlist = gson.fromJson(totalresult, type);
        }

    }


    public ArrayList<?> getLoadInfo() {
        return meeting_userlist;
    }


    public CommRecyclerAdapter initAdapter() {
        messageRAdapter = new CommRecyclerAdapter<newsMessage>(mContext, R.layout.item_contact_addfriend) {
            @Override
            public void convert(CommRecyclerViewHolder viewHolder, newsMessage mainMessage) {
                //对对应的View进行赋值
//                viewHolder.setText(R.id.news_title, mainMessage.getTitle());
                SimpleDraweeView portal_news_img = (SimpleDraweeView) viewHolder.getView(R.id.news_label_pic);
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
//                intent2.putExtra("url", DistApp.serverAbsolutePath + "/snews!mobileNewsdetail.action?news.id=4028816f4d4be502014d4c0e22dc003d");
//                intent2.putExtra("name", "消息通知");
//                startActivity(intent2);
//                mContext.overridePendingTransition(0, 0);
            }
        });
        return null;
    }

//    public PostFormBuilder httpBuilder() {
//        return OkHttpUtils
//                .post()
////                .url(WowuApp.serverAbsolutePath)
//                .url("http://58.246.138.178:8000/DistMobile/mobileMeeting!getAllMeeting.action")
//                .addParams("type", "smartplan")
//                .addParams("action", "getlawrulelist");
//    }

    public boolean loadMore() {
        return false;
    }

    private void showWeiXinShareDialog() {
        View view = this.getLayoutInflater().inflate(R.layout.fragement_contact_weixinadd_pop, null);
        final Dialog dialog = new Dialog(mContext, R.style.transparentFrameWindowStyle);
        dialog.setContentView(view, new ViewGroup.LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();

        view.findViewById(R.id.home_member_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO 自动生成的方法存根
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.rt_weixin_pyq).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO 自动生成的方法存根
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.rt_weixin_hy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO 自动生成的方法存根
                dialog.dismiss();
            }
        });

        // 设置显示动画
        window.setWindowAnimations(R.style.main_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = this.getWindowManager().getDefaultDisplay().getHeight();
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        // 设置显示位置
        dialog.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }


    private void showQQShareDialog() {
        View view = this.getLayoutInflater().inflate(R.layout.fragement_contact_qqadd_pop, null);
        final Dialog dialog = new Dialog(mContext, R.style.transparentFrameWindowStyle);
        dialog.setContentView(view, new ViewGroup.LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();

        view.findViewById(R.id.home_member_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO 自动生成的方法存根
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.rt_qq_kj).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO 自动生成的方法存根
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.rt_qq_hy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO 自动生成的方法存根
                dialog.dismiss();
            }
        });
        // 设置显示动画
        window.setWindowAnimations(R.style.main_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = this.getWindowManager().getDefaultDisplay().getHeight();
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        // 设置显示位置
        dialog.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }
}
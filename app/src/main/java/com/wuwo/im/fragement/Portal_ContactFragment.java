package com.wuwo.im.fragement;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Request;
import com.wuwo.im.activity.UserInfoEditActivity;
import com.wuwo.im.adapter.CommRecyclerAdapter;
import com.wuwo.im.adapter.CommRecyclerViewHolder;
import com.wuwo.im.bean.LocalUser;
import com.wuwo.im.config.WowuApp;
import com.wuwo.im.service.LoadserverdataService;
import com.wuwo.im.service.loadServerDataListener;
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
 * 首页 设置 fragment
 *
 * @author dewyze
 */
@SuppressLint("ValidFragment")
public class Portal_ContactFragment extends BaseAppFragment implements View.OnClickListener , loadServerDataListener {

    Activity mContext;
    SharedPreferences mSettings;
    SharedPreferences.Editor editor;
    private PullLoadMoreRecyclerView mPullLoadMoreRecyclerView;
    private int mCount = 0;
    private ArrayList<LocalUser.DataBean> meeting_userlist = new ArrayList<LocalUser.DataBean>(); //记录所有的最新消息
     private SearchView search_view;
    private String searchinfo;
    CommRecyclerAdapter messageRAdapter;

    mHandlerWeak mtotalHandler;

    LoadserverdataService loadDataService;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mContext = activity;
        mSettings = mContext.getSharedPreferences(WowuApp.PREFERENCE_KEY,
                android.content.Context.MODE_PRIVATE);
        mtotalHandler = new mHandlerWeak(this);
        loadDataService = new LoadserverdataService(this);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragement_contact_pullloadmore_recyclerview, container, false);
        initViews(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        initViews(view);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadData();
    }

    private  int searchInfoTag;//由于采用的是快速搜素方法，假如用户连续输入，而网络延迟，可能搜索过程中产生的结果并不是用户想要的这是就要过滤掉
    private void initViews(View view) {
        mPullLoadMoreRecyclerView = (PullLoadMoreRecyclerView) view.findViewById(R.id.pullLoadMoreRecyclerView);
        mPullLoadMoreRecyclerView.setRefreshing(true);
        mPullLoadMoreRecyclerView.setLinearLayout();//setGridLayout  setStaggeredGridLayout
////      mPullLoadMoreRecyclerView.setGridLayout(3);
//        mPullLoadMoreRecyclerView.setStaggeredGridLayout(2);
        mPullLoadMoreRecyclerView.setOnPullLoadMoreListener(new PullLoadMoreListener());

        search_view = (SearchView) view.findViewById(R.id.search_view);
        search_view.setVisibility(View.VISIBLE);
//       loadData();

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
    }
    //    /从网络加载流转日志数据并展示出来
    private void searchData(String info, final int searchInfoTag) {
        OkHttpUtils
                .post()
                .url("http://58.246.138.178:8081/DistMobile/mobileMeeting!getAllMeeting.action")
                .addParams("MeetingId", "4028826f505a3b0f01506553b0c80c3a")
                .addParams("page",mCount+"")
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
                            java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<List<LocalUser.DataBean >>() {
                            }.getType();

                            meeting_userlist = gson.fromJson(response, type);
                            //在view界面上展示结果
                            Message msg = new Message();
                            msg.what = DOWNLOADED_LocalUser;
                            msg.arg1=searchInfoTag;
                            mtotalHandler.sendMessage(msg);

                        }
                    }
                });
    }

    Gson gson = new GsonBuilder().create();


    private final int LOAD_DATA=1;
    //    从网络加载流转日志数据并展示出来
    private void loadData() {

        loadDataService.loadGetJsonRequestData( WowuApp.GetFriendsURL+"?lon=" + WowuApp.longitude + "&lat=" + WowuApp.latitude+ "&userId=" + "437ca552-ca12-4542-98e0-b2011399b849"+ "&PhoneNumber="+WowuApp.PhoneNumber ,LOAD_DATA);

    }

    public static final int DOWNLOADED_LocalUser= 0;
    public static final int DOWNLOADED_ERROR = 1;
    public static final int REFRESH_DATA = 2;

    @Override
    public void onClick(View v) {




    }

    @Override
    public void loadServerData(String response, int flag) {
        switch (flag){
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
        }



    }

    @Override
    public void loadDataFailed(String response, int flag) {

        switch (flag){
            case LOAD_DATA:
                Message msg = new Message();
                msg.what = DOWNLOADED_ERROR;
                mtotalHandler.sendMessage(msg);
                break;
        }






    }






    private static class mHandlerWeak extends Handler {
        private WeakReference<Portal_ContactFragment> activity = null;

        public mHandlerWeak(Portal_ContactFragment act) {
            super();
            this.activity = new WeakReference<Portal_ContactFragment>(act);
        }

        @Override
        public void handleMessage(Message msg) {
            Portal_ContactFragment act = activity.get();
            if (null == act) {
                return;
            }
            switch (msg.what) {
                // 正在下载
                case DOWNLOADED_LocalUser:
                    if (act.messageRAdapter == null) {
                        act.initAdapter();
                        act.messageRAdapter.setData(act.getLoadInfo());
                        act.mPullLoadMoreRecyclerView.setAdapter(act.messageRAdapter);
                    } else {
                        act.messageRAdapter.setData(act.getLoadInfo());
//                        act. messageRAdapter.notifyDataSetChanged();
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
            java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<List<LocalUser.DataBean>>() {
            }.getType();
            meeting_userlist = gson.fromJson(totalresult, type);
        }

    }


    public ArrayList<?> getLoadInfo() {
        return meeting_userlist;
    }


    public CommRecyclerAdapter initAdapter() {
        messageRAdapter = new CommRecyclerAdapter<LocalUser.DataBean>(getActivity(), R.layout.item_contact_list) {
            @Override
            public void convert(CommRecyclerViewHolder viewHolder, LocalUser.DataBean mainMessage) {

                //对对应的View进行赋值
                viewHolder.setText(R.id.contract_title, mainMessage.getName());

                viewHolder.setText(R.id.contract_code, mainMessage.getDistance()+" | "+ mainMessage.getBefore());
                viewHolder.setText(R.id.contact_userinfo, mainMessage.getDisposition());
                viewHolder.setText(R.id.contact_gender, mainMessage.getAge()+"");


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
//                //        intent2.putExtra("content", LocalUser.DataBeanlist.get(tempPosition-1).getContent());
//                intent2.putExtra("url", DistApp.serverAbsolutePath + "/snews!mobileNewsdetail.action?news.id=4028816f4d4be502014d4c0e22dc003d");
//                intent2.putExtra("name", "消息通知");
                startActivity(intent2);
                mContext.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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


    public String getFragmentName() {
        return "Portal_ContactFragment";
    }

}

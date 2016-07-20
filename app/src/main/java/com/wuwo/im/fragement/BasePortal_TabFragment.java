package com.wuwo.im.fragement;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wuwo.im.adapter.CommRecyclerAdapter;
import com.wuwo.im.config.WowuApp;
import com.wuwo.im.util.MyToast;
import com.wuwo.im.view.PullLoadMoreRecyclerView;
import com.zhy.http.okhttp.service.LoadserverdataService;
import com.zhy.http.okhttp.service.loadServerDataListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import im.wuwo.com.wuwo.R;

/**
 * 首页 设置 fragment
 *
 * @author dewyze
 */
@SuppressLint("ValidFragment")
abstract class BasePortal_TabFragment extends BaseAppFragment  implements loadServerDataListener {

      Activity mContext;
      SharedPreferences mSettings;
      Editor editor;
    private PullLoadMoreRecyclerView mPullLoadMoreRecyclerView;
    private int mCount = 0;
      LoadserverdataService loadDataService;

      CommRecyclerAdapter messageRAdapter;

//    private ArrayList<?> meeting_userlist = new ArrayList(); //记录所有的最新消息

    public BasePortal_TabFragment() {
        // TODO Auto-generated constructor stub
    }
    mHandlerWeak mtotalHandler;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mContext = activity;
        mSettings = mContext.getSharedPreferences(WowuApp.PREFERENCE_KEY,
                android.content.Context.MODE_PRIVATE);
          mtotalHandler=new mHandlerWeak(this);
        loadDataService = new LoadserverdataService(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pullloadmore_recyclerview, container, false);
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

    private void initViews(View view) {
        mPullLoadMoreRecyclerView = (PullLoadMoreRecyclerView) view.findViewById(R.id.pullLoadMoreRecyclerView);
        mPullLoadMoreRecyclerView.setRefreshing(true);
        mPullLoadMoreRecyclerView.setLinearLayout();//setGridLayout  setStaggeredGridLayout
////      mPullLoadMoreRecyclerView.setGridLayout(3);
//        mPullLoadMoreRecyclerView.setStaggeredGridLayout(2);
        mPullLoadMoreRecyclerView.setOnPullLoadMoreListener(new PullLoadMoreListener());
//       loadData();
    }



    Gson gson = new GsonBuilder().create();

    //    从网络加载流转日志数据并展示出来
    private void loadData() {


        try {
            if(postURL()!=null){
                loadDataService.loadPostJsonRequestData(WowuApp.JSON, postURL(), postJsonObject().put("page", mCount + "").toString(), R.id.tv_register_two_sure);
            }else{
                loadDataService.loadGetJsonRequestData(getURL()+"&pageIndex="+mCount ,0);
                Log.d("获取到的请求服务器url为：：：",getURL()+"&pageIndex="+mCount);
            }




        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static final int DOWNLOADED_NEWSMESSAGE = 0;
    public static final int DOWNLOADED_ERROR = 1;
    public static final int REFRESH_DATA =2;

    private static class mHandlerWeak extends Handler  {
        private WeakReference<BasePortal_TabFragment> activity = null;

        public mHandlerWeak(BasePortal_TabFragment act) {
            super();
            this.activity = new WeakReference<BasePortal_TabFragment>(act);
        }

        @Override
        public void handleMessage(Message msg)
        {
            BasePortal_TabFragment act = activity.get();
            if (null == act) {
                return;
            }
            switch (msg.what) {
                // 正在下载
                case DOWNLOADED_NEWSMESSAGE:
                    if (act.messageRAdapter == null) {
                        act.initAdapter();

                        act.messageRAdapter.setData(act.getLoadInfo());
                        act. mPullLoadMoreRecyclerView.setAdapter(act.messageRAdapter);
                    } else {
                        act.messageRAdapter.setData(act.getLoadInfo());
//                        act. messageRAdapter.notifyDataSetChanged();
                    }
                    act. mPullLoadMoreRecyclerView.setRefreshing(false);
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
            if(loadMore()){
                mCount = mCount + 1;
                loadData();
            }else{
                mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();
            }
        }
    }

    private void setRefresh() {
//        mXinWen_RecyclerViewAdapter.getDataList().clear();
      if(messageRAdapter!=null)  messageRAdapter.clearDate();
        mCount = 0;
    }


    @Override
    public void loadServerData(String response, int flag) {
//        MyToast.show(mContext, "返回的结果为：：：：" + response);
        Log.i("返回的结果为", response.toString());


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


    }

    @Override
    public void loadDataFailed(String request,int flag) {
        MyToast.show(mContext, "返回值失败" + request.toString());
        Log.i("返回值失败", request.toString());


        Message msg = new Message();
        msg.what = DOWNLOADED_ERROR;
        mtotalHandler.sendMessage(msg);

    }

    public abstract void setLoadInfo(String info) throws JSONException;
    public abstract ArrayList<?> getLoadInfo() ;
    public abstract CommRecyclerAdapter initAdapter();
//    public abstract PostFormBuilder httpBuilder();
    public abstract JSONObject postJsonObject() throws JSONException;
    public abstract String postURL();
    public abstract String getURL();
    public abstract boolean loadMore();//表示是否有更多需要加载
}

//package com.wuwo.im.fragement;
//
//
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Toast;
//
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import com.squareup.okhttp.Request;
//import com.wuwo.im.adapter.XiaoxiViewAdapter;
//import com.wuwo.im.bean.newsMessage;
//import com.wuwo.im.util.MyToast;
//import com.wuwo.im.view.PullLoadMoreRecyclerView;
//import com.zhy.http.okhttp.OkHttpUtils;
//import com.zhy.http.okhttp.callback.StringCallback;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import im.wuwo.com.wuwo.R;
//
///**
// *
// * 用于验证  recyclelibary 中的加载和下拉的功能的
//*desc
//*@author 王明远
//*@日期： 2016/1/15 9:24
//*@版权:Copyright 上海数慧系统有限公司  All rights reserved.
//*/
//
//public class Portal_XiaoXiFragment_old extends BaseAppFragment {
//
//    private PullLoadMoreRecyclerView mPullLoadMoreRecyclerView;
//    private int mCount = 1;
//    private XiaoxiViewAdapter mRecyclerViewAdapter;
//
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_xiaoxi_recycler, container, false);
//    }
//
//    @Override
//    public void onViewCreated(View view, Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        mPullLoadMoreRecyclerView = (PullLoadMoreRecyclerView) view.findViewById(R.id.pullLoadMoreRecyclerView);
//        mPullLoadMoreRecyclerView.setRefreshing(true);
////        new DataAsyncTask().execute();
//
//        loadData();
//        mPullLoadMoreRecyclerView.setLinearLayout();//setGridLayout  setStaggeredGridLayout
////        mPullLoadMoreRecyclerView.setGridLayout(3);
////        mPullLoadMoreRecyclerView.setStaggeredGridLayout(2);
//
//        mPullLoadMoreRecyclerView.setOnPullLoadMoreListener(new PullLoadMoreListener());
//
//    }
//
//    @Override
//    public String getFragmentName() {
//        return "Portal_XiaoXiFragment";
//    }
//
//
//    private ArrayList<newsMessage> newsMessage_userlist = new ArrayList<newsMessage>(); //记录所有的最新消息
//    Gson gson = new GsonBuilder().create();
//
//    //    /从网络加载流转日志数据并展示出来
//    private void loadData() {
//        OkHttpUtils
//                .post()
//                .url("http://58.246.138.178:8000/DistMobile/mobileMeeting!getAllMeeting.action")
//                .addParams("newsMessageId", "4028826f505a3b0f01506553b0c80c3a")
//                .addParams("page",mCount+"")
//                .build()
//                .execute(new StringCallback() {
//                    @Override
//                    public void onError(Request request, Exception e) {
//                        MyToast.show(getActivity(), "获取服务器信息失败", Toast.LENGTH_LONG);
//                    }
//
//                    @Override
//                    public void onResponse(String response) {
//                        if (response != null) {
//                            java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<List<newsMessage>>() {
//                            }.getType();
//
//                            newsMessage_userlist = gson.fromJson(response, type);
//                            //在view界面上展示结果
//                            Message msg = new Message();
//                            msg.what = DOWNLOADED_NEWSMESSAGE;
//                            mtotalHandler.sendMessage(msg);
//                        }
//                    }
//                });
//
//    }
//
//    public static final int DOWNLOADED_NEWSMESSAGE = 0;
//    private Handler mtotalHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                // 正在下载
//                case DOWNLOADED_NEWSMESSAGE:
////                    news_loading_progressbar.setVisibility(View.GONE);
////                    showOnView();
//
//                    if (mRecyclerViewAdapter == null) {
//                        if(newsMessage_userlist.size()>=pageSize){
//                            mRecyclerViewAdapter = new XiaoxiViewAdapter(getActivity(), newsMessage_userlist.subList(0,pageSize-1));
//                        }else{
//                            mRecyclerViewAdapter = new XiaoxiViewAdapter(getActivity(), newsMessage_userlist);
//                        }
//
//                        mPullLoadMoreRecyclerView.setAdapter(mRecyclerViewAdapter);
//
////                设置点击监听事件
//                        mRecyclerViewAdapter.setOnItemClickLitener(new XiaoxiViewAdapter.OnItemClickLitener()
//                        {
//                            @Override
//                            public void onItemClick(View view, int position)
//                            {
//                                Toast.makeText(getActivity(), position + " click",
//                                        Toast.LENGTH_SHORT).show();
//
///*                                Intent intent2 = new Intent(getActivity(), ChatListActivity.class);
//                                //        intent2.putExtra("content", newsMessagelist.get(tempPosition-1).getContent());
//                                //                intent2.putExtra("url", DistApp.serverAbsolutePath + "/snews!mobileNewsdetail.action?news.id=4028816f4d4be502014d4c0e22dc003d");
//                                //                intent2.putExtra("name", "消息通知");
//                                startActivity(intent2);
//                                getActivity().overridePendingTransition(0, 0);*/
//
//                            }
//                            @Override
//                            public void onItemLongClick(View view, int position)
//                            {
//                                Toast.makeText(getActivity(), position + " long click",
//                                        Toast.LENGTH_SHORT).show();
////                mRecyclerViewAdapter.removeData(position);
//                            }
//                        });
//
//                    } else {
//                        mRecyclerViewAdapter.getDataList().addAll(newsMessage_userlist);
//                        mRecyclerViewAdapter.notifyDataSetChanged();
//                    }
//                    mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();
//
//                    break;
//                default:
//                    break;
//            }
//        }
//
//        ;
//    };
//
//    int pageSize=20;
//
//    class PullLoadMoreListener implements PullLoadMoreRecyclerView.PullLoadMoreListener {
//        @Override
//        public void onRefresh() {
//            setRefresh();
//            loadData();
//        }
//
//        @Override
//        public void onLoadMore() {
//            mCount = mCount + 1;
//            loadData();
//        }
//    }
//
//    private void setRefresh() {
//        mRecyclerViewAdapter.getDataList().clear();
//        mCount = 1;
//    }
//}

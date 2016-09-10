package com.wuwo.im.fragement;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hyphenate.chatuidemo.db.DemoDBManager;
import com.hyphenate.chatuidemo.db.UserDao;
import com.wuwo.im.activity.UserInfoEditActivity;
import com.wuwo.im.adapter.CommRecyclerAdapter;
import com.wuwo.im.adapter.CommRecyclerViewHolder;
import com.wuwo.im.bean.LocalUser;
import com.wuwo.im.config.WowuApp;
import com.wuwo.im.view.PullLoadMoreRecyclerView;
import com.zhy.http.okhttp.service.LoadserverdataService;
import com.zhy.http.okhttp.service.loadServerDataListener;

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
    private ArrayList<LocalUser.DataBean> chat_userlist = new ArrayList<LocalUser.DataBean>(); //记录所有的最新消息
//     private SearchView search_view;
    private String searchinfo;
    CommRecyclerAdapter messageRAdapter;

    mHandlerWeak mtotalHandler;

    LoadserverdataService loadDataService;

    protected EditText query;
    protected ImageButton clearSearch;
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

//        search_view = (SearchView) view.findViewById(R.id.search_view);
//        search_view.setVisibility(View.VISIBLE);
////       loadData();
//        search_view.setSearchListener(new SearchView.searchListener() {
//            @Override
//            public void searchInfo(String info) {
//                searchInfoTag = searchInfoTag + 1;
//                searchinfo = info;
//                if (info != null && !info.equals("")) {
//                    searchData(info, searchInfoTag);
//                } else {
//                    boolean temp = search_view.resertSearch();
//                    if (messageRAdapter != null) {//说明已经加载过搜索
//                        setRefresh();
//                        messageRAdapter.notifyDataSetChanged();
//                    }
//                }
//            }
//        });


        initQuery(view);

    }
    List<LocalUser.DataBean> searchList=new ArrayList<LocalUser.DataBean>() ;
    String currentSearchInfo="";
    private void initQuery(View view) {
        query = (EditText) view.findViewById(R.id.query);
        // button to clear content in search bar
        clearSearch = (ImageButton) view.findViewById(R.id.search_clear);

        query.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchInfo=s.toString();
                if (s.length() > 0) {
                    clearSearch.setVisibility(View.VISIBLE);
                    SearchFilter(s);
                } else {
                    clearSearch.setVisibility(View.INVISIBLE);

                    searchList=chat_userlist;
                    Message msg = new Message();
                    msg.what = SEARCH_DATA;
                    mtotalHandler.sendMessage(msg);

                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
        clearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query.getText().clear();
                hideSoftKeyboard();
            }
        });

    }

    private void SearchFilter(final CharSequence s) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(chat_userlist!=null){
                    searchList=new ArrayList<LocalUser.DataBean>() ;
                    for(int i=0;i<chat_userlist.size();i++){
                        if(((LocalUser.DataBean)chat_userlist.get(i)).getName().contains(s)){
                            LocalUser.DataBean temp= (LocalUser.DataBean)chat_userlist.get(i);
                            searchList.add(temp);
                        }
                    }
                    if(s.toString().equals(currentSearchInfo)){
                        Message msg = new Message();
                        msg.what = SEARCH_DATA;
                        mtotalHandler.sendMessage(msg);
                    }
                }
            }
        }).start();
    }

    protected void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getActivity().getCurrentFocus() != null)
                inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }



    //    /从网络加载流转日志数据并展示出来
//    private void searchData(String info, final int searchInfoTag) {
//        OkHttpUtils
//                .post()
//                .url("http://58.246.138.178:8081/DistMobile/mobileMeeting!getAllMeeting.action")
//                .addParams("MeetingId", "4028826f505a3b0f01506553b0c80c3a")
//                .addParams("page",mCount+"")
//                .build()
//                .execute(new StringCallback() {
//                    @Override
//                    public void onError(Request request, Exception e) {
//                        MyToast.show(mContext, "获取服务器信息失败", Toast.LENGTH_LONG);
//                        mPullLoadMoreRecyclerView.setRefreshing(false);
//                        mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();
//                    }
//
//                    @Override
//                    public void onResponse(String response) {
//                        if (response != null) {
//                            java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<List<LocalUser.DataBean >>() {
//                            }.getType();
//
//                            ssmeeting_userlist = gson.fromJson(response, type);
//                            //在view界面上展示结果
//                            Message msg = new Message();
//                            msg.what = DOWNLOADED_LocalUser;
//                            msg.arg1=searchInfoTag;
//                            mtotalHandler.sendMessage(msg);
//
//                        }
//                    }
//                });
//    }

    Gson gson = new GsonBuilder().create();


    private final int LOAD_DATA=1;
    //    从网络加载流转日志数据并展示出来
    private void loadData() {
        loadDataService.loadGetJsonRequestData( WowuApp.GetFriendsURL+"?lon=" + mSettings.getString("longitude", "0") + "&lat=" + mSettings.getString("latitude", "0") ,LOAD_DATA);//+ "&userId=" + WowuApp.UserId+ "&PhoneNumber="+WowuApp.PhoneNumber
    }

    public static final int DOWNLOADED_LocalUser= 0;
    public static final int DOWNLOADED_ERROR = 1;
    public static final int REFRESH_DATA = 2;
    public static final int SEARCH_DATA = 3;

    @Override
    public void onClick(View v) {

    }

    @Override
    public void loadServerData(String response, int flag) {
        Log.i("Portal_ContactFragment:",response+" ;");
        switch (flag){
            case LOAD_DATA:
                try {
                    setLoadInfo(response);
                    Message msg = new Message();
                    msg.what = DOWNLOADED_LocalUser;
                    mtotalHandler.sendMessage(msg);
                } catch (Exception e) {
//                                    e.printStackTrace();
                    loadError();
                }
                break;
        }
    }

    @Override
    public void loadDataFailed(String response, int flag) {
        switch (flag){
            case LOAD_DATA:
                try {
                    setLoadInfo(null);

                    Message msg = new Message();
                    msg.what = DOWNLOADED_LocalUser;
                    mtotalHandler.sendMessage(msg);
                } catch (Exception e) {
                    loadError();
                    return;
                }
//                loadError();
                break;
        }
    }



    private void loadError() {
        Message msg = new Message();
        msg.what = DOWNLOADED_ERROR;
        mtotalHandler.sendMessage(msg);
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
                case SEARCH_DATA:
                    if(act !=null &&  act.messageRAdapter !=null ){
                        act.messageRAdapter.setData2(act.searchList);
                    }
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


    public void setLoadInfo(final String totalresult) throws JSONException {

        Gson gson = new GsonBuilder().create();
        java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<List<LocalUser.DataBean>>() {}.getType();
        if (totalresult != null) {
            chat_userlist = gson.fromJson(totalresult, type);
//          缓存到数据库
            new Thread(new Runnable() {
                @Override
                public void run() {
                    DemoDBManager.getInstance().saveCacheJson(UserDao.CACHE_MAIN_CONTRACT,totalresult);
                }
            }).start();
        }else{
//            读取缓存信息
            String CacheJsonString = DemoDBManager.getInstance().getCacheJson(UserDao.CACHE_MAIN_CONTRACT);
            chat_userlist = gson.fromJson(CacheJsonString, type);
        }
    }


    public ArrayList<?> getLoadInfo() {
        return chat_userlist;
    }


    public CommRecyclerAdapter initAdapter() {
        messageRAdapter = new CommRecyclerAdapter<LocalUser.DataBean>(getActivity(), R.layout.item_contact_list) {
            @Override
            public void convert(CommRecyclerViewHolder viewHolder, LocalUser.DataBean mainMessage) {

                //对对应的View进行赋值
                viewHolder.setText(R.id.contract_title, mainMessage.getName());

                viewHolder.setText(R.id.contract_code, mainMessage.getDistance()+" | "+ mainMessage.getBefore());
                viewHolder.setText(R.id.contact_userinfo, mainMessage.getDisposition());

                ImageView tv_isvip= (ImageView) viewHolder.getView(R.id.tv_isvip);
                if(mainMessage.isIsVip()){
                    tv_isvip.setVisibility(View.VISIBLE);
                }else{
                    tv_isvip.setVisibility(View.GONE);
                }

                ImageView yewu_code_m= (ImageView) viewHolder.getView(R.id.yewu_code_m);
                ImageView yewu_code_w= (ImageView) viewHolder.getView(R.id.yewu_code_w);

                SimpleDraweeView portal_news_img = (SimpleDraweeView) viewHolder.getView(R.id.news_label_pic);
                portal_news_img.setImageURI(Uri.parse(mainMessage.getPhotoUrl()));
                GenericDraweeHierarchy hierarchy = portal_news_img.getHierarchy();

                if(mainMessage.getGender()==1){
//                    genderm.setVisibility(View.VISIBLE);
//                    genderw.setVisibility(View.GONE);
//                    genderm.setText(mainMessage.getAge()+"");
                    yewu_code_m.setVisibility(View.VISIBLE);
                    yewu_code_w.setVisibility(View.GONE);

                    hierarchy.setControllerOverlay(getResources().getDrawable(R.drawable.overlay_male));

                }else{
//                    genderm.setVisibility(View.GONE);
//                    genderw.setVisibility(View.VISIBLE);
//                    genderw.setText(mainMessage.getAge()+"");
                    yewu_code_m.setVisibility(View.GONE);
                    yewu_code_w.setVisibility(View.VISIBLE);
                    hierarchy.setControllerOverlay(getResources().getDrawable(R.drawable.overlay_fmale));
                }

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
                intent2.putExtra("localUser", chat_userlist.get(position));
                intent2.putExtra("userType", "contact");
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

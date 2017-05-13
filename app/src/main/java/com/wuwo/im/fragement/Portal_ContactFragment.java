package com.wuwo.im.fragement;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hyphenate.chatuidemo.db.DemoDBManager;
import com.hyphenate.chatuidemo.db.UserDao;
import com.net.grandcentrix.tray.AppPreferences;
import com.wuwo.im.activity.UserInfoEditActivity;
import com.wuwo.im.adapter.CommRecyclerAdapter;
import com.wuwo.im.adapter.CommRecyclerViewHolder;
import com.wuwo.im.bean.LocalUser;
import com.wuwo.im.bean.SanGuan;
import com.wuwo.im.config.WowuApp;
import com.wuwo.im.util.LogUtils;
import com.wuwo.im.util.MyToast;
import com.wuwo.im.util.UtilsTool;
import com.wuwo.im.view.PullLoadMoreRecyclerView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.service.LoadserverdataService;
import com.zhy.http.okhttp.service.loadServerDataListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import im.imxianzhi.com.imxianzhi.R;

/**
 * 首页 设置 fragment
 *
 * @author dewyze
 */
@SuppressLint("ValidFragment")
public class Portal_ContactFragment extends BaseAppFragment implements View.OnClickListener, loadServerDataListener {
    private final String TAG = "Portal_ContactFragment";
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
//        initViews(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadData();
    }

    private int searchInfoTag;//由于采用的是快速搜素方法，假如用户连续输入，而网络延迟，可能搜索过程中产生的结果并不是用户想要的这是就要过滤掉

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


/*        view.findViewById(R.id.ln_menu).setVisibility(View.VISIBLE);

        TextView tv_fragement_menu1 = (TextView) view.findViewById(R.id.tv_fragement_menu1);
        TextView tv_fragement_menu2 = (TextView) view.findViewById(R.id.tv_fragement_menu2);
        TextView tv_fragement_menu3 = (TextView) view.findViewById(R.id.tv_fragement_menu3);
        TextView tv_fragement_menu4 = (TextView) view.findViewById(R.id.tv_fragement_menu4);
        tv_fragement_menu1.setOnClickListener(this);
        tv_fragement_menu2.setOnClickListener(this);
        tv_fragement_menu3.setOnClickListener(this);
        tv_fragement_menu4.setOnClickListener(this);*/

        initQuery(view);

    }

    List<LocalUser.DataBean> searchList = new ArrayList<LocalUser.DataBean>();
    String currentSearchInfo = "";

    private void initQuery(View view) {
        query = (EditText) view.findViewById(R.id.query);
        // button to clear content in search bar
        clearSearch = (ImageButton) view.findViewById(R.id.search_clear);

        query.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchInfo = s.toString();
                if (s.length() > 0) {
                    clearSearch.setVisibility(View.VISIBLE);
                    SearchFilter(s);
                } else {
                    clearSearch.setVisibility(View.INVISIBLE);

                    searchList = chat_userlist;
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
                if (chat_userlist != null) {
                    searchList = new ArrayList<LocalUser.DataBean>();
                    for (int i = 0; i < chat_userlist.size(); i++) {
                        if (((LocalUser.DataBean) chat_userlist.get(i)).getName().contains(s)) {
                            LocalUser.DataBean temp = (LocalUser.DataBean) chat_userlist.get(i);
                            searchList.add(temp);
                        }
                    }
                    if (s.toString().equals(currentSearchInfo)) {
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
    String longitude;
    String latitude;

    public void setLocation(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    private final int LOAD_DATA = 1;

    //    从网络加载流转日志数据并展示出来
    private void loadData() {
//        if(longitude!=null && latitude!=null){
//            loadDataService.loadGetJsonRequestData( WowuApp.GetFriendsURL+"?lon=" +longitude+ "&lat=" +latitude ,LOAD_DATA);//+ "&userId=" + WowuApp.UserId+ "&PhoneNumber="+WowuApp.PhoneNumber
//        }else{
//            loadDataService.loadGetJsonRequestData( WowuApp.GetFriendsURL+"?lon=" +mSettings.getString("longitude","121.716738" ) + "&lat=" + mSettings.getString("latitude", "31.196566") ,LOAD_DATA);//+ "&userId=" + WowuApp.UserId+ "&PhoneNumber="+WowuApp.PhoneNumber
//        }
//          LogUtils.i("Portal_ContactFragment：", WowuApp.GetFriendsURL+ "?lon=" + mSettings.getString("longitude", "121.716738 ") + "&lat=" + mSettings.getString("latitude", "31.196566") + "&pageIndex=" + mCount);
//        LogUtils.i("Portal_ContactFragment：", WowuApp.GetFriendsURL+ "?lon=" +((WowuApp)getActivity().getApplication()).getLongitude()+ "&lat=" +((WowuApp)getActivity().getApplication()).getLatitude() + "&pageIndex=" + mCount);
//        LogUtils.i("Portal_ContactFragment：", WowuApp.GetFriendsURL+ "?lon=" +longitude+ "&lat=" +latitude + "&pageIndex=" + mCount);

/*
        AppPreferences appPreferences = new AppPreferences(mContext.getApplicationContext()); // this Preference comes for free from the library
// save a key value pair
        LogUtils.i(TAG, WowuApp.GetFriendsURL+ "?lon=" + appPreferences.getString("longitude", "0.1")+ "&lat=" + appPreferences.getString("latitude", "0.1") + "&pageIndex=" + mCount);
*/


        AppPreferences appPreferences = new AppPreferences(mContext.getApplicationContext());
        loadDataService.loadGetJsonRequestData(WowuApp.GetFriendsURL + "?lon=" + appPreferences.getString("longitude", "0.1") + "&lat=" + appPreferences.getString("latitude", "0.1"), LOAD_DATA);
        LogUtils.i(TAG, WowuApp.GetFriendsURL + "?lon=" + appPreferences.getString("longitude", "0.1") + "&lat=" + appPreferences.getString("latitude", "0.1") + "&pageIndex=" + mCount);
    }

    boolean loactionChange = false;

    public void loadByLocationChange() {
        loactionChange = true;
        loadData();
    }


    public static final int DOWNLOADED_LocalUser = 0;
    public static final int DOWNLOADED_ERROR = 1;
    public static final int SEARCH_DATA = 3;

    public static final int SANGUAN_PICK = 200;
    public static final int REFRESH_DATA = 8;
    public static final int LOADING = 9;
    private ProgressDialog pg;
    SanGuan mSanGuan = new SanGuan();

    @Override
    public void onClick(View v) {
/*        switch (v.getId()) {
            case R.id.tv_fragement_menu1:
                Intent tempIntent = new Intent(mContext, VisitedMeUsersActivity.class);
                startActivity(tempIntent);
                mContext.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.tv_fragement_menu2:
                Intent tempIntent2 = new Intent(mContext, BlackUserActivity.class);
                startActivity(tempIntent2);
                mContext.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.tv_fragement_menu3:
                Intent tempIntent3 = new Intent(mContext, CharacterTestHistoryActivity.class);
                startActivity(tempIntent3);
                mContext.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.tv_fragement_menu4:
                Intent tempIntent4 = new Intent(mContext, CharacterDetailActivity.class);// ShareCharacterReportActivity
                startActivity(tempIntent4);
                mContext.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
        }*/

    }

    @Override
    public void loadServerData(String response, int flag) {
        LogUtils.i(TAG, response + " ;");
        switch (flag) {
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
            case SANGUAN_PICK:
                if (flag == SANGUAN_PICK && response != null) {  //说明是三观配返回的值
                    java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<SanGuan>() {
                    }.getType();
                    mSanGuan = gson.fromJson(response, type);
                    Message msg = new Message();
                    msg.what = SANGUAN_PICK;
                    mtotalHandler.sendMessage(msg);
                }
                break;
        }
    }

    @Override
    public void loadDataFailed(String response, int flag) {
        if(response!=null &&  response.equals("401")){
            Intent intent = new Intent(OkHttpUtils.TOKEN_OUTDATE);
            intent.putExtra("token_out_date",  "token_out_date");
            mContext.sendBroadcast(intent);
            return;
        }

        switch (flag) {
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
                    } else if (act.messageRAdapter != null && act.loactionChange) {
                        act.loactionChange = false;
                        act.messageRAdapter.setData2(act.getLoadInfo());
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
                    if (act != null && act.messageRAdapter != null) {
                        act.messageRAdapter.setData2(act.searchList);
                    }
                    break;
                case LOADING:
                    act.pg = UtilsTool.initProgressDialog(act.mContext, "正在连接.....");
                    act.pg.show();  // if (act.pg != null && act.pg.isShowing()) act.pg.dismiss();
                    break;
                case SANGUAN_PICK:
                    if (act.pg != null && act.pg.isShowing()) act.pg.dismiss();
//                    act.showSanguanPickDialog();
                    if (act != null && act.mSanGuan != null) {
                        if (act.mSanGuan.getSuccessfully() != 2) {
                            act.showSanguanPickDialog();
                        } else {
//                            MyToast.show(act.mContext, "普通会员每天只可配对三次");
                            UtilsTool temp = new UtilsTool();
                            temp.showSetVipDialog(act.mContext);
                        }
                    } else {
                        MyToast.show(act.mContext, "获取失败，请重试");
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

        new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intentLocation = new Intent(WowuApp.RESET_LOCATION_TIME);
                intentLocation.putExtra("locationNoChangLength", 3 * 1000 + "");
                mContext.sendBroadcast(intentLocation);
            }
        }).start();
//        mXinWen_RecyclerViewAdapter.getDataList().clear();
        if (messageRAdapter != null) messageRAdapter.clearDate();
        mCount = 0;
    }


    public void setLoadInfo(final String totalresult) throws JSONException {

        Gson gson = new GsonBuilder().create();
        java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<List<LocalUser.DataBean>>() {
        }.getType();
        if (totalresult != null) {
            chat_userlist = gson.fromJson(totalresult, type);
//          缓存到数据库
            new Thread(new Runnable() {
                @Override
                public void run() {
                    DemoDBManager.getInstance().saveCacheJson(UserDao.CACHE_MAIN_CONTRACT, totalresult);
                }
            }).start();
        } else {
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
                viewHolder.setText(R.id.contract_title, mainMessage.getRemarkName()!=null ?mainMessage.getRemarkName(): mainMessage.getName());

                viewHolder.setText(R.id.contract_code, mainMessage.getDistance());// + " | " + mainMessage.getBefore()
                viewHolder.setText(R.id.contact_userinfo, mainMessage.getDisposition());

                ImageView tv_isvip = (ImageView) viewHolder.getView(R.id.tv_isvip);

                if (mainMessage.isIsVip()) {
                    tv_isvip.setVisibility(View.VISIBLE);
                    if( mainMessage.getVipType()==5 ){
                        tv_isvip.setImageResource(R.drawable.svip);
                    }else{
                        tv_isvip.setImageResource(R.drawable.vip);
                    }
                } else {
                    tv_isvip.setVisibility(View.GONE);
//                    tv_isvip.setImageResource(R.drawable.no_vip);
                }


                ImageView yewu_code_m = (ImageView) viewHolder.getView(R.id.yewu_code_m);
                ImageView yewu_code_w = (ImageView) viewHolder.getView(R.id.yewu_code_w);

                SimpleDraweeView portal_news_img = (SimpleDraweeView) viewHolder.getView(R.id.news_label_pic);
                if (mainMessage.getPhotoUrl() != null) {
                    portal_news_img.setImageURI(Uri.parse(mainMessage.getPhotoUrl()));
                }
                GenericDraweeHierarchy hierarchy = portal_news_img.getHierarchy();

                if (mainMessage.getGender() == 1) {
//                    genderm.setVisibility(View.VISIBLE);
//                    genderw.setVisibility(View.GONE);
//                    genderm.setText(mainMessage.getAge()+"");
                    yewu_code_m.setVisibility(View.VISIBLE);
                    yewu_code_w.setVisibility(View.GONE);

                    hierarchy.setControllerOverlay(getResources().getDrawable(R.drawable.overlay_male1));

                } else {
//                    genderm.setVisibility(View.GONE);
//                    genderw.setVisibility(View.VISIBLE);
//                    genderw.setText(mainMessage.getAge()+"");
                    yewu_code_m.setVisibility(View.GONE);
                    yewu_code_w.setVisibility(View.VISIBLE);
                    hierarchy.setControllerOverlay(getResources().getDrawable(R.drawable.overlay_fmale1));
                }

                final String userId = mainMessage.getUserId();
                yewu_code_w.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadSanguan(userId);
                    }
                });
                yewu_code_m.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadSanguan(userId);
                    }
                });
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
                if (currentSearchInfo != null && !currentSearchInfo.equals("") && searchList != null && searchList.size() > 0) {
                    intent2.putExtra("localUser", searchList.get(position));
                } else {
                    intent2.putExtra("localUser", chat_userlist.get(position));
                }
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
        return TAG;
    }


    void loadSanguan(final String userId) {
        //1、加载进度条
        Message msg = new Message();
        msg.what = LOADING;
        mtotalHandler.sendMessage(msg);
        //加载user 三观配

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject json = new JSONObject();
                    json.put("userId", userId);
                    loadDataService.loadPostJsonRequestData(WowuApp.JSON, WowuApp.MatchURL + "?userId=" + userId, json.toString(), SANGUAN_PICK);
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
        View view = mContext.getLayoutInflater().inflate(com.hyphenate.easeui.R.layout.dialog_sanguan_pick, null);
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
            if (mSanGuan.getUserPhotoUrl2() != null) {
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
        } else {
            MyToast.show(mContext, "获取信息失败");
        }


        // 设置显示动画
        window.setWindowAnimations(com.hyphenate.easeui.R.style.main_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = mContext.getWindowManager().getDefaultDisplay().getHeight();
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

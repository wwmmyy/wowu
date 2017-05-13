package com.wuwo.im.fragement;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.net.grandcentrix.tray.AppPreferences;
import com.wuwo.im.adapter.CommRecyclerAdapter;
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

import im.imxianzhi.com.imxianzhi.R;

/**
 * 首页 设置 fragment
 *
 * @author dewyze
 */
@SuppressLint("ValidFragment")
abstract class BasePortal_TabFragment extends BaseAppFragment implements loadServerDataListener {

    Activity mContext;
    SharedPreferences mSettings;
    Editor editor;
    public PullLoadMoreRecyclerView mPullLoadMoreRecyclerView;
    public int mCount = 0;
    LoadserverdataService loadDataService;
    private ProgressDialog pg;
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
        mtotalHandler = new mHandlerWeak(this);
        loadDataService = new LoadserverdataService(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pullloadmore_recyclerview, container, false);
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

    private void initViews(View view) {
        mPullLoadMoreRecyclerView = (PullLoadMoreRecyclerView) view.findViewById(R.id.pullLoadMoreRecyclerView);
        mPullLoadMoreRecyclerView.setRefreshing(true);
        mPullLoadMoreRecyclerView.setLinearLayout();//setGridLayout  setStaggeredGridLayout
////      mPullLoadMoreRecyclerView.setGridLayout(3);
//        mPullLoadMoreRecyclerView.setStaggeredGridLayout(2);
        mPullLoadMoreRecyclerView.setOnPullLoadMoreListener(new PullLoadMoreListener());
//       loadData();
    }

    public void refresh() {
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

    Gson gson = new GsonBuilder().create();
    String longitude;
    String latitude;

    public String getLatitude() {
        if (latitude != null && !latitude.equals("")) {
            return latitude;
        } else {
            return mSettings.getString("latitude", "31.196566");
        }

    }

    public String getLongitude() {
        if (longitude != null && !longitude.equals("")) {
            return longitude;
        } else {
            return mSettings.getString("longitude", "121.716738");
        }
    }

    public void setLocation(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }


    //    从网络加载流转日志数据并展示出来
    private void loadData() {

        //      当用户刷新数据时，更新定位频率
        new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intentLocation = new Intent(WowuApp.RESET_LOCATION_TIME);
                intentLocation.putExtra("locationNoChangLength", 3 * 1000 + "");
                mContext.sendBroadcast(intentLocation);
            }
        }).start();


        try {
            if (postURL() != null) {
                loadDataService.loadPostJsonRequestData(WowuApp.JSON, postURL(), postJsonObject().put("page", mCount + "").toString(), R.id.tv_register_two_sure);
            } else {

/*//                loadDataService.loadGetJsonRequestData(getURL() + "?lon=" + WowuApp.longitude + "&lat=" + WowuApp.latitude + "&pageIndex=" + mCount, 0);
                loadDataService.loadGetJsonRequestData(getURL() + "?lon=" + getLongitude() + "&lat=" + getLatitude() + "&pageIndex=" + mCount, 0);
             //   LogUtils.i("BasePortal_TabFragment：", getURL() + "?lon=" + mSettings.getString("longitude", "121.716738 ") + "&lat=" + mSettings.getString("latitude", "31.196566") + "&pageIndex=" + mCount);
                LogUtils.i("BasePortal_TabFragment：", getURL() + "?lon=" + getLongitude() + "&lat=" + getLatitude() + "&pageIndex=" + mCount);*/


                AppPreferences appPreferences = new AppPreferences(mContext.getApplicationContext());
                loadDataService.loadGetJsonRequestData(getURL() + "?lon=" + appPreferences.getString("longitude", "0.1") + "&lat=" + appPreferences.getString("latitude", "0.1") + "&pageIndex=" + mCount, 0);


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public static final int SANGUAN_PICK = 200;
    public static final int SANGUAN_PICK_FAIL = 201;
    public static final int DOWNLOADED_NEWSMESSAGE = 0;
    public static final int DOWNLOADED_ERROR = 1;
    public static final int REFRESH_DATA = 2;
    public static final int LOADING = 3;


    private static class mHandlerWeak extends Handler {
        private WeakReference<BasePortal_TabFragment> activity = null;

        public mHandlerWeak(BasePortal_TabFragment act) {
            super();
            this.activity = new WeakReference<BasePortal_TabFragment>(act);
        }

        @Override
        public void handleMessage(Message msg) {
            BasePortal_TabFragment act = activity.get();
            if (null == act) {
                return;
            }
            switch (msg.what) {
                // 正在下载
                case DOWNLOADED_NEWSMESSAGE:
                    LogUtils.i("BasePortal_TabFragm：：", "返回过来的条数值" + act.getLoadInfo().size());
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
//                            UtilsTool temp=new UtilsTool();
//                            temp.showExitDialog(act.mContext);
                        } else {
//                            MyToast.show(act.mContext,  "普通会员每天只可配对三次");
//                            act.showSetVipDialog();
                            UtilsTool temp = new UtilsTool();
                            temp.showSetVipDialog(act.mContext);
                        }
                    } else {
                        MyToast.show(act.mContext, "获取失败，请重试");
                    }


                    break;
                case SANGUAN_PICK_FAIL:
                    if (act.pg != null && act.pg.isShowing()) act.pg.dismiss();
                    MyToast.show(act.mContext, "返回结果异常");
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

    SanGuan mSanGuan = new SanGuan();

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


    @Override
    public void loadServerData(String response, int flag) {
//        MyToast.show(mContext, "返回的结果为：：：：" + response);
        LogUtils.i("BasePortal_TabFragm：：", response.toString());

        if (flag == SANGUAN_PICK) {  //说明是三观配返回的值
            if (response != null) {
                java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<SanGuan>() {
                }.getType();
                mSanGuan = gson.fromJson(response, type);
//            MyToast.show(mContext, "返回的结果为：：：：" + response);
                Message msg = new Message();
                msg.what = SANGUAN_PICK;
                mtotalHandler.sendMessage(msg);
            } else {
                Message msg = new Message();
                msg.what = SANGUAN_PICK_FAIL;
                mtotalHandler.sendMessage(msg);
            }
        } else {
            try {
                setLoadInfo(response);

                Message msg = new Message();
                msg.what = DOWNLOADED_NEWSMESSAGE;
                mtotalHandler.sendMessage(msg);
            } catch (Exception e) {
//                                    e.printStackTrace();
                loadError();
            }
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

 /*       MyToast.show(mContext, "返回值失败" + request.toString());
        LogUtils.i("BasePortal_TabFragm：：", request.toString());*/
        if (flag == SANGUAN_PICK) {  //说明是三观配返回的值
            Message msg = new Message();
            msg.what = SANGUAN_PICK_FAIL;
            mtotalHandler.sendMessage(msg);
        } else {

            try {
                setLoadInfo(null);

                Message msg = new Message();
                msg.what = DOWNLOADED_NEWSMESSAGE;
                mtotalHandler.sendMessage(msg);
            } catch (Exception e) {
                loadError();
                return;
            }
            loadError();
        }

    }

    private void loadError() {
        Message msg = new Message();
        msg.what = DOWNLOADED_ERROR;
        mtotalHandler.sendMessage(msg);
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


    public abstract void setLoadInfo(String info) throws JSONException;

    public abstract ArrayList<?> getLoadInfo();

    public abstract CommRecyclerAdapter initAdapter();

    //    public abstract PostFormBuilder httpBuilder();
    public abstract JSONObject postJsonObject() throws JSONException;

    public abstract String postURL();

    public abstract String getURL();

    public abstract boolean loadMore();//表示是否有更多需要加载
}

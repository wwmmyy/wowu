package com.wuwo.im.fragement;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.net.grandcentrix.tray.AppPreferences;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.wuwo.im.activity.MyCharacterResultActivity;
import com.wuwo.im.adapter.JuBaoAdapter;
import com.wuwo.im.adapter.MyViewPagerAdapter;
import com.wuwo.im.adapter.XingGePuAdapter;
import com.wuwo.im.bean.CharacterPopInfo;
import com.wuwo.im.bean.CharacterPu;
import com.wuwo.im.config.WowuApp;
import com.wuwo.im.util.LogUtils;
import com.wuwo.im.util.MyToast;
import com.wuwo.im.util.UtilsTool;
import com.wuwo.im.view.PullLoadMoreRecyclerView;
import com.zhy.http.okhttp.service.LoadserverdataService;
import com.zhy.http.okhttp.service.loadServerDataListener;

import org.json.JSONException;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import im.imxianzhi.com.imxianzhi.R;

/**
 * 首页 设置 fragment
 *
 * @author dewyze
 */

@SuppressLint("ValidFragment")
public class Character_XingGePu_FindFragment_Update extends BaseAppFragment implements View.OnClickListener, loadServerDataListener, XingGePuAdapter.onViewClickListener{
    private Activity mContext;
    private SharedPreferences.Editor editor;
     private static final String TAG = "Character_XingGePu_FindFragment";
    private LoadserverdataService loadDataService;
     private mHandlerWeak mtotalHandler;
    private SharedPreferences mSettings;
    private RecyclerView mPullLoadMoreRecyclerView;
    XingGePuAdapter mXingGePuAdapter;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mContext = activity;
        mSettings = mContext.getSharedPreferences(WowuApp.PREFERENCE_KEY,
                android.content.Context.MODE_PRIVATE);
        loadDataService = new LoadserverdataService(this);
        mtotalHandler = new mHandlerWeak(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragement_character_xinggepu_update, container, false);
        initViews(view);
        loadData();
        return view;
    }

    /**
     * 根据性格谱获取性格弹框
     */
    public void loadData() {
        loadPopInfo(WowuApp.GetManualInfoURL, LOAD_DATA);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        initViews(view);
    }

    public static final int LOAD_RECOMMEND_DATA = 1;

    private void initViews(View view) {
        mSettings = mContext.getSharedPreferences(WowuApp.PREFERENCE_KEY, android.content.Context.MODE_PRIVATE);
        mPullLoadMoreRecyclerView = (RecyclerView) view.findViewById(R.id.pullLoadMoreRecyclerView);
//        mPullLoadMoreRecyclerView.setRefreshing(false);
//        mPullLoadMoreRecyclerView.setGridLayout(2);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 2);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mPullLoadMoreRecyclerView.setLayoutManager(gridLayoutManager);

        mXingGePuAdapter =new XingGePuAdapter(mContext, mCharacterPuList);
        mXingGePuAdapter.setOnViewClickListener(this);
        mPullLoadMoreRecyclerView.setAdapter(mXingGePuAdapter);

    }

    private void loadPopInfo(String url, int popDialog) {
        Message msg = new Message();
        msg.what = DATA_LOADING;
        mtotalHandler.sendMessage(msg);
        loadDataService.loadGetJsonRequestData(url, popDialog);//后面需要改过来
    }

    @Override
    public String getFragmentName() {
        return TAG;
    }


    @Override
    public void onClick(View v) {
        //判断是否为会员
        AppPreferences appPreferences = new AppPreferences(mContext.getApplicationContext());
        boolean  IsVip =appPreferences.getBoolean("IsVip",false);
        if(!IsVip){
            UtilsTool temp = new UtilsTool();
            temp.showSetVipDialogInfo(mContext,"查看性格谱，需要购买会员，点击“确定”，前往购买。");
            return ;
        }


        if (mCharacterPuList == null || mCharacterPuList.size() == 0) {
            return;
        }
        switch (v.getId()) {
            case R.id.user_info_detail:
                Intent temp1Intent = new Intent(mContext, MyCharacterResultActivity.class);
                startActivity(temp1Intent);
                mContext.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.rt_part1://5//
                loadPopInfo(WowuApp.GetManualDialogURL + "?id=" + mCharacterPuList.get(4).getId(), POP_DIALOG);
                break;
            case R.id.rt_part1_1://1
                loadPopInfo(WowuApp.GetManualDialogURL + "?id=" + mCharacterPuList.get(0).getId(), POP_DIALOG);
                break;
            case R.id.rt_part2://6
                loadPopInfo(WowuApp.GetManualDialogURL + "?id=" + mCharacterPuList.get(5).getId(), POP_DIALOG);
                break;
            case R.id.rt_part2_1://2
                loadPopInfo(WowuApp.GetManualDialogURL + "?id=" + mCharacterPuList.get(1).getId(), POP_DIALOG);
                break;
            case R.id.rt_part3://8
                loadPopInfo(WowuApp.GetManualDialogURL + "?id=" + mCharacterPuList.get(7).getId(), POP_DIALOG);
                break;
            case R.id.rt_part3_1://4
                loadPopInfo(WowuApp.GetManualDialogURL + "?id=" + mCharacterPuList.get(3).getId(), POP_DIALOG);
                break;
            case R.id.rt_part4://7
                loadPopInfo(WowuApp.GetManualDialogURL + "?id=" + mCharacterPuList.get(6).getId(), POP_DIALOG);
                break;
            case R.id.rt_part4_1://3
                loadPopInfo(WowuApp.GetManualDialogURL + "?id=" + mCharacterPuList.get(2).getId(), POP_DIALOG);
                break;
        }
    }

    public void loadDataByHistory(String historyId) {
//        loadPopInfo(WowuApp.GetManualInfoURL+" ?historyId="+historyId, LOAD_DATA);
//        loadDataService.loadGetJsonRequestData(WowuApp.GetManualInfoURL+" ?historyId="+historyId, LOAD_DATA);//后面需要改过来

        loadPopInfo(WowuApp.GetManualInfoURL+"?historyId="+historyId, LOAD_DATA);

    }

    public static final int LOAD_DATA = 5;
    public static final int UPDATE_UI = 6;
    public static final int POP_DIALOG = 7;
    public static final int DATA_LOADING = 9;

    @Override
    public void loadServerData(String response, int flag) {
        LogUtils.i(TAG, response + ";");
        switch (flag) {
            case LOAD_DATA:
                try {
                    setLoadInfo(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case POP_DIALOG:
                //采用弹出窗口的方式展示
                Gson gson = new GsonBuilder().create();
                if (response != null) {
                    java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<List<CharacterPopInfo>>() {
                    }.getType();
                    mCharacterPuPopList = gson.fromJson(response, type);
                    Message msg = new Message();
                    msg.what = POP_DIALOG;
                    mtotalHandler.sendMessage(msg);
                }
                break;
        }

    }


    private ArrayList<CharacterPu> mCharacterPuList = new ArrayList<CharacterPu>();
    private ArrayList<CharacterPopInfo> mCharacterPuPopList = new ArrayList<CharacterPopInfo>();

    public void setLoadInfo(String totalresult) throws JSONException {
        Gson gson = new GsonBuilder().create();
        if (totalresult != null) {
            java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<List<CharacterPu>>() {
            }.getType();
            mCharacterPuList = gson.fromJson(totalresult, type);
            ArrayList<CharacterPu>   mTempList = gson.fromJson(totalresult, type);
            if(mTempList!=null && mTempList.size()== 8){
                mCharacterPuList.clear();
                mCharacterPuList.add(mTempList.get(0));
                mCharacterPuList.add(mTempList.get(1));
                mCharacterPuList.add(mTempList.get(3));
                mCharacterPuList.add(mTempList.get(2));
                mCharacterPuList.add(mTempList.get(4));
                mCharacterPuList.add(mTempList.get(5));
                mCharacterPuList.add(mTempList.get(7));
                mCharacterPuList.add(mTempList.get(6));
            }
            Message msg = new Message();
            msg.what = UPDATE_UI;
            mtotalHandler.sendMessage(msg);
        }
    }

    public void setTitle(String info) {
//        if (tv_character_type_name != null) {
//            tv_character_type_name.setText(info);
//        }
    }

    private ProgressDialog pd;

    @Override
    public void onViewClick(View view, int type, int mposition) {
        //判断是否为会员

        AppPreferences appPreferences = new AppPreferences(mContext.getApplicationContext());
        boolean  IsVip =appPreferences.getBoolean("IsVip",false);
        if(!IsVip){
            UtilsTool temp = new UtilsTool();
            temp.showSetVipDialogInfo(mContext,"查看性格谱，需要购买会员，点击“确定”，前往购买。");
            return ;
        }
         loadPopInfo(WowuApp.GetManualDialogURL + "?id=" + mCharacterPuList.get(mposition).getId(), POP_DIALOG);
    }

    private static class mHandlerWeak extends Handler {
        private WeakReference<Character_XingGePu_FindFragment_Update> activity = null;
        public mHandlerWeak(Character_XingGePu_FindFragment_Update act) {
            super();
            this.activity = new WeakReference<Character_XingGePu_FindFragment_Update>(act);
        }

        @Override
        public void handleMessage(Message msg) {
            final Character_XingGePu_FindFragment_Update act = activity.get();
            if (null == act) {
                return;
            }
            switch (msg.what) {
                case DATA_LOADING:
                    act.pd = UtilsTool.initProgressDialog(act.mContext, "请稍后...");
                    act.pd.show();
                    break;
                case UPDATE_UI:
                    if (act.pd != null && act.pd.isShowing()) {
                        act.pd.dismiss();
                    }

                    if (act.mCharacterPuList != null) {
//                        for (int i = 0; i < act.mCharacterPuList.size(); i++) {
//                            switch (act.mCharacterPuList.get(i).getFunctionLevel()) {
////                                case 7:
////                                    act.rt_part4_0_0.setText(act.mCharacterPuList.get(i).getFunctionModule());
////                                    act.rt_part4_0_1.setText(act.mCharacterPuList.get(i).getCode());
////                                    break;
//                            }
//                        }
                        act.mXingGePuAdapter.setData2(act.mCharacterPuList);
                    }
                    break;
                case POP_DIALOG:
                    if (act.pd != null && act.pd.isShowing()) {
                        act.pd.dismiss();
                    }
//                    act.showCharacterInfoDialog();
                    if (act.mCharacterPuPopList != null && act.mCharacterPuPopList.size() > 0) {
                        act.showCharacterInfoDialog();
                    } else {
                        MyToast.show(act.mContext, "暂无该性格谱信息");
                    }
                    break;
            }
        }
    }

    @Override
    public void loadDataFailed(final String response, int flag) {
        LogUtils.i(TAG, response + ";");
    }


    /**
     * 性格谱弹出窗
     */
    int currentItem = 0;
//    ImageView[] imgs;

    private void showCharacterInfoDialog() {
        currentItem = 0;
        View view = mContext.getLayoutInflater().inflate(R.layout.dialog_character_info, null);
        final Dialog dialog = new Dialog(mContext, com.hyphenate.easeui.R.style.Dialog_Fullscreen);
        dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        Window window = dialog.getWindow();

        final int popSize = mCharacterPuPopList.size();
        final ViewPager viewPager = (ViewPager) view.findViewById(R.id.main_vp_container);
        final TextView ib_character_qiehuan_info = (TextView) view.findViewById(R.id.ib_character_qiehuan_info);

        ArrayList<View> views = new ArrayList<View>();
        LayoutInflater inflater = mContext.getLayoutInflater();
        ib_character_qiehuan_info.setText(1+"/"+popSize);
        for (int i = 0; i < popSize; i++) {
            View view1 = inflater.inflate(R.layout.item_xinggepu, null);
            SimpleDraweeView character_label_pic = (SimpleDraweeView) view1.findViewById(R.id.character_label_pic0);
            TextView tv_character_part1 = (TextView) view1.findViewById(R.id.tv_character_part0);
            TextView tv_character_pop_title = (TextView) view1.findViewById(R.id.tv_character_pop_title);
            tv_character_pop_title.setText(mCharacterPuPopList.get(i).getTitle());
            tv_character_part1.setText(mCharacterPuPopList.get(i).getText());

            if (mCharacterPuPopList.get(i).getPhotoUrl() != null) {
                character_label_pic.setImageURI(Uri.parse(mCharacterPuPopList.get(i).getPhotoUrl()));
            }
            views.add(view1);
        }
        viewPager.setAdapter(new MyViewPagerAdapter(views));
        viewPager.setCurrentItem(0);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
                    currentItem=position;

                        ib_character_qiehuan_info.setText(position+1+"/"+popSize);
//                    }
//                });
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });


//        view.findViewById(R.id.tv_sanguan_close).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//                dialog.dismiss();
//            }
//        });
        view.findViewById(R.id.tv_return_pop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.ib_character_qiehuan_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentItem>0){
                    currentItem--;
                    viewPager.setCurrentItem(currentItem);
                }
            }
        });
        view.findViewById(R.id.ib_character_qiehuan_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentItem<popSize-1){
                    currentItem ++;
                     viewPager.setCurrentItem(currentItem);
                }
            }
        });
//      分享到朋友圈
        view.findViewById(R.id.iv_pop_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        wechatShare(SendMessageToWX.Req.WXSceneTimeline, mCharacterPuPopList.get(currentItem).getId(),
                                mCharacterPuPopList.get(currentItem).getPhotoUrl(), mCharacterPuPopList.get(currentItem).getTitle(),"1");
                    }
                }).start();
            }
        });
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
        if(!((Activity) mContext).isFinishing()) {
            dialog.show();
        }
    }




    /**
     * 下面的内容为弹出分享到朋友圈
     */

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
    private IWXAPI wxApi;
    private void wechatShare(final int flag, final String id, String photoUrl, final String title, final String type) {
        wxApi = WXAPIFactory.createWXAPI(mContext, WowuApp.WeChat_APP_ID, false);
        wxApi.registerApp(WowuApp.WeChat_APP_ID);
        if (!wxApi.isWXAppSupportAPI()) {
            Message msg = new Message();
            msg.what = LoadingError;
            mHandler.sendMessage(msg);
            return;
        }
        UtilsTool temp=new UtilsTool();
        temp.wechatShare(mContext,wxApi,  flag, id,  photoUrl,title,type);
    }


}

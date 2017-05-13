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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
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
import com.wuwo.im.adapter.MyViewPagerAdapter;
import com.wuwo.im.bean.CharacterPopInfo;
import com.wuwo.im.bean.CharacterPu;
import com.wuwo.im.config.WowuApp;
import com.wuwo.im.util.LogUtils;
import com.wuwo.im.util.MyToast;
import com.wuwo.im.util.UtilsTool;
import com.zhy.http.okhttp.service.LoadserverdataService;
import com.zhy.http.okhttp.service.loadServerDataListener;

import org.json.JSONException;

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
public class Character_XingGePu_FindFragment extends BaseAppFragment implements View.OnClickListener, loadServerDataListener {
    private Activity mContext;
    private SharedPreferences.Editor editor;
    //    private int mCount = 0;
    private static final String TAG = "Character_XingGePu_FindFragment";
    private LoadserverdataService loadDataService;
    private SimpleDraweeView find_share_f_ic7;
    private mHandlerWeak mtotalHandler;
    private SharedPreferences mSettings;
    private TextView tv_character_type_name, rt_part1_1_0, rt_part1_1_1, rt_part1_0_0, rt_part1_0_1, rt_part1_0_2;
    private TextView rt_part2_1_0, rt_part2_1_1, rt_part2_0_0, rt_part2_0_1, rt_part2_0_2;
    private TextView rt_part3_1_0, rt_part3_1_1, rt_part3_0_0, rt_part3_0_1, rt_part3_0_2;
    private TextView rt_part4_1_0, rt_part4_1_1, rt_part4_0_0, rt_part4_0_1, rt_part4_0_2;
    RelativeLayout rt_part1, rt_part1_1, rt_part2, rt_part2_1, rt_part3, rt_part3_1, rt_part4, rt_part4_1;


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
        View view = inflater.inflate(R.layout.fragement_character_xinggepu, container, false);
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
    public void loadDataByHistory(String historyId) {
//        loadPopInfo(WowuApp.GetManualInfoURL+" ?historyId="+historyId, LOAD_DATA);
        loadDataService.loadGetJsonRequestData(WowuApp.GetManualInfoURL+" ?historyId="+historyId, LOAD_DATA);//后面需要改过来
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        initViews(view);
    }

    public static final int LOAD_RECOMMEND_DATA = 1;

    private void initViews(View view) {
        mSettings = mContext.getSharedPreferences(WowuApp.PREFERENCE_KEY, android.content.Context.MODE_PRIVATE);
//        tv_character_type_name = (TextView) view.findViewById(R.id.tv_character_type_name);

        rt_part1 = (RelativeLayout) view.findViewById(R.id.rt_part1);
        rt_part1_1 = (RelativeLayout) view.findViewById(R.id.rt_part1_1);
        rt_part1_1_0 = (TextView) view.findViewById(R.id.rt_part1_1_0);
        rt_part1_1_1 = (TextView) view.findViewById(R.id.rt_part1_1_1);
        rt_part1_0_0 = (TextView) view.findViewById(R.id.rt_part1_0_0);
        rt_part1_0_1 = (TextView) view.findViewById(R.id.rt_part1_0_1);
        rt_part1_0_2 = (TextView) view.findViewById(R.id.rt_part1_0_2);
        rt_part1.setOnClickListener(this);
        rt_part1_1.setOnClickListener(this);

        rt_part2 = (RelativeLayout) view.findViewById(R.id.rt_part2);
        rt_part2_1 = (RelativeLayout) view.findViewById(R.id.rt_part2_1);
        rt_part2_1_0 = (TextView) view.findViewById(R.id.rt_part2_1_0);
        rt_part2_1_1 = (TextView) view.findViewById(R.id.rt_part2_1_1);
        rt_part2_0_0 = (TextView) view.findViewById(R.id.rt_part2_0_0);
        rt_part2_0_1 = (TextView) view.findViewById(R.id.rt_part2_0_1);
        rt_part2_0_2 = (TextView) view.findViewById(R.id.rt_part2_0_2);
        rt_part2.setOnClickListener(this);
        rt_part2_1.setOnClickListener(this);

        rt_part3 = (RelativeLayout) view.findViewById(R.id.rt_part3);
        rt_part3_1 = (RelativeLayout) view.findViewById(R.id.rt_part3_1);
        rt_part3_1_0 = (TextView) view.findViewById(R.id.rt_part3_1_0);
        rt_part3_1_1 = (TextView) view.findViewById(R.id.rt_part3_1_1);
        rt_part3_0_0 = (TextView) view.findViewById(R.id.rt_part3_0_0);
        rt_part3_0_1 = (TextView) view.findViewById(R.id.rt_part3_0_1);
        rt_part3_0_2 = (TextView) view.findViewById(R.id.rt_part3_0_2);
        rt_part3.setOnClickListener(this);
        rt_part3_1.setOnClickListener(this);

        rt_part4 = (RelativeLayout) view.findViewById(R.id.rt_part4);
        rt_part4_1 = (RelativeLayout) view.findViewById(R.id.rt_part4_1);
        rt_part4_1_0 = (TextView) view.findViewById(R.id.rt_part4_1_0);
        rt_part4_1_1 = (TextView) view.findViewById(R.id.rt_part4_1_1);
        rt_part4_0_0 = (TextView) view.findViewById(R.id.rt_part4_0_0);
        rt_part4_0_1 = (TextView) view.findViewById(R.id.rt_part4_0_1);
        rt_part4_0_2 = (TextView) view.findViewById(R.id.rt_part4_0_2);
        rt_part4.setOnClickListener(this);
        rt_part4_1.setOnClickListener(this);

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





/*//              此种是跳到一个新的activity，但是这种动画效果不好
                if (response != null && !response.equals("[]")) {
                    Gson gson = new GsonBuilder().create();
                    java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<List<CharacterPopInfo>>() {
                    }.getType();
                    mCharacterPuPopList = gson.fromJson(response, type);
                    if (mCharacterPuPopList != null && mCharacterPuPopList.size() >0) {
                        Intent intent = new Intent(getActivity(), CharacterPuPopActivity.class);
                        intent.putExtra("response", response);
                        startActivity(intent);
                        getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    } else {
                        Message msg = new Message();
                        msg.what = POP_DIALOG;
                        mtotalHandler.sendMessage(msg);
                    }

                } else {
                Message msg = new Message();
                msg.what = POP_DIALOG;
                mtotalHandler.sendMessage(msg);
                }*/

                break;
        }

    }


    private ArrayList<CharacterPu> mCharacterPuList = new ArrayList<CharacterPu>(); //记录所有的最新消息
    private ArrayList<CharacterPopInfo> mCharacterPuPopList = new ArrayList<CharacterPopInfo>(); //记录所有的最新消息

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

    private static class mHandlerWeak extends Handler {
        private WeakReference<Character_XingGePu_FindFragment> activity = null;

        public mHandlerWeak(Character_XingGePu_FindFragment act) {
            super();
            this.activity = new WeakReference<Character_XingGePu_FindFragment>(act);
        }

        @Override
        public void handleMessage(Message msg) {
            final Character_XingGePu_FindFragment act = activity.get();
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
                        for (int i = 0; i < act.mCharacterPuList.size(); i++) {
                            switch (act.mCharacterPuList.get(i).getFunctionLevel()) {
                                case 1:
                                    act.rt_part1_1_1.setText(act.mCharacterPuList.get(i).getFunctionModule() + "\n" + act.mCharacterPuList.get(i).getCode());
                                    break;
                                case 5:
                                    act.rt_part1_0_0.setText(act.mCharacterPuList.get(i).getFunctionModule());
                                    act.rt_part1_0_1.setText(act.mCharacterPuList.get(i).getCode());
                                    break;
                                case 2:
                                    act.rt_part2_1_1.setText(act.mCharacterPuList.get(i).getFunctionModule() + "\n" + act.mCharacterPuList.get(i).getCode());
                                    break;
                                case 6:
                                    act.rt_part2_0_0.setText(act.mCharacterPuList.get(i).getFunctionModule());
                                    act.rt_part2_0_1.setText(act.mCharacterPuList.get(i).getCode());
                                    break;
                                case 4:
                                    act.rt_part3_1_1.setText(act.mCharacterPuList.get(i).getCode() + "\n" + act.mCharacterPuList.get(i).getFunctionModule());
                                    break;
                                case 8:
                                    act.rt_part3_0_0.setText(act.mCharacterPuList.get(i).getFunctionModule());
                                    act.rt_part3_0_1.setText(act.mCharacterPuList.get(i).getCode());
                                    break;
                                case 3:
                                    act.rt_part4_1_1.setText(act.mCharacterPuList.get(i).getCode() + "\n" + act.mCharacterPuList.get(i).getFunctionModule());
                                    break;
                                case 7:
                                    act.rt_part4_0_0.setText(act.mCharacterPuList.get(i).getFunctionModule());
                                    act.rt_part4_0_1.setText(act.mCharacterPuList.get(i).getCode());
                                    break;
                            }
                        }
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
        dialog.setContentView(view, new ViewGroup.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
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
            if (mCharacterPuPopList.get(i).getPhotoUrl() != null) {
                character_label_pic.setImageURI(Uri.parse(mCharacterPuPopList.get(i).getPhotoUrl()));
                tv_character_part1.setText(mCharacterPuPopList.get(i).getText());
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
                                mCharacterPuPopList.get(currentItem).getPhotoUrl(), mCharacterPuPopList.get(currentItem).getTitle(),"2");
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
/*    private void showCharacterInfoDialog() {
        currentItem = 0;
        View view = mContext.getLayoutInflater().inflate(R.layout.dialog_character_info, null);
        final Dialog dialog = new Dialog(mContext, com.hyphenate.easeui.R.style.Dialog_Fullscreen);
        dialog.setContentView(view, new ViewGroup.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        Window window = dialog.getWindow();
        MyScrollLayout mScrollLayout = (MyScrollLayout) view.findViewById(R.id.ScrollLayout);
        final int count = mScrollLayout.getChildCount();

        int pics[] = {R.id.character_label_pic0, R.id.character_label_pic1, R.id.character_label_pic2, R.id.character_label_pic3, R.id.character_label_pic4};
        int texts[] = {R.id.tv_character_part0, R.id.tv_character_part1, R.id.tv_character_part2, R.id.tv_character_part3, R.id.tv_character_part4};

        final int popSize = (mCharacterPuPopList.size() > count ? count : mCharacterPuPopList.size());

        mScrollLayout.setScreenSize(popSize);

        for (int i = 0; i < popSize; i++) {
            mScrollLayout.getChildAt(i).setVisibility(View.VISIBLE);

            SimpleDraweeView character_label_pic = (SimpleDraweeView) view.findViewById(pics[i]);
            if (mCharacterPuPopList.get(i).getPhotoUrl() != null)
                character_label_pic.setImageURI(Uri.parse(mCharacterPuPopList.get(i).getPhotoUrl()));

            TextView tv_character_part1 = (TextView) view.findViewById(texts[i]);
            tv_character_part1.setText(mCharacterPuPopList.get(i).getText());
        }


        view.findViewById(R.id.tv_sanguan_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.tv_return_pop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });
//        ViewPagerAdapter vpAdapter = new ViewPagerAdapter(mContext.getSupportFragmentManager(), this);
//        vpAdapter.addFrag(new Portal_FindFragment(), "");


        LinearLayout pointLLayout = (LinearLayout) view.findViewById(R.id.llayout);
        imgs = new ImageView[popSize];
        for (int i = 0; i < popSize; i++) {
            imgs[i] = (ImageView) pointLLayout.getChildAt(i);
            imgs[i].setVisibility(View.VISIBLE);
            imgs[i].setEnabled(true);
            imgs[i].setTag(i);
        }
        currentItem = 0;
        imgs[currentItem].setEnabled(false);


        mScrollLayout.SetOnViewChangeListener(new MyScrollLayout.OnViewChangeListener() {
            @Override
            public void OnViewChange(int position) {
                if (position < 0 || position > popSize - 1 || currentItem == position) {
                    return;
                }
                imgs[currentItem].setEnabled(true);
                imgs[position].setEnabled(false);
                currentItem = position;
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
        dialog.show();
    }*/




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

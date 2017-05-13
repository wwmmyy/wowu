package com.wuwo.im.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.wuwo.im.adapter.MyViewPagerAdapter;
import com.wuwo.im.bean.CharacterPopInfo;
import com.wuwo.im.bean.Characters;
import com.wuwo.im.config.WowuApp;
import com.wuwo.im.fragement.CharacterQingXiangDuFragment;
import com.wuwo.im.fragement.Character_XingGePu_FindFragment_Update;
import com.wuwo.im.util.LogUtils;
import com.wuwo.im.util.MyToast;
import com.wuwo.im.util.UtilsTool;
import com.zhy.http.okhttp.service.LoadserverdataService;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import im.imxianzhi.com.imxianzhi.R;

public class CharacterDetailActivity extends BaseLoadActivity {
    private final String TAG = "CharacterDetailActivity";
    private RelativeLayout head_layout;
    //    private TabLayout toolbar_tab;
    private ViewPager main_vp_container;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private CoordinatorLayout root_layout;
    private ViewPagerAdapter vpAdapter;
    private AppBarLayout app_bar_layout;
    private SimpleDraweeView news_head_pic, portal_news_img;
    private LinearLayout ln_character_name;
    private TextView tv_character_real_name, tv_character_name, tv_character_type;
    private mHandlerWeak mtotalHandler;
    private ProgressDialog pg;
    //创建一个handler，内部完成处理消息方法
    public Characters mCharacter = null;
    //    Character_XingGePu_FindFragment xingGePu;
    Character_XingGePu_FindFragment_Update xingGePu;
    CharacterQingXiangDuFragment qingXiang;
    //用于分享到朋友圈时url拼接
    private String currentType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_detail_layout);
        loadDataService = new LoadserverdataService(this);
        initView();

        mtotalHandler = new mHandlerWeak(this);
//        pg = UtilsTool.initProgressDialog(mContext, "正在连接.....");
//        pg.show();
        loadData();
    }

    private ArrayList<CharacterPopInfo> mCharacterPuPopList = new ArrayList<CharacterPopInfo>(); //记录所有的最新消息

    public static final int LOAD_DATA = 5;
    public static final int POP_DIALOG = 7;
    private static final int RETURN_BACK = 111;
    private static final int RETURN_BACK_HISTORY = 112;
    public static final int PG_CLOSE = 13;

    private TextView tv_user_info, tv_character_qiangdu, tv_character_xinggepu;


    /**
     * 加载数据信息,获取性格谱信息
     */
    private void loadData() {
        loadDataService.loadGetJsonRequestData(WowuApp.GetDispositionInfoURL, LOAD_DATA);
    }

    private void initView() {

        app_bar_layout = (AppBarLayout) findViewById(R.id.app_bar_layout);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        head_layout = (RelativeLayout) findViewById(R.id.head_layout);
        root_layout = (CoordinatorLayout) findViewById(R.id.root_layout);

        portal_news_img = (SimpleDraweeView) findViewById(R.id.news_label_pic);
//        portal_news_img.setImageURI(Uri.parse("http://upload.m4.cn/2016/0416/1460786735779.jpg"));
        news_head_pic = (SimpleDraweeView) findViewById(R.id.news_head_pic);
        if (WowuApp.iconPath != null && !WowuApp.iconPath.equals("")) {
            news_head_pic.setImageURI(Uri.parse(WowuApp.iconPath));
        }

        ln_character_name = (LinearLayout) findViewById(R.id.ln_character_name);
        tv_character_real_name = (TextView) findViewById(R.id.tv_character_real_name);
        tv_character_type = (TextView) findViewById(R.id.tv_character_type);
        tv_character_name = (TextView) findViewById(R.id.tv_character_name);
        tv_character_real_name.setText(WowuApp.Name);


        findViewById(R.id.tv_mycharacter_more_xinggp).setOnClickListener(this);
        findViewById(R.id.tv_mycharacter_moreronggr).setOnClickListener(this);
        findViewById(R.id.tv_mycharacter_ronggr).setOnClickListener(this);


        //使用CollapsingToolbarLayout必须把title设置到CollapsingToolbarLayout上，设置到Toolbar上则不会显示
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id
                .collapsing_toolbar_layout);
        app_bar_layout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset <= -head_layout.getHeight() / 2) {
                    mCollapsingToolbarLayout.setTitle("");
                    head_layout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    news_head_pic.setVisibility(View.GONE);
                    ln_character_name.setVisibility(View.GONE);
                    tv_character_real_name.setVisibility(View.GONE);
                } else {
                    mCollapsingToolbarLayout.setTitle(" ");
                    head_layout.setBackgroundColor(getResources().getColor(R.color.white));
                    news_head_pic.setVisibility(View.VISIBLE);
                    ln_character_name.setVisibility(View.VISIBLE);
                    tv_character_real_name.setVisibility(View.VISIBLE);
                }
            }
        });
//        toolbar_tab = (TabLayout) findViewById(R.id.toolbar_tab);
        main_vp_container = (ViewPager) findViewById(R.id.main_vp_container);

        vpAdapter = new ViewPagerAdapter(getSupportFragmentManager(), this);
        setupViewPager();
        main_vp_container.setAdapter(vpAdapter);
//        main_vp_container.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener
//                (toolbar_tab));
//        toolbar_tab.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener
//                (main_vp_container));

//        StatusBarUtil.setTranslucent(CharacterDetailActivity.this, StatusBarUtil.DEFAULT_STATUS_BAR_ALPHA);
        //tablayout和viewpager建立联系为什么不用下面这个方法呢？自己去研究一下，可能收获更多
//        toolbar_tab.setupWithViewPager(main_vp_container);
//        loadBlurAndSetStatusBar();

//        ImageView head_iv = (ImageView) findViewById(R.id.head_iv);
//        Glide.with(this).load(R.drawable.logo0).bitmapTransform(new RoundedCornersTransformation(this,                90, 0)).into(head_iv);

//        root_layout.setBackgroundResource(R.drawable.start);

        findViewById(R.id.tv_character_typeintro).setOnClickListener(this);
        tv_user_info = (TextView) findViewById(R.id.tv_user_info);
        tv_user_info.setOnClickListener(this);
        tv_character_qiangdu = (TextView) findViewById(R.id.tv_character_qiangdu);
        tv_character_qiangdu.setOnClickListener(this);
        tv_character_xinggepu = (TextView) findViewById(R.id.tv_character_xinggepu);
        tv_character_xinggepu.setOnClickListener(this);

        main_vp_container.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    tv_character_qiangdu.setTextColor(getResources().getColor(R.color.best_luck_yellow));
                    tv_character_xinggepu.setTextColor(getResources().getColor(R.color.text_dark_grey));
                } else {
                    tv_character_qiangdu.setTextColor(getResources().getColor(R.color.text_dark_grey));
                    tv_character_xinggepu.setTextColor(getResources().getColor(R.color.best_luck_yellow));
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO 自动生成的方法存根
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO 自动生成的方法存根
            }
        });


    }

    /**
     * 设置毛玻璃效果和沉浸状态栏
     */
    private void loadBlurAndSetStatusBar() {
//        StatusBarUtil.setTranslucent(CharacterDetailActivity.this, StatusBarUtil.DEFAULT_STATUS_BAR_ALPHA);
        Glide.with(this).load(Uri.parse("http://f.hiphotos.baidu.com/image/pic/item/b151f8198618367a9f738e022a738bd4b21ce573.jpg"))
//                .bitmapTransform(new BlurTransformation(this, 100))
                .into(new SimpleTarget<GlideDrawable>() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super
                            GlideDrawable> glideAnimation) {
//                head_layout.setBackground(resource);
                        root_layout.setBackground(resource);
//                        app_bar_layout.setBackground(resource);
                    }
                });

//        Glide.with(this).load(R.drawable.logo0).bitmapTransform(new BlurTransformation(this, 100))
//                .into(new SimpleTarget<GlideDrawable>() {
//            @Override
//            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super
//                    GlideDrawable> glideAnimation) {
//                mCollapsingToolbarLayout.setContentScrim(resource);
//            }
//        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_loading, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//            String msg = "";
//            switch (item.getItemId()) {
//                case R.id.webview:
//                    msg += "博客跳转";
//                    break;
//                case R.id.weibo:
//                    msg += "微博跳转";
//                    break;
//                case R.id.action_settings:
//                    msg += "设置";
//                    break;
//            }
//            if (!msg.equals("")) {
//        Toast.makeText(CharacterDetailActivity.this, "微博跳转", Toast.LENGTH_SHORT).show();
//        showCharacterRetestDialog();
        showCharacteShareDialog();
//            }
        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager() {
        String buttonName[] = {"类型描述", "性格强度", "性格谱"};//,"总倾向度","性格报告"
//        xingGePu = new Character_XingGePu_FindFragment();

        xingGePu = new Character_XingGePu_FindFragment_Update();
        qingXiang = new CharacterQingXiangDuFragment();
//        vpAdapter.addFrag(xingGePu, buttonName[0]);//
        vpAdapter.addFrag(qingXiang, buttonName[1]);
        vpAdapter.addFrag(xingGePu, buttonName[2]);//
    }

    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    @Override
    public void onClick(View v) {
        switch (v.getId()) {


            case R.id.tv_character_qiangdu:
                main_vp_container.setCurrentItem(0);
                tv_character_qiangdu.setTextColor(getResources().getColor(R.color.best_luck_yellow));
                tv_character_xinggepu.setTextColor(getResources().getColor(R.color.text_dark_grey));
                break;
            case R.id.tv_character_xinggepu:
                main_vp_container.setCurrentItem(1);
                tv_character_qiangdu.setTextColor(getResources().getColor(R.color.text_dark_grey));
                tv_character_xinggepu.setTextColor(getResources().getColor(R.color.best_luck_yellow));
                break;
            case R.id.tv_user_info:
                currentType = "2";
                if (mCharacter != null) {
                    String dispositionId = null;
                    if (mCharacter.getDispositionId() != null) {
                        dispositionId = mCharacter.getDispositionId();
                    } else if (mCharacter.getScore() != null) {
                        if (mCharacter.getScore().getDispositionId() != null) {
                            dispositionId = mCharacter.getScore().getDispositionId();
                        }
                    }
                    if (dispositionId != null) {
                        loadDataInfo(WowuApp.GetDispositionNicknamesURL + "?id=" + dispositionId, 1000);
                    } else {
                        MyToast.show(mContext, "抱歉，服务器端数据异常");
                    }

                } else {
                    MyToast.show(mContext, "数据信息异常请稍后重试");
                    loadData();
                }
                break;
            case R.id.tv_character_typeintro:
                currentType = "2";
                if (mCharacter != null) {
                    String dispositionId = null;
                    if (mCharacter.getDispositionId() != null) {
                        dispositionId = mCharacter.getDispositionId();
                    } else if (mCharacter.getScore() != null) {
                        if (mCharacter.getScore().getDispositionId() != null) {
                            dispositionId = mCharacter.getScore().getDispositionId();
                        }
                    }
                    if (dispositionId != null) {
                        loadDataInfo(WowuApp.GetDispositionNicknamesURL + "?id=" + dispositionId, 1000);
                    } else {
                        MyToast.show(mContext, "抱歉，服务器端数据异常");
                    }
                } else {
                    MyToast.show(mContext, "数据信息异常请稍后重试");
                    loadData();
                }
                break;
            case R.id.tv_mycharacter_more_xinggp:
                currentType = "7";
                loadDataInfo(WowuApp.GetManualDescDialog, R.id.tv_mycharacter_moreronggr);
                break;
            case R.id.tv_mycharacter_moreronggr:
                currentType = "4";
                loadDataInfo(WowuApp.GetMbtiInfoURL, R.id.tv_mycharacter_moreronggr);
                break;
            case R.id.tv_mycharacter_ronggr:
                currentType = "5";
                loadDataInfo(WowuApp.GetRgInfoURL, R.id.tv_mycharacter_moreronggr);
                break;
        }
    }

    private void loadDataInfo(String getMbtiInfoURL, int tv_mycharacter_moreronggr) {
        pg = UtilsTool.initProgressDialog(mContext, "正在连接.....");
        pg.show();
        loadDataService.loadGetJsonRequestData(getMbtiInfoURL, tv_mycharacter_moreronggr);
    }

    Gson gson2 = new GsonBuilder().create();

    @Override
    public void loadServerData(String response, int flag) {

        LogUtils.i(TAG, response + ";");

        switch (flag) {
            case LOAD_DATA:
                Gson gson = new GsonBuilder().create();
                java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<Characters>() {
                }.getType();
                try {
                    mCharacter = gson.fromJson(response, type);
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtils.i(TAG, "fail:" + response + ";");
                }

                if (qingXiang != null) {
                    qingXiang.setQingXiangDuInfo(mCharacter);
                }

                Message msg = new Message();
                msg.what = LOAD_DATA;
                msg.obj = response;
                mtotalHandler.sendMessage(msg);
                break;
            case R.id.tv_mycharacter_moreronggr:
//                Message msg2 = new Message();
//                msg2.what = END;
//                mtotalHandler.sendMessage(msg2);
//
//                Intent intent = new Intent(mContext, CharacterPuPopActivity.class);
//                intent.putExtra("response", response);
//                startActivity(intent);
//                mContext.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);


                if (response != null) {
                    java.lang.reflect.Type type2 = new com.google.gson.reflect.TypeToken<List<CharacterPopInfo>>() {
                    }.getType();
                    mCharacterPuPopList = gson2.fromJson(response, type2);
                    Message msg3 = new Message();
                    msg3.what = POP_DIALOG;
                    mtotalHandler.sendMessage(msg3);
                }
                break;
            case 1000:
                if (response != null) {
                    java.lang.reflect.Type type2 = new com.google.gson.reflect.TypeToken<List<CharacterPopInfo>>() {
                    }.getType();
                    ArrayList<CharacterPopInfo> tempCharacterPuPopList = gson2.fromJson(response, type2);
                    if (tempCharacterPuPopList != null && tempCharacterPuPopList.size() > 0) {
                        mCharacterPuPopList.clear();
                        for (int i = 0; i < tempCharacterPuPopList.size(); i++) {
                            CharacterPopInfo temp = tempCharacterPuPopList.get(i);
                            temp.setPhotoUrl(temp.getHeroPhotoUrl());
                            temp.setTitle(temp.getNickName());
                            temp.setContent(temp.getDetail());
                            mCharacterPuPopList.add(temp);
                        }
                    }
                    Message msg3 = new Message();
                    msg3.what = POP_DIALOG;
                    mtotalHandler.sendMessage(msg3);
                }
                break;


        }
    }


    @Override
    public void loadDataFailed(String response, int flag) {
        LogUtils.i(TAG, response + " ;");
        Message msg = new Message();
        msg.what = PG_CLOSE;
        msg.obj = response;
        mtotalHandler.sendMessage(msg);


    }


    private static class mHandlerWeak extends Handler {
        private WeakReference<CharacterDetailActivity> activity = null;

        public mHandlerWeak(CharacterDetailActivity act) {
            super();
            this.activity = new WeakReference<CharacterDetailActivity>(act);
        }

        @Override
        public void handleMessage(Message msg) {
            CharacterDetailActivity act = activity.get();
            if (null == act) {
                return;
            }
            switch (msg.what) {
                // 正在下载
                case LOAD_DATA:
                    if (act.mCharacter != null) {
                        if (act.mCharacter.getIconUrl() != null) {
                            act.portal_news_img.setImageURI(Uri.parse(act.mCharacter.getIconUrl()));
                        }
                        act.tv_character_name.setText(act.mCharacter.getCelebrity());
                        act.tv_user_info.setText(act.mCharacter.getName() + act.mCharacter.getTitle());
                    }
//                    act.xingGePu.setTitle(act.mCharacter.getName()+act.mCharacter.getTitle());
                    if (act.pg != null && act.pg.isShowing()) {
                        act.pg.dismiss();
                    }
                    break;
                case PG_CLOSE:
                    if (act.pg != null && act.pg.isShowing()) {
                        act.pg.dismiss();
                    }
                    break;
                case POP_DIALOG:
                    if (act != null) {
                        if (act.pg != null && act.pg.isShowing()) {
                            act.pg.dismiss();
                        }
//                    act.showCharacterInfoDialog();
                        if (act.mCharacterPuPopList != null && act.mCharacterPuPopList.size() > 0) {
                            act.showCharacterInfoDialog();
                        } else {
                            MyToast.show(act.mContext, "暂无该性格谱信息");
                        }
                    }
                    break;


            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && data.getExtras() != null && resultCode == RESULT_OK) {
            switch (requestCode) {
                case RETURN_BACK:
//                    loadData();
//                    if (qingXiang != null) qingXiang.loadData();
//                    if (xingGePu != null) xingGePu.loadData();
//                    break;
                case RETURN_BACK_HISTORY:
                    String historyId = data.getExtras().getString("historyId");//得到新Activity关闭后返回的数据  x
                    String testType = data.getExtras().getString("testType");
                    LogUtils.i(TAG, historyId + "::::" + testType);

//                    loadData();
                    if (historyId == null) {
                        break;
                    }
                    loadDataService.loadGetJsonRequestData(WowuApp.GetDispositionInfoFromHistoryURL + "?historyId=" + historyId, LOAD_DATA);
                    if (qingXiang != null) qingXiang.loadDataByHitory(historyId, testType);
                    if (xingGePu != null) xingGePu.loadDataByHistory(historyId);
                    break;

            }
        }
    }


    public class ViewPagerAdapter extends FragmentPagerAdapter {

        final int PAGE_COUNT = 5;
        //        private String tabTitles[] = new String[]{"", "分享"};//, "收藏", "关注"
        private Context context;

        public ViewPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }

        @Override
        public Fragment getItem(int position) {
//            return PageFragment.newInstance(position + 1);
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
//            return PAGE_COUNT;
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
//            return tabTitles[position];
            return mFragmentTitleList.get(position);
        }
    }


//    public class PageFragment extends Fragment {
//        public static final String ARG_PAGE = "ARG_PAGE";
//        private int mPage;
//        private RecyclerView lv;
//
//        public static PageFragment newInstance(int page) {
//            Bundle args = new Bundle();
//            args.putInt(ARG_PAGE, page);
//            PageFragment pageFragment = new PageFragment();
//            pageFragment.setArguments(args);
//            return pageFragment;
//        }
//
//        @Override
//        public void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//            mPage = getArguments().getInt(ARG_PAGE);
//        }
//
//        @Override
//        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
//                savedInstanceState) {
//            View view = inflater.inflate(R.layout.fragment_page, null);
//            lv = (RecyclerView) view.findViewById(R.id.lv);
//            // 创建一个线性布局管理器
//
//            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
//
//            // 设置布局管理器
//
//            lv.setLayoutManager(layoutManager);
//
//            List<String> list = new ArrayList<String>();
//            for (int i = 0; i < 100; i++) {
//                list.add(i + "");
//            }
//            lv.setAdapter(new MyAdapter(list));
//            return view;
//        }
//    }


    private void showCharacterRetestDialog() {
        View view = getLayoutInflater().inflate(R.layout.pop_character_test, null);
        final Dialog dialog = new Dialog(mContext, R.style.transparentFrameWindowStyle);
        dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();

        view.findViewById(R.id.home_member_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });

        view.findViewById(R.id.rt_charactertest_retest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.rt_charactertest_set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });

        // 设置显示动画
        window.setWindowAnimations(R.style.main_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = getWindowManager().getDefaultDisplay().getHeight();
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.MATCH_PARENT;//WRAP_CONTENT

        // 设置显示位置
        dialog.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        dialog.setCanceledOnTouchOutside(true);
        if (!((Activity) mContext).isFinishing()) {
            dialog.show();
        }
    }


    private void showCharacteShareDialog() {
        View view = getLayoutInflater().inflate(R.layout.pop_character_share, null);
        final Dialog dialog = new Dialog(mContext, R.style.transparentFrameWindowStyle);//
        dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();

        view.findViewById(R.id.home_member_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });

        view.findViewById(R.id.rt_character_history).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
//                Intent intent = new Intent(mContext, CharacterTestHistoryActivity.class);
                Intent intent = new Intent(mContext, CharacterTestHistoryNewActivity.class);
                startActivityForResult(intent, RETURN_BACK_HISTORY);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);


            }
        });
        view.findViewById(R.id.rt_character_retest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
                Intent intent2 = new Intent(mContext, CharacterTestActivity.class);
                intent2.putExtra("tetsType", 2);
                intent2.putExtra("registerMode", false);
                startActivityForResult(intent2, RETURN_BACK);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        view.findViewById(R.id.rt_character_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
                Intent intent = new Intent(mContext, ShareCharacterReportActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            }
        });

        // 设置显示动画
        window.setWindowAnimations(R.style.main_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
//        wl.x = 0;
//        wl.y = getWindowManager().getDefaultDisplay().getHeight() / 2;
        wl.x = 0;
        wl.y = getWindowManager().getDefaultDisplay().getHeight();
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.MATCH_PARENT;//WRAP_CONTENT
        wl.gravity = Gravity.BOTTOM;

//        window.setAttributes(wl);
        window.setGravity(Gravity.BOTTOM);

        // 设置显示位置
        dialog.onWindowAttributesChanged(wl);

        // 设置点击外围解散
        dialog.setCanceledOnTouchOutside(true);
        if (!((Activity) mContext).isFinishing()) {
            dialog.show();
        }
    }


    /**
     * 性格谱弹出窗
     */
    int currentItem = 0;
//    ImageView[] imgs;

    private void showCharacterInfoDialog() {
        currentItem = 0;
        View view = getLayoutInflater().inflate(R.layout.dialog_character_info, null);
        final Dialog dialog = new Dialog(mContext, com.hyphenate.easeui.R.style.Dialog_Fullscreen);
        dialog.setContentView(view, new ViewGroup.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        Window window = dialog.getWindow();

        final int popSize = mCharacterPuPopList.size();
        final ViewPager viewPager = (ViewPager) view.findViewById(R.id.main_vp_container);
        final TextView ib_character_qiehuan_info = (TextView) view.findViewById(R.id.ib_character_qiehuan_info);
        ib_character_qiehuan_info.setText(1 + "/" + popSize);
        ArrayList<View> views = new ArrayList<View>();
        LayoutInflater inflater = getLayoutInflater();

        for (int i = 0; i < popSize; i++) {
            View view1 = inflater.inflate(R.layout.item_xinggepu, null);
            SimpleDraweeView character_label_pic = (SimpleDraweeView) view1.findViewById(R.id.character_label_pic0);
            TextView tv_character_part1 = (TextView) view1.findViewById(R.id.tv_character_part0);
            TextView tv_character_pop_title = (TextView) view1.findViewById(R.id.tv_character_pop_title);
            tv_character_pop_title.setText(mCharacterPuPopList.get(i).getTitle());
            tv_character_part1.setText(mCharacterPuPopList.get(i).getText() != null ? mCharacterPuPopList.get(i).getText() : mCharacterPuPopList.get(i).getContent());
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
                currentItem = position;
                ib_character_qiehuan_info.setText(position + 1 + "/" + popSize);
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
                if (currentItem > 0) {
                    currentItem--;
                    viewPager.setCurrentItem(currentItem);
                }
            }
        });
        view.findViewById(R.id.ib_character_qiehuan_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentItem < popSize - 1) {
                    currentItem++;
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
                                mCharacterPuPopList.get(currentItem).getPhotoUrl(), mCharacterPuPopList.get(currentItem).getTitle());
                    }
                }).start();
            }
        });
        // 设置显示动画
        window.setWindowAnimations(com.hyphenate.easeui.R.style.main_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = getWindowManager().getDefaultDisplay().getHeight();
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.MATCH_PARENT;//WRAP_CONTENT

        // 设置显示位置
        dialog.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        dialog.setCanceledOnTouchOutside(true);
        if (!((Activity) mContext).isFinishing()) {
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

    private void wechatShare(final int flag, final String id, String photoUrl, final String title) {
        wxApi = WXAPIFactory.createWXAPI(mContext, WowuApp.WeChat_APP_ID, false);
        wxApi.registerApp(WowuApp.WeChat_APP_ID);
        if (!wxApi.isWXAppSupportAPI()) {
            Message msg = new Message();
            msg.what = LoadingError;
            mHandler.sendMessage(msg);
            return;
        }
        UtilsTool temp = new UtilsTool();
        temp.wechatShare(mContext, wxApi, flag, id, photoUrl, title, currentType);
    }


}

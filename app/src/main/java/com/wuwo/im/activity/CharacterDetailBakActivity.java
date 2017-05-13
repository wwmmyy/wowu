package com.wuwo.im.activity;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wuwo.im.fragement.Portal_FindFragment;

import java.util.ArrayList;
import java.util.List;

import im.imxianzhi.com.imxianzhi.R;

/**
 *  老版本已废弃
 */
public class CharacterDetailBakActivity extends BaseLoadActivity  {
    private WebView mWebView;
    private ImageView iv;
    private AppBarLayout mAppBarLayout;
    private boolean isLight;
    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_detail_layout_bak);
        isLight = getIntent().getBooleanExtra("isLight", true);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
//        mAppBarLayout.setVisibility(View.INVISIBLE);
        iv = (ImageView) findViewById(R.id.iv);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });
        CollapsingToolbarLayout mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
//        mCollapsingToolbarLayout.setTitle(entity.getTitle());
//        mCollapsingToolbarLayout.setContentScrimColor(getResources().getColor(isLight ? R.color.light_toolbar : R.color.dark_toolbar));
//        mCollapsingToolbarLayout.setStatusBarScrimColor(getResources().getColor(isLight ? R.color.light_toolbar : R.color.dark_toolbar));

//        setupRevealBackground(savedInstanceState);
//        setStatusBarColor(getResources().getColor(isLight ? R.color.light_toolbar : R.color.dark_toolbar));

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        setupViewPager();
        mViewPager = (ViewPager) findViewById(R.id.tabanim_viewpager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
//        mViewPager.setOffscreenPageLimit(5);//设置懒加载及缓存数量

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabanim_tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        parseJson("");
    }

    private void parseJson(String responseString) {
//        Gson gson = new Gson();
//        content = gson.fromJson(responseString, Content.class);
        final ImageLoader imageloader = ImageLoader.getInstance();
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        imageloader.displayImage("http://cdnq.duitang.com/uploads/blog/201309/16/20130916142913_tkxNZ.jpeg", iv, options);

//        String css = "<link rel=\"stylesheet\" href=\"file:///android_asset/css/news.css\" type=\"text/css\">";
//        String html = "<html><head>" + css + "</head><body>" + content.getBody() + "</body></html>";
//        html = html.replace("<div class=\"img-place-holder\">", "");
//        mWebView.loadDataWithBaseURL("x-data://base", html, "text/html", "UTF-8", null);
    }


//    private void setupRevealBackground(Bundle savedInstanceState) {
//        vRevealBackground.setOnStateChangeListener(this);
//        if (savedInstanceState == null) {
//            final int[] startingLocation = getIntent().getIntArrayExtra(Constant.START_LOCATION);
//            vRevealBackground.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//                @Override
//                public boolean onPreDraw() {
//                    vRevealBackground.getViewTreeObserver().removeOnPreDrawListener(this);
//                    vRevealBackground.startFromLocation(startingLocation);
//                    return true;
//                }
//            });
//        } else {
//            vRevealBackground.setToFinishedFrame();
//        }
//    }

//    @Override
//    public void onStateChange(int state) {
//        if (RevealBackgroundView.STATE_FINISHED == state) {
//            mAppBarLayout.setVisibility(View.VISIBLE);
//            setStatusBarColor(Color.TRANSPARENT);
//        }
//    }


    private void setupViewPager() {
        String buttonName[] = {"性格谱","功能等级","总倾向度","性格报告"};

        mSectionsPagerAdapter.addFrag(new Portal_FindFragment(), buttonName[0]);//
        mSectionsPagerAdapter.addFrag(new Portal_FindFragment(),buttonName[1]);
        mSectionsPagerAdapter.addFrag(new Portal_FindFragment(),buttonName[2]);
        mSectionsPagerAdapter.addFrag(new Portal_FindFragment(),buttonName[3]);
    }


    @TargetApi(21)
    private void setStatusBarColor(int statusBarColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // If both system bars are black, we can remove these from our layout,
            // removing or shrinking the SurfaceFlinger overlay required for our views.
            Window window = this.getWindow();
            if (statusBarColor == Color.BLACK && window.getNavigationBarColor() == Color.BLACK) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            } else {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            }
            window.setStatusBarColor(statusBarColor);
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void loadServerData(String response, int flag) {

    }

    @Override
    public void loadDataFailed(String response, int flag) {

    }


    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
//            return PlaceholderFragment.newInstance(position + 1);
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {

            return mFragmentTitleList.get(position);
/*            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;*/
        }
    }
}

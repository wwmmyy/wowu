package com.wuwo.im.activity;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wuwo.im.fragement.MatchMe_Users_Fragment;
import com.wuwo.im.fragement.VisitedMe_Users_Fragment;
import com.wuwo.im.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

import im.imxianzhi.com.imxianzhi.R;

/**
 * 记录访问过和匹配过我的人
 */
public class VisitedMeUsersActivity extends BaseLoadActivity {
    private final String TAG = "VisitedMeUsersActivity";
    private RelativeLayout head_layout;
    private TabLayout toolbar_tab;
    private ViewPager main_vp_container;
    private ViewPagerAdapter vpAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visited_me_users);
        initView();

    }

    private void initView() {
        toolbar_tab = (TabLayout) findViewById(R.id.toolbar_tab);
        main_vp_container = (ViewPager) findViewById(R.id.main_vp_container);
        ((TextView) findViewById(R.id.top_title)).setText("访客");


        vpAdapter = new ViewPagerAdapter(getSupportFragmentManager(), this);
        setupViewPager();
        main_vp_container.setAdapter(vpAdapter);
        main_vp_container.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener
                (toolbar_tab));
        toolbar_tab.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener
                (main_vp_container));
        findViewById(R.id.return_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VisitedMeUsersActivity.this.finish();
               overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }

    private void setupViewPager() {
        String buttonName[] = {"谁看过我", "谁配过我"};//,"总倾向度","性格报告"

        vpAdapter.addFrag(new VisitedMe_Users_Fragment(), buttonName[0]);//
        vpAdapter.addFrag(new MatchMe_Users_Fragment(), buttonName[1]);
    }

    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    public class ViewPagerAdapter extends FragmentPagerAdapter {

        final int PAGE_COUNT = 5;
//      private String tabTitles[] = new String[]{"", "分享"};//, "收藏", "关注"
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


    @Override
    public void onClick(View v) {

    }

    @Override
    public void loadServerData(String response, int flag) {
        LogUtils.i("VisitedMeUsersActivity", "：：" + response);

    }

    @Override
    public void loadDataFailed(String response, int flag) {
        LogUtils.i("VisitedMeUsersActivity", "：：" + response);

    }
}

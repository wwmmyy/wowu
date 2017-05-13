package com.wuwo.im.activity;

import android.content.Context;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wuwo.im.bean.CharacterPopInfo;
import com.wuwo.im.bean.CharacterTongLei;
import com.wuwo.im.config.ExitApp;
import com.wuwo.im.fragement.BaseAppFragment;
import com.wuwo.im.fragement.CharacterPuPopFragment;
import com.wuwo.im.fragement.MatchMe_Users_Fragment;
import com.wuwo.im.fragement.VisitedMe_Users_Fragment;
import com.zhy.http.okhttp.service.LoadserverdataService;

import java.util.ArrayList;
import java.util.List;

import im.imxianzhi.com.imxianzhi.R;

/**
 * 性格谱弹出框
 */
public class CharacterPuPopActivity extends BaseFragementActivity implements View.OnClickListener {
    private ViewPager main_vp_container;
    private ViewPagerAdapter vpAdapter;
    private TextView ib_character_qiehuan_info;

    //    LinearLayout pointLLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.character_info_pop);// dialog_character_info
        initView();
    }

    //    ImageView[] imgs;
    int currentItem = 0;

    private void initView() {
//        pointLLayout = (LinearLayout)findViewById(R.id.llayout);
        main_vp_container = (ViewPager) findViewById(R.id.main_vp_container);
        vpAdapter = new ViewPagerAdapter(getSupportFragmentManager(), this);
        findViewById(R.id.tv_return_pop).setOnClickListener(this);
        findViewById(R.id.ib_character_qiehuan_left).setOnClickListener(this);
        findViewById(R.id.ib_character_qiehuan_right).setOnClickListener(this);
        ib_character_qiehuan_info = (TextView) findViewById(R.id.ib_character_qiehuan_info);


        setupViewPager();
        main_vp_container.setAdapter(vpAdapter);


        main_vp_container.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(final int position) {

//                if(imgs!=null){
//                    imgs[currentItem].setEnabled(true);
//                    imgs[position].setEnabled(false);
//                    currentItem = position;
//                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ib_character_qiehuan_info.setText(position+1+"/"+mFragmentList.size());
                    }
                });


                currentItem = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.ib_character_qiehuan_left:
                if(currentItem>0){
                    currentItem--;
                    main_vp_container.setCurrentItem(currentItem, true);
                }
                break;
            case R.id.ib_character_qiehuan_right:
                if(currentItem<mFragmentList.size()-1){
                    currentItem ++;
                    main_vp_container.setCurrentItem(currentItem, true);
                }
                break;
            case R.id.tv_return_pop:
                CharacterPuPopActivity.this.finish();
                this.overridePendingTransition(R.anim.activity_close,0);
                 break;
        }

    }
    @Override
    public void finish() {
        // TODO Auto-generated method stub
        super.finish();
        //关闭窗体动画显示

    }

    private ArrayList<CharacterPopInfo> mCharacterPuPopList = new ArrayList<CharacterPopInfo>(); //记录所有的最新消息

    private void setupViewPager() {
        String response = getIntent().getStringExtra("response");
        String characterType = getIntent().getStringExtra("characterType");
        Gson gson = new GsonBuilder().create();
        if (response != null) {

            //由于从性格谱和从性格详情页跳转过来的内容不同，所以需要将内容进行转化
            if (characterType != null && characterType.equals("xingGeTongLei")) {
                java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<List<CharacterTongLei>>() {
                }.getType();
                ArrayList<CharacterTongLei> temptongleiList = gson.fromJson(response, type);
                if (temptongleiList != null) {
                    for (int i = 0; i < temptongleiList.size(); i++) {
                        CharacterPopInfo temp = new CharacterPopInfo();
                        temp.setPhotoUrl(temptongleiList.get(i).getHeroPhotoUrl());
                        temp.setText(temptongleiList.get(i).getDetail());
                        temp.setTitle(temptongleiList.get(i).getHeroName());
                        mCharacterPuPopList.add(temp);
                    }
                }
            } else {
                java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<List<CharacterPopInfo>>() {
                }.getType();
                mCharacterPuPopList = gson.fromJson(response, type);
            }


        }

        if (mCharacterPuPopList != null) {
            for (int i = 0; i < mCharacterPuPopList.size(); i++) {
                    vpAdapter.addFrag(CharacterPuPopFragment.newInstance(mCharacterPuPopList.get(i).getPhotoUrl(),
                            mCharacterPuPopList.get(i).getText()==null? mCharacterPuPopList.get(i).getContent(): mCharacterPuPopList.get(i).getText(),
                            mCharacterPuPopList.get(i).getTitle()), mCharacterPuPopList.get(i).getTitle());//

                            ib_character_qiehuan_info.setText(1+"/"+mFragmentList.size());

            }
        }

//        if(mFragmentList.size()!=0 ){
//            imgs = new ImageView[mFragmentList.size()];
//            for (int i = 0; i < mFragmentList.size(); i++) {
//                imgs[i] = (ImageView) pointLLayout.getChildAt(i);
//                imgs[i].setVisibility(View.VISIBLE);
//                imgs[i].setEnabled(true);
//                imgs[i].setTag(i);
//            }
//            currentItem = 0;
//            imgs[currentItem].setEnabled(false);
//        }


//        String buttonName[] = {"谁看过我", "谁配过我"};//,"总倾向度","性格报告"
//
//        vpAdapter.addFrag(new VisitedMe_Users_Fragment(), buttonName[0]);//
//        vpAdapter.addFrag(new MatchMe_Users_Fragment(), buttonName[1]);
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


//    public class CharacterPuPopFragment extends BaseAppFragment{
//
//
//
//        @Override
//        public String getFragmentName() {
//            return "CharacterPuPop";
//        }
//    }
}

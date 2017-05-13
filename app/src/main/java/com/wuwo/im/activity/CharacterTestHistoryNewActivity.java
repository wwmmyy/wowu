/*
 * Copyright 2016 Yan Zhenjie
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wuwo.im.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.net.grandcentrix.tray.AppPreferences;
import com.wuwo.im.adapter.ListViewDecoration;
import com.wuwo.im.adapter.MenuAdapter;
import com.wuwo.im.adapter.MyViewPagerAdapter;
import com.wuwo.im.adapter.OnItemClickListener;
import com.wuwo.im.bean.CharacterPopInfo;
import com.wuwo.im.bean.CharacterTongLei;
import com.wuwo.im.bean.Characters;
import com.wuwo.im.bean.HistoryCharacterTest;
import com.wuwo.im.config.WowuApp;
import com.wuwo.im.util.LogUtils;
import com.wuwo.im.util.MyToast;
import com.wuwo.im.view.PullLoadMoreRecyclerView;
import com.yanzhenjie.recyclerview.swipe.Closeable;
import com.yanzhenjie.recyclerview.swipe.OnSwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import im.imxianzhi.com.imxianzhi.R;

//

/**
  */
public class CharacterTestHistoryNewActivity extends BaseLoadActivity {
    private final String TAG = "CharacterTestHistoryActivity";
    private Activity mContext;

//    private List<String> mStrings;

    private LinearLayoutManager mLinearLayoutManager;

    private MenuAdapter mMenuAdapter;


    private PullLoadMoreRecyclerView mPullLoadMoreRecyclerView;
    private int mCount = 0;
    private ArrayList<HistoryCharacterTest.DataBean> historyCharacterTestList = new ArrayList<HistoryCharacterTest.DataBean>(); //记录所有的最新消息
    //    private XinWen_RecyclerViewAdapter mXinWen_RecyclerViewAdapter;
//    private SearchView search_view;
//    private String searchinfo;
//    private CommRecyclerAdapter messageRAdapter;

    private mHandlerWeak mtotalHandler;
    protected ImageButton clearSearch;

    private SharedPreferences mSettings;
    private Characters mCharacter = new Characters();
    public static final int LOAD_RECOMMEND_DATA = 111;
    private TextView tv_character_type_set;
    private TextView tx_top_right;
    private boolean EDIT_MODE = false;
    private ArrayList<CharacterPopInfo> mCharacterPuPopList = new ArrayList<CharacterPopInfo>();
    int CurrentSelect = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_test_history);//R.layout.activity
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mContext = this;


        //当用户性格改变后用此方法可以刷新主界面
//        AppPreferences appPreferences = new AppPreferences(mContext.getApplicationContext());
//        appPreferences.put("characterType",System.currentTimeMillis());
//        appPreferences.put("characterChanged",true);
        initViews();
    }



    private void initViews() {
        findViewById(R.id.return_back).setOnClickListener(this);
        ((TextView) findViewById(R.id.top_title)).setText("历史评测");

        tv_character_type_set = ((TextView) findViewById(R.id.tv_character_type_set));
        tv_character_type_set.setOnClickListener(this);

        tx_top_right = ((TextView) findViewById(R.id.tx_top_right));
        tx_top_right.setText("编辑");
        tx_top_right.setOnClickListener(this);
        tx_top_right.setTextColor(getResources().getColor(R.color.blue));
//        不用弹出编辑了，直接就可以编辑
        tx_top_right.setVisibility(View.GONE);
        tv_character_type_set.setVisibility(View.VISIBLE);

        SwipeMenuRecyclerView swipeMenuRecyclerView = (SwipeMenuRecyclerView) findViewById(R.id.recycler_view);
        mLinearLayoutManager = new LinearLayoutManager(this);
        swipeMenuRecyclerView.setLayoutManager(mLinearLayoutManager);// 布局管理器。
        swipeMenuRecyclerView.setHasFixedSize(true);// 如果Item够简单，高度是确定的，打开FixSize将提高性能。
        swipeMenuRecyclerView.setItemAnimator(new DefaultItemAnimator());// 设置Item默认动画，加也行，不加也行。
        swipeMenuRecyclerView.addItemDecoration(new ListViewDecoration());// 添加分割线。

        // 为SwipeRecyclerView的Item创建菜单就两句话，不错就是这么简单：
        // 设置菜单创建器。
        swipeMenuRecyclerView.setSwipeMenuCreator(swipeMenuCreator);
        // 设置菜单Item点击监听。
        swipeMenuRecyclerView.setSwipeMenuItemClickListener(menuItemClickListener);

        mMenuAdapter = new MenuAdapter(historyCharacterTestList);
        mMenuAdapter.setOnItemClickListener(onItemClickListener);
        swipeMenuRecyclerView.setAdapter(mMenuAdapter);

        swipeMenuRecyclerView.setLongPressDragEnabled(false);// bu开启拖拽，就这么简单一句话。
        //        swipeMenuRecyclerView.setOnItemMoveListener(onItemMoveListener);// 监听拖拽，更新UI。


        mtotalHandler = new mHandlerWeak(this);
        loadData();

    }

//    /**
//     * 当Item移动的时候。
//     */
//    private OnItemMoveListener onItemMoveListener = new OnItemMoveListener() {
//        @Override
//        public boolean onItemMove(int fromPosition, int toPosition) {
//            // 当Item被拖拽的时候。
//            Collections.swap(mStrings, fromPosition, toPosition);
//            mMenuAdapter.notifyItemMoved(fromPosition, toPosition);
//            return true;// 返回true表示处理了，返回false表示你没有处理。
//        }
//
//        @Override
//        public void onItemDismiss(int position) {
//            // 当Item被滑动删除掉的时候，在这里是无效的，因为这里没有启用这个功能。
//            // 使用Menu时就不用使用这个侧滑删除啦，两个是冲突的。
//        }
//    };





    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.return_back:
                Bundle bundle = new Bundle();
//                bundle.putString("info", mCurrentProviceName+" "+mCurrentCityName+" "
//                        +mCurrentDistrictName);//给 bundle 写入数据
                Intent mIntent = new Intent();
                mIntent.putExtras(bundle);
                setResult(RESULT_OK, mIntent);
                this.finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.tx_top_right:
                if (tx_top_right.getText().toString().equals("编辑")) {
                    EDIT_MODE = true;
                    tx_top_right.setText("取消");
                    tv_character_type_set.setVisibility(View.VISIBLE);
                } else {
                    tx_top_right.setText("编辑");
                    EDIT_MODE = false;
                    tv_character_type_set.setVisibility(View.GONE);
                }
                mMenuAdapter.notifyDataSetChanged();
                break;
            case R.id.tv_character_type_set: //设置为当前性格

                if(mMenuAdapter.getLastSelectedId()==null){
                    MyToast.show(mContext,"请选择要设置的性格记录");
                    break;
                }



                //当用户性格改变后用此方法可以刷新主界面
                    AppPreferences appPreferences = new AppPreferences(mContext.getApplicationContext());
                    appPreferences.put("characterType",mMenuAdapter.getLastIntro());
                    appPreferences.put("characterChanged",true);
                try {
                    JSONObject json = new JSONObject();
                    json.put("historyId",mMenuAdapter.getLastSelectedId());//APP版本号historyCharacterTestList.get(CurrentSelect).getId()
                    loadDataService.loadPostJsonRequestData(WowuApp.JSON,WowuApp.SetDispositionFromHistoryURL+"?historyId="+mMenuAdapter.getLastSelectedId(),json.toString(),SET_DISPOSITION_FROMHISTRORY);//historyCharacterTestList.get(CurrentSelect).getId()
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_OK, null);
        this.finish();
    }

    Gson gson = new GsonBuilder().create();
    //    从网络加载流转日志数据并展示出来
    private void loadData() {
        loadDataService.loadGetJsonRequestData(WowuApp.GetTestHistoryURL + "?pageIndex=" + mCount, LOAD_DATA);
    }

    public static final int DOWNLOADED_NEWSMESSAGE = 0;
    public static final int DOWNLOADED_ERROR = 1;
    public static final int REFRESH_DATA = 2;
    public static final int SEARCH_DATA = 3;
    public static final int REFRESH_VIEW = 4;
    private static final int LOAD_DATA = 5;
    private static final int SET_DISPOSITION_FROMHISTRORY = 6;
    public static final int POP_DIALOG = 17;


    private static class mHandlerWeak extends Handler {
        private WeakReference<CharacterTestHistoryNewActivity> activity = null;

        public mHandlerWeak(CharacterTestHistoryNewActivity act) {
            super();
            this.activity = new WeakReference<CharacterTestHistoryNewActivity>(act);
        }

        @Override
        public void handleMessage(Message msg) {
            CharacterTestHistoryNewActivity act = activity.get();
            if (null == act) {
                return;
            }
            switch (msg.what) {
                // 正在下载
                case DOWNLOADED_NEWSMESSAGE:
                    act.mMenuAdapter.setData(act.getLoadInfo());
                    break;
                case DOWNLOADED_ERROR:
//                    MyToast.show(mContext, "服务器返回值异常", Toast.LENGTH_LONG);
                    act.mPullLoadMoreRecyclerView.setRefreshing(false);
//                    mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();
                    break;
                case REFRESH_DATA:
//                    act.setRefresh();
                    act.loadData();
                    break;
                case REFRESH_VIEW:
                    act.mMenuAdapter.notifyDataSetChanged();
                    break;
                case POP_DIALOG:
                    act.showCharacterInfoDialog();
                    break;
                case SET_DISPOSITION_FROMHISTRORY:
                    MyToast.show(act.mContext, msg.obj + "");

                    Bundle bundle = new Bundle();
                    bundle.putString("historyId",act.mMenuAdapter.getLastSelectedId());
                    bundle.putString("testType",act.mMenuAdapter.getLastSelectedType()==0 ?"选择":(act.mMenuAdapter.getLastSelectedType()==1 ?"快速":"精确"));
                    Intent mIntent = new Intent();
                    mIntent.putExtras(bundle);
                    act.setResult(RESULT_OK, mIntent);
                    act.finish();
                    act.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);


                    break;
                default:
                    break;
            }
        }
    }


    /**
     * 菜单创建器。
     */
    private SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
            int width = getResources().getDimensionPixelSize(R.dimen.item_height);

            // MATCH_PARENT 自适应高度，保持和内容一样高；也可以指定菜单具体高度，也可以用WRAP_CONTENT。
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            // 添加右侧的，如果不添加，则右侧不会出现菜单。
            {
                SwipeMenuItem deleteItem = new SwipeMenuItem(mContext)
                        .setBackgroundColor(Color.RED)
//                        .setImage(R.drawable.character_bgred)
                        .setText("删除") // 文字，还可以设置文字颜色，大小等。。
                        .setTextSize(16)
                        .setTextColor(Color.WHITE)
                        .setWidth(width)
                        .setHeight(height);
                swipeRightMenu.addMenuItem(deleteItem);// 添加一个按钮到右侧侧菜单。

//                SwipeMenuItem closeItem = new SwipeMenuItem(mContext)
//                        .setBackgroundDrawable(R.drawable.selector_purple)
//                        .setImage(R.mipmap.ic_action_close)
//                        .setWidth(width)
//                        .setHeight(height);
//                swipeRightMenu.addMenuItem(closeItem); // 添加一个按钮到右侧菜单。
//
//                SwipeMenuItem addItem = new SwipeMenuItem(mContext)
//                        .setBackgroundDrawable(R.drawable.selector_green)
//                        .setText("添加")
//                        .setTextColor(Color.WHITE)
//                        .setWidth(width)
//                        .setHeight(height);
//                swipeRightMenu.addMenuItem(addItem); // 添加一个按钮到右侧菜单。
            }
        }
    };

    HistoryCharacterTest historyCharacterTest = null;
    public void setLoadInfo(String totalresult) throws JSONException {
        Gson gson = new GsonBuilder().create();
        if (totalresult != null) {
            java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<HistoryCharacterTest>() {
            }.getType();
            historyCharacterTest = gson.fromJson(totalresult, type);
            historyCharacterTestList = (ArrayList<HistoryCharacterTest.DataBean>) historyCharacterTest.getData();
        }
    }


    public ArrayList<HistoryCharacterTest.DataBean> getLoadInfo() {
        return historyCharacterTestList;
    }

    private void loadPopInfo(String url, int popDialog) {
        loadDataService.loadGetJsonRequestData(url, popDialog);//后面需要改过来
    }

    private OnItemClickListener onItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(int position) {
//            Toast.makeText(mContext, "我目前是第" + position + "条。", Toast.LENGTH_SHORT).show();
//            MyToast.show(mContext,"我目前是第" + position + "条。");
//            loadPopInfo(WowuApp.GetDispositionNicknamesURL + "?id=" + historyCharacterTestList.get(position).getId(), POP_DIALOG);

            Bundle bundle = new Bundle();
            bundle.putString("historyId",historyCharacterTestList.get(position).getId());
            bundle.putString("testType",historyCharacterTestList.get(position).getQuestionType()==0 ?"选择":(historyCharacterTestList.get(position).getQuestionType()==1 ?"快速":"精确"));
            Intent mIntent = new Intent();
            mIntent.putExtras(bundle);
//            mIntent.putExtra("historyId",historyCharacterTestList.get(position).getId());
//            mIntent.putExtra("testType",historyCharacterTestList.get(position).getQuestionType()==0 ?"选择":(historyCharacterTestList.get(position).getQuestionType()==1 ?"快速":"精确"));
            setResult(RESULT_OK, mIntent);
            CharacterTestHistoryNewActivity.this.finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        }
    };

    /**
     * 菜单点击监听。
     */
    private OnSwipeMenuItemClickListener menuItemClickListener = new OnSwipeMenuItemClickListener() {
        @Override
        public void onItemClick(Closeable closeable, int adapterPosition, int menuPosition, int direction) {
            closeable.smoothCloseMenu();// 关闭被点击的菜单。

            if (direction == SwipeMenuRecyclerView.RIGHT_DIRECTION) {
//                Toast.makeText(mContext, "list第" + adapterPosition + "; 右侧菜单第" + menuPosition, Toast.LENGTH_SHORT).show();
//                MyToast.show(mContext,"list第" + adapterPosition + "; 右侧菜单第" + menuPosition);

                historyCharacterTestList.remove(adapterPosition);
                mMenuAdapter.setData2(historyCharacterTestList);
//                mMenuAdapter.getHistoryCharacterTestList().remove(adapterPosition);


            } else if (direction == SwipeMenuRecyclerView.LEFT_DIRECTION) {
//                Toast.makeText(mContext, "list第" + adapterPosition + "; 左侧菜单第" + menuPosition, Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }




    @Override
    public void loadServerData(final String response, int flag) {

        LogUtils.i(TAG, "：：" + response);
        switch (flag) {
            case LOAD_DATA:
//                LogUtils.i("获取好友推荐列表", "：：" + response);

//                UtilsTool.saveStringToSD(json.toString());
                try {
                    if (response != null) {
                        setLoadInfo(response);
                    }
                    Message msg = new Message();
                    msg.what = DOWNLOADED_NEWSMESSAGE;
                    mtotalHandler.sendMessage(msg);
                } catch (Exception e) {
//                                    e.printStackTrace();
                    Message msg = new Message();
                    msg.what = DOWNLOADED_ERROR;
                    mtotalHandler.sendMessage(msg);
                }
                break;

            case SET_DISPOSITION_FROMHISTRORY:
                Message msg = new Message();
                msg.what = SET_DISPOSITION_FROMHISTRORY;
                msg.obj="设置成功";
                mtotalHandler.sendMessage(msg);
                break;
            case LOAD_RECOMMEND_DATA:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new GsonBuilder().create();
                        java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<Characters>() {
                        }.getType();
                        if (response != null) {
                            mCharacter = gson.fromJson(response, type);
                            SharedPreferences.Editor editor = mSettings.edit();
                            editor.putString("characterInfo", response);
                            if (mCharacter.getPhotoUrl() != null) {
                                editor.putString("characterUrl", mCharacter.getPhotoUrl());
                            }
                            editor.commit();
                        }
                    }
                }).start();
                break;
            case POP_DIALOG:
//              此种是跳到一个新的activity，但是这种动画效果不好
                if (response != null) {
//                    Intent intent = new Intent(mContext, CharacterPuPopActivity.class);
//                    intent.putExtra("response", response);
//                    intent.putExtra("characterType", "xingGeTongLei");
//                    startActivity(intent);
//                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);



//                    Gson gson2 = new GsonBuilder().create();
//                    if (response != null) {
//                        java.lang.reflect.Type type2 = new com.google.gson.reflect.TypeToken<List<CharacterPopInfo>>() {
//                        }.getType();
//                        mCharacterPuPopList = gson2.fromJson(response, type2);
//                        Message msg3 = new Message();
//                        msg3.what = POP_DIALOG;
//                        mtotalHandler.sendMessage(msg3);
//                    }


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
                        //                 由于弹出效果不好，换用另一种方式展现

                        Message msg1 = new Message();
                        msg1.what =POP_DIALOG;
                        mtotalHandler.sendMessage(msg1);
                    } else {
                        Message msg1 = new Message();
                        msg1.what = SET_DISPOSITION_FROMHISTRORY;
                        msg1.obj = "获取性格弹出信息失败，请重试";
                        mtotalHandler.sendMessage(msg1);
                    }

                } else {
                    Message msg1 = new Message();
                    msg1.what = SET_DISPOSITION_FROMHISTRORY;
                    msg1.obj = "获取性格弹出信息失败，请重试";
                    mtotalHandler.sendMessage(msg1);
                }
                break;

        }
    }

    @Override
    public void loadDataFailed(String response, int flag) {
        LogUtils.i(TAG, "：：" + response);
//        MyToast.show(mContext, response);
//        if(LOAD_RECOMMEND_DATA ==flag){
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    Gson gson = new GsonBuilder().create();
//                    java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<Characters>() {
//                    }.getType();
//                    if(mSettings.getString("characterInfo",null) !=null){
//                        mCharacter = gson.fromJson(mSettings.getString("characterInfo",""), type);
//                    }
//                }
//            }).start();
//        }

        Message msg = new Message();
        msg.what = SET_DISPOSITION_FROMHISTRORY;
        msg.obj="设置失败";
        mtotalHandler.sendMessage(msg);

    }


    public boolean loadMore() {
        return false;
    }

    public void refresh() {
        if (loadDataService != null) {
//            loadDataService.loadGetJsonRequestData(WowuApp.GetDispositionInfoURL, LOAD_RECOMMEND_DATA);
            loadData();
        }
    }
    /**
     * 性格谱弹出窗
     */
    int currentItem = 0;
    private void showCharacterInfoDialog() {
        currentItem = 0;
        View view = getLayoutInflater().inflate(R.layout.dialog_character_info, null);
        final Dialog dialog = new Dialog(mContext, com.hyphenate.easeui.R.style.Dialog_Fullscreen);
        dialog.setContentView(view, new ViewGroup.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        Window window = dialog.getWindow();

        final int popSize = mCharacterPuPopList.size();
        final ViewPager viewPager = (ViewPager) view.findViewById(R.id.main_vp_container);
        final TextView ib_character_qiehuan_info = (TextView) view.findViewById(R.id.ib_character_qiehuan_info);

        ArrayList<View> views = new ArrayList<View>();
        LayoutInflater inflater = getLayoutInflater();
        ib_character_qiehuan_info.setText(1 + "/" + popSize);
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
        if(!((Activity) mContext).isFinishing()) {
            dialog.show();
        }
    }

}
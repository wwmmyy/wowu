package com.wuwo.im.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.widget.EaseAlertDialog;
import com.hyphenate.util.DeviceUuidFactory;
import com.squareup.okhttp.Request;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.wuwo.im.adapter.CommRecyclerAdapter;
import com.wuwo.im.adapter.CommRecyclerViewHolder;
import com.wuwo.im.adapter.MyViewPagerAdapter;
import com.wuwo.im.bean.CharacterPopInfo;
import com.wuwo.im.bean.CharacterTongLei;
import com.wuwo.im.bean.Characters;
import com.wuwo.im.bean.HistoryCharacterTest;
import com.wuwo.im.bean.HistoryCharacterTest.DataBean;
import com.wuwo.im.config.WowuApp;
import com.wuwo.im.util.LogUtils;
import com.wuwo.im.util.MyToast;
import com.wuwo.im.util.UtilsTool;
import com.wuwo.im.view.PullLoadMoreRecyclerView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import im.imxianzhi.com.imxianzhi.R;

/**
 * 历史测评
 */
public class CharacterTestHistoryActivity extends BaseLoadActivity {

    private final String TAG = "CharacterTestHistoryActivity";
    private PullLoadMoreRecyclerView mPullLoadMoreRecyclerView;
    private int mCount = 0;
    private ArrayList<HistoryCharacterTest.DataBean> historyCharacterTestList = new ArrayList<HistoryCharacterTest.DataBean>(); //记录所有的最新消息
    //    private XinWen_RecyclerViewAdapter mXinWen_RecyclerViewAdapter;
//    private SearchView search_view;
    private String searchinfo;
    private CommRecyclerAdapter messageRAdapter;

    private mHandlerWeak mtotalHandler;
    protected ImageButton clearSearch;

    private SharedPreferences mSettings;
    private Characters mCharacter = new Characters();
    public static final int LOAD_RECOMMEND_DATA = 111;
    private TextView tv_character_type_set;
    private TextView tx_top_right;
    private boolean EDIT_MODE = false;
    private ArrayList<CharacterPopInfo> mCharacterPuPopList = new ArrayList<CharacterPopInfo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_list);
//        UtilsTool.hideSoftKeyboard(CharacterTestHistoryActivity.this);
        mSettings = mContext.getSharedPreferences(WowuApp.PREFERENCE_KEY,
                android.content.Context.MODE_PRIVATE);

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

        mPullLoadMoreRecyclerView = (PullLoadMoreRecyclerView) findViewById(R.id.pullLoadMoreRecyclerView);
        mPullLoadMoreRecyclerView.setRefreshing(true);
        mPullLoadMoreRecyclerView.setLinearLayout();//setGridLayout  setStaggeredGridLayout
////      mPullLoadMoreRecyclerView.setGridLayout(3);
//        mPullLoadMoreRecyclerView.setStaggeredGridLayout(2);
        mPullLoadMoreRecyclerView.setOnPullLoadMoreListener(new PullLoadMoreListener());

        mtotalHandler = new mHandlerWeak(this);
        loadData();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Gson gson = new GsonBuilder().create();
//                java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<Characters>() {
//                }.getType();
//                if (mSettings.getString("characterInfo", null) != null) {
//                    mCharacter = gson.fromJson(mSettings.getString("characterInfo", ""), type);
//                }
//            }
//        }).start();

    }


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
                messageRAdapter.notifyDataSetChanged();
                break;
            case R.id.tv_character_type_set: //设置为当前性格
                try {
                    JSONObject json = new JSONObject();
                    json.put("historyId",historyCharacterTestList.get(CurrentSelect).getId());//APP版本号
                    loadDataService.loadPostJsonRequestData(WowuApp.JSON,WowuApp.SetDispositionFromHistoryURL+"?historyId="+historyCharacterTestList.get(CurrentSelect).getId(),json.toString(),SET_DISPOSITION_FROMHISTRORY);
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
        private WeakReference<CharacterTestHistoryActivity> activity = null;

        public mHandlerWeak(CharacterTestHistoryActivity act) {
            super();
            this.activity = new WeakReference<CharacterTestHistoryActivity>(act);
        }

        @Override
        public void handleMessage(Message msg) {
            CharacterTestHistoryActivity act = activity.get();
            if (null == act) {
                return;
            }
            switch (msg.what) {
                // 正在下载
                case DOWNLOADED_NEWSMESSAGE:
                    if (act.messageRAdapter == null) {
                        act.initAdapter();
                        act.messageRAdapter.setData(act.getLoadInfo());
                        act.mPullLoadMoreRecyclerView.setAdapter(act.messageRAdapter);
                    } else {
                        act.messageRAdapter.setData(act.getLoadInfo());
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
                case REFRESH_VIEW:
                    act.messageRAdapter.notifyDataSetChanged();
                    break;
                case POP_DIALOG:
                    act.showCharacterInfoDialog();
                    break;
                case SET_DISPOSITION_FROMHISTRORY:
                    MyToast.show(act.mContext, msg.obj+"");
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

    HistoryCharacterTest historyCharacterTest = null;

    public void setLoadInfo(String totalresult) throws JSONException {
        Gson gson = new GsonBuilder().create();
        if (totalresult != null) {
            java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<HistoryCharacterTest>() {
            }.getType();
            historyCharacterTest = gson.fromJson(totalresult, type);
            historyCharacterTestList = (ArrayList<DataBean>) historyCharacterTest.getData();
        }
    }


    public ArrayList<?> getLoadInfo() {
        return historyCharacterTestList;
    }


    //用于记录当前点击添加的好友的id
//    String currentClickfriendId = null;
    int CurrentSelect = 0;
    ImageView lastView = null;
    String var_year = "yyyy";
    String var_month_day = "MM/dd ";
    SimpleDateFormat format_year=new SimpleDateFormat(var_year);//, Locale.ENGLISH
    SimpleDateFormat format_month_day=new SimpleDateFormat(var_month_day);//, Locale.ENGLISH
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    public CommRecyclerAdapter initAdapter() {
        messageRAdapter = new CommRecyclerAdapter<HistoryCharacterTest.DataBean>(mContext, R.layout.item_character_testhistory) {
            @Override
            public void convert(CommRecyclerViewHolder viewHolder, HistoryCharacterTest.DataBean mainMessage) {
                //对对应的View进行赋值
//                viewHolder.setText(R.id.tv_label_time, mainMessage.getTime().replace(" ", "\n"));
                try {
                    Date date = sdf.parse(mainMessage.getTime());
                    viewHolder.setText(R.id.tv_label_time, format_month_day.format( date));
                    viewHolder.setText(R.id.tv_label_time0, format_year.format(date));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                viewHolder.setText(R.id.contract_title, mainMessage.getDispositionName());

                if (!EDIT_MODE) {
                    viewHolder.getView(R.id.tv_test_history).setVisibility(View.VISIBLE);
                    viewHolder.setText(R.id.tv_test_history, mainMessage.getPropensityScore() + "%");
                    viewHolder.getView(R.id.iv_character_set).setVisibility(View.GONE);
                } else {
                    viewHolder.getView(R.id.tv_test_history).setVisibility(View.GONE);
                    viewHolder.getView(R.id.iv_character_set).setVisibility(View.VISIBLE);
                }

                ImageView character_test_type = (ImageView) viewHolder.getView(R.id.character_type);
                switch (mainMessage.getQuestionType()) {
                    case 0:
                        character_test_type.setImageResource(R.drawable.choose_2);
                        break;
                    case 1:
                        character_test_type.setImageResource(R.drawable.kuaisu);
                        break;
                    case 2:
                        character_test_type.setImageResource(R.drawable.jinque);
                        break;
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

                if (EDIT_MODE) {
                    if (lastView != null) {
                        lastView.setImageResource(R.drawable.normal);
                    }
                    CommRecyclerViewHolder holder = (CommRecyclerViewHolder) view.getTag();
                    lastView = holder.getView(R.id.iv_character_set);
                    lastView.setImageResource(R.drawable.select);
                    CurrentSelect = position;
//                messageRAdapter.notifyDataSetChanged();

//                Intent intent2 = new Intent(mContext, UserInfoEditActivity.class);
//                intent2.putExtra("chatUserId", ((HistoryCharacterTest.DataBean) messageRAdapter.getData().get(position)).getId());
//                intent2.putExtra("PhotoUrl", ((HistoryCharacterTest.DataBean) messageRAdapter.getData().get(position)).getId());
//                startActivity(intent2);
//                CharacterTestHistoryActivity.this.overridePendingTransition(0, 0);
                }else{
// fsdfd
//                    showCharacterInfoDialog();
                    loadPopInfo(WowuApp.GetDispositionNicknamesURL + "?id=" + historyCharacterTestList.get(position).getId(), POP_DIALOG);
                }
            }
        });
        return null;
    }

    private void loadPopInfo(String url, int popDialog) {
        loadDataService.loadGetJsonRequestData(url, popDialog);//后面需要改过来
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

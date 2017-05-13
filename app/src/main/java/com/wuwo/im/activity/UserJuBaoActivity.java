package com.wuwo.im.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wuwo.im.adapter.CommRecyclerAdapter;
import com.wuwo.im.adapter.CommRecyclerViewHolder;
import com.wuwo.im.adapter.JuBaoAdapter;
import com.wuwo.im.bean.UserInfoDetail;
import com.wuwo.im.config.WowuApp;
import com.wuwo.im.util.LogUtils;
import com.wuwo.im.util.MyToast;
import com.wuwo.im.util.UtilsTool;
import com.wuwo.im.view.PullLoadMoreRecyclerView;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Arrays;

import im.imxianzhi.com.imxianzhi.R;

public class UserJuBaoActivity extends BaseLoadActivity {
    Context mContext = UserJuBaoActivity.this;
    EditText feed_back_content, feed_back_edit3;
    private final  String TAG="UserJuBaoActivity";
    SharedPreferences mSettings;
    public static final int ADD_ATTACHMENT = 1000;
    private PullLoadMoreRecyclerView mPullLoadMoreRecyclerView;
//    private CommRecyclerAdapter messageRAdapter;
    private JuBaoAdapter mJuBaoAdapter;

    mHandlerWeak mtotalHandler;
    RelativeLayout rt_jubao_sublog, rt_jubao_liaotian, rt_jubao_ziliao;
    public final int CATA_LOG_STATE = 11;
    public final int SUB_LOG_ONE_STATE = 12;
    public final int SUB_LOG_TWO_STATE = 13;
    private String userId = "";
    public int CURRENT_STATE = CATA_LOG_STATE;
    public int CURRENT_SELECTED_POSITION = -1;


    String[] subLog_One = {"骚扰辱骂", "淫秽色情", "垃圾广告", "血腥暴力", "欺诈（酒托、花费拖等）", "违法行为（咋骗、违禁品、反动等）"};
    String[] subLog_Two = {"淫秽色情", "垃圾广告", "血腥暴力", "违法行为（咋骗、违禁品、反动等）", "TA的账号被盗了"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO 自动生成的方法存根
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        //            WindowManager.LayoutParams.FLAG_FULLSCREEN);// 去掉信息栏
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jubao);
        mSettings = getSharedPreferences(WowuApp.PREFERENCE_KEY, MODE_PRIVATE);
        mtotalHandler = new mHandlerWeak(this);

        userId = getIntent().getStringExtra("userId");

        rt_jubao_liaotian = (RelativeLayout) findViewById(R.id.rt_jubao_liaotian);
        rt_jubao_liaotian.setOnClickListener(this);
        rt_jubao_ziliao = (RelativeLayout) findViewById(R.id.rt_jubao_ziliao);
        rt_jubao_ziliao.setOnClickListener(this);
        findViewById(R.id.return_back).setOnClickListener(this);
        findViewById(R.id.bt_jubao_submit).setOnClickListener(this);
        ((TextView) findViewById(R.id.top_title)).setText("举报");

        initSubLogView();

    }

    private void initSubLogView() {
        rt_jubao_sublog = (RelativeLayout) findViewById(R.id.rt_jubao_sublog);
        mPullLoadMoreRecyclerView = (PullLoadMoreRecyclerView) findViewById(R.id.jubao_pullLoadMoreRecyclerView);
        mPullLoadMoreRecyclerView.setRefreshing(false);
        mPullLoadMoreRecyclerView.setLinearLayout();//setGridLayout  setStaggeredGridLayout

//        initAdapter();
//        messageRAdapter.setData2(Arrays.asList(subLog_One));
//        mPullLoadMoreRecyclerView.setAdapter(messageRAdapter);
        mJuBaoAdapter =new   JuBaoAdapter(mContext,Arrays.asList(subLog_One));
        mPullLoadMoreRecyclerView.setAdapter(mJuBaoAdapter);

        mJuBaoAdapter.setOnItemClickLitener(new JuBaoAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                CURRENT_SELECTED_POSITION = position;
                mJuBaoAdapter.setDefaultSelectPos(position);

            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });


    }

//    View lastView = null;

//    public CommRecyclerAdapter initAdapter() {
//        messageRAdapter = new CommRecyclerAdapter<String>(mContext, R.layout.item_simple_jubao_list) {
//            @Override
//            public void convert(CommRecyclerViewHolder viewHolder, String mainMessage) {
//                //对对应的View进行赋值
//                viewHolder.setText(R.id.tv_jubao_info, mainMessage);
//            }
//
//            @Override
//            public int getItemViewType(int position) {
//                return super.getItemViewType(position);
//            }
//        };
//        //设置item点击事件
//        messageRAdapter.setOnItemClick(new CommRecyclerAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//
//            }
//        });
//
//        //设置item点击事件
//        messageRAdapter.setOnItemClick(new CommRecyclerAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                CommRecyclerViewHolder holder = (CommRecyclerViewHolder) view.getTag();
//                if (lastView != null) {
//                    lastView.setBackgroundResource(R.drawable.item_background_selector_light);
//                }
//                lastView = holder.getView(R.id.ln_item_jubao);
//                lastView.setBackgroundResource(R.drawable.card_light_press);
//                CURRENT_SELECTED_POSITION = position;
//            }
//        });
//
//        return null;
//    }


    @Override
    public void onClick(View v) {
        // TODO 自动生成的方法存根
        switch (v.getId()) {
            case R.id.rt_jubao_liaotian:
                changeView();
//                messageRAdapter.setData2(Arrays.asList(subLog_One));
                mJuBaoAdapter.setData2(Arrays.asList(subLog_One));
                CURRENT_STATE = SUB_LOG_ONE_STATE;

                break;
            case R.id.rt_jubao_ziliao:

                CURRENT_STATE = SUB_LOG_TWO_STATE;
                changeView();
//                messageRAdapter.setData2(Arrays.asList(subLog_Two));
                mJuBaoAdapter.setData2(Arrays.asList(subLog_Two));

                break;
            case R.id.return_back:

                if (CURRENT_STATE == CATA_LOG_STATE) {
                    UserJuBaoActivity.this.finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                } else {
                    CURRENT_STATE = CATA_LOG_STATE;
                    rt_jubao_sublog.setVisibility(View.GONE);
                    rt_jubao_liaotian.setVisibility(View.VISIBLE);
                    rt_jubao_ziliao.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.bt_jubao_submit:
                sendToServer();
                break;
            default:
                break;
        }

    }

    private void changeView() {
        rt_jubao_sublog.setVisibility(View.VISIBLE);
        rt_jubao_liaotian.setVisibility(View.GONE);
        rt_jubao_ziliao.setVisibility(View.GONE);
    }

    private static class mHandlerWeak extends Handler {
        private WeakReference<UserJuBaoActivity> activity = null;

        public mHandlerWeak(UserJuBaoActivity act) {
            super();
            this.activity = new WeakReference<UserJuBaoActivity>(act);
        }

        @Override
        public void handleMessage(Message msg) {
            UserJuBaoActivity act = activity.get();
            if (null == act) {
                return;
            }
            switch (msg.what) {
                case ADD_ATTACHMENT:
                    UserInfoDetail.PhotosBean temp = new UserInfoDetail.PhotosBean();
                    temp.setLocalPath(msg.obj + "");
                    break;
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * @param
     * @return void
     * @throws
     * @Title: sendToServer
     * @Description: 上传到服务器
     */
    ProgressDialog pb;

    public void sendToServer() {

        pb = UtilsTool.initProgressDialog(mContext, "请稍后...");
        pb.show();
        int tempcatalog = 1;
        String subCatalog = "1";

        if (CURRENT_STATE == SUB_LOG_ONE_STATE) {
            tempcatalog = 1;
            subCatalog = CURRENT_SELECTED_POSITION + 1 + "";
        } else {
            tempcatalog = 2;
            subCatalog = "10" + CURRENT_SELECTED_POSITION + 1 + "";
        }


        try {
            JSONObject json = new JSONObject();
            json.put("userId", userId);// WowuApp.UserId
            //    POST api/Chat?userId={userId}&catalog={catalog}&subCatalog={subCatalog}    举报
            loadDataService.loadPostJsonRequestData(WowuApp.JSON, WowuApp.apiChatURL + "?userId=" + userId + "&catalog=" + tempcatalog + "&subCatalog= " + subCatalog, json.toString(), R.id.bt_jubao_submit);//
            LogUtils.i(TAG, "：：" +  WowuApp.apiChatURL + "?userId=" + userId + "&catalog=" + tempcatalog + "&subCatalog= " + subCatalog);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ProgressDialog pg;
    private final int Loading = 1;
    private final int END = 2;
    private final int WRONG = 3;

    /**
     * 上传到服务器是加载进度框
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Loading:
                    //pg = UtilsTool.initProgressDialog(mContext, "正在上传.....");
                    pg = UtilsTool.initProgressDialog(mContext, "正在上传.....");
                    pg.show();
                    break;
                case END:
                    if (pb != null && pb.isShowing()) {
                        pb.dismiss();
                    }
                    MyToast.show(mContext, "举报成功");
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    break;

                case WRONG:
                    if (pb != null && pb.isShowing()) {
                        pb.dismiss();
                    }
                    MyToast.show(mContext, "举报失败");
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    break;
            }

            super.handleMessage(msg);
        }
    };


    @Override
    public void loadServerData(String response, int flag) {
        Message msg = Message.obtain();
        msg.what = END;
        mHandler.sendMessage(msg);

    }

    @Override
    public void loadDataFailed(String response, int flag) {
        Message msg = Message.obtain();
        msg.what = WRONG;
        mHandler.sendMessage(msg);
    }


}

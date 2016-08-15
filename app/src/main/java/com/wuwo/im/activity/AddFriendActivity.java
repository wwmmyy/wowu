package com.wuwo.im.activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.widget.EaseAlertDialog;
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
import com.wuwo.im.bean.RecommendFriends;
import com.wuwo.im.config.WowuApp;
import com.wuwo.im.util.MyToast;
import com.wuwo.im.util.UtilsTool;
import com.wuwo.im.view.PullLoadMoreRecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import im.wuwo.com.wuwo.R;

/**
 * 添加密码
 * desc RegisterStepThreeActivity
 *
 * @author 王明远
 * @日期： 2016/6/9 0:07
 * @版权:Copyright All rights reserved.
 */

public class AddFriendActivity extends BaseLoadActivity {

    private final String TAG = "AddFriendActivity";
    private PullLoadMoreRecyclerView mPullLoadMoreRecyclerView;
    private int mCount = 1;
    private ArrayList<RecommendFriends> RecommendFriends_List = new ArrayList<RecommendFriends>(); //记录所有的最新消息
    //    private XinWen_RecyclerViewAdapter mXinWen_RecyclerViewAdapter;
//    private SearchView search_view;
    private String searchinfo;
    private  CommRecyclerAdapter messageRAdapter;

    private mHandlerWeak mtotalHandler;
    protected EditText query;
    protected ImageButton clearSearch;

    private IWXAPI wxApi;
    private  int scenePengYouQuan = SendMessageToWX.Req.WXSceneTimeline;
    private int sceneHaoYou = SendMessageToWX.Req.WXSceneSession;
    private Tencent mTencent;
    private SharedPreferences mSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfriend_pullloadmore_recyclerview);
//        UtilsTool.hideSoftKeyboard(AddFriendActivity.this);


        initViews();

        wxApi = WXAPIFactory.createWXAPI(mContext, WowuApp.WeChat_APP_ID, false);
        wxApi.registerApp(WowuApp.WeChat_APP_ID);
        // 其中APP_ID是分配给第三方应用的appid，类型为String。
        mTencent = Tencent.createInstance(WowuApp.QQ_APP_ID, mContext.getApplicationContext());
        mSettings = mContext.getSharedPreferences(WowuApp.PREFERENCE_KEY,
                android.content.Context.MODE_PRIVATE);
    }


    private int searchInfoTag;//由于采用的是快速搜素方法，假如用户连续输入，而网络延迟，可能搜索过程中产生的结果并不是用户想要的这是就要过滤掉

    private void initViews() {
        findViewById(R.id.return_back).setOnClickListener(this);
        ((TextView) findViewById(R.id.top_title)).setText("添加好友");

        mPullLoadMoreRecyclerView = (PullLoadMoreRecyclerView) findViewById(R.id.pullLoadMoreRecyclerView);
        mPullLoadMoreRecyclerView.setRefreshing(true);
        mPullLoadMoreRecyclerView.setLinearLayout();//setGridLayout  setStaggeredGridLayout
////      mPullLoadMoreRecyclerView.setGridLayout(3);
//        mPullLoadMoreRecyclerView.setStaggeredGridLayout(2);
        mPullLoadMoreRecyclerView.setOnPullLoadMoreListener(new PullLoadMoreListener());

        mtotalHandler = new mHandlerWeak(this);

        findViewById(R.id.tv_contact_add).setOnClickListener(this);
        findViewById(R.id.tv_weixin_add).setOnClickListener(this);
        findViewById(R.id.tv_qq_add).setOnClickListener(this);


//初始化搜索模块
        initQuery();

        loadData();
    }

    List<RecommendFriends> searchList = new ArrayList<RecommendFriends>();
    String currentSearchInfo = "";

    private void initQuery() {
        query = (EditText) findViewById(R.id.query);
        // button to clear content in search bar
        clearSearch = (ImageButton) findViewById(R.id.search_clear);

        query.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchInfo = s.toString();
                if (s.length() > 0) {
                    clearSearch.setVisibility(View.VISIBLE);
                    SearchFilter(s);
                } else {
                    clearSearch.setVisibility(View.INVISIBLE);
                    searchList = RecommendFriends_List;
                    Message msg = new Message();
                    msg.what = SEARCH_DATA;
                    mtotalHandler.sendMessage(msg);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
        clearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query.getText().clear();
                UtilsTool.hideSoftKeyboard(AddFriendActivity.this);
            }
        });
    }

    private void SearchFilter(final CharSequence s) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (RecommendFriends_List != null) {
                    searchList = new ArrayList<RecommendFriends>();
                    for (int i = 0; i < RecommendFriends_List.size(); i++) {
                        if (((RecommendFriends) RecommendFriends_List.get(i)).getName().contains(s)) {
                            RecommendFriends temp = (RecommendFriends) RecommendFriends_List.get(i);
                            searchList.add(temp);
                        }
                    }
                    if (s.toString().equals(currentSearchInfo)) {
                        Message msg = new Message();
                        msg.what = SEARCH_DATA;
                        mtotalHandler.sendMessage(msg);
                    }
                }
            }
        }).start();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.return_back:
                this.finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.tv_contact_add:
                Intent txl = new Intent(mContext, FromContractsActivity.class);
                startActivity(txl);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.tv_weixin_add:
                showWeiXinShareDialog();
                break;
            case R.id.tv_qq_add:
                showQQShareDialog();
                break;

        }
    }

    Gson gson = new GsonBuilder().create();
    private final int LOAD_DATA = 1;

    //    从网络加载流转日志数据并展示出来
    private void loadData() {
        loadDataService.loadGetJsonRequestData(WowuApp.ChatRecommendFriendURL, LOAD_DATA);
    }

    public static final int DOWNLOADED_NEWSMESSAGE = 0;
    public static final int DOWNLOADED_ERROR = 1;
    public static final int REFRESH_DATA = 2;
    public static final int SEARCH_DATA = 3;
    public static final int REFRESH_VIEW = 4;


    private static class mHandlerWeak extends Handler {
        private WeakReference<AddFriendActivity> activity = null;

        public mHandlerWeak(AddFriendActivity act) {
            super();
            this.activity = new WeakReference<AddFriendActivity>(act);
        }

        @Override
        public void handleMessage(Message msg) {
            AddFriendActivity act = activity.get();
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
                case SEARCH_DATA:
                    if (act != null && act.messageRAdapter != null) {
                        act.messageRAdapter.setData2(act.searchList);
                    }
                    break;
                case REFRESH_VIEW:
                    act.messageRAdapter.notifyDataSetChanged();
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
        mCount = 1;
    }


    public void setLoadInfo(String totalresult) throws JSONException {
        Gson gson = new GsonBuilder().create();
        if (totalresult != null) {
            java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<List<RecommendFriends>>() {
            }.getType();
            RecommendFriends_List = gson.fromJson(totalresult, type);
        }
    }


    public ArrayList<?> getLoadInfo() {
        return RecommendFriends_List;
    }


    //用于记录当前点击添加的好友的id
    String currentClickfriendId = null;

    public CommRecyclerAdapter initAdapter() {
        messageRAdapter = new CommRecyclerAdapter<RecommendFriends>(mContext, R.layout.item_contact_addfriend) {
            @Override
            public void convert(CommRecyclerViewHolder viewHolder, RecommendFriends mainMessage) {
//                {"UserId":"cb510cdf98ca4b9f9082051bf7190ff4",
//                        "PhotoUrl":"http://xzxj.oss-cn-shanghai.aliyuncs.com/user/c7f13738-1768-406d-a914-53278f2a0835x480.jpg","Gender":0,"Name":"噜噜","Description":null},

                //对对应的View进行赋值
                viewHolder.setText(R.id.contract_title, mainMessage.getName());
                SimpleDraweeView portal_news_img = (SimpleDraweeView) viewHolder.getView(R.id.news_label_pic);
                portal_news_img.setImageURI(Uri.parse(mainMessage.getPhotoUrl()));
//                if(!(mainMessage.getDescription() + "").equals("null")) {
                viewHolder.setText(R.id.contact_userinfo, (mainMessage.getDescription() + "").equals("null") ? "这人很懒，什么都没写" : mainMessage.getDescription() + "");
//                }


                viewHolder.setText(R.id.contact_add, mainMessage.isAddState() == true ? "已添加" : "添加");
                final String tempCurrentClickfriendId = mainMessage.getUserId();
                final boolean state = mainMessage.isAddState();
                viewHolder.getView(R.id.contact_add).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        if (!state) {
                            new EaseAlertDialog(mContext, null, "关注TA可查看并接收TA的全部动态，确定关注TA？", null, new EaseAlertDialog.AlertDialogUser() {
                                @Override
                                public void onResult(boolean confirmed, Bundle bundle) {
                                    if (confirmed) {
                                        currentClickfriendId = tempCurrentClickfriendId;
                                        try {
                                            JSONObject json = new JSONObject();
                                            json.put("userId", tempCurrentClickfriendId);// WowuApp.UserId
                                            loadDataService.loadPostJsonRequestData(WowuApp.JSON, WowuApp.FocusFriendsURL + "?userId=" + tempCurrentClickfriendId, json.toString(), R.id.contact_add);//
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        ((TextView) v.findViewById(R.id.contact_add)).setText("已添加");
                                    }
                                }
                            }, true).show();
                            //发送一句对话给他，让他去关注你
//                    发送文本消息
//创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此
                            EMMessage message = EMMessage.createTxtSendMessage(WowuApp.Name + "关注了你哦", tempCurrentClickfriendId);
                            EMClient.getInstance().chatManager().sendMessage(message);
                        } else {
                            MyToast.show(mContext, "已经添加");
                        }


                    }
                });
            }

            @Override
            public int getItemViewType(int position) {
                return super.getItemViewType(position);
            }
        };
        //设置item点击事件
//        messageRAdapter.setOnItemClick(new CommRecyclerAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//
//
////                Intent intent2 = new Intent(mContext, NewsOneDetailActivity.class);
////                //        intent2.putExtra("content", newsMessagelist.get(tempPosition-1).getContent());
////                intent2.putExtra("url", DistApp.serverAbsolutePath + "/snews!mobileNewsdetail.action?news.id=4028816f4d4be502014d4c0e22dc003d");
////                intent2.putExtra("name", "消息通知");
////                startActivity(intent2);
////                mContext.overridePendingTransition(0, 0);
//            }
//        });
        return null;
    }


    @Override
    public void loadServerData(String response, int flag) {
        switch (flag) {
            case LOAD_DATA:
//                Log.i("获取好友推荐列表", "：：" + response);

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

            case R.id.contact_add:
                MyToast.show(mContext, "添加成功");

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //将已经添加过的人员进行标记，更新列表
                        for (int i = 0; i < RecommendFriends_List.size(); ) {
                            if (RecommendFriends_List.get(i).getUserId().equals(currentClickfriendId)) {
                                RecommendFriends temp = RecommendFriends_List.get(i);
                                temp.setAddState(true);
                                RecommendFriends_List.remove(i);
                                RecommendFriends_List.add(i, temp);
                                Log.i(TAG, "添加好友找到了对应的行");
                                Message msg = new Message();
                                msg.what = REFRESH_VIEW;
                                mtotalHandler.sendMessage(msg);
                                return;
                            }
                        }
                    }
                }).start();
                break;
              case 200:
                MyToast.show(mContext,"进入了分享");
                com.tencent.mm.sdk.modelmsg. WXWebpageObject webpage = new com.tencent.mm.sdk.modelmsg.WXWebpageObject();
                webpage.webpageUrl = WowuApp.shareURL;
                WXMediaMessage msg = new WXMediaMessage(webpage);
                msg.title ="快和我一起加入先知先觉，发现更多附近新奇";
                msg.description = "我在先知先觉，先知号："+WowuApp.XianZhiNumber;
//            Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.icon);


                byte[] bitmapArray;
                bitmapArray = Base64.decode( response, Base64.DEFAULT);
                Bitmap thumb =   BitmapFactory.decodeByteArray(bitmapArray, 0,
                        bitmapArray.length);

                msg.thumbData =  UtilsTool.bmpToByteArray(thumb, true);
                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction =  "transaction"+System.currentTimeMillis(); // transaction字段用于唯一标识一个请求
                req.message = msg;
                req.scene =flag;
                wxApi.sendReq(req);
                break;

        }
    }

    @Override
    public void loadDataFailed(String response, int flag) {
        MyToast.show(mContext, response);
    }


    public boolean loadMore() {
        return false;
    }

    private void showWeiXinShareDialog() {
        View view = this.getLayoutInflater().inflate(R.layout.fragement_contact_weixinadd_pop, null);
        final Dialog dialog = new Dialog(mContext, R.style.transparentFrameWindowStyle);
        dialog.setContentView(view, new ViewGroup.LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();

        view.findViewById(R.id.home_member_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO 自动生成的方法存根
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.rt_weixin_pyq).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO 自动生成的方法存根
                wechatShare(scenePengYouQuan);
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.rt_weixin_hy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO 自动生成的方法存根
                wechatShare(sceneHaoYou);
                dialog.dismiss();
            }
        });

        // 设置显示动画
        window.setWindowAnimations(R.style.main_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = this.getWindowManager().getDefaultDisplay().getHeight();
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        // 设置显示位置
        dialog.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }


    private void showQQShareDialog() {
        View view = this.getLayoutInflater().inflate(R.layout.fragement_contact_qqadd_pop, null);
        final Dialog dialog = new Dialog(mContext, R.style.transparentFrameWindowStyle);
        dialog.setContentView(view, new ViewGroup.LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();

        view.findViewById(R.id.home_member_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO 自动生成的方法存根
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.rt_qq_kj).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO 自动生成的方法存根
                shareToQzone ();
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.rt_qq_hy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO 自动生成的方法存根
                onClickQQShare();
                dialog.dismiss();
            }
        });
        // 设置显示动画
        window.setWindowAnimations(R.style.main_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = this.getWindowManager().getDefaultDisplay().getHeight();
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        // 设置显示位置
        dialog.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }



    /**
     * 微信分享 （这里仅提供一个分享网页的示例，其它请参看官网示例代码）
     *
     * @param  (:分享到微信好友，1：分享到微信朋友圈)
     */
    private void wechatShare(int flag ) {
        if (!wxApi.isWXAppSupportAPI()) {
            MyToast.show(mContext, "当前版本不支持分享功能");
            return;
        }

        //这里替换一张自己工程里的图片资源
//        Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.share_logo);
//        msg.setThumbImage(thumb);
//        SendMessageToWX.Req req = new SendMessageToWX.Req();
//        req.transaction = String.valueOf(System.currentTimeMillis());
//        req.message = msg;
//        req.scene = flag==0?SendMessageToWX.Req.WXSceneSession:SendMessageToWX.Req.WXSceneTimeline;
//        wxApi.sendReq(req);


/*
爆出异常，暂时屏蔽，以后找到原因开启！！！！！！！！！！！！！！
//为了分享时将用户的头像分享出去做的设置，
        loadDataService.loadGetJsonRequestData(mSettings.getString("iconPath","http//#"),200);
        <Code>InvalidArgument</Code>
                08-08 23:26:15.962 615-615/im.wuwo.com.wuwo W/System.err:   <Message>Authorization header is invalid.</Message>
                08-08 23:26:15.962 615-615/im.wuwo.com.wuwo W/System.err:   <RequestId>57A8A496E442C7CF1D8E902B</RequestId>
                08-08 23:26:15.962 615-615/im.wuwo.com.wuwo W/System.err:   <HostId>xzxj.oss-cn-shanghai.aliyuncs.com</HostId>
                08-08 23:26:15.962 615-615/im.wuwo.com.wuwo W/System.err:   <Authorization>Bearer 87917de3135e4feeb103ab0ac637603d5439d75a43ae41e0ad603b6c78ea42e0</Authorization>
                08-08 23:26:15.962 615-615/im.wuwo.com.wuwo W/System.err: </Error>
        */

//       由于上面的接口无法用，暂时用这个代替
        com.tencent.mm.sdk.modelmsg. WXWebpageObject webpage = new com.tencent.mm.sdk.modelmsg.WXWebpageObject();
        webpage.webpageUrl = WowuApp.shareURL;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title ="快和我一起加入先知先觉，发现更多附近新奇";
        msg.description = "我在先知先觉，先知号："+WowuApp.XianZhiNumber;
        Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
        msg.thumbData =  UtilsTool.bmpToByteArray(thumb, true);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction =  "transaction"+System.currentTimeMillis(); // transaction字段用于唯一标识一个请求
        req.message = msg;
        req.scene =flag;
        wxApi.sendReq(req);
    }




    private void shareToQzone () {
        //分享类型
        final Bundle params = new Bundle();
        params.putString(QzoneShare.SHARE_TO_QZONE_KEY_TYPE,QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT+"" );
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, "快和我一起加入先知先觉，发现更多附近新奇");//必填
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY,"我在先知先觉，先知号："+WowuApp.XianZhiNumber );//选填
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, WowuApp.shareURL);//必填

        ArrayList<String> strList=new ArrayList<String>();
        strList.add( mSettings.getString("iconPath","http//#"));
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL,strList);
        params.putString(QzoneShare.SHARE_TO_QQ_IMAGE_URL, mSettings.getString("iconPath","http//#"));

        mTencent.shareToQzone(AddFriendActivity.this, params, new BaseUiListener());
    }


    private void onClickQQShare() {
        final Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, "快和我一起加入先知先觉，发现更多附近新奇");
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY,  "我在先知先觉，先知号："+WowuApp.XianZhiNumber);
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL,  WowuApp.shareURL);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, mSettings.getString("iconPath","http//#"));//WowuApp.iconPath
//        params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, mSettings.getString("iconPath","http//#"));//WowuApp.iconPath
//
//        "http://imgcache.qq.com/music/photo/mid_album_300/V/E/000J1pJ50cDCVE.jpg"  http://xzxj.oss-cn-shanghai.aliyuncs.com/user/35b5090b-aaf4-4c6d-b46f-e42073e11f4ex128.jpg
//        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,"http://img3.douban.com/lpic/s3635685.jpg" );


        params.putString(QQShare.SHARE_TO_QQ_APP_NAME,  "先知先觉");
//        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT,  "其他附加功能");
        if(mTencent!=null){
//            mTencent.shareToQQ(mContext, params, new BaseUiListener());
            mTencent.shareToQQ(AddFriendActivity.this, params, new BaseUiListener());

        }else{
            MyToast.show(mContext,"初始化失败！！！");
        }

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mTencent.onActivityResult(requestCode, resultCode, data);
    }






    private class BaseUiListener implements IUiListener {
        @Override
        public void onComplete(Object response) {
            //V2.0版本，参数类型由JSONObject 改成了Object,具体类型参考api文档
//            mBaseMessageText.setText("onComplete:");
//            doComplete(response);
            MyToast.show(mContext,response.toString());
        }
        protected void doComplete(JSONObject values) {
        }
        @Override
        public void onError(UiError e) {
            MyToast.show(mContext, "code:" + e.errorCode + ", msg:"
                    + e.errorMessage + ", detail:" + e.errorDetail);
//            showResult("onError:", "code:" + e.errorCode + ", msg:"
//                    + e.errorMessage + ", detail:" + e.errorDetail);
        }
        @Override
        public void onCancel() {
//            showResult("onCancel", "");
            MyToast.show(mContext,"onCancel");
        }
    }


















}

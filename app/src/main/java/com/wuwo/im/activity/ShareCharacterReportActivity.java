package com.wuwo.im.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import com.wuwo.im.bean.Characters;
import com.wuwo.im.config.WowuApp;
import com.wuwo.im.util.MyToast;
import com.wuwo.im.util.UtilsTool;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.service.LoadserverdataService;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import im.imxianzhi.com.imxianzhi.R;

public class ShareCharacterReportActivity extends BaseLoadActivity {

    private TextView tv_username, tv_character_type, tv_character_tonglei;
    public static final int LOAD_DATA = 5;
    public Characters mCharacter = null;
    private mHandlerWeak mtotalHandler;
    private SimpleDraweeView news_label_pic;
    private IWXAPI wxApi;
    private int scenePengYouQuan = SendMessageToWX.Req.WXSceneTimeline;
    private int sceneHaoYou = SendMessageToWX.Req.WXSceneSession;
    private SharedPreferences mSettings;
    private Tencent mTencent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_character_report);
//        mContext=ShareCharacterReportActivity.this;
        mSettings = getSharedPreferences(WowuApp.PREFERENCE_KEY,
                android.content.Context.MODE_PRIVATE);
        loadDataService = new LoadserverdataService(this);
        mtotalHandler = new mHandlerWeak(this);
        initView();
    }

    private void initView() {
        ((TextView) findViewById(R.id.top_title)).setText("分享我的性格报告");
        findViewById(R.id.return_back).setOnClickListener(this);

        tv_username = (TextView) findViewById(R.id.tv_username);
        tv_character_type = (TextView) findViewById(R.id.tv_character_type);
        tv_character_tonglei = (TextView) findViewById(R.id.tv_character_tonglei);

        news_label_pic = (SimpleDraweeView) findViewById(R.id.news_label_pic);
//        if (WowuApp.iconPath != null && !WowuApp.iconPath.equals("")) {
//            news_label_pic.setImageURI(Uri.parse(WowuApp.iconPath));
//        }
        tv_username.setText(WowuApp.Name);
        mSettings = mContext.getSharedPreferences(WowuApp.PREFERENCE_KEY,
                android.content.Context.MODE_PRIVATE);


        wxApi = WXAPIFactory.createWXAPI(mContext, WowuApp.WeChat_APP_ID, false);
        wxApi.registerApp(WowuApp.WeChat_APP_ID);
        // 其中APP_ID是分配给第三方应用的appid，类型为String。
        mTencent = Tencent.createInstance(WowuApp.QQ_APP_ID, mContext.getApplicationContext());

        findViewById(R.id.rt_share_friend).setOnClickListener(this);
        findViewById(R.id.rt_share_friend_circle).setOnClickListener(this);
        findViewById(R.id.rt_share_qq).setOnClickListener(this);
        findViewById(R.id.rt_share_friend_qqzeng).setOnClickListener(this);

        loadData();
    }


    private void loadData() {
        loadDataService.loadGetJsonRequestData(WowuApp.GetDispositionInfoURL, LOAD_DATA);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.return_back:
                this.finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.rt_share_friend:
                wechatShare(sceneHaoYou);//分享到微信朋友圈
                break;
            case R.id.rt_share_friend_circle:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        wechatShare(scenePengYouQuan);
                    }
                }).start();
                break;
            case R.id.rt_share_qq:
                onClickQQShare();
                break;
            case R.id.rt_share_friend_qqzeng:
                shareToQzone();
                break;
        }
    }

    private static class mHandlerWeak extends Handler {
        private WeakReference<ShareCharacterReportActivity> activity = null;

        public mHandlerWeak(ShareCharacterReportActivity act) {
            super();
            this.activity = new WeakReference<ShareCharacterReportActivity>(act);
        }

        @Override
        public void handleMessage(Message msg) {
            ShareCharacterReportActivity act = activity.get();
            if (null == act) {
                return;
            }
            switch (msg.what) {
                // 正在下载
                case LOAD_DATA:
                    if (act.mCharacter != null) {
                        if (act.mCharacter.getIconUrl() != null) {
                            act.news_label_pic.setImageURI(Uri.parse(act.mCharacter.getIconUrl()));
                        }
                        act.tv_character_tonglei.setText(act.mCharacter.getCelebrity());
                    }
                    act.tv_character_type.setText(act.mCharacter.getName() + act.mCharacter.getTitle());
                    break;
            }
        }
    }

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

    /**
     * 微信分享 （这里仅提供一个分享网页的示例，其它请参看官网示例代码）
     *
     * @param (:分享到微信好友，1：分享到微信朋友圈)
     */
    private void wechatShare(final int flag) {
        if (!wxApi.isWXAppSupportAPI()) {
            Message msg = new Message();
            msg.what = LoadingError;
            mHandler.sendMessage(msg);
            return;
        }

///*        params.putString(QQShare.SHARE_TO_QQ_TITLE, "快和我一起加入先知先觉，发现更多附近新奇");
//        params.putString(QQShare.SHARE_TO_QQ_SUMMARY,  "我在先知先觉，先知号："+WowuApp.XianZhiNumber);
//        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL,  "http://www.baidu.com");
//        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, WowuApp.iconPath);
//        params.putString(QQShare.SHARE_TO_QQ_APP_NAME,  "先知先觉");*/
//
//        // 初始化一个WXTextObject对象
//        WXTextObject textObj = new WXTextObject();
//        textObj.text = "快和我一起加入先知先觉，发现更多附近新奇";
//        // 用WXTextObject对象初始化一个WXMediaMessage对象
//        WXMediaMessage msg = new WXMediaMessage();
//        msg.mediaObject = textObj;
//        // 发送文本类型的消息时，title字段不起作用
//        // msg.title = "Will be ignored";
//        msg.description = "我在先知先觉，先知号："+WowuApp.XianZhiNumber;
//
//        // 构造一个Req
//        SendMessageToWX.Req req = new SendMessageToWX.Req();
//        req.transaction = "transaction"+System.currentTimeMillis(); // transaction字段用于唯一标识一个请求
//        req.message = msg;
//        req.scene = flag;
//
//        // 调用api接口发送数据到微信
//        wxApi.sendReq(req);
////        finish();


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


        if (mSettings.getString("characterUrl", null) != null) {
            OkHttpUtils
                    .get()//
                    .url(mSettings.getString("characterUrl", null))//
                    .build()//
                    .execute(new BitmapCallback() {
                        @Override
                        public void onError(Request request, Exception e) {
                            startShareToWX(flag, BitmapFactory.decodeResource(getResources(), R.drawable.icon));
                        }

                        @Override
                        public void onResponse(Bitmap bitmap) {
                            startShareToWX(flag, bitmap);
                        }
                    });
        } else {
            loadData();
        }


    }

    private void startShareToWX(int flag, Bitmap thumb) {
        //       由于上面的接口无法用，暂时用这个代替
        com.tencent.mm.sdk.modelmsg.WXWebpageObject webpage = new com.tencent.mm.sdk.modelmsg.WXWebpageObject();
        webpage.webpageUrl = "http://weixin.imxianzhi.com/Share/UserInfo?userId=" + WowuApp.UserId + "&from=timeline&isappinstalled=1";//WowuApp.shareURL;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = flag == scenePengYouQuan ? "我的性格类型是" + mCharacter.getName() + mCharacter.getTitle() + "," + mCharacter.getCelebrity() + "是我的性格同类" : "一起三观配，让我发现你的美，你人美心更美。#先知有三观配，我在先知先觉等你#";//
        msg.description = "一起三观配，让我发现你的美，你人美心更美。#先知有三观配，我在先知先觉，先知号：" + WowuApp.XianZhiNumber;
//          thumb = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
//        Bitmap thumb =find_share_f_ic7.getDrawingCache();


        msg.thumbData = UtilsTool.bmpToByteArray(thumb, true);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = "transaction" + System.currentTimeMillis(); // transaction字段用于唯一标识一个请求
        req.message = msg;
        req.scene = flag;
        wxApi.sendReq(req);
    }


    private void onClickQQShare() {
        final Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, "我的性格类型是" + mCharacter.getName() + mCharacter.getTitle() + "," + mCharacter.getCelebrity() + "是我的性格同类");//"一起三观配，让我发现你的美，你人美心更美。#先知有三观配，我在先知先觉等你#");
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, "我在先知先觉，先知号：" + WowuApp.XianZhiNumber);//   "我的性格类型是"+mCharacter.getTitle()+","+mCharacter.getName()+"是我的性格同类"
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, "http://weixin.imxianzhi.com/Share/UserInfo?userId=" + WowuApp.UserId + "&from=timeline&isappinstalled=1");//WowuApp.shareURL;  WowuApp.shareURL);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, mSettings.getString("iconPath", "http//#"));//WowuApp.iconPath
//        params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, mSettings.getString("iconPath","http//#"));//WowuApp.iconPath
//
//        "http://imgcache.qq.com/music/photo/mid_album_300/V/E/000J1pJ50cDCVE.jpg"  http://xzxj.oss-cn-shanghai.aliyuncs.com/user/35b5090b-aaf4-4c6d-b46f-e42073e11f4ex128.jpg
//        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,"http://img3.douban.com/lpic/s3635685.jpg" );


        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "先知先觉");
//        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT,  "其他附加功能");
        if (mTencent != null) {
//            mTencent.shareToQQ(mContext, params, new BaseUiListener());
            mTencent.shareToQQ(ShareCharacterReportActivity.this, params, new BaseUiListener());

        } else {
            MyToast.show(mContext, "初始化失败！！！");
        }

    }

    private void shareToQzone() {
//        　　//分享类型
//        　　params.putString(QzoneShare.SHARE_TO_QQ_KEY_TYPE,SHARE_TO_QZONE_TYPE_IMAGE_TEXT );
//        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, "标题");//必填
//        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, "摘要");//选填
//        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, "跳转URL");//必填
//        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, "图片链接ArrayList");
//        mTencent.shareToQzone(activity, params, new BaseUiListener());

        //分享类型
        final Bundle params = new Bundle();
        params.putString(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT + "");
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, "一起三观配，让我发现你的美，你人美心更美。#先知有三观配，我在先知先觉等你#");//必填  mCharacter!=null?"我的性格类型是"+mCharacter.getName()+mCharacter.getTitle()+","+mCharacter.getCelebrity()+"是我的性格同类":
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, "一起三观配，让我发现你的美，你人美心更美。#先知有三观配，我在先知先觉，先知号：" + WowuApp.XianZhiNumber);//选填
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, WowuApp.shareURL);//必填

        ArrayList<String> strList = new ArrayList<String>();
        strList.add(WowuApp.iconPath.equals("") == true ? mSettings.getString("iconPath", "http//#") : WowuApp.iconPath);
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, strList);
//        params.putString(QzoneShare.SHARE_TO_QQ_IMAGE_URL,WowuApp.iconPath.equals("")==true?mSettings.getString("iconPath", "http//#"):WowuApp.iconPath);

        mTencent.shareToQzone(ShareCharacterReportActivity.this, params, new BaseUiListener());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mTencent.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void loadServerData(String response, int flag) {
        Gson gson = new GsonBuilder().create();
        java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<Characters>() {
        }.getType();
        switch (flag) {
            case LOAD_DATA:
                mCharacter = gson.fromJson(response, type);
                Message msg = new Message();
                msg.what = LOAD_DATA;
                msg.obj = response;
                mtotalHandler.sendMessage(msg);
                break;
        }
    }

    private class BaseUiListener implements IUiListener {
        @Override
        public void onComplete(Object response) {
            //V2.0版本，参数类型由JSONObject 改成了Object,具体类型参考api文档
//            mBaseMessageText.setText("onComplete:");
//            doComplete(response);
//            MyToast.show(mContext,response.toString());
        }

        protected void doComplete(JSONObject values) {
        }

        @Override
        public void onError(UiError e) {
//            MyToast.show(mContext, "code:" + e.errorCode + ", msg:"
//                    + e.errorMessage + ", detail:" + e.errorDetail);
//            showResult("onError:", "code:" + e.errorCode + ", msg:"
//                    + e.errorMessage + ", detail:" + e.errorDetail);
        }

        @Override
        public void onCancel() {
//            showResult("onCancel", "");
//            MyToast.show(mContext,"onCancel");
        }
    }

    @Override
    public void loadDataFailed(String response, int flag) {


    }
}

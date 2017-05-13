package com.wuwo.im.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.hyphenate.chatuidemo.Constant;
import com.net.grandcentrix.tray.AppPreferences;
import com.squareup.okhttp.Request;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.wuwo.im.config.ExitApp;
import com.wuwo.im.config.WowuApp;
import com.wuwo.im.util.LogUtils;
import com.wuwo.im.util.MyToast;
import com.wuwo.im.util.UtilsTool;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;

import org.json.JSONException;
import org.json.JSONObject;

import im.imxianzhi.com.imxianzhi.R;

/**
 * desc 性格测试结果
 *
 * @author 王明远
 * @日期： 2016/6/24 22:53
 * @版权:Copyright All rights reserved.
 */

public class CharacterTResultActivity extends BaseLoadActivity {
    private Context mContext = this;
    private SimpleDraweeView portal_news_img;
    private TextView tv_user_type, tv_user_typeintro, tv_user_typename;
    private IWXAPI wxApi;
    private boolean  registerMode=false;
    SharedPreferences mSettings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ExitApp.getInstance().addOpenedActivity(this);
        setContentView(R.layout.activity_character_tresult);
        refresh();
        initView();
        if(getIntent()!=null){
            registerMode =  getIntent().getBooleanExtra("registerMode",false);
        }
        wxApi = WXAPIFactory.createWXAPI(mContext, WowuApp.WeChat_APP_ID, false);
        wxApi.registerApp(WowuApp.WeChat_APP_ID);

        mSettings = mContext.getSharedPreferences(WowuApp.PREFERENCE_KEY,
                android.content.Context.MODE_PRIVATE);
    }

    private void refresh() {
        loadDataService.loadGetJsonRequestData(WowuApp.GetDispositionInfoURL, R.id.bt_jingque);
    }

    private void initView() {
        findViewById(R.id.tv_user_types_enter).setOnClickListener(this);
        findViewById(R.id.tv_user_typeshare).setOnClickListener(this);

        pg = UtilsTool.initProgressDialog(mContext, "正在连接.....");
        pg.show();

        tv_user_type = (TextView) findViewById(R.id.tv_user_type);
        tv_user_typeintro = (TextView) findViewById(R.id.tv_user_typeintro);
        tv_user_typename = (TextView) findViewById(R.id.tv_user_typename);

        portal_news_img = (SimpleDraweeView) findViewById(R.id.user_label_pic);
//        portal_news_img.setImageURI(Uri.parse("http://www.gog.com.cn/pic/0/10/91/11/10911138_955870.jpg"));

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_user_types_enter:
                if(registerMode){
                    SharedPreferences.Editor editor = mSettings.edit();
//                    editor.clear();
//                    editor.commit();
                    editor.putString("PhoneNumber", WowuApp.PhoneNumber);
                    editor.putString("Password", WowuApp.Password);
                    editor.commit();

                    Intent intent2 = new Intent(mContext, LoadingActivity.class);
                    startActivity(intent2);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    this.finish();
                }else{
                    Intent  intent2 = new Intent(mContext, MainActivity.class);
                    intent2.putExtra(Constant.RETEST_CHARACTER,true);
                    startActivity(intent2);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                }

                break;
            case R.id.tv_user_typeshare:
                wechatShare(SendMessageToWX.Req.WXSceneTimeline);
                break;
        }

    }

    @Override
    public void loadServerData(String response, int flag) {
        switch (flag) {
            case R.id.bt_jingque:
                LogUtils.i("test result:返回的结果为：：：：", response);

                Message msg = Message.obtain();
                msg.what = REFRESH;
                msg.obj = response;
                mHandler.sendMessage(msg);





                break;
        }
    }


    @Override
    public void loadDataFailed(String response, int flag) {
        MyToast.show(mContext, response);
    }


    private final int REFRESH = 1;
    private ProgressDialog pg;
    private final int Loading = 3;
    String photoUrl;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Loading:
                    pg = UtilsTool.initProgressDialog(mContext, "正在连接.....");
                    pg.show();
                    break;
                case REFRESH:
                    if (pg != null && pg.isShowing()) pg.dismiss();
/*            {"PhotoUrl":"http://xzxj.oss-cn-shanghai.aliyuncs.com/celebrity/fd3d8302-ba1c-4798-bdf8-d9c29e7c519b.jpg",
                    "IconUrl":"http://xzxj.oss-cn-shanghai.aliyuncs.com/celebrity/0224bdd1-b880-4691-ab5f-568321e5dd14.jpg",
                    "Celebrity":"迈克尔•杰克逊","CelebrityDescription":"美国流行音乐之王","DispositionId":"d9d1a6a2-0b7e-4063-814e-26beb081a078","Name":"ISFP","Title":"艺术家",
                    "Score":{"UserId":"637e5acb638f46f5873ec86f0b4b49ce","E":10,"I":11,"S":15,"N":11,"T":11,"F":13,"J":10,"P":12,"CreateOn":"2016-12-18T21:15:44.127","IsCurrent":true,
                    "DispositionId":"d9d1a6a2-0b7e-4063-814e-26beb081a078","QuestionType":2,"PropensityScore":9.0,"EI_PropensityScore":5.0,
                    "SN_PropensityScore":-15.0,"TF_PropensityScore":8.0,"JP_PropensityScore":9.0,"PropensityDescription":"轻微","CreateOnString":"2016/12/18","Id":"86f834e2-61cd-4d93-b481-bbd1bfde5dcc"}}   */
                    try {
                        JSONObject json = new JSONObject(msg.obj.toString());
                       if(json.optString("PhotoUrl",null) !=null &&  !json.optString("PhotoUrl","").equals("")){
                           portal_news_img.setImageURI(Uri.parse(json.optString("PhotoUrl", "http://#")));//http://www.gog.com.cn/pic/0/10/91/11/10911138_955870.jpg
                       }
                        photoUrl=json.optString("PhotoUrl", "http://#");
                        tv_user_type.setText(json.optString("Name") + " " + json.optString("Title"));
                        tv_user_typeintro.setText(json.optString("CelebrityDescription"));
                        tv_user_typename.setText(json.optString("Celebrity"));


//                        JSONObject score = new JSONObject(json.optString("Score",""));
                        //当用户性格改变后用此方法可以刷新主界面
                        AppPreferences appPreferences = new AppPreferences(mContext.getApplicationContext());
                        appPreferences.put("characterType",json.optString("Name","")+" "+json.optString("Title",""));
                        appPreferences.put("characterChanged",true);
                        try {
                            JSONObject json2 = new JSONObject();
                            json2.put("historyId",json.optString("DispositionId",""));//APP版本号historyCharacterTestList.get(CurrentSelect).getId()
                            loadDataService.loadPostJsonRequestData(WowuApp.JSON,WowuApp.SetDispositionFromHistoryURL+"?historyId="+json.optString("DispositionId",""),json2.toString(),61218);//historyCharacterTestList.get(CurrentSelect).getId()
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }



                    break;
            }
            super.handleMessage(msg);
        }
    };



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

        if(photoUrl!=null) {
            OkHttpUtils
                    .get()//
                    .url(photoUrl)//
                    .build()//
                    .execute(new BitmapCallback() {
                        @Override
                        public void onError(Request request, Exception e) {
                            startShareToWX(BitmapFactory.decodeResource(getResources(), R.drawable.icon));
                        }

                        @Override
                        public void onResponse(Bitmap bitmap) {
                            startShareToWX(bitmap);
                        }
                    });
        }else{
            MyToast.show(mContext,"返回结果异常，请重试");
            refresh();
        }

    }


    private void startShareToWX(Bitmap thumb) {
        //       由于上面的接口无法用，暂时用这个代替
        com.tencent.mm.sdk.modelmsg. WXWebpageObject webpage = new com.tencent.mm.sdk.modelmsg.WXWebpageObject();
        webpage.webpageUrl = WowuApp.shareURL;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title ="我的性格类型是"+tv_user_type.getText().toString()+","+tv_user_typename.getText().toString()+"是我的性格同类";//"一起三观配，让我发现你的美，你人美心更美。#先知有三观配，我在先知先觉等你#";
        msg.description = "我在先知先觉，先知号："+WowuApp.XianZhiNumber;
//          thumb = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
//        Bitmap thumb =find_share_f_ic7.getDrawingCache();


        msg.thumbData =   UtilsTool.bmpToByteArray(thumb, true);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction =  "transaction"+System.currentTimeMillis(); // transaction字段用于唯一标识一个请求
        req.message = msg;
        req.scene =SendMessageToWX.Req.WXSceneTimeline;
        wxApi.sendReq(req);
    }


}

package com.wuwo.im.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.wuwo.im.config.ExitApp;
import com.wuwo.im.config.WowuApp;
import com.wuwo.im.util.MyToast;
import com.wuwo.im.util.UtilsTool;

import org.json.JSONException;
import org.json.JSONObject;

import im.wuwo.com.wuwo.R;

/**
 * desc 性格测试结果
 *
 * @author 王明远
 * @日期： 2016/6/24 22:53
 * @版权:Copyright All rights reserved.
 */

public class CharacterTResultActivity extends BaseLoadActivity {
    Context mContext = this;
    SimpleDraweeView portal_news_img;
    TextView tv_user_type, tv_user_typeintro, tv_user_typename;
    IWXAPI wxApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ExitApp.getInstance().addOpenedActivity(this);
        setContentView(R.layout.activity_character_tresult);
        loadDataService.loadGetJsonRequestData(WowuApp.GetDispositionInfoURL, R.id.bt_jingque);
        initView();

        wxApi = WXAPIFactory.createWXAPI(mContext, WowuApp.WeChat_APP_ID, false);
        wxApi.registerApp(WowuApp.WeChat_APP_ID);
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
                Intent intent2 = new Intent(mContext, MainActivity.class);
                startActivity(intent2);
                this.finish();
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
                Log.d("test result:返回的结果为：：：：", response);
//             {"PhotoUrl":null,"Celebrity":"莎士比亚","CelebrityDescription":"英国文学史上最杰出的戏剧家","Name":"INFP","Title":"化解者"}
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
//                 {"PhotoUrl":null,"Celebrity":"莎士比亚","CelebrityDescription":"英国文学史上最杰出的戏剧家","Name":"INFP","Title":"化解者"}
                    try {
                        JSONObject json = new JSONObject(msg.obj.toString());
                        portal_news_img.setImageURI(Uri.parse(json.optString("PhotoUrl", "http://#")));//http://www.gog.com.cn/pic/0/10/91/11/10911138_955870.jpg
                        tv_user_type.setText(json.optString("Name") + " " + json.optString("Title"));
                        tv_user_typeintro.setText(json.optString("CelebrityDescription"));
                        tv_user_typename.setText(json.optString("Celebrity"));
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


//       由于上面的接口无法用，暂时用这个代替
        com.tencent.mm.sdk.modelmsg. WXWebpageObject webpage = new com.tencent.mm.sdk.modelmsg.WXWebpageObject();
        webpage.webpageUrl = WowuApp.shareURL;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title ="快和我一起加入先知先觉，发现更多附近新奇";
        msg.description = "我的测试性格是："+tv_user_typeintro.getText().toString();
        Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
        msg.thumbData =   UtilsTool.bmpToByteArray(thumb, true);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction =  "transaction"+System.currentTimeMillis(); // transaction字段用于唯一标识一个请求
        req.message = msg;
        req.scene =flag;
        wxApi.sendReq(req);
    }



}

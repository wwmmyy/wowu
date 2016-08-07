package com.wuwo.im.fragement;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.tencent.connect.share.QQShare;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.wuwo.im.activity.FeedbackActivity;
import com.wuwo.im.activity.MyCharacterResultActivity;
import com.wuwo.im.activity.VersionIntroActivity;
import com.wuwo.im.config.WowuApp;
import com.wuwo.im.util.MyToast;
import com.zhy.http.okhttp.service.LoadserverdataService;
import com.zhy.http.okhttp.service.loadServerDataListener;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

import im.wuwo.com.wuwo.R;

/**
 * 首页 设置 fragment
 *
 * @author dewyze
 */

@SuppressLint("ValidFragment")
public class Portal_FindFragment extends BaseAppFragment implements View.OnClickListener,loadServerDataListener {
    Activity mContext;
    SharedPreferences mSettings;
    SharedPreferences.Editor editor;
    private int mCount = 1;

    IWXAPI wxApi;
    int scenePengYouQuan = SendMessageToWX.Req.WXSceneTimeline;
    int sceneHaoYou = SendMessageToWX.Req.WXSceneSession;
    Tencent mTencent;
    LoadserverdataService loadDataService;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mContext = activity;
        mSettings = mContext.getSharedPreferences(WowuApp.PREFERENCE_KEY,
                android.content.Context.MODE_PRIVATE);
        loadDataService = new LoadserverdataService(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragement_find_main, container, false);
        initViews(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        initViews(view);
    }


    private void initViews(View view) {

        view.findViewById(R.id.user_info_detail).setOnClickListener(this);
        view.findViewById(R.id.find_share_f).setOnClickListener(this);
//        view.findViewById(R.id.find_share_friend).setOnClickListener(this);
        view.findViewById(R.id.find_inv_friend).setOnClickListener(this);
        view.findViewById(R.id.find_share_friendcircle).setOnClickListener(this);
        view.findViewById(R.id.find_version_will).setOnClickListener(this);
        view.findViewById(R.id.find_feedback).setOnClickListener(this);



        wxApi = WXAPIFactory.createWXAPI(mContext, WowuApp.WeChat_APP_ID, false);
        wxApi.registerApp(WowuApp.WeChat_APP_ID);


        // 其中APP_ID是分配给第三方应用的appid，类型为String。
        mTencent = Tencent.createInstance(WowuApp.QQ_APP_ID, mContext.getApplicationContext());

    }


    @Override
    public String getFragmentName() {
        return "test_FindFragement";
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_info_detail:
                Intent temp1Intent=new Intent(mContext, MyCharacterResultActivity.class);
                startActivity(temp1Intent);
                mContext.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);


                break;
            case R.id.find_share_f:
                showSharefDialog();
                break;
            case R.id.find_inv_friend:
                showShareInviteDialog();
                break;

            case R.id.find_share_friendcircle:
//                showSanguanPickDialog();
                wechatShare(scenePengYouQuan);
                 break;
            case R.id.find_version_will:
                Intent temp2Intent=new Intent(mContext, VersionIntroActivity.class);
                startActivity(temp2Intent);
                mContext.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.find_feedback:
                Intent tempIntent=new Intent(mContext, FeedbackActivity.class);
                startActivity(tempIntent);
                mContext.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
        }
    }


    private void showSharefDialog() {
        View view = mContext.getLayoutInflater().inflate(R.layout.fragement_find_sharef_pop, null);
        final Dialog dialog = new Dialog(mContext, R.style.transparentFrameWindowStyle);
        dialog.setContentView(view, new ViewGroup.LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();

        view.findViewById(R.id.share_f_weixin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                wechatShare(sceneHaoYou);//分享到微信朋友圈
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.share_f_contact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.share_f_qq).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
//                wechatShare(0);//分享到微信朋友圈
                onClickQQShare();
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.home_member_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });

        // 设置显示动画
        window.setWindowAnimations(R.style.main_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = mContext.getWindowManager().getDefaultDisplay().getHeight();
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
     * 用户三观配显示，该模块后面要移到会话模块，暂时在这里测试
     */
    private void showSanguanPickDialog() {
        View view = mContext.getLayoutInflater().inflate(R.layout.dialog_sanguan_pick, null);
        final Dialog dialog = new Dialog(mContext, R.style.transparentFrameWindowStyle);
        dialog.setContentView(view, new ViewGroup.LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();
        view.findViewById(R.id.iv_sanguan_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                 dialog.dismiss();
            }
        });



        // 设置显示动画
        window.setWindowAnimations(R.style.main_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = mContext.getWindowManager().getDefaultDisplay().getHeight();
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.MATCH_PARENT ;//WRAP_CONTENT

        // 设置显示位置
        dialog.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }



    private void showShareInviteDialog() {
        View view = mContext.getLayoutInflater().inflate(R.layout.fragement_find_share_invite_pop, null);
        final Dialog dialog = new Dialog(mContext, R.style.transparentFrameWindowStyle);
        dialog.setContentView(view, new ViewGroup.LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();
        view.findViewById(R.id.share_f_weixin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                wechatShare(sceneHaoYou);//分享到微信朋友圈
                dialog.dismiss();
            }
        });

        view.findViewById(R.id.share_f_contact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.share_f_qq).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
//                wechatShare(0);//分享到微信朋友圈
                onClickQQShare();
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.home_member_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });

        // 设置显示动画
        window.setWindowAnimations(R.style.main_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = mContext.getWindowManager().getDefaultDisplay().getHeight();
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


        //为了分享时将用户的头像分享出去做的设置，
        loadDataService.loadGetJsonRequestData(mSettings.getString("iconPath","http//#"),200);


//        com.tencent.mm.sdk.modelmsg. WXWebpageObject webpage = new com.tencent.mm.sdk.modelmsg.WXWebpageObject();
//        webpage.webpageUrl = WowuApp.shareURL;
//        WXMediaMessage msg = new WXMediaMessage(webpage);
//        msg.title ="快和我一起加入先知先觉，发现更多附近新奇";
//        msg.description = "我在先知先觉，先知号："+WowuApp.XianZhiNumber;
//        Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
//        msg.thumbData =  bmpToByteArray(thumb, true);
//        SendMessageToWX.Req req = new SendMessageToWX.Req();
//        req.transaction =  "transaction"+System.currentTimeMillis(); // transaction字段用于唯一标识一个请求
//        req.message = msg;
//        req.scene =flag;
//        wxApi.sendReq(req);
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
            mTencent.shareToQQ(mContext, params, new BaseUiListener());

        }else{
            MyToast.show(mContext,"初始化失败！！！");
        }

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mTencent.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void loadServerData(String response, int flag) {

        if(flag==200){
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

            msg.thumbData =  bmpToByteArray(thumb, true);
            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction =  "transaction"+System.currentTimeMillis(); // transaction字段用于唯一标识一个请求
            req.message = msg;
            req.scene =flag;
            wxApi.sendReq(req);
        }

    }

    @Override
    public void loadDataFailed(String response, int flag) {

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




    //图片压缩
    public static byte[] bmpToByteArray(final Bitmap bmp,
                                        final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        int WX_THUMB_SIZE = 120;
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, WX_THUMB_SIZE, WX_THUMB_SIZE, true);
        bmp.recycle();

        thumbBmp.compress(Bitmap.CompressFormat.PNG, 80, output);
        if (needRecycle) {
            thumbBmp.recycle();
        }
        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }



//    private boolean share(WXMediaMessage.IMediaObject mediaObject, String title, Bitmap thumb, String description, int scene) {
//        //初始化一个WXMediaMessage对象，填写标题、描述
//        WXMediaMessage msg = new WXMediaMessage(mediaObject);
//        if (title != null) {
//            msg.title = title;
//        }
//        if (description != null) {
//            msg.description = description;
//        }
//        if (thumb != null) {
//            msg.thumbData = WXUtil.bmpToByteArray(thumb, true);
//        }
//
//        //构造一个Req
//        SendMessageToWX.Req req = new SendMessageToWX.Req();
//        req.transaction = String.valueOf(System.currentTimeMillis());
//        req.message = msg;
//        req.scene = scene;
//        return wxApi.sendReq(req);
//    }


}

package com.wuwo.im.fragement;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.wuwo.im.activity.FeedbackActivity;
import com.wuwo.im.activity.MyCharacterResultActivity;
import com.wuwo.im.activity.VersionIntroActivity;
import com.wuwo.im.config.WowuApp;

import cn.sharesdk.wechat.utils.WXMediaMessage;
import cn.sharesdk.wechat.utils.WXWebpageObject;
import im.wuwo.com.wuwo.R;

/**
 * 首页 设置 fragment
 *
 * @author dewyze
 */

@SuppressLint("ValidFragment")
public class Portal_FindFragment extends BaseAppFragment implements View.OnClickListener {
    Activity mContext;
    SharedPreferences mSettings;
    SharedPreferences.Editor editor;
    private int mCount = 1;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mContext = activity;
        mSettings = mContext.getSharedPreferences(WowuApp.PREFERENCE_KEY,
                android.content.Context.MODE_PRIVATE);
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
                // TODO 自动生成的方法存根
                wechatShare(1);//分享到微信朋友圈
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.share_f_contact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO 自动生成的方法存根
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.share_f_qq).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO 自动生成的方法存根
                wechatShare(0);//分享到微信朋友圈
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.home_member_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO 自动生成的方法存根
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


    private void showShareInviteDialog() {
        View view = mContext.getLayoutInflater().inflate(R.layout.fragement_find_share_invite_pop, null);
        final Dialog dialog = new Dialog(mContext, R.style.transparentFrameWindowStyle);
        dialog.setContentView(view, new ViewGroup.LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();
        view.findViewById(R.id.share_f_weixin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                wechatShare(1);//分享到微信朋友圈
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
                wechatShare(0);//分享到微信朋友圈
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
     * @param flag(0:分享到微信好友，1：分享到微信朋友圈)
     */
    private void wechatShare(int flag) {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = "这里填写链接url";
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = "这里填写标题";
        msg.description = "这里填写内容";
        //这里替换一张自己工程里的图片资源
//        Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.share_logo);
//        msg.setThumbImage(thumb);

//        SendMessageToWX.Req req = new SendMessageToWX.Req();
//        req.transaction = String.valueOf(System.currentTimeMillis());
//        req.message = msg;
//        req.scene = flag==0?SendMessageToWX.Req.WXSceneSession:SendMessageToWX.Req.WXSceneTimeline;
//        wxApi.sendReq(req);
    }
}

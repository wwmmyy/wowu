package com.wuwo.im.fragement;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hyphenate.EMCallBack;
import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.chatuidemo.db.DemoDBManager;
import com.net.grandcentrix.tray.AppPreferences;
import com.wuwo.im.activity.BlackUserActivity;
import com.wuwo.im.activity.CharacterDetailActivity;
import com.wuwo.im.activity.OwnerInfoEditActivity;
import com.wuwo.im.activity.UserBindPhoneActivity;
import com.wuwo.im.activity.UserModifyPasswdActivity;
import com.wuwo.im.activity.UserPayActivity;
import com.wuwo.im.activity.UserPaySuccessActivity;
import com.wuwo.im.activity.UserSetWarnActivity;
import com.wuwo.im.activity.VisitedMeUsersActivity;
import com.wuwo.im.bean.Characters;
import com.wuwo.im.bean.UserInfoDetail;
import com.wuwo.im.config.ExitApp;
import com.wuwo.im.config.WowuApp;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.service.LoadserverdataService;
import com.zhy.http.okhttp.service.loadServerDataListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

import im.imxianzhi.com.imxianzhi.R;

/**
 * 首页 设置 fragment
 * AppPreferences appPreferences = new AppPreferences(mContext.getApplicationContext());
 *
 * @author dewyze
 */
@SuppressLint("ValidFragment")
public class Portal_OwnerFragment extends BaseAppFragment implements View.OnClickListener, loadServerDataListener {
    //    TextView user_setting_ipinfo;
    boolean initState = false;//记录button按钮的初始状态，假入手势是开启状态则初始置为true，同时避免监听响应该事件
    //    SwitchButton show_sogudu_switch;
    Activity mContext;
    SharedPreferences mSettings;
    SharedPreferences.Editor editor;
    LoadserverdataService loadDataService;
    TextView tv_user_id, tv_usertype, tv_my_character;
    TextView user_login_name;
    ImageView iv_isvip;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mContext = activity;
        mSettings = mContext.getSharedPreferences(WowuApp.PREFERENCE_KEY,
                android.content.Context.MODE_PRIVATE);
        loadDataService = new LoadserverdataService(this);
        mtotalHandler = new mHandlerWeak(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragement_owner_setting, container, false);
        initViews(view);
        return view;
    }


    private void initViews(View view) {

        RelativeLayout user_info_detail = (RelativeLayout) view.findViewById(R.id.user_info_detail);
        TextView user_info_edit = (TextView) view.findViewById(R.id.user_info_edit);
//        user_setting_ipinfo = (TextView) view.findViewById(R.id.user_setting_ipinfo);
//        RelativeLayout user_setting_iplay = (RelativeLayout) view.findViewById(R.id.user_setting_iplay);

        RelativeLayout clear_cache_quit = (RelativeLayout) view.findViewById(R.id.clear_cache_quit);

//        user_setting_iplay.setOnClickListener(this);
//        user_setting_ipinfo.setOnClickListener(this);
        user_info_edit.setOnClickListener(this);
        user_info_detail.setOnClickListener(this);
        clear_cache_quit.setOnClickListener(this);

        SimpleDraweeView draweeView = (SimpleDraweeView) view.findViewById(R.id.user_login_pic);
//        draweeView.setImageURI(Uri.parse(DistApp.userImagePath + mSettings.getString("userid", "") + ".jpg"));
        if (WowuApp.iconPath != null && !WowuApp.iconPath.equals("")) {
            draweeView.setImageURI(Uri.parse(WowuApp.iconPath));//"http://www.gog.com.cn/pic/0/10/91/11/10911138_955870.jpg"
        }


        user_login_name = ((TextView) view.findViewById(R.id.user_login_name));
        user_login_name.setText(WowuApp.Name);

        view.findViewById(R.id.update_pwd).setOnClickListener(this);
        view.findViewById(R.id.message_warn).setOnClickListener(this);
        view.findViewById(R.id.bind_phone).setOnClickListener(this);
        view.findViewById(R.id.bind_pay).setOnClickListener(this);
        view.findViewById(R.id.rt_my_character).setOnClickListener(this);
        view.findViewById(R.id.rt_chakanfangke).setOnClickListener(this);
        view.findViewById(R.id.rt_heimingdan).setOnClickListener(this);

        tv_user_id = (TextView) view.findViewById(R.id.tv_user_id);
        tv_usertype = (TextView) view.findViewById(R.id.tv_usertype);
        iv_isvip = (ImageView) view.findViewById(R.id.iv_isvip);
        tv_my_character = (TextView) view.findViewById(R.id.tv_my_character);
        loadData();
    }


    private final int LOADDATA = 11;
    public static final int LOAD_RECOMMEND_DATA = 1;

    private void loadData() {
        loadDataService.loadGetJsonRequestData(OkHttpUtils.GetUserInfoURL + "?userId=" + WowuApp.UserId, LOADDATA);

        loadDataService.loadGetJsonRequestData(WowuApp.GetDispositionInfoURL, LOAD_RECOMMEND_DATA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                Boolean soguduIsOpen = mSettings.getBoolean("gestureIsOpen", false);
                break;
            case WowuApp.ALIPAY:
                loadData();
                break;
            case USER_INFO_EDIT:
                loadData();
//                MyToast.show(mContext,"xiangyingle "+ data.getExtras(). getBoolean("changed"));
                break;
        }

    }

    Intent intent2 = new Intent();
    AlertDialog dialog;

    private final int USER_INFO_EDIT = 100;

    public void onClick(View v) {
        // TODO 自动生成的方法存根

        switch (v.getId()) {
            case R.id.clear_cache_quit:

                dialog = new AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle)
                        .setTitle("提示")
//                .setCancelable(true)
                        .setMessage("退出后将重新输入用户名和密码登录，确定退出？")
                        .setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        try {
                                            JSONObject json = new JSONObject();
                                            loadDataService.loadPostJsonRequestData(WowuApp.JSON, WowuApp.LogoutURL, json.toString(), R.id.clear_cache_quit);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                        try {
                                            DemoHelper.getInstance().logout(false, new EMCallBack() {
                                                @Override
                                                public void onSuccess() {
                                                }

                                                @Override
                                                public void onError(int i, String s) {
                                                }

                                                @Override
                                                public void onProgress(int i, String s) {
                                                }
                                            });
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        DemoDBManager.getInstance().deleteTable();
                                    }
                                })
                        .setNegativeButton("取消",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
//                .setView(v)
                        .create();

                // change color of positive button
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogs) {
                        Button b = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                        b.setTextColor(getResources().getColor(R.color.favorite_del));

//                Button b2 = d.getButton(DialogInterface.BUTTON_NEGATIVE);
//                b2.setTextColor(getResources().getColor(R.color.teal));
                    }
                });
                dialog.show();


//                logout();


                break;
            case R.id.user_info_edit:
            case R.id.user_info_detail:

                intent2.setClass(mContext, OwnerInfoEditActivity.class);
//                intent2.putExtra("UserDetail", mUserDetail);
//                mContext.startActivity(intent2);
                mContext.startActivityForResult(intent2, USER_INFO_EDIT);
                getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                break;
//            case R.id.user_setting_ipinfo:
//            case R.id.user_setting_iplay:
//                System.out.println("得到点击响应事件::::::得到点击响应事件");
//                showDialog();
//                Intent intent2 = new Intent();
            //                UserSettingActivity.this.startActivity(intent2);
//                break;

            case R.id.rt_my_character:
                Intent temp1Intent = new Intent(mContext, CharacterDetailActivity.class);//MyCharacterResultActivity
                startActivity(temp1Intent);
                mContext.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.update_pwd:
                intent2.setClass(mContext, UserModifyPasswdActivity.class);
                mContext.startActivity(intent2);
                getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.message_warn:
                intent2.setClass(mContext, UserSetWarnActivity.class);
                mContext.startActivity(intent2);
                getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.bind_phone:
                intent2.setClass(mContext, UserBindPhoneActivity.class);
                mContext.startActivity(intent2);
                getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;

            case R.id.bind_pay:

                if (mUserDetail != null && mUserDetail.isIsVip()) {
                    intent2.setClass(mContext, UserPaySuccessActivity.class);
//                    AppPreferences appPreferences = new AppPreferences(mContext.getApplicationContext());
//                    appPreferences.put("IsVip",mUserDetail.isIsVip());
                } else {
                    intent2.setClass(mContext, UserPayActivity.class);
                    if (mUserDetail != null) intent2.putExtra("isVip", mUserDetail.isIsVip());
                }
                mContext.startActivityForResult(intent2, WowuApp.ALIPAY);
                getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;


            case R.id.rt_chakanfangke:
                intent2.setClass(mContext, VisitedMeUsersActivity.class);
                mContext.startActivity(intent2);
                getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.rt_heimingdan:
                intent2.setClass(mContext, BlackUserActivity.class);
                mContext.startActivity(intent2);
                getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;


            default:
                break;
        }
    }

    void logout() {
        final ProgressDialog pd = new ProgressDialog(getActivity());
        String st = getResources().getString(R.string.Are_logged_out);
        pd.setMessage(st);
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        DemoHelper.getInstance().logout(false, new EMCallBack() {

            @Override
            public void onSuccess() {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        pd.dismiss();

                        SharedPreferences.Editor editor = mSettings.edit();
//                        editor.putString("username", "");
//                        editor.putString("password", "");
//                        editor.putBoolean("login_save_pwd_check", false);
                        editor.clear();
                        editor.commit();

                        //            彻底退出应用程序，经测试，效果很好
                        Intent startMain = new Intent(Intent.ACTION_MAIN);
                        startMain.addCategory(Intent.CATEGORY_HOME);
                        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(startMain);
                        ExitApp.getInstance().exit();
                        System.exit(0);

                    }
                });
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        pd.dismiss();
                        Toast.makeText(getActivity(), "unbind devicetokens failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public String getFragmentName() {
        return "Portal_ownFragment";
    }


    UserInfoDetail mUserDetail = new UserInfoDetail();
    mHandlerWeak mtotalHandler;
    public static final int DOWNLOADED_LocalUser = 0;
    public static final int DOWNLOADED_Contact = 1;
    public Characters mCharacter = null;

    @Override
    public void loadServerData(final String response, int flag) {
        switch (flag) {
            case R.id.clear_cache_quit:

                //            彻底退出应用程序，经测试，效果很好
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        DemoDBManager.getInstance().deleteTable();


                        SharedPreferences.Editor editor = mSettings.edit();
/*                editor.putString("username", "");
                editor.putString("password", "");
//                                        editor.putBoolean("login_auto_check", false);
                editor.putBoolean("login_save_pwd_check", false);*/
                        editor.clear();
                        editor.commit();
                        ExitApp.getInstance().exit();
                        System.exit(0);
                    }
                }).start();


                dialog.dismiss();
                break;

            case LOADDATA:
                try {
                    if (response != null) {
                        setLoadInfo(response);
                    }
                    Message msg = new Message();
                    msg.what = DOWNLOADED_LocalUser;
                    mtotalHandler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case LOAD_RECOMMEND_DATA:
                new Thread(new Runnable() {
                    @Override
                    public void run() {


//                        {"PhotoUrl":"http://xzxj.oss-cn-shanghai.aliyuncs.com/celebrity/6cbeca3a-989f-4756-8b46-bc781a1d123a.jpg",
//                                "Celebrity":"莎士比亚","CelebrityDescription":"英国文学史上最杰出的戏剧家","Name":"INFP","Title":"化解者",
//                                "Score":{"UserId":"637e5acb638f46f5873ec86f0b4b49ce",
//                                "E":0,"I":0,"S":0,"N":0,"T":0,"F":0,"J":0,"P":0,"PropensityScore":0.0,
//                                "EI_PropensityScore":0.0,"SN_PropensityScore":0.0,"TF_PropensityScore":0.0,
//                                "JP_PropensityScore":0.0,"PropensityDescription":"轻微","Id":"16967133-1356-4dfb-8ad4-abecdf755ca6"}}

//                        Gson gson = new GsonBuilder().create();
//                        java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<List<ContactFriend>>() {
//                        }.getType();
//                事实上这里要将好友区分出来，分为待添加和带邀请两类，分别放在两个数组中，然后刷新列表  xx
//                        mTianJia_Contacts = gson.fromJson(response, type);

                        Gson gson = new GsonBuilder().create();
                        java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<Characters>() {
                        }.getType();
                        mCharacter = gson.fromJson(response, type);


                        Message msg = new Message();
                        msg.what = DOWNLOADED_Contact;
                        msg.obj = response;
                        mtotalHandler.sendMessage(msg);
                    }
                }).start();
                break;

        }
    }


    public void setLoadInfo(String totalresult) throws JSONException {
        Gson gson = new GsonBuilder().create();
        if (totalresult != null) {
            java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<UserInfoDetail>() {
            }.getType();
            mUserDetail = gson.fromJson(totalresult, type);
            WowuApp.Name = mUserDetail.getName();
// 保存用户信息到sp
            SharedPreferences.Editor editor = mSettings.edit();
            editor.putString("userNumber", mUserDetail.getUserNumber());
            editor.putBoolean("isVip", mUserDetail.isIsVip());
            editor.putInt("VipType", mUserDetail.getVipLevel());
            editor.commit();
        }
    }


    private static class mHandlerWeak extends Handler {
        private WeakReference<Portal_OwnerFragment> activity = null;

        public mHandlerWeak(Portal_OwnerFragment act) {
            super();
            this.activity = new WeakReference<Portal_OwnerFragment>(act);
        }

        @Override
        public void handleMessage(Message msg) {
            Portal_OwnerFragment act = activity.get();
            if (null == act) {
                return;
            }
            switch (msg.what) {
                // 正在下载
                case DOWNLOADED_LocalUser:
//                    if (act.userPicAdapter == null) {
////                        act.initTopPicAdapter();
//                        act.userPicAdapter.setData(act.getUserInfoDetail().getPhotos());
//                        act.contentRAdapter.setData(act.mUserDetailList);
////                        act.mPicRecyclerView.setAdapter(act.userPicAdapter);
//                    } else {
//                        act.userPicAdapter.setData(act.getUserInfoDetail().getPhotos());
//                    }

                    WowuApp.XianZhiNumber = act.mUserDetail.getUserNumber();
                    act.tv_user_id.setText("先知号: " + WowuApp.XianZhiNumber);
//                    act.tv_usertype.setText(act.mUserDetail.isIsVip() == true ? "已开通" : "普通用户");
                    if (act.mUserDetail.isIsVip()) {
                        if (act.mUserDetail.getVipLevel() == 5) {
                            act.iv_isvip.setImageResource(R.drawable.svip);
                        } else {
                            act.iv_isvip.setImageResource(R.drawable.vip);
                        }
                    } else {
                        act.iv_isvip.setImageResource(R.drawable.no_vip);
                    }

                    act.user_login_name.setText(WowuApp.Name);
                    AppPreferences appPreferences = new AppPreferences(act.mContext.getApplicationContext());
                    appPreferences.put("IsVip", act.mUserDetail.isIsVip());
                    appPreferences.put("VipType", act.mUserDetail.getVipLevel());
                    break;
                case DOWNLOADED_Contact:
                    act.tv_my_character.setText(act.mCharacter.getName() + act.mCharacter.getTitle());

                    AppPreferences appPreferences2 = new AppPreferences(act.mContext.getApplicationContext());
                    appPreferences2.put("characterType", act.mCharacter.getName() + act.mCharacter.getTitle());
//                    appPreferences2.put("characterChanged",true);
                    break;
            }
        }
    }

    private boolean refreshState = false;
    @Override
    public void onResume() {
        super.onResume();
        if (refreshState) {
            AppPreferences appPreferences = new AppPreferences(mContext.getApplicationContext());
//            tv_usertype.setText(appPreferences.getBoolean("IsVip", false) == true ? "已开通" : "普通用户");
            if (appPreferences.getBoolean("IsVip", false)) {
                if (appPreferences.getInt("VipType", 0) == 5) {
                    iv_isvip.setImageResource(R.drawable.svip);
                } else {
                    iv_isvip.setImageResource(R.drawable.vip);
                }
            } else {
                iv_isvip.setImageResource(R.drawable.no_vip);
            }
            user_login_name.setText(WowuApp.Name);
            tv_my_character.setText(appPreferences.getString("characterType", tv_my_character.getText().toString()));
            refreshState = false;
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        refreshState = true;
    }

    @Override
    public void loadDataFailed(String response, int flag) {

        if (dialog != null) {
            dialog.dismiss();
        }


        if (flag == R.id.clear_cache_quit) {
            SharedPreferences.Editor editor = mSettings.edit();
            editor.putString("username", "");
            editor.putString("password", "");
//                                        editor.putBoolean("login_auto_check", false);
            editor.putBoolean("login_save_pwd_check", false);
            editor.commit();
//            MyToast.show(mContext, response);

            //            彻底退出应用程序，经测试，效果很好
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
            ExitApp.getInstance().exit();
            System.exit(0);
        }
    }
}

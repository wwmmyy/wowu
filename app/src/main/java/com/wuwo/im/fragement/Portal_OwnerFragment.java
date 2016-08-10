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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hyphenate.EMCallBack;
import com.hyphenate.chatuidemo.DemoHelper;
import com.wuwo.im.activity.UserPayActivity;
import com.wuwo.im.activity.OwnerInfoEditActivity;
import com.wuwo.im.activity.UserBindPhoneActivity;
import com.wuwo.im.activity.UserModifyPasswdActivity;
import com.wuwo.im.activity.UserSetWarnActivity;
import com.wuwo.im.bean.UserInfoDetail;
import com.wuwo.im.config.ExitApp;
import com.wuwo.im.config.WowuApp;
import com.wuwo.im.util.MyToast;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.service.LoadserverdataService;
import com.zhy.http.okhttp.service.loadServerDataListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

import im.wuwo.com.wuwo.R;

/**
 * 首页 设置 fragment
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
    TextView tv_user_id,tv_usertype;

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
        draweeView.setImageURI(Uri.parse(WowuApp.iconPath));//"http://www.gog.com.cn/pic/0/10/91/11/10911138_955870.jpg"

        ((TextView) view.findViewById(R.id.user_login_name)).setText(WowuApp.Name);

        view.findViewById(R.id.update_pwd).setOnClickListener(this);
        view.findViewById(R.id.message_warn).setOnClickListener(this);
        view.findViewById(R.id.bind_phone).setOnClickListener(this);
        view.findViewById(R.id.bind_pay).setOnClickListener(this);


          tv_user_id = (TextView) view.findViewById(R.id.tv_user_id);
          tv_usertype = (TextView) view.findViewById(R.id.tv_usertype);
            loadData();
    }



    private  final int LOADDATA=11;

    private void loadData() {
        loadDataService.loadGetJsonRequestData(OkHttpUtils.GetUserInfoURL + "?userId=" + WowuApp.UserId, LOADDATA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case 0:
                Boolean soguduIsOpen = mSettings.getBoolean("gestureIsOpen", false);
                break;
            case WowuApp.ALIPAY:
                loadData();
                break;
        }

    }

    Intent intent2 = new Intent();
      AlertDialog dialog;
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
//                                        SharedPreferences.Editor editor = mSettings.edit();
//                                        editor.putString("username", "");
//                                        editor.putString("password", "");
////                                        editor.putBoolean("login_auto_check", false);
//                                        editor.putBoolean("login_save_pwd_check",false);
//                                        editor.commit();
//
//                                        //            彻底退出应用程序，经测试，效果很好
//                                        Intent startMain = new Intent(Intent.ACTION_MAIN);
//                                        startMain.addCategory(Intent.CATEGORY_HOME);
//                                        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                        ExitApp.getInstance().exit();
//                                        startActivity(startMain);
//                                        System.exit(0);
//                                        dialog.dismiss();

                                        DemoHelper.getInstance().logout(false, new EMCallBack(){
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

                                        try {
                                            JSONObject json = new JSONObject();
                                            loadDataService.loadPostJsonRequestData(WowuApp.JSON, WowuApp.LogoutURL, json.toString(), R.id.clear_cache_quit);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

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
//            case R.id.user_info_detail:

                intent2.setClass(mContext, OwnerInfoEditActivity.class);
//                intent2.putExtra("UserDetail", mUserDetail);
                mContext.startActivity(intent2);
                getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                break;
//            case R.id.user_setting_ipinfo:
//            case R.id.user_setting_iplay:
//                System.out.println("得到点击响应事件::::::得到点击响应事件");
//                showDialog();
//                Intent intent2 = new Intent();
            //                UserSettingActivity.this.startActivity(intent2);
//                break;
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
                intent2.setClass(mContext, UserPayActivity.class);
//                mContext.startActivity(intent2);
                mContext.startActivityForResult(intent2, WowuApp.ALIPAY);
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
                        editor.putString("username", "");
                        editor.putString("password", "");
//                                        editor.putBoolean("login_auto_check", false);
                        editor.putBoolean("login_save_pwd_check", false);
                        editor.commit();

                        //            彻底退出应用程序，经测试，效果很好
                        Intent startMain = new Intent(Intent.ACTION_MAIN);
                        startMain.addCategory(Intent.CATEGORY_HOME);
                        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        ExitApp.getInstance().exit();
                        startActivity(startMain);
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






    @Override
    public void loadServerData(String response, int flag) {
        switch (flag) {
            case R.id.clear_cache_quit:
                                        SharedPreferences.Editor editor = mSettings.edit();
                                        editor.putString("username", "");
                                        editor.putString("password", "");
//                                        editor.putBoolean("login_auto_check", false);
                                        editor.putBoolean("login_save_pwd_check",false);
                                        editor.commit();

                                        //            彻底退出应用程序，经测试，效果很好
                                        Intent startMain = new Intent(Intent.ACTION_MAIN);
                                        startMain.addCategory(Intent.CATEGORY_HOME);
                                        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        ExitApp.getInstance().exit();
                                        startActivity(startMain);
                                        System.exit(0);
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
        }
    }






    public void setLoadInfo(String totalresult) throws JSONException {
        Gson gson = new GsonBuilder().create();
        if (totalresult != null) {
            java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<UserInfoDetail>() {
            }.getType();
            mUserDetail = gson.fromJson(totalresult, type);
// 保存用户信息到sp
            SharedPreferences.Editor editor = mSettings.edit();
            editor.putString("userNumber",mUserDetail.getUserNumber());
            editor.putBoolean("isVip",mUserDetail.isIsVip());
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

                    WowuApp.XianZhiNumber=act.mUserDetail.getUserNumber();
                    act.tv_user_id.setText(act.mUserDetail.getUserNumber());
                    act.tv_usertype.setText(act.mUserDetail.isIsVip()==true?"会员用户":"普通用户");

                    break;
            }
        }
    }





    @Override
    public void loadDataFailed(String response, int flag) {

        if(dialog!=null){
            dialog.dismiss();
        }


        if (flag==R.id.clear_cache_quit){
            SharedPreferences.Editor editor = mSettings.edit();
            editor.putString("username", "");
            editor.putString("password", "");
//                                        editor.putBoolean("login_auto_check", false);
            editor.putBoolean("login_save_pwd_check",false);
            editor.commit();
            MyToast.show(mContext, response);

            //            彻底退出应用程序，经测试，效果很好
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ExitApp.getInstance().exit();
            startActivity(startMain);
            System.exit(0);
        }
    }
}

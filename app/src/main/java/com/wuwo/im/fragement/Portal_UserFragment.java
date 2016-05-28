package com.wuwo.im.fragement;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wuwo.im.activity.UserDetailActivity;
import com.wuwo.im.config.ExitApp;
import com.wuwo.im.config.WowuApp;

import im.wuwo.com.wuwo.R;

/**
 * 首页 设置 fragment
 *
 * @author dewyze
 */
@SuppressLint("ValidFragment")
public class Portal_UserFragment extends BaseAppFragment implements View.OnClickListener {
    //    TextView user_setting_ipinfo;
    boolean initState = false;//记录button按钮的初始状态，假入手势是开启状态则初始置为true，同时避免监听响应该事件
    //    SwitchButton show_sogudu_switch;
    Activity mContext;
    SharedPreferences mSettings;
    SharedPreferences.Editor editor;

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
        View view = inflater.inflate(R.layout.fragement_user_setting, container, false);
        initViews(view);
        return view;
    }


    private void initViews(View view) {


        RelativeLayout user_info_detail = (RelativeLayout) view.findViewById(R.id.user_info_detail);
        TextView user_info_detail_edit = (TextView) view.findViewById(R.id.user_info_detail_edit);
//        user_setting_ipinfo = (TextView) view.findViewById(R.id.user_setting_ipinfo);
//        RelativeLayout user_setting_iplay = (RelativeLayout) view.findViewById(R.id.user_setting_iplay);

        RelativeLayout clear_cache_quit = (RelativeLayout) view.findViewById(R.id.clear_cache_quit);

//        user_setting_iplay.setOnClickListener(this);
//        user_setting_ipinfo.setOnClickListener(this);
        user_info_detail_edit.setOnClickListener(this);
        user_info_detail.setOnClickListener(this);
        clear_cache_quit.setOnClickListener(this);

        SimpleDraweeView draweeView = (SimpleDraweeView) view.findViewById(R.id.user_login_pic);
//        draweeView.setImageURI(Uri.parse(DistApp.userImagePath + mSettings.getString("userid", "") + ".jpg"));
        draweeView.setImageURI(Uri.parse("http://www.gog.com.cn/pic/0/10/91/11/10911138_955870.jpg"));


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case 0:
                Boolean soguduIsOpen = mSettings.getBoolean("gestureIsOpen", false);
                break;
        }

    }

    public void onClick(View v) {
        // TODO 自动生成的方法存根

        switch (v.getId()) {
            case R.id.clear_cache_quit:

                final AlertDialog d = new AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle)
                        .setTitle("提示")
//                .setCancelable(true)
                        .setMessage("退出后将重新输入用户名和密码登录，确定退出？")
                        .setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        SharedPreferences.Editor editor = mSettings.edit();
                                        editor.putString("username", "");
                                        editor.putString("password", "");
                                        editor.putBoolean("login_auto_check", false);
                                        editor.commit();

                                        //            彻底退出应用程序，经测试，效果很好
                                        Intent startMain = new Intent(Intent.ACTION_MAIN);
                                        startMain.addCategory(Intent.CATEGORY_HOME);
                                        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(startMain);
                                        ExitApp.getInstance().exit();
                                        System.exit(0);
                                        dialog.dismiss();

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
                d.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        Button b = d.getButton(DialogInterface.BUTTON_POSITIVE);
                        b.setTextColor(getResources().getColor(R.color.favorite_del));

//                Button b2 = d.getButton(DialogInterface.BUTTON_NEGATIVE);
//                b2.setTextColor(getResources().getColor(R.color.teal));
                    }
                });
                d.show();


                break;
            case R.id.user_info_detail_edit:
            case R.id.user_info_detail:
                Intent intent2 = new Intent();
                intent2.setClass(mContext, UserDetailActivity.class);
                mContext.startActivity(intent2);
                break;
//            case R.id.user_setting_ipinfo:
//            case R.id.user_setting_iplay:
//                System.out.println("得到点击响应事件::::::得到点击响应事件");
//                showDialog();
//                Intent intent2 = new Intent();
//                intent2.setClass(UserSettingActivity.this, UserDetailActivity.class);
//                UserSettingActivity.this.startActivity(intent2);
//                break;

//            case R.id.return_back:
//                finish();
//                overridePendingTransition(0, R.anim.slide_out_to_left);
//                break;
            default:
                break;
        }

    }


    @Override
    public String getFragmentName() {
        return "Portal_UserFragment";
    }
}

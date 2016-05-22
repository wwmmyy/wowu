package com.wuwo.im.fragement;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.wuwo.im.config.WowuApp;

import im.wuwo.com.wuwo.R;

/**
 * 首页 设置 fragment
 * @author dewyze
 */

@SuppressLint("ValidFragment")
public class Portal_FindFragment extends BaseAppFragment implements View.OnClickListener{
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
        view.findViewById(R.id.find_share_friend).setOnClickListener(this);
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
        switch (v.getId()){
            case R.id.user_info_detail:
                showSharefDialog();
                break;
            case R.id.find_share_f:
                showSharefDialog();
                break;
            case R.id.find_share_friend:
                showSharefDialog();
                break;
        }
    }














    private void showSharefDialog() {
        View view = mContext.getLayoutInflater().inflate(R.layout.fragement_find_sharef_pop, null);
        final Dialog dialog = new Dialog(mContext, R.style.transparentFrameWindowStyle);
        dialog.setContentView(view, new ViewGroup.LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();

        Button home_member_cancel = (Button) view.findViewById(R.id.home_member_cancel);
        TextView home_member_detail = (TextView) view.findViewById(R.id.home_member_detail);
        TextView home_member_jiazhang = (TextView) view.findViewById(R.id.home_member_jiazhang);



        home_member_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO 自动生成的方法存根
                dialog.dismiss();
            }
        });
        home_member_detail.setOnClickListener(new View.OnClickListener() {
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
}

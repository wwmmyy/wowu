package com.wuwo.im.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;

import com.wuwo.im.config.ExitApp;
import com.wuwo.im.util.LogUtils;

/** 
*desc
*@author 王明远
*@日期： 2016/6/9 0:09
*@版权:Copyright    All rights reserved.
*/

public class BaseFragementActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        if ( getSharedPreferences("theme", Context.MODE_PRIVATE)
//                .getBoolean("isDark", false)) {
//            setTheme(R.style.AppTheme_Dark);
//        }else {
//            setTheme(R.style.AppBaseTheme);
//        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//      改变状态栏字体颜色 此功能不成熟，暂时屏蔽
//        Helper.statusBarLightMode(BaseFragementActivity.this);

//        int currentTheme = getSharedPreferences("theme", Context.MODE_PRIVATE).getInt("currentTheme", 0);
//        switch (currentTheme) {
//            case 0:
//                setTheme(R.style.AppBaseTheme);
//                break;
//            case 1:
//                setTheme(R.style.AppTheme_VIOLET);
//                break;
//            case 2:
//                setTheme(R.style.AppTheme_Blue);
//                break;
//            case 3:
//                setTheme(R.style.AppTheme_Green);
//                break;
//            case 4:
//                setTheme(R.style.AppTheme_Wisteria);
//                break;
//            case 5:
//                setTheme(R.style.AppTheme_Googleplus);
//                break;
//            default:
//                setTheme(R.style.AppBaseTheme);
//                break;
//        }

        super.onCreate(savedInstanceState);
        ExitApp.getInstance().addOpenedActivity(this);
        LogUtils.i("BaseActivity", "BaseActivity onCreate");
    }

//    @Override
//    public void onBackPressed() {
//        finish();
//        overridePendingTransition(0, R.anim.slide_out_to_left);
//    }
}

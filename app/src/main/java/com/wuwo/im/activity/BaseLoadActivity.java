package com.wuwo.im.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;

import com.wuwo.im.config.ExitApp;
import com.zhy.http.okhttp.service.LoadserverdataService;
import com.zhy.http.okhttp.service.loadServerDataListener;

public abstract class BaseLoadActivity extends BaseActivity implements View.OnClickListener, loadServerDataListener {
    LoadserverdataService loadDataService;
    Context mContext = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        mContext = this;
        loadDataService = new LoadserverdataService(this);
        ExitApp.getInstance().addOpenedActivity(this);
        Log.i("BaseActivity", "BaseActivity onCreate");
    }


//    @Override
//    public void onBackPressed() {
////        finish();
////        overridePendingTransition(0, R.anim.slide_out_to_left);
//    }
}

package com.wuwo.im.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
/** 
*desc
*@author 王明远
*@日期： 2016/6/9 0:09
*@版权:Copyright    All rights reserved.
*/

public abstract class BaseActivity extends AppCompatActivity {
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


        super.onCreate(savedInstanceState);

        Log.i("BaseActivity", "BaseActivity onCreate");
    }


    @Override
    public void onBackPressed() {
        finish();
//        overridePendingTransition(0, R.anim.slide_out_to_left);
    }
}

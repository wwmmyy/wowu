package com.wuwo.im.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by wmy on 2016/12/18.
 */
public class PreferenceUtils {

    private SharedPreferences sharedPreferences;

    private SharedPreferences.Editor shareEditor;

    private static PreferenceUtils preferenceUtils = null;

    public static final String NOTE_TYPE_KEY = "NOTE_TYPE_KEY";

    public static final String EVERNOTE_ACCOUNT_KEY = "EVERNOTE_ACCOUNT_KEY";

    public static final String EVERNOTE_NOTEBOOK_GUID_KEY = "EVERNOTE_NOTEBOOK_GUID_KEY";

     protected PreferenceUtils(Context context){
        sharedPreferences = context.getSharedPreferences("XIANZHIXIANJUE", Context.MODE_PRIVATE);
        shareEditor = sharedPreferences.edit();
    }

    public static PreferenceUtils getInstance(Context context){
        if (preferenceUtils == null) {
            synchronized (PreferenceUtils.class) {
                if (preferenceUtils == null) {
                    preferenceUtils = new PreferenceUtils(context.getApplicationContext());
                }
            }
        }
        return preferenceUtils;
    }

    public String getStringParam(String key){
        return getStringParam(key, "");
    }

    public String getStringParam(String key, String defaultString){
        return sharedPreferences.getString(key, defaultString);
    }

    public void saveParam(String key, String value)
    {
        shareEditor.putString(key,value).commit();
    }




}

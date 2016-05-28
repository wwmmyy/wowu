package com.wuwo.im.config;

import android.app.Activity;
import android.app.Application;

import java.util.LinkedList;
import java.util.List;

/**
 * @类名:
 * @描述: * 用于共享ApplicationContext 在Application中共享的数据，
 * 生命周期和整个应用的生命周期一样   工具类，主要用于存储各种常量
 * @作者: 王明远
 * @修改人:
 * @修改时间:
 * @修改内容:
 * @版本: V1.0
 * @版权:Copyright ©  All rights reserved.
 */
public class ExitApp extends Application {
    //用于记录被打开的activity，为退出做准备记录
    private List<Activity> mOpenActivityList = new LinkedList<Activity>();

    private static ExitApp instance;

    private ExitApp() {
    }

    public synchronized static ExitApp getInstance() {
        if (null == instance) {
            instance = new ExitApp();
        }
        return instance;
    }


    // add Activity
    public void addOpenedActivity(Activity activity) {
        mOpenActivityList.add(activity);
    }

    public void exit() {
        try {
            for (Activity activity : mOpenActivityList) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }

    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }


}

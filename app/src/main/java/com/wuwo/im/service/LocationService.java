package com.wuwo.im.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.wuwo.im.config.WowuApp;
/** 
*desc
*@author 王明远
*@日期： 2016/6/11 16:51
*@版权:Copyright 上海数慧系统有限公司  All rights reserved.
*/

public class LocationService extends Service {
    private LocationClient mLocationClient;

    
    
    @Override
    public void onCreate() {
        // TODO 自动生成的方法存根
        super.onCreate();
        mLocationClient = ((WowuApp)getApplication()).mLocationClient;
        InitLocation();        
        mLocationClient.start();
        
        Log.i("地图定位已经启动", "地图定位已经启动");
        
        
    }
    
    
    
    @Override
    public IBinder onBind(Intent intent) {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    public void onDestroy() {
        // TODO 自动生成的方法存根
        mLocationClient.stop();
        super.onDestroy();
    }
    


    private void InitLocation(){
            LocationClientOption option = new LocationClientOption();
//          option.setLocationMode(tempMode);
            option.setLocationMode(LocationMode.Hight_Accuracy);
//          option.setCoorType(tempcoor);
            option.setCoorType("bd09ll");           
//            int span=120*1000;//两分钟定位一次
            int span=10*1000;//两分钟定位一次120
//          try {
//                  span = Integer.valueOf(frequence.getText().toString());
//          } catch (Exception e) {
//                  // TODO: handle exception
//          }
            option.setScanSpan(span);
//          option.setIsNeedAddress(checkGeoLocation.isChecked());
            mLocationClient.setLocOption(option);
    }
    
    
    
}

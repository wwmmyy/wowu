package com.wuwo.im.service;//package com.wuwo.im.service;
//
//import android.app.Service;
//import android.content.Intent;
//import android.os.IBinder;
//
//import com.baidu.location.LocationClient;
//import com.baidu.location.LocationClientOption;
//import com.baidu.location.LocationClientOption.LocationMode;
//import com.wuwo.im.config.WowuApp;
//import com.wuwo.im.util.LogUtils;
//
///**
//*desc
//*@author 王明远
//*@日期： 2016/6/11 16:51
//*@版权:Copyright 上海数慧系统有限公司  All rights reserved.
//*/
//
//public class MyLocationService extends Service {
//    private LocationClient mLocationClient;
//
//
//
//    @Override
//    public void onCreate() {
//        // TODO 自动生成的方法存根
//        super.onCreate();
//        mLocationClient = ((WowuApp)getApplication()).mLocationClient;
//        InitLocation();
//        mLocationClient.start();
//
//        LogUtils.i("地图定位已经启动", "地图定位已经启动");
//
////        registerLocationBoradcastReceiver();
//    }
//
//
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        // TODO 自动生成的方法存根
//        return null;
//    }
//
//    @Override
//    public void onDestroy() {
//        // TODO 自动生成的方法存根
//        mLocationClient.stop();
//        super.onDestroy();
//    }
//
//
//
//    private void InitLocation(){
//            LocationClientOption option = new LocationClientOption();
////          option.setLocationMode(tempMode);
//            option.setLocationMode(LocationMode.Hight_Accuracy);
////          option.setCoorType(tempcoor);
//            option.setCoorType("bd09ll");
////            int span=120*1000;//两分钟定位一次
//            int span=WowuApp.LocationTime;//两分钟定位一次120
////          try {
////                  span = Integer.valueOf(frequence.getText().toString());
////          } catch (Exception e) {
////                  // TODO: handle exception
////          }
//            option.setScanSpan(span);
////          option.setIsNeedAddress(checkGeoLocation.isChecked());
//            mLocationClient.setLocOption(option);
//    }
//
//
//
///*    public void registerLocationBoradcastReceiver() {
//        IntentFilter myIntentFilter = new IntentFilter();
//        myIntentFilter.addAction(WowuApp.RESET_LOCATION_TIME);
//        //注册广播
//        registerReceiver(mLocationBroadcastReceiver, myIntentFilter);
//    }
//
//    private BroadcastReceiver mLocationBroadcastReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (action.equals(WowuApp.RESET_LOCATION_TIME)) {
//                int timeLength = Integer.parseInt(intent.getStringExtra("locationNoChangLength") );
//                MyToast.show(context,timeLength+";xx");
//                if(timeLength !=0){
//                    newSetScanSpan(timeLength);
//                }
//            }
//
//        }
//    };
//
//    private void newSetScanSpan(final int timeLength) {
//        new Thread(new Runnable() {
//            @Override
//            public void run( ) {
//                LocationClientOption option= mLocationClient.getLocOption();
//                option.setScanSpan(timeLength);
//                mLocationClient.setLocOption(option);
//            }
//        }).start();
//   } */
//
//}

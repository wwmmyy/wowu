package com.wuwo.im.config;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.squareup.okhttp.MediaType;
import com.wuwo.im.util.UtilsTool;

import im.wuwo.com.wuwo.R;

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
public class WowuApp extends Application {
    static Context sContext;
    public static String ACTION_NAME = "deviceLocked";
    SharedPreferences settings;
    public LocationClient mLocationClient;
    public MyLocationListener mMyLocationListener;
    public static final String LOG_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/errorLog";
    //    public static String serverAbsolutePath = "http://58.246.138.178:8040/gzServices/ServiceProvider.ashx"; // dist
    public static String serverAbsolutePath = "http://139.196.110.136:7777/";//http://139.196.85.20/
    //此为即时通讯消息推送的服务器端ip及端口
//    public static String XMPPserverIP = serverIP;  // dist
    public static String XMPPserverIP = " ";
    public static int XMPPserverPort = 0;  // dist

    @Override
    public void onCreate() {
        super.onCreate();

        Fresco.initialize(getApplicationContext());

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder() //
                .showImageForEmptyUri(R.drawable.ic_launcher) //
                .showImageOnFail(R.drawable.ic_launcher) //
                .cacheInMemory(true) //
                .cacheOnDisk(true) //
                .build();//
        ImageLoaderConfiguration config = new ImageLoaderConfiguration//
                .Builder(getApplicationContext())//
                .defaultDisplayImageOptions(defaultOptions)//
                .discCacheSize(50 * 1024 * 1024)//
                .discCacheFileCount(100)// 缓存一百张图片
                .writeDebugLogs()//
                .build();//
        ImageLoader.getInstance().init(config);

//假如空指针的话application保存的东西就会清空重新加载
        settings = this.getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
        PhoneNumber = settings.getString("PhoneNumber", "");
        Name = settings.getString("Name", "");
        Thread.setDefaultUncaughtExceptionHandler(restartHandler); // 程序崩溃时触发线程  以下用来捕获程序崩溃异常


//        添加地图定位功能
        mLocationClient = new LocationClient(this.getApplicationContext());
        mMyLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mMyLocationListener);
    }

    //  记录位置坐标
    public static String latitude = "";
    public static String longitude = "";
    public static String Radius = "";

    public static final String PREFERENCE_KEY = "com.wowu.im";
//    public static String userId = "";
//    public static String userName = "";

    public static final String ALL_CachePathDirTemp = "/mnt/sdcard/Downloads/";//下载文件的暂存路径
    public static String userImagePath = serverAbsolutePath + "/appIcon/userIcon/";  //头像缩略图


    //表示请求服务器类型
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");


    //########Media   媒体相关##########################################
//    POST Media/PreUploadImage 预上传图片 {  "Content": "sample string 1",  "FromIOS": true }
//    Content	 Base64编码的图像  string    Required
//    FromIOS	 是否来自IOS的Base64  boolean   None.
    public static String PreUploadImageURL = serverAbsolutePath + "Media/PreUploadImage";

    //########User   账户相关##########################################
//     登录 {   "PhoneNumber": "sample string 1", "Password": "sample string 2" }
    public static String LoginURL = serverAbsolutePath + "Account/Login";
    //  用户注册，
    public static String RegisterURL = serverAbsolutePath + "Account/Register";
    //    发送短信验证码  {  "PhoneNumber": "sample string 1", "Type": 2 }
    public static String SmsValidateURL = serverAbsolutePath + "Account/SendSmsValidateCode";
    //    验证短信验证码 {   "PhoneNumber": "sample string 1", "Type": 2,    "SmsValidateCode": "sample string 3"  }    短信用途(0：注册，1：找回密码)
    public static String ValidateCodeURL = serverAbsolutePath + "Account/ValidateSmsCode";
    //    设置用户的性格 POST Account/SetDisposition?dispositionId={dispositionId}
    public static String SetDispositionURL = serverAbsolutePath + "Account/SetDisposition";
    //    GET Account/GetDispositionInfo 获取用户的性格
    public static String GetDispositionInfoURL = serverAbsolutePath + "Account/GetDispositionInfo";
    //    POST Account/SubmitLocation?lon={lon}&lat={lat} 提交位置
    public static String SubmitLocationURL = serverAbsolutePath + "Account/SubmitLocation?lon=" + longitude + "&lat=" + latitude;

    //########Disposition   性格相关##########################################
//    GET Disposition/DispositionList  性格列表
    public static String DispositionListURL = serverAbsolutePath + "Disposition/DispositionList";
    //    GET Disposition/QuestionList   测试题列表
    public static String QuestionListURL = serverAbsolutePath + "Disposition/QuestionList";
    //    POST Disposition/SubmitAnswer   提交答案   [  {  "QuestionId": "sample string 1",   "Answer": "sample string 2"  },
// {  "QuestionId": "sample string 1", "Answer": "sample string 2"  } ]
    public static String SubmitAnswerURL = serverAbsolutePath + "Disposition/SubmitAnswer";

    //########Chat   聊天室##########################################
//GET Chat/GetNearbyUser?lon={lon}&lat={lat}&pageIndex={pageIndex} 获取附近的用户
    public static String GetNearbyUserURL = serverAbsolutePath + "Chat/GetNearbyUser" ;
//        GET Chat/GetUserInfo?userId={userId} 获取目标用户的信息
    public static String GetUserInfoURL = serverAbsolutePath + "Chat/GetUserInfo" ;


    public static String UserId = "";
    public static String PhoneNumber = "";
    public static String Password = "";
    public static String SmsValidateCode = "";
    public static int Gender = 0;
    public static String Name = "";
    public static String picPath = null;//用户头像路径
    public static String token = "";


    // 创建服务用于捕获崩溃异常
    private Thread.UncaughtExceptionHandler restartHandler = new Thread.UncaughtExceptionHandler() {
        public void uncaughtException(Thread thread, Throwable ex) {
            UtilsTool.saveErrorFile(ex, "系统崩溃信息：" + System.currentTimeMillis() + ".txt");
        }
    };

    /**
     * 实现实位回调监听
     */
    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
//                    //Receive Location
//                    StringBuffer sb = new StringBuffer(256);
//                    sb.append("time : ");
//                    sb.append(location.getTime());
//                    sb.append("\nerror code : ");
//                    sb.append(location.getLocType());
//                    sb.append("\nlatitude : ");
//                    sb.append(location.getLatitude());
//                    sb.append("\nlontitude : ");
//                    sb.append(location.getLongitude());
//                    sb.append("\nradius : ");
//                    sb.append(location.getRadius());
//                    if (location.getLocType() == BDLocation.TypeGpsLocation){
//                            sb.append("\nspeed : ");
//                            sb.append(location.getSpeed());
//                            sb.append("\nsatellite : ");
//                            sb.append(location.getSatelliteNumber());
//                            sb.append("\ndirection : ");
//                            sb.append("\naddr : ");
//                            sb.append(location.getAddrStr());
//                            sb.append(location.getDirection());
//                    } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
//                            sb.append("\naddr : ");
//                            sb.append(location.getAddrStr());
//                            //运营商信息
//                            sb.append("\noperationers : ");
//                            sb.append(location.getOperators());
//                    }
            Log.i("获取到的定位信息为：：：", location.getLatitude() + "：：：" + location.getLongitude());
////                    UtilsTool.saveErrorFile(sb.toString(), "获取到的坐标为11.txt");

            latitude = location.getLatitude() + "";
            longitude = location.getLongitude() + "";
            Radius = location.getRadius() + "";

            mobileLocationLog();
        }
    }


    /**
     * @Description: 将终端的位置坐标上传到服务器
     */
    public void mobileLocationLog() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                // TODO 自动生成的方法存根
//            }
//        }).start();
    }


}

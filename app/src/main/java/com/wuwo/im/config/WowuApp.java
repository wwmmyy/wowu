package com.wuwo.im.config;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

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

    public static final String LOG_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/errorLog";
//    public static String serverAbsolutePath = "http://58.246.138.178:8040/gzServices/ServiceProvider.ashx"; // dist
    public static String serverAbsolutePath = "http://139.196.85.20/";
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
        userId = settings.getString("userid", "");
        userName = settings.getString("username", "");
        Thread.setDefaultUncaughtExceptionHandler(restartHandler); // 程序崩溃时触发线程  以下用来捕获程序崩溃异常
    }

    public static final String PREFERENCE_KEY = "com.wowu.im";
    public static String userId = "";
    public static String userName = "";

    public static final String ALL_CachePathDirTemp = "/mnt/sdcard/Downloads/";//下载文件的暂存路径
    public static String userImagePath = serverAbsolutePath + "/appIcon/userIcon/";  //头像缩略图




    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");


//########Media   媒体相关##########################################
//    POST Media/PreUploadImage 预上传图片 {  "Content": "sample string 1",  "FromIOS": true }
//    Content	 Base64编码的图像  string    Required
//    FromIOS	 是否来自IOS的Base64  boolean   None.
    public static String PreUploadImageURL = serverAbsolutePath + "Media/PreUploadImage";

//########User   账户相关##########################################
//     登录 {   "PhoneNumber": "sample string 1", "Password": "sample string 2" }
    public static String LoginURL = serverAbsolutePath + "Account/Login";
//    发送短信验证码  {  "PhoneNumber": "sample string 1", "Type": 2 }
    public static String SmsValidateURL = serverAbsolutePath + "Account/SendSmsValidateCode";
//    验证短信验证码 {   "PhoneNumber": "sample string 1", "Type": 2,    "SmsValidateCode": "sample string 3"  }    短信用途(0：注册，1：找回密码)
    public static String ValidateCodeURL = serverAbsolutePath + "Account/ValidateSmsCode";
//    设置用户的性格 POST Account/SetDisposition?dispositionId={dispositionId}
    public static String SetDispositionURL = serverAbsolutePath + "Account/SetDisposition";
//    GET Account/GetDispositionInfo 获取用户的性格
    public static String GetDispositionInfoURL = serverAbsolutePath + "Account/GetDispositionInfo";
//    OST Account/SubmitLocation?lon={lon}&lat={lat} 提交位置
    public static String SubmitLocationURL = serverAbsolutePath + "Account/SubmitLocation";

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
    public static String GetNearbyUserURL = serverAbsolutePath + "Chat/GetNearbyUser";



    public static String PhoneNumber="";
    public static String Password="";
    public static String SmsValidateCode="";
    public static String Gender="";
    public static String Name="";


    // 创建服务用于捕获崩溃异常
    private Thread.UncaughtExceptionHandler restartHandler = new Thread.UncaughtExceptionHandler() {
        public void uncaughtException(Thread thread, Throwable ex) {
            UtilsTool.saveErrorFile(ex, "系统崩溃信息：" + System.currentTimeMillis() + ".txt");
        }
    };
}

package com.wuwo.im.config;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.easemob.redpacketsdk.RedPacket;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.chatuidemo.DemoHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.squareup.okhttp.MediaType;
import com.wuwo.im.util.UtilsTool;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.Iterator;
import java.util.List;

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
    //    public static String OkHttpUtils.serverAbsolutePath = "http://58.246.138.178:8040/gzServices/ServiceProvider.ashx"; // dist
    //此为即时通讯消息推送的服务器端ip及端口
//    public static String XMPPserverIP = serverIP;  // dist
    public static String XMPPserverIP = " ";
    public static int XMPPserverPort = 0;  // dist

//环信
    public static Context applicationContext;
    private static WowuApp instance;
    // login user name
    public final String PREF_USERNAME = "username";

    /**
     * nickname for current user, the nickname instead of ID be shown when user receive notification from APNs
     */
    public static String currentUserNick = "";

    @Override
    public void onCreate() {
//        MultiDex.install(this);
        super.onCreate();
        applicationContext = this;
        instance = this;

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


//加入环信


        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
// 如果APP启用了远程的service，此application:onCreate会被调用2次
// 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
// 默认的APP会在以包名为默认的process name下运行，如果查到的process name不是APP的process name就立即返回

        if (processAppName == null ||!processAppName.equalsIgnoreCase(applicationContext.getPackageName())) {
            Log.e("WowuApp", "enter the service process!");

            // 则此application::onCreate 是被service 调用的，直接返回
            return;
        }



        //init demo helper
        DemoHelper.getInstance().init(applicationContext);
        RedPacket.getInstance().initContext(applicationContext);

        EMOptions options = new EMOptions();
//  默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(false);
//  初始化
        EMClient.getInstance().init(applicationContext, options);
//  在做打包混淆时，关闭debug模式，避免消耗不必要的资源
        EMClient.getInstance().setDebugMode(true);

    }


//    登陆环信的用户名和密码：
//    username=userid
     public static  String hx_pwd ="EdenPassword";


    private String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = this.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                // Log.d("Process", "Error>> :"+ e.toString());
            }
        }
        return processName;
    }

    public static WowuApp getInstance() {
        return instance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


    //  记录位置坐标
    public static String latitude = "716728";
    public static String longitude = "31.196694";
    public static String Radius = "";

    public static final String PREFERENCE_KEY = "com.wowu.im";
//    public static String userId = "";
//    public static String userName = "";

    public static final String ALL_CachePathDirTemp = "/mnt/sdcard/Downloads/";//下载文件的暂存路径
    public static String userImagePath = OkHttpUtils.serverAbsolutePath + "/appIcon/userIcon/";  //头像缩略图


    //表示请求服务器类型
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");


    //########Media   媒体相关##########################################
//    POST Media/PreUploadImage 预上传图片 {  "Content": "sample string 1",  "FromIOS": true }
//    Content	 Base64编码的图像  string    Required
//    FromIOS	 是否来自IOS的Base64  boolean   None.
    public static String PreUploadImageURL = OkHttpUtils.serverAbsolutePath + "Media/PreUploadImage";

    //########User   账户相关##########################################fv
//     登录 {   "PhoneNumber": "sample string 1", "Password": "sample string 2" }
    public static String LoginURL = OkHttpUtils.serverAbsolutePath + "Account/Login";
    //  用户注册，
    public static String RegisterURL = OkHttpUtils.serverAbsolutePath + "Account/Register";
    //    发送短信验证码  {  "PhoneNumber": "sample string 1", "Type": 2 }
    public static String SmsValidateURL = OkHttpUtils.serverAbsolutePath + "Account/SendSmsValidateCode";
    //    验证短信验证码 {   "PhoneNumber": "sample string 1", "Type": 2,    "SmsValidateCode": "sample string 3"  }    短信用途(0：注册，1：找回密码)
    public static String ValidateCodeURL = OkHttpUtils.serverAbsolutePath + "Account/ValidateSmsCode";
    //    设置用户的性格 POST Account/SetDisposition?dispositionId={dispositionId}
    public static String SetDispositionURL = OkHttpUtils.serverAbsolutePath + "Account/SetDisposition";
    //    GET Account/GetDispositionInfo 获取用户的性格
    public static String GetDispositionInfoURL = OkHttpUtils.serverAbsolutePath + "Account/GetDispositionInfo";
    //    POST Account/SubmitLocation?lon={lon}&lat={lat} 提交位置
    public static String SubmitLocationURL = OkHttpUtils.serverAbsolutePath + "Account/SubmitLocation?lon=" + longitude + "&lat=" + latitude;


    //    GET 获取用户的照片
    public static String GetPhotosURL= OkHttpUtils.serverAbsolutePath + "Account/GetPhotos";
    //    POST  退出登录
    public static String LogoutURL= OkHttpUtils.serverAbsolutePath + "Account/Logout";
    //    POST 修改密码 {    "OldPassword": "sample string 1","NewPassword": "sample string 2"}
    public static String ChangePasswordURL= OkHttpUtils.serverAbsolutePath + "Account/ChangePassword";
    //    POST找回密码{ "NewPassword": "sample string 1", "SmsValidateCode": "sample string 2","PhoneNumber":"sample string 3"}
    public static String FindPasswordURL=  OkHttpUtils.serverAbsolutePath + "Account/FindPassword";
/*    //    POST 更新用户资料
    {        "Name": "sample string 1",
            "Birthday": "2016-07-08T13:59:47.103956+08:00",
            "HomeId": 2,
            "Description": "sample string 3",
            "MaritalStatus": 4 情感状态,
            "Job": "sample string 5",
            "Company": "sample string 6",
            "School": "sample string 7",
            "JobAddress": "sample string 8",
            "LifeAddress": "sample string 9",
            "DailyAddress": "sample string 10",
            "Photos": [        {   //更新的用户照片，第一个为头像
            "PhotoId": "sample string 1",
                "Base64Image": "sample string 2"
        },        {
            "PhotoId": "sample string 1",
                "Base64Image": "sample string 2"
        }        ]    }*/
    public static String UpdateUserInfoURL= OkHttpUtils.serverAbsolutePath + "Account/UpdateUserInfo";



    //########Disposition   性格相关##########################################
//    GET Disposition/DispositionList  性格列表
    public static String DispositionListURL = OkHttpUtils.serverAbsolutePath + "Disposition/DispositionList";
    //    GET Disposition/QuestionList   测试题列表
    public static String QuestionListURL = OkHttpUtils.serverAbsolutePath + "Disposition/QuestionList?isSimple=false";
    public static String Question_JINGJIANListURL = OkHttpUtils.serverAbsolutePath + "Disposition/QuestionList?isSimple=true";
    //    POST Disposition/SubmitAnswer   提交答案   [  {  "QuestionId": "sample string 1",   "Answer": "sample string 2"  },
// {  "QuestionId": "sample string 1", "Answer": "sample string 2"  } ]
    public static String SubmitAnswerURL = OkHttpUtils.serverAbsolutePath + "Disposition/SubmitAnswer";



    //########Chat   聊天室##########################################
//GET Chat/GetNearbyUser?lon={lon}&lat={lat}&pageIndex={pageIndex} 获取附近的用户
    public static String GetNearbyUserURL = OkHttpUtils.serverAbsolutePath + "Chat/GetNearbyUser" ;
//    GET 获取目标用户的信息
    public static String GetUserInfoURL= OkHttpUtils.serverAbsolutePath + "Chat/GetUserInfo" ;//?userId={userId}
//    POST 三观配
    public static String MatchURL= OkHttpUtils.serverAbsolutePath + "Chat/Match";//?userId={userId}
//    POST  根据一组用户ID获取用户的头像
    public static String UserPhotoURL = OkHttpUtils.serverAbsolutePath + "Chat/UserPhoto";



    //########Friend  好友相关##########################################
//     获取好友列表
    public static String GetFriendsURL = OkHttpUtils.serverAbsolutePath + "Friend/GetFriends" ;//?lon={lon}&lat={lat}
//    POST   关注某人
    public static String FocusFriendsURL = OkHttpUtils.serverAbsolutePath + "Friend/Focus" ;//userId={userId}
//    POST 取消关注
    public static String RemoveFocusURL = OkHttpUtils.serverAbsolutePath + "Friend/RemoveFocus" ;//?userId={userId}
//    POST  删除好友
    public static String RemoveFriendURL = OkHttpUtils.serverAbsolutePath + "Friend/RemoveFriend" ;//?friendId={friendId}



    //######## Pay##########################################
//    POST
    public static String  WechatNotifyURL = OkHttpUtils.serverAbsolutePath +  "api/Pay/WechatNotify" ;
//    POST
    public static String AliypayNotifyURL = OkHttpUtils.serverAbsolutePath +  "api/Pay/AliypayNotify" ;
//    GET
    public static String  PayNotifyURL = OkHttpUtils.serverAbsolutePath +  "api/Pay/PayNotify";//?orderNumber={orderNumber}&payOk={payOk}


    public static String UserId = "";
    public static String PhoneNumber = "";
    public static String Password = "";
    public static String SmsValidateCode = "";
    public static int Gender = 0;
    public static String Name = "";
    public static String iconPath = "";//用户头像路径
//    public static String token = "";


////        环信IM appkey
//    public static String ClientId="YXA6HWVlsDtsEeae3XlMmSCTxA";
//    public static String ClientSecret="YXA6URQvvr4B_mKfhXOnn3RsUdY62zU";








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

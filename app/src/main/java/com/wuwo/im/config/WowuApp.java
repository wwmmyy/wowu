package com.wuwo.im.config;

import android.app.ActivityManager;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.easemob.redpacketsdk.RedPacket;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.chatuidemo.DemoHelper;
import com.net.grandcentrix.tray.AppPreferences;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.squareup.okhttp.MediaType;
import com.wuwo.im.service.LocationService;
import com.wuwo.im.util.LocalImageHelper;
import com.wuwo.im.util.LogUtils;
import com.wuwo.im.util.UtilsTool;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.service.LoadserverdataService;

import org.json.JSONObject;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import im.imxianzhi.com.imxianzhi.R;

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
//    坐标变化时及时发出广播，更新相应列表
    public static String ACTION_NAME = "LocationChanged";
    SharedPreferences settings;
    //    public LocationClient mLocationClient;
    public LocationService locationService;
    public MyLocationListener mMyLocationListener;
    public static final String LOG_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/errorLog";
    //    public static String OkHttpUtils.serverAbsolutePath = "http://58.246.138.178:8040/gzServices/ServiceProvider.ashx"; // dist
    //此为即时通讯消息推送的服务器端ip及端口
//    public static String XMPPserverIP = serverIP;  // dist
    public static String XMPPserverIP = " ";
    public static int XMPPserverPort = 0;  // dist

    //环信
    public static Context applicationContext;
    public static WowuApp instance;
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


//        // 用来监控卡顿的
//        BlockCanary.install(this, new AppBlockCanaryContext()).start();

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
                .discCacheSize(60 * 1024 * 1024)//
                .discCacheFileCount(60)// 缓存一百张图片
                .writeDebugLogs()//
                .build();//
        ImageLoader.getInstance().init(config);

        //本地图片辅助类初始化
        LocalImageHelper.init(this);

//假如空指针的话application保存的东西就会清空重新加载
        settings = this.getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
        PhoneNumber = settings.getString("PhoneNumber", "");
        Name = settings.getString("Name", "");
        XianZhiNumber = settings.getString("userNumber", "");
        Thread.setDefaultUncaughtExceptionHandler(restartHandler); // 程序崩溃时触发线程  以下用来捕获程序崩溃异常


//        添加地图定位功能
/*        mLocationClient = new LocationClient(this.getApplicationContext());
        mMyLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mMyLocationListener);*/

        /***
         * 初始化定位sdk，建议在Application中创建
         */
        try {
            mMyLocationListener = new MyLocationListener();
            locationService = new LocationService(getApplicationContext());
//        mVibrator =(Vibrator)getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
//        WriteLog.getInstance().init(); // 初始化日志
            locationService.setLocationOption(locationService.getDefaultLocationClientOption());
            SDKInitializer.initialize(getApplicationContext());
            locationService.registerListener(mMyLocationListener);
            locationService.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

//加入环信


        //假如空指针的话application保存的东西就会清空重新加载
        settings = this.getSharedPreferences(WowuApp.PREFERENCE_KEY, MODE_PRIVATE);
        UserId = settings.getString("UserId", "");
        OkHttpUtils.token = settings.getString("token", "");
        iconPath = settings.getString("iconPath", "");
        Gender = settings.getInt("Gender", 0);


        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
// 如果APP启用了远程的service，此application:onCreate会被调用2次
// 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
// 默认的APP会在以包名为默认的process name下运行，如果查到的process name不是APP的process name就立即返回

        if (processAppName == null || !processAppName.equalsIgnoreCase(applicationContext.getPackageName())) {
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
//        options.setShowNotificationInBackgroud(true);

//  初始化
        EMClient.getInstance().init(applicationContext, options);
//  在做打包混淆时，关闭debug模式，避免消耗不必要的资源
        EMClient.getInstance().setDebugMode(true);
        registerLocationBoradcastReceiver();

    }


    //    登陆环信的用户名和密码：
//    username=userid
    public static String hx_pwd = "EdenPassword";


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

    //微信支付
    public static final String WeChat_APP_ID = "wxc1cc2ed7c5a30810";
    //QQ分享
    public static final String QQ_APP_ID = "1105597236";
    public static final String QQ_APP_KEY = "FWJiDEZW9DUJm3lL";
    public static final String shareURL = "http://weixin.imxianzhi.com/share?download=true";
    public static String UserId = "";
    public static String PhoneNumber = "";
    public static String XianZhiNumber = "";
    public static String Password = "";
    public static String SmsValidateCode = "";
    public static int Gender = 1;
    public static String Name = "";
    public static String iconPath = "";//用户头像路径

    public static final String PREFERENCE_KEY = "com.wowu.im";
    public static final String ALL_CachePathDirTemp = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Downloads/";//下载文件的暂存路径 /mnt/sdcard
    //    /storage/emulated/0/Pictures/锁屏壁纸/I01020052.jpg
    public static final String tempPicPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Downloads/";//下载文件的暂存路径  /mnt/sdcard/Downloads/
    public static String userImagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Downloads/userIcon/";  //头像缩略图  OkHttpUtils.serverAbsolutePath
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
    public static String SubmitLocationURL = OkHttpUtils.serverAbsolutePath + "Account/SubmitLocation";
    //    POST Account/VipInfo
    public static String VipInfoURL = OkHttpUtils.serverAbsolutePath + "Account/VipInfo";
    //    POST Account/SubmitSuggestion   提交意见
    public static String SubmitSuggestionURL = OkHttpUtils.serverAbsolutePath + "Account/SubmitSuggestion";
    //    GET 获取用户的照片
    public static String GetPhotosURL = OkHttpUtils.serverAbsolutePath + "Account/GetPhotos";
    //    POST  退出登录
    public static String LogoutURL = OkHttpUtils.serverAbsolutePath + "Account/Logout";
    //    POST 修改密码 {    "OldPassword": "sample string 1","NewPassword": "sample string 2"}
    public static String ChangePasswordURL = OkHttpUtils.serverAbsolutePath + "Account/ChangePassword";
    //    POST找回密码{ "NewPassword": "sample string 1", "SmsValidateCode": "sample string 2","PhoneNumber":"sample string 3"}
    public static String FindPasswordURL = OkHttpUtils.serverAbsolutePath + "Account/FindPassword";


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
    public static String UpdateUserInfoURL = OkHttpUtils.serverAbsolutePath + "Account/UpdateUserInfo";
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
    public static String GetNearbyUserURL = OkHttpUtils.serverAbsolutePath + "Chat/GetNearbyUser";
    //    GET 获取目标用户的信息
    public static String GetUserInfoURL = OkHttpUtils.serverAbsolutePath + "Chat/GetUserInfo";//?userId={userId}  userId={userId}&lon={lon}&lat={lat}
    //    POST 三观配
    public static String MatchURL = OkHttpUtils.serverAbsolutePath + "Chat/Match";//?userId={userId}
    //    POST  根据一组用户ID获取用户的头像
    public static String UserPhotoURL = OkHttpUtils.serverAbsolutePath + "Chat/UserPhoto";
    //    POST Logger/Write            写日志
    public static String LoggerWriteURL = OkHttpUtils.serverAbsolutePath + "Logger/Write";
    //########Friend  好友相关##########################################
//     获取好友列表
    public static String GetFriendsURL = OkHttpUtils.serverAbsolutePath + "Friend/GetFriends";//?lon={lon}&lat={lat}
    //    POST   关注某人
    public static String FocusFriendsURL = OkHttpUtils.serverAbsolutePath + "Friend/Focus";//userId={userId}
    //    POST 取消关注
    public static String RemoveFocusURL = OkHttpUtils.serverAbsolutePath + "Friend/RemoveFocus";//?userId={userId}
    //    POST  删除好友
    public static String RemoveFriendURL = OkHttpUtils.serverAbsolutePath + "Friend/RemoveFriend";//?friendId={friendId}
    //    POST Friend/SyncContacts  同步通讯录好友
    public static String FriendSyncContactsURL = OkHttpUtils.serverAbsolutePath + "Friend/SyncContacts";//?friendId={friendI
    //    GET Friend/ContactsRecommend  获取通讯录推荐
    public static String FriendContactsRecommendURL = OkHttpUtils.serverAbsolutePath + "Friend/ContactsRecommend";//?friendId={friendI
    //    GET System/VersionInfo           版本信息
    public static String SystemVersionInfoURL = OkHttpUtils.serverAbsolutePath + "System/VersionInfo";
    //  GET   推荐好友
    public static String ChatRecommendFriendURL = OkHttpUtils.serverAbsolutePath + "Chat/RecommendFriend";
    //######## Pay##########################################
//    POST
    public static String WechatNotifyURL = OkHttpUtils.serverAbsolutePath + "api/Pay/WechatNotify";
    //    POST
    public static String AliypayNotifyURL = OkHttpUtils.serverAbsolutePath + "api/Pay/AliypayNotify";
    //    GET
    public static String PayNotifyURL = OkHttpUtils.serverAbsolutePath + "api/Pay/PayNotify";//?orderNumber={orderNumber}&payOk={payOk}
    //   购买VIP   POST { "VipType": 1,"PaymentMethod": "sample string 2"} PaymentMethod 支付方式,微信：wechat，支付宝：alipay
//    return {"OrderId":"","OrderNumber":"","Total":0,"PrepayId":"","Wechat":{"AppId":"","PartnerId":"","PrepayId":"","PackageValue":"","NonceStr":"","TimeStamp":"1461397867","Sign":""}}
    public static String OrderBuyURL = OkHttpUtils.serverAbsolutePath + "Order/Buy";
    //    GET Chat/Search?key={key}&pageIndex={pageIndex}	    根据昵称或者先知号搜索
    public static String ChatSearchURL = OkHttpUtils.serverAbsolutePath + "Chat/Search";
    //    GET Disposition/GetManualDialog?id={id} 根据性格谱获取性格弹框
    public static String GetManualDialogURL = OkHttpUtils.serverAbsolutePath + "Disposition/GetManualDialog";
    //    GET Disposition/GetDispositionNicknames?id={id} 获取性格昵称
    public static String GetDispositionNicknamesURL = OkHttpUtils.serverAbsolutePath + "Disposition/GetDispositionNicknames";
    //    GET Account/GetMatchMeUsers?pageIndex={pageIndex}&pageSize={pageSize}    谁配过我
    public static String GetMatchMeUsersURL = OkHttpUtils.serverAbsolutePath + "Account/GetMatchMeUsers";
    //    GET Account/GetWatchMeUsers?pageIndex={pageIndex}&pageSize={pageSize}    谁看过我
    public static String GetWatchMeUsersURL = OkHttpUtils.serverAbsolutePath + "Account/GetWatchMeUsers";
    //    POST Account/UpdateGender?gender={gender}    修改性别
    public static String UpdateGenderURL = OkHttpUtils.serverAbsolutePath + "Account/UpdateGender";
    //    POST Friend/SetRemarkName            设置备注名称
    public static String SetRemarkNameURL = OkHttpUtils.serverAbsolutePath + "Friend/SetRemarkName";
    //    GET Friend/Blacklist?pageIndex={pageIndex}    黑名单
    public static String BlacklistURL = OkHttpUtils.serverAbsolutePath + "Friend/Blacklist";
    //    POST Friend/RemoveBlacklist?id={id}    删除黑名单
    public static String RemoveBlacklistURL = OkHttpUtils.serverAbsolutePath + "Friend/RemoveBlacklist";
    //    POST Friend/MoveToBlacklist?userId={userId}    添加黑名单
    public static String MoveToBlacklistURL = OkHttpUtils.serverAbsolutePath + "Friend/MoveToBlacklist";
    //    GET Disposition/GetTestHistory?pageIndex={pageIndex}    获取测试列表
    public static String GetTestHistoryURL = OkHttpUtils.serverAbsolutePath + "Disposition/GetTestHistory";
    //    POST Disposition/SetDispositionFromHistory?historyId={historyId}    把测评历史设为当前性格
    public static String SetDispositionFromHistoryURL = OkHttpUtils.serverAbsolutePath + "Disposition/SetDispositionFromHistory";
//    POST api/Chat?userId={userId}&catalog={catalog}&subCatalog={subCatalog}    举报
    public static String apiChatURL = OkHttpUtils.serverAbsolutePath + "api/Chat";
//    GET Disposition/GetTendencyDialog            获取倾向度弹框
    public static String GetTendencyDialogURL = OkHttpUtils.serverAbsolutePath + "Disposition/GetTendencyDialog";
//    GET Disposition/GetDimensionsDialog?dimession={dimession}    获取性格纬度弹框
    public static String GetDimensionsDialogURL = OkHttpUtils.serverAbsolutePath + "Disposition/GetDimensionsDialog";
//    GET Disposition/GetMbtiInfo            获取MBTI弹框信息
    public static String GetMbtiInfoURL = OkHttpUtils.serverAbsolutePath + "Disposition/GetMbtiInfo";
//    GET Disposition/GetRgInfo            获取荣格弹框信息
    public static String GetRgInfoURL = OkHttpUtils.serverAbsolutePath + "Disposition/GetRgInfo";
    //    GET Disposition/GetDispositionInfoFromHistory?historyId={historyId}    根据测评历史获得历史测评信息
    public static String GetDispositionInfoFromHistoryURL = OkHttpUtils.serverAbsolutePath + "Disposition/GetDispositionInfoFromHistory";
    //    GET Disposition/GetManualInfo?historyId={historyId}    根据历史测评获取性格谱信息
    public static String GetManualInfoURL = OkHttpUtils.serverAbsolutePath + "Disposition/GetManualInfo";
    public static String popShareURL =  "http://weixin.imxianzhi.com/Share/Dialog";//OkHttpUtils.serverAbsolutePath +

//    http://weixin.imxianzhi.com/Share/Dialog?type=2&id=f9800e94-b2b9-434a-b603-b86adb39ea8b
//GET Disposition/GetManualDescDialog             获取性格谱介绍弹框
    public static String GetManualDescDialog = OkHttpUtils.serverAbsolutePath + "Disposition/GetManualDescDialog";


////        环信IM appkey
//    public static String ClientId="YXA6HWVlsDtsEeae3XlMmSCTxA";
//    public static String ClientSecret="YXA6URQvvr4B_mKfhXOnn3RsUdY62zU";




/*    先知先觉APP在“注册”、“会员”、“发现”中一些共性的功能，我已经做成网页，只需要通过webview显示就可以，其中“发现”页的“版本预览”是通过后台控制的，具体地址如下：
    条款：http://api.imxianzhi.com/provisions.html
    隐私权政策:http://api.imxianzhi.com/privacy.html
    先知先觉会员协议:http://api.imxianzhi.com/vipprotocol.html
    版本预览：http://api.imxianzhi.com/Version/Preview/*/

    public static String TiaoKuanURL = "http://api.imxianzhi.com/provisions.html";
    public static String YinSiZhengCeURL = "http://api.imxianzhi.com/privacy.html";
    public static String XieYiURL = "http://api.imxianzhi.com/vipprotocol.html";
    public static String YuLanURL = "http://api.imxianzhi.com/Version/Preview/";


    // 创建服务用于捕获崩溃异常
    private Thread.UncaughtExceptionHandler restartHandler = new Thread.UncaughtExceptionHandler() {
        public void uncaughtException(Thread thread, Throwable ex) {
            UtilsTool.saveErrorFile(ex, "系统崩溃信息：" + System.currentTimeMillis() + ".txt");
        }
    };


    //  记录位置坐标  31.196542,121.716733
    private String latitude = "31.196542";
    private String longitude = "121.716733";
    public static String Radius = "";
    public static float LocationSpeed = 5;
    public static int LocationTime = 3 * 1000;//表示定位时间间隔60 * 1000

    int locationTimes = 0;//如果连续8次定位数据不变，则将定位的时间间隔变为20分钟一次
    int locationNoChangLength = 10 * 60 * 1000; // 长时间坐标不变后定位的时间间隔
    public static String RESET_LOCATION_TIME = "RESET_LOCATION_TIME";


    /***
     * 实现实位回调监听
     */
    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(final BDLocation location) {
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
            LogUtils.i("获取到的定位信息为：：：", location.getLatitude() + "：：：" + location.getLongitude() + "：：：" + location.getRadius());
////                    UtilsTool.saveErrorFile(sb.toString(), "获取到的坐标为11.txt");
//将用户位置返回个服务器
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mobileLocationLog(location.getLatitude(), location.getLongitude(), location.getSpeed());


                    Intent intent = new Intent(WowuApp.ACTION_NAME);
                    intent.putExtra("latitude", location.getLatitude() + "");
                    intent.putExtra("longitude", location.getLongitude() + "");
                    sendBroadcast(intent);

                    //长时间定位坐标不变则将坐标拉长
                    if (latitude.equals(location.getLatitude() + "")) {
                        locationTimes++;
                    } else {
                        locationTimes = 0;
/*                        Intent intentLocation = new Intent(WowuApp.RESET_LOCATION_TIME);
                        intentLocation.putExtra("locationNoChangLength",LocationTime+"");
                        sendBroadcast(intentLocation);*/

       /*                 LocationClientOption option= mLocationClient.getLocOption();
                        option.setScanSpan(LocationTime);
                        mLocationClient.setLocOption(option);*/

//                        LocationClientOption tempSet = locationService.getDefaultLocationClientOption();
//                        tempSet.setScanSpan(LocationTime);
//                        locationService.setLocationOption(tempSet);
//                        locationService.start();

                        resetLocationOption(LocationTime);
                    }

                    if (locationTimes > 6) {
                        locationTimes = 0;
/*                        Intent intentLocation = new Intent(WowuApp.RESET_LOCATION_TIME);
                        intentLocation.putExtra("locationNoChangLength",locationNoChangLength+"");
                        sendBroadcast(intentLocation);*/
                        LogUtils.i("获取到的定位信息为：：：", "：：：进入了时间间隔变大阶段");

//                        LocationClientOption tempSet = locationService.getDefaultLocationClientOption();
//                        tempSet.setScanSpan(locationNoChangLength);
//                        locationService.setLocationOption(tempSet);
//                        locationService.start();

                        resetLocationOption(locationNoChangLength);

                    }


                    latitude = location.getLatitude() + "";
                    longitude = location.getLongitude() + "";

                    AppPreferences appPreferences = new AppPreferences(getApplicationContext()); // this Preference comes for free from the library
// save a key value pair
                    appPreferences.put("latitude", location.getLatitude() + "");
                    appPreferences.put("longitude", location.getLongitude() + "");

                }
            }).start();
        }
    }


    int _minSpeed = 3;
    int _minFilter = 5;
    int _minInteval = 3;
    int _minInteval_Default = 2000000;
    int locationCount = 3;

    //  登录以后即使不懂，也要同步一次位置到服务器
//    boolean uploadLocation = false;

    public static final int ALIPAY = 10;

    /**
     * @Description: 将终端的位置坐标上传到服务器
     */
    public void mobileLocationLog(double mlatitude, double mlongitude, float speed) {
//        /**
//         *  规则: 如果速度小于minSpeed m/s 则把触发范围设定为5m
//         *  否则将触发范围设定为minSpeed*minInteval
//         *  此时若速度变化超过10% 则更新当前的触发范围(这里限制是因为不能不停的设置distanceFilter,
//         *  否则uploadLocation会不停被触发)
//         */
//        - (void)adjustDistanceFilter:(CLLocationManager *)locationManager location:(CLLocation*)location{
////        NSLog(@"adjust:%f",location.speed);
//            if (location.speed < _minSpeed)
//            {
//                if (fabs(locationManager.distanceFilter - _minFilter) > 0.1f)
//                {
//                    locationManager.distanceFilter = _minFilter;
//                }
//            }
//            else
//            {
//                CGFloat lastSpeed = locationManager.distanceFilter / _minInteval;
//                if ((fabs(lastSpeed - location.speed) / lastSpeed > 0.1f) || (lastSpeed < 0)){
//                    CGFloat newSpeed = (int)(location.speed + 0.5f);
//                    CGFloat newFilter = newSpeed * _minInteval;
//                    locationManager.distanceFilter = newFilter;
//                }
//            }
//        }

        LogUtils.i("发送坐标位置给服务器0www：：：", OkHttpUtils.token + ":::::::" + getSharedPreferences(WowuApp.PREFERENCE_KEY, MODE_PRIVATE).getString("token", ""));
//        LatLng end_default = new LatLng( 31.196542,121.716733);
        AppPreferences appPreferences = new AppPreferences(getApplicationContext()); // this Preference comes for free from the library

        LatLng start = new LatLng(Float.parseFloat(appPreferences.getString("latitude", "0")), Float.parseFloat(appPreferences.getString("longitude", "0")));
        LatLng end = new LatLng(mlatitude, mlongitude);

        locationCount++;
        if (appPreferences.getString("latitude", "0").equals("0") || getDistance(start, end) > _minInteval_Default) {  //防止初始化时定位不准，偏移过大
//            uploadLocation2Server(31.196542,121.716733);
            uploadLocation2Server(mlatitude, mlongitude);
            locationCount = 0;
        } else if (getDistance(start, end) > _minInteval || locationCount > 3) {
//                uploadLoaction(mlatitude, mlongitude);
            uploadLocation2Server(mlatitude, mlongitude);
            locationCount = 0;
        }
    }

    private void updateLocation(double mlatitude, double mlongitude, LatLng start, LatLng end, LatLng end_default) {

    }

    private boolean uploadLocation2Server(double mlatitude, double mlongitude) {
//        if (!getSharedPreferences(WowuApp.PREFERENCE_KEY, MODE_PRIVATE).getString("token", "").equals("")) {
        latitude = mlatitude + "";
        longitude = mlongitude + "";
        try {
            JSONObject json = new JSONObject();
            json.put("lon", mlongitude + "");
            json.put("lat", mlatitude + "");
            new LoadserverdataService(null).loadPostJsonRequestData(WowuApp.JSON, WowuApp.SubmitLocationURL + "?lon=" + mlongitude + "&lat=" + mlatitude, json.toString(), 0);
            LogUtils.i("wowuapp 发送坐标位置给服务器：：1：", mlongitude + ":::::::" + mlatitude);
//                uploadLocation=true;

/*                SharedPreferences.Editor editor = settings.edit();
                editor.putString("latitude", mlatitude+"");
                editor.putString("longitude", mlongitude+"");
                editor.commit();*/


//             坐标变化时及时发出广播，更新相应列表
            Intent intent = new Intent(WowuApp.ACTION_NAME);
            intent.putExtra("latitude", mlatitude + "");
            intent.putExtra("longitude", mlongitude + "");
            sendBroadcast(intent);


            return true;
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.i("发送坐标位置给服务器 异常：：：", mlongitude + ":::::::" + mlatitude);
        }
//        }
        return false;
    }

    /**
     *也是发送坐标，暂时不用这个
     * @param mlatitude
     * @param mlongitude
     */
/*    private void uploadLoaction(final double mlatitude, final double mlongitude) {
//        if (OkHttpUtils.token != null && !OkHttpUtils.token.equals("")) {
//            try {
//                JSONObject json = new JSONObject();
//                json.put("lon", mlongitude + "");
//                json.put("lat", mlatitude + "");
//                new LoadserverdataService(null).loadPostJsonRequestData(WowuApp.JSON, WowuApp.SubmitLocationURL, json.toString(), 0);
//                uploadLocation = true;
//                LogUtils.i("发送坐标位置给服务器：：：", mlongitude + ":::::::" + mlatitude);
//
//            } catch (Exception e) {
//                e.printStackTrace();
//                LogUtils.i("发送坐标位置给服务器 异常：：：", mlongitude + ":::::::" + mlatitude);
//            }
//        }

        LogUtils.i("发送坐标位置给服务器：：3：", mlongitude + ":::::::" + mlatitude);
        new Thread(new Runnable() {
            @Override
            public void run() {
                PostStringBuilder psb = OkHttpUtils.postString();
//                判断是否已经有token了，有的话在请求头加上
                LogUtils.i("发送坐标位置给服务器00：：：",  getSharedPreferences(WowuApp.PREFERENCE_KEY, MODE_PRIVATE).getString("token", "")+ ":::::::" + mlatitude);
                if (  ! getSharedPreferences(WowuApp.PREFERENCE_KEY, MODE_PRIVATE).getString("token", "").equals("")) {
                    psb.addHeader("Authorization", "Bearer " +  getSharedPreferences(WowuApp.PREFERENCE_KEY, MODE_PRIVATE).getString("token", ""));
                    try {
                        LogUtils.i("发送坐标位置给服务器001：：：",  getSharedPreferences(WowuApp.PREFERENCE_KEY, MODE_PRIVATE).getString("token", "") + ":::::::" + mlatitude);
                        JSONObject json = new JSONObject();
                        json.put("lon", mlongitude + "");
                        json.put("lat", mlatitude + "");
                        psb.addHeader("content-type", "application/json")
                                .url(WowuApp.SubmitLocationURL+"?lon="+mlongitude+"&lat="+mlatitude)
                                .mediaType(WowuApp.JSON)
                                .content(json.toString())
                                .build()
                                .execute(new StringCallback() {
                                    @Override
                                    public void onError(Request request, Exception e) {
                                        e.printStackTrace();
                                        if (e.getMessage() != null) {
                                            LogUtils.i("发送坐标位置给服务器返回值：：：", e.getMessage() + ":::::::");
                                        }
                                    }
                                    @Override
                                    public void onResponse(String response) {
                                        LogUtils.i("发送坐标位置给服务器返回值2：：：", response + ":::::::");
                                    }
                                });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        ).start();
    }*/


    /**
     * 计算两点之间距离
     *
     * @param start
     * @param end
     * @return 米
     */
    public double getDistance(LatLng start, LatLng end) {
        double lat1 = (Math.PI / 180) * start.latitude;
        double lat2 = (Math.PI / 180) * end.latitude;

        double lon1 = (Math.PI / 180) * start.longitude;
        double lon2 = (Math.PI / 180) * end.longitude;

//      double Lat1r = (Math.PI/180)*(gp1.getLatitudeE6()/1E6);
//      double Lat2r = (Math.PI/180)*(gp2.getLatitudeE6()/1E6);
//      double Lon1r = (Math.PI/180)*(gp1.getLongitudeE6()/1E6);
//      double Lon2r = (Math.PI/180)*(gp2.getLongitudeE6()/1E6);

        //地球半径
        double R = 6371;

        //两点间距离 km，如果想要米的话，结果*1000就可以了
        double d = Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon2 - lon1)) * R;
        LogUtils.i("发送坐标位置给服务器两者距离：：：", d + ":::::::");
        return d * 1000;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }


    public void registerLocationBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(WowuApp.RESET_LOCATION_TIME);
        //注册广播
        registerReceiver(mLocationBroadcastReceiver, myIntentFilter);
    }

    private BroadcastReceiver mLocationBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(WowuApp.RESET_LOCATION_TIME)) {
                final int timeLength = Integer.parseInt(intent.getStringExtra("locationNoChangLength"));
//                MyToast.show(context,timeLength+";xx");
                if (timeLength != 0) {
                    resetLocationOption(timeLength);
                }
            }

        }
    };

    private void resetLocationOption(final int timeLength) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    LocationClientOption tempSet = locationService.getDefaultLocationClientOption();
                    tempSet.setScanSpan(timeLength);
                    locationService.setLocationOption(tempSet);
                    locationService.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    public String getCachePath() {
        File cacheDir;
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            cacheDir = getExternalCacheDir();
        else
            cacheDir = getCacheDir();
        if (!cacheDir.exists())
            cacheDir.mkdirs();
        return cacheDir.getAbsolutePath();
    }
}

package com.wuwo.im.activity;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.IntentCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.redpacketui.RedPacketConstant;
import com.easemob.redpacketui.utils.RedPacketUtil;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chatuidemo.Constant;
import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.chatuidemo.db.InviteMessgeDao;
import com.hyphenate.chatuidemo.db.UserDao;
import com.hyphenate.chatuidemo.runtimepermissions.PermissionsManager;
import com.hyphenate.chatuidemo.runtimepermissions.PermissionsResultAction;
import com.hyphenate.chatuidemo.ui.ChatActivity;
import com.hyphenate.chatuidemo.ui.ConversationListFragment;
import com.hyphenate.chatuidemo.ui.GroupsActivity;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.util.EMLog;
import com.squareup.otto.Bus;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.wuwo.im.config.ExitApp;
import com.wuwo.im.config.OneMapOttoBus;
import com.wuwo.im.config.WowuApp;
import com.wuwo.im.fragement.Portal_ContactFragment;
import com.wuwo.im.fragement.Portal_FindFragment;
import com.wuwo.im.fragement.Portal_LocalFragment;
import com.wuwo.im.fragement.Portal_OwnerFragment;
import com.wuwo.im.util.UpdateManager;
import com.wuwo.im.util.UtilsTool;
import com.wuwo.im.view.ActionItem;
import com.wuwo.im.view.MyTabWidget;
import com.wuwo.im.view.NoScrollViewPager;
import com.wuwo.im.view.PopupMenu;
import com.wuwo.im.view.TitlePopup;

import java.util.ArrayList;
import java.util.List;

import im.wuwo.com.wuwo.R;

/**
 * @类名:
 * @描述: TODO
 * @作者: 王明远
 * @日期: 2016-5-6 下午5:15:51
 * @修改人:
 * @修改时间:
 * @修改内容:
 * @版本: V1.0
 * @版权:Copyright ©  All rights reserved.
 */
public class MainActivity extends BaseFragementActivity implements MyTabWidget.OnTabSelectedListener, OnClickListener {

    Context mContext = MainActivity.this;
    public static final String TAG = "MainActivity";
    private MyTabWidget mTopIndicator;
    //    private TextView fagui;
    private String username_fromserver;
    ImageView function_add;
    //

    SharedPreferences mSettings;
    private NoScrollViewPager mViewPager;
    public List<Fragment> fragments = new ArrayList<Fragment>();
    private FragmentManager fragmentManager;

    private Bus mOttoBus;
    private ImageView return_back_igw;
    private TextView title_tv;
    private ImageView iv_top_title;
    private PopupMenu popupMenu;
    //负责更换主题的
    private ImageView menu_theme;
    //    private RelativeLayout title_bar;
    private TitlePopup titlePopup;
    private int currentTabIndex;
    private boolean isCurrentAccountRemoved = false;
    // user logged into another device
    public boolean isConflict = false;


    UpdateManager manager;

    /**
     * check if current user account was remove
     */
    public boolean getCurrentAccountRemoved() {
        return isCurrentAccountRemoved;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //make sure activity will not in background if user is logged into another device or removed
/*        if (savedInstanceState != null && savedInstanceState.getBoolean(Constant.ACCOUNT_REMOVED, false)) {
            DemoHelper.getInstance().logout(false, null);
            finish();
            startActivity(new Intent(this, com.hyphenate.chatuidemo.ui.LoginActivity.class));
            return;
        } else if (savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false)) {
            finish();
            startActivity(new Intent(this, com.hyphenate.chatuidemo.ui.LoginActivity.class));
            return;
        }*/


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//强制为竖屏
        setContentView(R.layout.activity_main);

        requestPermissions();

        initIM();


////      启动消息推送接收服务
//        Intent intent = new Intent(this,XMPPService.class);
//        startService(intent);
//        启动对话IM功能
//        chatLogin();


        mSettings = mContext.getSharedPreferences(WowuApp.PREFERENCE_KEY,
                MODE_PRIVATE);

//      弹出浮动小控件
//        popBut();
//        showPopBar();

        //注册广播
//        registerBoradcastReceiver();

////        启动终端定位服务 1
//        Intent startServiceIntent = new Intent(getApplicationContext(), LocationService.class);
//        startServiceIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        getApplicationContext().startService(startServiceIntent);


////      启动终端定位服务2   直接启动   在登录验证中就应该启动
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                // TODO 自动生成的方法存根
//                mLocationClient = ((DistApp)getApplication()).mLocationClient;
//                InitLocation();
//                mLocationClient.start();
//            }
//        }).start();

        // 注册Otto监听器
        mOttoBus = OneMapOttoBus.getInstance();
        mOttoBus.register(this);

        init();

        // 检查文件更新
        manager = new UpdateManager(mContext);
        manager.checkUpdateMe();
    }

    //    private ContactListFragment contactListFragment;
    private void initIM() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String packageName = getPackageName();
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                Intent intent = new Intent();
                intent.setAction(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                startActivity(intent);
            }
        }

        //umeng api
        MobclickAgent.updateOnlineConfig(this);
        UmengUpdateAgent.setUpdateOnlyWifi(false);
        UmengUpdateAgent.update(this);

        if (getIntent().getBooleanExtra(Constant.ACCOUNT_CONFLICT, false) && !isConflictDialogShow) {
            showConflictDialog();
        } else if (getIntent().getBooleanExtra(Constant.ACCOUNT_REMOVED, false) && !isAccountRemovedDialogShow) {
            showAccountRemovedDialog();
        }

        inviteMessgeDao = new InviteMessgeDao(this);
        userDao = new UserDao(this);

//        contactListFragment = new ContactListFragment();
        //register broadcast receiver to receive the change of group from DemoHelper
        registerBroadcastReceiver();

        EMClient.getInstance().contactManager().setContactListener(new MyContactListener());
        //debug purpose only
        registerInternalDebugReceiver();

    }

    @TargetApi(23)
    private void requestPermissions() {
        PermissionsManager.getInstance().requestAllManifestPermissionsIfNecessary(this, new PermissionsResultAction() {
            @Override
            public void onGranted() {
//				Toast.makeText(MainActivity.this, "All permissions have been granted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDenied(String permission) {
//              Toast.makeText(MainActivity.this, "Permission " + permission + " has been denied", Toast.LENGTH_SHORT).show();
            }
        });
    }


    EMMessageListener messageListener = new EMMessageListener() {
        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            // notify new message
            for (EMMessage message : messages) {
                DemoHelper.getInstance().getNotifier().onNewMsg(message);
            }
            refreshUIWithMessage();
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
            for (EMMessage message : messages) {
                EMCmdMessageBody cmdMsgBody = (EMCmdMessageBody) message.getBody();
                final String action = cmdMsgBody.action();//get the action user defined in command message
                if (action.equals(RedPacketConstant.REFRESH_GROUP_RED_PACKET_ACTION) && message.getChatType() == EMMessage.ChatType.GroupChat) {
                    RedPacketUtil.receiveRedPacketAckMessage(message);
                }
            }
            refreshUIWithMessage();
        }

        @Override
        public void onMessageReadAckReceived(List<EMMessage> messages) {
        }

        @Override
        public void onMessageDeliveryAckReceived(List<EMMessage> message) {
        }

        @Override
        public void onMessageChanged(EMMessage message, Object change) {
        }
    };

    private void refreshUIWithMessage() {
        runOnUiThread(new Runnable() {
            public void run() {
                // refresh unread count
                updateUnreadLabel();
                if (currentTabIndex == 0) {
                    // refresh conversation list
                    if (conversationListFragment != null) {
                        conversationListFragment.refresh();
                    }
                }
            }
        });
    }


    private void registerBroadcastReceiver() {
        broadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.ACTION_CONTACT_CHANAGED);
        intentFilter.addAction(Constant.ACTION_GROUP_CHANAGED);
        intentFilter.addAction(RedPacketConstant.REFRESH_GROUP_RED_PACKET_ACTION);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateUnreadLabel();
                updateUnreadAddressLable();
                if (currentTabIndex == 0) {
                    // refresh conversation list
                    if (conversationListFragment != null) {
                        conversationListFragment.refresh();
                    }
                }
//                else if (currentTabIndex == 1) {
//                    if(contactListFragment != null) {
//                        contactListFragment.refresh();
//                    }
//                }
                String action = intent.getAction();
                if (action.equals(Constant.ACTION_GROUP_CHANAGED)) {
                    if (EaseCommonUtils.getTopActivity(MainActivity.this).equals(GroupsActivity.class.getName())) {
                        GroupsActivity.instance.onResume();
                    }
                }
                if (action.equals(RedPacketConstant.REFRESH_GROUP_RED_PACKET_ACTION)) {
                    if (conversationListFragment != null) {
                        conversationListFragment.refresh();
                    }
                }
            }
        };
        broadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    public class MyContactListener implements EMContactListener {
        @Override
        public void onContactAdded(String username) {
        }

        @Override
        public void onContactDeleted(final String username) {
            runOnUiThread(new Runnable() {
                public void run() {
                    if (ChatActivity.activityInstance != null && ChatActivity.activityInstance.toChatUsername != null &&
                            username.equals(ChatActivity.activityInstance.toChatUsername)) {
                        String st10 = getResources().getString(R.string.have_you_removed);
                        Toast.makeText(MainActivity.this, ChatActivity.activityInstance.getToChatUsername() + st10, Toast.LENGTH_LONG)
                                .show();
                        ChatActivity.activityInstance.finish();
                    }
                }
            });
        }

        @Override
        public void onContactInvited(String username, String reason) {
        }

        @Override
        public void onContactAgreed(String username) {
        }

        @Override
        public void onContactRefused(String username) {
        }
    }

    private void unregisterBroadcastReceiver() {
        broadcastManager.unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        manager.closeDialog();
        if (conflictBuilder != null) {
            conflictBuilder.create().dismiss();
            conflictBuilder = null;
        }
        unregisterBroadcastReceiver();

        try {
            unregisterReceiver(internalDebugReceiver);
        } catch (Exception e) {
        }

    }

    /**
     * update unread message count
     */
    public void updateUnreadLabel() {
        int count = getUnreadMsgCountTotal();
        if (count > 0) {
            mTopIndicator.setIndicateDisplay(1, true, " ");// + count
        } else {
            mTopIndicator.setIndicateDisplay(1, false, "");
        }
    }

    /**
     * update the total unread count
     */
    public void updateUnreadAddressLable() {
        runOnUiThread(new Runnable() {
            public void run() {
                int count = getUnreadAddressCountTotal();
                if (count > 0) {
                    mTopIndicator.setIndicateDisplay(2, true, " ");//+ count
                } else {
                    mTopIndicator.setIndicateDisplay(2, false, "");
                }
            }
        });
    }

    /**
     * get unread event notification count, including application, accepted, etc
     *
     * @return
     */
    public int getUnreadAddressCountTotal() {
        int unreadAddressCountTotal = 0;
        unreadAddressCountTotal = inviteMessgeDao.getUnreadMessagesCount();
        return unreadAddressCountTotal;
    }

    /**
     * get unread message count
     *
     * @return
     */
    public int getUnreadMsgCountTotal() {
        int unreadMsgCountTotal = 0;
        int chatroomUnreadMsgCount = 0;
        unreadMsgCountTotal = EMClient.getInstance().chatManager().getUnreadMsgsCount();
        for (EMConversation conversation : EMClient.getInstance().chatManager().getAllConversations().values()) {
            if (conversation.getType() == EMConversation.EMConversationType.ChatRoom)
                chatroomUnreadMsgCount = chatroomUnreadMsgCount + conversation.getUnreadMsgCount();
        }
        return unreadMsgCountTotal - chatroomUnreadMsgCount;
    }

    private InviteMessgeDao inviteMessgeDao;
    private UserDao userDao;

    @Override
    protected void onResume() {
        super.onResume();

        if (!isConflict && !isCurrentAccountRemoved) {
            updateUnreadLabel();
            updateUnreadAddressLable();
        }

        // unregister this event listener when this activity enters the
        // background
        DemoHelper sdkHelper = DemoHelper.getInstance();
        sdkHelper.pushActivity(this);

        EMClient.getInstance().chatManager().addMessageListener(messageListener);
    }

    @Override
    protected void onStop() {
        EMClient.getInstance().chatManager().removeMessageListener(messageListener);
        DemoHelper sdkHelper = DemoHelper.getInstance();
        sdkHelper.popActivity(this);

        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("isConflict", isConflict);
        outState.putBoolean(Constant.ACCOUNT_REMOVED, isCurrentAccountRemoved);
        super.onSaveInstanceState(outState);
    }

 /*   @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }*/

    private android.app.AlertDialog.Builder conflictBuilder;
    private android.app.AlertDialog.Builder accountRemovedBuilder;
    private boolean isConflictDialogShow;
    private boolean isAccountRemovedDialogShow;
    private BroadcastReceiver internalDebugReceiver;
    private ConversationListFragment conversationListFragment;
    private BroadcastReceiver broadcastReceiver;
    private LocalBroadcastManager broadcastManager;

    /**
     * show the dialog when user logged into another device
     */
    private void showConflictDialog() {
        isConflictDialogShow = true;
        DemoHelper.getInstance().logout(false, null);
        String st = getResources().getString(R.string.Logoff_notification);
        if (!MainActivity.this.isFinishing()) {
            // clear up global variables
            try {
                if (conflictBuilder == null)
                    conflictBuilder = new android.app.AlertDialog.Builder(MainActivity.this);
                conflictBuilder.setTitle(st);
                conflictBuilder.setMessage(R.string.connect_conflict);
                conflictBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        conflictBuilder = null;
                        finish();
                        Intent intent = new Intent(MainActivity.this, com.hyphenate.chatuidemo.ui.LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
                conflictBuilder.setCancelable(false);
                conflictBuilder.create().show();
                isConflict = true;
            } catch (Exception e) {
                EMLog.e(TAG, "---------color conflictBuilder error" + e.getMessage());
            }

        }

    }

    /**
     * show the dialog if user account is removed
     */
    private void showAccountRemovedDialog() {
        isAccountRemovedDialogShow = true;
        DemoHelper.getInstance().logout(false, null);
        String st5 = getResources().getString(R.string.Remove_the_notification);
        if (!MainActivity.this.isFinishing()) {
            // clear up global variables
            try {
                if (accountRemovedBuilder == null)
                    accountRemovedBuilder = new android.app.AlertDialog.Builder(MainActivity.this);
                accountRemovedBuilder.setTitle(st5);
                accountRemovedBuilder.setMessage(R.string.em_user_remove);
                accountRemovedBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        accountRemovedBuilder = null;
                        finish();
                        startActivity(new Intent(MainActivity.this, com.hyphenate.chatuidemo.ui.LoginActivity.class));
                    }
                });
                accountRemovedBuilder.setCancelable(false);
                accountRemovedBuilder.create().show();
                isCurrentAccountRemoved = true;
            } catch (Exception e) {
                EMLog.e(TAG, "---------color userRemovedBuilder error" + e.getMessage());
            }

        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getBooleanExtra(Constant.ACCOUNT_CONFLICT, false) && !isConflictDialogShow) {
            showConflictDialog();
        } else if (intent.getBooleanExtra(Constant.ACCOUNT_REMOVED, false) && !isAccountRemovedDialogShow) {
            showAccountRemovedDialog();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
    }


    /**
     * debug purpose only, you can ignore this
     */
    private void registerInternalDebugReceiver() {
        internalDebugReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                DemoHelper.getInstance().logout(false, new EMCallBack() {

                    @Override
                    public void onSuccess() {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                finish();
                                startActivity(new Intent(MainActivity.this, com.hyphenate.chatuidemo.ui.LoginActivity.class));
                            }
                        });
                    }

                    @Override
                    public void onProgress(int progress, String status) {
                    }

                    @Override
                    public void onError(int code, String message) {
                    }
                });
            }
        };
        IntentFilter filter = new IntentFilter(getPackageName() + ".em_internal_debug");
        registerReceiver(internalDebugReceiver, filter);
    }


//    /**
//     * 用来监听设备的状态
//     */
//    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (action.equals(WowuApp.ACTION_NAME)) {
////                MyToast.show(context, "该设备已被挂失，稍后将销毁所有数据", Toast.LENGTH_LONG);
//                String result = intent.getStringExtra("result");
//                if (result != null) {
////                   Log.d("接收到的广播传递结果为：：：：：：：：", result);
//                    if (result.equals("2")) {
//                        SharedPreferences settings = context.getSharedPreferences(WowuApp.PREFERENCE_KEY, MODE_PRIVATE);
//                        SharedPreferences.Editor editor = settings.edit();
//                        editor.clear();
//                        editor.commit();
//                        MyToast.show(context, "该设备已被挂失，稍后将销毁所有数据", Toast.LENGTH_LONG);
//                    } else if (result.equals("3")) {
//                        MyToast.show(context, "该设备已被禁用,稍后退出系统！！", Toast.LENGTH_LONG);
//                    } else if (result.equals("-1") || result.equals("0")) {
//                        MyToast.show(context, "设备未审核或注册，稍后退出", Toast.LENGTH_LONG);
//                    }
//
//                    //  退出应用程序
//                    if (result.equals("-1") || result.equals("0") || result.equals("2") || result.equals("3")) {
//                        final Context tempcontext = context;
//                        (new Handler()).postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                //            彻底退出应用程序，经测试，效果很好
//                                Intent startMain = new Intent(Intent.ACTION_MAIN);
//                                startMain.addCategory(Intent.CATEGORY_HOME);
//                                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                tempcontext.startActivity(startMain);
//                                MainActivity.this.finish();
//                                System.exit(0);
//                            }
//                        }, 5000);
//                    }
//
//                }
//
//
//            }
//        }
//
//    };


//    public void registerBoradcastReceiver() {
//        IntentFilter myIntentFilter = new IntentFilter();
//        myIntentFilter.addAction(DistApp.ACTION_NAME);
//        //注册广播
//        registerReceiver(mBroadcastReceiver, myIntentFilter);
//    }


//    private void InitLocation(){
//            LocationClientOption option = new LocationClientOption();
////          option.setLocationMode(tempMode);
//            option.setLocationMode(LocationMode.Hight_Accuracy);
////          option.setCoorType(tempcoor);
//            option.setCoorType("bd09ll");
//            int span=5000;
////          try {
////                  span = Integer.valueOf(frequence.getText().toString());
////          } catch (Exception e) {
////                  // TODO: handle exception
////          }
//            option.setScanSpan(span);
////          option.setIsNeedAddress(checkGeoLocation.isChecked());
//            mLocationClient.setLocOption(option);
//    }


    /**
     *
     * @Title: popBut
     * @Description: 弹出浮动小控件
     * @param
     * @return void
     * @throws
     */
//    public void popBut() {
//        // 按钮被点击
//        this.startService(new Intent(getApplicationContext(), MserServes.class));
//        // new TableShowView(this).fun(); 如果只是在activity中启动
//        // 当activity跑去后台的时候[暂停态，或者销毁态] 我们设置的显示到桌面的view也会消失
//        // 所以这里采用的是启动一个服务，服务中创建我们需要显示到table上的view，并将其注册到windowManager上
//}


//    WindowManager      mWM;            // WindowManager
//    WindowManager.LayoutParams      mWMParams;      // WindowManager参数
//    View           win;
//    int tag = 0;
//
//
//    /**
//     * 弹出的用于切换政策法规及个人订阅的按钮
//    * @Title: showPopBar
//    * @Description: TODO
//    * @param
//    * @return void
//    * @throws
//     */
//    public void showPopBar() {
//        // 设置载入view WindowManager参数
//        mWM = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
//        win = LayoutInflater.from(this).inflate(R.layout.activity_imitate_widget_ctrl_window, null);
//        final ImageView pop_but= (ImageView) win.findViewById(R.id.pop_but);
//
//        final TextView pop_but0= (TextView) win.findViewById(R.id.pop_but0);
//        final TextView pop_but1= (TextView) win.findViewById(R.id.pop_but1);
//        pop_but0.setShadowLayer(3, 0, -3, Color.parseColor("#118CDE"));
//        pop_but1.setShadowLayer(3, 0, -3, Color.parseColor("#118CDE"));
//
//        win.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Integer integer = (Integer) pop_but.getTag();
//                integer = integer == null ? 0 : integer;
//                switch(integer) {
//                 case R.drawable.delete_default_qq_avatar:
//                     pop_but0.setVisibility(View.GONE);
//                     pop_but1.setVisibility(View.GONE);
//                     pop_but.setTag(null);
//                  break;
//                  default:
//                      pop_but0.setVisibility(View.VISIBLE);
//                      pop_but1.setVisibility(View.VISIBLE);
//                      pop_but.setTag(R.drawable.delete_default_qq_avatar);
//                      break;
//               }
//
//            }
//        });
//
//
//        final Drawable qq_avatar_nor=getResources().getDrawable(R.drawable.select0);
//        qq_avatar_nor.setBounds(0, 0, 90, 90);
//
//        final Drawable qq_avatar_yes=getResources().getDrawable(R.drawable.unselect0);
//        qq_avatar_yes.setBounds(0, 0, 90, 90);
//
//
//        pop_but0.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
////              切换背景图
//                Integer integer = (Integer) pop_but0.getTag();
//                integer = integer == null ? R.drawable.unselect0 : integer;
//                switch(integer) {
//                 case R.drawable.unselect0:
//
//                     pop_but0.setCompoundDrawables(null, null, qq_avatar_nor, null);
//                     pop_but0.setTag(R.drawable.select0);
//                     pop_but1.setCompoundDrawables(null, null,qq_avatar_yes, null);
//                     pop_but1.setTag(R.drawable.unselect0);
//                     function_add.setVisibility(View.GONE);
//                     MyToast.show(MainActivity.this, " 政策法规~", 1);
//                     mOttoBus.post(new updateAppEvent(0));
//
//
////                     pop_but0.setVisibility(View.GONE);
////                     pop_but1.setVisibility(View.GONE);
//                  break;
//               }
//            }
//        });
//        pop_but1.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // TODO 自动生成的方法存根
//                Integer integer = (Integer) pop_but1.getTag();
//                integer = integer == null ? 0 : integer;
//                switch(integer) {
//                 case R.drawable.unselect0:
//                     pop_but1.setCompoundDrawables(null, null, qq_avatar_nor, null);
//                     pop_but1.setTag(R.drawable.select0);
//                     pop_but0.setCompoundDrawables(null, null, qq_avatar_yes, null);
//                     pop_but0.setTag(R.drawable.unselect0);
//                     function_add.setVisibility(View.VISIBLE);
//                     MyToast.show(MainActivity.this, " 个人订阅~", 1);
//                     mOttoBus.post(new updateAppEvent(1));
//
//
////                     pop_but0.setVisibility(View.GONE);
////                     pop_but1.setVisibility(View.GONE);
//                  break;
//               }
//
//
//            }
//        });
//
//
//        win.setBackgroundColor(Color.TRANSPARENT);
//        // 这里是随便载入的一个布局文件
//
//        WindowManager wm = mWM;
//        WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
//        mWMParams = wmParams;
////      wmParams.type = 2002; // type是关键，这里的2002表示系统级窗口，你也可以试试2003。
//        wmParams.type=WindowManager.LayoutParams.TYPE_APPLICATION;
//        wmParams.flags = 40;// 这句设置桌面可控
//
////      wmParams.width = 80;
////      wmParams.height = 80;
//        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
//        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
//        wmParams.format = -3; // 透明
//
//        wmParams.gravity= Gravity.RIGHT|Gravity.BOTTOM;
//        wmParams.y=115;
//        wmParams.x=25;
//
//        wm.addView(win, wmParams);// 这句是重点 给WindowManager中丢入刚才设置的值
//                                                                // 只有addview后才能显示到页面上去。
//        // 注册到WindowManager win是要刚才随便载入的layout，wmParams是刚才设置的WindowManager参数集
//        // 效果是将win注册到WindowManager中并且它的参数是wmParams中设置饿
//
//}


    /**
     * @Title: chatLogin
     * @Description:
     * @param
     * @return void
     * @throws
     */
//    private void chatLogin() {
//        // TODO 自动生成的方法存根
//        Thread t=new Thread(new Runnable() {
//            @Override
//            public void run() {
//                    //sendEmptyMessage:发送一条消息
//                    try {
//                            //连接
////                            XmppTool.getConnection().login(USERID, PWD);
//                            XmppTool.getConnection().login("aa", "aa");
////                          Log.i("XMPPClient", "Logged in as " + XmppTool.getConnection().getUser());
//                            //状态
//                            Presence presence = new Presence(Presence.Type.available);
//                            XmppTool.getConnection().sendPacket(presence);
//                    }
//                    catch ( Exception e)
//                    {
//                            XmppTool.closeConnection();
//                    }
//            }
//    });
//    t.start();
//    }


    private static final int SETSUBSCRIBE = 0x11;


    // 底部菜单的文字数组
    private CharSequence[] mLabels = {"附近", "消息", "", "联系人", "我"};

    private void init() {

        return_back_igw = (ImageView) findViewById(R.id.return_back);
        return_back_igw.setVisibility(View.GONE);
        mTopIndicator = (MyTabWidget) findViewById(R.id.top_indicator);
        mTopIndicator.setOnTabSelectedListener(this);

        conversationListFragment = new ConversationListFragment();
//        fragments.add(new Portal_NewsFragment());
        fragments.add(new Portal_LocalFragment());
        fragments.add(conversationListFragment);
        fragments.add(new Portal_FindFragment());
        fragments.add(new Portal_ContactFragment());
//        fragments.add(contactListFragment);

//        fragments.add(new Portal_XiaoXiFragment());


        fragments.add(new Portal_OwnerFragment());


        mViewPager = (NoScrollViewPager) findViewById(R.id.view_pager);
//        mViewPager.setNoScroll(true);//表示不能滑动了
        mViewPager.setCurrentItem(2);


        findViewById(R.id.return_back).setVisibility(View.GONE);
        title_tv = (TextView) findViewById(R.id.top_title);
        title_tv.setText(mTopIndicator.getmLabels()[0]);
        iv_top_title = (ImageView) findViewById(R.id.iv_top_title);
        menu_theme = (ImageView) findViewById(R.id.menu_theme);
//        title_bar = (RelativeLayout)findViewById(R.id.tx_top_right);
//        title_bar.setVisibility(View.GONE);
        menu_theme.setOnClickListener(this);

        this.fragmentManager = this.getSupportFragmentManager();
        MyPagerAdapter myPagerAdapter = new MyPagerAdapter();
        this.mViewPager.setAdapter(myPagerAdapter);

        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // TODO 自动生成的方法存根
//                滑动完成后底部的导航条也跟着跳转
                mTopIndicator.setTabsDisplay(mContext, position > 4 ? 4 : position);

                title_tv.setText(mLabels[position]);

                if (position == 2) {
                    iv_top_title.setVisibility(View.VISIBLE);
                    title_tv.setVisibility(View.GONE);
                 } else {
                     title_tv.setVisibility(View.VISIBLE);
                    iv_top_title.setVisibility(View.GONE);
                }
                if (position == 3) {
                    menu_theme.setVisibility(View.VISIBLE);
                } else {
                    menu_theme.setVisibility(View.GONE);
                }



//        点击后关闭消息红点
                mTopIndicator.setIndicateDisplay(position, false, " ");

            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO 自动生成的方法存根
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO 自动生成的方法存根
            }
        });


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username_fromserver = extras.getString("username");
        }


//        return_back_igw.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mViewPager.setCurrentItem(4);//退回到更多界面
//                return_back_igw.setVisibility(View.GONE);
//    }
//});


        initPopupData();
//        title_tv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //Toast.makeText(MainActivity.this,"哈哈哈",Toast.LENGTH_LONG).show();
//
//                titlePopup.show(v);
//            }
//        });

        popupMenu = new PopupMenu(MainActivity.this);


////        底部导航条消息数量红点
//        mTopIndicator.setIndicateDisplay(1, true, "11");
//        mTopIndicator.setIndicateDisplay(2, true, "3");
//        mTopIndicator.setIndicateDisplay(3, true, "8");

    }


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        // TODO Auto-generated method stub
//        super.onActivityResult(requestCode, resultCode, data);
//        String sdStatus = Environment.getExternalStorageState();
//        switch (requestCode) {
//        case SETSUBSCRIBE:
//                return;
//        }
//    }


    @Override
    public void onBackPressed() {
        Intent home = new Intent(Intent.ACTION_MAIN);
        home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        home.addCategory(Intent.CATEGORY_HOME);
        startActivity(home);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                Intent home = new Intent(Intent.ACTION_MAIN);
                home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                home.addCategory(Intent.CATEGORY_HOME);
                startActivity(home);
                break;
        }
        return true;
    }



/*    @Override
    public void onBackPressed() {
        // 连续按两下return退出
        //          long currentTime = new Date().getTime();
        //          // 如果时间间隔大于2秒, 不处理
        //          if ((currentTime - preTime) > TWO_SECOND) {
        //              // 更新时间
        //              preTime = currentTime;
        //          } else {
        //              showExitDialog();
        //          }

//        showExitDialog(mContext);
        return;
    }*/
//    /**
//     * @Title: setFuncAddVisibility
//     * @Description: 设置订阅动能按钮的可见性
//     * @param @param position
//     * @return void
//     * @throws
//     */
//     public void setFuncAddVisibility(int position) {
//         if(position!=0){
//             function_add.setVisibility(View.GONE);
////             fagui.setVisibility(View.GONE);
//
//             win.setVisibility(View.GONE);
//         }else{
//             function_add.setVisibility(View.VISIBLE);
////             fagui.setVisibility(View.VISIBLE);
//
//             win.setVisibility(View.VISIBLE);
//         }
//     }

    @Override
    public void onTabSelected(int index) {
        // TODO 自动生成的方法存根
//        点击导航条后，页面跳转
        mViewPager.setCurrentItem(index, false);
        return_back_igw.setVisibility(View.GONE);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu_theme:

//                此部分为更换主题功能，暂时不需要屏蔽
//                popupMenu.showLocation(R.id.menu_theme);
//                popupMenu.setOnItemClickListener(new PopupMenu.OnItemClickListener() {
//                    @Override
//                    public void onClick(com.wuwo.im.view.PopupMenu.MENUITEM item, String str, int position) {
//                        putData(position);
//                    }
//                });


                Intent temp = new Intent(mContext, AddFriendActivity.class);
                startActivity(temp);


                break;
//            case R.id.menu_theme:
//
//                break;


        }
    }


    class MyPagerAdapter extends PagerAdapter {
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = fragments.get(position);// 取得位置，获取出来Fragment
            if (!fragment.isAdded()) { // 如果fragment还没有added
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.add(fragment, fragment.getClass().getSimpleName());
//                ft.commit();
                ft.commitAllowingStateLoss();
                /**
                 * 在用FragmentTransaction.commit()方法提交FragmentTransaction对象后
                 * 会在进程的主线程中，用异步的方式来执行。 如果想要立即执行这个等待中的操作，就要调用这个方法（只能在主线程中调用）。
                 * 要注意的是，所有的回调和相关的行为都会在这个调用中被执行完成，因此要仔细确认这个方法的调用位置。
                 */
                fragmentManager.executePendingTransactions();
            }

            if (fragment.getView().getParent() == null) {
                container.addView(fragment.getView()); // 为viewpager增加布局
            }

            return fragment.getView();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(fragments.get(position).getView()); // 移出viewpager两边之外的page布局
        }


        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }
    }


//

    /**
     * @param @param mContext
     * @return void
     * @throws
     * @Title: showExitDialog
     * @Description: 软件退出对话框
     */
    private void showExitDialog(Context mContext) {
        final AlertDialog d = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle)
                .setTitle("提示")
//                .setCancelable(true)
                .setMessage("确定退出程序吗？")
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                doSomething(dialog);
                                dialog.dismiss();
                                Intent intent = new Intent(Intent.ACTION_MAIN);
                                intent.addCategory(Intent.CATEGORY_HOME);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);

                                android.os.Process.killProcess(android.os.Process.myPid());
                                System.exit(0);
                                finish();

                            }
                        })
                .setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
//                .setView(v)
                .create();

        // change color of positive button
        d.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button b = d.getButton(DialogInterface.BUTTON_POSITIVE);
                b.setTextColor(getResources().getColor(R.color.favorite_del));

//                Button b2 = d.getButton(DialogInterface.BUTTON_NEGATIVE);
//                b2.setTextColor(getResources().getColor(R.color.teal));
            }
        });
        d.show();

    }

//    private void doSomething(DialogInterface dialog) {
//        dialog.dismiss();
//        Message msg = new Message();
//        msg.what = Loading;
//        mHandler.sendMessage(msg);
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                // TODO 自动生成的方法存根
//                String url = DistApp.serverAbsolutePath
//                        + "/mobile/app-mobileLog.action";
//                Map<String, String> map = new HashMap<String, String>();
//                map.put("deviceNumber",
//                        DistApp.ALL_deviceNumber);
//                map.put("action", "exit");
//                // map.put("appidentify", "com.dist.iportal");
//                // 把设备的坐标也传过去
//                map.put("latitude", DistApp.latitude);
//                map.put("longitude", DistApp.longitude);
//                map.put("radius", DistApp.Radius);
//                map.put("userId",   mSettings.getString("userid", ""));// 用户登录以后获取用户的userID并保存
//                try {
//                    // 退出时向服务器通报
//                    // Log.d("获取到的返回结果为 ",
//                    UtilsTool.getStringFromServer(url, map);
//                    // );
//                } catch (Exception e) {
//                    // TODO 自动生成的 catch 块
//                    e.printStackTrace();
//                }
//
//                Message msg2 = new Message();
//                msg2.what = END;
//                mHandler.sendMessage(msg2);
//
//            }
//        }).start();
//
//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                Message message = new Message();
//                message.what = END;
//                mHandler.sendMessage(message);
//            }
//        }, 3000);
//    }


    ProgressDialog pg;
    private final int Loading = 1;
    private final int END = 2;

    /**
     * 上传到服务器是加载进度框
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Loading:

                    pg = UtilsTool.initProgressDialog(mContext, "正在退出.....");
                    pg.show();
                    break;

                case END:
                    if (pg.isShowing()) {
                        pg.dismiss();

                    }


//          if(((DistApp)getApplication()).mLocationClient!=null&& ((DistApp)getApplication()).mLocationClient.isStarted()){
//              ((DistApp)getApplication()).mLocationClient.stop();
//          }


                    //            彻底退出应用程序，经测试，效果很好
                    Intent startMain = new Intent(Intent.ACTION_MAIN);
                    startMain.addCategory(Intent.CATEGORY_HOME);
                    startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ExitApp.getInstance().exit();
                    startActivity(startMain);
                    System.exit(0);


//                    Intent intent = new Intent(mContext,LoginValidateActivity.class);
//                    //传递退出所有Activity的Tag对应的布尔值为true
//                    intent.putExtra(LoginValidateActivity.EXIST, true);
//                    //启动BaseActivity
//                    startActivity(intent);
////                    finish();

                    break;
            }
            super.handleMessage(msg);
        }
    };

    //声明一个静态常量，用作退出BaseActivity的Tag
    public static final String EXIST = "exist";
    boolean isExist = false;

//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//
//        Log.d("进入LoginValidateActivity", "onNewIntent");
//
//        if (intent != null) {//判断其他Activity启动本Activity时传递来的intent是否为空
////            this.finish();
//            //获取intent中对应Tag的布尔值
//            isExist = intent.getBooleanExtra(EXIST, false);
//            //如果为真则退出本Activity
//            if (isExist) {
//                this.finish();
//                //  彻底退出应用程序，经测试，效果很好
//                Intent startMain = new Intent(Intent.ACTION_MAIN);
//                startMain.addCategory(Intent.CATEGORY_HOME);
//                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(startMain);
//                System.exit(0);
//            }
//        }
//    }

    private void initPopupData() {
        //实例化标题栏弹窗
        titlePopup = new TitlePopup(this, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        //给标题栏弹窗添加子类
        titlePopup.addAction(new ActionItem(this, "商务主题", R.drawable.back_, R.color.colorPrimary));
        titlePopup.addAction(new ActionItem(this, "粉色主题", R.drawable.back_, R.color.violet));
        titlePopup.addAction(new ActionItem(this, "蓝色主题", R.drawable.back_, R.color.blue));
        titlePopup.addAction(new ActionItem(this, "绿色主题", R.drawable.back_, R.color.green));
        titlePopup.addAction(new ActionItem(this, "紫色主题", R.drawable.back_, R.color.wisteria));
        titlePopup.addAction(new ActionItem(this, "橘色主题", R.drawable.back_, R.color.googleplus));

//        titlePopup.addAction(new ActionItem(this, "项目树", R.drawable.mm_title_btn_compose_normal));
        //添加弹出框listitem项点击监听
        titlePopup.setItemOnClickListener(new TitlePopup.OnItemOnClickListener() {
            @Override
            public void onItemClick(ActionItem item, int position) {
//                    if(position%2==1){
////                    mIsDark = true;
//                    putData(mIsDark);}else{
//                        mIsDark = false;
//                        putData(mIsDark);
//                    }
//
                putData(position);
            }
        });
    }


    /**
     * 用来保存主题标识
     */
    private SharedPreferences mPreferences;

    /**
     * 是否是 Dark Theme(即是黑色主题) 主题
     */
//    private boolean mIsDark;
    private void putData(int position) {

        mPreferences = getSharedPreferences("theme", MODE_PRIVATE);
        mPreferences.edit().putInt("currentTheme", position).commit();
        //recreate();
        finish();
        Intent intent = getIntent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }


    /*在fragment的管理类中，我们要实现这部操作，而他的主要作用是，当D这个activity回传数据到
这里碎片管理器下面的fragnment中时，往往会经过这个管理器中的onActivityResult的方法。*/
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && data.getExtras() != null && resultCode == RESULT_OK) {
            Bundle result = data.getExtras(); //data为B中回传的Intent
            switch (requestCode) {
                case WowuApp.ALIPAY:
              /*在这里，我们通过碎片管理器中的Tag，就是每个碎片的名称，来获取对应的fragment*/
                    Fragment f = fragmentManager.findFragmentByTag(fragments.get(4).getClass().getSimpleName());
              /*然后在碎片中调用重写的onActivityResult方法*/
                    f.onActivityResult(requestCode, resultCode, data);
                    break;

            }
        }
    }

}

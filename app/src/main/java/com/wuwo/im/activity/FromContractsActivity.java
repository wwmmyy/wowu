package com.wuwo.im.activity;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hyphenate.chatuidemo.db.DemoDBManager;
import com.hyphenate.chatuidemo.db.UserDao;
import com.wuwo.im.adapter.CommRecyclerAdapter;
import com.wuwo.im.adapter.CommRecyclerViewHolder;
import com.wuwo.im.bean.Contact;
import com.wuwo.im.bean.ContactFriend;
import com.wuwo.im.config.WowuApp;
import com.wuwo.im.util.ContactsTool;
import com.wuwo.im.util.MyToast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import im.wuwo.com.wuwo.R;

/**
 * desc
 *
 * @author 王明远
 * @日期： 2016/6/9 0:08
 * @版权:Copyright All rights reserved.
 */

public class FromContractsActivity extends BaseLoadActivity {
    Cursor cursor;
    Context mContext = FromContractsActivity.this;
    ArrayList<Contact> mContacts = new ArrayList<Contact>();
    ArrayList<ContactFriend> mYaoqing_Contacts = new ArrayList<ContactFriend>();
    ArrayList<ContactFriend> mTianJia_Contacts = new ArrayList<ContactFriend>();
    RecyclerView mRecyclerView_Tianjia, mRecyclerView_Yaoqing;
    //    ContactAdapter mContactAdapter;
    CommRecyclerAdapter tianjiaRAdapter, yaoqingRAdapter;
    Gson gson = new GsonBuilder().create();
    private mHandlerWeak mtotalHandler;

    public static final int LOAD_RECOMMEND_DATA = 1;
    public static final int SYNC_CONTACTS_DATA = 2;
    private TextView tv_contact_tianjia, tv_contact_yaoqing;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_from_contracts);
        mRecyclerView_Tianjia = (RecyclerView) this.findViewById(R.id.recycler_view_tianjia);
        mRecyclerView_Yaoqing = (RecyclerView) this.findViewById(R.id.recycler_view_yaoqing);
        tv_contact_tianjia = (TextView) this.findViewById(R.id.tv_contact_tianjia);
        tv_contact_yaoqing = (TextView) this.findViewById(R.id.tv_contact_yaoqing);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView_Tianjia.setLayoutManager(linearLayoutManager);

        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(mContext);
        linearLayoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView_Yaoqing.setLayoutManager(linearLayoutManager2);


        ((TextView) findViewById(R.id.top_title)).setText("添加通讯录好友");
        findViewById(R.id.return_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mtotalHandler = new mHandlerWeak(this);
        initiAdapter();


//        1.判断是否已经同步过通讯好友到服务器，如果本地变更了联系人列表也要同步
        syncContacts();


//        loadPhoneContacts();
//        loadRecommendFriends();

    }

    //    同步通讯录到服务器
    private void syncContacts() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ContactsTool ct = new ContactsTool();
                    mContacts = ct.getPhoneContacts(mContext);

                    String CacheJsonString = DemoDBManager.getInstance().getCacheJson(UserDao.CONTRACTS_FRIENDS);
                    //说明之前已经保存同步通讯录，且没有变更
                    Log.i("FromContractsActivity表a", "：" + CacheJsonString.trim().length() + ":" + gson.toJson(mContacts).length());
                    if (CacheJsonString != null && CacheJsonString.trim().length() == gson.toJson(mContacts).length()) {
                        Log.i("FromContractsActivity列表", "：：" + "说明之前已经保存同步通讯录，且没有变更");
//                        直接获取当前待添加信息
                        loadRecommendFriends();
                    } else {
                        //[{"Name":"*0*","PhoneNumber":"*0*"},{"Name":"+8613875073163","PhoneNumber":"+488613875073163"}]
//                        Log.i("FromContractsActivity列表", "：" + gson.toJson(mContacts.subList(2, 6)));
                        loadDataService.loadPostJsonRequestData(WowuApp.JSON, WowuApp.FriendSyncContactsURL, gson.toJson(mContacts.subList(0, 30)), SYNC_CONTACTS_DATA);//
//                      //将新的通讯录保存到缓存数据库
                        DemoDBManager.getInstance().saveCacheJson(UserDao.CONTRACTS_FRIENDS, gson.toJson(mContacts));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 获取待添加和待邀请好友
     */
    private void loadRecommendFriends() {
        loadDataService.loadGetJsonRequestData(WowuApp.FriendContactsRecommendURL, LOAD_RECOMMEND_DATA);
    }

    private void initiAdapter() {
        tianjiaRAdapter = new CommRecyclerAdapter<ContactFriend>(mContext, R.layout.item_phonecontact_list) {
            @Override
            public void convert(CommRecyclerViewHolder viewHolder, ContactFriend mainMessage) {
                viewHolder.setText(R.id.contact_user, mainMessage.getName());
                viewHolder.setText(R.id.contact_phone, mainMessage.getPhoneNumber());

                final ContactFriend fMainMessage = mainMessage;
                viewHolder.getView(R.id.fl_contact_add).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MyToast.show(mContext, fMainMessage.getPhoneNumber());
                    }
                });
            }
        };
//        tianjiaRAdapter.setOnItemClick(new CommRecyclerAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
////                MyToast.show(mContext,mContacts.get(position).getPhoneNumber());
//            }
//        });

        yaoqingRAdapter = new CommRecyclerAdapter<ContactFriend>(mContext, R.layout.item_phonecontact_list) {
            @Override
            public void convert(CommRecyclerViewHolder viewHolder, ContactFriend mainMessage) {
                viewHolder.setText(R.id.contact_user, mainMessage.getName());
                viewHolder.setText(R.id.contact_phone, mainMessage.getPhoneNumber());
                viewHolder.setText(R.id.contact_add, "邀请");
                final ContactFriend fMainMessage = mainMessage;
                viewHolder.getView(R.id.fl_contact_add).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MyToast.show(mContext, fMainMessage.getPhoneNumber());
                    }
                });
            }
        };
    }

//    /**
//     * 从通讯录中加载联系人列表
//     */
//    public void loadPhoneContacts() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                ContactsTool ct = new ContactsTool();
//                mContacts = ct.getPhoneContacts(mContext);
//                Message msg = new Message();
//                msg.what = DOWNLOADED_Contact;
//                mtotalHandler.sendMessage(msg);
//            }
//        }).start();
//    }


    public static final int DOWNLOADED_Contact = 1;
    public static final int REFRESH_DATA = 2;
    //创建一个handler，内部完成处理消息方法

    private static class mHandlerWeak extends Handler {
        private WeakReference<FromContractsActivity> activity = null;

        public mHandlerWeak(FromContractsActivity act) {
            super();
            this.activity = new WeakReference<FromContractsActivity>(act);
        }

        @Override
        public void handleMessage(Message msg) {
            FromContractsActivity act = activity.get();
            if (null == act) {
                return;
            }
            switch (msg.what) {
                case DOWNLOADED_Contact:
                    if (act != null) {
                        act.yaoqingRAdapter.setData(act.mYaoqing_Contacts);
                        act.mRecyclerView_Yaoqing.setAdapter(act.yaoqingRAdapter);
                        act.tianjiaRAdapter.setData(act.mTianJia_Contacts);
                        act.mRecyclerView_Tianjia.setAdapter(act.tianjiaRAdapter);

                        act.tv_contact_tianjia.setText(act.mTianJia_Contacts.size() + "个好友待添加");
                        act.tv_contact_yaoqing.setText(act.mYaoqing_Contacts.size() + "个好友待邀请");
                    }
                    break;
                case REFRESH_DATA:
                    act.mRecyclerView_Yaoqing.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void loadServerData(final String response, int flag) {

        switch (flag) {
            case LOAD_RECOMMEND_DATA:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("获取带邀请好友列表", "：：" + response);
                        Gson gson = new GsonBuilder().create();
                        java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<List<ContactFriend>>() {
                        }.getType();
//                事实上这里要将好友区分出来，分为待添加和带邀请两类，分别放在两个数组中，然后刷新列表  xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
                        mTianJia_Contacts = gson.fromJson(response, type);

                        mYaoqing_Contacts = mTianJia_Contacts;

                        Message msg = new Message();
                        msg.what = DOWNLOADED_Contact;
                        mtotalHandler.sendMessage(msg);
                    }
                }).start();

                break;
            case SYNC_CONTACTS_DATA://同步通讯录后的返回值
                Log.i("获取同步好友列表结果", "：：" + response);
                //说明成功同步，开始加载待添加和待邀请好友
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        loadRecommendFriends();
                    }
                }
                ).start();

                break;
        }

    }

    @Override
    public void loadDataFailed(String response, int flag) {
        MyToast.show(mContext, response);
    }
}

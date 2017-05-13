package com.wuwo.im.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chatuidemo.db.DemoDBManager;
import com.hyphenate.chatuidemo.db.UserDao;
import com.wuwo.im.bean.Contact;
import com.wuwo.im.bean.ContactFriend;
import com.wuwo.im.config.WowuApp;
import com.wuwo.im.util.ContactsTool;
import com.wuwo.im.util.LogUtils;
import com.wuwo.im.util.MyToast;
import com.wuwo.im.util.UtilsTool;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import im.imxianzhi.com.imxianzhi.R;

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
//    ArrayList<ContactFriend> mYaoqing_Contacts = new ArrayList<ContactFriend>();
    ArrayList<ContactFriend> mTianJia_Contacts = new ArrayList<ContactFriend>();
    //    RecyclerView mRecyclerView_Tianjia, mRecyclerView_Yaoqing;
    //    ContactAdapter mContactAdapter;
//    CommRecyclerAdapter tianjiaRAdapter, yaoqingRAdapter;
    Gson gson = new GsonBuilder().create();
    private mHandlerWeak mtotalHandler;

    public static final int LOAD_RECOMMEND_DATA = 1;
    public static final int SYNC_CONTACTS_DATA = 2;
//    private TextView tv_contact_tianjia, tv_contact_yaoqing;


    private MyExpendAdapter mContactFriendGroupadapter;
    private ExpandableListView expendView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_from_contracts);


        mContactFriendGroupadapter = new MyExpendAdapter();
        expendView = (ExpandableListView) findViewById(R.id.expandableList);
        expendView.setGroupIndicator(null); // 设置默认图标不显示
        expendView.setAdapter(mContactFriendGroupadapter);


//        mRecyclerView_Tianjia = (RecyclerView) this.findViewById(R.id.recycler_view_tianjia);
//        mRecyclerView_Yaoqing = (RecyclerView) this.findViewById(R.id.recycler_view_yaoqing);
//        tv_contact_tianjia = (TextView) this.findViewById(R.id.tv_contact_tianjia);
//        tv_contact_yaoqing = (TextView) this.findViewById(R.id.tv_contact_yaoqing);

//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
//        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        mRecyclerView_Tianjia.setLayoutManager(linearLayoutManager);


/*        StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager( 1, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView_Tianjia.setLayoutManager(mLayoutManager);


//        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(mContext);
//        linearLayoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
//        mRecyclerView_Yaoqing.setLayoutManager(linearLayoutManager2);

        StaggeredGridLayoutManager mLayoutManager2 = new StaggeredGridLayoutManager( 1, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView_Yaoqing.setLayoutManager(mLayoutManager2);*/

        ((TextView) findViewById(R.id.top_title)).setText("添加通讯录好友");
        findViewById(R.id.return_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mtotalHandler = new mHandlerWeak(this);
//        initiAdapter();


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
                Message msg = new Message();
                msg.what = LoadingInfo;
                mtotalHandler.sendMessage(msg);
                try {
                    ContactsTool ct = new ContactsTool();
                    mContacts = ct.getPhoneContacts(mContext);

                    String CacheJsonString = DemoDBManager.getInstance().getCacheJson(UserDao.CONTRACTS_FRIENDS);
                    //说明之前已经保存同步通讯录，且没有变更
////                    LogUtils.i("FromContractsActivity表a", "：" + CacheJsonString.trim().length() + ":" + gson.toJson(mContacts).length());
//                    if (CacheJsonString != null && CacheJsonString.trim().length() == gson.toJson(mContacts).length()) {
//                        LogUtils.i("FromContractsActivity列表", "：：" + "说明之前已经保存同步通讯录，且没有变更");
////                        直接获取当前待添加信息
//                        loadRecommendFriends();
//                    } else {
//                        //[{"Name":"*0*","PhoneNumber":"*0*"},{"Name":"+8613875073163","PhoneNumber":"+488613875073163"}]
                        LogUtils.i("FromContractsActivity列表", "：" + gson.toJson(mContacts.subList(2, 6)));

                    if(mContacts.size()>20){
                        loadDataService.loadPostJsonRequestData(WowuApp.JSON, WowuApp.FriendSyncContactsURL, gson.toJson(mContacts.subList(0, mContacts.size()-2)), SYNC_CONTACTS_DATA);// gson.toJson(mContacts.subList(0, 30))

                    }else{
                        loadDataService.loadPostJsonRequestData(WowuApp.JSON, WowuApp.FriendSyncContactsURL, gson.toJson(mContacts.subList(0, mContacts.size()-1)), SYNC_CONTACTS_DATA);// gson.toJson(mContacts.subList(0, 30))
                    }
//                      //将新的通讯录保存到缓存数据库
                        DemoDBManager.getInstance().saveCacheJson(UserDao.CONTRACTS_FRIENDS, gson.toJson(mContacts));
//                    }
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



    public static final int DOWNLOADED_Contact = 1;
    public static final int REFRESH_DATA = 2;
    private static final int LoadingInfo = 3;
    private static final int LoadingError = 4;
    private ProgressDialog pg;

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
                    if (act.pg != null && act.pg.isShowing()) act.pg.dismiss();
                    if (act != null) {
                        act.mContactFriendGroupadapter.notifyDataSetChanged();

                        for (int i = 0; i < act.ContactFriendGroupList.size(); i++) {
                            act.expendView.expandGroup(i);
                        }


                    }
                    break;
                case REFRESH_DATA:
//                    act.mRecyclerView_Yaoqing.setVisibility(View.GONE);
                    break;
                case LoadingInfo:
                    act.pg = UtilsTool.initProgressDialog(act.mContext, "正在加载...");
                    act.pg.show();  // if (act.pg != null && act.pg.isShowing()) act.pg.dismiss();
                    break;
                case LoadingError:
                    MyToast.show(act.mContext, "加载失败");
                    if (act.pg != null && act.pg.isShowing()) act.pg.dismiss();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {

    }

    public class ContactFriendGroupInfo {
        private String ContactFriendTypeName;
        private List<ContactFriend> ContactFriendList;

        public String getContactFriendTypeName() {
            return ContactFriendTypeName;
        }

        public void setContactFriendTypeName(String contactFriendTypeName) {
            ContactFriendTypeName = contactFriendTypeName;
        }

        public List<ContactFriend> getContactFriendList() {
            return ContactFriendList;
        }

        public void setContactFriendList(List<ContactFriend> contactFriendList) {
            ContactFriendList = contactFriendList;
        }
    }


    private ArrayList<ContactFriendGroupInfo> ContactFriendGroupList = new ArrayList<ContactFriendGroupInfo>(); // 记录所有的会议列表


    @Override
    public void loadServerData(final String response, int flag) {

        switch (flag) {
            case LOAD_RECOMMEND_DATA:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        LogUtils.i("获取带邀请好友列表", "：：" + response);
                        Gson gson = new GsonBuilder().create();
                        java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<List<ContactFriend>>() {
                        }.getType();
//                事实上这里要将好友区分出来，分为待添加和带邀请两类，分别放在两个数组中，然后刷新列表  xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
                        mTianJia_Contacts = gson.fromJson(response, type);


                        if(mTianJia_Contacts!=null){
                            int size=mTianJia_Contacts.size();
                            ArrayList<ContactFriend> mTianJiaList = new ArrayList<ContactFriend>();
                            ArrayList<ContactFriend> mYaoqingList = new ArrayList<ContactFriend>();

                            for (int i = 0; i < size; i++) {
                                if(mTianJia_Contacts.get(i).isRegistered()){
                                    mTianJiaList.add(mTianJia_Contacts.get(i));
                                }else{
                                    mYaoqingList.add(mTianJia_Contacts.get(i));
                                }
                            }
                            ContactFriendGroupInfo temp = new ContactFriendGroupInfo();
                            temp.setContactFriendTypeName("待添加");
                            temp.setContactFriendList(mTianJiaList);
                            ContactFriendGroupList.add(temp);

                            ContactFriendGroupInfo temp2 = new ContactFriendGroupInfo();
                            temp2.setContactFriendTypeName("待邀请");
                            temp2.setContactFriendList(mYaoqingList);
                            ContactFriendGroupList.add(temp2);


                        }

//                        mYaoqing_Contacts = mTianJia_Contacts;

                        Message msg = new Message();
                        msg.what = DOWNLOADED_Contact;
                        mtotalHandler.sendMessage(msg);
                    }
                }).start();

                break;
            case SYNC_CONTACTS_DATA://同步通讯录后的返回值
                LogUtils.i("获取同步好友列表结果", "：：" + response);
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
        Message msg = new Message();
        msg.what = LoadingError;
        mtotalHandler.sendMessage(msg);

    }


    /**
     * 适配器
     *
     * @author Administrator
     */
    private class MyExpendAdapter extends BaseExpandableListAdapter {


        /**
         * 获取一级标签中二级标签的内容
         */
        @Override
        public Object getChild(int groupPosition, int childPosition) {
            // return child_text[groupPosition][childPosition];

            return ContactFriendGroupList.get(groupPosition).getContactFriendList().get(childPosition);
        }

        /**
         * 获取二级标签ID
         */
        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        /**
         * 对一级标签下的二级标签进行设置
         */
        @SuppressLint("SimpleDateFormat")
        @Override
        public View getChildView(final int groupPosition,
                                 final int childPosition, boolean isLastChild, View convertView,
                                 ViewGroup parent) {
            // convertView= getLayoutInflater().inflate(R.layout.email_child,
            // null);
            convertView = getLayoutInflater().inflate(R.layout.item_phonecontact_list, null);


            TextView contact_user = (TextView) convertView.findViewById(R.id.contact_user);
            TextView contact_phone = (TextView) convertView.findViewById(R.id.contact_phone);
            final TextView contact_add = (TextView) convertView.findViewById(R.id.contact_add);
            contact_user.setText(ContactFriendGroupList.get(groupPosition).getContactFriendList().get(childPosition).getName());
            contact_phone.setText(ContactFriendGroupList.get(groupPosition).getContactFriendList().get(childPosition).getPhoneNumber());
            contact_add.setText(groupPosition == 0 ? "添加" : "邀请");
            contact_add.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {


                    if (groupPosition == 0) {
                        //                        new EaseAlertDialog(FromContractsActivity.this, null, "关注TA可查看并接收TA的全部动态，确定关注TA？", null, new EaseAlertDialog.AlertDialogUser() {
//                            @Override
//                            public void onResult(boolean confirmed, Bundle bundle) {
//                                if (confirmed) {
                        try {
                            JSONObject json = new JSONObject();
                            json.put("userId", WowuApp.UserId);
                            loadDataService.loadPostJsonRequestData(WowuApp.JSON, WowuApp.FocusFriendsURL + "?userId=" + ContactFriendGroupList.get(groupPosition).getContactFriendList().get(childPosition).getUserId(), json.toString(), R.id.rb_guanzhu);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
//                                }
//                            }
//                        }, true).show();

                        //发送一句对话给他，让他去关注你
//                    发送文本消息
//创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此
                        EMMessage message = EMMessage.createTxtSendMessage(WowuApp.Name + "关注了你哦", ContactFriendGroupList.get(groupPosition).getContactFriendList().get(childPosition).getUserId());
//发送消息
                        EMClient.getInstance().chatManager().sendMessage(message);

                        MyToast.show(mContext,"已添加");
                        contact_add.setTextColor(getResources().getColor(R.color.deep_gray));

                    } else {

                        Intent tempIntent = new Intent(mContext, ShareToContractBySmsActivity.class);
                        tempIntent.putExtra("name", ContactFriendGroupList.get(groupPosition).getContactFriendList().get(childPosition).getName());
                        tempIntent.putExtra("num", ContactFriendGroupList.get(groupPosition).getContactFriendList().get(childPosition).getPhoneNumber());
                        startActivity(tempIntent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                    }


                }
            });
            return convertView;
        }

        /**
         * 一级标签下二级标签的数量
         */
        @Override
        public int getChildrenCount(int groupPosition) {
            // return child_text[groupPosition].length;
            return ContactFriendGroupList.get(groupPosition).getContactFriendList()
                    .size();
        }

        /**
         * 获取一级标签内容
         */
        @Override
        public Object getGroup(int groupPosition) {
            // return group_title[groupPosition];
            return ContactFriendGroupList.get(groupPosition).getContactFriendTypeName();
        }

        /**
         * 一级标签总数
         */
        @Override
        public int getGroupCount() {
            // return group_title.length;
            return ContactFriendGroupList.size();
        }

        /**
         * 一级标签ID
         */
        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        /**
         * 对一级标签进行设置
         */
        @Override
        public View getGroupView(final int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.item_phonecontact_list_title, null);

            TextView tv_contact_title = (TextView) convertView.findViewById(R.id.tv_contact_title);
            tv_contact_title.setText(groupPosition==0?ContactFriendGroupList.get(groupPosition).getContactFriendList().size()+"个好友待添加"
                    :ContactFriendGroupList.get(groupPosition).getContactFriendList().size()+"个好友待邀请");

            tv_contact_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContactFriendGroupadapter.notifyDataSetChanged();
                    if (expendView.isGroupExpanded(groupPosition)) {//&&groupPosition==0
                        expendView.collapseGroup(groupPosition);
                    } else {
                        expendView.expandGroup(groupPosition);
                    }
                }
            });

            return convertView;
        }

        /**
         * 指定位置相应的组视图
         */
        @Override
        public boolean hasStableIds() {
            return true;
        }

        /**
         * 当选择子节点的时候，调用该方法
         */
        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

    }


}

package com.wuwo.im.activity;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Request;
import com.wuwo.im.adapter.CommRecyclerAdapter;
import com.wuwo.im.adapter.CommRecyclerViewHolder;
import com.wuwo.im.bean.Contact;
import com.wuwo.im.util.ContactsTool;
import com.wuwo.im.util.MyToast;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;

import im.wuwo.com.wuwo.R;
/** 
*desc
*@author 王明远
*@日期： 2016/6/9 0:08
*@版权:Copyright    All rights reserved.
*/

public class FromContractsActivity extends BaseLoadActivity {
    Cursor cursor;
    Context mContext = FromContractsActivity.this;
    ArrayList<Contact> mContacts = new ArrayList<Contact>();
    ArrayList<Contact> mYaoqingContacts = new ArrayList<Contact>();
    RecyclerView mRecyclerViewTianjia, mRecyclerViewYaoqing;
    //    ContactAdapter mContactAdapter;
    CommRecyclerAdapter tianjiaRAdapter, yaoqingRAdapter;
    private static final String[] PHONES_PROJECTION = new String[]{
//            Phone.DISPLAY_NAME, Phone.NUMBER, Phone.PHOTO_ID,Phone.CONTACT_ID };
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};
    Gson gson = new GsonBuilder().create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_from_contracts);
        mRecyclerViewTianjia = (RecyclerView) this.findViewById(R.id.recycler_view_tianjia);
        mRecyclerViewYaoqing = (RecyclerView) this.findViewById(R.id.recycler_view_yaoqing);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerViewTianjia.setLayoutManager(linearLayoutManager);

        loadPhoneContacts();
        initiAdapter();

        ((TextView) findViewById(R.id.top_title)).setText("添加通讯录好友");
        findViewById(R.id.return_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    private void initiAdapter() {
        tianjiaRAdapter = new CommRecyclerAdapter<Contact>(mContext, R.layout.item_phonecontact_list) {
            @Override
            public void convert(CommRecyclerViewHolder viewHolder, Contact mainMessage) {
                viewHolder.setText(R.id.contact_user, mainMessage.getContactName());
                viewHolder.setText(R.id.contact_phone, mainMessage.getPhoneNumber());

                final Contact fMainMessage = mainMessage;
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

        yaoqingRAdapter = new CommRecyclerAdapter<Contact>(mContext, R.layout.item_phonecontact_list) {
            @Override
            public void convert(CommRecyclerViewHolder viewHolder, Contact mainMessage) {
                viewHolder.setText(R.id.contact_user, mainMessage.getContactName());
                viewHolder.setText(R.id.contact_phone, mainMessage.getPhoneNumber());
                viewHolder.setText(R.id.contact_add, "邀请");
                final Contact fMainMessage = mainMessage;
                viewHolder.getView(R.id.fl_contact_add).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MyToast.show(mContext, fMainMessage.getPhoneNumber());
                    }
                });
            }
        };

    }

    /**
     * 从通讯录中加载联系疼人列表
     */
    public void loadPhoneContacts() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ContactsTool ct = new ContactsTool();
                mContacts = ct.getPhoneContacts(mContext);
                Message msg = new Message();
                msg.what = DOWNLOADED_Contact;
                mtotalHandler.sendMessage(msg);
            }
        }).start();
    }


    //    /从网络加载该user的好友信息
    private void loadData() {
        OkHttpUtils
                .post()
                .url("http://58.246.138.178:8000/DistMobile/mobileMeeting!getAllMeeting.action")
                .addParams("newsMessageId", "4028826f505a3b0f01506553b0c80c3a")
//                .addParams("page", mCount + "")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Request request, Exception e) {
                        MyToast.show(mContext, "获取服务器信息失败", Toast.LENGTH_LONG);
                    }

                    @Override
                    public void onResponse(String response) {
                        if (response != null) {
//                            java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<List<newsMessage>>() {
//                            }.getType();
//                            newsMessage_userlist = gson.fromJson(response, type);
//                            //在view界面上展示结果
                            Message msg = new Message();
                            msg.what = REFRESH_DATA;
                            mtotalHandler.sendMessage(msg);
                        }
                    }
                });

    }


    public static final int DOWNLOADED_Contact = 0;
    public static final int REFRESH_DATA = 2;
    //创建一个handler，内部完成处理消息方法
    Handler mtotalHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                // 正在下载
                case DOWNLOADED_Contact:
                    tianjiaRAdapter.setData(mContacts);
                    mRecyclerViewTianjia.setAdapter(tianjiaRAdapter);
                    break;
                case REFRESH_DATA:
//                    如果网络返回数据，在加载显示，现在测试阶段，测试下功能，下面都要屏蔽掉
                    mRecyclerViewYaoqing.setVisibility(View.GONE);
//                    yaoqingRAdapter.setData(mContacts);
//                    mRecyclerViewYaoqing.setAdapter(yaoqingRAdapter);

                    break;
                default:
                    break;
            }
        }
    };


    @Override
    public void onClick(View v) {

    }

    @Override
    public void loadServerData(String response, int flag) {

    }

    @Override
    public void loadDataFailed(String response, int flag) {

    }
}

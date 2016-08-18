package com.wuwo.im.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wuwo.im.adapter.CommRecyclerAdapter;
import com.wuwo.im.adapter.CommRecyclerViewHolder;
import com.wuwo.im.bean.Contact;
import com.wuwo.im.util.ContactsTool;
import com.wuwo.im.util.MyToast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import im.wuwo.com.wuwo.R;

/**
 * desc
 *
 * @author 王明远
 * @日期： 2016/6/9 0:08
 * @版权:Copyright All rights reserved.
 */

public class ShareToContractsActivity extends BaseLoadActivity {
    Cursor cursor;
    Context mContext = ShareToContractsActivity.this;
    ArrayList<Contact> mContacts = new ArrayList<Contact>();
    RecyclerView mRecyclerView_Tianjia;
    //    ContactAdapter mContactAdapter;
    CommRecyclerAdapter tianjiaRAdapter ;
    Gson gson = new GsonBuilder().create();
    private mHandlerWeak mtotalHandler;

    public static final int LOAD_RECOMMEND_DATA = 1;
    public static final int SYNC_CONTACTS_DATA = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_to_contracts);
        mRecyclerView_Tianjia = (RecyclerView) this.findViewById(R.id.recycler_view_tianjia);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView_Tianjia.setLayoutManager(linearLayoutManager);



        ((TextView) findViewById(R.id.top_title)).setText("分享给好友");
        findViewById(R.id.return_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        mtotalHandler = new mHandlerWeak(this);
        initiAdapter();

        loadPhoneContacts();


    }



/*    *//**
     * 获取待添加和待邀请好友
     *//*
    private void loadRecommendFriends() {
        loadDataService.loadGetJsonRequestData(WowuApp.FriendContactsRecommendURL, LOAD_RECOMMEND_DATA);
    }*/

    private void initiAdapter() {
        tianjiaRAdapter = new CommRecyclerAdapter<Contact>(mContext, R.layout.item_phonecontact_list) {
            @Override
            public void convert(CommRecyclerViewHolder viewHolder, Contact mainMessage) {
                viewHolder.setText(R.id.contact_user, mainMessage.getName());
                viewHolder.setText(R.id.contact_phone, mainMessage.getPhoneNumber());

                final Contact fMainMessage = mainMessage;
                viewHolder.getView(R.id.fl_contact_add).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        MyToast.show(mContext, fMainMessage.getPhoneNumber());
                        Intent tempIntent=new Intent(mContext, ShareToContractBySmsActivity.class);
                        tempIntent.putExtra("name",fMainMessage.getName());
                        tempIntent.putExtra("num",fMainMessage.getPhoneNumber());
                        startActivity(tempIntent);
                        ShareToContractsActivity.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                });
            }
        };
        tianjiaRAdapter.setOnItemClick(new CommRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                MyToast.show(mContext,mContacts.get(position).getName());
                Intent tempIntent=new Intent(mContext, ShareToContractBySmsActivity.class);
                tempIntent.putExtra("name",mContacts.get(position).getName());
                tempIntent.putExtra("num",mContacts.get(position).getPhoneNumber());
                startActivity(tempIntent);
                ShareToContractsActivity.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });


    }

    /**
     * 从通讯录中加载联系人列表
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


    public static final int DOWNLOADED_Contact = 1;
    public static final int REFRESH_DATA = 2;
    //创建一个handler，内部完成处理消息方法

    private static class mHandlerWeak extends Handler {
        private WeakReference<ShareToContractsActivity> activity = null;

        public mHandlerWeak(ShareToContractsActivity act) {
            super();
            this.activity = new WeakReference<ShareToContractsActivity>(act);
        }

        @Override
        public void handleMessage(Message msg) {
            ShareToContractsActivity act = activity.get();
            if (null == act) {
                return;
            }
            switch (msg.what) {
                case DOWNLOADED_Contact:
                    if (act != null) {
                        act.tianjiaRAdapter.setData(act.mContacts);
                        act.mRecyclerView_Tianjia.setAdapter(act.tianjiaRAdapter);
                    }
                    break;
                case REFRESH_DATA:
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
            case SYNC_CONTACTS_DATA://同步通讯录后的返回值
                break;
        }

    }

    @Override
    public void loadDataFailed(String response, int flag) {
        MyToast.show(mContext, response);
    }
}

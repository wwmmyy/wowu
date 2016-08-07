package com.wuwo.im.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wuwo.im.adapter.CommRecyclerAdapter;
import com.wuwo.im.adapter.CommRecyclerViewHolder;
import com.wuwo.im.bean.userPay;
import com.wuwo.im.config.WowuApp;
import com.wuwo.im.util.UtilsTool;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import im.wuwo.com.wuwo.R;

/**
 * 获取支付信息列表
 */
public class UserPayActivity extends BaseLoadActivity {


    private RecyclerView  mRecyclerView ;
    private  CommRecyclerAdapter messageRAdapter;
    private ArrayList<userPay> userpay_type_list = new ArrayList<userPay>();
    LinearLayout vip_type_shuoming;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_pay);
        initView();
    }

    private void initView() {
        findViewById(R.id.return_back).setOnClickListener(this);
        ((TextView)findViewById(R.id.top_title)).setText("会员支付");
//        findViewById(R.id.memberpay_vip1).setOnClickListener(this);
/*        findViewById(R.id.memberpay_vip2).setOnClickListener(this);
        findViewById(R.id.memberpay_vip3).setOnClickListener(this);
        findViewById(R.id.memberpay_vip4).setOnClickListener(this);*/
        vip_type_shuoming = (LinearLayout)findViewById(R.id.vip_type_shuoming);



        pg = UtilsTool.initProgressDialog(mContext, "正在连接.....");
        pg.show();


        mRecyclerView = (RecyclerView) findViewById(R.id.rc_vip_type_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        initAdapter();
        mRecyclerView.setAdapter(messageRAdapter);


        //获取VIP等级支付类型列表
        try {
            JSONObject json = new JSONObject();
            loadDataService.loadPostJsonRequestData(WowuApp.JSON, WowuApp.VipInfoURL, json.toString(), R.id.top_title);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }







    private final int REFRESH = 1;
    private final int VISIABLE = 4;
    private ProgressDialog pg;
    private final int Loading = 3;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Loading:
                    pg = UtilsTool.initProgressDialog(mContext, "正在连接.....");
                    pg.show();
                    break;
                case REFRESH:
                    if(pg !=null &&pg.isShowing()){
                        pg.dismiss();
                    }
                    messageRAdapter.setData(userpay_type_list);
                    vip_type_shuoming.setVisibility(View.VISIBLE);
                    break;
                case VISIABLE:
                    break;
            }
            super.handleMessage(msg);
        }
    };


















    Intent intent  = null;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.return_back:
                this.finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
           /* case R.id.memberpay_vip1:
                intent= new Intent(mContext, UserPayChooseActivity.class);
                intent.putExtra("vipType", 1);
                intent.putExtra("money", "¥0.01");
                intent.putExtra("intro", "十二个月会员");
//                startActivity(intent);
                startActivityForResult(intent, WowuApp.ALIPAY);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.memberpay_vip2:
                intent= new Intent(mContext, UserPayChooseActivity.class);
                intent.putExtra("vipType", 2);
                intent.putExtra("money", "¥0.01");
                intent.putExtra("intro", "六个月会员");
//                startActivity(intent);
                startActivityForResult(intent, WowuApp.ALIPAY);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.memberpay_vip3:
                intent= new Intent(mContext, UserPayChooseActivity.class);
                intent.putExtra("vipType", 3);
                intent.putExtra("money", "¥0.01");
                intent.putExtra("intro", "三个月会员");
//                startActivity(intent);
                startActivityForResult(intent, WowuApp.ALIPAY);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.memberpay_vip4:
                intent= new Intent(mContext, UserPayChooseActivity.class);
                intent.putExtra("vipType", 4);
                intent.putExtra("money", "¥0.01");
                intent.putExtra("intro", "一个月会员");
//                startActivity(intent);
                startActivityForResult(intent, WowuApp.ALIPAY);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;*/

        }
    }












    public void initAdapter() {
        messageRAdapter = new CommRecyclerAdapter<userPay>(mContext, R.layout.item_userpay_list) {
            @Override
            public void convert(CommRecyclerViewHolder viewHolder, userPay mainMessage) {
                //            [{"Name":"一个月","Price":0.01,"Type":1},{"Name":"3个月","Price":0.01,"Type":2},{"Name":"6个月","Price":0.01,"Type":3},{"Name":"一年","Price":0.01,"Type":4}]
                //对对应的View进行赋值
                viewHolder.setText(R.id.tv_userpay_price, mainMessage.getPrice());
                viewHolder.setText(R.id.tv_userpay_month, mainMessage.getName());
                viewHolder.getView(R.id.tv_userpay_price).setTag( mainMessage.getType());

                switch ( mainMessage.getType()) {
                     case 1:
                         viewHolder.getView(R.id.iv_userpay_pic).setBackgroundResource(R.drawable.month1);
                        break;
                    case 2:
                        viewHolder.getView(R.id.iv_userpay_pic).setBackgroundResource(R.drawable.month3);
                        break;
                    case 3:
                        viewHolder.getView(R.id.iv_userpay_pic).setBackgroundResource(R.drawable.month6);
                        break;
                    case 4:
                        viewHolder.getView(R.id.iv_userpay_pic).setBackgroundResource(R.drawable.month12);
                        break;
                }

            }

            @Override
            public int getItemViewType(int position) {
                return super.getItemViewType(position);
            }
        };
        //设置item点击事件
        messageRAdapter.setOnItemClick(new CommRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                intent= new Intent(mContext, UserPayChooseActivity.class);
                intent.putExtra("vipType", userpay_type_list.get(position).getType());
                intent.putExtra("money", userpay_type_list.get(position).getPrice());
                intent.putExtra("intro", userpay_type_list.get(position).getName());
//                startActivity(intent);
                startActivityForResult(intent, WowuApp.ALIPAY);//主要是为了支付成功后点击确认能连锁关掉其他activity
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }


        });
    }







    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && data.getExtras() != null && resultCode == RESULT_OK) {
            Bundle result = data.getExtras(); //data为B中回传的Intent
            switch (requestCode) {
                case WowuApp.ALIPAY:
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("payState", result.getBoolean("payState"));//给 bundle 写入数据
                    Intent mIntent = new Intent();
                    mIntent.putExtras(bundle);
                    setResult(RESULT_OK, mIntent);
                    finish();//此处一定要调用finish()方法
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    break;

            }
        }
    }


    @Override
    public void loadServerData(String response, int flag) {
        Log.d("支付列表:返回的结果为：：：：" , response);


       final String  mResponse=response;
        new Thread(new Runnable() {
            @Override
            public void run() {
                Gson gson = new GsonBuilder().create();
                if (mResponse != null) {
                    java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<List<userPay>>() {
                    }.getType();
                    userpay_type_list = gson.fromJson(mResponse, type);

                    Message msg = Message.obtain();
                    msg.what = REFRESH;
                    mHandler.sendMessage(msg);
                }
            }
        }).start();

    }

    @Override
    public void loadDataFailed(String response, int flag) {

    }


//    @Override
//    public void onDestroy() {
//        //  If null, all callbacks and messages will be removed.
//        mHandler.removeCallbacksAndMessages(null);
//    }
}

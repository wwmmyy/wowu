package com.wuwo.im.activity;

import android.content.Context;
import android.content.Intent;
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
import com.wuwo.im.bean.Question;
import com.wuwo.im.config.ExitApp;
import com.wuwo.im.config.WowuApp;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import im.wuwo.com.wuwo.R;

public class CharacterTestActivity extends BaseLoadActivity {
    Context mContext=this;
    RecyclerView mRecyclerView;
    CommRecyclerAdapter messageRAdapter;
    TextView tv_question_sure,tv_question;
    HashMap<String, String>  anwsers=new HashMap<>();
    TextView return_back;

    String[] questionData  = { "A.....", "B......." } ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ExitApp.getInstance().addOpenedActivity(this);
        setContentView(R.layout.activity_character_test);
        initTop();


      String  stringQuestionList= getIntent().getStringExtra("questionList");
//     下一步转化为列表
        initData(stringQuestionList);


        ; //"C.Vertical", "D.Horizontal", "E.RecyclerView"

        initAdapter();
        messageRAdapter.setData(Arrays.asList(questionData));
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_question);

//        mRecyclerView.addItemDecoration(new RecycleViewDivider(mContext, LinearLayoutManager.VERTICAL));


////        //如果布局大小一致有利于优化
//        mRecyclerView.setHasFixedSize(true);

//        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext,4);
//        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        mRecyclerView.setLayoutManager(gridLayoutManager);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        //分割线
//        mRecyclerView.addItemDecoration(new RecycleViewDivider(mContext, LinearLayoutManager.HORIZONTAL));
        //初始化适配器并绑定适配器
        mRecyclerView.setAdapter(messageRAdapter);
    }

    Gson gson = new GsonBuilder().create();
    private ArrayList<Question> Questions = new ArrayList<Question>(); //记录所有的最新消息
    private void initData(final String stringQuestionList) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<List<Question>>() { }.getType();
                Questions = gson.fromJson(stringQuestionList, type);
                if(Questions!=null &&Questions.size()>0){
                    Message msg = Message.obtain();
                    msg.what = REFRESH;
                    msg.arg1=0;
                    mHandler.sendMessage(msg);
                }

            }
        }).start();

    }

    private void initTop() {
        return_back= (TextView)  findViewById(R.id.return_back);
        return_back.setOnClickListener(this);
        TextView tx_top_right= (TextView) findViewById(R.id.tx_top_right);
        tx_top_right.setVisibility(View.VISIBLE);
        tx_top_right.setOnClickListener(this);
        tv_question_sure= (TextView) findViewById(R.id.tv_question_sure);
        tv_question= (TextView) findViewById(R.id.tv_question);
        tv_question_sure.setOnClickListener(this);
    }


    public void initAdapter() {
        messageRAdapter = new CommRecyclerAdapter<String>(mContext, R.layout.item_chacter_test) {
            @Override
            public void convert(CommRecyclerViewHolder viewHolder, String mainMessage) {
                //对对应的View进行赋值
                viewHolder.setText(R.id.tv_test_choose, mainMessage);

//                SimpleDraweeView portal_news_img = (SimpleDraweeView) viewHolder.getView(R.id.news_label_pic);
//                portal_news_img.setImageURI(Uri.parse("http://www.gog.com.cn/pic/0/10/91/11/10911138_955870.jpg"));
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

//                Intent intent2 = new Intent(mContext, ChatListActivity.class);
//                startActivity(intent2);
//                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                refreshQuestion(position);


            }


        });
    }


//用于记录当前是第几题
    int currentPosition=0;
    /**
     * 刷新下一个题目及问题列表， 记录已选择的答案
     * @param position
     */
    private void refreshQuestion(int position) {

        //记录答案
        anwsers.put(Questions.get(currentPosition).getId(),position+"");
        //判断是否是最后一个问题

        if(currentPosition==(Questions.size()-1)){
            tv_question_sure.setVisibility(View.VISIBLE);
            tv_question_sure.postInvalidate();
        }else{
            currentPosition++;
            //刷新列表
            Message msg = Message.obtain();
            msg.what = REFRESH;
            msg.arg1=currentPosition;
            mHandler.sendMessage(msg);
        }
    }



    private final int REFRESH = 1;
    private final int END = 2;
    private final int PICSHOW = 3;

    /**
     * 上传到服务器是加载进度框
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH:

                    tv_question.setText((msg.arg1+1)+".\n"+ Questions.get(msg.arg1).getTitle());
                    questionData[0]="A."+ Questions.get(msg.arg1).getOptionA();
                    questionData[1]="B."+ Questions.get(msg.arg1).getOptionB();

                    messageRAdapter.clearDate();
                    messageRAdapter.setData(Arrays.asList(questionData));

                    break;
            }
            super.handleMessage(msg);
        }
    };






    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tx_top_right:
                Intent intent2 = new Intent(mContext, CharacterTResultActivity.class);
                startActivity(intent2);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.return_back:

                if(currentPosition==0){
                    return_back.setText("返回");
                    CharacterTestActivity.this.finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }else{
                    return_back.setText("上一题");
                    currentPosition--;
                    //刷新列表
                    Message msg = Message.obtain();
                    msg.what = REFRESH;
                    msg.arg1=currentPosition;
                    mHandler.sendMessage(msg);
                }

                break;
            case  R.id.tv_question_sure:
            //上传所选择的答案列表

                try {
                    JSONObject json = new JSONObject();
//                    json.put("PhoneNumber", WowuApp.PhoneNumber);

                    java.util.Iterator it = anwsers.entrySet().iterator();
                    while (it.hasNext()) {
                        java.util.Map.Entry entry = (java.util.Map.Entry) it.next();
                        json.put(entry.getKey() + "",entry.getValue() + "");
                    }

                    loadDataService.loadPostJsonRequestData(WowuApp.JSON, WowuApp.SubmitAnswerURL, json.toString(), R.id.tv_register_two_sure);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
        }

    }

    @Override
    public void loadServerData(String response, int flag) {
        Intent intent2 = new Intent(mContext, CharacterTResultActivity.class);
        startActivity(intent2);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

    }

    @Override
    public void loadDataFailed(String response, int flag) {

    }
}


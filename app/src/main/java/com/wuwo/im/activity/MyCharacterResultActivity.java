package com.wuwo.im.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wuwo.im.bean.Characters;
import com.wuwo.im.config.WowuApp;
import com.wuwo.im.util.UtilsTool;
import com.wuwo.im.view.RangeSeekBar;

import java.lang.ref.WeakReference;

import im.wuwo.com.wuwo.R;

public class MyCharacterResultActivity extends BaseLoadActivity  {

    private final String TAG="MyCharacterctivity";
    RangeSeekBar rs_character,rs_character1,rs_character2,rs_character3;
    public static final int LOAD_RECOMMEND_DATA = 1;
    private mHandlerWeak mtotalHandler;
    private ProgressDialog pg;
    TextView  tv_user_typeintro, tv_user_typename,tv_user_result;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_character_result);
        initView();
    }

    private void initView() {
        findViewById(R.id.tv_character_re_test).setOnClickListener(this);
        findViewById(R.id.return_back).setOnClickListener(this);

         tv_user_typeintro = (TextView) findViewById(R.id.tv_user_typeintro);
        tv_user_typename = (TextView) findViewById(R.id.tv_user_typename);
        tv_user_result= (TextView) findViewById(R.id.tv_user_result);

        SimpleDraweeView draweeView = (SimpleDraweeView) findViewById(R.id.user_login_pic);
        draweeView.setImageURI(Uri.parse(WowuApp.iconPath));//"http://www.gog.com.cn/pic/0/10/91/11/10911138_955870.jpg"

        rs_character= (RangeSeekBar) findViewById(R.id.rs_character);
        rs_character1= (RangeSeekBar) findViewById(R.id.rs_character1);
        rs_character2= (RangeSeekBar) findViewById(R.id.rs_character2);
        rs_character3= (RangeSeekBar) findViewById(R.id.rs_character3);
        mtotalHandler = new mHandlerWeak(this);

        loadDataService.loadGetJsonRequestData(WowuApp.GetDispositionInfoURL, LOAD_RECOMMEND_DATA);
        pg = UtilsTool.initProgressDialog(mContext, "正在连接.....");
        pg.show();
    }


    @Override
    protected void onResume() {
        super.onResume();
//        rs_character.setCharacter(0.1f,0.8f);
//        rs_character1.setCharacter(0.4f,0.7f);
//        rs_character2.setCharacter(0.2f,0.6f);
//        rs_character3.setCharacter(0.3f,0.9f);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.return_back:
                this.finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.tv_character_re_test:
//                Intent temp2 = new Intent(this, CharacterTestActivity.class);
                Intent temp2 = new Intent(this, CharacterChooseActivity.class);
                this.startActivity(temp2);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
        }

    }

    @Override
    public void loadServerData(final String response, int flag) {

        switch (flag) {
            case LOAD_RECOMMEND_DATA:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("MyCharacterctivity", "：：" + response);

//                        {"PhotoUrl":"http://xzxj.oss-cn-shanghai.aliyuncs.com/celebrity/6cbeca3a-989f-4756-8b46-bc781a1d123a.jpg",
//                                "Celebrity":"莎士比亚","CelebrityDescription":"英国文学史上最杰出的戏剧家","Name":"INFP","Title":"化解者",
//                                "Score":{"UserId":"637e5acb638f46f5873ec86f0b4b49ce",
//                                "E":0,"I":0,"S":0,"N":0,"T":0,"F":0,"J":0,"P":0,"PropensityScore":0.0,
//                                "EI_PropensityScore":0.0,"SN_PropensityScore":0.0,"TF_PropensityScore":0.0,
//                                "JP_PropensityScore":0.0,"PropensityDescription":"轻微","Id":"16967133-1356-4dfb-8ad4-abecdf755ca6"}}

//                        Gson gson = new GsonBuilder().create();
//                        java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<List<ContactFriend>>() {
//                        }.getType();
//                事实上这里要将好友区分出来，分为待添加和带邀请两类，分别放在两个数组中，然后刷新列表  xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
//                        mTianJia_Contacts = gson.fromJson(response, type);

                        Gson gson = new GsonBuilder().create();
                        java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<Characters>() {
                        }.getType();
                        mCharacter = gson.fromJson(response, type);


                        Message msg = new Message();
                        msg.what = DOWNLOADED_Contact;
                        msg.obj=response;
                        mtotalHandler.sendMessage(msg);
                    }
                }).start();
                break;
        }
    }

    public static final int DOWNLOADED_Contact = 1;
    public static final int REFRESH_DATA = 2;
    //创建一个handler，内部完成处理消息方法
    public Characters mCharacter=null;

    private static class mHandlerWeak extends Handler {
        private WeakReference<MyCharacterResultActivity> activity = null;

        public mHandlerWeak(MyCharacterResultActivity act) {
            super();
            this.activity = new WeakReference<MyCharacterResultActivity>(act);
        }

        @Override
        public void handleMessage(Message msg) {
            MyCharacterResultActivity act = activity.get();
            if (null == act) {
                return;
            }
            switch (msg.what) {
                case DOWNLOADED_Contact:
                    if (act != null) {
                            if (act.pg != null && act.pg.isShowing()) act.pg.dismiss();
/*                        {"PhotoUrl":"http://xzxj.oss-cn-shanghai.aliyuncs.com/celebrity/c6916880-fb12-4f1b-a510-51155e2622f4.jpg",
"Celebrity":"甘地","CelebrityDescription":"印度民族解放运动的领导人","Name":"INFJ","Title":"劝告者",
"Score":{"UserId":"637e5acb638f46f5873ec86f0b4b49ce","E":2,"I":3,"S":2,"N":3,"T":2,"F":3,"J":3,"P":2,"PropensityScore":4.0,
"EI_PropensityScore":5.0,"SN_PropensityScore":4.0,"TF_PropensityScore":4.0,"JP_PropensityScore":-5.0,"PropensityDescription":"轻微","Id":"179555a5-b421-4c2b-ae57-d20ecb2d67d8"}}  */


                        act.tv_user_typeintro.setText(act.mCharacter.getCelebrityDescription());//json.optString("CelebrityDescription")
                        act.tv_user_typename.setText(act.mCharacter.getCelebrity());// json.optString("Celebrity")
                        act.tv_user_result.setText(act.mCharacter.getScore().getPropensityScore()+"% "+act.mCharacter.getScore().getPropensityDescription() );//(new JSONObject(json.optString("Score"))).optString("PropensityDescription")

                        if(act.mCharacter.getScore().getEI_PropensityScore()>0){
                            act.rs_character.setCharacter(0.5f, (float) (0.5f+act.mCharacter.getScore().getEI_PropensityScore()/100));

                            ( (TextView) act.findViewById(R.id.tv_dec1_r)).setText(act.mCharacter.getScore().getEI_PropensityScore()+"% ");
                        } else{
                            act.rs_character.setCharacter((float) (0.5f+act.mCharacter.getScore().getEI_PropensityScore()/100),0.5f);
                            ( (TextView) act.findViewById(R.id.tv_dec1_l)).setText(Math.abs(act.mCharacter.getScore().getEI_PropensityScore())+"% ");
                        }

                        if(act.mCharacter.getScore().getSN_PropensityScore()>0){
                            act.rs_character1.setCharacter(0.5f, (float) (0.5f+act.mCharacter.getScore().getSN_PropensityScore()/100));
                            ( (TextView) act.findViewById(R.id.tv_dec2_r)).setText(act.mCharacter.getScore().getSN_PropensityScore()+"% ");
                        } else{
                            act.rs_character1.setCharacter((float) (0.5f+act.mCharacter.getScore().getSN_PropensityScore()/100),0.5f);
                            ( (TextView) act.findViewById(R.id.tv_dec2_l)).setText(Math.abs(act.mCharacter.getScore().getSN_PropensityScore())+"% ");
                        }

                        if(act.mCharacter.getScore().getTF_PropensityScore()>0){
                            act.rs_character2.setCharacter(0.5f, (float) (0.5f+act.mCharacter.getScore().getTF_PropensityScore()/100));
                            ( (TextView) act.findViewById(R.id.tv_dec3_r)).setText(act.mCharacter.getScore().getTF_PropensityScore()+"% ");
                        } else{
                            act.rs_character2.setCharacter((float) (0.5f+act.mCharacter.getScore().getTF_PropensityScore()/100),0.5f);
                            ( (TextView) act.findViewById(R.id.tv_dec3_l)).setText(Math.abs(act.mCharacter.getScore().getTF_PropensityScore())+"% ");
                        }


                        if(act.mCharacter.getScore().getJP_PropensityScore()>0){
                            act.rs_character3.setCharacter(0.5f, (float) (0.5f+act.mCharacter.getScore().getJP_PropensityScore()/100));
                            ( (TextView) act.findViewById(R.id.tv_dec4_r)).setText(act.mCharacter.getScore().getJP_PropensityScore()+"% ");
                        } else{
                            act.rs_character3.setCharacter((float) (0.5f+act.mCharacter.getScore().getJP_PropensityScore()/100),0.5f);
                            ( (TextView) act.findViewById(R.id.tv_dec4_l)).setText(Math.abs(act.mCharacter.getScore().getJP_PropensityScore())+"% ");
                        }


//                        try {
//                                JSONObject json = new JSONObject(msg.obj.toString());
//                                act.tv_user_typeintro.setText(json.optString("CelebrityDescription"));
//                                act.tv_user_typename.setText(json.optString("Celebrity"));
//                                act.tv_user_result.setText( (new JSONObject(json.optString("Score"))).optString("PropensityDescription"));
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
                    }
                    break;
                case REFRESH_DATA:
//                    act.mRecyclerView_Yaoqing.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void loadDataFailed(String response, int flag) {

    }
}

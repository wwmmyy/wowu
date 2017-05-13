package com.wuwo.im.fragement;


import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.wuwo.im.adapter.MyViewPagerAdapter;
import com.wuwo.im.bean.CharacterPopInfo;
import com.wuwo.im.bean.Characters;
import com.wuwo.im.config.WowuApp;
import com.wuwo.im.util.LogUtils;
import com.wuwo.im.util.MyToast;
import com.wuwo.im.util.UtilsTool;
import com.wuwo.im.view.RangeSeekBar;
import com.zhy.http.okhttp.service.LoadserverdataService;
import com.zhy.http.okhttp.service.loadServerDataListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import im.imxianzhi.com.imxianzhi.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CharacterQingXiangDuFragment extends BaseAppFragment implements View.OnClickListener, loadServerDataListener {

    public static final int LOAD_RECOMMEND_DATA = 1;
    public static final int DOWNLOADED_Contact = 1;
    public static final int REFRESH_DATA = 2;
    public static final int END = 5;
    public static final int POP_DIALOG = 7;
    private final String TAG = "CharacterQingXiangDuFragment";
    //创建一个handler，内部完成处理消息方法
    public Characters mCharacter = null;
    RangeSeekBar rs_character, rs_character1, rs_character2, rs_character3;
    LoadserverdataService loadDataService;
    private mHandlerWeak mtotalHandler;
    ProgressDialog pg;
    private TextView  tv_user_result, tv_user_result_2,tv_dec1_r, tv_dec1_l, tv_dec2_r, tv_dec2_l, tv_dec3_r, tv_dec3_l, tv_dec4_r, tv_dec4_l, tv_character_test_time, tv_test_type;
    private TextView tv_waixiang, tv_neixiang, tv_ganjue, tv_zhijue, tv_sikao, tv_qinggan, tv_panduan, tv_zhijue2;
    private Activity mContext;
    private boolean  loadState=false;

    public CharacterQingXiangDuFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mContext = activity;
        loadDataService = new LoadserverdataService(this);
        mtotalHandler = new mHandlerWeak(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_character_result, container, false);
        initViews(view);
        loadState=true;
        return view;
    }

    private void initViews(View view) {

        tv_user_result = (TextView) view.findViewById(R.id.tv_user_result);
        tv_user_result_2 = (TextView) view.findViewById(R.id.tv_user_result_2);
//        tv_user_info = (TextView) view.findViewById(R.id.tv_user_info);
        tv_character_test_time = (TextView) view.findViewById(R.id.tv_character_test_time);
        tv_test_type = (TextView) view.findViewById(R.id.tv_test_type);
//        tv_user_info.setOnClickListener(this);


        tv_dec1_r = (TextView) view.findViewById(R.id.tv_dec1_r);
        tv_dec1_l = (TextView) view.findViewById(R.id.tv_dec1_l);
        tv_dec2_r = (TextView) view.findViewById(R.id.tv_dec2_r);
        tv_dec2_l = (TextView) view.findViewById(R.id.tv_dec2_l);
        tv_dec3_r = (TextView) view.findViewById(R.id.tv_dec3_r);
        tv_dec3_l = (TextView) view.findViewById(R.id.tv_dec3_l);
        tv_dec4_r = (TextView) view.findViewById(R.id.tv_dec4_r);
        tv_dec4_l = (TextView) view.findViewById(R.id.tv_dec4_l);


        tv_waixiang = (TextView) view.findViewById(R.id.tv_waixiang);
        tv_neixiang = (TextView) view.findViewById(R.id.tv_neixiang);
        tv_ganjue = (TextView) view.findViewById(R.id.tv_ganjue);
        tv_zhijue = (TextView) view.findViewById(R.id.tv_zhijue);
        tv_sikao = (TextView) view.findViewById(R.id.tv_sikao);
        tv_qinggan = (TextView) view.findViewById(R.id.tv_qinggan);
        tv_panduan = (TextView) view.findViewById(R.id.tv_panduan);
        tv_zhijue2 = (TextView) view.findViewById(R.id.tv_zhijue2);


        tv_waixiang.setOnClickListener(this);
        tv_neixiang.setOnClickListener(this);
        tv_ganjue.setOnClickListener(this);
        tv_zhijue.setOnClickListener(this);
        tv_sikao.setOnClickListener(this);
        tv_qinggan.setOnClickListener(this);
        tv_panduan.setOnClickListener(this);
        tv_zhijue2.setOnClickListener(this);
        tv_user_result.setOnClickListener(this);
        tv_user_result_2.setOnClickListener(this);
//        view.findViewById(R.id.tv_mycharacter_moreronggr).setOnClickListener(this);
//        view.findViewById(R.id.tv_mycharacter_ronggr).setOnClickListener(this);
//        view.findViewById(R.id.tv_character_typeintro).setOnClickListener(this);

        //"http://www.gog.com.cn/pic/0/10/91/11/10911138_955870.jpg"

        rs_character = (RangeSeekBar) view.findViewById(R.id.rs_character);
        rs_character1 = (RangeSeekBar) view.findViewById(R.id.rs_character1);
        rs_character2 = (RangeSeekBar) view.findViewById(R.id.rs_character2);
        rs_character3 = (RangeSeekBar) view.findViewById(R.id.rs_character3);

        view.findViewById(R.id.iv_character1).setOnClickListener(this);
        view.findViewById(R.id.iv_character2).setOnClickListener(this);
        view.findViewById(R.id.iv_character3).setOnClickListener(this);
        view.findViewById(R.id.iv_character4).setOnClickListener(this);

        rs_character.setOnClickListener(this);
        rs_character1.setOnClickListener(this);
        rs_character2.setOnClickListener(this);
        rs_character3.setOnClickListener(this);

        mtotalHandler = new mHandlerWeak(this);
//        loadData();
//        loadDataService.loadGetJsonRequestData(WowuApp.GetDispositionInfoURL, LOAD_RECOMMEND_DATA);

    }

    public void loadData() {
        loadDataInfo(WowuApp.GetDispositionInfoURL, LOAD_RECOMMEND_DATA);
    }


    String testType=null;
    public void loadDataByHitory(String historyId,String testType) {
       this. testType=testType;
        loadDataInfo(WowuApp.GetDispositionInfoFromHistoryURL+"?historyId="+historyId, LOAD_RECOMMEND_DATA);
    }

    @Override
    public String getFragmentName() {
        return "CharacterQingXiangDuFragment";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
/*            case R.id.tv_mycharacter_moreronggr:
                loadDataInfo(WowuApp.GetMbtiInfoURL, R.id.tv_mycharacter_moreronggr);
                break;
            case R.id.tv_mycharacter_ronggr:
                loadDataInfo(WowuApp.GetRgInfoURL, R.id.tv_mycharacter_moreronggr);
                break;*/
//            case R.id.tv_user_info:
//            case R.id.tv_character_typeintro:
//                currentType="7";
//                loadDataInfo(WowuApp.GetTendencyDialogURL, R.id.tv_mycharacter_moreronggr);
//                break;
            case R.id.tv_user_result:
            case R.id.tv_user_result_2:
                currentType="3";
                loadDataInfo(WowuApp.GetTendencyDialogURL, R.id.tv_mycharacter_moreronggr);
                break;
            case R.id.tv_waixiang:
                currentType="6";
                loadDataInfo(WowuApp.GetDimensionsDialogURL + "?dimession=e", R.id.tv_mycharacter_moreronggr);
                break;
            case R.id.tv_neixiang:
                currentType="6";
                loadDataInfo(WowuApp.GetDimensionsDialogURL + "?dimession=i", R.id.tv_mycharacter_moreronggr);
                break;
            case R.id.tv_ganjue:
                currentType="6";
                loadDataInfo(WowuApp.GetDimensionsDialogURL + "?dimession=s", R.id.tv_mycharacter_moreronggr);
                break;
            case R.id.tv_sikao:
                currentType="6";
                loadDataInfo(WowuApp.GetDimensionsDialogURL + "?dimession=t", R.id.tv_mycharacter_moreronggr);
                break;
            case R.id.tv_qinggan:
                currentType="6";
                loadDataInfo(WowuApp.GetDimensionsDialogURL + "?dimession=f", R.id.tv_mycharacter_moreronggr);
                break;
            case R.id.tv_panduan:
                currentType="6";
                loadDataInfo(WowuApp.GetDimensionsDialogURL + "?dimession=j", R.id.tv_mycharacter_moreronggr);
                break;
            case R.id.tv_zhijue2:
                currentType="6";
                loadDataInfo(WowuApp.GetDimensionsDialogURL + "?dimession=p", R.id.tv_mycharacter_moreronggr);
                break;
            case R.id.tv_zhijue:
                currentType="6";
                loadDataInfo(WowuApp.GetDimensionsDialogURL + "?dimession=n", R.id.tv_mycharacter_moreronggr);
                break;
            case R.id.iv_character1:
            case R.id.rs_character:
                currentType="6";
                loadDataInfo(WowuApp.GetDimensionsDialogURL + "?dimession=ei", R.id.tv_mycharacter_moreronggr);
                break;
            case R.id.iv_character2:
            case R.id.rs_character1:
                currentType="6";
                loadDataInfo(WowuApp.GetDimensionsDialogURL + "?dimession=sn", R.id.tv_mycharacter_moreronggr);
                break;
            case R.id.iv_character3:
            case R.id.rs_character2:
                currentType="6";
                loadDataInfo(WowuApp.GetDimensionsDialogURL + "?dimession=tf", R.id.tv_mycharacter_moreronggr);
                break;
            case R.id.iv_character4:
            case R.id.rs_character3:
                currentType="6";
                loadDataInfo(WowuApp.GetDimensionsDialogURL + "?dimession=jp", R.id.tv_mycharacter_moreronggr);
                break;
        }
    }

    private void loadDataInfo(String getMbtiInfoURL, int tv_mycharacter_moreronggr) {
        pg = UtilsTool.initProgressDialog(mContext, "正在连接.....");
        pg.show();
        loadDataService.loadGetJsonRequestData(getMbtiInfoURL, tv_mycharacter_moreronggr);
    }

    private ArrayList<CharacterPopInfo> mCharacterPuPopList = new ArrayList<CharacterPopInfo>(); //记录所有的最新消息

    @Override
    public void loadServerData(final String response, int flag) {
        LogUtils.i("MyCharacterctivity", "：：" + response);
        switch (flag) {
            case LOAD_RECOMMEND_DATA:
                Gson gson = new GsonBuilder().create();
                java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<Characters>() {
                }.getType();
                mCharacter = gson.fromJson(response, type);
                Message msg = new Message();
                msg.what = DOWNLOADED_Contact;
                msg.obj = response;
                mtotalHandler.sendMessage(msg);
                break;
            case R.id.tv_mycharacter_moreronggr:
//                Message msg2 = new Message();
//                msg2.what = END;
//                mtotalHandler.sendMessage(msg2);
//
//                Intent intent = new Intent(mContext, CharacterPuPopActivity.class);
//                intent.putExtra("response", response);
//                startActivity(intent);
//                mContext.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                Gson gson2 = new GsonBuilder().create();
                if (response != null) {
                    java.lang.reflect.Type type2 = new com.google.gson.reflect.TypeToken<List<CharacterPopInfo>>() {
                    }.getType();
                    mCharacterPuPopList = gson2.fromJson(response, type2);
                    Message msg3 = new Message();
                    msg3.what = POP_DIALOG;
                    mtotalHandler.sendMessage(msg3);
                }
                break;
        }
    }

    @Override
    public void loadDataFailed(String response, int flag) {
        Message msg2 = new Message();
        msg2.what = END;
        mtotalHandler.sendMessage(msg2);
    }

    public void setQingXiangDuInfo(Characters qingXiangDuInfo) {
        mCharacter = qingXiangDuInfo;

        if(loadState){
            Message msg = new Message();
            msg.what = DOWNLOADED_Contact;
            mtotalHandler.sendMessage(msg);
        }
    }

    private static class mHandlerWeak extends Handler {
        private WeakReference<CharacterQingXiangDuFragment> activity = null;

        public mHandlerWeak(CharacterQingXiangDuFragment act) {
            super();
            this.activity = new WeakReference<CharacterQingXiangDuFragment>(act);
        }

        @Override
        public void handleMessage(Message msg) {
            CharacterQingXiangDuFragment act = activity.get();
            if (null == act) {
                return;
            }
            switch (msg.what) {
                case DOWNLOADED_Contact:
                    if (act.pg != null && act.pg.isShowing()) {
                        act.pg.dismiss();
                    }
                    if (act != null  && act.isAdded()) {
                        if (act.pg != null && act.pg.isShowing()) act.pg.dismiss();
/*                        {"PhotoUrl":"http://xzxj.oss-cn-shanghai.aliyuncs.com/celebrity/c6916880-fb12-4f1b-a510-51155e2622f4.jpg",
"Celebrity":"甘地","CelebrityDescription":"印度民族解放运动的领导人","Name":"INFJ","Title":"劝告者",
"Score":{"UserId":"637e5acb638f46f5873ec86f0b4b49ce","E":2,"I":3,"S":2,"N":3,"T":2,"F":3,"J":3,"P":2,"PropensityScore":4.0,
"EI_PropensityScore":5.0,"SN_PropensityScore":4.0,"TF_PropensityScore":4.0,"JP_PropensityScore":-5.0,"PropensityDescription":"轻微","Id":"179555a5-b421-4c2b-ae57-d20ecb2d67d8"}}  */

                        if (act.mCharacter != null) {
//                            act.tv_user_info.setText(act.mCharacter.getName() + act.mCharacter.getTitle());

                            if (act.mCharacter.getScore() != null) {
                                act.tv_user_result.setText("倾向程度：" + act.mCharacter.getScore().getPropensityScore() + "% ");//(new JSONObject(json.optString("Score"))).optString("PropensityDescription")
                                act.tv_user_result_2.setText( "(" + act.mCharacter.getScore().getPropensityDescription()+")");//(new JSONObject(json.optString("Score"))).optString("PropensityDescription")


                                act.tv_character_test_time.setText(act.mCharacter.getScore().getCreateOnString());
                                if (act.mCharacter.getScore().getEI_PropensityScore() > 0) {
                                    act.rs_character.setCharacter(0.5f, (float) (0.5f + act.mCharacter.getScore().getEI_PropensityScore() / 100));
                                    act.tv_dec1_r.setText(act.mCharacter.getScore().getEI_PropensityScore() + "% ");
                                    act.tv_neixiang.setTextColor(act.getResources().getColor(R.color.seekbar_color));
                                    act.tv_waixiang.setTextColor(act.getResources().getColor(R.color.black));
                                    act.tv_dec1_l.setVisibility(View.GONE);
                                    act.tv_dec1_r.setVisibility(View.VISIBLE);
                                } else  if (act.mCharacter.getScore().getEI_PropensityScore() == 0) {
                                    act.rs_character.setCharacter(0.5f, 0.5f);
                                    act.tv_dec1_l.setVisibility(View.GONE);
                                    act.tv_dec1_r.setVisibility(View.GONE);
                                }else {
                                    act.rs_character.setCharacter((float) (0.5f + act.mCharacter.getScore().getEI_PropensityScore() / 100), 0.5f);
                                    act.tv_dec1_l.setText(Math.abs(act.mCharacter.getScore().getEI_PropensityScore()) + "% ");
                                    act.tv_waixiang.setTextColor(act.getResources().getColor(R.color.seekbar_color));
                                    act.tv_neixiang.setTextColor(act.getResources().getColor(R.color.black));
                                    act.tv_dec1_r.setVisibility(View.GONE);
                                    act.tv_dec1_l.setVisibility(View.VISIBLE);
                                }

                                if (act.mCharacter.getScore().getSN_PropensityScore() > 0) {
                                    act.rs_character1.setCharacter(0.5f, (float) (0.5f + act.mCharacter.getScore().getSN_PropensityScore() / 100));
                                    act.tv_dec2_r.setText(act.mCharacter.getScore().getSN_PropensityScore() + "% ");
                                    act.tv_zhijue.setTextColor(act.getResources().getColor(R.color.seekbar_color));
                                    act.tv_ganjue.setTextColor(act.getResources().getColor(R.color.black));
                                    act.tv_dec2_l.setVisibility(View.GONE);
                                    act.tv_dec2_r.setVisibility(View.VISIBLE);
                                }else  if (act.mCharacter.getScore().getSN_PropensityScore() == 0) {
                                    act.rs_character1.setCharacter(0.5f, 0.5f);
                                    act.tv_dec2_r.setVisibility(View.GONE);
                                    act.tv_dec2_l.setVisibility(View.GONE);
                                } else {
                                    act.rs_character1.setCharacter((float) (0.5f + act.mCharacter.getScore().getSN_PropensityScore() / 100), 0.5f);
                                    act.tv_dec2_l.setText(Math.abs(act.mCharacter.getScore().getSN_PropensityScore()) + "% ");
                                    act.tv_ganjue.setTextColor(act.getResources().getColor(R.color.seekbar_color));
                                    act.tv_zhijue.setTextColor(act.getResources().getColor(R.color.black));
                                    act.tv_dec2_r.setVisibility(View.GONE);
                                    act.tv_dec2_l.setVisibility(View.VISIBLE);
                                }

                                if (act.mCharacter.getScore().getTF_PropensityScore() > 0) {
                                    act.rs_character2.setCharacter(0.5f, (float) (0.5f + act.mCharacter.getScore().getTF_PropensityScore() / 100));
                                    act.tv_dec3_r.setText(act.mCharacter.getScore().getTF_PropensityScore() + "% ");
                                    act.tv_qinggan.setTextColor(act.getResources().getColor(R.color.seekbar_color));
                                    act.tv_sikao.setTextColor(act.getResources().getColor(R.color.black));
                                    act.tv_dec3_l.setVisibility(View.GONE);
                                    act.tv_dec3_r.setVisibility(View.VISIBLE);
                                }else  if (act.mCharacter.getScore().getTF_PropensityScore() == 0) {
                                    act.rs_character2.setCharacter(0.5f, 0.5f);
                                    act.tv_dec3_r.setVisibility(View.GONE);
                                    act.tv_dec3_l.setVisibility(View.GONE);
                                } else {
                                    act.rs_character2.setCharacter((float) (0.5f + act.mCharacter.getScore().getTF_PropensityScore() / 100), 0.5f);
                                    act.tv_dec3_l.setText(Math.abs(act.mCharacter.getScore().getTF_PropensityScore()) + "% ");
                                    act.tv_sikao.setTextColor(act.getResources().getColor(R.color.seekbar_color));
                                    act.tv_qinggan.setTextColor(act.getResources().getColor(R.color.black));
                                    act.tv_dec3_r.setVisibility(View.GONE);
                                    act.tv_dec3_l.setVisibility(View.VISIBLE);
                                }

                                if (act.mCharacter.getScore().getJP_PropensityScore() > 0) {
                                    act.rs_character3.setCharacter(0.5f, (float) (0.5f + act.mCharacter.getScore().getJP_PropensityScore() / 100));
                                    act.tv_dec4_r.setText(act.mCharacter.getScore().getJP_PropensityScore() + "% ");
                                    act.tv_zhijue2.setTextColor(act.getResources().getColor(R.color.seekbar_color));
                                    act.tv_panduan.setTextColor(act.getResources().getColor(R.color.black));
                                    act.tv_dec4_l.setVisibility(View.GONE);
                                    act.tv_dec4_r.setVisibility(View.VISIBLE);
                                }else  if (act.mCharacter.getScore().getJP_PropensityScore() == 0) {
                                    act.rs_character3.setCharacter(0.5f, 0.5f);
                                    act.tv_dec4_r.setVisibility(View.GONE);
                                    act.tv_dec4_l.setVisibility(View.GONE);
                                }else {
                                    act.rs_character3.setCharacter((float) (0.5f + act.mCharacter.getScore().getJP_PropensityScore() / 100), 0.5f);
                                    act.tv_dec4_l.setText(Math.abs(act.mCharacter.getScore().getJP_PropensityScore()) + "% ");
                                    act.tv_panduan.setTextColor(act.getResources().getColor(R.color.seekbar_color));
                                    act.tv_zhijue2.setTextColor(act.getResources().getColor(R.color.black));
                                    act.tv_dec4_r.setVisibility(View.GONE);
                                    act.tv_dec4_l.setVisibility(View.VISIBLE);
                                }

                                if(act.testType!=null){
                                    act.tv_test_type.setText(act.testType+"测试");
                                }


                            } else {
                                act.tv_test_type.setText("选择测试");
//                            MyToast.show(act.mContext, "服务器返回值异常");
                                act.rs_character.setCharacter(0.5f, 0.5f);
                                act.rs_character1.setCharacter(0.5f, 0.5f);
                                act.rs_character2.setCharacter(0.5f, 0.5f);
                                act.rs_character3.setCharacter(0.5f, 0.5f);
                            }
                        } else {
//                            MyToast.show(act.mContext, "服务器返回值异常");
                            act.rs_character.setCharacter(0.5f, 0.5f);
                            act.tv_dec1_r.setText(" ");
                            act.rs_character1.setCharacter(0.5f, 0.5f);
                            act.tv_dec2_r.setText(" ");
                            act.rs_character2.setCharacter(0.5f, 0.5f);
                            act.tv_dec3_r.setText(" ");
                            act.rs_character3.setCharacter(0.5f, 0.5f);
                            act.tv_dec4_r.setText(" ");
                        }
                    }
                    break;
                case REFRESH_DATA:
//                    act.mRecyclerView_Yaoqing.setVisibility(View.GONE);
                    if (act.pg != null && act.pg.isShowing()) {
                        act.pg.dismiss();
                    }
                    break;
                case END:
                    if (act.pg != null && act.pg.isShowing()) {
                        act.pg.dismiss();
                    }
                    break;
                case POP_DIALOG:
                    if (act != null) {
                        if (act.pg != null && act.pg.isShowing()) {
                            act.pg.dismiss();
                        }
//                    act.showCharacterInfoDialog();
                        if (act.mCharacterPuPopList != null && act.mCharacterPuPopList.size() > 0) {
                            act.showCharacterInfoDialog();
                        } else {
                            MyToast.show(act.mContext, "暂无该性格谱信息");
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }


    /**
     * 性格谱弹出窗
     */
    int currentItem = 0;
//    ImageView[] imgs;
    String currentType="";

    private void showCharacterInfoDialog() {
        currentItem = 0;
        View view = mContext.getLayoutInflater().inflate(R.layout.dialog_character_info, null);
        final Dialog dialog = new Dialog(mContext, com.hyphenate.easeui.R.style.Dialog_Fullscreen);
        dialog.setContentView(view, new ViewGroup.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        Window window = dialog.getWindow();

        final int popSize = mCharacterPuPopList.size();
        final ViewPager viewPager = (ViewPager) view.findViewById(R.id.main_vp_container);
        final TextView ib_character_qiehuan_info = (TextView) view.findViewById(R.id.ib_character_qiehuan_info);
        ib_character_qiehuan_info.setText(1 + "/" + popSize);
        ArrayList<View> views = new ArrayList<View>();
        LayoutInflater inflater = mContext.getLayoutInflater();

        for (int i = 0; i < popSize; i++) {
            View view1 = inflater.inflate(R.layout.item_xinggepu, null);
            SimpleDraweeView character_label_pic = (SimpleDraweeView) view1.findViewById(R.id.character_label_pic0);
            TextView tv_character_part1 = (TextView) view1.findViewById(R.id.tv_character_part0);
            TextView tv_character_pop_title = (TextView) view1.findViewById(R.id.tv_character_pop_title);
            tv_character_pop_title.setText(mCharacterPuPopList.get(i).getTitle());
            if (mCharacterPuPopList.get(i).getPhotoUrl() != null) {
                character_label_pic.setImageURI(Uri.parse(mCharacterPuPopList.get(i).getPhotoUrl()));
                tv_character_part1.setText(mCharacterPuPopList.get(i).getText()!=null?mCharacterPuPopList.get(i).getText():mCharacterPuPopList.get(i).getContent());
            }
            views.add(view1);
        }
        viewPager.setAdapter(new MyViewPagerAdapter(views));
        viewPager.setCurrentItem(0);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
                currentItem=position;
                ib_character_qiehuan_info.setText(position + 1 + "/" + popSize);
//                    }
//                });
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });


//        view.findViewById(R.id.tv_sanguan_close).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//                dialog.dismiss();
//            }
//        });
        view.findViewById(R.id.tv_return_pop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.ib_character_qiehuan_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentItem > 0) {
                    currentItem--;
                    viewPager.setCurrentItem(currentItem);
                }
            }
        });
        view.findViewById(R.id.ib_character_qiehuan_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentItem < popSize - 1) {
                    currentItem++;
                    viewPager.setCurrentItem(currentItem);
                }
            }
        });
//      分享到朋友圈
        view.findViewById(R.id.iv_pop_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        wechatShare(SendMessageToWX.Req.WXSceneTimeline, mCharacterPuPopList.get(currentItem).getId(),
                                mCharacterPuPopList.get(currentItem).getPhotoUrl(), mCharacterPuPopList.get(currentItem).getTitle(),currentType);
                    }
                }).start();
            }
        });
        // 设置显示动画
        window.setWindowAnimations(com.hyphenate.easeui.R.style.main_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = mContext.getWindowManager().getDefaultDisplay().getHeight();
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.MATCH_PARENT;//WRAP_CONTENT

        // 设置显示位置
        dialog.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        dialog.setCanceledOnTouchOutside(true);
        if(!((Activity) mContext).isFinishing()) {
            dialog.show();
        }
    }




    /**
     * 下面的内容为弹出分享到朋友圈
     */

    private final int LoadingError = 1;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LoadingError:
                    MyToast.show(mContext, "当前版本不支持分享功能");
                    break;
            }
            super.handleMessage(msg);
        }
    };
    private IWXAPI wxApi;
    private void wechatShare(final int flag, final String id, String photoUrl, final String title, final String type) {
        wxApi = WXAPIFactory.createWXAPI(mContext, WowuApp.WeChat_APP_ID, false);
        wxApi.registerApp(WowuApp.WeChat_APP_ID);
        if (!wxApi.isWXAppSupportAPI()) {
            Message msg = new Message();
            msg.what = LoadingError;
            mHandler.sendMessage(msg);
            return;
        }
        UtilsTool temp=new UtilsTool();
        temp.wechatShare(mContext,wxApi,  flag, id,  photoUrl,title,type);
    }


}

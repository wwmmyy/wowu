package com.wuwo.im.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.chatuidemo.DemoModel;
import com.hyphenate.chatuidemo.ui.BlacklistActivity;
import com.hyphenate.chatuidemo.ui.DiagnoseActivity;
import com.hyphenate.chatuidemo.ui.UserProfileActivity;
import com.hyphenate.easeui.widget.EaseSwitchButton;
import com.wuwo.im.config.WowuApp;
import com.wuwo.im.util.DateTimePickerDialog;
import com.wuwo.im.view.MaterialDialog;

import java.text.SimpleDateFormat;

import im.imxianzhi.com.imxianzhi.R;

public class UserSetWarnActivity extends BaseLoadActivity {
    TextView tv_set_pwd, top_title;


    /**
     * new message notification
     */
    private RelativeLayout rl_switch_notification;
    /**
     * sound
     */
    private RelativeLayout rl_switch_sound;
    /**
     * vibration
     */
    private RelativeLayout rl_switch_vibrate;
    /**
     * speaker
     */
    private RelativeLayout rl_switch_speaker;


    /**
     * line between sound and vibration
     */
    private TextView textview1, textview2,tv_locktime;

//    private LinearLayout blacklistContainer;

//    private LinearLayout userProfileContainer;

    /**
     * logout
     */
//    private Button logoutBtn;

//    private RelativeLayout rl_switch_chatroom_leave;

//    private RelativeLayout rl_switch_delete_msg_when_exit_group;
//    private RelativeLayout rl_switch_auto_accept_group_invitation;
//    private RelativeLayout rl_switch_adaptive_video_encode;

    /**
     * Diagnose
     */
//    private LinearLayout llDiagnose;
    /**
     * display name for APNs
     */
    private LinearLayout pushNick;

    private EaseSwitchButton notifiSwitch;
    private EaseSwitchButton soundSwitch;
    private EaseSwitchButton vibrateSwitch;
    private EaseSwitchButton speakerSwitch;
//    private EaseSwitchButton ownerLeaveSwitch;
//    private EaseSwitchButton switch_delete_msg_when_exit_group;
//    private EaseSwitchButton switch_auto_accept_group_invitation;
//    private EaseSwitchButton switch_adaptive_video_encode;
    private DemoModel settingsModel;
    private EMOptions chatOptions;
    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_set_warn);
        settings = this.getSharedPreferences(WowuApp.PREFERENCE_KEY, MODE_PRIVATE);

        top_title = (TextView) findViewById(R.id.top_title);
        top_title.setText("消息提醒");
        findViewById(R.id.return_back).setOnClickListener(this);

        initView();

    }

    private void initView() {

//        if(savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false))
//            return;
        rl_switch_notification = (RelativeLayout) findViewById(R.id.rl_switch_notification);
        rl_switch_sound = (RelativeLayout)  findViewById(R.id.rl_switch_sound);
        rl_switch_vibrate = (RelativeLayout) findViewById(R.id.rl_switch_vibrate);
        rl_switch_speaker = (RelativeLayout)  findViewById(R.id.rl_switch_speaker);
//        rl_switch_chatroom_leave = (RelativeLayout) getView().findViewById(R.id.rl_switch_chatroom_owner_leave);
//        rl_switch_delete_msg_when_exit_group = (RelativeLayout) getView().findViewById(R.id.rl_switch_delete_msg_when_exit_group);
//        rl_switch_auto_accept_group_invitation = (RelativeLayout) getView().findViewById(R.id.rl_switch_auto_accept_group_invitation);
//        rl_switch_adaptive_video_encode = (RelativeLayout) getView().findViewById(R.id.rl_switch_adaptive_video_encode);

        notifiSwitch = (EaseSwitchButton)findViewById(R.id.switch_notification);
        soundSwitch = (EaseSwitchButton)  findViewById(R.id.switch_sound);
        vibrateSwitch = (EaseSwitchButton) findViewById(R.id.switch_vibrate);
        speakerSwitch = (EaseSwitchButton) findViewById(R.id.switch_speaker);
//        ownerLeaveSwitch = (EaseSwitchButton) getView().findViewById(R.id.switch_owner_leave);
//        switch_delete_msg_when_exit_group = (EaseSwitchButton) getView().findViewById(R.id.switch_delete_msg_when_exit_group);
//        switch_auto_accept_group_invitation = (EaseSwitchButton) getView().findViewById(R.id.switch_auto_accept_group_invitation);
//        switch_adaptive_video_encode = (EaseSwitchButton) getView().findViewById(R.id.switch_adaptive_video_encode);
//        LinearLayout llChange = (LinearLayout) getView().findViewById(R.id.ll_change);
//        logoutBtn = (Button) getView().findViewById(R.id.btn_logout);
//        if(!TextUtils.isEmpty(EMClient.getInstance().getCurrentUser())){
//            logoutBtn.setText(getString(R.string.button_logout) + "(" + EMClient.getInstance().getCurrentUser() + ")");
//        }

        textview1 = (TextView)  findViewById(R.id.textview1);
        textview2 = (TextView)  findViewById(R.id.textview2);
        tv_locktime= (TextView)  findViewById(R.id.tv_locktime);
        tv_locktime.setText( settings.getString("locktime","00:00--08：00"));


//        blacklistContainer = (LinearLayout) getView().findViewById(R.id.ll_black_list);
//        userProfileContainer = (LinearLayout) getView().findViewById(R.id.ll_user_profile);
//        llDiagnose=(LinearLayout) getView().findViewById(R.id.ll_diagnose);
        pushNick=(LinearLayout)  findViewById(R.id.ll_set_push_nick);

        settingsModel = DemoHelper.getInstance().getModel();
        chatOptions = EMClient.getInstance().getOptions();

//        blacklistContainer.setOnClickListener(this);
//        userProfileContainer.setOnClickListener(this);
        rl_switch_notification.setOnClickListener(this);
        rl_switch_sound.setOnClickListener(this);
        rl_switch_vibrate.setOnClickListener(this);
        rl_switch_speaker.setOnClickListener(this);
//        logoutBtn.setOnClickListener(this);
//        llDiagnose.setOnClickListener(this);
        pushNick.setOnClickListener(this);
//        rl_switch_chatroom_leave.setOnClickListener(this);
//        rl_switch_delete_msg_when_exit_group.setOnClickListener(this);
//        rl_switch_auto_accept_group_invitation.setOnClickListener(this);
//        rl_switch_adaptive_video_encode.setOnClickListener(this);
//        llChange.setOnClickListener(this);

        // the vibrate and sound notification are allowed or not?
        if (settingsModel.getSettingMsgNotification()) {
            notifiSwitch.openSwitch();
        } else {
            notifiSwitch.closeSwitch();
        }

        // sound notification is switched on or not?
        if (settingsModel.getSettingMsgSound()) {
            soundSwitch.openSwitch();
        } else {
            soundSwitch.closeSwitch();
        }

        // vibrate notification is switched on or not?
        if (settingsModel.getSettingMsgVibrate()) {
            vibrateSwitch.openSwitch();
        } else {
            vibrateSwitch.closeSwitch();
        }

        // the speaker is switched on or not?
        if (settingsModel.getSettingMsgSpeaker()) {
            speakerSwitch.openSwitch();
        } else {
            speakerSwitch.closeSwitch();
        }

/*        // if allow owner leave
        if(settingsModel.isChatroomOwnerLeaveAllowed()){
            ownerLeaveSwitch.openSwitch();
        }else{
            ownerLeaveSwitch.closeSwitch();
        }

        // delete messages when exit group?
        if(settingsModel.isDeleteMessagesAsExitGroup()){
            switch_delete_msg_when_exit_group.openSwitch();
        } else {
            switch_delete_msg_when_exit_group.closeSwitch();
        }

        if (settingsModel.isAutoAcceptGroupInvitation()) {
            switch_auto_accept_group_invitation.openSwitch();
        } else {
            switch_auto_accept_group_invitation.closeSwitch();
        }

        if (settingsModel.isAdaptiveVideoEncode()) {
            switch_adaptive_video_encode.openSwitch();
            EMClient.getInstance().callManager().getVideoCallHelper().setAdaptiveVideoFlag(true);
        } else {
            switch_adaptive_video_encode.closeSwitch();
            EMClient.getInstance().callManager().getVideoCallHelper().setAdaptiveVideoFlag(false);
        }*/






    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

/*            case R.id.ll_change:
                RedPacketUtil.startChangeActivity(getActivity());
                break;*/
            case R.id.rl_switch_notification:
                if (notifiSwitch.isSwitchOpen()) {
                    notifiSwitch.closeSwitch();
                    rl_switch_sound.setVisibility(View.GONE);
                    rl_switch_vibrate.setVisibility(View.GONE);
                    textview1.setVisibility(View.GONE);
                    textview2.setVisibility(View.GONE);

                    settingsModel.setSettingMsgNotification(false);
                } else {
                    notifiSwitch.openSwitch();
                    rl_switch_sound.setVisibility(View.VISIBLE);
                    rl_switch_vibrate.setVisibility(View.VISIBLE);
                    textview1.setVisibility(View.VISIBLE);
                    textview2.setVisibility(View.VISIBLE);
                    settingsModel.setSettingMsgNotification(true);
                }
                break;
            case R.id.rl_switch_sound:
                if (soundSwitch.isSwitchOpen()) {
                    soundSwitch.closeSwitch();
                    settingsModel.setSettingMsgSound(false);
                } else {
                    soundSwitch.openSwitch();
                    settingsModel.setSettingMsgSound(true);
                }
                break;
            case R.id.rl_switch_vibrate:
                if (vibrateSwitch.isSwitchOpen()) {
                    vibrateSwitch.closeSwitch();
                    settingsModel.setSettingMsgVibrate(false);
                } else {
                    vibrateSwitch.openSwitch();
                    settingsModel.setSettingMsgVibrate(true);
                }
                break;
            case R.id.rl_switch_speaker:
                if (speakerSwitch.isSwitchOpen()) {
                    speakerSwitch.closeSwitch();
                    settingsModel.setSettingMsgSpeaker(false);
                } else {
                    speakerSwitch.openSwitch();
                    settingsModel.setSettingMsgVibrate(true);
                }
                break;
          /*  case R.id.rl_switch_chatroom_owner_leave:
                if(ownerLeaveSwitch.isSwitchOpen()){
                    ownerLeaveSwitch.closeSwitch();
                    settingsModel.allowChatroomOwnerLeave(false);
                    chatOptions.allowChatroomOwnerLeave(false);
                }else{
                    ownerLeaveSwitch.openSwitch();
                    settingsModel.allowChatroomOwnerLeave(true);
                    chatOptions.allowChatroomOwnerLeave(true);
                }
                break;
            case R.id.rl_switch_delete_msg_when_exit_group:
                if(switch_delete_msg_when_exit_group.isSwitchOpen()){
                    switch_delete_msg_when_exit_group.closeSwitch();
                    settingsModel.setDeleteMessagesAsExitGroup(false);
                    chatOptions.setDeleteMessagesAsExitGroup(false);
                }else{
                    switch_delete_msg_when_exit_group.openSwitch();
                    settingsModel.setDeleteMessagesAsExitGroup(true);
                    chatOptions.setDeleteMessagesAsExitGroup(true);
                }
                break;
            case R.id.rl_switch_auto_accept_group_invitation:
                if(switch_auto_accept_group_invitation.isSwitchOpen()){
                    switch_auto_accept_group_invitation.closeSwitch();
                    settingsModel.setAutoAcceptGroupInvitation(false);
                    chatOptions.setAutoAcceptGroupInvitation(false);
                }else{
                    switch_auto_accept_group_invitation.openSwitch();
                    settingsModel.setAutoAcceptGroupInvitation(true);
                    chatOptions.setAutoAcceptGroupInvitation(true);
                }
                break;
            case R.id.rl_switch_adaptive_video_encode:
                EMLog.d("switch", "" + !switch_adaptive_video_encode.isSwitchOpen());
                if (switch_adaptive_video_encode.isSwitchOpen()){
                    switch_adaptive_video_encode.closeSwitch();
                    settingsModel.setAdaptiveVideoEncode(false);
                    EMClient.getInstance().callManager().getVideoCallHelper().setAdaptiveVideoFlag(false);
                }else{
                    switch_adaptive_video_encode.openSwitch();
                    settingsModel.setAdaptiveVideoEncode(true);
                    EMClient.getInstance().callManager().getVideoCallHelper().setAdaptiveVideoFlag(true);
                }
                break;*/
            case R.id.ll_black_list:
                startActivity(new Intent(mContext, BlacklistActivity.class));
                break;
            case R.id.ll_diagnose:
                startActivity(new Intent(mContext, DiagnoseActivity.class));
                break;
            case R.id.ll_set_push_nick:
//                startActivity(new Intent(mContext, OfflinePushNickActivity.class));
//                MyToast.show(mContext,"后续升级");
                  showTimeDialog();
                break;
            case R.id.ll_user_profile:
                startActivity(new Intent(mContext, UserProfileActivity.class).putExtra("setting", true)
                        .putExtra("username", EMClient.getInstance().getCurrentUser()));
                break;

            case R.id.tv_set_pwd_finish:
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.return_back:
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        if(.isConflict){
//            outState.putBoolean("isConflict", true);
//        }else if(getCurrentAccountRemoved()){
//            outState.putBoolean(Constant.ACCOUNT_REMOVED, true);
//        }
    }



    /**
     * 添加日期
     * @Title: showTimeDialog
     * @Description: TODO
     * @param
     * @return void
     * @throws
     */
    String StartTime="";
    String EndTime="";
    long dateStart;
    long dateEnd;
    public void showTimeDialog()
    {
        DateTimePickerDialog dialog  = null;
//        if(addDate!=null){
//            try {
//                dialog  = new DateTimePickerDialog(this,  (new SimpleDateFormat("yyyy-MM-dd HH:mm")).parse(addDate+" "+new SimpleDateFormat("HH:mm").format(new Date())).getTime());
//            } catch (ParseException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }}else{
            dialog  = new DateTimePickerDialog(this, System.currentTimeMillis());
//        }
        dialog.setOnDateTimeSetListener(new DateTimePickerDialog.OnDateTimeSetListener()
        {
            @Override
            public void OnDateTimeSet(MaterialDialog dialog, long date, long date1) {
//                Toast.makeText(mContext, "您输入的日期是："+getStringDate(date,date1), Toast.LENGTH_LONG).show();
                tv_locktime.setText(getStringDate(date,date1));
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("locktime", tv_locktime.getText().toString()+"");
                editor.commit();

            }
        });
        dialog.show();
        //Toast.makeText(mContext, date_selected, 1).show();
    }


    public   String getStringDate(Long date, long date1)
    {
        SimpleDateFormat formatter0 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//        String dateString = formatter.format(date);

        SimpleDateFormat formatter1 = new SimpleDateFormat("HH:mm");
        String dateString1 = formatter1.format(date1);
        String dateString = formatter1.format(date);


        StartTime=formatter0.format(date);
        EndTime=formatter0.format(date1);

        return dateString+"--"+dateString1;
//        return dateString+"--"+dateString1;

    }


    @Override
    public void loadServerData(String response, int flag) {

    }

    @Override
    public void loadDataFailed(String response, int flag) {

    }
}

package com.hyphenate.chatuidemo.ui;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.redpacketui.RedPacketConstant;
import com.easemob.redpacketui.utils.RedPacketUtil;
import com.easemob.redpacketui.widget.ChatRowRedPacket;
import com.easemob.redpacketui.widget.ChatRowRedPacketAck;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chatuidemo.Constant;
import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.chatuidemo.domain.EmojiconExampleGroupData;
import com.hyphenate.chatuidemo.domain.RobotUser;
import com.hyphenate.chatuidemo.widget.ChatRowVoiceCall;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.ui.EaseChatFragment;
import com.hyphenate.easeui.ui.EaseChatFragment.EaseChatFragmentHelper;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.hyphenate.easeui.widget.chatrow.EaseCustomChatRowProvider;
import com.hyphenate.easeui.widget.emojicon.EaseEmojiconMenu;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.EasyUtils;
import com.hyphenate.util.PathUtil;
import com.wuwo.im.activity.UserInfoEditActivity;
import com.wuwo.im.bean.LocalUser;
import com.wuwo.im.bean.SanGuan;
import com.wuwo.im.config.WowuApp;
import com.wuwo.im.util.MyToast;
import com.wuwo.im.util.UtilsTool;
import com.zhy.http.okhttp.service.LoadserverdataService;
import com.zhy.http.okhttp.service.loadServerDataListener;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;

import im.wuwo.com.wuwo.R;

public class ChatFragment extends EaseChatFragment implements EaseChatFragmentHelper, loadServerDataListener {

    // constant start from 11 to avoid conflict with constant in base class
    private static final int ITEM_VIDEO = 11;
    private static final int ITEM_FILE = 12;
    private static final int ITEM_VOICE_CALL = 13;
    private static final int ITEM_VIDEO_CALL = 14;
    private static final int ITEM_RED_PACKET = 16;

    private static final int REQUEST_CODE_SELECT_VIDEO = 11;
    private static final int REQUEST_CODE_SELECT_FILE = 12;
    private static final int REQUEST_CODE_GROUP_DETAIL = 13;
    private static final int REQUEST_CODE_CONTEXT_MENU = 14;
    private static final int REQUEST_CODE_SELECT_AT_USER = 15;

    private static final int REQUEST_CODE_SEND_MONEY = 16;

    private static final int MESSAGE_TYPE_SENT_VOICE_CALL = 1;
    private static final int MESSAGE_TYPE_RECV_VOICE_CALL = 2;
    private static final int MESSAGE_TYPE_SENT_VIDEO_CALL = 3;
    private static final int MESSAGE_TYPE_RECV_VIDEO_CALL = 4;

    private static final int MESSAGE_TYPE_RECV_MONEY = 5;
    private static final int MESSAGE_TYPE_SEND_MONEY = 6;
    private static final int MESSAGE_TYPE_SEND_LUCKY = 7;
    private static final int MESSAGE_TYPE_RECV_LUCKY = 8;


    LoadserverdataService loadDataService;
    private final int LOAD_DATA = 100;
    private final int SANGUAN_PICK = 200;
    private final int SANGUAN_PICK2 = 202;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        loadDataService = new LoadserverdataService(this);
        mtotalHandler = new mHandlerWeak(this);
    }

    /**
     * if it is chatBot
     */
    private boolean isRobot;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void setUpView() {
        setChatFragmentListener(this);
        if (chatType == Constant.CHATTYPE_SINGLE) {
            Map<String, RobotUser> robotMap = DemoHelper.getInstance().getRobotList();
            if (robotMap != null && robotMap.containsKey(toChatUsername)) {
                isRobot = true;
            }
        }
        super.setUpView();
        // set click listener
        titleBar.setLeftLayoutClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (EasyUtils.isSingleActivity(getActivity())) {
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                }
                getActivity().finish();
                getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            }
        });

        if (toChatUserName_Show != null) {
            titleBar.setTitle(toChatUserName_Show);
        }

//        toChatUsername

//

        titleBar.getRight_image_user().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(getActivity(), UserInfoEditActivity.class);
                LocalUser.DataBean temp=new LocalUser.DataBean();
                temp.setUserId((toChatUsername != null && !toChatUsername.equals("")) == true ? toChatUsername : chatUserId);
                if(mSanGuan!=null){
                    temp.setPhotoUrl(mSanGuan.getUserPhotoUrl2());
                }
                intent2.putExtra("localUser",temp);
                startActivity(intent2);
                getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });



        ((EaseEmojiconMenu) inputMenu.getEmojiconMenu()).addEmojiconGroup(EmojiconExampleGroupData.getData());
        if (chatType == EaseConstant.CHATTYPE_GROUP) {
            inputMenu.getPrimaryMenu().getEditText().addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (count == 1 && "@".equals(String.valueOf(s.charAt(start)))) {
                        startActivityForResult(new Intent(getActivity(), PickAtUserActivity.class).
                                putExtra("groupId", toChatUsername), REQUEST_CODE_SELECT_AT_USER);
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            EMGroup group = EMClient.getInstance().groupManager().getGroup(toChatUsername);
            if (group == null || group.getAffiliationsCount() <= 0) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            EMClient.getInstance().groupManager().getGroupFromServer(toChatUsername);
                        } catch (HyphenateException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }


//        loadDataService.loadGetJsonRequestData(OkHttpUtils.GetUserInfoURL + "?userId=" + toChatUsername, LOAD_DATA);
        // hold to record voice
//        iv_sanguan_pick.setImageResource(); 应该根据性别决定显示图片背景
        iv_sanguan_pick.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mSanGuan != null && mSanGuan.getTitle() != null) { //说明起始加载三观配内容成功
                    showSanguanPickDialog();
                } else {

                    pd = UtilsTool.initProgressDialog(getActivity(), "请稍后...");
                    pd.show();
                    loadSanGuan(SANGUAN_PICK2);
                }


            }
        });
        loadSanGuan(SANGUAN_PICK);
    }

    private ProgressDialog pd;

    private void loadSanGuan(final int SANGUAN_PICK0) {
        final String mchatUserId = ((toChatUsername != null && !toChatUsername.equals("")) == true ? toChatUsername : chatUserId);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject json = new JSONObject();
                    json.put("userId", mchatUserId);
                    loadDataService.loadPostJsonRequestData(WowuApp.JSON, WowuApp.MatchURL + "?userId=" + mchatUserId, json.toString(), SANGUAN_PICK0);

                    Log.i("ChatFragment::", WowuApp.MatchURL + "?userId=" + mchatUserId + "toChatUsername::" + toChatUsername);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    /**
     * 用户三观配显示，该模块后面要移到会话模块，暂时在这里测试
     */
    private void showSanguanPickDialog() {
        View view = getActivity().getLayoutInflater().inflate(com.hyphenate.easeui.R.layout.dialog_sanguan_pick, null);
        final Dialog dialog = new Dialog(getActivity(), com.hyphenate.easeui.R.style.transparentFrameWindowStyle);
        dialog.setContentView(view, new ViewGroup.LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();
        view.findViewById(com.hyphenate.easeui.R.id.iv_sanguan_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });
        view.findViewById(com.hyphenate.easeui.R.id.tv_sanguan_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });

        TextView tv_sanguan_user1 = (TextView) view.findViewById(com.hyphenate.easeui.R.id.tv_sanguan_user1);
        TextView tv_sanguan_user2 = (TextView) view.findViewById(com.hyphenate.easeui.R.id.tv_sanguan_user2);
        tv_sanguan_user2.setText(getActivity().getSharedPreferences(WowuApp.PREFERENCE_KEY, getActivity().MODE_PRIVATE).getString("Name", "http://#"));
        if (mSanGuan != null) {
            tv_sanguan_user1.setText(mSanGuan.getUserName2());
        }


        TextView genderm = (TextView) view.findViewById(com.hyphenate.easeui.R.id.tvage_gender1_male);
//        TextView genderw = (TextView) view.findViewById(com.hyphenate.easeui.R.id.tvage_gender1_female);
//        if (mSanGuan.getUserGender2() == 0) {
//            genderm.setVisibility(View.VISIBLE);
//            genderw.setVisibility(View.GONE);
//            genderm.setText(mSanGuan.getUserAge2() + "");
//        } else {
//            genderm.setVisibility(View.GONE);
//            genderw.setVisibility(View.VISIBLE);
//            genderw.setText(mSanGuan.getUserAge2() + "");
//        }

        genderm.setText(mSanGuan.getUserAge2() + " | " + mSanGuan.getUserDisposition2());

        TextView genderm2 = (TextView) view.findViewById(com.hyphenate.easeui.R.id.tvage_gender2_male);
//        TextView genderw2 = (TextView) view.findViewById(com.hyphenate.easeui.R.id.tvage_gender2_female);
//        if (mSanGuan.getUserGender1() == 0) {
//            genderm2.setVisibility(View.VISIBLE);
//            genderw2.setVisibility(View.GONE);
//            genderm2.setText(mSanGuan.getUserAge1() + "");
//        } else {
//            genderm2.setVisibility(View.GONE);
//            genderw2.setVisibility(View.VISIBLE);
//            genderw2.setText(mSanGuan.getUserAge1() + "");
//        }

        genderm2.setText(mSanGuan.getUserAge1() + " | " + mSanGuan.getUserDisposition1());


/*        TextView genderm1 = (TextView) view.findViewById(com.hyphenate.easeui.R.id.tvage_gender2_male);
        TextView genderw1 = (TextView) view.findViewById(com.hyphenate.easeui.R.id.tvage_gender2_female);
        if (getActivity().getSharedPreferences(WowuApp.PREFERENCE_KEY, getActivity().MODE_PRIVATE).getInt("Gender", 0) == 0) {
            genderm1.setVisibility(View.VISIBLE);
            genderw1.setVisibility(View.GONE);
            genderm1.setText(getActivity().getSharedPreferences(WowuApp.PREFERENCE_KEY, getActivity().MODE_PRIVATE).getInt("Gender", 0) + "");
        } else {
            genderm1.setVisibility(View.GONE);
            genderw1.setVisibility(View.VISIBLE);
            genderw1.setText(getActivity().getSharedPreferences(WowuApp.PREFERENCE_KEY, getActivity().MODE_PRIVATE).getInt("Gender", 0) + "");
        }*/

        TextView tv_sanguan_result = (TextView) view.findViewById(com.hyphenate.easeui.R.id.tv_sanguan_result);
        TextView tv_sanguan_shuoming = (TextView) view.findViewById(com.hyphenate.easeui.R.id.tv_sanguan_shuoming);


        if (mSanGuan != null) {
            tv_sanguan_result.setText(mSanGuan.getTitle());
            tv_sanguan_shuoming.setText(mSanGuan.getDescription());


            SimpleDraweeView draweeView = (SimpleDraweeView) view.findViewById(com.hyphenate.easeui.R.id.sdv_sanguan_pic2 );
//        if (toChatUsername != null && EaseImageUtils.usersPhotoUrl.get(toChatUsername) != null) {
//            draweeView.setImageURI(Uri.parse(EaseImageUtils.usersPhotoUrl.get(toChatUsername)));//"http://w messageList.getItem(0).getTo()
//        } else if (iconPath != null) {
//            draweeView.setImageURI(Uri.parse(iconPath));//"http://w messageList.getItem(0).getTo()
//        }


            draweeView.setImageURI(Uri.parse(mSanGuan.getUserPhotoUrl1()));
            SimpleDraweeView draweeView2 = (SimpleDraweeView) view.findViewById(com.hyphenate.easeui.R.id.sdv_sanguan_pic1);
//        draweeView2.setImageURI(Uri.parse( EaseImageUtils.usersPhotoUrl.get( getActivity().getSharedPreferences(WowuApp.PREFERENCE_KEY,getActivity().MODE_PRIVATE).getString("UserId", "http://#"))));//"http://w messageList.getItem(0).getFrom() WowuApp.UserId EMClient.getInstance().getCurrentUser()
//        draweeView2.setImageURI(Uri.parse(WowuApp.iconPath));
            draweeView2.setImageURI(Uri.parse(mSanGuan.getUserPhotoUrl2()));
            GenericDraweeHierarchy hierarchy2 = draweeView.getHierarchy();
            GenericDraweeHierarchy hierarchy1 = draweeView2.getHierarchy();

            if (mSanGuan.getUserGender1() == 1) {
                hierarchy1.setControllerOverlay(getResources().getDrawable(R.drawable.overlay_male));
            } else {
                hierarchy1.setControllerOverlay(getResources().getDrawable(R.drawable.overlay_fmale));
            }

            if (mSanGuan.getUserGender2() == 1) {
                hierarchy2.setControllerOverlay(getResources().getDrawable(R.drawable.overlay_male));
            } else {
                hierarchy2.setControllerOverlay(getResources().getDrawable(R.drawable.overlay_fmale));
            }
        }


        // 设置显示动画
        window.setWindowAnimations(com.hyphenate.easeui.R.style.main_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = getActivity().getWindowManager().getDefaultDisplay().getHeight();
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.MATCH_PARENT;//WRAP_CONTENT

        // 设置显示位置
        dialog.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }


    @Override
    protected void registerExtendMenuItem() {
        //use the menu in base class
        super.registerExtendMenuItem();
        //extend menu items
//        暂时屏蔽掉扩展的功能
//        inputMenu.registerExtendMenuItem(R.string.attach_video, R.drawable.em_chat_video_selector, ITEM_VIDEO, extendMenuItemClickListener);
//        inputMenu.registerExtendMenuItem(R.string.attach_file, R.drawable.em_chat_file_selector, ITEM_FILE, extendMenuItemClickListener);
//        if (chatType == Constant.CHATTYPE_SINGLE) {
//            inputMenu.registerExtendMenuItem(R.string.attach_voice_call, R.drawable.em_chat_voice_call_selector, ITEM_VOICE_CALL, extendMenuItemClickListener);
//            inputMenu.registerExtendMenuItem(R.string.attach_video_call, R.drawable.em_chat_video_call_selector, ITEM_VIDEO_CALL, extendMenuItemClickListener);
//        }
//        //no red packet in chatroom
//        if (chatType != Constant.CHATTYPE_CHATROOM) {
//            inputMenu.registerExtendMenuItem(R.string.attach_red_packet, R.drawable.em_chat_red_packet_selector, ITEM_RED_PACKET, extendMenuItemClickListener);
//        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CONTEXT_MENU) {
            switch (resultCode) {
                case ContextMenuActivity.RESULT_CODE_COPY: // copy
                    clipboard.setPrimaryClip(ClipData.newPlainText(null,
                            ((EMTextMessageBody) contextMenuMessage.getBody()).getMessage()));
                    break;
                case ContextMenuActivity.RESULT_CODE_DELETE: // delete
                    conversation.removeMessage(contextMenuMessage.getMsgId());
                    messageList.refresh();
                    break;

                case ContextMenuActivity.RESULT_CODE_FORWARD: // forward
                    Intent intent = new Intent(getActivity(), ForwardMessageActivity.class);
                    intent.putExtra("forward_msg_id", contextMenuMessage.getMsgId());
                    startActivity(intent);
                    getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    break;

                default:
                    break;
            }
        }
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_SELECT_VIDEO: //send the video
                    if (data != null) {
                        int duration = data.getIntExtra("dur", 0);
                        String videoPath = data.getStringExtra("path");
                        File file = new File(PathUtil.getInstance().getImagePath(), "thvideo" + System.currentTimeMillis());
                        try {
                            FileOutputStream fos = new FileOutputStream(file);
                            Bitmap ThumbBitmap = ThumbnailUtils.createVideoThumbnail(videoPath, 3);
                            ThumbBitmap.compress(CompressFormat.JPEG, 100, fos);
                            fos.close();
                            sendVideoMessage(videoPath, file.getAbsolutePath(), duration);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case REQUEST_CODE_SELECT_FILE: //send the file
                    if (data != null) {
                        Uri uri = data.getData();
                        if (uri != null) {
                            sendFileByUri(uri);
                        }
                    }
                    break;
                case REQUEST_CODE_SELECT_AT_USER:
                    if (data != null) {
                        String username = data.getStringExtra("username");
                        inputAtUsername(username, false);
                    }
                    break;

                case REQUEST_CODE_SEND_MONEY:
                    if (data != null) {
                        sendMessage(RedPacketUtil.createRPMessage(getActivity(), data, toChatUsername));
                    }
                    break;
                default:
                    break;
            }
        }

    }

    @Override
    public void onSetMessageAttributes(EMMessage message) {
        if (isRobot) {
            //set message extension
            message.setAttribute("em_robot_message", isRobot);
        }
    }

    @Override
    public EaseCustomChatRowProvider onSetCustomChatRowProvider() {
        return new CustomChatRowProvider();
    }


    @Override
    public void onEnterToChatDetails() {
        if (chatType == Constant.CHATTYPE_GROUP) {
            EMGroup group = EMClient.getInstance().groupManager().getGroup(toChatUsername);
            if (group == null) {
                Toast.makeText(getActivity(), R.string.gorup_not_found, Toast.LENGTH_SHORT).show();
                return;
            }
            startActivityForResult(
                    (new Intent(getActivity(), GroupDetailsActivity.class).putExtra("groupId", toChatUsername)),
                    REQUEST_CODE_GROUP_DETAIL);
        } else if (chatType == Constant.CHATTYPE_CHATROOM) {
            startActivityForResult(new Intent(getActivity(), ChatRoomDetailsActivity.class).putExtra("roomId", toChatUsername), REQUEST_CODE_GROUP_DETAIL);
        }
    }

    @Override
    public void onAvatarClick(String username) {
        //handling when user click avatar此处应跳转到用户详情界面，或者三观配界面

//        Intent intent = new Intent(getActivity(), UserProfileActivity.class);
//        intent.putExtra("username", username);

        Intent intent = new Intent(getActivity(), UserInfoEditActivity.class);
        intent.putExtra("chatUserId", username);
        startActivity(intent);
        getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onAvatarLongClick(String username) {
        inputAtUsername(username);
    }


    @Override
    public boolean onMessageBubbleClick(EMMessage message) {
        //open red packet if the message is red packet
        if (message.getBooleanAttribute(RedPacketConstant.MESSAGE_ATTR_IS_RED_PACKET_MESSAGE, false)) {
            RedPacketUtil.openRedPacket(getActivity(), chatType, message, toChatUsername, messageList);
            return true;
        }
        return false;
    }

    @Override
    public void onCmdMessageReceived(List<EMMessage> messages) {
        for (EMMessage message : messages) {
            EMCmdMessageBody cmdMsgBody = (EMCmdMessageBody) message.getBody();
            String action = cmdMsgBody.action();//get user defined action
            if (action.equals(RedPacketConstant.REFRESH_GROUP_RED_PACKET_ACTION) && message.getChatType() == EMMessage.ChatType.GroupChat) {
                RedPacketUtil.receiveRedPacketAckMessage(message);
                messageList.refresh();
            }
        }
        super.onCmdMessageReceived(messages);
    }

    @Override
    public void onMessageBubbleLongClick(EMMessage message) {
        // no message forward when in chat room
        startActivityForResult((new Intent(getActivity(), ContextMenuActivity.class)).putExtra("message", message)
                        .putExtra("ischatroom", chatType == EaseConstant.CHATTYPE_CHATROOM),
                REQUEST_CODE_CONTEXT_MENU);
        getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public boolean onExtendMenuItemClick(int itemId, View view) {
        switch (itemId) {
            case ITEM_VIDEO:
                Intent intent = new Intent(getActivity(), ImageGridActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SELECT_VIDEO);
                getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case ITEM_FILE: //file
                selectFileFromLocal();
                break;
            case ITEM_VOICE_CALL:
                startVoiceCall();
                break;
            case ITEM_VIDEO_CALL:
                startVideoCall();
                break;
            case ITEM_RED_PACKET:
                RedPacketUtil.startRedPacketActivityForResult(this, chatType, toChatUsername, REQUEST_CODE_SEND_MONEY);
                break;
            default:
                break;
        }
        //keep exist extend menu
        return false;
    }

    /**
     * select file
     */
    protected void selectFileFromLocal() {
        Intent intent = null;
        if (Build.VERSION.SDK_INT < 19) { //api 19 and later, we can't use this way, demo just select from images
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);

        } else {
            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        startActivityForResult(intent, REQUEST_CODE_SELECT_FILE);
    }

    /**
     * make a voice call
     */
    protected void startVoiceCall() {
        if (!EMClient.getInstance().isConnected()) {
            Toast.makeText(getActivity(), R.string.not_connect_to_server,  Toast.LENGTH_SHORT).show();
        } else {
            startActivity(new Intent(getActivity(), VoiceCallActivity.class).putExtra("username", toChatUsername)
                    .putExtra("isComingCall", false));
            // voiceCallBtn.setEnabled(false);
            inputMenu.hideExtendMenuContainer();
            getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }

    /**
     * make a video call
     */
    protected void startVideoCall() {
        if (!EMClient.getInstance().isConnected())
            Toast.makeText(getActivity(), R.string.not_connect_to_server,  Toast.LENGTH_SHORT).show();
        else {
            startActivity(new Intent(getActivity(), VideoCallActivity.class).putExtra("username", toChatUsername)
                    .putExtra("isComingCall", false));
            // videoCallBtn.setEnabled(false);
            inputMenu.hideExtendMenuContainer();
            getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }

    //    UserInfoDetail mUserDetail = new UserInfoDetail();
    SanGuan mSanGuan = new SanGuan();
    //    public void setLoadInfo(String totalresult) throws JSONException {
//        Gson gson = new GsonBuilder().create();
//        if (totalresult != null) {
//            java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<UserInfoDetail>() {
//            }.getType();
//            mUserDetail = gson.fromJson(totalresult, type);
//         }
//    }
    mHandlerWeak mtotalHandler;
    public static final int DOWNLOADED = 2;
    public static final int DOWNLOADED_INIT = 1;
    private static class mHandlerWeak extends Handler {
        private WeakReference<ChatFragment> activity = null;

        public mHandlerWeak(ChatFragment act) {
            super();
            this.activity = new WeakReference<ChatFragment>(act);
        }

        @Override
        public void handleMessage(Message msg) {
            ChatFragment act = activity.get();
            if (null == act) {
                return;
            }
            switch (msg.what) {
                // 正在下载
                case DOWNLOADED:
                    act.showSanguanPickDialog();
                    act.titleBar.getRight_image_user().setImageURI(Uri.parse(act.mSanGuan.getUserPhotoUrl2()));


                    break;
                case DOWNLOADED_INIT:
                    act.titleBar.getRight_image_user().setImageURI(Uri.parse(act.mSanGuan.getUserPhotoUrl2()));

                    if(act.mSanGuan.getUserGender2()==0){
                        act.iv_sanguan_pick.setImageResource(R.drawable.mate_red3);
                    }else{
                        act.iv_sanguan_pick.setImageResource(R.drawable.mate_blue3);
                    }

                    break;
            }
        }
    }

    @Override
    public void loadServerData(final String response, int flag) {

        Log.i("ChatFragment :::", "::" + response);

        Gson gson = new GsonBuilder().create();
//        ::{"Id":"dcae97ff67114b609980d1df61726051","Name":"maggie","PhoneNumber":"18321730153","EnglishName":null,
//                "Disposition":"INFJ劝告者","DispositionId":"dbf9f079-3b79-4170-969d-77ccba6af0fd",
//                "EasemobId":"16a0ae60-5ecb-11e6-a072-c38b2db750a9","Birthday":null,
//                "Age":0,"Gender":0,"Description":null,"MaritalStatus":0,"Job":null,"Company":null,
//                "School":null,"Home":null,"City":null,"JobAddress":null,"LifeAddress":null,
//                "DailyAddress":null,"VisitedAttractions":null,"IsVip":false,"VipLevel":0,
//                "Star":0,"UserNumber":"83565785","Foucs":false,"Tag":null,"CreateTime":"2016-08-10 15:21:43","Status":0,"Photos":[{"Url":"http://xzxj.oss-cn-shanghai.aliyuncs.com/user/2b92e72d-5eb3-4253-9f47-d2f5a999fe10x480.jpg","FullUrl":"http://xzxj.oss-cn-shanghai.aliyuncs.com/user/2b92e72d-5eb3-4253-9f47-d2f5a999fe10.jpg","Id":"450e7541-398a-4ddb-b3fd-86163378c89e","IsIcon":true}],"Icon":{"Url":"http://xzxj.oss-cn-shanghai.aliyuncs.com/user/2b92e72d-5eb3-4253-9f47-d2f5a999fe10x480.jpg","FullUrl":"http://xzxj.oss-cn-shanghai.aliyuncs.com/user/2b92e72d-5eb3-4253-9f47-d2f5a999fe10.jpg","Id":"450e7541-398a-4ddb-b3fd-86163378c89e","IsIcon":true},"Distance":null,"Before":null,"LastActivity":null}

        switch (flag) {
//            case LOAD_DATA:
//                try {
//                    if (response != null) {
//                        setLoadInfo(response);
//////                        可以将聊天者的信息缓存，以后扩展吧
////                        new Thread(new Runnable() {
////                            @Override
////                            public void run() {
//////                                DemoDBManager.getInstance().saveCacheJson(xxxx, response);
////                            }
////                        }).start();
//                    }
//                } catch (Exception e) {
//                      e.printStackTrace();
//                }
//                break;

            case SANGUAN_PICK:
//            {"Successfully":1,"Title":"三观貌合神离","Description":"双方可以谈论很多共同的话题，相互交流能够激发对方隐藏的盲点和攻击点，彼此印象深刻惺惺相惜相见恨晚。但是需要意识到各自的目的和行动是相反的，一个南辕一个北辙，不能邯郸学步，否则便开始误解，渐渐感到不能信任彼此。","UserName1":"wmy","UserPhotoUrl1":"http://xzxj.oss-cn-shanghai.aliyuncs.com/user/ae95c3fd-8577-4f39-8d16-0a10e0ad51c7x480.jpg","UserAge1":1,"UserGender1":0,"UserName2":"maggie","UserPhotoUrl2":"http://xzxj.oss-cn-shanghai.aliyuncs.com/user/2b92e72d-5eb3-4253-9f47-d2f5a999fe10x480.jpg","UserAge2":0,"UserGender2":0}


                if (response != null) {
                    java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<SanGuan>() {
                    }.getType();
                    mSanGuan = gson.fromJson(response, type);
                }
                Log.i("ChatFragment:w:", response + ":::::=" + toChatUsername);
                Message msg = new Message();
                msg.what = DOWNLOADED_INIT;
                mtotalHandler.sendMessage(msg);

                break;
            case SANGUAN_PICK2:
//            {"Successfully":1,"Title":"三观貌合神离","Description":"双方可以谈论很多共同的话题，相互交流能够激发对方隐藏的盲点和攻击点，彼此印象深刻惺惺相惜相见恨晚。但是需要意识到各自的目的和行动是相反的，一个南辕一个北辙，不能邯郸学步，否则便开始误解，渐渐感到不能信任彼此。","UserName1":"wmy","UserPhotoUrl1":"http://xzxj.oss-cn-shanghai.aliyuncs.com/user/ae95c3fd-8577-4f39-8d16-0a10e0ad51c7x480.jpg","UserAge1":1,"UserGender1":0,"UserName2":"maggie","UserPhotoUrl2":"http://xzxj.oss-cn-shanghai.aliyuncs.com/user/2b92e72d-5eb3-4253-9f47-d2f5a999fe10x480.jpg","UserAge2":0,"UserGender2":0}
                if (pd != null) {
                    pd.dismiss();
                }
                if (response != null) {
                    java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<SanGuan>() {
                    }.getType();
                    mSanGuan = gson.fromJson(response, type);
                }
                Log.i("ChatFragment:w:", response + ":::::=" + toChatUsername);

                Message msg2 = new Message();
                msg2.what = DOWNLOADED;
                mtotalHandler.sendMessage(msg2);
                break;
        }
    }

    @Override
    public void loadDataFailed(String response, int flag) {
        MyToast.show(getActivity(), response + ".");
        if (pd != null) {
            pd.dismiss();
        }
    }


    /**
     * chat row provider
     */
    private final class CustomChatRowProvider implements EaseCustomChatRowProvider {
        @Override
        public int getCustomChatRowTypeCount() {
            //here the number is the message type in EMMessage::Type
            //which is used to count the number of different chat row
            return 8;
        }

        @Override
        public int getCustomChatRowType(EMMessage message) {
            if (message.getType() == EMMessage.Type.TXT) {
                //voice call
                if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false)) {
                    return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VOICE_CALL : MESSAGE_TYPE_SENT_VOICE_CALL;
                } else if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VIDEO_CALL, false)) {
                    //video call
                    return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VIDEO_CALL : MESSAGE_TYPE_SENT_VIDEO_CALL;
                } else if (message.getBooleanAttribute(RedPacketConstant.MESSAGE_ATTR_IS_RED_PACKET_MESSAGE, false)) {
                    //sent redpacket message
                    return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_MONEY : MESSAGE_TYPE_SEND_MONEY;
                } else if (message.getBooleanAttribute(RedPacketConstant.MESSAGE_ATTR_IS_RED_PACKET_ACK_MESSAGE, false)) {
                    //received redpacket message
                    return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_LUCKY : MESSAGE_TYPE_SEND_LUCKY;
                }
            }
            return 0;
        }

        @Override
        public EaseChatRow getCustomChatRow(EMMessage message, int position, BaseAdapter adapter) {
            if (message.getType() == EMMessage.Type.TXT) {
                // voice call or video call
                if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false) ||
                        message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VIDEO_CALL, false)) {
                    return new ChatRowVoiceCall(getActivity(), message, position, adapter);
                } else if (message.getBooleanAttribute(RedPacketConstant.MESSAGE_ATTR_IS_RED_PACKET_MESSAGE, false)) {//send redpacket
                    return new ChatRowRedPacket(getActivity(), message, position, adapter);
                } else if (message.getBooleanAttribute(RedPacketConstant.MESSAGE_ATTR_IS_RED_PACKET_ACK_MESSAGE, false)) {//open redpacket message
                    return new ChatRowRedPacketAck(getActivity(), message, position, adapter);
                }
            }
            return null;
        }

    }

}

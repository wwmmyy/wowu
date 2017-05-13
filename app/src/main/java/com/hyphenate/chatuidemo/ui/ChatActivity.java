package com.hyphenate.chatuidemo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.hyphenate.chatuidemo.runtimepermissions.PermissionsManager;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.util.EasyUtils;
import com.wuwo.im.activity.MainActivity;

import im.imxianzhi.com.imxianzhi.R;

/**
 * chat activity，EaseChatFragment was used {@link   }
 *
 */
public class ChatActivity extends BaseActivity{
    public static ChatActivity activityInstance;
    private EaseChatFragment chatFragment;
    public String toChatUsername,iconPath;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.em_activity_chat);
        activityInstance = this;
        //get user id or group id   将要聊天者的userid
        toChatUsername = getIntent().getExtras().getString(EaseConstant.EXTRA_USER_ID);
        iconPath= getIntent().getExtras().getString(EaseConstant.EXTRA_USER_ICONPATH);
        //use EaseChatFratFragment
        chatFragment = new ChatFragment();
        //pass parameters to chat fragment
        chatFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().add(R.id.container, chatFragment).commit();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityInstance = null;
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
    	// make sure only one chat activity is opened
        String username = intent.getStringExtra("userId");
        if (toChatUsername.equals(username))
            super.onNewIntent(intent);
        else {
            startActivity(intent);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        }

    }
    
    @Override
    public void onBackPressed() {
        chatFragment.onBackPressed();
        try {
            if (EasyUtils.isSingleActivity(this)) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public String getToChatUsername(){
        return toChatUsername;
    }

    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults) {
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
    }
}

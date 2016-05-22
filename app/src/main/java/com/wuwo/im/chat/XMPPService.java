package com.wuwo.im.chat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.wuwo.im.activity.LoginActivity;

import im.wuwo.com.wuwo.R;

/**
 * 
* @类名: XMPPService 
* @描述: 消息推送后台服务
* @作者: 王明远 
* @日期: 2015-4-22 上午9:47:33 
* @修改人: 
 * @修改时间: 
 * @修改内容:
 * @版本: V1.0
 * @版权:Copyright ©  All rights reserved.
 */
public class XMPPService extends Service {

	private Context context = null;
	private static final String TAG = "XMPPService";
	private static final boolean RECIEVE_FLAG = true;
    private NotificationManager manager;
	private int i=0;
	Thread xmppThread = null;
	
	@Override
	public void onCreate() {
		Log.i(TAG, "service is created");
		super.onCreate();
		this.context = getApplicationContext();
		manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		xmppThread = new Thread(new XMPPThread(mHandler,context));
		xmppThread.start();
	}

	@Override
	public int onStartCommand(Intent intent, int flag,int startId) {
		Log.i(TAG, "service is started. thread id is "+Thread.currentThread().getName());
		return super.onStartCommand(intent,flag, startId);
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "service is destroy");
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	
	private Handler mHandler = new Handler(){  
	    @Override  
	    public void handleMessage(Message msg) {  
	        switch(msg.what){  
	        case 0:  
	        {  
	            if(RECIEVE_FLAG) {
					  final org.jivesoftware.smack.packet.Message mes = (org.jivesoftware.smack.packet.Message)msg.obj;
					  System.out.println("来自："+mes.getFrom()+"  消息内容：" + mes.getBody());
				
					  //构建一个通知对象，指定了图标，标题，和时间
					  Notification notification = new Notification(R.drawable.notification, "通知", System.currentTimeMillis());
					  //TODO 处理消息
					  //界面跳转
//					  Intent intent = new Intent(context,NotificationActivity.class);
					  
					  Intent intent = new Intent(context,LoginActivity.class);
					  
					  
					  //消息重复接收关键
					  intent.setData(Uri.parse("custom://"+System.currentTimeMillis()));
					  
					  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  
					  intent.putExtra("Body", mes.getBody());			         
					  intent.putExtra("From", mes.getFrom()); 
						  
					  //触发界面跳转
					  PendingIntent    mPendingIntent = PendingIntent.getActivity(context, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
					  //消息栏
					  notification.setLatestEventInfo(context, "您有新的消息", mes.getBody(),mPendingIntent);
					  notification.flags = Notification.FLAG_AUTO_CANCEL;//点击后自动消失
					  notification.defaults = Notification.DEFAULT_SOUND;//声音默认
					  //消息叠加
					  manager.notify(i, notification);
					  i++;
				  }
	            break;  
	        }  
	        default:  
	            break;  
	        }  
	    }  
	};  
}

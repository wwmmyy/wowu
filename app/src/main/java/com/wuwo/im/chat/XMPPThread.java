package com.wuwo.im.chat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.wuwo.im.config.WowuApp;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smackx.OfflineMessageManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class XMPPThread implements Runnable {
	
	private Handler handler = null;
	private Connection conn = null;
	private static final String TAG = "XMPPThread";
	private static final boolean RECIEVE_FLAG = true;
	Context mcontext;
	 SharedPreferences settings;
	  String loginName,username;
	public XMPPThread(Handler handler,Context context){
		this.handler = handler;
		this.mcontext=context;
		 settings = context.getSharedPreferences(WowuApp.PREFERENCE_KEY, Context.MODE_PRIVATE);
		
	         loginName= settings.getString("loginName", "");
	         username= settings.getString("username", "");
//	         username= DistApp.ALL_deviceNumber;
	}

	@Override
        public void run() {
    
            try {
                this.conn = XmppTool.getConnection();
                if (this.conn != null&&conn.isConnected()) {
                    ////		         实现新增用户功能 方法1， 容易出现重复注册报错
                    //	              AccountManager amgr = conn.getAccountManager();
                    //	              amgr.createAccount("薇", "111111");
                    //	              Log.i("获取用户列表信息的尝试", amgr.getAccountAttributes().toString());

                    if(!TextUtils.isEmpty(loginName)){
                        regiest(username, loginName);//  实现新增用户功能 方法2
                    }
                  
                    login(conn);//  登录	
                    Log.i("获取到的登录信息为：：：：：：：：", conn.getUser());
                    getOffLineMessage();// 为获取离线消息进行的设置 			
                    //接着，上线
                    Presence presence = new Presence(Presence.Type.available);
                    conn.sendPacket(presence);
                    handleMessage();
                }
            } catch (XMPPException e) {
                e.printStackTrace();
                Log.e(TAG, e.getMessage(), e);
            }
    
        }
	
	/**
	 * 登陆
	 * @param conn
	 * @return
	 * @throws XMPPException
	 */
	private boolean login(Connection conn) throws XMPPException {
//		conn.login("admin", "admin");
		conn.login(username, loginName);
		
		return true;
	}
	
	public void handleMessage()
	{
		//消息接受包
		PacketFilter filter=new PacketTypeFilter(org.jivesoftware.smack.packet.Message.class);
		//消息接受连接
		PacketListener myListener=new PacketListener(){
			@Override
            @SuppressWarnings("deprecation")
			public void processPacket(final Packet packet) {
				  if(RECIEVE_FLAG) {
					  System.out.println("Activity----processPacket" + packet.toXML());
					  final org.jivesoftware.smack.packet.Message mes = (org.jivesoftware.smack.packet.Message)packet;
					  System.out.println("来自："+mes.getFrom()+"  消息内容：" + mes.getBody());
					  android.os.Message msg = handler.obtainMessage(0, packet);
					  handler.sendMessage(msg);
				  }
			}
		};
		conn.addPacketListener(myListener, filter);
	}




	/**
	 * 获取离线消息
	* @Title: getOffLineMessage
	* @Description: TODO
	* @param
	* @return void
	* @throws
	 */
	public void getOffLineMessage(){

	    OfflineMessageManager offlineManager = new OfflineMessageManager(
	            conn);
	        try {
	            Iterator<Message> it = offlineManager
	                    .getMessages();

//	            System.out.println(offlineManager.supportsFlexibleRetrieval());
//	            System.out.println("离线消息数量: " + offlineManager.getMessageCount());


	            Map<String,ArrayList<Message>> offlineMsgs = new HashMap<String,ArrayList<Message>>();

	            while (it.hasNext()) {
	                org.jivesoftware.smack.packet.Message message = it.next();
//	                System.out .println("收到离线消息, Received from 【" + message.getFrom()  
//	                                + "】 message: " + message.getBody());  
	                String fromUser = message.getFrom().split("/")[0];  
	                
	                
	                android.os.Message msg = handler.obtainMessage(0, message);
	                handler.sendMessage(msg);
	                
	                
	                if(offlineMsgs.containsKey(fromUser))  
	                {  
	                    offlineMsgs.get(fromUser).add(message);  
	                }else{  
	                    ArrayList<Message> temp = new ArrayList<Message>();
	                    temp.add(message);  
	                    offlineMsgs.put(fromUser, temp);  
	                }  
	            }  
	  
//	            //在这里进行处理离线消息集合......  
//	            Set<String> keys = offlineMsgs.keySet();  
//	            Iterator<String> offIt = keys.iterator();  
//	            while(offIt.hasNext())  
//	            {  
//	                String key = offIt.next();  
//	                ArrayList<Message> ms = offlineMsgs.get(key);  
//	                TelFrame tel = new TelFrame(key);  
//	                ChatFrameThread cft = new ChatFrameThread(key, null);  
//	                cft.setTel(tel);  
//	                cft.start();  
//	                for (int i = 0; i < ms.size(); i++) {  
//	                    tel.messageReceiveHandler(ms.get(i));  
//	                }  
//	            }  
	              
//	            记得最后要把离线消息删除，即通知服务器删除离线消息
	            offlineManager.deleteMessages();  
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        }  
	    
	    
	}
	
	
	
	
	
	
	
	
	
	
	
	/**
         * 注册
         * 
         * @param account 注册帐号
         * @param password 注册密码
         * @return 1、注册成功 0、服务器没有返回结果2、这个账号已经存在3、注册失败
         */
        public String regiest(String account, String password) {            
                if (conn == null)
                        return "0";
                Registration reg = new Registration();
                reg.setType(IQ.Type.SET);
                reg.setTo(conn.getServiceName());
                reg.setUsername(account);// 注意这里createAccount注册时，参数是username，不是jid，是“@”前面的部分。
                reg.setPassword(password);
                reg.addAttribute("android", "geolo_createUser_android");// 这边addAttribute不能为空，否则出错。所以做个标志是android手机创建的吧！！！！！
                PacketFilter filter = new AndFilter(new PacketIDFilter(
                                reg.getPacketID()), new PacketTypeFilter(IQ.class));
                PacketCollector collector = conn
                                .createPacketCollector(filter);
                conn.sendPacket(reg);
                IQ result = (IQ) collector.nextResult(SmackConfiguration
                                .getPacketReplyTimeout());
                // Stop queuing results
                collector.cancel();// 停止请求results（是否成功的结果）
                if (result == null) {
                        Log.e("RegistActivity", "注册没有应答");
                        return "0";
                } else if (result.getType() == IQ.Type.RESULT) {
                    Log.e("RegistActivity", "注册成功了");
                        return "1";
                } else { // if (result.getType() == IQ.Type.ERROR)
                        if (result.getError().toString().equalsIgnoreCase("conflict(409)")) {
                                Log.e("RegistActivity", "IQ.Type.ERROR:注册错误 "
                                                + result.getError().toString());
                                return "2";
                        } else {
                                Log.e("RegistActivity", "IQ.Type.ERROR:注册错误 "
                                                + result.getError().toString());
                                return "3";
                        }
                }
        }

	
}

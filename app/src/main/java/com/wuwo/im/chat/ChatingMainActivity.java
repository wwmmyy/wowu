//package com.wuwo.im.chat;
//
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.view.Window;
//import android.widget.AbsListView;
//import android.widget.BaseAdapter;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.ListView;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.wuwo.im.util.TimeRender;
//
//import org.jivesoftware.smack.Chat;
//import org.jivesoftware.smack.ChatManager;
//import org.jivesoftware.smack.ChatManagerListener;
//import org.jivesoftware.smack.MessageListener;
//import org.jivesoftware.smack.PacketListener;
//import org.jivesoftware.smack.XMPPException;
//import org.jivesoftware.smack.filter.PacketFilter;
//import org.jivesoftware.smack.filter.PacketTypeFilter;
//import org.jivesoftware.smack.packet.Message;
//import org.jivesoftware.smack.packet.Packet;
//import org.jivesoftware.smackx.OfflineMessageManager;
//import org.jivesoftware.smackx.filetransfer.FileTransfer;
//import org.jivesoftware.smackx.filetransfer.FileTransfer.Status;
//import org.jivesoftware.smackx.filetransfer.FileTransferListener;
//import org.jivesoftware.smackx.filetransfer.FileTransferManager;
//import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
//import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;
//import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import java.util.Timer;
//import java.util.TimerTask;
//
//import im.imxianzhi.com.imxianzhi.R;
///**
//*desc 正在聊天的界面
//*@author 王明远
//*@日期： 2016/5/10 23:24
//*@版权:Copyright 上海数慧系统有限公司  All rights reserved.
//*/
//
//public class ChatingMainActivity extends Activity {
//
//    private MyAdapter adapter;
//    private List<Msg> listMsg = new ArrayList<Msg>();
//
//    private EditText msgText;
//    private ProgressBar pb;
//
//    private String servername = "@chat.com";//表示服务器名称
//    //	private  String  clientname1="mm";//表示将要聊天 的用户名称
//    //        private  String  clientname1="wwmmyy";//表示将要聊天 的用户名称
//    private String pUSERID = "wwmmyy";
//    private String clientname1 = "aa";//表示将要聊天 的用户名称
//
//    Chat newchat;
//
//    public class Msg {
//        String userid;
//        String msg;
//        String date;
//        String from;
//
//        public Msg(String userid, String msg, String date, String from) {
//            this.userid = userid;
//            this.msg = msg;
//            this.date = date;
//            this.from = from;
//        }
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentView(R.layout.activity_chat_main);
//        //获取Intent传过来的用户名  登录者的用户名
//        //		this.pUSERID = getIntent().getStringExtra("USERID");
//
//        ListView listview = (ListView) findViewById(R.id.listview);
//        listview.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
//
//        this.adapter = new MyAdapter(this);
//        listview.setAdapter(adapter);
//
//        //获取文本信息
//        this.msgText = (EditText) findViewById(R.id.et_sendmessage);
//        this.pb = (ProgressBar) findViewById(R.id.formclient_pb);
//
//        Thread t = new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//                //消息监听
//                //                                    if(XmppTool.getConnection()!=null){
//
//                //            //                                      为获取离线消息进行的设置
//                //                                                    getOffLineMessage();
//                //            //                                      接着，上线
//                //                                                     Presence presence = new Presence(Presence.Type.available);
//                //                                                     XmppTool.getConnection().sendPacket(presence);
//                //                                                     handleMessage();
//
//                ChatManager cm = XmppTool.getConnection().getChatManager();
//                //发送消息给chat.com服务器的mm（获取自己的服务器，和好友）
//                newchat = cm.createChat(clientname1 + servername, null);
//
//                cm.addChatListener(new ChatManagerListener() {
//                    @Override
//                    public void chatCreated(Chat chat, boolean able) {
//                        chat.addMessageListener(new MessageListener() {
//                            @Override
//                            public void processMessage(Chat chat2, Message message) {
//                                //收到来自chat.com服务器小王的消息（获取自己的服务器，和好友）
//                                if (message.getFrom().contains(clientname1 + servername)) {
//                                    //获取用户、消息、时间、IN
//                                    String[] args = new String[] { clientname1, message.getBody(),
//                                            TimeRender.getDate(), "IN" };
//
//                                    //在handler里取出来显示消息
//                                    android.os.Message msg = handler.obtainMessage();
//                                    msg.what = 1;
//                                    msg.obj = args;
//                                    msg.sendToTarget();
//                                } else {
//                                    //message.getFrom().cantatins(获取列表上的用户，组，管理消息);
//                                }
//
//                            }
//                        });
//                    }
//                });
//
//                //                                    }
//
//            }
//        });
//        t.start();
//
//        //		//附件
//        //		Button btattach = (Button) findViewById(R.id.formclient_btattach);
//        //		btattach.setOnClickListener(new OnClickListener() {
//        //			@Override
//        //			public void onClick(View arg0)
//        //			{
//        //				Intent intent = new Intent(FormClient.this, FormFiles.class);
//        //				startActivityForResult(intent, 2);
//        //			}
//        //		});
//        //发送消息
//        Button btsend = (Button) findViewById(R.id.btn_send);
//        btsend.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //获取text文本
//                String msg = msgText.getText().toString();
//
//                if (msg.length() > 0) {
//                    //发送消息
//                    listMsg.add(new Msg(pUSERID, msg, TimeRender.getDate(), "OUT"));
//                    //刷新适配器
//                    adapter.notifyDataSetChanged();
//
//                    try {
//                        //发送消息给mm
//                        if (newchat != null) {
//                            newchat.sendMessage(msg);
//                        } else {
//                            Toast.makeText(ChatingMainActivity.this, "暂时无法连接服务器", Toast.LENGTH_SHORT)
//                                    .show();
//                        }
//
//                    } catch (XMPPException e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    Toast.makeText(ChatingMainActivity.this, "请输入信息", Toast.LENGTH_SHORT).show();
//                }
//                //清空text
//                msgText.setText("");
//            }
//        });
//
//        //		//接受文件
//        //		FileTransferManager fileTransferManager = new FileTransferManager(XmppTool.getConnection());
//        //		fileTransferManager.addFileTransferListener(new RecFileTransferListener());
//
//
//        initTopView();
//
//    }
//
//
//
//
//
//    /**
//    * @Title: initTopView
//    * @Description: 初始化顶部按钮功能
//    * @param
//    * @return void
//    * @throws
//    */
//    public void initTopView() {
//
//        TextView   back_news=(TextView) findViewById(R.id.return_back_sixindetail);
//        ImageView   back_news1=(ImageView) findViewById(R.id.return_back0_sixindetail);
//        back_news.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//                // TODO 自动生成的方法存根
//                ChatingMainActivity.this.finish();
//            }
//        });
//        back_news1.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//                // TODO 自动生成的方法存根
//                ChatingMainActivity.this.finish();
//            }
//        });
//    }
//
//
//
//
//
//
//
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        //发送附件
//        if (requestCode == 2 && resultCode == 2 && data != null) {
//
//            String filepath = data.getStringExtra("filepath");
//            if (filepath.length() > 0) {
//                sendFile(filepath);
//            }
//        }
//    }
//
//    private void sendFile(String filepath) {
//        // ServiceDiscoveryManager sdm = new ServiceDiscoveryManager(connection);
//
//        final FileTransferManager fileTransferManager = new FileTransferManager(
//                XmppTool.getConnection());
//        //发送给chat.com服务器，mm（获取自己的服务器，和好友）
//        final OutgoingFileTransfer fileTransfer = fileTransferManager
//                .createOutgoingFileTransfer(clientname1 + servername + "/Spark 2.6.3");
//
//        final File file = new File(filepath);
//
//        try {
//            fileTransfer.sendFile(file, "Sending");
//        } catch (Exception e) {
//            Toast.makeText(ChatingMainActivity.this, "发送失败!", Toast.LENGTH_SHORT).show();
//            e.printStackTrace();
//        }
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    while (true) {
//                        Thread.sleep(500L);
//
//                        Status status = fileTransfer.getStatus();
//                        if ((status == FileTransfer.Status.error)
//                                || (status == FileTransfer.Status.complete)
//                                || (status == FileTransfer.Status.cancelled)
//                                || (status == FileTransfer.Status.refused)) {
//                            handler.sendEmptyMessage(4);
//                            break;
//                        } else if (status == FileTransfer.Status.negotiating_transfer) {
//                            //..
//                        } else if (status == FileTransfer.Status.negotiated) {
//                            //..
//                        } else if (status == FileTransfer.Status.initial) {
//                            //..
//                        } else if (status == FileTransfer.Status.negotiating_stream) {
//                            //..
//                        } else if (status == FileTransfer.Status.in_progress) {
//                            //进度条显示
//                            handler.sendEmptyMessage(2);
//
//                            long p = fileTransfer.getBytesSent() * 100L
//                                    / fileTransfer.getFileSize();
//
//                            android.os.Message message = handler.obtainMessage();
//                            message.arg1 = Math.round(p);
//                            message.what = 3;
//                            message.sendToTarget();
//                            Toast.makeText(ChatingMainActivity.this, "发送成功!", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                } catch (Exception e) {
//                    Toast.makeText(ChatingMainActivity.this, "发送失败!", Toast.LENGTH_SHORT).show();
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//    }
//
//    private FileTransferRequest request;
//    private File file;
//
//    class RecFileTransferListener implements FileTransferListener {
//        @Override
//        public void fileTransferRequest(FileTransferRequest prequest) {
//            //接受附件
//            //			System.out.println("The file received from: " + prequest.getRequestor());
//
//            file = new File("mnt/sdcard/" + prequest.getFileName());
//            request = prequest;
//            handler.sendEmptyMessage(5);
//        }
//    }
//
//    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(android.os.Message msg) {
//
//            switch (msg.what) {
//
//            case 0:
//                if (RECIEVE_FLAG) {
//                    final org.jivesoftware.smack.packet.Message mes = (org.jivesoftware.smack.packet.Message) msg.obj;
//                    System.out.println("来自：" + mes.getFrom() + "  消息内容：" + mes.getBody());
//                    //获取消息并显示
//                    //	                                                  String[] args = (String[]) msg.obj;
//                    listMsg.add(new Msg(mes.getFrom(), mes.getBody(), mes.getFrom(), mes.getBody()));
//                    //刷新适配器
//                    adapter.notifyDataSetChanged();
//
//                }
//                break;
//
//            case 1:
//                //获取消息并显示
//                String[] args = (String[]) msg.obj;
//                listMsg.add(new Msg(args[0], args[1], args[2], args[3]));
//                //刷新适配器
//                adapter.notifyDataSetChanged();
//                break;
//            case 2:
//                //附件进度条
//                if (pb.getVisibility() == View.GONE) {
//                    pb.setMax(100);
//                    pb.setProgress(1);
//                    pb.setVisibility(View.VISIBLE);
//                }
//                break;
//            case 3:
//                pb.setProgress(msg.arg1);
//                break;
//            case 4:
//                pb.setVisibility(View.GONE);
//                break;
//            case 5:
//                final IncomingFileTransfer infiletransfer = request.accept();
//
//                //提示框
//                AlertDialog.Builder builder = new AlertDialog.Builder(ChatingMainActivity.this);
//
//                builder.setTitle("附件：").setCancelable(false)
//                        .setMessage("是否接收文件：" + file.getName() + "?")
//                        .setPositiveButton("接受", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int id) {
//                                try {
//                                    infiletransfer.recieveFile(file);
//                                } catch (XMPPException e) {
//                                    Toast.makeText(ChatingMainActivity.this, "接收失败!", Toast.LENGTH_SHORT)
//                                            .show();
//                                    e.printStackTrace();
//                                }
//
//                                handler.sendEmptyMessage(2);
//
//                                Timer timer = new Timer();
//                                TimerTask updateProgessBar = new TimerTask() {
//                                    @Override
//                                    public void run() {
//                                        if ((infiletransfer.getAmountWritten() >= request
//                                                .getFileSize())
//                                                || (infiletransfer.getStatus() == FileTransfer.Status.error)
//                                                || (infiletransfer.getStatus() == FileTransfer.Status.refused)
//                                                || (infiletransfer.getStatus() == FileTransfer.Status.cancelled)
//                                                || (infiletransfer.getStatus() == FileTransfer.Status.complete)) {
//                                            cancel();
//                                            handler.sendEmptyMessage(4);
//                                        } else {
//                                            long p = infiletransfer.getAmountWritten() * 100L
//                                                    / infiletransfer.getFileSize();
//
//                                            android.os.Message message = handler.obtainMessage();
//                                            message.arg1 = Math.round(p);
//                                            message.what = 3;
//                                            message.sendToTarget();
//                                            Toast.makeText(ChatingMainActivity.this, "接收完成!",
//                                                    Toast.LENGTH_SHORT).show();
//                                        }
//                                    }
//                                };
//                                timer.scheduleAtFixedRate(updateProgessBar, 10L, 10L);
//                                dialog.dismiss();
//                            }
//                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int id) {
//                                request.reject();
//                                dialog.cancel();
//                            }
//                        }).show();
//                break;
//            default:
//                break;
//            }
//        };
//    };
//
//    //退出
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        XmppTool.closeConnection();
//        System.exit(0);
//        android.os.Process.killProcess(android.os.Process.myPid());
//    }
//
//    class MyAdapter extends BaseAdapter {
//
//        private Context cxt;
//        private LayoutInflater inflater;
//
//        public MyAdapter(ChatingMainActivity chatActivity) {
//            this.cxt = chatActivity;
//        }
//
//        @Override
//        public int getCount() {
//            return listMsg.size();
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return listMsg.get(position);
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            //显示消息的布局：内容、背景、用户、时间
//            this.inflater = (LayoutInflater) this.cxt
//                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//            //IN,OUT的图片
//            if (listMsg.get(position).from.equals("IN")) {
//                convertView = this.inflater.inflate(R.layout.chatting_item_msg_text_left, null);
//            } else {
//                convertView = this.inflater.inflate(R.layout.chatting_item_msg_text_right, null);
//            }
//
//            TextView useridView = (TextView) convertView.findViewById(R.id.tv_username);
//            TextView dateView = (TextView) convertView.findViewById(R.id.tv_sendtime);
//            TextView msgView = (TextView) convertView.findViewById(R.id.tv_chatcontent);
//
//            useridView.setText(listMsg.get(position).userid);
//            dateView.setText(listMsg.get(position).date);
//            msgView.setText(listMsg.get(position).msg);
//
//            return convertView;
//        }
//    }
//
//    private static final boolean RECIEVE_FLAG = true;
//
//    public void handleMessage() {
//        //消息接受包
//        PacketFilter filter = new PacketTypeFilter(org.jivesoftware.smack.packet.Message.class);
//        //消息接受连接
//        PacketListener myListener = new PacketListener() {
//            @Override
//            @SuppressWarnings("deprecation")
//            public void processPacket(final Packet packet) {
//                if (RECIEVE_FLAG) {
//                    System.out.println("Activity----processPacket" + packet.toXML());
//                    final org.jivesoftware.smack.packet.Message mes = (org.jivesoftware.smack.packet.Message) packet;
//                    System.out.println("来自：" + mes.getFrom() + "  消息内容：" + mes.getBody());
//                    android.os.Message msg = handler.obtainMessage(0, packet);
//                    handler.sendMessage(msg);
//                }
//            }
//        };
//        XmppTool.getConnection().addPacketListener(myListener, filter);
//    }
//
//    /**
//     * 获取离线消息
//     *
//     * @Title: getOffLineMessage
//     * @Description: TODO
//     * @param
//     * @return void
//     * @throws
//     */
//    public void getOffLineMessage() {
//
//        OfflineMessageManager offlineManager = new OfflineMessageManager(XmppTool.getConnection());
//        try {
//            Iterator<Message> it = offlineManager.getMessages();
//
//            //	                  System.out.println(offlineManager.supportsFlexibleRetrieval());
//            //	                  System.out.println("离线消息数量: " + offlineManager.getMessageCount());
//
//            Map<String, ArrayList<Message>> offlineMsgs = new HashMap<String, ArrayList<Message>>();
//
//            while (it.hasNext()) {
//                org.jivesoftware.smack.packet.Message message = it.next();
//                System.out.println("收到离线消息, Received from 【" + message.getFrom() + "】 message: "
//                        + message.getBody());
//                String fromUser = message.getFrom().split("/")[0];
//
//                android.os.Message msg = handler.obtainMessage(0, message);
//                handler.sendMessage(msg);
//
//                if (offlineMsgs.containsKey(fromUser)) {
//                    offlineMsgs.get(fromUser).add(message);
//                } else {
//                    ArrayList<Message> temp = new ArrayList<Message>();
//                    temp.add(message);
//                    offlineMsgs.put(fromUser, temp);
//                }
//            }
//
//            //	                  //在这里进行处理离线消息集合......
//            //	                  Set<String> keys = offlineMsgs.keySet();
//            //	                  Iterator<String> offIt = keys.iterator();
//            //	                  while(offIt.hasNext())
//            //	                  {
//            //	                      String key = offIt.next();
//            //	                      ArrayList<Message> ms = offlineMsgs.get(key);
//            //	                      TelFrame tel = new TelFrame(key);
//            //	                      ChatFrameThread cft = new ChatFrameThread(key, null);
//            //	                      cft.setTel(tel);
//            //	                      cft.start();
//            //	                      for (int i = 0; i < ms.size(); i++) {
//            //	                          tel.messageReceiveHandler(ms.get(i));
//            //	                      }
//            //	                  }
//
//            //	                  记得最后要把离线消息删除，即通知服务器删除离线消息
//            offlineManager.deleteMessages();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//
//}
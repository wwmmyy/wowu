//package com.wuwo.im.chat;
//
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.Window;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.wuwo.im.activity.BaseActivity;
//
//import im.wuwo.com.wuwo.R;
//
//public class NotificationActivity extends BaseActivity {
//    Context mContext = NotificationActivity.this;
//        @Override
//        protected void onCreate(Bundle savedInstanceState) {
//                super.onCreate(savedInstanceState);
//                requestWindowFeature(Window.FEATURE_NO_TITLE);
//                setContentView(R.layout.chatting_im_notification);
//                Log.i("MainActivity", "thread id is "+Thread.currentThread().getName());
///*//                启动服务通知
//                Intent intent = new Intent(this,XMPPService.class);
//                startService(intent);*/
//
//                //获取到上一个页面传过来的Intent
//                Intent intent1=this.getIntent();
//                //获取Intent中的Bundle数据
//                Bundle  bl=intent1.getExtras();
//                if(bl!=null){
//                String Body=bl.getString("Body");
//                TextView   maginfo=(TextView) findViewById(R.id.message_info);
//                if(Body!=null){
//                    maginfo.setText("来自"+bl.getString("From")+"::"+Body);
//                }
//
//                initTopbar();
//        }
//
//
//
//        }
//        /**
//        * @Title: initTopbar
//        * @Description: TODO
//        * @param
//        * @return void
//        * @throws
//        */
//        public void initTopbar() {
//            ImageView user_set= (ImageView) findViewById(R.id.menu_theme);
//            user_set.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View arg0) {
//                    // TODO 自动生成的方法存根
//                    Intent intent2 = new Intent();
//                    intent2.setClass(mContext, ChatListActivity.class);
//                    startActivity(intent2);
//                }
//            });
//        }
//
//
//
//
//
//
//
//
////      @Override
////      public boolean onCreateOptionsMenu(Menu menu) {
////              // Inflate the menu; this adds items to the action bar if it is present.
////              getMenuInflater().inflate(R.menu.main, menu);
////              return true;
////      }
//
//}

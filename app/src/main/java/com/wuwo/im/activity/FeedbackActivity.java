package com.wuwo.im.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wuwo.im.adapter.MailGridViewAdapter;
import com.wuwo.im.bean.Attachment;
import com.wuwo.im.util.MyToast;
import com.wuwo.im.util.UtilsTool;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import im.wuwo.com.wuwo.R;

public class FeedbackActivity extends BaseLoadActivity {
    Context mContext = FeedbackActivity.this;
    String titlename;
    TextView feed_back_send;
    EditText feed_back_content;
    SharedPreferences mSettings;
    //添加附件材料
    private MailGridViewAdapter<Attachment> Attach_adapter = null;
    private GridView Attach_gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO 自动生成的方法存根
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        //            WindowManager.LayoutParams.FLAG_FULLSCREEN);// 去掉信息栏
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_feedback);

        mSettings = getSharedPreferences("com.dist.iportal.password",
                Context.MODE_PRIVATE);

        feed_back_send = (TextView) findViewById(R.id.tx_top_right);
        feed_back_content = (EditText) findViewById(R.id.feed_back_edit);
        feed_back_send.setOnClickListener(this);
        feed_back_content.setOnClickListener(this);

//        feed_back_send.setText("发送");
        ((TextView) findViewById(R.id.top_title)).setText("意见反馈");


        ImageView return_back0_feedback = (ImageView) findViewById(R.id.return_back);
        return_back0_feedback.setOnClickListener(this);


//      附件材料
        Attach_gridView = (GridView) findViewById(R.id.meeting_pre_view);
        Attach_adapter = new MailGridViewAdapter<Attachment>(this);
        Attach_gridView.setAdapter(Attach_adapter);
        Attach_gridView.setOnItemClickListener(new MyOnItemClickListener());

    }

    @Override
    public void onClick(View v) {
        // TODO 自动生成的方法存根
        switch (v.getId()) {
            case R.id.tx_top_right:
                sendToServer();
                break;
            case R.id.return_back:
                FeedbackActivity.this.finish();
                break;
            default:
                break;
        }

    }

    /**
     * @param
     * @return void
     * @throws
     * @Title: sendToServer
     * @Description: 上传到服务器
     */
    public void sendToServer() {
        final String requestURL = OkHttpUtils.serverAbsolutePath + "/mobile/app-feedBack.action";

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //                    if (file != null) {
                Message msg = new Message();
                msg.what = Loading;
                mHandler.sendMessage(msg);
                try {

                    //请求普通信息
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("userId", mSettings.getString("userid", ""));//用户登录以后获取用户的userID并保存
                    map.put("content", feed_back_content.getText().toString());
//                    map.put("appidentify", "com.dist.iportal");                    
                    String totalresult = UtilsTool.getStringFromServer(requestURL, map);
                    Message msg2 = new Message();

                    if (totalresult != null && !totalresult.equals("")) {
                        JSONObject obj = new JSONObject(totalresult);
                        if (obj.optBoolean("state")) {
                            msg2.what = END;
                        } else {
                            msg2.what = WRONG;
                        }
                    } else {
                        msg2.what = WRONG;
                    }

                    mHandler.sendMessage(msg2);
                } catch (Exception e) {
                    // TODO 自动生成的 catch 块
                    e.printStackTrace();
                }

                //                    }
            }
        });
        t.start();
    }

    ProgressDialog pg;
    private final int Loading = 1;
    private final int END = 2;
    private final int WRONG = 3;

    /**
     * 上传到服务器是加载进度框
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Loading:
                    //pg = UtilsTool.initProgressDialog(mContext, "正在上传.....");
                    pg = UtilsTool.initProgressDialog(mContext, "正在上传.....");
                    pg.show();
                    break;
                case END:
                    pg.dismiss();
                    MyToast.show(getApplicationContext(), "意见反馈成功", Toast.LENGTH_LONG);
                    finish();
                    break;

                case WRONG:
                    pg.dismiss();
                    MyToast.show(getApplicationContext(), "意见反馈失败", Toast.LENGTH_LONG);
                    finish();
                    break;
            }

            super.handleMessage(msg);
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1 && null != intent) {
                Uri selectedImage = intent.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                String photoPath = "";
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        //int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        int columnIndex = cursor.getColumnIndexOrThrow(filePathColumn[0]);
                        photoPath = cursor.getString(columnIndex);
                    }

                } else {
                    if (selectedImage != null) {
                        String tmpPath = selectedImage.getPath();
                        if (tmpPath != null && (tmpPath.endsWith(".jpg") || tmpPath.endsWith(".png") || tmpPath.endsWith(".gif"))) {
                            photoPath = tmpPath;
                        }
                    }
                }
                cursor.close();
                Attachment affInfos = Attachment.GetFileInfo(photoPath);
//                Attach_adapter.appendToList(affInfos);
                int a = Attach_adapter.getList().size();
                int count = (int) Math.ceil(a / 2.0);
                Attach_gridView.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        (int) (120 * 1.5 * count)));

            }
        }
    }

    @Override
    public void loadServerData(String response, int flag) {

    }

    @Override
    public void loadDataFailed(String response, int flag) {

    }


    /**
     * 点击事件 删除附件材料
     *
     * @author Administrator
     */
    private class MyOnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1,
                                final int arg2, long arg3) {

////                 MyToast.show(mContext, "附件添加需完善", Toast.LENGTH_LONG);
//            if (arg2 == Attach_adapter.getList().size()) {
//                addAttachment();
//            }else{
//                Attachment infos = (Attachment) Attach_adapter.getItem(arg2);
//                AlertDialog.Builder builder = new AlertDialog.Builder(
//                        AddMeetingActivity.this);
//                builder.setTitle(infos.getFileName());
//                builder.setIcon(getResources().getColor(
//                        android.R.color.transparent));
//                builder.setMessage("是否删除当前附件");
//                builder.setNegativeButton("确定",
//                        new DialogInterface.OnClickListener() {
//
//                            @Override
//                            public void onClick(DialogInterface dialog,
//                                                int which) {
//                                Attach_adapter.clearPositionList(arg2);
////                                                    int a = Attach_adapter.getList().size();
////                                                    int count = (int) Math.ceil(a / 2.0);
////                                                    Attach_gridView.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
////                                                                    LayoutParams.MATCH_PARENT,
////                                                                    (int) (94 * 1.5 * count)));
//                            }
//                        });
//                builder.setPositiveButton("取消",null);
//                builder.create().show();
//            }

        }
    }
}

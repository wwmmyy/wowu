package com.wuwo.im.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.wuwo.im.adapter.PicAdapter;
import com.wuwo.im.bean.UserInfoDetail;
import com.wuwo.im.config.WowuApp;
import com.wuwo.im.util.ImageDecodeUtil;
import com.wuwo.im.util.LogUtils;
import com.wuwo.im.util.MyToast;
import com.wuwo.im.util.UtilsTool;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;

import im.imxianzhi.com.imxianzhi.R;

public class FeedbackActivity extends BaseLoadActivity implements PicAdapter.OnItemClickLitener {
    Context mContext = FeedbackActivity.this;
    String titlename;
    TextView feed_back_send;
    EditText feed_back_content,feed_back_edit3;
    SharedPreferences mSettings;
    //添加附件材料
//    private MailGridViewAdapter<Attachment> Attach_adapter = null;
//    private GridView Attach_gridView;
    private RecyclerView mPicRecyclerView;
    PicAdapter userPicAdapter;
    /** 请求码 */
    private static final int IMAGE_REQUEST_CODE = 0;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int RESULT_REQUEST_CODE = 2;
    public static final int ADD_ATTACHMENT = 1000; 

    mHandlerWeak mtotalHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO 自动生成的方法存根
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        //            WindowManager.LayoutParams.FLAG_FULLSCREEN);// 去掉信息栏
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_feedback);

        mSettings = getSharedPreferences(WowuApp.PREFERENCE_KEY, MODE_PRIVATE);

        feed_back_send = (TextView) findViewById(R.id.tx_top_right);
        feed_back_content = (EditText) findViewById(R.id.feed_back_edit);
        feed_back_edit3 = (EditText) findViewById(R.id.feed_back_edit3);
        feed_back_send.setOnClickListener(this);
        feed_back_content.setOnClickListener(this);

//        feed_back_send.setText("发送");
        ((TextView) findViewById(R.id.top_title)).setText("意见反馈");


        findViewById(R.id.return_back).setOnClickListener(this);
//        findViewById(R.id.bt_feedback_submit).setOnClickListener(this);

        final Button  bt_feedback_submit = (Button) findViewById(R.id.bt_feedback_submit);
        bt_feedback_submit.setOnClickListener(this);
        bt_feedback_submit.getBackground().setAlpha(50);//0~255透明度值
        bt_feedback_submit.setTextColor(bt_feedback_submit.getTextColors().withAlpha(50));
        feed_back_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (feed_back_content.getText().toString().equals("")) {
                    bt_feedback_submit.getBackground().setAlpha(50);//0~255透明度值
                    bt_feedback_submit.setTextColor(bt_feedback_submit.getTextColors().withAlpha(50));
                } else {
                    bt_feedback_submit.getBackground().setAlpha(255);//0~255透明度值
                    bt_feedback_submit.setTextColor(bt_feedback_submit.getTextColors().withAlpha(255));
                }
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        mPicRecyclerView = (RecyclerView) findViewById(R.id.pullLoadMoreRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mPicRecyclerView.setLayoutManager(linearLayoutManager);

        userPicAdapter = new PicAdapter(mContext, null, R.layout.item_userpic_recycler2);
        userPicAdapter.setOnItemClickLitener(this);
        mPicRecyclerView.setAdapter(userPicAdapter);
/*//      附件材料
        Attach_gridView = (GridView) findViewById(R.id.meeting_pre_view);
        Attach_adapter = new MailGridViewAdapter<Attachment>(this);
        Attach_gridView.setAdapter(Attach_adapter);
        Attach_gridView.setOnItemClickListener(new MyOnItemClickListener());*/
        mtotalHandler = new mHandlerWeak(this);


        hideSoftKeyboard(this);

    }


    protected void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (activity.getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (activity.getCurrentFocus() != null)
                inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


















    private static class mHandlerWeak extends Handler {
        private WeakReference<FeedbackActivity> activity = null;

        public mHandlerWeak(FeedbackActivity act) {
            super();
            this.activity = new WeakReference<FeedbackActivity>(act);
        }

        @Override
        public void handleMessage(Message msg) {
            FeedbackActivity act = activity.get();
            if (null == act) {
                return;
            }
            switch (msg.what) {
                case ADD_ATTACHMENT:
                    UserInfoDetail.PhotosBean temp = new UserInfoDetail.PhotosBean();
                    temp.setLocalPath(msg.obj + "");
                    act.userPicAdapter.addItem(temp);
                    break;
            }
        }
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        if (resultCode == RESULT_OK) {
        if (resultCode == RESULT_OK && requestCode == ADD_ATTACHMENT && null != data) {
            Uri selectedImage = data.getData();
            String photoPath = getRealFilePath(selectedImage);

            Message msg = new Message();
            msg.what = ADD_ATTACHMENT;
            msg.obj = photoPath;
            mtotalHandler.sendMessage(msg);

        }


        // 结果码不等于取消时候
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case IMAGE_REQUEST_CODE :
                    String path=getRealFilePath(data.getData());
                    String b = path.substring(path.lastIndexOf("/") + 1, path.length());
                    File dir = new File(WowuApp.userImagePath);
                    if (dir.exists() == false) {
                        boolean br = dir.mkdirs();
                    }
                    imageUri = Uri.fromFile(new File(WowuApp.userImagePath, b));
//                    LogUtils.i("OwnerInfoEditActivity名称",WowuApp.userImagePath+b);
                    startPhotoZoom(data.getData());
                    break;
                case RESULT_REQUEST_CODE : // 图片缩放完成后
                    if (data != null) {
//                        getImageToView(data);
                        clearOldDrable();
//                        usersetting_userpic.setImageURI(imageUri);

                        Message msg = new Message();
                        msg.what = ADD_ATTACHMENT;
                        msg.obj =  getRealFilePath(imageUri)  ;
                        LogUtils.i("OwnerInfoEditActivity2",msg.obj+"");
                        mtotalHandler.sendMessage(msg);
                    }
                    break;
            }
        }

    }



    @Override
    public void onClick(View v) {
        // TODO 自动生成的方法存根
        switch (v.getId()) {
            case R.id.tx_top_right:
            case R.id.bt_feedback_submit:

                if(feed_back_content.getText().toString().equals("")){
                    MyToast.show(mContext,"问题和意见不能为空");
                }else{
                    sendToServer();
                }
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
     *
     * {
    "Text": "sample string 1",
    "Contact": "sample string 2",
    "Photos": [
    {
    "Content": "sample string 1",
    "FromIOS": true
    },
    {
    "Content": "sample string 1",
    "FromIOS": true
    }
    ]
    }
     *
     */
    ProgressDialog pb;
    public void sendToServer() {

        pb= UtilsTool.initProgressDialog(mContext,"请稍后...");
        pb.show();


            try {
                JSONObject json = new JSONObject();
                json.put("Text", feed_back_content.getText().toString());// WowuApp.UserId
                json.put("Contact",feed_back_edit3.getText().toString());

//            //LogUtils.i("上传到服务器的用户编辑资料为0：：：：：", json.toString());

//
                if (userPicAdapter.getList() != null && userPicAdapter.getList().size() > 0) {
                    JSONArray jarry = new JSONArray();
                    for (int i = 0; i < userPicAdapter.getList().size(); i++) {
                        if (((UserInfoDetail.PhotosBean) userPicAdapter.getList().get(i)).getLocalPath() != null) {//说明是刚增加的图片

                            JSONObject tempjson0 = new JSONObject();
//                        tempjson.put("PhotoId", ((UserInfoDetail.PhotosBean) userPicAdapter.getList().get(i)).getId());

/*                            tempjson0.put("Content", UtilsTool.bitmaptoString(BitmapFactory.decodeFile(
//                            tempjson0.put("Content", UtilsTool.compressBitmap(BitmapFactory.decodeFile(
                                    ((UserInfoDetail.PhotosBean) userPicAdapter.getList().get(i)).getLocalPath()
//                            ),100));
                            )));*/


                            tempjson0.put("Content",ImageDecodeUtil.bitmaptoString(ImageDecodeUtil.decodeBitmap(((UserInfoDetail.PhotosBean) userPicAdapter.getList().get(i)).getLocalPath())));

                            tempjson0.put("FromIOS","false");
                            jarry.put(tempjson0);
                        }
                    }
                    json.put("Photos", jarry);//.toString()

                }

//            //LogUtils.i("上传到服务器的用户编辑资料为：：：：：", json.toString());
//            UtilsTool.saveStringToSD(json.toString());
                loadDataService.loadPostJsonRequestData(WowuApp.JSON, WowuApp.SubmitSuggestionURL, json.toString(), R.id.contact_add);//

            } catch (Exception e) {
                e.printStackTrace();
            }
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
                     MyToast.show(mContext,"意见反馈成功");
                    finish();
                    break;

                case WRONG:
                    pg.dismiss();
                    MyToast.show(mContext,"意见反馈失败");
                    finish();
                    break;
            }

            super.handleMessage(msg);
        }
    };


    private String getRealFilePath(Uri selectedImage) {
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
                if (tmpPath != null && (tmpPath.endsWith(".jpg") || tmpPath.endsWith(".png") || tmpPath.endsWith(".gif") || tmpPath.endsWith(".jpeg"))) {
                    photoPath = tmpPath;
                }
            }
        }
        if(cursor!=null)cursor.close();
        return photoPath;
    }


    Uri imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "image.jpg"));
    /**
     * @param
     * @return void
     * @throws
     * @Title: clearOldDrable
     * @Description: 删除过时的缓存
     */
    public void clearOldDrable() {
//        imageLoader.getImageProvider().clearDiskCache();
//        imageLoader.getImageProvider().clearMemoryCache();
//        imageLoader.getImageProvider().flushFileCache();

        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.evictFromMemoryCache(imageUri);
        imagePipeline.evictFromDiskCache(imageUri);
        imagePipeline.evictFromCache(imageUri);

    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);


        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra("outputFormat", "JPEG");// 返回格式
        intent.putExtra("return-data", true);
        startActivityForResult(intent, RESULT_REQUEST_CODE);
    }



    @Override
    public void loadServerData(String response, int flag) {
        if(pb!=null &&pb.isShowing()){
            pb.dismiss();
        }

        this.finish();
        MyToast.show(mContext,"反馈成功~");
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

    }

    @Override
    public void loadDataFailed(String response, int flag) {

    }

    @Override
    public void onItemClick(View view, int position) {
        if (position == userPicAdapter.getList().size()) {
            if(userPicAdapter.getList().size()<4){
                addAttachment();
            }else{
                MyToast.show(mContext,"最多只能上传四张图片");
            }

        } else {


        }
    }




    private void addAttachment() {
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("image/*");

//        Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
        Intent intentFromGallery = new Intent();
        if (Build.VERSION.SDK_INT < 19) {
            intentFromGallery = new Intent(Intent.ACTION_GET_CONTENT);
            intentFromGallery.setType("image/*");
        } else {
            intentFromGallery = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }

        try {
            startActivityForResult(Intent.createChooser(intentFromGallery, "请选择要上传的图片"), ADD_ATTACHMENT);
//            这个是进行裁剪
//            startActivityForResult(intentFromGallery, IMAGE_REQUEST_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "无法选择文件，请先安装文件管理器", Toast.LENGTH_SHORT).show();
        }
    }













//    ArrayList<UserInfoDetail.PhotosBean> removedList = new ArrayList<UserInfoDetail.PhotosBean>(); //记录所有的最新消息
    @Override
    public void onItemLongClick(View view, final int position) {
        if (position == userPicAdapter.getList().size()) {
//            addAttachment();
        } else {
            final UserInfoDetail.PhotosBean removedInfos = (UserInfoDetail.PhotosBean) userPicAdapter.getItem(position);
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    this);
            builder.setTitle("附件删除");
            builder.setIcon(getResources().getColor(
                    android.R.color.transparent));
            builder.setMessage("是否删除当前附件");
            builder.setNegativeButton("确定",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                            int which) {
/*//                            如果是删除之前已经上传的图片，则将删除的图片信息保存下来
                            if (removedInfos.getLocalPath() == null) {
                                removedList.add(removedInfos);
                            }*/
                            userPicAdapter.removeItem(position);
                        }
                    });
            builder.setPositiveButton("取消", null);
            builder.create().show();
        }
    }


}

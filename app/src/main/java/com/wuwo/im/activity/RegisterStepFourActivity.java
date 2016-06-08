package com.wuwo.im.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.wuwo.im.config.WowuApp;
import com.wuwo.im.service.LoadserverdataService;
import com.wuwo.im.util.FormFile;
import com.wuwo.im.util.MyToast;
import com.wuwo.im.util.SocketHttpRequester;
import com.wuwo.im.util.UtilsTool;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import im.wuwo.com.wuwo.R;

/**
*desc RegisterStepFourActivity
*@author 王明远
*@日期： 2016/6/9 0:06
*@版权:Copyright   All rights reserved.
*/

public class RegisterStepFourActivity extends BaseLoadActivity  {

//    private  Context mContext = RegisterStepFourActivity.this;
    //    in.srain.cube.image.ImageLoader imageLoader;//加载用户头像
    private  SharedPreferences mSettings;
    private SimpleDraweeView usersetting_userpic;
    private  EditText user_register_nicheng;
    private  Uri uri;
    private LoadserverdataService loadDataService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_four);
//        ExitApp.getInstance().addOpenedActivity(this);
//        loadDataService = new LoadserverdataService(this);
        mSettings = mContext.getSharedPreferences(WowuApp.PREFERENCE_KEY, MODE_PRIVATE);
        initView();
    }

    private void initView() {
        findViewById(R.id.return_back).setOnClickListener(this);
        findViewById(R.id.set_user_pic).setOnClickListener(this);
        findViewById(R.id.register_finish).setOnClickListener(this);
        user_register_nicheng= (EditText) findViewById(R.id.user_register_nicheng);

        uri = Uri.parse(WowuApp.userImagePath + mSettings.getString("userid", "") + ".jpg");
        usersetting_userpic = (SimpleDraweeView) findViewById(R.id.usersetting_userpic);
        //usersetting_userpic.setImageURI(Uri.parse(WowuApp.userImagePath + mSettings.getString("userid", "") + ".jpg"));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.return_back:
                this.finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.set_user_pic:
                showDialog();
                break;
            case R.id.register_finish:
//                MainActivity
                WowuApp.Name=user_register_nicheng.getText().toString();
                Intent temp = new Intent(this, CharacterChooseActivity.class);
                startActivity(temp);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//                finish();
                break;

        }
    }


    private void showDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_user_pic_choose, null);
        final Dialog dialog = new Dialog(this, R.style.transparentFrameWindowStyle);
        dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();

        Button xiangmubanli_cancel = (Button) view.findViewById(R.id.userpic_setting_cancel);

        xiangmubanli_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO 自动生成的方法存根
                dialog.dismiss();
            }
        });

        //      直接拍照
        Button userimg_take_picture = (Button) view.findViewById(R.id.userimg_take_picture);
        userimg_take_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO 自动生成的方法存根
                openCamera();
                dialog.dismiss();
            }
        });

        //        从相册中选择
        Button userimg_select_pic = (Button) view.findViewById(R.id.userimg_select_pic);
        userimg_select_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO 自动生成的方法存根

                Intent intent = new Intent();
                //       这个是调用android内置的intent，来过滤图片文件 ，同时也可以过滤其他的
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PHOTO_SELECT);
                dialog.dismiss();
            }
        });

        // 设置显示动画
        window.setWindowAnimations(R.style.main_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = getWindowManager().getDefaultDisplay().getHeight();
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        // 设置显示位置
        dialog.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    private static String requestURL = WowuApp.serverAbsolutePath
            + "/mobile/updateUserImg!upUserImg.action";
    private String picPath = null;
    private static final int PHOTO_SELECT = 110;

    public static final String photoPath = "/mnt/sdcard/BrowsePhoto/";//测试，保存拍摄及展示的照片
    Uri imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "image.jpg"));
    private static final int PHOTO_CAPTURE = 0x11;

    //    String photoName;

    /**
     * 打开照相机
     */
    public void openCamera() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        File file = new File(photoPath);
        if (!file.exists()) { // 检查图片存放的文件夹是否存在
            file.mkdir(); // 不存在的话 创建文件夹
        }
        picPath = photoPath + System.currentTimeMillis() + ".jpg";
        File photo = new File(picPath);
        imageUri = Uri.fromFile(photo);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri); // 这样就将文件的存储方式和uri指定到了Camera应用中
        startActivityForResult(intent, PHOTO_CAPTURE);
    }

    @SuppressLint("NewApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        String sdStatus = Environment.getExternalStorageState();
        Log.i("获取到返回值结果", "获取到返回值结果");
        switch (requestCode) {
            case PHOTO_CAPTURE:
                if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
                    Log.i("内存卡错误", "请检查您的内存卡");
                } else {
                    clearOldDrable();
                    sendToServer();
                }
                break;

            case PHOTO_SELECT:

                /**
                 * 当选择的图片不为空的话，在获取到图片的途径
                 */
                Uri uri = null;
                if (data != null) {
                    uri = data.getData();
                }

                try {
                    String[] pojo = {MediaStore.MediaColumns.DATA};
                    Cursor cursor = managedQuery(uri, pojo, null, null, null);
                    if (cursor != null) {
                        ContentResolver cr = this.getContentResolver();
                        int colunm_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                        cursor.moveToFirst();
                        String path = cursor.getString(colunm_index);
                        /***
                         * 这里加这样一个判断主要是为了第三方的软件选择，比如：使用第三方的文件管理器的话，你选择的文件就不一定是图片了，
                         * 这样的话，我们判断文件的后缀名 如果是图片格式的话，那么才可以
                         */
                        if (path.endsWith("jpg") || path.endsWith("png")) {
                            picPath = path;
                            clearOldDrable();
                            sendToServer();
                        } else {
                            alert();
                        }
                    } else {
                        alert();
                    }

                } catch (Exception e) {
                }

                break;
            default:
                return;
        }

    }

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
        imagePipeline.evictFromMemoryCache(uri);
        imagePipeline.evictFromDiskCache(uri);
        imagePipeline.evictFromCache(uri);

    }

    private void alert() {
        Dialog dialog = new AlertDialog.Builder(this).setTitle("提示").setMessage("您选择的不是有效的图片")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        picPath = null;
                    }
                }).create();
        dialog.show();
    }

    /**
     * @param
     * @return void
     * @throws
     * @Title: sendToServer
     * @Description: 上传到服务器
     */
    public void sendToServer() {
        File tempfile = null;
        if (picPath != null) {
            tempfile = new File(picPath);
        }
        final File file = tempfile;

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //                    if (file != null) {
                Message msg = new Message();
                msg.what = Loading;
                mHandler.sendMessage(msg);
                //请求普通信息
                Map<String, String> params = new HashMap<String, String>();
                params.put("userId", mSettings.getString("userid", ""));//用户登录以后获取用户的userID并保存
//                if(MakeToken.refreshToken()){//重新刷新token
//                params.put("access_token", WowuApp.access_token);

                try {
                    FormFile formfile = null;
                    if (file != null) {
                        params.put("fileName", file.getName());
                        formfile = new FormFile(file.getName(), file, "image",
                                "application/octet-stream");
                    }
                    //                    上传图片到服务器
                    boolean result = SocketHttpRequester.post(requestURL, params, formfile);
                } catch (Exception e) {
                    // TODO 自动生成的 catch 块
                    e.printStackTrace();
                }

                Message msg2 = new Message();
                msg2.what = END;
                mHandler.sendMessage(msg2);

//                   }
            }
        });
        t.start();
    }

    ProgressDialog pg;
    private final int Loading = 1;
    private final int END = 2;

    /**
     * 上传到服务器是加载进度框
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Loading:

                    pg = UtilsTool.initProgressDialog(mContext, "正在上传.....");
                    pg.show();
                    break;

                case END:
                    clearOldDrable();
                    Drawable drawable = new BitmapDrawable(UtilsTool.loadCompressedBitmap(picPath, 80,
                            80));
                    usersetting_userpic.setImageDrawable(drawable);
                    //                usersetting_userpic.refreshDrawableState();
                    pg.dismiss();
                    MyToast.show(getApplicationContext(), "修改成功", Toast.LENGTH_LONG);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public void loadServerData(String response) {

    }

    @Override
    public void loadDataFailed(String response) {

    }
}

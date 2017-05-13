package com.wuwo.im.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
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
import android.provider.MediaStore.MediaColumns;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.wuwo.im.config.WowuApp;
import com.wuwo.im.util.FormFile;
import com.wuwo.im.util.LogUtils;
import com.wuwo.im.util.MyToast;
import com.wuwo.im.util.SocketHttpRequester;
import com.wuwo.im.util.UtilsTool;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import im.imxianzhi.com.imxianzhi.R;
/** 
*desc OwnerInfoDetailActivity
*@author 王明远
*@日期： 2016/6/9 0:08
*@版权:Copyright    All rights reserved.
*/

public class OwnerInfoDetailActivity extends BaseActivity implements OnClickListener {
    Context mContext = OwnerInfoDetailActivity.this;
//    in.srain.cube.image.ImageLoader imageLoader;//加载用户头像
    SharedPreferences mSettings;
    SimpleDraweeView usersetting_userpic;
    TextView set_userrole;
    Uri uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO 自动生成的方法存根
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        //            WindowManager.LayoutParams.FLAG_FULLSCREEN);// 去掉信息栏
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_owner_detail);
        mSettings = mContext.getSharedPreferences(WowuApp.PREFERENCE_KEY,
                MODE_PRIVATE);

        //      加载用户头像
//        imageLoader = ImageLoaderFactory.create(mContext);
//        usersetting_userpic = (in.srain.cube.image.CircleCubeImageView) findViewById(R.id.usersetting_userpic);
//        usersetting_userpic.loadImage(imageLoader, DistApp.userImagePath + mSettings.getString("userid", "") + ".jpg");// 设为缓存图片

        uri=Uri.parse(WowuApp.userImagePath + mSettings.getString("userid", "") + ".jpg");
         usersetting_userpic = (SimpleDraweeView)findViewById(R.id.usersetting_userpic);
        usersetting_userpic.setImageURI(Uri.parse(WowuApp.userImagePath + mSettings.getString("userid", "") + ".jpg"));

        TextView back_news1 = (TextView) findViewById(R.id.user_detail_return_back);
        back_news1.setOnClickListener(this);
        usersetting_userpic.setOnClickListener(this);

        set_userrole = (TextView) findViewById(R.id.set_userrole);
        RelativeLayout usersetting_userpic_layout = (RelativeLayout) findViewById(R.id.usersetting_userpic_layout);
        usersetting_userpic_layout.setOnClickListener(this);

        TextView set_username = (TextView) findViewById(R.id.set_username);
        set_username.setText(mSettings.getString("username", "游客"));

        TextView set_organsition = (TextView) findViewById(R.id.set_organsition);
        set_organsition.setText(mSettings.getString("userOrganzation", "").equals("") ? "无信息" : mSettings.getString("userOrganzation", "无信息"));


        TextView set_userrole = (TextView) findViewById(R.id.set_userrole);
        set_userrole.setText(mSettings.getString("userRole", "").equals("") ? "无信息" : mSettings.getString("userRole", "无信息"));


    }

    @Override
    public void onClick(View v) {
        // TODO 自动生成的方法存根
        switch (v.getId()) {
            case R.id.user_detail_return_back:
                OwnerInfoDetailActivity.this.finish();
                overridePendingTransition(0, R.anim.slide_out_to_left);
                break;
            case R.id.usersetting_userpic_layout:
            case R.id.usersetting_userpic:
            showDialog();
                break;

            default:
                break;
        }

    }

    private void showDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_user_pic_choose, null);
        final Dialog dialog = new Dialog(this, R.style.transparentFrameWindowStyle);
        dialog.setContentView(view, new LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();

        Button xiangmubanli_cancel = (Button) view.findViewById(R.id.userpic_setting_cancel);

        xiangmubanli_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO 自动生成的方法存根
                dialog.dismiss();
            }
        });

        //      直接拍照
        Button userimg_take_picture = (Button) view.findViewById(R.id.userimg_take_picture);
        userimg_take_picture.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO 自动生成的方法存根
                openCamera();
                dialog.dismiss();
            }
        });

        //        从相册中选择
        Button userimg_select_pic = (Button) view.findViewById(R.id.userimg_select_pic);
        userimg_select_pic.setOnClickListener(new OnClickListener() {
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
        wl.width = LayoutParams.MATCH_PARENT;
        wl.height = LayoutParams.WRAP_CONTENT;

        // 设置显示位置
        dialog.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        dialog.setCanceledOnTouchOutside(true);
        if(!((Activity) mContext).isFinishing()) {
            dialog.show();
        }
    }

    private static String requestURL = OkHttpUtils.serverAbsolutePath
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
        LogUtils.i("获取到返回值结果", "获取到返回值结果");
        switch (requestCode) {
            case PHOTO_CAPTURE:
                if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
                    LogUtils.i("内存卡错误", "请检查您的内存卡");
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
                    String[] pojo = {MediaColumns.DATA};

                    Cursor cursor = managedQuery(uri, pojo, null, null, null);
                    if (cursor != null) {
                        ContentResolver cr = this.getContentResolver();
                        int colunm_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
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

}

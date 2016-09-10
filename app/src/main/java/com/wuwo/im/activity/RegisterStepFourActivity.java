package com.wuwo.im.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.wuwo.im.config.WowuApp;
import com.wuwo.im.util.MyToast;
import com.wuwo.im.util.UtilsTool;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import im.wuwo.com.wuwo.R;

/**
 * 添加头像和性别
 *
 * @author 王明远
 * @日期： 2016/6/9 0:06
 * @版权:Copyright All rights reserved.
 */

public class RegisterStepFourActivity extends BaseLoadActivity {

    //    private  Context mContext = RegisterStepFourActivity.this;
    //    in.srain.cube.image.ImageLoader imageLoader;//加载用户头像
    private SharedPreferences mSettings;
    private SimpleDraweeView usersetting_userpic;
    private EditText user_register_nicheng;
    private Uri uri;
    private RadioGroup user_register_rg_gender;
    private TextView register_finish;

    //    private LoadserverdataService loadDataService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_four);
        mSettings = mContext.getSharedPreferences(WowuApp.PREFERENCE_KEY, MODE_PRIVATE);
        initView();
    }

    private void initView() {
        findViewById(R.id.return_back).setOnClickListener(this);
//        findViewById(R.id.set_user_pic).setOnClickListener(this);
        user_register_nicheng = (EditText) findViewById(R.id.user_register_nicheng);
        user_register_rg_gender = (RadioGroup) findViewById(R.id.user_register_rg_gender);

        findViewById(R.id.iv_top_title).setVisibility(View.VISIBLE);
        findViewById(R.id.top_title).setVisibility(View.GONE);



//        uri = Uri.parse(WowuApp.userImagePath + mSettings.getString("userid", "") + ".jpg");
        usersetting_userpic = (SimpleDraweeView) findViewById(R.id.usersetting_userpic);
        usersetting_userpic .setOnClickListener(this);
//        usersetting_userpic.setImageURI(Uri.parse(WowuApp.userImagePath + mSettings.getString("userid", "") + ".jpg"));

        user_register_rg_gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                //checkId就是当前选中的RadioButton
                if (R.id.user_register_rb_gender1 == checkedId) {
                    WowuApp.Gender = 1;
                } else {
                    WowuApp.Gender = 0;
                }
            }
        });

        register_finish= (TextView)findViewById(R.id.register_finish);
        register_finish.setOnClickListener(this);
        register_finish.getBackground().setAlpha(50);//0~255透明度值
        register_finish.setTextColor(register_finish.getTextColors().withAlpha(50));
        user_register_nicheng.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if(user_register_nicheng.getText().toString().equals("")){
                    register_finish.getBackground().setAlpha(50);//0~255透明度值
                    register_finish.setTextColor(register_finish.getTextColors().withAlpha(50));
                }else{
                    register_finish.getBackground().setAlpha(255);//0~255透明度值
                    register_finish.setTextColor(register_finish.getTextColors().withAlpha(255));
                }
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.return_back:
                this.finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.usersetting_userpic:
                showDialog();
                break;
            case R.id.register_finish:

                WowuApp.Name = user_register_nicheng.getText().toString();

                Message msg = new Message();
                msg.what = Loading;
                mHandler.sendMessage(msg);

                        try {
                            JSONObject json = new JSONObject();
                            json.put("PhoneNumber", WowuApp.PhoneNumber);
                            json.put("SmsValidateCode", WowuApp.SmsValidateCode);
                            json.put("Platform", "android");
                            json.put("Name", WowuApp.Name);
                            json.put("Gender", WowuApp.Gender);
                            json.put("Password",WowuApp.Password);
//                            if (WowuApp.picPath != null) {
//                                json.put("Photo", UtilsTool.bitmaptoString(UtilsTool.loadCompressedBitmap(WowuApp.picPath, 80, 80)));//BitmapFactory.decodeFile(picPath)
//                            }
                            Log.i("图像存放的路径为：：：","：："+imageUri.getPath());
                            json.put("Photo",UtilsTool.bitmaptoString(BitmapFactory.decodeFile(imageUri.getPath()) ) ); //   UtilsTool.bitmaptoString(photo)　　  UtilsTool.compressBitmap(photo,13)

                            loadDataService.loadPostJsonRequestData(WowuApp.JSON, WowuApp.RegisterURL, json.toString(), 0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                break;

        }
    }


    private void showDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_user_pic_choose, null);
        final Dialog dialog = new Dialog(this, R.style.transparentFrameWindowStyle);
        dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();

//        Button xiangmubanli_cancel = (Button) view.findViewById(R.id.userpic_setting_cancel);
        view.findViewById(R.id.userpic_setting_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO 自动生成的方法存根
                dialog.dismiss();
            }
        });

        //      直接拍照
//        Button userimg_take_picture = (Button) view.findViewById(R.id.userimg_take_picture);
        view.findViewById(R.id.userimg_take_picture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO 自动生成的方法存根
//                openCamera();
                Intent intentFromCapture = new Intent(
                        MediaStore.ACTION_IMAGE_CAPTURE);
                // 判断存储卡是否可以用，可用进行存储
                String state = Environment
                        .getExternalStorageState();
                if (state.equals(Environment.MEDIA_MOUNTED)) {
                    File path = Environment
                            .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                    File file = new File(path, IMAGE_FILE_NAME);
                    intentFromCapture.putExtra(
                            MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(file));
                }
                startActivityForResult(intentFromCapture, CAMERA_REQUEST_CODE);
                dialog.dismiss();
            }
        });

        //        从相册中选择
//        Button userimg_select_pic = (Button) view.findViewById(R.id.userimg_select_pic);
        view.findViewById(R.id.userimg_select_pic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
//                Intent intent = new Intent();
//                //       这个是调用android内置的intent，来过滤图片文件 ，同时也可以过滤其他的
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(intent, PHOTO_SELECT);

//                Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
//                intent.addCategory(Intent.CATEGORY_OPENABLE);


                Intent intentFromGallery = new Intent();
                if (Build.VERSION.SDK_INT < 19) {
                    intentFromGallery = new Intent(Intent.ACTION_GET_CONTENT);
                    intentFromGallery.setType("image/*");
                } else {
                    intentFromGallery = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                }

//                Intent intentFromGallery = new Intent();
//                intentFromGallery.setType("image/*"); // 设置文件类型
//                intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intentFromGallery, IMAGE_REQUEST_CODE);

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

    private static String requestURL = OkHttpUtils.serverAbsolutePath + "/mobile/updateUserImg!upUserImg.action";
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

        // 结果码不等于取消时候
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case IMAGE_REQUEST_CODE :
                    startPhotoZoom(data.getData());
                    break;
                case CAMERA_REQUEST_CODE :
                    // 判断存储卡是否可以用，可用进行存储
                    String state = Environment.getExternalStorageState();
                    if (state.equals(Environment.MEDIA_MOUNTED)) {
                        File path = Environment
                                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                        File tempFile = new File(path, IMAGE_FILE_NAME);
                        startPhotoZoom(Uri.fromFile(tempFile));
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "未找到存储卡，无法存储照片！", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case RESULT_REQUEST_CODE : // 图片缩放完成后
                    if (data != null) {
//                        getImageToView(data);
                        clearOldDrable();
                        usersetting_userpic.setImageURI(imageUri);
                    }
                    break;
            }
        }




        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
//        String sdStatus = Environment.getExternalStorageState();
//        Log.i("获取到返回值结果", "获取到返回值结果");
//        switch (requestCode) {
//            case PHOTO_CAPTURE:
//                if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
//                    Log.i("内存卡错误", "请检查您的内存卡");
//                } else {
//                    clearOldDrable();
//                    sendToServer();
//                }
//                break;
//
//            case PHOTO_SELECT:
//                /**
//                 * 当选择的图片不为空的话，在获取到图片的途径
//                 */
//                Uri uri = null;
//                if (data != null) {
//                    uri = data.getData();
//                }
//                try {
//                    String[] pojo = {MediaStore.MediaColumns.DATA};
//                    Cursor cursor = managedQuery(uri, pojo, null, null, null);
//                    if (cursor != null) {
//                        ContentResolver cr = this.getContentResolver();
//                        int colunm_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
//                        cursor.moveToFirst();
//                        String path = cursor.getString(colunm_index);
//                        /***
//                         * 这里加这样一个判断主要是为了第三方的软件选择，比如：使用第三方的文件管理器的话，你选择的文件就不一定是图片了，
//                         * 这样的话，我们判断文件的后缀名 如果是图片格式的话，那么才可以
//                         */
//                        if (path.endsWith("jpg") || path.endsWith("png")) {
//                            picPath = path;
//                            WowuApp.picPath = picPath;
//                            clearOldDrable();
//                            sendToServer();
//                        } else {
//                            alert();
//                        }
//                    } else {
//                        alert();
//                    }
//                } catch (Exception e) {
//                }
//                break;
//            default:
//                return;
//        }


    }




    /** 头像名称 */
    private static final String IMAGE_FILE_NAME = "image.jpg";

    /** 请求码 */
    private static final int IMAGE_REQUEST_CODE = 0;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int RESULT_REQUEST_CODE = 2;

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

    /**
     * 保存裁剪之后的图片数据
     *
     * @param
     */
    Bitmap photo=null;
    private void getImageToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
              photo = extras.getParcelable("data");

            Drawable drawable = new BitmapDrawable(this.getResources(), photo);
            usersetting_userpic.setImageDrawable(drawable);
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
        imagePipeline.evictFromMemoryCache(imageUri);
        imagePipeline.evictFromDiskCache(imageUri);
        imagePipeline.evictFromCache(imageUri);

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
//            tempfile = new File(picPath);

            return;
        }

//        final File file = tempfile;
//        Thread t = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                //                    if (file != null) {
//                Message msg = new Message();
//                msg.what = Loading;
//                mHandler.sendMessage(msg);
//                //请求普通信息
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("userId", mSettings.getString("userid", ""));//用户登录以后获取用户的userID并保存
////                if(MakeToken.refreshToken()){//重新刷新token
////                params.put("access_token", WowuApp.access_token);
//
//                try {
//                    FormFile formfile = null;
//                    if (file != null) {
//                        params.put("fileName", file.getName());
//                        formfile = new FormFile(file.getName(), file, "image",
//                                "application/octet-stream");
//                    }
//                    //                    上传图片到服务器
//                    boolean result = SocketHttpRequester.post(requestURL, params, formfile);
//                } catch (Exception e) {
//                    // TODO 自动生成的 catch 块
//                    e.printStackTrace();
//                }
//
//                Message msg2 = new Message();
//                msg2.what = END;
//                mHandler.sendMessage(msg2);
//
////                   }
//            }
//        });
//        t.start();


    }

    private ProgressDialog pg;
    private final int Loading = 1;
    private final int END = 2;
    private final int PICSHOW = 3;

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
//                    clearOldDrable();
//                    Drawable drawable = new BitmapDrawable(UtilsTool.loadCompressedBitmap(picPath, 80, 80));
//                    usersetting_userpic.setImageDrawable(drawable);
                   if(pg!=null) pg.dismiss();
//                    MyToast.show(getApplicationContext(), "修改成功", Toast.LENGTH_LONG);

                    Intent temp = new Intent(mContext, CharacterChooseActivity.class);
                    startActivity(temp);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    break;
                case PICSHOW:
                    clearOldDrable();
//                    Drawable drawable = new BitmapDrawable(UtilsTool.loadCompressedBitmap(picPath, 80, 80));
//                    usersetting_userpic.setImageDrawable(drawable);
                    File photo = new File(picPath);
                    imageUri = Uri.fromFile(photo);
                    usersetting_userpic.setImageURI(imageUri);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public void loadServerData(String response, int flag) {
        MyToast.show(mContext, "返回的结果为：：：：" + response);
        Log.i("返回的结果为", response.toString());

//        temp2 = new Intent(mContext, RegisterStepThreeActivity.class);
//        mContext.startActivity(temp2);
//        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//        temp2 = new Intent(mContext, RegisterStepThreeActivity.class);
//        mContext.startActivity(temp2);
//        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);



//        {"token":"380a389909944158bb050ea4126e94e85b521129eb8547409a91ece5f75a360f","easemobId":"8ed631fa-3565-11e6-988d-59e695f37b1d"}

        try {
            JSONObject responseJson=new JSONObject(response);
            OkHttpUtils.token= responseJson.optString("token");

            Message msg2 = new Message();
            msg2.what = END;
            mHandler.sendMessage(msg2);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void loadDataFailed(String request,int flag) {
        MyToast.show(mContext, "返回值失败" + request.toString());
        Log.i("返回值失败", request.toString());
        pg.dismiss();
    }
}

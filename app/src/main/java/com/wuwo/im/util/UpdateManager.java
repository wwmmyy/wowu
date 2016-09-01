package com.wuwo.im.util;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.wuwo.im.config.WowuApp;
import com.zhy.http.okhttp.service.LoadserverdataService;
import com.zhy.http.okhttp.service.loadServerDataListener;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

import im.wuwo.com.wuwo.R;

/**
 * 检查更新
 */
public class UpdateManager implements loadServerDataListener {
    /* 下载中 */
    private static final int DOWNLOAD = 1;
    /* 下载结束 */
    private static final int DOWNLOAD_FINISH = 2;
    //      /* 保存解析的XML信息 */
//      HashMap<String, String> mHashMap;
        /* 下载保存路径 */
    private String mSavePath;
    /* 记录进度条数量 */
    private int progress;
    /* 是否取消更新 */
    private boolean cancelUpdate = false;

    private Context mContext;
    /* 更新进度条 */
    private ProgressBar mProgress;
    private Dialog mDownloadDialog;

    //      新版本最新下载地址
    String new_version_url = "";
    //      新apk的保存名称
    String apkname;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                // 正在下载
                case DOWNLOAD:
                    // 设置进度条位置
                    mProgress.setProgress(progress);
                    break;
                case DOWNLOAD_FINISH:
                    // 安装文件
                    installApk();
                    break;
                default:
                    break;
            }
        }

        ;
    };

    public UpdateManager(Context context) {
        this.mContext = context;
    }


    //      final String  versionCode = getVersionCode(mContext)+"";
    String versionCode = "";
    String info = "";

    public void checkUpdateMe() {
        LoadserverdataService loadDataService = new LoadserverdataService(this);
        loadDataService.loadGetJsonRequestData(WowuApp.SystemVersionInfoURL, 100);
    }


//      /**
//       * 检测软件更新
//       */
//      public void checkUpdate() {
//            //调用地图服务配置信息，将获取到的服务配置信息实例化成类
//          SysConfig _rootconfigclient = SysConfig.createInstance(mContext);
//              if(_rootconfigclient.getDmpclient()!=null){
//                  _rootconfigclient.getSysVersionService("sys");
//                  _rootconfigclient.setmSysVersionResultListener(new onSysVersionResultListener() {
//                        @Override
//                        public void onSysVersionResult(DmpResponse dmpResponse) {
//                            // TODO 自动生成的方法存根
//                            if(dmpResponse!=null){
//                                // TODO 自动生成的方法存根
//                                JSONObject data = dmpResponse.getResponseJSON();
////                              String result = data.toString();
//                                String currentversion=(String) data.opt("version");
////                              // 版本判断
//                              if (! currentversion .equals(versionCode) ) {
//                                  //拼接将要下载安装文件的网址
//                                  new_version_url=getServerCheckUrl("?type=install");
////                                  new_version_url="http://7u2ftj.com1.z0.glb.clouddn.com/gudu2.1.apk";//表示最新的app下载地址；
////                                  getApkDownloadUrl();
//                                  handler.sendEmptyMessage(0);
//                              }
//                            }
//                        }
//                    });
//          }
//
//      }


    public void showUpdateDialog(String url) {
        new_version_url = url;
//               new_version_url="http://gdown.baidu.com/data/wisegame/69eddab391f8296e/shoujibaidu_16787211.apk";
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO 自动生成的方法存根
                handler.sendEmptyMessage(0);
            }
        }).start();

    }


    //定义Handler对象
    private Handler handler = new Handler() {
        @Override
        //当有消息发送出来的时候就执行Handler的这个方法
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //处理UI
            if (!NotOpenDialog) {
                showNoticeDialog();
            }
        }
    };


    /**
     * 获取最新版本的下载地址
     *
     * @param @return
     * @return String
     * @throws
     * @Title: getApkDownloadUrl
     * @Description: TODO
     */
    public String getServerCheckUrl(String type) {

        String url = "";
        Properties props = new Properties();
        //放在assets中的文件读取方法
        try {
            //方法一：通过activity中的context攻取s
            InputStream in = mContext.getAssets().open("dmp-client.config");
            //方法二：通过class获取setting.properties的FileInputStream
            //InputStream in = PropertiesUtill.class.getResourceAsStream("/assets/  setting.properties "));
            props.load(in);

            String serverProtocol = props.getProperty("serverProtocol", "http");
            String serverHost = props.getProperty("serverHost", "");
            String serverPort = props.getProperty("serverPort", "80");
            String serverContext = props.getProperty("serverContext", "");
            String serverProvider = props.getProperty("serverProvider", "");

            url = serverProtocol + "://" + serverHost + ":" + serverPort + serverContext + serverProvider + type;

            Log.i("拼接成的ip地址为：：", url);

        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        return url;
    }


    /**
     * 获取软件版本号
     *
     * @param context
     * @return
     */
    public String getVersionCode(Context context) {
        String versionCode = "0";
        try {
            // 获取软件版本号，对应AndroidManifest.xml下android:versionCode
            versionCode = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 显示软件更新对话框
     */
    AlertDialog noticeDialog;

    private void showNoticeDialog() {
        // 构造对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            /*    noticeDialog = new AlertDialog(mContext);
                noticeDialog.setTitle(R.string.soft_update_title);

                if(info!=null && !info.equals("")){
                    noticeDialog.setMessage(info);
                }else{
                    noticeDialog.setMessage(R.string.soft_update_info);


                *//*builder.setTitle(R.string.soft_update_title);
                builder.setMessage(R.string.soft_update_info);*//*
                noticeDialog.setPositiveButton(R.string.soft_update_updatebtn, new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						noticeDialog.dismiss();
                        // 显示下载对话框
                        showDownloadDialog();
					}
				});
                noticeDialog.setNegativeButton(R.string.soft_update_later, new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						noticeDialog.dismiss();
					}
				});*/
        // 更新
        builder.setTitle(R.string.soft_update_title);
        builder.setMessage(R.string.soft_update_info);
        builder.setPositiveButton(R.string.soft_update_updatebtn,
                new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        // 显示下载对话框
                        showDownloadDialog();
                    }
                });
        // 稍后更新
        builder.setNegativeButton(R.string.soft_update_later,
                new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

//                                                 ToolExitApplication.getInstance().exitMain(mContext);
//                                                   android.os.Process.killProcess(android.os.Process.myPid());
//                                                   System.exit(0);
//                                                   Intent intent = new Intent(Intent.ACTION_MAIN);
//                                                   intent.addCategory(Intent.CATEGORY_HOME);
//                                                   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                                   mContext.startActivity(intent);

                    }
                });
        noticeDialog = builder.create();
//              点击对话框的外部，对话框不消失
        noticeDialog.setCanceledOnTouchOutside(false);
        noticeDialog.show();
    }

    /**
     * 显示软件下载对话框
     */
    private void showDownloadDialog() {
        // 构造软件下载对话框
        Builder builder = new Builder(mContext);
        builder.setTitle(R.string.soft_updating);
        // 给下载对话框增加进度条
        final LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.download_file_progress, null);
        mProgress = (ProgressBar) v.findViewById(R.id.update_progress);
        builder.setView(v);
        builder.setCancelable(false);
        // 取消更新
        builder.setNegativeButton(R.string.soft_update_cancel,
                new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        // 设置取消状态
                        cancelUpdate = true;
                    }
                });
        mDownloadDialog = builder.create();
        mDownloadDialog.show();
//               mDownloadDialog.setCanceledOnTouchOutside(false);
        // 现在文件
        downloadApk();
    }

    /**
     * 下载apk文件
     */
    private void downloadApk() {
        // 启动新线程下载软件
        new downloadApkThread().start();
    }


    @Override
    public void loadServerData(final String response, int flag) {
/*        app版本：{"AndroidVersionName":"内测版","AndroidVersionCode":"1.0","AndroidDescription":"","AndroidUpdateTime":"2016-08-25",
                "AndroidDownloadUrl":"http://www.imxianzhi.com/先知先觉.apk",
                "AndroidRequrie":false,"IosVersionName":"内测版","IosVersionCode":"1.0",
                "IosDescription":"","IosUpdateTime":"2016-08-25","IosRequrie":false,"VersionPreview":"<p>这个版本好啊。</p>\n"}*/
        Log.d("UpdateManager获取的当前版本：", "app版本：" + response);
        // TODO 自动生成的方法存根
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject obj = new JSONObject(response);
                    String currentversion = (String) obj.opt("AndroidVersionCode");
                    new_version_url = obj.optString("AndroidDownloadUrl");
                    info = obj.optString("AndroidDescription");
                    versionCode = getVersionCode(mContext)+"";;
//                              // 版本判断
                    if (!currentversion.trim().equals(versionCode.trim())) {
                        handler.sendEmptyMessage(0);
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                    return;
                }
            }
        }).start();


    }

    @Override
    public void loadDataFailed(String response, int flag) {

    }

    /**
     * 下载文件线程
     *
     * @author coolszy
     * @date 2012-4-26
     * @blog http://blog.92coding.com
     */
    private class downloadApkThread extends Thread {
        @Override
        public void run() {
            try {
                // 判断SD卡是否存在，并且是否具有读写权限
                if (Environment.getExternalStorageState().equals(
                        Environment.MEDIA_MOUNTED)) {
                    // 获得存储卡的路径
                    String sdpath = Environment.getExternalStorageDirectory()
                            + "/";
                    mSavePath = sdpath + "download";
//                                      URL url = new URL(mHashMap.get("url"));
                    URL url = new URL(new_version_url);
//                                      URL url = new URL(URLEncoder.encode(new_version_url, "UTF-8"));

                    // 创建连接
                    HttpURLConnection conn = (HttpURLConnection) url
                            .openConnection();
                    conn.connect();
                    // 获取文件大小
                    int length = conn.getContentLength();
                    // 创建输入流
                    InputStream is = conn.getInputStream();

                    deleteTempfile();

                    File file = new File(mSavePath);
                    // 判断文件目录是否存在
                    if (!file.exists()) {
                        file.mkdir();
                    }
//                                      File apkFile = new File(mSavePath, mHashMap.get("name")+".apk");
                    apkname = "chengdu" + System.currentTimeMillis() + ".apk";
                    File apkFile = new File(mSavePath, apkname);
                    FileOutputStream fos = new FileOutputStream(apkFile);
                    int count = 0;
                    // 缓存
                    byte buf[] = new byte[1024];
                    // 写入到文件中
                    do {
                        int numread = is.read(buf);
                        count += numread;
                        // 计算进度条位置
                        progress = (int) (((float) count / length) * 100);
                        // 更新进度
                        mHandler.sendEmptyMessage(DOWNLOAD);
                        if (numread <= 0) {
                            // 下载完成
                            mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
                            break;
                        }
                        // 写入文件
                        fos.write(buf, 0, numread);
                    } while (!cancelUpdate);// 点击取消就停止下载.
                    fos.close();
                    is.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 取消下载对话框显示
            mDownloadDialog.dismiss();
        }

        private void deleteTempfile() {
            File dir = new File(mSavePath);
            if (dir.exists()) {
                File[] files = dir.listFiles();
                try {
                    for (File file3 : files) {
                        boolean flag = file3.delete();
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
            }
        }
    }

    ;

    /**
     * 安装APK文件
     */
    private void installApk() {
//              File apkfile = new File(mSavePath, mHashMap.get("name")+".apk");
        File apkfile = new File(mSavePath, apkname);
        if (!apkfile.exists()) {
            return;
        }
        // 通过Intent安装APK文件
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
                "application/vnd.android.package-archive");
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(i);
    }


    boolean NotOpenDialog = false;//activity销毁时不要打开升级弹出框

    public void closeDialog() {
        // TODO Auto-generated method stub
        NotOpenDialog = true;
        if (noticeDialog != null && noticeDialog.isShowing()) {
            noticeDialog.dismiss();
        }

        if (mDownloadDialog != null && mDownloadDialog.isShowing()) {
            mDownloadDialog.dismiss();
        }

    }
}
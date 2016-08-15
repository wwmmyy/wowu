package com.wuwo.im.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.wuwo.im.activity.LoginChooseActivity;
import com.wuwo.im.config.WowuApp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UtilsTool {
    private static final String TAG = UtilsTool.class.getSimpleName();
    public static final int SINA_SHARE_SUCCESS = 10;
    public static final int SINA_SHARE_ERROR = 11;
    public static final int SINA_SHARE_EXCEPTION = 12;
    public static final int TENCENT_SHARE_SUCCESS = 13;
    public static final int TENCENT_SHARE_ERROR = 14;
    public static final int QZONE_SHARE_SUCCESS = 18;
    public static final int QZONE_SHARE_ERROR = 19;
    public static final int TENCENT_SHARE_EXCEPTION = 15;
    public static final int FAVORITE_IMAGE_SUCCESS = 16;
    public static final int FAVORITE_IMAGE_FAILE = 17;
    public final static String MESSAGE_URL = "";

    public final static String APP_PREF_FILE = "sssconf";
    public static final String APP_FOLDER_ON_SD = Environment
            .getExternalStorageDirectory().getAbsolutePath()
            + "/TianXiang/TianXiang";

    public static final String PHOTOCACHE_FOLDER = APP_FOLDER_ON_SD
            + "/photo_cache";
    public static final String MY_FAVOURITE_FOLDER = APP_FOLDER_ON_SD
            + "/my_favourite";
    public static final String INSTALL_APK_PATH = APP_FOLDER_ON_SD
            + "/TianXiang.apk";


    private static UtilsTool mInstance = null;

    public static UtilsTool getInstance() {
        if (mInstance == null) {
            mInstance = new UtilsTool();
        }
        return mInstance;
    }

    public static String getNumberFromString(String str) {
        String str2 = "";
        if (str != null && !"".equals(str)) {
            for (int i = 0; i < str.length(); i++) {
                if (str.charAt(i) >= 48 && str.charAt(i) <= 57) {
                    str2 += str.charAt(i);
                }
            }
        }
        return str2;
    }


    /**
     * 读取验证码
     */
    public static String read(String str) throws IOException {
        File file = new File(str);
        FileInputStream fis = new FileInputStream(file);
        StringBuffer sb = new StringBuffer();

        BufferedInputStream bis = new BufferedInputStream(fis);
        BufferedReader read = new BufferedReader(new InputStreamReader(bis));
        int c = 0;
        while ((c = read.read()) != -1) {
            sb.append((char) c);
        }
        read.close();
        bis.close();
        fis.close();
        Log.i(TAG, sb.toString());
        String verify = sb.toString();
        return verify;
    }

    /**
     * 获取6位随机数字
     */
    public static int getSixNum() {
        int numcode = (int) ((Math.random() * 9 + 1) * 100000);
        return numcode;
    }

    /**
     * MD5加密
     *
     * @param enc
     * @return
     */
    public static String encryption(String enc) {
        String md5 = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] bs = enc.getBytes();
            digest.update(bs);
            md5 = byte2hex(digest.digest());
            Log.i("md5", md5);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return md5;
    }

    private static String byte2hex(byte[] b) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (Integer.toHexString(b[n] & 0xFF));
            if (stmp.length() == 1)
                hs = hs + "0" + stmp;
            else
                hs = hs + stmp;
        }
        return hs;
    }

    /**
     * 获取当前日期
     */
    public static String getCurrentDate() {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // 获取当前年份
        int mMonth = c.get(Calendar.MONTH);// 获取当前月份
        int mDay = c.get(Calendar.DAY_OF_MONTH);// 获取当前月份的日期号码
        return mYear + "-" + mMonth + "-" + mDay;
    }

    /**
     * 格式化文件路径(用于辨别是否是路径)
     *
     * @param filePath 文件路径
     * @return 格式化后的文件路径
     */
    public static String enCodeFilePath(String filePath) {
        filePath = "file:" + filePath;
        return filePath;
    }

    /**
     * px to sp
     *
     * @param pxValue
     * @param
     * @return
     */
    public static int px2sp(float pxValue, Context context) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 格式化文件路径(用于辨别是否是路径)
     *
     * @param filePath 文件路径
     * @return 格式化后的文件路径
     */
    public static String unEnCodeFilePath(String filePath) {
        if (filePath != null) {
            filePath = filePath.replace("file:", "");
        }
        return filePath;
    }

    /**
     * 判断是否是文件路径
     *
     * @param filePath
     * @return
     */
    public static boolean isFilePath(String filePath) {
        if (filePath != null) {
            if (filePath.contains("file:")) {
                return true;
            }
        }

        return false;
    }

    // 判断注册邮箱是否填写规范
    public static boolean emailFormat(String email) {
        boolean tag = true;
        final String pattern1 = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        final Pattern pattern = Pattern.compile(pattern1);
        final Matcher mat = pattern.matcher(email);
        if (!mat.find()) {
            tag = false;
        }
        return tag;
    }

    /**
     * 检查是否存在SDCard
     *
     * @return
     */
    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }


    /**
     * 设置白天和夜间模式
     *
     * @param window       当前的Window窗体
     * @param isNightModel 是否是夜间模式
     */
    public void setScreenBrightness(Window window, boolean isNightModel) {

        WindowManager.LayoutParams lp = window.getAttributes();

        if (isNightModel) {

            lp.screenBrightness = 0.4f;

        } else {

            lp.screenBrightness = 1.0f;

        }

        window.setAttributes(lp);

    }

    /**
     * 显示提示(String)
     *
     * @param context
     * @param msg
     */
    public void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 显示提示(resource id)
     *
     * @param context
     * @param msg
     */
    public void showToast(Context context, int msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 跳转Activity
     *
     * @param context
     * @param to
     */
    public static void jump(Context context, Class to) {
        Intent intent = new Intent(context, to);
        context.startActivity(intent);
    }

    /**
     * 验证字符串是否为空 或 ""
     *
     * @param str
     * @return
     */
    public static boolean validateString(String str) {
        if (str != null && !"".equals(str.trim()))
            return false;

        return true;
    }

    /**
     * 得到版本号
     *
     * @return
     */
    public static String getVersionName(Context context) {
        PackageInfo pinfo;
        String versionName = "";
        try {
            pinfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(),
                            PackageManager.GET_CONFIGURATIONS);
            versionName = pinfo.versionName;

        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        return versionName;
    }

    /**
     * 获得渠道号(推广平台0=本站,1=安智,2=机锋,3=安卓官方)
     *
     * @return
     */
    public static String getChannelCode(Context context) {
        String channelCode = "0";
        try {
            ApplicationInfo ai = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            if (bundle != null) {

                Object obj = bundle.get("UMENG_CHANNEL");
                if (obj != null) {
                    channelCode = obj.toString();
                }
                //Logger.d(TAG, "channelCode:" + channelCode + " obj:" + obj);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return channelCode;
    }

    /***
     * 检查网络是否可用
     */
    public static boolean isConnect(Context context) {
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                // 获取网络连接管理的对象
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null && info.isConnected()) {
                    // 判断当前网络是否已经连接
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            Log.v("error", e.toString());
        }
        return false;
    }

    // 这个函数会对图片的大小进行判断，并得到合适的缩放比例，比如2即1/2,3即1/3
    public static int computeSampleSize(Options options,
                                        int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    private static int computeInitialSampleSize(Options options,
                                                int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
                .sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 80 : (int) Math.min(
                Math.floor(w / minSideLength), Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    /**
     * 根据图片名称删除图片
     * <p/>
     * 文件名
     */
    public void deletePictureByFilePath(final Context context,
                                        final Handler handler, final String url) {

        new Thread() {
            @Override
            public void run() {
                if (url != null && !url.equals("")) {
                    // url转换HashCode
                    String urlHashCode = convertUrlToFileName(url);

                    // PictureSDUtil myPictureSDUtil = new
                    // PictureSDUtil(context);
                    File file = new File(MY_FAVOURITE_FOLDER,
                            urlHashCode + ".png");
                    if (file != null) {
                        file.delete();
                    }
                    Message message = new Message();
                    message.what = 2;
                    handler.sendMessage(message);

                }
            }
        }.start();
    }

    /**
     * 根据图片名称拷贝图片
     *
     * @param
     */
    public void copyPictureByFilePath(final Handler handler, final String url) {

        new Thread() {
            @Override
            public void run() {
                try {

                    if (url != null && !url.equals("")) {

                        // url转换HashCode
                        String urlHashCode = convertUrlToFileName(url);

                        copyFile(urlHashCode, urlHashCode);

                        Message message = new Message();
                        message.what = 3;
                        handler.sendMessage(message);

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }

    /**
     * 文件的复制(从一个文件夹复制到新的文件夹)
     * <p/>
     * 目标文件
     */
    public void copyFile(String filename, String urlHashCode)
            throws IOException {

        File myFavouriteFolder = new File(MY_FAVOURITE_FOLDER);
        if (!myFavouriteFolder.exists()) {
            myFavouriteFolder.mkdirs();
        }

        // 新建文件输入流并对它进行缓冲
        FileInputStream input = new FileInputStream(
                PHOTOCACHE_FOLDER + File.separator
                        + urlHashCode);
        BufferedInputStream inBuff = new BufferedInputStream(input);

        // 新建文件输出流并对它进行缓冲
        FileOutputStream output = new FileOutputStream(
                MY_FAVOURITE_FOLDER + File.separator
                        + filename);
        BufferedOutputStream outBuff = new BufferedOutputStream(output);

        // 缓冲数组
        byte[] b = new byte[1024 * 5];
        int len;
        while ((len = inBuff.read(b)) != -1) {
            outBuff.write(b, 0, len);
        }
        // 刷新此缓冲的输出流
        outBuff.flush();

        // 关闭流
        inBuff.close();
        outBuff.close();
        output.close();
        input.close();
    }

    /**
     * 将URL转换成HashCode
     *
     * @param url
     * @return
     */
    public String convertUrlToFileName(String url) {
        String fn = null;
        if (url != null && url.trim().length() > 0) {
            if (url.contains(".png")) {

                fn = String.valueOf(url.hashCode()) + ".png";

            } else {

                fn = String.valueOf(url.hashCode()) + ".jpg";

            }
        }
        return fn;
    }

    /**
     * 以md5方式加密字符串
     *
     * @param content 字符串
     * @param length  返回的长度,支持16位和32位,例length=16,length=32
     * @return 返回加密后的字符串
     */
    public static String getMd5(String content, int length) {
        try {
            MessageDigest bmd5 = MessageDigest.getInstance("MD5");
            bmd5.update(content.getBytes());
            int i;
            StringBuffer buf = new StringBuffer();
            byte[] b = bmd5.digest();
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            String md5Content = buf.toString();
            switch (length) {
                case 16:
                    md5Content = md5Content.substring(0, 16);
                    break;
                case 32:
                default:
                    break;
            }
            return md5Content;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public ByteArrayOutputStream getByteArrayOutputStreamByInputStream(
            InputStream inputStream) throws Exception {

        byte[] buffer = new byte[1024];
        int len = -1;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, len);
        }
        return byteArrayOutputStream;
    }


    /**
     * 获取屏幕的大小
     *
     * @param context
     * @return
     */
    public static Screen getScreenPix(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        return new Screen(dm.widthPixels, dm.heightPixels);
    }

    /**
     * 获取屏幕的宽
     *
     * @param context
     * @return
     */
    public static int getScreenWidthPix(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        return new Screen(dm.widthPixels, dm.heightPixels).widthPixels;
    }

    /**
     * 获取屏幕的高
     *
     * @param context
     * @return
     */
    public static int getScreenHeightPix(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        return new Screen(dm.widthPixels, dm.heightPixels).heightPixels;
    }

    public static class Screen {
        public int widthPixels;
        public int heightPixels;

        public Screen() {
        }

        public Screen(int widthPixels, int heightPixels) {
            this.widthPixels = widthPixels;
            this.heightPixels = heightPixels;
        }

        @Override
        public String toString() {
            return "(" + widthPixels + "," + heightPixels + ")";
        }
    }


    public static boolean checkFsWritable() {
        // Create a temporary file to see whether a volume is really writeable.
        // It's important not to put it in the root directory which may have a
        // limit on the number of files.

        // //Logger.d(TAG, "checkFsWritable directoryName ==   "
        // + APP_FOLDER_ON_SD);

        File directory = new File(APP_FOLDER_ON_SD);
        if (!directory.isDirectory()) {
            if (!directory.mkdirs()) {
                //Logger.e(TAG, "checkFsWritable directoryName 000  ");
                return false;
            }
        }
        File f = new File(APP_FOLDER_ON_SD, ".probe");
        try {
            // Remove stale file if any
            if (f.exists()) {
                f.delete();
            }
            if (!f.createNewFile()) {
                return false;
            }
            f.delete();
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    /**
     * 计算listview的高度
     *
     * @param listView
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight() + 5;
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }


    public static boolean hasStorage() {
        boolean hasStorage = false;
        String str = Environment.getExternalStorageState();

        if (str.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            hasStorage = checkFsWritable();
        }

        return hasStorage;
    }

    public static int freeSpaceOnSd() {
        int freeSize = 0;

        if (hasStorage()) {
            StatFs statFs = new StatFs(APP_FOLDER_ON_SD);

            try {
                long nBlocSize = statFs.getBlockSize();
                long nAvailaBlock = statFs.getAvailableBlocks();
                freeSize = (int) (nBlocSize * nAvailaBlock / 1024 / 1024);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return freeSize;
    }

    public static void updateFileTime(String dir, String fileName) {
        File file = new File(dir, fileName);
        long newModifiedTime = System.currentTimeMillis();
        file.setLastModified(newModifiedTime);
    }


    public static String getAPN(Context context) {
        String apn = "";
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();

        if (info != null) {
            if (ConnectivityManager.TYPE_WIFI == info.getType()) {
                apn = info.getTypeName();
                if (apn == null) {
                    apn = "wifi";
                }
            } else {
                apn = info.getExtraInfo().toLowerCase();
                if (apn == null) {
                    apn = "mobile";
                }
            }
        }
        return apn;
    }

    public static String getModel(Context context) {
        return Build.MODEL;
    }

    //
    // public static String getHardware(Context context) {
    // if (getPhoneSDK(context) < 8) {
    // return "undefined";
    // } else {
    // //Logger.d(TAG, "hardware:" + Build.HARDWARE);
    // }
    // return Build.HARDWARE;
    // }

    public static String getManufacturer(Context context) {
        return Build.MANUFACTURER;
    }

    public static String getFirmware(Context context) {
        return Build.VERSION.RELEASE;
    }

    public static String getSDKVer() {
        return Integer.valueOf(Build.VERSION.SDK_INT).toString();
    }

    public static String getLanguage() {
        Locale locale = Locale.getDefault();
        String languageCode = locale.getLanguage();
        if (TextUtils.isEmpty(languageCode)) {
            languageCode = "";
        }
        return languageCode;
    }

    public static String getCountry() {
        Locale locale = Locale.getDefault();
        String countryCode = locale.getCountry();
        if (TextUtils.isEmpty(countryCode)) {
            countryCode = "";
        }
        return countryCode;
    }

    public static String getIMEI(Context context) {
        TelephonyManager mTelephonyMgr = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String imei = mTelephonyMgr.getDeviceId();
        if (TextUtils.isEmpty(imei) || imei.equals("000000000000000")) {
            imei = "0";
        }

        return imei;
    }

    public static String getIMSI(Context context) {
        TelephonyManager mTelephonyMgr = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String imsi = mTelephonyMgr.getSubscriberId();
        if (TextUtils.isEmpty(imsi)) {
            return "0";
        } else {
            return imsi;
        }
    }

    // public static String getLac(Context context) {
    // CellIdInfo cell = new CellIdInfo();
    // CellIDData cData = cell.getCellId(context);
    // return (cData != null ? cData.getLac() : "1");
    // }
    //
    // public static String getCellid(Context context) {
    // CellIdInfo cell = new CellIdInfo();
    // CellIDData cData = cell.getCellId(context);
    // return (cData != null ? cData.getCellid() : "1");
    // }

    public static String getMcnc(Context context) {

        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String mcnc = tm.getNetworkOperator();
        if (TextUtils.isEmpty(mcnc)) {
            return "0";
        } else {
            return mcnc;
        }
    }

    /**
     * Get phone SDK version
     *
     * @return
     */
    public static int getPhoneSDK(Context mContext) {
        TelephonyManager phoneMgr = (TelephonyManager) mContext
                .getSystemService(Context.TELEPHONY_SERVICE);
        //Logger.i(TAG, "Bild model:" + Build.MODEL);
        //Logger.i(TAG, "Phone Number:" + phoneMgr.getLine1Number());
        //Logger.i(TAG, "SDK VERSION:" + Build.VERSION.SDK);
        //Logger.i(TAG, "SDK RELEASE:" + Build.VERSION.RELEASE);
        int sdk = 7;
        try {
            sdk = Integer.parseInt(Build.VERSION.SDK);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return sdk;
    }

    public static Object getMetaData(Context context, String keyName) {
        try {
            ApplicationInfo info = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);

            Bundle bundle = info.metaData;
            Object value = bundle.get(keyName);
            return value;
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    public static String getAppVersion(Context context) {
        PackageManager pm = context.getPackageManager();
        PackageInfo pi;
        try {
            pi = pm.getPackageInfo(context.getPackageName(), 0);
            String versionName = pi.versionName;
            return versionName;
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    public static String getSerialNumber(Context context) {
        String serial = null;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            serial = (String) get.invoke(c, "ro.serialno");
            if (serial == null || serial.trim().length() <= 0) {
                TelephonyManager tManager = (TelephonyManager) context
                        .getSystemService(Context.TELEPHONY_SERVICE);
                serial = tManager.getDeviceId();
            }
            //Logger.d(TAG, "Serial:" + serial);
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        return serial;
    }

    /**
     * 判断SDCard是否已满
     *
     * @return
     */
    public boolean isSDCardSizeOverflow() {
        boolean result = false;
        // 取得SDCard当前的状态
        String sDcString = Environment.getExternalStorageState();

        if (sDcString.equals(Environment.MEDIA_MOUNTED)) {

            // 取得sdcard文件路径
            File pathFile = Environment
                    .getExternalStorageDirectory();
            StatFs statfs = new StatFs(pathFile.getPath());

            // 获取SDCard上BLOCK总数
            long nTotalBlocks = statfs.getBlockCount();

            // 获取SDCard上每个block的SIZE
            long nBlocSize = statfs.getBlockSize();

            // 获取可供程序使用的Block的数量
            long nAvailaBlock = statfs.getAvailableBlocks();

            // 获取剩下的所有Block的数量(包括预留的一般程序无法使用的块)
            long nFreeBlock = statfs.getFreeBlocks();

            // 计算SDCard 总容量大小MB
            long nSDTotalSize = nTotalBlocks * nBlocSize / 1024 / 1024;

            // 计算 SDCard 剩余大小MB
            long nSDFreeSize = nAvailaBlock * nBlocSize / 1024 / 1024;
            if (nSDFreeSize <= 1) {
                result = true;
            }
        }// end of if
        // end of func
        return result;
    }


    /**
     * 根据图像URL创建Bitmap
     *
     * @param url URL地址
     * @return bitmap
     */
    public Bitmap CreateImage(String url) {
        // //Logger.d("ImageDownloader",
        // "开始调用CreateImage():" + System.currentTimeMillis());
        Bitmap bitmap = null;
        if (url == null || url.equals("")) {
            return null;
        }
        try {
            // //Logger.d(
            // "ImageDownloader",
            // "C Before SDCard decodeStream==>" + "Heap:"
            // + (Debug.getNativeHeapSize() / 1024) + "KB "
            // + "FreeHeap:"
            // + (Debug.getNativeHeapFreeSize() / 1024) + "KB "
            // + "AllocatedHeap:"
            // + (Debug.getNativeHeapAllocatedSize() / 1024)
            // + "KB" + " url:" + url);

            FileInputStream fis = new FileInputStream(url);
            Options opts = new Options();
            opts.inPreferredConfig = Config.ARGB_8888;
            opts.inTempStorage = new byte[100 * 1024];
            opts.inPurgeable = true;
            opts.inInputShareable = true;
            bitmap = BitmapFactory.decodeStream(fis, null, opts);

            // //Logger.d(
            // "ImageDownloader",
            // "C After SDCard decodeStream==>" + "Heap:"
            // + (Debug.getNativeHeapSize() / 1024) + "KB "
            // + "FreeHeap:"
            // + (Debug.getNativeHeapFreeSize() / 1024) + "KB "
            // + "AllocatedHeap:"
            // + (Debug.getNativeHeapAllocatedSize() / 1024)
            // + "KB" + " url:" + url);
        } catch (OutOfMemoryError e) {
            //Logger.e(TAG, "OutOfMemoryError", e);
            System.gc();
        } catch (FileNotFoundException e) {
            //Logger.e(TAG, "FileNotFoundException", e);
        }
        // //Logger.d("ImageDownloader",
        // "结束调用CreateImage():" + System.currentTimeMillis());
        return bitmap;
    }

    public static byte[] bitmap2Bytes(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    // Recycle the resource of the Image
    public void recycleImage(Bitmap bitmap) {
        try {
            if (bitmap != null && !bitmap.isMutable() && !bitmap.isRecycled()) {
                bitmap.recycle();
                System.gc();
            }
        } catch (Exception e) {
            e.printStackTrace();
            //Logger.e(TAG, "bitmap recycle excpetion");
        }
    }

    /**
     * 替换特殊字符
     *
     * @param fileName 图片的处理前的名字
     * @return 图片处理后的名字
     */
    public static String renameUploadFile(String fileName) {

        String result = "yepcolor";

        if (fileName != null && !fileName.equals("")) {

            result = fileName.hashCode() + "";// 获得文件名称的hashcode值

        }
        return result;
        // 只允许字母和数字
        // String regEx = "[^a-zA-Z0-9]";
        // 清除掉所有特殊字符
        // String regEx =
        // "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        // Pattern p = Pattern.compile(regEx);
        // Matcher m = p.matcher(fileName);
        // result = m.replaceAll("").trim();

    }


    public boolean saveBitmapToJpegFile(Bitmap bitmap, String filePath) {
        return saveBitmapToJpegFile(bitmap, filePath, 75);
    }

    public boolean saveBitmapToJpegFile(Bitmap bitmap, String filePath,
                                        int quality) {
        try {
            FileOutputStream fileOutStr = new FileOutputStream(filePath);
            BufferedOutputStream bufOutStr = new BufferedOutputStream(
                    fileOutStr);
//                    resizeBitmap(bitmap).compress(CompressFormat.JPEG, quality, bufOutStr);
            bufOutStr.flush();
            bufOutStr.close();
        } catch (Exception exception) {
            return false;
        }
        return true;
    }

//            /**
//             * 缩小图片
//             * @param bitmap
//             * @return
//             */
//            public Bitmap resizeBitmap(Bitmap bitmap) {
//                    if(bitmap != null){
//                            int width = bitmap.getWidth();
//                            int height = bitmap.getHeight();
//                            if(width>maxWidth){
//                                    int pWidth = maxWidth;
//                                    int pHeight = maxWidth*height/width;
//                                    Bitmap result = Bitmap.createScaledBitmap(bitmap, pWidth, pHeight, false);
//                                    bitmap.recycle();
//                                    return result;
//                            }
//                            if(height>maxHeight){
//                                    int pHeight = maxHeight;
//                                    int pWidth = maxHeight*width/height;
//                                    Bitmap result = Bitmap.createScaledBitmap(bitmap, pWidth, pHeight, false);
//                                    bitmap.recycle();
//                                    return result;
//                            }
//                    }
//                    return bitmap;
//            }


    /**
     * 计算采样率
     *
     * @param options
     * @param minSideLength
     * @param maxNumOfPixels
     * @return
     */
    public static int computeSampleSize2(Options options,
                                         int minSideLength, int maxNumOfPixels) {

        int initialSize = computeInitialSampleSize(options, minSideLength,

                maxNumOfPixels);

        int roundedSize;

        if (initialSize <= 8) {

            roundedSize = 1;

            while (roundedSize < initialSize) {

                roundedSize <<= 1;

            }

        } else {

            roundedSize = (initialSize + 7) / 8 * 8;

        }

        return roundedSize;

    }

    /**
     * 计算初始采样率
     *
     * @param options
     * @param minSideLength
     * @param maxNumOfPixels
     * @return
     */
    private static int computeInitialSampleSize2(Options options,
                                                 int minSideLength, int maxNumOfPixels) {

        double w = options.outWidth;

        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == -1) ? 1 :

                (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));

        int upperBound = (minSideLength == -1) ? 128 :

                (int) Math.min(Math.floor(w / minSideLength),

                        Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {

            // return the larger one when there is no overlapping zone.

            return lowerBound;

        }

        if ((maxNumOfPixels == -1) &&

                (minSideLength == -1)) {

            return 1;

        } else if (minSideLength == -1) {

            return lowerBound;

        } else {

            return upperBound;

        }

    }
    /**
     * 根据图片的路径获取图片的大小
     *
     * @param item
     */
    // public static void getBitmapSize(Items item) {
    // URL url;
    // try {
    // url = new URL(item.getPicUrl());
    // URLConnection conn = url.openConnection();
    // conn.connect();
    // InputStream is = conn.getInputStream();
    // BitmapFactory.Options options = new BitmapFactory.Options();
    // BitmapFactory.decodeStream(is, null, options);
    // options.inJustDecodeBounds = true;
    // int height = options.outHeight;
    // int width = options.outWidth;
    // item.setImageWidth(width);
    // item.setImageHeight(height);
    // } catch (MalformedURLException e) {
    // e.printStackTrace();
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    //
    // }


    /**
     * 发送消息到服务端，获取到服务器端返回的字符串
     *
     * @param params 请求参数
     * @param
     * @return
     * @throws Exception
     */
    public static String getStringFromServer(String PATH, Map<String, String> params) throws Exception {
        //带上token服务器端才会验证通过
//        params.put("access_token", WowuApp.access_token);
        return getInfoFromServer(PATH, params);
    }


//
//    /**
//     * 验证设备的状态
//    * @Title: validateDevice
//    * @Description: TODO
//    * @param @param params
//    * @return void
//    * @throws
//     */
//    static String validateDeviceURL = WowuApp.deviceValidatePath + "/mobilevalidate/app-mobileDeviceValidate.action";
//    public static void validateDevice(final String PATH) {
//        Thread t = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    //请求普通信息
//                    Map<String, String> map = new HashMap<String, String>();
//                    map.put("deviceNumber", WowuApp.ALL_deviceNumber);
//                    map.put("action",getFileName(PATH));
//                    map.put("latitude", WowuApp.latitude);
//                    map.put("longitude", WowuApp.longitude);
//                    map.put("deviceType","android");
//                    map.put("userId", WowuApp.username);//在移动端并没有级联查询，所以直接保存用户名
//                    String totalresult = UtilsTool.getInfoFromServer(validateDeviceURL, map );
////                    Log.d("获取到的终端设备验证信息为：", totalresult);
//                    Message msg2 = new Message();
//                    if (totalresult != null && !totalresult.equals("")) {
//                        JSONObject obj = new JSONObject(totalresult);
//                        String result=obj.optString("result");
//                        if (result != null) {
////                            msg2.what = END;
//                            Intent mIntent = new Intent(WowuApp.ACTION_NAME);
//                            mIntent.putExtra("result",result.trim());
//                            //发送广播
//                            WowuApp.sContext.sendBroadcast(mIntent);
//
//                        } else {
////                            msg2.what = WRONG;
//                        }
//
//
//                    } else {
////                        msg2.what = WRONG;
//                    }
//
//
//                } catch (Exception e) {
//                    // TODO 自动生成的 catch 块
//                    e.printStackTrace();
//                }
//
//                //                    }
//            }
//        });
//        t.start();
//    }


    /**
     * 发送消息到服务端，获取到服务器端返回的字符串
     *
     * @param params 请求参数
     * @param encode 编码格式
     * @return
     * @throws Exception
     */
    public static String getInfoFromServer(String PATH, Map<String, String> params) throws Exception {
////      把设备的坐标也传过去
//      params.put("latitude", WowuApp.latitude);
//      params.put("longitude", WowuApp.longitude);
//      params.put("radius", WowuApp.Radius);
//      app标示号
        params.put("appidentify", "com.dist.xian");

        StringBuilder stringBuilder = new StringBuilder();
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, String> entry : params.entrySet()) {

                stringBuilder.append(entry.getKey()).append("=")
                        .append(URLEncoder.encode(entry.getValue(), "UTF-8")).append("&");

            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);


            URL url = new URL(PATH);

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(6000);
            urlConnection.setRequestMethod("POST"); // 以post请求方式提交
            urlConnection.setDoInput(true); // 读取数据
            urlConnection.setDoOutput(true); // 向服务器写数据
            //urlConnection.getContentLength();
            // 获取上传信息的大小和长度
            byte[] myData = stringBuilder.toString().getBytes();
            // 设置请求体的类型是文本类型,表示当前提交的是文本数据
            urlConnection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            urlConnection.setRequestProperty("Content-Length", String.valueOf(myData.length));
            // 获得输出流，向服务器输出内容
            OutputStream outputStream = urlConnection.getOutputStream();
            // 写入数据
            outputStream.write(myData, 0, myData.length);
            outputStream.close();
            // 获得服务器响应结果和状态码
            int responseCode = urlConnection.getResponseCode();

            //urlConnection.getContentLength();

            if (responseCode == 200) {
                // 取回响应的结果
                //                                  return changeInputStream(urlConnection.getInputStream(),
                //                                                  encode);

                //return urlConnection.getInputStream();

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] data = new byte[1024];
                int len = 0;
                String result = null;
                if (urlConnection.getInputStream() != null) {
                    try {
                        while ((len = urlConnection.getInputStream().read(data)) != -1) {
                            byteArrayOutputStream.write(data, 0, len);
                        }
                        result = new String(byteArrayOutputStream.toByteArray(), "UTF-8");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return result;
            } else if (responseCode == 401) {
////                说明token已经过期
////                    Log.d("说明token已经过期", "过期");
//                    if(MakeToken.refreshToken()){//重新刷新token
////                        Log.d("刷新token成功", "刷新token成功");
//                        params.put("access_token", WowuApp.access_token);
//                       return getInfoFromServer(PATH,params);//重新回调自身
//                    }
            }
        }
        return null;
    }


    /**
     * 系统退出时删除打开的缓存文件
     */
    public static void deleteTempFile(String filePath) {
        File dir = new File(filePath);
        if (dir.exists()) {

            File[] files = dir.listFiles();
            try {
                if (files.length > 0) {
                    for (File file3 : files) {
                        boolean flag = file3.delete();
                    }
                }

            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }
    }


    /**
     * 检查网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean checkNet(Context context) {// 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                // 获取网络连接管理的对象
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null && info.isConnected()) {
                    // 判断当前网络是否已经连接
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }

                }
            }
        } catch (Exception e) {
        }
        return false;
    }


    //获取文件格式名称
    public static String getFileType(String url) {
        int i = url.lastIndexOf(".");
        return url.substring(i + 1);
    }

    //获取文件格式名称 文件名
    public static String getFileName(String url) {
        int i = url.lastIndexOf("/");
        return url.substring(i + 1);
    }


    //时间比较函数
    public static boolean CompareTime(String time1, String time2) {
        java.text.DateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        try {
            c1.setTime(df.parse(time1));
            c2.setTime(df.parse(time2));
        } catch (java.text.ParseException e) {
            System.err.println("格式不正确");
        }
        int result = c1.compareTo(c2);
        if (result > 0) return true;
        else
            return false;
    }


    public static Properties loadProperties(Context context) {
        //        InputStream in = null;
        //        Properties props = null;
        //        try {
        //            in = getClass().getResourceAsStream(
        //                    "/org/androidpn/client/client.properties");
        //            if (in != null) {
        //                props = new Properties();
        //                props.load(in);
        //            } else {
        //                Log.e(LOGTAG, "Could not find the properties file.");
        //            }
        //        } catch (IOException e) {
        //            Log.e(LOGTAG, "Could not find the properties file.", e);
        //        } finally {
        //            if (in != null)
        //                try {
        //                    in.close();
        //                } catch (Throwable ignore) {
        //                }
        //        }
        //        return props;

        Properties props = new Properties();


//        //放在res文件中的读取操作方法
//        try {
//            int id = context.getResources().getIdentifier("dmppush", "raw",
//                    context.getPackageName());
//            props.load(context.getResources().openRawResource(id));
//        } catch (Exception e) {
////            Log.e(LOGTAG, "服务 ServiceManager Could not find the properties file.", e);
//             e.printStackTrace();
//        }


        //放在assets中的文件读取方法
        try {
            //方法一：通过activity中的context攻取setting.properties的FileInputStream
            InputStream in = context.getAssets().open("dmppush.properties");
            //方法二：通过class获取setting.properties的FileInputStream
            //InputStream in = PropertiesUtill.class.getResourceAsStream("/assets/  setting.properties "));
            props.load(in);
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return props;
    }

    public static String inputStreamToStr(InputStream inputStream) {
        StringBuilder sb = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                inputStream));
        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        }
        return sb.toString().trim();
    }



    public static void startActivity(Context context, Class<?> activity,boolean finish) {
        Intent intent = new Intent(context, activity);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        context.startActivity(intent);
    }

    /**
     * 开启activity
     */
    public static void launchActivity(Context context, Class<?> activity) {
        Intent intent = new Intent(context, activity);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        context.startActivity(intent);
    }

    public static void launchActivityForResult(Activity context,
                                               Class<?> activity, int requestCode) {
        Intent intent = new Intent(context, activity);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        context.startActivityForResult(intent, requestCode);
    }

    /**
     * 隐藏软键盘
     */
    public static void hideSoftKeybord(Activity activity) {

        if (null == activity) {
            return;
        }
        try {
            final View v = activity.getWindow().peekDecorView();
            if (v != null && v.getWindowToken() != null) {
                InputMethodManager imm = (InputMethodManager) activity
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        } catch (Exception e) {

        }
    }

    /**
     * 判断是否为合法的json
     *
     * @param jsonContent 需判断的字串
     */
    public static boolean isJsonFormat(String jsonContent) {
        try {
            new JsonParser().parse(jsonContent);
            return true;
        } catch (JsonParseException e) {
            return false;
        }
    }

    /**
     * 判断字符串是否为空
     *
     * @param text
     * @return true null false !null
     */
    public static boolean isNull(String text) {
        if (text == null || "".equals(text.trim()) || "null".equals(text))
            return true;
        return false;
    }


    /**
     * 判断网络是否可用
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    /**
     * 将正方形图片整成圆形图片
     *
     * @param bitmap
     * @return
     */
    public static Bitmap toRoundCorner(Bitmap bitmap) {

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);

        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;

        final Paint paint = new Paint();

        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        final RectF rectF = new RectF(rect);

        final float roundPx = bitmap.getHeight() / 2;

        paint.setAntiAlias(true);

        canvas.drawARGB(0, 0, 0, 0);

        paint.setColor(color);

        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));

        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;

    }

    /**
     * 得到设备屏幕的宽度
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 得到设备屏幕的高度
     */
    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 得到设备的密度
     */
    public static float getScreenDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }


    /**
     * 关闭弹出的软键盘
     */
    public static void closeKeyBoard(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        //得到InputMethodManager的实例
        if (imm.isActive()) {
            //如果开启
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
            //关闭软键盘，开启方法相同，这个方法是切换开启与关闭状态的
        }


//  //隐藏软键盘-可行
//   int flags = WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
//   ((Activity) context).getWindow().addFlags(flags);
    }


    /**
     * 初始化并加载进度框 MaterialDialog
     */
    public static ProgressDialog initProgressDialog(Context mContext, String loadInfo) {
    /*   ProgressDialog mdialog = new ProgressDialog(mContext);
       // 设置进度条风格，风格为圆形，旋转的(ProgressDialog.STYLE_HORIZONTAL长形风格)
       mdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
       //   // 设置ProgressDialog 标题
       //   progressdialog.setTitle("提示");
       // 设置ProgressDialog 提示信息
       mdialog.setMessage(loadInfo);
       // 设置ProgressDialog 标题图标
       // m_pDialog.setIcon(R.drawable.img1);
       // 设置ProgressDialog 的进度条是否不明确
       mdialog.setIndeterminate(false);
       // 设置ProgressDialog 是否可以按退回按键取消
       mdialog.setCancelable(true);
       // 设置ProgressDialog 的一个Button
       mdialog.setButton("确定", new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialog, int i)
           {
               // 点击“确定按钮”取消对话框
               dialog.cancel();
           }
       });
       // 让ProgressDialog显示
//       mdialog.show();
       return mdialog;*/

//	   final MaterialDialog materialDialog = new MaterialDialog(mContext);
//		int backgroundColor = Color.parseColor("#1E88E5");
//
//		if (materialDialog != null) {
//			View view = LayoutInflater.from(mContext).inflate(
//					R.layout.progressbar_dialogaa, null);
//			TextView textName1 = (TextView) view.findViewById(R.id.textName);
//			textName1.setText(loadInfo);
//			ProgressBarCircularIndetermininate progressBar = (ProgressBarCircularIndetermininate) view.findViewById(R.id.progressBarCircularIndetermininate);
//			progressBar.setBackgroundColor(backgroundColor);
//
//			materialDialog
//			.setPositiveButton("OK", new View.OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					materialDialog.dismiss();
//				}
//			})
//        .setContentView(view).show();
//		}
//		return materialDialog;


        ProgressDialog mdialog = new ProgressDialog(mContext);
        mdialog.setIndeterminate(true);
        mdialog.setMessage(loadInfo);
        return mdialog;

    }


//    public static MaterialDialog initMaterialProgressDialog(Context context, String textName){
//        final MaterialDialog materialDialog = new MaterialDialog(context);
//        int backgroundColor = Color.parseColor("#1E88E5");
//
//        if (materialDialog != null) {
//            View view = LayoutInflater.from(context).inflate(
//                    R.layout.progressbar_dialogaa, null);
//            TextView textName1 = (TextView) view.findViewById(R.id.textName);
//            textName1.setText(textName);
//            ProgressBarCircularIndetermininate progressBar = (ProgressBarCircularIndetermininate) view.findViewById(R.id.progressBarCircularIndetermininate);
//            progressBar.setBackgroundColor(backgroundColor);
//
//            materialDialog
//                    .setPositiveButton("OK", new View.OnClickListener() {
//
//                        @Override
//                        public void onClick(View v) {
//                            // TODO Auto-generated method stub
//                            materialDialog.dismiss();
//                        }
//                    })
//                    .setContentView(view).show();
//        }
//        return materialDialog;
//    }


    /**
     * 从网络服务器获取文件资料信息
     */
    //public  File getFileFromServer(String path,String filepath,ProgressDialog pd) throws Exception{
    public static File getFileFromServer(String path, String fileName, ProgressDialog pd) throws Exception {
        //          public  File getFileFromServer(String path, String fileName) throws Exception{
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            if (conn.getResponseCode() == 200) {
                int total = conn.getContentLength();
                pd.setMax(total);


//               System.out.println("获取到文件长度为：：：："+total);
                InputStream is = conn.getInputStream();
                //                  File file = new File(filepath);

                deleteTempFile(WowuApp.ALL_CachePathDirTemp);//删除临时文件

                File dir = new File(WowuApp.ALL_CachePathDirTemp);
                if (dir.exists() == false) {
                    boolean b = dir.mkdirs();
                }

                File file = new File(WowuApp.ALL_CachePathDirTemp, fileName);
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int len = 0;
                int process = 0;
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                    process += len;
                    pd.setProgress(process);
                    //                          Thread.sleep(10);
                }
                fos.flush();
                fos.close();
                is.close();
                pd.dismiss();
                return file;
            }
        }
        return null;
    }


    /**
     * 将assets下某个文件夹下的列表写到SD中
     *
     * @param sdfiledir
     */
    public static boolean writeAssetsToSD(Context mcontext, String sdfiledir, String assetsfiledirname) {
        File file = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            file = new File(sdfiledir);
            if (file.exists() == false) {
                boolean b = file.mkdirs();
            }
            String[] list_image = null;
            try {
                //得到assets/showpic/目录下的所有文件的文件名，以便后面打开操作时使用
                list_image = mcontext.getResources().getAssets().list(assetsfiledirname);
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            if (list_image != null) {
                for (int i = 0; i < list_image.length; i++) {
                    try {
                        String filename = assetsfiledirname + "/" + list_image[i];
                        InputStream bitmapinstream = null;
                        bitmapinstream = mcontext.getResources().getAssets().open(filename);
                        File imgfile = new File(sdfiledir, list_image[i]);
                        FileOutputStream fos = new FileOutputStream(imgfile);
                        byte[] buffer = new byte[1024];
                        int len = 0;
                        while ((len = bitmapinstream.read(buffer)) != -1) {
                            fos.write(buffer, 0, len);
                        }
                        fos.flush();
                        fos.close();
                        bitmapinstream.close();
                    } catch (IOException e) {
                        // TODO 自动生成的 catch 块
                        e.printStackTrace();
                    }

                }

            }


            return true;
        }
        return false;
    }


    /**
     * 将assets下某个文件夹下的列表写到SD中,并获取该文件夹下的文件列表
     */
    public static File[] getListFiles(Context mcontext, String sdfiledir, String assetsfiledirname) {
        if (UtilsTool.writeAssetsToSD(mcontext, sdfiledir, assetsfiledirname)) {
            File file = new File(sdfiledir);
            //            File[] listFiles = file.listFiles();
            return file.listFiles();
        }
        return null;
    }


    /**
     * 判断是否为平板
     *
     * @return
     */
    public static boolean isPad(Context mcontext) {
        WindowManager wm = (WindowManager) mcontext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        // 屏幕宽度
        float screenWidth = display.getWidth();
        // 屏幕高度
        float screenHeight = display.getHeight();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        double x = Math.pow(dm.widthPixels / dm.xdpi, 2);
        double y = Math.pow(dm.heightPixels / dm.ydpi, 2);
        // 屏幕尺寸
        double screenInches = Math.sqrt(x + y);
        // 大于6尺寸则为Pad
        if (screenInches >= 6.0) {
            return true;
        }
        return false;
    }


    public static InputStream loadAssetsJson(Context context, String fileName) {
        //放在assets中的文件读取方法
        InputStream in = null;
        try {
            //方法一：通过activity中的context攻取setting.properties的FileInputStream
//           in = context.getAssets().open("news.json");
            in = context.getAssets().open(fileName);
            //方法二：通过class获取setting.properties的FileInputStream
            //InputStream in = PropertiesUtill.class.getResourceAsStream("/assets/setting.properties "));
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return in;
    }


    /**
     * 由于暂时无法访问服务器，从本地文件获取新闻通知
     *
     * @param
     * @return void
     * @throws
     * @Title: getNews
     * @Description: TODO
     */
    public static String getInfoFromAsserts(Context mActivity, String fileName) {
        StringBuilder sb = new StringBuilder();
        InputStream inputStream = UtilsTool.loadAssetsJson(mActivity, fileName);
        try {

            if (inputStream != null) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                        inputStream));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
            } else {
                sb.append("");
            }

        } catch (IOException e) {

            return null;
        }

        return sb.toString();
    }


    /**
     * 除去数组中重复的值
     *
     * @param @param  num
     * @param @return
     * @return String[]
     * @throws
     * @Title: getDistinct
     * @Description: TODO
     */
    public static String[] getDistinct(String num[]) {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < num.length; i++) {
            if (!list.contains(num[i])) {//如果list数组不包括num[i]中的值的话，就返回true。
                list.add(num[i]); //在list数组中加入num[i]的值。已经过滤过。
            }
        }

        return list.toArray(new String[0]);  //toArray（数组）方法返回数组。并要指定Integer类型。new integer[o]的空间大小不用考虑。因为如果list中的长度大于0（你integer的长度），toArray方法会分配一个具有指定数组的运行时类型和此列表大小的新数组。
    }


    /**
     * @param @param  picpath
     * @param @return
     * @return Bitmap
     * @throws
     * @Title: loadCompressedBitmap
     * @Description: TODO
     */
    public static Bitmap loadCompressedBitmap(String picpath, int screenWidth, int screenHeight) {
        //2.得到图片的宽高。
        Options opts = new Options();//解析位图的附加条件
        opts.inJustDecodeBounds = true;//不去解析真实的位图，只是获取这个位图的头文件信息
//       Bitmap bitmap = BitmapFactory.decodeFile("/mnt/sdcard/photo.jpg", opts);
//       Bitmap bitmap = BitmapFactory.decodeFile(picpath, opts);
        int bitmapWidth = opts.outWidth;
        int bitmapHeight = opts.outHeight;
//       System.out.println("图片宽高： "+bitmapWidth+"-"+bitmapHeight);

        //3.计算缩放比例
        int dx = bitmapWidth / screenWidth;
        int dy = bitmapHeight / screenHeight;
        int scale = 1;
        if (dx > dy && dy > 1) {
//               System.out.println("按照水平方法缩放,缩放比例："+dx);
            scale = dx;
        }

        if (dy > dx && dx > 1) {
//               System.out.println("按照垂直方法缩放,缩放比例："+dy);
            scale = dy;
        }
        //4.缩放加载图片到内存。
        opts.inSampleSize = scale;
        opts.inJustDecodeBounds = false;//真正的去解析这个位图。
//       return   BitmapFactory.decodeFile("/mnt/sdcard/photo.jpg", opts);
        return BitmapFactory.decodeFile(picpath, opts);
    }


    public static Bitmap getCircleBitmap(Bitmap bitmap, int width, int height) {
        Bitmap croppedBitmap = scaleCenterCrop(bitmap, width, height);
        Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();

        final Rect rect = new Rect(0, 0, width, height);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);

        int radius = (width > height) ? height : width;
        radius /= 2;

        canvas.drawCircle(width / 2, height / 2, radius, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(croppedBitmap, rect, rect, paint);

        return output;
    }


    public static Bitmap scaleCenterCrop(Bitmap source, int newHeight, int newWidth) {
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();

        float xScale = (float) newWidth / sourceWidth;
        float yScale = (float) newHeight / sourceHeight;
        float scale = Math.max(xScale, yScale);

        float scaledWidth = scale * sourceWidth;
        float scaledHeight = scale * sourceHeight;

        float left = (newWidth - scaledWidth) / 2;
        float top = (newHeight - scaledHeight) / 2;

        RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);

        Bitmap dest = Bitmap.createBitmap(newWidth, newHeight, source.getConfig());
        Canvas canvas = new Canvas(dest);
        canvas.drawBitmap(source, null, targetRect, null);

        return dest;
    }


    public static void setListViewHeightBasedOnChildren2(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }


    /**
     * 　　*将错误日志文件保存到sd卡
     * 　　* @param toSaveString
     * 　　* @param filePath
     * 　　Throwable e
     *
     * @throws Exception
     */
    public static void saveErrorFile(String toSaveString, String errorname) {
        try {
//        Writer info = new StringWriter();
//        PrintWriter printWriter = new PrintWriter(info);
//        e.printStackTrace(printWriter);
//        Throwable cause = e.getCause();
//        while (cause != null) {
//            cause.printStackTrace(printWriter);
//            cause = cause.getCause();
//        }
//        String toSaveString = info.toString();
//        printWriter.close();
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                InputStream is = StringTOInputStream(toSaveString);
                File dir = new File("/mnt/sdcard/");
                if (dir.exists() == false) {
                    boolean b = dir.mkdirs();
                }

                File file = new File("/mnt/sdcard/", errorname);
                FileOutputStream fos = new FileOutputStream(file);
                //           fos.write(toSaveString.getBytes());
                //           fos.close();

                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
                fos.flush();
                fos.close();
                is.close();
            }
        } catch (Exception e2) {
            // TODO: handle exception
            e2.printStackTrace();
        }
    }

    public static InputStream StringTOInputStream(String in) throws Exception {
        ByteArrayInputStream is = new ByteArrayInputStream(in.getBytes("UTF-8"));
        return is;
    }

    ;


    /**
     * 　　*将错误日志文件保存到sd卡 并上传到服务器
     * 　　* @param toSaveString
     * 　　* @param filePath
     *
     * @throws Exception
     */
    public static void saveErrorFile(Throwable e, String errorname) {
        try {
            Writer info = new StringWriter();
            PrintWriter printWriter = new PrintWriter(info);
            e.printStackTrace(printWriter);
            Throwable cause = e.getCause();
            while (cause != null) {
                cause.printStackTrace(printWriter);
                cause = cause.getCause();
            }
            String toSaveString = info.toString();
            printWriter.close();
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                InputStream is = StringTOInputStream(toSaveString);
                File dir = new File(WowuApp.LOG_DIR);
                if (dir.exists() == false) {
                    boolean b = dir.mkdirs();
                }

                File file = new File(WowuApp.LOG_DIR, errorname);
                FileOutputStream fos = new FileOutputStream(file);
                //           fos.write(toSaveString.getBytes());
                //           fos.close();

                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
                fos.flush();
                fos.close();
                is.close();
            }
        } catch (Exception e2) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }


//   String methodName="getAllEmailUser";
//   Map<String, String> map0 = new HashMap<String, String>();  
//   SoapObject result=UtilsTool.getInfoFromWebService(methodName, map0);
////   Log.d("获取到的联系人结果为：：：：",result + "");  
////   Log.d("获取到的联系人结果为：getPropertyAsString：：：",result.getPropertyAsString("OrganizationVO") + "");  
//   
//   ArrayList<mailOrgan> mailOrganList = new ArrayList<mailOrgan>(); 
//   ArrayList<mailUser>  mailUserList = new ArrayList<mailUser>(); 
////   ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
//   if (result != null) {
//           int size = result.getPropertyCount();
//           if (size > 0) {
////                   Map<String, Object> map=null;
//                   for (int i = 0; i < size; i++) {
////                       SoapObject result0=(SoapObject) result.getProperty("OrganizationVO");
//                       SoapObject result0=(SoapObject) result.getProperty(i);
////                       Log.d("获取到的联系人结果为：result0：",result0 + "");  
//                       
////                       map=new HashMap<String, Object>();
////                       map.put("OrganizationVO", so.getProperty(i).toString());
//                       
////                       Log.d("部门为：Id：",result0.getProperty("Id") + ""); 
////                       Log.d("部门为：Name：",result0.getProperty("Name") + ""); 
////                       Log.d("获取到的联系人结果为：Remark：",result0.getProperty("Remark") + "");   
//                       
//                       mailOrgan tempOrgan=new mailOrgan(); 
//                       tempOrgan.setOrganId(result0.getProperty("Id")+ "");
//                       tempOrgan.setOrganName(result0.getProperty("Name") + ""); 
//                       
//                       SoapObject children=(SoapObject) result0.getProperty("children");
////                       Log.d("获取到的联系人结果为：children：",children + "");  
//                       ArrayList<mailUser>  tempmailUserList = new ArrayList<mailUser>(); 
//                     for (int j = 0; j < children.getPropertyCount(); j++) {                                    
//                         SoapObject UserVO=(SoapObject) children.getProperty(j); 
////                         Log.d("获取到的联系人结果为：UserVO.Id："+i+j+": ","   "+UserVO.getProperty("Id") + ""); 
////                         Log.d("获取到的联系人结果为：UserVO.Name："+i+j+": ","   "+UserVO.getProperty("Name") + ""); 
//////                       Log.d("获取到的联系人结果为：UserVO.RegisterDate："+i+j+": ",UserVO.getProperty("RegisterDate") + ""); 
//                           
//                         mailUser tempmailUser=new mailUser();
//                         tempmailUser.setId(UserVO.getProperty("Id") + "");
//                         tempmailUser.setName(UserVO.getProperty("Name") + ""); 
//                         tempmailUserList.add(tempmailUser);
//                         mailUserList.add(tempmailUser);
//                     }   
//                     tempOrgan.setMailUserList(tempmailUserList);
//                     mailOrganList.add(tempOrgan); 
//                   }
//            }
//   }
// SoapObject result0=(SoapObject) result.getProperty("OrganizationVO");
// Log.d("获取到的联系人结果为：result0：",result0 + "");  
// 
// 
// SoapObject children=(SoapObject) result0.getProperty("children");
// Log.d("获取到的联系人结果为：children：",children + "");  
// 
// SoapObject UserVO=(SoapObject) children.getProperty("anyType");
// Log.d("获取到的联系人结果为：UserVO：",UserVO + "");  
// 
//// SoapObject childs = (SoapObject) result.getProperty(0); 
//// 
////// Log.d("获取到的联系人结果为：childs：",childs + "");  
////// Log.d("获取到的联系人结果为：parseSoapObjectToList(childs)：",parseSoapObjectToList(childs) + "");   
//// 
////  
//// String detail = (String) result.getPropertyAsString("UserVO");
//// Log.d("获取到的联系人结果为：UserVO：String",detail + "");  
//   


//   private String connServerForResult(String url) {  
//       String strResult = "";  
//       try {  
//           HttpClient httpClient = new DefaultHttpClient();  
//           HttpPost httpPost = new HttpPost();  
//           httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");  
//           httpPost.setURI(new URI(url));  
//           //httpPost,通过urlParam添加请求参数  
//           List < NameValuePair > urlParam = new ArrayList < NameValuePair > ();  
//     
//           //中文内容，通过URLDecoder.decode转码  
//           urlParam.add(new BasicNameValuePair("username", URLDecoder.decode(username.getText().toString(), "utf-8")));  
//           urlParam.add(new BasicNameValuePair("password", URLDecoder.decode(password.getText().toString(), "utf-8")));  
//           httpPost.setEntity(new UrlEncodedFormEntity(urlParam, HTTP.UTF_8));  
//           HttpResponse response = httpClient.execute(httpPost);  
//           HttpEntity entity = response.getEntity();  
//           if (entity != null)  
//               strResult = EntityUtils.toString(entity, HTTP.UTF_8);  
//     
//       } catch (Exception e) {  
//           //  
//       }  
//       return strResult;  
//   }  


    // 将Bitmap转换成字符串
    public static String bitmaptoString(Bitmap bitmap) {

        String string = null;
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bStream);
        byte[] bytes = bStream.toByteArray();
        string = Base64.encodeToString(bytes, Base64.DEFAULT);

//        Log.i("获取到的图片大小为　２：：：：：",":　"+string);
//        saveStringToSD(string);

        return string;
    }

    public static void saveStringToSD   (String toSaveString) {

        try {
            Writer info = new StringWriter();


            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                InputStream is = StringTOInputStream(toSaveString);
                File dir = new File(WowuApp.LOG_DIR);
                if (dir.exists() == false) {
                    boolean b = dir.mkdirs();
                }

                File file = new File(WowuApp.LOG_DIR, "64返回的图片.txt");
                FileOutputStream fos = new FileOutputStream(file);
                //           fos.write(toSaveString.getBytes());
                //           fos.close();

                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
                fos.flush();
                fos.close();
                is.close();
            }
        } catch (Exception e2) {
            // TODO: handle exception
            e2.printStackTrace();
        }

    }


    public static String compressBitmap(Bitmap bitmap, float size) {
        String string = null;


        Log.i("获取到的图片大小为　１：：：：：",":　"+getSizeOfBitmap(bitmap) );
        if (bitmap == null || getSizeOfBitmap(bitmap) <= size) {
            return null;//如果图片本身的大小已经小于这个大小了，就没必要进行压缩
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);//如果签名是png的话，则不管quality是多少，都不会进行质量的压缩
        int quality = 100;
        while (baos.toByteArray().length / 1024f > size) {
            quality = quality - 4;// 每次都减少4
            baos.reset();// 重置baos即清空baos
            if (quality <= 0) {
                break;
            }
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        }
        byte[] bytes = baos.toByteArray();
        string = Base64.encodeToString(bytes, Base64.DEFAULT);

        Log.i("获取到的图片大小为　２：：：：：",":　"+string.length());

        return string;
    }

    private static float getSizeOfBitmap(Bitmap bitmap) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);//这里100的话表示不压缩质量
        return baos.toByteArray().length / 1024;//读出图片的kb大小
    }


    // 将字符串转换成Bitmap类型
    public static Bitmap stringtoBitmap(String string) {
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0,
                    bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }


    /**
     * 隐藏键盘
     * @param mContext
     */
    public static void hideSoftKeyboard(Activity mContext ) {
        InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if ( mContext.getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (mContext.getCurrentFocus() != null)
                inputMethodManager.hideSoftInputFromWindow(mContext.getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    //图片压缩
    public static byte[] bmpToByteArray(final Bitmap bmp,
                                        final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        int WX_THUMB_SIZE = 120;
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, WX_THUMB_SIZE, WX_THUMB_SIZE, true);
        bmp.recycle();

        thumbBmp.compress(Bitmap.CompressFormat.PNG, 80, output);
        if (needRecycle) {
            thumbBmp.recycle();
        }
        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}

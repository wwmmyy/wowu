package com.wuwo.im.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.wuwo.im.util.UtilsTool;

import im.imxianzhi.com.imxianzhi.R;

public class WebviewDetailActivity extends BaseActivity {
 
    private Context mContext=WebviewDetailActivity.this;
    private String url ;
    private String titlename;
    private  boolean  fujian=false;
    private String dataInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO 自动生成的方法存根
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//            WindowManager.LayoutParams.FLAG_FULLSCREEN);// 去掉信息栏
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_webview_detail);
    
    
    /*获取Intent中的Bundle对象*/
//    Bundle bundle = this.getIntent().getExtras();
//    if(bundle!=null){
        url = this.getIntent().getStringExtra("url");
        titlename = this.getIntent().getStringExtra("titlename");
//    }

        //Log.d("调用的formId js  网页接收到的参数url为", url); 

        //Log.d("调用的formId js  网页接收到的参数projectId：formId为",projectId+":"+formId); 



        initTopView();
        initWebview();
    }

    /**
     * @Title: initTopView
     * @Description: 初始化顶部按钮功能
     * @param
     * @return void
     * @throws
     */
    public void initTopView() {

        TextView top_title_name=(TextView) findViewById(R.id.top_title);
        if(titlename!=null&& !titlename.equals("")){
            top_title_name.setText(titlename);
        }
        findViewById(R.id.return_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO 自动生成的方法存根
                if(pd!=null&&pd.isShowing()){
                    pd.dismiss();
                }
                WebviewDetailActivity.this.finish();
                overridePendingTransition(0, R.anim.slide_out_to_left);
            }
        });


    }



    ProgressDialog pd;
    WebView newsdetail_webview;
    /**
     * 初始化webview 
     */
    private void initWebview() {
        // TODO 自动生成的方法存根
        newsdetail_webview=(WebView) findViewById(R.id.news_detail_web_view);
        newsdetail_webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);//允许js弹出窗口
        newsdetail_webview.requestFocus();
        newsdetail_webview.getSettings().setAllowFileAccess(true);//设置允许访问文件数据
        newsdetail_webview.setInitialScale(50);//为25%，最小缩放等级
        newsdetail_webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message,android.webkit.JsResult result) {
                //Required functionality here
                return super.onJsAlert(view, url, message, result);
            }
        });
        WebSettings webSettings = newsdetail_webview.getSettings();
        webSettings.setJavaScriptEnabled(true);

//    webSettings.setJavaScriptCanOpenWindowsAutomatically(true);//允许js弹出窗口
        webSettings.setDatabaseEnabled(true);
        webSettings.setDomStorageEnabled(true);
//    webSettings.setBuiltInZoomControls(false);
        webSettings.setBuiltInZoomControls(true); // 设置显示缩放按钮
        webSettings.setSupportZoom(true); // 支持缩放  //启用页面的缩放
        webSettings.setUseWideViewPort(true);//设置此属性，可任意比例缩放。

//    String dir = this.getApplicationContext().getDir("database", Context.MODE_PRIVATE)
//            .getPath();
//    webSettings.setDatabasePath(dir);
//    webSettings.setGeolocationEnabled(true);
//    webSettings.setLoadWithOverviewMode(true);


//    webSettings.setLoadWithOverviewMode(true);

        //不显示webview缩放按钮
        webSettings.setDisplayZoomControls(false);

//    newsdetail_webview.setPadding(-10, 0, 20, 0);

//    newsdetail_webview.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);//宽度自适应

        // String url="http://192.168.51.117/jh";
        // String url = "http://192.168.1.239/jhgh";
        newsdetail_webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                pd= UtilsTool.initProgressDialog(mContext, "正在缓冲");
                return true;
            }
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // TODO 自动生成的方法存根
//           if(pd!=null){
//               pd.show();
//           }
                super.onPageStarted(view, url, favicon);
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                // TODO 自动生成的方法存根
                if(pd!=null && pd.isShowing()){
                    pd.dismiss();
                }


/*                newsdetail_webview.loadUrl("javascript:window.loadForm("+ DistApp.userid+","+projectId+","+formId+")");
                if(getScreenWidth(WebviewDetailActivity.this)<4.5){
                    newsdetail_webview.loadUrl("javascript:document.body.style.zoom = 0.5");
                }*/
                super.onPageFinished(view, url);
            }
        });
        newsdetail_webview.requestFocus(View.FOCUS_DOWN);
        newsdetail_webview.loadUrl(url);



//     newsdetail_webview.loadUrl("http://192.168.14.218/xayd/form.HTM");
    }

    /**
     * 获得屏幕宽度
     *
     */
    public static double getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();


//    Display display = wm.getDefaultDisplay();
//    // 屏幕宽度
//    float screenWidth = display.getWidth();


        wm.getDefaultDisplay().getMetrics(outMetrics);
        double width = Math.pow(outMetrics.widthPixels / outMetrics.xdpi, 2);

//	Log.d("获取到的屏幕尺寸为：", width+":"+screenWidth);
        return width;
    }




/*    @Override
    public void onBackPressed() {
        if(pd!=null && pd.isShowing()){
            pd.dismiss();
        }
        super.onBackPressed();
    }*/

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub  
        if(pd!=null){
            pd.dismiss();
        }
        super.onDestroy();

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
//        if(pd!=null){
//            pd.dismiss();
//        }
    } 

}

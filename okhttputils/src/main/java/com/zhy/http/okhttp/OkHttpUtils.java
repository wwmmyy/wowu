package com.zhy.http.okhttp;

import android.os.Handler;
import android.os.Looper;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.zhy.http.okhttp.builder.GetBuilder;
import com.zhy.http.okhttp.builder.PostFileBuilder;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.builder.PostStringBuilder;
import com.zhy.http.okhttp.callback.Callback;
import com.zhy.http.okhttp.https.HttpsUtils;
import com.zhy.http.okhttp.request.RequestCall;

import java.io.IOException;
import java.io.InputStream;
import java.net.CookieManager;
import java.net.CookiePolicy;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * http://blog.csdn.net/lmj623565791/article/details/49734867
*desc OkHttpUtils
*@author 王明远
*@日期： 2016/1/14 16:13
*@版权:Copyright 上海数慧系统有限公司  All rights reserved.
*/

public class OkHttpUtils
{

    public static String token = "";//用户保存登录成功后的token
    public static String serverAbsolutePath = "http://api.imxianzhi.com/";//http://139.196.85.20/     http://api.imxianzhi.com/   http://139.196.110.136:7777/
    //        GET Chat/GetUserInfo?userId={userId} 获取目标用户的信息
    public static String GetUserInfoURL = OkHttpUtils.serverAbsolutePath + "Chat/GetUserInfo" ;



    public static final long DEFAULT_MILLISECONDS = 10000;
    private static OkHttpUtils mInstance;
    private OkHttpClient mOkHttpClient;
    private Handler mDelivery;

    private OkHttpUtils()
    {
        mOkHttpClient = new OkHttpClient();
        //cookie enabled
        mOkHttpClient.setCookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ORIGINAL_SERVER));
        mDelivery = new Handler(Looper.getMainLooper());


        if (false)
        {
            mOkHttpClient.setHostnameVerifier(new HostnameVerifier()
            {
                @Override
                public boolean verify(String hostname, SSLSession session)
                {
                    return true;
                }
            });
        }


    }

    public static OkHttpUtils getInstance()
    {
        if (mInstance == null)
        {
            synchronized (OkHttpUtils.class)
            {
                if (mInstance == null)
                {
                    mInstance = new OkHttpUtils();
                }
            }
        }
        return mInstance;
    }

    public Handler getDelivery()
    {
        return mDelivery;
    }

    public OkHttpClient getOkHttpClient()
    {
        return mOkHttpClient;
    }


    public static GetBuilder get()
    {
        return new GetBuilder();
    }

    public static PostStringBuilder postString()
    {
        return new PostStringBuilder();
    }

    public static PostFileBuilder postFile()
    {
        return new PostFileBuilder();
    }

    public static PostFormBuilder post()
    {
        return new PostFormBuilder();
    }


    public void execute(final RequestCall requestCall, Callback callback)
    {
        if (callback == null)
            callback = Callback.CALLBACK_DEFAULT;
        final Callback finalCallback = callback;

        requestCall.getCall().enqueue(new com.squareup.okhttp.Callback()
        {
            @Override
            public void onFailure(final Request request, final IOException e)
            {
                sendFailResultCallback(request, e, finalCallback);
            }

            @Override
            public void onResponse(final Response response)
            {
                if (response.code() >= 400 && response.code() <= 599)
                {
                    try
                    {
//                        Log.e("服务器请求返回异常",response.toString()+"::::::::::::"+response.message()+":::"+response.body().string());

                        sendFailResultCallback(requestCall.getRequest(), new RuntimeException(response.body().string()), finalCallback);
//                        Log.e("服务器请求返回异常",response.body().string());
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    return;
                }

                try
                {
                    Object o = finalCallback.parseNetworkResponse(response);
                    sendSuccessResultCallback(o, finalCallback);
                } catch (IOException e)
                {
                    sendFailResultCallback(response.request(), e, finalCallback);
                }

            }
        });
    }


    public void sendFailResultCallback(final Request request, final Exception e, final Callback callback)
    {
        if (callback == null) return;

        mDelivery.post(new Runnable()
        {
            @Override
            public void run()
            {
                callback.onError(request, e);
                callback.onAfter();
            }
        });
    }

    public void sendSuccessResultCallback(final Object object, final Callback callback)
    {
        if (callback == null) return;
        mDelivery.post(new Runnable()
        {
            @Override
            public void run()
            {
                callback.onResponse(object);
                callback.onAfter();
            }
        });
    }

    public void cancelTag(Object tag)
    {
        mOkHttpClient.cancel(tag);
    }


    public void setCertificates(InputStream... certificates)
    {
        HttpsUtils.setCertificates(getOkHttpClient(), certificates, null, null);
    }


}


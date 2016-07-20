package com.zhy.http.okhttp.service;

import android.util.Log;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.GetBuilder;
import com.zhy.http.okhttp.builder.PostStringBuilder;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

/**
 * desc
 *
 * @author 王明远
 * @日期： 2016/6/7 23:59
 * @版权:Copyright All rights reserved.
 */

public class LoadserverdataService {
    private loadServerDataListener mLoadListener;

    public static final int GET = 0;
    public static final int POST = 1;
    public static final int POSTSTRING = 2;
    public static final int POSTFILE = 3;
    //            PostStringBuilder psBuilder=null;
//            switch (ConnectType){
//                case GET:
//                    psBuilder=  OkHttpUtils.postString();
//                     break;
//                case POST:
//                    psBuilder=  OkHttpUtils.postString();
//                    break;
//                case POSTSTRING:
//                    psBuilder=  OkHttpUtils.postString();
//                    break;
//                case POSTFILE:
//                    break;
//            }


    public LoadserverdataService(loadServerDataListener loadListener) {
        mLoadListener = loadListener;
    }


    public void loadPostJsonRequestData(final MediaType mediaType, final String url, final String JsonRequest, final int flag) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                PostStringBuilder psb = OkHttpUtils
                        .postString();
//                判断是否已经有token了，有的话在请求头加上
                if (OkHttpUtils.token != null && !OkHttpUtils.token.equals("")) {
                    psb.addHeader("Authorization", "Bearer " + OkHttpUtils.token);
                }

                psb.addHeader("content-type", "application/json")
                        .url(url)
                        .mediaType(mediaType)
                        .content(JsonRequest)
                        .build()
                        .execute(new StringCallback() {
                            @Override
                            public void onError(Request request, Exception e) {
//                                e.printStackTrace();
                                try {
                                    JSONObject responseJson = new JSONObject(e.getMessage());
                                    if (responseJson != null) {
                                        mLoadListener.loadDataFailed(responseJson.optString("Message"), flag);  //因为返回值的格式统一：{"Message":"验证码错误"}，所以可以将错误信息直接统一解析出来
                                    } else {
                                        mLoadListener.loadDataFailed("返回值异常", flag);  //{"Message":"验证码错误"}
                                    }
                                } catch (Exception e2) {
                                    e2.printStackTrace();
                                    mLoadListener.loadDataFailed(e.getMessage(), flag);  //{"Message":"验证码错误"}
                                }
                            }

                            @Override
                            public void onResponse(String response) {
                                mLoadListener.loadServerData(response.toString(), flag);
                            }
                        });
            }
        }).start();
    }


    public void loadGetJsonRequestData(final String url, final int flag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                GetBuilder psb = OkHttpUtils
                        .get();
//                判断是否已经有token了，有的话在请求头加上
                if (OkHttpUtils.token != null && !OkHttpUtils.token.equals("")) {
                    psb.addHeader("Authorization", "Bearer " + OkHttpUtils.token);
                    Log.i("添加头文件WowuApp.token：：：", OkHttpUtils.token);
                }

                psb.addHeader("content-type", "application/json")
                        .url(url)
                        .build()
                        .execute(new StringCallback() {
                            @Override
                            public void onError(Request request, Exception e) {
                                e.printStackTrace();
                                Log.i("失败返回值为0", request.toString() + "xxxx");
//                                mLoadListener.loadDataFailed(request.toString(), flag);
                                try {
                                    JSONObject responseJson = new JSONObject(e.getMessage());
                                    if (responseJson != null) {
                                        mLoadListener.loadDataFailed(responseJson.optString("Message"), flag);  //因为返回值的格式统一：{"Message":"验证码错误"}，所以可以将错误信息直接统一解析出来
                                    } else {
                                        mLoadListener.loadDataFailed("返回值异常", flag);  //{"Message":"验证码错误"}
                                    }
                                } catch (Exception e2) {
                                    e2.printStackTrace();
                                    mLoadListener.loadDataFailed(e.getMessage(), flag);  //{"Message":"验证码错误"}
                                }
                            }

                            @Override
                            public void onResponse(String response) {
                                Log.i("成功返回值为", response + "xxxx");
                                mLoadListener.loadServerData(response.toString(), flag);
                            }
                        });
            }
        }).start();
    }








}

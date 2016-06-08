package com.wuwo.im.service;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

/**
 * desc
 *
 * @author 王明远
 * @日期： 2016/6/7 23:59
 * @版权:Copyright   All rights reserved.
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


    public void loadPostJsonRequestData ( MediaType mediaType,String url, String JsonRequest) {
            OkHttpUtils
                    .postString()
                    .addHeader("content-type", "application/json")
                    .url(url)
                    .mediaType(mediaType)
                    .content(JsonRequest)
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Request request, Exception e) {
                            e.printStackTrace();
                            mLoadListener.loadDataFailed(request.toString());
                        }
                        @Override
                        public void onResponse(String response) {
                            mLoadListener.loadServerData(response.toString());
                        }
                    });
    }


}

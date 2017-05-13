package com.wuwo.im.util;

import android.net.Uri;

/**
 * 图片剪裁回调
 * Created by linzb on 16-5-3.
 */
public interface PhotoCropCallBack {
    void onFailed(String message);

    void onPhotoCropped(Uri uri);
}
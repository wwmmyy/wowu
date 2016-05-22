package com.wuwo.im.view;

import android.content.Context;
import android.graphics.drawable.Drawable;

/**
 * @author yangyu
 *         功能描述：弹窗内部子类项（绘制标题和图标）
 */
public class ActionItem {
    //定义图片对象
    public Drawable mDrawable;
    //定义文本对象
    public CharSequence mTitle;

    public  int color;

    public ActionItem(Drawable drawable, CharSequence title, int color) {
        this.mDrawable = drawable;
        this.mTitle = title;
        this.color=color;
    }

    public ActionItem(Context context, int titleId, int drawableId, int color) {
        this.mTitle = context.getResources().getText(titleId);
        this.color=color;
        this.mDrawable = context.getResources().getDrawable(drawableId);
    }

    public ActionItem(Context context, CharSequence title, int drawableId, int color) {
        this.mTitle = title;
        this.color=color;
        this.mDrawable = context.getResources().getDrawable(drawableId);
    }
}

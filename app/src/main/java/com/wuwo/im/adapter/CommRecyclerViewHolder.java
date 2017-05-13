package com.wuwo.im.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

/**
*desc 
*@author 王明远
*@日期： 2016/2/3 17:07
*@版权:Copyright    All rights reserved.
*/

public class CommRecyclerViewHolder extends RecyclerView.ViewHolder {
    private SparseArray<View> mView;
    private int position;
    private int itemType;
    private ImageLoader imageLoader;

    public CommRecyclerViewHolder(View itemView, ViewGroup parent, int itemType) {
        super(itemView);
        this.itemType = itemType;
        this.mView = new SparseArray<View>();
        imageLoader = ImageLoader.getInstance();
    }

    public static  CommRecyclerViewHolder getCommRecyclerViewHolder(Context context, ViewGroup parent, int layoutId, int itemType) {
        CommRecyclerViewHolder holder = new CommRecyclerViewHolder(LayoutInflater.from(context).inflate(layoutId, parent, false), parent, itemType);

        return holder;
    }

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int mItemType) {
          itemType=mItemType;
    }




    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * 通过ViewId获取控件
     *
     * @param viewId
     * @param <T>
     * @return
     */
    public <T extends View> T getView(int viewId) {
        View view = mView.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            mView.put(viewId, view);
        }

        return (T) view;

    }

    public View getConvertView() {
        return itemView;
    }

    public View getItemView() {
        return itemView;
    }

    public CommRecyclerViewHolder setText(int viewId, String content) {
        TextView textView = getView(viewId);
        textView.setText(content);
        return this;
    }

    public CommRecyclerViewHolder setText(int viewId, CharSequence content) {
        TextView textView = getView(viewId);
        textView.setText(content);
        return this;
    }



}
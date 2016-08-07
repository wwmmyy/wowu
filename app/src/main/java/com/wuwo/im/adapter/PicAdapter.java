package com.wuwo.im.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wuwo.im.bean.UserInfoDetail;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import im.wuwo.com.wuwo.R;

/**
 * desc
 *
 * @author 王明远
 * @日期： 2016/5/29 19:19
 * @版权:Copyright 上海数慧系统有限公司  All rights reserved.
 */

public class PicAdapter extends RecyclerView.Adapter<PicAdapter.ViewHolder> {

    private Context mContext;
    private List<UserInfoDetail.PhotosBean> dataList;

    public List<UserInfoDetail.PhotosBean> getDataList() {
        return dataList;
    }


    public interface OnItemClickLitener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }
    public void setData(List<UserInfoDetail.PhotosBean> list) {
        this.dataList.addAll(list);
        notifyDataSetChanged();
    }


    public PicAdapter(Context context, List<UserInfoDetail.PhotosBean> dataList) {
        if(dataList==null){
            this.dataList=new ArrayList<UserInfoDetail.PhotosBean>();
        }else{
            this.dataList = dataList;
        }

        mContext = context;
    }
    public void removeItem(int position) {
        if (position >= 0 && position <  dataList.size()) {
            dataList.remove(position);
            notifyItemRemoved(position);
        }
    }


    public Object getItem(int position) {
        if (position > dataList.size() - 1) {
            return null;
        }
        return dataList.get(position);
    }

    //清除所有数据
    public void clear() {
         dataList.clear();
        notifyDataSetChanged();
    }

    public void addItem( UserInfoDetail.PhotosBean data) {
        dataList.add(data);
        notifyDataSetChanged();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        //        public TextView contact_phone,contact_user;
        SimpleDraweeView portal_news_img;

        public ViewHolder(View itemView) {
            super(itemView);
            portal_news_img = (SimpleDraweeView) itemView.findViewById(R.id.user_info_pic);
        }
    }
    public List<UserInfoDetail.PhotosBean> getList() {
        return dataList;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_userpic_recycler, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if(position==dataList.size()){
            holder.portal_news_img.setImageResource(R.drawable.icon_addpic_focused);
        }else{
            if(dataList.get(position).getFullUrl() != null){
                holder.portal_news_img.setImageURI(Uri.parse(dataList.get(position).getFullUrl()));
            }else{
                File photo = new File(dataList.get(position).getLocalPath());
                Uri  imageUri = Uri.fromFile(photo);
                holder.portal_news_img.setImageURI(imageUri);
            }
        }


        // 如果设置了回调，则设置点击事件
        if (mOnItemClickLitener != null) {
            final ViewHolder tempholder = holder;
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = tempholder.getLayoutPosition();
                    mOnItemClickLitener.onItemClick(tempholder.itemView, pos);
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = tempholder.getLayoutPosition();
                    mOnItemClickLitener.onItemLongClick(tempholder.itemView, pos);
                    return false;
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return dataList.size()+1;
    }

}
package com.wuwo.im.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
*desc
*@author 王明远
*@日期： 2016/2/3 17:08
*@版权:Copyright    All rights reserved.
*/
public abstract class CommRecyclerAdapter<T> extends RecyclerView.Adapter<CommRecyclerViewHolder> {
    protected Context mContext;
    protected List<T> mdate;
    /**
     * Item的点击事件
     */
    protected OnItemClickListener onItemClick;

    protected  int mItemLayoutId;

    public CommRecyclerAdapter(Context context,int itemLayoutId) {
        mContext = context;
        mdate = new ArrayList();
        this.mItemLayoutId = itemLayoutId;
    }

    public void setData(List<T> list) {
        if(list!=null){
            this.mdate.addAll(list);
        }
        notifyDataSetChanged();
    }

    public void setData2(List<T> list) {
        this.mdate=list;
        notifyDataSetChanged();
    }


    public void addItem(T date) {
        mdate.add(date);
        notifyDataSetChanged();
    }

    public void addItem(T date, int positon) {
        mdate.add(positon, date);
        notifyItemInserted(positon);
    }

    public List<T> getData() {
        return mdate;
    }

    public void removeItem(int positon) {
        mdate.remove(positon);
        notifyItemRemoved(positon);
    }

    public void clearDate() {
        mdate.clear();
        notifyDataSetChanged();
    }

    public OnItemClickListener getOnItemClick() {
        return onItemClick;
    }

    public void setOnItemClick(OnItemClickListener onItemClick) {
        this.onItemClick = onItemClick;
    }

    @Override
    public CommRecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        CommRecyclerViewHolder holder = CommRecyclerViewHolder.getCommRecyclerViewHolder(mContext, viewGroup,mItemLayoutId, i);
        holder.getConvertView().setOnClickListener(onClickListener);
        holder.getConvertView().setTag(holder);
        return holder;
    }

    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (onItemClick!=null){
                CommRecyclerViewHolder holder = (CommRecyclerViewHolder) v.getTag();
                onItemClick.onItemClick(v,holder.getPosition());
            }
        }
    };

    @Override
    public void onBindViewHolder(CommRecyclerViewHolder commRecyclerViewHolder, int i) {
        convert(commRecyclerViewHolder, mdate.get(i));
    }

    public abstract void convert(CommRecyclerViewHolder viewHolder, T t);

    @Override
    public int getItemCount() {
        return mdate.size();
    }

    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }

}
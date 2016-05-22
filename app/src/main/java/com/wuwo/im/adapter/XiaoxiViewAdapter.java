package com.wuwo.im.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wuwo.im.bean.newsMessage;

import java.util.List;

import im.wuwo.com.wuwo.R;

/**
 * Created by WuXiaolong on 2015/7/2.
 */
public class XiaoxiViewAdapter extends RecyclerView.Adapter<XiaoxiViewAdapter.ViewHolder> {

    private Context mContext;
    private List<newsMessage> dataList;

    public List<newsMessage> getDataList() {
        return dataList;
    }


    public interface OnItemClickLitener
    {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }
    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener)
    {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    public XiaoxiViewAdapter(Context context, List<newsMessage> dataList) {
        this.dataList = dataList;
        mContext = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public SimpleDraweeView news_label_pic;


        public ViewHolder(View itemView) {
            super(itemView);

            /*//实现点击响应监听的
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });*/

            title = (TextView) itemView.findViewById(R.id.title);
            news_label_pic = (SimpleDraweeView) itemView.findViewById(R.id.news_label_pic);

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_xiaoxi_view, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
//        holder.title.setText(dataList.get(position).getTitle());
        holder. news_label_pic.setImageURI(Uri.parse("http://www.gog.com.cn/pic/0/10/91/11/10911138_955870.jpg"));

        // 如果设置了回调，则设置点击事件
        if (mOnItemClickLitener != null)
        {
           final  ViewHolder tempholder=holder;
            holder.itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    int pos = tempholder.getLayoutPosition();
                    mOnItemClickLitener.onItemClick(tempholder.itemView, pos);
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View v)
                {
                    int pos = tempholder.getLayoutPosition();
                    mOnItemClickLitener.onItemLongClick(tempholder.itemView, pos);
                    return false;
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

}
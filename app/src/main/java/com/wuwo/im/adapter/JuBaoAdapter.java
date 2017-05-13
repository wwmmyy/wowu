package com.wuwo.im.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import im.imxianzhi.com.imxianzhi.R;


public class JuBaoAdapter extends RecyclerView.Adapter<JuBaoAdapter.ViewHolder> {

    private Context mContext;
    private List<String> dataList;

    int defaultSelectPos = -1;

    public List<String> getDataList() {
        return dataList;
    }

    public void setDefaultSelectPos(int defaultSelectPos) {
        this.defaultSelectPos = defaultSelectPos;
        notifyDataSetChanged();
    }

    public interface OnItemClickLitener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;

    }

    public JuBaoAdapter(Context context, List<String> dataList) {
        this.dataList = dataList;
        mContext = context;
    }

    public void setData2(List<String> list) {
        this.dataList = list;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_jubao_info;


        public ViewHolder(View itemView) {
            super(itemView);

            /*//实现点击响应监听的
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });*/

            tv_jubao_info = (TextView) itemView.findViewById(R.id.tv_jubao_info);

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_simple_jubao_list, parent, false);


        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.tv_jubao_info.setText(dataList.get(position));


        if (defaultSelectPos == position) {
            holder.itemView.setBackgroundResource(R.drawable.card_light_press);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.item_background_selector_light);
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

//            holder.itemView.setOnLongClickListener(new View.OnLongClickListener()
//            {
//                @Override
//                public boolean onLongClick(View v)
//                {
//                    int pos = tempholder.getLayoutPosition();
//                    mOnItemClickLitener.onItemLongClick(tempholder.itemView, pos);
//                    return false;
//                }
//            });
        }


    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

}
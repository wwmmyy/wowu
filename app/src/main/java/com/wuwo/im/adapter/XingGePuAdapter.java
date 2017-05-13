package com.wuwo.im.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wuwo.im.bean.CharacterPu;

import java.util.ArrayList;

import im.imxianzhi.com.imxianzhi.R;


public class XingGePuAdapter extends RecyclerView.Adapter<XingGePuAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<CharacterPu> dataList;
    int defaultSelectPos = -1;

    public ArrayList<CharacterPu> getDataList() {
        return dataList;
    }

    public void setDefaultSelectPos(int defaultSelectPos) {
        this.defaultSelectPos = defaultSelectPos;
        notifyDataSetChanged();
    }

    public interface onViewClickListener {
        void onViewClick(View view, int type, int mposition);
    }

    private onViewClickListener onViewClickListener;

    public void setOnViewClickListener(onViewClickListener monViewClickListener) {
        this.onViewClickListener = monViewClickListener;

    }

    public XingGePuAdapter(Context context, ArrayList<CharacterPu> dataList) {
        this.dataList = dataList;
        mContext = context;
    }

    public void setData2(ArrayList<CharacterPu> list) {
        this.dataList = list;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout rt_part1;
        public RelativeLayout rt_part1_1;
        public RelativeLayout rt_part1_0;
        public TextView rt_part1_1_0, rt_part1_1_1, rt_part1_0_2, rt_part1_0_0, rt_part1_0_1;


        public ViewHolder(View itemView) {
            super(itemView);
            rt_part1 = (RelativeLayout) itemView.findViewById(R.id.rt_part1);
            rt_part1_0 = (RelativeLayout) itemView.findViewById(R.id.rt_part1_0);
            rt_part1_1 = (RelativeLayout) itemView.findViewById(R.id.rt_part1_1);

            rt_part1_1_0 = (TextView) itemView.findViewById(R.id.rt_part1_1_0);
            rt_part1_1_1 = (TextView) itemView.findViewById(R.id.rt_part1_1_1);
            rt_part1_0_2 = (TextView) itemView.findViewById(R.id.rt_part1_0_2);
            rt_part1_0_0 = (TextView) itemView.findViewById(R.id.rt_part1_0_0);
            rt_part1_0_1 = (TextView) itemView.findViewById(R.id.rt_part1_0_1);

        }
    }

    @Override
    public int getItemViewType(int position) {
//        return super.getItemViewType(position);
        return position;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = null;
        switch (viewType) {
            case 0:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_xinggepu_cardview1, parent, false);
                break;
            case 1:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_xinggepu_cardview2, parent, false);
                break;
            case 2:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_xinggepu_cardview3, parent, false);
                break;
            case 3:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_xinggepu_cardview4, parent, false);
                break;
        }
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // 如果设置了回调，则设置点击事件
        if (dataList != null && dataList.size() > 0) {
            final ViewHolder tempholder = holder;
            if(position >1){
                holder.rt_part1_1_1.setText(dataList.get(position).getCode() + "\n" + dataList.get(position).getPosition() );
            }else{
                holder.rt_part1_1_1.setText(dataList.get(position).getPosition() + "\n" + dataList.get(position).getCode());
            }

            holder.rt_part1_1_0.setText(dataList.get(position ).getFunctionLevel()+"");
            holder.rt_part1_0_0.setText(dataList.get(position + 4).getPosition());
            holder.rt_part1_0_1.setText(dataList.get(position + 4).getCode());
            holder.rt_part1_0_2.setText(dataList.get(position + 4).getFunctionLevel()+"");


            holder.rt_part1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = tempholder.getLayoutPosition();
                    if (onViewClickListener != null) {
                        onViewClickListener.onViewClick(v, 0, pos+4);
                    }
                }
            });

            holder.rt_part1_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                tempholder.getAdapterPosition()
                    int pos = tempholder.getLayoutPosition();
                    if (onViewClickListener != null) {
                        onViewClickListener.onViewClick(v, 1, pos);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
//        return dataList.size();
        if(dataList == null || dataList.size()==0 ){
            return 0;
        }
        return 4;
    }

}
//package com.wuwo.im.adapter;
//
//import android.content.Context;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import com.wuwo.im.bean.Contact;
//
//import java.util.List;
//
//import im.wuwo.com.wuwo.R;
//
///**
//*desc
//*@author 王明远
//*@日期： 2016/5/29 19:19
//*@版权:Copyright 上海数慧系统有限公司  All rights reserved.
//*/
//
//public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {
//
//    private Context mContext;
//    private List<Contact> dataList;
//
//    public List<Contact> getDataList() {
//        return dataList;
//    }
//
//
//    public interface OnItemClickLitener
//    {
//        void onItemClick(View view, int position);
//        void onItemLongClick(View view, int position);
//    }
//    private OnItemClickLitener mOnItemClickLitener;
//
//    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener)
//    {
//        this.mOnItemClickLitener = mOnItemClickLitener;
//    }
//
//    public ContactAdapter(Context context, List<Contact> dataList) {
//        this.dataList = dataList;
//        mContext = context;
//    }
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//        public TextView contact_phone,contact_user;
//
//        public ViewHolder(View itemView) {
//            super(itemView);
//            contact_phone = (TextView) itemView.findViewById(R.id.contact_phone);
//            contact_user = (TextView) itemView.findViewById(R.id.contact_user);
//
//
//        }
//    }
//
//    @Override
//    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_phonecontact_list, parent, false);
//        return new ViewHolder(v);
//    }
//
//    @Override
//    public void onBindViewHolder(ViewHolder holder, final int position) {
////        holder.title.setText(dataList.get(position).getTitle());
//        holder. contact_phone.setText(dataList.get(position).getContactName());
//        holder. contact_user.setText(dataList.get(position).getPhoneNumber());
//
//        // 如果设置了回调，则设置点击事件
//        if (mOnItemClickLitener != null)
//        {
//           final  ViewHolder tempholder=holder;
//            holder.itemView.setOnClickListener(new View.OnClickListener()
//            {
//                @Override
//                public void onClick(View v)
//                {
//                    int pos = tempholder.getLayoutPosition();
//                    mOnItemClickLitener.onItemClick(tempholder.itemView, pos);
//                }
//            });
//
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
//        }
//
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return dataList.size();
//    }
//
//}
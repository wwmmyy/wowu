/*
 * Copyright 2016 Yan Zhenjie
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wuwo.im.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wuwo.im.bean.HistoryCharacterTest;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import im.imxianzhi.com.imxianzhi.R;

/**
 * Created by YOLANDA on 2016/7/22.
 */
public class MenuAdapter extends SwipeMenuAdapter<MenuAdapter.DefaultViewHolder> {

    //    private List<String> titles;
    ArrayList<HistoryCharacterTest.DataBean> historyCharacterTestList = new ArrayList<HistoryCharacterTest.DataBean>(); //记录所有的最新消息
    private OnItemClickListener mOnItemClickListener;
    //用于记录当前点击添加的好友的id
//    String currentClickfriendId = null;
//    int CurrentSelect = 0;
//    ImageView lastView = null;

    String lastSelectedId = null;  //上一个选中的
    int lastSelectedType = 0;  //上一个选中的
    String lastIntro = null;  //上一个选中的性格简介


//    public MenuAdapter(List<String> titles) {
//        this.titles = titles;
//    }

    public MenuAdapter(ArrayList<HistoryCharacterTest.DataBean> historyCharacterTestList) {

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return historyCharacterTestList == null ? 0 : historyCharacterTestList.size();
    }

    public String getLastSelectedId() {
        return lastSelectedId;
    }

    public int getLastSelectedType() {
        return lastSelectedType;
    }

    public void setLastSelectedType(int lastSelectedType) {
        this.lastSelectedType = lastSelectedType;
    }

    public String getLastIntro() {
        return lastIntro;
    }

    @Override
    public View onCreateContentView(ViewGroup parent, int viewType) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.item_character_testhistory, parent, false);
    }

    @Override
    public DefaultViewHolder onCompatCreateViewHolder(View realContentView, int viewType) {
        return new DefaultViewHolder(realContentView);
    }

    @Override
    public void onBindViewHolder(DefaultViewHolder holder, final int position) {
        holder.setData(historyCharacterTestList.get(position));
        holder.setOnItemClickListener(mOnItemClickListener);

        holder.iv_character_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                    lastSelectedId
                if (historyCharacterTestList.get(position).getId().equals(lastSelectedId)) {
                    return;
                }
                int tempSize = historyCharacterTestList.size();
                for (int i = 0; i < tempSize; i++) {
                    HistoryCharacterTest.DataBean tempDataBean = historyCharacterTestList.get(i);
                    if (tempDataBean.getId().equals(lastSelectedId)) {
                        historyCharacterTestList.remove(i);
                        tempDataBean.setSelectState(false);
                        historyCharacterTestList.add(i, tempDataBean);
                        continue;
                    }
                    if (historyCharacterTestList.get(position).getId().equals(tempDataBean.getId())) {
                        historyCharacterTestList.remove(i);
                        tempDataBean.setSelectState(true);
                        historyCharacterTestList.add(i, tempDataBean);
                    }
                }
                setData2(historyCharacterTestList);
                lastSelectedId = historyCharacterTestList.get(position).getId();
                lastSelectedType = historyCharacterTestList.get(position).getQuestionType();
                lastIntro =historyCharacterTestList.get(position).getDispositionName();

            }
        });


    }

    public ArrayList<HistoryCharacterTest.DataBean> getHistoryCharacterTestList() {
        return historyCharacterTestList;
    }


    public void setData2(ArrayList<HistoryCharacterTest.DataBean> list) {
        this.historyCharacterTestList = list;
        notifyDataSetChanged();
    }

    public void setData(ArrayList<HistoryCharacterTest.DataBean> loadInfo) {
        if (loadInfo != null) {
            this.historyCharacterTestList.addAll(loadInfo);
        }
        notifyDataSetChanged();
    }


    static class DefaultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        String var_year = "yyyy";
        String var_month_day = "MM/dd ";
        SimpleDateFormat format_year = new SimpleDateFormat(var_year);//, Locale.ENGLISH
        SimpleDateFormat format_month_day = new SimpleDateFormat(var_month_day);//, Locale.ENGLISH
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        OnItemClickListener mOnItemClickListener;
        TextView tvTitle, tv_label_time, tv_label_time0,tv_test_history;
        ImageView character_type, iv_character_set;

        public DefaultViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tvTitle = (TextView) itemView.findViewById(R.id.contract_title);
            tv_label_time = (TextView) itemView.findViewById(R.id.tv_label_time);
            tv_label_time0 = (TextView) itemView.findViewById(R.id.tv_label_time0);
            tv_test_history = (TextView) itemView.findViewById(R.id.tv_test_history);
            character_type = (ImageView) itemView.findViewById(R.id.character_type);
            iv_character_set = (ImageView) itemView.findViewById(R.id.iv_character_set);
        }


        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.mOnItemClickListener = onItemClickListener;
        }

        public void setData(  HistoryCharacterTest.DataBean dataBean) {
            this.tvTitle.setText(dataBean.getDispositionName());
            try {
                Date date = sdf.parse(dataBean.getTime());
                this.tv_label_time.setText(format_month_day.format(date));
                this.tv_label_time0.setText(format_year.format(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            switch (dataBean.getQuestionType()) {
                case 0:
                    this.character_type.setImageResource(R.drawable.choose_2);
                    tv_test_history.setVisibility(View.GONE);
                    break;
                case 1:
                    this.character_type.setImageResource(R.drawable.kuaisu);
                    tv_test_history.setVisibility(View.GONE);
                    break;
                case 2:
                    this.character_type.setImageResource(R.drawable.jinque);
                    tv_test_history.setText(dataBean.getPropensityScore() + "%");
                    tv_test_history.setVisibility(View.VISIBLE);
                    break;
            }
            iv_character_set.setImageResource(dataBean.isSelectState() == true ? R.drawable.select : R.drawable.normal);
//            iv_character_set.setImageResource(R.drawable.select);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(getAdapterPosition());
            }
        }
    }

}

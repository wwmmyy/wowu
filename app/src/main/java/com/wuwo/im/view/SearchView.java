package com.wuwo.im.view;


import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import im.wuwo.com.wuwo.R;


public class SearchView extends RelativeLayout {

    private RelativeLayout mContainer;
    //  用于快速搜索
    private ProgressBar mprogressBar1;//进度条
    private EditText metSearch;//快速搜索 EditText
    private ImageView mimgBtnClear;//快速搜索清空按钮

    public SearchView(Context context) {
        super(context);
    }

    public SearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContainer = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.view_search_layout, this);
        metSearch = (EditText) findViewById(R.id.etSearch);
        metSearch.setText("");
        mimgBtnClear = (ImageView) findViewById(R.id.imgBtnClear);
        mprogressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
         /*ImageButton imgBtnSearch = (ImageButton) view.findViewById(R.id.imgBtnSearch);
         imgBtnSearch.requestFocus(); */
        setListener();
    }


    /**
     * 布局头部文件快速搜索框的监听
     */
    private void setListener() {//
        mimgBtnClear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                metSearch.setText("");
            }
        });
        metSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mimgBtnClear.setVisibility(View.GONE);
                mprogressBar1.setVisibility(View.VISIBLE);
                if (mSearchListener != null) {
                    mSearchListener.searchInfo(metSearch.getText().toString().trim());
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

    }


    public void setSearchListener(searchListener mSearchListener) {
        this.mSearchListener = mSearchListener;
    }

    private searchListener mSearchListener;

    /**
     * 若 搜索信息为空则将搜索框重置,否则将现有的显示进度条隐藏，显示搜索结果叉号
     */
    public boolean resertSearch() {
        mprogressBar1.setVisibility(View.GONE);
        if (metSearch.getText().toString().trim().equals("")) {
            mimgBtnClear.setVisibility(View.GONE);
            return true;
        } else {
            mimgBtnClear.setVisibility(View.VISIBLE);
            return false;
        }
    }


    public interface searchListener {
        public void searchInfo(String info);
    }

    public String getSearchInfo() {

        return metSearch.getText().toString();
    }

    public void setSearchInfo(String info) {

        metSearch.setText(info);
    }


}


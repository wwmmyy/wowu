package com.wuwo.im.fragement;


import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import im.imxianzhi.com.imxianzhi.R;

/**
 *性格谱弹出框页面侧滑
 * CharacterPuPopFragment splashViewPagerFragment=CharacterPuPopFragment.newInstance("url","测试");
 */
public class CharacterPuPopFragment extends BaseAppFragment {

    String url;
    String text;
    String title;
    public CharacterPuPopFragment() {
        // Required empty public constructor
    }

    public static final CharacterPuPopFragment newInstance( String url,String text,String title)
    {
        CharacterPuPopFragment fragment = new CharacterPuPopFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url",url);
        bundle.putString("text", text);
        bundle.putString("title", title);
        fragment.setArguments(bundle);

        return fragment ;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        url=getArguments().getString("url");
        text=getArguments().getString("text");
        title=getArguments().getString("title");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragement_character_xingge_pop, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {

        SimpleDraweeView character_label_pic = (SimpleDraweeView) view.findViewById(R.id.character_label_pic);
       if(url!=null && !url.equals("")){
           character_label_pic.setImageURI(Uri.parse(url));
       }

        TextView tv_character_pop_title = (TextView) view.findViewById(R.id.tv_character_pop_title);
        tv_character_pop_title.setText(title);


        TextView tv_character_part1 = (TextView) view.findViewById(R.id.tv_character_part);
        tv_character_part1.setText(text);

    }


    @Override
        public String getFragmentName() {
            return "CharacterPuPopFragment";
        }

}

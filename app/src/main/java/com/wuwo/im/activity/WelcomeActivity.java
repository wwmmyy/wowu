package com.wuwo.im.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.wuwo.im.config.WowuApp;
import com.wuwo.im.view.MyScrollLayout;

import im.imxianzhi.com.imxianzhi.R;
/** 
*desc WelcomeActivity
*@author 王明远
*@日期： 2016/6/9 0:07
*@版权:Copyright    All rights reserved.
*/

public class WelcomeActivity extends BaseActivity implements MyScrollLayout.OnViewChangeListener {
	
	private MyScrollLayout mScrollLayout;
	private ImageView[] imgs;
	private int count;
	private int currentItem;
	private Button startBtn;
	private RelativeLayout mainRLayout;
	private LinearLayout pointLLayout;
	private LinearLayout leftLayout;
	private LinearLayout rightLayout;
	private LinearLayout animLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);//出去标题

        super.onCreate(savedInstanceState);


		// 判断是否第一次安装软件
		SharedPreferences mSettings = getSharedPreferences( WowuApp.PREFERENCE_KEY, MODE_PRIVATE);
		SharedPreferences.Editor editortemp = mSettings.edit();
		editortemp.putBoolean("mFirstTimeSeting", false);
		editortemp.commit();

        Intent intent = getIntent();  
      //获取数据   
//        start_type = intent.getStringExtra("start_type");
        
        setContentView(R.layout.activity_welcome);
        initView();
    }
    
	private void initView() {
		mScrollLayout  = (MyScrollLayout) findViewById(R.id.ScrollLayout);
		pointLLayout = (LinearLayout) findViewById(R.id.llayout);
		mainRLayout = (RelativeLayout) findViewById(R.id.mainRLayout);
		startBtn = (Button) findViewById(R.id.startBtn);
		startBtn.setOnClickListener(onClick);
//		animLayout = (LinearLayout) findViewById(R.id.animLayout);
//		leftLayout  = (LinearLayout) findViewById(R.id.leftLayout);
//		rightLayout  = (LinearLayout) findViewById(R.id.rightLayout);
		count = mScrollLayout.getChildCount();
		imgs = new ImageView[count];
		for(int i = 0; i< count;i++) {
			imgs[i] = (ImageView) pointLLayout.getChildAt(i);
			imgs[i].setEnabled(true);
			imgs[i].setTag(i);
		}
		currentItem = 0;
		imgs[currentItem].setEnabled(false);
		mScrollLayout.SetOnViewChangeListener(this);
	}
	
	private View.OnClickListener onClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.startBtn:
				mScrollLayout.setVisibility(View.GONE);
				pointLLayout.setVisibility(View.GONE);
//				animLayout.setVisibility(View.VISIBLE);
//				mainRLayout.setBackgroundResource(R.drawable.whatsnew_bg);
//				Animation leftOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_left);
//				Animation rightOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_right);
//				leftLayout.setAnimation(leftOutAnimation);
//				rightLayout.setAnimation(rightOutAnimation);
//				leftOutAnimation.setAnimationListener(new AnimationListener() {
//					@SuppressLint("ResourceAsColor")
//					@Override
//					public void onAnimationStart(Animation animation) {
//						mainRLayout.setBackgroundColor(R.color.bgColor);
//					}
//					@Override
//					public void onAnimationRepeat(Animation animation) {
//					}
//					@Override
//					public void onAnimationEnd(Animation animation) {
//						leftLayout.setVisibility(View.GONE);
//						rightLayout.setVisibility(View.GONE);

						Intent intent = new Intent(WelcomeActivity.this,LoginActivity.class);
						startActivity(intent);
						finish();
//						overridePendingTransition(R.anim.zoom_out_enter, R.anim.zoom_out_exit);
//					}
//				});
				break;
			}
		}
	};

	public void OnViewChange(int position) {
		setcurrentPoint(position);
	}

	private void setcurrentPoint(int position) {
		if(position < 0 || position > count -1 || currentItem == position) {
			return;
		}
		imgs[currentItem].setEnabled(true);
		imgs[position].setEnabled(false);
		currentItem = position;
	}

	public boolean execute() {
		// TODO Auto-generated method stub
		return false;
	}
}

package com.wuwo.im.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
*desc
*@author 王明远
*@日期： 2016/1/28 15:08
*@版权:Copyright 上海数慧系统有限公司  All rights reserved.
*/

public class NoScrollViewPager extends ViewPager {
    private boolean noScroll = false;
    private boolean noRightScroll = false;

    public NoScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public NoScrollViewPager(Context context) {
        super(context);
    }

    public void setNoScroll(boolean noScroll) {
        this.noScroll = noScroll;
    }

    public void setNoRightScroll(boolean noRightScroll) {
        this.noRightScroll = noRightScroll;
    }

    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent arg0) {
//                /* return false;//super.onTouchEvent(arg0); */
//        if (noScroll)
//            return false;
//        else
//            return super.onTouchEvent(arg0);
//    }





//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        // TODO Auto-generated method stub
//        float x = 0,moveX;
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                x = event.getX();
//                break;
//            case MotionEvent.ACTION_MOVE:
//                moveX = event.getX();
//                if(moveX-x<0 && noRightScroll){
//                    return false;
//                }
//                break;
//            case MotionEvent.ACTION_UP:
//                break;
//            }
//            return super.onTouchEvent(event);
//        }


    float x = 0,moveX;
    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        if (noScroll)// || noRightScroll
            return false;
        else if(noRightScroll){

            switch (arg0.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x = arg0.getX();
//                    Log.d("点击获取的坐标x为：：：","点击获取的坐标x为：：："+x+":");
                    break;
                case MotionEvent.ACTION_MOVE:
//                    moveX = arg0.getX();
//                    if(moveX-x>0){
//                        return true ;
//                    }
//                    return super.onInterceptTouchEvent(arg0);
                    break;
                case MotionEvent.ACTION_UP:
                    moveX = arg0.getX();
//                    Log.d("点击获取的坐标为：：：","点击获取的坐标为：：："+x+":"+moveX);
                    if(x-moveX>20){
                        x=moveX=0;
                        return true ;
                    }
                    x=moveX=0;
                    break;
            }

        } else{
            return super.onInterceptTouchEvent(arg0);
        }
        return super.onInterceptTouchEvent(arg0);
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(item, smoothScroll);
    }

    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item);
    }


    OnScrollRightListener mOnScrollRightListener;
    public interface OnScrollRightListener {
        public void onScrollRight(boolean state);
    }

    public void setOnScrollRightListener(OnScrollRightListener mOnScrollRightListener) {
        this.mOnScrollRightListener = mOnScrollRightListener;
    }

    /**
     * 上一次x坐标
     */
    private float beforeX;

    //-----禁止左滑-------左滑：上一次坐标 > 当前坐标
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN://按下如果‘仅’作为‘上次坐标’，不妥，因为可能存在左滑，motionValue大于0的情况（来回滑，只要停止坐标在按下坐标的右边，左滑仍然能滑过去）
                    beforeX = ev.getX();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float motionValue = ev.getX() - beforeX;
                    //监听翻页信息
                    if(motionValue < 0 &&mOnScrollRightListener!=null){
                        mOnScrollRightListener.onScrollRight(true);
                    }



                    beforeX = ev.getX();//手指移动时，再把当前的坐标作为下一次的‘上次坐标’，解决上述问题


                    if (noRightScroll&&motionValue< 0) {//禁止右滑
                        return true;//true
                    }

                    break;
                default:
                    break;
            }
            return super.dispatchTouchEvent(ev);
    }





} 
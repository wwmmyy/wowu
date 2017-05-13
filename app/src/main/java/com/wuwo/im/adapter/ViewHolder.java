package com.wuwo.im.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;


public class ViewHolder
{
	private final SparseArray<View> mViews;
	private int mPosition;
	private View mConvertView;

	ViewHolder(Context context, ViewGroup parent, int layoutId,
			int position)
	{
		this.mPosition = position;
		this.mViews = new SparseArray<View>();
		mConvertView = LayoutInflater.from(context).inflate(layoutId, parent,
				false);
		// setTag
		mConvertView.setTag(this);
	}

	public ViewHolder(Context mContext, ViewGroup parent, int mItemLayoutId, int position,
            View convertView) {
        // TODO 自动生成的构造函数存根
	    this.mPosition = position;
            this.mViews = new SparseArray<View>();
            if (convertView == null){
                mConvertView = LayoutInflater.from(mContext).inflate(mItemLayoutId, parent,
                        false); 
                 mConvertView.setTag(this);
            }else{
                mConvertView = convertView;  
            }
            
    }

    /**
	 * 拿到一个ViewHolder对象
	 * 
	 * @param context
	 * @param convertView
	 * @param parent
	 * @param layoutId
	 * @param position
	 * @return
	 */
	public static ViewHolder get(Context context, View convertView,
								 ViewGroup parent, int layoutId, int position)
	{
		if (convertView == null)
		{ 
			return new ViewHolder(context, parent, layoutId, position,null);
		} 
		
		return (ViewHolder) convertView.getTag();
	}
	
	

	public View getConvertView()
	{
		return mConvertView;
	}

	/**
	 * 通过控件的Id获取对于的控件，如果没有则加入views
	 * 
	 * @param viewId
	 * @return
	 */
	public <T extends View> T getView(int viewId)
	{
		View view = mViews.get(viewId);
		if (view == null)
		{
			view = mConvertView.findViewById(viewId);
			mViews.put(viewId, view);
		}
		return (T) view;
	}

	/**
	 * 为TextView设置字符串
	 * 
	 * @param viewId
	 * @param text
	 * @return
	 */
	public ViewHolder setText(int viewId, String text)
	{
		TextView view = getView(viewId);
		view.setText(text);
		return this;
	}
	
	
	
	  public void setVisibility(int viewId, int visiable)
	    {
	        getView(viewId).setVisibility(visiable); 
	               
	     }
	
	

	/**
	 * 为ImageView设置图片
	 * 
	 * @param viewId
	 * @param drawableId
	 * @return
	 */
	public ViewHolder setImageResource(int viewId, int drawableId)
	{
		ImageView view = getView(viewId);
		view.setImageResource(drawableId);

		return this;
	}

	/**
	 * 为ImageView设置图片
	 * 
	 * @param viewId
	 * @param
	 * @return
	 */
	public ViewHolder setImageBitmap(int viewId, Bitmap bm)
	{
		ImageView view = getView(viewId);
		view.setImageBitmap(bm);
		return this;
	}

	public ViewHolder setImageURI(int viewId,int id){
		SimpleDraweeView view = getView(viewId);
//		view.setImageURI(Uri.parse(DistApp.userImagePath + id + ".jpg"));
		return this;
	}

//	/**
//	 * 为ImageView设置图片
//	 * 
//	 * @param viewId
//	 * @param drawableId
//	 * @return
//	 */
//	public ViewHolder setImageByUrl(int viewId, String url)
//	{
//		ImageLoader.getInstance(3, Type.LIFO).loadImage(url,
//				(ImageView) getView(viewId));
//		return this;
//	}

	public int getPosition()
	{
		return mPosition;
	}

}

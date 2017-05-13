package com.wuwo.im.util;

import android.content.Context;
import android.content.DialogInterface;
import android.text.format.DateUtils;
import android.view.View;

import com.wuwo.im.view.MaterialDialog;

import java.util.Calendar;


public class DateTimePickerDialog extends MaterialDialog implements View.OnClickListener
    {
	private DateTimePicker mDateTimePicker;
	private Calendar mDate = Calendar.getInstance();
	private Calendar mDate1 = Calendar.getInstance();
	private OnDateTimeSetListener mOnDateTimeSetListener;
	Context context;

	@SuppressWarnings("deprecation")
	public DateTimePickerDialog(Context context, long date) {
		super(context);
		mDate.setTimeInMillis(date);
		mDate1.setTimeInMillis(date);

		mDateTimePicker = new DateTimePicker(context,date);
		this.context = context;
		setContentView(mDateTimePicker);
		mDateTimePicker
				.setOnDateTimeChangedListener(new DateTimePicker.OnDateTimeChangedListener() {
					@Override
					public void onDateTimeChanged(DateTimePicker view,
							int year, int month, int day, int hour, int minute,
							int hour1, int minute1) {

						mDate.set(Calendar.YEAR, year);
						mDate.set(Calendar.MONTH, month);
						mDate.set(Calendar.DAY_OF_MONTH, day);
						mDate.set(Calendar.HOUR_OF_DAY, hour);
						mDate.set(Calendar.MINUTE, minute);
						mDate.set(Calendar.SECOND, 0);

						mDate1.set(Calendar.YEAR, year);
						mDate1.set(Calendar.MONTH, month);
						mDate1.set(Calendar.DAY_OF_MONTH, day);
						mDate1.set(Calendar.HOUR_OF_DAY, hour1);
						mDate1.set(Calendar.MINUTE, minute1);
						mDate1.set(Calendar.SECOND, 0);

						updateTitle(mDate.getTimeInMillis(),
								mDate1.getTimeInMillis());
					}
				});


		 /*setButton("确定", this);
		 setButton2("取消", (OnClickListener)null);*/

		/*setPositiveButton("确定", new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mOnDateTimeSetListener.OnDateTimeSet(materialDialog, mDate.getTimeInMillis(),
						mDate1.getTimeInMillis());
			}

		});*/
		setPositiveButton("确定", this);
		setNegativeButton("取消", new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DateTimePickerDialog.this.dismiss();
			}
		});
		//setTitle("选择日期");


		updateTitle(mDate.getTimeInMillis(), mDate1.getTimeInMillis());
	}

	public interface OnDateTimeSetListener {
		void OnDateTimeSet(MaterialDialog dialog, long date, long date1);
	}

	private void updateTitle(long date, long date1) {
		// int flag = DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_DATE |
		// DateUtils.FORMAT_SHOW_WEEKDAY| DateUtils.FORMAT_SHOW_TIME;

//		int flag = DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_DATE
//				| DateUtils.FORMAT_SHOW_TIME;
		int flag =  DateUtils.FORMAT_SHOW_TIME;
		int flag1 = DateUtils.FORMAT_SHOW_TIME;// |DateUtils.FORMAT_SHOW_WEEKDAY
		setTitle(DateUtils.formatDateTime(context, date, flag) + "-"
				+ DateUtils.formatDateTime(context, date1, flag1));
	}

	public void setOnDateTimeSetListener(OnDateTimeSetListener callBack) {
		mOnDateTimeSetListener = callBack;
	}


	public void onClick(DialogInterface arg0, int arg1) {
		if (mOnDateTimeSetListener != null) {
			mOnDateTimeSetListener.OnDateTimeSet(this, mDate.getTimeInMillis(),
					mDate1.getTimeInMillis());
		}
	}

	/*DialogInterface.OnClickListener listener = new OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			if (mOnDateTimeSetListener != null) {
				mOnDateTimeSetListener.OnDateTimeSet(MaterialDialog.this, mDate.getTimeInMillis(),
						mDate1.getTimeInMillis());
			}
		}
	};*/

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

//		if(mDate.getTimeInMillis()>=mDate1.getTimeInMillis()){
//					MyToast.show(context, "开始时间不能大于结束时间！", Toast.LENGTH_LONG);
//		}else{
			if (mOnDateTimeSetListener != null) {
				mOnDateTimeSetListener.OnDateTimeSet(this, mDate.getTimeInMillis(),
						mDate1.getTimeInMillis());
			}
			DateTimePickerDialog.this.dismiss();
		}

//	}

}

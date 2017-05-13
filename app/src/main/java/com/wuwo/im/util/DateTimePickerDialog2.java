package com.wuwo.im.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.text.format.DateUtils;

import java.util.Calendar;


public class DateTimePickerDialog2 extends AlertDialog implements OnClickListener
{
    private DateTimePicker2 mDateTimePicker2;
    private Calendar mDate = Calendar.getInstance();
    private Calendar mDate1 = Calendar.getInstance();
    private OnDateTimeSetListener mOnDateTimeSetListener;

	@SuppressWarnings("deprecation")
	public DateTimePickerDialog2(Context context, long date)
	{
		super(context);
		mDateTimePicker2 = new DateTimePicker2(context);
	    setView(mDateTimePicker2);
	    mDateTimePicker2.setOnDateTimeChangedListener(new DateTimePicker2.OnDateTimeChangedListener()
		{
		  @Override
		 public void onDateTimeChanged(DateTimePicker2 view, int year, int month, int day, int hour, int minute, int hour1, int minute1)
		 {

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

                updateTitle(mDate.getTimeInMillis(),mDate1.getTimeInMillis());
			}
		});

	    setButton("确定", this);
        setButton2("取消", (OnClickListener)null);
	    mDate.setTimeInMillis(date);
	    updateTitle(mDate.getTimeInMillis(),mDate1.getTimeInMillis());
	}

	public interface OnDateTimeSetListener
    {
        void OnDateTimeSet(AlertDialog dialog, long date, long date1);
    }

	private void updateTitle(long date,long date1)
    {
//        int flag = DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_WEEKDAY| DateUtils.FORMAT_SHOW_TIME;
         int flag = DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_DATE ; //| DateUtils.FORMAT_SHOW_TIME
        int flag1 =DateUtils.FORMAT_SHOW_WEEKDAY;//|DateUtils.FORMAT_SHOW_TIME
        setTitle(DateUtils.formatDateTime(this.getContext(), date, flag)+"-"+DateUtils.formatDateTime(this.getContext(), date1, flag1));
    }

	public void setOnDateTimeSetListener(OnDateTimeSetListener callBack)
    {
        mOnDateTimeSetListener = callBack;
    }

	public void onClick(DialogInterface arg0, int arg1)
    {
        if (mOnDateTimeSetListener != null)
        {
            mOnDateTimeSetListener.OnDateTimeSet(this, mDate.getTimeInMillis(), mDate1.getTimeInMillis());
        }
    }
}

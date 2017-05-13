package com.wuwo.im.util;

import android.content.Context;
import android.text.format.DateFormat;
import android.widget.FrameLayout;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;

import java.util.Calendar;

import im.imxianzhi.com.imxianzhi.R;


public class DateTimePicker extends FrameLayout {
    private final NumberPicker mDateSpinner;
    private final NumberPicker mHourSpinner, mHourSpinner1;
    private final NumberPicker mMinuteSpinner, mMinuteSpinner1;
    private Calendar mDate =Calendar.getInstance();
    private int mHour, mMinute, mHour1, mMinute1;
    private String[] mDateDisplayValues = new String[7];
    private OnDateTimeChangedListener mOnDateTimeChangedListener;
    private final String[] mDisplayNames = {"00","10","20","30","40","50"};
    public DateTimePicker(Context context, long date) {
        super(context);
//        mDate = Calendar.getInstance();
        mDate.setTimeInMillis(date);

        mHour = mDate.get(Calendar.HOUR_OF_DAY);

//        mMinute = mDate.get(Calendar.MINUTE);
        mHour1 = mDate.get(Calendar.HOUR_OF_DAY);
//        mMinute1 = mDate.get(Calendar.MINUTE);

        inflate(context, R.layout.datedialog_date_choose, this);

        mDateSpinner = (NumberPicker) this.findViewById(R.id.np_date);
        mDateSpinner.setMinValue(0);
        mDateSpinner.setMaxValue(6);
        updateDateControl();
        mDateSpinner.setOnValueChangedListener(mOnDateChangedListener);

        mHourSpinner = (NumberPicker) this.findViewById(R.id.np_hour);
        mHourSpinner.setMaxValue(23);
        mHourSpinner.setMinValue(0);
        mHourSpinner.setValue(mHour);
        mHourSpinner.setOnValueChangedListener(mOnHourChangedListener);

        mMinuteSpinner = (NumberPicker) this.findViewById(R.id.np_minute);
        mMinuteSpinner.setDisplayedValues(mDisplayNames);
        mMinuteSpinner.setMaxValue(5);
        mMinuteSpinner.setMinValue(0);
//        mMinuteSpinner.setValue(mMinute);
        mMinuteSpinner.setOnValueChangedListener(mOnMinuteChangedListener);

        mHourSpinner1 = (NumberPicker) this.findViewById(R.id.np_hour1);
        mHourSpinner1.setMaxValue(23);
        mHourSpinner1.setMinValue(0);
        mHourSpinner1.setValue(mHour1);
        mHourSpinner1.setOnValueChangedListener(mOnHourChangedListener1);

        mMinuteSpinner1 = (NumberPicker) this.findViewById(R.id.np_minute1);
        mMinuteSpinner1.setDisplayedValues(mDisplayNames);
        mMinuteSpinner1.setMaxValue(5);
        mMinuteSpinner1.setMinValue(0);
//        mMinuteSpinner1.setValue(mMinute1);
        mMinuteSpinner1.setOnValueChangedListener(mOnMinuteChangedListener1);
    }

	private OnValueChangeListener mOnDateChangedListener = new OnValueChangeListener() {
        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            mDate.add(Calendar.DAY_OF_MONTH, newVal - oldVal);
            updateDateControl();
            onDateTimeChanged();
        }
    };

    private OnValueChangeListener mOnHourChangedListener = new OnValueChangeListener() {
        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            mHour = mHourSpinner.getValue();
            onDateTimeChanged();
        }
    };

    private OnValueChangeListener mOnMinuteChangedListener = new OnValueChangeListener() {
        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
//            mMinute = mMinuteSpinner.getValue();
//            mMinute1 = mMinuteSpinner1.getValue();
        	 mMinute =Integer.parseInt(mDisplayNames[newVal]) ;
            onDateTimeChanged();
        }
    };

    private OnValueChangeListener mOnHourChangedListener1 = new OnValueChangeListener() {
        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            mHour1 = mHourSpinner1.getValue();
            onDateTimeChanged();
        }
    };

    private OnValueChangeListener mOnMinuteChangedListener1 = new OnValueChangeListener() {
        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
//            mMinute1 = mMinuteSpinner1.getValue();
//            mMinute = mMinuteSpinner.getValue();


            mMinute1 =Integer.parseInt(mDisplayNames[newVal]) ;

            onDateTimeChanged();

        }
    };

    private void updateDateControl() {
        Calendar cal = Calendar.getInstance();

        cal.setTimeInMillis(mDate.getTimeInMillis());
        cal.add(Calendar.DAY_OF_YEAR, -7 / 2 - 1);
        mDateSpinner.setDisplayedValues(null);
        for (int i = 0; i < 7; ++i) {
            cal.add(Calendar.DAY_OF_YEAR, 1);
            mDateDisplayValues[i] = (String) DateFormat.format("MM.dd", cal);
            // mDateDisplayValues[i] = (String) DateFormat.format("MM.dd", cal);

        }
        mDateSpinner.setDisplayedValues(mDateDisplayValues);
        mDateSpinner.setValue(7 / 2);
        mDateSpinner.invalidate();
    }

    public interface OnDateTimeChangedListener {
        void onDateTimeChanged(DateTimePicker view, int year, int month, int day, int hour,
                               int minute, int hour1, int minute1);
    }

    public void setOnDateTimeChangedListener(OnDateTimeChangedListener callback) {
        mOnDateTimeChangedListener = callback;
    }

    private void onDateTimeChanged() {
        if (mOnDateTimeChangedListener != null) {
            mOnDateTimeChangedListener.onDateTimeChanged(this, mDate.get(Calendar.YEAR),
                    mDate.get(Calendar.MONTH), mDate.get(Calendar.DAY_OF_MONTH), mHour, mMinute,
                    mHour1, mMinute1);
        }
    }
}

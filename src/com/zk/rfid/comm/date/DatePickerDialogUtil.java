package com.zk.rfid.comm.date;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.DatePicker;
import android.widget.TimePicker;

/**
 * @Description: 时间选择器
 * @date:2016-5-18 下午5:13:43
 * @author: ldl
 */
public class DatePickerDialogUtil
{
	public static final int TYPE_DATE=1;
	public static final int TYPE_TIME=2;
	private Calendar calendar;
	
	private int mYear, mMonth,mDay,mHour,mMinute;
	private Dialog timeDialog;
	private Context context;
	private DateTimeCallBackListener listen;
	

    /**初始化日期控件**/
	public DatePickerDialogUtil(Context context)
	{
		this.context=context;
		calendar = Calendar.getInstance();
	    mYear = calendar.get(Calendar.YEAR);
	    mMonth = calendar.get(Calendar.MONTH);
	    mDay = calendar.get(Calendar.DAY_OF_MONTH);		
	    
	    mHour = calendar.get(Calendar.HOUR_OF_DAY);
        mMinute = calendar.get(Calendar.MINUTE);   
	}
		
    /**
     * @param 返回的监听
     * @return  获得日期控件所示值
     */
	public Dialog getDate(DateTimeCallBackListener listen) 
	{		
		this.listen=listen;	
		
		timeDialog = new MyDatePickerDialog(context, mDateSetListener, mYear, mMonth, mDay);		
		MyDatePickerDialog.listen=listen;
		timeDialog.setCancelable(true);
		
		return timeDialog;	
		
	}
	
	
	/**获得时间控件**/
	public Dialog getTime(DateTimeCallBackListener listen) 
	{
		this.listen=listen;			
		timeDialog = new MyTimePickerDialog(context, mTimeSetListener, mHour, mMinute, true);
		MyTimePickerDialog.listen=listen;
		timeDialog.setCancelable(true);		
		return timeDialog;	
	}

	/**日期控件的实现**/
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() 
	{
		@Override
		public void onDateSet(DatePicker view, int year, int month, int day) 
		{
			mYear = year;
			mMonth = month + 1;// 此处留意要加1
			String monthStr=mMonth+"";
			if(mMonth<10)
			{
				monthStr="0"+mMonth;
			}
			mDay = day;
			String dayStr=mDay+"";
			if(mDay<10)
			{
				dayStr="0"+mDay;
			}
			listen.dateTimeCallBack(mYear+"-"+monthStr+"-"+dayStr);	
			timeDialog.dismiss();							
		}	
		
	};
	
	  /**时间控件的实现**/
	  private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() 
	  {			
			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) 
			{
				mHour = hourOfDay;
				mMinute = minute;	
				listen.dateTimeCallBack(mHour+":"+mMinute);
				timeDialog.dismiss();
			}			
			
		};
				
}

/**自定义扩展日期控件**/
class MyDatePickerDialog extends DatePickerDialog 
{
    public MyDatePickerDialog(Context context, OnDateSetListener callBack,
            int year, int monthOfYear, int dayOfMonth) {
    	
        super(context, callBack, year, monthOfYear, dayOfMonth);
    }
    
    public static DateTimeCallBackListener listen;
    
    @Override
    protected void onStop()
    {
    	listen.dateTimeCallBack("cancle");
    	getDatePicker().clearFocus();
    	listen=null;
       // super.onStop();   //将行注掉，否则会在取消时也会返回值	
    }
   
}

/**自定义扩展时间控件**/
class MyTimePickerDialog extends TimePickerDialog
{ 
	public MyTimePickerDialog(Context context, OnTimeSetListener callBack,int hourOfDay, int minute, boolean is24HourView)
	{
		super(context, callBack, hourOfDay, minute, is24HourView);
	}

	   public static DateTimeCallBackListener listen;
	    
	    @Override
	    protected void onStop()
	    {
	    	listen.dateTimeCallBack("cancle");
	    	listen=null;
	       // super.onStop();   //将行注掉，否则会在取消时也会返回值		
	    }
}



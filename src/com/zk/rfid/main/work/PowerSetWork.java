package com.zk.rfid.main.work;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.zk.rfid.R;
import com.zk.rfid.comm.base.MyApplication;
import com.zk.rfid.main.util.ConfigUtil;
import com.zk.rfid.util.CommUtil;
import com.zk.rfid.util.LogUtil;

/**
 * @Description: 扫描功率设置
 * @date:2016-9-11下午1:30:48
 * @author: ldl
 */
public class PowerSetWork
{
	private static final String TAG="PowerSetWork";
	private Activity activity;
	private RadioGroup powerSetRadioGroup;
	private RadioButton powerSmallRadio,powerMiddleRadio,powerLargeRadio,powerMostRadio;
		 
	public PowerSetWork(Activity activity)
	{
		this.activity=activity;
		initView();
		new CountDownTimer(1000,100)
		{		
			@Override
			public void onTick(long millisUntilFinished)
			{			
			}			
			@Override
			public void onFinish()
			{
				setOldPowerValue();//默认上一次的值
			}
		}.start();
		
	}
	
	private void initView()
	{
		powerSetRadioGroup=(RadioGroup) activity.findViewById(R.id.powerSetRadioGroup);
		powerSmallRadio =(RadioButton) activity.findViewById(R.id.powerSmallRadio);
		powerMiddleRadio=(RadioButton) activity.findViewById(R.id.powerMiddleRadio);
		powerLargeRadio =(RadioButton) activity.findViewById(R.id.powerLargeRadio);
		powerMostRadio  =(RadioButton) activity.findViewById(R.id.powerMostRadio);
				
		powerSetRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1)
			{
				if (R.id.powerSmallRadio == arg1)
				{
					setPowerValue(ConfigUtil.POWER_SMALL);
				}
				
				if (R.id.powerMiddleRadio == arg1)
				{
					setPowerValue(ConfigUtil.POWER_MIDDLE);
				}
				
				if (R.id.powerLargeRadio == arg1)
				{
					setPowerValue(ConfigUtil.POWER_LARGE);
				}
				
				if (R.id.powerMostRadio== arg1)
				{
					setPowerValue(ConfigUtil.POWER_MOST);
				}
			}
		});
	}
	
	/**设置成上次设置的功率**/
	private void setOldPowerValue()
	{
		int value=getPowerSetValue(MyApplication.context);
		setPowerValue(value);
		
		powerSmallRadio.setChecked(false);
		powerMiddleRadio.setChecked(false);
		powerLargeRadio.setChecked(false);
		powerMostRadio.setChecked(false);
		
		SystemClock.sleep(100);
		if(ConfigUtil.POWER_SMALL==value)
		{
			powerSmallRadio.setChecked(true);
		}
		if(ConfigUtil.POWER_MIDDLE==value)
		{
			powerMiddleRadio.setChecked(true);
		}
		if(ConfigUtil.POWER_LARGE==value)
		{
			powerLargeRadio.setChecked(true);
		}
		if(ConfigUtil.POWER_MOST==value)
		{
			powerMostRadio.setChecked(true);
		}
	}
	
	/**设置指定天线功率**/
	private void setPowerValue(int value)
	{
		LogUtil.i(TAG, "当前功率:"+value);
		Editor editor=CommUtil.getShare(activity).edit();
		editor.putInt(ConfigUtil.POWER_SET_VALUE, value);
		editor.commit();
		
		setObjectPower(value);		
	}
	
	public static void setObjectPower(int value)
	{
		MyApplication.getRfidWork().setPower(true,value, 1000, 2000, 200);
	}
	
	/**获得上次功率设置值**/
	public static int getPowerSetValue(Context context)
	{
		return CommUtil.getShare(context).getInt(ConfigUtil.POWER_SET_VALUE, ConfigUtil.POWER_SMALL);
	}
}

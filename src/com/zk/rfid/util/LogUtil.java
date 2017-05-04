package com.zk.rfid.util;

import android.util.Log;

/**
 * @Description: 日志打出控制
 * @date:2014-9-23 下午4:45:22
 * @author: 罗德禄
 */
public class LogUtil
{
	
	private static final String TAG = "LogUtil";
	private static final int a=1;
	private static final int b=2;

	public static void i(String tag, String msg)
	{
		if (a<b)
		{
			Log.i(tag, msg);
		}
	}

	public static void e(String tag, String msg)
	{
		if (a<b)
		{
			Log.e(tag, msg);
		}
	}

	public static void d(String tag, String msg)
	{
		if (a<b)
		{
			Log.d(tag, msg);
		}
	}

	public static void w(String tag, String msg)
	{
		if (a<b)
		{
			Log.w(tag, msg);
		}
	}

	public static void v(String tag, String msg)
	{
		if (a<b)
		{
			Log.v(tag, msg);
		}
	}
}

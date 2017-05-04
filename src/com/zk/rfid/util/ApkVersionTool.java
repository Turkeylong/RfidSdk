package com.zk.rfid.util;

import android.content.Context;

import com.zk.rfid.R;

/** 应用版本工具类 **/
public class ApkVersionTool
{
	private static final String TAG = "ApkVersionTool";

	/** 获取当前已安装应用名 **/
	public static String getAppName(Context context)
	{
		String appName = context.getResources().getText(R.string.app_name).toString();
		return appName;
	}

	/** 获取当前已安装应用版本号 **/
	public static int getVerCode(Context context,String appPackName)
	{
		int verCode = -1;
		try
		{
			verCode = context.getPackageManager().getPackageInfo(appPackName, 0).versionCode;
			LogUtil.i(TAG, "当前已安装应用版本号:" + verCode);
		} catch (Exception e)
		{
			LogUtil.e(TAG, e.getMessage());
		}
		return verCode;
	}

	/** 获取当前已安装应用版本名 **/
	public static String getVerName(Context context,String appPackName)
	{
		String verName = "";
		try
		{
			verName = context.getPackageManager().getPackageInfo(appPackName, 0).versionName;
			LogUtil.i(TAG, "当前已安装应用版本名 :" + verName);
		} catch (Exception e)
		{
			LogUtil.e(TAG, e.getMessage());
		}
		return verName;
	}

}

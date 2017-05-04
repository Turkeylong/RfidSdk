package com.zk.rfid.comm.base;

import android.app.Activity;
import android.os.Bundle;

import com.zk.rfid.R;
import com.zk.rfid.util.CommUtil;
import com.zk.rfid.util.SysApplication;

/**
 * @Description: 基类
 * @date:2016年5月12日 上午9:57:21
 * @author: ldl
 */
public class BaseActivity extends Activity 
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		SysApplication.getInstance().addActivity(this);
	}
	
	@Override
	protected void onDestroy() 
	{
		super.onDestroy();		
	}
	
	
	/**
	 * 网络连接检查
	 * @return 是否连接
	 */
	public boolean isConn()
	{
		if(CommUtil.isConnect(this))
		{
			return true;
		}else
		{
			CommUtil.toastShow(getString(R.string.network_error), this);
			return false;
		}
	}
		
}
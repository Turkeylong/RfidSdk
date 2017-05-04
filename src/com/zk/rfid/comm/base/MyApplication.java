package com.zk.rfid.comm.base;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.zk.rfid.main.work.EpcWork;

/**
 * @Description: 应用application
 * @date:2016-8-10上午11:43:42
 * @author: ldl
 */
public class MyApplication extends Application
{
	public static Context context;
	private static EpcWork rfidWork;

	public static Handler handler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case 1:
				
				break;

			default:
				break;
			}
		};
	};

	@Override
	public void onCreate()
	{
		context = getApplicationContext();
		rfidWork = new EpcWork(context);
		rfidWork.setReaderHandler(handler);
		rfidWork.powerOff();
		super.onCreate();
	}


	/**获得统一引用标签操作对象**/
	public static EpcWork getRfidWork()
	{
		if (null == rfidWork)
		{
			rfidWork = new EpcWork(context);
		}
		return rfidWork;
	}

}

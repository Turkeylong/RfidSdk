package com.zk.rfid.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.zk.rfid.main.util.ConfigUtil;

/**
 * @Description:公用thread线程 
 * @date:2016-8-8下午4:58:31
 * @author: ldl
 */
public class GetDataThread extends Thread
{
	
	private static final String TAG="GetDataThread";
	private Handler handler;
	private Context context;
	
	private int handlerCode;
	private int arg1=0;
	private int arg2=0;
	
	private String params;
	
	private Handler mHandler=new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case 1:
				
				CommUtil.toastShow(msg.obj+"", context);
				break;

			default:
				break;
			}
		};
	};
	
	public GetDataThread(Context context,Handler handler)
	{
		this.context=context;
		this.handler=handler;
	}
	
	 @Override
	 public void run()
	 {
		 String session=CommUtil.getShare(context).getString(ConfigUtil.USER_SESSION, "");
		  params=params+"&session="+session;
		  LogUtil.i(TAG, "发请求参数："+params);
		  String res=HttpGetPostUtil.sendHttpPost(ConstantUtil.HTTP_SERVER_URL, params);
		  doResult(res);
	 }
	 
	 
	 private void doResult(String result)
	 {
			LogUtil.i(TAG, "Thread请求返回结果:"+result);
//			if(null!=result && result.contains("stout"))
//			{
//				Map<String,String> map=CommUtil.getJsonMap(result);
//				String msg=map.get("msg");
//				Message message=Message.obtain();
//				message.what=1;
//				message.obj=msg;
//				mHandler.sendMessage(message);
//			}else
//			{
//				Message msg=Message.obtain();
//				msg.what=getHandlerCode();
//				msg.obj =result;
//				msg.arg1=getArg1();
//				msg.arg2=getArg2();
//				handler.sendMessage(msg);	
//			}	
			
			Message msg=Message.obtain();
			msg.what=getHandlerCode();
			msg.obj =result;
			msg.arg1=getArg1();
			msg.arg2=getArg2();
			handler.sendMessage(msg);	
	 }
	 	 
	 public int getHandlerCode()
	 {
		return handlerCode;
	 }

	 public void setHandlerCode(int handlerCode) 
		{
			this.handlerCode = handlerCode;
		}


		public String getParams() 
		{
			return params;
		}

		public void setParams(String params) 
		{
			this.params = params;
		}
				
		public int getArg1() 
		{
			return arg1;
		}
		public void setArg1(int arg1) 
		{
			this.arg1 = arg1;
		}
		public int getArg2() 
		{
			return arg2;
		}
		public void setArg2(int arg2)
		{
			this.arg2 = arg2;
		}
		

}

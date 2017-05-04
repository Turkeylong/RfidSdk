package com.zk.rfid.util;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;

import com.zk.rfid.R;
import com.zk.rfid.main.util.ConfigUtil;

/**
 * @Description: 访问网络公用线程
 * @date:2016-5-16 下午5:21:10
 * @author: ldl
 */
public class GetDataTask extends AsyncTask<String, String, String> 
{
	private static final String TAG="GetDataTask";
	private Handler handler;
	private Context context;
	private Dialog dialog;
	
	private int handlerCode;
	private int arg1=0;
	private int arg2=0;
	
	private String params;
	
	public GetDataTask(Context context,Handler handler)
	{
		this.context=context;
		this.handler=handler;
	}
	
	
	@Override
	protected void onPreExecute() 
	{
		dialog = createDialog();
		dialog.show();
		super.onPreExecute();
	}
	
	@Override
	protected String doInBackground(String... arg0)
	{
		String session=CommUtil.getShare(context).getString(ConfigUtil.USER_SESSION, "");
		params=params+"&session="+session;
		LogUtil.i(TAG, "发请求参数："+params);
		String res=HttpGetPostUtil.sendHttpPost(ConstantUtil.HTTP_SERVER_URL, params);		
		return res;
	}
	
	@Override
	protected void onPostExecute(String result) 
	{
		if(null!=dialog && dialog.isShowing())
		{
			dialog.cancel();	
			dialog=null;
		}
		
		doResult(result);			
		super.onPostExecute(result);
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
	
	
	private void doResult(String result)
	{
		LogUtil.i(TAG, "请求返回结果:"+result);
		//{ "result": { "state": "3003", "msg": "连接超时，请重新登录！session不存在" } }
		if(null!=result && result.contains("3003"))
		{
			//Map<String,String> map=CommUtil.getJsonMap(result);
			String msg="会话超时，请重新登录!";
			CommUtil.toastShow(msg, context);
		}else
		{
			Message msg=Message.obtain();
			msg.what=getHandlerCode();
			msg.obj=result;
			msg.arg1=getArg1();
			msg.arg2=getArg2();
			handler.sendMessage(msg);	
		}							
	}
	
	private Dialog createDialog()
	{
		Dialog dialog=new Dialog(context,R.style.dialogstyle);
		dialog.setContentView(R.layout.loading_dialog);	
		dialog.setCancelable(true);
		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
		lp.alpha = 0.75f; //0.0-1.0
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();		
		return dialog;
	}
}

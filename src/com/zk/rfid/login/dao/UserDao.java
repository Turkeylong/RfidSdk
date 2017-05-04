package com.zk.rfid.login.dao;

import android.app.Activity;
import android.os.Handler;

import com.zk.rfid.util.GetDataTask;
import com.zk.rfid.util.LogUtil;
import com.zk.rfid.util.MD5Util;
import com.zk.rfid.util.ZKCmd;

/**
 * @Description: 数据操作层
 * @date:2016-5-17 下午3:20:58
 * @author: ldl
 */
public class UserDao 
{
	private static final String TAG="UserDao";
	
	private Activity activity;
	private Handler handler;
	
	public UserDao()
	{		
	}
	public UserDao(Activity activity,Handler handler)
	{		
		this.handler=handler;
		this.activity=activity;
	}
	
	
	/**
	 * 
	 * @param 用户名和密码
	 * @param activity
	 * @param handler
	 */
	public void checkLoginData(String array[])
	{
		if(null!=array && array.length==2)
		{
			String pwd=MD5Util.getMd5(array[1]);
			GetDataTask task=new GetDataTask(activity, handler);
			StringBuffer sb=new StringBuffer();
			sb.append("t="+ZKCmd.REQ_LOGIN_ZK).append("&");
			sb.append("username="+array[0]).append("&");
			sb.append("password="+pwd).append("&");
			//sb.append("type="+array[2]);
			task.setHandlerCode(ZKCmd.HAND_RESPONSE_DATA);
			task.setParams(sb.toString());
			task.execute("");
		}
	}
	
	/**修改密码参数，用户名，密码，新密码**/
	public void updatePwdData(String array[])
	{
		if(null!=array && array.length==3)
		{
			String pwd=MD5Util.getMd5(array[1]);
			String newpwd=MD5Util.getMd5(array[2]);
			
			GetDataTask task=new GetDataTask(activity, handler);
			StringBuffer sb=new StringBuffer();
			sb.append("t="+ZKCmd.REQ_UPDATE_PWD);
			sb.append("&username="+array[0]);
			sb.append("&password="+pwd);
			sb.append("&newpwd="+newpwd);
			task.setHandlerCode(ZKCmd.HAND_RESPONSE_DATA);
			task.setParams(sb.toString());
			task.execute("");
		}
	}
	
	
	/**根据扫描用户的标签获取用户数据自动登录,此接口暂未用**/
	public void getUserData(String areaCode,int hanCode)
	{
		GetDataTask task=new GetDataTask(activity, handler);
		StringBuffer sb=new StringBuffer();
		sb.append("t="+ZKCmd.REQ_QUERY_USER_INFO);		
		sb.append("&areaCode="+areaCode);
		LogUtil.i(TAG, "user url:"+sb.toString());
		task.setHandlerCode(hanCode);
		task.setParams(sb.toString());
		task.execute("");					
	}
}

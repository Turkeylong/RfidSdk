package com.zk.rfid.login.activity;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.zk.rfid.comm.base.BaseActivity;
import com.zk.rfid.login.dao.UserDao;
import com.zk.rfid.login.work.UserWork;
import com.zk.rfid.R;
import com.zk.rfid.util.CommUtil;
import com.zk.rfid.util.LogUtil;
import com.zk.rfid.util.SystemUtil;
import com.zk.rfid.util.ZKCmd;

/**
 * @Description: 登录
 * @date:2016年5月12日 上午9:57:21
 * @author: ldl
 */
public class LoginActivity extends BaseActivity
{
	private static final String TAG="LoginActivity";
	private Activity activity=LoginActivity.this;
	private UserWork userWork;
	
	private Handler handler=new Handler()
	{
		public void handleMessage(Message msg) 
		{
			switch (msg.what) 
			{
			
			case ZKCmd.HAND_REQUEST_DATA:
				
				if (isConn())
				{
					String array[]=(String[]) msg.obj;
					UserDao dao=new UserDao(activity, handler);
					dao.checkLoginData(array);
					dao=null;
				}				
				break;
				
			case ZKCmd.HAND_RESPONSE_DATA:
				
				String res=msg.obj+"";									
				userWork.loginResult(res);					
				break;
				
			default:
				break;
			}
		};
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		Window window = getWindow();  
        WindowManager.LayoutParams params = window.getAttributes();  
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE;
        window.setAttributes(params);
		
		setContentView(R.layout.login);		
		userWork=UserWork.getInstance(activity);
		userWork.initView(handler);
	}
	
			
	@Override
	protected void onDestroy() 
	{
		super.onDestroy();
		UserWork.clearWork();
	}
	
}

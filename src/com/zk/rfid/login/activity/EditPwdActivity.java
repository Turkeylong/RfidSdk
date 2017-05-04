package com.zk.rfid.login.activity;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.zk.rfid.comm.base.BaseActivity;
import com.zk.rfid.login.dao.UserDao;
import com.zk.rfid.login.work.EditPwdWork;
import com.zk.rfid.R;
import com.zk.rfid.util.ZKCmd;

/**
 * @Description: 修改密码
 * @date:2016年5月12日 上午9:57:21
 * @author: ldl
 */
public class EditPwdActivity extends BaseActivity
{
	private static final String TAG="MainActivity";
	private Activity activity=EditPwdActivity.this;
	private EditPwdWork work;
	
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
					dao.updatePwdData(array);
					dao=null;
				}				
				break;
				
			case ZKCmd.HAND_RESPONSE_DATA:
				
				String res=msg.obj+"";
				work.editPwdRes(res);
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
		
		setContentView(R.layout.editpwd);
		work=EditPwdWork.getInstance(activity);
		work.initView(handler);//初始化控件		
		 setFinishOnTouchOutside(false);//点击外围不消失
	}
	
	@Override
	protected void onDestroy() 
	{
		super.onDestroy();
		EditPwdWork.clearEditPwdWork();
	}
}

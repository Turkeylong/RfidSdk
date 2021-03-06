package com.zk.rfid.login.work;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zk.rfid.R;
import com.zk.rfid.init.InitApp;
import com.zk.rfid.main.activity.MainActivity;
import com.zk.rfid.main.util.ConfigUtil;
import com.zk.rfid.util.CommUtil;
import com.zk.rfid.util.DoubleTableView;
import com.zk.rfid.util.GetDataThread;
import com.zk.rfid.util.LogUtil;
import com.zk.rfid.util.MD5Util;
import com.zk.rfid.util.SystemUtil;
import com.zk.rfid.util.ZKCmd;

/**
 * @Description: 
 * @date:2016年5月12日 上午9:59:00
 * @author: ldl
 */
public class UserWork 
{
	private static final String TAG="UserWork";
	
	private Button login_submit_btn,login_cancle_btn,login_epc_btn;
	private ImageView zk_login_delete,zk_pwd_delete;
	private EditText login_username_edit,login_pwd_edit;
	private TextView versionText;
	//private RadioGroup radioGroup ;
	private Activity activity;

	private Handler handler;

	
	private static UserWork userWork;
	private UserWork(Activity activity)
	{
		this.activity=activity;
	}
	
	/**获得用户登录操作的单例模式对象**/
	public static UserWork getInstance(Activity activity)
	{
		if(null==userWork)
		{
			userWork=new UserWork(activity);
		}
		return userWork;
	}
	
	/**初始化登录界面控件，并设置监听**/
	public void initView(Handler handler)
	{
		this.handler=handler;
		getViewById() ;
		setOnClickListen();
		//login_submit_btn.performClick();
	}
	
	/**初始化登录界面控件**/
	private void getViewById() 
	{
		login_submit_btn=(Button) activity.findViewById(R.id.login_submit_btn);
		login_cancle_btn=(Button) activity.findViewById(R.id.login_cancle_btn);
		login_epc_btn	=(Button) activity.findViewById(R.id.login_epc_btn);
		login_username_edit=(EditText) activity.findViewById(R.id.login_username_edit);
		login_pwd_edit=(EditText) activity.findViewById(R.id.login_pwd_edit);
		
		//radioGroup = (RadioGroup)activity.findViewById(R.id.radioGroup);   
		zk_login_delete=(ImageView) activity.findViewById(R.id.zk_login_delete);
		zk_pwd_delete=(ImageView) activity.findViewById(R.id.zk_pwd_delete);
		
		CommUtil.editTextChange(login_username_edit, zk_login_delete);
		CommUtil.editTextChange(login_pwd_edit, zk_pwd_delete);
		
		login_username_edit.setText(CommUtil.getShare(activity).getString(ConfigUtil.USER_NAME, ""));
		
		versionText=(TextView) activity.findViewById(R.id.versionText);
		String ver=CommUtil.getAppVersionInfo(activity)[1];
		versionText.setText("当前版本:"+ver);
	}
	
	/**设置控件监听**/
	private void setOnClickListen()
	{
		login_submit_btn.setOnClickListener(onclickListen);	
		login_epc_btn.setOnClickListener(onclickListen);		
		login_cancle_btn.setOnClickListener(onclickListen);
		zk_login_delete.setOnClickListener(onclickListen);
		zk_pwd_delete.setOnClickListener(onclickListen);
				
	}
	
	private OnClickListener onclickListen=new View.OnClickListener()
	{		
		@Override
		public void onClick(View view)
		{
			switch (view.getId()) 
			{
			
			case R.id.login_submit_btn:
				     checkLogin();
				break;
				
			case R.id.login_epc_btn:
			     //epcLogin();
			break;
			
			
			case R.id.login_cancle_btn:
			     activity.finish();
			break;
			
			case R.id.zk_pwd_delete:
				
				login_pwd_edit.setText("");
				
			break;
			
			case R.id.zk_login_delete:
				
				login_username_edit.setText("");
				break;
			default:
				break;
			}
		}
	};
	
	/**登录的用户名和密码框的输入初步检验**/
	private void checkLogin()
	{
		String userName=login_username_edit.getText().toString();
		String pwd=login_pwd_edit.getText().toString();
		if("".equals(userName))
		{
			 CommUtil.toastShow(activity.getString(R.string.login_username_empty), activity);
			 return;
		}
		
		if("".equals(pwd))
		{
			 CommUtil.toastShow(activity.getString(R.string.login_pwd_empty), activity);
			 return;
		}
		
//		int radioId=radioGroup.getCheckedRadioButtonId();
//		String userType="";
//		if(radioId==R.id.radioEmployee)
//		{		
//			userType="2";//员工
//		}else if(radioId==R.id.radioManager)
//		{	
//			userType="1";//管理员
//		}else
//		{
//			userType="3";//临时工		
//		}
		String array[]=new String[]{userName,pwd};
		Editor editor=CommUtil.getShare(activity).edit();
		editor.putString(ConfigUtil.USER_PASSWORD, MD5Util.getMd5(pwd));
		editor.commit();//先将密码写入到本地
		
		Message msg=Message.obtain();
		msg.what=ZKCmd.HAND_REQUEST_DATA;
		msg.obj=array;
		handler.sendMessage(msg);	
	}
	
	/**登录返回结果,解析数据，并根据结果控制操作 **/
	public void loginResult(String result)
	{

		try
		{
			String str=new JSONObject(result).getJSONObject("result").toString();
			Map<String,String> map=CommUtil.getJsonMap(str);
			
			String state=map.get("state");	
			String power=map.get("power");
			
			LogUtil.d("test","power="+power);
			
			if("1".equals(state))
			{
				//登录成功
				new InitApp(handler).checkLoginRes(map);
				
				Intent intent=new Intent();				
				intent.setClass(activity, MainActivity.class);//跳转让到主界面	
				SystemUtil.power = power;					
				activity.startActivity(intent);
				activity.finish();
				
			}else
			{
				//登录失败
				CommUtil.toastShow(activity.getString(R.string.login_fail), activity);
			}
						
		}catch(Exception e)
		{	
			e.printStackTrace();
			CommUtil.toastShow(activity.getString(R.string.login_fail_error), activity);
		}		
	}
	
	
	/**释放登录操作的单例模式对象**/
	public static void  clearWork() 
	{
		userWork=null;
	}
	
	/**显示用户基本信息,组装数据**/
	public static void showUserInfo(Activity activity)
	{	    
	    List<String[]> listData=new ArrayList<String[]>();
	    String[] array=new String[2];
	    array[0]="用户名";
	    array[1]=CommUtil.getShare(activity).getString(ConfigUtil.USER_NAME, "");
	    listData.add(array);
	    
	    array=new String[2];
	    array[0]="工号";
	    array[1]=CommUtil.getShare(activity).getString(ConfigUtil.USER_JOB_NAME, "");
	    listData.add(array);
	    
	    array=new String[2];
	    array[0]="姓名";
	    array[1]=CommUtil.getShare(activity).getString(ConfigUtil.USER_FULL_NAME, "");
	    listData.add(array);
	    
	    array=new String[2];
	    array[0]="角色";
	    String type=CommUtil.getShare(activity).getString(ConfigUtil.USER_TYPE, "");
	    if(ConfigUtil.USER_EMPLOYEE.equals(type))
	    {
	    	 array[1]="普通员工";
	    }else if(ConfigUtil.USER_MANAGER.equals(type))
	    {
	    	 array[1]="管理员";
	    }else if(ConfigUtil.USER_TEMP.equals(type))
	    {
	    	 array[1]="临时工";
	    }else
	    {
	    	 array[1]="未知";
	    }	
	    listData.add(array);
	    
	    array=new String[2];
	    array[0]="注册时间";
	    array[1]=CommUtil.getShare(activity).getString(ConfigUtil.USER_REG_TIME, "");
	    listData.add(array);
	  	    
	    createDialog(activity, listData);	    	    
	}
	

	/**显示用户基本信息对话框*/
	private static void createDialog(final Activity activity, List<String[]> listData)
	{	    
		final Dialog dialog=new Dialog(activity,R.style.dialogstyle);	
		dialog.setContentView(R.layout.userdetail);	
		LinearLayout userTable=(LinearLayout) dialog.findViewById(R.id.userTable);
		userTable=new DoubleTableView(activity).getViewList(listData, userTable);
		
		ImageView closeBtn= (ImageView) dialog.findViewById(R.id.closeBtn);
		closeBtn.setOnClickListener(new View.OnClickListener()
		{			
			@Override
			public void onClick(View arg0) 
			{
				dialog.cancel();				
			}
		});	
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
		
	}
		
}

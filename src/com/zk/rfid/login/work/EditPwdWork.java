package com.zk.rfid.login.work;

import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
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
import com.zk.rfid.login.activity.LoginActivity;
import com.zk.rfid.main.util.ConfigUtil;
import com.zk.rfid.util.CommUtil;
import com.zk.rfid.util.SysApplication;
import com.zk.rfid.util.ZKCmd;

/**
 * @Description: 修改密码业务处理
 * @date:2016-5-13 上午11:28:50
 * @author: ldl
 */
public class EditPwdWork
{
	private static final String TAG="EditPwdWork";
	
	private Activity activity;
	private Button edit_submit_btn,edit_clear_btn;
	private EditText curr_pwd_edit,login_pwd_new_edit,login_pwd_new_ag_edit;
	private ImageView zk_curr_delete,zk_new_delete,zk_newag_delete,closeEditBtn;
	
	private static EditPwdWork editPwdWork;
	private Handler handler;
	
	private EditPwdWork(Activity act)
	{
		activity=act;
	}
	
	/**获得修改密码操作的单例模式对象**/
	public static EditPwdWork getInstance(Activity act)
	{
		if(null==editPwdWork)
		{
			editPwdWork=new EditPwdWork(act);
		}		
		return editPwdWork;
	}
	
	/**初始化界面，并传入回调的handler**/
	public void initView(Handler handler)
	{
		this.handler=handler;
		edit_submit_btn=(Button) activity.findViewById(R.id.edit_submit_btn);
		edit_clear_btn =(Button) activity.findViewById(R.id.edit_clear_btn);
		
		curr_pwd_edit        =(EditText) activity.findViewById(R.id.curr_pwd_edit);		
		login_pwd_new_edit   =(EditText) activity.findViewById(R.id.login_pwd_new_edit);
		login_pwd_new_ag_edit=(EditText) activity.findViewById(R.id.login_pwd_new_ag_edit);
		zk_curr_delete =(ImageView) activity.findViewById(R.id.zk_curr_delete);
		zk_new_delete  =(ImageView) activity.findViewById(R.id.zk_new_delete);
		zk_newag_delete=(ImageView) activity.findViewById(R.id.zk_newag_delete);
		closeEditBtn=(ImageView) activity.findViewById(R.id.closeEditBtn);
		
		CommUtil.editTextChange(curr_pwd_edit, zk_curr_delete);
		CommUtil.editTextChange(login_pwd_new_edit, zk_new_delete);
		CommUtil.editTextChange(login_pwd_new_ag_edit, zk_newag_delete);
		//zk_curr_delete,zk_new_delete,zk_newag_delete
		setListen();
	}
	
	/**设置控制的监听**/
	private void setListen()
	{
		edit_submit_btn.setOnClickListener(onclickListen);
		edit_clear_btn.setOnClickListener(onclickListen);
		closeEditBtn.setOnClickListener(onclickListen);		
	}
	
    private OnClickListener onclickListen=new View.OnClickListener()
    {
    	public void onClick(View v) 
    	{
    		switch (v.getId()) 
    		{
			case R.id.edit_submit_btn:
				    submitEdit();
				break;
				
			case R.id.edit_clear_btn:
				 	curr_pwd_edit.setText("");	
				 	login_pwd_new_edit.setText("");	
				 	login_pwd_new_ag_edit.setText("");	
				break;
				
			case R.id.closeEditBtn:
					activity.finish();
				break;

			default:
				break;
			}
    	};
    };
	
    /**提交修改**/
    private void submitEdit()
    {
    	String currPwd=getEditValue(curr_pwd_edit);	
		String newPwd=getEditValue(login_pwd_new_edit);	
		String newAgPwd=getEditValue(login_pwd_new_ag_edit);
		if(currPwd.equals(""))
		{
			CommUtil.toastShow(activity.getString(R.string.old_pwd_not_allow_empty), activity);
			return;
		}else if(newPwd.equals(""))
		{
			CommUtil.toastShow(activity.getString(R.string.new_pwd_not_allow_empty), activity);
			return;

		}else if(newAgPwd.equals(""))
		{
			CommUtil.toastShow(activity.getString(R.string.new_pwd_ag_not_allow_empty), activity);
			return;
		}else if(!newPwd.equals(newAgPwd))
		{
			CommUtil.toastShow(activity.getString(R.string.new_ag_pwd_not_equals), activity);
			return;
		}
		
		String array[]=new String[3];
		Message msg=Message.obtain();
		msg.what=ZKCmd.HAND_REQUEST_DATA;
		array[0]=CommUtil.getShare(activity).getString(ConfigUtil.USER_NAME, "");
		array[1]=currPwd;
		array[2]=newPwd;
		msg.obj=array;
		handler.sendMessage(msg);
    }
    
    /**处理修改密码返回结果**/
	public void editPwdRes(String result)
	{
		try
		{
			String str=new JSONObject(result).getJSONObject("result").toString();
			Map<String,String> map=CommUtil.getJsonMap(str);
			
			String state=map.get("state");	
			String msg=map.get("msg");	
			if("1".equals(state))
			{
				//修改成功
				createDialog(1,msg);
			}else
			{
				//失败
				createDialog(0,msg);
			}						
		}catch(Exception e)
		{	
			createDialog(0,"发生异常");
		}		
	}
	
	/**修改密码结果，并提示相关信息**/
	private void createDialog(final int type,final String msg)
	{
		final Dialog dialog=new Dialog(activity,R.style.dialogstyle);	
		dialog.setContentView(R.layout.result_dialog);	
		LinearLayout result_fail_lin=(LinearLayout) dialog.findViewById(R.id.result_fail_lin);
		LinearLayout result_success_lin=(LinearLayout) dialog.findViewById(R.id.result_success_lin);
		TextView updateMsg=(TextView) dialog.findViewById(R.id.updateMsg);
		
		if(0==type)
		{
			result_fail_lin.setVisibility(View.VISIBLE);
			result_success_lin.setVisibility(View.GONE);
			updateMsg.setText(msg);
		}
		if(1==type)
		{
			result_fail_lin.setVisibility(View.GONE);
			result_success_lin.setVisibility(View.VISIBLE);
			updateMsg.setText("修改密码需要重新登录");
		}
		Button sure= (Button) dialog.findViewById(R.id.zk_sure_btn);
		sure.setOnClickListener(new View.OnClickListener()
		{			
			@Override
			public void onClick(View arg0) 
			{
				dialog.cancel();
				if(1==type)
				{
					Intent intent=new Intent();
					intent.setClass(activity, LoginActivity.class);
					activity.startActivity(intent);
					activity.finish();
					SysApplication.getInstance().exit();
				}
			}
		});	
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}
	
	private String getEditValue(EditText edit)
	{
		return edit.getText().toString();
	}
	
	/**退出时，释放该单例对象**/
	public static void clearEditPwdWork()
	{
		editPwdWork=null;
	}
}

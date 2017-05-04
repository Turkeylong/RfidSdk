package com.zk.rfid.main.activity;


import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zk.rfid.R;
import com.zk.rfid.comm.base.BaseActivity;
import com.zk.rfid.comm.base.CommTopView;
import com.zk.rfid.main.bean.EpcInfo;
import com.zk.rfid.main.util.ConfigUtil;
import com.zk.rfid.main.work.LendDetailWork;
import com.zk.rfid.util.CommUtil;
import com.zk.rfid.util.GetDataTask;
import com.zk.rfid.util.LogUtil;
import com.zk.rfid.util.ZKCmd;

/**
 * @Description: 借领详细界面
 * @date:2016年5月12日 上午9:57:21
 * @author: ldl
 */
public class LendDetailActivity extends BaseActivity
{
	private static final String TAG="LendDetailActivity";
	private Activity activity=LendDetailActivity.this;
	private LendDetailWork work;
	private EpcInfo epcInfo;
	private TextView detailtitle;
	private EditText audit_remarks;//申请备注
	private LinearLayout remarks_lin;

	private Handler handler=new Handler()
	{
		public void handleMessage(Message msg) 
		{
			switch (msg.what) 
			{
			
			case ZKCmd.HAND_REQUEST_DATA:
				
				//审核
				if(ZKCmd.ARG_AUDIT_APPLY==msg.arg1)
				{
					submitAudit(msg.arg2, msg.obj+"");
				}
								
				break;
				
			case ZKCmd.HAND_RESPONSE_DATA:
				
				String res=msg.obj+"";
				if(ZKCmd.ARG_GET_APPLY_DETAIL==msg.arg1)
				{
					work.showDataDetail(res,epcInfo);	
				}
				if(ZKCmd.ARG_AUDIT_APPLY==msg.arg1)
				{
					operateBackRes(res,msg.arg1);
				}
				
				//借出
				if(ZKCmd.ARG_REQUEST_LEND==msg.arg1)
				{
					operateBackRes(res,msg.arg1);
				}
				//归还
				if(ZKCmd.ARG_REQUEST_RETURN==msg.arg1)
				{
					operateBackRes(res,msg.arg1);
				}
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
		
		setContentView(R.layout.lenddetail);
		detailtitle=(TextView) findViewById(R.id.detailtitle);
		audit_remarks= (EditText) findViewById(R.id.audit_remarks);
		remarks_lin  = (LinearLayout) findViewById(R.id.remarks_lin);
		
		new CommTopView(activity);
		//信息
		work=LendDetailWork.getInstance(activity);
		work.initView(handler);		
		epcInfo =(EpcInfo) getIntent().getSerializableExtra("info");
		
		getDetail();
	}
	
	/**获得详细的借领情况**/
	private void getDetail()
	{
		if(null!=epcInfo && !ConfigUtil.WAIT_ADUIT.equals(epcInfo.getAppliedStatus()))
		{
			remarks_lin.setVisibility(View.GONE);
		}
		if(isConn())
		{		
			StringBuffer sb = new StringBuffer();
			sb.append("t=" + ZKCmd.REQ_LEND_DETAIL);
			sb.append("&appliedOrderNumber=").append(epcInfo.getAppliedOrderNumber());
			CommUtil.executeCommTask(activity, handler, ZKCmd.HAND_RESPONSE_DATA, ZKCmd.ARG_GET_APPLY_DETAIL, 0, sb.toString());
		}		
	}
		
	/**提交申请审核**/
	private void submitAudit(int deal,String appliedOrderNumber)
	{		
		if(isConn())
		{
			String re=audit_remarks.getText().toString();
			StringBuffer sb=new StringBuffer();
			sb.append("t="+ZKCmd.REQ_AUDIT_APPLY);	

			sb.append("&processStatus=").append(deal+"");//通过或者不通过		
			sb.append("&appliedOrderNumber=").append(appliedOrderNumber);   //记录ID,申请单号
			sb.append("&auditRemarks=").append(re);//如果是审核，就是审核的备注，如果是审批，就是审批的备注
			LogUtil.i(TAG, "审核:"+sb.toString());
			
			CommUtil.executeCommTask(activity,handler,ZKCmd.HAND_RESPONSE_DATA, ZKCmd.ARG_AUDIT_APPLY, 0, sb.toString());	
		}
	}
	
	/**借出归还提交**/
	private void lendBackSubmit(String param,int handCode)
	{
		if(isConn())
		{
			 GetDataTask task=new GetDataTask(activity, handler);
			 task.setHandlerCode(handCode);
			 task.setParams(param);
			 task.execute("");						
		}
	}
	
	/**操作提交返回结果,如果是领出，成功的则跳转让到扫描标签的界面**/
	private void operateBackRes(String res,int type)
	{
		try 
		{
			JSONObject json=new JSONObject(res).getJSONObject("result");
			String state=json.optString("state", "");
			String msg=json.optString("msg", "");
			CommUtil.toastShow(msg, activity);
			if("1".equals(state))
			{
				setResult(ZKCmd.SEND_RES_CODE_RE_QUERY);
				if(type==ZKCmd.ARG_REQUEST_LEND)
				{
					Intent intent=new Intent();
					intent.setClass(activity, ModifyBoxActivity.class);
					startActivity(intent);
				}
				finish();//如果成功则关闭界面				
			}
		} catch (JSONException e) 
		{		
			CommUtil.toastShow("操作发生异常", activity);
			e.printStackTrace();
		}		
	}
			
	@Override
	protected void onDestroy() 
	{
		super.onDestroy();
		LendDetailWork.clearWork();
	}
	
	 public boolean onKeyDown(int keyCode, KeyEvent event) 
	 {		  
		 if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN)
		 {   
		    setResult(0);//物理返回 键则不需要重新请求并刷新界面   		        
		 }		 
		 return super.onKeyDown(keyCode, event);
	 }
 	
}

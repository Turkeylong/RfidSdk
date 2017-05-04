package com.zk.rfid.main.activity;


import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.zk.rfid.R;
import com.zk.rfid.comm.base.BaseActivity;
import com.zk.rfid.comm.base.CommTopView;
import com.zk.rfid.main.bean.EpcInfo;
import com.zk.rfid.main.dao.ApplyMaterialView;
import com.zk.rfid.main.util.ConfigUtil;
import com.zk.rfid.util.CommUtil;
import com.zk.rfid.util.LogUtil;
import com.zk.rfid.util.ZKCmd;

/**
 * @Description: 申请物资确认页
 * @date:2016年6月30日 上午9:57:21
 * @author: ldl
 */
public class ApplyMaterialActivity extends BaseActivity
{
	private static final String TAG="ApplyMaterialActivity";
	private Activity activity=ApplyMaterialActivity.this;
	private Button apply_submit;
	private EditText apply_remarks,use_purpose;
	private LinearLayout addLin;
	
	
	private List<View> listMatView;
		
	private Handler handler=new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch (msg.what) 
			{
				
			case ZKCmd.HAND_RESPONSE_DATA:
				
				if(ZKCmd.ARG_REQUEST_APPLY_MATERIAL==msg.arg1)
				{
					applyResult(msg.obj+"");
				}
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
        
		setContentView(R.layout.apply_material);
		initTopView();
		
		initView();
		initData();		 
	}
	
	/**初始化控件**/
	private void initView()
	{
		addLin=(LinearLayout) findViewById(R.id.addLin);
		apply_submit=(Button) findViewById(R.id.apply_submit);
		apply_remarks=(EditText) findViewById(R.id.apply_remarks);
		use_purpose=(EditText) findViewById(R.id.use_purpose);
		
		apply_submit.setOnClickListener(new View.OnClickListener() 
		{			
			@Override
			public void onClick(View v)
			{		
				if(listMatView.size()==0)
				{
					CommUtil.toastShow("请选择需要申请的物资", activity);
				}else
				{
					submitApply();	
				}			
			}
		});
		listMatView=new ArrayList<View>();
	}

	/**初始显示数据，根据传入的数据对象以列表形式显示**/
	private void initData()
	{
		EpcInfo info=(EpcInfo) getIntent().getSerializableExtra(ConfigUtil.APPLY_MATERIAL_LIST);
		List<EpcInfo> list=info.getListEpc();
				
		if(null!=list && list.size()>0)
		{
			int len=list.size();
			//LogUtil.i(TAG, "多少条:"+list.size());
			View view=null;
			for(int i=0;i<len;i++)
			{
				 view=new ApplyMaterialView(activity, handler).getView(list.get(i), listMatView);
				 addLin.addView(view);
			}			
		}else
		{
			LogUtil.i(TAG, "没有数据");
			
		}
	}
	
	/**提交申请**/
	private void submitApply()
	{
		String purpose=use_purpose.getText().toString();
		if("".equals(purpose))
		{
			CommUtil.toastShow("用途不能为空", activity);
			return;
		}
		int len=listMatView.size();//从添加的所有view中取出值获得数据
		View view=null;
		ApplyMaterialView amv=new ApplyMaterialView(activity, handler);
		
		JSONObject json=null;
		JSONArray jsonArray=new JSONArray();
		boolean flag=true;
		for(int i=0;i<len;i++)
		{
			view=listMatView.get(i);
			json=amv.getSubmitDataByView(view);
			
			if(null==json)
			{
				flag=false;
				break;//有不合条件的参数，未填全等原因,跳出
			}else
			{
				//LogUtil.i(TAG, "参数:"+json.toString());
				jsonArray.put(json);//通过则加入
			}		
		}
		if(flag)
		{
			StringBuffer sb=new StringBuffer();
			sb.append("t="+ZKCmd.REQ_APPLY_MATERIAL);
			sb.append("&materialList=").append(jsonArray.toString());
			LogUtil.i(TAG, "数组:"+jsonArray.toString());
			sb.append("&appliedRemarks=").append(apply_remarks.getText().toString());
			sb.append("&appliedPurpose=").append(use_purpose.getText().toString());
			sb.append("&appliedJobNumber=").append(CommUtil.getShare(activity).getString(ConfigUtil.USER_JOB_NAME, ""));//申请人
				
			CommUtil.executeCommTask(activity,handler,ZKCmd.HAND_RESPONSE_DATA, ZKCmd.ARG_REQUEST_APPLY_MATERIAL, 0, sb.toString());	
		}		
	}
	
	/**解析申请结果**/
	private void applyResult(String res)
	{
		try
		{
			JSONObject json=new JSONObject(res).getJSONObject("result");
			String state=json.optString("state", "0");
			if("1".equals(state))
			{
				CommUtil.toastShow("申请成功，等待审核", activity);
				finish();
			}else							
			{
				CommUtil.toastShow("申请失败", activity);
			}
		}catch(Exception e)
		{
			LogUtil.e(TAG, "申请结果异常:"+e.getMessage());
			CommUtil.toastShow("操作结果异常，请稍后再试", activity);
		}
	}
	
	/**执行公用顶部返回操作**/
	private void initTopView() 
	{
		new CommTopView(activity);    	    		
	}
	

		
	@Override
	protected void onDestroy()
	{
		//退出时清空
		super.onDestroy();
	}
			
}

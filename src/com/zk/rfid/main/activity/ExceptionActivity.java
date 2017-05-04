package com.zk.rfid.main.activity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.zk.rfid.R;
import com.zk.rfid.comm.base.CommTopView;
import com.zk.rfid.comm.listvew.CustomListView;
import com.zk.rfid.main.adapter.LendListAdapter;
import com.zk.rfid.main.adapter.MaterialListAdapter;
import com.zk.rfid.main.bean.EpcInfo;
import com.zk.rfid.util.LogUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ExceptionActivity extends Activity {
	private Activity activity = ExceptionActivity.this;
	private static final String TAG = "ExceptionActivity";
	private ArrayList<EpcInfo> listInfo = null;
	private String title;
	private TextView storemaptitle,showCounText,tipsText;
	private LinearLayout material_empty_record_lin,material_empty_list_lin;
	private CustomListView material_empty_list;
	private MaterialListAdapter listAdapter = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		super.onCreate(savedInstanceState);
		
		Window window = getWindow();  
        WindowManager.LayoutParams params = window.getAttributes();  
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE;
        window.setAttributes(params);
        
        Intent intent = getIntent();
        
//        listInfo =  (ArrayList<EpcInfo>) getIntent().getSerializableExtra(this.getString(R.string.remindNullWarehouse));
//        title = this.getString(R.string.remindNullWarehouse);
//        LogUtil.e(TAG, this.getString(R.string.remindNullWarehouse)+listObj);

        if(null == intent.getSerializableExtra(this.getString(R.string.remindAlarmWarehouse)))
        {
        	if(null == intent.getSerializableExtra(this.getString(R.string.remindNullWarehouse)))
        	{
        		if(null == intent.getSerializableExtra(this.getString(R.string.remindChangeEpcCount)))
        		{
        			if(null == intent.getSerializableExtra(this.getString(R.string.remindWriteEpc)))
        			{
        				LogUtil.e(TAG, "没有获取到列表参数");
        			}
        			else
        			{
        				listInfo = (ArrayList<EpcInfo>) intent.getSerializableExtra(this.getString(R.string.remindWriteEpc));
        				title = this.getString(R.string.remindWriteEpc);
        			}
        		}
        		else
        		{
        			listInfo = (ArrayList<EpcInfo>) intent.getSerializableExtra(this.getString(R.string.remindChangeEpcCount));
        			title = this.getString(R.string.remindChangeEpcCount);
        		}
        	}
        	else
        	{
        		listInfo = (ArrayList<EpcInfo>) intent.getSerializableExtra(this.getString(R.string.remindNullWarehouse));
        		title = this.getString(R.string.remindNullWarehouse);
        	}
        }
        else
        {
        	listInfo = (ArrayList<EpcInfo>) intent.getSerializableExtra(this.getString(R.string.remindAlarmWarehouse));
        	title = this.getString(R.string.remindAlarmWarehouse);
        }
        
        initView();
        initTopView();
        showListInfo();
	}
	
	/**执行公用顶部返回操作**/
	private void initTopView()
	{
		new CommTopView(activity);
	}

	private void initView() {
		// TODO 自动生成的方法存根
		LayoutInflater inflater = LayoutInflater.from(activity);
		setContentView(inflater.inflate(R.layout.exceptioninfo, null));
		
		tipsText = (TextView) activity.findViewById(R.id.typeText);
		
		storemaptitle = (TextView) activity.findViewById(R.id.storemaptitle);
		storemaptitle.setText(title);
		
		showCounText = (TextView) activity.findViewById(R.id.showCounText);

		material_empty_list = (CustomListView) activity.findViewById(R.id.add_record_list);
		
		material_empty_record_lin= (LinearLayout) activity.findViewById(R.id.material_empty_record_lin);
		material_empty_list_lin= (LinearLayout) activity.findViewById(R.id.material_empty_list_lin);
		
		}
	
	private void showListInfo() {
		// TODO 自动生成的方法存根
        if(listInfo!=null)
        {
    		material_empty_record_lin.setVisibility(View.VISIBLE);
    		material_empty_list_lin.setVisibility(View.VISIBLE);
    		
    		if(title.equals(this.getString(R.string.remindNullWarehouse)))
    		{
    			tipsText.setText("性质");
    			listAdapter = new MaterialListAdapter(activity,1, listInfo);
    		}
    		else
    		{
    			tipsText.setText("序号");
    			listAdapter = new MaterialListAdapter(activity,0, listInfo);
    		}
    		material_empty_list.setAdapter(listAdapter);
    		
    		showCounText.setText("记录总数共"+listInfo.size()+"条，当前"+listAdapter.getCount() +"条");
        	
//        	Iterator it1 = listInfo.iterator();
//        	while(it1.hasNext())
//        	{
//        		EpcInfo item = (EpcInfo) it1.next();
//        		LogUtil.d(TAG, "MaterialId="+item.getMaterialId()+",MaterialId="+item.getMaterialName()+",MaterialId="+item.getLocation());
//        	}
        }
	}
}

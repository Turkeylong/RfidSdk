package com.zk.rfid.main.work;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Handler;
import android.view.View;
import android.widget.ListView;

import com.zk.rfid.R;
import com.zk.rfid.main.adapter.LeftSetListAdapter;
import com.zk.rfid.util.CommUtil;
import com.zk.rfid.util.ZKCmd;

/**
 * @Description: 左侧界面
 * @date:2016-5-31上午11:38:31
 * @author: ldl
 */
public class LeftWork
{
	private static final String TAG="LeftWork";
	private static LeftWork work;
	private View leftView;
	private Activity activity;
	private Handler handler;
	private ListView left_listview;

	private LeftWork(Activity act)
	{
		activity=act;	
	}


	public static LeftWork getInstance(Activity act)
	{
		if(null==work)
		{
			work=new LeftWork(act);
		}		
		return work;
	}
	
	public void initView(View view,Handler handler)
	{
		this.handler=handler;
		this.leftView=view;		
		
		left_listview=(ListView) leftView.findViewById(R.id.left_listview);
	}
	
	public void setLeftData(List<Map<String,Object>> list)
	{
		LeftSetListAdapter listAdapter=new LeftSetListAdapter(activity,handler, list);
		left_listview.setAdapter(listAdapter);	
		CommUtil.setListViewHeight(left_listview);
	}
		
	
//	private void getEpcValue()
//	{
//		handler.sendEmptyMessage(ZKCmd.HAND_REQUEST_EPC_DATA);
//	}
//	
//	private void writeEpcValue()
//	{
//		handler.sendEmptyMessage(ZKCmd.HAND_WRITE_EPC_DATA);
//	}
	
	public static void clearLeftWork()
	{
		work=null;
	}
	
	
}

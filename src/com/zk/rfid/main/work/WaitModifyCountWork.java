package com.zk.rfid.main.work;

import java.util.List;

import android.app.Activity;
import android.os.Handler;
import android.widget.ListView;
import android.widget.TextView;

import com.zk.rfid.R;
import com.zk.rfid.main.adapter.WaitModifyAdapter;
import com.zk.rfid.main.bean.EpcInfo;
import com.zk.rfid.main.dao.ManagerDao;
import com.zk.rfid.main.util.ConfigUtil;
import com.zk.rfid.util.CommUtil;

/**
 * @Description: 需要修改数量提醒操作
 * @date:2016-9-21下午4:28:41
 * @author: ldl
 */
public class WaitModifyCountWork
{
	private static final String TAG = "WaitModifyCountWork";
	private Handler handler;
	private Activity activity;
    private static WaitModifyCountWork waitWork;
    private WaitModifyAdapter modifyAdapter;
    private ListView modify_list_view;
    private TextView showCounText;

	
    private WaitModifyCountWork(Activity activity)
    {
    	this.activity=activity;
    }
    public static WaitModifyCountWork getInstance(Activity activity)
    {
    	if(null==waitWork)
    	{
    		waitWork=new WaitModifyCountWork(activity);
    	}
    	return waitWork;
    }
    
   
    public void initView(final Handler handler)
    {
    	this.handler=handler;
    	showCounText=(TextView) activity.findViewById(R.id.showCounText);
    	modify_list_view=(ListView) activity.findViewById(R.id.modify_list_view);
    	
    	String res=CommUtil.getShare(activity).getString(ConfigUtil.NEED_MODIFY_DATA, "");
    	if(!"".equals(res))
    	{
    		List<EpcInfo> list=new ManagerDao(activity, handler).getNeedModifyDataList(res);
    		if(null!=list && list.size()>0)
    		{
    			modifyAdapter=new WaitModifyAdapter(activity, handler, list);
    			modify_list_view.setAdapter(modifyAdapter);
    			showCounText.setText("共"+modifyAdapter.getCount()+"条记录");
    		}else
    		{
    			CommUtil.toastShow("获取修改数据发生异常", activity);
    			showCounText.setText("共0条记录");
    		}
    	}else
    	{
    		CommUtil.toastShow("暂无需要修改数据的信息,请稍后再尝试", activity);
    		showCounText.setText("共0条记录");
    	}
    }
    
	public static void clearWork()
	{
		waitWork=null;	
	}
}

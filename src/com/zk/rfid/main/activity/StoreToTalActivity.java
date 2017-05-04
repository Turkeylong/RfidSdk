package com.zk.rfid.main.activity;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.zk.rfid.R;
import com.zk.rfid.comm.base.BaseActivity;
import com.zk.rfid.comm.base.CommTopView;
import com.zk.rfid.main.bean.EpcInfo;
import com.zk.rfid.main.dao.ManagerDao;
import com.zk.rfid.main.work.StoreTotalWork;
import com.zk.rfid.util.CommUtil;
import com.zk.rfid.util.GetDataTask;
import com.zk.rfid.util.LogUtil;
import com.zk.rfid.util.ZKCmd;

/**
 * @Description: 库存列表
 * @date:2016年6月29日 上午9:57:21
 * @author: ldl
 */
public class StoreToTalActivity extends BaseActivity
{
	private static final String TAG = "StoreToTalActivity";
	private Activity activity = StoreToTalActivity.this;

	private StoreTotalWork stWork;
	private String dataType;
	private ManagerDao managerDao;

	private Handler handler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{

			case ZKCmd.HAND_REQUEST_DATA:

				if (msg.arg1 == ZKCmd.ARG_REQUEST_STOAGE)
				{
					getDataTask(msg.obj + "", msg.arg1, msg.arg2);
				}

				break;

			case ZKCmd.HAND_RESPONSE_DATA:

				if (msg.arg1 == ZKCmd.ARG_REQUEST_STOAGE)
				{
					showData(msg.obj + "",msg.arg2);
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
		
		setContentView(R.layout.store_total);
		dataType = getIntent().getStringExtra("type");

		initTopView();
		initView();
		initData();
	}

	/**执行公用顶部返回操作**/
	private void initTopView()
	{
		new CommTopView(activity);
	}

	/**初始化控件**/
	private void initView()
	{
		stWork = StoreTotalWork.getInstance(activity);
		stWork.initView(handler, dataType);
	}

	/**初始化数据，自动执行查询**/
	private void initData()
	{
		stWork.autoQuery();// 自动执行
	}

	/**显示获得服务器结果**/
	private void showData(String res,int arg2)
	{
		try
		{
			if(null==managerDao)
			{
				managerDao = new ManagerDao(activity, handler);	
			}
			
			List<EpcInfo> list = managerDao.getStorageList(res);
			LogUtil.i(TAG, "数据长度:" + list.size());
			
			if(0==arg2)
			{
				stWork.showStoreData(list);	
			}else
			{
				stWork.showInfoByLoad(list,arg2);	
			}					
		} catch (Exception e)
		{
			CommUtil.toastShow("服务器返回数据格式异常", activity);
			e.printStackTrace();
			LogUtil.e(TAG, "解析数据异常:" + e.getMessage());
		}
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		StoreTotalWork.clearWork();
	}

	/**请求服务器共用线程**/
	private void getDataTask(String param, int arg1, int arg2)
	{
		GetDataTask task = new GetDataTask(activity, handler);
		task.setHandlerCode(ZKCmd.HAND_RESPONSE_DATA);
		task.setParams(param);
		task.setArg1(arg1);
		task.setArg2(arg2);
		task.execute("");
	}

}

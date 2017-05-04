package com.zk.rfid.main.activity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.zk.rfid.R;
import com.zk.rfid.comm.base.BaseActivity;
import com.zk.rfid.comm.scro.HoScrollView;
import com.zk.rfid.comm.scro.SizeCallbackForMenu;
import com.zk.rfid.init.InitApp;
import com.zk.rfid.main.bean.EpcInfo;
import com.zk.rfid.main.dao.ManagerDao;
import com.zk.rfid.main.util.ConfigUtil;
import com.zk.rfid.main.work.EpcWork;
import com.zk.rfid.main.work.LeftWork;
import com.zk.rfid.main.work.ManagerWork;
import com.zk.rfid.util.CommUtil;
import com.zk.rfid.util.GetDataThread;
import com.zk.rfid.util.LogUtil;
import com.zk.rfid.util.SystemUtil;
import com.zk.rfid.util.ZKCmd;

/**
 * @Description: 主界面
 * @date:2016年5月12日 上午9:57:21
 * @author: ldl
 */
public class MainActivity extends BaseActivity
{
	private static final String TAG = "MainActivity";
	private Activity activity = MainActivity.this;
	private InitApp initApp;

	private ManagerWork managerWork;
	private LeftWork leftWork;
	private HoScrollView scrollView;
	private View leftView, mainView;

	private static List<EpcInfo> APPLY_MATERIAL_LIST;

	private ManagerDao managerDao;

	private Handler handler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case ZKCmd.HAND_REQUEST_EXCEPTION:
			{
				if (msg.arg1 == ZKCmd.ARG_GET_EXCEPTION_INFO)
				{
					String param = msg.obj + "";//
					getException(param);
				}

				break;
			}
			case ZKCmd.HAND_REQUEST_DATA:

				if (isConn())
				{
					getData(msg);
				}
				break;

			case ZKCmd.HAND_RESPONSE_DATA:

				if (ZKCmd.ARG_GET_POS_RESPONSE == msg.arg1)
				{
					initApp.setLocationInfo(msg.obj + "");
				} else if (ZKCmd.ARG_GET_NEED_MODIFY_DATA == msg.arg1)
				{
					initApp.setModifyInfo(msg.obj + "");
				} else
				{
					showData(msg);
				}
				break;

			case ZKCmd.HAND_APPLY_SUCCESS_RES:

				managerWork.reQuery(); // 重新触发查询更新列表UI
				break;

			case ZKCmd.HAND_NOTIFY_DATA_CHANGE:

				managerWork.showCountText(ZKCmd.HAND_NOTIFY_DATA_CHANGE);

				break;
			case ZKCmd.HAND_NOTIFY_DATA_RETURN:

				managerWork.showCountText(ZKCmd.HAND_NOTIFY_DATA_RETURN);

				break;

			case ZKCmd.HAND_LEFT_ONCLICK:

				leftSetOnClick(msg.obj + "");
				break;

			case ZKCmd.HAND_APPLY_JOIN:

				addApplyList((EpcInfo) msg.obj);
				break;

			case ZKCmd.HAND_APPLY_SUBMIT:

				if (null != APPLY_MATERIAL_LIST && APPLY_MATERIAL_LIST.size() > 0)
				{
					// 提交申请，跳到确认页
					Intent intent = new Intent();
					intent.setClass(activity, ApplyMaterialActivity.class);
					EpcInfo info = new EpcInfo();
					info.setListEpc(APPLY_MATERIAL_LIST);
					intent.putExtra(ConfigUtil.APPLY_MATERIAL_LIST, info);
					startActivity(intent);
				} else
				{
					CommUtil.toastShow("请先加入", activity);
				}
				break;

			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Window window = getWindow();  
        WindowManager.LayoutParams params = window.getAttributes();  
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE;
        window.setAttributes(params);
		// setContentView(R.layout.mainview);
		initApp = new InitApp(handler);
		initView();
		initData();
	}

	protected void getException(String result) {
		// TODO 自动生成的方法存根
		try
		{
			JSONObject json = new JSONObject(result).getJSONObject("result");
			
			//LogUtil.i(TAG, "获取异常信息："+json.toString());
			
			JSONArray exceptionAlarmWarehouse = json.getJSONArray("ExceptionAlarmWarehouse");
			
			JSONArray exceptionNullWarehouse = json.getJSONArray("ExceptionNullWarehouse");

			JSONArray exceptionChangeEpcCount = json.getJSONArray("ExceptionChangeEpcCount");
			
			JSONArray exceptionWriteEpc = json.getJSONArray("ExceptionWriteEpc");

			//LogUtil.i(TAG, "异常出库提醒："+exceptionAlarmWarehouse.toString());
			
			if(exceptionAlarmWarehouse.length() > 0)
			{
				//LogUtil.i(TAG, "异常出库提醒："+exceptionAlarmWarehouse.toString());
				ArrayList<EpcInfo> list=new ArrayList<EpcInfo>();
				for(int i=0;i<exceptionAlarmWarehouse.length();i++)
				{
					JSONObject epc = exceptionAlarmWarehouse.getJSONObject(i);
					//LogUtil.i(TAG, "JSONObject："+epc.toString());
					EpcInfo epc_info = new EpcInfo();
					epc_info.setMaterialId(epc.getString("materialId"));
					epc_info.setMaterialName(epc.getString("materialName"));
					epc_info.setMaterialCount(epc.getString("materialCount"));
					//epc_info.set(epc.getString("materialGroup"));
					epc_info.setMaterialUnit(epc.getString("materialUnit"));
					epc_info.setMaterialType(epc.getString("materialType"));
					epc_info.setLocation(epc.getString("location"));
					epc_info.setDeliveryOrderNumber(epc.getString("deliveryOrderNumber"));
					epc_info.setMaterialEmergency(epc.getString("materialEmergency"));
					epc_info.setIsBox(epc.getString("isBox"));
					epc_info.setIsInBox(epc.getString("isInBox"));
					epc_info.setMaterialSpec(epc.getString("materialSpec"));
					epc_info.setSerialNumber(epc.getString("materialSn"));
					epc_info.setEpcCode(epc.getString("epc_code"));
					list.add(epc_info);
				}
				SystemUtil.showDefaultNotification(activity,0,this.getString(R.string.remindAlarmWarehouse),"您有"+exceptionAlarmWarehouse.length()+"多项物资异常出库,请注意。",list);
			}
			if(exceptionNullWarehouse.length() > 0)
			{
				//LogUtil.i(TAG, "物资为零提醒："+exceptionNullWarehouse.toString());
				ArrayList<EpcInfo> list=new ArrayList<EpcInfo>();
				for(int i=0;i<exceptionNullWarehouse.length();i++)
				{
					JSONObject epc = exceptionNullWarehouse.getJSONObject(i);
					//LogUtil.i(TAG, "JSONObject："+epc.toString());
					EpcInfo epc_info = new EpcInfo();
					epc_info.setMaterialId(epc.getString("materialId"));
					epc_info.setMaterialName(epc.getString("materialName"));
					epc_info.setMaterialCount(epc.getString("materialTotalCount"));
					//epc_info.set(epc.getString("materialGroup"));
					epc_info.setMaterialUnit(epc.getString("materialUnit"));
					epc_info.setMaterialType(epc.getString("materialType"));
					epc_info.setLocation(epc.getString("locationOrder"));
					epc_info.setMaterialSpec(epc.getString("materialSpec"));
					epc_info.setMaterialEmergency(epc.getString("materialEmergency"));
					epc_info.setSerialNumber(epc.getString("materialSn"));
					list.add(epc_info);
				}
				SystemUtil.showDefaultNotification(activity,1,this.getString(R.string.remindNullWarehouse),"您有"+exceptionNullWarehouse.length()+"多项物资已无库存,请注意。",list);
			}
			if(exceptionWriteEpc.length() > 0)
			{
				//LogUtil.i(TAG, "物资验收提醒："+exceptionWriteEpc.toString());
				ArrayList<EpcInfo> list=new ArrayList<EpcInfo>();
				for(int i=0;i<exceptionWriteEpc.length();i++)
				{
					JSONObject epc = exceptionWriteEpc.getJSONObject(i);
					//LogUtil.i(TAG, "JSONObject："+epc.toString());
					EpcInfo epc_info = new EpcInfo();
					epc_info.setMaterialId(epc.getString("materialId"));
					epc_info.setMaterialName(epc.getString("materialName"));
					epc_info.setMaterialCount(epc.getString("materialCount"));
					//epc_info.set(epc.getString("materialGroup"));
					epc_info.setMaterialUnit(epc.getString("materialUnit"));
					epc_info.setMaterialType(epc.getString("materialType"));
					epc_info.setLocation(epc.getString("location"));
					epc_info.setDeliveryOrderNumber(epc.getString("deliveryOrderNumber"));
					epc_info.setMaterialEmergency(epc.getString("materialEmergency"));
					epc_info.setIsBox(epc.getString("isBox"));
					epc_info.setIsInBox(epc.getString("isInBox"));
					epc_info.setMaterialSpec(epc.getString("materialSpec"));
					epc_info.setSerialNumber(epc.getString("materialSn"));
					epc_info.setEpcCode(epc.getString("epc_code"));
					list.add(epc_info);
				}
				SystemUtil.showDefaultNotification(activity,2,this.getString(R.string.remindChangeEpcCount),"您有"+exceptionWriteEpc.length()+"多项物资需要验收贴标签,请注意。",list);
			}
			if(exceptionChangeEpcCount.length() > 0)
			{
				//LogUtil.i(TAG, "出库确认提醒："+exceptionChangeEpcCount.toString());
				ArrayList<EpcInfo> list=new ArrayList<EpcInfo>();
				for(int i=0;i<exceptionChangeEpcCount.length();i++)
				{
					JSONObject epc = exceptionNullWarehouse.getJSONObject(i);
					//LogUtil.i(TAG, "JSONObject："+epc.toString());
					EpcInfo epc_info = new EpcInfo();
					epc_info.setMaterialId(epc.getString("materialId"));
					epc_info.setMaterialName(epc.getString("materialName"));
					epc_info.setMaterialCount(epc.getString("materialCount"));
					//epc_info.set(epc.getString("materialGroup"));
					epc_info.setMaterialUnit(epc.getString("materialUnit"));
					epc_info.setMaterialType(epc.getString("materialType"));
					epc_info.setLocation(epc.getString("location"));
					epc_info.setDeliveryOrderNumber(epc.getString("deliveryOrderNumber"));
					epc_info.setMaterialEmergency(epc.getString("materialEmergency"));
					epc_info.setIsBox(epc.getString("isBox"));
					epc_info.setIsInBox(epc.getString("isInBox"));
					epc_info.setMaterialSpec(epc.getString("materialSpec"));
					epc_info.setSerialNumber(epc.getString("materialSn"));
					epc_info.setEpcCode(epc.getString("epc_code"));
					list.add(epc_info);
				}
				SystemUtil.showDefaultNotification(activity,3,this.getString(R.string.remindWriteEpc),"您有"+exceptionChangeEpcCount.length()+"多项物资出库后未用手持机确认修改数量,请注意。",list);
			}
		} 
		catch (Exception e)
		{
			CommUtil.toastShow("获取新EPC标签码发生异常", activity);
		}
	}

	/** 左侧菜单点击事件 **/
	private void leftSetOnClick(String type)
	{
		if (ConfigUtil.LEFT_MY_RECORD.equals(type))
		{
			if (isConn())
			{
				String param = managerWork.getMyRecordDataParam();
				managerDao = new ManagerDao(activity, handler);
				managerDao.getLendData(param);
			}
		}

		if (ConfigUtil.LEFT_MY_INFO.equals(type))
		{
			if (isConn())
			{
				String param = managerWork.getMyInfoDataParam();
				managerDao = new ManagerDao(activity, handler);
				managerDao.getLendData(param);
			}
		}
	}

	/** 初始化界面，由左菜单和主界面组成 **/
	private void initView()
	{
		APPLY_MATERIAL_LIST = new ArrayList<EpcInfo>();// 初始化，加入物资时，将其添加到此，申请时从中读取

		LayoutInflater inflater = LayoutInflater.from(activity);
		setContentView(inflater.inflate(R.layout.mainview_total, null));

		scrollView = (HoScrollView) findViewById(R.id.scrollView);
		mainView = inflater.inflate(R.layout.mainview, null);//主界面

		leftView = inflater.inflate(R.layout.left_set_listview, null);//左侧菜单
		View[] children = new View[]
		{ leftView, mainView };//将两个子view添加进

		View view = leftView.findViewById(R.id.left_vie_lin);
		scrollView.setLeftMenuArgs(3);//设置左菜单占用屏幕的1/3
		scrollView.initViews(children, 1, new SizeCallbackForMenu(view));//设定默认显示子view,下标从0开始

		Button btn = (Button) mainView.findViewById(R.id.menu_img);//设置左上角点击事件，点击显示左侧菜单
		btn.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				scrollView.moveToLeft(scrollView, leftView);//滑动到左边
			}
		});
	}

	/** 初始化数据，左侧菜单，及请求获得需要修改标签的数据，和获取仓库物资架上的箱子和散件摆放情况 **/
	private void initData()
	{
		managerWork = ManagerWork.getInstance(activity);
		managerWork.initView(handler, mainView);

		leftWork = LeftWork.getInstance(activity);
		leftWork.initView(leftView, handler);
		managerDao = new ManagerDao(activity, handler);
		leftWork.setLeftData(managerDao.getLeftData(activity));

		initApp.getStorageBoxData();
		initApp.getNeedModifyData();
		
		GetDataThread task2 = new GetDataThread(activity, handler);
		StringBuffer sb2 = new StringBuffer();
		sb2.append("t=" + ZKCmd.REQ_TIPS_Exception);
		task2.setHandlerCode(ZKCmd.HAND_REQUEST_EXCEPTION);
		task2.setParams(sb2.toString());
		task2.setArg1(ZKCmd.ARG_GET_EXCEPTION_INFO);
		task2.start();
	}

	private void addApplyList(EpcInfo info)
	{
		// 点加入，将全部加到list中,如果已经有重复的，就不要再添加
		int len = APPLY_MATERIAL_LIST.size();
		if (len == 0)
		{
			APPLY_MATERIAL_LIST.add(info);
		}
		if (len > 0)
		{
			String mateCode = info.getMaterialId();
			String code = null;
			boolean flag = true;
			for (int i = 0; i < len; i++)
			{
				code = APPLY_MATERIAL_LIST.get(i).getMaterialId();
				if (null != mateCode && mateCode.equals(code))
				{
					flag = false;
					break;// 有重复的不要再加添
				}
			}

			if (flag)
			{
				APPLY_MATERIAL_LIST.add(info);
			}
		}
	};

	@Override
	protected void onDestroy()
	{
		// 退出时清空
		ManagerWork.clearManagerWork();
		LeftWork.clearLeftWork();
		EpcWork.clearEpcWork();
		APPLY_MATERIAL_LIST = null;
		super.onDestroy();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		return scrollView.onTouchEvent(event, scrollView, leftView);
	}

	private long exitTime = 0;

	public boolean onKeyDown(int keyCode, KeyEvent event)
	{

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN)
		{
			if ((System.currentTimeMillis() - exitTime) > 2000)
			{
				String msg = getString(R.string.exit_text);
				CommUtil.toastShow(msg, activity);
				exitTime = System.currentTimeMillis();
				return false;
			} 
			else
			{
				//SysApplication.getInstance().exit();
				//System.exit(0);
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	private void getData(Message msg)
	{
		String param = msg.obj + "";
		
		Intent intent = getIntent();
		
		managerDao = new ManagerDao(activity, handler);

		if (ZKCmd.ARG_REQUEST_LEND == msg.arg1)
		{
			if (0 == msg.arg2)
			{
				managerDao.getLendData(param);
			} else
			{
				geDataThread(msg.arg1, msg.arg2, param);
			}
		}

		if (ZKCmd.ARG_REQUEST_STOAGE == msg.arg1)
		{
			if (0 == msg.arg2)
			{
				managerDao.getStorageData(param);
			} else
			{
				geDataThread(msg.arg1, msg.arg2, param);
			}
		}
		managerDao = null;
	}

	private void showData(Message msg)
	{
		String res = msg.obj + "";
		int arg1 = msg.arg1;
		int arg2 = msg.arg2;

		managerDao = new ManagerDao(activity, handler);
		if (ZKCmd.ARG_REQUEST_LEND == arg1)
		{
			if (0 == arg2)
			{
				managerWork.showLendInfo(managerDao.getLendList(res));
			} else
			{
				managerWork.showLendInfoByLoad(managerDao.getLendList(res), arg2);
			}
		}
		if (ZKCmd.ARG_REQUEST_STOAGE == arg1)
		{
			if (0 == arg2)
			{
				managerWork.showStoreInfo(managerDao.getStorageList(res));
			} else
			{
				managerWork.showStoreInfoByLoad(managerDao.getStorageList(res), arg2);
			}
		}

		managerDao = null;
	}

	private void geDataThread(int arg1, int arg2, String param)
	{
		GetDataThread thread = new GetDataThread(activity, handler);
		thread.setParams(param);
		thread.setHandlerCode(ZKCmd.HAND_RESPONSE_DATA);
		thread.setArg1(arg1);
		thread.setArg2(arg2);
		thread.start();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		LogUtil.i(TAG, "收到回调:" + resultCode);
		if (ZKCmd.SEND_RES_CODE_RE_QUERY == resultCode)
		{
			// 返回需要重新查询
			managerWork.reQuery();
		}
	}
}

package com.zk.rfid.main.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.content.Context;
import android.widget.TextView;

import com.zk.rfid.R;
import com.zk.rfid.util.CommUtil;

public class ConfigUtil
{
	private static final String TAG = "ConfigUtil";
	public static final String USER_TYPE = "usertype";
	public static final String USER_MANAGER = "manager";
	public static final String USER_EMPLOYEE = "employee";
	public static final String USER_TEMP = "temp";
	public static final String USER_NAME = "username";
	public static final String USER_PASSWORD = "pwd";
	public static final String USER_FULL_NAME = "name";
	public static final String USER_JOB_NAME = "jobname";
	public static final String USER_REG_TIME = "regtime";
	public static final String LEND_RECORD = "record";
	public static final String MATERIAL_STORAGE = "storage";
	public static final String USER_POWER_AUDIT = "audit";
	public static final String USER_POWER_APPROVE = "approve";
	public static final String USER_SESSION       = "session";
	public static final int INIT_DATA_BETWEEN_DAY = 5;

	public static final String INIT_BOX_DATA = "boxdata";
	public static final String MATERIAL_STATUS = "state";
	public static final String MATERIAL_SPEC = "materialSpec";
	public static final String APPROVE_PROCESS = "process";

	public static final String MATERIAL_POSITION = "position";
	public static final String MATERIAL_POSITION_MAP = "posmap";
	public static final String GET_NEW_MATERIAL_EPC = "getepc";

	public static final String INIT_DATA_DATE = "initime";
	public static final String APPLY_MATERIAL_LIST = "applylist";

	public static final String LEFT_REPORTS			 = "reports";
	public static final String LEFT_EXIT_REPORTS 	 = "exitreports";
	public static final String LEFT_ARRIVE_REPORTS 	 = "arrivereports";
	public static final String LEFT_EDIT_PAWSSORD 	 = "editpwd";
	public static final String LEFT_STORAGE_MAP      = "map";
	public static final String LEFT_WRITE_ECP 	     = "writeecp";
	public static final String LEFT_MODIFY_BOX_COUNT = "modifybox";
	public static final String LEFT_MODIFY_NEED      = "modifyneed";
	public static final String LEFT_READ_ECP 		 = "readecp";
	public static final String LEFT_USER_INFO		 = "userinfo";
	public static final String LEFT_EXCEPTION_INFO 	 = "exception";
	public static final String LEFT_MY_RECORD 		 = "myrecord";
	public static final String LEFT_MY_INFO 		 = "myinfo";
	public static final String LEFT_MATERIAL_STORAGE = "mastorage";
	public static final String LEFT_MATERIAL_NEW_EPC = "newepc";
	public static final String LEFT_MATERIAL_ADD 	 = "add";
	public static final String LEFT_MATERIAL_ADD_RECORD = "addrecord";
	public static final String LEFT_MATERIAL_CACHE 	    = "cache";
	public static final String LEFT_SYSTEM_SET 			= "set";
	public static final String LEFT_SYSTEM_EXIT 		= "exit";

	public static final String EPC_SERVER_WAN_IP = "epcwanip";
	public static final String EPC_SERVER_COM_IP = "epccomip";
	public static final String EPC_SERVER_WAN_PORT = "epcport";
	public static final String EPC_METHOD = "epcmet";// 选择方式
	public static final String EPC_METHOD_WAN = "epcwan";// 网络方式
	public static final String EPC_METHOD_COM = "epccom";// 串口方式

	// 审核状态: [ { "name": "申请", "code": "0" }, { "name": "待审核", "code": "1" }, {
	// "name": "审核通过", "code": "2" }, { "name": "待审批", "code": "3" }, { "name":
	// "审批通过", "code": "4" }, { "name": "审核未通过", "code": "5" }, { "name":
	// "审批未通过", "code": "6" }, { "name": "已领用", "code": "7" }, { "name": "已归还",
	// "code": "8" } ]

	// 物资状态 ：[ { "name": "可借领", "code": "1" }, { "name": "已领用", "code": "2" }, {
	// "name": "已损坏", "code": "3" } ]

	public static final String HAVE_LOGOUT  = "0";// 注销
	public static final String ALLOW_APPLY  = "1";
	public static final String HAVE_GET_USE = "2";// 已领用
	public static final String HAVE_DAMAGE  = "3";

	public static final String WAIT_ADUIT = "1";
	public static final String ADUIT_PASS = "2";
	// public static final String WAIT_APPROVE = "3";
	// public static final String APPROVE_PASS = "4";
	public static final String ADUIT_NOT_PASS = "5";
	// public static final String APPROVE_NOT_PASS = "6";
	//public static final String CHECK_PASS = "7";
	public static final String APPLY_HAVE_USE = "8";
	public static final String APPLY_HAVE_RETURN = "9";

	
	public static final String ORDER_DATE = "orderdate";
	public static final String ORDER_INDATE = "orderindate";
	public static final String ORDER_CODE = "ordercode";
	public static final String ORDER_POSITION = "orderpos";
	public static final String ORDER_DEV  = "delivery";
	public static final String ORDER_LEND  = "lendNumber";

	public static final String NORMAL  = "0";
	public static final String SMALL   = "1";
	public static final String MIDDLE  = "2";
	public static final String LARGE   = "3";

	// 物质性质
	public static final String CONSUMABLES = "0";
	public static final String NOTCONSUMABLES = "1";
	
	public static final String SPEC_DISPERSE="4";
	public static final String SPEC_WET="5";
	
	//功率设置
	public static final String POWER_SET_VALUE  = "powerset";
	
	//150,200,250,300
	public static final int POWER_SMALL  = 150;
	public static final int POWER_MIDDLE = 200;
	public static final int POWER_LARGE  = 250;
	public static final int POWER_MOST   = 300;

	//扫描epc优先显示物资id
	public static final String PRIOR_MATERIAL_STRING="prior";
	
	//检查有需要修改数量的记录
	public static final String NEED_MODIFY_DATA="needmodify";

	// private static final String array[]=new
	// String[]{"0_不可用","2_审核中","3_已借出","4_已领用","5_已归还","6_已损坏","7_审核通过","8_拒绝审请","9_可借领","10_未放好","11_未验收"};
	public static String getMaterialStatus(Context context, String code)
	{
		String name = "";
		String jsonStr = CommUtil.getShare(context).getString(MATERIAL_STATUS, "");
		try
		{
			Map<String, String> map = CommUtil.getJsonMap(jsonStr);
			// LogUtil.i(TAG, "状态表："+map);
			name = map.get(code);
		} catch (Exception e)
		{
		}
		return name;
	}

	public static String getProcessStatus(Context context, String code)
	{
		String name = "";
		String jsonStr = CommUtil.getShare(context).getString(APPROVE_PROCESS, "");
		try
		{
			Map<String, String> map = CommUtil.getJsonMap(jsonStr);
			// LogUtil.i(TAG, "状态表："+map);
			name = map.get(code);
		} catch (Exception e)
		{
		}
		return name;
	}

	public static String getSpecCodeByName(Context context, String name)
	{
		String code = "";
		String jsonStr = CommUtil.getShare(context).getString(MATERIAL_SPEC, "");
		try
		{
			Map<String, String> map = CommUtil.getJsonMap(jsonStr);
			Iterator it = map.entrySet().iterator();
			while (it.hasNext())
			{
				Entry entry = (Entry) it.next();
				String key = (String) entry.getKey();
				String val = (String) entry.getValue();
				if (val.equals(name))
				{
					code = key;
					break;
				}
			}
		} catch (Exception e)
		{
		}
		return code;
	}
	
	public static String getSpecNameByCode(Context context, String code)
	{
		String name = "";
		String jsonStr = CommUtil.getShare(context).getString(MATERIAL_SPEC, "");
		try
		{
			Map<String, String> map = CommUtil.getJsonMap(jsonStr);
			Iterator it = map.entrySet().iterator();
			while (it.hasNext())
			{
				Entry entry = (Entry) it.next();
				String key = (String) entry.getKey();
				String val = (String) entry.getValue();
				if (key.equals(code))
				{
					name = val;
					break;
				}
			}
		} catch (Exception e)
		{
		}
		return name;
	}

	public static String getMaterialPos(Context context, String code)
	{
		String name = "";
		try
		{
			String jsonStr = CommUtil.getShare(context).getString(MATERIAL_POSITION, "");
			Map<String, String> map = CommUtil.getJsonMap(jsonStr);
			if (null != map.get(code))
			{
				name = map.get(code);
			}
		} catch (Exception e)
		{
			name = "";
			e.printStackTrace();
		}
		return name;
	}

	public static Map<String, String> getStorageInfo(Context context)
	{
		try
		{
			String jsonStr = CommUtil.getShare(context).getString(INIT_BOX_DATA, "");
			return CommUtil.getJsonMap(jsonStr);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	private static List<String> listUnit;
	private static String unitArray[] =
	{ "个", "件", "套", "只", "条", "台", "千克", "桶", "米", "盒", "袋", "根", "瓶" };

	public static List<String> getMaterialUnit()
	{
		if (null == listUnit)
		{
			listUnit = new ArrayList<String>();
			for (int i = 0; i < unitArray.length; i++)
			{
				listUnit.add(unitArray[i]);
			}
		}
		return listUnit;
	}

	private static List<String> listSpec;

	public static List<String> getSpec(Context context)
	{
		if (null == listSpec)
		{
			listSpec = new ArrayList<String>();

			String jsonStr = CommUtil.getShare(context).getString(MATERIAL_SPEC, "");
			try
			{
				Map<String, String> map = CommUtil.getJsonMap(jsonStr);
				Iterator it = map.entrySet().iterator();
				while (it.hasNext())
				{
					Entry entry = (Entry) it.next();
					String key = entry.getKey() + "";
					//LogUtil.i("取得:", key + ">>" + map.get(key));
					listSpec.add(map.get(key));
				}
			} catch (Exception e)
			{
			}
		}
		return listSpec;
	}

	public static List<String> getStoreStatusText(Context context)
	{
		List<String> listState = new ArrayList<String>();
		String type = CommUtil.getShare(context).getString(USER_TYPE, "");
		if (USER_MANAGER.equals(type))
		{
			listState.add("全部");
			listState.add(getMaterialStatus(context, ALLOW_APPLY));
			listState.add(getMaterialStatus(context, HAVE_LOGOUT));
			listState.add(getMaterialStatus(context, HAVE_DAMAGE));
			listState.add(getMaterialStatus(context, HAVE_GET_USE));

			// listState.add(getMaterialStatus(context, NOT_ACCEPTE));
		} else
		{
			listState.add(getMaterialStatus(context, ALLOW_APPLY));// 员工只能看到可以申请的记录
		}
		return listState;
	}

	public static List<String> getLendStatusText(Context context)
	{
		List<String> listState = new ArrayList<String>();
		listState.add("全部");

		listState.add(getProcessStatus(context, WAIT_ADUIT));
		listState.add(getProcessStatus(context, ADUIT_PASS));
		// listState.add(getProcessStatus(context, WAIT_APPROVE));
		// listState.add(getProcessStatus(context, APPROVE_PASS));
		// listState.add(getProcessStatus(context, ADUIT_NOT_PASS));
		listState.add(getProcessStatus(context, APPLY_HAVE_USE));
		listState.add(getProcessStatus(context, APPLY_HAVE_RETURN));

		return listState;
	}

	public static String getCodeByText(Context context, String name,String stateType)
	{
		String code = "";// 默认全部
		String jsonStr = CommUtil.getShare(context).getString(stateType, "");
		try
		{
			Map<String, String> map = CommUtil.getJsonMap(jsonStr);
			Iterator<Entry<String, String>> it = map.entrySet().iterator();
			while (it.hasNext())
			{
				Entry<String, String> entry = (Entry<String, String>) it.next();
				String key = (String) entry.getKey();
				String val = (String) entry.getValue();
				if (val.equals(name))
				{
					code = key;
					break;
				}
			}
		} catch (Exception e)
		{
		}
		return code;
	}

	public static void setTextColor(Activity activity, TextView textView, String statusCode)
	{
		if (ConfigUtil.HAVE_DAMAGE.equals(statusCode) || ConfigUtil.HAVE_LOGOUT.equals(statusCode)
				|| ConfigUtil.ADUIT_NOT_PASS.equals(statusCode))
		{
			CommUtil.setFrontColor(activity, textView, R.color.red);// 损坏,拒绝,注销

		} else if (ConfigUtil.ADUIT_PASS.equals(statusCode))
		{
			CommUtil.setFrontColor(activity, textView, R.color.green);
		} else if (ConfigUtil.HAVE_GET_USE.equals(statusCode) || ConfigUtil.APPLY_HAVE_USE.equals(statusCode))
		{
			CommUtil.setFrontColor(activity, textView, R.color.yellow);
		} else if (ConfigUtil.APPLY_HAVE_RETURN.equals(statusCode))
		{
			CommUtil.setFrontColor(activity, textView, R.color.blue);
		} else
		{
			CommUtil.setFrontColor(activity, textView, R.color.black);
		}
	}
	
}

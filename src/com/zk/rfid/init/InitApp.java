package com.zk.rfid.init;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zk.rfid.R;
import com.zk.rfid.comm.base.MyApplication;
import com.zk.rfid.main.util.ConfigUtil;
import com.zk.rfid.util.CommUtil;
import com.zk.rfid.util.GetDataThread;
import com.zk.rfid.util.LogUtil;
import com.zk.rfid.util.SysApplication;
import com.zk.rfid.util.ZKCmd;

/**
 * @Description: 系统初始化
 * @date:2016-8-10上午11:51:09
 * @author: ldl
 */
public class InitApp
{
	private static final String TAG = "InitApp";

	private Handler handler;

	public InitApp(Handler handler)
	{
		this.handler = handler;
	}

	/** 初始化数据,获取物资状态，审批流程状态，规格码及对应中文，保存在share文件中 **/
	public void getInitData()
	{
		GetDataThread task1 = new GetDataThread(MyApplication.context, handler);
		StringBuffer sb1 = new StringBuffer();
		sb1.append("t=" + ZKCmd.REQ_INIT_DATA);
		task1.setHandlerCode(ZKCmd.HAND_RESPONSE_DATA);
		task1.setParams(sb1.toString());
		task1.setArg1(ZKCmd.ARG_REQUEST_INIT);
		task1.start();
	}

	/**获取保存的用户名和密码,尝试自动登录过程**/
	public void autoLogin()
	{
		String name = CommUtil.getShare(MyApplication.context).getString(ConfigUtil.USER_NAME, "");
		String md5Pwd = CommUtil.getShare(MyApplication.context).getString(ConfigUtil.USER_PASSWORD, "");
		if ("".equals(name) || "".equals(md5Pwd))
		{
			handler.sendEmptyMessage(ZKCmd.HAND_AUTO_LOGIN_FAIL);
		} else
		{
			GetDataThread thread = new GetDataThread(MyApplication.context, handler);
			StringBuffer sb = new StringBuffer();
			sb.append("t=" + ZKCmd.REQ_LOGIN_ZK).append("&");
			sb.append("username=" + name).append("&");
			sb.append("password=" + md5Pwd).append("&");
			thread.setHandlerCode(ZKCmd.HAND_AUTO_LOGIN);
			thread.setParams(sb.toString());
			thread.start();
		}
	}

	/**检查登录返回的数据，通过则保存相关数据**/
	public void checkLoginRes(Map<String, String> map)
	{
		String status = map.get("status");

		if ("1".equals(status))
		{
			setUserInfo(ConfigUtil.USER_MANAGER, map);
		}
		if ("2".equals(status))
		{
			setUserInfo(ConfigUtil.USER_EMPLOYEE, map);
		}
		if ("3".equals(status))
		{
			setUserInfo(ConfigUtil.USER_TEMP, map);
		}
	}
	/**保存登录相关数据**/
	private int setUserInfo(String type, Map<String, String> map)
	{

		String name = map.get("fullname");
		String username = map.get("username");
		String jobNum = map.get("jobnum");
		String regtime = map.get("regtime");
		String power = map.get("power");
		String session = map.get("sessionId");

		boolean auditFlag = false;
		boolean approveFlag = false;

		if (!"".equals(power) && null != power)
		{
			try
			{
				// 从后往前数,权限（账号编辑，读取日志，新增物资，统计，审批，审核）
				char array[] = power.substring(power.length() - 2, power.length()).toCharArray();
				if ('1' == array[0])
				{
					approveFlag = true;
				}
				if ('1' == array[1])
				{
					auditFlag = true;
				}

			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		Editor editor = CommUtil.getShare(MyApplication.context).edit();
		editor.putString(ConfigUtil.USER_TYPE, type);
		editor.putString(ConfigUtil.USER_NAME, username); // 用户名
		editor.putString(ConfigUtil.USER_FULL_NAME, name); // 姓名
		editor.putString(ConfigUtil.USER_JOB_NAME, jobNum);// 工号
		editor.putString(ConfigUtil.USER_REG_TIME, regtime);// 注册时间
		editor.putBoolean(ConfigUtil.USER_POWER_AUDIT, auditFlag); // 是否有审核权
		editor.putBoolean(ConfigUtil.USER_POWER_APPROVE, approveFlag); // 是否有审批权
		editor.putString(ConfigUtil.USER_SESSION, session); // 会话session

		editor.commit();
		return 1;
	}

	/** 处理藜得的初始化数据,保存状态和规格 **/
	public int saveInitData(String json)
	{
		int res = 0;
		try
		{

			String str = new JSONObject(json).getJSONObject("result").toString();
			Map<String, String> map = CommUtil.getJsonMap(str);

			// String state = map.get("state");
			String status = jsonArrayToJson(map.get("materialStatus"));
			String spec = jsonArrayToJson(map.get("materialSpec"));
			String process = jsonArrayToJson(map.get("processStatus"));

			if (null != spec && !"".equals(spec) && null != status && !"".equals(status) && null != process && !"".equals(process))
			{
				// 成功
				Editor editor = CommUtil.getShare(MyApplication.context).edit();
				editor.putString(ConfigUtil.MATERIAL_STATUS, status);// 代表状态
				editor.putString(ConfigUtil.MATERIAL_SPEC, spec); // 代表规格
				editor.putString(ConfigUtil.APPROVE_PROCESS, process);

				// editor.putString(ConfigUtil.INIT_DATA_DATE,
				// StringUtil.getDate());// 将日期保存，指定时间内不重复获取
				editor.commit();
				res = 1;
			}

		} catch (Exception e)
		{
			res = 0;
		}
		return res;
	}

	/**解析初始化获得的服务端数据**/
	private String jsonArrayToJson(String str)
	{
		JSONObject json = new JSONObject();
		try
		{
			JSONArray jsonArray = new JSONArray(str);
			int len = jsonArray.length();
			for (int i = 0; i < len; i++)
			{
				String code = jsonArray.getJSONObject(i).getString("code");
				String name = jsonArray.getJSONObject(i).getString("name");
				json.put(code, name);
			}
		} catch (Exception e)
		{
		}
		return json.toString();
	}

	/**初始化失败对话框,失败需要重新退出进行**/
	public void initFailDialog(final Activity activity)
	{
		final Dialog dialog = new Dialog(activity, R.style.dialogstyle);
		dialog.setContentView(R.layout.result_dialog);
		LinearLayout result_fail_lin = (LinearLayout) dialog.findViewById(R.id.result_fail_lin);
		LinearLayout result_success_lin = (LinearLayout) dialog.findViewById(R.id.result_success_lin);
		TextView result_fail = (TextView) dialog.findViewById(R.id.result_fail);
		TextView updateMsg = (TextView) dialog.findViewById(R.id.updateMsg);

		result_fail_lin.setVisibility(View.VISIBLE);
		result_success_lin.setVisibility(View.GONE);
		updateMsg.setText("点击确定退出重新进入初始化");
		result_fail.setText(activity.getString(R.string.init_fail));

		Button sure = (Button) dialog.findViewById(R.id.zk_sure_btn);
		sure.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				dialog.cancel();
				SysApplication.getInstance().exit();
			}
		});
		dialog.setCanceledOnTouchOutside(false);
		dialog.setCancelable(false);
		dialog.show();
	}

	/** 请求获取箱子和散件摆放情况，平面图上显示多少个箱子，多少散件 **/
	public void getStorageBoxData()
	{
		GetDataThread thread = new GetDataThread(MyApplication.context, handler);
		thread.setHandlerCode(ZKCmd.HAND_RESPONSE_DATA);
		thread.setArg1(ZKCmd.ARG_GET_POS_RESPONSE);
		String param = "t=" + ZKCmd.REQ_GET_STOAGE_BOX;
		thread.setParams(param);
		thread.start();
	}

	/**获得箱子和散件摆放情况数据，解析保存**/
	public void setLocationInfo(String res)
	{
		try
		{
			JSONObject jsonObj = new JSONObject(res).getJSONObject("result");
			JSONArray boxList = jsonObj.getJSONArray("boxList");
			JSONArray materialList = jsonObj.getJSONArray("materialList");

			Map<String, String> boxMap = new HashMap<String, String>();
			Map<String, String> notBoxMap = new HashMap<String, String>();

			if (null != boxList && boxList.length() > 0)
			{
				int len = boxList.length();

				JSONObject json1 = null;
				String code = "";
				String count = "";
				String newCde = "";
				int index = 0;
				for (int i = 0; i < len; i++)
				{
					// 2-3-0-1-1-1-1-1-1
					json1 = boxList.getJSONObject(i);
					code = json1.optString("positionCode");
					count = json1.optString("count");
					int arryLen = code.split("-").length;
					if (9 == arryLen)
					{
						index = code.lastIndexOf("-");
					}
					if (8 == arryLen)
					{
						index = code.length();
					}
					if (8 == arryLen || 9 == arryLen)
					{
						newCde = code.substring(0, index);
						boxMap.put(newCde, count);
					}
				}
			}
			if (null != materialList && materialList.length() > 0)
			{
				int len = materialList.length();

				JSONObject json1 = null;
				String code = "";
				String count = "";
				String newCde = "";
				int index = 0;
				for (int i = 0; i < len; i++)
				{
					json1 = boxList.getJSONObject(i);
					code = json1.optString("positionCode");
					count = json1.optString("count");

					int arryLen = code.split("-").length;
					if (9 == arryLen)
					{
						index = code.lastIndexOf("-");
					}
					if (8 == arryLen)
					{
						index = code.length();
					}
					if (8 == arryLen || 9 == arryLen)
					{
						newCde = code.substring(0, index);
						notBoxMap.put(newCde, count);
					}
				}
			}

			String jsonStr = CommUtil.getShare(MyApplication.context).getString(ConfigUtil.MATERIAL_POSITION, "");// 所有的位置
			Map<String, String> posMap = CommUtil.getJsonMap(jsonStr);
			JSONObject json = new JSONObject();

			Iterator it = posMap.entrySet().iterator();
			while (it.hasNext())
			{
				Entry entry = (Entry) it.next();
				String key = entry.getKey() + "";

				String count = boxMap.get(key);
				String notBoxCount = notBoxMap.get(key);
				int total = 0;
				String text = "空仓位";
				String co = "";

				if (null != count && !"".equals(count))
				{
					total = Integer.parseInt(count);
					co = total + "箱";
					text = co;
				}
				if (null != notBoxCount && !"".equals(notBoxCount))
				{
					total = Integer.parseInt(notBoxCount);
					co = co + total + "散件";
					text = co;
				}
				json.put(key, text);
			}

			Editor editor = CommUtil.getShare(MyApplication.context).edit();
			editor.putString(ConfigUtil.INIT_BOX_DATA, json.toString());
			// LogUtil.i(TAG, "获得仓位数据：" + json.toString());
			editor.commit();

		} catch (Exception e)
		{
			LogUtil.e(TAG, "查询仓位数据异常:" + e.getMessage());
			e.printStackTrace();
		}
	}

	/**向服务器请求获取需要修改箱子和金属标签上的数量**/
	public void getNeedModifyData()
	{
		Editor editor = CommUtil.getShare(MyApplication.context).edit();
		editor.putString(ConfigUtil.NEED_MODIFY_DATA, "");
		editor.commit();//先清空之前的
		
		GetDataThread thread = new GetDataThread(MyApplication.context, handler);
		thread.setHandlerCode(ZKCmd.HAND_RESPONSE_DATA);
		thread.setArg1(ZKCmd.ARG_GET_NEED_MODIFY_DATA);
		String param = "t=" + ZKCmd.REQ_NEED_MODIFY_DATA;
		thread.setParams(param);
		thread.start();
	}

	/**解析并保存需要修改的标签数据**/
	public void setModifyInfo(String res)
	{
		try
		{
			LogUtil.i(TAG, "获得需要修改数据:"+res);
			JSONArray jsonArr  = new JSONObject(res).getJSONArray("result");
			if(null!=jsonArr && jsonArr.length()>0)
			{
				Editor editor = CommUtil.getShare(MyApplication.context).edit();
				editor.putString(ConfigUtil.NEED_MODIFY_DATA, res);
				editor.commit();
			}else
			{
				LogUtil.i(TAG, "获得需要修改数据为空,不需要修改");
			}			
		} catch (Exception e)
		{
			LogUtil.e(TAG, "获得需要修改数据异常:" + e.getMessage());
		}
		
	}
}

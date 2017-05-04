package com.zk.rfid.main.work;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.zk.rfid.R;
import com.zk.rfid.comm.base.MyApplication;
import com.zk.rfid.main.util.ConfigUtil;
import com.zk.rfid.util.CommUtil;
import com.zk.rfid.util.FourTableView;
import com.zk.rfid.util.LogUtil;
import com.zk.rfid.util.StringUtil;
import com.zk.rfid.util.SystemUtil;
import com.zk.rfid.util.ZKCmd;

/**
 * @Description:
 * @date:2016-8-19上午9:18:51
 * @author: ldl
 */
public class MaterialDetailWork
{
	private static final String TAG = "MaterialDetailWork";

	private static MaterialDetailWork detailWork;
	private Activity activity;
	private Handler handler;
	private EpcWork epcWork;

	private LinearLayout detailTable, mangerOperateLin;
	private Button writ_btn, logout_btn, damage_btn, change_use_btn, change_lend_btn;
	private Map<String, String> dataMap;

	private MaterialDetailWork(Activity act)
	{
		activity = act;
	}

	public static MaterialDetailWork getInstance(Activity act)
	{
		if (null == detailWork)
		{
			detailWork = new MaterialDetailWork(act);
		}
		return detailWork;
	}

	public void initView(final Handler handler)
	{
		this.handler = handler;

		detailTable = (LinearLayout) activity.findViewById(R.id.detailTable);
		mangerOperateLin = (LinearLayout) activity.findViewById(R.id.mangerOperateLin);
		logout_btn = (Button) activity.findViewById(R.id.logout_btn);
		damage_btn = (Button) activity.findViewById(R.id.damage_btn);
		change_use_btn = (Button) activity.findViewById(R.id.change_use_btn);
		writ_btn = (Button) activity.findViewById(R.id.writ_btn);
		change_lend_btn = (Button) activity.findViewById(R.id.change_lend_btn);

		logout_btn.setOnClickListener(listener);
		damage_btn.setOnClickListener(listener);
		change_use_btn.setOnClickListener(listener);
		writ_btn.setOnClickListener(listener);
		change_lend_btn.setOnClickListener(listener);

		epcWork = MyApplication.getRfidWork();
		epcWork.setReaderHandler(handler);

	}

	private OnClickListener listener = new View.OnClickListener()
	{
		@Override
		public void onClick(View key)
		{
			switch (key.getId())
			{
			case R.id.logout_btn:
				if(SystemUtil.getPower(SystemUtil.MATERIAL_UPDATE))
				{
					changeMaterialFlag(ConfigUtil.HAVE_LOGOUT);
				}
				else
				{
					CommUtil.toastShow("您没有物资状态修改权限，请联系管理员！", activity);
				}
				break;

			case R.id.damage_btn:
				if(SystemUtil.getPower(SystemUtil.MATERIAL_UPDATE))
				{
					changeMaterialFlag(ConfigUtil.HAVE_DAMAGE);
				}
				else
				{
					CommUtil.toastShow("您没有物资状态修改权限，请联系管理员！", activity);
				}
				
				break;

			case R.id.change_use_btn:
				if(SystemUtil.getPower(SystemUtil.MATERIAL_UPDATE))
				{
					changeMaterialFlag(ConfigUtil.ALLOW_APPLY);
				}
				else
				{
					CommUtil.toastShow("您没有物资状态修改权限，请联系管理员！", activity);
				}
				
				break;

			case R.id.change_lend_btn:
				if(SystemUtil.getPower(SystemUtil.MATERIAL_UPDATE))
				{
					changeMaterialFlag(ConfigUtil.HAVE_GET_USE);
				}
				else
				{
					CommUtil.toastShow("您没有物资状态修改权限，请联系管理员！", activity);
				}
				break;

			case R.id.writ_btn:
				if(SystemUtil.getPower(SystemUtil.WRITE_TAG))
				{
					witeEpc();
				}
				else
				{
					CommUtil.toastShow("您没有物资状态修改权限，请联系管理员！", activity);
				}
				
				break;
			default:
				break;
			}
		}
	};

	/**修改物资的状态**/
	private void changeMaterialFlag(String state)
	{
		Message msg = Message.obtain();
		msg.what = ZKCmd.HAND_REQUEST_DATA;
		msg.arg1 = ZKCmd.ARG_REQUEST_UPDATE_STATE;
		msg.obj = state;
		handler.sendMessage(msg);

	}

	/**显示物资的详细信息**/
	public void showDetail(String res)
	{
		try
		{
			detailTable.removeAllViews();
			dataMap = null;

			List<String[]> listData = null;
			JSONArray jarr = new JSONObject(res).getJSONArray("result");

			String jsonString = jarr.getString(0);
			dataMap = CommUtil.getJsonMap(jsonString);
			listData = getListData(dataMap);

			if (null != listData && listData.size() > 0)
			{
				detailTable = new FourTableView().getViewList(listData, detailTable);
				String type = CommUtil.getShare(activity).getString(ConfigUtil.USER_TYPE, "");
				if (ConfigUtil.USER_MANAGER.equals(type))
				{
					mangerOperateLin.setVisibility(View.VISIBLE);// 管理员才显示
				}
			}

		} catch (Exception e)
		{
			CommUtil.toastShow("获取数据失败，请稍后再试", activity);
			LogUtil.e(TAG, "解析库存数据异常：" + e.getMessage());
		}

	}

	private List<String[]> getListData(Map<String, String> map)
	{
		List<String[]> list = new ArrayList<String[]>();
		String array[] = null;

		array = new String[]
		{ "物资编号", map.get("materialId"), "名称规格", map.get("materialName") + map.get("materialSpecDescribe") };
		list.add(array);

		array = new String[]
		{ "入库单号", map.get("deliveryOrderNumber"), "状态",
				ConfigUtil.getMaterialStatus(activity, map.get("materialStatus")) };
		list.add(array);

		array = new String[]
		{ "物资组", map.get("materialGroup"), "内含数量", map.get("materialCount") };
		list.add(array);

		array = new String[]
		{ "入库日期", StringUtil.formatDateByString(map.get("inDate")), "存放位置", map.get("location") + "" };
		list.add(array);
		array = new String[]
		{ "验收单号", map.get("approvalOrderNumber"), "序号", map.get("materialSn") + "" };
		list.add(array);

		String type = map.get("materialType");
		if (ConfigUtil.CONSUMABLES.equals(type))
		{
			type = activity.getString(R.string.consume);
		}
		if (ConfigUtil.NOTCONSUMABLES.equals(type))
		{
			type = activity.getString(R.string.not_consume);
		}
		array = new String[]
		{ "性质", type, "EPC码", map.get("epc_code") };
		list.add(array);

		String qu = map.get("materialQuality");
		if ("1".equals(qu))
		{
			qu = "良";
		} else
		{
			qu = "差";
		}
		array = new String[]
		{ "质量状况", qu, "规格", ConfigUtil.getSpecNameByCode(activity, map.get("materialSpec")) };
		list.add(array);

		array = new String[]
		{ "供货单位", map.get("sendCompany"), "到货日期", StringUtil.formatDateByString(map.get("arrivedDate")) };
		list.add(array);

		array = new String[]
		{ "经办人", map.get("deliveryPersonName"), "备注", map.get("deliveryRemarks") };
		list.add(array);

		return list;
	}

	/**详细物资的epc码写入标签**/
	private void witeEpc()
	{
		if (null != dataMap && dataMap.size() > 0)
		{
			String epccode = dataMap.get("epc_code");
			if (!"".equals(epccode) && epccode.length() == 32)
			{
				epcWork.powerOn();
				SystemClock.sleep(500);
				epcWork.writeEpcData(epccode);
			} else
			{
				CommUtil.toastShow("标签格式不正确:" + epccode, activity);
			}
		} else
		{
			CommUtil.toastShow("数据为空，无法获得标签信息", activity);
		}
	}

	public static void clearWork()
	{
		detailWork = null;
	}

	public void powerOff() {
		// TODO 自动生成的方法存根
		epcWork.powerOff();
	}
}

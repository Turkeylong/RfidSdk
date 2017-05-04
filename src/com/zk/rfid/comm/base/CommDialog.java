package com.zk.rfid.comm.base;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zk.rfid.R;
import com.zk.rfid.main.util.ConfigUtil;
import com.zk.rfid.util.CommUtil;
import com.zk.rfid.util.DoubleTableView;
import com.zk.rfid.util.LogUtil;
import com.zk.rfid.util.SystemUtil;

/**
 * @Description: 用于公用显示对话框
 * @date:2016-6-22上午10:46:50
 * @author: ldl
 */
public class CommDialog
{
	private static final String TAG = "CommDialog";
	private Activity activity;

	public CommDialog(Activity activity)
	{
		this.activity = activity;
	}

	/**
	 * 显示物资详细信息 
	 * @param res 服务端获得json
	 * @param isBox 是否箱子
	 */
	public void showMaterialDetailDialog(String res, boolean isBox)
	{
		try
		{
			JSONObject obj = new JSONObject(res).getJSONObject("result");
			List<String[]> listData = null;
			if ("1".equals(obj.optString("state", "")))
			{
				String jsonString = obj.getJSONObject("info").toString();
				Map<String, String> map = CommUtil.getJsonMap(jsonString);
				listData = getListData(map, isBox);
			} else
			{
				CommUtil.toastShow("获取数据失败", activity);
			}
			if (null != listData)
			{
				final Dialog dialog = new Dialog(activity, R.style.dialogstyle);
				dialog.setContentView(R.layout.userdetail);

				TextView titleId = (TextView) dialog.findViewById(R.id.titleId);
				titleId.setText("物资详细");
				LinearLayout userTable = (LinearLayout) dialog.findViewById(R.id.userTable);
				userTable = new DoubleTableView(activity).getViewList(listData, userTable);

				ImageView closeBtn = (ImageView) dialog.findViewById(R.id.closeBtn);
				closeBtn.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View arg0)
					{
						dialog.cancel();
					}
				});
				dialog.setCanceledOnTouchOutside(false);
				dialog.show();
			}

		} catch (Exception e)
		{
			CommUtil.toastShow("获取数据失败，请稍后再试", activity);
			LogUtil.e(TAG, "解析库存数据异常：" + e.getMessage());
		}
	}

	/**
	 * 
	 * @param map 数据
	 * @param 是否箱子 
	 * @return 解析数据，装封到list返回
	 */
	private List<String[]> getListData(Map<String, String> map, boolean isBox)
	{
		List<String[]> list = new ArrayList<String[]>();
		String array[] = null;

		array = new String[]
		{ "名称", map.get("materialname") };
		list.add(array);

		array = new String[]
		{ "物资编号", map.get("materialId") };
		list.add(array);

		array = new String[]
		{ "状态", ConfigUtil.getMaterialStatus(activity, map.get("status")) };
		list.add(array);

		array = new String[]
		{ "入库单号", map.get("deliveryOrderNumber") };
		list.add(array);

		array = new String[]
		{ "存放位置", ConfigUtil.getMaterialPos(activity, map.get("positions")) };
		list.add(array);

		array = new String[]
		{ "入库时间", map.get("cometime") };
		list.add(array);

		if (!isBox)
		{
			String type = map.get("materialtype");
			if (ConfigUtil.CONSUMABLES.equals(type))
			{
				type = activity.getString(R.string.consume);
			}
			if (ConfigUtil.NOTCONSUMABLES.equals(type))
			{
				type = activity.getString(R.string.not_consume);
			}
			array = new String[]
			{ "性质", type };
			list.add(array);

			array = new String[]
			{ "EPC码", map.get("epccode") };
			list.add(array);
			// array=new String[]{"送货人",map.get("handlername")};
			// list.add(array);
			array = new String[]
			{ "供货单位", map.get("shippmentcompany") };
			list.add(array);
			array = new String[]
			{ "到货日期", map.get("sendtime") };
			list.add(array);
			array = new String[]
			{ "经办人", map.get("handlername") };
			list.add(array);
			array = new String[]
			{ "备注", map.get("remarks") };
			list.add(array);
		}

		return list;
	}

	/**
	 * 根据结果码显示标签操作结果
	 * @param 结果码
	 */
	public void showReadWriteResMsg(int tag_err)
	{
		if (0 == tag_err)
		{
			
			//SystemUtil.showDefaultNotification(activity);
			CommUtil.toastShow("写入成功", activity);
			SystemUtil.startAlarm(activity);
			//SystemUtil.removeNotification(activity);
		} else
		{
			if ((tag_err & 0xFF) == 3)
			{
				CommUtil.toastShow("没有标签", activity);
			} else if ((tag_err & 0xFF) == 4)
			{
				CommUtil.toastShow("访问密码错误", activity);
			} else if ((tag_err & 0xFF) == 9)
			{
				CommUtil.toastShow("尝试失败", activity);
			} else
			{
				CommUtil.toastShow("写入失败:" + tag_err, activity);
			}
		}
	}
}

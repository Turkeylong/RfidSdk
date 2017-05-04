package com.zk.rfid.main.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zk.rfid.R;
import com.zk.rfid.comm.base.CommDialog;
import com.zk.rfid.main.activity.AdjustActivity;
import com.zk.rfid.main.bean.EpcInfo;
import com.zk.rfid.main.util.ConfigUtil;
import com.zk.rfid.util.CommUtil;
import com.zk.rfid.util.LogUtil;
import com.zk.rfid.util.ZKCmd;

/**
 * @Description: 扫描物资信息容器
 * @date:2016-7-8 上午11:21:30
 * @author: ldl
 */

public class OperateEpcListAdapter extends BaseAdapter
{
	private static final String TAG = "ScanEpcListAdapter";
	private Activity activity;
	private List<EpcInfo> scanList;
	// private HashMap<Integer, View> map = new HashMap<Integer, View>();
	private Handler handler;// 主界面的handler
	private Map<String, String> checkMap = new HashMap<String, String>();

	private Handler mHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case ZKCmd.HAND_RESPONSE_DATA:

				if (msg.arg1 == ZKCmd.ARG_REQUEST_UPDATE_STATE)
				{
					updateRes(msg.obj + "");
				}
				break;

			case ZKCmd.HAND_MATERIAL_DETAIL:

				CommDialog dia = new CommDialog(activity);
				dia.showMaterialDetailDialog(msg.obj + "", false);

				break;
			default:
				break;
			}
		};
	};

	public OperateEpcListAdapter(Activity act, Handler handler, List<EpcInfo> data)
	{
		checkMap.clear();// 先清掉前面的
		this.handler = handler;
		activity = act;
		scanList = data;
	}

	static class ViewHolder
	{
		TextView checkText;
		CheckBox orderCheck;
		TextView orderText;
		TextView nameText;
		TextView numberText;
		TextView statueText;
		TextView posText;
		TextView boxText;
		LinearLayout itemLin;
	}

	@Override
	public int getCount()
	{
		if (null != scanList)
		{
			return scanList.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int arg0)
	{
		return scanList.get(arg0);
	}

	@Override
	public long getItemId(int arg0)
	{
		return arg0;
	}

	@Override
	public View getView(final int position, View paramView, ViewGroup arg2)
	{

		ViewHolder holder = null;
		if (paramView == null)
		{
			paramView = LayoutInflater.from(activity).inflate(R.layout.scan_epc_list_item, null);
			// map.put(position, paramView);

			holder = new ViewHolder();
			holder.checkText = (TextView) paramView.findViewById(R.id.checkText);
			holder.orderText = (TextView) paramView.findViewById(R.id.orderText);
			holder.orderCheck = (CheckBox) paramView.findViewById(R.id.orderCheck);
			holder.checkText.setVisibility(View.GONE);
			holder.orderCheck.setVisibility(View.VISIBLE);
			holder.posText = (TextView) paramView.findViewById(R.id.posText);
			holder.numberText = (TextView) paramView.findViewById(R.id.numberText);
			holder.nameText = (TextView) paramView.findViewById(R.id.nameText);
			holder.statueText = (TextView) paramView.findViewById(R.id.statueText);
			holder.boxText = (TextView) paramView.findViewById(R.id.boxText);
			holder.itemLin = (LinearLayout) paramView.findViewById(R.id.itemLin);

			paramView.setTag(holder);
		} else
		{
			// paramView = map.get(position);
			holder = (ViewHolder) paramView.getTag();
		}

		if (paramView != null)
		{
			final EpcInfo info = scanList.get(position);
			holder.orderText.setText((position + 1) + "");
			holder.numberText.setText(info.getMaterialId());
			holder.nameText.setText(info.getMaterialName() + info.getMaterialSpecDescribe());
			holder.posText.setText(info.getLocation());

			if ("1".equals(info.getIsBox()) || ConfigUtil.SPEC_DISPERSE.equals(info.getMaterialSpec())
					|| ConfigUtil.SPEC_WET.equals(info.getMaterialSpec()))
			{
				holder.boxText.setText("是");
				if (ConfigUtil.SPEC_DISPERSE.equals(info.getMaterialSpec()))
				{
					holder.boxText.setText("");
				}
				holder.statueText.setText("");
				holder.orderCheck.setChecked(false);

			} else
			{
				checkMap.put(info.getEpcCode(), info.getEpcCode());// 先加入
				holder.orderCheck.setChecked(true);
				holder.boxText.setText("否");
				holder.statueText.setText(ConfigUtil.getMaterialStatus(activity, info.getMaterialStatus()));
			}

			ConfigUtil.setTextColor(activity, holder.statueText, info.getMaterialStatus());

			copyTextView(holder.numberText);
			copyTextView(holder.nameText);
			copyTextView(holder.posText);

			holder.numberText.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View arg0)
				{
					setMaterialPos(info);
				}
			});

			holder.nameText.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View arg0)
				{
					setMaterialPos(info);
				}
			});

			holder.posText.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View arg0)
				{
					setMaterialPos(info);
				}
			});
			holder.itemLin.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View arg0)
				{
					setMaterialPos(info);
				}
			});

			final CheckBox check = holder.orderCheck;
			holder.orderCheck.setOnCheckedChangeListener(new OnCheckedChangeListener()
			{
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
				{
					if ("1".equals(info.getIsBox()) || ConfigUtil.SPEC_DISPERSE.equals(info.getMaterialSpec()))
					{
						CommUtil.toastShow("箱子或金属标签不可更改状态", activity);
						check.setChecked(false);
						return;
					}

					if (isChecked)
					{
						checkMap.put(info.getEpcCode(), info.getEpcCode());
					} 
					else
					{
						checkMap.remove(info.getEpcCode());
					}
				}
			});
		}

		return paramView;
	}

	/**跳转调整仓位**/
	private void setMaterialPos(EpcInfo epcInfo)
	{
		Intent intent = new Intent();
		intent.setClass(activity, AdjustActivity.class);
		intent.putExtra("info", epcInfo);
		activity.startActivityForResult(intent, 1);
	}

	/** 批量提交修改 **/
	public void updateStatus(String state)
	{
		if (checkMap.size() > 0)
		{
			Iterator it = checkMap.entrySet().iterator();
			LogUtil.i(TAG, "选中的map:" + checkMap);
			int size = checkMap.size();
			StringBuffer sb = new StringBuffer();
			int count = 0;
			while (it.hasNext())
			{
				Entry entry = (Entry) it.next();
				LogUtil.i(TAG, "选中的key:" + entry.getKey());
				count++;
				if (count == size)
				{
					sb.append("'" + entry.getKey() + "'");
				} else
				{
					sb.append("'" + entry.getKey() + "'").append(",");
				}

			}

			String idList = sb.toString();

			sb = new StringBuffer();
			sb.append("t=" + ZKCmd.REQ_CHANGE_MATERIAL_STATE);
			sb.append("&epcCodeList=").append(idList);
			sb.append("&materialStatus=").append(state);

			LogUtil.i(TAG, "上传参数:" + sb.toString());
			CommUtil.executeCommTask(activity, mHandler, ZKCmd.HAND_RESPONSE_DATA, ZKCmd.ARG_REQUEST_UPDATE_STATE, 0,
					sb.toString());
		} else
		{
			CommUtil.toastShow("未选中任何记录", activity);
		}
	}

	/**批量修改物资状态**/
	private void updateRes(String res)
	{
		// {"result":{"state":"1","msg":"操作成功"}}
		try
		{
			String str = new JSONObject(res).getJSONObject("result").toString();
			Map<String, String> map = CommUtil.getJsonMap(str);
			// String state=map.get("state");
			CommUtil.toastShow(map.get("msg"), activity);
		} catch (Exception e)
		{
			CommUtil.toastShow("操作返回结果异常", activity);
		}
	}
	
	/**设置内容可复制剪贴板**/
	private void copyTextView(TextView textView)
	{
		final String val = textView.getText().toString();
		textView.setOnLongClickListener(new View.OnLongClickListener()
		{
			@Override
			public boolean onLongClick(View v)
			{
				CommUtil.copyTextViewContent(activity, val, true);
				return true;
			}
		});
	}

	public void addItem(List<EpcInfo> list)
	{
		if (null != list && list.size() > 0)
		{
			scanList.addAll(list);// 加载全部
		}
	}
	
	
	
    private List<EpcInfo> copyData;
    /**根据扫描到的物资标签，优先排借领记录中的物资**/
	public void showByOrder()
	{
		List<EpcInfo> filterData = null;
		filterData = new ArrayList<EpcInfo>();
		copyData=new ArrayList<EpcInfo>();
		copyData.addAll(scanList);
		
		LogUtil.e(TAG, "最后排序");
		if (null != copyData && copyData.size() > 0)
		{
			int len = copyData.size();
			String materialId = "";
			String materialList=CommUtil.getShare(activity).getString(ConfigUtil.PRIOR_MATERIAL_STRING, "");//借领的相关物资ID串
			LogUtil.e(TAG, "最后排序所属字符串:"+materialList);
			for (int i = 0; i < len; i++)
			{
				EpcInfo info = copyData.get(i);
				materialId = info.getMaterialId();
				
				if (!"".equals(materialList) && materialList.contains(materialId))
				{
					LogUtil.e(TAG, "最后排序，找到这条记录:"+materialId);
					filterData.add(info);
					scanList.remove(info);//先把他移除
				}
			}
		}

		if(filterData.size()>0)
		{
			LogUtil.e(TAG, "最后排序，倒过来");
			scanList.addAll(filterData);
			Collections.reverse(scanList);//倒序排
			//scanList=copyData;
			notifyDataSetChanged();
		}else
		{
			LogUtil.e(TAG, "没有找到，不需要排");
		}
	}
	
}

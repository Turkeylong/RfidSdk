package com.zk.rfid.map.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zk.rfid.R;
import com.zk.rfid.comm.base.CommDialog;
import com.zk.rfid.comm.search.SeacherEdit;
import com.zk.rfid.comm.search.SeacherEditCallBackListen;
import com.zk.rfid.main.activity.MaterialDetailActivity;
import com.zk.rfid.main.bean.EpcInfo;
import com.zk.rfid.main.util.ConfigUtil;
import com.zk.rfid.util.CommUtil;
import com.zk.rfid.util.GetDataTask;
import com.zk.rfid.util.LogUtil;
import com.zk.rfid.util.StringUtil;
import com.zk.rfid.util.ZKCmd;

/**
 * @Description: EPC值的信息容器
 * @date:2016-5-14 下午11:21:30
 * @author: ldl
 */
public class EpcValListAdapter extends BaseAdapter
{
	private static final String TAG = "EpcValListAdapter";
	private Activity activity;
	private List<EpcInfo> epcList;
	private List<EpcInfo> copyList;
	// private HashMap<Integer, View> map = new HashMap<Integer, View>();
	private Handler handler;// 主界面的handler

	public EpcValListAdapter(Activity act, Handler handler, List<EpcInfo> data)
	{
		this.handler = handler;
		activity = act;
		epcList = data;
		copyList = data;
	}

	static class ViewHolder
	{
		TextView orderText;
		TextView numberText;
		TextView nameText;
		TextView dateText;
		TextView boxText;
		TextView posText;
		TextView stateText;
		Button operate;

		TextView operateText;

		LinearLayout itemLin, operateLin;
	}

	@Override
	public int getCount()
	{
		if (null != epcList)
		{
			return epcList.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int arg0)
	{
		return epcList.get(arg0);
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
			paramView = LayoutInflater.from(activity).inflate(R.layout.epc_list_item, null);
			// map.put(position, paramView);

			holder = new ViewHolder();
			holder.orderText = (TextView) paramView.findViewById(R.id.orderText);
			holder.numberText = (TextView) paramView.findViewById(R.id.numberText);
			holder.nameText = (TextView) paramView.findViewById(R.id.nameText);
			holder.dateText = (TextView) paramView.findViewById(R.id.dateText);
			holder.boxText = (TextView) paramView.findViewById(R.id.boxText);
			holder.posText = (TextView) paramView.findViewById(R.id.posText);

			holder.operate = (Button) paramView.findViewById(R.id.operate);
			holder.stateText = (TextView) paramView.findViewById(R.id.stateText);

			holder.operate.setBackgroundResource(R.drawable.onclick_btn);
			holder.operateText = (TextView) paramView.findViewById(R.id.operateText);
			holder.operateText.setVisibility(View.GONE);

			holder.itemLin = (LinearLayout) paramView.findViewById(R.id.itemLin);
			holder.operateLin = (LinearLayout) paramView.findViewById(R.id.operateLin);
			holder.operateLin.setVisibility(View.VISIBLE);
			paramView.setTag(holder);
		} else
		{
			// paramView = map.get(position);
			holder = (ViewHolder) paramView.getTag();
		}

		if (paramView != null)
		{
			final EpcInfo info = epcList.get(position);
			if(info.getItemColor() == Color.GREEN)
			{
				holder.itemLin.setBackgroundColor(Color.GREEN);
			}
			else if(info.getItemColor() == Color.RED)
			{
				holder.itemLin.setBackgroundColor(Color.RED);
			}
			else
			{
				holder.itemLin.setBackgroundResource(R.drawable.white);
			}
			
			holder.orderText.setText((position + 1) + "");
			holder.numberText.setText(info.getMaterialId());

			holder.nameText.setText(info.getMaterialName()+info.getMaterialSpecDescribe());
			holder.dateText.setText(StringUtil.formatDateByString(info.getInDate()));

			holder.stateText.setText(ConfigUtil.getMaterialStatus(activity, info.getMaterialStatus()));
			ConfigUtil.setTextColor(activity, holder.stateText, info.getMaterialStatus());

			holder.operate.setBackgroundResource(R.drawable.onclick_blue_btn);
			// holder.operate.setTextColor(activity.getResources().getColor(R.color.white));
			// holder.operate.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);

			holder.posText.setText(info.getLocation());
			String box = info.getIsBox();
			if ("1".equals(box))
			{
				box = "是";
			} else
			{
				box = "否";
			}
			holder.boxText.setText(box);

			copyTextView(holder.numberText);
			copyTextView(holder.nameText);
			copyTextView(holder.dateText);
			holder.operate.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View arg0)
				{
					createEpc(info, position);
				}
			});

			holder.numberText.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View arg0)
				{
					//showMateriaDetail(info.getMaterialId(), info.getIsBox());
					showMateriaDetail(info);
				}
			});

			holder.nameText.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View arg0)
				{
					//showMateriaDetail(info.getMaterialId(), info.getIsBox());
					showMateriaDetail(info);
				}
			});

			holder.dateText.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View arg0)
				{
					//showMateriaDetail(info.getMaterialId(), info.getIsBox());
					showMateriaDetail(info);
				}
			});
			// holder.itemLin.setOnClickListener(new View.OnClickListener()
			// {
			// @Override
			// public void onClick(View arg0)
			// {
			// showMateriaDetail(info.getGoodsId(), info.getdeliveryOrderNumber(),
			// info.getIsBox());
			// }
			// });
		}

		return paramView;
	}

	private void createEpc(EpcInfo info, int arg)
	{
		String code = info.getEpcCode();
		if (null == code || "".equals(code) || code.length() != 32)
		{
			CommUtil.toastShow("标签码格式不正确:" + code, activity);
			return;
		}
		Message msg = Message.obtain();
		msg.what = ZKCmd.HAND_CREATE_EPC_ONCLICK;
		msg.obj = info;
		msg.arg1 = arg;
		handler.sendMessage(msg);
	}
	/**显示这条记录的详细情况**/
	private void showMateriaDetail(EpcInfo epcInfo)
	{
		String userType = CommUtil.getShare(activity).getString(ConfigUtil.USER_TYPE, "");
		if (ConfigUtil.USER_MANAGER.equals(userType))
		{
			Intent intent = new Intent();
			intent.setClass(activity, MaterialDetailActivity.class);
			intent.putExtra("info", epcInfo);
			activity.startActivity(intent);
		}
	}
	

	private void showMateriaDetail(String epcCode,String isbox)
	{
		StringBuffer sb = new StringBuffer();
		sb.append("t=" + ZKCmd.REQ_GET_MATERIAL_DET);
		sb.append("&epcCode=").append(epcCode);
		
		LogUtil.i(TAG, "查详细参数:" + sb.toString());
		GetDataTask task = new GetDataTask(activity, mHandler);
		if ("1".equals(isbox))
		{
			task.setArg1(1);
		} else
		{
			task.setArg1(0);
		}
		task.setHandlerCode(ZKCmd.HAND_MATERIAL_DETAIL);
		task.setParams(sb.toString());
		task.execute("");
	}

	private Handler mHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case ZKCmd.HAND_MATERIAL_DETAIL:
				CommDialog dia = new CommDialog(activity);
				if (1 == msg.arg1)
				{
					dia.showMaterialDetailDialog(msg.obj + "", true);
				} else
				{
					dia.showMaterialDetailDialog(msg.obj + "", false);
				}

				break;

			default:
				break;
			}
		};
	};

	public void addItem(List<EpcInfo> list)
	{
		if (null != list && list.size() > 0)
		{
			// 加载全部
			copyList.addAll(list);
			epcList = copyList;
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

	public void seacherTextChange(final EditText editText, final View imgview)
	{
		SeacherEdit.seacherTextChange(editText, imgview, new SeacherEditCallBackListen()
		{		
			@Override
			public void callBack(String value)
			{
				dataChange(value);			
			}
		});
	}

	private List<EpcInfo> filterData;

	/**过滤监听，根据包含字符实现列表数据刷新**/
	private void dataChange(String str)
	{
		if (str.length() == 0)
		{
			filterData = copyList;
		} else
		{
			filterData = null;
			filterData = new ArrayList<EpcInfo>();

			if (null != copyList && copyList.size() > 0)
			{
				int len = copyList.size();
				String code = "";
				String name = "";
				String pos = "";
				String date="";
				for (int i = 0; i < len; i++)
				{
					EpcInfo info = copyList.get(i);
					code = info.getMaterialId() + "";
					name = info.getMaterialName() +info.getMaterialSpecDescribe()+ "";
					pos  = info.getLocation() + "";
					date = info.getInDate()+"";
					if (code.contains(str) || name.contains(str) || pos.contains(str) || date.contains(str))
					{
						filterData.add(info);
					}
				}
			}
		}

		epcList = filterData;
		notifyDataSetChanged();
		handler.sendEmptyMessage(ZKCmd.HAND_NOTIFY_DATA_CHANGE);
	}

	private boolean CHANGE = false;

	/**点击排序，升序或者降序**/
	public void orderChange(final String type)
	{
		Collections.sort(epcList, new Comparator<EpcInfo>()
		{
			@Override
			public int compare(EpcInfo info1, EpcInfo info2)
			{
				// 对日期字段进行升序，如果欲降序可采用after方法			
				if(ConfigUtil.ORDER_CODE.equals(type))
				{		
					long code1=CommUtil.getStringToLong(info1.getMaterialId());
					long code2=CommUtil.getStringToLong(info2.getMaterialId());
					return getCompareRes(code1, code2, CHANGE);
				}	
				if(ConfigUtil.ORDER_DATE.equals(type))
				{		
					long code1=CommUtil.getStringToLong(StringUtil.formatDateByString(info1.getInDate()));
					long code2=CommUtil.getStringToLong(StringUtil.formatDateByString(info2.getInDate()));
					return getCompareRes(code1, code2, CHANGE);
				}
				if(ConfigUtil.ORDER_POSITION.equals(type))
				{		
					long code1=CommUtil.getStringToLong(StringUtil.formatSplite(info1.getLocation()));
					long code2=CommUtil.getStringToLong(StringUtil.formatSplite(info2.getLocation()));
					return getCompareRes(code1, code2, CHANGE);
				}
							
				return -1;
			}
		});
		
		if(CHANGE)
		{
			CHANGE=false;
		}else
		{
			CHANGE=true;
		}
		
		notifyDataSetChanged();
	}
	
	private int getCompareRes(long code1,long code2,boolean change)
	{
		if(change)
		{					
			if (code1>code2)
			{
				return 1;
			}
		}else
		{
			if (code1<code2)
			{
				return 1;
			}
		}		
		return -1;
	}
}

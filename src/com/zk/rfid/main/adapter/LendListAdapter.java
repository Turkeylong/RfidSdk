package com.zk.rfid.main.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zk.rfid.R;
import com.zk.rfid.comm.search.SeacherEdit;
import com.zk.rfid.comm.search.SeacherEditCallBackListen;
import com.zk.rfid.main.activity.LendDetailActivity;
import com.zk.rfid.main.bean.EpcInfo;
import com.zk.rfid.main.util.ConfigUtil;
import com.zk.rfid.util.CommUtil;
import com.zk.rfid.util.StringUtil;
import com.zk.rfid.util.ZKCmd;

/**
 * @Description: EPC信息容器
 * @date:2016-5-14 下午11:21:30
 * @author: ldl
 */
public class LendListAdapter extends BaseAdapter
{
	private static final String TAG = "LendListAdapter";
	private Activity activity;
	private List<EpcInfo> epcList;
	private List<EpcInfo> copyList;
	private Handler handler;
	// private HashMap<Integer, View> map = new HashMap<Integer, View>();

	public LendListAdapter(Activity act, Handler handler,List<EpcInfo> data)
	{
		activity = act;
		epcList = data;
		copyList=data;
		this.handler=handler;
	}

	static class ViewHolder
	{
		TextView orderText;
		//TextView nameText;
		//TextView countText;
		TextView lendNumberText;
		TextView statueText;
		TextView dateText;
		TextView userText;
		TextView purposeText;
		LinearLayout itemLin;
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
			paramView = LayoutInflater.from(activity).inflate(R.layout.lendlist_item, null);
			// map.put(position, paramView);

			holder = new ViewHolder();
			holder.orderText = (TextView) paramView.findViewById(R.id.orderText);
			holder.lendNumberText = (TextView) paramView.findViewById(R.id.lendNumberText);			
			holder.userText = (TextView) paramView.findViewById(R.id.userText);
			holder.statueText = (TextView) paramView.findViewById(R.id.statueText);
			holder.itemLin = (LinearLayout) paramView.findViewById(R.id.itemLin);
			holder.dateText = (TextView) paramView.findViewById(R.id.dateText);
			holder.purposeText= (TextView) paramView.findViewById(R.id.purposeText);
			paramView.setTag(holder);
		} else
		{
			// paramView = map.get(position);
			holder = (ViewHolder) paramView.getTag();
		}
		if (paramView != null)
		{
			final EpcInfo info = epcList.get(position);
			holder.orderText.setText((position + 1) + "");
						
			holder.userText.setText(info.getAppliedPersonName());
			holder.lendNumberText.setText(info.getAppliedOrderNumber());
			holder.statueText.setText(ConfigUtil.getProcessStatus(activity, info.getAppliedStatus()));

			ConfigUtil.setTextColor(activity, holder.statueText, info.getAppliedStatus());
			holder.dateText.setText(StringUtil.formatDateByString(info.getAppliedDate()));
			holder.purposeText.setText(info.getAppliedPurpose());
			copyTextView(holder.userText);
			copyTextView(holder.lendNumberText);
			copyTextView(holder.dateText);
			
			holder.userText.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View arg0)
				{
					detail(info);
				}
			});
			holder.lendNumberText.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View arg0)
				{
					detail(info);
				}
			});
			holder.dateText.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View arg0)
				{
					detail(info);
				}
			});
			holder.itemLin.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View arg0)
				{
					detail(info);
				}
			});
			
		}

		return paramView;
	}

	
	/**分页，加载更多数据**/
	public void addItem(List<EpcInfo> list)
	{
		if (null != list && list.size() > 0)
		{
			epcList.addAll(list);// 加载全部
		}
	}
	
	/**借领详细，跳转**/
	private void detail(EpcInfo info)
	{
		if(!CommUtil.isConnect(activity))
		{
			CommUtil.toastShow(activity.getString(R.string.network_error), activity);
			return ;
		}
		
		Intent intent = new Intent();
		intent.setClass(activity, LendDetailActivity.class);
		intent.putExtra("info", info);
		activity.startActivityForResult(intent, 1);
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
	
	/**设置记录过滤监听**/
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
				String dateStr = "";
				//String pos = "";
				for (int i = 0; i < len; i++)
				{
					EpcInfo info = copyList.get(i);
					code = info.getAppliedOrderNumber() + "";
					dateStr = StringUtil.formatDateByString(info.getAppliedDate());
					//pos = info.getLocation() + "";

					if (code.contains(str) || dateStr.contains(str))
					{
						filterData.add(info);
					}
				}
			}
		}

		epcList = filterData;
		notifyDataSetChanged();
		
		if(str.length() == 0)
		{
			//恢复显示全部
			handler.sendEmptyMessage(ZKCmd.HAND_NOTIFY_DATA_RETURN);	
		}else
		{
			//有值则就过滤符合条件的记录
			handler.sendEmptyMessage(ZKCmd.HAND_NOTIFY_DATA_CHANGE);
		}		
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
					long code1=CommUtil.getStringToLong(info1.getAppliedOrderNumber());
					long code2=CommUtil.getStringToLong(info2.getAppliedOrderNumber());
					return getCompareRes(code1, code2, CHANGE);
				}	
				if(ConfigUtil.ORDER_DATE.equals(type))
				{		
					long code1=CommUtil.getStringToLong(StringUtil.formatDateByString(info1.getInDate()));
					long code2=CommUtil.getStringToLong(StringUtil.formatDateByString(info2.getInDate()));
					return getCompareRes(code1, code2, CHANGE);
				}
				if(ConfigUtil.ORDER_LEND.equals(type))
				{		
					long code1=CommUtil.getStringToLong(info1.getDeliveryOrderNumber());
					long code2=CommUtil.getStringToLong(info2.getDeliveryOrderNumber());
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

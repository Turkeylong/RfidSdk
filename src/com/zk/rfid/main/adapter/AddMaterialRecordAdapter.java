package com.zk.rfid.main.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zk.rfid.R;
import com.zk.rfid.main.activity.MaterialDetailActivity;
import com.zk.rfid.main.bean.EpcInfo;
import com.zk.rfid.main.util.ConfigUtil;
import com.zk.rfid.util.CommUtil;

/**
 * @Description: 物资入库记录信息容器
 * @date:2016-9-27 上午9:21:30
 * @author: ldl
 */
public class AddMaterialRecordAdapter extends BaseAdapter
{
	private static final String TAG = "AddMaterialRecordAdapter";
	private Activity activity;
	private List<EpcInfo> epcInfoList;
	//private Handler handler;// 主界面的handler


	public AddMaterialRecordAdapter(Activity act, List<EpcInfo> data)
	{
		activity = act;
		epcInfoList = data;
	}

	static class ViewHolder
	{
		TextView orderText;
		TextView numberText;
		TextView nameText;
		TextView countText;
		TextView unitText;
		TextView posText;
		TextView typeText;
		TextView specText;
		TextView emergencyText;
		LinearLayout itemLin;
		
	}

	@Override
	public int getCount()
	{
		if (null != epcInfoList)
		{
			return epcInfoList.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int arg0)
	{
		return epcInfoList.get(arg0);
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
			paramView = LayoutInflater.from(activity).inflate(R.layout.add_material_record_item, null);
		
			holder = new ViewHolder();
			holder.orderText  = (TextView) paramView.findViewById(R.id.orderText);
			holder.numberText = (TextView) paramView.findViewById(R.id.numberText);
			holder.nameText   = (TextView) paramView.findViewById(R.id.nameText);
			holder.countText  = (TextView) paramView.findViewById(R.id.countText);			
			holder.unitText   = (TextView) paramView.findViewById(R.id.unitText);
			holder.posText    = (TextView) paramView.findViewById(R.id.posText);
			holder.typeText   = (TextView) paramView.findViewById(R.id.typeText);
			holder.specText   = (TextView) paramView.findViewById(R.id.specText);
			holder.emergencyText   = (TextView) paramView.findViewById(R.id.emergencyText);
			holder.itemLin	  = (LinearLayout) paramView.findViewById(R.id.itemLin);        
			
			paramView.setTag(holder);
		} else
		{
			holder = (ViewHolder) paramView.getTag();
		}

		if (paramView != null)
		{
			final EpcInfo info = epcInfoList.get(position);
			holder.orderText.setText((position + 1) + "");
			holder.numberText.setText(info.getMaterialId()+"");			
			holder.nameText.setText(info.getMaterialName()+info.getMaterialSpecDescribe()+"");
			holder.unitText.setText(info.getMaterialUnit()+"");
			holder.countText.setText(info.getMaterialCount()+"");
			holder.posText.setText(info.getLocation()+"");
			
			holder.typeText.setText(activity.getString(R.string.consume));
			if(ConfigUtil.NOTCONSUMABLES.equals(info.getMaterialType()))
			{
				holder.typeText.setText(activity.getString(R.string.not_consume));
			}
	
			holder.specText.setText(ConfigUtil.getSpecNameByCode(activity, info.getMaterialSpec())+"");
			
			if(info.getMaterialEmergency().equals("应急物资"))
				holder.emergencyText.setText("是");
			else
				holder.emergencyText.setText(info.getMaterialEmergency()+"");
			
			holder.numberText.setOnClickListener(new View.OnClickListener()
			{				
				@Override
				public void onClick(View v)
				{					
					getMaterialDetail(info);
				}
			});
			holder.nameText.setOnClickListener(new View.OnClickListener()
			{				
				@Override
				public void onClick(View v)
				{					
					getMaterialDetail(info);
				}
			});
			holder.posText.setOnClickListener(new View.OnClickListener()
			{				
				@Override
				public void onClick(View v)
				{					
					getMaterialDetail(info);
				}
			});
			holder.itemLin.setOnClickListener(new View.OnClickListener()
			{				
				@Override
				public void onClick(View v)
				{					
					getMaterialDetail(info);
				}
			});
			
			copyTextView(holder.nameText);
			copyTextView(holder.numberText);
		}
		
		return paramView;
	}

	/** 管理查看详细操作 **/
	private void getMaterialDetail(EpcInfo epcInfo)
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
	

	
	/**分页，加载更多数据**/
	public void addItem(List<EpcInfo> list)
	{
		if (null != list && list.size() > 0)
		{
			epcInfoList.addAll(list);// 加载全部
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

}

package com.zk.rfid.main.adapter;

import java.util.ArrayList;

import com.zk.rfid.R;
import com.zk.rfid.main.adapter.StorageApproveAdapter.ViewHolder;
import com.zk.rfid.main.bean.EpcInfo;
import com.zk.rfid.main.util.ConfigUtil;
import com.zk.rfid.util.StringUtil;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MaterialListAdapter extends BaseAdapter {
	private Activity activity;
	private int type;
	private ArrayList<EpcInfo> list;
	
	static class ViewHolder
	{
		TextView orderText;
		TextView materialIdText;
		TextView materialNameText;
		TextView materialCountText;
		TextView materialUnit;
		TextView locationText;
		TextView materialTypeText;
		TextView materialSpectText;
		TextView materialEmergencyText;	
	}
	
	public MaterialListAdapter(Activity activity, int type,ArrayList<EpcInfo> listInfo) {
		// TODO 自动生成的构造函数存根
		this.activity = activity;
		this.type = type;
		list = listInfo;
	}

	@Override
	public int getCount() {
		// TODO 自动生成的方法存根
		if (null != list)
		{
			return list.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO 自动生成的方法存根
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO 自动生成的方法存根
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO 自动生成的方法存根
		ViewHolder holder = null;
		if (convertView == null)
		{
			convertView = LayoutInflater.from(activity).inflate(R.layout.add_material_record_item, null);
			
			holder = new ViewHolder();		
			holder.orderText = (TextView) convertView.findViewById(R.id.orderText);
			holder.materialIdText = (TextView) convertView.findViewById(R.id.numberText);
			holder.materialNameText= (TextView) convertView.findViewById(R.id.nameText);
			holder.materialCountText = (TextView) convertView.findViewById(R.id.countText);
			holder.materialUnit = (TextView) convertView.findViewById(R.id.unitText);
			holder.locationText = (TextView) convertView.findViewById(R.id.posText);
			holder.materialTypeText = (TextView) convertView.findViewById(R.id.typeText);
			holder.materialSpectText = (TextView) convertView.findViewById(R.id.specText);
			holder.materialEmergencyText= (TextView) convertView.findViewById(R.id.emergencyText);
			
			convertView.setTag(holder);
		} else
		{
			holder = (ViewHolder) convertView.getTag();
		}

		if (convertView != null)
		{
			final EpcInfo info = list.get(position);
			holder.orderText.setText((position + 1) + "");
			holder.materialIdText.setText(info.getMaterialId());	
			holder.materialNameText.setText(info.getMaterialName());	
			holder.materialCountText.setText(info.getMaterialCount());
			holder.materialUnit.setText(info.getMaterialUnit());
			holder.locationText.setText(info.getLocation());

			if(type == 1)
			{
				if(ConfigUtil.NOTCONSUMABLES.equals(info.getMaterialType()))
				{
					holder.materialTypeText.setText(activity.getString(R.string.not_consume));
				}
				else
				{
					holder.materialTypeText.setText(activity.getString(R.string.consume));
				}
			}
			else
			{
				holder.materialTypeText.setText(info.getSerialNumber());
			}
			
			holder.materialSpectText.setText(ConfigUtil.getSpecNameByCode(activity, info.getMaterialSpec())+"");
			
			String qu=info.getMaterialEmergency()+"";
			
			if("应急物资".equals(qu))
			{
				holder.materialEmergencyText.setText("是");	
			}else
			{
				holder.materialEmergencyText.setText("");
			}
		}
		
		return convertView;	}

}

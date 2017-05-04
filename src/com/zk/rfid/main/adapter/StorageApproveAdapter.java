package com.zk.rfid.main.adapter;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zk.rfid.R;
import com.zk.rfid.main.bean.EpcInfo;
import com.zk.rfid.util.CommUtil;
import com.zk.rfid.util.StringUtil;

/**
 * @Description: 验收信息容器
 * @date:2016-8-24 上午11:21:30
 * @author: ldl
 */
public class StorageApproveAdapter extends BaseAdapter
{
	private static final String TAG = "StorageApproveAdapter";
	private Activity activity;
	private List<EpcInfo> epcInfoList;
	//private Handler handler;// 主界面的handler


	public StorageApproveAdapter(Activity act, List<EpcInfo> data)
	{
		activity = act;
		epcInfoList = data;
	}

	static class ViewHolder
	{
		TextView orderText;
		TextView dateText;
		TextView inDateText;
		TextView nameText;
		TextView unitText;
		TextView totalText;
		TextView plateNumber;
		TextView attachmentText;
		TextView qualityText;	
		TextView approvalOrderNumberText;
		TextView handPersonText;
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
			paramView = LayoutInflater.from(activity).inflate(R.layout.store_approve_item, null);
			
			holder = new ViewHolder();		
			holder.orderText = (TextView) paramView.findViewById(R.id.orderText);
			holder.dateText = (TextView) paramView.findViewById(R.id.dateText);
			holder.inDateText= (TextView) paramView.findViewById(R.id.inDateText);
			holder.nameText = (TextView) paramView.findViewById(R.id.nameText);
			holder.unitText = (TextView) paramView.findViewById(R.id.unitText);
			holder.totalText = (TextView) paramView.findViewById(R.id.totalText);
			holder.plateNumber = (TextView) paramView.findViewById(R.id.plateNumber);
			holder.attachmentText = (TextView) paramView.findViewById(R.id.attachmentText);
			holder.qualityText= (TextView) paramView.findViewById(R.id.qualityText);
			holder.approvalOrderNumberText= (TextView) paramView.findViewById(R.id.approvalOrderNumberText);
			holder.handPersonText= (TextView) paramView.findViewById(R.id.handPersonText);
			
			paramView.setTag(holder);
		} else
		{
			holder = (ViewHolder) paramView.getTag();
		}

		if (paramView != null)
		{
			final EpcInfo info = epcInfoList.get(position);
			holder.orderText.setText((position + 1) + "");
			holder.dateText.setText(StringUtil.formatDateByString(info.getArrivedDate()));	
			holder.inDateText.setText(StringUtil.formatDateByString(info.getInDate()));	
			holder.nameText.setText(info.getMaterialName()+info.getMaterialSpecDescribe()+"");
			holder.unitText.setText(info.getMaterialUnit());
			holder.totalText.setText(info.getSumCount());
			holder.plateNumber.setText(info.getPlateNumber());
			holder.attachmentText.setText(info.getAttachment());
			String qu=info.getQuality()+"";
			
			if("0".equals(qu))
			{
				holder.qualityText.setText("差");	
			}else
			{
				holder.qualityText.setText("良");
			}
			
			holder.approvalOrderNumberText.setText(info.getApprovalOrderNumber());
			holder.handPersonText.setText(info.getHandlePersonName());
			
			copyTextView(holder.nameText);
			copyTextView(holder.approvalOrderNumberText);
		}
		
		return paramView;
	}

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

package com.zk.rfid.main.adapter;

import java.util.List;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zk.rfid.R;
import com.zk.rfid.main.bean.EpcInfo;
import com.zk.rfid.util.CommUtil;
import com.zk.rfid.util.ZKCmd;

/**
 * @Description: 待修改标签的信息容器
 * @date:2016-9-21 下午5:21:30
 * @author: ldl
 */
public class WaitModifyAdapter extends BaseAdapter
{
	private static final String TAG = "WaitModifyAdapter";
	private Activity activity;
	private List<EpcInfo> epcList;
	// private HashMap<Integer, View> map = new HashMap<Integer, View>();
	private Handler handler;// 主界面的handler

	public WaitModifyAdapter(Activity act, Handler handler, List<EpcInfo> data)
	{
		this.handler = handler;
		activity = act;
		epcList = data;
	}

	static class ViewHolder
	{
		TextView orderText;
		TextView numberText;
		TextView nameText;
		TextView countText;
		TextView objCountText;
		TextView posText;
		TextView serialNumber;
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
			paramView = LayoutInflater.from(activity).inflate(R.layout.wait_modify_item, null);
			// map.put(position, paramView);

			holder = new ViewHolder();
			holder.orderText = (TextView) paramView.findViewById(R.id.orderText);
			holder.numberText = (TextView) paramView.findViewById(R.id.numberText);
			holder.nameText = (TextView) paramView.findViewById(R.id.nameText);
			holder.objCountText = (TextView) paramView.findViewById(R.id.objCountText);
			holder.countText = (TextView) paramView.findViewById(R.id.countText);
			holder.posText = (TextView) paramView.findViewById(R.id.posText);
			holder.serialNumber=(TextView) paramView.findViewById(R.id.serialNumber);
			holder.operate = (Button) paramView.findViewById(R.id.operate);
			
			//holder.operate.setBackgroundResource(R.drawable.onclick_blue_btn);
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
			holder.orderText.setText((position + 1) + "");
			holder.numberText.setText(info.getMaterialId());

			holder.nameText.setText(info.getMaterialName() + info.getMaterialSpecDescribe());
			holder.countText.setText(info.getOldCount());
			holder.objCountText.setText(info.getMaterialCount());
			
			holder.posText.setText(info.getLocation());
			holder.serialNumber.setText(info.getSerialNumber());
			
			copyTextView(holder.numberText);
			copyTextView(holder.nameText);

			holder.operate.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View arg0)
				{
					createEpc(info, 0);
				}
			});	
		}

		return paramView;
	}

	/**修改指定epc标签为新的值**/
	private void createEpc(EpcInfo info, int arg)
	{
		String code = info.getEpcCode();
		String newCode=info.getNewEcpCode();
		
		if (null == code || "".equals(code) || code.length() != 32)
		{
			CommUtil.toastShow("原标签码格式不正确:" + code, activity);
			return;
		}
		if (null == newCode || "".equals(newCode) || newCode.length() != 32)
		{
			CommUtil.toastShow("新标签码格式不正确:" + newCode, activity);
			return;
		}
		
		Message msg = Message.obtain();
		msg.what = ZKCmd.HAND_MODIFY_EPC;
		msg.obj  = info;
		msg.arg1 = arg;
		handler.sendMessage(msg);
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

package com.zk.rfid.main.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zk.rfid.R;
import com.zk.rfid.comm.base.CommDialog;
import com.zk.rfid.main.bean.EpcInfo;
import com.zk.rfid.main.util.ConfigUtil;
import com.zk.rfid.util.CommUtil;
import com.zk.rfid.util.LogUtil;
import com.zk.rfid.util.ZKCmd;

/**
 * @Description: 修改箱子信息容器
 * @date:2016-7-8 上午11:21:30
 * @author: ldl
 */
public class ModifyBoxAdapter extends BaseAdapter
{
	private static final String TAG = "ModifyBoxAdapter";
	private Activity activity;
	private List<EpcInfo> scanList;
	private Handler handler;// 主界面的handler

	private Handler mHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case ZKCmd.HAND_RESPONSE_DATA:
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

	public ModifyBoxAdapter(Activity act, Handler handler, List<EpcInfo> data)
	{
		this.handler = handler;
		activity = act;
		scanList = data;
	}

	static class ViewHolder
	{
		TextView orderText;
		TextView nameText;
		TextView numberText;
		TextView serialNumber;
		TextView posText;
		TextView boxText;
		TextView countText;
		TextView objCountText;

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
			paramView = LayoutInflater.from(activity).inflate(R.layout.modify_box_item, null);
			// map.put(position, paramView);

			holder = new ViewHolder();
			holder.orderText = (TextView) paramView.findViewById(R.id.orderText);
			holder.posText = (TextView) paramView.findViewById(R.id.posText);
			holder.numberText = (TextView) paramView.findViewById(R.id.numberText);
			holder.nameText = (TextView) paramView.findViewById(R.id.nameText);
			holder.serialNumber = (TextView) paramView.findViewById(R.id.serialNumber);
			holder.boxText = (TextView) paramView.findViewById(R.id.boxText);
			holder.countText= (TextView) paramView.findViewById(R.id.countText);
			holder.objCountText = (TextView) paramView.findViewById(R.id.objCountText);
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
			holder.serialNumber.setText(info.getSerialNumber());
			holder.countText.setText(info.getOldCount()+"");
			holder.objCountText.setText(info.getMaterialCount());
			if(!info.getOldCount().equals(info.getMaterialCount()))
			{
				//不相等情况下提示红色
				CommUtil.setFrontColor(activity, holder.objCountText, R.color.red);		
			}
			if ("1".equals(info.getIsBox()))
			{
				holder.boxText.setText("是");
			} else
			{
				holder.boxText.setText("否");
			}
			holder.itemLin.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View arg0)
				{
					if ("1".equals(info.getIsBox()) || ConfigUtil.SPEC_DISPERSE.equals(info.getMaterialSpec())
							|| ConfigUtil.SPEC_WET.equals(info.getMaterialSpec()))
					{
						createDialog(info);
					} else
					{
						CommUtil.toastShow("单物件数量不能进行修改", activity);
					}
				}
			});

		}

		return paramView;
	}

	private Dialog dialog;

	/**修改箱子内含物资数量对话框**/
	private void createDialog(final EpcInfo epcInfo)
	{
		dialog = new Dialog(activity, R.style.dialogstyle);
		dialog.setContentView(R.layout.scan_info_detail);
		ImageView closeBtn = (ImageView) dialog.findViewById(R.id.closeBtn);
		closeBtn.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				dialog.dismiss();
			}
		});

		TextView scanDetailSn = (TextView) dialog.findViewById(R.id.scanDetailSn);
		TextView materialId = (TextView) dialog.findViewById(R.id.materialId);
		TextView scanDetailCount = (TextView) dialog.findViewById(R.id.scanDetailCount);
		TextView materialName = (TextView) dialog.findViewById(R.id.materialName);
		final EditText scanDetailUpCount = (EditText) dialog.findViewById(R.id.scanDetailUpCount);

		TextView boxPos = (TextView) dialog.findViewById(R.id.boxPos);
		scanDetailSn.setText(epcInfo.getSerialNumber());
		materialId.setText(epcInfo.getMaterialId());
		scanDetailCount.setText(epcInfo.getOldCount());
		materialName.setText(epcInfo.getMaterialName() + epcInfo.getMaterialSpecDescribe() + "");
		boxPos.setText(epcInfo.getLocation());
		scanDetailUpCount.setText(epcInfo.getMaterialCount());
		Button update_submit = (Button) dialog.findViewById(R.id.update_submit);
		update_submit.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				String newCount = scanDetailUpCount.getText().toString();
				int nc = CommUtil.getParseIntVal(newCount);
				if ("".equals(newCount) )
				{
					CommUtil.toastShow("更新数量不能为空", activity);
				} else if (nc > 2048)
				{
					CommUtil.toastShow("最大值不能超过2048", activity);
				} else
				{
					newCount = nc + "";
					// t=23&goodsId=&isbox=1&oldcode=10C100011142228FEB61771801800001&beforeCount=20&afterCount=19
					StringBuilder sb = new StringBuilder();
					sb.append("t=" + ZKCmd.REQ_MODIFY_GET_NEWEPC);
					sb.append("&materialId=").append(epcInfo.getMaterialId());
					// sb.append("&isBox=1");
					sb.append("&oldEpcCode=").append(epcInfo.getEpcCode());
					sb.append("&beforeCount=").append(epcInfo.getOldCount());
					sb.append("&afterCount=").append(newCount);

					Message msg = Message.obtain();
					msg.obj = sb.toString();
					msg.what = ZKCmd.HAND_MODIFY_EPC;
					handler.sendMessage(msg);
					dialog.dismiss();
				}

			}
		});

		dialog.setCanceledOnTouchOutside(false);
		dialog.show();

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
			notifyDataSetChanged();
		}else
		{
			LogUtil.e(TAG, "没有找到，不需要排");
		}
	}
}

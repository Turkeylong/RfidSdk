package com.zk.rfid.main.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
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
import com.zk.rfid.util.LogUtil;
import com.zk.rfid.util.StringUtil;
import com.zk.rfid.util.ZKCmd;

/**
 * @Description: 库存信息容器
 * @date:2016-5-14 下午11:21:30
 * @author: ldl
 */
public class StorageListAdapter extends BaseAdapter
{
	private static final String TAG = "StorageListAdapter";
	private Activity activity;
	private List<EpcInfo> storageList;
	private List<EpcInfo> copyList;
	// private HashMap<Integer, View> map = new HashMap<Integer, View>();
	private Handler handler;// 主界面的handler

	private Handler mHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{

			case ZKCmd.HAND_MATERIAL_DETAIL:

				CommDialog dia = new CommDialog(activity);
				dia.showMaterialDetailDialog(msg.obj + "", false);

				break;

			case ZKCmd.HAND_RESPONSE_DATA:

				if (ZKCmd.ARG_REQUEST_UPDATE_STATE == msg.arg1)
				{
					updateRes(msg.obj + "");
				}
				break;

			default:
				break;
			}
		};
	};

	public StorageListAdapter(Activity act, Handler handler, List<EpcInfo> data)
	{
		this.handler = handler;
		activity = act;
		storageList = data;
		copyList=data;
	}

	static class ViewHolder
	{
		TextView orderText;
		TextView numberText;
		TextView nameText;
		TextView countText;
		TextView statueText;
		TextView deliveryOrderNumber;
		TextView inDateText;
		Button operate;
		LinearLayout itemLin;
	}

	@Override
	public int getCount()
	{
		if (null != storageList)
		{
			return storageList.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int arg0)
	{
		return storageList.get(arg0);
	}

	@Override
	public long getItemId(int arg0)
	{
		return arg0;
	}

	@Override
	public View getView(final int position, View paramView, ViewGroup arg2)
	{

		LogUtil.e(TAG, "打出position："+position);
		ViewHolder holder = null;
		if (paramView == null)
		{
			paramView = LayoutInflater.from(activity).inflate(R.layout.storage_list_item, null);
			// map.put(position, paramView);

			holder = new ViewHolder();
			holder.orderText = (TextView) paramView.findViewById(R.id.orderText);
			holder.numberText = (TextView) paramView.findViewById(R.id.numberText);
			holder.nameText = (TextView) paramView.findViewById(R.id.nameText);
			holder.countText = (TextView) paramView.findViewById(R.id.countText);
			holder.statueText = (TextView) paramView.findViewById(R.id.statueText);
			holder.deliveryOrderNumber = (TextView) paramView.findViewById(R.id.deliveryOrderNumber);
			holder.inDateText= (TextView) paramView.findViewById(R.id.inDateText);
			
			holder.operate = (Button) paramView.findViewById(R.id.operate);
			holder.operate.setBackgroundResource(R.drawable.onclick_blue_btn);
			holder.operate.setTextColor(activity.getResources().getColor(R.color.white));
			holder.operate.setText(activity.getString(R.string.apply_join_text));
			holder.operate.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14); // SP
			holder.itemLin = (LinearLayout) paramView.findViewById(R.id.itemLin);

			paramView.setTag(holder);
		} else
		{
			// paramView = map.get(position);
			holder = (ViewHolder) paramView.getTag();
		}

		if (paramView != null)
		{
			final EpcInfo info = storageList.get(position);
			holder.orderText.setText((position + 1) + "");
			holder.numberText.setText(info.getMaterialId());
//			if ("0".equals(info.getMaterialCount()))
//			{
//				holder.countText.setText(info.getCounts()); // 如果不是箱装，则是独立的一个，则取其数量
//			} else
//			{
//				holder.countText.setText(info.getInBoxCount()); // 箱装则取内部数量
//			}
			
			holder.countText.setText(info.getMaterialCount()); 

			holder.inDateText.setText(StringUtil.formatDateByString(info.getInDate()));
			holder.nameText.setText(info.getMaterialName()+info.getMaterialSpecDescribe());

			holder.deliveryOrderNumber.setText(info.getDeliveryOrderNumber());
			holder.statueText.setText(ConfigUtil.getMaterialStatus(activity, info.getMaterialStatus()));
			ConfigUtil.setTextColor(activity, holder.statueText, info.getMaterialStatus());

			copyTextView(holder.deliveryOrderNumber);
			copyTextView(holder.numberText);
			copyTextView(holder.nameText);
			
			holder.deliveryOrderNumber.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View arg0)
				{
					getMaterialDetail(info);
				}
			});

			holder.numberText.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View arg0)
				{
					getMaterialDetail(info);
				}
			});

			holder.nameText.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View arg0)
				{
					 getMaterialDetail(info);
				}
			});

			holder.inDateText.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View arg0)
				{
					getMaterialDetail(info);
				}
			});

			// 加入购物车
			holder.operate.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View arg0)
				{
					String status = info.getMaterialStatus();
					if (ConfigUtil.ALLOW_APPLY.equals(status))
					{
						Message msg = Message.obtain();
						msg.what = ZKCmd.HAND_APPLY_JOIN;
						msg.obj = info;
						handler.sendMessage(msg);
					} else
					{
						String msg = ConfigUtil.getMaterialStatus(activity, status);
						CommUtil.toastShow(msg + "不允许加入", activity);
					}
				}
			});
		}

		return paramView;
	}

	/** 管理员的操作需要选择 **/
//	private void mangerDialog(final EpcInfo epcInfo)
//	{
//		Button logout_btn, damage_btn, change_use_btn;
//		final Dialog dialog = new Dialog(activity, R.style.dialogstyle);
//		dialog.setContentView(R.layout.manager_dialog);
//		logout_btn = (Button) dialog.findViewById(R.id.logout_btn);
//		damage_btn = (Button) dialog.findViewById(R.id.damage_btn);
//		change_use_btn = (Button) dialog.findViewById(R.id.change_use_btn);
//
//		logout_btn.setOnClickListener(new View.OnClickListener()
//		{
//			@Override
//			public void onClick(View v)
//			{
//				changeMaterialFlag(ConfigUtil.HAVE_LOGOUT, epcInfo);
//				dialog.cancel();
//			}
//		});
//		damage_btn.setOnClickListener(new View.OnClickListener()
//		{
//			@Override
//			public void onClick(View v)
//			{
//				changeMaterialFlag(ConfigUtil.HAVE_DAMAGE, epcInfo);
//				dialog.cancel();
//			}
//		});
//
//		change_use_btn.setOnClickListener(new View.OnClickListener()
//		{
//			@Override
//			public void onClick(View v)
//			{
//				changeMaterialFlag(ConfigUtil.ALLOW_APPLY, epcInfo);
//				dialog.cancel();
//			}
//		});
//		dialog.setCancelable(true);
//		dialog.show();
//	}

//	private void changeMaterialFlag(String type, EpcInfo epcInfo)
//	{
//		ManagerDao dao = new ManagerDao(activity, mHandler);// 回到自己内部处理
//		dao.updateMaterialState(type, epcInfo);
//		dao = null;
//	}

	private void updateRes(String res)
	{
		// {"result":{"state":"1","msg":"操作成功"}}

		try
		{
			String str = new JSONObject(res).getJSONObject("result").toString();
			Map<String, String> map = CommUtil.getJsonMap(str);

			String state = map.get("state");
			if ("1".equals(state))
			{
				handler.sendEmptyMessage(ZKCmd.HAND_APPLY_SUCCESS_RES);
			}
			CommUtil.toastShow(map.get("msg"), activity);
		} catch (Exception e)
		{
			CommUtil.toastShow("操作返回结果异常", activity);
		}
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

	public void addItem(List<EpcInfo> list)
	{
		if (null != list && list.size() > 0)
		{
			storageList.addAll(list);// 加载全部
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
				String name = "";
				String pos = "";
				for (int i = 0; i < len; i++)
				{
					EpcInfo info = copyList.get(i);
					code = info.getMaterialId() + info.getDeliveryOrderNumber()+"";
					name = info.getMaterialName() + info.getMaterialSpecDescribe()+"";
					pos = info.getLocation() + "";

					if (code.contains(str) || name.contains(str) || pos.contains(str))
					{
						filterData.add(info);
					}
				}
			}
		}

		storageList = filterData;
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
		Collections.sort(storageList, new Comparator<EpcInfo>()
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
				if(ConfigUtil.ORDER_INDATE.equals(type))
				{		
					long code1=CommUtil.getStringToLong(StringUtil.formatDateByString(info1.getInDate()));
					long code2=CommUtil.getStringToLong(StringUtil.formatDateByString(info2.getInDate()));
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

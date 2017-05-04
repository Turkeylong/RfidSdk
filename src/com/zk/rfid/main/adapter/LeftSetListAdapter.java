package com.zk.rfid.main.adapter;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
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
import android.widget.RadioGroup;
import android.widget.TextView;

import com.zk.rfid.R;
import com.zk.rfid.init.InitApp;
import com.zk.rfid.login.activity.EditPwdActivity;
import com.zk.rfid.login.work.UserWork;
import com.zk.rfid.main.activity.AdjustActivity;
import com.zk.rfid.main.activity.ModifyBoxActivity;
import com.zk.rfid.main.activity.OperateEpcActivity;
import com.zk.rfid.main.activity.StoreToTalActivity;
import com.zk.rfid.main.activity.WaitModifyCountActivity;
import com.zk.rfid.main.util.ConfigUtil;
import com.zk.rfid.map.activity.AddMaterialActivity;
import com.zk.rfid.map.activity.EpcWriteActivity;
import com.zk.rfid.util.CommUtil;
import com.zk.rfid.util.SysApplication;
import com.zk.rfid.util.ZKCmd;

/**
 * @Description: 左侧菜单容器
 * @date:2016-5-14 下午11:21:30
 * @author: ldl
 */
public class LeftSetListAdapter extends BaseAdapter
{

	private Activity activity;
	private List<Map<String, Object>> listData;
	// private HashMap<Integer, View> map = new HashMap<Integer, View>();
	private Handler handler;

	private Handler mHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{

			case ZKCmd.HAND_RESPONSE_DATA:

				int cb = msg.arg1;
				String res = msg.obj + "";
				if (ZKCmd.ARG_REQUEST_INIT == cb)
				{
					int count = new InitApp(mHandler).saveInitData(res);
					if (count > 0)
					{
						CommUtil.toastShow("刷新成功", activity);
					} else
					{
						CommUtil.toastShow("刷新失败", activity);
					}
				}
				break;

			default:
				break;
			}
		};
	};

	public LeftSetListAdapter(Activity act, Handler handler, List<Map<String, Object>> list)
	{
		activity = act;
		listData = list;
		this.handler = handler;
	}

	static class ViewHolder
	{
		ImageView item_img_left;
		TextView item_text;
		TextView remind_text;
		LinearLayout itemLin;
	}

	@Override
	public int getCount()
	{
		if (null != listData)
		{
			return listData.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int arg0)
	{
		return listData.get(arg0);
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
			paramView = LayoutInflater.from(activity).inflate(R.layout.left_set_item, null);
			// map.put(position, paramView);

			holder = new ViewHolder();
			holder.item_img_left = (ImageView) paramView.findViewById(R.id.item_img_left);
			holder.item_text = (TextView) paramView.findViewById(R.id.item_text);
			holder.remind_text = (TextView) paramView.findViewById(R.id.remind_text);
			holder.itemLin = (LinearLayout) paramView.findViewById(R.id.itemLin);
			paramView.setTag(holder);
		} else
		{
			// paramView = map.get(position);
			holder = (ViewHolder) paramView.getTag();
		}
		if (paramView != null)
		{
			final Map<String, Object> map = listData.get(position);
			holder.item_img_left.setImageBitmap((Bitmap) map.get("img"));
			holder.item_text.setText(map.get("text") + "");

			holder.itemLin.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View arg0)
				{
					detail(map.get("type") + "");
				}
			});
		}

		return paramView;
	}

	/**左侧菜单，根据标志码跳转到对应的界面**/
	private void detail(String type)
	{
		Message msg = null;

		// 修改箱子
		if (ConfigUtil.LEFT_MODIFY_BOX_COUNT.equals(type))
		{
			Intent edp = new Intent();
			edp.setClass(activity, ModifyBoxActivity.class);
			activity.startActivity(edp);
		}
		
		//待修改标签
		if(ConfigUtil.LEFT_MODIFY_NEED.equals(type))
		{
			Intent edp = new Intent();
			edp.setClass(activity, WaitModifyCountActivity.class);
			activity.startActivity(edp);	
		}

		//修改物资状态及调整仓位
		if (ConfigUtil.LEFT_READ_ECP.equals(type))
		{
			Intent edp = new Intent();
			edp.setClass(activity, OperateEpcActivity.class);
			activity.startActivity(edp);
		}

		// 库存明细,到货记录，发放记录，物资新增记录
		if (ConfigUtil.LEFT_REPORTS.equals(type) || ConfigUtil.LEFT_ARRIVE_REPORTS.equals(type)
				|| ConfigUtil.LEFT_EXIT_REPORTS.equals(type) || ConfigUtil.LEFT_MATERIAL_ADD_RECORD.equals(type))
		{
			Intent intent = new Intent();
			intent.setClass(activity, StoreToTalActivity.class);
			intent.putExtra("type", type);
			activity.startActivity(intent);
		}

		//修改密码
		if (ConfigUtil.LEFT_EDIT_PAWSSORD.equals(type))
		{
			Intent edp = new Intent();
			edp.setClass(activity, EditPwdActivity.class);
			activity.startActivity(edp);
		}
		
		//显示个信息
		if (ConfigUtil.LEFT_USER_INFO.equals(type))
		{
			UserWork.showUserInfo(activity);
		}

		//与我相关的,此项暂未用
		if (ConfigUtil.LEFT_MY_INFO.equals(type))
		{
			msg = Message.obtain();
			msg.obj = ConfigUtil.LEFT_MY_INFO;
			msg.what = ZKCmd.HAND_LEFT_ONCLICK;
			handler.sendMessage(msg);
		}

		//查询的借领记录，此项暂未用
		if (ConfigUtil.LEFT_MY_RECORD.equals(type))
		{
			msg = Message.obtain();
			msg.obj = ConfigUtil.LEFT_MY_RECORD;
			msg.what = ZKCmd.HAND_LEFT_ONCLICK;
			handler.sendMessage(msg);
		}

		//物资EPC码写入标签操作列表
		if (ConfigUtil.LEFT_MATERIAL_STORAGE.equals(type))
		{
			Intent edp = new Intent();
			edp.setClass(activity, EpcWriteActivity.class);
			activity.startActivity(edp);
		}

		//新增物资，20160926暂时屏蔽
		if (ConfigUtil.LEFT_MATERIAL_ADD.equals(type))
		{
			Intent edp = new Intent();
			edp.setClass(activity, AddMaterialActivity.class);
			activity.startActivity(edp);
		}

		//刷新缓存，主要针对初始化时获得的状态，流程码，及规格
		if (ConfigUtil.LEFT_MATERIAL_CACHE.equals(type))
		{
			InitApp init = new InitApp(mHandler);// 回这里提示信息
			init.getInitData();// 初始化网络数据
			init = null;
		}
		
		//调仓，此项暂未用，入口在调整状界面
		if (ConfigUtil.LEFT_MATERIAL_NEW_EPC.equals(type))
		{
			Intent edp = new Intent();
			edp.setClass(activity, AdjustActivity.class);
			activity.startActivity(edp);
		}

		//系统设置，暂未用，本用于设置网络的reader的IP地址
		if (ConfigUtil.LEFT_SYSTEM_SET.equals(type))
		{
			setConnParam(activity);
		}

		//退出系统
		if (ConfigUtil.LEFT_SYSTEM_EXIT.equals(type))
		{
			Editor editor = CommUtil.getShare(activity).edit();
			editor.remove(ConfigUtil.USER_NAME);
			editor.remove(ConfigUtil.USER_PASSWORD);
			editor.commit();
			SysApplication.getInstance().exit();
		}
	}

	/** 设置系统参数对话框 **/
	private void setConnParam(final Activity activity)
	{
		final Dialog dialog = new Dialog(activity, R.style.dialogstyle);
		dialog.setContentView(R.layout.setmethod);

		final EditText method_port = (EditText) dialog.findViewById(R.id.method_port);
		final TextView metp_text = (TextView) dialog.findViewById(R.id.metp_text);
		method_port.setText(CommUtil.getShare(activity).getString(ConfigUtil.EPC_SERVER_WAN_PORT, ""));

		final EditText method_ip = (EditText) dialog.findViewById(R.id.method_ip);
		method_ip.setText(CommUtil.getShare(activity).getString(ConfigUtil.EPC_SERVER_WAN_IP, ""));

		final RadioGroup methodRadioGroup = (RadioGroup) dialog.findViewById(R.id.methodRadioGroup);
		methodRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1)
			{
				if (R.id.radioCom == arg1)
				{
					metp_text.setVisibility(View.INVISIBLE);
					method_port.setVisibility(View.INVISIBLE);
					method_ip.setText(CommUtil.getShare(activity).getString(ConfigUtil.EPC_SERVER_COM_IP, ""));
				}
				if (R.id.radioWan == arg1)
				{
					metp_text.setVisibility(View.VISIBLE);
					method_port.setVisibility(View.VISIBLE);
					method_ip.setText(CommUtil.getShare(activity).getString(ConfigUtil.EPC_SERVER_WAN_IP, ""));
				}
			}
		});

		Button submit_btn = (Button) dialog.findViewById(R.id.submit_btn);
		submit_btn.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				int id = methodRadioGroup.getCheckedRadioButtonId();
				String edIp = method_ip.getText().toString();
				if (CommUtil.isIp(edIp))
				{
					Editor editor = CommUtil.getShare(activity).edit();
					if (R.id.radioCom == id)
					{
						editor.putString(ConfigUtil.EPC_SERVER_COM_IP, edIp);
						editor.putString(ConfigUtil.EPC_METHOD, ConfigUtil.EPC_METHOD_COM);
					}
					if (R.id.radioWan == id)
					{
						String port = method_port.getText().toString();
						editor.putString(ConfigUtil.EPC_SERVER_WAN_IP, edIp);
						editor.putString(ConfigUtil.EPC_METHOD, ConfigUtil.EPC_METHOD_WAN);
						editor.putString(ConfigUtil.EPC_SERVER_WAN_PORT, port);
					}
					editor.commit();
					CommUtil.toastShow("操作成功", activity);
				} else
				{
					CommUtil.toastShow("IP地址不合法", activity);
				}
			}
		});

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
}

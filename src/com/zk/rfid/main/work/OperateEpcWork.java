package com.zk.rfid.main.work;

import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.zk.rfid.R;
import com.zk.rfid.comm.base.BootBroadcastReceiver;
import com.zk.rfid.comm.base.MyApplication;
import com.zk.rfid.main.adapter.OperateEpcListAdapter;
import com.zk.rfid.main.bean.EpcInfo;
import com.zk.rfid.main.util.ConfigUtil;
import com.zk.rfid.util.CommUtil;
import com.zk.rfid.util.LogUtil;
import com.zk.rfid.util.SystemUtil;
import com.zk.rfid.util.ZKCmd;

/**
 * @Description: EPC盘点操作
 * @date:2016-6-27下午3:30:37
 * @author: ldl
 */
public class OperateEpcWork
{
	private static final String TAG = "OperateEpcWork";
	private static OperateEpcWork operateEpcWork;
	private Handler handler;
	private Activity activity;

	private LinearLayout operateLin;
	private Button start_btn, writ_btn, logout_btn, damage_btn, change_use_btn, change_lend_btn;
	private TextView start_show, showCounText;
	private EpcWork epcWork;
	private OperateEpcListAdapter scanAdapter;
	private ListView scan_list;

	private OperateEpcWork(Activity activity)
	{
		this.activity = activity;
	}

	public static OperateEpcWork getInstance(Activity activity)
	{
		if (null == operateEpcWork)
		{
			operateEpcWork = new OperateEpcWork(activity);
		}
		return operateEpcWork;
	}

	public void initView(final Handler handler)
	{
		this.handler = handler;
		operateLin = (LinearLayout) activity.findViewById(R.id.operateLin);
		scan_list = (ListView) activity.findViewById(R.id.scan_list);

		writ_btn = (Button) activity.findViewById(R.id.writ_btn);
		writ_btn.setVisibility(View.GONE);

		start_btn = (Button) activity.findViewById(R.id.start_btn);

		logout_btn = (Button) activity.findViewById(R.id.logout_btn);
		damage_btn = (Button) activity.findViewById(R.id.damage_btn);
		change_use_btn = (Button) activity.findViewById(R.id.change_use_btn);
		change_lend_btn = (Button) activity.findViewById(R.id.change_lend_btn);

		start_show = (TextView) activity.findViewById(R.id.start_show);
		showCounText = (TextView) activity.findViewById(R.id.showCounText);

		start_btn.setOnClickListener(listen);
		change_lend_btn.setOnClickListener(listen);
		change_use_btn.setOnClickListener(listen);
		logout_btn.setOnClickListener(listen);
		damage_btn.setOnClickListener(listen);

		epcWork = MyApplication.getRfidWork();
		epcWork.setReaderHandler(handler);

	}

	public static void clearOperateEpcWork()
	{
		operateEpcWork = null;
	}

	
	private OnClickListener listen = new View.OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			switch (v.getId())
			{

			case R.id.start_btn:

				startScan();
				break;

			case R.id.change_lend_btn:
				if(SystemUtil.getPower(SystemUtil.MATERIAL_UPDATE))
				{
					updateStatus(ConfigUtil.HAVE_GET_USE);
				}
				else
				{
					CommUtil.toastShow("您没有物资状态修改权限，请联系管理员！", activity);
				}
				
				break;

			case R.id.change_use_btn:
				if(SystemUtil.getPower(SystemUtil.MATERIAL_UPDATE))
				{
					updateStatus(ConfigUtil.ALLOW_APPLY);
				}
				else
				{
					CommUtil.toastShow("您没有物资状态修改权限，请联系管理员！", activity);
				}
				
				break;

			case R.id.logout_btn:
				if(SystemUtil.getPower(SystemUtil.MATERIAL_UPDATE))
				{
					updateStatus(ConfigUtil.HAVE_LOGOUT);
				}
				else
				{
					CommUtil.toastShow("您没有物资状态修改权限，请联系管理员！", activity);
				}
				
				break;

			case R.id.damage_btn:
				if(SystemUtil.getPower(SystemUtil.MATERIAL_UPDATE))
				{
					updateStatus(ConfigUtil.HAVE_DAMAGE);
				}
				else
				{
					CommUtil.toastShow("您没有物资状态修改权限，请联系管理员！", activity);
				}
				
				break;

			default:
				break;
			}
		}

	};

	/**更新所选中的物资状态，箱子或金属标签则不可直接修改状态**/
	private void updateStatus(String state)
	{
		if (null != scanAdapter)
		{
			scanAdapter.updateStatus(state);
		} else
		{
			CommUtil.toastShow("无数据可操作", activity);
		}
	}

	/**开始扫描**/
	public void startScan()
	{
		showCounText.setText("");
		epcWork.clearStackCash();
		epcWork.setReaderHandler(handler);
		epcWork.powerOn();
		SystemClock.sleep(500);
		epcWork.scanData();

		handler.sendEmptyMessage(ZKCmd.HAND_START_SCAN);
		if (null != scanAdapter)
		{
			scan_list.setAdapter(null);
		}
	}

	/**停止扫描**/
	public void stopScan()
	{
		epcWork.stopScan();
	}

	/**显示扫描的到数据**/
	public void showScanData(List<EpcInfo> listData)
	{
		if (0 == listData.size())
		{
			CommUtil.toastShow(activity.getString(R.string.query_no_data_msg), activity);
			operateLin.setVisibility(View.GONE);
		} else
		{
			operateLin.setVisibility(View.VISIBLE);
		}
		showCounText.setText("共" + listData.size() + "记录");
		showCounText.setVisibility(View.VISIBLE);

		LogUtil.i(TAG, "要显示多少条:" + listData.size());
		scanAdapter = null;
		scanAdapter = new OperateEpcListAdapter(activity, handler, listData);
		scan_list.setAdapter(scanAdapter);
		// CommUtil.setListViewHeight(scan_list);//这里不能设置。否则会导致列表显示不全
	}

	/** 后续分页追加的数据 **/
	public void showScanDataByLoad(List<EpcInfo> listData, boolean lastOne)
	{

		if (null != scanAdapter)
		{

			scanAdapter.addItem(listData);
			// SystemClock.sleep(100);
			scanAdapter.notifyDataSetChanged();
			// SystemClock.sleep(100);
			showCounText.setText("共" + scanAdapter.getCount() + "记录");
			if (lastOne)
			{
				// 延迟1.5秒再排序
				new CountDownTimer(1500, 100)
				{
					@Override
					public void onTick(long millisUntilFinished)
					{
					}

					@Override
					public void onFinish()
					{
						scanAdapter.showByOrder();// 最后一条则倒着排
					}
				}.start();

			}
		}
	}

	/** 修改epc **/
	public boolean modifyEpc(String oldCode, String newCode)
	{
		epcWork.setReaderHandler(handler);
		epcWork.powerOn();
		SystemClock.sleep(500);
		return epcWork.modifyEpcData(oldCode, newCode);
	}

	public void reQuery()
	{
		start_btn.performClick();
	}

	public void powerOff() {
		// TODO 自动生成的方法存根
		epcWork.powerOff();
	}

	public void startInfScan() {
		// TODO 自动生成的方法存根
		showCounText.setText("");
		epcWork.clearStackCash();
		epcWork.setReaderHandler(handler);
		epcWork.powerOn();
		SystemClock.sleep(500);
		epcWork.startInfScan();

		handler.sendEmptyMessage(ZKCmd.HAND_START_SCAN);
		if (null != scanAdapter)
		{
			scan_list.setAdapter(null);
		}	
	}

}

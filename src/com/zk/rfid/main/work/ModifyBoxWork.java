package com.zk.rfid.main.work;

import java.util.List;

import android.app.Activity;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.zk.rfid.R;
import com.zk.rfid.comm.base.MyApplication;
import com.zk.rfid.main.adapter.ModifyBoxAdapter;
import com.zk.rfid.main.bean.EpcInfo;
import com.zk.rfid.util.CommUtil;
import com.zk.rfid.util.LogUtil;
import com.zk.rfid.util.ZKCmd;

/**
 * @Description: 修改箱子操作
 * @date:2016-6-27下午3:30:37
 * @author: ldl
 */
public class ModifyBoxWork
{
	private static final String TAG = "ModifyBoxWork";
	private static ModifyBoxWork modifyBoxWork;
	private Handler handler;
	private Activity activity;

	private Button start_btn;
	private TextView start_show, showCounText;
	private EpcWork epcWork;
	private ModifyBoxAdapter modifyBoxAdapter;
	private ListView scan_list;

	private ModifyBoxWork(Activity activity)
	{
		this.activity = activity;
	}

	public static ModifyBoxWork getInstance(Activity activity)
	{
		if (null == modifyBoxWork)
		{
			modifyBoxWork = new ModifyBoxWork(activity);
		}
		return modifyBoxWork;
	}

	public void initView(final Handler handler)
	{
		this.handler = handler;
		scan_list = (ListView) activity.findViewById(R.id.scan_list);

		start_btn = (Button) activity.findViewById(R.id.start_btn);
		start_show = (TextView) activity.findViewById(R.id.start_show);
		showCounText = (TextView) activity.findViewById(R.id.showCounText);

		start_btn.setOnClickListener(listen);

		epcWork = MyApplication.getRfidWork();
		epcWork.setReaderHandler(handler);

	}

	public static void clearModifyBoxWork()
	{
		modifyBoxWork = null;
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

			default:
				break;
			}
		}

	};

	/**开始盘点，需清空一次栈缓存，通电并延迟再执行扫描**/
	private void startScan()
	{
		showCounText.setText("");
		epcWork.clearStackCash();
		
		epcWork.setReaderHandler(handler);
		epcWork.powerOn();
		SystemClock.sleep(500);
		epcWork.scanData();

		handler.sendEmptyMessage(ZKCmd.HAND_START_SCAN);
		if (null != modifyBoxAdapter)
		{
			scan_list.setAdapter(null);
		}
	}

	/**停止扫描**/
	public void stopScan()
	{
		epcWork.stopScan();
	}

	/**显示扫描到的标签数据**/
	public void showScanData(List<EpcInfo> listData)
	{
		if (0 == listData.size())
		{
			CommUtil.toastShow(activity.getString(R.string.query_no_data_msg), activity);
		} else
		{
			// operateLin.setVisibility(View.VISIBLE);
		}
		showCounText.setText("共" + listData.size() + "记录");
		showCounText.setVisibility(View.VISIBLE);

		LogUtil.i(TAG, "要显示多少条:" + listData.size());
		modifyBoxAdapter = null;
		modifyBoxAdapter = new ModifyBoxAdapter(activity, handler, listData);
		scan_list.setAdapter(modifyBoxAdapter);
		// CommUtil.setListViewHeight(scan_list);//设置会导致显示不全
	}

	/** 后续追加扫描的数据,因一次可能扫描到的数据量过大，会导致显示卡顿，所以采用第一次加载数据少，先显示出来再追加 **/
	public void showScanDataByLoad(List<EpcInfo> listData, boolean lastOne)
	{

		if (null != modifyBoxAdapter)
		{

			modifyBoxAdapter.addItem(listData);
			SystemClock.sleep(50);
			modifyBoxAdapter.notifyDataSetChanged();
			showCounText.setText("共" + modifyBoxAdapter.getCount() + "记录");
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
						modifyBoxAdapter.showByOrder();// 最后一条则倒着排
					}
				}.start();

			}
		}
	}

	/** 修改指定的epc码为新的码 **/
	public boolean modifyEpc(String oldCode, String newCode)
	{
		epcWork.setReaderHandler(handler);
		epcWork.powerOn();
		SystemClock.sleep(500);
		return epcWork.modifyEpcData(oldCode, newCode);
	}

	/** 重新扫描 **/
	public void reScan()
	{
		start_btn.performClick();
	}

	public void powerOff() {
		// TODO 自动生成的方法存根
		epcWork.powerOff();
	}

}

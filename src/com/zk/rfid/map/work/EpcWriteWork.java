package com.zk.rfid.map.work;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.zk.rfid.R;
import com.zk.rfid.comm.base.MyApplication;
import com.zk.rfid.comm.date.DatePickerDialogUtil;
import com.zk.rfid.comm.date.DateTimeCallBackListener;
import com.zk.rfid.comm.listvew.CustomListView;
import com.zk.rfid.comm.listvew.CustomListView.OnLoadListener;
import com.zk.rfid.comm.listvew.CustomListView.OnRefreshListener;
import com.zk.rfid.main.bean.EpcInfo;
import com.zk.rfid.main.util.ConfigUtil;
import com.zk.rfid.map.adapter.EpcValListAdapter;
import com.zk.rfid.util.CommUtil;
import com.zk.rfid.util.ConstantUtil;
import com.zk.rfid.util.LogUtil;
import com.zk.rfid.util.StringUtil;
import com.zk.rfid.util.ZKCmd;

/**
 * @Description: 标签处理
 * @date:2016-6-6下午3:27:01
 * @author: ldl
 */
public class EpcWriteWork
{

	private static final String TAG = "EpcWriteWork";

	private Activity activity;
	private Handler handler;

	private EpcValListAdapter listAdapter;

	private static Dialog timeDialog;

	// 查询控件
	private Button queryBtn;
	private EditText queryEditNumber, queryEditMaterial, beginTime, endTime, seacherEdit;
	private ImageView zk_gname_delete, zk_gnum_delete, zk_begintime_delete, zk_endtime_delete, seacher_delete;
	private RadioGroup boxRadioGroup;
	private CheckBox posCheck;

	// 显示列表
	private CustomListView dataListView;
	private TextView showCounText, numberText,dateText,posText;
	private LinearLayout countTextsLin;
	private int PAGE = 1;

	private static EpcWriteWork work;

	private EpcWriteWork(Activity activity)
	{
		this.activity = activity;
	}

	public static EpcWriteWork getInstance(Activity activity)
	{
		if (work == null)
		{
			work = new EpcWriteWork(activity);
		}
		return work;
	}

	public void initView(Handler handler)
	{
		this.handler = handler;
		initView();
		setListen();

	}

	/**根据是从新增物资跳转过来的，则自动填充当前日期，把今天的需要写标签的数据获取出来**/
	public void queryData(boolean time)
	{
		if (time)
		{
			String timeStr = StringUtil.getDate();
			beginTime.setText(timeStr);
			endTime.setText(timeStr);
		}

		startQuery(0);// 默认执行查询
	}

	private void initView()
	{
		dataListView = (CustomListView) activity.findViewById(R.id.dataListView);

		queryEditNumber = (EditText) activity.findViewById(R.id.queryEditNumber);
		queryEditMaterial = (EditText) activity.findViewById(R.id.queryEditMaterial);
		seacherEdit = (EditText) activity.findViewById(R.id.seacherEdit);

		beginTime = (EditText) activity.findViewById(R.id.beginTime);
		endTime   = (EditText) activity.findViewById(R.id.endTime);
		posCheck  = (CheckBox) activity.findViewById(R.id.posCheck);

		queryBtn  = (Button) activity.findViewById(R.id.queryBtn);
		boxRadioGroup = (RadioGroup) activity.findViewById(R.id.boxRadioGroup);
		boxRadioGroup.setVisibility(View.GONE);// 箱子与非箱子查询条件隐藏

		zk_gname_delete = (ImageView) activity.findViewById(R.id.zk_gname_delete);
		zk_gnum_delete  = (ImageView) activity.findViewById(R.id.zk_gnum_delete);
		zk_begintime_delete = (ImageView) activity.findViewById(R.id.zk_begintime_delete);
		zk_endtime_delete   = (ImageView) activity.findViewById(R.id.zk_endtime_delete);
		seacher_delete      = (ImageView) activity.findViewById(R.id.seacher_delete);

		// 显示信息
		showCounText = (TextView) activity.findViewById(R.id.showCounText);
		numberText   = (TextView) activity.findViewById(R.id.numberText);
		dateText     = (TextView) activity.findViewById(R.id.dateText);
		posText      = (TextView) activity.findViewById(R.id.posText);
		countTextsLin = (LinearLayout) activity.findViewById(R.id.countTextsLin);

	}

	private void setListen()
	{
		CommUtil.editTextChange(queryEditMaterial, zk_gname_delete);
		CommUtil.editTextChange(queryEditNumber, zk_gnum_delete);
		CommUtil.editTextChange(beginTime, zk_begintime_delete);
		CommUtil.editTextChange(endTime, zk_endtime_delete);

		queryBtn.setOnClickListener(listener);

		beginTime.setOnTouchListener(new View.OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				getTime(beginTime);
				return false;
			}
		});

		endTime.setOnTouchListener(new View.OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				getTime(endTime);
				return false;
			}
		});

		boxRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1)
			{
				startQuery(0);// 切换时也查询
			}
		});

		dataListView.setonRefreshListener(new OnRefreshListener()
		{

			@Override
			public void onRefresh()
			{
				// 下拉刷新
				LogUtil.i(TAG, "下拉刷新");
				PAGE = 1;
				startQuery(ZKCmd.HAND_REFRESH_DATA);
			}
		});

		dataListView.setonLoadListener(new OnLoadListener()
		{
			@Override
			public void onLoad()
			{
				// 加载更多
				LogUtil.i(TAG, "加载更多");
				PAGE++;
				startQuery(ZKCmd.HAND_LOAD_DATA);
			}
		});

		posCheck.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				if (isChecked)
				{
					CommUtil.toastShow("请从左滑平面图中筛选位置", activity);
				} else
				{
					Editor editor = CommUtil.getPositionShare(activity).edit();
					editor.clear();
					editor.commit();
					handler.sendEmptyMessage(ZKCmd.HAND_CLEAR_BOX_CHECK);
				}
			}
		});

		numberText.setOnClickListener(listener);
		dateText.setOnClickListener(listener);
		posText.setOnClickListener(listener);
	}

	private OnClickListener listener = new View.OnClickListener()
	{
		@Override
		public void onClick(View key)
		{
			switch (key.getId())
			{

			case R.id.queryBtn:

				PAGE = 1;// 恢复条件
				startQuery(0);

				break;

			case R.id.numberText:

				if (null != listAdapter)
				{
					listAdapter.orderChange(ConfigUtil.ORDER_CODE);
				}
				break;
			case R.id.dateText:

				if (null != listAdapter)
				{
					listAdapter.orderChange(ConfigUtil.ORDER_DATE);
				}
				break;
				
			case R.id.posText:

				if (null != listAdapter)
				{
					listAdapter.orderChange(ConfigUtil.ORDER_POSITION);
				}
				break;

			default:
				break;
			}
		}
	};

	boolean flag = false;

	private void getTime(final EditText editText)
	{
		if (!flag)
		{
			flag = true;// 这里设置标记，版本不同，可能会导致弹出多次
			DatePickerDialogUtil dia = new DatePickerDialogUtil(activity);
			timeDialog = dia.getDate(new DateTimeCallBackListener()
			{
				@Override
				public void dateTimeCallBack(String str)
				{
					flag = false;
					if (!"cancle".equals(str))
					{
						editText.setText(str);
					}
				}
			});
			timeDialog.show();
		}
	}

	/**执行查询**/
	private void startQuery(int loadType)
	{
		int radioId = boxRadioGroup.getCheckedRadioButtonId();

		String number = queryEditNumber.getText().toString();
		String materialName = queryEditMaterial.getText().toString();
		String begin = beginTime.getText().toString();
		String end = endTime.getText().toString();

		if (!"".equals(number))
		{
			if (11 != number.length())
			{
				CommUtil.toastShow("物资编号不足11位", activity);
				return;
			}
		}

		if (!"".equals(begin) && "".equals(end))
		{
			// 结束时间不能为空
			CommUtil.toastShow(activity.getString(R.string.date_time_notify_en_empty), activity);
			return;
		}
		if ("".equals(begin) && !"".equals(end))
		{
			// 开始时间不能为空
			CommUtil.toastShow(activity.getString(R.string.date_time_notify_be_empty), activity);
			return;
		}

		if (!"".equals(begin) && !"".equals(end))
		{
			// 开始时间不能大过结束时间
			int beTmp = Integer.parseInt(begin.replaceAll("-", ""));
			int enTmp = Integer.parseInt(end.replaceAll("-", ""));
			if (beTmp > enTmp)
			{
				CommUtil.toastShow(activity.getString(R.string.date_time_notify_not_allow), activity);
				return;
			}
		}

		String isBox = "0";
		if (radioId == R.id.radioBoxYes)
		{
			isBox = "1";
		}

		String location = "";
		// 是否选择了位置
		if (posCheck.isChecked())
		{
			Map<String, String> map = (Map<String, String>) CommUtil.getPositionShare(activity).getAll();
			if (null != map && map.size() > 0)
			{
				Iterator it = map.entrySet().iterator();
				StringBuilder sbCode = new StringBuilder();
				//int count=20;//模拟位号20个
				
				while (it.hasNext())
				{
					Entry entry = (Entry) it.next();
					
					sbCode.append("'"+entry.getValue()+"%' ").append(",");										
				}
				sbCode.deleteCharAt(sbCode.length() - 1);// 把最后一个,删掉
				location = sbCode.toString();
			} else
			{
				CommUtil.toastShow("没有选中任何位置", activity);
				return;
			}
		}

		StringBuffer sb = new StringBuffer();
		sb.append("t=" + ZKCmd.REQ_GET_STORAGE);
		sb.append("&materialId=" + number);
		sb.append("&materialName=" + materialName);
		sb.append("&beginInDate=").append(begin);
		sb.append("&endInDate=").append(end);
		sb.append("&page=").append(PAGE + "");
		sb.append("&pageSize=").append(ConstantUtil.PAGE_SIZE + "");
		// sb.append("&isbox=").append(isBox);//是否箱子,1代表箱子，0是物资
		sb.append("&positionCodeList=").append(location);// 仓位

		LogUtil.i(TAG, "请求的url:" + sb.toString());

		Message msg = Message.obtain();
		msg.obj = sb.toString();
		msg.arg1 = ZKCmd.ARG_REQUEST_EPC_LIST;
		msg.arg2 = loadType;
		msg.what = ZKCmd.HAND_REQUEST_DATA;
		handler.sendMessage(msg);

	}

	/** 显示查询物资的数据，以方便直接写入标签 **/
	public void showEpcData(List<EpcInfo> list)
	{
		// listAdapter = null;
		listAdapter = new EpcValListAdapter(activity, handler, list);
		dataListView.setAdapter(listAdapter);
		// CommUtil.setListViewHeight(dataListView); //若设置会导致无法滚到底部
		showCountText();
		listAdapter.seacherTextChange(seacherEdit, seacher_delete);
	}

	/**底部分页加载**/
	public void showEpcDataByLoad(List<EpcInfo> list, int type)
	{
		if (null != list && list.size() > 0)
		{
			LogUtil.i(TAG, "这次返回多少条数据：" + list.size());
			if (ZKCmd.HAND_REFRESH_DATA == type)
			{
				listAdapter = null;
				listAdapter = new EpcValListAdapter(activity, handler, list);
				dataListView.setAdapter(listAdapter);
				listAdapter.seacherTextChange(seacherEdit, seacher_delete);// 重新监听
				seacherEdit.setText("");// 利用这个来触发更新列表
			} else
			{
				listAdapter.addItem(list);
				// listAdapter.notifyDataSetChanged();
				seacherEdit.setText("");// 利用这个来触发更新列表
			}
			try
			{
				Thread.sleep(300);
			} catch (Exception e)
			{
			}
		} else
		{
			CommUtil.toastShow(activity.getString(R.string.query_no_more_data_msg), activity);
		}

		if (ZKCmd.HAND_REFRESH_DATA == type)
		{
			dataListView.onRefreshComplete(); // 下拉刷新完成
		}
		if (ZKCmd.HAND_LOAD_DATA == type)
		{
			dataListView.onLoadComplete(); // 加载完成
		}
		// CommUtil.setListViewHeight(dataListView);
		showCountText();
	}

	/**显示当前记录总数**/
	public void showCountText()
	{
		int count = listAdapter.getCount();
		if (0 == count)
		{
			// countTextsLin.setVisibility(View.GONE);
			CommUtil.toastShow(activity.getString(R.string.query_no_data_msg), activity);
			// dataListView.setLoadMoreViewVisibility(View.GONE);
		}
		showCounText.setText("共" + count + "条记录");
	}

	public static void clearWork()
	{
		Editor editor = CommUtil.getPositionShare(MyApplication.context).edit();
		editor.clear();
		editor.commit();// 退出时将这些位置数据清除,否则下次会有数据直接从位置上获取

		if (null != timeDialog && timeDialog.isShowing())
		{
			timeDialog.dismiss();
			timeDialog = null;
		}
		work = null;
	}

	public void changeColer(int arg,int color) {
		// TODO 自动生成的方法存根
		LogUtil.i(TAG, "写入成功ID="+arg);	
		EpcInfo info = (EpcInfo) listAdapter.getItem(arg);
		info.setItemColor(color);
		listAdapter.notifyDataSetChanged();
	}

}

package com.zk.rfid.main.work;

import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.zk.rfid.R;
import com.zk.rfid.comm.date.DatePickerDialogUtil;
import com.zk.rfid.comm.date.DateTimeCallBackListener;
import com.zk.rfid.comm.listvew.CustomListView;
import com.zk.rfid.comm.listvew.CustomListView.OnLoadListener;
import com.zk.rfid.comm.listvew.CustomListView.OnRefreshListener;
import com.zk.rfid.main.adapter.LendListAdapter;
import com.zk.rfid.main.adapter.StorageListAdapter;
import com.zk.rfid.main.bean.EpcInfo;
import com.zk.rfid.main.util.ConfigUtil;
import com.zk.rfid.util.CommUtil;
import com.zk.rfid.util.ConstantUtil;
import com.zk.rfid.util.LogUtil;
import com.zk.rfid.util.SystemUtil;
import com.zk.rfid.util.ZKCmd;

/**
 * @Description: 主界面操作
 * @date:2016-5-13 下午4:18:56
 * @author: ldl
 */
public class ManagerWork
{
	private static final String TAG = "ManagerWork";

	private Activity activity;
	private LinearLayout query_number_lin, query_material_lin, query_name_lin, query_date_lin, query_lend_lin;

	private Button queryBtn, applyBtn;
	private EditText queryEditNumber, queryEditMaterial, queryEditUserNum, queryEditName, beginTime, endTime,
			lend_select_edit, store_select_edit, queryApplyNumber, deliveryOrderEditNumber, seacherEdit;
	private RadioGroup queryRadioGroup;
	private RadioButton radioLend, radioRecord;
	private LinearLayout epc_list_linear, store_list_linear, store_list_title_linear, epc_list_title_linear,
			list_total_title_linear, store_select_lin, lend_select_lin;
	private ImageView zk_gname_delete, zk_gnum_delete, zk_userno_delete, zk_username_delete, zk_begintime_delete,
			zk_endtime_delete, lend_select_imgview, store_select_imgview, zk_apply_delete, zk_deliveryOrder_delete,
			seacher_delete;
	private TextView showCounText;

	// 点击进行排序的字段
	private TextView deliveryOrderNumber, numberText, inDateText, lendNumberText, dateText;

	private static ManagerWork managerWork;

	private LendListAdapter listAdapter;
	private StorageListAdapter storelistAdapter;
	private CustomListView zk_epc_list, zk_storage_list;
	private int LEND_PAGE = 1;
	private int STORAGE_PAGE = 1;

	private Handler handler;
	private View mainView;
	private static Dialog timeDialog;

	private ManagerWork(Activity act)
	{
		activity = act;
	}

	public static ManagerWork getInstance(Activity act)
	{
		if (null == managerWork)
		{
			managerWork = new ManagerWork(act);
		}
		return managerWork;
	}

	public void initView(Handler handler, View view)
	{
		this.handler = handler;
		this.mainView = view;

		query_material_lin = getLin(R.id.query_material_lin);
		query_name_lin = getLin(R.id.query_name_lin);
		query_date_lin = getLin(R.id.query_date_lin);
		lend_select_lin = getLin(R.id.lend_select_lin);
		store_select_lin = getLin(R.id.store_select_lin);
		query_lend_lin = getLin(R.id.query_lend_lin);

		queryEditNumber = getEditText(R.id.queryEditNumber);
		queryEditMaterial = getEditText(R.id.queryEditMaterial);
		queryEditName = getEditText(R.id.queryEditName);
		queryEditUserNum = getEditText(R.id.queryEditUserNum);
		beginTime = getEditText(R.id.beginTime);
		endTime = getEditText(R.id.endTime);

		lend_select_edit = getEditText(R.id.lend_select_edit);
		queryApplyNumber = getEditText(R.id.queryApplyNumber);
		deliveryOrderEditNumber = getEditText(R.id.deliveryOrderEditNumber);
		seacherEdit = getEditText(R.id.seacherEdit);

		lend_select_edit.setText(ConfigUtil.getLendStatusText(activity).get(0));// 默认查全部

		store_select_edit = getEditText(R.id.store_select_edit);
		store_select_edit.setText(ConfigUtil.getStoreStatusText(activity).get(0));// 默认查全部

		zk_epc_list = (CustomListView) mainView.findViewById(R.id.zk_epc_list);
		zk_storage_list = (CustomListView) mainView.findViewById(R.id.zk_storage_list);

		epc_list_linear = (LinearLayout) mainView.findViewById(R.id.epc_list_linear);
		store_list_linear = (LinearLayout) mainView.findViewById(R.id.store_list_linear);
		store_list_title_linear = (LinearLayout) mainView.findViewById(R.id.store_list_title_linear);
		epc_list_title_linear = (LinearLayout) mainView.findViewById(R.id.epc_list_title_linear);
		list_total_title_linear = (LinearLayout) mainView.findViewById(R.id.list_total_title_linear);

		queryBtn = (Button) mainView.findViewById(R.id.queryBtn);
		applyBtn = (Button) mainView.findViewById(R.id.applyBtn);

		queryRadioGroup = (RadioGroup) mainView.findViewById(R.id.queryRadioGroup);
		radioLend = (RadioButton) mainView.findViewById(R.id.radioLend);
		radioRecord = (RadioButton) mainView.findViewById(R.id.radioRecord);

		zk_gname_delete = (ImageView) mainView.findViewById(R.id.zk_gname_delete);
		zk_gnum_delete = (ImageView) mainView.findViewById(R.id.zk_gnum_delete);

		zk_userno_delete = (ImageView) mainView.findViewById(R.id.zk_userno_delete);
		zk_username_delete = (ImageView) mainView.findViewById(R.id.zk_username_delete);
		zk_begintime_delete = (ImageView) mainView.findViewById(R.id.zk_begintime_delete);
		zk_endtime_delete = (ImageView) mainView.findViewById(R.id.zk_endtime_delete);
		lend_select_imgview = (ImageView) mainView.findViewById(R.id.lend_select_imgview);
		store_select_imgview = (ImageView) mainView.findViewById(R.id.store_select_imgview);
		zk_apply_delete = (ImageView) mainView.findViewById(R.id.zk_apply_delete);
		zk_deliveryOrder_delete = (ImageView) mainView.findViewById(R.id.zk_deliveryOrder_delete);
		seacher_delete = (ImageView) mainView.findViewById(R.id.seacher_delete);

		showCounText = (TextView) mainView.findViewById(R.id.showCounText);

		deliveryOrderNumber = (TextView) mainView.findViewById(R.id.deliveryOrderNumber);
		numberText = (TextView) mainView.findViewById(R.id.numberText);
		inDateText = (TextView) mainView.findViewById(R.id.inDateText);
		lendNumberText = (TextView) mainView.findViewById(R.id.lendNumberText);
		dateText = (TextView) mainView.findViewById(R.id.dateText);

		setOnListen();

		CommUtil.editTextChange(queryEditUserNum, zk_userno_delete);
		CommUtil.editTextChange(queryEditName, zk_username_delete);
		CommUtil.editTextChange(queryEditMaterial, zk_gname_delete);
		CommUtil.editTextChange(queryEditNumber, zk_gnum_delete);
		CommUtil.editTextChange(beginTime, zk_begintime_delete);
		CommUtil.editTextChange(endTime, zk_endtime_delete);
		CommUtil.editTextChange(queryApplyNumber, zk_apply_delete);
		CommUtil.editTextChange(deliveryOrderEditNumber, zk_deliveryOrder_delete);

		reQuery();// 自动执行查询
	}

	private void setOnListen()
	{
		final String type = CommUtil.getShare(activity).getString(ConfigUtil.USER_TYPE, ConfigUtil.USER_EMPLOYEE);
		if (type.equals(ConfigUtil.USER_EMPLOYEE))
		{
			query_name_lin.setVisibility(View.GONE);
		}

		zk_epc_list.setonRefreshListener(new OnRefreshListener()
		{
			@Override
			public void onRefresh()
			{
				// 下拉刷新
				LogUtil.i(TAG, "下拉刷新");
				LEND_PAGE = 1;
				submitQuery(ZKCmd.HAND_REFRESH_DATA);
			}
		});

		zk_epc_list.setonLoadListener(new OnLoadListener()
		{
			@Override
			public void onLoad()
			{
				// 加载更多
				LogUtil.i(TAG, "加载更多");
				LEND_PAGE++;
				submitQuery(ZKCmd.HAND_LOAD_DATA);
			}
		});

		zk_storage_list.setonRefreshListener(new OnRefreshListener()
		{
			@Override
			public void onRefresh()
			{
				// 下拉刷新
				LogUtil.i(TAG, "下拉刷新");
				STORAGE_PAGE = 1;
				submitQuery(ZKCmd.HAND_REFRESH_DATA);
			}
		});
		zk_storage_list.setonLoadListener(new OnLoadListener()
		{
			@Override
			public void onLoad()
			{
				// 加载更多
				LogUtil.i(TAG, "加载更多");
				STORAGE_PAGE++;
				submitQuery(ZKCmd.HAND_LOAD_DATA);
			}
		});

		queryRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1)
			{
				if (R.id.radioLend == arg1)
				{
					if (type.equals(ConfigUtil.USER_MANAGER))
					{
						query_name_lin.setVisibility(View.VISIBLE);
					}
					// query_date_lin.setVisibility(View.VISIBLE);
					query_lend_lin.setVisibility(View.VISIBLE);
					query_material_lin.setVisibility(View.GONE);
					applyBtn.setVisibility(View.GONE);

					setLinearVisable(epc_list_title_linear, epc_list_linear, lend_select_lin);
				}
				if (R.id.radioRecord == arg1)
				{
					query_lend_lin.setVisibility(View.GONE);
					query_material_lin.setVisibility(View.VISIBLE);
					applyBtn.setVisibility(View.VISIBLE);
					query_name_lin.setVisibility(View.GONE);
					// query_date_lin.setVisibility(View.INVISIBLE);
					setLinearVisable(store_list_title_linear, store_list_linear, store_select_lin);
				}
			}
		});

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

		queryBtn.setOnClickListener(listener);
		applyBtn.setOnClickListener(listener);
		store_select_imgview.setOnClickListener(listener);
		lend_select_imgview.setOnClickListener(listener);

		dateText.setOnClickListener(listener);
		lendNumberText.setOnClickListener(listener);
		inDateText.setOnClickListener(listener);
		numberText.setOnClickListener(listener);
		deliveryOrderNumber.setOnClickListener(listener);
	}

	// 指定清空文本框
	private void clearEditText(EditText editText)
	{
		if (null != editText)
		{
			editText.setText("");
		} else
		{
			clearEditText(queryEditNumber);
			clearEditText(queryEditMaterial);
			clearEditText(queryEditUserNum);
			clearEditText(queryEditName);
			clearEditText(beginTime);
			clearEditText(endTime);
		}
	}

	private OnClickListener listener = new View.OnClickListener()
	{
		@Override
		public void onClick(View key)
		{
			switch (key.getId())
			{
			case R.id.queryBtn:

				submitQuery(0);
				break;

			case R.id.store_select_imgview:

				List<String> stateList = ConfigUtil.getStoreStatusText(activity);
				CommUtil.initOptionListWedget(activity, store_select_lin, store_select_edit, stateList);

				break;
			case R.id.lend_select_imgview:

				List<String> lendList = ConfigUtil.getLendStatusText(activity);
				CommUtil.initOptionListWedget(activity, lend_select_lin, lend_select_edit, lendList);

				break;

			case R.id.applyBtn:
				if(SystemUtil.getPower(SystemUtil.MATERIAL_APPLY))
				{
					handler.sendEmptyMessage(ZKCmd.HAND_APPLY_SUBMIT);
				}
				else
				{
					CommUtil.toastShow("您没有物资申请权限，请联系管理员！", activity);
				}
				break;

			case R.id.dateText:

				if (null != listAdapter)
				{
					dateText.setBackgroundColor(Color.LTGRAY);
					lendNumberText.setBackgroundColor(Color.WHITE);
					inDateText.setBackgroundColor(Color.WHITE);
					deliveryOrderNumber.setBackgroundColor(Color.WHITE);
					numberText.setBackgroundColor(Color.WHITE);
					listAdapter.orderChange(ConfigUtil.ORDER_DATE);
				}
				break;

			case R.id.lendNumberText:

				if (null != listAdapter)
				{
					dateText.setBackgroundColor(Color.WHITE);
					lendNumberText.setBackgroundColor(Color.LTGRAY);
					inDateText.setBackgroundColor(Color.WHITE);
					deliveryOrderNumber.setBackgroundColor(Color.WHITE);
					numberText.setBackgroundColor(Color.WHITE);
					listAdapter.orderChange(ConfigUtil.ORDER_CODE);
				}
				break;
				
			case R.id.inDateText:

				if (null != storelistAdapter)
				{
					dateText.setBackgroundColor(Color.WHITE);
					lendNumberText.setBackgroundColor(Color.WHITE);
					inDateText.setBackgroundColor(Color.LTGRAY);
					deliveryOrderNumber.setBackgroundColor(Color.WHITE);
					numberText.setBackgroundColor(Color.WHITE);
					storelistAdapter.orderChange(ConfigUtil.ORDER_INDATE);
				}
				break;

			case R.id.deliveryOrderNumber:

				if (null != storelistAdapter)
				{
					dateText.setBackgroundColor(Color.WHITE);
					lendNumberText.setBackgroundColor(Color.WHITE);
					inDateText.setBackgroundColor(Color.WHITE);
					deliveryOrderNumber.setBackgroundColor(Color.LTGRAY);
					numberText.setBackgroundColor(Color.WHITE);
					storelistAdapter.orderChange(ConfigUtil.ORDER_LEND);
				}
				break;
				
			case R.id.numberText:

				if (null != storelistAdapter)
				{
					dateText.setBackgroundColor(Color.WHITE);
					lendNumberText.setBackgroundColor(Color.WHITE);
					inDateText.setBackgroundColor(Color.WHITE);
					deliveryOrderNumber.setBackgroundColor(Color.WHITE);
					numberText.setBackgroundColor(Color.LTGRAY);
					storelistAdapter.orderChange(ConfigUtil.ORDER_CODE);
				}
				break;

			default:
				break;
			}
		}
	};

	/**根据状态查对应的借领数据或者物资列表**/
	private void submitQuery(int type)
	{
		int id = queryRadioGroup.getCheckedRadioButtonId();

		String begin = beginTime.getText().toString();
		String end = endTime.getText().toString();

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

		// 查询借出记录
		if (R.id.radioLend == id)
		{
			if (0 == type)
			{
				LEND_PAGE = 1;// 按钮查询，则恢复
			}
			getLendData(begin, end, type);
		}
		// 库存
		if (R.id.radioRecord == id)
		{
			if (0 == type)
			{
				STORAGE_PAGE = 1;// 按钮查询，则恢复
			}
			getStorageData(begin, end, type);
		}
	}

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

	/** 获得借领的分页数据 **/
	private void getLendData(String begin, String end, int loadType)
	{

		// String number =queryEditNumber.getText().toString();
		// String goodsName=queryEditGoods.getText().toString();
		String userNum = queryEditUserNum.getText().toString();// 工号
		String fullName = queryEditName.getText().toString();
		String select = lend_select_edit.getText().toString();
		String applyNumber = queryApplyNumber.getText().toString();// 出库号

		StringBuffer sb = new StringBuffer();
		sb.append("t=" + ZKCmd.REQ_GET_LEND);
	
		sb.append("&appliedOrderNumber=").append(applyNumber);
		String type = CommUtil.getShare(activity).getString(ConfigUtil.USER_TYPE, "");

		if (ConfigUtil.USER_MANAGER.equals(type))
		{
			sb.append("&appliedJobNumber=").append(userNum);
		} else
		{
			sb.append("&appliedJobNumber=").append(CommUtil.getShare(activity).getString(ConfigUtil.USER_JOB_NAME, ""));// 非管理员情况只查自己的
		}
		sb.append("&appliedPersonName=").append(fullName);
		sb.append("&endAppliedDate=").append(end);
		sb.append("&page=").append(LEND_PAGE + "");
		sb.append("&pageSize=").append(ConstantUtil.PAGE_SIZE + "");
		sb.append("&beginAppliedDate=").append(begin);
		sb.append("&appliedStatus=" + ConfigUtil.getCodeByText(activity, select,ConfigUtil.APPROVE_PROCESS));

		LogUtil.i(TAG, "借领数据查询url："+sb.toString());
		Message msg = Message.obtain();
		msg.obj = sb.toString();
		msg.arg1 = ZKCmd.ARG_REQUEST_LEND;
		msg.arg2 = loadType;
		msg.what = ZKCmd.HAND_REQUEST_DATA;
		handler.sendMessage(msg);
	}

	/**获得物资分页数据**/
	private void getStorageData(String begin, String end, int loadType)
	{
		String number = queryEditNumber.getText().toString();
		String materialName = queryEditMaterial.getText().toString();
		String select = store_select_edit.getText().toString();
		String deliveryOrderNumber = deliveryOrderEditNumber.getText().toString();

		// t=6&materialId=&materialName=&materialStatus=&deliveryOrderNumber=&page=1&pageSize=30
		StringBuffer sb = new StringBuffer();
		sb.append("t=" + ZKCmd.REQ_GET_STORAGE);
		sb.append("&materialId=" + number);
		sb.append("&materialName=" + materialName);
		sb.append("&beginInDate=").append(begin);
		sb.append("&endInDate=").append(end);
		sb.append("&page=").append(STORAGE_PAGE + "");
		sb.append("&pageSize=").append(ConstantUtil.PAGE_SIZE + "");
		sb.append("&materialStatus=" + ConfigUtil.getCodeByText(activity, select,ConfigUtil.MATERIAL_STATUS));
		sb.append("&deliveryOrderNumber=" + deliveryOrderNumber);

		Message msg = Message.obtain();
		msg.obj = sb.toString();
		msg.arg1 = ZKCmd.ARG_REQUEST_STOAGE;
		msg.arg2 = loadType;
		msg.what = ZKCmd.HAND_REQUEST_DATA;
		handler.sendMessage(msg);
	}

	/** 显示借领物资记录信息 **/
	public void showLendInfo(List<EpcInfo> epcList)
	{
		listAdapter = null;
		listAdapter = new LendListAdapter(activity, handler, epcList);
		zk_epc_list.setAdapter(listAdapter);
		setLinearVisable(epc_list_title_linear, epc_list_linear, lend_select_lin);
		epc_list_linear.setVisibility(View.VISIBLE);
		epc_list_title_linear.setVisibility(View.VISIBLE);
		// CommUtil.setListViewHeight(zk_epc_list);

		String totalCount = "0";
		if (null != epcList && epcList.size() > 0)
		{
			totalCount = epcList.get(0).getTotalCount();
		}
		showCountText(listAdapter.getCount(), totalCount);
		listAdapter.seacherTextChange(seacherEdit, seacher_delete);
	}

	/**显示借领记录信息，底部加载方式**/
	public void showLendInfoByLoad(List<EpcInfo> list, int type)
	{
		if (null != list && list.size() > 0)
		{
			if (ZKCmd.HAND_REFRESH_DATA == type)
			{
				showLendInfo(list);// 下拉刷新时重新加载
				seacherEdit.setText("");// 利用这个来触发更新列表
			} else
			{
				listAdapter.addItem(list);
				// listAdapter.notifyDataSetChanged();
				seacherEdit.setText("");// 利用这个来触发更新列表
			    SystemClock.sleep(1000);
			}

		} else
		{
			CommUtil.toastShow(activity.getString(R.string.query_no_more_data_msg), activity);
		}

		if (ZKCmd.HAND_REFRESH_DATA == type)
		{
			zk_epc_list.onRefreshComplete(); // 下拉刷新完成
		}
		if (ZKCmd.HAND_LOAD_DATA == type)
		{
			zk_epc_list.onLoadComplete(); // 加载完成
		}
		
		if(null!=list && list.size()>0)
		{
			showCountText(listAdapter.getCount(),  list.get(0).getTotalCount());
		}
	}

	private void setLinearVisable(LinearLayout item, LinearLayout listV, LinearLayout selectLin)
	{
		epc_list_linear.setVisibility(View.GONE);
		epc_list_title_linear.setVisibility(View.GONE);
		store_list_linear.setVisibility(View.GONE);
		store_list_title_linear.setVisibility(View.GONE);
		lend_select_lin.setVisibility(View.GONE);
		store_select_lin.setVisibility(View.GONE);

		item.setVisibility(View.VISIBLE);
		listV.setVisibility(View.VISIBLE);
		selectLin.setVisibility(View.VISIBLE);
	}

	/** 下拉刷新方式获取物资存储信息 **/
	public void showStoreInfo(List<EpcInfo> epcList)
	{
		storelistAdapter = null;
		storelistAdapter = new StorageListAdapter(activity, handler, epcList);
		zk_storage_list.setAdapter(storelistAdapter);
		setLinearVisable(store_list_title_linear, store_list_linear, store_select_lin);
		// CommUtil.setListViewHeight(zk_storage_list);
		String totalCount = "0";
		if (null != epcList && epcList.size() > 0)
		{
			totalCount = epcList.get(0).getTotalCount();
		}
		showCountText(storelistAdapter.getCount(), totalCount);
		storelistAdapter.seacherTextChange(seacherEdit, seacher_delete);
	}

	/**底部加载物资分页数据**/
	public void showStoreInfoByLoad(List<EpcInfo> list, int type)
	{
		if (null != list && list.size() > 0)
		{
			if (ZKCmd.HAND_REFRESH_DATA == type)
			{
				showStoreInfo(list);// 下拉刷新时重新加载
				seacherEdit.setText("");// 利用这个来触发更新列表
			} else
			{
				storelistAdapter.addItem(list);
				// storelistAdapter.notifyDataSetChanged();
				seacherEdit.setText("");// 利用这个来触发更新列表
				SystemClock.sleep(300);				
			}

		} else
		{
			CommUtil.toastShow(activity.getString(R.string.query_no_more_data_msg), activity);
		}

		if (ZKCmd.HAND_REFRESH_DATA == type)
		{
			zk_storage_list.onRefreshComplete(); // 下拉刷新完成
		}
		if (ZKCmd.HAND_LOAD_DATA == type)
		{
			zk_storage_list.onLoadComplete(); // 加载完成
		}
		
		if(null!=list && list.size()>0)
		{
			
			showCountText(storelistAdapter.getCount(), list.get(0).getTotalCount());
		}
	}

	private String totalCountTemp;//保存当前记录总数
	private void showCountText(int count, String totalCount)
	{
		totalCountTemp=totalCount;
		int tCount = 0;
		try
		{
			tCount = Integer.parseInt(totalCount);
		} catch (Exception e)
		{
			tCount = 0;
		}
		if (0 == count)
		{
			// showCounText.setVisibility(View.GONE);
			CommUtil.toastShow(activity.getString(R.string.query_no_data_msg), activity);
		}

		list_total_title_linear.setVisibility(View.VISIBLE);
		showCounText.setVisibility(View.VISIBLE);
		showCounText.setText("记录总数共" + tCount + "条,当前" + count + "条");
	}

	public void showCountText(int type)
	{
		int id = queryRadioGroup.getCheckedRadioButtonId();
		int count = 0;
		// 查询借出记录
		if (R.id.radioLend == id)
		{
			count = listAdapter.getCount();
		}
		// 库存
		if (R.id.radioRecord == id)
		{
			count = storelistAdapter.getCount();
		}
		if(ZKCmd.HAND_NOTIFY_DATA_RETURN==type)
		{
			showCounText.setText("记录总数共" + totalCountTemp + "条,当前" + count + "条");
		}
		if(ZKCmd.HAND_NOTIFY_DATA_CHANGE==type)
		{
			showCounText.setText("当前" + count + "条记录");
		}		
	}

	private LinearLayout getLin(int id)
	{
		return (LinearLayout) mainView.findViewById(id);
	}

	private EditText getEditText(int id)
	{
		return (EditText) mainView.findViewById(id);
	}

	/**释放主界面的单例对象**/
	public static void clearManagerWork()
	{
		if (null != timeDialog && timeDialog.isShowing())
		{
			timeDialog.dismiss();
			timeDialog = null;
		}
		managerWork = null;
	}

	/**触发查询按钮事件**/
	public void reQuery()
	{
		queryBtn.performClick();
	}

	/** 获取我的借领记录 **/
	public void setGroupRadio(int type)
	{
		if (0 == type)
		{
			radioLend.setChecked(true);
			radioRecord.setChecked(false);
		}
		if (1 == type)
		{
			radioLend.setChecked(false);
			radioRecord.setChecked(true);
		}
	}

	/** 查询我的记录 **/
	public String getMyRecordDataParam()
	{
		setGroupRadio(0);// 将选项设为借出记录
		applyBtn.setVisibility(View.GONE);
		String select = lend_select_edit.getText().toString();

		StringBuffer sb = new StringBuffer();
		sb.append("t=" + ZKCmd.REQ_GET_LEND);
		String job = CommUtil.getShare(activity).getString(ConfigUtil.USER_JOB_NAME, "");
		sb.append("&appliedJobNumber=").append(job);
		sb.append("&appliedStatus=" + ConfigUtil.getCodeByText(activity, select,ConfigUtil.APPROVE_PROCESS));
		return sb.toString();
	}

	/** 查询与我相关的事项 **/
	public String getMyInfoDataParam()
	{
		setGroupRadio(0);
		applyBtn.setVisibility(View.GONE);
		boolean auditPower = CommUtil.getShare(activity).getBoolean(ConfigUtil.USER_POWER_AUDIT, false);
		if (auditPower)
		{
			String se = ConfigUtil.getProcessStatus(activity, ConfigUtil.WAIT_ADUIT);
			lend_select_edit.setText(se);// 待审核
		}

		String select = lend_select_edit.getText().toString();

		StringBuffer sb = new StringBuffer();
		sb.append("t=" + ZKCmd.REQ_GET_LEND);
		sb.append("&appliedStatus=" + ConfigUtil.getCodeByText(activity, select,ConfigUtil.APPROVE_PROCESS));
		return sb.toString();
	}

}

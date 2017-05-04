package com.zk.rfid.main.work;

import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zk.rfid.R;
import com.zk.rfid.comm.date.DatePickerDialogUtil;
import com.zk.rfid.comm.date.DateTimeCallBackListener;
import com.zk.rfid.comm.listvew.CustomListView;
import com.zk.rfid.comm.listvew.CustomListView.OnLoadListener;
import com.zk.rfid.comm.listvew.CustomListView.OnRefreshListener;
import com.zk.rfid.main.adapter.AddMaterialRecordAdapter;
import com.zk.rfid.main.adapter.StorageApproveAdapter;
import com.zk.rfid.main.adapter.StorageExitAdapter;
import com.zk.rfid.main.adapter.StorageTotalAdapter;
import com.zk.rfid.main.bean.EpcInfo;
import com.zk.rfid.main.util.ConfigUtil;
import com.zk.rfid.util.CommUtil;
import com.zk.rfid.util.ConstantUtil;
import com.zk.rfid.util.ZKCmd;

/**
 * @Description: 库存列表操作
 * @date:2016-8-22上午10:45:49
 * @author: ldl
 */
public class StoreTotalWork
{
	private static final String TAG = "StoreTotalWork";
	private static StoreTotalWork storeListWork;
	private Handler handler;
	private Activity activity;
	private String dataType;
	private TextView titleTextView, showCounText;
	private EditText beginTime,endTime,queryEditNumber,queryEditMaterial,queryApprovalOrderNumber,queryPurchaseOrderNumber;
	private ImageView zk_endtime_delete,zk_begintime_delete,zk_gnum_delete,zk_gname_delete,zk_approve_delete,zk_purchase_delete;
	
	//到货
	private EditText arrive_beginTime,arrive_endTime,in_beginTime,in_endTime;
	private ImageView arrive_zk_endtime_delete,arrive_zk_begintime_delete,in_zk_endtime_delete,in_zk_begintime_delete;
	
	private Button queryBtn;
	
	private LinearLayout query_in_lin,query_out_lin,query_material_lin,query_add_material_lin,add_material_record_lin;
	private LinearLayout store_total_lin, store_exit_lin, store_approve_lin, store_total_list_lin, store_exit_list_lin,store_approve_list_lin,add_record_list_lin;
	private CustomListView store_total_list, store_exit_list, store_approve_list,add_record_list;

	private StorageApproveAdapter approveAdapter;
	private StorageTotalAdapter totalAdapter;
	private StorageExitAdapter exitAdapter;
	private AddMaterialRecordAdapter addRecordAdapter;
	private int TOTAL_PAGE=1,EXIT_PAGE=1,APPROVE_PAGE=1,ADD_PAGE=1;

	private StoreTotalWork(Activity activity)
	{
		this.activity = activity;
	}

	public static StoreTotalWork getInstance(Activity activity)
	{
		if (null == storeListWork)
		{
			storeListWork = new StoreTotalWork(activity);
		}
		return storeListWork;
	}

	public void initView(Handler handler, String type)
	{
		this.handler = handler;
		this.dataType = type;
		initView();
		setTitle();	
		setListen();
	}

	private void initView()
	{
		beginTime=(EditText) activity.findViewById(R.id.beginTime);
		endTime  =(EditText) activity.findViewById(R.id.endTime);
		queryEditNumber=(EditText) activity.findViewById(R.id.queryEditNumber);
		queryEditMaterial=(EditText) activity.findViewById(R.id.queryEditMaterial);
		queryApprovalOrderNumber=(EditText) activity.findViewById(R.id.queryApprovalOrderNumber);
		queryPurchaseOrderNumber=(EditText) activity.findViewById(R.id.queryPurchaseOrderNumber);
		//到货
		arrive_beginTime=(EditText) activity.findViewById(R.id.arrive_beginTime);
		arrive_endTime  =(EditText) activity.findViewById(R.id.arrive_endTime);
		in_beginTime    =(EditText) activity.findViewById(R.id.in_beginTime);
		in_endTime      =(EditText) activity.findViewById(R.id.in_endTime);
		
		arrive_zk_endtime_delete  =(ImageView) activity.findViewById(R.id.arrive_zk_endtime_delete);
		arrive_zk_begintime_delete=(ImageView) activity.findViewById(R.id.arrive_zk_begintime_delete);
		in_zk_endtime_delete  	  =(ImageView) activity.findViewById(R.id.in_zk_endtime_delete);
		in_zk_begintime_delete    =(ImageView) activity.findViewById(R.id.in_zk_begintime_delete);
		zk_approve_delete		  =(ImageView) activity.findViewById(R.id.zk_approve_delete);
		zk_purchase_delete		  =(ImageView) activity.findViewById(R.id.zk_purchase_delete);

		
		zk_begintime_delete=(ImageView) activity.findViewById(R.id.zk_begintime_delete);
		zk_endtime_delete  =(ImageView) activity.findViewById(R.id.zk_endtime_delete);
		zk_gnum_delete     =(ImageView) activity.findViewById(R.id.zk_gnum_delete);
		zk_gname_delete    =(ImageView) activity.findViewById(R.id.zk_gname_delete);
		queryBtn=(Button) activity.findViewById(R.id.queryBtn);
		
		query_in_lin = (LinearLayout) activity.findViewById(R.id.query_in_lin);
		query_out_lin = (LinearLayout) activity.findViewById(R.id.query_out_lin);
		query_material_lin= (LinearLayout) activity.findViewById(R.id.query_material_lin);
		query_add_material_lin= (LinearLayout) activity.findViewById(R.id.query_add_material_lin);
		
		store_total_lin   = (LinearLayout) activity.findViewById(R.id.store_total_lin);
		store_exit_lin    = (LinearLayout) activity.findViewById(R.id.store_exit_lin);
		store_approve_lin = (LinearLayout) activity.findViewById(R.id.store_approve_lin);
		add_material_record_lin= (LinearLayout) activity.findViewById(R.id.add_material_record_lin);

		store_total_list_lin = (LinearLayout) activity.findViewById(R.id.store_total_list_lin);
		store_exit_list_lin = (LinearLayout) activity.findViewById(R.id.store_exit_list_lin);
		store_approve_list_lin = (LinearLayout) activity.findViewById(R.id.store_approve_list_lin);
		add_record_list_lin= (LinearLayout) activity.findViewById(R.id.add_record_list_lin);
		

		store_total_list   = (CustomListView) activity.findViewById(R.id.store_total_list);
		store_exit_list    = (CustomListView) activity.findViewById(R.id.store_exit_list);
		store_approve_list = (CustomListView) activity.findViewById(R.id.store_approve_list);
		add_record_list	   = (CustomListView) activity.findViewById(R.id.add_record_list);
	}
	
	private void setListen()
	{
		queryBtn.setOnClickListener(new View.OnClickListener()
		{			
			@Override
			public void onClick(View v)
			{
				submitQuery(0);				
			}
		});
		store_approve_list.setonRefreshListener(new OnRefreshListener()
		{
			@Override
			public void onRefresh()
			{
				APPROVE_PAGE = 1;
				submitQuery(ZKCmd.HAND_REFRESH_DATA);
			}
		});
		store_approve_list.setonLoadListener(new OnLoadListener()
		{
			@Override
			public void onLoad()
			{
				APPROVE_PAGE++;
				submitQuery(ZKCmd.HAND_LOAD_DATA);
			}
		});
		store_exit_list.setonRefreshListener(new OnRefreshListener()
		{
			@Override
			public void onRefresh()
			{
				EXIT_PAGE = 1;
				submitQuery(ZKCmd.HAND_REFRESH_DATA);
			}
		});
		store_exit_list.setonLoadListener(new OnLoadListener()
		{
			@Override
			public void onLoad()
			{
				// 加载更多
				EXIT_PAGE++;
				submitQuery(ZKCmd.HAND_LOAD_DATA);
			}
		});
		
		store_total_list.setonRefreshListener(new OnRefreshListener()
		{
			@Override
			public void onRefresh()
			{
				TOTAL_PAGE = 1;
				submitQuery(ZKCmd.HAND_REFRESH_DATA);
			}
		});
		store_total_list.setonLoadListener(new OnLoadListener()
		{
			@Override
			public void onLoad()
			{
				TOTAL_PAGE++;
				submitQuery(ZKCmd.HAND_LOAD_DATA);
			}
		});
		
		
		add_record_list.setonRefreshListener(new OnRefreshListener()
		{
			@Override
			public void onRefresh()
			{
				ADD_PAGE = 1;
				submitQuery(ZKCmd.HAND_REFRESH_DATA);
			}
		});
		add_record_list.setonLoadListener(new OnLoadListener()
		{
			@Override
			public void onLoad()
			{
				ADD_PAGE++;
				submitQuery(ZKCmd.HAND_LOAD_DATA);
			}
		});
		
		setDateListen(beginTime);
		setDateListen(endTime);
		setDateListen(arrive_beginTime);
		setDateListen(arrive_endTime);
		setDateListen(in_beginTime);
		setDateListen(in_endTime);
				
		CommUtil.editTextChange(queryApprovalOrderNumber, zk_approve_delete);
		CommUtil.editTextChange(queryPurchaseOrderNumber, zk_purchase_delete);
		
		CommUtil.editTextChange(beginTime, zk_begintime_delete);
		CommUtil.editTextChange(endTime, zk_endtime_delete);
		CommUtil.editTextChange(queryEditMaterial, zk_gname_delete);
		CommUtil.editTextChange(queryEditNumber, zk_gnum_delete);
		
		//到货
		CommUtil.editTextChange(arrive_beginTime, arrive_zk_begintime_delete);
		CommUtil.editTextChange(arrive_endTime, arrive_zk_endtime_delete);
		CommUtil.editTextChange(in_beginTime, in_zk_begintime_delete);
		CommUtil.editTextChange(in_endTime, in_zk_endtime_delete);
	}
	
	private void setDateListen(final EditText editText)
	{
		editText.setOnTouchListener(new View.OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				getTime(editText);
				return false;
			}
		});
	}
	
	boolean flag = false;
	private static Dialog timeDialog;
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

	/**根据不同的情况显示不同的标题**/
	private void setTitle()
	{
		titleTextView = (TextView) activity.findViewById(R.id.title);
		showCounText = (TextView) activity.findViewById(R.id.showCounText);

		if (ConfigUtil.LEFT_EXIT_REPORTS.equals(dataType))
		{
			titleTextView.setText(R.string.store_exit_table_title);
			query_out_lin.setVisibility(View.VISIBLE);
		}
		if (ConfigUtil.LEFT_ARRIVE_REPORTS.equals(dataType))
		{
			titleTextView.setText(R.string.store_approve_table_title);
			query_in_lin.setVisibility(View.VISIBLE);
		}
		if (ConfigUtil.LEFT_REPORTS.equals(dataType))
		{
			titleTextView.setText(R.string.store_detail_table_title);	
			query_material_lin.setVisibility(View.VISIBLE);
		}
		if (ConfigUtil.LEFT_MATERIAL_ADD_RECORD.equals(dataType))
		{
			titleTextView.setText(R.string.store_add_material_table_title);	
			query_add_material_lin.setVisibility(View.VISIBLE);
		}
	}
	
	/**根据状态控制查询列表**/
	private void submitQuery(int type)
	{
			
		StringBuffer sb = new StringBuffer();
		
		if (ConfigUtil.LEFT_EXIT_REPORTS.equals(dataType))
		{
			String begin = beginTime.getText().toString();
			String end   = endTime.getText().toString();

			boolean flag=checkDate(begin, end);
			if(!flag)
			{
				return;
			}
			
			if(type==0)
			{
				EXIT_PAGE=1;
			}			
			sb.append("t=" + ZKCmd.REQ_STORE_EXIT_DATA);
			sb.append("&page=").append(EXIT_PAGE + "");
			
			sb.append("&beginInDate=").append(begin);
			sb.append("&endInDate=").append(end);
		}
		if (ConfigUtil.LEFT_ARRIVE_REPORTS.equals(dataType))
		{
			String arriveBegin = arrive_beginTime.getText().toString();
			String arriveEnd   =  arrive_endTime.getText().toString();

			boolean flag=checkDate(arriveBegin, arriveEnd);
			if(!flag)
			{
				return;
			}
			String inBegin =  in_beginTime.getText().toString();
			String inEnd   =  in_endTime.getText().toString();
			
			flag=checkDate(inBegin, inEnd);
			if(!flag)
			{
				return;
			}
			
			if(type==0)
			{
				APPROVE_PAGE=1;
			}		
			sb.append("t=" + ZKCmd.REQ_STORE_APPROVE_DATA);
			sb.append("&page=").append(APPROVE_PAGE + "");

			sb.append("&beginArrivedDateDate=").append(arriveBegin);
			sb.append("&endArrivedDateDate=").append(arriveEnd);
			
			sb.append("&beginInDate=").append(inBegin);
			sb.append("&endInDate=").append(inEnd);
		}
		if (ConfigUtil.LEFT_REPORTS.equals(dataType))
		{
			if(type==0)
			{
				TOTAL_PAGE=1;
			}
			sb.append("t=" + ZKCmd.REQ_STORE_TOTAL_DATA);
			sb.append("&page=").append(TOTAL_PAGE + "");
			
			String number = queryEditNumber.getText().toString();
			String materialName = queryEditMaterial.getText().toString();			
			sb.append("&materialId=" + number);
			sb.append("&materialName=" + materialName);
		}
		
		if (ConfigUtil.LEFT_MATERIAL_ADD_RECORD.equals(dataType))
		{
			if(type==0)
			{
				ADD_PAGE=1;
			}
			sb.append("t=" + ZKCmd.REQ_GET_STORAGE);
			sb.append("&page=").append(ADD_PAGE + "");
			
			String approveNumber  = queryApprovalOrderNumber.getText().toString();
			String purchaseNumber = queryPurchaseOrderNumber.getText().toString();			
			sb.append("&approvalOrderNumber=" + approveNumber);
			sb.append("&purchaseOrderNumber=" + purchaseNumber);
		}
			
		sb.append("&pageSize=").append(ConstantUtil.PAGE_SIZE + "");
		
		Message msg = Message.obtain();
		msg.obj  = sb.toString();
		msg.arg1 = ZKCmd.ARG_REQUEST_STOAGE;
		msg.arg2 = type;
		msg.what = ZKCmd.HAND_REQUEST_DATA;
		handler.sendMessage(msg);
	}
	
	/**检查日期的开始和结束输入空或者不全情况**/
	private boolean checkDate(String begin,String end)
	{
		if (!"".equals(begin) && "".equals(end))
		{
			// 结束时间不能为空
			CommUtil.toastShow(activity.getString(R.string.date_time_notify_en_empty), activity);
			return false;
		}
		if ("".equals(begin) && !"".equals(end))
		{
			// 开始时间不能为空
			CommUtil.toastShow(activity.getString(R.string.date_time_notify_be_empty), activity);
			return false;
		}

		if (!"".equals(begin) && !"".equals(end))
		{
			// 开始时间不能大过结束时间
			int beTmp = Integer.parseInt(begin.replaceAll("-", ""));
			int enTmp = Integer.parseInt(end.replaceAll("-", ""));
			if (beTmp > enTmp)
			{
				CommUtil.toastShow(activity.getString(R.string.date_time_notify_not_allow), activity);
				return false;
			}
		}
		return true;
	}
	
	
	/**触发点击查询**/
	public void autoQuery()
	{
		queryBtn.performClick();
	}

	public void showStoreData(List<EpcInfo> list)
	{
		setViewVisable();
		int count = 0;
		if (ConfigUtil.LEFT_EXIT_REPORTS.equals(dataType))
		{
			exitAdapter = null;
			exitAdapter = new StorageExitAdapter(activity, list);
			store_exit_list.setAdapter(exitAdapter);
			count = exitAdapter.getCount();
		}
		if (ConfigUtil.LEFT_ARRIVE_REPORTS.equals(dataType))
		{
			approveAdapter = null;
			approveAdapter = new StorageApproveAdapter(activity, list);
			store_approve_list.setAdapter(approveAdapter);
			count = approveAdapter.getCount();
		}
		if (ConfigUtil.LEFT_REPORTS.equals(dataType))
		{
			totalAdapter = null;
			totalAdapter = new StorageTotalAdapter(activity, list);
			store_total_list.setAdapter(totalAdapter);
			count = totalAdapter.getCount();
		}
		
		if(ConfigUtil.LEFT_MATERIAL_ADD_RECORD.equals(dataType))
		{
			addRecordAdapter=null;
			addRecordAdapter=new AddMaterialRecordAdapter(activity, list);
			add_record_list.setAdapter(addRecordAdapter);
			count = addRecordAdapter.getCount();
		}

		if (null != list && list.size() > 0)
		{
			String tCount = list.get(0).getTotalCount();
			showCountText(tCount, count);
		}else
		{
			showCountText("0",count);
		}
	}

	public void setViewVisable()
	{
		store_approve_lin.setVisibility(View.GONE);
		store_exit_lin.setVisibility(View.GONE);
		store_total_lin.setVisibility(View.GONE);
		add_material_record_lin.setVisibility(View.GONE);
		
		store_total_list_lin.setVisibility(View.GONE);
		store_exit_list_lin.setVisibility(View.GONE);
		store_approve_list_lin.setVisibility(View.GONE);
		add_record_list_lin.setVisibility(View.GONE);

		if (ConfigUtil.LEFT_EXIT_REPORTS.equals(dataType))
		{
			store_exit_lin.setVisibility(View.VISIBLE);
			store_exit_list_lin.setVisibility(View.VISIBLE);
		}
		if (ConfigUtil.LEFT_ARRIVE_REPORTS.equals(dataType))
		{
			store_approve_lin.setVisibility(View.VISIBLE);
			store_approve_list_lin.setVisibility(View.VISIBLE);
		}

		if (ConfigUtil.LEFT_REPORTS.equals(dataType))
		{
			store_total_lin.setVisibility(View.VISIBLE);
			store_total_list_lin.setVisibility(View.VISIBLE);
		}
		
		if (ConfigUtil.LEFT_MATERIAL_ADD_RECORD.equals(dataType))
		{
			add_material_record_lin.setVisibility(View.VISIBLE);
			add_record_list_lin.setVisibility(View.VISIBLE);
		}
	}

	/**分页加载数据**/
	public void showInfoByLoad(List<EpcInfo> list, int type)
	{
		if (null != list && list.size() > 0)
		{
			if (ZKCmd.HAND_REFRESH_DATA == type)
			{
				showStoreData(list);// 下拉刷新时重新加载
			} else
			{
				onDataComplete(list, false, type);
			}
		} else
		{
			CommUtil.toastShow(activity.getString(R.string.query_no_more_data_msg), activity);
		}
		
		onDataComplete(list, true, type);
	}

	/**获得数据，是否完成下拉或者加载,上拉或者加载的类型**/
	private void onDataComplete(List<EpcInfo> list,boolean finishFlag,int completeType)
	{
		if(!finishFlag)
		{
			String totalCount=list.get(0).getTotalCount();
			int adapterCount=0;
			if (ConfigUtil.LEFT_EXIT_REPORTS.equals(dataType))
			{
				exitAdapter.addItem(list);
				adapterCount=exitAdapter.getCount();
				exitAdapter.notifyDataSetChanged();
			}
			if (ConfigUtil.LEFT_ARRIVE_REPORTS.equals(dataType))
			{
				approveAdapter.addItem(list);
				adapterCount=approveAdapter.getCount();
				approveAdapter.notifyDataSetChanged();
			}
			if (ConfigUtil.LEFT_REPORTS.equals(dataType))
			{
				totalAdapter.addItem(list);
				adapterCount=totalAdapter.getCount();
				totalAdapter.notifyDataSetChanged();
			}
			
			if (ConfigUtil.LEFT_MATERIAL_ADD_RECORD.equals(dataType))
			{
				addRecordAdapter.addItem(list);
				adapterCount=addRecordAdapter.getCount();
				addRecordAdapter.notifyDataSetChanged();
			}
			
			SystemClock.sleep(300);			
			showCountText(totalCount, adapterCount);
		}else
		{
			if (ConfigUtil.LEFT_EXIT_REPORTS.equals(dataType))
			{
				updateView(store_exit_list, completeType);
			}
			if (ConfigUtil.LEFT_ARRIVE_REPORTS.equals(dataType))
			{
				updateView(store_approve_list, completeType);
			}
			if (ConfigUtil.LEFT_REPORTS.equals(dataType))
			{
				updateView(store_total_list, completeType);
			}
			if (ConfigUtil.LEFT_MATERIAL_ADD_RECORD.equals(dataType))
			{
				updateView(add_record_list, completeType);
			}
		}				
	}
	
	/**底部加载或者下拉刷新的完成**/
	private void updateView(CustomListView listView,int type)
	{

		if (ZKCmd.HAND_REFRESH_DATA == type)
		{
			listView.onRefreshComplete(); // 下拉刷新完成
		}
		if (ZKCmd.HAND_LOAD_DATA == type)
		{
			listView.onLoadComplete(); // 加载完成
		}
	}

	/**显示当前数据总数和当前已经加载的记录数**/
	private void showCountText(String totalCount, int count)
	{
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

		showCounText.setText("记录总数共" + tCount + "条,当前" + count + "条");
	}

	public static void clearWork()
	{
		storeListWork = null;
	}

}

package com.zk.rfid.main.activity;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rfid.sdk.public_utils.HostPacketType;
import com.rfid.sdk.public_utils.MAC_Command;
import com.rfid.sdk.public_utils.Message_Type;
import com.rfid.sdk.rfidclass.TAG_TID;
import com.zk.rfid.R;
import com.zk.rfid.comm.base.BaseActivity;
import com.zk.rfid.comm.base.BootBroadcastReceiver;
import com.zk.rfid.comm.base.CommTopView;
import com.zk.rfid.main.bean.EpcInfo;
import com.zk.rfid.main.dao.ManagerDao;
import com.zk.rfid.main.work.OperateEpcWork;
import com.zk.rfid.main.work.PowerSetWork;
import com.zk.rfid.util.CommUtil;
import com.zk.rfid.util.GetDataThread;
import com.zk.rfid.util.LogUtil;
import com.zk.rfid.util.ZKCmd;

/**
 * @Description: 物资盘点及操作
 * @date:2016年7月7日 上午9:57:21
 * @author: ldl
 */
public class OperateEpcActivity extends BaseActivity
{
	private static final String TAG = "OperateEpcActivity";
	private Activity activity = OperateEpcActivity.this;
	private OperateEpcWork work;
	private ManagerDao managerDao;
	private static Map<String, String> scanEpcMap = new HashMap<String, String>();
	private Dialog scanDialog;
	private String modifyParamTemp;

	private int Cur_Command = 0;
	private boolean start_inv;
	private List<EpcInfo> scanEpcList = null;
	private TextView numberText,nameText,posText;
	
	public class MyBroadcastReceiver extends BroadcastReceiver 
	{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO 自动生成的方法存根
			start_inv = intent.getBooleanExtra("start_inventory", true);
			if(!start_inv)
			{
				LogUtil.d(TAG, "start_inv="+start_inv);
				work.stopScan();
				inv_state = END;
				getScanData();
			}
		}
		
	}
	
	private MyBroadcastReceiver myBroadcastReceiver;

	private Handler handler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{

			case ZKCmd.HAND_REQUEST_DATA:

				break;

			case ZKCmd.HAND_START_SCAN:

				startScan();
				break;

			case ZKCmd.HAND_RESPONSE_DATA:

				String res = msg.obj + "";
				int arg = msg.arg1;
				managerDao = new ManagerDao(activity, handler);

				if (ZKCmd.ARG_GET_SCAN_EPC == arg)
				{
					scanEpcList = managerDao.getScanList(res);
					work.showScanData(scanEpcList);
					dialogDismiss();
				}

				if (ZKCmd.ARG_GET_SCAN_CONTINUE == arg)
				{
					LogUtil.e(TAG, "这时的arg2是多少："+msg.arg2);	
					scanEpcList.addAll(managerDao.getScanList(res));
					if (ZKCmd.ARG_GET_SCAN_LAST_ONE == msg.arg2)
					{
						work.showScanDataByLoad(managerDao.getScanList(res), true);// 追加
					} else
					{
						work.showScanDataByLoad(managerDao.getScanList(res), false);// 追加
					}
				}

				if (ZKCmd.ARG_MODIFY_EPC == arg)
				{
					LogUtil.i(TAG, "获取新epc结果：" + res);
					getNewEpcCode(res);
				}
				if (ZKCmd.ARG_MODIFY_EPC_SERVER_RES == arg)
				{
					// 通知服务器修改epc成功,返回结果
					LogUtil.i(TAG, "通知服务器修改epc成功,返回结果：" + res);
				}
				managerDao = null;
				break;

			case ZKCmd.HAND_MODIFY_EPC:

				modifyEpc(msg.obj + "");
				break;

			case HostPacketType.RFID_PACKET_TYPE_COMMAND_BEGIN:
			{
				LogUtil.i(TAG, "收到开始命令");
				int cmd_type = msg.getData().getInt(Message_Type.cmd_type);
				Cur_Command = cmd_type;
				if (MAC_Command.CMD_18K6CINV == cmd_type)
				{
					inv_state = BEGIN;
					scanEpcMap.clear();
				}
				break;
			}
			case HostPacketType.RFID_PACKET_TYPE_18K6C_INVENTORY:
			{
					Bundle bd = msg.getData();
					String type = bd.getString(Message_Type.msg_type);
					// LogUtil.i(TAG, "操作返回状态:" + type);
					if (Message_Type.tag_inventory.equals(type))
					{
						// List<TAG_TID> tagTids=(List<TAG_TID>)
						// bd.getSerializable(Message_Type.tag_inventory);
						TAG_TID tag_data = (TAG_TID) msg.getData().getSerializable(Message_Type.tag_inventory);
						saveTempData(tag_data);
					}
					break;
			}
			case HostPacketType.RFID_PACKET_TYPE_18K6C_TAG_ACCESS:
			{
				Bundle bd = msg.getData();
				String type = bd.getString(Message_Type.msg_type);
				LogUtil.i(TAG, "收到TAG_ACCESS命令");
				if (Message_Type.tag_check.equals(type))
				{
					if (MAC_Command.CMD_18K6CWRITE == Cur_Command)
					{
						// 0代表写入成功
						int tag_err = msg.getData().getInt(Message_Type.tag_err);
						LogUtil.i(TAG, "收到TAG_ACCESS命令,tag_err:" + tag_err);
						modifyType = tag_err;// 在一定时间段内保存这个标记
					}
				}
				break;
			}
			// 盘点结束命令
			case HostPacketType.RFID_PACKET_TYPE_COMMAND_END:
			{
				if (Cur_Command == MAC_Command.CMD_18K6CINV)
				{
					LogUtil.d(TAG, "收到正常结束命令");

					String msg_type = msg.getData().getString(Message_Type.msg_type);
					if (Message_Type.mac_err.equals(msg_type))
					{
						String mac_err = msg.getData().getString(Message_Type.mac_err);

						if ("0000".equals(mac_err))
						{
							inv_state = END;
							getScanData();
						} else
						{
							LogUtil.e(TAG, "异常结束情况");
							CommUtil.toastShow("MAC错误：" + mac_err, activity);
						}
					}
					work.stopScan();
				}

				break;
			}
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		Window window = getWindow();  
        WindowManager.LayoutParams params = window.getAttributes();  
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE;
        window.setAttributes(params);
		
		setContentView(R.layout.operateepc);

		initView();
		
		myBroadcastReceiver = new MyBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.rfid.sdk.protocol.ACTION");
        registerReceiver(myBroadcastReceiver, filter);
	}

	private int BEGIN = 0;
	private int END    = 1;
	private int inv_state = END;
	
	@Override
	protected void onStart() {
		// TODO 自动生成的方法存根
		super.onStart();
		Intent intent = getIntent();
		start_inv = intent.getBooleanExtra("start_inventory", false);
		if(start_inv)
		{
			start_inv = false;
			LogUtil.d(TAG, "start_inv="+start_inv);
			work.startInfScan();
		}
	}

	/**开始盘点**/
	private void startScan()
	{
		LogUtil.d(TAG, "开始时的状态：");
		
		scanDialog = CommUtil.createDialog(activity);
		
		new CountDownTimer(8000, 100)
		{
			@Override
			public void onTick(long millisUntilFinished)
			{
			}

			@Override
			public void onFinish()
			{
				LogUtil.d(TAG, "盘点倒计时结束,调用stop");
				dialogDismiss();// 超时
				if(inv_state != END)
				{
					work.stopScan();//如果指定时间到了仍然没有结束就获取结果，并且关闭射频板电源。
					getScanData();	
				}					
			}
		}.start();
	}
	
	/**初始化界面控件**/
	private void initView()
	{
		new CommTopView(activity);
		new PowerSetWork(activity);// 设功率

		work = OperateEpcWork.getInstance(activity);
		work.initView(handler);
		
		//numberText,nameText,posText
		numberText = (TextView) activity.findViewById(R.id.numberText);
		numberText.setOnClickListener(listen);
		nameText = (TextView) activity.findViewById(R.id.nameText);
		nameText.setOnClickListener(listen);
		posText = (TextView) activity.findViewById(R.id.posText);
		posText.setOnClickListener(listen);
	}
	
	private OnClickListener listen = new View.OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			switch (v.getId())
			{
				case R.id.numberText:
					LogUtil.i(TAG, "Click numberText Item:");
					if(null != scanEpcList)
					{
						Collections.sort(scanEpcList, new Comparator<EpcInfo>()
						{
							@Override
							public int compare(EpcInfo p1, EpcInfo p2) {
								// TODO 自动生成的方法存根
								return p1.getMaterialId().compareTo(p2.getMaterialId()); //编号排序
							}
						});
						numberText.setBackgroundColor(Color.LTGRAY);
						nameText.setBackgroundColor(Color.WHITE);
						posText.setBackgroundColor(Color.WHITE);
						work.showScanData(scanEpcList);
					}
					//work.showScanData(managerDao.getScanList(res));
				break;
				case R.id.nameText:
					LogUtil.i(TAG, "Click nameText Item:");
					if(null != scanEpcList)
					{
						Collections.sort(scanEpcList, new Comparator<EpcInfo>()
						{
							@Override
							public int compare(EpcInfo p1, EpcInfo p2) {
								// TODO 自动生成的方法存根
								return p1.getMaterialName().compareTo(p2.getMaterialName()); //编号排序
							}
						});
						nameText.setBackgroundColor(Color.LTGRAY);
						numberText.setBackgroundColor(Color.WHITE);
						posText.setBackgroundColor(Color.WHITE);
						work.showScanData(scanEpcList);
					}
					//work.showScanData(managerDao.getScanList(res));
				break;
				case R.id.posText:
					LogUtil.i(TAG, "Click posText Item:");
					if(null != scanEpcList)
					{
						Collections.sort(scanEpcList, new Comparator<EpcInfo>()
						{
							@Override
							public int compare(EpcInfo p1, EpcInfo p2) 
							{
								// TODO 自动生成的方法存根
								return p1.getLocation().compareTo(p2.getLocation()); //编号排序
							}
						});
						posText.setBackgroundColor(Color.LTGRAY);
						numberText.setBackgroundColor(Color.WHITE);
						nameText.setBackgroundColor(Color.WHITE);
						work.showScanData(scanEpcList);
					}
					//work.showScanData(managerDao.getScanList(res));
				break;
			}
		}
	};

	private void saveTempData(TAG_TID tagTids)
	{
		//LogUtil.d(TAG, "盘到数据:" + tagTids.getEPC());
		scanEpcMap.put(tagTids.getEPC(), tagTids.getEPC());
		//LogUtil.d(TAG, "盘到数据长度:" + scanEpcMap.size());
	}

	/**临时保有存下层接口发来数据，待盘点结束后再提交查询**/
	private void getScanData()
	{
		//getTempDate();
		if (scanEpcMap.size() > 0)
		{
			LogUtil.i(TAG, "盘点得多少条:" + scanEpcMap.size());
			// LogUtil.i(TAG, "盘点的数据:" + scanEpcMap);
			getScanDataByEpc(scanEpcMap, ZKCmd.ARG_GET_SCAN_EPC);

		} else
		{
			dialogDismiss();
			CommUtil.toastShow("盘点结束,未找到任何标签", activity);
			LogUtil.i(TAG, "盘点读不到");
		}
	}

	/**向服务器查询该数据，组装数据**/
	private void getScanDataByEpc(Map<String, String> map, int arg1)
	{
		StringBuffer sbCode = new StringBuffer();
		Iterator it = map.entrySet().iterator();
		int count = 0;
		boolean firstFlag = true;
		int sum=0;
		String epcList = "";
		while (it.hasNext())
		{
			count++;
			Entry entry = (Entry) it.next();
			sbCode.append("'" + entry.getValue() + "'").append(",");
		}
		epcList = sbCode.deleteCharAt(sbCode.length() - 1).toString();// 把最后一个,删掉
		LogUtil.e(TAG, "epcList:" + epcList);
		startGetDataTask(arg1, arg1, epcList);//10条数目少，可以直观看到。不需要排序
		/*
		while (it.hasNext())
		{
			count++;
			Entry entry = (Entry) it.next();
			sbCode.append("'" + entry.getValue() + "'").append(",");
			if (10 == count)
			{
				sum++;
				String epcList = sbCode.deleteCharAt(sbCode.length() - 1).toString();// 把最后一个,删掉
				if (firstFlag)
				{					
					startGetDataTask(arg1, arg1, epcList);//10条数目少，可以直观看到。不需要排序
					firstFlag = false;
				} 
				else
				{
					//SystemClock.sleep(100);
					LogUtil.e(TAG, "还在加数据");
					if(sum==(map.size()/10))
					{
						//当刚好扫描到的数据是10的整数倍，也通知排序
						LogUtil.e(TAG, "刚好整数倍，也通知排序");
						startGetDataTask(ZKCmd.ARG_GET_SCAN_CONTINUE, ZKCmd.ARG_GET_SCAN_LAST_ONE, epcList);
					}
					else
					{					
						startGetDataTask(ZKCmd.ARG_GET_SCAN_CONTINUE, arg1, epcList);
					}					
				}
				sbCode = new StringBuffer();// 恢复，后面追加
				count = 0;
			}
		}
		if (count > 0)
		{		
			LogUtil.e(TAG, "最后一次加数据:" + count);
			String epcList = sbCode.deleteCharAt(sbCode.length() - 1).toString();// 把最后一个,删掉
			if(firstFlag)
			{
				startGetDataTask(ZKCmd.ARG_GET_SCAN_EPC, arg1, epcList);
			}
			else
			{
				SystemClock.sleep(500);
				startGetDataTask(ZKCmd.ARG_GET_SCAN_CONTINUE, ZKCmd.ARG_GET_SCAN_LAST_ONE, epcList);
			}
		
		}
		*/
	}

	/**向服务器查询该数据，发起请求**/
	private void startGetDataTask(int arg1, int arg2, String epcList)
	{
		StringBuffer sb = new StringBuffer();
		sb.append("t=" + ZKCmd.REQ_MATERIAL_BY_EPC);
		sb.append("&epcCodeList=").append(epcList);

		LogUtil.i(TAG, "提交的参数:" + sb.toString());
		final GetDataThread thread = new GetDataThread(activity, handler);
		thread.setArg1(arg1);
		thread.setArg2(arg2);
		thread.setHandlerCode(ZKCmd.HAND_RESPONSE_DATA);
		thread.setParams(sb.toString());
		new CountDownTimer(100,100)
		{		
			@Override
			public void onTick(long millisUntilFinished)
			{				
			}			
			@Override
			public void onFinish()
			{
				thread.start();				
			}
		}.start();
		
	}

	/** 修改epc值请求，先请求服务器获得新的epc,再进行修改，修改成功后再向服务器发消息确认修改成功 **/
	private void modifyEpc(String param)
	{
		modifyParamTemp = param;

		GetDataThread thread = new GetDataThread(activity, handler);
		thread.setHandlerCode(ZKCmd.HAND_RESPONSE_DATA);
		thread.setArg1(ZKCmd.ARG_MODIFY_EPC);
		thread.setParams(param);
		thread.start();
	}

	/** 从服务器获得新的标签 ，解析json，如果成功则执行修改动作**/
	private void getNewEpcCode(String res)
	{
		try
		{
			LogUtil.i(TAG, "获得新的epc数据:" + res);
			JSONObject json = new JSONObject(res).getJSONObject("result");
			String state = json.optString("state", "");
			if ("1".equals(state))
			{
				String oldCode = json.optString("oldcode", "");
				String newCode = json.optString("newcode", "");
				if (!"".equals(oldCode) && oldCode.length() == 32 && !"".equals(newCode) && newCode.length() == 32)
				{
					// modifyTimer();//当开始修改，开倒计时，一定时间内检查
					boolean flag = work.modifyEpc(oldCode, newCode);
					// work.modifyEpc(oldCode, newCode);
					LogUtil.i(TAG, "修改epc结果:" + flag);
					// dialogDismiss();
					if (flag)
					{
						modifyepcRes(0);
					} else
					{
						modifyepcRes(1);
					}
				} else
				{
					dialogDismiss();
					CommUtil.toastShow("返回新标签格式有误", activity);
				}
			} else
			{
				dialogDismiss();
				CommUtil.toastShow("获取新epc码失败", activity);
			}
		} catch (Exception e)
		{
			dialogDismiss();
			CommUtil.toastShow("获取新epc码发生异常,修改失败", activity);
		}
	}

	private boolean sueecssFinish = false;
	private int modifyType = 1000;

	// private void modifyTimer()
	// {
	// sueecssFinish=false;
	// modifyType=1000;//开始计时恢复原始
	// //倒数8秒，每300毫秒检查一次状态
	// new CountDownTimer(8000,300)
	// {
	// @Override
	// public void onTick(long millisUntilFinished)
	// {
	// LogUtil.i(TAG, "当前状态："+modifyType);
	// if(0==modifyType)
	// {
	// modifyepcRes(0);
	// }
	// }
	// @Override
	// public void onFinish()
	// {
	// //如果这个时间段内已经收到成功，就不要再处理了,否则就处理
	// if(!sueecssFinish)
	// {
	// modifyepcRes(modifyType);
	// }
	// }
	// }.start();
	// }

	/** 手持机写入结果 **/
	private void modifyepcRes(int type)
	{
		dialogDismiss();
		// 修改成功
		if (0 == type)
		{
			CommUtil.toastShow("修改成功", activity);
			if (null != modifyParamTemp)
			{
				// sueecssFinish=true;
				// 将原来的23换成28，参数不变，服务端进行修改
				String param = modifyParamTemp.replace("t=" + ZKCmd.REQ_MODIFY_GET_NEWEPC, "t="
						+ ZKCmd.REQ_MODIFY_SUCCESS);
				// 成功后，告诉服务器，让服务器更新数据库
				GetDataThread thread = new GetDataThread(activity, handler);
				thread.setHandlerCode(ZKCmd.HAND_RESPONSE_DATA);
				thread.setArg1(ZKCmd.ARG_MODIFY_EPC_SERVER_RES);
				thread.setParams(param);
				thread.start();
			}
		} else
		{
			CommUtil.toastShow("修改失败", activity);
		}

		work.powerOff();// 直接关电
	}

	@Override
	protected void onDestroy()
	{
		if (null != work)
		{
			work.stopScan();
			work.powerOff();
			// work.disConn();// 退出时执行一次关闭
		}
		unregisterReceiver(myBroadcastReceiver);
		OperateEpcWork.clearOperateEpcWork();
		dialogDismiss();
		super.onDestroy();
	}

	private void dialogDismiss()
	{
		if (null != scanDialog && scanDialog.isShowing())
		{
			scanDialog.dismiss();
			scanDialog = null;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		LogUtil.i(TAG, "收到回调:" + resultCode);
		if (ZKCmd.SEND_RES_CODE_RE_QUERY == resultCode)
		{
			// 返回需要重新触发扫描
			work.reQuery();
		}
	}

}

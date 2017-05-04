package com.zk.rfid.map.activity;

import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import com.rfid.sdk.public_utils.HostPacketType;
import com.rfid.sdk.public_utils.MAC_Command;
import com.rfid.sdk.public_utils.Message_Type;
import com.zk.rfid.R;
import com.zk.rfid.comm.base.BaseActivity;
import com.zk.rfid.comm.base.CommDialog;
import com.zk.rfid.comm.base.CommTopView;
import com.zk.rfid.comm.base.MyApplication;
import com.zk.rfid.comm.scro.HoScrollView;
import com.zk.rfid.comm.scro.SizeCallbackForMenu;
import com.zk.rfid.main.bean.EpcInfo;
import com.zk.rfid.main.util.ConfigUtil;
import com.zk.rfid.main.work.EpcWork;
import com.zk.rfid.main.work.PowerSetWork;
import com.zk.rfid.map.dao.StorageDao;
import com.zk.rfid.map.work.EpcWriteWork;
import com.zk.rfid.map.work.MapWork;
import com.zk.rfid.util.CommUtil;
import com.zk.rfid.util.GetDataThread;
import com.zk.rfid.util.LogUtil;
import com.zk.rfid.util.ZKCmd;

/**
 * @Description: 仓库存写标签列表
 * @date:2016年5月12日 上午9:57:21
 * @author: ldl
 */
public class EpcWriteActivity extends BaseActivity
{
	private static final String TAG = "EpcWriteActivity";
	private Activity activity = EpcWriteActivity.this;
	private HoScrollView scrollView;
	private View leftView, mainView;

	private MapWork mapWork;

	private EpcWriteWork stWork;

	private StorageDao storageDao;

	private EpcWork epcWork;
	
	private int writeFlag =-1;
	private int resultFlag=-1;

	private Handler handler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{

			case ZKCmd.HAND_REQUEST_DATA:

				if (ZKCmd.ARG_REQUEST_EPC_LIST == msg.arg1)
				{
					if (0 == msg.arg2)
					{
						// 查询ECP码的数据请求
						storageDao.getEpcListDataTask(msg.obj + "");
					} 
					else
					{
						GetDataThread thread = new GetDataThread(activity, handler);
						thread.setParams(msg.obj + "");
						thread.setHandlerCode(ZKCmd.HAND_RESPONSE_DATA);
						thread.setArg1(ZKCmd.ARG_REQUEST_EPC_LIST);
						thread.setArg2(msg.arg2);
						thread.start();
					}
				}

				break;

			case ZKCmd.HAND_RESPONSE_DATA:

				if (ZKCmd.ARG_REQUEST_EPC_LIST == msg.arg1)
				{
					List<EpcInfo> list = storageDao.getEpcList(msg.obj + "");

					if (0 == msg.arg2)
					{
						// 这是点击查询按钮的
						stWork.showEpcData(list);
					} 
					else
					{
						// 这里是下拉或者加载更多
						stWork.showEpcDataByLoad(list, msg.arg2);
					}
				}
				break;

			case ZKCmd.HAND_NOTIFY_DATA_CHANGE:

				stWork.showCountText();

				break;

			case ZKCmd.HAND_CLEAR_BOX_CHECK:

				initMap();// 重新加载平面图

				break;

			case ZKCmd.HAND_CREATE_EPC_ONCLICK:

				EpcInfo info = (EpcInfo) msg.obj;
				//LogUtil.d(TAG, "Msg:arg1="+msg.arg1+",arg2="+msg.arg2);
				createEpc(info, msg.arg1);// 点击生成EPC

				break;

			case HostPacketType.RFID_PACKET_TYPE_COMMAND_BEGIN:
			{
				String msg_type = msg.getData().getString(Message_Type.msg_type);
				short cmd_type = msg.getData().getShort(Message_Type.begin_cmd);
				LogUtil.e(TAG, "收到来自下层开始命令"+msg_type+",cmd_type="+cmd_type);
				
				if(MAC_Command.CMD_18K6CWRITE == cmd_type)//18K6C_Write Command
				{
					writeFlag =-1;
					resultFlag=-1;
				}
				break;
			}
			case HostPacketType.RFID_PACKET_TYPE_18K6C_INVENTORY:
			{
				
				break;
			}
			case HostPacketType.RFID_PACKET_TYPE_18K6C_TAG_ACCESS:
			{
			
				String msg_type2 = msg.getData().getString(Message_Type.msg_type);
				int tag_err     = msg.getData().getInt(Message_Type.tag_err);
				short tag_write = msg.getData().getShort(Message_Type.tag_write);
				
				LogUtil.e(TAG, "收到来自下层TAG_ACCESS命令tag_err:"+tag_err);
				LogUtil.e(TAG, "传值情况writeFlag:"+writeFlag);
				LogUtil.e(TAG, "收到来自下层RFID_PACKET_TYPE_18K6C_TAG_ACCESS命令tag_write和tag_err:"+tag_write+">>"+tag_err);
				if (msg_type2 == Message_Type.tag_read)
				{

				} else if (msg_type2 == Message_Type.tag_read_EPC)
				{

				} else if (msg_type2 == Message_Type.tag_check)
				{
										
					writeFlag=tag_err;
					if(0==tag_err)
					{
						resultFlag=0;
						new CommDialog(activity).showReadWriteResMsg(tag_err);	
					}
				}
				break;
			}
			case HostPacketType.RFID_PACKET_TYPE_COMMAND_END:
			{
				String msg_type = msg.getData().getString(Message_Type.msg_type);

				if (msg_type == Message_Type.mac_err)
				{
					String mac_err = msg.getData().getString(Message_Type.mac_err);
					LogUtil.i(TAG, "RFID_PACKET_TYPE_COMMAND_END:" + mac_err);
					if (mac_err.equals("0000"))
					{

					} else
					{
						CommUtil.toastShow("MAC错误代码:" + mac_err, activity);
					}
				}
				LogUtil.i(TAG, "收end命令时的writeFlag值:"+writeFlag);
				LogUtil.i(TAG, "收end命令时的resultFlag值:"+resultFlag);
				
				if(0==resultFlag)
				{
					LogUtil.i(TAG, "写入是成功的");		
				}else
				{
					LogUtil.e(TAG, "写入失败");		
					if(-1==writeFlag)
					{
						CommUtil.toastShow("没有找到标签", activity);
					}else
					{
						new CommDialog(activity).showReadWriteResMsg(writeFlag);	
					}
				}
				
				epcWork.reset();
				//epcWork.powerOff();// 直接关电

				break;
			}
			default:
				break;
			}
		};

	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		initView();
		initData();
		initMap();
	}

	/**初始化控件**/
	private void initView()
	{

		LayoutInflater inflater = LayoutInflater.from(activity);
		setContentView(inflater.inflate(R.layout.store_material_total, null));

		scrollView = (HoScrollView) findViewById(R.id.scrollView);
		scrollView.setLeftMenuArgs(1);// 设置左侧占屏幕比例，1/1
		mainView = inflater.inflate(R.layout.store_material_view, null);

		leftView = inflater.inflate(R.layout.storemapview, null);
		View[] children = new View[]
		{ leftView, mainView };

		View view = leftView.findViewById(R.id.mapLin);
		scrollView.initViews(children, 1, new SizeCallbackForMenu(view));

		new CommTopView(activity);

		stWork = EpcWriteWork.getInstance(activity);
		stWork.initView(handler);

	}

	/**初始化平面图**/
	private void initMap()
	{
		if (null != mapWork)
		{
			mapWork = null;
		}
		mapWork = new MapWork(activity, true);
		mapWork.initView(handler, leftView);
	}

	/**初始化数据，并设置为小功率**/
	private void initData()
	{
		storageDao = new StorageDao(activity, handler);

		// 如果是新增物资过来，就将日期定为当天的，把当天新增的列出来
		boolean flag = getIntent().getBooleanExtra(ConfigUtil.GET_NEW_MATERIAL_EPC, false);
		stWork.queryData(flag);

		epcWork = MyApplication.getRfidWork();
		epcWork.setReaderHandler(handler);
		new CountDownTimer(1500,100)
		{		
			@Override
			public void onTick(long millisUntilFinished)
			{			
			}			
			@Override
			public void onFinish()
			{
				PowerSetWork.setObjectPower(ConfigUtil.POWER_SMALL);
			}
		}.start();		
	}

	/** 仓位信息 **/
//	private void getPositionData(String pos)
//	{
//		StringBuffer sb = new StringBuffer();
//		sb.append("t=" + ZKCmd.REQ_QUERY_POS);
//		sb.append("&position=").append(pos);
//		storageDao = new StorageDao(activity, handler);
//		storageDao.getPosDataTask(sb.toString());
//	}

	/** 将epc码写入标签中**/
	private void createEpc(EpcInfo info, final int arg)
	{
		writeFlag =-1;
		resultFlag=-1;
		
		//epcWork.powerOn();
		//SystemClock.sleep(1000);
		LogUtil.e(TAG, "EpcInfo["+arg+"]写入的epc:"+info.getEpcCode());
		epcWork.writeEpcData(info.getEpcCode());
		// epcWork.readEpcData();
		new CountDownTimer(1500,100)
		{		
			@Override
			public void onTick(long millisUntilFinished)
			{			
			}			
			@Override
			public void onFinish()
			{
				LogUtil.d(TAG, "收onFinish时的resultFlag值:"+resultFlag+",writeFlag="+writeFlag);
				//if((0==resultFlag) && (0==writeFlag))
				if(0==resultFlag)
				{
					stWork.changeColer(arg,Color.GREEN);
				}
				else
				{
					stWork.changeColer(arg,Color.RED);
				}
			}
		}.start();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		return scrollView.onTouchEvent(event, scrollView, leftView);
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		if (null != epcWork)
		{
			epcWork.stopScan();
			epcWork.powerOff();
		}

		EpcWriteWork.clearWork();

	}
}

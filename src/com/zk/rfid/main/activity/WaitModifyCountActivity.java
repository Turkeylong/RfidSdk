package com.zk.rfid.main.activity;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.rfid.sdk.public_utils.HostPacketType;
import com.rfid.sdk.public_utils.MAC_Command;
import com.rfid.sdk.public_utils.Message_Type;
import com.zk.rfid.R;
import com.zk.rfid.comm.base.BaseActivity;
import com.zk.rfid.comm.base.CommTopView;
import com.zk.rfid.comm.base.MyApplication;
import com.zk.rfid.main.bean.EpcInfo;
import com.zk.rfid.main.util.ConfigUtil;
import com.zk.rfid.main.work.EpcWork;
import com.zk.rfid.main.work.PowerSetWork;
import com.zk.rfid.main.work.WaitModifyCountWork;
import com.zk.rfid.util.CommUtil;
import com.zk.rfid.util.GetDataThread;
import com.zk.rfid.util.LogUtil;
import com.zk.rfid.util.ZKCmd;

/**
 * @Description: 需要修改数量控制器
 * @date:2016年7月7日 上午9:57:21
 * @author: ldl
 */
public class WaitModifyCountActivity extends BaseActivity
{
	private static final String TAG = "WaitModifyCountActivity";
	private Activity activity = WaitModifyCountActivity.this;
	private WaitModifyCountWork waitWork;
	private EpcWork epcWork;
	private EpcInfo epcInfo;
	
	private Dialog scanDialog;

	private int Cur_Command = 0;

	private Handler handler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{

			case ZKCmd.HAND_REQUEST_DATA:

				
				break;

			case ZKCmd.HAND_RESPONSE_DATA:

				String res = msg.obj + "";
				int arg1 = msg.arg1;
				if (ZKCmd.ARG_MODIFY_EPC_SERVER_RES == arg1)
				{
					// 通知服务器修改epc成功,返回结果
					LogUtil.i(TAG, "通知服务器修改epc成功,返回结果：" + res);					
				}			
				break;

			case ZKCmd.HAND_MODIFY_EPC:

				EpcInfo info=(EpcInfo) msg.obj;
				modifyEpc(info);
				break;

			// 处于盘点
			case HostPacketType.RFID_PACKET_TYPE_18K6C_INVENTORY:
			{
				Bundle bd = msg.getData();
				String type = bd.getString(Message_Type.msg_type);
				//LogUtil.i(TAG, "操作返回状态:" + type);
				if (Message_Type.tag_inventory.equals(type))
				{
					// List<TAG_TID> tagTids=(List<TAG_TID>)
					// bd.getSerializable(Message_Type.tag_inventory);				
				}
			}

				break;

			case HostPacketType.RFID_PACKET_TYPE_COMMAND_BEGIN:

				LogUtil.i(TAG, "收到开始命令");
				int cmd_type = msg.getData().getInt(Message_Type.cmd_type);
				Cur_Command = cmd_type;

				break;
				
			 case HostPacketType.RFID_PACKET_TYPE_18K6C_TAG_ACCESS:
										
				 Bundle bd = msg.getData();			
				 String type = bd.getString(Message_Type.msg_type);
				 LogUtil.i(TAG, "收到TAG_ACCESS命令");
				 if(Message_Type.tag_check.equals(type))
				 {
					 if(MAC_Command.CMD_18K6CWRITE==Cur_Command)
					 {
						//0代表写入成功
						 int tag_err = msg.getData().getInt(Message_Type.tag_err);
						 LogUtil.i(TAG, "收到TAG_ACCESS命令,tag_err:"+tag_err);
						 //modifyType=tag_err;//在一定时间段内保存这个标记
					 }					 								
				 }
			 break;

			// 盘点结束命令
			case HostPacketType.RFID_PACKET_TYPE_COMMAND_END:
			
				if (Cur_Command == MAC_Command.CMD_18K6CINV)
				{
											
				}
				else if(Cur_Command == MAC_Command.CMD_18K6CWRITE)
				{
					LogUtil.i(TAG, "写结束");
					dialogDismiss();
				}

				break;
				
			case ZKCmd.HAND_DIALOG_MISS:
			         
				   dialogDismiss();
				break;

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
		
		setContentView(R.layout.wait_modify);

		initView();	
	}

	/**初始控件，并设置天线为小功率**/
	private void initView()
	{
		new CommTopView(activity);
		waitWork=WaitModifyCountWork.getInstance(activity);
		waitWork.initView(handler);
			
		epcWork=MyApplication.getRfidWork();
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
				PowerSetWork.setObjectPower(ConfigUtil.POWER_SMALL);//写入标签用小功率
			}
		}.start();
	}

	/** 修改epc值,查找到原epc标签后再写入新的标签 **/
	private void modifyEpc(EpcInfo info)
	{
		epcInfo=info;
		scanDialog=CommUtil.createDialog(activity);
		String oldCode = info.getEpcCode();
		String newCode = info.getNewEcpCode();
		epcWork.powerOn();
		SystemClock.sleep(500);
		boolean flag   =epcWork.modifyEpcData(oldCode, newCode);			
		LogUtil.i(TAG, "修改epc结果:" + flag);					
		if(flag)
		{											
			modifyepcRes(0);
		}else
		{
			modifyepcRes(1);
		}		
	}

	/** 手持机写入结果 **/
	private void modifyepcRes(int type)
	{
		dialogDismiss();
		
		// 修改成功
		if (0 == type)
		{
			CommUtil.toastShow("修改成功", activity);
			
			StringBuilder sb = new StringBuilder();
			sb.append("t=" + ZKCmd.REQ_MODIFY_SUCCESS);
			sb.append("&materialId=").append(epcInfo.getMaterialId());
			sb.append("&oldEpcCode=").append(epcInfo.getEpcCode());
			sb.append("&beforeCount=").append(epcInfo.getOldCount());
			sb.append("&afterCount=").append(epcInfo.getMaterialCount());
			
			GetDataThread thread = new GetDataThread(activity, handler);
			thread.setHandlerCode(ZKCmd.HAND_RESPONSE_DATA);
			thread.setArg1(ZKCmd.ARG_MODIFY_EPC_SERVER_RES);
			thread.setParams(sb.toString());
			thread.start();
			
		} else
		{
			CommUtil.toastShow("修改失败", activity);
		}
		
		epcWork.powerOff();//再次关电，以防底下会再次修改
	}

	@Override
	protected void onDestroy()
	{
		if (null != epcWork)
		{
			epcWork.powerOff();
		}
		WaitModifyCountWork.clearWork();
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
	

}



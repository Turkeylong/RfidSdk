package com.zk.rfid.main.activity;

import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.rfid.sdk.public_utils.HostPacketType;
import com.rfid.sdk.public_utils.Message_Type;
import com.zk.rfid.R;
import com.zk.rfid.comm.base.BaseActivity;
import com.zk.rfid.comm.base.CommDialog;
import com.zk.rfid.comm.base.CommTopView;
import com.zk.rfid.comm.scro.HoScrollView;
import com.zk.rfid.comm.scro.SizeCallbackForMenu;
import com.zk.rfid.main.bean.EpcInfo;
import com.zk.rfid.main.work.AdjustWork;
import com.zk.rfid.map.work.MapWork;
import com.zk.rfid.util.CommUtil;
import com.zk.rfid.util.GetDataThread;
import com.zk.rfid.util.LogUtil;
import com.zk.rfid.util.ZKCmd;

/**
 * @Description: 调整仓位
 * @date:2016年6月29日 上午9:57:21
 * @author: ldl
 */
public class AdjustActivity extends BaseActivity
{
	private static final String TAG = "AdjustActivity";
	private Activity activity = AdjustActivity.this;

	private AdjustWork stWork;

	private HoScrollView scrollView;
	private View leftView, mainView;
	private Dialog scanDialog;
	private EpcInfo epcInfo;

	private Handler matViewHandler;

	private MapWork mapWork;
	private String modifyParamTemp;

	private Handler handler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{

			case ZKCmd.HAND_REQUEST_DATA:

				if (msg.arg1 == ZKCmd.ARG_GET_POS_EPC)
				{
					String param = msg.obj + "";//
					if (isConn())
					{
						getNewEpcCodeReq(param, ZKCmd.ARG_GET_POS_EPC);
					}
				}

				break;

			case ZKCmd.HAND_RESPONSE_DATA:

				String res = msg.obj + "";
				if (msg.arg1 == ZKCmd.ARG_GET_POS_EPC)
				{
					getNewEpcCodeRes(res);
				}

				if (msg.arg1 == ZKCmd.ARG_MODIFY_EPC_SERVER_RES)
				{
					// 通知服务器修改epc成功,返回结果
					LogUtil.i(TAG, "通知服务器调整位置epc修改成功,返回结果：" + res);
					setResult(ZKCmd.SEND_RES_CODE_RE_QUERY);
					finish();
				}
				break;

			case ZKCmd.HAND_GET_POS_BY_MAP:

				if (ZKCmd.ARG_GET_POS_REQUEST == msg.arg1)
				{
					matViewHandler = (Handler) msg.obj;// 增加物资时，点击后将指定的viewHand
				}
				break;

			case ZKCmd.HAND_MAP_ONCLICK:

				String objVal = msg.obj + "";
				LogUtil.i(TAG, "点击后获得位置:" + objVal);
				// 点击图后有值返回,如果这个不为空，则传到其更新UI
				if (null != matViewHandler)
				{
					Message ms = Message.obtain();
					ms.what = ZKCmd.HAND_MAP_ONCLICK;
					ms.obj = objVal;
					matViewHandler.sendMessage(ms);
				}
				// 点完后，mapWork还要改变点中的背景
				mapWork.setBtnBackground(objVal);
				break;

			case HostPacketType.RFID_PACKET_TYPE_COMMAND_END:

				getEpcOperate(msg);

				break;

			case HostPacketType.RFID_PACKET_TYPE_18K6C_TAG_ACCESS:

				getEpcOperate(msg);

				break;

			default:
				break;
			}
		};

	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Window window = getWindow();  
        WindowManager.LayoutParams params = window.getAttributes();  
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE;
        window.setAttributes(params);
		initView();
		initTopView();
		initData();

	}

	/**执行公用顶部返回操作**/
	private void initTopView()
	{
		new CommTopView(activity);
	}

	/**初始化界面，由左菜单和主界面组成**/
	private void initView()
	{
		LayoutInflater inflater = LayoutInflater.from(activity);
		setContentView(inflater.inflate(R.layout.adjust_epc_total, null));

		scrollView = (HoScrollView) findViewById(R.id.scrollView);
		scrollView.setLeftMenuArgs(1);// 设置左侧占屏幕比例，1/1
		mainView = inflater.inflate(R.layout.adjustepc, null);

		leftView = inflater.inflate(R.layout.storemapview, null);
		View[] children = new View[]
		{ leftView, mainView };

		View view = leftView.findViewById(R.id.mapLin);
		scrollView.initViews(children, 1, new SizeCallbackForMenu(view));
	}

	/**分发事件**/
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		return scrollView.onTouchEvent(event, scrollView, leftView);
	}

	/**初始执行数据**/
	private void initData()
	{
		stWork = AdjustWork.getInstance(activity);
		stWork.initView(handler, mainView);
		epcInfo = (EpcInfo) getIntent().getSerializableExtra("info");
		stWork.initData(epcInfo);

		mapWork = new MapWork(activity);// 平面图
		mapWork.initView(handler, leftView);
	}

	/** 修改epc值请求，先请求服务器获得新的epc,再进行修改，修改成功后再向服务器发消息确认修改成功 **/
	private void getNewEpcCodeReq(String param, int arg1)
	{
		modifyParamTemp = param;

		scanDialog = CommUtil.createDialog(activity);
		GetDataThread thread = new GetDataThread(activity, handler);
		thread.setHandlerCode(ZKCmd.HAND_RESPONSE_DATA);
		thread.setParams(param);
		thread.setArg1(arg1);
		thread.start();
	}

	/** epc读写操作结果,根据结果码显示相应信息 **/
	private void getEpcOperate(Message msg)
	{
		String msg_type = msg.getData().getString(Message_Type.msg_type);
		LogUtil.i(TAG, "读写操作返回:" + msg_type);
		if (msg_type == Message_Type.mac_err)
		{
			String mac_err = msg.getData().getString(Message_Type.mac_err);
			if (mac_err.equals("0000"))
			{

			} else
			{
				CommUtil.toastShow("MAC错误代码:" + mac_err, activity);
			}
		} else if (msg_type == Message_Type.tag_read)
		{
			// String data = msg.getData().getString(Message_Type.tag_read);
			String mes = msg.getData().getString(Message_Type.tag_read);
			// CommUtil.toastShow("读到:"+mes, activity);

		} else if (msg_type == Message_Type.tag_read_EPC)
		{
			String mes = msg.getData().getString(Message_Type.tag_read_EPC);
			// CommUtil.toastShow("读到:"+mes, activity);
		} else if (msg_type == Message_Type.tag_check)
		{
			int tag_err = msg.getData().getInt(Message_Type.tag_err);
			new CommDialog(activity).showReadWriteResMsg(tag_err);
			stWork.powerOff();
		}
	}
	/**获得新的标签码，解析服务端返回的json数据**/
	private void getNewEpcCodeRes(String result)
	{
		try
		{
			JSONObject json = new JSONObject(result).getJSONObject("result");

			String oldCode = epcInfo.getEpcCode();

			String newCode = json.optString("newEpcCode", "");
			if (!"".equals(oldCode) && oldCode.length() == 32 && !"".equals(newCode) && newCode.length() == 32)
			{
				boolean flag = stWork.modifyEpc(oldCode, newCode);
				LogUtil.i(TAG, "修改epc结果:" + flag);
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
				CommUtil.toastShow("返回新标签格式有误:"+newCode, activity);
			}
		} catch (Exception e)
		{
			dialogDismiss();
			CommUtil.toastShow("获取新EPC标签码发生异常", activity);
		}
	}

	/** 手持机写入指定标签结果 **/
	private void modifyepcRes(int type)
	{
		dialogDismiss();

		// 修改成功
		if (0 == type)
		{
			CommUtil.toastShow("调整成功", activity);
			if (null != modifyParamTemp)
			{
				// sueecssFinish=true;
				// 将原来的32换成33，参数不变，服务端进行修改
				String param = modifyParamTemp.replace("t=" + ZKCmd.REQ_GET_NEW_POSITION_CODE, "t="+ ZKCmd.REQ_UPDATE_POSITION);
				// 成功后，告诉服务器，让服务器更新数据库
				GetDataThread thread = new GetDataThread(activity, handler);
				thread.setHandlerCode(ZKCmd.HAND_RESPONSE_DATA);
				thread.setArg1(ZKCmd.ARG_MODIFY_EPC_SERVER_RES);
				thread.setParams(param);
				thread.start();
			}
		} else
		{
			CommUtil.toastShow("调整失败", activity);
		}
	}

	@Override
	protected void onDestroy()
	{
		AdjustWork.clearAdjustWork();
		stWork.powerOff();// 断电
		dialogDismiss();
		super.onDestroy();
	}

	/**对话框消失控制，必须执行，否在个别手机上会出现找不到依托对象而出现崩溃**/
	private void dialogDismiss()
	{
		if (null != scanDialog && scanDialog.isShowing())
		{
			scanDialog.dismiss();
			scanDialog = null;
		}
	}
}

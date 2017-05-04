package com.zk.rfid.main.work;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.zk.rfid.R;
import com.zk.rfid.comm.base.MyApplication;
import com.zk.rfid.main.bean.EpcInfo;
import com.zk.rfid.util.CommUtil;
import com.zk.rfid.util.ConstantUtil;
import com.zk.rfid.util.LogUtil;
import com.zk.rfid.util.ZKCmd;

/**
 * @Description: 仓位调整
 * @date:2016-7-13上午11:21:03
 * @author: ldl
 */
public class AdjustWork
{
	private static final String TAG = "AdjustWork";

	private static AdjustWork adjustWork;
	private Activity activity;
	private View mainView;
	private Handler handler;

	private EditText storage_pos, pos_item_edit;
	private TextView pos_full, isBoxText, material_id, material_name;
	private Button update_pos_submit;
	private EpcInfo epcInfo;
	private EpcWork epcWork;

	private Handler mHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case ZKCmd.HAND_MAP_ONCLICK:

				String val = msg.obj + "";// 平面图位置
				updateView(val);
				break;
			default:
				break;
			}
		};
	};

	private AdjustWork(Activity act)
	{
		activity = act;
	}

	public static AdjustWork getInstance(Activity act)
	{
		if (null == adjustWork)
		{
			adjustWork = new AdjustWork(act);
		}
		return adjustWork;
	}

	/**初始化界面**/
	public void initView(Handler handler, View mainView)
	{
		this.mainView = mainView;
		this.handler = handler;
		getViewById();
	}

	/**初始化控件**/
	private void getViewById()
	{
		material_id = (TextView) mainView.findViewById(R.id.material_id);
		material_name = (TextView) mainView.findViewById(R.id.material_name);
		storage_pos = (EditText) mainView.findViewById(R.id.storage_pos);
		pos_item_edit = (EditText) mainView.findViewById(R.id.pos_item_edit);
		pos_full = (TextView) mainView.findViewById(R.id.pos_full);
		isBoxText = (TextView) mainView.findViewById(R.id.isBoxText);

		update_pos_submit = (Button) mainView.findViewById(R.id.update_pos_submit);
		update_pos_submit.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				submitUpdate();
			}
		});

		storage_pos.setOnTouchListener(new View.OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				getPosCode();
				return false;
			}
		});

		epcWork = MyApplication.getRfidWork();
		epcWork.setReaderHandler(handler);
	}

	/**点击平面图，获得位置码**/
	private void getPosCode()
	{
		Message msg = Message.obtain();
		msg.what = ZKCmd.HAND_GET_POS_BY_MAP;
		msg.arg1 = ZKCmd.ARG_GET_POS_REQUEST;
		msg.obj = mHandler;
		handler.sendMessage(msg);// 点击后将位置传过去在平面图上点击时将位置传给他
	}

	/**调整仓位，初始化数据**/
	public void initData(EpcInfo info)
	{
		LogUtil.i(TAG, "收到:" + info.toString());
		this.epcInfo = info;
		try
		{
			String isbox = epcInfo.getIsBox();
			if ("1".equals(isbox))
			{
				isBoxText.setText("是");
			} else
			{
				isBoxText.setText("否");
			}

			material_id.setText(epcInfo.getMaterialId());
			material_name.setText(epcInfo.getMaterialName() + epcInfo.getMaterialSpecDescribe() + "");
			String posCode = epcInfo.getPositionCode();
			LogUtil.i(TAG, "位置:" + posCode);
			
			String[] posarray=posCode.split("-");

			String pos = posarray[4] + "-" + posarray[5] + "-" + posarray[7] + "-" +posarray[8];

			storage_pos.setText(pos);
			pos_full.setText(posCode);
			pos_item_edit.setText(posarray[8]);

		} catch (Exception e)
		{
			LogUtil.e(TAG, "调仓获得标签信息异常:" + e.getMessage());
		}
	}

	/**提交调整仓位数据**/
	private void submitUpdate()
	{
		String param = "";
		String posCode = storage_pos.getText().toString();
		String[] posarray=posCode.split("-");
		LogUtil.i(TAG, "posarray.length:" + posarray.length);
		if (posarray.length != 4)
		{
			CommUtil.toastShow("位置码格式不对", activity);
			return;
		}
		else
		{
			posCode = ConstantUtil.POSITION_HEAD_CODE + "-1-" +posarray[0] + "-" +posarray[1] + "-1-" +posarray[2] + "-" +posarray[3];
		}

		StringBuilder sb = new StringBuilder();
		sb.append("t=" + ZKCmd.REQ_GET_NEW_POSITION_CODE);
		sb.append("&epcCode=").append(epcInfo.getEpcCode());
		sb.append("&positionCode=").append(posCode);

		param = sb.toString();
		LogUtil.i(TAG, "提交的参数:" + param);

		Message msg = Message.obtain();
		msg.what = ZKCmd.HAND_REQUEST_DATA;
		msg.arg1 = ZKCmd.ARG_GET_POS_EPC;
		msg.obj = param;
		handler.sendMessage(msg);
	}

	/**获得平面图的新位置**/
	private void updateView(String val)
	{
		try
		{

			if (null != storage_pos)
			{
				String array[] = val.split("_");
				pos_full.setText(array[0]);
				LogUtil.i(TAG, "位置码:" + array[0]);
				storage_pos.setText(array[1]);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/** 修改标签上的位置**/
	public boolean modifyEpc(String oldCode, String newCode)
	{
		epcWork.powerOn();// 开电，隔几秒再修改
		SystemClock.sleep(500);
		return epcWork.modifyEpcData(oldCode, newCode);
	}

	public static void clearAdjustWork()
	{
		adjustWork = null;
	}

	public void powerOff() {
		// TODO 自动生成的方法存根
		epcWork.powerOff();
	}
}

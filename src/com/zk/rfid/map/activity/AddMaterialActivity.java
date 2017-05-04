package com.zk.rfid.map.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import com.zk.rfid.R;
import com.zk.rfid.comm.base.BaseActivity;
import com.zk.rfid.comm.base.CommTopView;
import com.zk.rfid.comm.scro.HoScrollView;
import com.zk.rfid.comm.scro.SizeCallbackForMenu;
import com.zk.rfid.main.util.ConfigUtil;
import com.zk.rfid.map.dao.StorageDao;
import com.zk.rfid.map.work.AddMaterialWork;
import com.zk.rfid.map.work.MapWork;
import com.zk.rfid.util.CommUtil;
import com.zk.rfid.util.LogUtil;
import com.zk.rfid.util.ZKCmd;

/**
 * @Description: 物资增加
 * @date:2016-6-17上午9:46:44
 * @author: ldl
 */
public class AddMaterialActivity extends BaseActivity
{
	private static final String TAG = "MainActivity";
	private Activity activity = AddMaterialActivity.this;

	private Handler matViewHandler;

	private AddMaterialWork materialWork;
	private StorageDao storageDao;
	private MapWork mapWork;

	private HoScrollView scrollView;
	private View leftView, mainView;

	private Handler handler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{

			case ZKCmd.HAND_REQUEST_DATA:

				if (isConn())
				{
					String parma = msg.obj + "";
					storageDao = new StorageDao(activity, handler);

					if (ZKCmd.ARG_REQUEST_ADD_MATERIAL == msg.arg1)
					{
						storageDao.addMaterialTask(parma);
					}
					if (ZKCmd.ARG_REQUEST_ADD_BOX == msg.arg1)
					{
						storageDao.addMaterialBoxTask(parma);
					}

					storageDao = null;
				}
				break;

			case ZKCmd.HAND_RESPONSE_DATA:

				String res = msg.obj + "";
				int arg = msg.arg1;
				if (ZKCmd.ARG_REQUEST_ADD_MATERIAL == arg)
				{
					addMaterialRes(res);
				}
				if (ZKCmd.ARG_REQUEST_ADD_BOX == arg)
				{
					addMaterialRes(res); // 增加物资结果,与增加物资处理相同
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
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.addmaterial_total);
		initView();
		initTopView();
		initData();
	}

	/** 初始化界面控件和数据，加载平面图 **/
	private void initData()
	{
		materialWork = AddMaterialWork.getInstance(activity);// 物资
		materialWork.initView(handler, mainView);

		mapWork = new MapWork(activity);// 平面图
		mapWork.initView(handler, leftView);
	}

	/** 初始化顶部返回操作 **/
	private void initTopView()
	{
		new CommTopView(activity);
	}

	/** 提交新增物资操作返回的结果处理 **/
	private void addMaterialRes(String res)
	{
		JSONObject obj;
		try
		{
			obj = new JSONObject(res).getJSONObject("result");
			CommUtil.toastShow(obj.optString("msg", ""), activity);
			if ("1".equals(obj.optString("state", "")))
			{
				Intent intent = new Intent();
				intent.setClass(activity, EpcWriteActivity.class);
				intent.putExtra(ConfigUtil.GET_NEW_MATERIAL_EPC, true);
				startActivity(intent);

				finish();// 关闭界面
			}
		} catch (JSONException e)
		{
			CommUtil.toastShow("返回操作结果异常", activity);
			e.printStackTrace();
		}

	}

	/** 初始化界面，由左菜单和主界面组成 **/
	private void initView()
	{
		LayoutInflater inflater = LayoutInflater.from(activity);
		setContentView(inflater.inflate(R.layout.addmaterial_total, null));

		scrollView = (HoScrollView) findViewById(R.id.scrollView);
		scrollView.setLeftMenuArgs(1);// 设置左侧占屏幕比例，1/1
		mainView = inflater.inflate(R.layout.addmaterial, null);

		leftView = inflater.inflate(R.layout.storemapview, null);
		View[] children = new View[]
		{ leftView, mainView };

		View view = leftView.findViewById(R.id.mapLin);
		scrollView.initViews(children, 1, new SizeCallbackForMenu(view));
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
		AddMaterialWork.clearWork();
		mapWork = null;
	}
}

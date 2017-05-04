package com.zk.rfid.map.work;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.zk.rfid.R;
import com.zk.rfid.comm.date.DatePickerDialogUtil;
import com.zk.rfid.comm.date.DateTimeCallBackListener;
import com.zk.rfid.main.util.ConfigUtil;
import com.zk.rfid.map.dao.MaterialViewDao;
import com.zk.rfid.util.CommUtil;
import com.zk.rfid.util.ConstantUtil;
import com.zk.rfid.util.HttpGetPostUtil;
import com.zk.rfid.util.LogUtil;
import com.zk.rfid.util.ZKCmd;

/**
 * @Description: 添加物资
 * @date:2016-6-20上午9:25:31
 * @author: ldl
 */
public class AddMaterialWork
{

	private static final String TAG = "AddMaterialWork";

	private static AddMaterialWork work;

	private Activity activity;
	private Handler handler;
	private View mainView;
	private static Dialog timeDialog;

	private AddMaterialWork(Activity act)
	{
		activity = act;
	}

	public static AddMaterialWork getInstance(Activity act)
	{
		if (null == work)
		{
			work = new AddMaterialWork(act);
		}
		return work;
	}

	private LinearLayout addLin, mat_box_lin;
	private EditText approvalOrderNumberEdit, send_company, arrivedDateEdit, purchaseOrderNumberEdit, add_remarks;
	private Button add_btn, add_submit_btn;

	// 箱子
	private EditText material_box_code, material_box_name, storage_box_pos, include_box_count, box_pos_item_edit,
			material_box_spec;
	private TextView show_box_count, box_pos_full;
	private Button add_box_submit;

	// 控制箱子和非箱子显示层
	private RadioGroup boxRadioGroup;
	private ScrollView add_mat_scro_lin;

	// 增加的view
	private List<View> listMatView;

	private Handler mHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case ZKCmd.HAND_GET_MATERIAL_NAME:

				setMaterialVal(msg.obj + "");
				// String name = msg.obj + "";
				// material_box_name.setText(name);
				// int cou = msg.arg1;
				// show_box_count.setText("库中已存在" + cou + "个箱子,将添加第" + (cou + 1)
				// + "号箱子");
				break;

			case ZKCmd.HAND_MAP_ONCLICK:

				String val = msg.obj + "";// 平面图位置
				if (null != storage_box_pos)
				{
					String array[] = val.split("_");
					box_pos_full.setText(array[0]);//完整位置码，方便数据存储的完整性
					storage_box_pos.setText(array[1]);//简略，客户需要看得懂
				}
				break;
			default:
				break;
			}
		};
	};

	public void initView(Handler handler, View mainView)
	{
		this.mainView = mainView;
		this.handler = handler;
		getViewById();

		new MaterialViewDao(activity, handler).getView(addLin, listMatView); // 默认先增加一条
	}

	private void getViewById()
	{
		listMatView = null;
		listMatView = new ArrayList<View>();

		addLin = (LinearLayout) mainView.findViewById(R.id.addLin);
		mat_box_lin = (LinearLayout) mainView.findViewById(R.id.mat_box_lin);
		add_btn = (Button) mainView.findViewById(R.id.add_btn);
		add_submit_btn = (Button) mainView.findViewById(R.id.add_submit_btn);
		boxRadioGroup = (RadioGroup) mainView.findViewById(R.id.boxRadioGroup);

		add_mat_scro_lin = (ScrollView) mainView.findViewById(R.id.add_mat_scro_lin);

		// 箱子
		material_box_code = (EditText) mainView.findViewById(R.id.material_box_code);
		material_box_name = (EditText) mainView.findViewById(R.id.material_box_name);
		storage_box_pos = (EditText) mainView.findViewById(R.id.storage_box_pos);
		include_box_count = (EditText) mainView.findViewById(R.id.include_box_count);
		box_pos_item_edit = (EditText) mainView.findViewById(R.id.box_pos_item_edit);
		material_box_spec = (EditText) mainView.findViewById(R.id.material_box_spec);

		add_box_submit = (Button) mainView.findViewById(R.id.add_box_submit);
		show_box_count = (TextView) mainView.findViewById(R.id.show_box_count);
		box_pos_full = (TextView) mainView.findViewById(R.id.box_pos_full);
		material_box_code.addTextChangedListener(textWatcher);
		
		storage_box_pos.setOnTouchListener(new View.OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				Message msg = Message.obtain();
				msg.what = ZKCmd.HAND_GET_POS_BY_MAP;
				msg.arg1 = ZKCmd.ARG_GET_POS_REQUEST;
				msg.obj = mHandler;
				handler.sendMessage(msg);// 点击后将位置传过去在平面图上点击时将位置传给他
				return false;
			}
		});

		add_btn.setOnClickListener(listen);
		add_submit_btn.setOnClickListener(listen);
		add_box_submit.setOnClickListener(listen);

		approvalOrderNumberEdit = (EditText) mainView.findViewById(R.id.approvalOrderNumberEdit);
		send_company = (EditText) mainView.findViewById(R.id.send_company);
		arrivedDateEdit = (EditText) mainView.findViewById(R.id.arrivedDateEdit);
		purchaseOrderNumberEdit = (EditText) mainView.findViewById(R.id.purchaseOrderNumberEdit);
		add_remarks = (EditText) mainView.findViewById(R.id.add_remarks);
		arrivedDateEdit.setOnTouchListener(new View.OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				getTime(arrivedDateEdit);
				return false;
			}
		});

		boxRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1)
			{
				if (R.id.radioBoxNo == arg1)
				{
					add_btn.setVisibility(View.VISIBLE);
					add_mat_scro_lin.setVisibility(View.VISIBLE);
					mat_box_lin.setVisibility(View.GONE);
				}
				if (R.id.radioBoxYes == arg1)
				{
					add_btn.setVisibility(View.GONE);
					add_mat_scro_lin.setVisibility(View.GONE);
					mat_box_lin.setVisibility(View.VISIBLE);
				}
			}
		});
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

	private TextWatcher textWatcher = new TextWatcher()
	{

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count)
		{
			if (!"".equals(s.toString()) && s.toString().length() == 11)
			{
				getBoxNameCount();
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after)
		{
			if (!"".equals(s.toString()) && s.toString().length() == 11)
			{
				getBoxNameCount();
			}
		}

		@Override
		public void afterTextChanged(Editable s)
		{
			if (!"".equals(s.toString()) && s.toString().length() == 11)
			{
				getBoxNameCount();
			}
		}
	};

	/**显示新增物资的结果**/
	public void showAddRes(String res)
	{
		try
		{
			JSONObject obj = new JSONObject(res).getJSONObject("result");

			String state = obj.optString("state", "0");
			if ("1".equals(state))
			{
				CommUtil.toastShow("添加成功", activity);
			} else
			{
				CommUtil.toastShow("添加失败", activity);
			}
		} catch (Exception e)
		{
			CommUtil.toastShow("添加异常", activity);
			LogUtil.e(TAG, "解析库存数据异常：" + e.getMessage());
		}
	}

	private OnClickListener listen = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			switch (v.getId())
			{
			case R.id.add_submit_btn:

				addMaterial();
				break;

			case R.id.add_btn:

				new MaterialViewDao(activity, handler).getView(addLin, listMatView);// 动态添加物资框

				break;

			case R.id.add_box_submit:
				addMaterialBox();
				break;

			default:
				break;
			}

		}

	};

	/** 增加物资提交按钮事件,将界面所填的物资信息封状到的json数组中 **/
	private void addMaterial()
	{
		String handNumber = CommUtil.getShare(activity).getString(ConfigUtil.USER_JOB_NAME, "");// 经办人就是登录用户
		String handName = CommUtil.getShare(activity).getString(ConfigUtil.USER_FULL_NAME, "");
		String approvalOrderNumber = approvalOrderNumberEdit.getText().toString();// 验收单号
		String sendCompany = send_company.getText().toString();
		String arriveDate = arrivedDateEdit.getText().toString();
		String purchaseOrderNumber = purchaseOrderNumberEdit.getText().toString();
		String remarks = add_remarks.getText().toString();

		if ("".equals(purchaseOrderNumber))
		{
			CommUtil.toastShow("采购单号不能为空", activity);
			return;
		}
		if ("".equals(approvalOrderNumber))
		{
			CommUtil.toastShow("验收单号不能为空", activity);
			return;
		}
		if ("".equals(arriveDate))
		{
			CommUtil.toastShow("到货时间不能为空", activity);
			return;
		}

		int len = listMatView.size();// 从添加的所有view中取出值获得数据
		View view = null;
		MaterialViewDao mdao = new MaterialViewDao(activity, handler);
		JSONObject json = null;
		boolean flag = true;// 标记是否可提交
		JSONArray jsonArray = new JSONArray();
		for (int i = 0; i < len; i++)
		{
			view = listMatView.get(i);
			json = mdao.getSubmitDataByView(view);
			if (null == json)
			{
				flag = false;
				break;// 有不合条件的参数，未填全等原因,跳出
			} else
			{
				LogUtil.i(TAG, "参数:" + json.toString());
				jsonArray.put(json);// 通过则加入
			}
		}
		if (flag)
		{
			StringBuffer sb = new StringBuffer();
			sb.append("t=" + ZKCmd.REQ_ADD_MATERIAL);
			sb.append("&materialList=").append(jsonArray.toString());

			sb.append("&personJobNumber=").append(handNumber);
			sb.append("&handlePersonName=").append(handName);

			sb.append("&approvalOrderNumber=").append(approvalOrderNumber);
			sb.append("&purchaseOrderNumber=").append(purchaseOrderNumber);
			
			sb.append("&sendCompany=").append(sendCompany);
			sb.append("&arrivedDate=").append(arriveDate);
			sb.append("&deliveryRemarks=").append(remarks);

			LogUtil.i(TAG, "新增物资上传:" + sb.toString());
			Message msg = Message.obtain();
			msg.arg1 = ZKCmd.ARG_REQUEST_ADD_MATERIAL;
			msg.what = ZKCmd.HAND_REQUEST_DATA;
			msg.obj = sb.toString();
			handler.sendMessage(msg);
		}

	}

	private void addMaterialBox()
	{
		// material_box_code,material_box_name,storage_box_pos,include_box_count
		String box_code = material_box_code.getText().toString();
		String box_name = material_box_name.getText().toString();
		String box_pos = box_pos_full.getText().toString();// 位置码
		String box_count = include_box_count.getText().toString();
		String box_pos_edt = storage_box_pos.getText().toString();
		String box_pos_item = box_pos_item_edit.getText().toString();

		if ("".equals(box_code) || box_code.length() != 11)
		{
			CommUtil.toastShow("物资编号不能为空且为11位", activity);
			return;
		}
		if ("".equals(box_name))
		{
			CommUtil.toastShow("物资名称不能为空", activity);
			return;
		}

		if ("".equals(box_pos_edt) || null == box_pos_edt)
		{
			CommUtil.toastShow("存放位置不能为空", activity);
			return;
		}
		int co = CommUtil.getParseIntVal(box_pos_item);
		if ("".equals(box_pos_item) || co == 0)
		{
			CommUtil.toastShow("位号不能为空且不为0", activity);
			return;
		}

		if ("".equals(box_count) || "0".equals(box_count))
		{
			CommUtil.toastShow("数量不为空且不为0", activity);
			return;
		}

		box_pos_item = co + "";

		StringBuffer sb = new StringBuffer();
		sb.append("t=" + ZKCmd.REQ_ADD_MATERIAL_BOX);

		sb.append("&positionCode=").append(box_pos + "-" + box_pos_item);
		sb.append("&materialId=").append(box_code);
		sb.append("&materialCount=").append(box_count);
		sb.append("&materialSpec=1");

		Message msg = Message.obtain();
		msg.arg1 = ZKCmd.ARG_REQUEST_ADD_BOX;
		msg.what = ZKCmd.HAND_REQUEST_DATA;
		msg.obj = sb.toString();
		handler.sendMessage(msg);

	}

	/**自动填充查询的到箱子包含数量**/
	private void getBoxNameCount()
	{
		Thread thread=new Thread()
		{
			@Override
			public void run()
			{
				StringBuffer sb = new StringBuffer();
				String code = material_box_code.getText().toString();
				if (!"".equals(code) && code.length() == 11)
				{
					sb.append("t=" + ZKCmd.REQ_GET_MATERIAL_NAME);// 获得名称
					sb.append("&materialId=").append(code);

					String session = CommUtil.getShare(activity).getString(ConfigUtil.USER_SESSION, "");
					sb.append("&session=").append(session);

					String res = HttpGetPostUtil.sendHttpPost(ConstantUtil.HTTP_SERVER_URL, sb.toString());

					Message msg = Message.obtain();
					msg.what = ZKCmd.HAND_GET_MATERIAL_NAME;
					msg.obj = res;
					mHandler.sendMessage(msg);
				}
			}
		};
		thread.start();
		

	}

	private void setMaterialVal(String res)
	{
		if (!"".equals(res))
		{
			try
			{
				LogUtil.i(TAG, "根据物资码获得信息：" + res);
				JSONArray jsonArr = new JSONObject(res).getJSONArray("result");
				if (null != jsonArr && jsonArr.length() > 0)
				{
					JSONObject json = jsonArr.getJSONObject(0);
					String materialName = json.optString("materialName");
					String materialSpecDescribe = json.optString("materialSpecDescribe", "");
//					String isInBox = json.optString("isInBox", "");
//					String materialType = json.optString("materialType", "");
//					String materialSpec = json.optString("materialSpec", "");
//					String unit = json.optString("materialUnit", "");
//					String group = json.optString("materialGroup", "");
					material_box_name.setText(materialName);
					material_box_spec.setText(materialSpecDescribe);

				}
			} catch (Exception e)
			{
				LogUtil.e(TAG, "解析获得名称数据异常：" + e.getMessage());
			}
		}
	}

	/**释放该单例对象**/
	public static void clearWork()
	{
		if (null != timeDialog && timeDialog.isShowing())
		{
			timeDialog.dismiss();
			timeDialog = null;
		}
		work = null;
	}
}

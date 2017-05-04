package com.zk.rfid.map.dao;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
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

import com.zk.rfid.R;
import com.zk.rfid.main.util.ConfigUtil;
import com.zk.rfid.util.CommUtil;
import com.zk.rfid.util.ConstantUtil;
import com.zk.rfid.util.HttpGetPostUtil;
import com.zk.rfid.util.LogUtil;
import com.zk.rfid.util.ZKCmd;

/**
 * @Description: 动态增加物资的view
 * @date:2016-6-23下午4:36:11
 * @author: ldl
 */
public class MaterialViewDao
{
	private static final String TAG = "MaterialView";
	private Activity activity;
	private Handler handler;

	private Handler mHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case ZKCmd.HAND_GET_MATERIAL_NAME:

				setMaterialVal(msg.obj + "");

				break;
			case ZKCmd.HAND_MAP_ONCLICK:

				String val = msg.obj + "";// 平面图位置
				if (null != storage_pos)
				{
					String array[] = val.split("_");
					pos_full.setText(array[0]);
					LogUtil.i(TAG, "位置码:" + array[0]);
					storage_pos.setText(array[1]);
				}
				break;
			default:
				break;
			}
		};
	};

	public MaterialViewDao(Activity activity, Handler handler)
	{
		this.activity = activity;
		this.handler = handler;
	}

	/** 物资新增 **/
	private EditText material_id, material_name, material_specDescribe, loopCount_edit, add_unit_edit, material_group,
			storage_pos, pos_item_edit, materialCount, spec_edit;
	private EditText plateNumberEdit, attachment;
	private TextView pos_full;// 位置信息的完整码
	private RadioGroup inboxRadioGroup, qualityRadioGroup, consumeRadioGroup,isBoxRadioGroup;
	private ImageView select_imgview, select_spec_imgview;
	private LinearLayout add_unit_lin, spec_lin;
	private Button delete_btn;
	private RadioButton radioInBoxYes, radioInBoxNo, radioNotConsume, radioConsume;

	private View view1;

	public View getView(final LinearLayout totalView, final List<View> viewList)
	{
		view1 = LayoutInflater.from(activity).inflate(R.layout.addmaterial_item, null);
		getViewById(view1);
		setOnClickListen();

		totalView.addView(view1);
		viewList.add(view1);
		if (viewList.size() == 1)
		{
			delete_btn.setVisibility(View.INVISIBLE);
		}
		delete_btn.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				totalView.removeView(view1);
				viewList.remove(view1);
			}
		});
		return view1;
	}

	public void getViewById(View view1)
	{
		material_id = (EditText) view1.findViewById(R.id.material_id);
		material_name = (EditText) view1.findViewById(R.id.material_name);
		material_specDescribe = (EditText) view1.findViewById(R.id.material_specDescribe);
		loopCount_edit = (EditText) view1.findViewById(R.id.loopCount_edit);
		add_unit_edit = (EditText) view1.findViewById(R.id.add_unit_edit);
		storage_pos = (EditText) view1.findViewById(R.id.storage_pos);
		pos_full = (TextView) view1.findViewById(R.id.pos_full);
		materialCount = (EditText) view1.findViewById(R.id.materialCount);
		material_group = (EditText) view1.findViewById(R.id.material_group);
		spec_edit = (EditText) view1.findViewById(R.id.spec_edit);
		plateNumberEdit = (EditText) view1.findViewById(R.id.plateNumberEdit);
		attachment = (EditText) view1.findViewById(R.id.attachment);
		pos_item_edit = (EditText) view1.findViewById(R.id.pos_item_edit);

		inboxRadioGroup = (RadioGroup) view1.findViewById(R.id.inboxRadioGroup);
		qualityRadioGroup = (RadioGroup) view1.findViewById(R.id.qualityRadioGroup);
		consumeRadioGroup = (RadioGroup) view1.findViewById(R.id.consumeRadioGroup);
		isBoxRadioGroup   = (RadioGroup) view1.findViewById(R.id.isBoxRadioGroup);

		select_imgview = (ImageView) view1.findViewById(R.id.select_imgview);
		select_spec_imgview = (ImageView) view1.findViewById(R.id.select_spec_imgview);

		delete_btn = (Button) view1.findViewById(R.id.delete_btn);
		add_unit_lin = (LinearLayout) view1.findViewById(R.id.add_unit_lin);
		spec_lin = (LinearLayout) view1.findViewById(R.id.spec_lin);

		radioInBoxYes = (RadioButton) view1.findViewById(R.id.radioInBoxYes);
		radioInBoxNo = (RadioButton) view1.findViewById(R.id.radioInBoxNo);
		radioNotConsume = (RadioButton) view1.findViewById(R.id.radioNotConsume);
		radioConsume = (RadioButton) view1.findViewById(R.id.radioConsume);
	}

	public void setOnClickListen()
	{
		//List<String> list = ConfigUtil.getSpec(activity);
//		if (null != list && list.size() > 0)
//		{
//			spec_edit.setText(list.get(0));// 默认第一个
//		}
		spec_edit.setText("常规");
		select_imgview.setOnClickListener(listen);
		select_spec_imgview.setOnClickListener(listen);
		material_id.addTextChangedListener(textWatcher);

		storage_pos.setOnTouchListener(new View.OnTouchListener()
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
	}

	private TextWatcher textWatcher = new TextWatcher()
	{

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count)
		{
			if (!"".equals(s.toString()) && s.toString().length() == 11)
			{
				getNameByThread();
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after)
		{
			if (!"".equals(s.toString()) && s.toString().length() == 11)
			{
				getNameByThread();
			}
		}

		@Override
		public void afterTextChanged(Editable s)
		{
			if (!"".equals(s.toString()) && s.toString().length() == 11)
			{
				getNameByThread();
			}
		}
	};

	/**异步线程通过物资编号获得物资的信息**/
	private void getNameByThread()
	{
		String code = material_id.getText().toString();
		if (!"".equals(code) && code.length() == 11)
		{
			Thread thread = new Thread()
			{
				public void run()
				{
					getMaterialInfo();
				};
			};
			thread.start();
		}else
		{
			setViewEnable(true);
		}
	}

	private OnClickListener listen = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			switch (v.getId())
			{
			case R.id.select_imgview:

				CommUtil.initOptionListWedget(activity, add_unit_lin, add_unit_edit, ConfigUtil.getMaterialUnit());

				break;

			case R.id.select_spec_imgview:

				CommUtil.initOptionListWedget(activity, spec_lin, spec_edit, ConfigUtil.getSpec(activity));

				break;

			case R.id.delete_btn:

				break;

			default:
				break;
			}
		}
	};

	/** 新增物资提交数据,获取界面的每一个item，组状成json数组 **/
	public JSONObject getSubmitDataByView(View view)
	{
		getViewById(view);// 先获得控件

		String code = material_id.getText().toString();
		String name = material_name.getText().toString();
		String pos_code = pos_full.getText().toString();

		String group = material_group.getText().toString();

		if ("".equals(code) || code.length() != 11)
		{
			CommUtil.toastShow("物资编号不能为空且为11位", activity);
			return null;
		}
		if ("".equals(name))
		{
			CommUtil.toastShow("物资名称不能为空", activity);
			return null;
		}
		String count = loopCount_edit.getText().toString();
		String unit = add_unit_edit.getText().toString();
		String spec = spec_edit.getText().toString();
		String specDescribe = material_specDescribe.getText().toString();
		String posItemVal = pos_item_edit.getText().toString();

		int co = CommUtil.getParseIntVal(count);
		if ("".equals(count) || 0 == co)
		{
			CommUtil.toastShow("数量不为空且不为0", activity);
			return null;
		}
		count = "" + co;
		if ("".equals(unit))
		{
			CommUtil.toastShow("单位不能为空", activity);
			return null;
		}
		if ("".equals(specDescribe))
		{
			CommUtil.toastShow("规格描述不能为空", activity);
			return null;
		}

		if ("".equals(spec))
		{
			CommUtil.toastShow("规格不能为空", activity);
			return null;
		}

		int item = CommUtil.getParseIntVal(posItemVal);
		if ("".equals(posItemVal) || 0 == item)
		{
			CommUtil.toastShow("位号不能为空且不为0", activity);
			return null;
		}
		posItemVal = "" + item;

		if ("".equals(pos_code) || null == pos_code)
		{
			CommUtil.toastShow("存放位置不能为空", activity);
			return null;
		}

		String inBox = "1";
		if (R.id.radioInBoxNo == inboxRadioGroup.getCheckedRadioButtonId())
		{
			inBox = "0";// 不是箱装
		}
		
		String isBox="1";
		if(R.id.radioIsBoxNo==isBoxRadioGroup.getCheckedRadioButtonId())
		{
			isBox="0";
		}

		String consume = ConfigUtil.NOTCONSUMABLES;// 非消耗
		if (R.id.radioConsume == consumeRadioGroup.getCheckedRadioButtonId())
		{
			consume = ConfigUtil.CONSUMABLES;// 消耗
		}

		String quality = "1";// 良好,质量状况
		if (R.id.radioBad == qualityRadioGroup.getCheckedRadioButtonId())
		{
			quality = "0";// 差,质量状况
		}

		String includeCount = materialCount.getText().toString();
		int incount=CommUtil.getParseIntVal(includeCount);
		if (0==incount)
		{
			incount = 1;// 没有填的情况下，设为1，表示已经具体到物件，有值，则表示是袋或者包或者盒子内含的数
		}
		
		includeCount=incount+"";
		
		int inc=CommUtil.getParseIntVal(includeCount);
		if(inc>2048)
		{
			CommUtil.toastShow("内含数量最大不能超过2048", activity);
			return null;
		}

		String plateNumber = plateNumberEdit.getText().toString();
		String attachmentText = attachment.getText().toString();

		// 这里对每条记录拼装json返回
		JSONObject json = new JSONObject();
		try
		{
			json.put("materialId", code); // 物资编号
			json.put("materialName", CommUtil.getURLEncoder(name));// (有特殊字箱需要中文转码)物资名称
			json.put("materialSpecDescribe", CommUtil.getURLEncoder(specDescribe));
			json.put("loopCount", count);// 数量
			json.put("materialUnit", unit);// 物资单位
			json.put("isInBox", inBox);// 0非箱装，1代表箱装
			json.put("materialGroup", group);// 物料组

			json.put("isBox", isBox); // 箱子还是非箱子
			json.put("materialType", consume);// 性质，0消耗性，1固定资产
			json.put("materialQuality", quality); // 质量
			json.put("plateNumber", plateNumber); // 车票号
			json.put("materialRemarks", attachmentText); // 附件资料

			json.put("materialSpec", ConfigUtil.getSpecCodeByName(activity, spec));// 规格

			json.put("materialCount", includeCount);// 如果是包或者袋装需要记录内部含数量才填
			json.put("positionCode", pos_code + "-" + posItemVal); // 存放位置，加上位号
		} catch (JSONException e)
		{
			e.printStackTrace();
		}

		return json;
	}

	/**异步获得物资的相关信息**/
	private void getMaterialInfo()
	{
		String code = material_id.getText().toString();

		StringBuffer sb = new StringBuffer();
		sb.append("t=" + ZKCmd.REQ_GET_MATERIAL_NAME);
		sb.append("&materialId=").append(code);
		String session = CommUtil.getShare(activity).getString(ConfigUtil.USER_SESSION, "");
		sb.append("&session=" + session);
		String res = HttpGetPostUtil.sendHttpPost(ConstantUtil.HTTP_SERVER_URL, sb.toString());
		Message msg = Message.obtain();
		msg.what = ZKCmd.HAND_GET_MATERIAL_NAME;
		msg.obj = res;
		mHandler.sendMessage(msg);

	}

	/**自动填充异步获得的物资信息**/
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
					String isInBox = json.optString("isInBox", "");
					String materialType = json.optString("materialType", "");
					// String materialSpec = json.optString("materialSpec", "");
					String unit = json.optString("materialUnit", "");
					String group = json.optString("materialGroup", "");

					material_name.setText(materialName);
					material_specDescribe.setText(materialSpecDescribe);

					if ("1".equals(isInBox))
					{
						radioInBoxYes.setChecked(true);
						radioInBoxNo.setChecked(false);
					} else
					{
						radioInBoxYes.setChecked(false);
						radioInBoxNo.setChecked(true);
					}

					if ("1".equals(materialType))
					{
						radioNotConsume.setChecked(true);
						radioConsume.setChecked(false);
					} else
					{
						radioNotConsume.setChecked(false);
						radioConsume.setChecked(true);
					}

					// spec_edit.setText(ConfigUtil.getSpecNameByCode(activity,
					// materialSpec));
					add_unit_edit.setText(unit);
					material_group.setText(group);
					setViewEnable(false);
				} else
				{
					setViewEnable(true);
				}
			} catch (Exception e)
			{
				LogUtil.e(TAG, "解析获得名称数据异常：" + e.getMessage());
				setViewEnable(true);
			}
		} else
		{
			setViewEnable(true);
		}
	}

	/**部份信息自动填充后，不允许用户随意修改，防止与服务端的不一致**/
	private void setViewEnable(boolean flag)
	{
		if (flag)
		{
			material_name.setText("");
			material_specDescribe.setText("");
		}

		material_name.setEnabled(flag);
		material_name.setFocusable(flag);
		material_specDescribe.setEnabled(flag);
		material_specDescribe.setFocusable(flag);

		// select_spec_imgview.setEnabled(flag);
		// select_spec_imgview.setFocusable(flag);
		material_group.setEnabled(flag);
		material_group.setFocusable(flag);

		radioConsume.setEnabled(flag);
		radioNotConsume.setEnabled(flag);
		radioInBoxNo.setEnabled(flag);
		radioInBoxYes.setEnabled(flag);
	}

}

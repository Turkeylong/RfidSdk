package com.zk.rfid.main.dao;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zk.rfid.R;
import com.zk.rfid.main.bean.EpcInfo;
import com.zk.rfid.util.CommUtil;

/**
 * @Description: 申请物资信息容器
 * @date:2016-5-14 下午11:21:30
 * @author: ldl
 */
public class ApplyMaterialView
{
	private static final String TAG = "ApplyMaterialView";
	private Activity activity;
	private Handler handler;// 主界面的handler

	public ApplyMaterialView(Activity act, Handler handler)
	{
		this.handler = handler;
		activity = act;
	}

	private CheckBox orderCheck;
	private TextView nameText;
	private TextView countText;
	private TextView checkText;
	private TextView unitText;
	
	private Button reduce_mat_btn;
	private Button add_mat_btn;
	private EditText apply_count;
	private LinearLayout applyCountLin;

	// 以下是隐藏值，保存在view中
	private TextView materialIdText;
//	private TextView deliveryOrderNumber;
//	private TextView materialId;
//	private TextView materialType;
//	private TextView backCount;
//	private TextView lendCount;

	public View getView(EpcInfo info, final List<View> checkView)
	{
		final View paramView = LayoutInflater.from(activity).inflate(R.layout.apply_material_item, null);
		getViewById(paramView);
		setValueListen(info);

		checkView.add(paramView);

		orderCheck.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				if (!isChecked)
				{
					checkView.remove(paramView);
				} else
				{
					checkView.add(paramView);
				}
			}
		});

		return paramView;
	}

//	public View getViewByData88(LendBackBean bean, final List<View> checkView)
//	{
//		final View paramView = LayoutInflater.from(activity).inflate(R.layout.apply_material_item, null);
//		getViewById(paramView);
//		setOnListen();
//		
//		apply_count.setText(bean.getApplyCount());
//		nameText.setText(bean.getMaterialName());
//		materialIdText.setText(bean.getMaterialId());
//		unitText.setText(bean.getMaterialUnit());
//		
//		checkView.add(paramView);
//
//		orderCheck.setOnCheckedChangeListener(new OnCheckedChangeListener()
//		{
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
//			{
//				if (!isChecked)
//				{
//					checkView.remove(paramView);
//				} else
//				{
//					checkView.add(paramView);
//				}
//			}
//		});
//
//		return paramView;
//	}

	private void setValueListen(EpcInfo info)
	{
		nameText.setText(info.getMaterialName()+info.getMaterialSpecDescribe());
		materialIdText.setText(info.getMaterialId());
		unitText.setText(info.getMaterialUnit());
		setOnListen();
	}

	private void setOnListen()
	{
		add_mat_btn.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				String co = apply_count.getText().toString();
				if (!"".equals(co))
				{
					int val = Integer.parseInt(co);
					apply_count.setText((val + 1) + "");
				} else
				{
					apply_count.setText("1");
				}
			}
		});

		reduce_mat_btn.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				String co = apply_count.getText().toString();
				if (!"".equals(co))
				{
					int val = Integer.parseInt(co);
					if (val <= 0)
					{
						apply_count.setText("0");
					} else
					{
						apply_count.setText((val - 1) + "");
					}
				} else
				{
					apply_count.setText("0");
				}
			}
		});
	}

	private void getViewById(View paramView)
	{
		orderCheck = (CheckBox) paramView.findViewById(R.id.orderCheck);
		orderCheck.setVisibility(View.VISIBLE);
		nameText  = (TextView) paramView.findViewById(R.id.nameText);
		countText = (TextView) paramView.findViewById(R.id.countText);
		checkText = (TextView) paramView.findViewById(R.id.checkText);
		unitText = (TextView) paramView.findViewById(R.id.unitText);
		materialIdText = (TextView) paramView.findViewById(R.id.materialIdText);
		
		countText.setVisibility(View.GONE);
		checkText.setVisibility(View.GONE);
		reduce_mat_btn = (Button) paramView.findViewById(R.id.reduce_mat_btn);

		add_mat_btn = (Button) paramView.findViewById(R.id.add_mat_btn);
		apply_count = (EditText) paramView.findViewById(R.id.apply_count);
		applyCountLin = (LinearLayout) paramView.findViewById(R.id.applyCountLin);
		applyCountLin.setVisibility(View.VISIBLE);

	}

	/**遍历申请数据，所有实填的值**/
	public JSONObject getSubmitDataByView(View view)
	{
		getViewById(view);// 先获得控件

		String count = apply_count.getText().toString();
		int co=CommUtil.getParseIntVal(count);
		
		if ("".equals(count) || 0==co)
		{
			CommUtil.toastShow("数量不能为空且不为0", activity);
			return null;
		}
		count=""+co;
		// 这里对每条记录拼装json返回
		JSONObject json = new JSONObject();
		try
		{
			json.put("appliedCount", count);
			json.put("materialId", materialIdText.getText().toString());
			
		} catch (JSONException e)
		{
			json = null;
			e.printStackTrace();
		}
		return json;
	}

}

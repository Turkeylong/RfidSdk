package com.zk.rfid.main.adapter;

import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zk.rfid.R;
import com.zk.rfid.main.bean.EpcInfo;
import com.zk.rfid.main.util.ConfigUtil;

/**
 * @Description: 申请领取详细表
 * @date:2016-7-18下午3:40:42
 * @author: ldl
 */
public class LendDetailAdapter extends BaseAdapter
{
	private static final String TAG = "LendDetailAdapter";

	private Activity activity;
	private List<EpcInfo> listData;
	private HashMap<Integer, View> map = new HashMap<Integer, View>();
	private Handler handler;
	private boolean returnFlag;
	private String status;

	public LendDetailAdapter(Activity activity, Handler handler, List<EpcInfo> listData)
	{
		this.activity = activity;
		this.handler = handler;
		this.listData = listData;
	}

	static class ViewHolder
	{
		TextView orderText, numberText, nameText, unit, pre_count, apply_count, returnCountText,materialNumberText;
		LinearLayout returnCountLin, lendCountLin;
		// Button reduce_mat_btn,add_mat_btn;
		EditText return_count, lend_count;
	}

	@Override
	public int getCount()
	{
		if (null != listData)
		{
			return listData.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position)
	{
		if (null != listData)
		{
			return listData.get(position);
		}

		return null;
	}

	@Override
	public long getItemId(int position)
	{
		return 0;
	}

	@Override
	public View getView(int position, View paramView, ViewGroup parent)
	{
		ViewHolder holder = null;
		if (paramView== null)
		{
			paramView = LayoutInflater.from(activity).inflate(R.layout.lenddetail_main_table_middle, null);
			map.put(position, paramView);

			holder = new ViewHolder();
			holder.orderText = (TextView) paramView.findViewById(R.id.orderText);
			holder.numberText = (TextView) paramView.findViewById(R.id.numberText);
			holder.nameText = (TextView) paramView.findViewById(R.id.nameText);
			holder.unit = (TextView) paramView.findViewById(R.id.unit);
			holder.materialNumberText=(TextView) paramView.findViewById(R.id.materialNumberText);

			holder.apply_count = (TextView) paramView.findViewById(R.id.apply_count);
			holder.pre_count = (TextView) paramView.findViewById(R.id.pre_count);
			holder.pre_count.setVisibility(View.GONE);

			holder.returnCountText = (TextView) paramView.findViewById(R.id.returnCountText);

			holder.returnCountLin = (LinearLayout) paramView.findViewById(R.id.returnCountLin);
			holder.lendCountLin = (LinearLayout) paramView.findViewById(R.id.lendCountLin);
			holder.lendCountLin.setVisibility(View.VISIBLE);

			if (isReturnFlag())
			{
				holder.returnCountText.setVisibility(View.GONE);
				holder.returnCountLin.setVisibility(View.VISIBLE);
			} else
			{
				holder.returnCountText.setVisibility(View.VISIBLE);
				holder.returnCountLin.setVisibility(View.GONE);
			}

			holder.return_count = (EditText) paramView.findViewById(R.id.return_count);
			holder.lend_count = (EditText) paramView.findViewById(R.id.lend_count);
			paramView.setTag(holder);
		} else
		{
			paramView = map.get(position);
			holder = (ViewHolder) paramView.getTag();
		}
		if (paramView != null)
		{
			final EpcInfo bean = listData.get(position);

			holder.orderText.setText(position + 1 + "");
			holder.numberText.setText(bean.getMaterialId());
			holder.nameText.setText(bean.getMaterialName()+bean.getMaterialSpecDescribe());
			holder.unit.setText(bean.getMaterialUnit() + "");
			holder.apply_count.setText(bean.getApplyCount() + "");
			holder.materialNumberText.setText(bean.getAppliedMaterialNumber());

			String lCount = bean.getLendCount() + "";
			if (!"".equals(lCount) && !"0".equals(lCount))
			{
				holder.lend_count.setText(lCount);
			} else
			{
				holder.lend_count.setText(bean.getApplyCount());// 填写申请的数量
			}

			String bcCount = bean.getReturnCount()+ "";
			if (!"".equals(bcCount) && !"0".equals(bcCount))
			{
				holder.return_count.setText(bcCount);
			} else
			{
				holder.return_count.setText("");
			}

			if (!isReturnFlag())
			{
				if (!"".equals(bcCount) && !"0".equals(bcCount))
				{
					holder.returnCountText.setText(bcCount);
				} else
				{
					holder.returnCountText.setText("");
				}
			}

			if (!ConfigUtil.ADUIT_PASS.equals(getStatus()))
			{
				holder.lend_count.setEnabled(false);// 非已通过情况下，这个不可以编辑
				holder.lend_count.setBackground(null);
			}
		}

		return paramView;
	}

	/**获得借领详细中物资的记录，以列表显示，根据是借或者还获得实填数据**/
	public String getMaterialJson(int type)
	{
		String res = "";
		int len = getCount();
		JSONArray jsonArray = new JSONArray();
		try
		{
			JSONObject json=null;
					
			for(int i=0;i<len;i++)
			{
				json=new JSONObject();
				View paramView=map.get(i);	
				String count="";
				
				TextView materialNumberText = (TextView) paramView.findViewById(R.id.materialNumberText);
				if(1==type)
				{
					//领
					EditText lend_count = (EditText) paramView.findViewById(R.id.lend_count);
					count=lend_count.getText().toString();
					
					json.put("appliedMaterialNumber", materialNumberText.getText().toString());
					json.put("lendCount",  count);
				}
				if(2==type)
				{
					//还
					EditText return_count = (EditText) paramView.findViewById(R.id.return_count);
					count=return_count.getText().toString();
					json.put("appliedMaterialNumber", materialNumberText.getText().toString());
					json.put("returnCount",  count);
				}
			
				jsonArray.put(json);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		res=jsonArray.toString();
		return res;
	}
	

	public boolean isReturnFlag()
	{
		return returnFlag;
	}

	public void setReturnFlag(boolean returnFlag)
	{
		this.returnFlag = returnFlag;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

}

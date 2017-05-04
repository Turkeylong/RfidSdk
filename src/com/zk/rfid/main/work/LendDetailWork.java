package com.zk.rfid.main.work;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.zk.rfid.R;
import com.zk.rfid.main.adapter.LendDetailAdapter;
import com.zk.rfid.main.bean.EpcInfo;
import com.zk.rfid.main.util.ConfigUtil;
import com.zk.rfid.util.CommUtil;
import com.zk.rfid.util.LogUtil;
import com.zk.rfid.util.StringUtil;
import com.zk.rfid.util.SystemUtil;
import com.zk.rfid.util.ZKCmd;

/**
 * @Description: 借领详细操作页
 * @date:2016-6-2上午10:57:12
 * @author: ldl
 */
public class LendDetailWork
{
	private static final String TAG = "LendDetailWork";
	private Activity activity;
	private Handler handler;
	private static LendDetailWork work;
	private static final int SURE_LEND = 1, RETURN = 2;
	private String appliedOrderNumber;
	private EpcInfo epcInfo;

	private LendDetailAdapter detailAdapter;
	private ListView lend_detail_listview;

	// 提交操作按钮
	private Button lendBtn, backBtn, agreeBtn, disagreeBtn;

	// 头和尾信息
	private TextView department, demand_date, notice_no, warehouse, requisition;
	private TextView department_Leader, requisition_person, requisition_time, return_person, return_date,
			hand_person, remarks_text, auditRemarks,purpose_text;
	private EditText repairOrderEdit;

	private LinearLayout lendCountLin, backCountLin;

	private LendDetailWork(Activity act)
	{
		this.activity = act;
	}

	public static LendDetailWork getInstance(Activity act)
	{
		if (null == work)
		{
			work = new LendDetailWork(act);
		}
		return work;
	}

	public void initView(Handler handler)
	{
		this.handler = handler;
		// 头和尾
		demand_date = (TextView) activity.findViewById(R.id.demand_date);
		notice_no = (TextView) activity.findViewById(R.id.notice_no);
		department_Leader = (TextView) activity.findViewById(R.id.department_Leader);
		requisition_person = (TextView) activity.findViewById(R.id.requisition_person);
		requisition_time = (TextView) activity.findViewById(R.id.requisition_time);
		return_person  = (TextView) activity.findViewById(R.id.return_person);
		return_date    = (TextView) activity.findViewById(R.id.return_date);		
		hand_person    = (TextView) activity.findViewById(R.id.hand_person);
		purpose_text   = (TextView) activity.findViewById(R.id.purpose_text);
		auditRemarks   = (TextView) activity.findViewById(R.id.auditRemarks);
		repairOrderEdit= (EditText) activity.findViewById(R.id.repairOrderEdit);
		// 头和尾
		lend_detail_listview = (ListView) activity.findViewById(R.id.lend_detail_listview);

		lendBtn = (Button) activity.findViewById(R.id.lendBtn);
		backBtn = (Button) activity.findViewById(R.id.backBtn);
		disagreeBtn = (Button) activity.findViewById(R.id.disagreeBtn);
		agreeBtn = (Button) activity.findViewById(R.id.agreeBtn);

		lendBtn.setOnClickListener(listener);
		backBtn.setOnClickListener(listener);
		disagreeBtn.setOnClickListener(listener);
		agreeBtn.setOnClickListener(listener);

	}

	private OnClickListener listener = new View.OnClickListener()
	{
		@Override
		public void onClick(View key)
		{
			switch (key.getId())
			{

			case R.id.lendBtn:
				if(SystemUtil.getPower(SystemUtil.MATERIAL_OUT))
				{
					lendOrBack(ZKCmd.ARG_REQUEST_LEND, SURE_LEND);
				}
				else
				{
					CommUtil.toastShow("您没有物资领用权限，请联系管理员！", activity);
				}
				
				break;

			case R.id.backBtn:
				if(SystemUtil.getPower(SystemUtil.MATERIAL_BACK))
				{
					lendOrBack(ZKCmd.ARG_REQUEST_RETURN, RETURN);
				}
				else
				{
					CommUtil.toastShow("您没有物资归还权限，请联系管理员！", activity);
				}
				

				break;

			case R.id.disagreeBtn:
				if(SystemUtil.getPower(SystemUtil.ADMIN))
				{
					auditApply(false);
				}
				else
				{
					CommUtil.toastShow("您没有审核权限，请联系管理员！", activity);
				}
				
				break;

			case R.id.agreeBtn:
				if(SystemUtil.getPower(SystemUtil.ADMIN))
				{
					auditApply(true);
				}
				else
				{
					CommUtil.toastShow("您没有审核权限，请联系管理员！", activity);
				}
				
				break;

			default:
				break;
			}
		}
	};

	// private List<View> lendListView = null;

	/** 借领详细情况,20160705下午更改为显示表格方式,20160902统一命名后修正 **/
	public void showDataDetail(String jsonStr, EpcInfo info)
	{
		setBtnByState(info.getAppliedStatus());// 根据状态控制显示
		appliedOrderNumber = info.getAppliedOrderNumber();// 先将ID保存,通知单号
		epcInfo = info;
		boolean returnFlag = false;
		if (ConfigUtil.APPLY_HAVE_USE.equals(info.getAppliedStatus()))
		{
			returnFlag = true;
		}
		List<EpcInfo> lendReturn = new ArrayList<EpcInfo>();
		try
		{
			LogUtil.i(TAG, "返回的数据：" + jsonStr);
			JSONObject jsonObj = new JSONObject(jsonStr).getJSONObject("result");
			JSONObject json = null;

			JSONArray jsonArray = jsonObj.getJSONArray("info");
			if (jsonArray.length() > 0)
			{
				int len = jsonArray.length();

				EpcInfo bean = null;
				StringBuffer idSb=new StringBuffer();
				
				for (int i = 0; i < len; i++)
				{
					json = jsonArray.getJSONObject(i);
					String materialId = json.optString("materialId", "");
					String materialName = json.optString("materialName", "");
					String describe = json.optString("materialSpecDescribe", "");
					String returnCount = json.optString("returnCount", "");
					String lendCount = json.optString("lendCount", "0");
					String appliedCount = json.optString("appliedCount", "0");
					String unit = json.optString("materialUnit", "");
					String appliedMaterialNumber=json.optString("appliedMaterialNumber", "");


					bean = new EpcInfo();
					bean.setMaterialId(materialId);
					bean.setMaterialName(materialName);
					bean.setMaterialSpecDescribe(describe);
					bean.setAppliedMaterialNumber(appliedMaterialNumber);
					bean.setReturnCount(returnCount);
					bean.setLendCount(lendCount);
					bean.setApplyCount(appliedCount);
					bean.setMaterialUnit(unit);
					lendReturn.add(bean);

					idSb.append(materialId).append(",");//这是作为扫描时，根据这条记录把扫描到的结果优先排前显示
				}
				//idSb.append("10000727228,11000055707,10001353543,10000727222").append(",");
				Editor editor=CommUtil.getShare(activity).edit();
				editor.putString(ConfigUtil.PRIOR_MATERIAL_STRING, idSb.toString());
				editor.commit();
				
				String time = jsonObj.optString("appliedDate", "");
				demand_date.setText(StringUtil.formatDateByString(time));
				notice_no.setText(appliedOrderNumber);
				department_Leader.setText(jsonObj.optString("auditPersonName", ""));
		
				String lenP = jsonObj.optString("appliedPersonName", "");
				requisition_person.setText(lenP);
				
				String repairOrderNumber=jsonObj.optString("repairOrderNumber", "");
				repairOrderEdit.setText(repairOrderNumber);

				requisition_time.setText(StringUtil.formatDateByString(jsonObj.optString("lendDate", "")));
				
				String returnPerson = jsonObj.optString("returnPersonName", "");
				return_person.setText(returnPerson);
				
				return_date.setText(StringUtil.formatDateByString(jsonObj.optString("returnDate", "")));

				String handP = jsonObj.optString("lendHandlePersonName", "");		
				hand_person.setText(handP);
				purpose_text.setText(epcInfo.getAppliedPurpose());
				auditRemarks.setText( jsonObj.optString("auditRemarks", ""));
				detailAdapter = null;
				detailAdapter = new LendDetailAdapter(activity, handler, lendReturn);
				detailAdapter.setReturnFlag(returnFlag);
				detailAdapter.setStatus(epcInfo.getAppliedStatus());
				lend_detail_listview.setAdapter(detailAdapter);
				CommUtil.setListViewHeight(lend_detail_listview);
			}

		} catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	/**领出或者归还操作**/
	private void lendOrBack(int arg1, int type)
	{
		
		String jarr = detailAdapter.getMaterialJson(type);

		StringBuffer sb = new StringBuffer();
		if (type == SURE_LEND)
		{
			String repairOrder=repairOrderEdit.getText().toString();
			sb.append("t=" + ZKCmd.REQ_LEND_CONFIRM); // 领
			sb.append("&repairOrderNumber").append(repairOrder);			
			sb.append("&appliedOrderNumber=").append(epcInfo.getAppliedOrderNumber());
			sb.append("&lendPersonName=").append(epcInfo.getAppliedPersonName()); // 领取人。也是申请人
			sb.append("&processStatus=").append(ConfigUtil.APPLY_HAVE_USE);//已领
		}
		if (type == RETURN)
		{
			sb.append("t=" + ZKCmd.REQ_BACK_CONFIRM); // 还
			sb.append("&appliedOrderNumber=").append(epcInfo.getAppliedOrderNumber());
			sb.append("&processStatus=").append(ConfigUtil.APPLY_HAVE_RETURN);//已还
			sb.append("&returnPersonName=").append(epcInfo.getAppliedPersonName());//已还
			
		}
	
		sb.append("&materialList=").append(jarr);
		//经办人通过上传的session，服务器自动去获得

		LogUtil.i(TAG, "提交的借或者还数据:" + sb.toString());
		CommUtil.executeCommTask(activity, handler, ZKCmd.HAND_RESPONSE_DATA, arg1, 0, sb.toString());

	}

	/** 是否同意,只有审核权的领导，并且是管理员才可以操作 **/
	private void auditApply(boolean agree)
	{
		Message msg = Message.obtain();
		msg.what = ZKCmd.HAND_REQUEST_DATA;
		msg.arg1 = ZKCmd.ARG_AUDIT_APPLY;// 申请
		if (agree)
		{
			msg.arg2 = CommUtil.getParseIntVal(ConfigUtil.ADUIT_PASS);
		} else
		{
			msg.arg2 = CommUtil.getParseIntVal(ConfigUtil.ADUIT_NOT_PASS);
		}
		msg.obj = appliedOrderNumber;
		handler.sendMessage(msg);
	}

	private void setBtnByState(String state)
	{
		boolean auditPower = CommUtil.getShare(activity).getBoolean(ConfigUtil.USER_POWER_AUDIT, false);
		if ((ConfigUtil.WAIT_ADUIT.equals(state) || ConfigUtil.ADUIT_NOT_PASS.equals(state)) && auditPower)
		{
			agreeBtn.setVisibility(View.VISIBLE);// 有审核权的才能审
			disagreeBtn.setVisibility(View.VISIBLE);
		}

		else
		{
			String userType = CommUtil.getShare(activity).getString(ConfigUtil.USER_TYPE, "");
			if (ConfigUtil.USER_MANAGER.equals(userType))
			{
				if (ConfigUtil.APPLY_HAVE_USE.equals(state))
				{
					backBtn.setVisibility(View.VISIBLE);// 管理员可以操作退回
				}
				if (ConfigUtil.ADUIT_PASS.equals(state))
				{
					lendBtn.setVisibility(View.VISIBLE);// 管理员可以操作确认取出
				}
			} else
			{
				// 员工只能查看，不能有其他操作
			}
		}

	}

	public static void clearWork()
	{
		work = null;
	}
}

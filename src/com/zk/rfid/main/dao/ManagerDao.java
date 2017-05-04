package com.zk.rfid.main.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Handler;

import com.rfid.sdk.rfidclass.TAG_TID;
import com.zk.rfid.R;
import com.zk.rfid.main.bean.EpcInfo;
import com.zk.rfid.main.util.ConfigUtil;
import com.zk.rfid.util.BitmapUtil;
import com.zk.rfid.util.CommUtil;
import com.zk.rfid.util.ConstantUtil;
import com.zk.rfid.util.LogUtil;
import com.zk.rfid.util.SystemUtil;
import com.zk.rfid.util.ZKCmd;

/**
 * @Description: 数据交互层
 * @date:2016-5-31下午3:20:05
 * @author: ldl
 */
public class ManagerDao
{

	private static final String TAG = "ManagerDao";
	private Activity activity;
	private Handler handler;

	public ManagerDao(Activity activity, Handler handler)
	{
		this.handler = handler;
		this.activity = activity;
	}

	/** 获取借领记录数据 **/
	public void getLendData(String param)
	{
		CommUtil.executeCommTask(activity, handler, ZKCmd.HAND_RESPONSE_DATA, ZKCmd.ARG_REQUEST_LEND, 0, param);
	}

	/** 获取存储记录数据 **/
	public void getStorageData(String param)
	{
		CommUtil.executeCommTask(activity, handler, ZKCmd.HAND_RESPONSE_DATA, ZKCmd.ARG_REQUEST_STOAGE, 0, param);
	}

	/** 公用获得总数 **/
	public int getCount(String res)
	{
		int count = 0;
		try
		{
			JSONObject json = new JSONObject(res).getJSONObject("result");
			count = Integer.parseInt(json.optString("totalcount", "0"));
		} catch (Exception e)
		{
			LogUtil.e(TAG, "解析库存数据异常：" + e.getMessage());
		}
		return count;
	}

	/** 存储数据 **/
	public List<EpcInfo> getStorageList(String res)
	{
		List<EpcInfo> list = new ArrayList<EpcInfo>();
		EpcInfo info = null;
		try
		{
			//LogUtil.i(TAG, "接收数据：" + res);
			JSONObject json = new JSONObject(res).getJSONObject("result");
			JSONArray jsonArr = json.getJSONArray("materialList");
			String totalCount=json.optString("totalCount","0");
			
			JSONObject obj = null;
			int len = jsonArr.length();
			for (int i = 0; i < len; i++)
			{
				info = new EpcInfo();
				obj = jsonArr.getJSONObject(i);
				info = new EpcInfo();
		
				info.setMaterialCount(obj.optString("materialCount", ""));//箱子包含的数量
				info.setMaterialId(obj.optString("materialId", ""));
				info.setMaterialName(obj.optString("materialName", ""));
				info.setDeliveryOrderNumber(obj.optString("deliveryOrderNumber", ""));
				info.setEpcCode(obj.optString("epc_code", ""));
				info.setInDate(obj.optString("inDate", ""));
				info.setMaterialUnit(obj.optString("materialUnit", ""));// 单位
				info.setIsBox(obj.optString("isBox", ""));				
				info.setLocation(obj.optString("location", ""));
				info.setMaterialStatus(obj.optString("materialStatus", ""));
				info.setMaterialSpecDescribe(obj.optString("materialSpecDescribe", ""));
				info.setMaterialType(obj.optString("materialType", ""));
				info.setMaterialSpec(obj.optString("materialSpec", ""));
				info.setMaterialEmergency(obj.optString("materialEmergency", ""));
								
				info.setTotalCount(totalCount);
				
				//统计
				info.setSumCount(obj.optString("sumMaterialCount", "0"));
				info.setQuality(obj.optString("materialQuality", "1"));
				info.setPlateNumber(obj.optString("plateNumber", ""));
				info.setApprovalOrderNumber(obj.optString("approvalOrderNumber", ""));//验收单
				info.setAppliedOrderNumber(obj.optString("appliedOrderNumber", ""));//申请单
				info.setHandlePersonName(obj.optString("deliveryPersonName", ""));
				info.setAttachment(obj.optString("materialRemarks", ""));
				info.setSendCompany(obj.optString("sendCompany", ""));
				info.setMaterialTotalCount(obj.optString("materialTotalCount", ""));
				info.setArrivedDate(obj.optString("arrivedDate", ""));
				info.setInDate(obj.optString("inDate", ""));
				info.setLendDate(obj.optString("lendDate", ""));
				info.setLendPersonName(obj.optString("lendPersonName", ""));
				info.setLendHandPerson(obj.optString("lendHandlePersonName", ""));
				info.setReturnCount(obj.optString("returnCount", "0"));	
				info.setLendCount(obj.optString("lendCount", "0"));	
				list.add(info);
			}
		} catch (Exception e)
		{
			LogUtil.e(TAG, "解析库存数据异常：" + e.getMessage());
		}
		return list;
	}
	
	/**解析扫描得到的数据**/
	public List<EpcInfo> getScanList(String res)
	{
		List<EpcInfo> list = new ArrayList<EpcInfo>();
		EpcInfo info = null;
		try
		{
			LogUtil.e(TAG, "接收数据：" + res);
			JSONObject json = new JSONObject(res).getJSONObject("result");
			JSONArray jsonArr = json.getJSONArray("materialList");
			
			JSONObject obj = null;
			int len = jsonArr.length();
			for (int i = 0; i < len; i++)
			{
				info = new EpcInfo();
				obj = jsonArr.getJSONObject(i);
				info = new EpcInfo();	
				
				info.setLocation(obj.optString("location", ""));
				info.setMaterialSpec(obj.optString("materialSpec", ""));
				info.setMaterialName(obj.optString("materialName", ""));
				info.setEpcCode(obj.optString("epc_code", ""));
				info.setMaterialId(obj.optString("materialId", ""));
				info.setMaterialUnit(obj.optString("materialUnit", ""));
				info.setMaterialStatus(obj.optString("materialStatus", ""));// 状态
				info.setSerialNumber(obj.optString("materialSn", ""));//编号
				info.setMaterialSpecDescribe(obj.optString("materialSpecDescribe", ""));
				info.setMaterialSpec(obj.optString("materialSpec", ""));				
				info.setIsBox(obj.optString("isBox", ""));
				info.setMaterialCount(obj.optString("materialCount", ""));
				info.setPositionCode(obj.optString("positionCode", ""));
				info.setIsInBox(obj.optString("isInBox", ""));
				info.setOldCount((obj.optString("epcCodeMaterialCount", "0")));
				
				list.add(info);
			}
		} catch (Exception e)
		{
			LogUtil.e(TAG, "解析库存数据异常：" + e.getMessage());
		}
		return list;
	}
	
	
	/**解析需要修改标签的数据**/
	public List<EpcInfo> getNeedModifyDataList(String res)
	{
		List<EpcInfo> list = new ArrayList<EpcInfo>();
		EpcInfo info = null;
		try
		{
			LogUtil.e(TAG, "接收数据：" + res);
			JSONArray jsonArr  = new JSONObject(res).getJSONArray("result");
						
			JSONObject obj = null;
			int len = jsonArr.length();
			for (int i = 0; i < len; i++)
			{
				info = new EpcInfo();
				obj = jsonArr.getJSONObject(i);
				info = new EpcInfo();	
				
				info.setLocation(obj.optString("location", ""));
				info.setMaterialSpec(obj.optString("materialSpec", ""));
				info.setMaterialName(obj.optString("materialName", ""));
				info.setEpcCode(obj.optString("epc_code", ""));
				info.setMaterialId(obj.optString("materialId", ""));
					
				info.setSerialNumber(obj.optString("materialSn", ""));//编号
				info.setMaterialSpecDescribe(obj.optString("materialSpecDescribe", ""));
				info.setMaterialSpec(obj.optString("materialSpec", ""));				
				info.setIsBox(obj.optString("isBox", ""));
				info.setMaterialCount(obj.optString("materialCount", ""));
				info.setPositionCode(obj.optString("positionCode", ""));
				info.setIsInBox(obj.optString("isInBox", ""));
				info.setOldCount((obj.optString("epcCodeMaterialCount", "0")));
				info.setNewEcpCode(obj.optString("newEpcCode", ""));
				list.add(info);
			}
		} catch (Exception e)
		{
			LogUtil.e(TAG, "解析待修改标签数据异常：" + e.getMessage());
		}
		return list;
	}

	/**解析借领数据 **/
	public List<EpcInfo> getLendList(String res)
	{
		List<EpcInfo> list = new ArrayList<EpcInfo>();
		EpcInfo info = null;
		try
		{
			JSONObject json = new JSONObject(res).getJSONObject("result");
			JSONArray jsonArr = json.getJSONArray("applyList");
			String totalCount=json.optString("totalCount","0");
			JSONObject obj = null;
			int len = jsonArr.length();
			for (int i = 0; i < len; i++)
			{
				info = new EpcInfo();
				obj = jsonArr.getJSONObject(i);
				info = new EpcInfo();
				
				info.setAppliedOrderNumber(obj.optString("appliedOrderNumber", ""));
				info.setAppliedDate(obj.optString("appliedDate", ""));
				info.setAppliedPersonName(obj.optString("appliedPersonName", ""));
				info.setAppliedStatus(obj.optString("appliedStatus", ""));
				info.setAppliedPurpose(obj.optString("appliedPurpose", ""));
				info.setTotalCount(totalCount);
				list.add(info);
			}
		} catch (Exception e)
		{
			LogUtil.e(TAG, "解析库存数据异常：" + e.getMessage());
		}
		return list;
	}

	/** 更改物资状态 **/
//	public void updateMaterialState(String state, EpcInfo epcInfo)
//	{
//		StringBuffer sb = new StringBuffer();
//		sb.append("t=" + ZKCmd.REQ_CHANGE_MATERIAL_STATE);
//		//sb.append("&goodsId=").append(epcInfo.getGoodsId());
//		sb.append("&select=").append(state);
//		CommUtil.executeCommTask(activity, handler, ZKCmd.HAND_RESPONSE_DATA, ZKCmd.ARG_REQUEST_UPDATE_STATE, 0,sb.toString());
//	}



	/** 左侧菜单数据 **/
	public List<Map<String, Object>> getLeftData(Activity activity)
	{
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		Bitmap bmp = null;

		String userType = CommUtil.getShare(activity).getString(ConfigUtil.USER_TYPE, "");

		//if (ConfigUtil.USER_MANAGER.equals(userType))
		{
			//power
			LogUtil.d(TAG, "power="+SystemUtil.power);
			
			if(SystemUtil.getPower(SystemUtil.REPORTS))
			{
				bmp=BitmapUtil.drawableToBitmap(activity, R.drawable.reports);	
				map=new HashMap<String, Object>();
				map.put("img", bmp);
				map.put("type", ConfigUtil.LEFT_REPORTS);
				map.put("text", activity.getString(R.string.store_detail_table_title));
				list.add(map);
				bmp=null;
			}
			
			if(SystemUtil.getPower(SystemUtil.LEFT_ARRIVE_REPORTS))
			{
				bmp=BitmapUtil.drawableToBitmap(activity, R.drawable.exit_store_img);	
				map=new HashMap<String, Object>();
				map.put("img", bmp);
				map.put("type", ConfigUtil.LEFT_EXIT_REPORTS);
				map.put("text", activity.getString(R.string.store_exit_table_title));
				list.add(map);
				bmp=null;
			}

			if(SystemUtil.getPower(SystemUtil.LEFT_EXIT_REPORTS))
			{
				bmp=BitmapUtil.drawableToBitmap(activity, R.drawable.approve_img);	
				map=new HashMap<String, Object>();
				map.put("img", bmp);
				map.put("type", ConfigUtil.LEFT_ARRIVE_REPORTS);
				map.put("text", activity.getString(R.string.store_approve_table_title));
				list.add(map);
				bmp=null;
			}
			
			if(SystemUtil.getPower(SystemUtil.ADMIN))
			{
				bmp = BitmapUtil.drawableToBitmap(activity, R.drawable.addmat);
				map = new HashMap<String, Object>();
				map.put("img", bmp);
				map.put("type", ConfigUtil.LEFT_MATERIAL_ADD_RECORD);
				map.put("text", activity.getString(R.string.store_add_material_table_title));
				list.add(map);
				bmp = null;
			}

			
//			bmp = BitmapUtil.drawableToBitmap(activity, R.drawable.exception);
//			map = new HashMap<String, Object>();
//			map.put("img", bmp);
//			map.put("type", ConfigUtil.LEFT_EXCEPTION_INFO);
//			map.put("text", "系统异常信息");
//			list.add(map);
//			bmp = null;

//			boolean auditPower  = CommUtil.getShare(activity).getBoolean(ConfigUtil.USER_POWER_AUDIT, false);
//			boolean approvePower=CommUtil.getShare(activity).getBoolean(ConfigUtil.USER_POWER_APPROVE, false);
//			if (auditPower || approvePower)
//			{
//				// 有审核权的管理员
//				bmp = BitmapUtil.drawableToBitmap(activity, R.drawable.myinfo);
//				map = new HashMap<String, Object>();
//				map.put("img", bmp);
//				map.put("type", ConfigUtil.LEFT_MY_INFO);
//				map.put("text", "与我相关");
//				list.add(map);
//				bmp = null;
//			}
			
//			bmp = BitmapUtil.drawableToBitmap(activity, R.drawable.myinfo);
//			map = new HashMap<String, Object>();
//			map.put("img", bmp);
//			map.put("type", ConfigUtil.LEFT_MODIFY_NEED);
//			map.put("text", activity.getString(R.string.modify_need_count_title));
//			list.add(map);
//			bmp = null;
			
			if(SystemUtil.getPower(SystemUtil.MATERIAL_INVTORY))
			{
				bmp = BitmapUtil.drawableToBitmap(activity, R.drawable.modifybox);
				map = new HashMap<String, Object>();
				map.put("img", bmp);
				map.put("type", ConfigUtil.LEFT_MODIFY_BOX_COUNT);
				map.put("text", activity.getString(R.string.modify_box_metal_count_title));
				list.add(map);
				bmp = null;
			}
			
			if(SystemUtil.getPower(SystemUtil.MATERIAL_NEW))
			{
				bmp = BitmapUtil.drawableToBitmap(activity, R.drawable.read);
				map = new HashMap<String, Object>();
				map.put("img", bmp);
				map.put("type", ConfigUtil.LEFT_READ_ECP);
				map.put("text", "物资状态更新及调仓位");
				list.add(map);
				bmp = null;
			}

			//新增暂时屏蔽,20160927
//			bmp = BitmapUtil.drawableToBitmap(activity, R.drawable.addmat);
//			map = new HashMap<String, Object>();
//			map.put("img", bmp);
//			map.put("type", ConfigUtil.LEFT_MATERIAL_ADD);
//			map.put("text", "物资新增");
//			list.add(map);
//			bmp = null;
			
			LogUtil.d("Write", "WRITE_TAG="+SystemUtil.getPower(SystemUtil.WRITE_TAG));
			if(SystemUtil.getPower(SystemUtil.WRITE_TAG))
			{
				bmp = BitmapUtil.drawableToBitmap(activity, R.drawable.createnew);
				map = new HashMap<String, Object>();
				map.put("img", bmp);
				map.put("type", ConfigUtil.LEFT_MATERIAL_STORAGE);
				map.put("text", activity.getString(R.string.zk_store_tag_text));
				list.add(map);
				bmp = null;
			}
		}

//		bmp = BitmapUtil.drawableToBitmap(activity, R.drawable.myrecord);
//		map = new HashMap<String, Object>();
//		map.put("img", bmp);
//		map.put("type", ConfigUtil.LEFT_MY_RECORD);
//		map.put("text", "我的申请记录");
//		list.add(map);
//		bmp = null;

		bmp = BitmapUtil.drawableToBitmap(activity, R.drawable.refresh);
		map = new HashMap<String, Object>();
		map.put("img", bmp);
		map.put("type", ConfigUtil.LEFT_MATERIAL_CACHE);
		map.put("text", "刷新系统缓存");
		list.add(map);
		bmp = null;

		bmp = BitmapUtil.drawableToBitmap(activity, R.drawable.editpwd);
		map = new HashMap<String, Object>();
		map.put("img", bmp);
		map.put("type", ConfigUtil.LEFT_EDIT_PAWSSORD);
		map.put("text", "修改密码");
		list.add(map);
		bmp = null;

		bmp = BitmapUtil.drawableToBitmap(activity, R.drawable.userhead);
		map = new HashMap<String, Object>();
		map.put("img", bmp);
		map.put("type", ConfigUtil.LEFT_USER_INFO);
		map.put("text", "用户信息");
		list.add(map);
		bmp = null;

		if (ConfigUtil.USER_MANAGER.equals(userType) && ConstantUtil.USE_EPC_METHOD.equals(ConfigUtil.EPC_METHOD_WAN))
		{
			bmp = BitmapUtil.drawableToBitmap(activity, R.drawable.systemset);
			map = new HashMap<String, Object>();
			map.put("img", bmp);
			map.put("type", ConfigUtil.LEFT_SYSTEM_SET);
			map.put("text", activity.getString(R.string.mothod_set));
			list.add(map);
			bmp = null;
		}
		
		bmp = BitmapUtil.drawableToBitmap(activity, R.drawable.exit);
		map = new HashMap<String, Object>();
		map.put("img", bmp);
		map.put("type", ConfigUtil.LEFT_SYSTEM_EXIT);
		map.put("text", "退出系统");
		list.add(map);
		bmp = null;

		return list;
	}

	/** 盘点转换 **/
	public List<String> getScanData(List<TAG_TID> listTid)
	{
		List<String> listData = new ArrayList<String>();
		try
		{
			String str = "";
			int len = listTid.size();
			for (int i = 0; i < len; i++)
			{
				str = listTid.get(i).getEPC();
				if (null != str && !"".equals(str))
				{
					listData.add(str);
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return listData;
	}
	
}

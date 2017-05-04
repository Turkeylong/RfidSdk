package com.zk.rfid.map.dao;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Handler;

import com.zk.rfid.main.bean.EpcInfo;
import com.zk.rfid.util.CommUtil;
import com.zk.rfid.util.GetDataTask;
import com.zk.rfid.util.LogUtil;
import com.zk.rfid.util.ZKCmd;

/**
 * @Description: 标签及存储操作数据层
 * @date:2016-6-13下午1:51:58
 * @author: ldl
 */
public class StorageDao
{
	private static final String TAG="StorageDao";
	
	private Activity activity;
	private Handler handler;
	
	public StorageDao(Activity activity,Handler handler)
	{		
		this.handler=handler;
		this.activity=activity;
	}
	
	/**获得物资EPC列表的数据**/
	public void getEpcListDataTask(String param)
	{
		GetDataTask task=new GetDataTask(activity, handler);
		task.setHandlerCode(ZKCmd.HAND_RESPONSE_DATA);
		task.setArg1(ZKCmd.ARG_REQUEST_EPC_LIST);
		task.setParams(param);
		task.execute("");	
	}
	
	/**存储数据json解析**/
	public List<EpcInfo> getEpcList(String res)
	{
		List<EpcInfo> list = new ArrayList<EpcInfo>();
		EpcInfo info = null;
		try
		{
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
				// goodsid,materialId,materialname,counts,unit,epccode,positions,code,name,materialtype
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
				info.setTotalCount(totalCount);
				list.add(info);
			}
		} catch (Exception e)
		{
			LogUtil.e(TAG, "解析库存数据异常：" + e.getMessage());
		}
		return list;
	}
	
	/**获取仓位信息**/
	public void getPosDataTask(String param)
	{
		GetDataTask task=new GetDataTask(activity, handler);
		task.setHandlerCode(ZKCmd.HAND_RESPONSE_DATA);
		task.setArg1(2);
		task.setParams(param);
		task.execute("");	
	}
	
	/**获取仓位信息,当前仓位，是否箱子**/
	public void getEpcDataTask(EpcInfo info,String currPosition,int arg)
	{
		StringBuffer sb=new StringBuffer();
		sb.append("t="+ZKCmd.REQ_CREATE_EPC);		
		sb.append("&position=").append(currPosition);
		sb.append("&goodsid=").append(info.getMaterialId());
		sb.append("&inthebox=").append(""+arg);
		sb.append("&goodsCode=").append(info.getMaterialId());
		
		GetDataTask task=new GetDataTask(activity, handler);
		task.setHandlerCode(ZKCmd.HAND_RESPONSE_DATA);
		task.setArg1(3);
		task.setParams(sb.toString());
		task.execute("");	
	}
	
	/**解析生成的EPC信息**/
//	public String[] getEpcInfo(String res)
//	{
//		String[] array=new String[]{"无法定位","0"};
//
//		try
//		{
//
//			JSONObject obj=new JSONObject(res).getJSONObject("result");
//			String state=obj.optString("state", "");
//			if("1".equals(state))
//			{
//				array[0]=obj.optString("postext", "");
//				array[1]=obj.optString("posval", "");
//			}			
//		}catch(Exception e)
//		{
//			LogUtil.e(TAG, "解析库存没有EPC值数据异常："+e.getMessage());
//		}
//		return array;
//	}
	
	/**仓位平面架存储情况**/
//	public String getPosDataStr(String res)
//	{
//		String str="仓位无任何物品";
//		try
//		{
//			JSONArray jsonArr=new JSONObject(res).getJSONArray("result");
//			JSONObject obj=null;
//			int len=jsonArr.length();
//			if(len==0)
//			{
//				str="仓位无任何物品";
//			}else
//			{
//				LogUtil.i(TAG, "仓位数据多少条:"+len);
//				Map<String,String> map=new HashMap<String, String>();
//				LogUtil.i(TAG, "仓位数据:"+res);
//				for(int i=0;i<len;i++)
//				{
//					obj=jsonArr.getJSONObject(i);
//					//materialname,unit	,materialId
//					String code=obj.optString("materialId", "");
//					String name=obj.optString("materialname", "");
//					String unit=obj.optString("unit", "");
//					
//					map.put(code+"_code", code);
//					map.put(code+"_name", name);
//					map.put(code+"_unit", unit);
//					
//					String count=map.get(code+"_count");
//					if(null==count)
//					{
//						count="0";
//					}
//					map.put(code+"_count", (Integer.parseInt(count)+1)+"");
//				}
//				
//				  Iterator it = map.entrySet().iterator();
//				  StringBuffer sb=new StringBuffer();
//				  //sb.append("仓位包含:");
//				  while(it.hasNext())
//				  {
//					 Entry entry = (Entry)it.next();
//					 String key=entry.getKey()+"";
//					 String val=entry.getValue()+"";
//					 if(key.contains("_code"))
//					 {
//						 String name=map.get(val+"_name");
//						 String count=map.get(val+"_count");
//						 String unit=map.get(val+"_unit");
//						 sb.append("共"+count).append(unit).append("  "+name).append(";\n");
//					 }
//				  }				  
//				  str=sb.toString();
//			}
//						
//		}catch(Exception e)
//		{
//			str="仓位信息查询异常，请稍后再试!";
//			LogUtil.e(TAG, "解析仓位数据异常："+e.getMessage());
//		}	
//		return str;
//	}
	
	
	/**物资新增**/
	public void addMaterialTask(String param)
	{
		CommUtil.executeCommTask(activity,handler,ZKCmd.HAND_RESPONSE_DATA, ZKCmd.ARG_REQUEST_ADD_MATERIAL, 0, param);	
	}
	
	/**物资箱子新增**/
	public void addMaterialBoxTask(String param)
	{
		CommUtil.executeCommTask(activity,handler,ZKCmd.HAND_RESPONSE_DATA, ZKCmd.ARG_REQUEST_ADD_BOX, 0, param);	
	}
	
}

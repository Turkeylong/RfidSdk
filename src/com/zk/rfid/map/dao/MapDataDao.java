package com.zk.rfid.map.dao;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences.Editor;

import com.zk.rfid.main.util.ConfigUtil;
import com.zk.rfid.util.CommUtil;
import com.zk.rfid.util.ConstantUtil;

/**
 * @Description: 平面图数据
 * @date:2016-8-3下午5:34:14
 * @author: ldl
 */
public class MapDataDao
{
	private static final String TAG = "MapDataDao";
	// 1区是7架3栏2层 前6架每两架贴在一起
	// 2区是5架4栏3层
	// 3区是10架2栏2层 每两架贴在一起
	// 4区是正面左侧大门楼梯下面，只有一个3层2栏
	// 5区是5架4栏3层
	// "020301 0101 010101":"广东省清远市龙山镇 1厂1区 1架1栏1层",20160808,将改成每3位代表一个，如002003001 001001 001001001
	//20160901，改成2-3-0-1-1 -1-1-1-1 最后一个表现位号

	private JSONObject json = new JSONObject();

	/**动态生成各区域的平面图数据**/
	public void createPositionData(Context context)
	{
		showOneArea();
		showTwoArea();
		showThreeArea();
		showFourArea();
		showFiveArea();

		Editor editor = CommUtil.getShare(context).edit();
		editor.putString(ConfigUtil.MATERIAL_POSITION, json.toString());
		//LogUtil.i(TAG, "位置生成数据:" + json.toString());
		editor.commit();
	}

	/**1区域的平面图数据**/
	private void showOneArea()
	{
		int count = 7;// 6架全都是2层3栏,第七架是2层2栏
		for (int k = 1; k <= count; k++)
		{
			int leve = 3, player = 2;// 1架3栏2层
			if (k == 1)
			{
				leve = 2; // 第1是2栏2层
			}
			for (int i = 1; i <= player; i++)
			{
				for (int j = 1; j <= leve; j++)
				{
					setPositionCode(k, j, i, ConstantUtil.ONE_AREA);
				}
			}
		}
	}
	/**2区域的平面图数据**/
	private void showTwoArea()
	{
		int count = 5;// 5架全都是3层4栏
		for (int k = 1; k <= count; k++)
		{
			int leve = 4, player = 3;
			for (int i = 1; i <= player; i++)
			{
				for (int j = 1; j <= leve; j++)
				{
					setPositionCode(k, j, i, ConstantUtil.TWO_AREA);
				}
			}
		}
	}
	/**3区域的平面图数据**/
	private void showThreeArea()
	{

		int count = 10;// 10架全都是2层2栏

		for (int k = 1; k <= count; k++)
		{
			int leve = 2, player = 2;
			for (int i = 1; i <= player; i++)
			{

				for (int j = 1; j <= leve; j++)
				{
					setPositionCode(k, j, i, ConstantUtil.THREE_AREA);
				}
			}
		}
	}
	/**4区域的平面图数据**/
	private void showFourArea()
	{
		int count = 1;// 4区1架3层2栏
		for (int k = 1; k <= count; k++)
		{
			int leve = 2, player = 3;
			for (int i = 1; i <= player; i++)
			{

				for (int j = 1; j <= leve; j++)
				{
					setPositionCode(k, j, i, ConstantUtil.FOUR_AREA);
				}

			}
		}
	}
	/**5区域的平面图数据**/
	private void showFiveArea()
	{
		int count = 10;// 5架全都是3层4栏,从6到10
		for (int k = 6; k <= count; k++)
		{
			int leve = 4, player = 3;
			for (int i = 1; i <= player; i++)
			{

				for (int j = 1; j <= leve; j++)
				{
					setPositionCode(k, j, i, ConstantUtil.FIVE_AREA);
				}
			}
		}
	}

	/** 1号库中的架，层,栏,区 **/
	private void setPositionCode(int jia, int lan, int ceng, String area)
	{			
		try
		{
			String code=getFrameCode(jia, lan, ceng, area);
			json.put(code, code);
		} catch (JSONException e)
		{
			e.printStackTrace();
		}
	}
	
	public static String getFrameCode(int jia, int lan, int ceng, String area)
	{
					
		StringBuffer sb = new StringBuffer();
		sb.append(ConstantUtil.POSITION_HEAD_CODE).append("-");
		sb.append(ConstantUtil.ONE_HOUSE).append("-").append(area);
		
		sb.append("-");
		sb.append(jia+"").append("-");
		sb.append(lan+"").append("-");
		sb.append(ceng+"");
		
		return sb.toString();
	}
	
}

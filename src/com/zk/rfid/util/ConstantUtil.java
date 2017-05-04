package com.zk.rfid.util;

import com.zk.rfid.main.util.ConfigUtil;

/**
 * @Description: 常量集合
 * @date:2016-5-16 下午4:33:20
 * @author: ldl
 */
public class ConstantUtil
{
	//public static final String INIT_HTTP_URL ="";//初始化url

	//public static final String HTTP_SERVER_URL = "http://192.168.1.13:8089/requst.aspx";
	//public static final String HTTP_SERVER_URL = "http://192.168.1.12:8000/requst.aspx";
	//public static final String HTTP_SERVER_URL = "http://acrfid.vicp.io:8088/requst.aspx";//测试库
	public static final String HTTP_SERVER_URL = "http://acrfid.vicp.io:8000/requst.aspx";//正式库

	public static final String HTTP_APK_JSON = "version.json";
	public static final String HTTP_APK_Name = "zsyzk.apk";
	public static final String APP_PACK_NAME = "com.zk.rfid";
	public static final String USE_EPC_METHOD = ConfigUtil.EPC_METHOD_COM;// 串口方式
	// public static final String USE_EPC_METHOD =ConfigUtil.EPC_METHOD_WAN;//网络方式

	// 省市镇
	public static final String POSITION_HEAD_NAME = "广东省清远市龙山镇";
	public static final String POSITION_HEAD_CODE = "2-3-0";

	// 仓库
	public static final String ONE_HOUSE = "1";
//	public static final String TWO_HOUSE = "002";
//	public static final String THREE_HOUSE = "003";

	// 一号库5个区域
	public static final String ONE_AREA = "1";
	public static final String TWO_AREA = "2";
	public static final String THREE_AREA = "3";
	public static final String FOUR_AREA = "4";
	public static final String FIVE_AREA = "5";

	public static final int PAGE_SIZE = 10;

}

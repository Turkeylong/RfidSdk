package com.zk.rfid.util;
/**
 * @Description: 请求码或者handler
 * @date:2016-5-16 下午4:33:43
 * @author: ldl
 */
public class ZKCmd 
{
	//请求网络接口	
	public static final int REQ_INIT_DATA   	 =1;//获取初始化数据
	public static final int REQ_LOGIN_ZK  		 =2;
	public static final int REQ_QUERY_USER_INFO  =3;
	public static final int REQ_UPDATE_PWD		 =4;
	public static final int REQ_GET_LEND   		 =5;
	public static final int REQ_GET_STORAGE		 =6;
	public static final int REQ_APPLY_MATERIAL   =7;
	public static final int REQ_LEND_DETAIL      =8;
	public static final int REQ_LEND_CONFIRM     =9;
	public static final int REQ_BACK_CONFIRM     =10;
	public static final int REQ_CREATE_EPC_DATA  =11;
	public static final int REQ_QUERY_POS        =12;
	public static final int REQ_CREATE_EPC       =13;
	public static final int REQ_GET_MATERIAL_DET =14;
	public static final int REQ_ADD_MATERIAL     =15;	
	public static final int REQ_CHANGE_MATERIAL_STATE    =16;
	public static final int REQ_GET_MATERIAL_NAME        =17;
	public static final int REQ_GET_MATERIAL_BOX_COUNT   =18;
	public static final int REQ_ADD_MATERIAL_BOX         =19;
	
	public static final int REQ_GET_STOAGE_BOX     	     =21;
	public static final int REQ_MATERIAL_BY_EPC          =22;
	public static final int REQ_MODIFY_GET_NEWEPC        =23;
	public static final int REQ_REGISTER_USER 		     =24;
	public static final int REQ_AUDIT_APPLY      		 =25;//审核
	
	public static final int REQ_APPROVE_APPLY            =26;//审批
	
	//27不知哪个用了
	public static final int REQ_MODIFY_SUCCESS            =28;
	public static final int REQ_UPDATE_MATERIAL_BY_LIST_ID=29;
	public static final int REQ_NOTIFY_UPDATE_SUCCESS     =30;//调整位置成功后通知服务器
	public static final int REQ_GETMATERIAL_TOTAL         =31;//物资统计
	public static final int REQ_GET_NEW_POSITION_CODE     =32;
	public static final int REQ_UPDATE_POSITION           =33;
	
	public static final int REQ_STORE_APPROVE_DATA        =34;
	public static final int REQ_STORE_EXIT_DATA           =35;
	public static final int REQ_STORE_TOTAL_DATA          =36;
	public static final int REQ_NEED_MODIFY_DATA          =43;
	
	public static final int REQ_TIPS_Exception          =53;//获取异常信息
	
	//handler请求发送码
	public static final int HAND_REQUEST_DATA			=1001;
	public static final int HAND_RESPONSE_DATA			=1002;
	public static final int HAND_REQUEST_EPC_DATA 		=1003;
	public static final int HAND_RESPONSE_EPC_DATA		=1004;
	public static final int HAND_WRITE_EPC_DATA   		=1005;
	public static final int HAND_MATERIAL_APPLY   		=1006;
	public static final int HAND_DETAIL_ONCLICK   		=1007;
	public static final int HAND_INIT_DATA   	   	 	=1008;
	public static final int HAND_MAP_ONCLICK   	   	    =1009;
	public static final int HAND_REQUEST_USER_INFO  	=1010;
	public static final int HAND_RESPONSE_USER_INFO     =1011;
	public static final int HAND_REQUEST_LEND_CONFIRM   =1012;	
	public static final int HAND_RESPONSE_LEND_CONFIRM  =1013;
	public static final int SEND_RES_CODE_RE_QUERY      =1014;
	
	public static final int HAND_REQUEST_BACK_CONFIRM   =1015;	
	public static final int HAND_RESPONSE_BACK_CONFIRM  =1016;
	public static final int HAND_APPLY_SUCCESS_RES      =1017;
	public static final int HAND_LEFT_ONCLICK           =1018;
	public static final int HAND_CREATE_EPC_ONCLICK     =1019;
	public static final int HAND_ADD_MATERIAL_SUBMIT    =1020;
	public static final int HAND_MATERIAL_DETAIL        =1021;
	public static final int HAND_GET_MATERIAL_NAME      =1022;
	public static final int HAND_GET_POS_BY_MAP         =1023;
	public static final int HAND_APPLY_JOIN             =1024;
	public static final int HAND_APPLY_SUBMIT           =1025;
	public static final int HAND_GET_MATERIAL_BOX_COUNT =1026;
	public static final int HAND_START_SCAN             =1027;
	public static final int HAND_DIALOG_MISS            =1028;
	
	public static final int HAND_LOAD_DATA       	    = 1028;
	public static final int HAND_REFRESH_DATA           = 1029;
	public static final int HAND_AUTO_LOGIN_FAIL        = 1030;
	public static final int HAND_AUTO_LOGIN             = 1031;
	public static final int HAND_MODIFY_EPC             = 1032;
	public static final int HAND_CLEAR_BOX_CHECK        = 1033;
	public static final int HAND_NOTIFY_DATA_CHANGE     = 1034;
	public static final int HAND_NOTIFY_DATA_RETURN     = 1035;

	public static final int HAND_REQUEST_EXCEPTION		= 1036;
		
	//区分arg1码
	public static final int ARG_REQUEST_INIT		=2001;
	public static final int ARG_REQUEST_LEND		=2002;
	public static final int ARG_REQUEST_RETURN		=2003;
	public static final int ARG_REQUEST_CREATE		=2004;
	public static final int ARG_REQUEST_STOAGE		=2005;
	public static final int ARG_REQUEST_ADD_MATERIAL=2006;	
	public static final int ARG_REQUEST_APPLY_MATERIAL=2007;
	public static final int ARG_REQUEST_UPDATE_STATE  =2008;
	public static final int ARG_GET_POS_REQUEST       =2009;
	public static final int ARG_GET_POS_RESPONSE      =2010;
	public static final int ARG_REQUEST_ADD_BOX       =2011;
	public static final int ARG_REQUEST_EPC_LIST      =2012;
	public static final int ARG_AUDIT_APPLY           =2013;
	public static final int ARG_GET_APPLY_DETAIL      =2014;
	public static final int ARG_GET_SCAN_EPC          =2015;
	public static final int ARG_GET_POS_EPC           =2016;
	public static final int ARG_MODIFY_EPC            =2017;
	public static final int ARG_MODIFY_EPC_SERVER_RES =2018;
	public static final int ARG_GET_SCAN_CONTINUE     =2019;
	public static final int ARG_GET_SCAN_LAST_ONE     =2020;
	public static final int ARG_GET_NEED_MODIFY_DATA  =2021;
	
	public static final int ARG_GET_EXCEPTION_INFO    =2022;
}

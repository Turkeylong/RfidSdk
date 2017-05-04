package com.zk.rfid.main.activity;

import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.rfid.sdk.public_utils.HostPacketType;
import com.rfid.sdk.public_utils.Message_Type;
import com.zk.rfid.R;
import com.zk.rfid.comm.base.BaseActivity;
import com.zk.rfid.comm.base.CommDialog;
import com.zk.rfid.comm.base.CommTopView;
import com.zk.rfid.main.bean.EpcInfo;
import com.zk.rfid.main.util.ConfigUtil;
import com.zk.rfid.main.work.MaterialDetailWork;
import com.zk.rfid.main.work.PowerSetWork;
import com.zk.rfid.util.CommUtil;
import com.zk.rfid.util.LogUtil;
import com.zk.rfid.util.ZKCmd;

/**
 * @Description: 物资详细
 * @date:2016年6月30日 上午9:57:21
 * @author: ldl
 */
public class MaterialDetailActivity extends BaseActivity
{
	private static final String TAG = "MaterialDetailActivity";

	private Activity activity = MaterialDetailActivity.this;
	private MaterialDetailWork detailWork;
	private EpcInfo epcInfo;
	private List<View> listMatView;

	private Handler handler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{

			case ZKCmd.HAND_REQUEST_DATA:

				if (ZKCmd.ARG_REQUEST_UPDATE_STATE == msg.arg1)
				{
					updateMaterialState(msg.obj + "");
				}
				break;

			case ZKCmd.HAND_RESPONSE_DATA:

				if (ZKCmd.HAND_MATERIAL_DETAIL == msg.arg1)
				{
					detailWork.showDetail(msg.obj + "");
				}

				if (ZKCmd.ARG_REQUEST_UPDATE_STATE == msg.arg1)
				{					 
					updateRes(msg.obj+"");				 
				}

				break;
				
			case HostPacketType.RFID_PACKET_TYPE_18K6C_TAG_ACCESS:
				
				getEpcOperate(msg);
								
			  break;
			  
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		Window window = getWindow();  
        WindowManager.LayoutParams params = window.getAttributes();  
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE;
        window.setAttributes(params);
		
		setContentView(R.layout.material_detail);

		initView();
		initData();
	}

	/**执行公用顶部及返回操作**/
	private void initView()
	{
		new CommTopView(activity);
		
		detailWork = MaterialDetailWork.getInstance(activity);
		detailWork.initView(handler);
	}

	/**接收数据对象，并查询该记录的详细数据**/
	private void initData()
	{
		epcInfo = (EpcInfo) getIntent().getSerializableExtra("info");
		if (null != epcInfo)
		{
			getMaterialDetail();
			new CountDownTimer(1500,100)
			{		
				@Override
				public void onTick(long millisUntilFinished)
				{			
				}			
				@Override
				public void onFinish()
				{
					PowerSetWork.setObjectPower(ConfigUtil.POWER_SMALL);//写入标签用小功率
				}
			}.start();	
		}
	}

	/**更新该记录的数据库状态，如已领用，注销等**/
	private void updateMaterialState(String state)
	{
		if (isConn())
		{
			StringBuffer sb = new StringBuffer();
			sb.append("t=" + ZKCmd.REQ_CHANGE_MATERIAL_STATE);
			sb.append("&epcCodeList=").append("'"+epcInfo.getEpcCode()+"'");
			sb.append("&materialStatus=").append(state);
			CommUtil.executeCommTask(activity, handler, ZKCmd.HAND_RESPONSE_DATA, ZKCmd.ARG_REQUEST_UPDATE_STATE, 0,sb.toString());
		}
	}

	/**获得物资详细的请求**/
	private void getMaterialDetail()
	{
		if (isConn())
		{
			StringBuffer sb = new StringBuffer();
			sb.append("t=" + ZKCmd.REQ_GET_MATERIAL_DET);
			sb.append("&epcCode=").append(epcInfo.getEpcCode());
			//sb.append("&deliveryOrderNumber=").append(epcInfo.getDeliveryOrderNumber());
			//sb.append("&isbox=").append("0"); // 物资，不含箱子
			CommUtil.executeCommTask(activity, handler, ZKCmd.HAND_RESPONSE_DATA, ZKCmd.HAND_MATERIAL_DETAIL, 0,sb.toString());
		}
	}
	

	/**修改物资状态的操作结果解析**/
	private void updateRes(String res)
	{		
		//{"result":{"state":"1","msg":"操作成功"}}	
		try
		{
			String str=new JSONObject(res).getJSONObject("result").toString();
			Map<String,String> map=CommUtil.getJsonMap(str);
			
			String state=map.get("state");	
			if("1".equals(state))
			{
				getMaterialDetail();
			}
			CommUtil.toastShow(map.get("msg"), activity);
		}catch(Exception e)
		{	
			CommUtil.toastShow("操作返回结果异常", activity);
		}
	}
	
	/**标签读写操作,根据结果码显示**/
	private void getEpcOperate(Message msg)
	{			
		String msg_type = msg.getData().getString(Message_Type.msg_type);	
		LogUtil.i(TAG, "读写操作返回:"+msg_type);
		if(msg_type == Message_Type.mac_err)
		{
			String mac_err = msg.getData().getString(Message_Type.mac_err);
            if(mac_err.equals("0000"))
            { 
            	
            }else
            {
          	  CommUtil.toastShow("MAC错误代码:"+mac_err, activity);  
            }
		}
		else if(msg_type == Message_Type.tag_read)
    	 {
    		// String data = msg.getData().getString(Message_Type.tag_read);
			 String mes=msg.getData().getString(Message_Type.tag_read);
        	//CommUtil.toastShow("读到:"+mes, activity);
    	  		 
    	  }else if(msg_type == Message_Type.tag_read_EPC)
    	  {
    		 String mes=msg.getData().getString(Message_Type.tag_read_EPC);
        	// CommUtil.toastShow("读到:"+mes, activity);
    	  }
    	  else if(msg_type == Message_Type.tag_check)
    	  {
    		  int tag_err = msg.getData().getInt(Message_Type.tag_err);
              new CommDialog(activity).showReadWriteResMsg(tag_err);
              
              detailWork.powerOff();//断电             
    	  }
	}

	@Override
	protected void onDestroy()
	{
		// 退出时清空
		MaterialDetailWork.clearWork();
		super.onDestroy();
	}

}

package com.zk.rfid.comm.base;

import com.rfid.sdk.public_utils.DebugLog;
import com.rfid.sdk.public_utils.Public;

import android.app.Instrumentation;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

public class BootBroadcastReceiver extends BroadcastReceiver { 
	static final String action_boot="android.intent.action.BOOT_COMPLETED"; 
	private ButtonInferface ctrl_interface;
	private Intent new_intent=null;
	
	public void setBtnMsgListener(ButtonInferface ctrl_interface) 
	{
		this.ctrl_interface = ctrl_interface;
	}
	
	public static void simulateKeystroke(final int KeyCode) {
		 

        new Thread(new Runnable() {
            
            public void run() {
                // TODO Auto-generated method stub
                try {
                    
                    Instrumentation inst=new Instrumentation();
                    inst.sendKeyDownUpSync(KeyCode);
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
        }).start();
    }
	
	@Override 
	public void onReceive(Context context, Intent intent) { 
		if (intent.getAction().equals(action_boot))
		{ 
			Intent StartIntent=new Intent(context,com.zk.rfid.update.AutoUpdateActivty.class); //接收到广播后，跳转到MainActivity 
			StartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
			context.startActivity(StartIntent);
		}
		else if(intent.getAction().toString().equals(Public.BROADCAST_BUTTON_ACTION))
		{  // 注意此处的Intent的action要对应广播发送者发送时的action
			String msg = (String) intent.getExtras().get("button");
			DebugLog.d("test","SendMsg:"+msg);
			
			try {
				ctrl_interface.handleMsg(msg);
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			//DebugLog.d("Test",context.toString());
			
			try{
				if(msg.equals("left_btn_Down"))
				{
					simulateKeystroke(KeyEvent.KEYCODE_BACK);
//					new_intent = new Intent(context,com.zk.rfid.main.activity.OperateEpcActivity.class); 
//					new_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
//					context.startActivity(new_intent);	
				}
				else if(msg.equals("left_btn_Up"))
				{
					
				}
				else if(msg.equals("right_btn_Down"))
				{
					new_intent = new Intent(context,com.zk.rfid.main.activity.OperateEpcActivity.class);
					new_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
					new_intent.putExtra("start_inventory", true);
					context.startActivity(new_intent);	
				}
				else if(msg.equals("right_btn_Up"))
				{
					Intent intent_Msg = new Intent("com.rfid.sdk.protocol.ACTION");
	                intent_Msg.putExtra("start_inventory", false);
	                context.sendBroadcast(intent_Msg);
				}
			}catch(NullPointerException e)
			{
				e.printStackTrace();
			}

		}
	} 
} 

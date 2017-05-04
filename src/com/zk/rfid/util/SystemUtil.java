package com.zk.rfid.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.zk.rfid.R;
import com.zk.rfid.main.bean.EpcInfo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

/**
 * Description: 系统调用工具集
 * @author 阳龙
 * @date:2014-10-28 下午4:45:22
 */
public class SystemUtil {
    private static Context context;
    private static final String TAG = "SystemUtil";
    public static String power;
    
	public static final int REPORTS =             0;	//统计物资信息
	public static final int LEFT_EXIT_REPORTS =   1;  	//物资到货记录
	public static final int LEFT_ARRIVE_REPORTS = 2;  	//物资发放记录
	public static final int MATERIAL_INVTORY = 	  5;	//物资盘点
	public static final int MATERIAL_UPDATE = 	  13;	//写标签
	public static final int WRITE_TAG = 		  14;	//写标签
	public static final int MATERIAL_APPLY = 	  15;	//物资申请
	public static final int MATERIAL_NEW = 	  	  16;	//新增物资
	public static final int MATERIAL_OUT = 	  	  19;	//领出
	public static final int MATERIAL_BACK = 	  20;	//退返
	public static final int ADMIN = 	  		  21;	//管理
	
    public SystemUtil(Context context) {
		super();
		this.context = context;
	}

//获取系统默认铃声的Uri  
    private Uri getSystemDefultRingtoneUri() {  
        return RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);  
    } 
    
    public static void startAlarm(Context context) 
    {  
    	MediaPlayer mp = new MediaPlayer();
    	try 
    	{
	    	mp.setDataSource(context, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
	    	mp.prepare();
	    	mp.start();
    	} 
    	catch (Exception e) 
    	{
    		e.printStackTrace();
    	} 
    	
//    	NotificationManager nm = (NotificationManager)context.getSystemService(android.content.Context.NOTIFICATION_SERVICE);  
//    	Uri ringUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);  
//    	      Notification noti = new NotificationCompat.Builder(context)  
////    	                .setTicker(name+": " + msg)  
////    	                .setContentTitle(name)  
////    	                .setContentText(msg)  
////    	                .setSmallIcon(R.drawable.ic_launcher)  
////    	                .setContentIntent(pIntent)  
//    	                .setSound(ringUri)  
//    	                .build();  
//    	      nm.notify(1, noti);
    } 
    
  //自定义显示的通知 ，创建RemoteView对象  
    /*
    private void showCustomizeNotification() 
    {  
  
        CharSequence title = "i am new";  
        int icon = R.drawable.icon;  
        long when = System.currentTimeMillis();  
        Notification noti = new Notification(icon, title, when + 10000);  
        noti.flags = Notification.FLAG_INSISTENT;  
          
        // 1、创建一个自定义的消息布局 view.xml  
        // 2、在程序代码中使用RemoteViews的方法来定义image和text。然后把RemoteViews对象传到contentView字段  
        RemoteViews remoteView = new RemoteViews(context.getPackageName(),R.layout.notification);  
        remoteView.setImageViewResource(R.id.image, R.drawable.icon);  
        remoteView.setTextViewText(R.id.text , "通知类型为：自定义View");  
        noti.contentView = remoteView;  
        // 3、为Notification的contentIntent字段定义一个Intent(注意，使用自定义View不需要setLatestEventInfo()方法)  
         
        //这儿点击后简单启动Settings模块  
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,new Intent("android.settings.SETTINGS"), 0);  
        noti.contentIntent = contentIntent;  
      
        NotificationManager mnotiManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);  
        mnotiManager.notify(0, noti);  
  
    }  ArrayList<EpcInfo> listObj = new ArrayList<EpcInfo>();
  */
    // 默认显示的的Notification  
    public static void showDefaultNotification(Context context,int id,String title,String info, ArrayList<EpcInfo> list) {  
        // 定义Notication的各种属性  
        int icon = R.drawable.init_bg;  
        long when = System.currentTimeMillis();  
        Notification noti = new Notification(icon, title, when + 10000);  
        noti.flags = Notification.FLAG_INSISTENT;  
  
        // 创建一个通知  
        Notification mNotification = new Notification();  
  
        // 设置属性值  
        mNotification.icon = R.drawable.init_bg;  
        mNotification.tickerText = "NotificationTest";  
        mNotification.when = System.currentTimeMillis(); // 立即发生此通知  
  
        // 带参数的构造函数,属性值如上  
        // Notification mNotification = = new Notification(R.drawable.icon,"NotificationTest", System.currentTimeMillis()));  
  
        // 添加声音效果  
        mNotification.defaults |= Notification.DEFAULT_SOUND;  
  
        // 添加震动,后来得知需要添加震动权限 : Virbate Permission  
        //mNotification.defaults |= Notification.DEFAULT_VIBRATE ;   
  
        //添加状态标志   
  
        //FLAG_AUTO_CANCEL          该通知能被状态栏的清除按钮给清除掉  
        //FLAG_NO_CLEAR                 该通知能被状态栏的清除按钮给清除掉  
        //FLAG_ONGOING_EVENT      通知放置在正在运行  
        //FLAG_INSISTENT                通知的音乐效果一直播放  
        mNotification.flags = Notification.FLAG_AUTO_CANCEL;
        
        Intent appIntent = new Intent(Intent.ACTION_MAIN);
        appIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        appIntent.setComponent(new ComponentName(context, com.zk.rfid.main.activity.ExceptionActivity.class));//用ComponentName得到class对象
        appIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);// 关键的一步，设置启动模式，两种情况
        
        //LogUtil.d(TAG,title+list.toString());
        
//        Bundle bundle=new Bundle();
//        bundle.putSerializable(title, (Serializable) list);//序列化
//        appIntent.putExtras(bundle);//发送数据
        appIntent.putExtra(title, (Serializable) list);  
        //将该通知显示为默认View  
        //PendingIntent contentIntent = PendingIntent.getActivity(context, 0,new Intent("android.settings.SETTINGS"), 0); 
        
        //PendingIntent contentIntent = PendingIntent.getActivities(context,0,makeIntentStack(context),PendingIntent.FLAG_CANCEL_CURRENT);
        //PendingIntent contentIntent = PendingIntent.getActivity(context, 0,new Intent(context,com.zk.rfid.main.activity.ExceptionActivity.class), 0); 
        PendingIntent contentIntent = PendingIntent.getActivity(context, id,appIntent, PendingIntent.FLAG_UPDATE_CURRENT); 

        //mNotification.flags |= Notification.DEFAULT_ALL;
        
        mNotification.setLatestEventInfo(context, title, info,contentIntent);
          
        // 设置setLatestEventInfo方法,如果不设置会App报错异常  
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);  
          
        //注册此通知   
        // 如果该NOTIFICATION_ID的通知已存在，会显示最新通知的相关信息 ，比如tickerText 等  
        mNotificationManager.notify(id, mNotification);
  
    }
    
    static Intent[] makeIntentStack(Context context) 
    {  
        Intent[] intents = new Intent[2];  
        intents[0] = Intent.makeRestartActivityTask(new ComponentName(context, com.zk.rfid.main.activity.MainActivity.class));  
        intents[1] = new Intent(context,  com.zk.rfid.main.activity.ExceptionActivity.class);  
        return intents;  
    }  
     
	public static void removeNotification(Context context)  
    {  
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);  
        // 取消的只是当前Context的Notification  
        mNotificationManager.cancel(2);  
    }

	public static boolean getPower(int type) {
		// TODO 自动生成的方法存根
		//LogUtil.d(TAG, "power:"+power.substring(type,type+1).equals("1"));
		try
		{
			if(power.substring(type,type+1) != null)
			{
				if(power.substring(type,type+1).equals("1"))
					return true;
			}
		}catch(NullPointerException e)
		{
			e.printStackTrace();
		}

		return false;
	} 
}

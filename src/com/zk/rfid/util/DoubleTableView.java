package com.zk.rfid.util;

import java.util.List;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zk.rfid.R;

/**
 * @Description: 公用表格
 * @date:2016-5-20 下午3:07:39
 * @author: ldl
 */
public class DoubleTableView 
{
	private Activity activity;
	private Handler handler;
	private int handlerCode=0;
	
	public DoubleTableView(Activity activity)
	{
		this.activity=activity;
	}
	
	public LinearLayout getViewList(List<String[]> list,LinearLayout linear)
	{
		if(null==list || list.size()==0)
		{
			return null;
		}else
		{			
			int len=list.size();
			for(int i=0;i<len;i++)
			{
				int count=list.get(i).length;
				if(count==2)
				{
					linear.addView(getView(list.get(i)[0],list.get(i)[1],""));
				}
				if(count==3)
				{
					linear.addView(getView(list.get(i)[0],list.get(i)[1],list.get(i)[2]));
				}									
			}			
			return linear;
		}
	}
	
	private  View view1 =null;
	private  TextView textLeft=null;
	private  TextView textRight=null;
	
	public View getView(String leftStr,String rightStr,final String id)
	{		  
		  view1 = LayoutInflater.from(activity).inflate(R.layout.table_item, null);	  
		  textLeft=(TextView) view1.findViewById(R.id.textLeft);
		  textRight=(TextView) view1.findViewById(R.id.textRight);
		  				  
		  textLeft.setText(leftStr);		   
		  textRight.setText(rightStr);	
		  textRight.setOnClickListener(new View.OnClickListener() 
		  {			
			@Override
			public void onClick(View v)
			{
				if(!"".equals(id))
				{
					Message msg=Message.obtain();
					msg.what=handlerCode;
					msg.obj=id;
					handler.sendMessage(msg);
				}				
			}	    
		  });
		  return view1;
	}
	
	
	

	public Handler getHandler() 
	{
		return handler;
	}

	public void setHandler(Handler handler) 
	{
		this.handler = handler;
	}

	public int getHandlerCode()
	{
		return handlerCode;
	}

	public void setHandlerCode(int handlerCode)
	{
		this.handlerCode = handlerCode;
	}
	
	
}

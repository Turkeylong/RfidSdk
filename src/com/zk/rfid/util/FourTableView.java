package com.zk.rfid.util;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zk.rfid.R;
import com.zk.rfid.comm.base.MyApplication;

/**
 * @Description:4列公用表格
 * @date:2016-8-19 下午3:07:39
 * @author: ldl
 */
public class FourTableView 
{

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
				
				linear.addView(getView(list.get(i)));
			}			
			return linear;
		}
	}
	
	private  View view1 =null;
	private  TextView textLeftName=null;
	private  TextView textLeftVal=null;
	private  TextView textRightName=null;
	private  TextView textRightVal=null;
	
	private View getView(String array[])
	{		
		if(null!=array && array.length==4)
		{
			  view1 = LayoutInflater.from(MyApplication.context).inflate(R.layout.double_table_item, null);	  
			  textLeftName=(TextView) view1.findViewById(R.id.textLeftName);
			  textLeftVal=(TextView) view1.findViewById(R.id.textLeftVal);
			  textRightName=(TextView) view1.findViewById(R.id.textRightName);
			  textRightVal=(TextView) view1.findViewById(R.id.textRightVal);
			  				  
			  textLeftName.setText(array[0]);		   
			  textLeftVal.setText(array[1]);
			  textRightName.setText(array[2]);		   
			  textRightVal.setText(array[3]);
			  
			  return view1;
		}else
		{
			return null;	
		}	
	}
	
}

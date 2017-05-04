package com.zk.rfid.map.dao;

import java.util.Map;

import android.app.Activity;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.zk.rfid.R;
import com.zk.rfid.util.CommUtil;
import com.zk.rfid.util.ConstantUtil;
import com.zk.rfid.util.LogUtil;
import com.zk.rfid.util.ZKCmd;

/**
 * @Description: 动态生成平面图的物资架
 * @date:2016-6-27上午11:31:46
 * @author: ldl
 */
public class MapViewBtn
{

	private static final String TAG="MapViewBtn";
    private Activity activity;
    private Handler handler;
  
	public MapViewBtn(Activity activity,Handler handler)
	{
		 this.activity=activity;
		 this.handler =handler;
	}

	/**位置码,是否是显示库存总量,架位上统计数量（如3箱5散件）**/
	public View getBtnView(final String posCode,boolean storageFlag,Map<String,String> map,boolean showCheckBox)
	{
		final View view = LayoutInflater.from(activity).inflate(R.layout.map_layer_item, null);
		Button button = (Button) view.findViewById(R.id.button);
		CheckBox checkBox=(CheckBox) view.findViewById(R.id.checkBox);
		if(showCheckBox)
		{
			checkBox.setVisibility(View.VISIBLE);
		}else
		{
			checkBox.setVisibility(View.GONE);
		}
		final TextView codeText = (TextView) view.findViewById(R.id.codeText);
		//int len=code.length();
		try
		{
			//2-3-0-1-2 -5-3-3 广东清远龙山 1库2区，5架3栏3层
			if(null!=posCode)
			{
				String code=ConstantUtil.POSITION_HEAD_CODE+"-"+ConstantUtil.ONE_HOUSE+"-";
				String name =posCode.replaceAll(code, "");
							
				StringBuffer sb=new StringBuffer();
				sb.append(posCode).append("_").append(name);
				codeText.setText(sb.toString());//将完整的传回001号库001区001架001栏001层
				
				button.setText(name);
				if(storageFlag && null!=map && map.size()>0)					
				{
					String st=map.get(posCode);
					if(null!=st && !"".equals(st) && !"null".equals(st))
					{
						button.setText(st);
					}						
				}
				button.setOnClickListener(new View.OnClickListener()
				{			
					@Override
					public void onClick(View v)
					{
						Message msg=Message.obtain();
						msg.what=ZKCmd.HAND_MAP_ONCLICK;
						msg.obj =codeText.getText().toString();//位置码_加名称
						handler.sendMessage(msg);			
					}
				});	
				
				checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener()
				{					
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
					{					
						Editor editor=CommUtil.getPositionShare(activity).edit();
						if(isChecked)	
						{
							editor.putString(posCode, posCode);
						}else
						{
							editor.remove(posCode);
						}
						editor.commit();
					}
				});				
			}				  
		}catch(Exception e)
		{
			LogUtil.e(TAG, "动态生成按钮异常:"+e.getMessage());
		}
		    
		return view;
	}

}

package com.zk.rfid.main.adapter;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.zk.rfid.R;
/**
 * @Description: 下拉单位adapter
 * @date:2016-7-23 下午4:45:22
 * @author: 罗德禄
 */
public class UnitdAdapter extends BaseAdapter
{

	private final static String TAG="UnitdAdapter";
	private List<String> list;
	private Activity activity;
	private EditText editText;
	private PopupWindow selectPopupWindow ;

	static class ViewHolder
	{
		TextView recordTextView;		
	}

	public UnitdAdapter(Activity activity, List<String> list,EditText editText,PopupWindow window) 
	{
		this.list = list;
		this.activity = activity;
		this.editText= editText;
		this.selectPopupWindow=window;
	}

	@Override
	public int getCount() 
	{
		return list.size();
	}

	@Override
	public Object getItem(int position)
	{
		return list.get(position);
	}

	@Override
	public long getItemId(int position) 
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder holder = null;
		//StringBuffer sb=new StringBuffer();
		if (convertView == null) 
		{
			holder = new ViewHolder();
			convertView = LayoutInflater.from(activity).inflate(R.layout.apply_option_item, null);		
			holder.recordTextView=(TextView) convertView.findViewById(R.id.item_text);			
			convertView.setTag(holder);
		} 
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}
		
	    final String unitname=list.get(position).toString();
	   
		holder.recordTextView.setText(unitname);
		//sb.setLength(0);//最后清空内容
		// 文本事件
		holder.recordTextView.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				editText.setText(unitname); 
				selectPopupWindow.dismiss();
			}
		});
		
		return convertView;
	}
}

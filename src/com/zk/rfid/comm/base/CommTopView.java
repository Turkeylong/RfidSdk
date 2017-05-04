package com.zk.rfid.comm.base;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zk.rfid.R;
import com.zk.rfid.util.LogUtil;

/**
 * @Description: 顶部标题公用类
 * @date:2016-8-22上午10:52:01
 * @author: ldl
 */
public class CommTopView
{

	private static final String TAG = "CommTopView";
	private Activity activity;

	// 返回
	private ImageView backImg;
	private TextView gobacktext;

	public CommTopView(Activity activity)
	{
		this.activity = activity;
		initTopView();
	}

	/** 公用显示顶部返回**/
	private void initTopView()
	{
		backImg = (ImageView) activity.findViewById(R.id.backImg);
		gobacktext = (TextView) activity.findViewById(R.id.gobacktext);

		backImg.setOnClickListener(listener);
		gobacktext.setOnClickListener(listener);
	}

	private OnClickListener listener = new View.OnClickListener()
	{
		@Override
		public void onClick(View key)
		{
			switch (key.getId())
			{
			case R.id.gobacktext:

				activity.finish();
				break;

			case R.id.backImg:
				activity.finish();
				break;
			default:
				break;
			}
		}
	};

}

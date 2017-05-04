package com.zk.rfid.comm.search;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

/**
 * @Description: 搜索框事件
 * @date:2016-8-30上午11:52:31
 * @author: ldl
 */
public class SeacherEdit
{
	public static void seacherTextChange(final EditText editText, final View imgview,final SeacherEditCallBackListen listen)
	{
		editText.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3)
			{
				listen.callBack(arg0.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3)
			{
				if (arg0.length() != 0)
				{
					imgview.setVisibility(View.VISIBLE);
				} else
				{
					imgview.setVisibility(View.GONE);
				}
			}

			@Override
			public void afterTextChanged(Editable arg0)
			{
				if (arg0.length() != 0)
				{
					imgview.setVisibility(View.VISIBLE);
				} else
				{
					imgview.setVisibility(View.GONE);
				}
			}
		});

		if (!"".equals(editText.getText().toString()))
		{
			imgview.setVisibility(View.VISIBLE);
		}

		imgview.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				editText.setText("");
			}
		});
	}
}

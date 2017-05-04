package com.zk.rfid.map.work;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.zk.rfid.R;
import com.zk.rfid.util.ConstantUtil;
import com.zk.rfid.util.LogUtil;

/**
 * @Description: 平面图
 * @date:2016-6-27下午3:57:18
 * @author: ldl
 */
public class MapWork
{

    private static final String TAG="MapWork";
	
//	private static MapWork work;
	
	private Activity activity;
	private Handler handler;
	private View    mainView;
	private LinearLayout mapone,maptwo,mapthree,mapfour,mapfive;	 
	private RadioGroup stoRadioGroup,sposRadioGroup;
	private RadioButton radioPos,radioStorage;
	
	private String checkHouse,checkArea;
	private LinearLayout currLin;//当前选中
	private boolean showCheckBox=false;
	

	private List<View> listMatView;
	
	public MapWork(Activity act)
	{
		activity=act;	
	}
	
	public MapWork(Activity act,boolean showCheckBox)
	{
		activity=act;	
		this.showCheckBox=showCheckBox;
	}
	

//	public static MapWork getInstance(Activity act)
//	{
//		if(null==work)
//		{
//			work=new MapWork(act);
//		}		
//		return work;
//	}
	
	
	private void getViewById()
	{
		stoRadioGroup=(RadioGroup) mainView.findViewById(R.id.stoRadioGroup);	
		RadioButton stoOne = (RadioButton) mainView.findViewById(R.id.stoOne);
		stoOne.performClick();
		
		sposRadioGroup=(RadioGroup) mainView.findViewById(R.id.sposRadioGroup);//仓位还是存储
		radioPos=(RadioButton) mainView.findViewById(R.id.radioPos);
		radioStorage=(RadioButton) mainView.findViewById(R.id.radioStorage);
				
		maptwo=(LinearLayout) mainView.findViewById(R.id.maptwo);
		mapone=(LinearLayout) mainView.findViewById(R.id.mapone);
		mapthree=(LinearLayout) mainView.findViewById(R.id.mapthree);
		mapfour=(LinearLayout) mainView.findViewById(R.id.mapfour);
		mapfive=(LinearLayout) mainView.findViewById(R.id.mapfive);
		
		sposRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) 
			{				
				if(R.id.radioPos==checkedId)
				{
					LogUtil.i(TAG, "位置");
					setViewGetDataVisable(currLin, checkHouse, checkArea);
					getDataByArea(checkHouse, checkArea,false);
				}
				if(R.id.radioStorage==checkedId)
				{
					LogUtil.i(TAG, "存放东西");
					getDataByArea(checkHouse, checkArea,true);
				}
			}			
		});
		
		stoRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() 
		{		
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1)
			{
				radioPos.setChecked(true);
				radioStorage.setChecked(false);
				
				switch (arg1) 
				{				
				case R.id.stoOne:
					
					setViewGetDataVisable(mapone, ConstantUtil.ONE_HOUSE, ConstantUtil.ONE_AREA);
					getDataByArea(ConstantUtil.ONE_HOUSE, ConstantUtil.ONE_AREA,false);
					break;
					
				case R.id.stoSecond:
					setViewGetDataVisable(maptwo, ConstantUtil.ONE_HOUSE, ConstantUtil.TWO_AREA);
					getDataByArea(ConstantUtil.ONE_HOUSE, ConstantUtil.TWO_AREA,false);
					break;
					
				case R.id.stoThird:
					setViewGetDataVisable(mapthree, ConstantUtil.ONE_HOUSE, ConstantUtil.THREE_AREA);
					getDataByArea(ConstantUtil.ONE_HOUSE, ConstantUtil.THREE_AREA,false);
					break;
					
				case R.id.stoFour:
					setViewGetDataVisable(mapfour, ConstantUtil.ONE_HOUSE, ConstantUtil.FOUR_AREA);
					getDataByArea(ConstantUtil.ONE_HOUSE, ConstantUtil.FOUR_AREA,false);
					break;
					
				case R.id.stoFifth:
					setViewGetDataVisable(mapfive, ConstantUtil.ONE_HOUSE, ConstantUtil.FIVE_AREA);
					getDataByArea(ConstantUtil.ONE_HOUSE, ConstantUtil.FIVE_AREA,false);
					break;
					
				default:
					break;
				}
			}
		});
	}
	
	/**控制显示的区域**/
	private void setViewGetDataVisable(LinearLayout lin,String house,String area)
	{
		checkHouse=house;
		checkArea =area;
		currLin   =lin;
		
		mapone.setVisibility(View.GONE);
		maptwo.setVisibility(View.GONE);
		mapthree.setVisibility(View.GONE);
		mapfour.setVisibility(View.GONE);
		mapfive.setVisibility(View.GONE);
		
		lin.setVisibility(View.VISIBLE);				
	}
	
	public void initView(Handler handler,View mainView)
	{
		this.mainView=mainView;
		this.handler=handler;

		getViewById();			
		setViewGetDataVisable(mapone, ConstantUtil.ONE_HOUSE, ConstantUtil.ONE_AREA);//默认显示一区
		getDataByArea(ConstantUtil.ONE_HOUSE, ConstantUtil.ONE_AREA,false);	//默认显示示的是仓位
	}
	
	/**根据区域选择**/
	public void getDataByArea(String house,String area,boolean flag)
	{
		listMatView=new ArrayList<View>();			
		if(ConstantUtil.ONE_HOUSE.equals(house))
		{
			OneHouseWork aw=new OneHouseWork(activity, mainView);
			aw.setShowCheckBox(showCheckBox);//控制是否显示复选框
			aw.getViewById(area, handler, listMatView,flag);						
    	}
		
	}
	
	/**点击后设置背景不一样,传过来的是完整位置码_btn的文字内容**/
	public void setBtnBackground(String objVal)
	{
		int len=listMatView.size();
		LogUtil.i(TAG, "共有多少个view:"+len);
		View view=null;
		Button btn=null;
		for(int i=0;i<len;i++)
		{
			view=listMatView.get(i);
			btn=(Button) view.findViewById(R.id.button);//如：6架6栏6层
			//包含内容
			//LogUtil.i(TAG, "按钮的文字:"+btn.getText().toString());
			//LogUtil.i(TAG, "位置的文字:"+objVal);
			if(null!=objVal && objVal.contains(btn.getText().toString()))
			{								
				btn.setBackgroundResource(R.drawable.btn_yellow); 	
			}else
			{
				btn.setBackgroundResource(R.drawable.btn_blue); 	
			}
		}		
	}						
}

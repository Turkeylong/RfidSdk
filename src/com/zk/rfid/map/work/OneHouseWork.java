package com.zk.rfid.map.work;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;

import com.zk.rfid.R;
import com.zk.rfid.main.util.ConfigUtil;
import com.zk.rfid.map.dao.MapDataDao;
import com.zk.rfid.map.dao.MapViewBtn;
import com.zk.rfid.util.CommUtil;
import com.zk.rfid.util.ConstantUtil;

/**
 * @Description:1仓库区域 
 * @date:2016-6-28上午10:21:35
 * @author: ldl
 */
public class OneHouseWork
{
	private static final String TAG="OneHouseWork";
	
	private LinearLayout one_oneLin,one_twoLin,one_threeLin,one_fourLin,one_fiveLin;//1区
	private LinearLayout one_sixLin,one_sevenLin;//1区
	
	private LinearLayout two_oneLin,two_twoLin,two_threeLin,two_fourLin,two_fiveLin;//2区
	private LinearLayout three_oneLin,three_twoLin,three_threeLin,three_fourLin,three_fiveLin;//3区
	private LinearLayout three_sixLin,three_sevenLin,three_eighthLin,three_nineLin,three_tenLin;//3区
	
	private LinearLayout five_oneLin,five_twoLin,five_threeLin,five_fourLin,five_fiveLin;//5区
	private LinearLayout four_oneLin;//4区
	
	private View mainView;
	private Activity activity;
	private Handler handler;
	private List<View> listMatView;
	private Map<String,String> storageMap;//统计架位上的存货数据
	
	private boolean showCheckBox=false;
	
	public OneHouseWork(Activity activity,View mainView)
	{
		this.activity=activity;
		this.mainView=mainView;
	}
	
	/**各区初始控件**/
	public void getViewById(String area,Handler handler,List<View> listMatView,boolean flag)
	{
		this.handler=handler;
		this.listMatView=listMatView;
		storageMap=ConfigUtil.getStorageInfo(activity);
		
		if(ConstantUtil.ONE_AREA.equals(area))
		{
			one_oneLin=getLinearLayout(R.id.one_oneLin);	
			one_twoLin=getLinearLayout(R.id.one_twoLin);			
			one_threeLin=getLinearLayout(R.id.one_threeLin);	
			one_fourLin=getLinearLayout(R.id.one_fourLin);	
			one_fiveLin=getLinearLayout(R.id.one_fiveLin);	
			one_sixLin=getLinearLayout(R.id.one_sixLin);	
			one_sevenLin=getLinearLayout(R.id.one_sevenLin);	
			showOneArea(flag);
		}
		
		if(ConstantUtil.TWO_AREA.equals(area))
		{
			two_oneLin=getLinearLayout(R.id.two_oneLin);
			two_twoLin=getLinearLayout(R.id.two_twoLin);
			two_threeLin=getLinearLayout(R.id.two_threeLin);
			two_fourLin=getLinearLayout(R.id.two_fourLin);	
			two_fiveLin=getLinearLayout(R.id.two_fiveLin);			
			showTwoArea(flag);
		}
		
		if(ConstantUtil.THREE_AREA.equals(area))
		{
			three_oneLin=getLinearLayout(R.id.three_oneLin);
			three_twoLin=getLinearLayout(R.id.three_twoLin);
			three_threeLin=getLinearLayout(R.id.three_threeLin);
			three_fourLin=getLinearLayout(R.id.three_fourLin);	
			three_fiveLin=getLinearLayout(R.id.three_fiveLin);			
			three_sixLin=getLinearLayout(R.id.three_sixLin);	
			three_sevenLin=getLinearLayout(R.id.three_sevenLin);
			three_eighthLin=getLinearLayout(R.id.three_eighthLin);
			three_nineLin=getLinearLayout(R.id.three_nineLin);
			three_tenLin=getLinearLayout(R.id.three_tenLin);
			showThreeArea(flag);
		}
		

		if(ConstantUtil.FOUR_AREA.equals(area))
		{
			four_oneLin=getLinearLayout(R.id.four_oneLin);					
			showFourArea(flag);
		}
		
		if(ConstantUtil.FIVE_AREA.equals(area))
		{
			five_oneLin=getLinearLayout(R.id.five_oneLin);
			five_twoLin=getLinearLayout(R.id.five_twoLin);
			five_threeLin=getLinearLayout(R.id.five_threeLin);
			five_fourLin=getLinearLayout(R.id.five_fourLin);	
			five_fiveLin=getLinearLayout(R.id.five_fiveLin);
			
			showFiveArea(flag);
		}
		
		
	}
	
	/**1区图**/
	private void showOneArea(boolean flag)
	{
		LinearLayout lin=null;
		int count=7;//6架全都是2层3栏,第七架是2层2栏
		for(int k=1;k<=count;k++)
		{
			int leve=3,player=2;//1架3栏2层
			if(k==1)
			{
				leve=2;//第1是2栏2层	
			}
			for(int i=1;i<=player;i++)
			{
				lin=CommUtil.getLinearLayout(activity, 0);			
				for(int j=1;j<=leve;j++)
				{
					String code=MapDataDao.getFrameCode(k, j, i, ConstantUtil.ONE_AREA);
					View view=getMapButtonView(code, flag);
					lin.addView(view);
				
					listMatView.add(view);					
				}
				if(1==k)
				{
					one_oneLin.addView(lin);	
				}	
				if(2==k)
				{
					one_twoLin.addView(lin);	
				}
				if(3==k)
				{
					one_threeLin.addView(lin);	
				}
				if(4==k)
				{
					one_fourLin.addView(lin);	
				}	
				if(5==k)
				{				
					one_fiveLin.addView(lin);	
				}
				if(6==k)
				{				
					one_sixLin.addView(lin);	
				}
				if(7==k)
				{				
					one_sevenLin.addView(lin);	
				}
			}
		}
	}
	
	/**2区图**/
	private void showTwoArea(boolean flag)
	{
		LinearLayout lin=null;
		int count=5;//5架全都是3层4栏
		for(int k=1;k<=count;k++)
		{
			int leve=4,player=3;
			for(int i=1;i<=player;i++)
			{
				lin=CommUtil.getLinearLayout(activity, 0);			
				for(int j=leve;j>=1;j--)
				{
					String code=MapDataDao.getFrameCode(k, j, i, ConstantUtil.TWO_AREA);
					View view=getMapButtonView(code, flag);
					
					lin.addView(view);
					listMatView.add(view);					
				}
				if(1==k)
				{
					two_oneLin.addView(lin);	
				}	
				if(2==k)
				{
					two_twoLin.addView(lin);	
				}
				if(3==k)
				{
					two_threeLin.addView(lin);	
				}
				if(4==k)
				{
					two_fourLin.addView(lin);	
				}	
				if(5==k)
				{				
					two_fiveLin.addView(lin);	
				}
			}
		}
	}
	/**3区图**/
	private void showThreeArea(boolean flag)
	{
		LinearLayout lin=null;
		int count=10;//10架全都是2层2栏
		for(int k=1;k<=count;k++)
		{
			int leve=2,player=2;
			for(int i=1;i<=player;i++)
			{
				lin=CommUtil.getLinearLayout(activity, 0);			
				for(int j=1;j<=leve;j++)
				{
					String code=MapDataDao.getFrameCode(k, j, i, ConstantUtil.THREE_AREA);
					View view=getMapButtonView(code, flag);
					
					lin.addView(view);
					
					listMatView.add(view);					
				}
											
				if(1==k)
				{
					three_oneLin.addView(lin);	
				}	
				if(2==k)
				{
					three_twoLin.addView(lin);	
				}
				if(3==k)
				{
					three_threeLin.addView(lin);	
				}
				if(4==k)
				{
					three_fourLin.addView(lin);	
				}	
				if(5==k)
				{				
					three_fiveLin.addView(lin);	
				}
				if(6==k)
				{				
					three_sixLin.addView(lin);	
				}
				if(7==k)
				{				
					three_sevenLin.addView(lin);	
				}
				if(8==k)
				{				
					three_eighthLin.addView(lin);	
				}
				if(9==k)
				{				
					three_nineLin.addView(lin);	
				}
				if(10==k)
				{				
					three_tenLin.addView(lin);	
				}
			}
		}
	}
	/**4区图**/
	private void showFourArea(boolean flag)
	{
		LinearLayout lin=null;
		int count=1;//4区1架3层2栏
		for(int k=1;k<=count;k++)
		{
			int leve=2,player=3;
			for(int i=1;i<=player;i++)
			{
				lin=CommUtil.getLinearLayout(activity, 0);			
				for(int j=1;j<=leve;j++)
				{
					String code=MapDataDao.getFrameCode(k, j, i, ConstantUtil.FOUR_AREA);
					View view=getMapButtonView(code, flag);
					
					lin.addView(view);
					listMatView.add(view);					
				}
				if(1==k)
				{
					four_oneLin.addView(lin);	
				}					
			}
		}
	}
	/**5区图**/
	private void showFiveArea(boolean flag)
	{
		LinearLayout lin=null;
		int count=10;//5架全都是3层4栏,从6到10
		for(int k=6;k<=count;k++)
		{
			int leve=4,player=3;
			for(int i=1;i<=player;i++)
			{
				lin=CommUtil.getLinearLayout(activity, 0);			
				for(int j=1;j<=leve;j++)
				{
					String code=MapDataDao.getFrameCode(k, j, i, ConstantUtil.FIVE_AREA);
					View view=getMapButtonView(code, flag);
					
					lin.addView(view);
					listMatView.add(view);					
				}
				if(10==k)
				{
					five_oneLin.addView(lin);	
				}	
				if(9==k)
				{
					five_twoLin.addView(lin);	
				}
				if(8==k)
				{
					five_threeLin.addView(lin);	
				}
				if(7==k)
				{
					five_fourLin.addView(lin);	
				}	
				if(6==k)
				{				
					five_fiveLin.addView(lin);	
				}
			}
		}
	}
	
	private View getMapButtonView(String posCode,boolean storageFlag)
	{
		View view=new MapViewBtn(activity, handler).getBtnView(posCode, storageFlag, storageMap, getShowCheckBox());
		return view;
	}
	
	private LinearLayout getLinearLayout(int id)
	{
		LinearLayout lin=(LinearLayout) mainView.findViewById(id);
		lin.removeAllViews();
		return lin;
	}
	
	public boolean getShowCheckBox()
	{
		return showCheckBox;
	}

	public void setShowCheckBox(boolean showCheckBox)
	{
		this.showCheckBox = showCheckBox;
	}
}

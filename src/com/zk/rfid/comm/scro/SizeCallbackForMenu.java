package com.zk.rfid.comm.scro;

import android.view.View;

import com.zk.rfid.comm.scro.HoScrollView.SizeCallback;

public class SizeCallbackForMenu implements SizeCallback
{

	 private int btnWidth;
	 private  View btnSlide;

      public SizeCallbackForMenu(View btnSlide) 
      {
          super();
          this.btnSlide = btnSlide;
      }

      @Override
      public void onGlobalLayout() 
      {
          btnWidth = btnSlide.getMeasuredWidth();
          //System.out.println("btnWidth=" + btnWidth);
      }

     
      @Override
      public void getViewSize(int idx, int w, int h, int[] dims,int args)
      {
          dims[0] = w;
          dims[1] = h;
          int menuIdx = 0;
          if(args==0)
          {
        	  args=3;
          }
          if (idx == menuIdx) 
          {
             // dims[0] = w - btnWidth*4;//控制左侧菜单的宽度
        	  dims[0] = w/args;//占屏幕几分之几
          }
      }

}

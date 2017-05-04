package com.zk.rfid.comm.scro;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.HorizontalScrollView;

/**
 * A HorizontalScrollView (HSV) implementation that disallows touch events (so
 * no scrolling can be done by the user).
 * 
 * This HSV MUST contain a single ViewGroup as its only child, and this
 * ViewGroup will be used to display the children Views passed in to the
 * initViews() method.
 */
public class HoScrollView extends HorizontalScrollView 
{ 
	public HoScrollView(Context context, AttributeSet attrs,int defStyle) 
	{
		super(context, attrs, defStyle);
		init(context);
	}

	public HoScrollView(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		init(context);
	}

	public HoScrollView(Context context)
	{
		super(context);
		init(context);
	}

	void init(Context context) 
	{
		// remove the fading as the HSV looks better without it
		setHorizontalFadingEdgeEnabled(false);
		setVerticalFadingEdgeEnabled(false);
	}
	
	/**初始化数据**/
//	public void initView(ScrollView scrollView,int menuWidth,View leftView)
//	{
//		this.menuWidth=menuWidth;
//		this.scrollView=scrollView;
//		this.leftView=leftView;
//	}
	
	

	/**
	 * @param children
	 *            The child Views to add to parent.
	 * @param scrollToViewIdx
	 *            The index of the View to scroll to after initialisation.
	 * @param sizeCallback
	 *            A SizeCallback to interact with the HSV.
	 */
	public void initViews(View[] children, int scrollToViewIdx,
			SizeCallback sizeCallback) 
	{
		// A ViewGroup MUST be the only child of the HSV
		ViewGroup parent = (ViewGroup) getChildAt(0);

		// Add all the children, but add them invisible so that the layouts are
		// calculated, but you can't see the Views
		for (int i = 0; i < children.length; i++) 
		{
			// children[i].setVisibility(View.INVISIBLE);
			parent.addView(children[i]);
		}

		// Add a layout listener to this HSV
		// This listener is responsible for arranging the child views.
		OnGlobalLayoutListener listener = new MyOnGlobalLayoutListener(parent,children, scrollToViewIdx, sizeCallback);
		getViewTreeObserver().addOnGlobalLayoutListener(listener);
	}

	
	/**
	 * An OnGlobalLayoutListener impl that passes on the call to onGlobalLayout
	 * to a SizeCallback, before removing all the Views in the HSV and adding
	 * them again with calculated widths and heights.
	 */
	class MyOnGlobalLayoutListener implements OnGlobalLayoutListener
	{
		ViewGroup parent;
		View[] children;
		int scrollToViewIdx;
		int scrollToViewPos = 0;
		SizeCallback sizeCallback;

		/**
		 * @param parent
		 *            The parent to which the child Views should be added.
		 * @param children
		 *            The child Views to add to parent.
		 * @param scrollToViewIdx
		 *            The index of the View to scroll to after initialisation.
		 * @param sizeCallback
		 *            A SizeCallback to interact with the HSV.
		 */
		public MyOnGlobalLayoutListener(ViewGroup parent, View[] children,int scrollToViewIdx, SizeCallback sizeCallback) 
		{
			this.parent = parent;
			this.children = children;
			this.scrollToViewIdx = scrollToViewIdx;
			this.sizeCallback = sizeCallback;
		}

		@Override
		public void onGlobalLayout() 
		{
			// System.out.println("onGlobalLayout");

			final HoScrollView me = HoScrollView.this;

			// The listener will remove itself as a layout listener to the HSV
			me.getViewTreeObserver().removeGlobalOnLayoutListener(this);

			// Allow the SizeCallback to 'see' the Views before we remove them
			// and re-add them.
			// This lets the SizeCallback prepare View sizes, ahead of calls to
			// SizeCallback.getViewSize().
			sizeCallback.onGlobalLayout();

			parent.removeViewsInLayout(0, children.length);

			final int w = me.getMeasuredWidth();
			final int h = me.getMeasuredHeight();

			// System.out.println("w=" + w + ", h=" + h);

			// Add each view in turn, and apply the width and height returned by
			// the SizeCallback.
			int[] dims = new int[2];
			scrollToViewPos = 0;
			for (int i = 0; i < children.length; i++) 
			{
				sizeCallback.getViewSize(i, w, h, dims,getLeftMenuArgs());
				// System.out.println("addView w=" + dims[0] + ", h=" +
				// dims[1]);
				children[i].setVisibility(View.VISIBLE);
				parent.addView(children[i], dims[0], dims[1]);
				if (i < scrollToViewIdx) {
					scrollToViewPos += dims[0];
				}
			}

			// For some reason we need to post this action, rather than call
			// immediately.
			// If we try immediately, it will not scroll.
			new Handler().post(new Runnable() 
			{
				@Override
				public void run() 
				{
					me.scrollBy(scrollToViewPos, 0);
				}
			});
		}
	}
	
	
	
	//左侧滑操作
    private int begin = 2;  // 标识 1 左边   2 右边
	private float startX = 0;
    private float endX = 0;
    private boolean isFirst = true;
    private int menuWidth;
    
	public boolean onTouchEvent(MotionEvent event,HoScrollView hs,View leftView)
	{
		switch (event.getAction()) 
    	{
		case MotionEvent.ACTION_MOVE :
			if(isFirst) 
			{
				isFirst = false;
				startX = event.getX();
			}
			break;
		case MotionEvent.ACTION_UP :
			isFirst = true;
			endX = event.getX();
			menuWidth = leftView.getMeasuredWidth();
			if(endX - startX > 50) 
			{
				//向右划
				hs.smoothScrollTo(0, 0);
				hs.scrollTo(0, 0);
				begin = 1; // 到左边了
			}else if(endX - startX < -50) 
			{
				begin = 2; 
				hs.smoothScrollTo(menuWidth, 0);
				hs.scrollTo(menuWidth, 0);
			}else
			{
				if(begin == 1)
				{
					//原来在左边
					hs.smoothScrollTo(0, 0);
					hs.scrollTo(0, 0);
				}else 
				{
					hs.smoothScrollTo(menuWidth, 0);
					hs.scrollTo(menuWidth, 0);
				}
			}
			break;
		}		
		return hs.onTouchEvent(event);
	}
	
	/**控制界面移动到左边**/
	public void moveToLeft(HoScrollView hs,View leftView)
	{
		menuWidth = leftView.getMeasuredWidth();		
		hs.smoothScrollTo(-menuWidth, 0);
		hs.scrollTo(-menuWidth, 0);		
	}

	private int leftMenuArgs=0;
	
	public int getLeftMenuArgs() 
	{
		return leftMenuArgs;
	}

	public void setLeftMenuArgs(int leftMenuArgs) 
	{
		this.leftMenuArgs = leftMenuArgs;
	}


	/**
	 * Callback interface to interact with the HSV.
	 */
	public interface SizeCallback 
	{
		/**
		 * Used to allow clients to measure Views before re-adding them.
		 */
		public void onGlobalLayout();

		/**
		 * Used by clients to specify the View dimensions.
		 * 
		 * @param idx
		 *            Index of the View.
		 * @param w
		 *            Width of the parent View.
		 * @param h
		 *            Height of the parent View.
		 * @param dims
		 *            dims[0] should be set to View width. dims[1] should be set
		 *            to View height.
		 *  @param args 左侧菜单占屏幕的比例    ,几分之几
		 */
		public void getViewSize(int idx, int w, int h, int[] dims,int args);
	}
	   
}

package com.zk.rfid.util;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;
/**
 * @Description: activity集合控制器 
 * @date:2014-9-23 下午4:45:22
 * @author: 罗德禄
 */
public class SysApplication extends Application
{

		//运用list来保存们每一个activity是关键
	    private List<Activity> mList = new LinkedList<Activity>();
	    //为了实现每次使用该类时不创建新的对象而创建的静态对象
	    private static SysApplication instance; 
	    //构造方法
	    private SysApplication(){}
	    //实例化一次
	    public synchronized static SysApplication getInstance()
	    { 
	        if (null == instance) { 
	            instance = new SysApplication(); 
	        } 
	        return instance; 
	    } 
	    // add Activity  
	    public void addActivity(Activity activity) 
	    { 
	        mList.add(activity); 
	    } 
	    //关闭每一个list内的activity
	    public void exit() 
	    { 
	        try { 
	            for (Activity activity:mList) 
	            { 
	                if (activity != null) 
	                    activity.finish(); 
	            } 
	        } catch (Exception e) 
	        { 
	            e.printStackTrace(); 
	        }
	    } 
	    //杀进程
	    public void onLowMemory() 
	    { 
	        super.onLowMemory();     
	        System.gc(); 
	    }  
	}
	 

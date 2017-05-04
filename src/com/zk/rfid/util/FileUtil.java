package com.zk.rfid.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class FileUtil
{
	//设置防反编开始
	private static String  SET_str1=""; 		
	private static boolean SET_BOOLEAN_1=false;    
	private static boolean SET_boolean_2=false;       
	private static String  SET_STR2="";			
	//设置防反编结束
	
	private static final String TAG = "FileUtil";

	public static String getFileToString(String path, String ecod) 
	{
		String content = "";
		InputStreamReader in = null;
		BufferedReader br = null;
		try
		{
			if (!"".equals(ecod)) {
				in = new InputStreamReader(new FileInputStream(path), ecod);
			} else {
				in = new InputStreamReader(new FileInputStream(path));
			}
			br = new BufferedReader(in);
			StringBuilder sb = new StringBuilder();
			while ((content = br.readLine()) != null) {
				sb.append(content).append("\r\n");
			}
			content = sb.toString();
			br.close();
			in.close();
		} catch (Exception e) 
		{
			e.printStackTrace();
		}
		return content;
	}
	
	//读取文件内容,传入编码，逐行地读，并添加到list中
	public static List<String> getContentList(String path, String ecod) 
	{
		List<String> contentList = new ArrayList<String>();
		String content="";
		InputStreamReader in = null;
		BufferedReader br = null;
		try {
			if (!"".equals(ecod)&&null!=ecod)
			{
				in = new InputStreamReader(new FileInputStream(path), ecod);
			} else
			{
				in = new InputStreamReader(new FileInputStream(path));
			}
			br = new BufferedReader(in);
		
			while ((content = br.readLine()) != null) 
			{
				contentList.add(content);
			}		
			br.close();
			in.close();
		} catch (Exception e) 
		{
			e.printStackTrace();
		}
		return contentList;
	}

	//将内容写到文件中
	public static void writeStringToFile(String path, String content) 
	{
		FileOutputStream outStream = null;
		try 
		{
			outStream = new FileOutputStream(path);
			outStream.write(content.getBytes());
			outStream.close();
		} catch (Exception e) {
			Log.e(TAG,"字符串写到文件中"+ e.getMessage());
		} finally {
			if (outStream != null) {
				outStream=null;
			}
		}
	}
	
	//创建文件夹
	public static void createFold(String foldPath)
	{
		File file=new File(foldPath);
		if(!file.exists())
		{
			file.mkdirs();	
		}
	}
	
	//删除
	public static void deleteFile(String foldPath)
	{
		File file=new File(foldPath);
		if(file.exists()&&file.isFile())
		{
			file.delete();	
		}else if(file.isDirectory())
		{   //否则如果它是一个目录
			File files[] = file.listFiles();     
			int len=files.length;//声明目录下所有的文件 files[];
			for(int i=0;i<len;i++)
			{  
				deleteFile(files[i].getPath());//把每个文件 用这个方法进行迭代
			} 
		}
		file.delete();
	}

	public static void main(String[] args) 
	{
		String pa="E:\\serWork\\TestBat\\tbbb";
		deleteFile(pa);
	}
}

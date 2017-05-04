package com.zk.rfid.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * @Description: 流数据工具类 
 * @date:2014-9-23 下午4:45:22
 * @author: 罗德禄
 */
public class StreamUtil
{

	//设置防反编开始
	private static String  SET_str1=""; 		
	private static boolean SET_BOOLEAN_1=false;    
	private static boolean SET_boolean_2=false;       
	private static String  SET_STR2="";			
	//设置防反编结束
	
	/**
	 * 从输入流中获取数据(二进制)
	 * 
	 * @param inStream
	 * @return
	 * @throws Exception
	 */
	public static byte[] readInputStream(InputStream inStream) throws Exception 
	{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] bytes=null;
		byte[] buffer = new byte[1024]; // 1K 缓冲区
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) 
		{
			outStream.write(buffer, 0, len);
		}
		
		bytes=outStream.toByteArray();
		outStream.close();
		
		return bytes;
	}

	/**
	 * 网络获取输出流
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public static InputStream getInputStreamFromUrl(String url) throws IOException {
		URL u = new URL(url);
		URLConnection conn = u.openConnection();
		InputStream inputStream = conn.getInputStream();
		return inputStream;
	}

	/**
	 * InputStream --> String
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static String inputStreamToString(InputStream is) throws IOException
	{
		BufferedReader in = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8192);
		StringBuffer sb = new StringBuffer();
		String line = null;
		while (null != (line = in.readLine())) 
		{
			sb.append(line + "\n");
		}
		in.close();
		return sb.toString();
	}

	/**
	 * String --> InputStream
	 * 
	 * @param str
	 * @return
	 */
	public static InputStream stringToInputStream(String str) {
		ByteArrayInputStream inStream = new ByteArrayInputStream(str.getBytes());
		return inStream;
	}

	/**
	 * InputStream --> File
	 * 
	 * @param is
	 * @param file
	 * @throws IOException
	 */
	public static void inputStreamToFile(InputStream is, File file) throws Exception {
		OutputStream os = new FileOutputStream(file);
		int len = -1;
		byte[] buffer = new byte[1024];
		while ((len = is.read(buffer, 0, buffer.length)) != -1) 
		{
			os.write(buffer, 0, len);
		}
		os.close();
	}

	/**
	 * File --> InputStream
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 * 
	 */
	public static InputStream fileToInputStream(File file) throws IOException {
		InputStream inStream = new FileInputStream(file);
		return inStream;
	}
	
	/**
	 * 
	 * @param 网络url
	 * @return byte
	 */
	public static byte[] getImageByteFromUrl(String path) 
	{
		byte[] res=null;	
        URL url=null;
        InputStream inputStream=null;
        HttpURLConnection conn =null;
		try
		{
			url = new URL(path);
			conn= (HttpURLConnection)url.openConnection();
		    conn.setDoInput(true);
		    conn.connect(); 
		    inputStream=conn.getInputStream();		     
	        res = readInputStream(inputStream); 
	        inputStream.close();
	        conn.disconnect();		        
		} catch (MalformedURLException e) 
		{			
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		} catch (Exception e) 
		{
			e.printStackTrace();
		} finally
		{
			conn=null;
			inputStream=null;
			url=null;
		}   		
        return res;                   
	}
	
	
}

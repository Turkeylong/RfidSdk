package com.zk.rfid.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

/**
 * 辅助类，为整个应用程序提供唯一的一个HttpClient对象。
 * 这个对象有一些初始化的属性连接属性，这些属性可以被HttpGet、HttpPost的属性覆盖
 */
public class HttpClientTool
{
	//设置防反编开始
	private static String  SET_str1=""; 		
	private static boolean SET_BOOLEAN_1=false;    
	private static boolean SET_boolean_2=false;       
	private static String  SET_STR2="";			
	//设置防反编结束
	
	private static final String TAG = "HttpClientTool";
	
	private static HttpClient httpClient;

	private HttpClientTool()
	{
	}

	public static synchronized HttpClient getHttpClient() 
	{
		if (null == httpClient)
		{
			// 初始化工作
			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET);
			HttpProtocolParams.setUseExpectContinue(params, true);

			ConnManagerParams.setTimeout(params, 10000); // 连接管理器超时
			HttpConnectionParams.setConnectionTimeout(params, 10000); // 连接超时
			HttpConnectionParams.setSoTimeout(params, 10000); // Socket超时

			SchemeRegistry schReg = new SchemeRegistry();
			schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 80));

			ClientConnectionManager conManager = new ThreadSafeClientConnManager(params, schReg);

			httpClient = new DefaultHttpClient(conManager, params);
		}

		return httpClient;
	}
	
	
	  
		public static String getPostDataByHttp(String path,Map<String, String> map) 
		{
			InputStream inputStream = null;
			OutputStream outputStream = null;
			String content = null; 
			HttpURLConnection connection = null;
			StringBuffer buffer = null;
			try
			{
				URL url = new URL(path);
				//LogUtil.i(TAG, "访问HTTP:" + path );
				connection = (HttpURLConnection) url.openConnection();
				connection.setDoInput(true);
				connection.setDoOutput(true);
				connection.setConnectTimeout(10*1000);
				connection.setReadTimeout(10*1000);
				connection.setRequestMethod("POST");
				
				buffer = new StringBuffer();
				if (null != map && !map.isEmpty())
				{						 
					for (Map.Entry<String, String> entry : map.entrySet()) 
					{
						buffer.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
					}
					buffer.deleteCharAt(buffer.length() - 1);
				}
				byte[] data = buffer.toString().getBytes();
				connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
				connection.setRequestProperty("Content-Length",String.valueOf(data.length));
				outputStream = connection.getOutputStream();
				outputStream.write(data);
				
				if (connection.getResponseCode() == HttpStatus.SC_OK)
				{
					inputStream = connection.getInputStream();
					content = StreamUtil.inputStreamToString(inputStream);
					inputStream.close();
				} 
				
				outputStream.close();			
				connection.disconnect();			
			} catch (Exception e) 
			{
				LogUtil.i(TAG, "访问:" + path +"?"+ buffer.toString() + "获取数据异常:"+e.getMessage());
				e.printStackTrace();
			} finally 
			{
				if (null != outputStream) 
				{
					outputStream = null;
				}
				if (null != inputStream) 
				{
					inputStream = null;
				}
				if (null != connection) 
				{
					connection = null;
				}
			}
			return content;
		}
		
		
		/**非葡萄平台向外发起的HTTP请求**/
		public static String getContentByUrl(String url) 
		{
			String res="";
			try 
			{
				String array[] = url.split("[?]");
				String path = array[0];
	            LogUtil.i(TAG, "改用post请求HTTP："+path);
	            LogUtil.i(TAG, "改用post请求Map："+getPostMapByUrl(array[1]));
	            
	            res= getPostDataByHttp(path, getPostMapByUrl(array[1]));			
			} catch (Exception e) 
			{
				LogUtil.e(TAG, "改用post请求获取信息异常:" + e.getMessage());
			}
			LogUtil.i(TAG, "改用post请求返回结果："+res);
			return res;
		}
		
		public static Map<String, String> getPostMapByUrl(String url) 
		{
			LogUtil.i(TAG, "封装前URL："+url);
			Map<String, String> map = new HashMap<String, String>();
			String array[] = url.split("&");
			if (null != array && array.length > 0)
			{            
				int len=array.length;
				for(int i=0;i<len;i++)
				{
					try
					{
						String ary[]=array[i].split("=");					
						map.put(ary[0], ary[1]);
					}catch(Exception e)
					{
						LogUtil.e(TAG, "有参数异常:"+e.getMessage());
						continue;//避免有个别非必须参数上传导致异常跳出
					}				
				}
			}
			if(null!= map && map.size()>0)
			{
				LogUtil.i(TAG, "封装map："+map);
			}
			return map;
		}
}
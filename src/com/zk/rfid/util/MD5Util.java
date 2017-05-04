package com.zk.rfid.util;

import java.security.MessageDigest;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

public class MD5Util 
{
	public final static String getMd5(String s) 
	{
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };
		try
		{
			byte[] strTemp = s.getBytes("UTF-8");
			MessageDigest mdTemp = MessageDigest.getInstance("MD5");
			mdTemp.update(strTemp);
			byte[] md = mdTemp.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++)
			{
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			return null;
		}
	}
	
	
	 public static String getApkMd5Val(Context context) 
	 {
			String MD5Val="";
			try 
			{
				PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
				Signature signatures = pi.signatures[0];
				MessageDigest md = MessageDigest.getInstance("MD5");
				md.update(signatures.toByteArray());
				byte[] digest = md.digest();
				MD5Val = toHexString(digest);				
			}catch (Exception e)
			{
				e.printStackTrace();
			}
						
			return MD5Val;
		}
	 
		private static String toHexString(byte[] block)
		{
			StringBuffer buf = new StringBuffer();
			int len = block.length;

			for (int i = 0; i < len; i++) 
			{
				byte2hex(block[i], buf);
				if (i < len - 1)
				{
					buf.append(":");
				}
			}
			return buf.toString();

		}
		private static void byte2hex(byte b, StringBuffer buf)
		{
			char[] hexChars = { '0', '1', '2', '3', '4', '5', '6', '7', '8','9', 'A', 'B', 'C', 'D', 'E', 'F' };
			int high = ((b & 0xf0) >> 4);
			int low = (b & 0x0f);
			buf.append(hexChars[high]);
			buf.append(hexChars[low]);
		}
}

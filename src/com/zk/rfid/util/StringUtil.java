package com.zk.rfid.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** 字符串操作的工具类 **/
public class StringUtil
{
	// 设置防反编开始
	private static String SET_str1 = "";
	private static boolean SET_BOOLEAN_1 = false;
	private static boolean SET_boolean_2 = false;
	private static String SET_STR2 = "";
	// 设置防反编结束

	private static final String TAG = "StringUtil";
	private static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";
	private static final String DEFAULT_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

	/** 格式化日期字符串 yyyy-MM-dd **/
	public static String formatDate(Date date)
	{
		return formatDate(date, DEFAULT_DATE_PATTERN);
	}

	/** 格式化日期时间字符串yyyy-MM-dd HH:mm:ss **/
	public static String formatDateTime(Date date)
	{
		return formatDate(date, DEFAULT_DATE_TIME_PATTERN);
	}

	/** 格式化获取当前时间 yyyy-MM-dd **/
	public static String getDate()
	{
		return formatDate(new Date(), DEFAULT_DATE_PATTERN);
	}

	/** 格式化获取当前时间yyyy-MM-dd HH:mm:ss **/
	public static String getDateTime()
	{
		return formatDate(new Date(), DEFAULT_DATE_TIME_PATTERN);
	}

	/** 格式化日期字符串 **/
	public static String formatDate(Date date, String pattern)
	{
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		return format.format(date);
	}

	/** 字符串为空判断 **/
	public static boolean isStrEmpty(String str)
	{
		return (null == str || str.length() == 0);
	}

	/** 日期格式字符串取代/ **/
	public static String formatDateByString(String oldStr)
	{
		String newStr = "";
		try
		{
			newStr = oldStr.replaceAll("\\/", "-");
			newStr = formatDateToObj(newStr, DEFAULT_DATE_PATTERN);
		} catch (Exception e)
		{
			//e.printStackTrace();
			newStr = oldStr;
		}
		return newStr;
	}

	/** 日期格式字符串转换成指定字符串 **/
	public static String formatDateToObj(String oldStr, String objStrType)
	{
		String newStr = "";
		if (null != oldStr)
		{
			try
			{
				SimpleDateFormat sdf = new SimpleDateFormat(objStrType);
				Date da = sdf.parse(oldStr);
				newStr = sdf.format(da);
			} catch (Exception e)
			{
				// LogUtil.i(TAG, "返回原字符：" + oldStr);
				//e.printStackTrace();
				LogUtil.i(TAG, "转换出错,返回原字符：" + oldStr);
				newStr = oldStr;
			}
		}
		return newStr;
	}

	public static String formatSplite(String oldStr)
	{
		String newStr = "";
		if (null != oldStr)
		{
			try
			{	
				oldStr = oldStr.replaceAll("\\/", "-");
				newStr = oldStr.replaceAll("-", "");
			} catch (Exception e)
			{
				// LogUtil.i(TAG, "返回原字符：" + oldStr);
				e.printStackTrace();
				LogUtil.i(TAG, "转换出错,返回原字符：" + oldStr);
				newStr = oldStr;
			}
		}
		return newStr;
	}

	/**
	 * 将字符串时间转换成 （yyyy年MM月dd日 HH时mm分）格式
	 * 
	 * @param str
	 * @return 成功返回正确字符串，失败返回空字符串
	 */
	public static String Convert(String str)
	{
		String dateStr = "";
		Date date = null;
		try
		{
			DateFormat formatOld = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+08:00");
			DateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分");
			date = formatOld.parse(str);
			if (null != date)
			{
				dateStr = format.format(date);
			}
		} catch (Exception e)
		{
			LogUtil.e(TAG, e.getMessage());
		}
		return dateStr;
	}

	/**
	 * 将字符串时间转换成 （yyyy年MM月dd日 HH时mm分）格式
	 * 
	 * @param str
	 * @return 成功返回正确字符串，失败返回空字符串
	 */
	public static String ConvertString(String str)
	{
		String dateStr = "";
		Date date = null;
		try
		{
			DateFormat formatOld = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			DateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分");
			date = formatOld.parse(str);
			if (null != date)
			{
				dateStr = format.format(date);
			}
		} catch (Exception e)
		{
			LogUtil.e(TAG, e.getMessage());
		}
		return dateStr;
	}

	/**
	 * 将字符串时间转换成指定格式
	 * 
	 * @param str
	 * @return 成功返回正确字符串，失败返回空字符串
	 */
	public static String Convert(String str, String pattern)
	{
		String dateStr = "";
		Date date = null;
		try
		{
			DateFormat formatOld = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+08:00");
			DateFormat format = new SimpleDateFormat(pattern);
			date = formatOld.parse(str);
			if (date != null)
			{
				dateStr = format.format(date);
			}
		} catch (Exception e)
		{
			LogUtil.e(TAG, e.getMessage());
			e.printStackTrace();
		}
		return dateStr;
	}

	/**
	 * 将字符串时间转换成指定格式
	 * 
	 * @param str
	 * @return 成功返回正确字符串，失败返回空字符串
	 */
	public static String ConvertString2(String str, String pattern)
	{
		String dateStr = "";
		Date date = null;
		try
		{
			DateFormat formatOld = new SimpleDateFormat("yyyy/MM/dd");
			DateFormat format = new SimpleDateFormat(pattern);
			date = formatOld.parse(str);
			if (date != null)
			{
				dateStr = format.format(date);
			}
		} catch (Exception e)
		{
			LogUtil.e(TAG, e.getMessage());
			e.printStackTrace();
		}
		return dateStr;
	}

	/**
	 * 将字符串时间转换成指定格式
	 * 
	 * @param str
	 * @return 成功返回正确字符串，失败返回空字符串
	 */
	public static String ConvertString(String str, String pattern)
	{
		String dateStr = "";
		Date date = null;
		try
		{
			DateFormat formatOld = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			DateFormat format = new SimpleDateFormat(pattern);
			date = formatOld.parse(str);
			if (date != null)
			{
				dateStr = format.format(date);
			}
		} catch (Exception e)
		{
			LogUtil.e(TAG, e.getMessage());
			e.printStackTrace();
		}
		return dateStr;
	}

	/**
	 * 将纯字符串出生日期(例 20121031)转换成标准日期格式
	 * 
	 * @param str
	 *            出生日期
	 * @param args
	 *            连接符号
	 * @return
	 */
	public static String ConverBirthDay(String str, String args)
	{
		String spilt = "/";
		if (!args.equals(""))
			spilt = args;
		if (str.equals("") || str.length() != 8)
			return "";
		String birthDate = "";
		try
		{
			String year = str.substring(0, 4);
			String month = str.substring(4, 6);
			String day = str.substring(6);
			birthDate = year + spilt + month + spilt + day;
		} catch (Exception e)
		{
			birthDate = "";
			LogUtil.e(TAG, e.getMessage());
		}
		return birthDate;
	}

	/** 回传为字符串数组:[0]大小;[1]单位 **/
	public static String[] fileSize(long size)
	{
		String str = "";
		if (size >= 1024)
		{
			str = "KB";
			size /= 1024;
			if (size >= 1024)
			{
				str = "MB";
				size /= 1024;
			}
		}
		DecimalFormat formatter = new DecimalFormat();
		formatter.setGroupingSize(3); // 每3个数字用,分隔如：1,000
		String result[] = new String[2];
		result[0] = formatter.format(size);
		result[1] = str;
		return result;
	}

	/** 字符串分割成数组 **/
	public static String[] splitStr(String source, char spliter)
	{
		List<String> list = new ArrayList<String>();
		String subStr;
		String[] result;
		if (source.charAt(0) == spliter)
		{
			source = source.substring(1, source.length());
		}
		if (source.charAt(source.length() - 1) == spliter)
		{
			source = source.substring(0, source.length() - 1);
		}
		int start = 0;
		int end = source.indexOf(spliter);
		while (end > 0)
		{
			subStr = source.substring(start, end);
			list.add(subStr);
			start = end + 1;
			end = source.indexOf(spliter, start);
		}
		subStr = source.substring(start, source.length());
		list.add(subStr);
		result = new String[list.size()];
		Iterator<String> iter = list.iterator();
		int i = 0;
		while (iter.hasNext())
		{
			result[i++] = (String) iter.next();
		}
		return result;
	}

	/** 字符串首字母进行大写 **/
	public static String firstLetterToUpper(String str)
	{
		char[] array = str.toCharArray();
		array[0] -= 32;
		return String.valueOf(array);
	}

	/**
	 * 唯一码
	 * 
	 * @author LQ
	 * @return
	 */
	public static String getUUID()
	{
		String s = UUID.randomUUID().toString();
		// 去掉“-”符号
		StringBuilder sb = new StringBuilder();
		sb.append(s.substring(0, 8));
		sb.append(s.substring(9, 13));
		sb.append(s.substring(14, 18));
		sb.append(s.substring(19, 23));
		sb.append(s.substring(24));
		return sb.toString().toUpperCase();
	}

	/** 判断是否是数字 **/
	public static boolean isNumber(String number)
	{
		boolean isNumber = false;
		int index = number.indexOf(",");
		if (index >= 0)
		{
			// 有逗号等分隔符的数字
			isNumber = number.matches("[+-]?[0-9]+[0-9]*(,[0-9]{3})+(\\.[0-9]+)?");
		} else
		{
			isNumber = number.matches("[+-]?[0-9]+[0-9]*(\\.[0-9]+)?");
		}
		return isNumber;
	}

	/** 电话号码校验 **/
	public static boolean isTelPhoneNO(String mobiles)
	{
		boolean flag = false;
		try
		{
			Pattern p = Pattern.compile("(0([3-9]\\d\\d|10|2[1-9])[2-8]\\d{6,7}|(13|15|18)\\d{9})");
			Matcher m = p.matcher(mobiles);
			flag = m.matches();
		} catch (Exception e)
		{
			flag = false;
		}
		return flag;
	}

	/** 检查字符串是否为null或空串 **/
	public static boolean isNullOrEmpty(String str)
	{
		if (str == null || "".equals(str))
		{
			return true;
		}
		return false;
	}

	/** 传入要比较的时间类型，时间值和间隔值，返回是否超时 **/
	public static boolean compareTime(String oldTime, String distance, String timeType)
	{
		boolean flag = false;
		long mindis = 0;
		try
		{
			long dis = Long.parseLong(distance);
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = df.parse(oldTime);
			Date nowTime = new Date();
			long time1 = nowTime.getTime();
			long time2 = date.getTime();
			long diff = 0;
			if (time2 < time1)
			{
				diff = time1 - time2;
				if ("hour".equals(timeType))
				{
					mindis = diff / (60 * 60 * 1000) + 1;//
				}
				if ("min".equals(timeType))
				{
					mindis = diff / (60 * 1000) + 1;// 相差多少分钟
				}
				if ("sec".equals(timeType))
				{
					mindis = diff / (1000) + 1;// 相差多少秒
				}
				if (mindis > dis)
				{
					flag = true;
				}
			}
		} catch (Exception e)
		{
			LogUtil.e(TAG, "比较时间异常：" + e.getMessage());
			e.printStackTrace();
		}
		return flag;
	}

	/** 截取字符串 **/
	public static String getNeedStr(String xml, String indexStr, String endStr)
	{
		String str = "";
		try
		{
			int indexbegin = xml.indexOf(indexStr);
			int indexend = xml.indexOf(endStr);
			str = xml.substring(indexbegin + indexStr.length(), indexend); // 是这个字符串的长度
			LogUtil.i(TAG, "打出截取的字符串：" + str);
		} catch (Exception e)
		{
			LogUtil.e(TAG, "截取字符串异常：" + e.getMessage());
		}
		return str;
	}

	public static String getURLEncoder(String str)
	{
		try
		{
			str = URLEncoder.encode(str, "UTF-8");
		} catch (UnsupportedEncodingException ex)
		{
			ex.printStackTrace();
		}
		return str;
	}

	// 中文转码
	public static String getURLDecoder(String str)
	{
		String msg = str;
		try
		{
			// msg=new String(str.getBytes("iso-8859-1"));
			msg = URLDecoder.decode(str, "UTF-8");
		} catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		return msg;
	}

	/**
	 * 
	 * @param smallDate
	 *            小日期
	 * @param bigDate
	 *            大日期
	 * @return 天数
	 * @throws ParseException
	 */
	public static int daysBetween(Date smallDate, Date bigDate) throws ParseException
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		smallDate = sdf.parse(sdf.format(smallDate));
		bigDate = sdf.parse(sdf.format(bigDate));
		Calendar cal = Calendar.getInstance();
		cal.setTime(smallDate);
		long time1 = cal.getTimeInMillis();
		cal.setTime(bigDate);
		long time2 = cal.getTimeInMillis();
		long between_days = (time2 - time1) / (1000 * 3600 * 24);

		return Integer.parseInt(String.valueOf(between_days));
	}

	/**
	 * 
	 * @param smdate
	 *            小日期
	 * @param bdate
	 *            大日期
	 * @return 天数
	 * @throws ParseException
	 */
	public static int daysBetween(String smdate, String bdate) throws ParseException
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		cal.setTime(sdf.parse(smdate));
		long time1 = cal.getTimeInMillis();
		cal.setTime(sdf.parse(bdate));
		long time2 = cal.getTimeInMillis();
		long between_days = (time2 - time1) / (1000 * 3600 * 24);

		return Integer.parseInt(String.valueOf(between_days));
	}
}

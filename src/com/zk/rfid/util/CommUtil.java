package com.zk.rfid.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.PluginState;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.zk.rfid.R;
import com.zk.rfid.main.adapter.UnitdAdapter;

/**
 * @Description: 常用公用方法工具类
 * @date:2014-9-23 下午4:45:22
 * @author: 罗德禄
 */
public class CommUtil
{

	private final static String TAG = "CommUtil";

	private static int screenWidth = 0;
	private static int screenHeight = 0;

	/** 控制显示高度,处理ScrollView中listview显示不完整问题, 若用了下拉刷新和下拉加载则不能设置，会导致无法滚到底部 **/
	public static void setListViewHeight(ListView listView)
	{
		// 获取ListView对应的Adapter
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null)
		{
			return;
		}
		int totalHeight = 0;
		int len = listAdapter.getCount();
		for (int i = 0; i < len; i++)
		{
			// listAdapter.getCount()返回数据项的数目
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0); // 计算子项View 的宽高
			totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		// listView.getDividerHeight()获取子项间分隔符占用的高度
		// params.height最后得到整个ListView完整显示需要的高度
		listView.setLayoutParams(params);
	}

	/** 调用打电话功能 **/
	public static void callPhone(Activity activity, String phoneNumber)
	{
		LogUtil.i(TAG, "打电话：" + phoneNumber);
		Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
		activity.startActivity(intent);
	}

	/** 读取字符串文件 **/
	public static String readFromfile(Context context, String fileName)
	{
		StringBuilder returnString = new StringBuilder();
		InputStream fIn = null;
		InputStreamReader isr = null;
		BufferedReader input = null;
		try
		{
			fIn = context.getResources().getAssets().open(fileName, Context.MODE_WORLD_READABLE);
			isr = new InputStreamReader(fIn);
			input = new BufferedReader(isr);
			String line = "";
			while ((line = input.readLine()) != null)
			{
				returnString.append(line).append("\n");
			}
		} catch (Exception e)
		{
			e.getMessage();
		} finally
		{
			try
			{
				if (isr != null)
					isr.close();
				if (fIn != null)
					fIn.close();
				if (input != null)
					input.close();
			} catch (Exception e2)
			{
				e2.getMessage();
			}
		}
		return returnString.toString();
	}

	public static boolean isConnect(Context context)
	{
		boolean flag = false;
		try
		{
			ConnectivityManager connectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null)
			{
				// 获取网络连接管理的对象
				NetworkInfo info = connectivity.getActiveNetworkInfo();
				if (info != null && info.isConnected())
				{
					if (info.getState() == NetworkInfo.State.CONNECTED)
					{
						flag = true;
					}
				}
			}
		} catch (Exception e)
		{
			LogUtil.e(TAG, e.toString());
		}
		return flag;
	}

	private static Toast toast;
	private static View toastRoot;
	private static TextView text;

	public static void toastShow(String mes, Context context)
	{
		if (null == toastRoot)
		{
			toastRoot = LayoutInflater.from(context).inflate(R.layout.zk_toast_item, null);
			toastRoot.setAlpha(0.5f);
		}
		if (null == toast)
		{
			toast = new Toast(context);
			toast.setView(toastRoot);
			toast.setGravity(Gravity.CENTER, 0, 0);
		}
		if (null == text)
		{
			text = (TextView) toastRoot.findViewById(R.id.zk_toast_mes);
		}
		text.setText(mes);
		toast.show();
	}

	public static boolean isPhoneNumber(String number)
	{
		boolean flag = false;
		Pattern pattern = Pattern.compile("^[1][0-9]{10}$");// [1][358]\\d{9}
		if (pattern.matcher(number).matches())
		{
			flag = true;
		}
		return flag;
	}

	// 中文转码
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
			msg = URLDecoder.decode(str, "UTF-8");
		} catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		return msg;
	}

	public static boolean isNullOrEmpty(String str)
	{
		boolean flag = false;
		if (null == str || "".equals(str))
		{
			flag = true;
		}
		return flag;
	}

	/** 本地对象 **/
	public static SharedPreferences getShare(Context context)
	{
		SharedPreferences share = context.getSharedPreferences("zk_rfid_share_info", Context.MODE_PRIVATE
				+ Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE);
		return share;
	}

	public static SharedPreferences getPositionShare(Context context)
	{
		SharedPreferences share = context.getSharedPreferences("zk_rfid_share_pos", Context.MODE_PRIVATE
				+ Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE);
		return share;
	}

	/** email正则 **/
	public static boolean isEmail(String email)
	{
		boolean flag = false;
		try
		{
			Pattern p = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");// 复杂匹配
			Matcher m = p.matcher(email);
			flag = m.matches();
		} catch (Exception e)
		{
			flag = false;
			e.printStackTrace();
		}
		return flag;
	}

	/** 获取SD卡路径，指定文件夹 **/
	public static String getSDPath(String folder)
	{
		String sdDir = "";
		boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);// 判断sd卡是否存在
		if (sdCardExist)
		{
			sdDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + folder + "/";// 获取根目录
			File dir = new File(sdDir);
			if (!dir.exists())
			{
				dir.mkdirs();
			}
		}
		return sdDir;
	}

	/** 从SD卡中获取图片并转换成Bitmap **/
	public static Bitmap getBitmapFromSd(String path)
	{
		Bitmap bitmap = null;
		InputStream is = null;
		try
		{
			File file = new File(path);
			if (file.exists() && file.isFile())
			{
				is = StreamUtil.fileToInputStream(new File(path));
				byte[] result = StreamUtil.readInputStream(is);
				bitmap = BitmapFactory.decodeByteArray(result, 0, result.length);
				is.close();
			}

		} catch (IOException e)
		{
			e.printStackTrace();
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			if (null != is)
			{
				is = null;
			}
		}
		return bitmap;
	}

	/** Bitmap压缩 **/
	public static Bitmap compressImage(Bitmap image)
	{

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > 15)
		{ // 循环判断如果压缩后图片是否大于10kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			options -= 10;// 每次都减少10
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}

	public static String convertIconToString(Bitmap bitmap)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();// outputstream
		bitmap.compress(CompressFormat.PNG, 100, baos);
		byte[] appicon = baos.toByteArray();// 转为byte数组
		return Base64.encodeToString(appicon, Base64.DEFAULT);
	}

	// 如果图片大于maxSize
	public static boolean isSurpassMaxSize(Bitmap bitmap, int maxSize)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		if (baos.toByteArray().length / 1024 > maxSize)
		{
			return true;
		}
		return false;
	}

	// 是否是数字
	public static boolean isNumber(String number)
	{
		boolean flag = true;
		if (null == number || "".equals(number))
		{
			flag = false;
		} else
		{
			for (int i = 0; i < number.length(); i++)
			{
				if (!Character.isDigit(number.charAt(i)))
				{
					flag = false;
					break;
				}
			}
		}
		return flag;
	}

	public static void writeToSd(byte res[], String filePath)
	{
		InputStream is = null;
		try
		{
			is = new ByteArrayInputStream(res);
			StreamUtil.inputStreamToFile(is, new File(filePath));
			is.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			if (null != is)
			{
				is = null;
			}
		}
	}

	public static byte[] bitmapBytes(Bitmap bm)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		return baos.toByteArray();
	}

	// 图片圆角处理
	public static Bitmap toRoundCorner(Bitmap bitmap, int dpRound, Context context)
	{
		int pxRound = dip2px(context, dpRound);
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		// final int color = 0xff424242;
		// final int color=R.color.pt_goods_center_textcolor;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pxRound;
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		// paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue)
	{
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	// 获取状态栏的高度
	public static int getStatusHeight(Context ctx)
	{
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0, sbar = 0;
		try
		{
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			sbar = ctx.getResources().getDimensionPixelSize(x);
			LogUtil.e(TAG, "statusHeigth:" + sbar);
		} catch (Exception e1)
		{
			e1.printStackTrace();
		}
		return sbar;
	}

	public static int px2dip(Context context, float pxValue)
	{
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/** 获取指定长度随机数 **/
	public static String getRandom(int len)
	{
		StringBuffer sb = new StringBuffer();
		Random random = new Random();
		if (len == 0)
		{
			len = 1;
		}
		for (int i = 0; i < len; i++)
		{
			sb.append(String.valueOf(Math.abs(random.nextInt()) % 10));
		}
		return sb.toString();
	}

	// 获取MAC
	public static String getPhoneMac(Context context)
	{
		String mac = "";
		try
		{
			WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			WifiInfo info = wifi.getConnectionInfo();
			mac = info.getMacAddress();
			if (mac.length() > 20)
			{
				mac = "";
			}
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return mac;
	}

	private static boolean resetText;
	private static String editTmp = "";

	/** 过滤表情符 **/
	public static void fillterEmo(final Context context, final EditText et, final int len)
	{
		et.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{

				if (!resetText)
				{
					// 防止删除字符的时候报indexoutofboundsException
					if (count == 2)
					{
						// 匹配是否是表情符号
						if (!EmojiFilter.containsEmoji(s.toString().substring(start, start + 2)))
						{
							resetText = true;
							// 是表情符号就将文本还原为输入表情符号之前的内容
							et.setText(editTmp);
							et.invalidate();
							if (et.getText().length() > 1)
							{
								Selection.setSelection(et.getText(), et.getText().length());
							}
							toastShow("不支持表情输入", context);
						}
					}
				} else
				{
					resetText = false;
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after)
			{
				if (!resetText)
				{
					editTmp = s.toString();// 这里用s.toString()而不直接用s是因为如果用s，那么，tmp和s在内存中指向的是同一个地址，s改变了，tmp也就改变了，那么表情过滤就失败了
				}
			}

			@Override
			public void afterTextChanged(Editable s)
			{
				if (s.toString().length() == len)
				{
					toastShow("已达" + len + "字符,不能再输入了", context);
				}
			}
		});
	}

	/** 获取SIM卡状态,是否可用 **/
	public static boolean getSIMStatus(Context context)
	{
		boolean flag = false;
		TelephonyManager manager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
		int index = manager.getSimState();
		if (TelephonyManager.SIM_STATE_READY == index)
		{
			flag = true;
		}
		return flag;
	}

	/** 安装应用 **/
	public static void installApk(String apkName, Context context)
	{
		File file = new File(Environment.getExternalStorageDirectory(), apkName);
		if (!file.exists())
			return;
		Uri uri = Uri.fromFile(file);
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(uri, "application/vnd.android.package-archive");
		context.startActivity(intent);
	}

	/**
	 * 判断当前网络是否是wifi网络
	 * if(activeNetInfo.getType()==ConnectivityManager.TYPE_MOBILE) { //判断3G网
	 * 
	 * @param context
	 * @return boolean
	 */
	public static boolean isWifiNet(Context context)
	{
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI)
		{
			return true;
		}
		return false;
	}

	/**
	 * 判断当前网络是否是3G网络
	 * 
	 * @param context
	 * @return boolean
	 */
	public static boolean is3GNet(Context context)
	{
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE)
		{
			return true;
		}
		return false;
	}

	/** 转为两位小数字符 **/
	public static String getFloatMoney(String money)
	{
		String str = money;
		if (null != money && !"".equals(money))
		{
			try
			{
				if (money.endsWith("."))
				{
					str = money + "0";
				} else if (!money.contains("."))
				{
					str = money + ".0";
				}
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return str;
	}

	public static String getFormatPhone(String phone)
	{
		String str = phone;
		try
		{
			int len = phone.length();
			int half = len / 2;
			str = phone.substring(0, half) + "-" + phone.substring(half, len);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return str;
	}

	/** 解析公用Json **/
	public static Map<String, String> getJsonMap(String jsonString)
	{
		Map<String, String> resultMap = null;
		try
		{
			if (null != jsonString && !"".equals(jsonString))
			{
				resultMap = new HashMap<String, String>();
				JSONObject jsonObject = new JSONObject(jsonString);

				for (Iterator<String> iter = jsonObject.keys(); iter.hasNext();)
				{
					String key = iter.next();
					resultMap.put(key, String.valueOf(jsonObject.get(key)));
				}
			}

		} catch (Exception e)
		{
			LogUtil.e(TAG, "解析公共JSON异常：" + e.getMessage());
		}

		return resultMap;
	}

	public static void editTextChange(final EditText editText, final View imgview)
	{
		editText.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3)
			{
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3)
			{
				if (arg0.length() != 0)
				{
					imgview.setVisibility(View.VISIBLE);
				} else
				{
					imgview.setVisibility(View.GONE);
				}
			}

			@Override
			public void afterTextChanged(Editable arg0)
			{
				if (arg0.length() != 0)
				{
					imgview.setVisibility(View.VISIBLE);
				} else
				{
					imgview.setVisibility(View.GONE);
				}
			}
		});

		if (!"".equals(editText.getText().toString()))
		{
			imgview.setVisibility(View.VISIBLE);
		}

		imgview.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				editText.setText("");
			}
		});
	}

	private static final String CHARS_STR = "abcdefghijklmnopqrstuvwxyz";

	public static String getRandomStr(int len)
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < len; i++)
		{
			sb.append(CHARS_STR.charAt((int) (Math.random() * 26)));
		}
		return sb.toString();
	}

	public static String longToHex(String code) throws Exception
	{
		long as = Long.parseLong(code);
		String hex = Long.toHexString(as).toUpperCase();
		return hex;
	}

	public static String hexToLong(String s) throws Exception
	{
		return Long.parseLong(s, 16) + "";
	}

	/** 0为versioncode,1为versionName,2为包名 **/
	public static String[] getAppVersionInfo(Context context)
	{
		String versionStr[] = new String[3];
		versionStr[0] = "";
		versionStr[1] = "";
		versionStr[2] = "";// 先初值，防null异常
		try
		{
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			versionStr[0] = pi.versionCode + "";
			versionStr[1] = pi.versionName + "";
			versionStr[2] = pi.packageName + "";
		} catch (Exception e)
		{
			LogUtil.e(TAG, e.getMessage());
		}
		return versionStr;
	}

	/** 从AndroidManifest.xml读取 **/
	public static String getMetaInfo(Activity activity, String str)
	{
		ApplicationInfo appInfo = null;
		String agentid = "";
		try
		{
			appInfo = activity.getPackageManager().getApplicationInfo(activity.getPackageName(),
					PackageManager.GET_META_DATA);
			agentid = appInfo.metaData.getString(str);
		} catch (NameNotFoundException e)
		{
			e.printStackTrace();
		}
		return agentid;
	}

	public static String getImei(Context context)
	{
		TelephonyManager TelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return TelephonyMgr.getDeviceId() + "";
	}

	/** 动态字体颜色 **/
	public static void setFrontColor(Context context, TextView view, int id)
	{
		view.setTextColor(context.getResources().getColor(id));
	}

	@SuppressWarnings("deprecation")
	public static void setWebSettings(WebSettings web, Context context)
	{
		// WebSettings常用方法：
		web.setAllowFileAccess(false);// 启用或禁止WebView访问文件数据
		web.setBlockNetworkImage(false);// 是否阻塞显示网络图像
		web.setBuiltInZoomControls(true);// 设置是否支持缩放

		web.setSupportZoom(true);

		// 扩大比例的缩放
		web.setUseWideViewPort(true);
		// 自适应屏幕
		web.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		web.setLoadWithOverviewMode(true);

		// web.setCacheMode(WebSettings.LOAD_NORMAL) ;//设置缓冲的模式
		// web.setDefaultTextEncodingName(code);
		// LOAD_CACHE_ONLY 只读取本地缓存
		// LOAD_DEFAULT默认的缓存模式
		// LOAD_NORMAL一般的缓存模式
		// LOAD_NO_CACHE不读取缓存，所有内容均从网络下载

		web.setPluginState(PluginState.ON);
		web.setAllowFileAccess(true);
		web.setCacheMode(WebSettings.LOAD_NO_CACHE);

		web.setJavaScriptEnabled(true);// 设置是否支持Javascript
		web.setJavaScriptCanOpenWindowsAutomatically(true);// 支持js打开的新窗口
		// web.setLayoutAlgorithm ;//设置布局方式
		web.setLightTouchEnabled(true);// 设置用鼠标激活被选项
		web.setSaveFormData(false);// 表单数据是否保存
		web.setSupportZoom(true);// 设置是否支持变焦
	}

	private static PopupWindow selectPopupWindow;

	/** 上下文,父控件,输入框,下拉数据 **/
	public static void initOptionListWedget(Activity activity, LinearLayout input_l, EditText editText,
			List<String> datasList)
	{
		// 获取下拉框依附的组件宽度
		int width = input_l.getWidth();
		int pwidth = width;
		// PopupWindow浮动下拉框布局
		View loginwindow = (View) activity.getLayoutInflater().inflate(R.layout.apply_options, null);
		ListView listView = (ListView) loginwindow.findViewById(R.id.apply_option_list);
		// 设置自定义Adapter
		selectPopupWindow = new PopupWindow(loginwindow, pwidth, LayoutParams.WRAP_CONTENT, true);
		selectPopupWindow.setOutsideTouchable(true);
		// 这一句是为了实现弹出PopupWindow后当点击屏幕其他部分及Back键时PopupWindow会消失
		selectPopupWindow.setBackgroundDrawable(new BitmapDrawable());
		// 将selectPopupWindow作为parent的下拉框显示并指定selectPopupWindow在Y方向上向上偏移X pix
		int movepix = 1;
		selectPopupWindow.showAsDropDown(input_l, 0, movepix);

		UnitdAdapter optionsAdapter = new UnitdAdapter(activity, datasList, editText, selectPopupWindow);
		listView.setAdapter(optionsAdapter);
	}

	/** 执行公共线程任务 **/
	public static void executeCommTask(Activity activity, Handler handler, int responseCode, int arg1, int arg2,
			String param)
	{
		GetDataTask task = new GetDataTask(activity, handler);
		task.setHandlerCode(responseCode);
		task.setArg1(arg1);
		task.setArg2(arg2);
		task.setParams(param);
		task.execute("");
	}

	/** 获得LinearLayout,0横屏，1竖屏 **/
	public static LinearLayout getLinearLayout(Activity activity, int type)
	{
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		LinearLayout view = new LinearLayout(activity);
		view.setLayoutParams(lp);// 设置布局参数
		if (LinearLayout.HORIZONTAL == type)
		{
			view.setOrientation(LinearLayout.HORIZONTAL);
		} else
		{
			view.setOrientation(LinearLayout.VERTICAL);
		}
		return view;
	}

	public static boolean isIp(String ipAddress)
	{
		String ip = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
		Pattern pattern = Pattern.compile(ip);
		Matcher matcher = pattern.matcher(ipAddress);
		return matcher.matches();
	}

	public static Dialog createDialog(Context context)
	{
		Dialog dialog = new Dialog(context, R.style.dialogstyle);
		dialog.setContentView(R.layout.loading_dialog);
		dialog.setCancelable(true);
		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
		lp.alpha = 0.75f; // 0.0-1.0
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
		return dialog;
	}

	/** 复制剪贴板,是否提示信息 **/
	public static void copyTextViewContent(Context context, String value, boolean showFlag)
	{
		ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		cmb.setText(value); // 将内容放入粘贴管理器,在别的地方长按选择"粘贴"即可
		if (showFlag)
		{
			toastShow("已复制至剪贴板", context);
		}
	}

	public static View getCheckBox(Context context, int id)
	{
		View cb = LayoutInflater.from(context).inflate(R.layout.map_layer_check_box, null);
		cb.setId(id);
		return cb;
	}

	public static long getStringToLong(String code)
	{
		long val=0;
		try
		{
			val=Long.parseLong(code);
		} catch (Exception e)
		{
			val=0;
		}		
		return val;
	}
	
	public static int getParseIntVal(String code)
	{
		int val=0;
		try
		{
			val=Integer.parseInt(code);
		} catch (Exception e)
		{
			val=0;
		}		
		return val;
	}
}

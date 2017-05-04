package com.zk.rfid.update;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.zk.rfid.R;
import com.zk.rfid.comm.base.BaseActivity;
import com.zk.rfid.comm.base.MyApplication;
import com.zk.rfid.init.InitApp;
import com.zk.rfid.login.activity.LoginActivity;
import com.zk.rfid.main.activity.MainActivity;
import com.zk.rfid.main.util.ConfigUtil;
import com.zk.rfid.map.dao.MapDataDao;
import com.zk.rfid.util.ApkVersionTool;
import com.zk.rfid.util.CommUtil;
import com.zk.rfid.util.ConstantUtil;
import com.zk.rfid.util.FileUtil;
import com.zk.rfid.util.HttpClientTool;
import com.zk.rfid.util.HttpGetPostUtil;
import com.zk.rfid.util.LogUtil;
import com.zk.rfid.util.SystemUtil;
import com.zk.rfid.util.ZKCmd;

/**
 * APP版本更新与初始化化，尝试登录
 * 
 * @author Administrator
 * 
 */

public class AutoUpdateActivty extends BaseActivity
{
	private static final String TAG = "AutoUpdateActivty";

	private Activity activity = AutoUpdateActivty.this;

	private TextView percentTextView;
	private ProgressDialog pBar;

	private String apkDownPathDir = "";
	private String versionJson    = ConstantUtil.HTTP_APK_JSON;
	private String apkName  = ConstantUtil.HTTP_APK_Name;
	private String packName = ConstantUtil.APP_PACK_NAME;

	private String newVersionName = "";
	private int newVersionCode = -1;
	private int apkSize;
	private int downLoadApkSize;

	private String json = null;
	private GetJsonTask jsonTask = null;
	private InitApp initApp;

	private Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case ZKCmd.HAND_REQUEST_DATA:

				initData();
				break;

			case 0x112:

				long result = downLoadApkSize * 100 / apkSize;
				pBar.show();
				pBar.setMessage("请稍后..." + result + "%");
				percentTextView.setText("应用升级检测..." + result + "%");
				break;

			case ZKCmd.HAND_AUTO_LOGIN:
				// 自动登记录返回结果
				checkAutoRes(msg.obj + "");
				break;

			case ZKCmd.HAND_AUTO_LOGIN_FAIL:
				autoLoginRes(false);
				break;

			case ZKCmd.HAND_RESPONSE_DATA:

				if (ZKCmd.ARG_REQUEST_INIT == msg.arg1)
				{
					int co = initApp.saveInitData(msg.obj + "");
					LogUtil.i(TAG, "初始化成功还是失败:" + co);
					if (0 == co)
					{
						initApp.initFailDialog(activity);
					} else
					{
						// 初始化成功则检查自动登录
						initApp.autoLogin();
					}
				}

				break;

			case 0x999:

				String showMsg = "APK升级网络不通";
				CommUtil.toastShow(showMsg, activity);
				percentTextView.setText(showMsg);
				percentTextView.setTextColor(Color.RED);
				break;

			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Window window = getWindow();  
        WindowManager.LayoutParams params = window.getAttributes();  
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE;
        window.setAttributes(params);
		setContentView(R.layout.update);
		percentTextView = (TextView) findViewById(R.id.percentTextView);
		apkDownPathDir = getString(R.string.updateUrl).trim();
		initApp = new InitApp(handler);

		if (isConn())
		{
			new CountDownTimer(1000, 100)
			{
				@Override
				public void onTick(long millisUntilFinished)
				{
				}

				@Override
				public void onFinish()
				{

					LogUtil.i(TAG, "执行升级检查");
					jsonTask = new GetJsonTask();
					jsonTask.execute(apkDownPathDir + "/" + versionJson);
				
				}
			}.start();
		} 
		else
		{
			sendMsg(0x999);
		}

	}

	/**获取是否需要升级比对json**/
	private class GetJsonTask extends AsyncTask<String, String, String>
	{
		@Override
		protected String doInBackground(String... params)
		{
			LogUtil.i(TAG, "获得地址:" + params[0]);
			json = HttpGetPostUtil.sendHttpGet(params[0], null);

			LogUtil.i(TAG, "获得json: " + json);
			if (!"".equals(json) && null != json)
			{
				try
				{
					// {"app":{"versionCode":"2","versionName":"1.0.1"}}
					JSONObject jsonobj = new JSONObject(json).getJSONObject("app");
					newVersionCode = Integer.parseInt(jsonobj.optString("versionCode", "0"));
					newVersionName = jsonobj.getString("versionName");
					LogUtil.i(TAG, "获得升级版本:"+newVersionCode);
				} 
				catch (Exception e)
				{
					LogUtil.i(TAG, "获得升级版本信息异常:"+e.getMessage());
					newVersionCode = -1;
				}
			} else
			{
				newVersionCode = -1;
			}
			return newVersionCode + "";
		}

		@Override
		protected void onPostExecute(String result)
		{
			super.onPostExecute(result);
			int currVerCode = ApkVersionTool.getVerCode(activity, packName);
			LogUtil.i(TAG, "currVCode:" + currVerCode + " ;newVCode:" + newVersionCode + " ;newVName:" + newVersionName);
			if (newVersionCode > currVerCode)
			{
				FileUtil.deleteFile(apkName);
				//这里要升级
				showUpdateDialog();
				
			} else
			{
				sendMsg(ZKCmd.HAND_REQUEST_DATA);
			}
		}
	}

	/** 更新当前版本 **/
	private void showUpdateProgressDialog()
	{
		pBar = new ProgressDialog(activity);
		pBar.setTitle("正在下载");
		pBar.setMessage("请稍后");
		pBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pBar.setCanceledOnTouchOutside(false);
		pBar.setCancelable(true);
		downloadApK(apkDownPathDir +"/"+ apkName);
	}

	/** 显示是否更新对话框 **/
	private void showUpdateDialog()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("检测版本信息:\n");
		sb.append("当前版本:");
		sb.append(ApkVersionTool.getVerName(activity, packName));
		sb.append("\n");
		sb.append("最新版本:");
		sb.append(newVersionName);
		// sb.append("\n");
		// sb.append("是否更新？");
		Dialog dialog = new AlertDialog.Builder(activity).setTitle("软件更新").setMessage(sb.toString())
				.setNegativeButton("以后再说", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which)
					{
						sendMsg(ZKCmd.HAND_REQUEST_DATA);
					}
				}).setPositiveButton("立即更新", new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						showUpdateProgressDialog();
					}
				}).create();
		dialog.setCancelable(false);
		dialog.show();
	}

	/** 下载APK **/
	private void downloadApK(final String apkUrl)
	{
		new Thread()
		{
			@Override
			public void run()
			{
				HttpGet get = new HttpGet(apkUrl);
				HttpResponse response;
				try
				{
					response = HttpClientTool.getHttpClient().execute(get);
					HttpEntity entity = response.getEntity();
					apkSize = (int) entity.getContentLength();// 根据响应获取文件大小
					InputStream inStream = entity.getContent();
					FileOutputStream outStream = null;
					if (null == inStream)
					{
						LogUtil.e(TAG, "Apk Stream is null");
						throw new RuntimeException("isStream is null");
					}
					File file = new File(Environment.getExternalStorageDirectory(), apkName);
					outStream = new FileOutputStream(file);
					byte[] buf = new byte[8192];
					int len = -1;
					do
					{
						len = inStream.read(buf);
						if (-1 == len)
						{
							if (downLoadApkSize < apkSize)
							{
								response = HttpClientTool.getHttpClient().execute(get);
								Thread.sleep(1000);
								if (null == response)
								{
									LogUtil.e("response", "response null");
									sendMsg(0x999);
								}
								entity = response.getEntity();
								inStream = entity.getContent();
								inStream.mark(downLoadApkSize);
							} 
							else
							{
								LogUtil.i(TAG, "[downLoadApkSize = apkSize]" + apkSize);
								break;
							}
						}
						outStream.write(buf, 0, len);
						downLoadApkSize += len;
						// 更新进度
						sendMsg(0x112);
					} while (true);

					inStream.close();
					outStream.close();
					// 更新完毕
					downLoadApkOver();
				} catch (Exception e)
				{
					LogUtil.e(TAG, e.getMessage());
				}
			}
		}.start();
	}

	/** 下载完毕后，取消进度框，准备安装APK **/
	private void downLoadApkOver()
	{
		LogUtil.i(TAG, "APK下载完毕");
		handler.post(new Runnable()
		{
			public void run()
			{
				CommUtil.installApk(apkName, activity);
			}
		});

	}

	/** 发消息 **/
	private void sendMsg(int number)
	{
		Message msg = Message.obtain();
		msg.what = number;
		handler.sendMessage(msg);
	}

	/**升级检查完毕，进行初始数据获取和自动登录，生成位置的初步数据**/
	private void initData()
	{
		LogUtil.i(TAG, "初始化执行");
		String status = CommUtil.getShare(activity).getString(ConfigUtil.MATERIAL_STATUS, "");// 状态码
		String spec = CommUtil.getShare(activity).getString(ConfigUtil.MATERIAL_SPEC, "");// 规格
		String process = CommUtil.getShare(activity).getString(ConfigUtil.APPROVE_PROCESS, "");// 审核进度

		if (isConn())
		{
			new MapDataDao().createPositionData(MyApplication.context);// 生成位置码

			if ("".equals(status) || "".equals(spec) || "".equals(process))
			{
				initApp.getInitData();// 初始化网络数据
			} else
			{
				// 不为空的情况下检查登录
				initApp.autoLogin();
			}
		} else
		{
			initApp.initFailDialog(activity);// 失败
		}
	}

	/**检查自动登录的结果,成功则到主界面，失败则跳转让到登录界面**/
	private void checkAutoRes(String result)
	{
		try
		{
			String str = new JSONObject(result).getJSONObject("result").toString();
			Map<String, String> map = CommUtil.getJsonMap(str);

			String state = map.get("state");
			String power = map.get("power");
			LogUtil.d("test","power="+power);
			if ("1".equals(state))
			{
				SystemUtil.power = power;
				// 登录成功
				initApp.checkLoginRes(map);// 保存信息
				SystemClock.sleep(1000);
				autoLoginRes(true);
			} else
			{
				autoLoginRes(false);
			}

		}
		catch (Exception e)
		{
			autoLoginRes(false);
		}
	}

	private void autoLoginRes(boolean success)
	{
		Intent intent = new Intent();
		if (success)
		{
			intent.setClass(activity, MainActivity.class);
		} else
		{
			intent.setClass(activity, LoginActivity.class);
		}
		startActivity(intent);
		finish();
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}
}

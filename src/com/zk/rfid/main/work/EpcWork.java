package com.zk.rfid.main.work;

import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;

import com.rfid.sdk.protocol.Reader_TCP_Protocol;
import com.rfid.sdk.protocol.Reader_UART_Protocol;
import com.rfid.sdk.public_utils.MAC_Register;
import com.rfid.sdk.public_utils.Message_Type;
import com.rfid.sdk.public_utils.Public;
import com.rfid.sdk.rfidclass.ANTERNNA;
import com.zk.rfid.comm.base.MyApplication;
import com.zk.rfid.main.util.ConfigUtil;
import com.zk.rfid.util.CommUtil;
import com.zk.rfid.util.ConstantUtil;
import com.zk.rfid.util.LogUtil;

/**
 * @Description: EPC操作接口
 * @date:2016-5-27下午3:30:37
 * @author: ldl
 */
public class EpcWork
{
	private static final String TAG = "EpcWork";
	private Handler handler;
	private Reader_TCP_Protocol tcpProtocol;
	private Reader_UART_Protocol uartProtocol;

	// private Activity activity;
	// private Context context;

	/**构造方法，获得底层接口对象**/
	public EpcWork(Context context)
	{
		if (ConstantUtil.USE_EPC_METHOD.equals(ConfigUtil.EPC_METHOD_COM))
		{
			uartProtocol = Reader_UART_Protocol.getInstance(context);
		}

		if (ConstantUtil.USE_EPC_METHOD.equals(ConfigUtil.EPC_METHOD_WAN))
		{
			tcpProtocol = Reader_TCP_Protocol.getInstance(context);
		}
		// this.activity=activity;
	}

	/**初始设置电源，为是启动程序时，控制电源开或关**/
	public void initPower(Handler handler)
	{
		this.handler = handler;
		uartProtocol.setHandler(handler);
	}

	/**设置 底层接口数据回调handler**/
	public void setReaderHandler(Handler handler)
	{
		this.handler = handler;
		setEpcServer();
	}

	static boolean flag = false;

	/**与底层连接,进入程序只连接一次**/
	private void setEpcServer()
	{
		if (ConstantUtil.USE_EPC_METHOD.equals(ConfigUtil.EPC_METHOD_COM))
		{
			uartProtocol.setHandler(handler);
			if (!flag)
			{
				boolean res = uartProtocol.connectReader();
				if (res)
				{
					flag = true;
					//CommUtil.toastShow("连接COM成功", MyApplication.context);
				} else
				{
					CommUtil.toastShow("连接COM失败", MyApplication.context);
				}
			}
		}

		if (ConstantUtil.USE_EPC_METHOD.equals(ConfigUtil.EPC_METHOD_WAN))
		{
			String ip = CommUtil.getShare(MyApplication.context).getString(ConfigUtil.EPC_SERVER_WAN_IP, "");
			String port = CommUtil.getShare(MyApplication.context).getString(ConfigUtil.EPC_SERVER_WAN_PORT, "0");
			int po = 0;
			try
			{
				po = Integer.parseInt(port);
			} catch (Exception e)
			{
				e.printStackTrace();
				po = 0;
			}
			tcpProtocol.setServerIP(ip, po);
			tcpProtocol.setHandler(handler);

			boolean res = tcpProtocol.connectReader();
			if (res)
			{
				CommUtil.toastShow("连接成功", MyApplication.context);
			} else
			{
				CommUtil.toastShow("连接失败:" + ip + ";" + port, MyApplication.context);
			}
		}
	}

	/**关闭连接，此方法暂未用，关闭时是直接断电**/
	public void disConnection()
	{
		if (ConstantUtil.USE_EPC_METHOD.equals(ConfigUtil.EPC_METHOD_COM))
		{
			uartProtocol.disconnectReader();
		}

		if (ConstantUtil.USE_EPC_METHOD.equals(ConfigUtil.EPC_METHOD_WAN))
		{
			tcpProtocol.disconnectReader();
		}
	}

	/**读取epc标签数据**/
	public void readEpcData()
	{
		if (ConstantUtil.USE_EPC_METHOD.equals(ConfigUtil.EPC_METHOD_COM))
		{
			uartProtocol.tagAccessRead(Message_Type.EPC, 0);
		}

		if (ConstantUtil.USE_EPC_METHOD.equals(ConfigUtil.EPC_METHOD_WAN))
		{
			tcpProtocol.tagAccessRead(Message_Type.EPC, 0);
		}

	}

	/**盘点**/
	public void scanData()
	{

		if (ConstantUtil.USE_EPC_METHOD.equals(ConfigUtil.EPC_METHOD_COM))
		{
			uartProtocol.startInventory();
		}

		if (ConstantUtil.USE_EPC_METHOD.equals(ConfigUtil.EPC_METHOD_WAN))
		{
			tcpProtocol.startInventory();
		}
	}
	

	/**停止盘点**/
	public void stopScan()
	{

		if (ConstantUtil.USE_EPC_METHOD.equals(ConfigUtil.EPC_METHOD_COM))
		{
			LogUtil.i(TAG, "接口调用stop");
			uartProtocol.stopInventory();
			reset();
			//powerOff();
		}

		if (ConstantUtil.USE_EPC_METHOD.equals(ConfigUtil.EPC_METHOD_WAN))
		{
			tcpProtocol.stopInventory();
		}
	}
	
	public void reset()
	{
		if (ConstantUtil.USE_EPC_METHOD.equals(ConfigUtil.EPC_METHOD_COM))
		{
			uartProtocol.reset();
			LogUtil.i(TAG, "MAC复位");
		}
	}

	public void powerOn()
	{
		if (ConstantUtil.USE_EPC_METHOD.equals(ConfigUtil.EPC_METHOD_COM))
		{
			uartProtocol.power_on();
			LogUtil.i(TAG, "打开电源");
		}
	}

	public void powerOff()
	{
		if (ConstantUtil.USE_EPC_METHOD.equals(ConfigUtil.EPC_METHOD_COM))
		{
			clearStackCash();
			SystemClock.sleep(500);// 延迟再关闭
			uartProtocol.power_off();
			LogUtil.i(TAG, "关电");
			clearStackCash();
		}
	}

	/**epc写入标签**/
	public void writeEpcData(String data)
	{
		if (ConstantUtil.USE_EPC_METHOD.equals(ConfigUtil.EPC_METHOD_COM))
		{
			// uartProtocol.tagAccessWrite(Message_Type.EPC, data, 0);
			LogUtil.i(TAG, "调用了接口:" + data);
			uartProtocol.setMaterial_EPC(data, 0);
		}

		if (ConstantUtil.USE_EPC_METHOD.equals(ConfigUtil.EPC_METHOD_WAN))
		{
			// tcpProtocol.tagAccessWrite(Message_Type.EPC, data, 0);
		}
	}

	/**修改指定的epc*/
	public boolean modifyEpcData(String oldData, String newData)
	{
		boolean flag = false;
		if (ConstantUtil.USE_EPC_METHOD.equals(ConfigUtil.EPC_METHOD_COM))
		{
			flag = uartProtocol.modifyMaterial_EPC(oldData, newData, 0);
			LogUtil.i(TAG, "接口修改epc结果:" + flag);
		}
		return flag;

	}

	/**
	 * @author yl
	 * @param enbale
	 *            使能天线
	 * @param power
	 *            天线功率
	 * @param dwelltime
	 * @param inv_rounds
	 * @param cycles
	 */
	public void setPower(final boolean enbale, final int power, final int dwelltime, final int inv_rounds, final int cycles)
	{
		if (ConstantUtil.USE_EPC_METHOD.equals(ConfigUtil.EPC_METHOD_COM))
		{
			powerOn();// 开电
			LogUtil.i(TAG, "设置功率：" + power);

			new CountDownTimer(500, 10)
			{
				@Override
				public void onTick(long millisUntilFinished)
				{
				}

				@Override
				public void onFinish()
				{
					ANTERNNA ant1 = new ANTERNNA(0, enbale, power, dwelltime, inv_rounds);
					//final ANTERNNA ant2 = new ANTERNNA(1, enbale, power, dwelltime, inv_rounds);
					ant1.setCycles((short) cycles);
					//ant2.setCycles((short) cycles);
					uartProtocol.setAntParm(0, ant1);
					//SystemClock.sleep(100);
					//uartProtocol.setAntParm(1, ant2);
				}
			}.start();

		}
	}

	/** 清空盘点的栈数据，释放空间 **/
	public void clearStackCash()
	{
		if (ConstantUtil.USE_EPC_METHOD.equals(ConfigUtil.EPC_METHOD_COM))
		{
			LogUtil.i(TAG, "清空栈缓存数据");
//			LogUtil.i(TAG, "栈compact:"+Public.getByteBuffer().compact());
//			LogUtil.i(TAG, "栈position:"+Public.getByteBuffer().position());
//			LogUtil.i(TAG, "栈limit:"+Public.getByteBuffer().limit());
			
			//uartProtocol.clear();
		}
	}

	// public void setPower(int value)
	// {
	// if(ConstantUtil.USE_EPC_METHOD.equals(ConfigUtil.EPC_METHOD_COM))
	// {
	// powerOn();//开电
	//
	// SystemClock.sleep(300);
	// ANTERNNA ant1 = new ANTERNNA(0,true,value,2000,0xFFFF);
	// final ANTERNNA ant2 = new ANTERNNA(1,true,value,2000,0xFFFF);
	// ant1.setCycles((short)0xFFFF);
	// ant2.setCycles((short)0xFFFF);
	//
	// uartProtocol.setAntParm(0,ant1);
	// new CountDownTimer(200,10)
	// {
	// @Override
	// public void onTick(long millisUntilFinished)
	// {
	// }
	// @Override
	// public void onFinish()
	// {
	// uartProtocol.setAntParm(2,ant2);
	// }
	// }.start();
	//
	// }
	// }

	/**释放连接底层接口对象**/
	public static void clearEpcWork()
	{
		EpcWork epcWork = MyApplication.getRfidWork();
		if (null != epcWork)
		{
			epcWork.initPower(MyApplication.handler);
			epcWork.powerOff();
		}
		flag = false;
	}

	public void startInfScan() {
		// TODO 自动生成的方法存根
		if (ConstantUtil.USE_EPC_METHOD.equals(ConfigUtil.EPC_METHOD_COM))
		{
			uartProtocol.WriteRegister(MAC_Register.HST_ANT_CYCLES,0xFFFF);
			uartProtocol.startInventory();
		}

		if (ConstantUtil.USE_EPC_METHOD.equals(ConfigUtil.EPC_METHOD_WAN))
		{
			tcpProtocol.startInventory();
		}
	}

}

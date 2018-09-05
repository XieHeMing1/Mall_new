package com.qy.zgz.mall.lcb_game;

import android.util.Log;

import org.winplus.serial.SerialPortCommunication;
import org.winplus.serial.utils.CRCutil;
import org.winplus.serial.utils.ConverterUtil;

public class MainControl implements SerialPortCommunication.ReceivedCallback {

	private static MainControl mainControl;
	private static SerialPortCommunication serialPortCommunication;
	private ReceiveHandle receiveHandle;

	public synchronized static MainControl getControl() {
		if (mainControl == null) {
			mainControl = new MainControl();
			serialPortCommunication = SerialPortCommunication.getInstance();
			serialPortCommunication.setReceivedCallback(mainControl);
		}
		return mainControl;
	}

	public void setReceiveHandle(ReceiveHandle receiveHandle) {
		this.receiveHandle = receiveHandle;
	}

	/**
	 * 初始化
	 */
	public void ID() {
		byte[] b = { 0x02, 0x01, (byte) 0xC1, 0x10 };
		Log.v("link", "---发送ID" + ConverterUtil.bytesToHex(b));
		serialPortCommunication.send(b);
	}

	/**
	 * 用于通知驱动模块启动电机。设置参数如下 Z1 = 1 字节 表示电机索引号（00~79）。 响应数据 Y1 = 1 个字节 表示是否启动成功，0
	 * 表示成功，大于 0 时表示失败，具体的值来表示失败的原因。1 表示无效的电机索引 号，2 表示当前有另一个电机正在运行，3
	 * 表示另一台电机的运转结果还未取走
	 */
	public void RUN(int position) {
		byte[] data = { 0x02, 0x05, (byte) position };
		int crc = CRCutil.calcCrc16(data);
		byte[] b = { 0x02, 0x05, (byte) position, (byte) CRCutil.LB(crc),
				(byte) CRCutil.UB(crc) };
		Log.v("link", "---发送RUN" + ConverterUtil.bytesToHex(b));
		serialPortCommunication.send(b);
	}
	/**
	 * 查询状态执行，POLL可能回应零条或多条消息。如果没有消息，则驱动模块回发 ACK来响应 POLL
	 */
	public void POLL() {
		byte[] b = { 0x02, 0x03, 0x40, (byte) 0xD1 };
		Log.v("link", "---发送POLL" + ConverterUtil.bytesToHex(b));
		serialPortCommunication.send(b);
	}
	/**
	 * 用于通知驱动模块主机已经获取到了上次运行的结果
	 */
	public void ACK() {
		byte[] b = { 0x02, 0x06, (byte)0x80, (byte) 0xD2 };
		Log.v("link", "---发送ACK" + ConverterUtil.bytesToHex(b));
		serialPortCommunication.send(b);
	}

	/**
	 * 数据回传回调
	 *
	 * @param buf
	 */
	@Override
	public void onReceived(byte[] buf) {
		System.out.println("---接收串口数据：" + ConverterUtil.bytesToHex(buf));
		if (buf != null && receiveHandle != null) {
			receiveHandle.process(buf);
		}
	}

	/**
	 * 数据回调接口
	 */
	public interface ReceiveHandle {
		public void process(byte[] data);
	}
}

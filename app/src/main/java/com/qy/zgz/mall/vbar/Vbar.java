package com.qy.zgz.mall.vbar;


import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;

public class Vbar {

	public interface Vdll extends Library {

		Vdll INSTANCE = (Vdll) Native.loadLibrary("vbar", Vdll.class);

		//打开信道
		public IntByReference vbar_channel_open(int type, long arg);

		//发送数据
		public int vbar_channel_send(IntByReference vbar_channel, byte[] buffer, int length);

		//接收数据
		public int vbar_channel_recv(IntByReference vbar_channel, byte[] buffer, int size, int
                milliseconds);

		//关闭信道
		public void  vbar_channel_close(IntByReference vbar_channel);


	}


	//初始化设备变量
	IntByReference vbar_channel = null;


	//打开设备
	public boolean vbarOpen() {
		if(vbar_channel == null) {
			vbar_channel = Vbar.Vdll.INSTANCE.vbar_channel_open(1, 1);
		}
		if (vbar_channel != null) {
			System.out.println("open device success");
			return true;
		} else {
			System.out.println("open device fail");
			return false;
		}
	}
	//背光控制
	public void vbarLight(boolean lightstate)
	{

		byte[] buffer = new byte[64];
		byte[] buffer1 = new byte[64];
		int i = 0;
		if(lightstate)
		{
			buffer[i] = 0x77;
			buffer[++i] = (byte)0xCC;
			buffer[++i] = (byte)0xDD;
			buffer[++i] = 0x06;
			buffer[++i] = 0x00;
			buffer[++i] = 0x01;
			buffer[++i] = 0x00;

			buffer[++i] = 0x07;
			buffer[++i] = 0x66;

			for (int j = 19; j < 64; j++)
			{
				buffer[j] = 0x00;
			}
			int sendreturn = Vbar.Vdll.INSTANCE.vbar_channel_send(vbar_channel,buffer,64);
			Vbar.Vdll.INSTANCE.vbar_channel_recv(vbar_channel, buffer1,64,100);


		}
		else
		{
			buffer[i] = 0x77;
			buffer[++i] = (byte)0xCC;
			buffer[++i] = (byte)0xDD;
			buffer[++i] = 0x06;
			buffer[++i] = 0x00;
			buffer[++i] = 0x01;
			buffer[++i] = 0x00;

			buffer[++i] = 0x08;
			buffer[++i] = 0x69;
			for (int j = 19; j < 64; j++)
			{
				buffer[j] = 0x00;
			}
			Vbar.Vdll.INSTANCE.vbar_channel_send(vbar_channel,buffer,64);
			Vbar.Vdll.INSTANCE.vbar_channel_recv(vbar_channel, buffer1, 64, 200);

		}

	}
	//蜂鸣器控制   参数可以为1和2
	public void vbarBeep(int times)
	{
		byte[] buffer = new byte[64];
		int i = 0;
		if(times == 1)
		{
			buffer[i] = 0x77;
			buffer[++i] = (byte)0xCC;
			buffer[++i] = (byte)0xDD;
			buffer[++i] = 0x06;
			buffer[++i] = 0x00;
			buffer[++i] = 0x01;
			buffer[++i] = 0x00;
			buffer[++i] = 0x01;
			buffer[++i] = 0x60;
			for (int j = 9; j < 64; j++)
			{
				buffer[j] = 0x00;
			}
			int beep1state = Vbar.Vdll.INSTANCE.vbar_channel_send(vbar_channel,buffer,64);
			Vbar.Vdll.INSTANCE.vbar_channel_recv(vbar_channel, buffer, 64, 200);

		}
		else if(times == 2)
		{
			buffer[i] = 0x77;
			buffer[++i] = (byte)0xCC;
			buffer[++i] = (byte)0xDD;
			buffer[++i] = 0x06;
			buffer[++i] = 0x00;
			buffer[++i] = 0x01;
			buffer[++i] = 0x00;
			buffer[++i] = 0x02;
			buffer[++i] = 0x63;
			for (int j = 9; j < 64; j++)
			{
				buffer[j] = 0x00;
			}
			int beep2state = Vbar.Vdll.INSTANCE.vbar_channel_send(vbar_channel,buffer,64);
			Vbar.Vdll.INSTANCE.vbar_channel_recv(vbar_channel, buffer, 64, 200);

		}

	}
	//关闭设备
	public void closeDev()
	{
		if(vbar_channel != null)
		{
			Vbar.Vdll.INSTANCE.vbar_channel_close(vbar_channel);
			vbar_channel = null;
			System.out.println("close success");
		}
	}

	byte [] readBuffer = new byte[1024];
	int readBufferIndex = 0;
	//接收结果
	public String getResultsingle()
	{
		byte[] buffer = new byte[64];
		int i = 0;
		buffer[i] = 0x77;
		buffer[++i] = (byte) 0xCC;
		buffer[++i] = (byte) 0xDD;
		buffer[++i] = 0x0B;
		buffer[++i] = 0x00;
		buffer[++i] = 0x02;
		buffer[++i] = 0x00;
		buffer[++i] = 0x02;
		buffer[++i] = 0x00;
		buffer[++i] = 0x6D;
		for (int j = 10; j < 64; j++)
		{
			buffer[j] = 0x00;
		}
		Vbar.Vdll.INSTANCE.vbar_channel_send(vbar_channel, buffer, 64);
		byte[] bufferrecv = new byte[64];
		int getstate = Vbar.Vdll.INSTANCE.vbar_channel_recv(vbar_channel, bufferrecv, 64, 2500);

		if (bufferrecv[0] == 119 && bufferrecv[1] == -52 && bufferrecv[2] == -35 && bufferrecv[5] == 0)
		{
			int datalen = (bufferrecv[6] & 0xff) + ((bufferrecv[7] << 8) & 0xff);  //高位左移位8位 按协议低位在前 高位在后 扫码数据总长度
			if (datalen <= 55)
			{
				System.arraycopy(bufferrecv, 0, readBuffer, readBufferIndex, 64);//扫码一次接收成功
				byte[] receivebuffer = new byte[datalen];
				System.arraycopy(readBuffer, 8, receivebuffer, 0, datalen);
				String str = new String(receivebuffer);
				readBufferIndex = 0;
				return str;
			}
			else
			{
				System.arraycopy(bufferrecv, 0, readBuffer, readBufferIndex, 64);
				readBufferIndex += 64;
				int readtime = (datalen - 55) / 64 + 1;
				for (int r = 0; r < readtime; r++)
				{
					Vbar.Vdll.INSTANCE.vbar_channel_recv(vbar_channel, bufferrecv, 64, 200);
					System.arraycopy(bufferrecv, 0, readBuffer, readBufferIndex, 64);
					readBufferIndex += 64;
				}
				byte receivebuffer[] = new byte[datalen];
				System.arraycopy(readBuffer, 8, receivebuffer, 0, datalen);
				String str = new String(receivebuffer);
				readBufferIndex = 0;
				return str;
			}
		}
		else
		{
			return null;
		}
	}

	}



	
	


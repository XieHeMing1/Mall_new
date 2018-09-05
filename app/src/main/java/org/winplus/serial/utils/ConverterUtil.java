package org.winplus.serial.utils;

import android.annotation.SuppressLint;

/**
 * 各种转换工具
 * @author Administrator
 *
 */
public class ConverterUtil{

	/**
	 * Convert hex string to byte[]
	 * @param hexString the hex string
	 * @return byte[]
	 */
	@SuppressLint("DefaultLocale")
	public static byte[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}
	/**
	 * Convert char to byte
	 * @param c char
	 * @return byte
	 */
	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	public static String bytesToHex(byte[] bytes)
	{
		StringBuilder sb = new StringBuilder();
		String tmp = null;
		for (byte b : bytes)
		{
			tmp = Integer.toHexString(0xFF & b);
			if (tmp.length() == 1)
			{
				tmp = "0" + tmp;
			}
			sb.append(tmp);
		}

		return sb.toString();

	}
	public static String bytesToHex(byte[] bytes, int len)
	{
		StringBuilder sb = new StringBuilder();
		String tmp = null;
		byte b;
		for (int i=0;i<len;i++)
		{
			b=bytes[i];
			tmp = Integer.toHexString(0xFF & b);
			if (tmp.length() == 1)
			{
				tmp = "0" + tmp;
			}
			sb.append(tmp);
		}

		return sb.toString();

	}
	public static String byteToHex(byte bt)
	{
		String temp = Integer.toHexString(0xFF & bt);
		if (temp.length() == 1)
		{
			temp = "0" + temp;
		}
		return temp;
	}

	public static int byteToint(byte bt){
		if (bt<0) {
			return bt+256;
		}else{
			return bt;
		}
	}

}
		

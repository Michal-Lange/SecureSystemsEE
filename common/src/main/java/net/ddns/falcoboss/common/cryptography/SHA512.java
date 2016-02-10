package net.ddns.falcoboss.common.cryptography;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

public class SHA512 {

	public static String hashText(String textToHash) throws Exception
	{
		final MessageDigest sha512 = MessageDigest.getInstance("SHA-512");
		sha512.update(textToHash.getBytes());
		return convertByteToHex(sha512.digest());
	}
 
	public static String hashFile(File input) {
		try (InputStream in = new FileInputStream(input)) {
			final MessageDigest sha512 = MessageDigest.getInstance("SHA-512");
			byte[] block = new byte[4096];
			int length;
			while ((length = in.read(block)) > 0) {
				sha512.update(block, 0, length);
			}
			return convertByteToHex(sha512.digest());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String convertByteToHex(byte data[])
	{
		StringBuffer hexData = new StringBuffer();
		for (int byteIndex = 0; byteIndex < data.length; byteIndex++)
			hexData.append(Integer.toString((data[byteIndex] & 0xff) + 0x100, 16).substring(1));
		return hexData.toString();
	}
}


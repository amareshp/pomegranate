package com.qatrend.testutils.encryption;

import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

import com.qatrend.testutils.logging.PLogger;


/**
 * This program generates a AES key, retrieves its raw bytes, and then
 * reinstantiates a AES key from the key bytes. The reinstantiated key is used
 * to initialize a AES cipher for encryption and decryption.
 */

public class EncryptionUtil {

	private static String sSecretKey = "somesecretkey";
	/**
	 * Turns array of bytes into string
	 * 
	 * @param buf
	 *            Array of bytes to convert to hex string
	 * @return Generated hex string
	 */
	public static String asHex(byte buf[]) {
		StringBuffer strbuf = new StringBuffer(buf.length * 2);
		int i;

		for (i = 0; i < buf.length; i++) {
			if (((int) buf[i] & 0xff) < 0x10)
				strbuf.append("0");

			strbuf.append(Long.toString((int) buf[i] & 0xff, 16));
		}

		return strbuf.toString();
	}

	public byte[] hexToBytes(char[] hex) {
		int length = hex.length / 2;
		byte[] raw = new byte[length];
		for (int i = 0; i < length; i++) {
			int high = Character.digit(hex[i * 2], 16);
			int low = Character.digit(hex[i * 2 + 1], 16);
			int value = (high << 4) | low;
			if (value > 127)
				value -= 256;
			raw[i] = (byte) value;
		}
		return raw;
	}

	public String encrypt(String strToEncrypt) throws Exception {
		// Get the KeyGenerator
		KeyGenerator kgen = KeyGenerator.getInstance("AES");
		kgen.init(128); // 192 and 256 bits may not be available

		// Generate the secret key specs.
		// SecretKey skey = kgen.generateKey();
		// String keyStr = new String(skey.getEncoded());
		// PLogger.getLogger().debug( "Secret key: " + keyStr );
		// byte[] raw = skey.getEncoded();
		//using a hard coded key
		byte[] raw = sSecretKey.getBytes();
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");

		// Instantiate the cipher
		Cipher cipher = Cipher.getInstance("AES");
		// encrypt using the secret key
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec);

		byte[] encrypted = cipher.doFinal(strToEncrypt.getBytes());
		String encStrHex = asHex(encrypted);
		String encStr = new String(encrypted);
		//PLogger.getLogger().debug( "encrypted string in hex: " + encStrHex);
		// decrypt using the secret key
		//cipher.init(Cipher.DECRYPT_MODE, skeySpec);
		//byte[] original = cipher.doFinal(encrypted);
		//String originalString = new String(original);
		//PLogger.getLogger().debug( "Original string: " + originalString + " and hex: "+ asHex(original));

		return encStrHex;
	}

	public String decrypt(String strHexToDecrypt) throws Exception {

		byte[] raw = sSecretKey.getBytes();
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");

		// Instantiate the cipher
		Cipher cipher = Cipher.getInstance("AES");
		// decrypt using the secret key
		cipher.init(Cipher.DECRYPT_MODE, skeySpec);
		byte[] original = cipher.doFinal( hexToBytes(strHexToDecrypt.toCharArray()) );
		String originalString = new String(original);
		//this will print the decrypted text
		//PLogger.getLogger().debug( "Original string: " + originalString);
		return originalString;
	}

	public String threePartEnc(String part1, String part2, String part3) {
		EncryptionUtil encUtil = new EncryptionUtil();
		String fullStr = "#" + part1.length() + "#" + part1 + "#" + part2.length() + "#" + part2 + "#" + part3.length() + "#" + part3;
		//PLogger.getLogger().debug( "String to be encoded is: " + fullStr);
		String encStrHex = "";
		try{
			encStrHex = encUtil.encrypt( fullStr );
			PLogger.getLogger().debug( "Encrypted string in hex: " + encStrHex);			
		}
		catch(Exception ex){
			PLogger.getLogger().debug( "Exception: " + ex.getMessage());
		}
		return encStrHex;
	}
	
	public ArrayList<String> threePartDecrypt(String encryptedStrHex) {
		EncryptionUtil encUtil = new EncryptionUtil();
		//PLogger.getLogger().debug( "String to be encoded is: " + fullStr);
		List<String> list = new ArrayList<String>();
		try{
			String decryptedStr = encUtil.decrypt(encryptedStrHex);
			//PLogger.getLogger().debug( "Decrypted string: " + decryptedStr );
			//part1 is the username
			int length1_start = 0;
			int length1_end = decryptedStr.indexOf('#', length1_start+1);
			String len1Str = decryptedStr.substring(length1_start+1, length1_end);
			//PLogger.getLogger().debug( "length of part1: " + len1Str);
			int len1 = Integer.parseInt(len1Str);
			//PLogger.getLogger().debug( "length of part1 = " + len1);
			String part1 = decryptedStr.substring(length1_end+1, length1_end+len1+1);
			//PLogger.getLogger().debug( "part1 = " + part1  );
			
			//part2 is the Windows or Corp password
			int length2_start = length1_end + len1 + 1;
			int length2_end = decryptedStr.indexOf('#', length2_start+1);
			String len2Str = decryptedStr.substring(length2_start+1, length2_end);
			//PLogger.getLogger().debug( "length of part2: " + len2Str);
			int len2 = Integer.parseInt(len2Str);
			//PLogger.getLogger().debug( "length of part2 = " + len2);
			String part2 = decryptedStr.substring(length2_end+1, length2_end+len2+1);
			//PLogger.getLogger().debug( "part2 = " + part2  );
			
			//part3 is the UNIX password
			int length3_start = length2_end + len2 + 1;
			int length3_end = decryptedStr.indexOf('#', length3_start+1);
			String len3Str = decryptedStr.substring(length3_start+1, length3_end);
			//PLogger.getLogger().debug( "length of part3: " + len3Str);
			int len3 = Integer.parseInt(len3Str);
			//PLogger.getLogger().debug( "length of part3 = " + len3);
			String part3 = decryptedStr.substring(length3_end+1, length3_end+len3+1);
			//PLogger.getLogger().debug( "part3 = " + part3  );
			list.add(part1);  //username
			list.add(part2);  //Windows or Corp password
			list.add(part3);  //UNIX password
		}
		catch(Exception ex){
			PLogger.getLogger().debug( "Exception: " + ex.getMessage());
		}
		return (ArrayList<String>)list;
		
	}
	
	
}

package services;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class Decrypt {
	public static String decrypt(String toDecrypt) throws EncryptionException {
		try {
			return decrypt(toDecrypt, getKey());
		} catch (Exception e) {
			throw new EncryptionException("error during decryption");
		}
	}

	private static String decrypt(String toDecrypt, String key) throws Exception {
		return getAES(key).decrypt(toDecrypt);
	}

	static AES getAES(String myKey) throws Exception {
		byte[] salt = myKey.getBytes();
		char[] password = "9dh4dkg8".toCharArray();
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
		PBEKeySpec spec = new PBEKeySpec(password, salt, 65536, 256);
		SecretKey tmp = factory.generateSecret(spec);
		SecretKey key = new SecretKeySpec(tmp.getEncoded(), "AES");
		return new AES(key);
	}

	static String getKey() throws Exception {
		String k1 = readFile("k2.txt");
		String k2 = readFile("k3.txt");
		String k1xork2 = xor(k1, k2);
		String k3 = readFile("k6.txt");
		String ke = xor(k1xork2, k3);
		String k4 = Decrypt.decrypt(readFile("k7.txt"), ke);
		return k4;
	}

	private static String xor(String str1, String str2) {
		if (str1.getBytes().length != str2.getBytes().length) {
			return null;
		}
		byte[] bytes1 = str1.getBytes();
		byte[] bytes2 = str2.getBytes();
		String res = "";
		for(int i = 0; i < bytes1.length; i++) {
			res += Integer.toHexString(bytes1[i] ^ bytes2[i]);
		}

		return res;
	}

	private static String readFile(String path) throws Exception {
		File file = new File(path);
		FileInputStream fis = new FileInputStream(file.getPath());
		InputStreamReader isr = new InputStreamReader(fis);
		BufferedReader br = new BufferedReader(isr);
		return br.readLine();
	}
}
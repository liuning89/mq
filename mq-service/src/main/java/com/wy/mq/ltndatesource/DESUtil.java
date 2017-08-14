package com.wy.mq.ltndatesource;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class DESUtil {
	 private static final String DES_ALGORITHM = "DES";

	    public static String encryption(String plainData, String secretKey) throws Exception {
	        Cipher cipher = null;
	        try {
	            cipher = Cipher.getInstance(DES_ALGORITHM);
	            cipher.init(Cipher.ENCRYPT_MODE, generateKey(secretKey));
	        } catch (NoSuchAlgorithmException e) {
	            e.printStackTrace();
	        } catch (NoSuchPaddingException e) {
	            e.printStackTrace();
	        } catch (InvalidKeyException e) {
	        }
	        try {
	            // 为了防止解密时报javax.crypto.IllegalBlockSizeException: Input length must
	            // be multiple of 8 when decrypting with padded cipher异常，
	            // 不能把加密后的字节数组直接转换成字符串
	            byte[] buf = cipher.doFinal(plainData.getBytes());
	            return Base64Util.encode(buf);
	        } catch (IllegalBlockSizeException e) {
	            e.printStackTrace();
	            throw new Exception("IllegalBlockSizeException", e);
	        } catch (BadPaddingException e) {
	            e.printStackTrace();
	            throw new Exception("BadPaddingException", e);
	        }

	    }

	    public static String decryption(String secretData, String secretKey) throws Exception {
	        Cipher cipher = null;
	        try {
	            cipher = Cipher.getInstance(DES_ALGORITHM);
	            cipher.init(Cipher.DECRYPT_MODE, generateKey(secretKey));
	        } catch (NoSuchAlgorithmException e) {
	            e.printStackTrace();
	            throw new Exception("NoSuchAlgorithmException", e);
	        } catch (NoSuchPaddingException e) {
	            e.printStackTrace();
	            throw new Exception("NoSuchPaddingException", e);
	        } catch (InvalidKeyException e) {
	            e.printStackTrace();
	            throw new Exception("InvalidKeyException", e);
	        }
	        try {
	            byte[] buf = cipher.doFinal(Base64Util.decode(secretData.toCharArray()));
	            return new String(buf);
	        } catch (IllegalBlockSizeException e) {
	            e.printStackTrace();
	            throw new Exception("IllegalBlockSizeException", e);
	        } catch (BadPaddingException e) {
	            e.printStackTrace();
	            throw new Exception("BadPaddingException", e);
	        }
	    }

	    private static SecretKey generateKey(String secretKey) throws NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException {
	        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES_ALGORITHM);
	        DESKeySpec keySpec = new DESKeySpec(secretKey.getBytes());
	        keyFactory.generateSecret(keySpec);
	        return keyFactory.generateSecret(keySpec);
	    }

	    public static void main(String[] a) throws Exception {
	        String input = "ltnpassword2005";
	        String key = "123456789";
	        String result = encryption(input, key);
	        System.out.println(result);

	        System.out.println(decryption(result, key));

	    }
	}

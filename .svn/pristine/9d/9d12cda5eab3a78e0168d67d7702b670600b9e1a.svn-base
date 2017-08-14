package com.wy.mq.ltndatesource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * Created by zhanggt on 2016-1-3.
 * 
 */
public class LTNDataSource extends BasicDataSource {

	private static final String key = "LTNzsxedc3rfv4tgb5yhn";

	private Log logger = LogFactory.getLog(this.getClass());

//	@Override
//	public String getPassword() {
//		String passwordCiphertext = super.getPassword();
//		String passwordPlain = "";
//		try {
//			passwordPlain = DESUtil.decryption(passwordCiphertext, key);
//		} catch (Exception e) {
//			logger.error(e.getMessage(), e);
//		}
//		return passwordPlain;
//	}
	
	@Override
	public void setPassword(String password) {
		String passwordPlain = "";
		try {
			passwordPlain = DESUtil.decryption(password, key);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		super.setPassword(passwordPlain);
	}

	//DB密码加密
	public static void main(String[] args) throws Exception {
		String input = "12345";      //原始明文密码
		String result = DESUtil.encryption(input, key);
		System.out.println(result);
		System.out.println(DESUtil.decryption("M56od1YW0yA=", key));
	}
}

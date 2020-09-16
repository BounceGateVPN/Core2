package org.skunion.BunceGateVPN.core2;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class BGVConfig {

	public static BGVConfig bgvConf = new BGVConfig();
	
	private Properties pro = new Properties();
	
	public BGVConfig() {
		if(!new File("config").exists())
			new File("config").mkdirs();
		if(!new File("config/bgv.conf").exists())
			try {
				new File("config/bgv.conf").createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		try {
			pro.load(new BufferedInputStream(new FileInputStream("config/bgv.conf")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getConf(String key) {
		try {
			pro.load(new BufferedInputStream(new FileInputStream("config/bgv.conf")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pro.getProperty(key);
	}
	
	public void setConf(String key,String value) {
		try {
			pro.setProperty(key, value);
			pro.store(new BufferedOutputStream(new FileOutputStream("config/bgv.conf")),"Save Configs File.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
}

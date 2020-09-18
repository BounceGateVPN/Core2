package org.skunion.BunceGateVPN.core2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.skunion.BunceGateVPN.core2.websocket.WS_Client;
import org.skunion.BunceGateVPN.core2.websocket.WS_Server;

import com.github.smallru8.BounceGateVPN.Switch.VirtualSwitch;
import com.github.smallru8.Secure2.Secure2;
import com.github.smallru8.Secure2.SQL.SQL;
import com.github.smallru8.Secure2.config.Config;
import com.github.smallru8.driver.tuntap.TapDevice;

public class Main {
	
	public static TapDevice td = new TapDevice();
	public static VirtualSwitch localVS = new VirtualSwitch();
	
	public static Map<String,WS_Server> WS_Server_List = new HashMap<String, WS_Server>();//Listen port number,WS_Server
	
	public static void main( String[] args ) throws SQLException, URISyntaxException, IOException
    {
		onBGVStart();
		
		if(args[0].equalsIgnoreCase("-s")) {//server
			Config vSwitch = new Config();
			vSwitch.setConf("defaultSwitch", Config.ConfType.SERVER);//建default vSwitch
			
			Connection conn = Secure2.sql.getSQLConnection("defaultSwitch");//建庫
			if(SQL.getPasswd("defaultSwitch", conn, "test")==null)
				SQL.registered("defaultSwitch", conn, "test", "test");//新增使用者
			conn.close();
			
			InetSocketAddress addr = new InetSocketAddress(args[1],8787);
    		WS_Server sv = new WS_Server(addr);
    		sv.run();
    	}else if(args[0].equalsIgnoreCase("-c")){
    		Config user = new Config();
    		user.setConf("test", Config.ConfType.CLIENT);//建default vSwitch
    		
    		System.out.println("Host:"+user.host);
    		System.out.println("Port:"+user.port);
    		System.out.println("Name:"+user.userName);
    		System.out.println("Passwd:"+user.passwd);
    		
    		WS_Client cli = new WS_Client(user);//僅測試加密系統,未啟動tap device
    		cli.connect();
    		
    	}
    	
		System.out.println("Running...Press any key to stop.");
    	System.in.read();
    }
	
	private static void onBGVStart() {
		/**
		 * Start local vSwitch
		 */
		Main.localVS.start();
		
		/**
		 * Start Tap device
		 */
		Main.td.startEthernetDev(Main.localVS.addDevice(Main.td));
		if(BGVConfig.bgvConf.getConf("Tap")!=null&&BGVConfig.bgvConf.getConf("Tap").equalsIgnoreCase("true")) {
			Main.td.runFlag = true;
			Main.td.start();
		}else {
			BGVConfig.bgvConf.setConf("Tap", "false");
			Main.td.runFlag = false;
		}
		
		/**
		 * Get listen port list from BGV.conf and start WS_Server.
		 * WS_Server will store in WS_Server_List.
		 */
		String portLs = BGVConfig.bgvConf.getConf("Listen");
		if(portLs!=null) {
			String[] portArray = portLs.split(",");
			for(int i=0;i<portArray.length;i++) {
				if(!portArray[i].equals("")) {
					InetSocketAddress addr = new InetSocketAddress("0.0.0.0",Integer.parseInt(portArray[i]));
					WS_Server sv = new WS_Server(addr);
		    		sv.start();
					WS_Server_List.put(portArray[i],sv);
				}
			}
		}else {
			BGVConfig.bgvConf.setConf("Listen", ",");
		}
	}
	
}

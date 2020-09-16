package org.skunion.BunceGateVPN.core2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;

import org.skunion.BunceGateVPN.core2.websocket.WS_Client;
import org.skunion.BunceGateVPN.core2.websocket.WS_Server;

import com.github.smallru8.BounceGateVPN.Switch.VirtualSwitch;
import com.github.smallru8.Secure2.Secure2;
import com.github.smallru8.Secure2.SQL.SQL;
import com.github.smallru8.Secure2.config.Config;
import com.github.smallru8.driver.tuntap.TapDevice;

public class Main {
	
	public static TapDevice td = new TapDevice();
	public static VirtualSwitch localVS = new VirtualSwitch();//MainWindow.java 250 run()
	
	public static void main( String[] args ) throws SQLException, URISyntaxException, IOException
    {
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
	
}

package org.skunion.BunceGateVPN.core2;

import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.UIManager;

import org.skunion.BunceGateVPN.GUI.MainWindow;
import org.skunion.BunceGateVPN.core2.websocket.WS_Client;
import org.skunion.BunceGateVPN.core2.websocket.WS_Server;

import com.formdev.flatlaf.FlatDarkLaf;
import com.github.Mealf.BounceGateVPN.Router.VirtualRouter;
import com.github.smallru8.BounceGateVPN.Switch.VirtualSwitch;
import com.github.smallru8.BounceGateVPN.bridge.Bridge;
import com.github.smallru8.Secure2.config.Config;
import com.github.smallru8.driver.tuntap.TapDevice;
import com.github.smallru8.util.Pair;
import com.github.smallru8.util.log.Event;
import com.github.smallru8.util.log.EventSender;

public class Main {
	
	public static ImageIcon icon;
	public static MainWindow mainframe;
	
	public static TapDevice td = new TapDevice();
	public static VirtualSwitch localVS = new LocalhostVirtualSwitch();//LOCAL_SWITCH
	//public static VirtualRouter router;
	//private static Bridge bridge;
	
	/**
	 * 給vSwitch用
	 */
	public static Map<String,WS_Client> L2Linker = new HashMap<String,WS_Client>();//configName.conf,WS_Client
	
	public static Map<String,WS_Client> WS_Client_List = new HashMap<String,WS_Client>();//configName.conf,WS_Client
	public static Map<String,WS_Server> WS_Server_List = new HashMap<String,WS_Server>();//Listen port number,WS_Server
	
	public static void main( String[] args ) throws SQLException, URISyntaxException, IOException
    {
		/*
		Config routerConfig = new Config();
		routerConfig.setConf("router", Config.ConfType.ROUTER);
		router = new VirtualRouter(routerConfig);
		Config routerInterfaceConfig = new Config();
		routerInterfaceConfig.setConf("routerInterface", Config.ConfType.CLIENT);
		router.addRouterInterface(routerInterfaceConfig);
		//to 192.168.87.0/24 from Interface
		//router.addRoutingTable(-1062709504, 24, 0, 1);
		router.start();
		*/
		
		try {
		    UIManager.setLookAndFeel(new FlatDarkLaf());
		} catch( Exception ex ) {
		    EventSender.sendLog(Event.LogEvent.Type.ERROR,"Failed to initialize LaF.");
		}
		
		//icon = new ImageIcon(Main.class.getClassLoader().getResource("Logo.png"));
		//icon = new ImageIcon("F:\\git\\BounceGateVPN\\core2\\src\\main\\resources\\bgv.ico");
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					mainframe = new MainWindow();
					mainframe.setVisible(true);
					onBGVStart();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		/*
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
    	*/
    }
	
	private static void onBGVStart() {
		/**
		 * Start local vSwitch
		 */
		Main.localVS.start();
		Pair<Config,VirtualSwitch> p = new Pair<Config,VirtualSwitch>();
		p.makePair(null, localVS);//將LOCAL_SWITCH也加到列表中
		WS_Server.switchLs.put("LOCAL_SWITCH", p);
		EventSender.sendLog("Starting local switch.");
		/**
		 * Start Tap device
		 */
		Main.td.startEthernetDev(Main.localVS.addDevice(Main.td));
		EventSender.sendLog("Register tap device.");
		if(BGVConfig.bgvConf.getConf("Tap")!=null&&BGVConfig.bgvConf.getConf("Tap").equalsIgnoreCase("true")) {
			Main.td.runFlag = true;
			Main.td.start();
			EventSender.sendLog("Starting tap device.");
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
		
		/**
		 * 啟動vSwitch
		 */
		File f = new File("config/server/");
		String[] serverCfgLs = f.list();
		
		for(int i=0;i<serverCfgLs.length;i++) {
			Config cfg = new Config();
			cfg.setConf(serverCfgLs[i].split("\\.")[0], Config.ConfType.SERVER);
			Pair<Config,VirtualSwitch> cfgSv = new Pair<Config,VirtualSwitch>();
			cfgSv.makePair(cfg, new VirtualSwitch());
			cfgSv.second.start();
			WS_Server.switchLs.put(cfg.switchName,cfgSv);
			//bridge = new Bridge(cfgSv.second, localVS);
			//routerBridge = new Bridge(cfgSv.second, router);
			
			new Bridge(cfgSv.second, localVS);//每台對外vSwitch都跟local switch建立連接
			
			EventSender.sendLog("VirtualSwitch : " + cfg.switchName + " start.");
		}
		
		/**
		 * 啟動vRouter
		 */
		f = new File("config/router/");
		serverCfgLs = f.list();
		for(int i=0;i<serverCfgLs.length;i++) {
			Config cfg = new Config();
			cfg.passwd = null;//為router時cfg的passwd欄位拿來存routerinterface的名稱,為null表示沒有interface
			cfg.setConf(serverCfgLs[i].split("\\.")[0], Config.ConfType.ROUTER);
			Pair<Config,VirtualRouter> cfgRo = new Pair<Config,VirtualRouter>();
			cfgRo.makePair(cfg, new VirtualRouter(cfg));
			cfgRo.second.name = cfg.confName;
			
			if(cfg.passwd!=null) {//加Interface
				Config routerInterfaceConfig = new Config();
				routerInterfaceConfig.setConf(cfg.passwd, Config.ConfType.INTERFACE);
				try {
					cfgRo.second.addRouterInterface(routerInterfaceConfig);
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
			}
			cfgRo.second.start();
			
			WS_Server.routerLs.put(cfg.confName, cfgRo);
		}
		
		Layer2Layer.loadData();//載入bridge
	}
	
}

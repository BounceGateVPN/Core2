package org.skunion.BunceGateVPN.core2.websocket;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import com.github.smallru8.BounceGateVPN.Switch.VirtualSwitch;
import com.github.smallru8.Secure2.Secure2;
import com.github.smallru8.Secure2.Data.UsrData;
import com.github.smallru8.Secure2.SQL.SQL;
import com.github.smallru8.Secure2.config.Config;
import com.github.smallru8.Secure2.config.Config.ConfType;
import com.github.smallru8.util.Pair;
import com.github.smallru8.util.log.Event;
import com.github.smallru8.util.log.EventSender;

public class WS_Server extends WebSocketServer{

	public final Base64.Decoder decoder = Base64.getDecoder();
	public final Base64.Encoder encoder = Base64.getEncoder();
	
	public static Map<String,Pair<Config,VirtualSwitch>> switchLs = new HashMap<>();//SwitchName,<switch config,switch>
	public Map<WebSocket,WS_Package> WSRecord = new HashMap<>();//Connection list
	
	public WS_Server(InetSocketAddress address) {
		super(address);
		this.getCfgs();
	}
	
	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		// TODO Auto-generated method stub
		String ip = conn.getRemoteSocketAddress().getAddress().getHostAddress();
		EventSender.sendLog("IP : "+ip+" connected.");
	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		// TODO Auto-generated method stub
		String ip = conn.getRemoteSocketAddress().getAddress().getHostAddress();
		WSRecord.remove(conn);
		EventSender.sendLog("IP : "+ip+" disconnected. Due to "+reason);
	}

	@Override
	public void onMessage(WebSocket conn, String message) {
		// TODO Auto-generated method stub
		WS_Package wsp = WSRecord.get(conn);
		if(wsp == null) {
			//接收client公鑰, 並紀錄connection
			wsp = new WS_Package(conn);
			//EventSender.sendLog("Public key : "+message);/////////////////
			wsp.ud = new UsrData(decoder.decode(message));
			//System.out.println("Client pubk : "+message);//////////
			//System.out.println("Server pubk : "+encoder.encodeToString(wsp.ud.dh.getPublicKey()));////////////
			conn.send(encoder.encodeToString(wsp.ud.dh.getPublicKey()));//server端公鑰,編碼成UTF-8字串後送出給client
			WSRecord.put(conn, wsp);//加入紀錄
			
		}else if(!wsp.readyFlag) {//收帳號密碼
			try {
				/**
				 * data[0] = switchname
				 * data[1] = username
				 * data[2] = passwd
				 */
				String[] data = new String(wsp.ud.dh.decryption(decoder.decode(message)),"UTF-8").split("\n");//String轉(BASE64)bytearray>>解密>>bytearray轉(UTF-8)String
				if(data.length==3&&switchLs.get(data[0])!=null) {//有3筆資料, 且switch存在
					wsp.ud.destSwitchName = data[0];
					wsp.ud.name = data[1];
					wsp.ud.passwd = data[2];
					wsp.ud.readyFlag = true;
					
					/**
					 * 驗證資料
					 */
					if(!verifyUser(wsp)) {//未通過則執行
						WSRecord.remove(conn);
						conn.close();
					}
					
				}else {//資料有問題,關閉連線
					WSRecord.remove(conn);
					conn.close();
				}
			} catch (SQLException | UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else {
			//測試用
			String str;
			try {
				str = new String(wsp.ud.dh.decryption(decoder.decode(message)),"UTF-8");
				EventSender.sendLog(wsp.ud.name+" : "+str);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 收封包解密並轉發到對應switch
	 */
	@Override
	public void onMessage(WebSocket conn, ByteBuffer message) {
		WS_Package wsp = WSRecord.get(conn);
		if(wsp!=null&&wsp.readyFlag) {
			wsp.sport.sendToVirtualDevice(wsp.ud.dh.decryption(message.array()));//解密 並送給switch
		}
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
		// TODO Auto-generated method stub
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			ex.printStackTrace(pw);
			EventSender.sendLog(Event.LogEvent.Type.ERROR,sw.toString());
			pw.close();
			try {
				sw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		EventSender.sendLog("Open websocket server, listen on port : " + this.getPort());
	}

	/*------------------------------------------------------------------------------------------------*/
	
	/**
	 * 設定伺服器端switch config
	 * 啟動VirtualSwitch
	 */
	private void getCfgs() {
		String[] cfgs = new File("config/server/").list();
		for(int i=0;i<cfgs.length;i++) {
			Config cfg = new Config();
			cfg.setConf(cfgs[i].split("\\.")[0],ConfType.SERVER);
			Pair<Config,VirtualSwitch> p = new Pair<Config, VirtualSwitch>();
			VirtualSwitch vSwitch = new VirtualSwitch();
			vSwitch.start();
			p.makePair(cfg, vSwitch);
			switchLs.put(cfg.confName, p);
		}
	}
	
	/**
	 *	 驗證使用者帳號密碼
	 * 	通過則註冊到對應switch
	 *  
	 * @param wsp
	 * @return true=通過/false=未通過
	 * @throws SQLException 
	 */
	private boolean verifyUser(WS_Package wsp) throws SQLException {
		Pair<Config,VirtualSwitch> vSwitch = switchLs.get(wsp.ud.destSwitchName);
		Connection sqlconn;
		if(vSwitch.first.SQLFlag)//使用SQL
			sqlconn = Secure2.sql.getSQLConnection(vSwitch.first.confName, vSwitch.first.host, vSwitch.first.userName, vSwitch.first.passwd);
		else //使用SQLite
			sqlconn = Secure2.sql.getSQLConnection(vSwitch.first.confName);
		
		if(SQL.getPasswd(vSwitch.first.confName, sqlconn, wsp.ud.name).equals(wsp.ud.passwd)) {//密碼符合
			wsp.sport = vSwitch.second.addDevice(wsp);
			wsp.readyFlag = true;
		}
		sqlconn.close();
		return wsp.readyFlag;
	}
	
}

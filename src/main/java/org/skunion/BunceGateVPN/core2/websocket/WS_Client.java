package org.skunion.BunceGateVPN.core2.websocket;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.Base64;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.skunion.BunceGateVPN.core2.Main;
import com.github.smallru8.Secure2.DH.DHSender;
import com.github.smallru8.Secure2.Data.UsrData;
import com.github.smallru8.Secure2.config.Config;
import com.github.smallru8.util.abstracts.Port;
import com.github.smallru8.util.log.Event;
import com.github.smallru8.util.log.EventSender;

/**
 * DH全程使用String傳輸
 * 使用者帳號密碼使用String傳輸
 * 封包使用byte[]傳輸
 * 
 * @author smallru8
 *
 */
public class WS_Client extends WebSocketClient{

	public final Base64.Decoder decoder = Base64.getDecoder();
	public final Base64.Encoder encoder = Base64.getEncoder();
	
	public boolean autoReconnect = false;
	
	public boolean readyFlag;
	public UsrData ud;
	public Port sport; //Switch port
	
	public WS_Client(Config cfg) throws URISyntaxException{
		super(new URI(cfg.host + ":" + cfg.port));
		ud = new UsrData();//初始化user data
		ud.setNamePasswd(cfg.userName, cfg.passwd);
		ud.destSwitchName = cfg.switchName;
		ud.IPaddr = cfg.host;
		ud.port = cfg.port;
		ud.sessionName = cfg.confName;
		readyFlag = false;
	}
	
	/**
	 * 跟Switch註冊後拿到的port
	 * @param port
	 */
	public void setPort(Port port) {
		this.sport = port;
	}
	
	/**
	 * connect後自動送出publickey
	 */
	@Override
	public void connect() {
		super.connect();
		try {
			int counter = 0;
			while(!isOpen()&&counter<3) {
				Thread.sleep(3000);
				counter++;
			}
			if(isOpen()) {
				EventSender.sendLog("Start sending public key to server.");
				//System.out.println("Client pubk : "+encoder.encodeToString(ud.dh.getPublicKey()));///////////
				this.send(encoder.encodeToString(ud.dh.getPublicKey()));//送出publickey, 以UTF-8編碼
			}else {
				EventSender.sendLog(Event.LogEvent.Type.ERROR,"Can't connect to " + ud.destSwitchName + "@" + ud.IPaddr + ":" + ud.port);
				close();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 要根據持有這個WS_Client的VirtualSwitch/VirtualRouter,在new時在Override一次這個method
	 */
	@Override
	public void close() {
		Main.localVS.delDevice(this.hashCode());
		Main.WS_Client_List.remove(ud.sessionName+".conf");
		super.close();
	}
	
	/**
	 * 送publickey、帳號密碼
	 */
	@Override
	public void send(String text) {
		if(!ud.readyFlag)//Send public key
			super.send(text);
		else {
			try {
				super.send(encoder.encodeToString(ud.dh.encrypt(text.getBytes("UTF-8"))));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}//String轉(UTF-8)bytearray>>加密>>bytearray轉(BASE64)String
		}
	}
	
	/**
	 * 加密送出封包
	 */
	@Override
	public void send(ByteBuffer bytes) {
		if(readyFlag) {
			super.send(ud.dh.encrypt(bytes.array()));
			EventSender.sendLog("send : "+bytes.array().length);/////////////////////Debug
		}
	}
	
	/**
	 * 加密送出封包
	 */
	@Override
	public void send(byte[] bytes) {
		if(readyFlag) {
			super.send(ud.dh.encrypt(bytes));
			EventSender.sendLog("send : "+bytes.length);/////////////////////Debug
		}
	}
	
	@Override
	public void onOpen(ServerHandshake handshakedata) {
		// TODO Auto-generated method stub
		EventSender.sendLog("Open websocket connection.");
	}

	/**
	 * 收publickey
	 */
	@Override
	public void onMessage(String message) {
		// TODO Auto-generated method stub
		if(!ud.readyFlag) {
			ud.readyFlag = true;
			//readyFlag = true;
			
			((DHSender)ud.dh).initAESKey(decoder.decode(message));///
			//System.out.println("Server pubk : "+message);////////
			EventSender.sendLog("D-H done.");
			//DH完成,送帳號密碼 密碼不能有空白建!!!
			
			//System.out.println(ud.destSwitchName+" "+ud.name+" "+ud.passwd);
			//send(ud.destSwitchName+" "+ud.name+" "+ud.passwd);//<switchname>\n<username>\n<passwd>
			try {
				String usrData = encoder.encodeToString(ud.dh.encrypt((ud.destSwitchName+"\n"+ud.name+"\n"+ud.passwd).getBytes("UTF-8")));
				System.out.println(usrData);
				super.send(usrData);
				
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try {//測試加密資料送出
				super.send(encoder.encodeToString(ud.dh.encrypt("わためは悪くないよね。".getBytes("UTF-8"))));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			readyFlag = true;
		}
	}
	
	/**
	 * 解密接收封包
	 */
	@Override
	public void onMessage(ByteBuffer message) {
		// TODO Auto-generated method stub
		if(ud.readyFlag&&readyFlag) {
			sport.sendToVirtualDevice(ud.dh.decryption(message.array()));
		}
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		// TODO Auto-generated method stub
		EventSender.sendLog("Close websocket connection. " + reason);
		close();
		if(autoReconnect) {//auto reconnect
			EventSender.sendLog("Reconnect websocket connection.");
			readyFlag = false;
			ud.readyFlag = false;
			connect();
		}
		
		/*
		if(sport.type.equals(Port.DeviceType.virtualSwitch)) {
			((SwitchPort)sport).vs.delDevice(this.hashCode());
		}else if(sport.type.equals(Port.DeviceType.virtualRouter)) {
			((RouterPort)sport).vr.delDevice(this.hashCode());
		}else {
			
		}
		*/
	}

	@Override
	public void onError(Exception ex) {
		// TODO Auto-generated method stub
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		ex.printStackTrace(pw);
		EventSender.sendLog(Event.LogEvent.Type.ERROR,sw.toString());
		pw.close();
		try {
			sw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

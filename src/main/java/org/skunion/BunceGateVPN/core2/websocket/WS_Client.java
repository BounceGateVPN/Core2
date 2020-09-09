package org.skunion.BunceGateVPN.core2.websocket;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.skunion.BunceGateVPN.core2.log.Event;
import org.skunion.BunceGateVPN.core2.log.EventSender;
import com.github.smallru8.BounceGateVPN.device.Port;
import com.github.smallru8.Secure2.DH.DHSender;
import com.github.smallru8.Secure2.Data.UsrData;
import com.github.smallru8.Secure2.config.Config;

/**
 * DH全程使用String傳輸
 * 使用者帳號密碼使用String傳輸
 * 封包使用byte[]傳輸
 * 
 * @author smallru8
 *
 */
public class WS_Client extends WebSocketClient{

	public boolean readyFlag;
	public UsrData ud;
	public Port sport; //Switch port
	
	public WS_Client(Config cfg) throws URISyntaxException {
		super(new URI(cfg.host + ":" + cfg.port));
		ud = new UsrData();//初始化user data
		ud.setNamePasswd(cfg.userName, cfg.passwd);
		ud.destSwitchName = cfg.switchName;
		ud.IPaddr = cfg.host;
		ud.port = cfg.port;
		ud.sessionName = cfg.confName;
		readyFlag = false;
		try {
			this.send(new String(ud.dh.getPublicKey(),"UTF-8"));//送出publickey, 以UTF-8編碼
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 跟Switch註冊後拿到的port
	 * @param port
	 */
	public void setPort(Port port) {
		this.sport = port;
	}
	
	/**
	 * 送publickey、帳號密碼
	 */
	@Override
	public void send(String text) {
		if(!readyFlag)
			super.send(text);
		else {
			try {//加密帳號密碼
				super.send(new String(ud.dh.encrypt(text.getBytes("UTF-8")),"UTF-8"));//String轉bytearray>>加密>>bytearray轉String
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 加密送出封包
	 */
	@Override
	public void send(ByteBuffer bytes) {
		super.send(ud.dh.encrypt(bytes.array()));
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
			readyFlag = true;
			try {
				((DHSender)ud.dh).initAESKey(message.getBytes("UTF-8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			EventSender.sendLog("D-H done.");
			//DH完成,送帳號密碼
			send(ud.destSwitchName+"\n"+ud.name+"\n"+ud.passwd);//<switchname>\n<username>\n<passwd>
		}else {//只有在測試時才會執行這行
			EventSender.sendLog("Recv : " + message);
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

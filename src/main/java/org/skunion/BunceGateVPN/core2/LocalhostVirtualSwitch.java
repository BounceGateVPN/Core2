package org.skunion.BunceGateVPN.core2;

import com.github.smallru8.BounceGateVPN.Switch.SwitchPort;
import com.github.smallru8.BounceGateVPN.Switch.VirtualSwitch;
import com.github.smallru8.driver.tuntap.TapDevice;
import com.github.smallru8.util.abstracts.Port;
import com.github.smallru8.util.log.Event;
import com.github.smallru8.util.log.EventSender;

/**
 * 
 * 特規Switch
 * 可有多個對外(WS/vSwitch)，有一個對內(tap)
 * 來源不是tap都只往tap送;來源為tap則正常轉發
 * 避免 WS <-> WS (都是對外的兩個區網互傳)
 * @author smallru8
 *
 */
public class LocalhostVirtualSwitch extends VirtualSwitch{

	private int td_Hashcode = -2147483648;
	private boolean td_Hashcode_is_set = false;
	
	public LocalhostVirtualSwitch() {
		super();
	}
	
	@Override
	public Port addDevice(TapDevice td) {
		SwitchPort sp = new SwitchPort(td);
		sp.vs = this;
		port.put(td.hashCode(), sp);
		td_Hashcode = td.hashCode();
		td_Hashcode_is_set = true;
		return sp;
	}
	
	@Override
	protected void sendDataToDevice(int devHashCode,byte[] data) {//由Switch呼叫，之後要在送出前加密，現在先直接送
		if(devHashCode == 0) {//廣播
			EventSender.sendLog("Send Broadcast.");
			int tmpHashCode = switchTable.searchSrcPortHashCode(data);
			for(int k : port.keySet()) {
				if(k!=tmpHashCode) {//不要送給自己
					port.get(k).sendToDevice(data);
				}
			}
		}else {//送給指定port,tap
			port.get(devHashCode).sendToDevice(data);
		}
	}
	
	@Override
	public void run() {
		byte[] buffer;
		while(powerFlag) {
			try {
				buffer = outputQ.take();//這裡會Blocking直到有東西進來
				if(td_Hashcode_is_set) {
					if(switchTable.searchSrcPortHashCode(buffer)==td_Hashcode) {//由本地用戶送出(tap)
						sendDataToDevice(switchTable.searchDesPortHashCode(buffer),buffer);//往外送(WS)
					}else{//外部輸入到本地
						
						sendDataToDevice(td_Hashcode,buffer);//一律往tap送
					}
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}

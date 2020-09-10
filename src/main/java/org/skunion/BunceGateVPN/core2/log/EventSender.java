package org.skunion.BunceGateVPN.core2.log;

import org.greenrobot.eventbus.EventBus;
import org.skunion.BunceGateVPN.core2.log.Event.LogEvent.Type;

public class EventSender {

	/**
	 * Send log
	 * @param t
	 * @param text
	 */
	public static void sendLog(Event.LogEvent.Type t,String text) {
		String front = "";
		if(t.equals(Type.INFO))
			front = "[INFO] ";
		else if(t.equals(Type.ERROR))
			front = "[ERROR] ";
		else
			front = "[WARN] ";
		System.out.println(front+text);
		if(EventBus.getDefault().hasSubscriberForEvent(Event.LogEvent.class))
			EventBus.getDefault().post(new Event.LogEvent(t,text));
	}
	
	/**
	 * Send log default(INFO)
	 * @param text
	 */
	public static void sendLog(String text) {
		System.out.println("[INFO] "+text);
		if(EventBus.getDefault().hasSubscriberForEvent(Event.LogEvent.class))
			EventBus.getDefault().post(new Event.LogEvent(Event.LogEvent.Type.INFO,text));
	}
	
}

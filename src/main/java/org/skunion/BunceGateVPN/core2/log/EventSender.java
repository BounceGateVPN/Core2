package org.skunion.BunceGateVPN.core2.log;

import org.greenrobot.eventbus.EventBus;

public class EventSender {

	/**
	 * Send log
	 * @param t
	 * @param text
	 */
	public static void sendLog(Event.LogEvent.Type t,String text) {
		if(EventBus.getDefault().hasSubscriberForEvent(Event.LogEvent.class))
			EventBus.getDefault().post(new Event.LogEvent(t,text));
	}
	
	/**
	 * Send log default(INFO)
	 * @param text
	 */
	public static void sendLog(String text) {
		if(EventBus.getDefault().hasSubscriberForEvent(Event.LogEvent.class))
			EventBus.getDefault().post(new Event.LogEvent(Event.LogEvent.Type.INFO,text));
	}
	
}

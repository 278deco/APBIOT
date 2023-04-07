package apbiot.core.event.events.discord;

import apbiot.core.objects.interfaces.ILoggerEvent;

public class EventInstanceReady implements ILoggerEvent {
	
	private boolean isCommandatorRunning;
	private String prefix;
	
	public EventInstanceReady(boolean isCommandatorRunning, String prefix) {
		this.isCommandatorRunning = isCommandatorRunning;
		this.prefix = prefix;
	}
	
	public boolean isCommandatorRunning() {
		return this.isCommandatorRunning;
	}
	
	public String getPrefix() {
		return this.prefix;
	}

	@Override
	public String getLoggerMessage() {
		return "Client initialised with success. All systems are operational. (Using prefix: "+getPrefix()+", Using commandator: "+isCommandatorRunning()+")";
	}
	
	@Override
	public EventPriority getEventPriority() {
		return EventPriority.INFO;
	}
	
}

package apbiot.core.event.events.discord;

import apbiot.core.objects.interfaces.ILoggerEvent;

public class EventInstanceConnected implements ILoggerEvent {
	
	private boolean isCommandatorRunning;
	private String prefix;
	
	public EventInstanceConnected(boolean isCommandatorRunning, String prefix) {
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
		return "Client connected with success. All systems are operational. (Using prefix: "+getPrefix()+", Using commandator: "+isCommandatorRunning()+")";
	}
	
	@Override
	public EventPriority getEventPriority() {
		return EventPriority.INFO;
	}
	
}

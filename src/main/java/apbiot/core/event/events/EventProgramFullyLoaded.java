package apbiot.core.event.events;

import apbiot.core.objects.interfaces.ILoggerEvent;

public class EventProgramFullyLoaded implements ILoggerEvent {

	private boolean isCommandatorRunning;
	private String prefix;
	
	public EventProgramFullyLoaded(boolean isCommandatorRunning, String prefix) {
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
		return "The client is fully loaded, all systems are operational. (Using prefix: "+getPrefix()+", Using commandator: "+isCommandatorRunning()+")";
	}

	@Override
	public EventPriority getEventPriority() {
		return EventPriority.INFO;
	}

}

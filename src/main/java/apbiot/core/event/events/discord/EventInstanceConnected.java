package apbiot.core.event.events.discord;

import apbiot.core.objects.interfaces.ILoggerEvent;

public class EventInstanceConnected implements ILoggerEvent {

	@Override
	public String getLoggerMessage() {
		return "Client connected to gateway with success.";
	}	
	
	@Override
	public EventPriority getEventPriority() {
		return EventPriority.INFO;
	}
}

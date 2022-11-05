package apbiot.core.event.events;

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
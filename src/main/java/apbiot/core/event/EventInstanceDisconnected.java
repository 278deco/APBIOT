package apbiot.core.event;

import apbiot.core.objects.interfaces.ILoggerEvent;

public class EventInstanceDisconnected implements ILoggerEvent {

	@Override
	public String getLoggerMessage() {
		return "Client disconnected from gateway with success.";
	}
	
	@Override
	public EventPriority getEventPriority() {
		return EventPriority.INFO;
	}
}
